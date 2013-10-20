/*
 * $Id$
 */
package rebeca.peersim;

import java.util.Collection;

import peersim.config.*;
import peersim.core.*;

import rebeca.*;
import rebeca.routing.*;

/**
 * @author parzy
 *
 */
public class EventMatching extends ProtocolEngine implements MatchingEngine
{
	// parameters and constants -----------------------------------------------
	// ------------------------------------------------------------------------
	
	/** 
	 * String name of the parameter used to configure the applied routing
	 * engine for subscriptions.
	 * @config
	 */	
	private static final String PAR_ENGINE = "engine";
	
	
	// initialization -----------------------------------------------------
	// --------------------------------------------------------------------

	/**
	 * Reads configuration parameter.
	 */
	public EventMatching(String name)
	{
		// save configuration prefix
		prefix = name;
		
		// determine own protocol identifier
		pid = CommonState.getPid();

		// determine own node
		node = CommonState.getNode();
		
		// create an instance of the given advertisement engine
		engine = (MatchingEngine) Configuration.getInstance(prefix + "."
				+ PAR_ENGINE, new BasicMatchingEngine());
	}
	
	// EDProtocol implementation ----------------------------------------------
	// ------------------------------------------------------------------------

	@Override
	public Object clone() 
	{
		EventMatching clone = (EventMatching) super.clone();
		clone.node = CommonState.getNode();
		// TODO parzy maybe support cloning for AdvertisementEngines
		clone.engine = (MatchingEngine) Configuration.getInstance(prefix + "."
				+ PAR_ENGINE, new BasicMatchingEngine());
		return clone;
	}	
	
	
	// MatchingEngine implementation ------------------------------------------
	// ------------------------------------------------------------------------
	
	@Override
	public RoutingTable getTable()
	{
		return ((MatchingEngine)engine).getTable();
	}
	
	@Override
	public void setTable(RoutingTable table)
	{
		((MatchingEngine)engine).setTable(table);
	}
	
	@Override
	public void match(Event event, EventProcessor source)
	{
		((MatchingEngine)engine).match(event, source);
	}
	
	@Override
	public void forward(Event event, Collection<EventProcessor> destinations)
	{
		((MatchingEngine)engine).forward(event, destinations);
	}
}