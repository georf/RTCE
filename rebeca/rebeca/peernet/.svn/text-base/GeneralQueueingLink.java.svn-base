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
public class GeneralQueueingLink extends QueueingLink
{
	// parameters and constants -----------------------------------------------
	// ------------------------------------------------------------------------
	
	private static final String PAR_DISTRIBUTION = "distribution";
	private static final String PAR_SEED = "seed";
	
	private static final Distribution DEFAULT_DISTRIBUTION = new ConstantDistribution();
	private static final long DEFAULT_SEED = 3644626328580714070L;

	
	// fields -----------------------------------------------------------------
	// ------------------------------------------------------------------------
	
	protected Distribution distribution;
	
	
	// instantiation ----------------------------------------------------------
	// ------------------------------------------------------------------------
	
	public GeneralQueueingLink()
	{
		this(DEFAULT_DISTRIBUTION);
	}
	
	public GeneralQueueingLink(Distribution distribution)
	{
		this.distribution = distribution;
	}
	
	public GeneralQueueingLink(String prefix)
	{
		this.pid = CommonState.getPid();
		this.distribution = (Distribution)Configuration.getInstance(prefix 
				+ "." + PAR_DISTRIBUTION, DEFAULT_DISTRIBUTION);
		long seed = Configuration.getLong(prefix + "." + PAR_SEED, 
				CommonState.r.getLastSeed()^DEFAULT_SEED);
		distribution.setSeed(seed);
	}
	
	@Override
	public void init(SimSink src, SimSink dst, Graph.Edge e)
	{
		super.init(src,dst,e);
		
		Object className = e.get(PAR_DISTRIBUTION);
		if (className != null)
		{
			try
			{
				Random r = distribution.getRandom();
				Class<?> clazz = Class.forName((String)className);
				Constructor<?> con = clazz.getConstructor(new Class[]{ Graph.Edge.class });
				distribution = (Distribution) con.newInstance(e);
				distribution.setRandom(r);
			}
			catch (Exception x)
			{
				log.error("link initialization error",x);
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
	public void schedule(SimMessage msg) 
	{
		long delay = (long)distribution.getValue();
		EDSimulator.add(delay, msg, msg.getDestination(), pid);
	}		
}
