/*
 * $id$
 */
package rebeca.routing;


import rebeca.*;
import rebeca.routing.RoutingTable.EntrySelector;
import rebeca.routing.RoutingTable.RoutingEntry;

/**
 * @author parzy
 *
 */
public class SimpleRoutingTable extends BasicRoutingTable
{
	public static class EqualsSelector implements EntrySelector
	{
		protected Filter filter;
		protected EventProcessor destination;
		
		public EqualsSelector()
		{
			this(null,null);
		}
		
		public EqualsSelector(Filter filter)
		{
			this(filter,null);
		}
		
		public EqualsSelector(EventProcessor destination)
		{
			this(null,destination);
		}
		
		public EqualsSelector(Filter filter, EventProcessor destination)
		{
			this.filter = filter;
			this.destination = destination;
		}
		
		public boolean select(RoutingEntry entry)
		{
			return ( filter == null || filter.equals(entry.getFilter()) ) &&
				   ( destination == null || destination.equals(entry.getDestination()));
		}
	}	

	// Constructors -----------------------------------------------------------
	// ------------------------------------------------------------------------
	
	public SimpleRoutingTable()
	{
		super();
	}
	
	public SimpleRoutingTable(RoutingTable table)
	{
		super(table);
	}
	
	
	
	public boolean add(Filter f, EventProcessor p)
	{
		EntrySelector selector = new EqualsSelector(f,p);
		if (! this.getEntries(selector).isEmpty())
		{
			return false;
		}
		return table.add(new BasicRoutingEntry(f,p));
	}
	
	public boolean remove(Filter f, EventProcessor p)
	{
		EntrySelector selector = new EqualsSelector(f,p);
		return this.removeEntries(selector);
	}
}
