/*
 * $id$
 */
package rebeca.filter;

import rebeca.Event;
import rebeca.Filter;
import rebeca.event.*;
import rebeca.scope.*;
import rebeca.util.*;


/**
 * @author parzy
 *
 */
public class ScopeIdentifier extends BasicFilter
{

	private static final long serialVersionUID = 20081002073355L;
	
	protected String name;
	
	public ScopeIdentifier(Scope scope)
	{
		this(scope.getName());
	}
	
	public ScopeIdentifier(String name)
	{
		super(new UniqueName(name));
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	@Override
	public String toString()
	{
		return "Scope(" + name + ")";
	}
	
	public String getSuperscope()
	{
		int i = name.lastIndexOf('.');
		// TODO parzy replace "" with a constant
		return i>0 ? name.substring(0,i) : "";
	}

	
	/* (non-Javadoc)
	 * @see rebeca.Filter#match(rebeca.Event)
	 */
	@Override
	public boolean match(Event event) 
	{
		if ( !(event instanceof ScopeEvent))
		{
			return false;
		}
		
		ScopeEvent e = (ScopeEvent)event;
		return name.equals(e.getName());
	}

	@Override
	public boolean isOverlapping(Filter filter)
	{
		if (filter instanceof ScopeIdentifier)
		{
			return isOverlapping((ScopeIdentifier)filter);
		}
		return false;
	}
	
	public boolean isOverlapping(ScopeIdentifier ide)
	{
		// TODO parzy check whether this is sufficient
		// maybe we will need superscope also
		return name.equals(ide.getName());
	}
	
	@Override
	public boolean isIdentical(Filter filter)
	{
		if (filter instanceof ScopeIdentifier)
		{
			return isIdentical((ScopeIdentifier)filter);
		}
		return false;
	}
	
	public boolean isIdentical(ScopeIdentifier ide)
	{
		// TODO parzy check whether this is sufficient
		return name.equals(ide.getName());
	}

	// TODO parzy maybe support covering based routing
	@Override
	public boolean isCovering(Filter filter)
	{
		return false;
	}

	@Override
	public boolean isCoveredBy(Filter filter)
	{
		return false;
	}

//	@Override
//	public Filter doMerge(Filter filter)
//	{
//		return null;
//	}
	
//	@Override
//	public Filter doAnd(Filter filter)
//	{
//		return null;
//	}

	@Override
	public Filter doIntersection(Filter filter)
	{
		return null;
	}
	
//	@Override
//	public Filter doOr(Filter filter)
//	{
//		return null;
//	}

	@Override
	public Filter doUnion(Filter filter)
	{
		return null;
	}
	
	@Override
	public Filter not()
	{
		return null;
	}
}
