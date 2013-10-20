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
public class UniformQueueingRouter extends SimRouter 
{
	// parameters and constants -----------------------------------------------
	// ------------------------------------------------------------------------
	private static final String PAR_MIN = "min";
	private static final String PAR_MAX = "max";
	private static final String PAR_SEED = "seed";
	
	private static final double DEFAULT_MIN = 0.0d;
	private static final double DEFAULT_MAX = 1.0d;
	private static final long DEFAULT_SEED = -8782524602988391651L;
	
	
	// fields -----------------------------------------------------------------
	// ------------------------------------------------------------------------
	
	protected UniformDistribution distribution;

	
	// instantiation ----------------------------------------------------------
	// ------------------------------------------------------------------------
	
	public UniformQueueingRouter()
	{
		this(DEFAULT_MIN, DEFAULT_MAX);
	}
	
	public UniformQueueingRouter(double min, double max)
	{
		this.distribution = new UniformDistribution(min,max);
	}

	public UniformQueueingRouter(double min, double max, long seed)
	{
		this.distribution = new UniformDistribution(min, max, seed);
	}

	public UniformQueueingRouter(String prefix)
	{
		super(prefix);

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

	
	// transport logic --------------------------------------------------------
	// ------------------------------------------------------------------------

	@Override
	public void schedule(RoutingEvent e)
	{
		long delay = (long)distribution.getValue();
		EDSimulator.add(delay, e, e.getDestinationNode(), pid);
	}	
}
