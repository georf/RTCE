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
public class TrueFilter extends BasicFilter
{
	// serial version UID
	private static final long serialVersionUID = 8429646619616754182L;

	private static TrueFilter trueFilter = new TrueFilter();
	
	public static TrueFilter getInstance()
	{
		return trueFilter;
	}
	
	@Override
	public Filter doIntersection(Filter f) 
	{
		return f;
	}
	

	@Override
	public Filter doUnion(Filter f) 
	{
		return this;
	}

	
	/* (non-Javadoc)
	 * @see rebeca.newfilter.Filter#isCoveredBy(rebeca.newfilter.Filter, int)
	 */
	public boolean isCoveredBy(TrueFilter f, int ttl) 
	{
		return true;
	}
	
	@Override
	public boolean isCovering(Filter f) 
	{
		return true;
	}

	/* (non-Javadoc)
	 * @see rebeca.newfilter.Filter#isIdentical(rebeca.newfilter.Filter, int)
	 */
	public boolean isIdentical(TrueFilter f) 
	{
		return true;
	}

	/* (non-Javadoc)
	 * @see rebeca.newfilter.Filter#isOverlapping(rebeca.newfilter.Filter, int)
	 */
	public boolean isOverlapping(FalseFilter f) 
	{
		return false;
	}

	/* (non-Javadoc)
	 * @see rebeca.newfilter.Filter#match(rebeca.Event)
	 */
	@Override
	public boolean match(Event e) 
	{
		return true;
	}

	/* (non-Javadoc)
	 * @see rebeca.newfilter.Filter#not()
	 */
	@Override
	public Filter not() 
	{
		return FalseFilter.getInstance();
	}

	@Override
	public String toString()
	{
		return "TRUE";
	}
}
