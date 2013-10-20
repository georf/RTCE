/**
 * 
 */
package rebeca.math;

import java.util.*;

import peersim.core.*;

import rebeca.peersim.ApplicationProtocol;
import rebeca.peersim.ApplicationProtocol.*;

/**
 * Creates a multidimensional distribution based on the outer product of the
 * marginal probabilities of profiles and nodes.
 * @author parzy
 */
public class OuterAllocation extends Allocation 
{

	// constructors -----------------------------------------------------------
	// ------------------------------------------------------------------------
	
	public OuterAllocation() 
	{
		super();
	}

	public OuterAllocation(long seed) 
	{
		super(seed);
	}

	public OuterAllocation(Random r) 
	{
		super(r);
	}

	public OuterAllocation(String prefix) 
	{
		super(prefix);
	}

	// allocation logic -------------------------------------------------------
	// ------------------------------------------------------------------------

	@Override
	public MultiDistribution multiDistribution(Profile[] profiles, 
			double[] selections, Node[] nodes, double[] placements) 
	{
		MultiDistribution d = new MultiDistribution(profiles,nodes);
		for (int i=0; i<selections.length; i++)
		{
			for (int j=0; j<placements.length; j++)
			{
				d.setProbability(selections[i]*placements[j], i,j);
			}
		}
		return d;
	}
}
