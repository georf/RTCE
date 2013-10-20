/*
 * $id$
 */
package rebeca.event;

import java.util.Collection;

import rebeca.*;
import rebeca.filter.*;
import rebeca.scope.*;

/**
 * @author parzy
 *
 */
public class SubscriptionFactory 
{
	public static Subscription getSubscription(Filter f)
	{
		if (f instanceof ScopedFilter)
		{
			return getSubscription((ScopedFilter)f);
		}
		if (f instanceof ScopeIdentifier)
		{
			return getSubscription((ScopeIdentifier)f);
		}
		return new Subscription(f);
	}
	
	public static Subscription getSubscription(ScopedFilter f)
	{
		if (f.getFilter() instanceof ScopeIdentifier)
		{
			return new ScopeSubscription(f);
		}
		return new Subscription(f);
	}
	
	public static ScopeSubscription getSubscription(ScopeIdentifier f)
	{
		return new ScopeSubscription(f);
	}
	
	public static Unsubscription getUnsubscription(Filter f)
	{
		if (f instanceof ScopedFilter)
		{
			return getUnsubscription((ScopedFilter)f);
		}
		if (f instanceof ScopeIdentifier)
		{
			return getUnsubscription((ScopeIdentifier)f);
		}
		return new Unsubscription(f);
	}
	
	public static Unsubscription getUnsubscription(ScopedFilter f)
	{
		if (f.getFilter() instanceof ScopeIdentifier)
		{
			return new ScopeUnsubscription(f);
		}
		return new Unsubscription(f);
	}

	public static ScopeUnsubscription getUnsubscription(ScopeIdentifier f)
	{
		return new ScopeUnsubscription(f);
	}
	
	public static Unsubscription getUnsubscription(Filter f, 
			Collection<? extends Subscription> c )
	{
		Unsubscription unsubscription = getUnsubscription(f);
		if (c != null)
		{
			unsubscription.addAllUncovered(c);
		}
		return unsubscription;
	}
	
	
	public static Advertisement getAdvertisement(Filter f)
	{
		if (f instanceof ScopedFilter)
		{
			return getAdvertisement((ScopedFilter)f);
		}
		return new Advertisement(f);
	}
	
	public static Advertisement getAdvertisement(ScopedFilter f)
	{
		if (f.getFilter() instanceof ScopeIdentifier)
		{
			return new ScopeAdvertisement(f);
		}
		return new Advertisement(f);
	}

	public static ScopeAdvertisement getAdvertisement(Scope s)
	{
		return new ScopeAdvertisement(s);
	}
	
	public static Unadvertisement getUnadvertisement(Filter f)
	{
		if (f instanceof ScopedFilter)
		{
			return getUnadvertisement((ScopedFilter)f);
		}
		if (f instanceof ScopeIdentifier)
		{
			return getUnadvertisement((ScopeIdentifier)f);
		}
		return new Unadvertisement(f);
	}
	
	public static Unadvertisement getUnadvertisement(ScopedFilter f)
	{
		if (f.getFilter() instanceof ScopeIdentifier)
		{
			return new ScopeUnadvertisement(f);
		}
		return new Unadvertisement(f);
	}

	public static ScopeUnadvertisement getUnadvertisement(ScopeIdentifier f)
	{
		return new ScopeUnadvertisement(f);
	}
	
	public static Unadvertisement getUnadvertisement(Filter f, 
			Collection<? extends Subscription> c )
	{
		Unadvertisement unadvertisement = getUnadvertisement(f);
		if (c != null)
		{
			unadvertisement.addAllUncovered(c);
		}
		return unadvertisement;
	}
}
