/*
 * $Id: Interval.java 648 2009-11-23 17:47:44Z parzy $
 */
package rebeca.filter;

import java.io.*;

/**
 * Implements an interval for objects, implementing the Comparable interface.
 * The IntervalFilter is based on these intervals. 
 * @author parzy
 */
public class Interval implements Comparable<Interval>, Serializable
{
	/**
	 * Serial id to check compatibility.
	 */
	private static final long serialVersionUID = 20090113142709L;
	
	/**
	 * The interval's lower bound.
	 */
	protected IntervalBound lowerBound;
	
	/**
	 * The interval's upper bound.
	 */
	protected IntervalBound upperBound;
	
	/**
	 * Creates the interval (-infinity,+infinity).
	 * To create an interval from x to y, prefer using the following line:
	 * 'new Interval().From(x).To(y)'.
	 */
	public Interval()
	{
		lowerBound = IntervalBound.BOTTOM;
		upperBound = IntervalBound.TOP;
	}
	
	/**
	 * Creates the interval from lower to upper bound.
     * @param lower the interval's lower bound.
	 * @param upper the interval's upper bound.
     * @throws IllegalArgumentException if wrong type bound is given or 
     *         the lower bound is greater than the upper bound.
     */
	public Interval(IntervalBound lower, IntervalBound upper)
	{
		// Check the arguments.
		if(!lower.isLower())
		{
			throw new IllegalArgumentException("Illegal interval bounds: lower bound is of type upper bound.");
		}
		if(!upper.isUpper())
		{
			throw new IllegalArgumentException("Illegal interval bounds: upper bound is of type lower bound.");
		}
		if(lower.compareTo(upper) >= 0)
		{
			throw new IllegalArgumentException("Illegal interval bounds: lower bound is greater than upper bound.");
		}
		// finally set the fields
		this.lowerBound = lower;
		this.upperBound = upper;
	}

	// More "constructors" ----------------------------------------------------
	// ------------------------------------------------------------------------

	/**
	 * Sets the interval's lower bound to '[val'.
	 * @param val value of the interval's new lower bound.
	 * @return this interval.
	 * @throws IllegalArgumentException if the interval's new lower bound is 
	 *         greater than its upper bound.
	 */
	public Interval from(Comparable<?> val)
	{
		// check the new lower bound
		IntervalBound lb = 
				new IntervalBound(val, IntervalBound.Kind.LOWER_INCLUSIVE);
		if(lb.compareTo(upperBound) >= 0)
		{
			throw new IllegalArgumentException("Illeagal lower bound: lower bound is greater than upper bound.");
		}
		// Set the attribute.
		this.lowerBound = lb;
		return this;
	}

	/**
	 * Sets the interval's lower bound to '(val'.
	 * @param val value of the interval's new lower bound.
	 * @return this interval.
	 * @throws IllegalArgumentException if the interval's new lower bound is 
	 *         greater than its upper bound.
	 */
	public Interval fromExcl(Comparable<?> val)
	{
		// check the new lower bound
		IntervalBound lb = 
				new IntervalBound(val, IntervalBound.Kind.LOWER_EXCLUSIVE);
		if(lb.compareTo(upperBound) >= 0)
		{
			throw new IllegalArgumentException("Illegal lower bound: lower bound is greater than upper bound.");
		}
		// Set the attribute.
		this.lowerBound = lb;
		return this;
	}

	/**
	 * Sets the interval's upper bound to 'val]'.
	 * @param val value of the interval's new upper bound.
	 * @return this interval.
	 * @throws IllegalArgumentException if the interval's new upper bound is 
	 *         smaller than its lower bound.
	 */
	public Interval to(Comparable<?> val)
	{
		// check the new upper bound
		IntervalBound ub = 
			new IntervalBound(val, IntervalBound.Kind.UPPER_INCLUSIVE);
		if(ub.compareTo(lowerBound) <= 0)
		{
			throw new IllegalArgumentException("Illegal upper bound: upper bound is less than lower bound.");
		}
		// Set the attribute.
		this.upperBound = ub;
		return this;
	}

	/**
	 * Sets the interval's upper bound to 'val)'.
	 * @param val value of the interval's new upper bound.
	 * @return this interval.
	 * @throws IllegalArgumentException if the interval's new upper bound is 
	 *         smaller than its lower bound.
	 */
	public Interval toExcl(Comparable<?> val)
	{
		// check the new upper bound
		IntervalBound ub = 
			new IntervalBound(val, IntervalBound.Kind.UPPER_EXCLUSIVE);
		if(ub.compareTo(lowerBound) <= 0)
		{
			throw new IllegalArgumentException("Illegal upper bound: upper bound is less than lower bound.");
		}
		// Set the attribute.
		this.upperBound = ub;
		return this;
	}

	
	// Getters ----------------------------------------------------------------
	// ------------------------------------------------------------------------
	public IntervalBound getLowerBound()
	{
		return lowerBound;
	}
	
	public IntervalBound getUpperBound()
	{
		return upperBound;
	}
	
	
	// Matching and related ---------------------------------------------------
	// ------------------------------------------------------------------------
	
	/**
	 * Tests, whether the given Comparable c is an element of this interval.
	 * @param c a comparable to test.
	 * @return 0, if c is an element of this interval. -1, if c is smaller than 
	 *         the interval's lower bound. 1, if c is greater than the 
	 *         interval's upper bound.
	 */
	public int contains(Comparable<?> c)
	{
		// c is less?
		if(!lowerBound.contains(c))
		{
			return 1;
			//return -1;
		}
		// c is greater?
		if(!upperBound.contains(c))
		{
			return -1;
			//return 1;
		}
		return 0;
	}

	/**
	 * Tests, whether this instance and the given interval overlap each other.
	 * Two intervals [i1,i2] and [j1,j2] are overlapping, iff 
	 * i1 &lt;= j1 &lt;= i2 or j1 &lt;= i1 &lt;= j2.
	 * @param i the interval to test against.
	 * @return true, if the intervals are overlapping, otherwise false.
	 */
	public boolean overlaps(Interval i)
	{
		return ( lowerBound.compareTo(i.lowerBound) >= 0 && 
				 lowerBound.compareTo(i.upperBound) <= 0 ) ||
			   ( i.lowerBound.compareTo(lowerBound) >= 0 && 
			     i.lowerBound.compareTo(upperBound) <= 0 );
	}
	
	/**
	 * Tests, whether this instance covers the given interval.
	 * An interval [i1,i2] covers the interval [j1,j2], iff i1 &lt;= j1 and 
	 * j2 &lt;= i1.
	 * @param i the interval to test against.
	 * @return true, if the interval is covered, otherwise false.
 	 */ 
	public boolean covers(Interval i)
	{
		return lowerBound.compareTo(i.lowerBound) <= 0 &&
				upperBound.compareTo(i.upperBound) >= 0;
	}
	
	/**
	 * Tests, whether this instance is adjacent to the given interval.
	 * Interval [i1,i2] is adjacent to [j1,j2], iff i1 is adjacent to j2 or i2 
	 * is adjacent to j1. 
	 * @param i The interval to test against.
	 * @return true, if one interval is adjacent to the other, otherwise false.
	 */
	public boolean isAdjacentTo(Interval i)
	{
		return lowerBound.isAdjacentTo(i.upperBound) ||
				upperBound.isAdjacentTo(i.lowerBound);
	}
	
	// Operations -------------------------------------------------------------
	// ------------------------------------------------------------------------
	
	/**
	 * Intersection of two intervals.
	 * @param i an interval
	 * @return the section of both intervals, if it exists, otherwise null.
	 */
	public Interval intersection(Interval i)
	{
		// get the higher lower bound,			
		IntervalBound lb = lowerBound.compareTo(i.lowerBound) >= 0 ? this.lowerBound : i.lowerBound;
		// the lower upper bound
		IntervalBound ub = upperBound.compareTo(i.upperBound) <= 0 ? this.upperBound : i.upperBound;
		// and return a new interval, if it exists.
		if(lb.compareTo(ub) < 0)
		{
			return new Interval(lb,ub);
		}
		return null;
	}

	/**
	 * Union of two intervals.
	 * @param i an interval.
	 * @return the union of the two intervals, if it can be expressed by one 
	 *         only interval, otherwise null.
	 */
	public Interval union(Interval i)
	{
		// not possible to express the union by one interval?
		if( !(overlaps(i) || isAdjacentTo(i)) )
		{
			return null;
		}
		// get the smaller lower bound and higher upper bound 
		// before returning the new interval
		IntervalBound lb = lowerBound.compareTo(i.lowerBound)<=0 ? lowerBound : i.lowerBound;
		IntervalBound ub = upperBound.compareTo(i.upperBound)>=0 ? upperBound : i.upperBound;
		return new Interval(lb,ub);
	}

	/**
	 * Negates this interval resulting in at most two new intervals: the first,
	 * if it exists, describes all values preceding this interval; the second,
	 * if it exists, contains all values succeeding this interval. 
	 * @return an array of at most two intervals as described above. If the
	 *         intervals do not exist the array contains null at corresponding
	 *         positions.
	 */
	public Interval[] complement()
	{
		// turn the bounds around
		IntervalBound nlb = this.lowerBound.complement();
		IntervalBound nub = this.upperBound.complement();
		// construct appropriated intervals, if they exist.
		Interval[] rst = new Interval[2];
		if(nlb != null)
		{
			rst[0] = new Interval(IntervalBound.BOTTOM, nlb);
		}
		if(nub != null)
		{
			rst[1] = new Interval(nub, IntervalBound.TOP);
		}
		return rst;
	}
	
	// Comparable implementation ----------------------------------------------
	// ------------------------------------------------------------------------
	
	/**
	 * Compares this instance to the given interval. The order is defined by
	 * the interval bounds. An interval precedes another interval if its lower
	 * bound is smaller than the lower bound of the other interval.  If both 
	 * lower bounds are equal the upper bounds are also compared in same way.
	 * @return -1, if this instance precedes the given interval, 0, if both 
	 *         intervals are equal, or 1 otherwise. 
	 */
	public int compareTo(Interval i)
	{
		// first, compare the lower bounds and 
		// if these are equal also compare the upper bounds
		int rst = lowerBound.compareTo(i.lowerBound);
		return rst != 0 ? rst : upperBound.compareTo(i.upperBound);
	}

	// Visualization ----------------------------------------------------------
	// ------------------------------------------------------------------------
	
	/**
	 * Returns a textual representation of this interval instance.
	 * @return a textual representation
	 */
	public String toString()
	{
		return lowerBound + "," + upperBound;
	}
}
