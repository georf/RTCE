package rebeca.peernet;

import java.util.*;

import org.apache.log4j.Logger;

import peersim.config.*;
import peersim.core.*;
import peersim.edsim.*;

import rebeca.util.Graph;
import rebeca.util.HashGraph;
import rebeca.util.OverlayGraph;
import rebeca.util.Graph.*;

public class NetworkGraph implements Cleanable, EDProtocol, Networking
{
	// parameters ---------------------------------------------------------
	//---------------------------------------------------------------------
	
	/** 
	 * Logger for Networking class. 
	 */
	private static final Logger LOG = Logger.getLogger(NetworkGraph.class);
	
	private static final String PAR_UNDERLAY = "underlay";

	
	// fields -------------------------------------------------------------
	// --------------------------------------------------------------------
	
	protected int pid;
	protected int uid;
	
	protected Node node;
	protected Vertex vertex;
	
	protected Graph graph;

	
	// instantiation ----------------------------------------------------------
	// ------------------------------------------------------------------------

	// @SuppressWarnings("unchecked")
	public NetworkGraph(String prefix)
	{
		// determine own node and protocol identifier
		node = CommonState.getNode();
		pid = CommonState.getPid();
		
		// are we an overlay?
		uid = Configuration.getPid(prefix + "." + PAR_UNDERLAY, -1);

		// TODO 
		graph = isOverlay() ? new OverlayGraph() : new HashGraph();
	}	
	
	@Override
	public Object clone()
	{
		
		try
		{
			NetworkGraph clone = (NetworkGraph) super.clone();
			clone.node = CommonState.getNode();
			return clone;
		} 
		catch (CloneNotSupportedException e)
		{
			return null;
		}
	}
	
	// Cleanable and EDProtocol implementation --------------------------------
	// ------------------------------------------------------------------------
	
	@Override
	public void onKill() { }
	@Override
	public void processEvent(Node node, int pid, Object event) { }

	
	// graphs and properties --------------------------------------------------
	// ------------------------------------------------------------------------

	@Override
	public Graph getGraph()
	{
		return graph;
	}
	
	@Override
	public Property getProperty(String name)
	{
		Graph.Property p = graph.getProperty(name);
		if (p==null && isOverlay())
		{
			NetworkGraph n = (NetworkGraph)node.getProtocol(uid);
			p = n.getProperty(name);
		}
		return p;
	}	

	
	// overlay management -----------------------------------------------------
	// ------------------------------------------------------------------------

	@Override
	public boolean isOverlay()
	{
		return uid != -1;
	}

	@Override
	public boolean hasUnderlay()
	{
		return isOverlay();
	}
	
	@Override
	public Networking getUnderlay()
	{
		return isOverlay() ? (Networking)node.getProtocol(uid) : null;
	}
	

	// property management ----------------------------------------------------
	// ------------------------------------------------------------------------
	
	/**
	 * 
	 * @param p
	 * @throws NullPointerException if this node is not associated with a 
	 *         vertex.
	 * @return
	 */
	public Object get(Property p)
	{
		return vertex.get(p);
	}
	
	
	// vertex management ------------------------------------------------------
	// ------------------------------------------------------------------------
	
	@Override
	public boolean hasVertex()
	{
		return vertex != null;
	}
	
	@Override
	public boolean hasVertex(Node n)
	{
		return ((NetworkGraph)n.getProtocol(pid)).hasVertex();
	}
	
	@Override
	public Vertex getVertex()
	{
		return vertex;
	}
	
	@Override
	public Vertex getVertex(Node n)
	{
		return ((NetworkGraph)n.getProtocol(pid)).getVertex();
	}
	
	@Override
	public void setVertex(Vertex v)
	{
		// remove old association, store vertex, and associate vertex with node
		if (vertex!=null) vertex.set(null);
		vertex = v;
		if (vertex!=null) vertex.set(node);
	}
	
	@Override
	public void setVertex(Vertex v, Node n)
	{
		((NetworkGraph)n.getProtocol(pid)).setVertex(v);
	}
	
	public Vertex addVertex()
	{
		Vertex v = getVertex();
		
//		if (v==null && isOverlay())
//		{
//			Vertex u = ((NetworkGraph)node.getProtocol(uid)).getVertex();
//			if (u!=null)
//			{
//				v = graph.addVertex(u);
//			}
//		}
//		
		if (v==null)
		{
			v = graph.addVertex();
			setVertex(v);
		}
		return v;

	}
	
	public Vertex addVertex(Node n)
	{
		return ((NetworkGraph)n.getProtocol(pid)).addVertex();
	}
	
	public void addVertices(Collection<Node> nodes)
	{
		for (Node n : nodes)
		{
			addVertex(n);
		}
	}
	
	public Vertex addOverlayVertex()
	{
		if (!isOverlay()) throw new UnsupportedOperationException();
		
		Vertex u = ((NetworkGraph)node.getProtocol(uid)).getVertex();
		if (u==null) throw new IllegalStateException("no underlay vertex found");
		
		Vertex v = getVertex();
		if (v==null)
		{
			v = graph.addVertex(u);
			setVertex(v);
		}
		return v;
	}
	
	public Vertex addOverlayVertex(Node n)
	{
		return ((NetworkGraph)n.getProtocol(pid)).addOverlayVertex();
	}
	
	public void addOverlayVertices(Collection<Node> nodes)
	{
		for (Node n : nodes)
		{
			addOverlayVertex(n);
		}
	}
	
	
	// edge and link management -----------------------------------------------
	// ------------------------------------------------------------------------

	@Override
	public boolean hasEdge(Vertex u, Vertex v)
	{
		return graph.hasEdge(u, v);
	}
	
	@Override
	public boolean hasEdge(Node n, Node m)
	{
		return hasEdge(getVertex(n), getVertex(m));
	}
	
	@Override
	public Edge getEdge(Vertex u, Vertex v)
	{
		return graph.getEdge(u, v);
	}
	
	@Override
	public Edge getEdge(Node n, Node m)
	{
		return getEdge(getVertex(n),getVertex(m));
	}
	
	@Override
	public boolean hasLink(Vertex u, Vertex v)
	{
		return getLink(u,v) != null;
	}
	
	@Override
	public boolean hasLink(Node n, Node m)
	{
		return hasLink(getVertex(n),getVertex(m));
	}
	
	@Override
	public SimLink getLink(Vertex u, Vertex v)
	{
		Edge e = getEdge(u,v);
		return (SimLink)(e!=null ? e.get() : null);
	}

	@Override
	public SimLink getLink(Node n, Node m)
	{
		return getLink(getVertex(n),getVertex(m));
	}

	@Override
	public void setLink(Vertex u, Vertex v, SimLink l)
	{
		Edge e = getEdge(u,v);
		if (e==null)
		{
			throw new IllegalStateException();
		}
		e.set(l);
	}

	@Override
	public void setLink(Node n, Node m, SimLink l)
	{
		setLink(getVertex(n),getVertex(m),l);
	}
	
	public Edge addEdge(Vertex u, Vertex v)
	{
		return graph.addEdge(u,v);
	}

	public Edge addOverlayEdge(Vertex u, Vertex v, Edge e)
	{
		if (!isOverlay()) throw new UnsupportedOperationException();
		return graph.addEdge(u, v, e);
	}

	public Edge addEdge(Node n, Node m)
	{
		return addEdge(getVertex(n),getVertex(m));
	}
	
	public Edge addOverlayEdge(Node n, Node m)
	{
		if (!isOverlay()) throw new UnsupportedOperationException();
		Edge e = getUnderlay().getEdge(n, m);
		return addOverlayEdge(getVertex(n),getVertex(m),e);
	}
	
	public Object addLink(Vertex u, Vertex v, SimLink l)
	{
		Edge e = addEdge(u,v);
		return e.set(l);
	}

	public Object addLink(Node n, Node m, SimLink l)
	{
		return addLink(getVertex(n), getVertex(m), l);
	}
	

	// node views -------------------------------------------------------------
	// ------------------------------------------------------------------------

	private Set<Node> nodes;
	public Set<Node> nodes()
	{
		return nodes!=null ? nodes : (nodes = new NodeSet());
	}
	
	private Set<Node> neighbors;
	public Set<Node> neighbors()
	{
		return neighbors!=null ? neighbors : (neighbors = new NodeSet(vertex));
	}
	
	class NodeSet extends AbstractSet<Node>
	{
		private Vertex vertex;
		
		public NodeSet() { this.vertex = null; }
		public NodeSet(Vertex vertex) { this.vertex = vertex; }
		
		public int size()
		{
			int size = 0;
			for (Iterator<Node> it = iterator(); it.hasNext(); it.next())
			{
				size++;
			}
			return size;
		}
		
		public Iterator<Node> iterator()
		{
			return new NodeIterator(vertex);
		}
	}

	class NodeIterator implements Iterator<Node>
	{
		private Iterator<Vertex> vertices;
		private Vertex next;

		public NodeIterator(Vertex vertex)
		{
			this.vertices = vertex==null ? graph.vertices().iterator() : graph.vertices(vertex).iterator();
			this.next = null;	
		}
		
		@Override
		public boolean hasNext() 
		{
			// next node already found?
			if (next != null && next.get() != null)
			{
				return true;
			}
			
			// skip vertices until one with associated node is found 
			while (vertices.hasNext())
			{
				next = vertices.next();
				if (next.get() != null)
				{
					return true;
				}
			}
			
			// no further nodes were found
			next = null;
			return false;
		}

		@Override
		public Node next() 
		{
			// ensure we have a next vertex
			if (!hasNext())
			{
				throw new NoSuchElementException();
			}
			
			// and return the associated node
			Node n = (Node)next.get();
			next = null;
			return n;
		}

		@Override
		public void remove() 
		{
			throw new UnsupportedOperationException();
		}
	}
	
	
	// link views -------------------------------------------------------------
	// ------------------------------------------------------------------------
	
	private Set<SimLink> links;
	public Set<SimLink> links()
	{
		return links!=null ? links : (links = new LinkSet());
	}
	
	private Set<SimLink> connections;
	public Set<SimLink> connections()
	{
		return connections!=null ? connections : (connections = new LinkSet(vertex));
	}
	
	public Set<SimLink> connections(Node n)
	{
		return ((NetworkGraph)n.getProtocol(pid)).connections();
	}
	
	class LinkSet extends AbstractSet<SimLink>
	{
		private Vertex vertex;
		
		public LinkSet() { this.vertex = null; }
		public LinkSet(Vertex vertex) { this.vertex = vertex; }
		
		@Override
		public int size() 
		{
			int size = 0;
			for (Iterator<SimLink> it = iterator(); it.hasNext(); it.next())
			{
				size++;
			}
			return size;
		}

		@Override
		public Iterator<SimLink> iterator() 
		{
			return new LinkIterator(vertex);
		}		
	}
	
	class LinkIterator implements Iterator<SimLink>
	{
		private Iterator<Edge> edges;
		private Edge next;

		public LinkIterator(Vertex vertex)
		{
			edges = vertex==null ? graph.edges().iterator() : graph.edges(vertex).iterator();
			next = null;
		}
		
		@Override
		public boolean hasNext() 
		{
			// next edge already found?
			if (next != null && next.get() != null)
			{
				return true;
			}
			
			// skip edges until one with associated link object is found 
			while (edges.hasNext())
			{
				next = edges.next();
				if (next.get() != null)
				{
					return true;
				}
			}
			
			// no further edges were found
			next = null;
			return false;
		}
		
		@Override
		public SimLink next() 
		{
			// ensure we have a next edges
			if (!hasNext())
			{
				throw new NoSuchElementException();
			}
			
			// and return the associated link object
			Object o = next.get();
			next = null;
			return (SimLink)o;
		}

		@Override
		public void remove() 
		{
			throw new UnsupportedOperationException();
		}
	}	
}
