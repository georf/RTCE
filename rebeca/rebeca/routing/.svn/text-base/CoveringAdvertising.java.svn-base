/*
 * $Id$
 */
package rebeca.routing;

import rebeca.Broker;
import rebeca.Filter;
import rebeca.component.ComponentSink;
import rebeca.event.Advertisement;
import rebeca.event.SubscriptionFactory;
import rebeca.event.Unadvertisement;
import rebeca.routing.FilterTable.FilterSelector;


/**
 * @author parzy
 *
 */
public class CoveringAdvertising extends BasicAdvertisementEngine 
{
	// Constructors -----------------------------------------------------------
	// ------------------------------------------------------------------------

	public CoveringAdvertising()
	{
		this(null,(RoutingTable)null);
	}
	
	public CoveringAdvertising(Broker broker)
	{
		this(broker,(RoutingTable)null);
	}

	public CoveringAdvertising(Broker broker, RoutingEngine routing)
	{
		this(broker,routing != null ? routing.getTable() : null);
	}
	
	public CoveringAdvertising(Broker broker, RoutingTable subscriptions)
	{
		super(broker,subscriptions,new CoveringRouting(broker));
	}
	
	/**
	 * PeerSim convenience constructor ignores arguments and calls the default 
	 * constructor.
	 * @param prefix ignored
	 */
	public CoveringAdvertising(String prefix)
	{
		this();
	}

	
	
	protected class CoveringComponentSink extends AdvertisementComponentSink
	{
		public CoveringComponentSink(ComponentSink sink)
		{
			this( sink,new AdvertisementFilterTable(new BasicFilterTable()), 
					   new AdvertisementFilterTable(new IdentityFilterTable()));
		}
		
		protected CoveringComponentSink(ComponentSink sink, 
				AdvertisementFilterTable filters, 
				AdvertisementFilterTable advertisements)
		{
			super(sink,filters,advertisements);
		}

		@Override
		public void advertise(Filter filter)
		{
			FilterSelector selector = CoveringFilterTable.getCoveringSelector(filter);
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
			
			FilterSelector covering = CoveringFilterTable.getCoveringSelector(filter);
			if (filters.containsFilters(covering))
			{
				return;
			}
			
			Unadvertisement u = SubscriptionFactory.getUnadvertisement(filter);
			FilterSelector covered = CoveringFilterTable.getCoveredSelector(filter);
			u.addAllUncovered(filters.getFilters(covered));
			out.out(u);
		}
	}
}
