/*
 * $id$
 */
package rebeca;

import rebeca.*;

/**
 * @author parzy
 *
 */
public interface BrokerEngine 
{
	public Broker getBroker();
	public void setBroker(Broker broker);
	
	public Object getKey();
	
	public void process(Event event, EventProcessor source);
	
	public void init();
	public void activate();
	public void passivate();
	public void exit();
	
	public Object plug(Object obj);
}
