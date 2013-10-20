/*
 * $Id$
 */
package rebeca.transport;

import java.net.*;

import rebeca.*;

/**
 * @author parzy
 *
 */
public class InetContactProfile implements ContactProfile 
{
	protected InetSocketAddress local;
	protected InetSocketAddress remote;

	
	public InetContactProfile()
	{
		this((InetSocketAddress)null,(InetSocketAddress)null);
	}
	public InetContactProfile(InetSocketAddress local, InetContactProfile remote)
	{
		this(local,remote.getRemoteSocketAddress());
	}
	public InetContactProfile(InetContactProfile local, InetSocketAddress remote)
	{
		this(local.getLocalSocketAddress(),remote);
	}
	public InetContactProfile(InetSocketAddress local, InetSocketAddress remote)
	{
		this.local = local;
		this.remote = remote;
	}
		
	public InetSocketAddress getLocalSocketAddress()
	{
		return local;	
	}
	
	public InetSocketAddress getRemoteSocketAddress()
	{
		return remote;	
	}
	
	@Override
	public Object getEngineKey()
	{
		return TransportEngine.class;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (! (obj instanceof InetContactProfile))
		{
			return false;
		}
		
		InetContactProfile p = (InetContactProfile)obj;
		return (local != null ? local.equals(p.local) : p.local == null)
			&& (remote != null ? remote.equals(p.remote) : p.remote == null);
	}
	
	@Override
	public int hashCode()
	{
		return (local != null ? local.hashCode() : 12345)
			^ (remote != null ? remote.hashCode() : 67890);
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
		return "unbound";
	}
}
