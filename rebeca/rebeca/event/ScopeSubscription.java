/*
 * $Id$
 */
package rebeca.event;

import org.apache.log4j.*;

import rebeca.*;
import rebeca.Filter;
import rebeca.filter.*;
import rebeca.scope.*;

/**
 * @author parzy
 *
 */
public class ScopeSubscription extends Subscription 
{
	/**
	 * Version id for serialization. 
	 */
	private static final long serialVersionUID = 20090318091829L;
	
	/** 
	 * Logger for scope subscriptions. 
	 */
	private static final Logger LOG = Logger.getLogger(ScopeSubscription.class);
	
	// constructors -----------------------------------------------------------
	// ------------------------------------------------------------------------
	
	ScopeSubscription(String name)
	{
		this(new ScopeIdentifier(name));
	}
	
	ScopeSubscription(Scope scope)
	{
		this(new ScopeIdentifier(scope));
	}
	
	// please notice that the ScopedFilter is not associated to a scope engine yet
	ScopeSubscription(ScopeIdentifier ide)
	{	
		ScopeSet set = new BasicScopeSet();
		set.add(ide.getName());
		this.filter = new ScopedFilter(ide, set);
		this.scopes = new BasicScopeSet(set);
	}
	
	ScopeSubscription(ScopedFilter filter)
	{	
		if ( !(filter.getFilter() instanceof ScopeIdentifier))
		{
			throw new IllegalArgumentException();
		}
		this.filter = filter;
		this.scopes = new BasicScopeSet(filter.getScopes());
	}
	
	@Override
	public String toString()
	{
		return "ScopeSubscription(" + filter + ")";
	}
	
	public ScopeIdentifier getIdentifier()
	{
		// TODO parzy this should not fail but a more sophisticated handling is appreciated
		return (ScopeIdentifier)((ScopedFilter)filter).getFilter();
	}

	// TODO parzy this should not fail but a more sophisticated handling is appreciated
	public String getScopeName()
	{
		return getIdentifier().getName();
	}
}
