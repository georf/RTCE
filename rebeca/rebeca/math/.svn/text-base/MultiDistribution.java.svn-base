/**
 * 
 */
package rebeca.math;

import java.util.*;

/**
 * @author parzy
 *
 */
public class MultiDistribution 
{
	protected Random rand;
	
	protected double[] p;
	protected double[] q;

	protected Object[] dim;
	
	protected boolean scaled;
	
	public MultiDistribution(Object[]... dim)
	{
		this(new Random(),dim);
	}
	
	public MultiDistribution(long seed, Object[]... dim)
	{
		this(new Random(seed),dim);
	}
	
	public MultiDistribution(Random rand, Object[]... dim)
	{
		this.rand = rand;
		setDimensions(dim);
	}
	
	
	public Random getRandom()
	{
		return rand;
	}
	
	public void setRandom(Random rand)
	{
		this.rand = rand;
	}
	
	public void setSeed(long seed)
	{
		rand.setSeed(seed);
	}
	
	public void setDimensions(Object[]... dim)
	{
		Object[] d = new Object[dim.length];
		int size = 1;
		for (int i = 0; i < dim.length; i++)
		{
			if ( !(dim[i] instanceof Object[]) )
			{
				throw new IllegalArgumentException();
			}
			size *= ((Object[])dim[i]).length;
			d[i] = dim[i];
		}
		this.dim = d;
		this.p = new double[size];
		this.q = new double[size];
		for (int i = 0; i < size; i++)
		{
			this.p[i] = 1.0d;
		}
		this.scaled = false;
	}
	
	public Object[] getDimension(int d)
	{
		return (Object[])dim[d];
	}
	
	public double getProbability(int... d)
	{
		return p[calcPos(d)];
	}
	
	public void setProbability(double p, int... d)
	{
		this.p[calcPos(d)] = p;
		this.scaled = false;
	}
	
	protected void scale()
	{
		double sum = 0.0d;
		for (int i = 0; i < p.length; i++) 
		{
			sum += p[i];
			q[i] = sum;
		}
		
		for (int i = 0; i < p.length; i++) 
		{
			q[i] /= sum;	
		}
		
		scaled = true;
	}
	
	protected int calcPos(int... d)
	{
		int pos = 0;
		for (int i = 0; i < dim.length; i++)
		{
			pos *= ((Object[])dim[i]).length;
			pos += d[i];
		}
		return pos;
	}

	protected int[] calcDim(int pos)
	{
		int[] d = new int[dim.length];
		for (int i = dim.length-1; i >= 0; i--)
		{
			d[i] = pos % ((Object[])dim[i]).length;
			pos = pos / ((Object[])dim[i]).length;
		}
		return d;
	}
	
	public Object[] next()
	{
		if (!scaled)
		{
			scale();
		}
		
		double r = rand.nextDouble();
		int pos = findPos(r);
		int[] d = calcDim(pos);
		
		Object[] v = new Object[d.length];
		for (int i=0; i<v.length; i++)
		{
			v[i] = ((Object[])dim[i])[d[i]];
		}
		return v;
	}
	
	protected int findPos(double r)
	{
		int pos = Arrays.binarySearch(q, r);
		
		if (pos < 0)
		{
			pos = -pos-1;
			return pos < q.length ? pos : q.length;
		}
		else
		{
			while (q[pos++]==r);
		}
		return pos < q.length ? pos : q.length;
	}
	
	/**
	 * Returns the marginal probabilities for the specified dimension.  
	 * @param dim the dimension for which marginal probabilities are calculated.
	 * @return the marginal probabilities for the specified dimension.
	 */
	public double[] getMarginalDistribution(int dim)
	{
		double[] m = new double[getDimension(dim).length]; 
		double sum = 0.0d;                                 
		// sum up all probabilities
		for (int i=0; i<p.length; i++)
		{
			// projection to dim
			m[calcDim(i)[dim]] += p[i];
			sum += p[i];     
		}
		// normalize values
		for (int i=0; i<m.length; i++)
		{
			m[i] /= sum;
		}
		return m;
	}
}
