/*
 * $id$
 */
package rebeca.routing;

import java.util.*;

import rebeca.EventProcessor;
import rebeca.Filter;

/**
 * @author parzy
 *
 */
public class CoveringRoutingTable extends IdentityRoutingTable 
{
	protected static class CoveredSelector extends EqualsSelector
	{
		protected boolean identical;
		
		public CoveredSelector()
		{
			this(null,null,true);
		}
		
		public CoveredSelector(Filter filter)
		{
			this(filter,null,true);
		}

		public CoveredSelector(Filter filter, boolean identical)
		{
			this(filter,null,identical);
		}
		
		public CoveredSelector(EventProcessor destination)
		{
			this(null,destination,true);
		}

		public CoveredSelector(Filter filter, EventProcessor destination)
		{
			this(filter,destination,true);
		}
		
		public CoveredSelector(Filter filter, EventProcessor destination, boolean identical)
		{
			super(filter,destination);
			this.identical = identical;
		}
		
		public boolean select(RoutingEntry entry)
		{
			if (identical)
			{
				return ( filter == null || (filter.covers(entry.getFilter())) ) &&
				   	   ( destination == null || destination.equals(entry.getDestination()));
			}
			
			return ( filter == null || (filter.covers(entry.getFilter()) && !filter.identical(entry.getFilter())) ) &&
				   ( destination == null || destination.equals(entry.getDestination()));
		}
	}

	
	protected static class CoveringSelector extends EqualsSelector
	{
		protected boolean identical;
		
		public CoveringSelector()
		{
			this(null,null,true);
		}
		
		public CoveringSelector(Filter filter)
		{
			this(filter,null,true);
		}

		public CoveringSelector(Filter filter, boolean identical)
		{
			this(filter,null,identical);
		}
		
		public CoveringSelector(EventProcessor destination)
		{
			this(null,destination,true);
		}

		public CoveringSelector(Filter filter, EventProcessor destination)
		{
			this(filter,destination,true);
		}
		
		public CoveringSelector(Filter filter, EventProcessor destination, boolean identical)
		{
			super(filter,destination);
			this.identical = identical;
		}
		
		public boolean select(RoutingEntry entry)
		{
			if (identical)
			{
				return ( filter == null || (entry.getFilter().covers(filter)) ) &&
				   	   ( destination == null || destination.equals(entry.getDestination()));
			}
			
			return ( filter == null || (entry.getFilter().covers(filter) && !filter.identical(entry.getFilter())) ) &&
				   ( destination == null || destination.equals(entry.getDestination()));
		}

	}
	
	// Constructors -----------------------------------------------------------
	// ------------------------------------------------------------------------

	public CoveringRoutingTable()
	{
		super();
	}
	
	public CoveringRoutingTable(RoutingTable table)
	{
		super(table);
	}
	
	
	
	
	// Overriden to implement identity based routing
	@Override
	public boolean add(Filter f, EventProcessor p)
	{
		// check whether covering entries exist
		EntrySelector selector = new CoveringSelector(f,p,true);
		if (! this.getEntries(selector).isEmpty())
		{
			return false;
		}
		
		// remove all filters that are covered by f
		selector = new CoveredSelector(f,p);
		removeEntries(selector);
		
		// and finally add f
		return table.add(new BasicRoutingEntry(f,p));
	}
	
	@Override
	public boolean remove(Filter f, EventProcessor p)
	{
		EntrySelector selector = new IdentitySelector(f,p);
		return this.removeEntries(selector);
	}
	
	
	
	public Collection<EventProcessor> getCoveringDestinations(Filter filter)
	{
		EntrySelector selector = new CoveringSelector(filter,true);
		return this.getDestinations(selector);
	}
	
	public Collection<EventProcessor> getStrictlyCoveringDestinations(Filter filter)
	{
		EntrySelector selector = new CoveringSelector(filter,false);
		return this.getDestinations(selector);
	}

	public Collection<EventProcessor> getCoveredDestinations(Filter filter)
	{
		EntrySelector selector = new CoveredSelector(filter,true);
		return this.getDestinations(selector);
	}

	public Collection<EventProcessor> getStrictlyCoveredDestinations(Filter filter)
	{
		EntrySelector selector = new CoveredSelector(filter,false);
		return this.getDestinations(selector);
	}
	
	
	@Override
	public Collection<Filter> getFilters(EntrySelector selector)
	{
		Collection<Filter> filters = new LinkedList<Filter>();
		for (RoutingEntry entry : table)
		{
			// continue if selector does not match
			if (!selector.select(entry))
			{
				continue;
			}
			
			// add filter if no covering filter is contained
			// remove covered filters meanwhile
			boolean contained = false;
			for (Iterator<Filter> it = filters.iterator(); it.hasNext(); )
			{
				Filter f = it.next();
				if (f.covers(entry.getFilter()))
				{
					contained = true;
					break;
				}
				if (entry.getFilter().covers(f))
				{
					it.remove();
				}
			}
			if (!contained)
			{
				filters.add(entry.getFilter());
			}
		}
		return filters;
	}
	
	public Collection<Filter> getStrictlyCoveredFilters(Filter filter)
	{
		EntrySelector selector = new CoveredSelector(filter,false);
		// only identical filters needed so that uncovered are still contained
		return super.getFilters(selector);
	}
}
