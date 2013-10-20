package rebeca.peernet;

import java.util.*;

import peersim.core.*;

/**
 * @author parzy
 *
 */
public class QueueingLink extends SimLink
{
	// fields -----------------------------------------------------------------
	// ------------------------------------------------------------------------

	protected LinkedList<SimMessage> out; // queues for each direction
	protected LinkedList<SimMessage> in;  // ensuring FIFO ordering

	
	// instantiation ----------------------------------------------------------
	// ------------------------------------------------------------------------

	public QueueingLink() { super(); }
	public QueueingLink(String prefix) { super(prefix); }
	
	@Override
	public void init(SimSink src, SimSink dst) 
	{
		super.init(src, dst);
		in  = new LinkedList<SimMessage>();
		out = new LinkedList<SimMessage>();
	}

	
	// transport logic --------------------------------------------------------
	// ------------------------------------------------------------------------
	
	@Override
	public void send(SimSink sender, Node src, Node dst, Object msg) 
	{
		// create new message event
		SimMessage m = new SimMessage(this,sender,src,dst,msg); 
		// queue and schedule it when appropriate
		if (isOutbound(m))
		{
			out.addLast(m);
			if (out.size()==1) schedule(m);
		}
		else
		{
			in.addLast(m);
			if (in.size()==1) schedule(m);
		}
	}
	
	@Override
	public void processEvent(Node node, int pid, Object msg)
	{
		SimMessage cur=null, next=null;
		
		// check type 
		if ( !(msg instanceof SimMessage) ) 
		{ 
			log.error("unhandled message '" + msg + "'");
			return; 
		}
		cur = (SimMessage)msg;
		
		// determine queue and remove serviced message
		if (isOutbound(cur))
		{
			out.removeFirst();
			if (!out.isEmpty())
			{
				next = out.getFirst();
			}
		}
		else
		{
			in.removeFirst();
			if (!in.isEmpty())
			{
				next = in.getFirst();
			}
		}
		
		// upward event along the receiving sink
		receive(cur);
		
		// schedule next message when available
		if (next!=null)
		{
			schedule(next);
		}
	}
	
	public boolean isOutbound(SimMessage msg)
	{
		return msg.getSender()==this.src;
	}
}
