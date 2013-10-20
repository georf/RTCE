/**
 * 
 */
package rebeca.peernet;


import org.apache.log4j.*;

import peersim.config.*;
import peersim.core.*;
import peersim.edsim.*;

import rebeca.*;
import rebeca.peersim.*;
import rebeca.util.*;
//import rebeca.util.Graph.*;


/**
 * @author parzy
 *
 */
public class NetworkTransport extends NetworkGraph
implements  EDProtocol, SimTransportEngine 
{
	
	// constants and parameters -----------------------------------------------
	// ------------------------------------------------------------------------

	/**
	 * Key identifying this engine. 
	 */
	public static final Class<SimTransportEngine> KEY = SimTransportEngine.class;
	
	/** 
	 * Logger for transport engines. 
	 */
	private static final Logger LOG = Logger.getLogger(KEY);

	/**
	 * Parameter identifying the link implementation class.
	 */
	private static final String PAR_LINK = "link";

	// FIXME parzy check instantiation of routing link
	private static final SimLink DEFAULT_LINK = new RoutingLink();

	
	// fields -----------------------------------------------------------------
	// ------------------------------------------------------------------------
	
	/**
	 * The broker facade.
	 */
	protected Broker broker;
	
	/**
	 * Prototype link.
	 */
	protected SimLink prototype;
	
	
	// instantiation ----------------------------------------------------------
	// ------------------------------------------------------------------------
	
	public NetworkTransport(String prefix)
	{
		super(prefix);

		// create the connection prototype
		prototype = (SimLink)Configuration.getInstance(prefix + "."
				+ PAR_LINK, DEFAULT_LINK);
		prototype.init(LOG, pid);
	}
	
	@Override
	public Object clone()
	{
		NetworkTransport clone = (NetworkTransport) super.clone();
		clone.node = CommonState.getNode();
		return clone;
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
	
	//@SuppressWarnings("unchecked")
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
		
		// check if network edge exists and has no associated link
		Graph.Edge base = getEdge(srcNode,dstNode);
		if (base==null)
		{
			LOG.error("connection failed: no network edge found as base of a "
					+ "connection");
			return null;
		}
		if (base.get()!=null)
		{
			LOG.error("connection failed: already connected");
			return null;
		}
		
		// create link and sinks
		SimLink link = (SimLink)prototype.clone();
		SimSink srcSink = new SimSink(srcNode, dstNode, link);
		SimSink dstSink = new SimSink(dstNode, srcNode, link);
		link.init(srcSink, dstSink, base);
		base.set(link);
		
		// plug sinks
		
		// TODO parzy remove below
		NetworkTransport t =(NetworkTransport)srcNode.getProtocol(pid);
		System.out.println(t + " " + t.getBroker());
		
		Broker srcBroker = ((NetworkTransport)srcNode.getProtocol(pid)).broker;
		Broker dstBroker = ((NetworkTransport)dstNode.getProtocol(pid)).broker;
		srcBroker.plug(srcSink);
		dstBroker.plug(dstSink);
		
		// return new profile for the established connection
		return new PeerSimContactProfile(srcNode,dstNode);		
	}

	// TODO parzy still need a disconnect
	
	/**
	 * Checks whether a link can be established between two nodes. This is the 
	 * case if an edge is defined in the underlying graph between the two 
	 * corresponding vertices.
	 */
	public boolean isLinkable(Node n, Node m)
	{
		return hasEdge(n,m);
	}
	
	// BrokerEngine implementation --------------------------------------------
	// ------------------------------------------------------------------------
	
	@Override
	public Object getKey() { return KEY; }
	@Override
	public Broker getBroker() { return broker; }
	@Override
	public void setBroker(Broker broker) { this.broker = broker; }
	@Override
	public void init() { }
	@Override
	public void activate(){ }
	@Override
	public void passivate() { }
	@Override
	public void exit() { }
	@Override
	public void process(Event event, EventProcessor source) { }
	@Override
	public Object plug(Object obj) { return obj; }
}


