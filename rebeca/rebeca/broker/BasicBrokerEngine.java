/*
 * $Id$
 */
package rebeca.broker;

import rebeca.*;

/**
 * @author parzy
 *
 */
public class BasicBrokerEngine implements BrokerEngine 
{

	protected Broker broker;
	
	public BasicBrokerEngine()
	{
		this(null);
	}
	
	public BasicBrokerEngine(Broker broker)
	{
		this.broker = broker;
	}

	// getters and setters ----------------------------------------------------
	// ------------------------------------------------------------------------

	@Override
	public Object getKey() 
	{
		// TODO parzy maybe make this method abstract
		return null;
	}

	
	@Override
	public Broker getBroker() 
	{
		return broker;
	}

	@Override
	public void setBroker(Broker broker) 
	{
		this.broker = broker;
	}

	// startup and shutdown ---------------------------------------------------
	// ------------------------------------------------------------------------
	
	@Override
	public void init() { }
	@Override
	public void activate(){ }
	@Override
	public void passivate() { }
	@Override
	public void exit() { }
	
	// business methods -------------------------------------------------------
	// ------------------------------------------------------------------------
	
	@Override
	public void process(Event event, EventProcessor source) { }
	@Override
	public Object plug(Object obj) 
	{
		return obj;
	}
}
