/*
 * $Id: IntervalList.java 648 2009-11-23 17:47:44Z parzy $
 */
package rebeca.filter;

import java.util.ArrayList;
import org.apache.log4j.*;

/**
 * An interval list contains an ordered list of matching intervals.  
 * As this class extends an array list, the random access methods and 
 * the contains method have a good time complexity of O(1) and O(log n).
 * @author parzy
 */
public class IntervalList extends ArrayList<Interval>
{
	/** 
	 * Logger for IntervalLists. 
	 */
	private static final Logger LOG = Logger.getLogger(IntervalList.class);

	/**
	 * Serial id to check compatibility.
	 */
	private static final long serialVersionUID = 20090113142806L;

	
	// Constructors -----------------------------------------------------------
	// ------------------------------------------------------------------------
	
	/**
	 * Creates an empty interval list.
	 */
	// TODO parzy maybe make this constructor private as it allows the construction of an empty list which seems to be an illegal state
	public IntervalList()
	{
		super();
	}
	
	/**
	 * Creates a new interval list containing the interval i as single element.
	 * @param i a interval of matching elements.
	 */
	public IntervalList(Interval i)
	{
		// Check the argument (interval) before adding to the list.
		if(i == null)
		{
			throw new NullPointerException();
		}
		add(i);
	}
	
	
	//
	// ------------------------------------------------------------------------
	
	// TODO parzy maybe postpone exception handling to the caller
	
	/**
	 * Matches a Comparable. Tries to find an interval containing the Comparable 
	 * val. Uses a binary search on the ordered interval list.
	 * @param val the Comparable to match.
	 * @return true, if an interval exists containing val, otherwise false.
	 */
	public boolean match(Comparable<?> val)
	{
		try
		{		
			//System.out.println("binary search on interval: "+ binarySearch(val));
			return binarySearch(val) >= 0;
		}
		// be verbose when exceptions occur
		catch(Exception e)
		{
			if (LOG.isInfoEnabled())
			{
				if (LOG.isDebugEnabled())
				{
					LOG.debug("Exception occurred while matching value '" + val 
							+ "' on interval list '" + this + "'.", e);
				}
				else
				{
					LOG.info("Exception occurred while matching.",e);
				}
			}
		}
		return false;
	}
	
	/**
	 * Binary search algorithm for interval lists.
	 * @param val the Comparable value to look for
	 * @return index of the interval containing val if one exists, otherwise
	 *         (-(insertion point) - 1) is returned.
	 */
    private int binarySearch(Comparable<?> val) 
    {
    	int low = 0;
    	int high = size()-1;
       	for (;low <= high;) 
    	{
    		int mid = (low + high) >>> 1;
    		int cmp = get(mid).contains(val);
    		// val is bigger (right of)
    		if (cmp < 0)
    		{
    			low = mid + 1;
    		}
    		// val is smaller (left of)
    		else if (cmp > 0)
    		{
    			high = mid - 1;
    		}
    		// val found; return its position
    		else
    		{
    			return mid; 
    		}
    	}
    	// val not found; return negative position to insert
    	return -(low + 1);
    }
	
    /**
     * Tests, whether two interval lists are overlapping.
	 * Two interval lists are overlapping, iff two overlapping intervals exist.
	 * @param l the interval list to test against.
	 * @eturn true, if two overlapping intervals are found, otherwise false.
	 */
	public boolean overlaps(IntervalList l)
	{
		try
		{
			// Repeat until the end of one list is reached.
			for (int p=0, q=0; p < this.size() && q < l.size(); )
			{
				// get intervals of current positions
				Interval i = get(p);
				Interval j = l.get(q);
				// and test for overlapping
				if (i.overlaps(j))
				{
					return true;
				}
				// if they do not overlap, then try the next one.
				if (i.compareTo(j) < 0)
				{
					p++;
				}
				else
				{
					q++;
				}
			}
		}
		// be verbose about occurred exceptions
		catch (Exception e)
		{
			if (LOG.isInfoEnabled())
			{
				if (LOG.isDebugEnabled())
				{
					LOG.debug("Exception occurred while testing if interval "
							+ "lists '" + this + "' and '" + l 
							+ "' overlap.", e); 
				}
				else
				{
					LOG.info("Exception occured while testing for overlapping "
							+ "intervals.", e);
				}
			}
		}
		return false;
	}		
    
	/**
	 * Tests, whether two interval lists are identical.
	 * Two interval lists are identical, iff they contain identical intervals.
	 * @param l the interval list to test against.
	 * @return true, if both lists only contain identical intervals, 
	 *         otherwise false.
	 */
	public boolean identical(IntervalList l)
	{
		// both lists must have the same size
		if (this.size() != l.size())
		{
			return false;
		}
		try
		{
			// all intervals must be identical, one by one
			for (int p=0; p<size(); p++)
			{
				Interval i = get(p);
				Interval j = l.get(p);
				if (i.compareTo(j) != 0)
				{
					return false;
				}
			}
			return true;
		}
		// log occurred exceptions
		catch (Exception e)
		{
			if (LOG.isInfoEnabled())
			{
				if (LOG.isDebugEnabled())
				{
					LOG.debug("Exception occurred while testing for identity "
							+ "of lists '" + this + "' and '" + l + "'.", e);
				}
				else
				{
					LOG.info("Exception occured while testing for identity.",e);
				}
			}
		}
		return false;
	}
	
	/**
	 * Tests, whether this interval list covers the given one.
	 * An interval list L1 is covered by an other list L2, iff for each 
	 * interval I of the covered list L1, there is an interval J in L2, 
	 * which covers I.  
   	 * @param l the interval list to test against.
	 * @return true, if the list's intervals cover all intervals of l, 
	 *         otherwise false.
	 */
	public boolean covers(IntervalList l)
	{
		try
		{	
			// repeat until the end of one list is reached.
			int p,q;
			for (p=q=0; p < size() && q < l.size(); )
			{
				Interval i = get(p);
				Interval j = l.get(q);
				// all intervals of l must be covered once
				if ( i.covers(j) )
				{
					q++;
				}
				else
				{
					p++;
				}
			}
			// TODO parzy could be optimized, i.e. stop when coverage cannot be reached anymore.
			return q >= l.size();
		}
		// log occurred exceptions
		catch (Exception e)
		{
			if (LOG.isInfoEnabled())
			{
				if (LOG.isDebugEnabled())
				{
					LOG.debug("Exception occurred while testing if list '" 
							+ this + "' covers '" + l + "'.", e);
				}
				else
				{
					LOG.info("Exception occured while performing covering "
							+ "test.", e);
				}
			}
		}
		return false;
	}

	/**
	 * Conjunction of two interval lists.
	 * @param l a list of intervals.
	 * @return a new list of intervals resulted from conjunction.
	 */
	// TODO parzy what about exception handling and logging
	public IntervalList intersection(IntervalList l)
	{
		// 'and' each interval of this list with every other interval 
		// of the second list
		IntervalList rst = new IntervalList();
		for (Interval i : this)
		{
			for (Interval j : l)
			{
				Interval k = i.intersection(j);
				if (k != null)
				{
					rst.add(k);
				}
			}
		}
		return rst;
		// TODO parzy maybe we should return null if the interval list contains no elements
		// return rst.size() == 0 ? null : rst;
	}
	
	/**
	 * Disjunction of two interval lists or simply merges both interval lists
	 * into a new one.
	 * @param l a list of intervals to be merged with.
	 * @return a new list of intervals resulting from disjunction/merging.
	 */
	public IntervalList union(IntervalList l)
	{
		IntervalList rst = new IntervalList(); 
		// repeat until each interval of both lists was 'merged'
		int p,q; Interval prev;
		for (p=q=0, prev = null; p < size() || q < l.size(); )
		{
			// compare intervals and get the smaller one (or the one left over)
			Interval cur;
			if (p < size() && q < l.size())
			{
				cur = (get(p).compareTo(l.get(q))<=0 ? get(p++) : l.get(q++));
			}
			else if (p < size())
			{
				cur = get(p++);
			}
			else
			{
				cur = l.get(q++);
			}
			// merge it with the one from the previous iteration
			Interval tmp = prev == null ? cur : prev.union(cur);
			if (tmp != null)
			{
				prev = tmp;
			}
			else
			{
				rst.add(prev);
				prev = cur;
			}
		}
		// return resulting list including the last merged interval
		if (prev != null)
		{
			rst.add(prev);
		}
		return rst;
	}		
	
	/**
	 * Negates this interval list.
	 * @return a list of intervals resulted from negation.
	 */
	public IntervalList complement()
	{
		// create a new interval for each interval gap in this list 
		IntervalList rst = new IntervalList();
		for ( int p=0; p <= size(); p++ )
		{
			// take care to handle first and last interval appropriately---
			// that is why we need one iteration more than intervals in this list
			IntervalBound lb = p==0 ? IntervalBound.BOTTOM : 
				                      get(p-1).getUpperBound().complement();
			IntervalBound ub = p==size() ? IntervalBound.TOP :
									       get(p).getLowerBound().complement();
			if (lb != null && ub != null)
			{
				rst.add(new Interval(lb,ub));
			}
		}
		return rst;
	}
	
	
	// Visualization ----------------------------------------------------------
	// ------------------------------------------------------------------------
	
	// inherited from ArrayList :-)
}
