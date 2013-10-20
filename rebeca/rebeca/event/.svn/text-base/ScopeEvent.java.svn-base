/*
 * $id$
 */
package rebeca.event;

import java.util.*;

import rebeca.*;
import rebeca.filter.*;
import rebeca.scope.*;

/**
 * @author parzy
 *
 */
// TODO parzy this class was once abstract => check what the intention was
public abstract class ScopeEvent extends Event 
{
	private static final long serialVersionUID = 20081001093615L;
	
	protected final ScopeIdentifier identifier;
	protected final boolean approval;	
	protected final Collection<Scope> subscopes;
	
	protected ScopeEvent(ScopeIdentifier identifier, boolean approval)
	{	
		// set identifier and type
		this.identifier = identifier;
		this.approval = approval;
		this.subscopes = new ArrayList<Scope>();
		// and assign appropriate scope set
		ScopeSet scopes = new BasicScopeSet();
		scopes.add(identifier.getName());
		setScopes(scopes);
	}

	public String getName()
	{
		return identifier.getName();
	}
	
	public ScopeIdentifier getIdentifier()
	{
		return identifier;
	}
	
	public boolean isApproval()
	{
		return approval;
	}
	
	public boolean isDenial()
	{
		return !approval;
	}
	
	/**
	 * @return the subscopes
	 */
	public Collection<Scope> getSubscopes() 
	{
		return subscopes;
	}
	
//	/**
//	 * 
//	 * @param scopes
//	 */
//	// TODO parzy do we need modifiable subscopes?
//	public void setSubscopes(Collection<Scope> scopes)
//	{
//		subscopes = scopes;
//	}
	
	// visualization ----------------------------------------------------------
	// ------------------------------------------------------------------------
	
	@Override
	public String toString()
	{
		return "Scope" + (approval ? "Approval" : "Denial") + "(" + getName() 
			 + ")";
	}
}
