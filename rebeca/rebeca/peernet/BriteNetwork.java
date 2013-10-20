/**
 * 
 */
package rebeca.peernet;

//import org.apache.log4j.*;

import peersim.edsim.*;

import rebeca.*;
import rebeca.util.*;

/**
 * @author parzy
 *
 */
public class BriteNetwork extends NetworkGraph 
implements BrokerEngine, EDProtocol
{
	// parameters and constants -----------------------------------------------
	// ------------------------------------------------------------------------
	
	/**
	 * Key identifying this engine. 
	 */
	public static final Class<BriteNetwork> KEY = BriteNetwork.class;
	
	/** 
	 * Logger for transport engines. 
	 */
	//private static final Logger LOG = Logger.getLogger(KEY);
	
	public static final String PAR_X = "x";
	public static final String PAR_Y = "y";
	public static final String PAR_IN = "in";
	public static final String PAR_OUT = "out";
	public static final String PAR_AS = "as";
	public static final String PAR_TYPE = "type";
	public static final String PAR_LENGTH = "length";
	public static final String PAR_DELAY = "delay";
	public static final String PAR_BANDWIDTH = "bandwidth";

	
	// fields -----------------------------------------------------------------
	// ------------------------------------------------------------------------
	
	protected Broker broker;
	
	protected Graph.Property x;
	protected Graph.Property y;
	protected Graph.Property in;
	protected Graph.Property out;
	protected Graph.Property as;
	protected Graph.Property type;
	protected Graph.Property length;
	protected Graph.Property delay;
	protected Graph.Property bandwidth;

	
	// instantiation ----------------------------------------------------------
	// ------------------------------------------------------------------------
	
	public BriteNetwork(String prefix)
	{
		super(prefix);
		
		x = graph.addProperty(PAR_X);
		y = graph.addProperty(PAR_Y);
		in = graph.addProperty(PAR_IN);
		out = graph.addProperty(PAR_OUT);
		as = graph.addProperty(PAR_AS);
		type = graph.addProperty(PAR_TYPE);
		length = graph.addProperty(PAR_LENGTH);
		delay = graph.addProperty(PAR_DELAY);
		bandwidth = graph.addProperty(PAR_BANDWIDTH);
		
		// TODO parzy implement remaining initializations
	}
	
	// clone method of super class is sufficient

	
	// property management ----------------------------------------------------
	// ------------------------------------------------------------------------
	
	public Graph.Property addProperty()
	{
		return graph.addProperty();
	}
	
	public Graph.Property addProperty(String property)
	{
		return graph.addProperty(property);
	}
	
	public void removeProperty(Graph.Property p)
	{
		graph.removeProperty(p);
	}
	
	public void removeProperty(String property)
	{
		graph.removeProperty(property);
	}
	
	
	
	/**
	 * @return the x
	 */
	public Graph.Property getX() 
	{
		return x;
	}

	/**
	 * @param x the x to set
	 */
	public void setX(Graph.Property x) 
	{
		graph.removeProperty(this.x);
		this.x = x;
	}

	/**
	 * @return the y
	 */
	public Graph.Property getY() 
	{
		return y;
	}

	/**
	 * @param y the y to set
	 */
	public void setY(Graph.Property y) 
	{
		graph.removeProperty(this.y);
		this.y = y;
	}

	/**
	 * @return the in
	 */
	public Graph.Property getIn() 
	{
		return in;
	}

	/**
	 * @param in the in to set
	 */
	public void setIn(Graph.Property in) 
	{
		graph.removeProperty(this.in);
		this.in = in;
	}

	/**
	 * @return the out
	 */
	public Graph.Property getOut() 
	{
		return out;
	}

	/**
	 * @param out the out to set
	 */
	public void setOut(Graph.Property out) 
	{
		graph.removeProperty(this.out);
		this.out = out;
	}

	/**
	 * @return the as
	 */
	public Graph.Property getAs() 
	{
		return as;
	}

	/**
	 * @param as the as to set
	 */
	public void setAs(Graph.Property as) 
	{
		graph.removeProperty(this.as);
		this.as = as;
	}

	/**
	 * @return the type
	 */
	public Graph.Property getType() 
	{
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(Graph.Property type) 
	{
		graph.removeProperty(this.type);
		this.type = type;
	}

	/**
	 * @return the length
	 */
	public Graph.Property getLength() 
	{
		return length;
	}

	/**
	 * @param length the length to set
	 */
	public void setLength(Graph.Property length) 
	{
		graph.removeProperty(this.length);
		this.length = length;
	}

	/**
	 * @return the delay
	 */
	public Graph.Property getDelay() 
	{
		return delay;
	}

	/**
	 * @param delay the delay to set
	 */
	public void setDelay(Graph.Property delay) 
	{
		graph.removeProperty(this.delay);
		this.delay = delay;
	}

	/**
	 * @return the bandwidth
	 */
	public Graph.Property getBandwidth() 
	{
		return bandwidth;
	}

	/**
	 * @param bandwidth the bandwidth to set
	 */
	public void setBandwidth(Graph.Property bandwidth) 
	{
		graph.removeProperty(this.bandwidth);
		this.bandwidth = bandwidth;
	}
	
	
	// BrokerEngine implementation --------------------------------------------
	// ------------------------------------------------------------------------
	
	@Override
	public Object getKey() { return KEY; }
	@Override
	public Broker getBroker() { return broker; }
	@Override
	public void setBroker(Broker broker) { this.broker = broker; }
	@Override
	public void init() { }
	@Override
	public void activate(){ }
	@Override
	public void passivate() { }
	@Override
	public void exit() { }
	@Override
	public void process(Event event, EventProcessor source) { }
	@Override
	public Object plug(Object obj) { return obj; }
}
