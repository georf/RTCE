package rebeca.util;

import java.util.Set;

public interface Graph 
{
	public interface Element
	{
		public Object get();
		public Object get(Property p);
		public Object get(String property);
		public Object set(Object obj);
		public Object set(Property p, Object obj);
		public Object set(String property, Object obj);
		public Object remove();
		public Object remove(Property p);
		public Object remove(String property);
		public Graph network();
		// TODO parzy factor out into new interface
		public Graph underlay();
	}
	
	// vertex management ------------------------------------------------------
	// ------------------------------------------------------------------------

	public interface Vertex extends Element 
	{ 
		// TODO parzy factor out into new interface
		public Vertex base();
	}
	
	public Vertex addVertex();
	public Vertex addVertex(Object obj);
	public Object removeVertex(Vertex n);
	public int vertexCount();
	public Set<Vertex> vertices();
	
	
	// edge management --------------------------------------------------------
	// ------------------------------------------------------------------------
	
	public interface Edge extends Element
	{
		public Vertex head();
		public Vertex tail();
		public Vertex[] nodes();
		public Edge base();
	}
	
	public Edge addEdge(Vertex n, Vertex m);
	public Edge addEdge(Vertex n, Vertex m, Object obj);
	public Edge getEdge(Vertex n, Vertex m);
	public boolean hasEdge(Vertex n, Vertex m);
	public Object removeEdge(Vertex n, Vertex m);
	public Object removeEdge(Edge link);
	public int edgeCount();
	public Set<Edge> edges();

	
	// neighboring views ------------------------------------------------------
	// ------------------------------------------------------------------------
	
	public int degree(Vertex n);
	public Set<Vertex> vertices(Vertex n);
	public Set<Edge> edges(Vertex n);

	
	// property management ----------------------------------------------------
	// ------------------------------------------------------------------------
	
	public interface Property
	{
		public Graph network();
		public String getName();
	}

	public Property addProperty(); // anonymous temporary property
	public Property addProperty(String name);
	public Property getProperty(String name);
	public boolean hasProperty(String name);
	public void removeProperty(String name);
	public void removeProperty(Property p);
	public Set<Property> propertySet();
	
	
	// field management -------------------------------------------------------
	// ------------------------------------------------------------------------
	
	public Object putField(Vertex n, Property p, Object o);
	public Object putField(Edge l, Property p, Object o);
	public Object getField(Vertex n, Property p);
	public Object getField(Edge l, Property p);
	public boolean containsField(Vertex n, Property p);
	public boolean containsField(Edge l, Property p);
	public Object removeField(Vertex n, Property p);
	public Object removeField(Edge l, Property p);
}