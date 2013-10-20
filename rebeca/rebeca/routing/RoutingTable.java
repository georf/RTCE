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
public interface RoutingTable extends Iterable<RoutingTable.RoutingEntry>
{
	public interface RoutingEntry
	{
		public Filter getFilter();
		public EventProcessor getDestination();
	}
	
	public interface EntrySelector
	{
		public boolean select(RoutingTable.RoutingEntry entry);
	}
	
	public Collection<RoutingEntry> getEntries(EntrySelector selector);
	
	public Collection<EventProcessor> getDestinations(EntrySelector selector);
	
	public Collection<Filter> getFilters(EntrySelector selector);

	public boolean add(Filter f, EventProcessor destination);
	
	public boolean remove(Filter f, EventProcessor destination);

	public boolean removeEntries(EntrySelector selector);
	
	// TODO parzy check whether this method is really necessary
	public Collection<RoutingEntry> getTable();
	
	public int size();
	
	public int size(EntrySelector selector);
}
