/*
 * $Id$
 */
package rebeca.routing;

import rebeca.Broker;
import rebeca.Event;
import rebeca.Filter;
import rebeca.component.ComponentSink;
import rebeca.event.Advertisement;
import rebeca.event.Subscription;
import rebeca.event.SubscriptionFactory;
import rebeca.event.Unadvertisement;
import rebeca.routing.FilterTable.FilterSelector;

/**
 * @author parzy
 *
 */
public class IdentityAdvertising extends BasicAdvertisementEngine 
{
	// Constructors -----------------------------------------------------------
	// ------------------------------------------------------------------------

	public IdentityAdvertising()
	{
		this(null,(RoutingTable)null);
	}
	
	public IdentityAdvertising(Broker broker)
	{
		this(broker,(RoutingTable)null);
	}

	public IdentityAdvertising(Broker broker, RoutingEngine routing)
	{
		this(broker,routing != null ? routing.getTable() : null);
	}
	
	public IdentityAdvertising(Broker broker, RoutingTable subscriptions)
	{
		super(broker,subscriptions,new IdentityRouting(broker));
	}
	
	/**
	 * PeerSim convenience constructor ignores arguments and calls the default 
	 * constructor.
	 * @param prefix ignored
	 */
	public IdentityAdvertising(String prefix)
	{
		this();
	}

	
	
	
	protected class IdentityComponentSink extends AdvertisementComponentSink
	{
		public IdentityComponentSink(ComponentSink sink)
		{
			this( sink,new AdvertisementFilterTable(new BasicFilterTable()), 
					   new AdvertisementFilterTable(new IdentityFilterTable()));
		}
		
		protected IdentityComponentSink(ComponentSink sink, 
				AdvertisementFilterTable filters, 
				AdvertisementFilterTable advertisements)
		{
			super(sink,filters,advertisements);
		}

		@Override
		public void advertise(Filter filter)
		{
			FilterSelector selector = IdentityFilterTable.getIdentitySelector(filter);
			boolean forward = !filters.containsFilters(selector);			
			filters.add(filter);
			if (forward)
			{
				Advertisement a = SubscriptionFactory.getAdvertisement(filter);
				out.out(a);
			}
		}
		
		@Override
		public void unadvertise(Filter filter)
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
						
			Unadvertisement u = SubscriptionFactory.getUnadvertisement(filter);
			out.out(u);
		}
	}
}
