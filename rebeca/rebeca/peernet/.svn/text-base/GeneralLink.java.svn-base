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
import rebeca.util.Graph.*;

/**
 * @author parzy
 *
 */
public class GeneralLink extends SimLink 
{
	// parameters and constants -----------------------------------------------
	// ------------------------------------------------------------------------
	
	private static final String PAR_DISTRIBUTION = "distribution";
	private static final String PAR_FIFO = "fifo";
	private static final String PAR_SEED = "seed";

	private static final boolean DEFAULT_FIFO = false;
	private static final Distribution DEFAULT_DISTRIBUTION = new ConstantDistribution();
	private static final long DEFAULT_SEED = 3644626328580714070L;

	
	// fields -----------------------------------------------------------------
	// ------------------------------------------------------------------------
	
	protected Distribution distribution;
	protected boolean fifo;
	protected long srcTime;
	protected long dstTime;
	
	
	// instantiation ----------------------------------------------------------
	// ------------------------------------------------------------------------
	
	public GeneralLink()
	{
		this(DEFAULT_DISTRIBUTION, DEFAULT_FIFO);
	}
	
	public GeneralLink(Distribution distribution)
	{
		this(distribution, DEFAULT_FIFO);
	}
	
	public GeneralLink(boolean fifo)
	{
		this(DEFAULT_DISTRIBUTION,fifo);
	}
	
	public GeneralLink(Distribution distribution, boolean fifo)
	{
		this.distribution = distribution;
		this.fifo = fifo;
		this.srcTime = this.dstTime = -1;
	}
	
	public GeneralLink(String prefix)
	{
		super(prefix);
		
		this.fifo = Configuration.getBoolean(prefix + "." + PAR_FIFO, 
				DEFAULT_FIFO);
		this.distribution = (Distribution)Configuration.getInstance(prefix 
				+ "." + PAR_DISTRIBUTION, DEFAULT_DISTRIBUTION);
		long seed = Configuration.getLong(prefix + "." + PAR_SEED, 
				CommonState.r.getLastSeed()^DEFAULT_SEED);
		distribution.setSeed(seed);
		this.srcTime = this.dstTime = -1;
	}
	
	@Override
	public void init(SimSink src, SimSink dst, Edge e)
	{
		super.init(src,dst,e);
		
		Object fifo = e.get(PAR_FIFO);
		if (fifo != null) this.fifo = (Boolean)fifo;
		Object className = e.get(PAR_DISTRIBUTION);
		if (className != null)
		{
			try
			{
				Random r = distribution.getRandom();
				Class<?> clazz = Class.forName((String)className);
				Constructor<?> con = clazz.getConstructor(new Class[]{ Edge.class });
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

	/**
	 * @return the fifo
	 */
	public boolean isFifo() 
	{
		return fifo;
	}

	/**
	 * @param fifo the fifo to set
	 */
	public void setFifo(boolean fifo) 
	{
		this.fifo = fifo;
	}


	// transport logic --------------------------------------------------------
	// ------------------------------------------------------------------------
	
	@Override
	public void schedule(SimMessage msg) 
	{
		long delay = (long)distribution.getValue();
		
		if (fifo)
		{
			// guarantee a fifo ordering on the link 
			// by extending the delay if necessary 
			long arrival = CommonState.getTime()+delay;
			if (getDestinationNode()==msg.getDestination())
			{
				// ensure that arrival is scheduled at least 
				// one tick after the  last one
				if (arrival <= dstTime)
				{
					arrival = dstTime+1;
					delay = arrival-CommonState.getTime();
				}
				dstTime = arrival;
			}
			else
			{
				// ensure that arrival is scheduled at least 
				// one tick after the  last one
				if (arrival <= srcTime)
				{
					arrival = srcTime+1;
					delay = arrival-CommonState.getTime();
				}
				srcTime = arrival;
			}
		}
		
		EDSimulator.add(delay, msg, msg.getDestination(), pid);
	}		
}
