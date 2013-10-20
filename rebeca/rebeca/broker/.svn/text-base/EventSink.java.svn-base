/*
 * $Id$
 */
package rebeca.broker;

import rebeca.*;

/**
 * @author parzy
 *
 */
public interface EventSink 
{
	public void setInputSink(EventSink sink);
	
	public void setOutputSink(EventSink sink);
	
	public void in(Event event);
	
	public void out(Event event);
	
	public EventProcessor getProcessor();
	
	public EventSink getSink();
	public ContactProfile getContactProfile();
	
	public void activate();
	public void passivate();
	
	public void init();
	public void exit();
	
	// TODO parzy replace when configuration engine is integrated
	public boolean isLocal();
}
