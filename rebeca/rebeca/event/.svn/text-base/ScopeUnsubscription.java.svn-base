/*
 * $id$
 */
package rebeca.event;

import org.apache.log4j.Logger;

import rebeca.filter.*;
import rebeca.scope.*;

/**
 * @author parzy
 *
 */
public class ScopeUnsubscription extends Unsubscription 
{
	/**
	 * Version id for serialization. 
	 */
	private static final long serialVersionUID = 20090318092933L;
	
	/** 
	 * Logger for scope unsubscriptions. 
	 */	
	private static final Logger LOG = Logger.getLogger(ScopeUnsubscription.class);
	
	// constructors -----------------------------------------------------------
	// ------------------------------------------------------------------------
	
	ScopeUnsubscription(String name)
	{
		this(new ScopeIdentifier(name));
	}
	
	ScopeUnsubscription(Scope scope)
	{
		this(new ScopeIdentifier(scope));
	}
	
	// please notice that the ScopedFilter is not associated to a scope engine yet
	ScopeUnsubscription(ScopeIdentifier ide)
	{	
		ScopeSet set = new BasicScopeSet();
		set.add(ide.getName());
		this.filter = new ScopedFilter(ide, set);
		this.scopes = new BasicScopeSet(set);
	}
	
	ScopeUnsubscription(ScopedFilter filter)
	{	
		if ( !(filter.getFilter() instanceof ScopeIdentifier))
		{
			throw new IllegalArgumentException();
		}
		this.filter = filter;
		this.scopes = new BasicScopeSet(filter.getScopes());
	}
	
//	ScopeUnsubscription(ScopeIdentifier ide)
//	{
//		// create unsubscription
//		super(ide);
//		// and assign appropriate scope set
//		ScopeSet scopes = new BasicScopeSet();
//		scopes.add(ide.getName());
//		setScopes(scopes);
//	}
	
	@Override
	public String toString()
	{
		return "ScopeUnsubscription(" + filter + ")";
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
