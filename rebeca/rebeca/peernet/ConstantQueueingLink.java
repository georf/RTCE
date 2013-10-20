/**
 * 
 */
package rebeca.peernet;

import peersim.config.*;
import peersim.edsim.*;

import rebeca.util.Graph.Edge;


/**
 * @author parzy
 *
 */
public class ConstantQueueingLink extends QueueingLink 
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
	
	public ConstantQueueingLink()
	{
		this(DEFAULT_DELAY);
	}
	
	public ConstantQueueingLink(long delay)
	{
		this.delay = delay;
	}
	
	public ConstantQueueingLink(String prefix)
	{
		super(prefix);
		
		this.delay = Configuration.getLong(prefix + "." + PAR_DELAY, 
				DEFAULT_DELAY);
	}

	@Override
	public void init(SimSink src, SimSink dst, Edge e)
	{
		super.init(src,dst,e);
		
		Object delay = e.get(PAR_DELAY);
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
	public void schedule(SimMessage msg)
	{
		// long delay = this.delay;	
		EDSimulator.add(delay, msg, msg.getDestination(), pid);
	}	
}
