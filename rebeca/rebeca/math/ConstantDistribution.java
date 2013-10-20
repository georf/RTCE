package rebeca.math;

import java.util.Random;

import peersim.config.*;
import rebeca.util.*;

public class ConstantDistribution implements Distribution 
{
	private static final String PAR_CONSTANT = "constant";
	private static final double DEFAULT_CONSTANT = 1.0d;
	
	private double c;

	// instantiation ----------------------------------------------------------
	// -----------------------------------------------------------------------
	
	public ConstantDistribution()
	{
		this(DEFAULT_CONSTANT);
	}
	
	public ConstantDistribution(double c)
	{
		this.c = c;
	}
	
	public ConstantDistribution(String prefix)
	{
		// PeerSim convenience constructor
		c = Configuration.getDouble(prefix+"."+PAR_CONSTANT, DEFAULT_CONSTANT);
	}
	
	public ConstantDistribution(Graph.Element e)
	{
		// network topology support
		Object c = e.get(PAR_CONSTANT);
		this.c = c!=null ? (Double)c : DEFAULT_CONSTANT;
	}
	
	@Override
	public double getExpectedValue() 
	{
		return c;
	}

	@Override
	public double getValue() 
	{
		return c;
	}

	@Override
	public double getVariance() 
	{
		return 0;
	}

	@Override
	public Random getRandom() { return null; } 
	@Override
	public void setRandom(Random rand) { /* unnecessary */ }
	@Override
	public void setSeed(long seed) { /* unnecessary */ }
}
