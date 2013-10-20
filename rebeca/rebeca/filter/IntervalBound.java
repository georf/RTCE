/*
 * $Id: IntervalBound.java 648 2009-11-23 17:47:44Z parzy $
 */
package rebeca.filter;

import java.io.*;

/**
 * Instances of this class represent interval bounds.
 * @author parzy
 */
public class IntervalBound implements Comparable<IntervalBound>, Serializable
{
	/**
	 * Serial id to test compatibility.
	 */
	private static final long serialVersionUID = 20090113110947L;

	/**
	 * All possible types of interval bounds.
	 * @author parzy
	 */
	public static enum Kind { BOTTOM, LOWER_INCLUSIVE, LOWER_EXCLUSIVE, 
			UPPER_EXCLUSIVE, UPPER_INCLUSIVE, TOP };
	
	/**
	 * Constant representing negative infinity.
	 */
	public static final IntervalBound BOTTOM = new IntervalBound(Kind.BOTTOM);

	/**
	 * Constant representing positive infinity.
	 */
	public static final IntervalBound TOP = new IntervalBound(Kind.TOP);
	
	/**
	 * The kind of this bound.
	 */
	private Kind kind;
	
	/**
	 * The bound's value.
	 */
	private Comparable<Object> value;
	
	/**
	 * Private constructor to create BOTTOM and TOP instances.
	 * @param kind only BOTTOM or TOP allowed, but not checked.
	 */
	private IntervalBound(Kind kind)
	{
		this.kind = kind;
	}
	
	/**
	 * Created a new interval bound.
	 * @param val the bound's value
	 * @param kind kind of bound: LOWER_EXCLUSIVE, LOWER_INCLUSIVE, 
	 *        UPPER_EXCLUSIVE, or UPPER_INCLUSIVE.
	 * @throws NullPointerException if value is null
	 * @throws IllegalArgumentException if kind is BOTTOM or TOP
	 *         (simply use the bound constants BOTTOM or TOP, instead).
	 */
	@SuppressWarnings("unchecked")
	public IntervalBound(Comparable<?> val, Kind kind)
	{
		// sanity check
		if (val == null)
		{
			throw new NullPointerException();
		}
		if (kind == Kind.BOTTOM || kind == Kind.TOP)
		{
			throw new IllegalArgumentException();
		}
		// store given values
		this.value = (Comparable<Object>)val;
		this.kind = kind;
	}
	
	/**
	 * Tests, whether this bound represents negative infinity.
	 * @return true, if this instance represents negative infinity,
	 *         otherwise false.
	 */
	public boolean isBottom()
	{
		return kind == Kind.BOTTOM;
	}
	
	/**
	 * Tests, whether this bound represents positive infinity.
	 * @return true, if this instance represents positive infinity
	 *         otherwise false.
	 */
	public boolean isTop()
	{
		return kind == Kind.TOP;
	}
	
	/**
	 * Tests, whether this bound instance is TOP or BOTTOM.
	 * @return
	 */
	public boolean isInfinity()
	{
		return isBottom() || isTop();
	}
	
	/**
	 * Tests, whether this bound instance is a lower interval bound.
	 * @return true, if this instance is a lower interval bound, 
	 *         otherwise false.
	 */
	public boolean isLower()
	{
		return kind == Kind.BOTTOM || kind == Kind.LOWER_INCLUSIVE 
				|| kind == Kind.LOWER_EXCLUSIVE;
	}

	/**
	 * Tests, whether this bound instance is an upper interval bound.
	 * @return true, if this instance is an upper interval bound, 
	 *               otherwise false.
	 */
	public boolean isUpper()
	{
		return !isLower();
	}
	
	/**
	 * Tests, whether the bound's value belongs to the implied set of 
	 * contained values.
	 * @return true, if the bound's value belongs to the set of contained 
	 *         values, otherwise false. 
	 */
	public boolean isInclusive()
	{
		return kind == Kind.LOWER_INCLUSIVE || kind == Kind.UPPER_INCLUSIVE;
	}

	/**
	 * Tests, whether the bound's value does not belong to the implied set of 
	 * contained values.
	 * @return true, if the bound's value does NOT belong to the set of 
	 *         contained values, otherwise false. 
	 */
	public boolean isExclusive()
	{
		return !isInclusive();
	}

	// 
	// ------------------------------------------------------------------------

	/**
	 * Tests, whether the given Comparable c is an element of the set bounded 
	 * by this instance.
	 * @param c the value to test
	 * @return True, if c is an element of the bounded set, otherwise false.
	 */
	public boolean contains(Comparable<?> c)
	{
		switch (kind)
		{
		case BOTTOM:
			return true;
		case UPPER_EXCLUSIVE:
			return value.compareTo(c) > 0;
		case LOWER_INCLUSIVE:
			return value.compareTo(c) <= 0;
		case UPPER_INCLUSIVE:
			return value.compareTo(c) >= 0;
		case LOWER_EXCLUSIVE:
			return value.compareTo(c) < 0;
		case TOP:
			return true;
		}
		throw new IllegalStateException("Unhandled kind of interval bound.");
	}
	
	/**
	 * Tests, whether this instance is adjacent to the given bound.  Bound b1 
	 * is adjacent to bound b2, if !b1 == b2.
	 * @param b the probably adjacent bound
	 * @return true, if both bounds are adjacent to each other, otherwise false 
	 */
	public boolean isAdjacentTo(IntervalBound b)
	{
		// infinity bounds are never adjacent to anything
		if (this.isInfinity() || b.isInfinity())
		{
			return false;
		}
		// if values (real not integer) differ, the bounds are also not adjacent 
		if (this.value.compareTo(b.value) != 0)
		{
			return false;
		}
		// decide for each kind of bound
		switch (kind)
		{
		case UPPER_EXCLUSIVE:
			return b.kind == Kind.LOWER_INCLUSIVE;
		case LOWER_INCLUSIVE:
			return b.kind == Kind.UPPER_EXCLUSIVE;
		case UPPER_INCLUSIVE:
			return b.kind == Kind.LOWER_EXCLUSIVE;
		case LOWER_EXCLUSIVE:
			return b.kind == Kind.UPPER_INCLUSIVE;
		}
		throw new IllegalStateException("Unhandled kind of interval bound.");
	}
	
	/**
	 * Negates this bound. Infinity bounds such as BOTTOM or TOP cannot be 
	 * negated.
	 * @return the negated bound or null if this instance is BOTTOM or TOP.
	 */
	public IntervalBound complement()
	{
		switch (kind)
		{
		case BOTTOM:
			return null;
		case UPPER_EXCLUSIVE:
			return new IntervalBound(value, Kind.LOWER_INCLUSIVE);
		case LOWER_INCLUSIVE:
			return new IntervalBound(value, Kind.UPPER_EXCLUSIVE);
		case UPPER_INCLUSIVE:
			return new IntervalBound(value, Kind.LOWER_EXCLUSIVE);
		case LOWER_EXCLUSIVE:
			return new IntervalBound(value, Kind.UPPER_INCLUSIVE);
		case TOP:
			return null;
		}
		throw new IllegalStateException("Unhandled kind of interval bound.");
	}
	
	// Comparable implementation ----------------------------------------------
	// ------------------------------------------------------------------------
	
	/**
	 * Compares two bounds.
	 * @param b the bound to compare to
	 * @return 0, if both bounds are equal, -1 if this instance is smaller, 
	 *         1 otherwise.
	 */
	public int compareTo(IntervalBound b)
	{
		// cases in which at least one bound is BOTTOM or TOP
		if (this.isInfinity() || b.isInfinity())
		{
			if (isBottom())
			{
				return b.isBottom() ? 0 : -1;
			}
			if (isTop())
			{
				return b.isTop() ? 0 : 1;
			}
			return b.isBottom() ? 1 : -1;
		}
		// as no bound represents infinity we can simply compare their values 
		int rst = this.value.compareTo(b.value);
		// if both values are equal have a closer look at the bounds itself
		return rst == 0 ? compareBounds(b) : rst;
	}
	
	/**
	 * Compares two bounds to each other in case their values are equal. 
	 * The order is defined as UPPER_ECLUSIVE < LOWER_INCLUSIVE < 
	 * UPPER_INCLUSIVE < LOWER_EXCLUSIVE.
	 * @param b the bound to compare to
	 * @return 0 if both bounds are identical, -1 if this bound precedes b in
	 *         above order, or 1 otherwise.
	 * @throws IllegalStateException when called on a TOP or BOTTOM bound or
	 *         with a TOP or BOTTOM bound as argument
	 */
	private int compareBounds(IntervalBound b)
	{
		if (b.kind != Kind.BOTTOM && b.kind != Kind.TOP)
		{
			switch (kind)
			{
			case UPPER_EXCLUSIVE:
				return b.kind == Kind.UPPER_EXCLUSIVE ? 0 : -1;
			case LOWER_INCLUSIVE:
				return b.kind == Kind.UPPER_EXCLUSIVE ? 1 : b.kind == Kind.LOWER_INCLUSIVE ? 0 : -1;
			case UPPER_INCLUSIVE:
				return b.kind == Kind.LOWER_EXCLUSIVE ? -1 : b.kind == Kind.UPPER_INCLUSIVE ? 0 : 1;
			case LOWER_EXCLUSIVE:
				return b.kind == Kind.LOWER_EXCLUSIVE ? 0 : 1;
			}
		}
		throw new IllegalStateException("Unhandled kind of interval bound.");
	}
	
	
	// Visualization ----------------------------------------------------------
	// ------------------------------------------------------------------------
	
	/**
	 * Returns a textual representation of this bound instance.
	 * @return a textual representation.
	 */
	public String toString()
	{
		switch (kind)
		{
		case BOTTOM:
			return "(-Inf";
		case LOWER_EXCLUSIVE:
			return "(" + value;
		case LOWER_INCLUSIVE:
			return "[" + value;
		case UPPER_INCLUSIVE:
			return value + "]";
		case UPPER_EXCLUSIVE:
			return value + ")";
		case TOP:
			return "Inf)";
		}
		throw new IllegalStateException("Unhadled kind of interval bound.");
	}
}
