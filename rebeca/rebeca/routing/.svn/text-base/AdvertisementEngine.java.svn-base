/*
 * $id$
 */
package rebeca.routing;

import java.util.Collection;

import rebeca.BrokerEngine;
import rebeca.Event;
import rebeca.EventProcessor;
import rebeca.event.Advertisement;
import rebeca.event.Unadvertisement;
import rebeca.routing.RoutingTable.*;

/**
 * @author parzy
 *
 */
public interface AdvertisementEngine extends BrokerEngine
{
	public RoutingEngine getEngine();
	public void setEngine(RoutingEngine engine);
	public RoutingTable getTable();
	
	public int getTableSize();
	public int getTableSize(EntrySelector selector);
	
	
	public void advertise(Advertisement advertisement, EventProcessor source);
	
	public void unadvertise(Unadvertisement unadvertisement, EventProcessor source);

	public void forward(Event event, Collection<EventProcessor> destinations);

	public interface AdvertisementSink
	{
		
	}
	
	public interface AdvertisementComponentSink
	{
		public FilterTable getTable();
	}
}
