package rebeca.routing;

import java.util.*;

import rebeca.*;
import rebeca.routing.RoutingTable.EntrySelector;
import rebeca.routing.RoutingTable.RoutingEntry;

public class BasicMatchingTable extends BasicRoutingTable 
{
	protected static class MatchingSelector implements EntrySelector
	{
		Event event;
		
		public MatchingSelector(Event event)
		{
			this.event = event;
		}
		
		public boolean select(RoutingEntry entry)
		{
			return entry.getFilter().match(event);
		}
	}
	
	public Collection<EventProcessor> getMatchingDestinations(Event event)
	{
		MatchingSelector selector = new MatchingSelector(event);
	    return getDestinations(selector);
	}
}
