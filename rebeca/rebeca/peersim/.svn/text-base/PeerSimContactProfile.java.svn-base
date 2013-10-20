/*
 * $Id$
 */
package rebeca.peersim;

import peersim.core.*;

import rebeca.*;
import rebeca.transport.TransportEngine;

/**
 * @author parzy
 *
 */
public class PeerSimContactProfile implements ContactProfile 
{
	protected Node local;
	protected Node remote;

	public PeerSimContactProfile()
	{
		this(null,null);
	}
	
	public PeerSimContactProfile(Node local, Node remote)
	{
		this.local = local;
		this.remote = remote;
	}
	
	public Node getLocal()
	{
		return local;
	}
	
	public Node getRemote()
	{
		return remote;
	}
	
	@Override
	public Object getEngineKey()
	{
		return SimTransportEngine.class;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (! (obj instanceof PeerSimContactProfile))
		{
			return false;
		}
		
		PeerSimContactProfile p = (PeerSimContactProfile)obj;
		return (local != null ? local.equals(p.local) : p.local == null)
			&& (remote != null ? remote.equals(p.remote) : p.remote == null);
	}
	
	@Override
	public int hashCode()
	{
		return (local != null ? local.hashCode() : 98760)
			^ (remote != null ? remote.hashCode() : 54321);
	}
	
	@Override
	public String toString()
	{
		if (remote != null)
		{
			return "remote:"+remote.toString();
		}
		if (local != null)
		{
			return "local:"+local.toString();
		}
		return "unconnected";
	}
}
