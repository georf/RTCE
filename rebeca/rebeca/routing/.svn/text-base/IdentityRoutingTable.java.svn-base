/*
 * $id$
 */
package rebeca.routing;

import java.util.*;

import rebeca.EventProcessor;
import rebeca.Filter;
import rebeca.routing.CoveringRoutingTable.CoveredSelector;
import rebeca.routing.RoutingTable.EntrySelector;
import rebeca.routing.RoutingTable.RoutingEntry;
import rebeca.routing.SimpleRoutingTable.EqualsSelector;

/**
 * @author parzy
 *
 */
public class IdentityRoutingTable extends SimpleRoutingTable 
{
	protected static class IdentitySelector extends EqualsSelector
	{
		public IdentitySelector()
		{
			this(null,null);
		}
		
		public IdentitySelector(Filter filter)
		{
			this(filter,null);
		}
		
		public IdentitySelector(EventProcessor destination)
		{
			this(null,destination);
		}
		
		public IdentitySelector(Filter filter, EventProcessor destination)
		{
			super(filter,destination);
		}
		
		public boolean select(RoutingEntry entry)
		{
			return ( filter == null || filter.identical(entry.getFilter()) ) &&
				   ( destination == null || destination.equals(entry.getDestination()));
		}
	}

	
	
	// Constructors -----------------------------------------------------------
	// ------------------------------------------------------------------------

	public IdentityRoutingTable()
	{
		super();
	}
	
	public IdentityRoutingTable(RoutingTable table)
	{
		super(table);
	}

		
	// Overriden to implement identity based routing
	@Override
	public boolean add(Filter f, EventProcessor p)
	{
		EntrySelector selector = new IdentitySelector(f,p);
		if (! this.getEntries(selector).isEmpty())
		{
			return false;
		}
		return table.add(new BasicRoutingEntry(f,p));
	}
	
	@Override
	public boolean remove(Filter f, EventProcessor p)
	{
		EntrySelector selector = new IdentitySelector(f,p);
		return this.removeEntries(selector);
	}
	
	public Collection<EventProcessor> getIdenticalDestinations(Filter f)
	{
		EntrySelector selector = new IdentitySelector(f);
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
			
			// add filter if no identical filter is contained
			boolean contained = false;
			for (Filter f : filters)
			{
				if (f.identical(entry.getFilter()))
				{
					contained = true;
					break;
				}
			}
			if (!contained)
			{
				filters.add(entry.getFilter());
			}
		}
		return filters;
	}
}
