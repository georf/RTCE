/*
 * $id$
 */
package rebeca.routing;

import java.util.*;

import rebeca.*;
import rebeca.event.Subscription;
import rebeca.event.Unsubscription;
import rebeca.routing.RoutingTable.*;

/**
 * @author parzy
 *
 */
public interface RoutingEngine extends BrokerEngine 
{
	public RoutingTable getTable();

	public void setTable(RoutingTable table);
	public void setTable(MatchingEngine engine);
	
	public int getTableSize();
	public int getTableSize(EntrySelector selector);
	
	public void subscribe(Subscription subscription, EventProcessor source);
	
	public void unsubscribe(Unsubscription unsubscription, EventProcessor source);

	public void forward(Event event, Collection<EventProcessor> destinations);

	public interface RoutingSink
	{
		
	}
	
	public interface RoutingComponentSink
	{
		public FilterTable getTable();
	}
}
