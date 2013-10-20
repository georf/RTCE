/**
 * 
 */
package rebeca.peernet;

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
public class UniformLink extends SimLink 
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
	private static final long DEFAULT_SEED = 3644626328580714070L;

	
	// fields -----------------------------------------------------------------
	// ------------------------------------------------------------------------
	
	protected UniformDistribution distribution;
	protected boolean fifo;
	protected long srcTime;
	protected long dstTime;
	
	
	// instantiation ----------------------------------------------------------
	// ------------------------------------------------------------------------
	
	public UniformLink()
	{
		this(DEFAULT_MIN, DEFAULT_MAX, DEFAULT_FIFO);
	}
	
	public UniformLink(double min, double max)
	{
		this(min, max, DEFAULT_FIFO);
	}
	
	public UniformLink(boolean fifo)
	{
		this(DEFAULT_MIN, DEFAULT_MAX,fifo);
	}
	
	public UniformLink(double min, double max, boolean fifo)
	{
		this.fifo = fifo;
		this.srcTime = this.dstTime = -1;
		this.distribution = new UniformDistribution(min,max);
	}
	
	public UniformLink(double min, double max, boolean fifo, long seed)
	{
		this.fifo = fifo;
		this.srcTime = this.dstTime = -1;
		this.distribution = new UniformDistribution(min,max,seed);
	}
	
	public UniformLink(String prefix)
	{
		super(prefix);
		
		this.fifo = Configuration.getBoolean(prefix + "." + PAR_FIFO, 
				DEFAULT_FIFO);
		this.srcTime = this.dstTime = -1;
		double min = Configuration.getDouble(prefix + "." + PAR_MIN, 
				DEFAULT_MIN);
		double max = Configuration.getDouble(prefix + "." + PAR_MAX, 
				DEFAULT_MAX);
		long seed = Configuration.getLong(prefix + "." + PAR_SEED, 
				CommonState.r.getLastSeed()^DEFAULT_SEED);
		distribution = new UniformDistribution(min,max,seed);
	}
	
	@Override
	public void init(SimSink src, SimSink dst, Edge e)
	{
		super.init(src,dst,e);
		
		Object fifo = e.get(PAR_FIFO);
		if (fifo != null) this.fifo = (Boolean)fifo;
		Object min = e.get(PAR_MIN);
		Object max = e.get(PAR_MAX);
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
