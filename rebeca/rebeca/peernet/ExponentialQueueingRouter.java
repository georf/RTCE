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
public class ExponentialQueueingRouter extends SimRouter 
{
	// parameters and constants -----------------------------------------------
	// ------------------------------------------------------------------------
	private static final String PAR_LAMBDA = "lambda";
	private static final String PAR_SEED = "seed";
	
	private static final double DEFAULT_LAMBDA = 1.0;	
	private static final long DEFAULT_SEED = 7210075715874663918L;
	
	// fields -----------------------------------------------------------------
	// ------------------------------------------------------------------------
	
	protected ExponentialDistribution distribution;
	
	
	// instantiation ----------------------------------------------------------
	// ------------------------------------------------------------------------
	
	public ExponentialQueueingRouter()
	{
		this(DEFAULT_LAMBDA);
	}
	
	public ExponentialQueueingRouter(double lambda)
	{
		this.distribution = new ExponentialDistribution(lambda);
	}

	public ExponentialQueueingRouter(double lambda, long seed)
	{
		this.distribution = new ExponentialDistribution(lambda,seed);
	}

	public ExponentialQueueingRouter(String prefix)
	{
		super(prefix);

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
	
	
	// transport logic --------------------------------------------------------
	// ------------------------------------------------------------------------

	@Override
	public void schedule(RoutingEvent e)
	{
		long delay = (long)distribution.getValue();
		EDSimulator.add(delay, e, e.getDestinationNode(), pid);
	}	
}
