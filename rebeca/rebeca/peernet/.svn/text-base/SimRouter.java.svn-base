/**
 * 
 */
package rebeca.peernet;

import org.apache.log4j.Logger;

import peersim.core.*;
import peersim.edsim.*;

import rebeca.*;
import rebeca.broker.*;
import rebeca.peernet.RoutingLink.*;
import rebeca.util.Graph;


/**
 * @author parzy
 *
 */
public class SimRouter extends SimSink implements SimComponent
{
	public static class RoutingEvent implements SimEvent
	{
		protected SimRouter router = null;
		protected RoutedMessage msg = null;;
		
		protected RoutingEvent() { }
		public RoutingEvent(SimRouter router, RoutedMessage msg)
		{
			this.router = router;
			this.msg = msg;
		}
		
		// getters and setters ------------------------------------------------
		// --------------------------------------------------------------------
		
		public SimRouter getComponent() 
		{
			return router;
		}
		public void setComponent(SimRouter router) 
		{
			this.router = router;
		}

		public RoutedMessage getMessage() 
		{
			return msg;
		}
		public void setMsg(RoutedMessage msg) 
		{
			this.msg = msg;
		}
		
		public Node getDestinationNode()
		{
			return msg.getDestination();
		}
	}
	

	// fields -----------------------------------------------------------------
	// ------------------------------------------------------------------------
	
	protected Logger log = null;
	protected int pid = -1;
	

	// instantiation ---------------------------------------------------------
	// ------------------------------------------------------------------------
	
	public SimRouter() { }
	
	public SimRouter(String prefix)
	{
		this();
		this.pid = CommonState.getPid();
	}
	
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
	
	public void init(Graph.Vertex v) { /* to be overridden in subclasses */ }
	
	
	// message handling -------------------------------------------------------
	// ------------------------------------------------------------------------
	
	@Override
	public void in(Object msg)
	{
		if (msg instanceof RoutedMessage)
		{
			forward((RoutedMessage)msg);
			return;
		}
		log.error("unhandled message '" + msg + "'");
	}
		
	@Override
	public void send(SimSink sender, Node src, Node dst, Object msg)
	{
		if (msg instanceof RoutedMessage)
		{
			send(sender,src,dst,(RoutedMessage)msg);
			return;
		}
		log.error("unhandled message '" + msg + "'");
	}
	
	public void send(SimSink sender, Node src, Node dst, RoutedMessage msg)
	{
		schedule(new RoutingEvent(this,msg));
	}
	
	public void schedule(RoutingEvent e)
	{
		EDSimulator.add(0, e, e.getDestinationNode(), pid);
	}
	
	@Override
	public void processEvent(Node node, int pid, Object e)
	{
		// dispatch event
		if ( e instanceof RoutingEvent ) 
		{
			processEvent(node,pid,(RoutingEvent)e);
			return; 
		}
		log.error("unhandled event '" + e + "'");
	}
	
	public void processEvent(Node node, int pid, RoutingEvent e)
	{
		forward(e.getMessage());
	}
	
	public void forward(RoutedMessage msg)
	{
		if (!msg.isCanceled())
		{
			RoutingLink link = msg.getComponent();
			link.forward(this, msg);
		}
	}

	
	// visualization ----------------------------------------------------------
	// ------------------------------------------------------------------------
	
	@Override
	public String toString()
	{
		return this.getClass().getName(); 
	}
	
	
	// EventSink implementation -----------------------------------------------
	// ------------------------------------------------------------------------

	@Override
	public void activate() 
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void exit() 
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public EventProcessor getProcessor() 
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public EventSink getSink() 
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public int hashCode() 
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void init() 
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isLocal() 
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void passivate() 
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void setInputSink(EventSink sink) 
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void setOutputSink(EventSink sink) 
	{
		throw new UnsupportedOperationException();
	}
}
