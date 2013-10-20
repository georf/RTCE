package rebeca.peernet;

import java.util.*;

import peersim.core.*;

import rebeca.peernet.RoutingLink.*;
import rebeca.util.*;

/**
 * @author parzy
 *
 */
public class QueueingRouter extends SimRouter
{
	// fields -----------------------------------------------------------------
	// ------------------------------------------------------------------------

	protected LinkedList<RoutedMessage> queue; // input/processing queue

	
	// instantiation ----------------------------------------------------------
	// ------------------------------------------------------------------------

	public QueueingRouter() { super(); }
	public QueueingRouter(String prefix) { super(prefix); }
	
	@Override
	public void init(Graph.Vertex v) 
	{
		super.init(v);
		
		queue  = new LinkedList<RoutedMessage>();
	}

	
	// transport logic --------------------------------------------------------
	// ------------------------------------------------------------------------
	
	@Override
	public void send(SimSink sender, Node src, Node dst, RoutedMessage msg) 
	{
		queue.addLast(msg);
		if (queue.size()==1) schedule(new RoutingEvent(this,msg));
	}
	
	@Override
	public void processEvent(Node node, int pid, RoutingEvent e)
	{
		// look at current and next message to handle
		RoutedMessage cur = e.getMessage();
		RoutedMessage next = null;
		
		// update queue's state
		queue.removeFirst();
		if (!queue.isEmpty())
		{
			next = queue.getFirst();
		}
		
		// forward current message
		forward(cur);
		
		// schedule next message when available
		if (next!=null)
		{
			schedule(new RoutingEvent(this,next));
		}
	}
}
