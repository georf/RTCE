/*
 * $Id$
 */
package rebeca;

import rebeca.util.*;


public interface Filter
{
	public Object clone();
	
//	// TODO parzy maybe hide these two methods?
//	public Id getId();
//	public void setId(Id id);
	
	public boolean match(Event e);
	
	public boolean overlaps(Filter f);
	//public boolean isOverlapping(Filter f, int ttl);
	public boolean isOverlapping(Filter f);
	
	public boolean identical(Filter f);
	//public boolean isIdentical(Filter f, int ttl);
	public boolean isIdentical(Filter f);
	
	public boolean covers(Filter f);
	//public boolean isCovering(Filter f, int ttl);
	public boolean isCovering(Filter f);
	//public boolean isCoveredBy(Filter f, int ttl);
	public boolean isCoveredBy(Filter f);

	
	public Filter merge(Filter f);
	//public Filter doMerge(Filter f, int ttl);
	//public Filter doMerge(Filter f);

	public Filter intersection(Filter f);
	//public Filter cap(Filter f);
	//public Filter doAnd(Filter f, int ttl);
	public Filter doIntersection(Filter f);
	
	public Filter and(Filter f);
	public Filter doAnd(Filter g);
	
	public Filter union(Filter f);
	//public Filter cup(Filter f);
	//public Filter doOr(Filter f, int ttl);
	public Filter doUnion(Filter f);
	
	public Filter or(Filter f);
	public Filter doOr(Filter f);
	
	public Filter not();
	
	// optional methods -------------------------------------------------------
	// ------------------------------------------------------------------------

	public Id getId();
	public void setId(Id id);
}
