/*
 * $Id$
 */
package rebeca.routing;

import rebeca.Broker;

/**
 * @author parzy
 *
 */
public class SimpleAdvertising extends BasicAdvertisementEngine 
{
	// Constructors -----------------------------------------------------------
	// ------------------------------------------------------------------------

	public SimpleAdvertising()
	{
		this(null,(RoutingTable)null);
	}
	
	public SimpleAdvertising(Broker broker)
	{
		this(broker,(RoutingTable)null);
	}

	public SimpleAdvertising(Broker broker, RoutingEngine routing)
	{
		this(broker,routing != null ? routing.getTable() : null);
	}
	
	public SimpleAdvertising(Broker broker, RoutingTable subscriptions)
	{
		super(broker,subscriptions,new SimpleRouting(broker));
	}
	
	/**
	 * PeerSim convenience constructor ignores arguments and calls the default 
	 * constructor.
	 * @param prefix ignored
	 */
	public SimpleAdvertising(String prefix)
	{
		this();
	}

}
