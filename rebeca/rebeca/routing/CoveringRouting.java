/*
 * $Id$
 */
package rebeca.routing;

import java.util.*;

import rebeca.*;
import rebeca.component.ComponentSink;
import rebeca.event.Advertisement;
import rebeca.event.Subscription;
import rebeca.event.SubscriptionFactory;
import rebeca.event.Unadvertisement;
import rebeca.event.Unsubscription;
import rebeca.routing.BasicRoutingEngine.RoutingComponentSink;
import rebeca.routing.FilterTable.FilterSelector;

/**
 * @author parzy
 *
 */
public class CoveringRouting extends IdentityRouting
{
	// Constructors -----------------------------------------------------------
	// ------------------------------------------------------------------------

	public CoveringRouting()
	{
		this(null,new CoveringRoutingTable());
	}
	
	public CoveringRouting(Broker broker)
	{
		this(broker,new CoveringRoutingTable());
	}
	
	public CoveringRouting(Broker broker, MatchingEngine matching)
	{
		this(broker,matching!=null ? new CoveringRoutingTable(matching.getTable()) : null);
	}
	
	protected CoveringRouting(Broker broker, RoutingTable table)
	{
		super(broker,table);
	}
	
	/**
	 * PeerSim convenience constructor ignores arguments and calls the default 
	 * constructor.
	 * @param prefix ignored
	 */
	public CoveringRouting(String prefix)
	{
		this();
	}

	
	// Getters and Setters ----------------------------------------------------
	// ------------------------------------------------------------------------

	@Override
	public void setTable(MatchingEngine engine)
	{
		setTable(new CoveringRoutingTable(engine.getTable()));
	}
	
	
	
	@Override
	public void subscribe(Subscription subscription, EventProcessor source)
	{
		// add filter to routing table if no covering filter exists
		if (! table.add(subscription.getFilter(), source))
		{
			return;
		}
		
		// determine sources of covering filters
		CoveringRoutingTable table = (CoveringRoutingTable)this.table;
		Collection<EventProcessor> destinations = 
				table.getCoveringDestinations(subscription.getFilter());
		destinations.remove(source);
		
		// 2 or more found -> subscriptions needs not to be forwarded anymore
		if (destinations.size() >= 2)
		{
			return;
		}
		
		// 0 found -> subscription needs to be forwarded to all neighbors
		else if (destinations.size() == 0)
		{
			destinations = getDestinations();
			destinations.remove(source);
		}

		// finally forward it
		forward(subscription,destinations);
	}
	
	@Override
	public void unsubscribe(Unsubscription unsubscription, EventProcessor source)
	{
		// check whether the routing table contains a corresponding subscription
		Filter filter = unsubscription.getFilter();
		if (! table.remove(filter, source) )
		{
			return;
		}
		
		// add all uncovered subscriptions that were included
		for (Subscription s : unsubscription.getUncovered())
		{
			table.add(s.getFilter(), source);
		}

		// determine sources of covering filters
		CoveringRoutingTable table = (CoveringRoutingTable)this.table;
		Collection<EventProcessor> destinations = 
				table.getCoveringDestinations(filter);
		destinations.remove(source);
		
		// found 2 or more => unsubscription needs not to be forwarded anymore
		if (destinations.size() >= 2)
		{
			return;
		}

		// found 1 => unsubscription needs only forwarded towards this one
		// found 0 => unsubscription needs to be forwarded to all neighbors 
		else if (destinations.size() == 0)
		{
			destinations = getDestinations();
			destinations.remove(source);
		}

		// now determine all really uncovered filters
		LinkedHashMap<Filter,Collection<EventProcessor>> map = 
				new LinkedHashMap<Filter,Collection<EventProcessor>>();  
		Collection<Filter> uncoveredFilters = 
				table.getStrictlyCoveredFilters(filter);

		// for each uncovered filter
		for (Filter f : uncoveredFilters)
		{
			// try to find a new really covering one
			Collection<EventProcessor> coveringDestinations = table.getStrictlyCoveringDestinations(f);
			Collection<EventProcessor> uncoveredDestinations = null;

			// found 2 or more different sources of really covering filters ->
			// this uncovered filter needs not to be forwarded anymore
			if (coveringDestinations.size()>=2)
			{
				continue;
			}
			
			// found only one issuer of a really covering filter ->
			// the uncovered filter needs only to be forwarded towards the
			// the issuer of the really covering one
			else if (coveringDestinations.size() == 1)
			{
				uncoveredDestinations = new LinkedHashSet<EventProcessor>(coveringDestinations);
				uncoveredDestinations.remove(source);
				// except the covering filter came from the unsubscribing source 
				if (uncoveredDestinations.size()==0)
				{
					continue;
				}
			}
			
			// found no really covering filter ->
			// the uncovered filter has to be forwarded to all destinations 
			// (except towards its issuer)
			else
			{
				uncoveredDestinations = new LinkedHashSet<EventProcessor>(destinations);
				// determine issuer and check for identical subscriptions as well
				Collection<EventProcessor> identicalDestinations = table.getIdenticalDestinations(f);
				// remove issuer when exactly 1 is found (otherwise, the
				// uncovered filter must be forwarded to all destinations)
				if (identicalDestinations.size() == 1)
				{
					uncoveredDestinations.removeAll(identicalDestinations);
				}
			}
			
			map.put(f, uncoveredDestinations);
		}

		// finally forward the unsubscription along with uncovered filters
		forward(unsubscription,destinations,map);
	}
	
	public void forward(Unsubscription unsubscription, Collection<EventProcessor> 
			destinations, Map<Filter,Collection<EventProcessor>> uncoveredMap)
	{
		for (EventProcessor p : destinations)
		{
			unsubscription.clearUncoveredSubscriptions();
			for (Map.Entry<Filter, Collection<EventProcessor>> entry : uncoveredMap.entrySet())
			{
				if (entry.getValue().contains(p))
				{
					unsubscription.addUncovered(entry.getKey());
				}
			}
			p.process(unsubscription);
		}
	}
	
	
	
	
	
	
	
	
	protected static class CoveringComponentSink extends RoutingComponentSink
	{
		
		public CoveringComponentSink(ComponentSink sink)
		{
			super(sink);
			filters = new CoveringFilterTable(filters);
		}
	
		@Override
		public void subscribe(Filter filter)
		{
			FilterSelector selector = CoveringFilterTable.getCoveringSelector(filter);
			boolean forward = !filters.containsFilters(selector);			
			filters.add(filter);
			if (forward)
			{
				Subscription s = SubscriptionFactory.getSubscription(filter);
				out.out(s);
			}
		}
		
		@Override
		public void unsubscribe(Filter filter)
		{
			if (!filters.remove(filter))
			{
				return;
			}
			
			FilterSelector covering = CoveringFilterTable.getCoveringSelector(filter);
			if (filters.containsFilters(covering))
			{
				return;
			}
			
			Unsubscription u = SubscriptionFactory.getUnsubscription(filter);
			FilterSelector covered = CoveringFilterTable.getCoveredSelector(filter);
			u.addAllUncovered(filters.getFilters(covered));
			out.out(u);
		}
	}
}

