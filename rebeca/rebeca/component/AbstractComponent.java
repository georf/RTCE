/*
 * $Id$
 */
package rebeca.component;

import rebeca.*;

/**
 * @author parzy
 *
 */
public abstract class AbstractComponent implements Component 
{
	protected ComponentBroker broker;
	protected ComponentContactProfile profile;
	
	@Override
	public ContactProfile getContactProfile()
	{
		return profile!=null ? profile : (profile=new ComponentContactProfile(this));
	}

	@Override
	public ComponentBroker getBroker()
	{
		return broker;
	}
	
	@Override
	public void setBroker(ComponentBroker broker)
	{
		this.broker = broker;
	}
	
	
	/* (non-Javadoc)
	 * @see rebeca.scope.Component#notify(rebeca.Event)
	 */
	@Override
	public abstract void notify(Event event);
	
	@Override
	public void init() { /* empty default implementation */ }
	
	@Override
	public void activate() { /* empty default implementation */ }

	@Override
	public void passivate() { /* empty default implementation */ }

	@Override
	public void exit() { /* empty default implementation */ }

	
	public void publish(Event event)
	{
		if (broker != null)
		{
			broker.publish(event);
		}
	}
	
	public void subscribe(Filter filter)
	{
		if (broker != null)
		{
			broker.subscribe(filter);
		}
	}

	public void unsubscribe(Filter filter)
	{
		if (broker != null)
		{
			broker.unsubscribe(filter);
		}
	}
	
	public void advertise(Filter filter)
	{
		if (broker != null)
		{
			broker.advertise(filter);
		}
	}
	
	public void unadvertise(Filter filter)
	{
		if (broker != null)
		{
			broker.unadvertise(filter);
		}
	}	
}
