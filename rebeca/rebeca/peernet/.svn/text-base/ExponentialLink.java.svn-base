/**
 * 
 */
package rebeca.peernet;

import java.util.*;

import peersim.config.*;
import peersim.core.*;
import peersim.edsim.*;

import rebeca.math.*;
import rebeca.util.Graph.Edge;

/**
 * @author parzy
 *
 */
public class ExponentialLink extends SimLink 
{
	// parameters and constants -----------------------------------------------
	// ------------------------------------------------------------------------
	
	private static final String PAR_LAMBDA = "lambda";
	private static final String PAR_FIFO = "fifo";
	private static final String PAR_SEED = "seed";

	private static final boolean DEFAULT_FIFO = false;
	private static final double DEFAULT_LAMBDA = 1.0;
	private static final long DEFAULT_SEED = 3644626328580714070L;

	
	// fields -----------------------------------------------------------------
	// ------------------------------------------------------------------------
	
	protected ExponentialDistribution distribution;
	protected boolean fifo;
	protected long srcTime;
	protected long dstTime;
	
	
	// instantiation ----------------------------------------------------------
	// ------------------------------------------------------------------------
	
	public ExponentialLink()
	{
		this(DEFAULT_LAMBDA, DEFAULT_FIFO);
	}
	
	public ExponentialLink(double lambda)
	{
		this(lambda, DEFAULT_FIFO);
	}
	
	public ExponentialLink(boolean fifo)
	{
		this(DEFAULT_LAMBDA,fifo);
	}
	
	public ExponentialLink(double lambda, boolean fifo)
	{
		this.fifo = fifo;
		this.srcTime = this.dstTime = -1;
		this.distribution = new ExponentialDistribution(lambda);
	}
	
	public ExponentialLink(double lambda, boolean fifo, long seed)
	{
		this.fifo = fifo;
		this.srcTime = this.dstTime = -1;
		this.distribution = new ExponentialDistribution(lambda,seed);
	}
	
	public ExponentialLink(String prefix)
	{
		this.pid = CommonState.getPid();
		this.fifo = Configuration.getBoolean(prefix + "." + PAR_FIFO, 
				DEFAULT_FIFO);
		this.srcTime = this.dstTime = -1;
		double lambda = Configuration.getDouble(prefix + "." + PAR_LAMBDA, 
				DEFAULT_LAMBDA);
		long seed = Configuration.getLong(prefix + "." + PAR_SEED, 
				CommonState.r.getLastSeed()^DEFAULT_SEED);
		distribution = new ExponentialDistribution(lambda,seed);
	}
	
	@Override
	public void init(SimSink src, SimSink dst, Edge e)
	{
		super.init(src,dst,e);
		
		Object fifo = e.get(PAR_FIFO);
		if (fifo != null) this.fifo = (Boolean)fifo;
		Object lambda = e.get(PAR_LAMBDA);
		if (lambda != null)
		{
			Random r = distribution.getRandom();
			distribution = new ExponentialDistribution((Double)lambda,r);
		}
	}
	
	// getters and setters ----------------------------------------------------
	// ------------------------------------------------------------------------
	
	public double getLambda()
	{
		return distribution.getLambda();
	}
	
	public void setLambda(double lambda)
	{
		distribution.setLambda(lambda);
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
