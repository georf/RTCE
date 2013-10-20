package rebeca.peernet;

import java.util.*;

import peersim.core.*;

import rebeca.util.Graph;
import rebeca.util.Graph.*;


public interface Networking 
{
	public boolean isOverlay();
	public boolean hasUnderlay();
	public Networking getUnderlay();
	
	public Graph getGraph();
	public Property getProperty(String name);
	
	public boolean hasVertex();
	public boolean hasVertex(Node n);
	public Vertex getVertex();
	public Vertex getVertex(Node n);
	public void setVertex(Vertex v);
	public void setVertex(Vertex v, Node n);

	public boolean hasEdge(Vertex u, Vertex v);
	public boolean hasEdge(Node n, Node m);
	public Edge getEdge(Vertex u, Vertex v);
	public Edge getEdge(Node n, Node m);
	public boolean hasLink(Vertex u, Vertex v);
	public boolean hasLink(Node n, Node m);
	public SimLink getLink(Vertex u, Vertex v);
	public SimLink getLink(Node n, Node m);
	public void setLink(Vertex u, Vertex v, SimLink l);
	public void setLink(Node n, Node m, SimLink l);
		
	public Set<Node> nodes();
	public Set<SimLink> links();

}