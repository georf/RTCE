/*
 * $id$
 */
package rebeca.event;

import org.apache.log4j.Logger;

import rebeca.*;
import rebeca.filter.*;
import rebeca.scope.*;

/**
 * @author parzy
 *
 */
public class ScopeAdvertisement extends Advertisement 
{
	/**
	 * Version id for serialization. 
	 */
	private static final long serialVersionUID = 20090318092957L;
	
	/** 
	 * Logger for scope advertisements. 
	 */
	private static final Logger LOG = Logger.getLogger(ScopeAdvertisement.class);
	
	Scope scope;

	// constructors -----------------------------------------------------------
	// ------------------------------------------------------------------------
	
	ScopeAdvertisement(ScopedFilter filter)
	{	
		if ( !(filter.getFilter() instanceof ScopeIdentifier))
		{
			throw new IllegalArgumentException();
		}
		ScopeIdentifier ide = (ScopeIdentifier)filter.getFilter();
		ScopeEngine engine = filter.getEngine();
		Scope scope = engine.getScope(ide.getName());
		if (scope == null)
		{
			throw new IllegalStateException();
		}
		this.scope = scope;
		this.filter = filter;
		this.scopes = new BasicScopeSet(filter.getScopes());
	}

	// please notice that the ScopedFilter is not associated to a scope engine yet
	public ScopeAdvertisement(Scope scope)
	{
		ScopeSet set = new BasicScopeSet();
		set.add(scope.getName());
		this.scope = scope;
		this.filter = new ScopedFilter(new ScopeIdentifier(scope), set);
		this.scopes = new BasicScopeSet(set);
	}
	
	
//	public ScopeAdvertisement(Scope scope)
//	{
//		// super(null);
//		
//		// set scope definition
//		this.scope = scope;
//
//		// set correct scopes
//		String name = scope.getName();
//		ScopeSet scopes = new BasicScopeSet();
//		scopes.add(name);
//		setScopes(scopes);
//
//		// set correct filter
//		ScopeIdentifier ide = new ScopeIdentifier(name);
//		Filter filter = new ScopedFilter(ide, scopes, ScopedFilter.Type.ADVERTISEMENT);
//		setFilter(filter);
//	}
	
	public Scope getScope()
	{
		return scope;
	}
	
	public String getScopeName()
	{
		return scope.getName();
	}
	
	@Override
	public String toString()
	{
		return "ScopeAdvertisement(" + filter + ")";
	}
	
	public ScopeIdentifier getIdentifier()
	{
		// TODO parzy this should not fail but a more sophisticated handling is appreciated
		return (ScopeIdentifier)((ScopedFilter)filter).getFilter();
	}
}
