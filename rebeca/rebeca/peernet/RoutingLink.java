/**
 * 
 */
package rebeca.peernet;

import java.util.*;

import peersim.config.Configuration;
import peersim.core.*;

import rebeca.util.*;

/**
 * @author parzy
 *
 */
public class RoutingLink extends SimLink
{
	public static class RoutedMessage extends SimMessage
	{
		protected boolean outbound;
		protected ListIterator<SimComponent> it = null;
		
		
		public RoutedMessage() { }
		public RoutedMessage(RoutingLink link, SimSink sender, Node src, Node dst, Object msg)
		{
			super(link,sender,src,dst,msg);
			outbound = link.getSource()==sender;
			
			it = link.listIterator(isOutbound()?0:link.size());
		}
		
		@Override
		public RoutingLink getComponent()
		{
			return (RoutingLink)link;
		}
		@Override
		public void setComponent(SimLink link)
		{
			this.link = (RoutingLink)link;
		}
		
		private boolean isOutbound()
		{
			return outbound;
		}
		
		public boolean hasArrived()
		{
			return isOutbound() ? !it.hasNext() : !it.hasPrevious();
		}
		
		public SimComponent next()
		{
			return isOutbound() ? it.next() : it.previous();
		}
	}
	
	// parameters and constants -----------------------------------------------
	// ------------------------------------------------------------------------
	
	private static final String PAR_ROUTING = "routing";
	private static final String PAR_ROUTE = "route";
	
	// fields -----------------------------------------------------------------
	// ------------------------------------------------------------------------

	String property;
	int routing;
	List<SimComponent> route;
	
	// instantiation & initialization -----------------------------------------
	// ------------------------------------------------------------------------

	// TODO parzy how do we get the hops in the link?
	public RoutingLink() 
	{ 
		super();
		
		this.routing = -1;
		this.property = PAR_ROUTE;
	}
		
	public RoutingLink(String prefix) 
	{ 
		super(prefix);
		
		this.routing = Configuration.getPid(prefix + "." + PAR_ROUTING, -1);
		this.property = Configuration.getString(prefix + "." + PAR_ROUTE, PAR_ROUTE);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void init(SimSink src, SimSink dst, Graph.Edge e)
	{
		super.init(src, dst, e);
		
		// determine nodes
		Node n = getSourceNode(); 
		Node m = getDestinationNode();
		boolean reverse = n.getID()>m.getID();
		
		// get calculated route associated with edge
		List<SimComponent> path = null;
		if (routing!=-1)
		{
			NetworkRouting r = (NetworkRouting)n.getProtocol(routing);
			Graph.Property p = r.getRoute();
			path = (List<SimComponent>)e.get(p);
		}
		else
		{
			path = (List<SimComponent>)e.get(property);
		}
		if (path==null) throw new IllegalStateException("no route for link");
		
		// copy route (and reverse it if necessary)
		this.route = new ArrayList<SimComponent>(path);
		if (reverse) Collections.reverse(this.route);
	}
	

	// route management -------------------------------------------------------
	// ------------------------------------------------------------------------
	
	public ListIterator<SimComponent> listIterator()
	{
		return route.listIterator();
	}
	
	public ListIterator<SimComponent> listIterator(int index)
	{
		return route.listIterator(index);
	}
	
	public int size()
	{
		return route.size();
	}
	
	
	// transport logic --------------------------------------------------------
	// ------------------------------------------------------------------------
	
	
	@Override
	public void send(SimSink sender, Node src, Node dst, Object msg)
	{
		forward(sender,new RoutedMessage(this,sender,src,dst,msg));
	}
	
	public void forward(SimSink sender, RoutedMessage msg)
	{
		// arrived at destination?
		if (msg.hasArrived())
		{
			receive(msg);
			return;
		}
		
		// send message along next hop
		SimComponent next = msg.next();
		next.send(sender,msg.getSource(),msg.getDestination(),msg);
	}	

	@Override
	public void schedule(SimMessage msg) { }
	
	@Override
	public void processEvent(Node node, int pid, Object event) { }	
}