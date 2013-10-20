/**
 * 
 */
package rebeca.peernet;

import org.apache.log4j.*;

import peersim.config.*;
import peersim.core.*;

import rebeca.peersim.*;
import rebeca.util.*;


/**
 * @author parzy
 *
 */
public class NetworkLinkBuilder implements Control
{
	// parameters and constants -----------------------------------------------
	// ------------------------------------------------------------------------

	private static final Logger LOG = Logger.getLogger(NetworkLinkBuilder.class);
	
	public static final String PAR_TRANSPORT = "transport";
	
	
	// fields -----------------------------------------------------------------
	// ------------------------------------------------------------------------
	
	private int tid;
	

	// instantiation ----------------------------------------------------------
	// ------------------------------------------------------------------------

	/**
	 * Reads configuration parameter.
	 */
	public NetworkLinkBuilder(String prefix)
	{
		this.tid = Configuration.getPid(prefix + "." + PAR_TRANSPORT);
	}

	
	// Control implementation -------------------------------------------------
	// ------------------------------------------------------------------------

	@Override
	public boolean execute()
	{
		// get graph 
		Node node = Network.prototype;
		NetworkTransport transport = (NetworkTransport)node.getProtocol(tid);
		Graph graph = transport.getGraph();
		
		// create a link for each graph edge
		for (Graph.Edge e : graph.edges())
		{
			Node n = (Node)e.tail().get();
			Node m = (Node)e.head().get();
			transport.connect(new PeerSimContactProfile(n,m));
		}
		
		if (LOG.isInfoEnabled()) LOG.info("successfully created links for all "
				+ "transport edges");
		
		return false;
	}
}
