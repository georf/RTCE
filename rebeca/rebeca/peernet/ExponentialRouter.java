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
public class ExponentialRouter extends SimRouter 
{
	// parameters and constants -----------------------------------------------
	// ------------------------------------------------------------------------
	private static final String PAR_LAMBDA = "lambda";
	private static final String PAR_FIFO = "fifo";
	private static final String PAR_SEED = "seed";
	
	private static final boolean DEFAULT_FIFO = false;
	private static final double DEFAULT_LAMBDA = 1.0;	
	private static final long DEFAULT_SEED = 636802320652277248L;
	
	// fields -----------------------------------------------------------------
	// ------------------------------------------------------------------------
	
	protected ExponentialDistribution distribution;
	protected boolean fifo;
	protected long time;
	
	// instantiation ----------------------------------------------------------
	// ------------------------------------------------------------------------
	
	public ExponentialRouter()
	{
		this(DEFAULT_LAMBDA, DEFAULT_FIFO);
	}
	
	public ExponentialRouter(double lambda)
	{
		this(lambda, DEFAULT_FIFO);
	}
	
	public ExponentialRouter(boolean fifo)
	{
		this(DEFAULT_LAMBDA,fifo);
	}
	
	public ExponentialRouter(double lambda, boolean fifo)
	{
		this.fifo = fifo;
		this.time = -1;
		this.distribution = new ExponentialDistribution(lambda);
	}

	public ExponentialRouter(double lambda, boolean fifo, long seed)
	{
		this.fifo = fifo;
		this.time = -1;
		this.distribution = new ExponentialDistribution(lambda,seed);
	}

	public ExponentialRouter(String prefix)
	{
		super(prefix);

		this.fifo = Configuration.getBoolean(prefix + "." + PAR_FIFO, 
				DEFAULT_FIFO);
		this.time = -1;
		double lambda = Configuration.getDouble(prefix + "." + PAR_LAMBDA, 
				DEFAULT_LAMBDA);
		long seed = Configuration.getLong(prefix + "." + PAR_SEED, 
				CommonState.r.getLastSeed()^DEFAULT_SEED);
		distribution = new ExponentialDistribution(lambda,seed);
	}
	
	@Override
	public void init(Graph.Vertex v)
	{
		super.init(v);
		
		Object fifo = v.get(PAR_FIFO);
		if (fifo != null) this.fifo = (Boolean)fifo;
		Object lambda = v.get(PAR_LAMBDA);
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
