package rebeca.peernet;

import java.util.*;

import org.apache.log4j.*;

import peersim.config.*;
import peersim.core.*;

import rebeca.peersim.*;
import rebeca.util.*;


public class TreeBuilder implements Control 
{
	// parameters and constants -----------------------------------------------
	// ------------------------------------------------------------------------

	private static final Logger LOG = Logger.getLogger(TreeBuilder.class);
	
	private static enum Strategy {LIST, MINIMUM, MAXIMUM, RANDOM};
	
	public static final String PAR_NETWORK = "network";
	public static final String PAR_STRATEGY = "strategy";
	public static final String PAR_EDGING = "edging";
	public static final String PAR_LINKING = "linking";
	
	public static final String PAR_COST = "cost";
	public static final String PAR_OVERLAY = "overlay";
	public static final String PAR_SEED = "seed";
	public static final String PAR_TRANSPORT = "transport";
	
	private static final String DEFAULT_STRATEGY = Strategy.LIST.toString(); 
	private static final long DEFAULT_SEED = 0;
	
	
	// fields -----------------------------------------------------------------
	// ------------------------------------------------------------------------
	
	private Strategy strategy;
	
	private int nid;
	private int oid;
	private int tid;
	
	private String cost;
	private long seed;
	
	// instantiation ----------------------------------------------------------
	// ------------------------------------------------------------------------

	/**
	 * Reads configuration parameter.
	 */
	public TreeBuilder(String prefix)
	{
		this.nid = Configuration.getPid(prefix + "." + PAR_NETWORK);
		String strategy = Configuration.getString(prefix + "." 
				+ PAR_STRATEGY, DEFAULT_STRATEGY);
		for (Strategy s : Strategy.values())
		{
			if (s.toString().equalsIgnoreCase(strategy))
			{
				this.strategy = s;
				break;
			}	
		}		
		
		oid = Configuration.getBoolean(prefix + "." + PAR_EDGING, false) ?
				Configuration.getPid(prefix + "." + PAR_OVERLAY) : -1;		
		tid = Configuration.getBoolean(prefix + "." + PAR_LINKING, false) ?
				Configuration.getPid(prefix + "." + PAR_TRANSPORT) : -1;

		cost = Configuration.getString(prefix + "." + PAR_COST, PAR_COST);
		seed = Configuration.getLong(prefix + "." + PAR_SEED, 
				CommonState.r.getLastSeed()^DEFAULT_SEED);
//		LOG.debug("OID: "+oid);
//		LOG.debug("TID: "+tid);
//		LOG.debug("cost: "+cost);
//		LOG.debug("edging: "+Configuration.getBoolean(prefix + "." + PAR_EDGING));
	}

	
	// getters and setters ----------------------------------------------------
	// ------------------------------------------------------------------------
	
	public String getCost()
	{
		return cost;
	}
	
	
	// Control implementation -------------------------------------------------
	// ------------------------------------------------------------------------

	@Override
	public boolean execute()
	{
		Node node = Network.prototype;
		Networking net = (Networking)node.getProtocol(nid);
		Graph graph = net.getGraph();

		// prepare overlay when enabled
		if (oid!=-1) addOverlayVertices();
		
		// get list of edges with ordering determined by strategy
		ArrayList<Graph.Edge> edges = null;
		switch (strategy) {
		case LIST:
			edges = list();
			break;
		case RANDOM:
			edges = random();
			break;
		case MINIMUM:
			edges = minimum();
			break;
		case MAXIMUM:
			edges = maximum();
			break;	
		}
		
		// adaptation of Kruskal's algorithm based on different sorting strategies
		// the part below checks that edges are only added into a tree
		Graph.Property component = graph.addProperty();
		int components = 0;
		for (Graph.Edge e : edges)
		{
			Graph.Vertex u = e.head();
			Graph.Vertex v = e.tail();
			
			// create a component for every new vertex
			Object o = u.get(component);
			if (o==null)
			{
				o = components++;
				u.set(component,o);
			}
			int c = (Integer)o;
			
			Object p = v.get(component);
			if (p==null)
			{
				p = components++;
				v.set(component,p);
			}
			int d = (Integer)p;
			
			// allow edges only between two different components
			if (c==d) continue;
			edge(e);
			link(e);
			
			// join two components
			int min = Math.min(c, d);
			int max = Math.max(c, d);
			for (Graph.Vertex w : graph.vertices())
			{
				Object q = w.get(component);
				if (q==null) continue;
				int i = (Integer)q;
				if (i!=max) continue;
				w.set(component,min);
			}
		}
		graph.removeProperty(component);
		
		return false;
	}
	

	// strategies -------------------------------------------------------------
	// ------------------------------------------------------------------------
	
	public ArrayList<Graph.Edge> list()
	{
		ArrayList<Graph.Edge> edges = new ArrayList<Graph.Edge>(Network.size()-1);
		
		Networking net = (Networking)Network.prototype.getProtocol(nid);
		for (int i=0; i<Network.size()-1; i++)
		{
			Graph.Edge e = net.getEdge(Network.get(i),Network.get(i+1));
			if (e==null)
			{
				LOG.error("missing edge between node " + i + " and " + (i+1));
			}
			edges.add(e);
		}
		return edges;
	}
	
	public ArrayList<Graph.Edge> random()
	{
		Node node = Network.prototype;
		Networking net = (Networking)node.getProtocol(nid);
		Graph graph = net.getGraph();
		Random rnd = new Random(seed);
		
		ArrayList<Graph.Edge> edges = new ArrayList<Graph.Edge>();
		edges.addAll(graph.edges());
		Collections.shuffle(edges, rnd);
		
		return edges;
	}

	public ArrayList<Graph.Edge> minimum()
	{
		Comparator<Graph.Edge> cmp = new Comparator<Graph.Edge>() 
		{		
			public int compare(Graph.Edge e1, Graph.Edge e2) 
			{
				Double d1 = (Double)e1.get(cost);
				Double d2 = (Double)e2.get(cost);
				return Double.compare(d1, d2);
			}
		};
		
		Node node = Network.prototype;
		Networking net = (Networking)node.getProtocol(nid);
		Graph graph = net.getGraph();
		
		ArrayList<Graph.Edge> edges = new ArrayList<Graph.Edge>();
		edges.addAll(graph.edges());
		Collections.sort(edges, cmp);
		
		return edges;
	}
	
	public ArrayList<Graph.Edge> maximum()
	{
		Comparator<Graph.Edge> cmp = new Comparator<Graph.Edge>() 
		{		
			public int compare(Graph.Edge e1, Graph.Edge e2) 
			{
				Double d1 = (Double)e1.get(cost);
				Double d2 = (Double)e2.get(cost);
				return -Double.compare(d1, d2);
			}
		};
		
		Node node = Network.prototype;
		Networking net = (Networking)node.getProtocol(nid);
		Graph graph = net.getGraph();
		
		ArrayList<Graph.Edge> edges = new ArrayList<Graph.Edge>();
		edges.addAll(graph.edges());
		Collections.sort(edges, cmp);
		
		return edges;
	}
	
	
	// helpers ----------------------------------------------------------------
	// ------------------------------------------------------------------------
	
	public void addOverlayVertices()
	{
		// WARNING: assumes that the overlay is an NetworkGraph!
		Node node = Network.prototype;
		NetworkGraph overlay = (NetworkGraph)node.getProtocol(oid);
		Networking underlay = (Networking)node.getProtocol(nid);
		overlay.addOverlayVertices(underlay.nodes());
	}
	
	public void edge(Graph.Edge e)
	{
		// check if edging is enabled
		if (oid==-1) return;
		
		// WARNING: assumes that the overlay is a NetworkGraph!
		Node n = (Node)e.tail().get();
		Node m = (Node)e.head().get();
		NetworkGraph net = (NetworkGraph) n.getProtocol(oid);
		net.addOverlayEdge(net.getVertex(n), net.getVertex(m),e);
	}
	
	public void link(Graph.Edge e)
	{
		// check if linking is enabled
		if (tid==-1) return;
		
		Node n = (Node)e.tail().get();
		Node m = (Node)e.head().get();
		NetworkTransport transport = (NetworkTransport)n.getProtocol(tid);
		transport.connect(new PeerSimContactProfile(n,m));
	}
}
