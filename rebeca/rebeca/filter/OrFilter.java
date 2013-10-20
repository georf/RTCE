/*
 * $Id: OrFilter.java 2086 2010-10-20 17:34:30Z parzy $
 */
package rebeca.filter;

import java.util.*;

import rebeca.*;

/**
 * @author parzy
 *
 */
public class OrFilter extends BasicFilter implements Filter 
{
	private static final long serialVersionUID = 7238017943082812914L;

	protected ArrayList<Filter> filters;
	
	public OrFilter()
	{
		filters = new ArrayList<Filter>();
	}

	public OrFilter(AndFilter f, AndFilter g)
	{
		this();
		filters.add(f);
		filters.add(g);
	}
	
//	public OrFilter(Filter f, Filter g)
//	{
//		this();
//		filters.add(f);
//		filters.add(g);
//	}
	
	/**
	 * Tests if this filter instance matches the provided event.
	 * An OrFilter f matches the Event e, iff at least one constituent filter 
	 * of f matches e.
	 * 
	 * @param e the event to match
	 * @return true if this filter instance matches e
	 */
	public boolean match(Event e)
	{
		// one filter has to match e 
		for (Filter f : filters)
		{
			if ( f.match(e) )
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Constructs a new Filter representing the conjunction of this filter
	 * instance with the provided OrFilter. 
	 * 
	 * @param f filter for conjunction
	 * @return a new Filter (in most cases an OrFilter) representing the 
	 *         conjunction of this filter instance with the provided filter.
	 */
	public Filter doAnd(OrFilter f)
	{
		// 'and' every constituent filter of this instance with every other 
		// constituent filter of f
		Filter rst = null;
		for (Filter g : filters)
		{
			for (Filter h : f.filters)
			{
				rst = rst == null ? g.and(h) : rst.or(g.and(h));
			}
		}
		return rst;
	}
	
	/**
	 * Constructs a new Filter representing the conjunction of this filter
	 * instance with the provided OrFilter. 
	 * 
	 * @param f filter for conjunction
	 * @return a new Filter (in most cases an OrFilter) representing the 
	 *         conjunction of this filter instance with the provided filter.
	 */
	public Filter doIntersection(OrFilter f)
	{
		// 'and' every constituent filter of this instance with every other 
		// constituent filter of f
		Filter rst = null;
		for (Filter g : filters)
		{
			for (Filter h : f.filters)
			{
				rst = rst == null ? g.and(h) : rst.or(g.and(h));
			}
		}
		return rst;
	}
	
	
	/**
	 * Constructs a new Filter representing the disjunction of this filter
	 * instance with the provided OrFilter.  Thereby, constituent filters are
	 * combined when possible.
	 * 
	 * @param f filter for disjunction
	 * @return a new Filter (in most cases an OrFilter) representing the 
	 *         disjunction of this filter instance with the provided filter.
	 */
	public Filter doOr(OrFilter f)
	{
		// try to merge every constituent filter of this instance with every 
		// other constituent filter of f
		ArrayList<Filter> l = new ArrayList<Filter>(filters);
		for (Filter g : f.filters)
		{
			for (ListIterator<Filter> it = l.listIterator(); it.hasNext(); )
			{
				Filter m = g.merge(it.next());
				if (m != null)
				{
					g = m;
					it.remove();
					// maybe the merged filter can be merged again!
					it = l.listIterator();
				}
			}
			l.add(g);
		}
		// we need at least two constituent filters for a valid OrFilter
		if (l.size() == 1)
		{
			return l.remove(0);
		}
		// create the resulting filter
		OrFilter rst = new OrFilter();
		rst.filters = l;
		return rst;
	}

	/**
	 * Constructs a new Filter representing the opposite/negation of this 
	 * filter instance. 
	 * 
	 * @return a new Filter (in most cases an AndFilter) representing the 
	 *         negation of this filter instance.
	 */
	public Filter not()
	{
		// negate all constituent filters and 'and' them
		Filter rst = null;
		for (Filter f : filters)
		{
			rst = rst == null ? f.not() : rst.and(f.not());
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
			sb.append(it.hasNext()?" | ":"");
		}
		sb.append(filters.size()>1?"":")");
		return sb.toString();
	}
}

