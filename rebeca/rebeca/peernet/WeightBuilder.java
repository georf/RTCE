/**
 * 
 */
package rebeca.peernet;

import java.util.*;

import org.apache.log4j.*;

import peersim.config.*;
import peersim.core.*;

import rebeca.math.*;
import rebeca.util.*;


/**
 * @author parzy
 *
 */
public class WeightBuilder implements Control 
{
	// parameters and constants -----------------------------------------------
	// ------------------------------------------------------------------------

	private static final Logger LOG = Logger.getLogger(WeightBuilder.class);
	
	private static enum Strategy {CONSTANT, DISTRIBUTION, PROPERTY, RANDOM};
	private static enum Element {EDGE,VERTEX};
	
	public static final String PAR_NETWORK = "network";
	public static final String PAR_STRATEGY = "strategy";
	public static final String PAR_ELEMENT = "element";
	public static final String PAR_WEIGHT = "weight";
	public static final String PAR_MAX = "max";
	public static final String PAR_MIN = "min";
	public static final String PAR_SCALE = "scale";

	public static final String PAR_DISTRIBUTION = "distribution";
	public static final String PAR_PROPERTY = "property";
	public static final String PAR_SEED = "seed";
	public static final String PAR_VALUE = "value";
	
	private static final String DEFAULT_STRATEGY = Strategy.RANDOM.toString();
	private static final String DEFAULT_ELEMENT = Element.EDGE.toString();
	private static final double DEFAULT_MAX = Double.MAX_VALUE;
	private static final double DEFAULT_MIN = 0.0d;
	private static final double DEFAULT_SCALE = 1.0d;
	private static final Distribution DEFAULT_DISTRIBUTION = new ConstantDistribution();
	private static final String DEFAULT_PROPERTY = "bandwidth";
	private static final long DEFAULT_SEED = -8212177829938233586L;
	private static final double DEFAULT_VALUE = 1.0d;
	
	// fields -----------------------------------------------------------------
	// ------------------------------------------------------------------------

	private String prefix;
	private int nid;

	private Strategy strategy;
	private Element element;
	private String weight;
	
	private double max;
	private double min;
	private double scale;
	
	
	// instantiation ----------------------------------------------------------
	// ------------------------------------------------------------------------
	
	public WeightBuilder(String prefix)
	{
		this.prefix = prefix;
		this.nid = Configuration.getPid(prefix + "." + PAR_NETWORK);

		String strategy = Configuration.getString(prefix + "." 
				+ PAR_STRATEGY, DEFAULT_STRATEGY);
		for (Strategy s : Strategy.values())
		{
			if (s.toString().equalsIgnoreCase(strategy))
			{
				this.strategy = s;
				break;
			}	
		}		
		String element = Configuration.getString(prefix + "." + PAR_ELEMENT,
				DEFAULT_ELEMENT);
		for (Element e : Element.values())
		{
			if (e.toString().equalsIgnoreCase(element))
			{
				this.element = e;
				break;
			}	
		}
		
		this.weight = Configuration.getString(prefix + "." + PAR_WEIGHT, 
				PAR_WEIGHT);
		this.max = Configuration.getDouble(prefix + "." + PAR_MAX, 
				DEFAULT_MAX);
		this.min = Configuration.getDouble(prefix + "." + PAR_MIN, 
				DEFAULT_MIN);
		this.scale = Configuration.getDouble(prefix + "." + PAR_SCALE, 
				DEFAULT_SCALE);

		// lazy configuration: remaining parameters are only read on demand
	}
	
	
	// Control implementation -------------------------------------------------
	// ------------------------------------------------------------------------

	@Override
	public boolean execute() 
	{
		Node node = Network.prototype;
		Networking net = (Networking)node.getProtocol(nid);
		Graph graph = net.getGraph();
		Graph.Property weight = graph.addProperty(this.weight);
		System.out.println(this.weight+": "+weight);
		
		Set<? extends Graph.Element> elements = null;
		switch (this.element) {
		case VERTEX:
			elements = graph.vertices();
			break;
		case EDGE:
			elements = graph.edges();
			break;
		}
		
		for (Graph.Element e : elements)
		{
			double value = 0.0d; 
			switch (this.strategy) {
			case CONSTANT:
				value = constant(e);
				break;
			case DISTRIBUTION:
				value = distribution(e);
				break;
			case PROPERTY:
				value = property(e);
				break;
			case RANDOM:
				value = random(e);
				break;
			}

			value = this.scale*value;
			value = Math.max(value, this.min);
			value = Math.min(value, this.max);
			
			e.set(weight,value);
		}
		
		if (LOG.isInfoEnabled())
		{
			String msg = "successfully assigned weights to ";
			switch (this.element) {
			case EDGE:
				msg += "'" + elements.size() + "' edges ";
				break;
			case VERTEX:
				msg += "'" + elements.size() + "' vertices ";
				break;
			}
			switch (this.strategy) {
			case CONSTANT:
				msg += "using a constant value of '" + value + "'";
				break;
			case DISTRIBUTION:
				msg += "using '" + distribution + "' values";
				break;
			case PROPERTY:
				msg += "using '" + property + "' values";
				break;
			case RANDOM:
				msg += "using random values";
				break;
			}	
			LOG.info(msg);
		}
		
		return false;
	}
	
	
	// strategies -------------------------------------------------------------
	// ------------------------------------------------------------------------
	
	private Random r = null;
	public double random(Graph.Element e)
	{
		if (r==null)
		{
			long seed = Configuration.getLong(prefix + "." + PAR_SEED, 
					CommonState.r.getLastSeed()^DEFAULT_SEED);
			r = new Random(seed);
		}
		
		return (min+((max-min)*r.nextDouble()))/scale;
	}
	
	double value = Double.NaN;
	public double constant(Graph.Element e)
	{
		if (Double.isNaN(value))
		{
			value = Configuration.getDouble(prefix + "." + PAR_VALUE, 
					DEFAULT_VALUE);
		}
		return value;
	}
	
	Distribution distribution = null;
	public double distribution(Graph.Element e)
	{
		if (distribution==null)
		{
			distribution = (Distribution)Configuration.getInstance(prefix 
					+ "." + PAR_DISTRIBUTION, DEFAULT_DISTRIBUTION);
		}
		return distribution.getValue();
	}
	
	Graph.Property property = null;
	public double property(Graph.Element e)
	{
		if (property==null)
		{
			String name = Configuration.getString(prefix + "." + PAR_PROPERTY, 
					DEFAULT_PROPERTY);
			Node node = Network.prototype;
			Networking net = (Networking)node.getProtocol(nid);
			property = net.getProperty(name);
			if (property==null)
			{
				LOG.error("property '" + name + "' not found");
			}
		}
		return (Double)e.get(property);
	}
}
