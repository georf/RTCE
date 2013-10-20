/*
 * $Id$
 */
package rebeca.broker;

import java.util.*;

import rebeca.*;

/**
 * @author parzy
 *
 */
public interface ProcessingEngine extends BrokerEngine
{
	public interface EventSourceTuple
	{
		public Event getEvent();
		public EventProcessor getSource();
	}
	
	public Queue<EventSourceTuple> getQueue();
	public void setQueue(Queue<EventSourceTuple> queue);
	
	public void process(EventSourceTuple tuple);
	public BrokerEngine registerEventClass(Class<?> clazz, BrokerEngine engine);
	public BrokerEngine deregisterEventClass(Class<?> clazz);
	public BrokerEngine deregisterEventClass(BrokerEngine engine);
	public Set<Class<?>> eventClassSet();
	public void handle(EventSourceTuple tuple);
	public void handle(Event event, EventProcessor source);	
}
