/**
 * 
 */
package rebeca.math;

import java.util.*;

import peersim.config.*;
import peersim.core.*;
import rebeca.util.Graph;

/**
 * @author parzy
 *
 */
public class UniformDistribution implements Distribution 
{
	private static final String PAR_MIN = "min";
	private static final String PAR_MAX = "max";
	private static final String PAR_SEED = "seed";
	
	private static final double DEFAULT_MIN = 0.0d;
	private static final double DEFAULT_MAX = 1.0d;
	private static final long DEFAULT_SEED = 20090906112009L;
	
	private double min;
	private double max;
	private Random r;
	
	// instantiation ----------------------------------------------------------
	// ------------------------------------------------------------------------
	
	public UniformDistribution()
	{
		this(DEFAULT_MIN,DEFAULT_MAX);
	}
	
	public UniformDistribution(double min, double max)
	{
		this(min,max,System.currentTimeMillis());
	}
	
	public UniformDistribution(double min, double max, long seed)
	{
		this(min,max,new Random(DEFAULT_SEED ^ seed));
	}
	
	public UniformDistribution(double min, double max, Random r)
	{
		// check bounds
		if (min > max)
		{
			throw new IllegalArgumentException("illegal bounds");
		}
		// set fields
		this.min = min;
		this.max = max;
		this.r = r;
	}
	
	public UniformDistribution(String prefix)
	{
		// get lower and upper bounds
		min = Configuration.getDouble(prefix + "." + PAR_MIN, DEFAULT_MIN);
		max = Configuration.getDouble(prefix + "." + PAR_MAX, DEFAULT_MAX);
		// get random seed if provided 
		if (Configuration.contains(prefix + "." + PAR_SEED))
		{
			this.r = new Random( DEFAULT_SEED ^ Configuration.getLong(prefix 
					+ "." + PAR_SEED));
		}
		// or use simulation's random generator instead
		else
		{
			this.r = CommonState.r;
		}
	}
	
	public UniformDistribution(Graph.Element e)
	{
		Object min = e.get("PAR_MIN");
		this.min = min!=null ? (Double)min : DEFAULT_MIN;
		Object max = e.get("PAR_MIN");
		this.max = max!=null ? (Double)max : DEFAULT_MAX;
		Object seed = e.get("PAR_SEED");
		if (seed!=null)
		{
			this.r = new Random(DEFAULT_SEED ^ (Long)seed);
		}
		else
		{
			this.r = CommonState.r;
		}
	}
	

	// management of randomness -----------------------------------------------
	// ------------------------------------------------------------------------
	
	public Random getRandom()
	{
		return r;
	}
	
	@Override
	public void setRandom(Random rand) 
	{
		this.r = rand;
	}
	
	@Override
	public void setSeed(long seed)
	{
		r.setSeed(DEFAULT_SEED ^ seed);
	}
	
	
	// mathematics ------------------------------------------------------------
	// ------------------------------------------------------------------------
	
	@Override
	public double getValue() 
	{
		return min+(max-min)*r.nextDouble();
	}
		
	@Override
	public double getExpectedValue() 
	{
		return (max-min)/(2.0d);
	}

	@Override
	public double getVariance() 
	{
		return (max-min)*(max-min)/12.0d;
	}

	
	// specific getters and setters -------------------------------------------
	// ------------------------------------------------------------------------
	
	/**
	 * @return the min
	 */
	public double getMin() 
	{
		return min;
	}

	/**
	 * @param min the min to set
	 */
	public void setMin(double min) 
	{
		this.min = min;
	}

	/**
	 * @return the max
	 */
	public double getMax() 
	{
		return max;
	}

	/**
	 * @param max the max to set
	 */
	public void setMax(double max) 
	{
		this.max = max;
	}
}
