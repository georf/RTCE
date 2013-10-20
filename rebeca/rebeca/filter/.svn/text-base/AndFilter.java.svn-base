/*
 * $Id: AndFilter.java 2086 2010-10-20 17:34:30Z parzy $
 */
package rebeca.filter;

import java.util.*;
import rebeca.Event;
import rebeca.Filter;

/**
 * @author parzy
 *
 */
public class AndFilter extends BasicFilter 
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1610239106881948164L;
	protected ArrayList<Filter> filters;

	public AndFilter()
	{
		filters = new ArrayList<Filter>();
	}
	
	public AndFilter(AttributeFilter f)
	{
		// TODO parzy check args carefully
		this();
		filters.add(f);
	}
	
	public AndFilter(AttributeFilter f, AttributeFilter g)
	{
		// TODO parzy check args carefully
		this();
		filters.add(f);
		filters.add(g);
	}

	
	/**
	 * Tests if this filter instance matches the provided event.
	 * An AndFilter f matches the Event e, iff each constituent filter 
	 * of f matches e.
	 * 
	 * @param e the event to match
	 * @return true if this filter instance matches e
	 */
	public boolean match(Event e)
	{
		// each filter has to match e 
		for (Filter f : filters)
		{
			if ( !f.match(e) )
			{
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Tests if this filter instance and the provided filter overlap each other.
	 * An AndFilters f and an AttributeFilter g are NOT overlapping, if at least 
	 * one of the constituent filters of f does not overlap with g. Otherwise 
	 * f and g overlap each other, that is, there are possibly events which are 
	 * matched by f and g at the same time.
	 * 
	 * @param f the filter to test for overlapping
	 * @return true if both filters are overlapping
	 */
	public boolean isOverlapping(AttributeFilter f)
	{
		for (Filter g : filters)
		{
			if ( !g.overlaps(f))
			{
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Tests if this filter instance and the provided filter overlap each other.
	 * Two AndFilters f and g are NOT overlapping, if at least one combination 
	 * of two constituent filters of f and g, respectively, exists which is not 
	 * overlapping.  Otherwise f and g overlap each other, that is, there are 
	 * possibly events which are matched by f and g at the same time.
	 * 
	 * @param f the filter to test for overlapping
	 * @return true if both filters are overlapping
	 */
	public boolean isOverlapping(AndFilter f)
	{
		for (Filter g : filters)
		{
			for (Filter h : f.filters)
			{
				if ( !g.overlaps(h) )
				{
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * Tests if this filter instance is identical to the provided filter.
	 * Two AndFilters f and g are identical, if their constituent filters are
	 * identical.  In that case both filters match the same events. 
	 * 
	 * @param f the filter to test for identity
	 * @return true if both filters are identical
	 */
	public boolean isIdentical(AndFilter f)
	{
		// first, both filters must have the same number of constituent filters
		if (filters.size() != f.filters.size())
		{
			return false;
		}
		
		// second, for each constituent filter we have to find an identical one in f 
		outer:
		for (Filter g : filters)
		{
			for (Filter h : f.filters)
			{
				if ( g.identical(h))
				{
					continue outer;
				}
			}
			return false;
		}
		return true;
	}

	/**
	 * Tests if this filter instance covers the provided filter.
	 * An AndFilter f covers an AttributeFilter g, if every constituent filter 
	 * of f covers g.  In that case, every event matched by g is also matched 
	 * by f.
	 * 
	 * @param f the filter to test for coverage
	 * @return true if this filter instance covers the provided filter
	 */
	public boolean isCovering(AttributeFilter f)
	{
		for (Filter g : filters)
		{
			if (!g.covers(f))
			{
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Tests if this filter instance if covered by the provided filter.
	 * An AndFilter f is covered by an AttributeFilter g, if g covers every 
	 * constituent filter of f.  
	 * In that case, every event matched by f is also matched by g.
	 * @param f
	 * @return
	 */
	public boolean isCoveredBy(AttributeFilter f)
	{
		for (Filter g : filters)
		{
			if (!f.covers(g))
			{
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Tests if this filter instance covers the provided filter.
	 * An AndFilter f covers an AndFilter g, if every constituent filter of f
	 * covers at least one constituent filter of g.  In that case, every event
	 * matched by g is also matched by f.
	 * 
	 * @param f the filter to test for coverage
	 * @return true if this filter instance covers the provided filter
	 */
	public boolean isCovering(AndFilter f)
	{
		outer:
		for (Filter g : filters)
		{
			for (Filter h : f.filters)
			{
				if (g.covers(h))
				{
					continue outer;
				}
			}
			return false;
		}
		return true;
	}
	
	/**
	 * Tests if this filter instance is covered by the provided filter.
	 * An AndFilter f is covered by an AndFilter g, if every constituent filter 
	 * of g covers at least one constituent filter of f.  In that case, every 
	 * event matched by f is also matched by g.
	 * 
	 * @param f the probably covering filter
	 * @return true if this filter instance is covered by the provided filter
	 */
	public boolean isCoveredBy(AndFilter f)
	{
		// AndFilter which will handle the coverage test for sure
		return f.isCovering(this);
	}
	
	/**
	 * Tries to perfectly merge this filter instance with the provided filter.
	 * Two AndFilters can be perfectly merged, if all their constituent filters
	 * are pairwise identical, except one pair that can be merged.
	 * 
	 * @param f the filter to merge with
	 * @return a new AndFilter if merging both filters was successful, 
	 *         null otherwise.
	 */
	public Filter doMerge(AndFilter f)
	{
		// if one filter covers the other, we are done :-)
		if (this.isCovering(f))
		{
			return this;
		}
		if (f.isCovering(this))
		{
			return f;
		}
		// to be perfectly mergeable, both filters must have the same number 
		// of constituent filters
		if (filters.size() != f.filters.size())
		{
			return null;
		}
		// furthermore, the constituent filters must be pairwise identical, 
		// except one pair which can be merges
		LinkedList<Filter> k = new LinkedList<Filter>(filters);
		LinkedList<Filter> l = new LinkedList<Filter>(f.filters);
		int counter = 0;
		outer:
		for (ListIterator<Filter> it = k.listIterator(); it.hasNext(); )
		{
			Filter g = it.next();
			for (ListIterator<Filter> jt = l.listIterator(); jt.hasNext(); )
			{
				Filter h = jt.next();
				if (g.identical(h))
				{
					it.remove();
					jt.remove();
					continue outer;
				}
			}
			// should only be passed once (in case of the mergeable pair)
			if (counter++ >= 1)
			{
				return null;
			}
		}
		// exactly one mergeable pair of filters should be left
		if (k.size() != 1)
		{
			return null;
		}
		// merge it
		Filter g = k.remove();
		Filter h = l.remove();
		Filter m = g.merge(h);
		if (m == null)
		{
			return null;
		}
		// and construct the new AndFilter
		AndFilter rst = new AndFilter();
		rst.filters = new ArrayList<Filter>(filters);
		rst.filters.set(rst.filters.indexOf(g), m);
		return rst;
	}

	/**
	 * Constructs a new AndFilter representing the conjunction of this filter
	 * instance with the provided filter.  Thereby, constituent filters are
	 * combined when possible.
	 * 
	 * @param f filter for conjunction
	 * @return a new AndFilter representing the conjunction of this filter
	 *         instance with the provided filter.
	 */
	public Filter doIntersection(AndFilter f)
	{
		// instead of simply appending f's constituent filters, we look for
		// pairs of constituent filters that can be conflated by conjunction
		ArrayList<Filter> l = new ArrayList<Filter>(filters);
		outer:
		for (Filter g : f.filters)
		{
			// try to and a pair of constituent filters
			for (int i=0; i<filters.size(); i++)
			{
				// TODO parzy guarantee that no new AndFilter is created by g.and(h)
				Filter h = l.get(i);
				Filter a = g.intersection(h);
				if (a != null)
				{
					l.set(i, a);
					continue outer;
				}
			}
			// otherwise simply add them
			l.add(g);
		}
		// create the resulting AndFilter
		AndFilter rst = new AndFilter();
		rst.filters = l;
		return rst;
	}
	
	/**
	 * Tries to perfectly merge this filter instance with the provided filter.
	 * Two AndFilters can be perfectly merged, if all their constituent filters
	 * are pairwise identical, except one pair that can be merged.
	 * 
	 * @param f the filter to merge with
	 * @return a new AndFilter if merging both filters was successful, 
	 *         null otherwise.
	 */
	public Filter doUnion(AndFilter f)
	{
		// if one filter covers the other, we are done :-)
		if (this.isCovering(f))
		{
			return this;
		}
		if (f.isCovering(this))
		{
			return f;
		}
		// to be perfectly mergeable, both filters must have the same number 
		// of constituent filters
		if (filters.size() != f.filters.size())
		{
			return null;
		}
		// furthermore, the constituent filters must be pairwise identical, 
		// except one pair which can be merges
		LinkedList<Filter> k = new LinkedList<Filter>(filters);
		LinkedList<Filter> l = new LinkedList<Filter>(f.filters);
		int counter = 0;
		outer:
		for (ListIterator<Filter> it = k.listIterator(); it.hasNext(); )
		{
			Filter g = it.next();
			for (ListIterator<Filter> jt = l.listIterator(); jt.hasNext(); )
			{
				Filter h = jt.next();
				if (g.identical(h))
				{
					it.remove();
					jt.remove();
					continue outer;
				}
			}
			// should only be passed once (in case of the mergeable pair)
			if (counter++ >= 1)
			{
				return null;
			}
		}
		// exactly one mergeable pair of filters should be left
		if (k.size() != 1)
		{
			return null;
		}
		// merge it
		Filter g = k.remove();
		Filter h = l.remove();
		Filter m = g.merge(h);
		if (m == null)
		{
			return null;
		}
		// and construct the new AndFilter
		AndFilter rst = new AndFilter();
		rst.filters = new ArrayList<Filter>(filters);
		rst.filters.set(rst.filters.indexOf(g), m);
		return rst;
	}
	
	
//	/**
//	 * Constructs a new Filter representing the disjunction of this filter
//	 * instance with the provided AndFilter.  First, the method attempts to 
//	 * merge the filters perfectly. If this fails, a new OrFilter is 
//	 * constructed.
//	 * 
//	 * @param f filter for disjunction
//	 * @param ttl ignored as AndFilters are handled appropriately
//	 * @return a new Filter (in most cases an OrFilter) representing the 
//	 *         disjunction of this filter instance with the provided filter.
//	 */
//	public Filter doOr(AndFilter f, int ttl)
//	{
//		// first, try to merge; alternatively create a new OrFilter
//		Filter m = merge(f);
//		return m == null ? new OrFilter(this,f) : m;
//		
//	}

	/**
	 * Constructs a new Filter representing the opposite/negation of this 
	 * filter instance. 
	 * 
	 * @return a new Filter (in most cases an OrFilter) representing the 
	 *         negation of this filter instance.
	 */
	public Filter not()
	{
		// negate each constituent filter and 'or' them afterwards
		Filter rst = null;
		for (Filter f : filters)
		{
			rst = rst == null ? f.not() : rst.or(f.not());
		}
		return rst;
	}
	
	/**
	 * Creates a textual representation of this filter instance.
	 * @return a textual representation of this filter instance.
	 */
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(filters.size()>1?"(":"");
		for (Iterator<Filter> it = filters.iterator(); it.hasNext(); )
		{
			sb.append(it.next());
			sb.append(it.hasNext()?" & ":"");
		}
		sb.append(filters.size()>1?"":")");
		return sb.toString();
	}
	
}
