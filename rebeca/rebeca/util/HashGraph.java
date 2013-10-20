package rebeca.util;

import java.util.*;


public class HashGraph implements Graph 
{
	// fields -----------------------------------------------------------------
	// ------------------------------------------------------------------------
	
	// graph and associated properties
	ElementPropertyValueMap<Graph.Vertex,Graph.Vertex,Graph.Edge> net;
	ElementPropertyValueMap<Graph.Element,Graph.Property,Object> map;
	
	// vertices, property keys, edge count and stuff
	List<Graph.Vertex> vertices;
	Map<String,Graph.Property> properties;
	int edges;
	int counter;
	
	// TODO parzy provide support for directed graphs
	boolean directed;
	
	
	// instantiation ----------------------------------------------------------
	// ------------------------------------------------------------------------

	public HashGraph()
	{
		net = new ElementPropertyValueMap<Graph.Vertex,Graph.Vertex,Graph.Edge>();
		map = new ElementPropertyValueMap<Graph.Element,Graph.Property,Object>();
		// TODO parzy check whether we should be faster here 
		vertices = new ArrayList<Graph.Vertex>();
		properties = new HashMap<String,Graph.Property>();
		edges = 0;
		directed = false;
		counter = 0;
	}
	
	
	// vertex management ------------------------------------------------------
	// ------------------------------------------------------------------------
		
	protected class Element implements Graph.Element
	{
		Object element;
		int id;
		
		public Element()
		{
			this(null);
		}
		
		public Element(Object o)
		{
			this.element = o;
			this.id = counter++;
		}
		
		public Graph network()
		{
			return HashGraph.this;
		}
		
		public Graph underlay()
		{
			return null;
		}
		
		public Object set(Object o)
		{
			Object old = this.element;
			this.element = o;
			return old;
		}
		
		public Object set(Graph.Property p, Object o)
		{
			return putField(this,p,o);
		}
		
		public Object set(String property, Object o)
		{
			Graph.Property p = getProperty(property);
			return p!=null ? set(p,o) : null;
		}
		
		public Object get()
		{
			return element;
		}
		
		public Object get(Graph.Property p)
		{
			return getField(this,p);
		}
		
		public Object get(String property)
		{
			Graph.Property p = getProperty(property);
			return p!=null ? get(p) : null;
		}
		
		public Object remove()
		{
			return set(null);
		}
		
		public Object remove(Graph.Property p)
		{
			return removeField(this,p);
		}
		
		public Object remove(String property)
		{
			Graph.Property p = getProperty(property);
			return p!=null ? remove(p) : null;
		}
		
		public Map<Graph.Property,Object> propertyMap()
		{
			return getPropertyMap(this);
		}
		
//		@Override
//		public int hashCode()
//		{
//			return id;
//		}
	}
	
	protected class Vertex extends Element implements Graph.Vertex
	{	
		public Vertex()
		{
			super();
		}
		
		public Vertex(Object obj)
		{
			super(obj);
		}
		
		public Graph.Vertex base()
		{
			return null;
		}
		
		@Override
		public String toString()
		{
			return "vertex #"+id;
		}
	}
	
	protected Graph.Vertex createVertex()
	{
		return new Vertex();
	}
	
	protected Graph.Vertex createVertex(Object obj)
	{
		return new Vertex(obj);
	}
	
	@Override
	public Graph.Vertex addVertex()
	{
		Graph.Vertex v = createVertex();
		vertices.add(v);
		return v;
	}
	
	@Override
	public Graph.Vertex addVertex(Object obj)
	{
		Graph.Vertex v = addVertex();
		v.set(obj);
		return v;
	}

	@Override
	public Object removeVertex(Graph.Vertex v)
	{
		net.removeElement(v);
		net.removeProperty(v);
		map.removeElement(v);
		vertices.remove(v);
		return v.get();
	}

	@Override
	public int vertexCount()
	{
		return vertices.size();
	}
	
	// edge management --------------------------------------------------------
	// ------------------------------------------------------------------------
	
	protected class Edge extends Element implements Graph.Edge
	{
		protected Graph.Vertex head, tail;
		
		public Edge(Graph.Vertex head, Graph.Vertex tail)
		{
			this(head,tail,null);
		}
		
		public Edge(Graph.Vertex head, Graph.Vertex tail, Object obj)
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
		
		public Graph.Edge base()
		{
			return null;
		}
		
		@Override
		public String toString()
		{
			return "edge["+tail+"->"+head+"]";
		}
	}

	protected Graph.Edge createEdge(Graph.Vertex head, Graph.Vertex tail)
	{
		return new Edge(head,tail);
	}
	
	protected Graph.Edge createEdge(Graph.Vertex head, Graph.Vertex tail, Object obj)
	{
		return new Edge(head,tail,obj);
	}

	@Override
	public Graph.Edge addEdge(Graph.Vertex u, Graph.Vertex v)
	{
		//
		if (u.network() != this || v.network() != this)
		{
			throw new IllegalArgumentException();
		}
		
		// first try returning existing link
		Graph.Edge link = getEdge(u,v);
		if (link != null)
		{
			return link;
		}
		
		// otherwise create a new one and add it (twice if graph is undirected)
		link = createEdge(u,v);
//		System.out.println("Edge "+link+" hash "+net.hash(u,v));
		net.put(u,v,link);
		if (!directed && u!=v) net.put(v,u,link);
		edges++;
		
		return link;
	}
	
	@Override
	public Graph.Edge addEdge(Graph.Vertex u, Graph.Vertex v, Object obj)
	{
		// get or add edge, respectively, and replace object it contains
		Graph.Edge edge = addEdge(u,v);
		edge.set(obj);
		return edge;
	}
		
	@Override
	public Graph.Edge getEdge(Graph.Vertex n, Graph.Vertex m)
	{
		return net.get(n,m);
	}

	@Override
	public boolean hasEdge(Graph.Vertex n, Graph.Vertex m)
	{
		return getEdge(n,m) != null;
	}
	
	@Override
	public Object removeEdge(Graph.Vertex n, Graph.Vertex m)
	{
		// remove link (twice if graph is undirected)
		Graph.Edge link = net.remove(n,m);
		if (!directed) net.remove(m,n);
		edges--;
		map.removeElement(link);
		return link.get();
	}
	
	@Override
	public Object removeEdge(Graph.Edge link)
	{
		return removeEdge(link.head(),link.tail());
	}
	
	@Override
	public int edgeCount()
	{
		return edges;
	}

	
	// property management ----------------------------------------------------
	// ------------------------------------------------------------------------
	
	protected class Property implements Graph.Property
	{
		String name;
		
		public Property()
		{ 
			this(null);
		}
		
		public Property(String name)
		{
			this.name = name;
		}
		
		public Graph network()
		{
			return HashGraph.this;
		}
		
		public String getName()
		{
			return name;
		}
		
		public Map<Graph.Element, Object> elementMap()
		{
			return map.elementMap(this);
		}
	}
	
	protected Graph.Property createProperty()
	{
		return new Property();
	}
	
	protected Graph.Property createProperty(String name)
	{
		return new Property(name);
	}
	
	@Override
	public Graph.Property addProperty()
	{
		return createProperty();
	}
	
	@Override
	public Graph.Property addProperty(String name)
	{
		// check if property already exists
		Graph.Property property = properties.get(name);
		if (property != null)
		{
			return property;
		}
		
		// otherwise create a new one
		property = createProperty(name);
		properties.put(name,property);
		return property;
	}
	
	@Override
	public Graph.Property getProperty(String name)
	{
		return properties.get(name);
	}
	
	@Override	
	public boolean hasProperty(String name)
	{
		return getProperty(name) != null;
	}

	@Override
	public void removeProperty(String name)
	{
		Graph.Property p = getProperty(name);
		if (p != null)
		{
			removeProperty(p);
		}
	}
	
	@Override
	public void removeProperty(Graph.Property p)
	{
		if (p.network() == this)
		{
			map.removeProperty(p);
			if (p.getName() != null)
			{
				properties.remove(p.getName());
			}
		}
	}
		

	// field management -------------------------------------------------------
	// ------------------------------------------------------------------------
	
	@Override
	public Object putField(Graph.Vertex n, Graph.Property p, Object o)
	{
		return putField((Element)n, p, o);
	}
	
	@Override
	public Object putField(Graph.Edge l, Graph.Property p, Object o)
	{
		return putField((Element)l, p, o);
	}

	protected Object putField(Graph.Element e, Graph.Property p, Object o)
	{
		if (e.network() != this || p.network() != this)
		{
			throw new IllegalArgumentException();
		}
		return map.put(e, p, o);
	}

	@Override
	public Object getField(Graph.Vertex n, Graph.Property p)
	{
		return getField((Graph.Element)n, p);
	}
	
	@Override
	public Object getField(Graph.Edge l, Graph.Property p)
	{
		return getField((Graph.Element)l, p);
	}
	
	protected Object getField(Graph.Element e, Graph.Property p)
	{
		return map.get(e, p);
	}
	
	@Override
	public boolean containsField(Graph.Vertex n, Graph.Property p)
	{
		return containsField((Graph.Element)n,p);
	}
	
	@Override
	public boolean containsField(Graph.Edge l, Graph.Property p)
	{
		return containsField((Graph.Element)l,p);
	}
	
	protected boolean containsField(Graph.Element e, Graph.Property p)
	{
		return map.contains(e, p);
	}
	
	@Override
	public Object removeField(Graph.Vertex n, Graph.Property p)
	{
		return removeField((Element)n,p);
	}
	
	@Override
	public Object removeField(Graph.Edge l, Graph.Property p)
	{
		return removeField((Graph.Element)l,p);
	}
	
	protected Object removeField(Graph.Element e, Graph.Property p)
	{
		return map.remove(e, p);
	}

	
	// property views ---------------------------------------------------------
	// ------------------------------------------------------------------------
	
	private Set<Graph.Property> propertySet = null;

	public Set<Graph.Property> propertySet()
	{
		return propertySet!=null ? propertySet : (propertySet = new PropertySet());
	}
	
	private class PropertySet extends AbstractSet<Graph.Property>
	{
		public int size()
		{
			return properties.size();
		}
		
		public Iterator<Graph.Property> iterator()
		{
			return new PropertyIterator();
		}
		
		public boolean contains(Object o)
		{
			if ( !(o instanceof Graph.Property))
			{
				return false;
			}
			
			Graph.Property p = (Graph.Property)o;
			return properties.containsKey(p.getName());
		}
	}
	
	private class PropertyIterator implements Iterator<Graph.Property>
	{
		Iterator<Graph.Property> props;
		Graph.Property last;
		
		public PropertyIterator()
		{
			this.props = properties.values().iterator();
			this.last = null;
		}
		
		public boolean hasNext()
		{
			return props.hasNext();
		}
		
		public Graph.Property next()
		{
			return last = props.next();
		}
		
		public void remove()
		{
			if ( last==null )
			{
				throw new NoSuchElementException();
			}
			
			removeProperty(last);
		}
	}
	
	// TODO maybe publish method via Graph interface
	protected Map<Graph.Property,Object> getPropertyMap(Graph.Element e)
	{
		return map.propertyMap(e);
	}
		
	// TODO maybe publish method via Graph interface
	protected Map<Graph.Element,Object> getElementMap(Graph.Property p)
	{
		return map.elementMap(p);
	}

	
	// vertex view ------------------------------------------------------------
	// ------------------------------------------------------------------------
	
	protected VertexSet vertexSet;

	public Set<Graph.Vertex> vertices()
	{
		return vertexSet != null ? vertexSet : (vertexSet = new VertexSet());
	}
	
	protected class VertexSet extends AbstractSet<Graph.Vertex>
	{
		public int size()
		{
			return vertexCount();
		}
		
		public Iterator<Graph.Vertex> iterator()
		{
			return new VertexIterator();
		}
	}
	
	protected class VertexIterator implements Iterator<Graph.Vertex>
	{
		private int last = -1;
		
		public boolean hasNext()
		{
			return last+1 < vertexCount();
		}
		
		public Graph.Vertex next()
		{
			last++;
			return vertices.get(last);
		}
		
		public void remove()
		{
			removeVertex(vertices.get(last));
			last--;
		}
	}
	

	// edge view --------------------------------------------------------------
	// ------------------------------------------------------------------------
	
	private Set<Graph.Edge> edgeSet = null;
	public Set<Graph.Edge> edges()
	{
		return edgeSet != null ? edgeSet : (edgeSet = new EdgeSet());
	}
	
	protected class EdgeSet extends AbstractSet<Graph.Edge>
	{
		public int size()
		{
			return edges;
		}
		
		public Iterator<Graph.Edge> iterator()
		{
			return new EdgeIterator();
		}
	}
	
	protected class EdgeIterator implements Iterator<Graph.Edge>
	{
		protected Iterator<Graph.Vertex> heads;
		protected Iterator<Graph.Edge> links;

		Graph.Edge last;
		Graph.Vertex head;
		Graph.Edge next;
		
		EdgeIterator()
		{
			heads = net.elementSet().iterator();
			head = heads.hasNext() ? heads.next() : null;
			links = head != null ? net.propertyMap(head).values().iterator() : null;
			last = next = null;
		}
		
		public boolean hasNext()
		{
			if (next != null)
			{
				return true;
			}
			
			while ( head != null )
			{
				while (links.hasNext())
				{
					next = links.next();
					if (next.head() == head)
					{
						return true;
					}
				}
				
				head = heads.hasNext() ? heads.next() : null;
				links = head != null ? net.propertyMap(head).values().iterator() : null;
			}

			next = null;
			return false;
		}
		
		public Graph.Edge next()
		{
			if (!hasNext())
			{
				throw new NoSuchElementException();
			}
			last = next;
			next = null;
			return last;
		}
		
		public void remove()
		{
			removeEdge(last);
		}
	}
	
	
	// neighboring views ------------------------------------------------------
	// ------------------------------------------------------------------------
	
	@Override
	public int degree(Graph.Vertex n)
	{
		int degree = net.propertyMap(n).size();
		// self loops count twice
		if (net.contains(n,n))
		{
			degree++;
		}	
		return degree;
	}
	
	@Override
	public Set<Graph.Edge> edges(Graph.Vertex n)
	{
		return new IncidenceSet(n);
	}

	protected class IncidenceSet extends AbstractSet<Graph.Edge>
	{
		private Graph.Vertex node;
		private Map<Graph.Vertex,Graph.Edge> map;
		
		public IncidenceSet(Graph.Vertex node)
		{
			this.node = node;
			this.map = net.propertyMap(node);
		}
		
		public int size()
		{
			return map != null ? map.size() : 0;
		}
		
		public Iterator<Graph.Edge> iterator()
		{
			return new IncidenceIterator(node, map);
		}
	}
	
	// TODO parzy extend functionality to handle directed graphs
	protected class IncidenceIterator implements Iterator<Graph.Edge>
	{
		private Iterator<Graph.Edge> links;
		private Graph.Edge last;
		
		IncidenceIterator(Graph.Vertex node, Map<Graph.Vertex,Graph.Edge> map)
		{
			this.links = map != null ? map.values().iterator() : null;
			this.last = null;
		}
		
		public boolean hasNext()
		{
			return links != null ? links.hasNext() : false;
		}
		
		public Graph.Edge next()
		{
			if (links == null)
			{
				throw new NoSuchElementException();
			}
			
			return (last = links.next());
		}
		
		public void remove()
		{
			if (last == null)
			{
				throw new NoSuchElementException();
			}
			removeEdge(last);
			last = null;
		}
	}
	
	@Override
	public Set<Graph.Vertex> vertices(Graph.Vertex n)
	{
		return new AdjacencySet(n);
	}

	protected class AdjacencySet extends AbstractSet<Graph.Vertex>
	{
		private Graph.Vertex node;
		private Map<Graph.Vertex,Graph.Edge> map;
		
		public AdjacencySet(Graph.Vertex node)
		{
			this.node = node;
			this.map = net.propertyMap(node);
		}
		
		public int size()
		{
			return map != null ? map.size() : 0;
		}
		
		public Iterator<Graph.Vertex> iterator()
		{
			return new AdjacencyIterator(node, map);
		}
	}
	
	// TODO parzy extend functionality to handle directed graphs
	protected class AdjacencyIterator implements Iterator<Graph.Vertex>
	{
		private Graph.Vertex node;
		private Iterator<Graph.Edge> links;
		private Graph.Edge last;
		
		AdjacencyIterator(Graph.Vertex node, Map<Graph.Vertex,Graph.Edge> map)
		{
			this.node = node;
			this.links = map != null ? map.values().iterator() : null;
			this.last = null;
		}
		
		public boolean hasNext()
		{
			return links != null ? links.hasNext() : false;
		}
		
		public Graph.Vertex next()
		{
			if (links == null)
			{
				throw new NoSuchElementException();
			}
			
			last = links.next();
			return last.head() == node ? last.tail() : last.head();
		}
		
		public void remove()
		{
			if (last == null)
			{
				throw new NoSuchElementException();
			}

			removeVertex(last.head() == node ? last.tail() : last.head());
			last = null;
		}
	}	
}
