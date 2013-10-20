/*
 * $id$
 */
package rebeca.filter;

import rebeca.*;
import rebeca.scope.*;
import rebeca.util.*;

/**
 * @author parzy
 *
 */
public class ScopedFilter extends BasicFilter 
{
	// serial version UID
	private static final long serialVersionUID = 3673465776000149861L;

	public static enum Type { SUBSCRIPTION, ADVERTISEMENT };
	
	protected Filter filter;
	protected ScopeSet scopes;
	protected Type type;
	protected ScopeEngine engine; 
	
	protected ScopedFilter()
	{
		this(null,null,null, null);
	}
	
	public ScopedFilter(Filter filter, ScopeSet scopes)
	{
		this(filter,scopes,Type.SUBSCRIPTION, null);
	}
	
	public ScopedFilter(Filter filter, ScopeSet scopes, Type type)
	{
		this(filter,scopes,type, null);
	}
	
	public ScopedFilter(Filter filter, ScopeSet scopes, Type type, 
			            ScopeEngine engine)
	{
		this.filter = filter;
		this.scopes = scopes;
		this.type = type;
		this.engine = engine;
		if (filter instanceof BasicFilter)
		{
			this.id = ((BasicFilter)filter).id;
		}
	}
	
	public Id getId()
	{
		return id;
	}
	
	// TODO parzy this should not be public
	public void setId(Id id)
	{
		this.id = id;
	}
	
	// TODO parzy make the set unmodifiable
	public ScopeSet getScopes()
	{
		return scopes;
	}
	
	// TODO parzy return a new scoped filter instead
	public void setScopes(ScopeSet scopes)
	{
		this.scopes = scopes;
	}
	
	public Filter getFilter()
	{
		return filter;
	}
	
	public void setFilter(Filter filter)
	{
		this.filter = filter;
	}
	
	public Type getType()
	{
		return type;
	}
	
	public void setType(Type type)
	{
		this.type = type;
	}
	
	public boolean isSubscribingFilter()
	{
		return type == Type.SUBSCRIPTION;
	}
	
	public boolean isAdvertisingFilter()
	{
		return type == Type.ADVERTISEMENT;
	}

	public ScopeEngine getEngine()
	{
		return engine;
	}
	
	public void setEngine(ScopeEngine engine)
	{
		this.engine = engine;
	}
	
	
	@Override
	public String toString()
	{
		// TODO parzy remove the id from representation
		return "[" + filter + "]_" + scopes + "@" + id;
	}
	
	
	@Override
	public boolean match(Event e)
	{
		ScopeSet s = e.getScopes();
//		Filter f = scopes.upwardSubscriptionFilter(filter, s);
		Filter f = engine.upwardFilter(filter, scopes, s, type);
		if (f == null)
		{
			return false;
		}
		
//		// TODO parzy revise the transformable stuff
//		if (e instanceof Transformable)
//		{
//			e = (Event)((Transformable)e).clone();
//		}

		e = (Event)e.clone();
//		e = s.upwardEvent(e, scopes);
		e = engine.upwardEvent(e, scopes);
		if (e == null)
		{
			return false;
		}
		
		return f.match(e);
	}

	@Override
	public boolean isOverlapping(Filter filter)
	{
		if (! (filter instanceof ScopedFilter))
		{
			filter = new ScopedFilter(filter, new BasicScopeSet(),
					type == Type.ADVERTISEMENT ? Type.SUBSCRIPTION : 
					                             Type.ADVERTISEMENT);
		}
		return isOverlapping((ScopedFilter)filter);
	}
	
	public boolean isOverlapping(ScopedFilter filter)
	{
//		if (engine==null)
//		{
//			System.out.println("foobar");
//		}
//		System.out.println("this: "+this);
//		System.out.println("filter: "+filter);
//		System.out.println("engine: "+engine);
		// transform both filters to the least common superscope
		Filter f = engine.upwardFilter(this.filter, this.scopes, 
				                       filter.scopes, this.type);
		if (f == null)
		{
			return false;
		}
		Filter g = engine.upwardFilter(filter.filter, filter.scopes, 
                                       this.scopes, filter.type);
		if (g == null)
		{
			return false;
		}
		// original overlapping test
		return f.overlaps(g);
	}
	
	@Override
	public boolean isIdentical(Filter filter)
	{
		if (! (filter instanceof ScopedFilter))
		{
			filter = new ScopedFilter(filter,new BasicScopeSet(),type);
		}
		return isIdentical((ScopedFilter)filter);
	}
	
	public boolean isIdentical(ScopedFilter filter)
	{
		Filter f = this.filter;
		ScopeSet s = this.scopes;
		Filter g = filter.filter;
		ScopeSet t = filter.scopes;

		return s.identical(t) && f.identical(g);
	}

	@Override
	public boolean isCovering(Filter filter)
	{
		if (! (filter instanceof ScopedFilter))
		{
			filter = new ScopedFilter(filter,new BasicScopeSet(),type);
		}
		return isCovering((ScopedFilter)filter);
	}
	
	@Override
	public boolean isCoveredBy(Filter filter)
	{
		if (! (filter instanceof ScopedFilter))
		{
			filter = new ScopedFilter(filter,new BasicScopeSet(),type);
		}
		return ((ScopedFilter)filter).isCovering(this);
	}
	
	public boolean isCovering(ScopedFilter filter)
	{
		// subscopes cover superscopes
		if (!filter.scopes.superscopeSet(this.scopes))
		{
			return false;
		}
		// transform both filters to the least common superscope
		Filter f = engine.upwardFilter(this.filter, this.scopes, 
                filter.scopes, this.type);
		if (f == null)
		{
			return false;
		}
		Filter g = engine.upwardFilter(filter.filter, filter.scopes, 
                this.scopes, filter.type);
		if (g == null)
		{
			return false;
		}
		
		return f.covers(g);
	}
		
	@Override
	public Filter doIntersection(Filter filter)
	{
		if ( (filter instanceof ScopedFilter))
		{
			return doIntersection((ScopedFilter)filter);
		}

		Filter f = this.filter.intersection(filter);
		if (f != null)
		{
			ScopeSet s = new BasicScopeSet(scopes);
			return new ScopedFilter(f,s,type);
		}
		return null;
	}
	
	public Filter doIntersection(ScopedFilter filter)
	{
		if ( this.scopes.identical(filter.scopes) && this.type == filter.type )
		{
			Filter f = this.filter.intersection(filter);
			if (f != null)
			{
				ScopeSet s = new BasicScopeSet(scopes);
				return new ScopedFilter(f,s,type);
			}
		}
		return null;
	}
	
	
	@Override
	public Filter doAnd(Filter filter)
	{
		if ( (filter instanceof ScopedFilter))
		{
			return doAnd((ScopedFilter)filter);
		}

		Filter f = this.filter.and(filter);
		if (f != null)
		{
			ScopeSet s = new BasicScopeSet(scopes);
			return new ScopedFilter(f,s,type);
		}
		throw new IllegalArgumentException("filters could not be anded");
	}
	
	public Filter doAnd(ScopedFilter filter)
	{
		if ( this.scopes.identical(filter.scopes) && this.type == filter.type )
		{
			Filter f = this.filter.and(filter);
			if (f != null)
			{
				ScopeSet s = new BasicScopeSet(scopes);
				return new ScopedFilter(f,s,type);
			}
		}
		throw new IllegalArgumentException("filters could not be anded");
	}
	
	@Override
	public Filter doUnion(Filter filter)
	{
		if ( !(filter instanceof ScopedFilter))
		{
			filter = new ScopedFilter(filter,scopes,type);
		}
		return doUnion((ScopedFilter)filter);
	}
	
	public Filter doUnion(ScopedFilter filter)
	{
		if ( !this.scopes.identical(filter.scopes) || this.type != filter.type)
		{
			return null;
		}

		Filter f = this.filter.union(filter.filter);
		if (f == null)
		{
			return null;
		}
		
		return new ScopedFilter(f,new BasicScopeSet(scopes),type);
	}	
	
	@Override
	public Filter doOr(Filter filter)
	{
		if ( (filter instanceof ScopedFilter))
		{
			return doOr((ScopedFilter)filter);
		}

		Filter f = this.filter.or(filter);
		if (f != null)
		{
			ScopeSet s = new BasicScopeSet(scopes);
			return new ScopedFilter(f,s,type);
		}
		
		throw new IllegalArgumentException("filters could not be ored");
	}
	
	public Filter doOr(ScopedFilter filter)
	{
		if ( this.scopes.identical(filter.scopes) && this.type == filter.type )
		{
			Filter f = this.filter.or(filter);
			if (f != null)
			{
				ScopeSet s = new BasicScopeSet(scopes);
				return new ScopedFilter(f,s,type);
			}
		}
		throw new IllegalArgumentException("filters could not be ored");
	}

	@Override
	public Filter not()
	{
		Filter f = filter.not();
		ScopeSet s = new BasicScopeSet(scopes);
		return new ScopedFilter(f,s,type);
	}
	
	
	// Filter upwarding stuff
	public Filter upward(ScopeSet set)
	{
		return engine.upwardFilter(this, set);
	}
	
	public Filter upward(String name)
	{
		return engine.upwardFilter(this, name);
	}
}
