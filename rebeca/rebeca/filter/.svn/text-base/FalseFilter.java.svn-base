/*
 * $id$
 */
package rebeca.filter;

import rebeca.Event;
import rebeca.Filter;

/**
 * @author parzy
 *
 */
public class FalseFilter extends BasicFilter 
{
	// serial version UID
	private static final long serialVersionUID = -8308702970206094604L;

	private static FalseFilter falseFilter = new FalseFilter();
	
	public static FalseFilter getInstance()
	{
		return falseFilter;
	}
	
	public Filter doIntersection(Filter f)
	{
		return this;
	}
	
	@Override
	public Filter doUnion(Filter f)
	{
		return f;
	}

	/* (non-Javadoc)
	 * @see rebeca.newfilter.Filter#isCoveredBy(rebeca.newfilter.Filter, int)
	 */
	@Override
	public boolean isCoveredBy(Filter f) 
	{
		return true;
	}
	
	// TODO parzy this method seems not to be necessary
	public boolean isCovering(FalseFilter f) 
	{
		return true;
	}

	/* (non-Javadoc)
	 * @see rebeca.newfilter.Filter#isIdentical(rebeca.newfilter.Filter, int)
	 */
	public boolean isIdentical(FalseFilter f) 
	{
		return true;
	}

	/* (non-Javadoc)
	 * @see rebeca.newfilter.Filter#isOverlapping(rebeca.newfilter.Filter, int)
	 */
	@Override
	public boolean isOverlapping(Filter f) 
	{
		return false;
	}

	/* (non-Javadoc)
	 * @see rebeca.newfilter.Filter#match(rebeca.Event)
	 */
	@Override
	public boolean match(Event e) 
	{
		return false;
	}

	/* (non-Javadoc)
	 * @see rebeca.newfilter.Filter#not()
	 */
	@Override
	public Filter not() 
	{
		return TrueFilter.getInstance();
	}
	
	@Override
	public String toString()
	{
		return "FALSE";
	}
}
