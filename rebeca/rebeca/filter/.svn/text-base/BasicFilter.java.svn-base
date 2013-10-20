/*
 * $Id$
 */
package rebeca.filter;

import java.io.*;
import rebeca.Event;
import rebeca.Filter;
import rebeca.util.*;

/**
 * @author parzy
 *
 */
public class BasicFilter implements Cloneable, Filter, Serializable
{
	// serial version UID
	private static final long serialVersionUID = 20090630213110L;
	
	protected Id id;
	
	public BasicFilter()
	{
//		id = Rebeca.idFactory.create();
		this(new RandomId());
	}
	
	protected BasicFilter(Id id)
	{
		this.id = id;
	}
	
	public Object clone()
	{
		try
		{
			return super.clone();
		}
		// never happens
		catch (CloneNotSupportedException e)
		{
			return null;
		}
	}
	
//	public Id getId()
//	{
//		return id;
//	}
//	
//	public void setId(Id id)
//	{
//		this.id = id;
//	}
	
	public boolean match(Event e)
	{
		return true;
	}
	
	
	public boolean overlaps(Filter f)
	{
		return FilterDispatcher.overlaps(this, f);
	}
	
	public boolean isOverlapping(Filter f)
	{
		return true;
	}
	
	public boolean identical(Filter f)
	{
		return FilterDispatcher.identical(this, f);
	}
	
	public boolean isIdentical(Filter f)
	{
		return false;
	}
	
	public boolean covers(Filter f)
	{
		return FilterDispatcher.covers(this, f);
	}
	
	public boolean isCovering(Filter f)
	{
		return false;
	}
	
	public boolean isCoveredBy(Filter f)
	{
		return false;
	}

	
	public Filter merge(Filter f)
	{
		return union(f);
	}
	
	
	public Filter intersection(Filter f)
	{
		return FilterDispatcher.intersection(this, f);
	}
	public Filter doIntersection(Filter f)
	{
		return null;
	}
	public Filter and(Filter f)
	{
		return FilterDispatcher.and(this, f);
	}
	public Filter doAnd(Filter f)
	{
		throw new IllegalStateException();
	}
	
	
	public Filter union(Filter f)
	{
		return FilterDispatcher.union(this, f);
	}

	public Filter doUnion(Filter f)
	{
		return null;
	}
	
	public Filter or(Filter f)
	{
		return FilterDispatcher.or(this, f);
	}
	public Filter doOr(Filter f)
	{
		throw new IllegalStateException();
	}
	
	
	// TODO parzy Maybe make this class abstract?
	public Filter not()
	{
		return null;
	}
	
	@Override
	public int hashCode()
	{
		return id.hashCode();
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if ( !(obj instanceof BasicFilter))
		{
			return false;
		}
		
		BasicFilter f = (BasicFilter)obj;
		return id.equals(f.id);
	}
	
	
	// optional methods -------------------------------------------------------
	// ------------------------------------------------------------------------
	
	public Id getId()
	{
		return id;
	}
	
	public void setId(Id id)
	{
		throw new UnsupportedOperationException();
	}
}
