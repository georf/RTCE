/*
 * $Id$
 */
package rebeca.filter;

import rebeca.Filter;


/**
 * @author parzy
 *
 */
public class IntervalFilter extends BasicAttributeFilter 
{
	/**
	 * Version info for serialized instances.
	 */
	private static final long serialVersionUID = 5130563560825780941L;

	protected IntervalList intervals;
	
	public IntervalFilter(String a)
	{
		this(a, new Interval());
	}
	
	public IntervalFilter(String a, Interval i)
	{	
		this(a, new IntervalList());
		if (i == null)
		{
			throw new NullPointerException();
		}
		intervals.add(i);
	}
	
	public IntervalFilter(String a, IntervalList l)
	{
		super(a);
		if (l == null)
		{
			throw new NullPointerException();
		}
		intervals = l; 
	}
	
	//
	// ------------------------------------------------------------------------

	
	
	@Override
	@SuppressWarnings("unchecked")
	public boolean match(Object obj)
	{
		if ( !(obj instanceof Comparable) )
		{
			return false;
		}
		//System.out.println("called with " + obj);
		return intervals.match( (Comparable)obj );
	}
	
	/**
	 * Tests if this instance overlaps the given interval filter. 
	 * Two interval filters are overlapping, iff they constrain the same 
	 * attribute and their intervals are overlapping. 
	 * @param f another interval filter to test for overlapping.
	 * @return true, if both filters are overlapping, otherwise false.
	 */
	public boolean isOverlapping(IntervalFilter f)
	{
		if ( !(getAttribute().equals(f.getAttribute())))
		{
			return true;
		}
		return this.intervals.overlaps(f.intervals);
	}
	
	/**
	 * Tests if this instance is identical to the given interval filter. 
	 * Two Interval filters are identical, iff they constrain the same 
	 * attribute and their intervals are pairwise identical.
	 * @param f another interval filter to test for identity.
	 * @return true, if both filters are identical, otherwise false.
	 */
	public boolean isIdentical(IntervalFilter f)
	{
		if ( !(getAttribute().equals(f.getAttribute())))
		{
			return false;
		}
		return this.intervals.identical(f.intervals);
	}

	/**
	 * Tests if this filter instance covers the given interval filter. 
	 * An interval filter f covers an interval filter g, iff both constrain 
	 * the same attribute and f's intervals cover g's intervals.
	 * @param f another interval filter to test for coverage.
	 * @return true, if this instance covers the given interval filter, 
	 *         otherwise false.
	 */
	public boolean isCovering(IntervalFilter f)
	{
		if ( !(getAttribute().equals(f.getAttribute())))
		{
			return false;
		}
		return this.intervals.covers(f.intervals);
	}

	/**
	 * Tests if this filter instance is covered by the given interval filter. 
	 * An interval filter f is covered by an interval filter g, iff both 
	 * constrain the same attribute and f's intervals are covered by g's 
	 * intervals.
	 * @param f another interval filter to test for coverage.
	 * @return true, if this instance is covered by the given interval filter, 
	 *         otherwise false.
	 */
	public boolean isCoveredBy(IntervalFilter f)
	{
		if ( !(getAttribute().equals(f.getAttribute())))
		{
			return false;
		}
		return f.intervals.covers(this.intervals);
	}
	
	/**
	 * Conjunction of this filter instance and the given interval filter.
	 * @param f another interval filter.
	 * @return a new interval filter resulting from conjunction, or null if
	 *         computing the conjunction was not successful.
	 */
	public Filter doIntersection(IntervalFilter f)
	{
		if ( !(getAttribute().equals(f.getAttribute())))
		{
			// TODO parzy maybe we should construct a new AndFilter?
			return null;
		}
		// TODO parzy check handling of empty interval lists
		IntervalList l = intervals.intersection(f.intervals);
		return new IntervalFilter(attribute,l);
	}
	
	
	/**
	 * Disjunction of this filter instance and the given interval filter.
	 * @param f another interval filter to merge with.
	 * @return a new interval filter containing a merged interval list or null 
	 *         if disjunction was not possible.
	 */
	public Filter doUnion(IntervalFilter f)
	{
		if ( !(getAttribute().equals(f.getAttribute())))
		{
			return null;
		}
		IntervalList l = intervals.union(f.intervals);
		return new IntervalFilter(attribute,l);
	}

	/**
	 * Negates this filter instance.
	 * @return a new interval filter instance resulting from negation of 
	 *         matching intervals.
	 */
	public Filter not()
	{
		return new IntervalFilter(attribute, intervals.complement());
	}

	
	// Visualization ----------------------------------------------------------
	// ------------------------------------------------------------------------

	/**
	 * Creates a textual representation of this interval filter instance.
	 * @return a textual representation of this interval filter instance.
	 */
	@Override
	public String toString()
	{
		return attribute + " in " + intervals;
	}
}