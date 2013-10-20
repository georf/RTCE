/**
 * 
 */
package rebeca.peernet;

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
public class UniformRouter extends SimRouter 
{
	// parameters and constants -----------------------------------------------
	// ------------------------------------------------------------------------
	private static final String PAR_MIN = "min";
	private static final String PAR_MAX = "max";
	private static final String PAR_FIFO = "fifo";
	private static final String PAR_SEED = "seed";
	
	private static final boolean DEFAULT_FIFO = false;
	private static final double DEFAULT_MIN = 0.0d;
	private static final double DEFAULT_MAX = 1.0d;
	private static final long DEFAULT_SEED = 7582196854212475124L;
	
	// fields -----------------------------------------------------------------
	// ------------------------------------------------------------------------
	
	protected UniformDistribution distribution;
	protected boolean fifo;
	protected long time;
	
	// instantiation ----------------------------------------------------------
	// ------------------------------------------------------------------------
	
	public UniformRouter()
	{
		this(DEFAULT_MIN, DEFAULT_MAX, DEFAULT_FIFO);
	}
	
	public UniformRouter(double min, double max)
	{
		this(min, max, DEFAULT_FIFO);
	}
	
	public UniformRouter(boolean fifo)
	{
		this(DEFAULT_MIN, DEFAULT_MAX, fifo);
	}
	
	public UniformRouter(double min, double max, boolean fifo)
	{
		this.fifo = fifo;
		this.time = -1;
		this.distribution = new UniformDistribution(min,max);
	}

	public UniformRouter(double min, double max, boolean fifo, long seed)
	{
		this.fifo = fifo;
		this.time = -1;
		this.distribution = new UniformDistribution(min, max, seed);
	}

	public UniformRouter(String prefix)
	{
		super(prefix);

		this.fifo = Configuration.getBoolean(prefix + "." + PAR_FIFO, 
				DEFAULT_FIFO);
		this.time = -1;
		double min = Configuration.getDouble(prefix + "." + PAR_MIN, 
				DEFAULT_MIN);
		double max = Configuration.getDouble(prefix + "." + PAR_MAX, 
				DEFAULT_MAX);
		long seed = Configuration.getLong(prefix + "." + PAR_SEED, 
				CommonState.r.getLastSeed()^DEFAULT_SEED);
		distribution = new UniformDistribution(min,max,seed);
	}
	
	@Override
	public void init(Graph.Vertex v)
	{
		super.init(v);
		
		Object fifo = v.get(PAR_FIFO);
		if (fifo != null) this.fifo = (Boolean)fifo;
		Object min = v.get(PAR_MIN);
		Object max = v.get(PAR_MAX);
		if (min != null || max != null)
		{
			Random r = distribution.getRandom();
			double low = min!=null ? (Double)min :((UniformDistribution)distribution).getMin();
			double high = max!=null ? (Double)max :((UniformDistribution)distribution).getMax();
			distribution = new UniformDistribution(low,high,r);
		}
	}
	
	
	// getters and setters ----------------------------------------------------
	// ------------------------------------------------------------------------
	
	public double getMin()
	{
		return distribution.getMin();
	}
	
	public void setMin(double min)
	{
		distribution.setMin(min);
	}

	public double getMax()
	{
		return distribution.getMax();
	}
	
	public void setMax(double min)
	{
		distribution.setMax(min);
	}

	public void setSeed(long seed)
	{
		distribution.setSeed(seed);
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
