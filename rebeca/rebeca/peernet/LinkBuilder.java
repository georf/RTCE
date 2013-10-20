/**
 * 
 */
package rebeca.peernet;

import org.apache.log4j.*;

import peersim.config.*;
import peersim.core.*;

import rebeca.peersim.*;


/**
 * @author parzy
 *
 */
public class LinkBuilder implements Control
{
	// parameters and constants -----------------------------------------------
	// ------------------------------------------------------------------------

	private static final Logger LOG = Logger.getLogger(LinkBuilder.class);
	
	private static enum Strategy {LIST, STAR, BINARY};
	
	public static final String PAR_TRANSPORT = "transport";
	public static final String PAR_STRATEGY = "strategy";
	
	private static final String DEFAULT_STRATEGY = Strategy.STAR.toString();
	
	// fields -----------------------------------------------------------------
	// ------------------------------------------------------------------------
	
	private int tid;
	private Strategy strategy;

	// instantiation ----------------------------------------------------------
	// ------------------------------------------------------------------------

	/**
	 * Reads configuration parameter.
	 */
	public LinkBuilder(String prefix)
	{
		this.tid = Configuration.getPid(prefix + "." + PAR_TRANSPORT);
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
	}

	
	// Control implementation -------------------------------------------------
	// ------------------------------------------------------------------------

	@Override
	public boolean execute()
	{
		// get transport
		Node node = Network.prototype;
		SimTransportEngine transport = (SimTransportEngine)node.getProtocol(tid);
		
		switch(strategy){
		case LIST:
			list(transport);
			break;
		case STAR:
			star(transport);
			break;
		case BINARY:
			binary(transport);
			break;
		}
		
		return false;
	}
	
	
	// strategies -------------------------------------------------------------
	// ------------------------------------------------------------------------
	
	public void list(SimTransportEngine transport)
	{
		int links = 0;
		for (int i=0; i<Network.size()-1; i++)
		{
			Node n = Network.get(i);
			Node m = Network.get(i+1);
			transport.connect(new PeerSimContactProfile(n,m));
			links++;
		}
		
		if (LOG.isInfoEnabled()) LOG.info("created " + links + " links");
	}
	
	public void star(SimTransportEngine transport)
	{
		int links = 0;
		Node c = Network.size()>0 ? Network.get(0) : null;
		for (int i=1; i<Network.size(); i++)
		{
			Node n = Network.get(i);
			transport.connect(new PeerSimContactProfile(n,c));
			links++;
		}
		
		if (LOG.isInfoEnabled()) LOG.info("created " + links + " links");
	}
	
	public void binary(SimTransportEngine transport)
	{
		int links = 0;
		for (int i=1; i<Network.size(); i++)
		{
			Node n = Network.get(i);
			Node p = Network.get((i-1)/2); // parent node in a binary tree
			transport.connect(new PeerSimContactProfile(n,p));
			links++;
		}
		
		if (LOG.isInfoEnabled()) LOG.info("created " + links + " links");
	}
}
