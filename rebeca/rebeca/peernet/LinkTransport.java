/*
 * $Id$
 */
package rebeca.peernet;

import java.util.*;

import org.apache.log4j.Logger;

import peersim.config.*;
import peersim.core.*;
import peersim.edsim.*;

import rebeca.*;
import rebeca.broker.*;
import rebeca.peersim.*;

/**
 * @author parzy
 *
 */
public class LinkTransport extends BasicBrokerEngine 
implements EDProtocol, SimTransportEngine 
{
	// constants and parameters -----------------------------------------------
	// ------------------------------------------------------------------------
		
	public static final Class<SimTransportEngine> KEY = SimTransportEngine.class;
	
	/** 
	 * Logger for transport engines. 
	 */
	private static final Logger LOG = Logger.getLogger(KEY);
	
	private static final String PAR_LINK = "link";
	private static final SimLink DEFAULT_LINK = new ConstantLink(true);
	
	// fields -------------------------------------------------------------
	// --------------------------------------------------------------------
	
	protected int pid;
	protected Node node;

	protected HashMap<Node,SimLink> links;
	protected SimLink prototype;

	
	// instantiation -----------------------------------------------------
	// --------------------------------------------------------------------

	public LinkTransport(String prefix)
	{
		// determine own protocol identifier
		node = CommonState.getNode();
		pid = CommonState.getPid();
		
		// create connection table for each node
		links = new HashMap<Node,SimLink>();
		
		// create the connection prototype
		prototype = (SimLink)Configuration.getInstance(prefix + "."
				+ PAR_LINK, DEFAULT_LINK);
		prototype.init(LOG, pid);
	}	
	
	@Override
	public Object clone()
	{
		try 
		{
			LinkTransport clone = (LinkTransport) super.clone();
			clone.node = CommonState.getNode();
			clone.links = new HashMap<Node, SimLink>();
			return clone;
		} 
		catch (CloneNotSupportedException e) 
		{
			// never happens
			return null;
		}
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
	
	// broker engine -------------------------------------------------
	// ------------------------------------------------------------------------

	public Object getKey()
	{
		return KEY;
	}
	
	// connection management --------------------------------------------------
	// ------------------------------------------------------------------------
	
	public ContactProfile connect(ContactProfile profile)
	{
		if (profile instanceof PeerSimContactProfile)
		{
			return connect((PeerSimContactProfile)profile);
		}
		LOG.error("unknown contact profile '" + profile 
				+ "': no connection established");
		return null;
	}
		
	public PeerSimContactProfile connect(PeerSimContactProfile profile)
	{
		// get nodes from profile
		Node srcNode = (Node)profile.getLocal();
		if (srcNode == null) 	
		{
			srcNode = node;
		}
		Node dstNode = (Node)profile.getRemote();
		if (dstNode == null) 	
		{
			LOG.error("connection failed: no remote address specified");
			return null;
		}
		
		// create link and sinks 
		SimLink link = (SimLink)prototype.clone();
		SimSink srcSink = new SimSink(srcNode, dstNode, link);
		SimSink dstSink = new SimSink(dstNode, srcNode, link);
		link.init(srcSink, dstSink);
		
		// and plug them
		Broker srcBroker = ((LinkTransport)srcNode.getProtocol(pid)).broker;
		Broker dstBroker = ((LinkTransport)dstNode.getProtocol(pid)).broker;
		srcBroker.plug(srcSink);
		dstBroker.plug(dstSink);
		
		// return new profile for the established connection
		return new PeerSimContactProfile(srcNode,dstNode);
	}
	
	// TODO parzy we still need a disconnect
}
