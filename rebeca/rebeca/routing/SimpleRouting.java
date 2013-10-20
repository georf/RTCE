/*
 * $Id$
 */
package rebeca.routing;

import java.util.Collection;

import org.apache.log4j.Logger;

import rebeca.*;
import rebeca.event.Subscription;
import rebeca.event.Unsubscription;

/**
 * @author parzy
 *
 */
public class SimpleRouting extends BasicRoutingEngine 
{
	
	/** 
	 * Logger for ProcessingEngines. 
	 */
	private static final Logger LOG = Logger.getLogger(KEY);

	// Constructors -----------------------------------------------------------
	// ------------------------------------------------------------------------

	public SimpleRouting()
	{
		this(null,new SimpleRoutingTable());
	}
	
	public SimpleRouting(Broker broker)
	{
		this(broker, new SimpleRoutingTable());
	}
	
	public SimpleRouting(Broker broker, MatchingEngine matching)
	{
		this(broker,matching!=null ? new SimpleRoutingTable(matching.getTable()) : null);
	}
	
	protected SimpleRouting(Broker broker, RoutingTable table)
	{
		super(broker,table);
	}
	
	/**
	 * PeerSim convenience constructor ignores arguments and calls the default 
	 * constructor.
	 * @param prefix ignored
	 */
	public SimpleRouting(String prefix)
	{
		this();
	}

	
	// Getters and Setters ----------------------------------------------------
	// ------------------------------------------------------------------------

	@Override
	public void setTable(MatchingEngine engine)
	{
		setTable(new SimpleRoutingTable(engine.getTable()));
	}

	
	
	
	@Override
	public void subscribe(Subscription subscription, EventProcessor source)
	{
		// System.out.println("Subscription");
		// add filter to routing table
		if (! table.add(subscription.getFilter(), source))
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("subscription '" + subscription + "' from '" + source 
						+ "' is already covered");
			}
			return;
		}
		if (LOG.isDebugEnabled())
		{
			LOG.debug("subscription '" + subscription + "' from '" + source 
					+ "' added to subscription table");
		}

		// forward subscription
		Collection<EventProcessor> destinations = getDestinations();
//		System.out.println("!!!!!!!!!!");
//		for (EventProcessor p : destinations)
//		{
//			System.out.println("Destination: "+ p);
//		}
		destinations.remove(source);
		forward(subscription,destinations);
	}
	
	@Override
	public void unsubscribe(Unsubscription unsubscription, EventProcessor source)
	{
		// remove filter from routing table when contained
		if (! table.remove(unsubscription.getFilter(), source))
		{
			return;
		}
		
		// forward unsubscription
		Collection<EventProcessor> destinations = getDestinations();
		destinations.remove(source);
		forward(unsubscription,destinations);
	}
}
