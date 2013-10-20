package rebeca.util;

import java.util.*;



public class OverlayGraph extends HashGraph 
{
	// instantiation ----------------------------------------------------------
	// ------------------------------------------------------------------------
	
	public OverlayGraph() 
	{ 
		super();
	}
	
	public OverlayGraph(Collection<Graph.Vertex> nodes) 
	{
		
	}
		
	public OverlayGraph(Graph net)
	{
		this(net.vertices(),net.edges());
	}
	
	public OverlayGraph(Collection<Graph.Vertex> nodes, Collection<Graph.Edge> links)
	{
		Map<Graph.Vertex,Graph.Vertex> map = new HashMap<Graph.Vertex,Graph.Vertex>();
		
		if (nodes != null)
		{
			for (Graph.Vertex n : nodes)
			{
				if (!map.containsKey(n))
				{
					map.put(n, addVertex(n));
				}
			}
		}
		
		if (links != null)
		{
			for (Graph.Edge l : links)
			{
				Graph.Vertex n = map.get(l.head());
				if (n==null)
				{
					map.put(l.head(), n=addVertex(l.head()));
				}
				Graph.Vertex m = map.get(l.tail());
				if (m==null)
				{
					map.put(l.tail(), m=addVertex(l.tail()));
				}
				addEdge(n,m,l);
			}
		}
	}
	
	
	// graph elements ---------------------------------------------------------
	// ------------------------------------------------------------------------
	
	protected class OverlayElement extends HashGraph.Element
	{
		public OverlayElement()
		{
			super(null);
		}
		
		public OverlayElement(Object o)
		{
			super(o);
		}
		
		@Override
		public Graph network()
		{
			return OverlayGraph.this;
		}
		
		@Override
		public Graph underlay()
		{
			if (element instanceof Graph.Element)
			{
				return ((Graph.Element)element).underlay();
			}
			return null;
		}

		@Override
		public Object set(Object o)
		{
			if (element instanceof Graph.Element)
			{
				return ((Graph.Element)element).set(o);
			}
			return super.set(o);
		}

		@Override
		public Object get()
		{
			if (element instanceof Graph.Element)
			{
				return ((Graph.Element)element).get();
			}
			return super.get();
		}

		@Override
		public Object remove()
		{
			if (element instanceof Graph.Element)
			{
				return ((Graph.Element)element).remove();
			}
			return super.set(null);
		}
		
		@Override
		public Object get(Graph.Property p) 
		{
			if (p.network()==OverlayGraph.this)
			{
				return getField(this,p);
			}
			if (element instanceof Graph.Element)
			{
				return ((Graph.Element)element).get(p);
			}
			return null;
		}
		
		// TODO parzy check whether resolving properties this way causes unwanted side effects
		@Override 
		public Object get(String property)
		{
			// try resolving property in this graph
			Graph.Property p = getProperty(property);
			if (p!=null) 
			{
				return get(p);
			}
			// try resolving property in underlay
			if (element instanceof Graph.Element) 
			{
				return ((Graph.Element)element).get(property);
			}
			// give up
			return null;
		}

		@Override
		public Object remove(Graph.Property p) 
		{
			if (p.network()==OverlayGraph.this)
			{
				return removeField(this,p);
			}
			if (element instanceof Graph.Element)
			{
				return ((Graph.Element)element).remove(p);
			}
			return null;
		}

		@Override
		public Object set(Graph.Property p, Object o) {
			if (p.network()==OverlayGraph.this)
			{
				return putField(this,p,o);
			}
			if (element instanceof Graph.Element)
			{
				return ((Graph.Element)element).set(p,o);
			}
			return null;
		}

		public Map<Graph.Property,Object> propertyMap()
		{
			return map.propertyMap(this);
		}
	}
	
	protected class OverlayVertex extends OverlayElement implements Graph.Vertex
	{
		public OverlayVertex()
		{
			super();
		}
		
		public OverlayVertex(Object obj)
		{
			super(obj);
		}
		
		@Override
		public  Graph.Vertex base()
		{
			return element instanceof Graph.Vertex ? (Graph.Vertex)element : null;
		}
		
		@Override
		public String toString()
		{
			return "overlay vertex #"+id;
		}
	}

	@Override
	protected Graph.Vertex createVertex() 
	{
		return new OverlayVertex();
	}

	@Override
	protected Graph.Vertex createVertex(Object obj) 
	{
		return new OverlayVertex(obj);
	}
	
	
	
	// edge management --------------------------------------------------------
	// ------------------------------------------------------------------------

	protected class OverlayEdge extends OverlayElement implements Graph.Edge
	{
		protected Graph.Vertex head, tail;
		
		public OverlayEdge(Graph.Vertex head, Graph.Vertex tail)
		{
			this(head,tail,null);
		}
		
		public OverlayEdge(Graph.Vertex head, Graph.Vertex tail, Object obj)
		{
			super(obj);
			this.head = head;
			this.tail = tail;
		}
		
		public Graph.Vertex head()
		{
			return head;
		}
		
		public Graph.Vertex tail()
		{
			return tail;
		}
		
		public Graph.Vertex[] nodes()
		{
			return new Graph.Vertex[]{head,tail};
		}
		
		@Override
		public Graph.Edge base()
		{
			return element instanceof Graph.Edge ? (Graph.Edge)element : null;
		}
		
		@Override
		public String toString()
		{
			return "overlay edge["+tail+"->"+head+"]";
		}
	}
	
	@Override
	protected Graph.Edge createEdge(Graph.Vertex head, Graph.Vertex tail) 
	{
		return new OverlayEdge(head, tail);
	}

	@Override
	protected Graph.Edge createEdge(Graph.Vertex head, Graph.Vertex tail, Object obj) 
	{
		return new OverlayEdge(head, tail, obj);
	}
}
