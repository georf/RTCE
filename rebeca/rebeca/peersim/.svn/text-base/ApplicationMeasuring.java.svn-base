/**
 * 
 */
package rebeca.peersim;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import org.apache.log4j.*;

import peersim.config.*;
import peersim.core.*;

import rebeca.*;
import rebeca.broker.*;
import rebeca.component.*;
import rebeca.event.*;
import rebeca.filter.*;
import rebeca.namevalue.*;
import rebeca.routing.*;
import rebeca.routing.RoutingTable.*;
import rebeca.util.*;

/**
 * @author parzy
 *
 */
public class ApplicationMeasuring extends EventMonitoring 
{
	// parameters and constants -----------------------------------------------
	// ------------------------------------------------------------------------
	
	/** 
	 * Logger for MonitoringEngines. 
	 */
	private static final Logger LOG = Logger.getLogger(MonitoringEngine.class);

	protected static final long UNDEFINED = -1L;
	protected static final Character CRLF = Character.LINE_SEPARATOR;

	private static final String PAR_EXPIRY = "expiry";
	
	
	// inner classes ----------------------------------------------------------
	// ------------------------------------------------------------------------
	
	protected static class ComponentData
	{
		public Component component = null;
		public long plugging = UNDEFINED;
		public long activation = UNDEFINED;
		public long passivation = UNDEFINED;
		public long unplugging = UNDEFINED;
		public long advertisements = 0;
		public long subscriptions = 0;
		public long notifications = 0;
	}
	
	protected static class EventData
	{
		public Event event = null;
		public long first = UNDEFINED;
		public long last = UNDEFINED;
		public int sent = 0;
		public int processed = 0;
		public int received = 0;
		// TODO parzy to remove
		public boolean flag = false;
	}
	
	protected static class ScopeSelector implements EntrySelector
	{
		@Override
		public boolean select(RoutingEntry entry) 
		{
			Filter f = entry.getFilter();
			if ( !(f instanceof ScopedFilter))
			{
				return false;
			}
			ScopedFilter s = (ScopedFilter)f;
			return s.getFilter() instanceof ScopeIdentifier;
		}
	}
	
	
	// static fields ----------------------------------------------------------
	// ------------------------------------------------------------------------
	
	// selector for counting scope identifiers
	protected static EntrySelector selector = new ScopeSelector();
	
	// maps for storing data about producers, consumers, and events
	protected static HashMap<Component, ComponentData> components;
	protected static HashMap<Id,EventData> events;	
	
	// numbers of evaluated producers, consumers, and events
	protected static long publishers;
	protected static long subscribers;
	protected static long advertisements;
	protected static long subscriptions;
	protected static long notifications;
	protected static long messages;
	protected static long processings;
	
	// avg spread of notifications
	protected static double avgReceipts;  // consumed notifications per component
	protected static double avgReceivers; // receivers per notification
	protected static double avgLinks;     // traveled links per notification
	protected static double avgHops;      // processing hops per notification
	
	// average delay of advertisements, subscriptions, and notifications
	protected static double advertisementDelay;
	protected static double subscriptionDelay;
	protected static double notificationDelay;
	
	// expiry time of messages
	protected static long expiry;
	
	// average activation delay of producers and consumers
	protected static double publisherActivation;
	protected static double subscriberActivation;
	
	// number of producers and consumers which were not activated
	protected static long inactivePublishers;
	protected static long inactiveSubscribers;
	
	// average/max routing table sizes for all entries/scope identifiers
	protected static double avgRouting;
	protected static double maxRouting;
	protected static double avgScopeRouting;
	protected static double maxScopeRouting;
	
	// average/max advertisement table sizes for all entries/scope identifiers
	protected static double avgAdvertising;
	protected static double maxAdvertising;
	protected static double avgScopeAdvertising;
	protected static double maxScopeAdvertising;
	
	// distributions
	protected static long[] subscriberHosts;
	protected static long[] subscriberProfiles;
	protected static long[] publisherHosts;
	protected static long[] publisherProfiles;
	
	// number of measurements
	protected static long measurements;
	
	// TODO parzy to remove
	protected static long pubs = 0;
	protected static long processed = 0;
	protected static long nv = 0;
//	protected static long clean = UNDEFINED;
	
	// instantiation ----------------------------------------------------------
	// ------------------------------------------------------------------------

	/**
	 * @param name
	 */
	public ApplicationMeasuring(String name) 
	{
		super(name);
			
		// get expiry time for events
		expiry = Configuration.getLong(name+"."+PAR_EXPIRY,interval);
		
		// data maps
		components = new HashMap<Component, ComponentData>();
		events = new HashMap<Id,EventData>();
		
		// counters
		publishers = subscribers = advertisements = subscriptions =
		notifications = inactivePublishers = inactiveSubscribers = 0;
		
		// notification spread
		avgReceipts = avgReceivers = 0;
		
		// delays
		advertisementDelay = subscriptionDelay = notificationDelay =
		publisherActivation = subscriberActivation = 0;
		
		// sizes
		avgRouting = maxRouting = avgScopeRouting = maxScopeRouting = 
		avgAdvertising = maxAdvertising = avgScopeAdvertising = 
		maxScopeAdvertising = 0;
		
		// distributions
		subscriberHosts = subscriberProfiles = publisherHosts = 
		publisherProfiles = new long[0];
		
		// measurements
		measurements = 0;
	}

	// ------------------------------------------------------------------------
	// overridden hooks (instance dispatching methods) ------------------------
	// ------------------------------------------------------------------------

	
	// statistics about components (producers and consumers) ------------------
	// ------------------------------------------------------------------------
	
	@Override
	public void afterPlugging(ComponentSink sink)
	{
		// create and register a new component data object
		ComponentData data = new ComponentData();
		data.component = sink.getComponent();
		data.plugging = CommonState.getTime();
		components.put(data.component, data);
	}
	
	@Override
	public void beforeActivation(ComponentSink sink)
	{
		ComponentData data = components.get(sink.getComponent());
		data.activation = CommonState.getTime();
	}

	@Override 
	public void afterPassivation(ComponentSink sink)
	{
		ComponentData data = components.get(sink.getComponent());
		data.passivation = CommonState.getTime();
	}
	
	@Override
	public void beforeUnplugging(ComponentSink sink)
	{
		ComponentData data = components.get(sink.getComponent());
		data.unplugging = CommonState.getTime();
	}
	
	
	// statistics about messages (events, advertisements, and subscriptions) --
	// ------------------------------------------------------------------------
	
	@Override
	public void afterProducing(Event event, ComponentSink sink)
	{
		// create and register a new event data object
		EventData data = new EventData();
		data.event = event;
		data.first = CommonState.getTime();
		events.put(event.getId(), data);

		// count events per component
		Component comp = sink.getComponent();
		ComponentData info = components.get(comp);	
		if ( (event instanceof Advertisement) && !(event instanceof ScopeAdvertisement) )
		{
			info.advertisements++;
		}
		else if ( (event instanceof Subscription) && !(event instanceof ScopeSubscription) )
		{
			info.subscriptions++;
		}
		else if ( event instanceof NameValueEvent)
		{
			info.notifications++;
		}
	
		// TODO parzy to remove
		pubs++;
	}
	
	@Override
	public void afterProcessing(Event event, EventProcessor source)
	{
		EventData data = events.get(event.getId());
		if (data==null) return;
		data.last = CommonState.getTime();
		data.processed++;
		processings++;
		
		processed++;
	}
	
	@Override
	public void beforeSending(Event event, EventSink sink) 
	{ 
		ContactProfile profile = sink.getContactProfile();
		if (profile instanceof ComponentContactProfile) return;
		EventData data = events.get(event.getId());
		if (data==null) return;
		data.last = CommonState.getTime();
		data.sent++;
		messages++;
	}
	
	@Override
	public void beforeConsuming(Event event, ComponentSink sink)
	{
		// update event data
		EventData data = events.get(event.getId());
		if (data==null) return;
		data.last = CommonState.getTime();
		data.received++;
		
		// update component info
		Component comp = sink.getComponent();
		ComponentData info = components.get(comp);
		if (event instanceof NameValueEvent)
		{
			info.notifications++;
		}		
	}
	
	
	
	// periodic measurements --------------------------------------------------
	// ------------------------------------------------------------------------

	@Override
	public void prepare()
	{
		messages = 0;
		processings = 0;
	}
	
	// TODO parzy implement
	@Override
	public void measure() 
	{
		measureTables();
		measureComponents();
		measureEvents();
		measurements++;
	}	

	public void measureTables()
	{
		double avg, avgScopes, max, maxScopes;

		// count subscriptions
		avg = avgScopes = max = maxScopes = 0.0d;
		for (int i=0; i<Network.size(); i++)
		{
			Node node = Network.get(i);
			ApplicationMeasuring measuring = (ApplicationMeasuring)node.getProtocol(pid);
			RoutingEngine routing = measuring.getEngine(RoutingEngine.class);
			if (routing==null) break;
			int size = routing.getTableSize();
			avg = average(size,avg,i);
			max = Math.max(max, size);
			int scopes = routing.getTableSize(selector);
			avgScopes = average(scopes,avgScopes,i);
			maxScopes = Math.max(maxScopes, scopes);
		}
		
		avgRouting = average(avg, avgRouting, measurements);
		maxRouting = Math.max(maxRouting, max);
		avgScopeRouting = average(avgScopes,avgScopeRouting,measurements);
		maxScopeRouting = Math.max(maxScopeRouting, maxScopes);

		// count advertisements
		avg = avgScopes = max = maxScopes = 0.0d;
		for (int i=0; i<Network.size(); i++)
		{
			Node node = Network.get(i);
			ApplicationMeasuring measuring = (ApplicationMeasuring)node.getProtocol(pid);
			AdvertisementEngine advertising = measuring.getEngine(AdvertisementEngine.class);
			if (advertising==null) break;
			int size = advertising.getTableSize();
			avg = average(size,avg,i);
			max = Math.max(max, size);
			int scopes = advertising.getTableSize(selector);
			avgScopes = average(scopes,avgScopes,i);
			maxScopes = Math.max(maxScopes, scopes);
		}
		
		avgAdvertising = average(avg, avgAdvertising, measurements);
		maxAdvertising = Math.max(maxAdvertising, max);
		avgScopeAdvertising = average(avgScopes,avgScopeAdvertising,measurements);
		maxScopeAdvertising = Math.max(maxScopeAdvertising, maxScopes);
	}
		
	public void measureComponents()
	{
		for (ComponentData data : components.values())
		{
			if ( data.unplugging!=UNDEFINED)
			{
				Component comp = data.component;
				if (comp instanceof EventProducing.ProducingComponent)
				{
					measureProducer(data);
				}
				else if (comp instanceof EventConsuming.ConsumingComponent)
				{
					measureConsumer(data);
				}
			}
		}
	}
	
	public void measureProducer(ComponentData data)
	{
		// measure distributions
		ApplicationProtocol.ApplicationComponent comp = (ApplicationProtocol.ApplicationComponent) data.component;
		publisherHosts = measureHostDistribution(comp,publisherHosts);
		publisherProfiles = measureProfileDistribution(comp,publisherProfiles);

		
		if (data.activation==UNDEFINED)
		{
			inactivePublishers++;
			return;
		}
		
		// calculate new average activation delay
		publisherActivation = average(data.activation-data.plugging,
				                      publisherActivation,publishers);
		// increment number of evaluated publishers
		publishers++;		
	}
	
	public void measureConsumer(ComponentData data)
	{
		// measure distributions
		ApplicationProtocol.ApplicationComponent comp = (ApplicationProtocol.ApplicationComponent) data.component;
		subscriberHosts = measureHostDistribution(comp,subscriberHosts);
		subscriberProfiles = measureProfileDistribution(comp,subscriberProfiles);
		
		// just considering activated components
		if (data.activation==UNDEFINED)
		{
			inactiveSubscribers++;
			return;
		}
		
		// calculate new average activation delay
		subscriberActivation = average(data.activation-data.plugging,
				                      subscriberActivation,subscribers);
		
		// calculate new average consumed notifications
		avgReceipts = average(data.notifications,avgReceipts,subscribers);
		
		// increment number of evaluated publishers
		subscribers++;
	}
	
	public long[] measureHostDistribution(ApplicationProtocol.ApplicationComponent comp, long[] hosts)
	{	
		Node host = comp.getNode();
		int i = host.getIndex();
		if (i>=hosts.length)
		{
			long[] tmp = new long[i+1];
			System.arraycopy(hosts, 0, tmp, 0, hosts.length);
			hosts = tmp;
		}
		hosts[i]++;
		return hosts;
	}
	
	public long[] measureProfileDistribution(ApplicationProtocol.ApplicationComponent comp, long[] profiles)
	{	
		ApplicationProtocol.Profile profile = comp.getApplicationProfile();
		int i = profile.getNumber();
		if (i>=profiles.length)
		{
			long[] tmp = new long[i+1];
			System.arraycopy(profiles, 0, tmp, 0, profiles.length);
			profiles = tmp;
		}
		profiles[i]++;
		return profiles;
	}
	
	public void measureEvents()
	{
		for (EventData data : events.values())
		{
			long time = CommonState.getTime();
			if ( data.last != UNDEFINED && data.last + expiry < time )
			{
				Event event = data.event;
				if (((event instanceof Advertisement) && !(event instanceof ScopeAdvertisement)) ||
				    ((event instanceof Unadvertisement) && !(event instanceof ScopeUnadvertisement)))
				{
					measureAdvertisement(data);
				}
				else if ( (event instanceof Subscription) && !(event instanceof ScopeSubscription) )
				{
					measureSubscription(data);
				}
				else if ( event instanceof NameValueEvent)
				{
					measureNotification(data);
				}
			}
		}
	}
	
	public void measureAdvertisement(EventData data)
	{
		// calculate new average advertisement delay to become effective
		advertisementDelay = average(data.last-data.first,
				                     advertisementDelay,publishers);
		advertisements++;
	}

	public void measureSubscription(EventData data)
	{
		// calculate new average advertisement delay to become effective
		subscriptionDelay = average(data.last-data.first,
				                    subscriptionDelay,subscriptions);
		subscriptions++;
	}
	
	public void measureNotification(EventData data)
	{
		// calculate new average advertisement delay to become effective
		notificationDelay = average(data.last-data.first,
				                    notificationDelay,notifications);

		// calculate average number of receivers
		avgReceivers = average(data.received,avgReceivers,notifications);
		System.err.println("event: " + data.event + ", receivers: "+data.received);
		
		// calculate average number of traveled links
		avgLinks = average(data.sent,avgLinks,notifications);
		
		// calculate average number of processing hops
		avgHops = average(data.processed,avgHops,notifications);
//		System.err.println("hops: "+data.processed);
		
		notifications++;
	}
	
	// calculates the running cumulative average
	public double average(double value, double average, long items)
	{
		return (average*(double)items + value) / ((double)items + 1.0D);
	}

	// house keeping ----------------------------------------------------------
	// ------------------------------------------------------------------------
	
	@Override
	public void cleanup()
	{
		// remove unplugged components
		for (Iterator<Map.Entry<Component,ComponentData>> it = components.entrySet().iterator(); it.hasNext(); )
		{
			Map.Entry<Component,ComponentData> entry = it.next();
			ComponentData data = entry.getValue();
			if (data.unplugging!=UNDEFINED)	it.remove();
		}
		
		// remove expired events
		long time = CommonState.getTime();
		for (Iterator<Map.Entry<Id,EventData>> it = events.entrySet().iterator(); it.hasNext(); )
		{
			Map.Entry<Id,EventData> entry = it.next();
			EventData data = entry.getValue();
			if (data.last != UNDEFINED && data.last + expiry < time)
			{
				if (data.event instanceof NameValueEvent)
				{
					nv++;
				}
				it.remove();
			}
		}
	}
	
	
	// save results -----------------------------------------------------------
	// ------------------------------------------------------------------------
	
	@Override
	public void report()
	{
		// construct result string
//		String rst = "";
//		rst += advertisementDelay+";"+subscriptionDelay+";"+notificationDelay+";";
//		rst += publisherActivation+";"+subscriberActivation+";";
//		rst += avgAdvertising+";"+maxAdvertising+";"+avgScopeAdvertising+";"+maxScopeAdvertising+";";
//		rst += avgRouting+";"+maxRouting+";"+avgScopeRouting+";"+maxScopeRouting+";";
//		rst += inactivePublishers+";"+inactiveSubscribers+";";
		
		String rst = "";
		rst += "measurement results" + CRLF;
		rst += "number of unique notifications: " + notifications + CRLF;
		rst += "average number of receivers: " + avgReceivers + CRLF;
		rst += "average number of travelled links: " + avgLinks + CRLF;
		rst += "average number of processing hops: " + avgHops + CRLF;
		rst += "number of unique (un)subscriptions: " + subscriptions + CRLF;
		rst += "number of unique (un)advertisements: " + advertisements + CRLF;
		rst += "overall messages: " + messages + CRLF;
		rst += "overall processings: " + processings + CRLF;
		rst += "number of publishers: " + publishers + CRLF;
		rst += "inactive publishers: " + inactivePublishers + CRLF; 
		rst += "number of subscribers: " + subscribers + CRLF;
		rst += "inactive subscribers: " + inactiveSubscribers + CRLF;
		rst += "average number of receipts: " + avgReceipts + CRLF;
		rst += "advertisement delay: " + advertisementDelay + CRLF; 
		rst += "subscription delay: " + subscriptionDelay + CRLF;
		rst += "notification delay: " + notificationDelay + CRLF;
		rst += "publisher activation: " + publisherActivation + CRLF;
		rst += "subscriber activation: " + subscriberActivation +CRLF;
		rst += "average advertisement table size: " + avgAdvertising + CRLF;
		rst += "max advertisement table size: " + maxAdvertising + CRLF;
		rst += "average scope advertisement table size: " + avgScopeAdvertising + CRLF;
		rst += "max scope advertisement table size: " + maxScopeAdvertising + CRLF;
		rst += "average subscription table size: " + avgRouting + CRLF;
		rst += "max subscription table size: " + maxRouting + CRLF;
		rst += "average scope subscription table size: " + avgScopeRouting + CRLF;
		rst += "max scope subscription table size: " + maxScopeRouting + CRLF;
		rst += "publisher profiles: " + publisherProfiles.length + CRLF;
		rst += "publisher profile distribution: " + Arrays.toString(publisherProfiles) + CRLF;
		rst += "publisher host distribution: " + Arrays.toString(publisherHosts) + CRLF;
		rst += "subscriber profiles: " + subscriberProfiles.length + CRLF;
		rst += "subscriber profile distribution: " + Arrays.toString(subscriberProfiles) + CRLF;
		rst += "subscriber host distribution: " + Arrays.toString(subscriberHosts) + CRLF;

		rst += "created pubs: " + pubs + CRLF;
		rst += "processed things: " + processed + CRLF;
		rst += "name values: " + nv + CRLF;
		
		// log results
		if (LOG.isInfoEnabled())
		{
//			LOG.info("measurement results: " + rst);
			LOG.info(rst);
		}
		
		// save results
		if (file != null)
		{
			try
			{
				FileWriter out = new FileWriter(file);
				out.write(rst);
				out.close();
			}
			catch (IOException e)
			{
				LOG.error("error writing results: "+rst,e);
			}
		}
	}
}
