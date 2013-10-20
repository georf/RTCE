/**
 * 
 */
package rebeca.peernet;

import java.util.*;

import org.apache.log4j.*;

import peersim.config.Configuration;
import peersim.core.*;
import peersim.edsim.*;

import rebeca.*;
import rebeca.util.*;
import rebeca.util.Graph.*;

/**
 * @author parzy
 *
 */
public class NetworkRouting extends NetworkGraph 
implements BrokerEngine, EDProtocol
{
	// parameters and constants -----------------------------------------------
	// ------------------------------------------------------------------------
	
	/**
	 * Key identifying this engine. 
	 */
	public static final Class<NetworkRouting> KEY = NetworkRouting.class;
	
	/** 
	 * Logger for transport engines. 
	 */
	private static final Logger LOG = Logger.getLogger(KEY);
	
	/**
	 * Parameter identifying the link implementation class.
	 */
	private static final String PAR_LINK = "link";

	/**
	 * Parameter identifying the link implementation class.
	 */
	private static final String PAR_ROUTER = "router";
	
	private static final String PAR_ROUTE = "route";
	private static final String PAR_COST = "cost";
	private static final String PAR_WEIGHT = "weight";
	
	private static final SimLink DEFAULT_LINK = new ConstantLink();
	private static final SimRouter DEFAULT_ROUTER = new ConstantRouter();
	private static final double DEFAULT_WEIGHT = 1.0d;

	
	// fields -----------------------------------------------------------------
	// ------------------------------------------------------------------------
	
	protected String prefix;
	protected Broker broker;
	protected SimRouter host;
	
	protected SimLink link;
	protected SimRouter router;
	
	protected Graph.Property route;
	protected Graph.Property cost;
	protected Graph.Property weight;
	
	
	// instantiation ----------------------------------------------------------
	// ------------------------------------------------------------------------
	
	public NetworkRouting(String prefix)
	{
		super(prefix);
	
		// store prefix enabling lazy configurations
		this.prefix = prefix;
		
		// instantiating prototypes
		this.link = (SimLink)Configuration.getInstance(prefix + "." 
				+ PAR_LINK, DEFAULT_LINK);
		link.init(LOG, pid);
		this.router = (SimRouter)Configuration.getInstance(prefix + "." 
				+ PAR_ROUTER, DEFAULT_ROUTER);
		router.init(LOG, pid);
		
		// initialize properties
		String r = Configuration.getString(prefix + "." + PAR_ROUTE, PAR_ROUTE);
		this.route = graph.addProperty(r);
		String c = Configuration.getString(prefix + "." + PAR_COST, PAR_COST);
		this.cost = graph.addProperty(c);
	}
	
	
	@Override
	public Object clone() 
	{
		NetworkRouting clone = (NetworkRouting) super.clone();
		clone.host = (SimRouter) router.clone();
		// initialization is done whenever a vertex gets associated
		return clone;
	}
	
	
	
	// PeerSim event processing -----------------------------------------------
	// ------------------------------------------------------------------------

	@Override
	public void processEvent( Node node, int pid, Object e ) 
	{
		if ( e instanceof SimComponent.SimEvent)
		{
			processEvent(node,pid,(SimComponent.SimEvent)e);
			return;
		}
		LOG.error("unhandled event '" + e + "'");
	}

	public void processEvent(Node node, int pid, SimComponent.SimEvent e)
	{
		SimComponent comp = e.getComponent();
		comp.processEvent(node, pid, e);
	}

	
	// getters and setters ----------------------------------------------------
	// ------------------------------------------------------------------------
	
	@Override
	public void setVertex(Graph.Vertex v)
	{
		super.setVertex(v);
		
		// initialize the host
		host.init(v);
		
		LOG.trace("host was initialized");
	}
	
	
	/**
	 * Returns the router prototype that is cloned for each node and vertex.
	 * @return the router prototype
	 */
	public SimRouter getRouter()
	{
		return router;
	}
	/**
	 * Sets the router prototype.
	 * @param router the router prototype
	 */
	public void setRouter(SimRouter router)
	{
		this.router = router;
	}
	/**
	 * Returns the cloned router instance that is associated with this node.
	 * @return the associated router instance
	 */
	public SimRouter getHost()
	{
		return host;
	}

	
	/**
	 * Returns the link prototype that is cloned for each edge.
	 * @return the link prototype
	 */
	public SimLink getLink() 
	{
		return link;
	}
	/**
	 * Sets the link prototype.
	 * @param link the link to set
	 */
	public void setLink(SimLink link) 
	{
		this.link = link;
	}
		
	public Graph.Property getRoute()
	{
		return route;
	}
	
	public Graph.Property getCost()
	{
		return cost;
	}
	
	public void setWeight(Graph.Property weight)
	{
		this.weight = weight;
	}

	/**
	 * Returns the weight property. If no weight property was set the property 
	 * is initialized using configuration parameters and settings.
	 * @return the weight property
	 */
	protected Graph.Property weight()
	{
		if (this.weight==null)
		{
			String prop = Configuration.getString(prefix + "." 
					+ PAR_WEIGHT, PAR_WEIGHT);
			// explicitly request weight property from underlay
			this.weight = (isOverlay() ? getUnderlay():this).getProperty(prop);
		}
		return this.weight;
	}

	
	// route management -------------------------------------------------------
	// ------------------------------------------------------------------------

	public boolean addRoute(Node n, Node m)
	{
		// check if route already exists
		if (hasEdge(n, m)) return false;
		
		// otherwise create a route
		LinkedList<SimComponent> r = new LinkedList<SimComponent>();
		double c = createRoute(n,m,r);
		
		// and store route if valid
		if (r.size()==0) return false;
		Edge e = addEdge(n,m);
		e.set(route,r);
		e.set(cost,c);
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public List<SimComponent> getRoute(Node n, Node m)
	{
		Edge e = getEdge(n,m);
		return e != null ? (List<SimComponent>)e.get(route) : null; 
	}
	
	public boolean hasRoute(Node n, Node m)
	{
		return getRoute(n,m)!=null;
	}
	
	public boolean removeRoute(Node n, Node m)
	{
		Edge e = getEdge(n,m);
		if (e!=null) graph.removeEdge(e); 
		return e!=null;
	}
	
	
	// path and route creation ------------------------------------------------
	// ------------------------------------------------------------------------
	
	private double createRoute(Node n, Node m, LinkedList<SimComponent> route)
	{	
		// ensure that routes have all the same direction
		if (n.getID()>m.getID())
		{
			Node tmp = n; 
			n = m; 
			m = tmp;
		}		

		// get underlay elements
		Networking underlay = getUnderlay();
		Graph graph = underlay.getGraph();
		Vertex src = underlay.getVertex(n);
		Vertex dst = underlay.getVertex(m);

		// calculate and populate shortest path
		LinkedList<Vertex> path = new LinkedList<Vertex>();
		double cost = calcPath(graph,src,dst,path);
		makePath(graph,path,route);	
		return cost;
	}
	
	/**
	 * Calculates the shortest path between two nodes using Dijkstra's 
	 * algorithm.  Distances are measured based on the specified cost property.
	 * The shortest path is stored as a list of vertices and its accumulated 
	 * cost is returned. 
	 * @param graph the graph in which the shortest path has to be calculated
	 * @param src the source vertex
	 * @param dst the destination vertex
	 * @param path list to store the path in
	 * @return accumulated cost of the path or MAX_VALUE if no path is found
	 */
	private double calcPath(Graph graph, Vertex src, Vertex dst, LinkedList<Graph.Vertex> path)
	{	
		// create and get properties
		Graph.Property distance = graph.addProperty();
		Graph.Property predecessor = graph.addProperty();
		Graph.Property weight = weight();
		
		// initialize dijkstra's algorithm
		HashSet<Vertex> vertices = new HashSet<Vertex>();
		vertices.add(src);
		Object s = src.get(weight);
		src.set(distance, s!=null && s instanceof Double ? s : DEFAULT_WEIGHT);
		
		// main loop
		while(!vertices.isEmpty())
		{
			// find closest vertex
			Vertex cur = null;
			double min = Double.MAX_VALUE;
			for (Vertex v : vertices)
			{
				Object o = v.get(distance);
				double d = o!=null && o instanceof Double ? (Double)o : Double.MAX_VALUE;
				if (d < min)
				{
					cur = v;
					min = d;
				}
			}
//			LOG.debug("min: "+min);
			
			// destination reached?
			if (cur==dst)
			{
				while(cur != null)
				{
					path.addFirst(cur);
					//System.out.println("a");
					cur = (Vertex)cur.get(predecessor);
				}
				graph.removeProperty(distance);
				graph.removeProperty(predecessor);
				return min; 
			}
			
			// relax distance
			for (Edge e : graph.edges(cur))
			{
				// extend set of reached vertices
				// sum up edge and vertex costs
				Vertex v = cur==e.tail() ? e.head() : e.tail();
				Object o = e.get(weight);
				Object p = v.get(weight);
				double w = (o!=null && o instanceof Double ? (Double)o : 0.0d)
						 + (p!=null && p instanceof Double ? (Double)p : 0.0d)
						 + (o==null && p==null ? DEFAULT_WEIGHT: 0.0d);
				// compare to vertex's distance
				Object q = v.get(distance);
				double d = q!=null && q instanceof Double ? (Double)q : Double.MAX_VALUE;
				// update distance and list of vertices when shorter way was found 
				if (min+w<d)
				{
					v.set(distance,min+w);
					v.set(predecessor,cur);
					vertices.add(v);
				}
			}
			// remove handled vertex
			vertices.remove(cur);
		}
		
		// destination not reached! 
		graph.removeProperty(distance);
		graph.removeProperty(predecessor);
		return Double.MAX_VALUE;
	}   
	
	/**
	 * Creates simulated network components (that are links and routers) along 
	 * a path if these do not exist yet.  
	 * @param graph the graph in which the components are created
	 * @param path list of vertices determining the path
	 * @param passage list of components belonging to the path
	 */
	private void makePath(Graph graph, List<Vertex> path, List<SimComponent> route)
	{	
		// get/create routers along the path
		LinkedList<SimRouter> routers = new LinkedList<SimRouter>();
		for (Vertex v : path)
		{
			Object o = v.get();
			// add existing router
			if (o instanceof SimRouter)
			{
				routers.addLast((SimRouter)o);
			}
			// add existing router from node
			else if (o instanceof Node)
			{
				NetworkRouting r = (NetworkRouting)((Node)o).getProtocol(pid);
				// initialization is done when associating the vertex
				routers.addLast(r.getHost());
			}
			// or clone new router from prototype
			else if (o==null)
			{
				SimRouter r = (SimRouter)router.clone();
				r.init(v);
				v.set(r);
				routers.addLast(r);
			}
			else
			{
				LOG.error("unknown object '" + o + "' associated with graph " 
						+ "vertex '" + v + "'");
			}
		}
		
		// get/create links along the path
		LinkedList<SimLink> links = new LinkedList<SimLink>();
		Vertex head = null, tail = null;
		for (Vertex v : path)
		{
			tail = head;
			head = v;
			
			if (head==null || tail == null) 
			{
				continue;
			}
			
			Edge e = graph.getEdge(head, tail);
//			LOG.debug("head: "+head + " , tail: "+tail);
			Object o = e.get();
			// add existing link
			if (o instanceof SimLink)
			{
				links.addLast((SimLink)o);
			}
			// or clone new link from prototype
			else if (o == null)
			{
				// get source sink
				SimRouter s = null;
				Object p = tail.get();
				if (p instanceof SimRouter)
				{
					s = (SimRouter)p;
				}
				else if (p instanceof Node)
				{
					Node n = (Node)p;
					NetworkRouting r = (NetworkRouting)n.getProtocol(pid);
					s = r.getHost();
				}
				else
				{
					LOG.error("unknown object '" + p + "' associated with " 
							+ "graph vertex");
				}

				// get destination sink
				SimRouter d = null;
				Object q = tail.get();
				if (q instanceof SimRouter)
				{
					d = (SimRouter)q;
				}
				else if (q instanceof Node)
				{
					Node n = (Node)q;
					NetworkRouting r = (NetworkRouting)n.getProtocol(pid);
					d = r.getHost();
				}
				else
				{
					LOG.error("unknown object '" + q + "' associated with " 
							+ "graph vertex");
				}

				// clone and init link
				SimLink l = (SimLink)link.clone();
				l.init(s,d,e);
				links.addLast(l);
				e.set(l);
			}
			else
			{
				LOG.error("unknown object '" + o + "' associated with graph " 
						+ "edge");
			}
		}

		// populate route
		while (!routers.isEmpty() || !links.isEmpty())
		{
			if (!routers.isEmpty())
			{
				route.add(routers.removeFirst());
			}
			if (!links.isEmpty())
			{
				route.add(links.removeFirst());
			}
		}
	}

	
	// BrokerEngine implementation --------------------------------------------
	// ------------------------------------------------------------------------
	
	@Override
	public Object getKey() { return KEY; }
	@Override
	public Broker getBroker() { return broker; }
	@Override
	public void setBroker(Broker broker) { this.broker = broker; }
	@Override
	public void init() { }
	@Override
	public void activate(){ }
	@Override
	public void passivate() { }
	@Override
	public void exit() { }
	@Override
	public void process(Event event, EventProcessor source) { }
	@Override
	public Object plug(Object obj) { return obj; }
}
