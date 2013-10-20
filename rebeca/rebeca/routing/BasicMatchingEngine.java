/*
 * $Id$
 */
package rebeca.routing;

import java.util.*;

import org.apache.log4j.Logger;

import rebeca.*;
import rebeca.broker.BasicBrokerEngine;
import rebeca.broker.ProcessingEngine;
import rebeca.component.ComponentSink;
import rebeca.component.PluggableComponentSink;
import rebeca.component.Publisher;
import rebeca.event.Subscription;
import rebeca.event.Unsubscription;
import rebeca.routing.MatchingEngine.MatchingComponentSink;

/**
 * @author parzy
 *
 */
public class BasicMatchingEngine extends BasicBrokerEngine 
implements MatchingEngine 
{
	// Constants and Parameters -----------------------------------------------
	// ------------------------------------------------------------------------
	
	public static final Class<MatchingEngine> KEY = MatchingEngine.class;

	/** 
	 * Logger for MatchingEngines. 
	 */
	private static final Logger LOG = Logger.getLogger(KEY);
	
	// Fields -----------------------------------------------------------------
	// ------------------------------------------------------------------------

	protected RoutingTable table;

	
	// constructors and initialization ----------------------------------------
	// ------------------------------------------------------------------------
	
	public BasicMatchingEngine()
	{
		this((Broker)null);
	}
	
	public BasicMatchingEngine(Broker broker)
	{
		super(broker);
		this.table = new BasicMatchingTable();
	}
	
	/**
	 * PeerSim convenience constructor ignores arguments and calls the default 
	 * constructor.
	 * @param prefix ignored
	 */
	public BasicMatchingEngine(String prefix)
	{
		this();
	}

	
	public void init()
	{
		// register events
		ProcessingEngine processing = broker.getEngine(ProcessingEngine.class);
		processing.registerEventClass(Event.class, this);
	}
	
	// Getters and Setters ----------------------------------------------------
	// ------------------------------------------------------------------------
	
	
	@Override
	public Object getKey() 
	{
		return KEY;
	}
	
	@Override
	public RoutingTable getTable() 
	{
		return table;
	}
	
	public void setTable(RoutingTable table)
	{
		this.table = table;
	}
	
	
	
	
	
	public void process(Event event, EventProcessor source)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("matching event '" + event + "' from '" + source +"'");
		}
		match(event, source);
	}
	
	public void match(Event event, EventProcessor source)
	{
		BasicMatchingTable table = (BasicMatchingTable)this.table;
		Collection<EventProcessor> destinations = 
				table.getMatchingDestinations(event);
		destinations.remove(source);
		forward(event,destinations);
	}
	
	public void forward(Event event, Collection<EventProcessor> destinations)
	{
		for (EventProcessor p : destinations)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("forwarding event '" + event + "' to '" + p + "'");
			}
			p.process(event);
		}
	}
	
	public Collection<EventProcessor> getDestinations()
	{
		// TODO parzy performance hack?
		return broker.getDestinations();
	}
	

	/* (non-Javadoc)
	 * @see rebeca.scope.BrokerEngine#plug(java.lang.Object)
	 */
	@Override
	public Object plug(Object obj) 
	{
		if (obj instanceof ComponentSink)
		{
			return plug((ComponentSink)obj);
		}
		return obj;
	}
	
	public ComponentSink plug(ComponentSink sink)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("plugging matching of own events into component sink '" 
					+ sink.getSink() + "'");
		}
		return new MatchingComponentSink(sink);
	}
	
	
	protected static class MatchingComponentSink extends PluggableComponentSink
	implements MatchingEngine.MatchingComponentSink, Publisher
	{
		protected MatchingFilterTable filters;

		public MatchingComponentSink(ComponentSink sink)
		{
			super(sink);
			filters = new MatchingFilterTable();
		}
		
		@Override
		public void in(Event event)
		{
			if (filters.match(event))
			{
				in.in(event);
			}
		}
		
		@Override
		public void out(Event event)
		{
			out.out(event);
			in(event);
		}

		public FilterTable getTable()
		{
			return filters;
		}
		
		public void publish(Event event)
		{
			out(event);
		}
	}
}
