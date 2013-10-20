/*
 * $Id$
 */
package rebeca.event;

import rebeca.*;
import rebeca.filter.*;
import rebeca.scope.*;


/**
 * @author parzy
 *
 */
public class Subscription extends Event 
{
	private static final long serialVersionUID = 20080921083928L;
	
	protected Filter filter;

	protected Subscription()
	{
		this(null);
	}
	
	Subscription(Filter filter)
	{
		this.filter = filter;
		if (filter instanceof ScopedFilter)
		{
			this.scopes = ((ScopedFilter)filter).getScopes();
		}
		
		
//		// FIXME parzy decide what to do?
//		this.id = filter.getId(); 
	}

	
	Subscription(Filter filter, ScopeSet scopes)
	{
		super(scopes);
		if (! (filter instanceof ScopedFilter))
		{
			filter = new ScopedFilter(filter,scopes);
		}
		
		// TODO parzy maybe the following line will cause trouble in future 
		((ScopedFilter)filter).setScopes(scopes);
		this.filter = filter;
		
//		// FIXME parzy decide what to do?
//		this.id = filter.getId();
		
	}

	@Override
	public Object clone()
	{
		Subscription clone = (Subscription)super.clone();
		clone.filter = (Filter)filter.clone();
		return clone;
	}
	
	public Filter getFilter()
	{
		return filter;
	}	
	
	public void setFilter(Filter filter)
	{
		this.filter = filter;
	}
	
//	// TODO parzy remove it later
//	public Scope getScope()
//	{
//		return null;
//	}
	
	@Override
	public String toString()
	{
		return "Subscription(" + filter + ")@" + id;
	}
}
