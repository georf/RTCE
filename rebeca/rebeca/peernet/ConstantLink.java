/**
 * $Id$
 */
package rebeca.peernet;

import peersim.config.*;
import peersim.core.*;
import peersim.edsim.*;

import rebeca.util.Graph.*;

/**
 * @author parzy
 *
 */
public class ConstantLink extends SimLink 
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
	protected long srcTime;
	protected long dstTime;
	
	// instantiation ----------------------------------------------------------
	// ------------------------------------------------------------------------
	
	public ConstantLink()
	{
		this(DEFAULT_DELAY, DEFAULT_FIFO);
	}
	
	public ConstantLink(long delay)
	{
		this(delay, DEFAULT_FIFO);
	}
	
	public ConstantLink(boolean fifo)
	{
		this(DEFAULT_DELAY,fifo);
	}
	
	public ConstantLink(long delay, boolean fifo)
	{
		this.delay = delay;
		this.fifo = fifo;
		this.srcTime = this.dstTime = -1;
	}
	
	public ConstantLink(String prefix)
	{
		this.pid = CommonState.getPid();
		this.delay = Configuration.getLong(prefix + "." + PAR_DELAY, 
				DEFAULT_DELAY);
		this.fifo = Configuration.getBoolean(prefix + "." + PAR_FIFO, 
				DEFAULT_FIFO);
		this.srcTime = this.dstTime = -1;
	}

	@Override
	public void init(SimSink src, SimSink dst, Edge e)
	{
		super.init(src,dst,e);
		
		Object delay = e.get(PAR_DELAY);
		if (delay != null) this.delay = delay instanceof Long ? (Long)delay : ((Double)delay).longValue();
		Object fifo = e.get(PAR_FIFO);
		if (fifo != null) this.fifo = (Boolean)fifo;
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
	public void schedule(SimMessage msg)
	{
		
		long delay = this.delay;	

		if (fifo)
		{
			// guarantee a fifo ordering on the link 
			// by extending the delay if necessary 
			long arrival = CommonState.getTime()+delay;
			if (getDestinationNode()==msg.getDestination())
			{
				// ensure that arrival is scheduled at least 
				// one tick after the  last one
				if (arrival <= dstTime)
				{
					arrival = dstTime+1;
					delay = arrival-CommonState.getTime();
				}
				dstTime = arrival;
			}
			else
			{
				// ensure that arrival is scheduled at least 
				// one tick after the  last one
				if (arrival <= srcTime)
				{
					arrival = srcTime+1;
					delay = arrival-CommonState.getTime();
				}
				srcTime = arrival;
			}
		}
		
		EDSimulator.add(delay, msg, msg.getDestination(), pid);
	}	
}
