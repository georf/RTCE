/**
 * 
 */
package rebeca.peernet;

import peersim.config.*;
import peersim.core.*;
import peersim.edsim.*;

/**
 * @author parzy
 *
 */
public class ConstantRouter extends SimRouter 
{
	// parameters and constants -----------------------------------------------
	// ------------------------------------------------------------------------
	
	private static final String PAR_DELAY = "delay";
	private static final String PAR_FIFO = "fifo";
	private static final long DEFAULT_DELAY = 1;
	private static final boolean DEFAULT_FIFO = false;
	
	
	// fields -----------------------------------------------------------------
	// ------------------------------------------------------------------------
	
	protected long delay;
	protected boolean fifo;
	protected long time;
	
	// instantiation ----------------------------------------------------------
	// ------------------------------------------------------------------------
	
	public ConstantRouter()
	{
		this(DEFAULT_DELAY, DEFAULT_FIFO);
	}
	
	public ConstantRouter(long delay)
	{
		this(delay, DEFAULT_FIFO);
	}
	
	public ConstantRouter(boolean fifo)
	{
		this(DEFAULT_DELAY,fifo);
	}
	
	public ConstantRouter(long delay, boolean fifo)
	{
		this.delay = delay;
		this.fifo = fifo;
		this.time = -1;
	}
	
	public ConstantRouter(String prefix)
	{
		super(prefix);
		
		this.delay = Configuration.getLong(prefix + "." + PAR_DELAY, 
				DEFAULT_DELAY);
		this.fifo = Configuration.getBoolean(prefix + "." + PAR_FIFO, 
				DEFAULT_FIFO);
		this.time = -1;
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
	
	/**
	 * @return the fifo
	 */
	public boolean isFifo() 
	{
		return fifo;
	}

	/**
	 * @param fifo the fifo to set
	 */
	public void setFifo(boolean fifo) 
	{
		this.fifo = fifo;
	}
	
	
	// transport logic --------------------------------------------------------
	// ------------------------------------------------------------------------

	@Override
	public void schedule(RoutingEvent e)
	{
		
		long delay = this.delay;	

		if (fifo)
		{
			// guarantee a fifo ordering on the link 
			// by extending the delay if necessary 
			long arrival = CommonState.getTime()+delay;
			// ensure that processing is scheduled at least 
			// one tick after the last event
			if (arrival <= time)
			{
				arrival = time+1;
				delay = arrival-CommonState.getTime();
			}
			time = arrival;
		}
		
		EDSimulator.add(delay, e, e.getDestinationNode(), pid);
	}	
}
