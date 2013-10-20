/*
 * $Id$
 */
package rebeca.routing;

import java.util.*;

import org.apache.log4j.Logger;

import peersim.core.CommonState;

import rebeca.*;
import rebeca.broker.BasicBrokerEngine;
import rebeca.broker.EventSink;
import rebeca.broker.PluggableEventSink;
import rebeca.broker.ProcessingEngine;
import rebeca.component.Advertiser;
import rebeca.component.ComponentSink;
import rebeca.component.PluggableComponentSink;
import rebeca.event.Advertisement;
import rebeca.event.Subscription;
import rebeca.event.SubscriptionFactory;
import rebeca.event.Unadvertisement;
import rebeca.event.Unsubscription;
import rebeca.filter.ScopeIdentifier;
import rebeca.filter.ScopedFilter;
import rebeca.routing.FilterTable.FilterSelector;
import rebeca.routing.RoutingEngine.RoutingComponentSink;
import rebeca.routing.RoutingTable.EntrySelector;

/**
 * @author parzy
 *
 */
public class BasicAdvertisementEngine extends BasicBrokerEngine
implements AdvertisementEngine
{
	// Constants and Parameters -----------------------------------------------
	// ------------------------------------------------------------------------

	public static final Class<AdvertisementEngine> KEY = AdvertisementEngine.class;
	
	/** 
	 * Logger for AdvertisementEngines. 
	 */
	private static final Logger LOG = Logger.getLogger(KEY);
	
	// Fields -----------------------------------------------------------------
	// ------------------------------------------------------------------------
	
	/**
	 * Routing engine used to forward advertisements
	 */
	RoutingEngine engine;

	/**
	 * Routing table used to store and forward advertisements.
	 */
	RoutingTable table;
	
	/**
	 * Reference to the routing table with subscriptions.
	 */
	RoutingTable subscriptions;
	
	
	// constructors and initialization ----------------------------------------
	// ------------------------------------------------------------------------

	public BasicAdvertisementEngine()
	{
		this(null,(RoutingTable)null,null);
	}
	
	public BasicAdvertisementEngine(Broker broker)
	{
		this(broker,(RoutingTable)null,null);
	}

	public BasicAdvertisementEngine(Broker broker, RoutingTable subscriptions)
	{
		this(broker,subscriptions,null);
	}
	
	public BasicAdvertisementEngine(Broker broker, RoutingEngine routing)
	{
		this(broker,routing,null);
	}
	
	protected BasicAdvertisementEngine(Broker broker, RoutingEngine routing, 
			RoutingEngine engine)
	{
		this(null,routing != null ? routing.getTable() : null,engine);
	}
	
	protected BasicAdvertisementEngine(Broker broker, RoutingTable 
			subscriptions, RoutingEngine engine)
	{
		super(broker);
		this.subscriptions = subscriptions;
		this.engine = engine;
		this.table = engine!=null ? new AdvertisementTable(engine.getTable()) : null;
	}

	public void init()
	{
		// get routing table of subscription engine
		setSubscriptionTable(broker.getEngine(RoutingEngine.class));
		
		// register events
		ProcessingEngine processing = broker.getEngine(ProcessingEngine.class);
		processing.registerEventClass(Advertisement.class, this);
		processing.registerEventClass(Unadvertisement.class, this);
	}
	
	
	// Getters and Setters ----------------------------------------------------
	// ------------------------------------------------------------------------
	
	@Override
	public void setBroker(Broker broker)
	{
		super.setBroker(broker);
		if (engine != null)
		{
			engine.setBroker(broker);
		}
	}
	
	@Override
	public Object getKey()
	{
		return KEY;
	}
	
	@Override
	public RoutingEngine getEngine()
	{
		return engine;
	}

	@Override
	public void setEngine(RoutingEngine engine)
	{
		this.engine = engine;
		this.table = engine!=null ? new AdvertisementTable(engine.getTable()) : null;
	}
	
	@Override
	public RoutingTable getTable()
	{
		return table;
	}
	
	public int getTableSize()
	{
		return table.size();
	}
	
	public int getTableSize(EntrySelector selector)
	{
		return table.size(selector);
	}

	public RoutingTable getSubscriptionTable()
	{
		return subscriptions;
	}
	
	public void setSubscriptionTable(RoutingTable subscriptions)
	{
		this.subscriptions = subscriptions;
	}
	
	public void setSubscriptionTable(RoutingEngine routing)
	{
		setSubscriptionTable(routing.getTable());
	}

	
	
	
	

	
	@Override
	public void process(Event event, EventProcessor source)
	{
		// log event and its source
		if (LOG.isDebugEnabled())
		{
			LOG.debug("advertisement table contains " + table.size() + " advertisements");
			LOG.debug("subscription table contains " + subscriptions.size() + " subscriptions");
			LOG.debug("processing '" + event + "' from '" + source +"'");
		}
		if (event instanceof Unadvertisement)
		{
			unadvertise((Unadvertisement)event,source);
			if (LOG.isDebugEnabled())
			{
				LOG.debug("advertisement table contains " + table.size() + " advertisements");
				LOG.debug("subscription table contains " + subscriptions.size() + " subscriptions");
//				for (RoutingTable.RoutingEntry e : table.getTable())
//				{
//					LOG.debug("Filter: "+ e.getFilter());
//				}
			}
			return;
		}
		if (event instanceof Advertisement)
		{
			advertise((Advertisement)event,source);
			if (LOG.isDebugEnabled())
			{
				LOG.debug("advertisement table contains " + table.size() + " advertisements");
//				for (RoutingTable.RoutingEntry e : table.getTable())
//				{
//					LOG.debug("Filter: "+ e.getFilter());
//				}
			}
			return;
		}
		LOG.error("unhandled event '" + event + "'");
	}
	
	@Override
	public void advertise(Advertisement advertisement, EventProcessor source)
	{
		// first, determine newly overlapping subscriptions that must be forwarded
		AdvertisementTable table = (AdvertisementTable)this.table;
		EntrySelector selector = table.getServingSelector(advertisement.getFilter(),source);
		Collection<Filter> filters = subscriptions.getFilters(selector);
		
		// now, add and forward the advertisement
		engine.subscribe(advertisement, source);

		// finally, forward the filters
		forward(filters,source);
	}
	
	public void unadvertise(Unadvertisement unadvertisement, EventProcessor source)
	{
		// first, remove the advertisement and forward the message
		engine.unsubscribe(unadvertisement, source);
		
		// now, remove unserved subscriptions
		AdvertisementTable table = (AdvertisementTable)this.table;
		// TODO parzy changed to find advertisement bug!!! Seems to work.
//		EntrySelector selector = table.getServingSelector(unadvertisement.getFilter(),source);
		EntrySelector selector = table.getUnservingSelector(unadvertisement.getFilter(),source);
		
		subscriptions.removeEntries(selector);
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
	
	public void forward(Collection<Filter> filters, EventProcessor destination)
	{
		for (Filter f : filters)
		{
			Subscription s = SubscriptionFactory.getSubscription(f);
			if (LOG.isDebugEnabled())
			{
				LOG.debug("forwarding '" + s + "' to '" + destination + "'");
			}
			destination.process(s);
		}
	}
	
	public boolean isAdvertised(Filter filter, EventProcessor destination)
	{
		AdvertisementTable table = (AdvertisementTable)this.table;
		return table.isAdvertised(filter, destination);
	}
	
	
	// plugging ---------------------------------------------------------------
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
			LOG.debug("plugging subscription filtering based on advertisements "
					+ "into sink '" + sink.getSink() + "'");
		}
		return new AdvertisementSink(sink);
	}
	
	public ComponentSink plug(ComponentSink sink)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("plugging advertiser interface into component sink '" 
					+ sink.getSink() + "'");
		}
		return new AdvertisementComponentSink(sink);
	}
	
	protected class AdvertisementSink extends PluggableEventSink
	{
		public AdvertisementSink(EventSink sink)
		{
			super(sink);
		}
		
		@Override
		public void out(Event event)
		{
			if (out == null)
			{
				LOG.error("unset output sink");
				return;
			}
			
			// check (un)subscriptions for overlapping advertisements
			if (  (event instanceof Subscription) && 
				 !(event instanceof Advertisement) &&
				 !(event instanceof Unadvertisement) )
			{
				Filter f = ((Subscription)event).getFilter();
				EventProcessor d = getProcessor();
				if (!isAdvertised(f,d))
				{
					if (LOG.isDebugEnabled())
					{
						LOG.debug("advertisement sink '" + getSink() 
								+ "' suppressed '" + event + "'");
					}
					return;
				}
			}
			out.out(event);
		}
		
		public void activate()
		{
			super.activate();
			
			BasicRoutingTable t = (BasicRoutingTable)table;
			EventProcessor p = getProcessor();
			Collection<Filter> c = t.getFilters(t.getDestinationSelector(p,false));
			for (Filter f : c)
			{
				Advertisement a = SubscriptionFactory.getAdvertisement(f);
				if (LOG.isDebugEnabled())
				{
					LOG.debug("forwarding '" + a + "' to '" 
							+ getSink() + "'" );
				}
				out(a);
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
//				// TODO parzy seems not to be necessary for scope advertisements 
//				//      because only scope subscriptions are responsible for 
//				//      scope memberships and no covering is supported 
//				// defer handling of scope advertisements
//				if (f instanceof ScopedFilter)
//				{
//					Filter g = ((ScopedFilter)f).getFilter();
//					if (g instanceof ScopeIdentifier)
//					{
//						if (LOG.isTraceEnabled()) LOG.trace("deferred handling of scope advertisement '" + g + "'" );
//						// TODO to remove
//						LOG.error("deferred handling of scope advertisement '" + g + "'" );
//						continue;
//					}
//				}				
				if (LOG.isDebugEnabled())
				{
					LOG.debug("unadvertising filter '" + f + "' for '" 
							+ getSink() + "'" );
				}
				
				// TODO parzy revise with unplugging mechanism
				// parzy: original handling of unadvertisements via sink in
				// in(SubscriptionFactory.getUnadvertisement(f));
				// parzy: changed handling unadvertisements in context of passivation
				// process(SubscriptionFactory.getUnadvertisement(f),p);
				// parzy: changed to employ the monitoring hook for measurements
				BrokerEngine e = getBroker().getEngine(ProcessingEngine.class);
				e.process(SubscriptionFactory.getUnadvertisement(f),p);
			}

			super.passivate();
		}
	}
	
	
	protected class AdvertisementComponentSink extends PluggableComponentSink
	implements AdvertisementEngine.AdvertisementComponentSink, Advertiser
	{
		AdvertisementFilterTable filters;
		AdvertisementFilterTable advertisements;
		FilterTable subscriptions;
		
		public AdvertisementComponentSink(ComponentSink sink) 
		{			
			this( sink, new AdvertisementFilterTable(new BasicFilterTable()), 
					    new AdvertisementFilterTable(new BasicFilterTable())  );
		}
		
		protected AdvertisementComponentSink( ComponentSink sink, 
				AdvertisementFilterTable filters, 
				AdvertisementFilterTable advertisements ) 
		{
			super(sink);
			this.filters = filters;
			this.advertisements = advertisements;
			
			RoutingEngine.RoutingComponentSink routing = 
				sink.getInterface(RoutingEngine.RoutingComponentSink.class);
			if (routing != null)
			{
				subscriptions = routing.getTable();
			}
			else
			{
				subscriptions = new BasicFilterTable();
			}
		}
		
//		protected AdvertisementComponentSink(ComponentSink sink, FilterTable 
//				table, AdvertisementFilterTable subscriptions)
//		{
//			super(sink);
//			this.filters = table;
//			this.subscriptions = subscriptions;
//			this.advertisements = new AdvertisementFilterTable(new BasicFilterTable());
//		}
		
		@Override
		public FilterTable getTable()
		{
			return filters;
		}
		
		@Override
		public void in(Event event)
		{
			// filter out unadvertisements
			if (event instanceof Unadvertisement) 
			{
				processUnadvertisement((Unadvertisement)event);
				return;
			}
			// reply advertisements with overlapping subscriptions
			if (event instanceof Advertisement)
			{
				processAdvertisement((Advertisement)event);
				return;
			}
			// otherwise let the event pass
			in.in(event);
		}

		public void processAdvertisement(Advertisement advertisement)
		{
			Filter filter = advertisement.getFilter();
			FilterSelector selector = advertisements.getServingSelector(filter);
			for (Filter f : subscriptions.getFilters(selector))
			{
				Subscription s = SubscriptionFactory.getSubscription(f);
				out.out(s);
			}
			advertisements.add(filter);
		}
		
		public void processUnadvertisement(Unadvertisement unadvertisement)
		{
			advertisements.remove(unadvertisement.getFilter());
		}
		
		@Override
		public void out(Event event)
		{
			if (event instanceof Unsubscription)
			{
				Unsubscription unsubscription = (Unsubscription) event;
				Filter filter = unsubscription.getFilter();
				FilterSelector selector = AdvertisementFilterTable.getOverlappingSelector(filter);
				if (advertisements.getFilters(selector).isEmpty())
				{
					return;
				}
			}
			else if (event instanceof Subscription)
			{
				Subscription subscription = (Subscription) event;
				Filter filter = subscription.getFilter();
				FilterSelector selector = AdvertisementFilterTable.getOverlappingSelector(filter);
				if (advertisements.getFilters(selector).isEmpty())
				{
					return;
				}
			}
			super.out(event);
		}
		
		public void advertise(Filter filter)
		{
			if (!filters.add(filter))
			{
				return;
			}
			
			Advertisement a = SubscriptionFactory.getAdvertisement(filter);
			out.out(a);
		}
		
		public void unadvertise(Filter filter)
		{
			if (!filters.remove(filter))
			{
				return;
			}

//			LOG.debug("component sink unadvertises filter '" + filter +"'");
			Unadvertisement u = SubscriptionFactory.getUnadvertisement(filter);
			if (LOG.isDebugEnabled()) LOG.debug("component sink '" + this + "' issues '" + u +"'");
			out.out(u);
		}
	}
}
