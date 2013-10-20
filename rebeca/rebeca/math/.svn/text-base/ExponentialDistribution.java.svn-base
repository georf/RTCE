package rebeca.math;

import java.util.Random;
import peersim.config.Configuration;
import peersim.core.CommonState;
import rebeca.util.Graph;

/**
 * This class provides exponential distributed values.
 */
public class ExponentialDistribution implements Distribution {

	private static final String PAR_LAMBDA = "lambda";
	private static final String PAR_SEED = "seed";
	
	private static final double DEFAULT_LAMBDA = 1.0d;
	private static final long DEFAULT_SEED = 1619016202194491220L;
	
	private Random r;
	private double lambda;

	// constructors -----------------------------------------------------------
	// ------------------------------------------------------------------------
	
	public ExponentialDistribution()
	{
		this(DEFAULT_LAMBDA);
	}
	
	public ExponentialDistribution(double lambda)
	{
		this(lambda, new Random());
	}
	
	public ExponentialDistribution(long seed)
	{
		this(DEFAULT_LAMBDA, seed);
	}
	
	public ExponentialDistribution(Random r)
	{
		this(DEFAULT_LAMBDA,r);
	}
	
	public ExponentialDistribution(double lambda, long seed)
	{
		this(lambda,new Random(DEFAULT_SEED ^ seed));
	}
	
	public ExponentialDistribution(double lambda, Random r) 
	{
		this.lambda = lambda;
		this.r = r;
	}
	
	public ExponentialDistribution(String prefix)
	{
		// PeerSim convenience constructor
		// Read lambda from configuration.
		lambda = Configuration.getDouble(prefix+"."+PAR_LAMBDA, DEFAULT_LAMBDA);
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

	public ExponentialDistribution(Graph.Element e)
	{
		// network topology support
		Object lambda = e.get(PAR_LAMBDA);
		this.lambda = lambda!=null ? (Double)lambda : DEFAULT_LAMBDA;
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
	
//	protected long scramble(long seed)
//	{
//		return seed ^ 1619016202194491220L;
//	}

	public Random getRandom()
	{
		return r;
	}
	
	public void setRandom(Random rand)
	{
		this.r = rand;
	}

	public void setSeed(long seed)
	{
		r.setSeed(DEFAULT_SEED ^ seed);
	}

	
	// mathematics ------------------------------------------------------------
	// ------------------------------------------------------------------------

	public double getValue() 
	{
		/*
		 * r = 1 - e^{-\lamda t} => t = - ln(1-r) / \lambda
		 */
		return -Math.log(1 - r.nextDouble()) / lambda;
	}

	public double getExpectedValue() 
	{
		return 1.0 / lambda;
	}

    public double getVariance() 
    {
		return 1.0 / (lambda*lambda);
    }
    
	
	// specific getters and setters -------------------------------------------
	// ------------------------------------------------------------------------
    
    public void setLambda(double lambda)
    {
    	this.lambda = lambda;
    }
    
    public double getLambda()
    {
    	return lambda;
    }
}
