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
public class GeneralRouter extends SimRouter 
{
	// parameters and constants -----------------------------------------------
	// ------------------------------------------------------------------------
	private static final String PAR_DISTRIBUTION = "distribution";
	private static final String PAR_FIFO = "fifo";
	private static final String PAR_SEED = "seed";
	
	private static final boolean DEFAULT_FIFO = false;
	private static final Distribution DEFAULT_DISTRIBUTION = new ConstantDistribution();
	private static final long DEFAULT_SEED = -5943883036760863181L;
	
	// fields -----------------------------------------------------------------
	// ------------------------------------------------------------------------
	
	protected Distribution distribution;
	protected boolean fifo;
	protected long time;
	
	// instantiation ----------------------------------------------------------
	// ------------------------------------------------------------------------
	
	public GeneralRouter()
	{
		this(DEFAULT_DISTRIBUTION, DEFAULT_FIFO);
	}
	
	public GeneralRouter(Distribution distribution)
	{
		this(distribution, DEFAULT_FIFO);
	}
	
	public GeneralRouter(boolean fifo)
	{
		this(DEFAULT_DISTRIBUTION,fifo);
	}
	
	public GeneralRouter(Distribution distribution, boolean fifo)
	{
		this.fifo = fifo;
		this.time = -1;
		this.distribution = distribution;
	}

	public GeneralRouter(String prefix)
	{
		super(prefix);

		this.fifo = Configuration.getBoolean(prefix + "." + PAR_FIFO, 
				DEFAULT_FIFO);
		this.time = -1;
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
		
		Object fifo = v.get(PAR_FIFO);
		if (fifo != null) this.fifo = (Boolean)fifo;
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
	public void schedule(RoutingEvent e)
	{
		long delay = (long)distribution.getValue();

		if (fifo)
		{
			// guarantee a fifo ordering on the link 
			// by extending the delay if necessary 
			long arrival = CommonState.getTime()+delay;
			// ensure that processing is scheduled at least 
			// one tick after the last event
			if (arrival <= time)
			{
				arrival = time+1;
				delay = arrival-CommonState.getTime();
			}
			time = arrival;
		}
		
		EDSimulator.add(delay, e, e.getDestinationNode(), pid);
	}	
}
