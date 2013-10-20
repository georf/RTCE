/**
 * 
 */
package rebeca.peernet;

import java.util.*;

import org.apache.log4j.Logger;

import peersim.config.*;
import peersim.core.*;

import rebeca.util.*;

/**
 * @author parzy
 *
 */
public class RouteBuilder implements Control 
{
	// parameters and constants -----------------------------------------------
	// ------------------------------------------------------------------------

	private static final Logger LOG = Logger.getLogger(RouteBuilder.class);
	
	private static enum Assignment {NONE, DIRECT, RANDOM};
	private static enum Wiring {NONE, COMPLETE};
	
	public static final String PAR_ROUTING = "routing";
	public static final String PAR_ASSIGNMENT = "assignment";
	public static final String PAR_WIRING = "wiring";
	public static final String PAR_SEED = "seed";
	
	private static final long DEFAULT_SEED = 6461202761445351720L;
	private static final String DEFAULT_ASSIGNMENT = Assignment.RANDOM.toString();
	private static final String DEFAULT_WIRING = Wiring.COMPLETE.toString();
	
	// fields -----------------------------------------------------------------
	// ------------------------------------------------------------------------
	
	private Random r;
	private int routing;
	private Assignment assignment;
	private Wiring wiring;
	
	// instantiation ----------------------------------------------------------
	// ------------------------------------------------------------------------
	
	public RouteBuilder(String prefix)
	{
		this.routing = Configuration.getPid(prefix + "." + PAR_ROUTING);
		long seed = Configuration.getLong(prefix + "." + PAR_SEED, 
				CommonState.r.getLastSeed()^DEFAULT_SEED);
		this.r = new Random(seed);
		String assignment = Configuration.getString(prefix + "." 
				+ PAR_ASSIGNMENT, DEFAULT_ASSIGNMENT);
		for (Assignment a : Assignment.values())
		{
			if (a.toString().equalsIgnoreCase(assignment))
			{
				this.assignment = a;
				break;
			}		
		}
		String wiring = Configuration.getString(prefix + "." 
				+ PAR_WIRING, DEFAULT_WIRING);
		for (Wiring w : Wiring.values())
		{
			if (w.toString().equalsIgnoreCase(wiring))
			{
				this.wiring = w;
				break;
			}		
		}

	}
	
	// Control implementation -------------------------------------------------
	// ------------------------------------------------------------------------
	
	@Override
	public boolean execute()
	{
		// perform node assignments
		switch (assignment){
		case DIRECT:
			directAssignment();
			break;
		case RANDOM: 
			randomAssignment();
			break;
		case NONE:
			break;
		}

		// create overlay nodes
		Node node = Network.prototype;
		NetworkRouting routing = (NetworkRouting)node.getProtocol(this.routing);
		Networking networking = routing.getUnderlay();
		routing.addOverlayVertices(networking.nodes());
		
		// perform network wiring
		switch (wiring){
		case COMPLETE:
			completeWiring();
			break;
		case NONE:
			break;
		}
		
		return false;
	}
	
	// node assignment --------------------------------------------------------
	// ------------------------------------------------------------------------
	
	public void randomAssignment()
	{
		// get protocols and graphs
		Node node = Network.prototype;
		NetworkRouting routing = (NetworkRouting)node.getProtocol(this.routing);
		Networking networking = routing.getUnderlay();
		Graph under = networking.getGraph();
		
		// randomly assign nodes
		ArrayList<Graph.Vertex> vertices = new ArrayList<Graph.Vertex>(under.vertices());
		for (int i=0; i<Network.size(); i++)
		{
			Node n = Network.get(i);
			int j = r.nextInt(vertices.size());
			Graph.Vertex v = vertices.remove(j);
			networking.setVertex(v, n);
		}
		
		if (LOG.isInfoEnabled())
		{
			LOG.info("randomly placed " + Network.size() + " nodes");
		}
	}
	
	public void directAssignment()
	{
		// get protocols and graphs
		Node node = Network.prototype;
		NetworkRouting routing = (NetworkRouting)node.getProtocol(this.routing);
		Networking networking = routing.getUnderlay();
		Graph under = networking.getGraph();
		
		// directly assign nodes
		ArrayList<Graph.Vertex> vertices = new ArrayList<Graph.Vertex>(under.vertices());
		for (int i=0; i<Network.size(); i++)
		{
			networking.setVertex(vertices.get(i), Network.get(i));
		}
		
		if (LOG.isInfoEnabled())
		{
			LOG.info("directly placed " + Network.size() + " nodes");
		}
	}
	
	
	// overlay wiring ---------------------------------------------------------
	// ------------------------------------------------------------------------

	public void completeWiring() 
	{
		// get protocols routing protocol
		Node node = Network.prototype;
		NetworkRouting routing = (NetworkRouting)node.getProtocol(this.routing);
	
		// wire a complete graph
		for (int i=0; i<Network.size(); i++)
		{
			for (int j=i+1; j<Network.size(); j++)
			{
				routing.addRoute(Network.get(i), Network.get(j));
				if (LOG.isInfoEnabled()) LOG.info("calculated route from node "
						+ i + " to " + j);
			}
		}
		
		if (LOG.isInfoEnabled()) LOG.info("wired a complete overlay graph");
	}
}
