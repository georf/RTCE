/**
 * 
 */
package rebeca.peernet;

import java.util.Random;

import peersim.config.*;
import peersim.core.*;
import peersim.edsim.*;
import rebeca.math.*;
import rebeca.util.Graph.Edge;

/**
 * @author parzy
 *
 */
public class ExponentialQueueingLink extends QueueingLink
{
	// parameters and constants -----------------------------------------------
	// ------------------------------------------------------------------------
	
	private static final String PAR_LAMBDA = "lambda";
	private static final String PAR_SEED = "seed";
	
	private static final double DEFAULT_LAMBDA = 1.0;
	private static final long DEFAULT_SEED = 7452771261130665586L;

	
	// fields -----------------------------------------------------------------
	// ------------------------------------------------------------------------
	
	protected ExponentialDistribution distribution;
	
	
	// instantiation ----------------------------------------------------------
	// ------------------------------------------------------------------------
	
	public ExponentialQueueingLink()
	{
		this(DEFAULT_LAMBDA);
	}
	
	public ExponentialQueueingLink(double lambda)
	{
		this.distribution = new ExponentialDistribution(lambda);
	}
	
	public ExponentialQueueingLink(double lambda, long seed)
	{
		this.distribution = new ExponentialDistribution(lambda, seed);
	}
	
	public ExponentialQueueingLink(String prefix)
	{
		super(prefix);

		double lambda = Configuration.getDouble(prefix + "." + PAR_LAMBDA, 
				DEFAULT_LAMBDA);
		long seed = Configuration.getLong(prefix + "." + PAR_SEED, 
				CommonState.r.getLastSeed()^DEFAULT_SEED);
		this.distribution = new ExponentialDistribution(lambda,seed);
	}
	
	@Override
	public void init(SimSink src, SimSink dst, Edge e)
	{
		super.init(src,dst,e);
		
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


	// transport logic --------------------------------------------------------
	// ------------------------------------------------------------------------
	
	@Override
	public void schedule(SimMessage msg) 
	{
		long delay = (long)distribution.getValue();
		EDSimulator.add(delay, msg, msg.getDestination(), pid);
	}		
}
