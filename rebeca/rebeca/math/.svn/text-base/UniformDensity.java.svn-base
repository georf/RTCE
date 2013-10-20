/**
 * 
 */
package rebeca.math;

import java.util.Random;

import rebeca.peersim.ApplicationProtocol;
import rebeca.peersim.ApplicationProtocol.ProbabilityDensity;

/**
 * @author parzy
 *
 */
public class UniformDensity extends ProbabilityDensity 
{
	// constructors -----------------------------------------------------------
	// ------------------------------------------------------------------------
	
	public UniformDensity() 
	{
		super();
	}

	public UniformDensity(long seed) 
	{
		super(seed);
	}

	public UniformDensity(Random r) 
	{
		super(r);
	}

	public UniformDensity(String prefix) 
	{
		super(prefix);
	}

	
	// mathematics ------------------------------------------------------------
	// ------------------------------------------------------------------------
	
	@Override
	public void probabilities(double[] probabilities) 
	{
		for (int i=0; i<probabilities.length; i++)
		{
			probabilities[i] = 1.0d / (double)probabilities.length;
		}
	}
}
