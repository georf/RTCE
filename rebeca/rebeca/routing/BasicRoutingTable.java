/*
 * $id$
 */
package rebeca.routing;

import java.util.*;

import rebeca.*;


/**
 * @author parzy
 *
 */
public class BasicRoutingTable implements RoutingTable 
{

	// TODO parzy maybe replace by an array list or a hash set?
	protected Collection<RoutingEntry> table;
	
	public BasicRoutingTable()
	{
		table = new LinkedList<RoutingEntry>();
	}
	
	public BasicRoutingTable(RoutingTable table)
	{
		this.table = table.getTable();
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<RoutingEntry> iterator() {
		// TODO Auto-generated method stub
		return table.iterator();
	}

	public Collection<RoutingEntry> getTable()
	{
		return table;
	}
	
	public Collection<RoutingEntry> getEntries(EntrySelector selector)
	{
		LinkedList<RoutingEntry> rst = new LinkedList<RoutingEntry>();
		for (RoutingEntry e : table)
		{
			if (selector.select(e))
			{
				rst.add(e);
			}
		}
		return rst;
	}
	
	public boolean removeEntries(EntrySelector selector)
	{
		boolean modified = false;
		for (Iterator<RoutingEntry> it = table.iterator(); it.hasNext(); )
		{
			if(selector.select(it.next()))
			{
				it.remove();
				modified = true;
			}
		}
		return modified;
	}
	
	public Collection<EventProcessor> getDestinations(EntrySelector selector)
	{
		LinkedHashSet<EventProcessor> rst = new LinkedHashSet<EventProcessor>();
		for (RoutingEntry e : table)
		{
			if (selector.select(e))
			{
				rst.add(e.getDestination());
			}
		}
		return rst;
	}
	
	public Collection<Filter> getFilters(EntrySelector selector)
	{
		LinkedHashSet<Filter> rst = new LinkedHashSet<Filter>();
		for (RoutingEntry e : table)
		{
			if (selector.select(e))
			{
				rst.add(e.getFilter());
			}
		}
		return rst;
	}
	
	
	public boolean add(Filter f, EventProcessor p)
	{
		RoutingEntry entry = new BasicRoutingEntry(f,p);
		EntrySelector selector = new BasicEntrySelector(entry);
		if (! this.getEntries(selector).isEmpty())
		{
			return false;
		}
		return table.add(entry);
	}
	
	public boolean remove(Filter f, EventProcessor p)
	{
		RoutingEntry entry = new BasicRoutingEntry(f,p);
		EntrySelector selector = new BasicEntrySelector(entry);
		return this.removeEntries(selector);
	}
	
	// statistics -------------------------------------------------------------
	// ------------------------------------------------------------------------
	
	public int size()
	{
		return table.size();
	}
	
	public int size(EntrySelector selector)
	{
		return getEntries(selector).size();
	}
	
	// visualization ----------------------------------------------------------
	// ------------------------------------------------------------------------
	
	@Override
	public String toString()
	{
		return table.toString();
	}
	
	protected static class BasicRoutingEntry implements RoutingEntry
	{
		Filter filter;
		EventProcessor destination;
		
		public BasicRoutingEntry()
		{
			this(null,null);
		}
		
		public BasicRoutingEntry(Filter filter)
		{
			this(filter,null);
		}
		
		public BasicRoutingEntry(EventProcessor destination)
		{
			this(null,destination);
		}
		
		public BasicRoutingEntry(Filter filter, EventProcessor destination)
		{
			this.filter = filter;
			this.destination = destination;
		}
		
		public Filter getFilter()
		{
			return filter;
		}
		
		public EventProcessor getDestination()
		{
			return destination;
		}
		
		@Override
		public boolean equals(Object o)
		{
			if (! (o instanceof RoutingEntry))
			{
				return false;
			}
			
			RoutingEntry e = (RoutingEntry)o;
			return (filter == null ? e.getFilter() == null : filter.equals(e.getFilter()) ) &&
			       (destination == null ? e.getDestination() == null : destination.equals(e.getDestination()) );
		}
		
		@Override
		public int hashCode()
		{
			return (filter == null ? 20080306 : filter.hashCode()) ^
				   (destination == null ? 6032008 : destination.hashCode());
		}
		
		@Override
		public String toString()
		{
			return "'" + filter + "' from '" + destination + "'";
		}
	}
	
	protected static class BasicEntrySelector implements EntrySelector
	{
		protected RoutingEntry entry;
		
		public BasicEntrySelector(RoutingEntry entry)
		{
			this.entry = entry;
		}
		
		public boolean select(RoutingEntry entry)
		{
			return this.entry.equals(entry);
		}
	}
	
	public EntrySelector getDestinationSelector(EventProcessor destination)
	{
		return new DestinationSelector(destination);
	}
	
	public EntrySelector getDestinationSelector(EventProcessor destination, boolean equals)
	{
		return new DestinationSelector(destination,equals);
	}

	protected static class DestinationSelector implements EntrySelector
	{
		protected EventProcessor processor;
		protected boolean equals;
		
		public DestinationSelector(EventProcessor processor)
		{
			this(processor,true);
		}
		
		public DestinationSelector(EventProcessor processor, boolean equals)
		{
			this.processor = processor;
			this.equals = equals;
		}
		
		public boolean select(RoutingEntry entry)
		{
			return (!equals) ^ (processor.equals(entry.getDestination()));
		}
	}
}
