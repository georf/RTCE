/**
 * 
 */
package rebeca.peernet;

import org.apache.log4j.Logger;

import peersim.core.*;
import peersim.edsim.*;
import rebeca.util.Graph.*;

/**
 * @author parzy
 *
 */
public class SimLink implements SimComponent
{
	/**
	 * Wraps a message object.
	 * @author parzy
	 *
	 */
	public static class SimMessage implements SimEvent
	{
		protected SimLink link = null;
		protected SimSink sender = null;
		protected Node src = null;
		protected Node dst = null;
		protected Object msg = null;
		protected boolean cancel = false;;
		
		public SimMessage() { }
		public SimMessage(SimLink link, SimSink sender, Node src, Node dst, Object msg)
		{
			this.link = link;
			this.sender = sender;
			this.src = src;
			this.dst = dst;
			this.msg = msg;
		}
		
		public Object get()
		{
			return msg;
		}
		public void set(Object msg)
		{
			this.msg = msg;
		}
		
		public SimLink getComponent()
		{
			return link;
		}
		public void setComponent(SimLink link)
		{
			this.link = link;
		}
		
		public SimSink getSender()
		{
			return sender;
		}
		public void setSender(SimSink sender)
		{
			this.sender = sender;
		}
		
		public Node getSource()
		{
			return src;
		}
		public void setSource(Node src)
		{
			this.src = src;
		}
		
		public Node getDestination()
		{
			return dst;
		}
		public void setDestination(Node dst)
		{
			this.dst = dst;
		}
		
		public boolean isCanceled()
		{
			return cancel;
		}
		public void cancel()
		{
			cancel = true;
		}
	}

	// fields -----------------------------------------------------------------
	// ------------------------------------------------------------------------
	
	protected Logger log = null;
	protected int pid = -1;
//	protected String prefix = null;
	
//	protected Edge edge = null;
	protected SimSink src = null;
	protected SimSink dst = null;
	
	
	// instantiation and initialization ---------------------------------------
	// ------------------------------------------------------------------------
	
	public SimLink() { }
	
	public SimLink(String prefix)
	{
		this();
		//this.prefix = prefix;
		this.pid = CommonState.getPid();
	}
	
	@Override
	public Object clone()
	{
		try
		{
			return super.clone();
		}
		// never happens
		catch (CloneNotSupportedException e) { return null; }
	}
	
	public void init(Logger log, int pid)
	{
		this.log = log;
		this.pid = pid;
	}
	
	public void init(SimSink src, SimSink dst)
	{ 
		this.src = src;
		this.dst = dst;
	}

	// to be overridden/extended in subclasses
	public void init(SimSink src, SimSink dst, Edge e) 
	{ 
		init(src,dst); 
	}
	
	
	// getters and setters ----------------------------------------------------
	// ------------------------------------------------------------------------
	
	
	/**
	 * @return the lOG
	 */
	public Logger getLogger() 
	{
		return log;
	}

	/**
	 * @param lOG the lOG to set
	 */
	public void setLogger(Logger log) 
	{
		this.log = log;
	}
	
	/**
	 * @return the pid
	 */
	public int getPid() 
	{
		return pid;
	}

	/**
	 * @param pid the pid to set
	 */
	public void setPid(int pid) 
	{
		this.pid = pid;
	}
	


	
	public SimSink getSource()
	{
		return src;
	}
	public void setSource(SimSink sink)
	{
		this.src = sink;
	}
	public Node getSourceNode()
	{
		return src.getLocal();
	}

	
	public SimSink getDestination()
	{
		return dst;
	}
	public void setDestination(SimSink sink)
	{
		this.dst = sink;
	}
	public Node getDestinationNode()
	{
		return dst.getLocal();
	}
	
	public void setSinks(SimSink src, SimSink dst)
	{
		this.src = src;
		this.dst = dst;
	}

	
	
	// transport logic --------------------------------------------------------
	// ------------------------------------------------------------------------
	
	public void send(SimSink sender, Node src, Node dst, Object msg)
	{
		if (log!=null && log.isDebugEnabled()) log.debug("transporting " 
				+ "message '" + msg + "' from node #" + src.getIndex() 
				+ " to #" + dst.getIndex() + " at time " 
				+ CommonState.getTime());			
		schedule(new SimMessage(this,sender,src,dst,msg));
	}
	
	public void schedule(SimMessage msg)
	{
		EDSimulator.add(0, msg, msg.getDestination(), pid);
	}
	
	public void processEvent(Node node, int pid, Object event)
	{
		// check type
		if ( !(event instanceof SimMessage) ) 
		{ 
			log.error("unhandled event '" + event + "'");
			return; 
		}
		// upward message along the receiving sink 
		receive((SimMessage)event);
	}
	
	public void receive(SimMessage msg)
	{
		if (!msg.isCanceled())
		{
			SimSink sink = msg.getSender()==dst ? src : dst;
			if (log!=null && log.isDebugEnabled()) log.debug("delivering " 
					+ "message '" + msg + "' at node #" 
					+ sink.getLocal().getIndex() + " at time "
					+ CommonState.getTime());
			sink.in(msg.get());
		}
	}
}
