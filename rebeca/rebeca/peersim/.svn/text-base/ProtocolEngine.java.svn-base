/*
 * $Id$
 */
package rebeca.peersim;

import peersim.config.*;
import peersim.core.*;
import peersim.edsim.*;

import rebeca.*;


/**
 * @author parzy
 *
 */
public class ProtocolEngine implements BrokerEngine, EDProtocol 
{
	// Parameters ---------------------------------------------------------
	//---------------------------------------------------------------------

	/** 
	 * String name of the parameter used to configure the applied routing
	 * engine for subscriptions.
	 * @config
	 */	
	private static final String PAR_ENGINE = "engine";
	
	// fields -----------------------------------------------------------------
	// ------------------------------------------------------------------------
	
	/** Configuration prefix. */
	protected String prefix;
	
	/** Own protocol identifier. */
	protected int pid;

	/** Own PeerSim node. */
	protected Node node;
	
	/** Used BrokerEngine engine. */
	protected BrokerEngine engine;

	
	// constructors -----------------------------------------------------------
	// ------------------------------------------------------------------------
	
	public ProtocolEngine() { }
	
	/**
	 * Reads configuration parameter.
	 */
	public ProtocolEngine(String prefix)
	{
		// save prefix
		this.prefix = prefix;
		
		// determine own protocol identifier
		pid = CommonState.getPid();
		
		// determine own node
		node = CommonState.getNode();
		
		// create an instance of the specified broker engine  
		engine = (BrokerEngine)Configuration.getInstance(prefix + "." + 
				PAR_ENGINE, null);
	}
		
	
	// getters and setters ----------------------------------------------------
	// ------------------------------------------------------------------------
	
	public BrokerEngine getEngine()
	{
		return engine;
	}
	
	
	// BrokerEngine implementation --------------------------------------------
	// ------------------------------------------------------------------------

	@Override
	public Object getKey() { return engine.getKey(); }
	@Override
	public Broker getBroker() { return engine.getBroker(); }
	@Override
	public void setBroker(Broker broker) { engine.setBroker(broker); }
	@Override
	public void init() { engine.init(); }
	@Override
	public void activate() { engine.activate(); }
	@Override
	public void passivate() { engine.passivate(); }
	@Override
	public void exit() { engine.exit(); }
	@Override
	public void process(Event event, EventProcessor source) { engine.process(event, source); }
	@Override
	public Object plug(Object obj) { return engine.plug(obj); }

	
	// EDProtocol implementation ----------------------------------------------
	// ------------------------------------------------------------------------

	@Override
	public void processEvent(Node node, int pid, Object event) { /* do nothing */ }

	@Override
	public Object clone() 
	{ 
		try 
		{ 
			return super.clone(); 
		} 
		// never happens
		catch (CloneNotSupportedException e)
		{
			return null;
		} 
	}
}
