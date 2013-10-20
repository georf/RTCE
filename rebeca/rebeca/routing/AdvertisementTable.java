/*
 * $id$
 */
package rebeca.routing;

import rebeca.*;

/**
 * @author parzy
 *
 */
public class AdvertisementTable extends BasicRoutingTable 
{
	
	public AdvertisementTable(RoutingTable table)
	{
		this.table = table.getTable();
	}

	
	public static class OverlappingSelector 
	extends SimpleRoutingTable.EqualsSelector
	{
		protected boolean equals;
		
		public OverlappingSelector()
		{
			this(null,null);
		}
		
		public OverlappingSelector(Filter filter)
		{
			this(filter,null);
		}
		
		public OverlappingSelector(EventProcessor destination)
		{
			this(null,destination);
		}
		
		public OverlappingSelector(Filter filter, EventProcessor destination)
		{
			super(filter,destination);
		}
		
		
		public boolean select(RoutingEntry entry)
		{
			return ( destination == null || destination.equals(entry.getDestination()) ) &&
			       ( filter == null || filter.overlaps(entry.getFilter()) );
		}	
	}
		
	// selector to determine subscriptions which overlap a newly arrived advertisement
	public class ServingSelector 
	extends SimpleRoutingTable.EqualsSelector
	{
		
		public ServingSelector(Filter filter, EventProcessor destination)
		{
			super(filter,destination);
		}
		
		public boolean select(RoutingEntry entry)
		{
			// looking for overlapping subscriptions not coming from destination
			if ( destination.equals(entry.getDestination()) ||
				 !filter.overlaps(entry.getFilter()) )
			{
				return false;
			}
			
			// check whether subscription is only covered by the new advertisement
			for (RoutingEntry e : AdvertisementTable.this)
			{
				if ( destination.equals(e.getDestination()) &&
					 e.getFilter().overlaps(entry.getFilter()) )
				{
					return false;
				}
			}
			
			// ok, done
			return true;
		}	
	}
	
	// selector to determine subscriptions that are not overlapped anymore 
	// (after an unadvertisement) 
	public class UnservingSelector 
	extends SimpleRoutingTable.EqualsSelector
	{
		
		public UnservingSelector(Filter filter, EventProcessor destination)
		{
			super(filter,destination);
		}
		
		public boolean select(RoutingEntry entry)
		{
			// looking for overlapping subscriptions not coming from destination
			if ( destination.equals(entry.getDestination()) ||
				 (filter != null && !filter.overlaps(entry.getFilter())) )
			{
				return false;
			}
			
			// check whether subscription is not overlapped by any other advertisement
			for (RoutingEntry e : AdvertisementTable.this)
			{
				if ( !e.getDestination().equals(entry.getDestination()) &&
					 e.getFilter().overlaps(entry.getFilter()) )
				{
					return false;
				}
			}
			
			// ok, done
			return true;
		}	
	}
	
	public boolean isAdvertised(Filter filter, EventProcessor destination)
	{
		EntrySelector selector = new OverlappingSelector(filter,destination);
		return getDestinations(selector).size() != 0;
	}
	
	public EntrySelector getServingSelector(Filter filter, EventProcessor destination)
	{
		return new ServingSelector(filter, destination);
	}
	
	public EntrySelector getUnservingSelector(Filter filter, EventProcessor destination)
	{
		return new UnservingSelector(filter, destination);
	}

}
