/*
 * $id$
 */
package rebeca.event;

import java.util.*;
import rebeca.filter.*;
import rebeca.scope.*;
import rebeca.util.Id;

/**
 * @author parzy
 *
 */
public class ScopeApproval extends ScopeEvent 
{
	private static final long serialVersionUID = 20090602144912L;
		
	public ScopeApproval(ScopeIdentifier identifier)
	{
		super(identifier,true);
	}

	public ScopeApproval(ScopeIdentifier identifier, Collection<Scope> subscopes)
	{
		this(identifier);
		this.subscopes.addAll(subscopes);
	}	
	
	public ScopeApproval(ScopeIdentifier identifier, Collection<Scope> subscopes, Id id)
	{
		this(identifier, subscopes);
		this.id = id;
	}	
}
