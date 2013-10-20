/*
 * $id$
 */
package rebeca.peersim;

import peersim.config.*;
import peersim.core.*;

import rebeca.component.*;
//import rebeca.scope.*;

/**
 * @author parzy
 *
 */
public class EventComponentizing extends ProtocolEngine
implements ComponentEngine
{
	// parameters ---------------------------------------------------------
	//---------------------------------------------------------------------

	/** 
	 * String name of the parameter used to configure the applied routing
	 * engine for advertisements.
	 * @config
	 */	
	private static final String PAR_ENGINE = "engine";

	
	// Initialization ---------------------------------------------------------
	// ------------------------------------------------------------------------

	/**
	 * Reads configuration parameter.
	 */
	public EventComponentizing(String name)
	{
		// Determine own node and protocol identifier.
		prefix = name;
		node = CommonState.getNode();
		pid = CommonState.getPid();
		
		// Create an instance of the given advertisement engine
		engine = (ComponentEngine) Configuration.getInstance(prefix + "."
				+ PAR_ENGINE, new BasicComponentEngine());
	}
	
	// EDProtocol implementation ----------------------------------------------
	// ------------------------------------------------------------------------
	@Override
	public Object clone()
	{
		EventComponentizing clone = (EventComponentizing) super.clone();
		// TODO parzy maybe support cloning for real BrokerEngines
		clone.node = CommonState.getNode();
		clone.engine = (ComponentEngine) Configuration.getInstance(prefix + "."
				+ PAR_ENGINE, new BasicComponentEngine());
		return clone;
	}
}
