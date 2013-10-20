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
public class EventAdvertising extends ProtocolEngine 
implements AdvertisementEngine
{
	// parameters -------------------------------------------------------------
	//-------------------------------------------------------------------------

	/** 
	 * String name of the parameter used to configure the applied routing
	 * engine for advertisements.
	 * @config
	 */	
	private static final String PAR_ENGINE = "engine";
	
	
	// initialization ---------------------------------------------------------
	// ------------------------------------------------------------------------

	/**
	 * Reads configuration parameter.
	 */
	public EventAdvertising(String name)
	{
		prefix = name;
		
		// Determine own protocol identifier.
		pid = CommonState.getPid();

		// Determine own node
		node = CommonState.getNode();
		
		// Create an instance of the given advertisement engine
		engine = (AdvertisementEngine) Configuration.getInstance(prefix + "."
				+ PAR_ENGINE, new SimpleAdvertising());
	}
	

	// EDProtocol implementation ----------------------------------------------
	// ------------------------------------------------------------------------
	@Override
	public Object clone()
	{
		EventAdvertising clone = (EventAdvertising) super.clone();
		clone.node = CommonState.getNode();
		// TODO parzy maybe support cloning for AdvertisementEngines
		clone.engine = (AdvertisementEngine) Configuration.getInstance(prefix
				+ "." + PAR_ENGINE, new SimpleAdvertising());
		return clone;
	}
	
	
	// AdvertisementEngine implementation -------------------------------------
	// ------------------------------------------------------------------------
	
	@Override
	public RoutingEngine getEngine()
	{
		return ((AdvertisementEngine)engine).getEngine();
	}
	
	@Override
	public void setEngine(RoutingEngine engine)
	{
		((AdvertisementEngine)engine).setEngine(engine);
	}
	
	@Override
	public RoutingTable getTable()
	{
		return ((AdvertisementEngine)engine).getTable();
	}

	@Override
	public int getTableSize()
	{
		return ((AdvertisementEngine)engine).getTableSize();
	}
	
	@Override
	public int getTableSize(EntrySelector selector)
	{
		return ((AdvertisementEngine)engine).getTableSize(selector);	
	}
	
	@Override
	public void advertise(Advertisement advertisement, EventProcessor source)
	{
		((AdvertisementEngine)engine).advertise(advertisement, source);
	}

	@Override
	public void unadvertise(Unadvertisement unadvertisement, EventProcessor source)
	{
		((AdvertisementEngine)engine).unadvertise(unadvertisement, source);
	}

	@Override
	public void forward(Event event, Collection<EventProcessor> destinations)
	{
		((AdvertisementEngine)engine).forward(event, destinations);
	}
}
