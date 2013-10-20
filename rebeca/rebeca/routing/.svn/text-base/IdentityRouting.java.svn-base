/*
 * $Id$
 */
package rebeca.routing;

import java.util.Collection;

import rebeca.*;
import rebeca.component.ComponentSink;
import rebeca.event.Advertisement;
import rebeca.event.Subscription;
import rebeca.event.SubscriptionFactory;
import rebeca.event.Unsubscription;
import rebeca.routing.FilterTable.FilterSelector;

/**
 * @author parzy
 *
 */
public class IdentityRouting extends SimpleRouting
{
	// Constructors -----------------------------------------------------------
	// ------------------------------------------------------------------------

	public IdentityRouting()
	{
		this(null,new IdentityRoutingTable());
	}
	
	public IdentityRouting(Broker broker)
	{
		this(broker, new IdentityRoutingTable());
	}
	
	public IdentityRouting(Broker broker, MatchingEngine matching)
	{
		this(broker,matching!=null ? new IdentityRoutingTable(matching.getTable()) : null);
	}
	
	protected IdentityRouting(Broker broker, RoutingTable table)
	{
		super(broker,table);
	}
	
	/**
	 * PeerSim convenience constructor ignores arguments and calls the default 
	 * constructor.
	 * @param prefix ignored
	 */
	public IdentityRouting(String prefix)
	{
		this();
	}

	
	// Getters and Setters ----------------------------------------------------
	// ------------------------------------------------------------------------

	@Override
	public void setTable(MatchingEngine engine)
	{
		setTable(new IdentityRoutingTable(engine.getTable()));
	}
	

	
	
	@Override
	public void subscribe(Subscription subscription, EventProcessor source)
	{
		// add filter to routing table if no identical filter exists
		if (! table.add(subscription.getFilter(), source))
		{
			return;
		}
		
		// determine sources of identical filters
		IdentityRoutingTable table = (IdentityRoutingTable)this.table;
		Collection<EventProcessor> destinations =  
				table.getIdenticalDestinations(subscription.getFilter());
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
		if (! table.remove(unsubscription.getFilter(), source) )
		{
			return;
		}

		// determine sources of identical filters
		IdentityRoutingTable table = (IdentityRoutingTable)this.table;
		Collection<EventProcessor> destinations = 
				table.getIdenticalDestinations(unsubscription.getFilter());
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
		
		// finally forward it
		forward(unsubscription,destinations);
	}
	
	

	
	
	
	
	
	
	
	
	protected static class IdentityComponentSink extends RoutingComponentSink
	{
		
		public IdentityComponentSink(ComponentSink sink)
		{
			super(sink);
			filters = new IdentityFilterTable(filters);
		}
	
		@Override
		public void subscribe(Filter filter)
		{
			FilterSelector selector = IdentityFilterTable.getIdentitySelector(filter);
			boolean forward = !filters.containsFilters(selector);			
			filters.add(filter);
			if (forward)
			{
				Subscription s = SubscriptionFactory.getSubscription(filter);
				out.out(s);
			}
		}
		
		public void unsubscribe(Filter filter)
		{
			if (!filters.remove(filter))
			{
				return;
			}
						
			FilterSelector selector = IdentityFilterTable.getIdentitySelector(filter);
			if (filters.containsFilters(selector))
			{
				return;
			}
			
			Unsubscription u = SubscriptionFactory.getUnsubscription(filter);
			out.out(u);
		}
	}
}
