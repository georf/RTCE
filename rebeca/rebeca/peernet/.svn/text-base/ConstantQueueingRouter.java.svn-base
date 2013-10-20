/**
 * 
 */
package rebeca.peernet;

import peersim.config.*;
import peersim.edsim.*;

import rebeca.util.*;

/**
 * @author parzy
 *
 */
public class ConstantQueueingRouter extends SimRouter 
{
	// parameters and constants -----------------------------------------------
	// ------------------------------------------------------------------------
	
	private static final String PAR_DELAY = "delay";
	private static final long DEFAULT_DELAY = 1;

	
	// fields -----------------------------------------------------------------
	// ------------------------------------------------------------------------
	
	protected long delay;

	
	// instantiation ----------------------------------------------------------
	// ------------------------------------------------------------------------
	
	public ConstantQueueingRouter()
	{
		this(DEFAULT_DELAY);
	}
	
	public ConstantQueueingRouter(long delay)
	{
		this.delay = delay;
	}
	
	public ConstantQueueingRouter(String prefix)
	{
		super(prefix);
		
		this.delay = Configuration.getLong(prefix + "." + PAR_DELAY, 
				DEFAULT_DELAY);
	}
	
	@Override
	public void init(Graph.Vertex v)
	{
		super.init(v);
		
		Object delay = v.get(PAR_DELAY);
		if (delay != null) this.delay = (Long)delay;
	}
	
	// getters and setters ----------------------------------------------------
	// ------------------------------------------------------------------------
	
	/**
	 * @return the delay
	 */
	public long getDelay() 
	{
		return delay;
	}

	/**
	 * @param delay the delay to set
	 */
	public void setDelay(long delay) 
	{
		this.delay = delay;
	}
	
	
	// transport logic --------------------------------------------------------
	// ------------------------------------------------------------------------

	@Override
	public void schedule(RoutingEvent e)
	{
		// long delay = this.delay;	
		EDSimulator.add(delay, e, e.getDestinationNode(), pid);
	}	
}
