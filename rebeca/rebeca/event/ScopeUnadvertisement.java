/*
 * $Id$
 */
package rebeca.event;

import org.apache.log4j.Logger;

import rebeca.filter.*;
import rebeca.scope.BasicScopeSet;
import rebeca.scope.Scope;
import rebeca.scope.ScopeSet;

/**
 * @author parzy
 *
 */
public class ScopeUnadvertisement extends Unadvertisement 
{
	/**
	 * Version id for serialization. 
	 */
	private static final long serialVersionUID = 20090318092933L;
	
	/** 
	 * Logger for scope unadvertisements. 
	 */
	private static final Logger LOG = Logger.getLogger(ScopeUnadvertisement.class);
	
	
	// constructors -----------------------------------------------------------
	// ------------------------------------------------------------------------
	
	ScopeUnadvertisement(String name)
	{
		this(new ScopeIdentifier(name));
	}
	
	ScopeUnadvertisement(Scope scope)
	{
		this(new ScopeIdentifier(scope));
	}
	
	// please notice that the ScopedFilter is not associated to a scope engine yet
	ScopeUnadvertisement(ScopeIdentifier ide)
	{	
		ScopeSet set = new BasicScopeSet();
		set.add(ide.getName());
		this.filter = new ScopedFilter(ide, set);
		this.scopes = new BasicScopeSet(set);
	}
	
	ScopeUnadvertisement(ScopedFilter filter)
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
		return "ScopeUnadvertisement(" + filter + ")";
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
