/**
 * 
 */
package rebeca.peernet;

import java.lang.reflect.Constructor;
import java.util.Random;

import peersim.config.*;
import peersim.core.*;
import peersim.edsim.*;

import rebeca.math.*;
import rebeca.util.*;


/**
 * @author parzy
 *
 */
public class GeneralQueueingRouter extends SimRouter 
{
	// parameters and constants -----------------------------------------------
	// ------------------------------------------------------------------------
	private static final String PAR_DISTRIBUTION = "distribution";
	private static final String PAR_SEED = "seed";
	
	private static final Distribution DEFAULT_DISTRIBUTION = new ConstantDistribution();
	private static final long DEFAULT_SEED = 5132870949927665712L;
	
	// fields -----------------------------------------------------------------
	// ------------------------------------------------------------------------
	
	protected Distribution distribution;

	
	// instantiation ----------------------------------------------------------
	// ------------------------------------------------------------------------
	
	public GeneralQueueingRouter()
	{
		this(DEFAULT_DISTRIBUTION);
	}
	
	public GeneralQueueingRouter(Distribution distribution)
	{
		this.distribution = distribution;
	}

	public GeneralQueueingRouter(String prefix)
	{
		super(prefix);

		this.distribution = (Distribution)Configuration.getInstance(prefix 
				+ "." + PAR_DISTRIBUTION, DEFAULT_DISTRIBUTION);
		long seed = Configuration.getLong(prefix + "." + PAR_SEED, 
				CommonState.r.getLastSeed()^DEFAULT_SEED);
		distribution.setSeed(seed);
	}
	
	@Override
	public void init(Graph.Vertex v)
	{
		super.init(v);
		
		Object className = v.get(PAR_DISTRIBUTION);
		if (className != null)
		{
			try
			{
				Random r = distribution.getRandom();
				Class<?> clazz = Class.forName((String)className);
				Constructor<?> con = clazz.getConstructor(new Class[]{ Graph.Vertex.class });
				distribution = (Distribution) con.newInstance(v);
				distribution.setRandom(r);
			}
			catch (Exception x)
			{
				log.error("router initialization error",x);
			}
		}
	}
	
	
	// getters and setters ----------------------------------------------------
	// ------------------------------------------------------------------------
	
	/**
	 * @return the distribution
	 */
	public Distribution getDistribution() 
	{
		return distribution;
	}

	/**
	 * @param distribution the distribution to set
	 */
	public void setDistribution(Distribution distribution) 
	{
		this.distribution = distribution;
	}

	
	// transport logic --------------------------------------------------------
	// ------------------------------------------------------------------------

	@Override
	public void schedule(RoutingEvent e)
	{
		long delay = (long)distribution.getValue();
		EDSimulator.add(delay, e, e.getDestinationNode(), pid);
	}	
}
