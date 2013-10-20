/*
 * $Id$
 */
package rebeca.routing;

import java.util.*;

import org.apache.log4j.Logger;

import rebeca.*;
import rebeca.broker.*;
import rebeca.component.*;
import rebeca.event.*;
import rebeca.filter.*;
import rebeca.routing.RoutingTable.*;


/**
 * Simply does matching and simple routing for subscriptions.
 * @author parzy
 *
 */
public class BasicRoutingEngine extends BasicBrokerEngine
implements RoutingEngine
{
	// Constants and Parameters -----------------------------------------------
	// ------------------------------------------------------------------------

	public static final Class<RoutingEngine> KEY = RoutingEngine.class;

	/** 
	 * Logger for RoutingEngines. 
	 */
	private static final Logger LOG = Logger.getLogger(KEY);

	// Fields -----------------------------------------------------------------
	// ------------------------------------------------------------------------

	protected RoutingTable table;

	// constructors and initialization ----------------------------------------
	// ------------------------------------------------------------------------
	
	public BasicRoutingEngine()
	{
		this(null,new BasicRoutingTable());
	}
	
	public BasicRoutingEngine(Broker broker)
	{
		this(broker,new BasicRoutingTable());
	}
	
	public BasicRoutingEngine(Broker broker, MatchingEngine engine)
	{
		this(broker,new BasicRoutingTable(engine.getTable()));
	}
	
	protected BasicRoutingEngine(Broker broker, RoutingTable table)
	{
		super(broker);
		this.table = table;
	}
	
	public void init()
	{
		// get routing table of matching engine
		setTable(broker.getEngine(MatchingEngine.class));
		
		// register events
		ProcessingEngine processing = broker.getEngine(ProcessingEngine.class);
		processing.registerEventClass(Subscription.class, this);
		processing.registerEventClass(Unsubscription.class, this);
	}
	
	// Getters and Setters ----------------------------------------------------
	// ------------------------------------------------------------------------
	
	public Object getKey()
	{
		return KEY;
	}
	
	public RoutingTable getTable()
	{
		return table;
	}
	
	public void setTable(RoutingTable table)
	{
		this.table = table;
	}
	
	public void setTable(MatchingEngine engine)
	{
		setTable(engine.getTable());
	}
	
	public int getTableSize()
	{
		return table.size();
	}
	
	public int getTableSize(EntrySelector selector)
	{
		return table.size(selector);
	}
	
	public void process(Event event, EventProcessor source)
	{
		// log event and its source
		if (LOG.isDebugEnabled())
		{
			LOG.debug("processing '" + event + "' from '" + source +"'");
		}
		
		if (event instanceof Unsubscription)
		{
			unsubscribe((Unsubscription)event,source);
			if (LOG.isDebugEnabled())
			{
				LOG.debug("routing table contains " + table.size() + " subscriptions");
//				for (RoutingTable.RoutingEntry e : table.getTable())
//				{
//					LOG.debug("Filter: "+ e.getFilter());
//				}
			}
			return;
		}
		if (event instanceof Subscription)
		{
			subscribe((Subscription)event,source);
			if (LOG.isDebugEnabled())
			{
				LOG.debug("routing table contains " + table.size() + " subscriptions");
//				for (RoutingTable.RoutingEntry e : table.getTable())
//				{
//					LOG.debug("Filter: "+ e.getFilter());
//				}
			}
			return;
		}
		LOG.error("unhandled event '" + event + "'");
	}
	
	public void forward(Event event, Collection<EventProcessor> destinations)
	{
		for (EventProcessor p : destinations)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("forwarding '" + event + "' to '" + p + "'");
			}
			p.process(event);
		}
	}
	
	public Collection<EventProcessor> getDestinations()
	{
		return new ArrayList<EventProcessor>(broker.getDestinations());
	}
	
	public void subscribe(Subscription subscription, EventProcessor source)
	{
		// add filter to routing table
		if (! table.add(subscription.getFilter(), source))
		{
			return;
		}
	}
	
	public void unsubscribe(Unsubscription unsubscription, EventProcessor source)
	{
		// remove filter from routing table when contained
		if (! table.remove(unsubscription.getFilter(), source))
		{
			return;
		}
	}
	
	
	// event sink plugging ----------------------------------------------------
	// ------------------------------------------------------------------------
	
	public Object plug(Object obj)
	{
		if (obj instanceof ComponentSink)
		{
			return plug((ComponentSink)obj);
		}
		if (obj instanceof EventSink)
		{
			return plug((EventSink)obj);
		}
		return obj;
	}
	
	public EventSink plug(EventSink sink)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("plugging check for preventing backward propagation of "
					+ "subscriptions into sink '" + sink.getSink() + "'");
		}
		return new RoutingSink(sink);
	}
	
	public ComponentSink plug(ComponentSink sink)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("plugging subscriber interface into component sink '" 
					+ sink.getSink() + "'");
		}
		return new RoutingComponentSink(sink);
	}
	
	
	
	
	protected class RoutingSink extends PluggableEventSink
	{
		public RoutingSink(EventSink sink)
		{
			super(sink);
		}
		
		public void activate()
		{
			super.activate();
			
			BasicRoutingTable t = (BasicRoutingTable)table;
			EventProcessor p = getProcessor();
			Collection<Filter> c = t.getFilters(t.getDestinationSelector(p,false));
			for (Filter f : c)
			{
				if (LOG.isDebugEnabled())
				{
					LOG.debug("forwarding filter '" + f + "' to '" 
							+ getSink() + "'" );
				}
				out(SubscriptionFactory.getSubscription(f));
			}
		}
		
		public void passivate()
		{
			BasicRoutingTable t = (BasicRoutingTable)table;
			EventProcessor p = getProcessor();
			// TODO parzy this may lead to race conditions as there might be still some unhandled subscriptions in the processing queue
			Collection<Filter> c = t.getFilters(t.getDestinationSelector(p,true));
			for (Filter f : c)
			{
				// TODO parzy does the routing engine need to know scopes at all?
				// defer handling of scope subscriptions
				if (f instanceof ScopedFilter)
				{
					Filter g = ((ScopedFilter)f).getFilter();
					if (g instanceof ScopeIdentifier)
					{
						if (LOG.isTraceEnabled()) LOG.trace("deferred handling of scope subscription '" + g + "'" );
						continue;
					}
				}
				// unsubscribe all filter
				if (LOG.isDebugEnabled())
				{
					LOG.error("unsubscribing filter '" + f + "' for '" 
							+ getSink() + "'" );
				}
				
				// TODO parzy revise with unplugging mechanism
				// parzy: original handling of unsubscriptions via sink in
				// in(SubscriptionFactory.getUnsubscription(f));
				// parzy: changed handle unsubscriptions in context of passivation
				// process(SubscriptionFactory.getUnsubscription(f),p);
				// parzy: changed to employ the monitoring hook for measurements
				BrokerEngine e = getBroker().getEngine(ProcessingEngine.class);
				e.process(SubscriptionFactory.getUnsubscription(f),p);
			}

			super.passivate();
		}
	}
	
	
	protected static class RoutingComponentSink extends PluggableComponentSink
	implements RoutingEngine.RoutingComponentSink, Subscriber
	{
		FilterTable filters;
		
		public RoutingComponentSink(ComponentSink sink)
		{
			this(sink,null);
			
			MatchingEngine.MatchingComponentSink matching = 
				sink.getInterface(MatchingEngine.MatchingComponentSink.class);
			if (matching != null)
			{
				filters = matching.getTable();
			}
			else
			{
				filters = new BasicFilterTable();
			}
		}
		
		protected RoutingComponentSink(ComponentSink sink, FilterTable table)
		{
			super(sink);
			this.filters = table;
		}
		
		@Override
		public FilterTable getTable()
		{
			return filters;
		}
		
		@Override
		public void in(Event event)
		{
			if (event instanceof Subscription || event instanceof Unsubscription)
			{
				return;
			}
			in.in(event);
		}

		public void subscribe(Filter filter)
		{
			if (!filters.add(filter))
			{
				return;
			}
			
			Subscription s = SubscriptionFactory.getSubscription(filter);
			out.out(s);
		}
		
		public void unsubscribe(Filter filter)
		{
			if (!filters.remove(filter))
			{
				return;
			}
			
			Unsubscription u = SubscriptionFactory.getUnsubscription(filter);
			out.out(u);
		}
	}
}
