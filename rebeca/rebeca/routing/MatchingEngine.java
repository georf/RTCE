/*
 * $Id$
 */
package rebeca.routing;

import java.util.Collection;

import rebeca.*;

/**
 * @author parzy
 *
 */
public interface MatchingEngine extends BrokerEngine 
{	
	public void match(Event event, EventProcessor source);
	
	public void forward(Event event, Collection<EventProcessor> destinations);
	
	public RoutingTable getTable();
	public void setTable(RoutingTable table);
	
	public interface MatchingSink
	{
		
	}
	
	public interface MatchingComponentSink
	{
		public FilterTable getTable();
	}
}
