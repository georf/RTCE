/*
 * $Id$
 */
package rebeca.peersim;

import java.util.*;

import peersim.config.*;
import peersim.core.*;

import rebeca.*;
import rebeca.event.*;
import rebeca.routing.*;
import rebeca.routing.RoutingTable.*;

/**
 * @author parzy
 *
 */
public class EventRouting extends ProtocolEngine implements RoutingEngine
{
	// Parameters ---------------------------------------------------------
	//---------------------------------------------------------------------

	/** 
	 * String name of the parameter used to configure the applied routing
	 * engine for subscriptions.
	 * @config
	 */	
	private static final String PAR_ENGINE = "engine";
	

	// Initialization -----------------------------------------------------
	// --------------------------------------------------------------------

	/**
	 * Reads configuration parameter.
	 */
	public EventRouting(String name)
	{
		// save prefix
		prefix = name;
		
		// determine own protocol identifier
		pid = CommonState.getPid();
		
		// determine own node
		node = CommonState.getNode();
		
		// create an instance of the given advertisement engine  
		engine = (RoutingEngine)Configuration.getInstance(prefix + "." + 
				PAR_ENGINE, new SimpleRouting());
	}
		
	
	// EDProtocol implementation ----------------------------------------------
	// ------------------------------------------------------------------------
	
	@Override
	public Object clone() 
	{
		EventRouting clone = (EventRouting) super.clone();
		clone.node = CommonState.getNode();
		// TODO parzy maybe support cloning for AdvertisementEngines
		clone.engine = (RoutingEngine) Configuration.getInstance(prefix + "."
				+ PAR_ENGINE, new SimpleRouting());
		return clone;
	}
	
	
	// RoutingEngine implementation -------------------------------------------
	// ------------------------------------------------------------------------
	
	@Override
	public void forward(Event event, Collection<EventProcessor> destinations)
	{
		((RoutingEngine)engine).forward(event, destinations);
	}
	
	@Override
	public RoutingTable getTable()
	{
		return ((RoutingEngine)engine).getTable();
	}
	
	@Override
	public int getTableSize()
	{
		return ((RoutingEngine)engine).getTableSize();
	}
	
	@Override
	public int getTableSize(EntrySelector selector)
	{
		return ((RoutingEngine)engine).getTableSize(selector);	
	}
		
	@Override
	public void setTable(RoutingTable table)
	{
		((RoutingEngine)engine).setTable(table);
	}
	
	@Override
	public void setTable(MatchingEngine engine)
	{
		((RoutingEngine)engine).setTable(engine);
	}
	
	@Override
	public void subscribe(Subscription subscription, EventProcessor source)
	{
		((RoutingEngine)engine).subscribe(subscription, source);
	}
	
	@Override
	public void unsubscribe(Unsubscription unsubscription, EventProcessor source)
	{
		((RoutingEngine)engine).unsubscribe(unsubscription, source);
	}
}

