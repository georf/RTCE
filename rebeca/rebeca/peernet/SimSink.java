/**
 * 
 */
package rebeca.peernet;

import org.apache.log4j.*;

import peersim.core.*;

import rebeca.*;
import rebeca.broker.*;
import rebeca.peersim.*;

/**
 * @author parzy
 *
 */
public class SimSink extends PluggableEventSink
{
	private static final Logger LOG = Logger.getLogger(SimSink.class);
	
	protected Node local = null;
	protected Node remote = null;
	protected SimLink link = null;
	
	public SimSink() { }
	
	public SimSink(Node local, Node remote, SimLink link)
	{
		this.local = local;
		this.remote = remote;
		this.link = link;
	}
	
	public Node getLocal()
	{
		return local;
	}
	
	public Node getRemote()
	{
		return remote;
	}
	
	// TODO parzy check whether this method can be removed 
	public void in(Object msg)
	{
		in((Event)msg);
	}
	
	@Override
	public void out(rebeca.Event event)
	{
		if (LOG.isDebugEnabled()) LOG.debug("sending event '" + event 
				+ " from node #" + local.getIndex() + " to node #" 
				+ remote.getIndex());
		link.send(this, local, remote, event);
	}
	
	private ContactProfile profile;
	@Override
	public ContactProfile getContactProfile()
	{
		return profile != null ? profile : 
			  (profile = new PeerSimContactProfile(local,remote));
	}
	
	
	// visualization ----------------------------------------------------------
	// ------------------------------------------------------------------------
	
	@Override
	public String toString()
	{
		return "Sink at #" + local.getIndex() + " from/to #" 
				+ remote.getIndex(); 
	}
}