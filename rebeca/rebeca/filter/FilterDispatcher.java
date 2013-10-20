/*
 * $id$
 */
package rebeca.filter;

import rebeca.*;

/**
 * @author parzy
 *
 */
public class FilterDispatcher 
{
	public static boolean overlaps(Filter f, Filter g)
	{
		// scope integration
		if (f instanceof ScopeIdentifier && g instanceof ScopeIdentifier) return ((ScopeIdentifier)f).isOverlapping((ScopeIdentifier)g);
		if (f instanceof ScopeIdentifier) return ((ScopeIdentifier)f).isOverlapping(g);
		if (g instanceof ScopeIdentifier) return ((ScopeIdentifier)g).isOverlapping(f);
		if (f instanceof ScopedFilter && g instanceof ScopedFilter) return ((ScopedFilter)f).isOverlapping((ScopedFilter)g);
		if (f instanceof ScopedFilter) return ((ScopedFilter)f).isOverlapping(g);
		if (g instanceof ScopedFilter) return ((ScopedFilter)g).isOverlapping(f);
		
		// true and false filter
		if (f instanceof FalseFilter) return ((FalseFilter)f).isOverlapping(g);
		if (g instanceof FalseFilter) return ((FalseFilter)g).isOverlapping(f);
		if (f instanceof TrueFilter) return ((TrueFilter)f).isOverlapping(g);
		if (g instanceof TrueFilter) return ((TrueFilter)g).isOverlapping(f);
	
		// interval filter
		if (f instanceof IntervalFilter && g instanceof IntervalFilter) return ((IntervalFilter)f).isOverlapping((IntervalFilter)g);

		// and filter
		if (f instanceof AndFilter && g instanceof AttributeFilter) return ((AndFilter)f).isOverlapping((AttributeFilter)g);
		if (g instanceof AndFilter && f instanceof AttributeFilter) return ((AndFilter)g).isOverlapping((AttributeFilter)f);
		if (f instanceof AndFilter && g instanceof AndFilter) return ((AndFilter)f).isOverlapping((AndFilter)g);

		// or filter
		if (f instanceof OrFilter && g instanceof AttributeFilter) return ((OrFilter)f).isOverlapping((AttributeFilter)g);
		if (g instanceof OrFilter && f instanceof AttributeFilter) return ((OrFilter)g).isOverlapping((AttributeFilter)f);
		if (f instanceof OrFilter && g instanceof AndFilter) return ((OrFilter)f).isOverlapping((AndFilter)g);
		if (g instanceof OrFilter && f instanceof AndFilter) return ((OrFilter)g).isOverlapping((AndFilter)f);
		if (f instanceof OrFilter && g instanceof OrFilter) return ((OrFilter)f).isOverlapping((OrFilter)g);

		// safe default
		return true;
	}
		
	public static boolean identical(Filter f, Filter g)
	{
		// scope integration
		if (f instanceof ScopeIdentifier && g instanceof ScopeIdentifier) return ((ScopeIdentifier)f).isIdentical((ScopeIdentifier)g);
		if (f instanceof ScopeIdentifier) return ((ScopeIdentifier)f).isIdentical(g);
		if (g instanceof ScopeIdentifier) return ((ScopeIdentifier)g).isIdentical(f);
		if (f instanceof ScopedFilter && g instanceof ScopedFilter) return ((ScopedFilter)f).isIdentical((ScopedFilter)g);
		if (f instanceof ScopedFilter) return ((ScopedFilter)f).isIdentical(g);
		if (g instanceof ScopedFilter) return ((ScopedFilter)g).isIdentical(f);
		
		// true and false filter
		if (f instanceof TrueFilter) return ((TrueFilter)f).isIdentical(g);
		if (g instanceof TrueFilter) return ((TrueFilter)g).isIdentical(f);
		if (f instanceof FalseFilter) return ((FalseFilter)f).isIdentical(g);
		if (g instanceof FalseFilter) return ((FalseFilter)g).isIdentical(f);
	
		// interval filter
		if (f instanceof IntervalFilter && g instanceof IntervalFilter) return ((IntervalFilter)f).isIdentical((IntervalFilter)g);

		// and filter
		if (f instanceof AndFilter && g instanceof AttributeFilter) return ((AndFilter)f).isIdentical((AttributeFilter)g);
		if (g instanceof AndFilter && f instanceof AttributeFilter) return ((AndFilter)g).isIdentical((AttributeFilter)f);
		if (f instanceof AndFilter && g instanceof AndFilter) return ((AndFilter)f).isIdentical((AndFilter)g);

		// or filter
		if (f instanceof OrFilter && g instanceof AttributeFilter) return ((OrFilter)f).isIdentical((AttributeFilter)g);
		if (g instanceof OrFilter && f instanceof AttributeFilter) return ((OrFilter)g).isIdentical((AttributeFilter)f);
		if (f instanceof OrFilter && g instanceof AndFilter) return ((OrFilter)f).isIdentical((AndFilter)g);
		if (g instanceof OrFilter && f instanceof AndFilter) return ((OrFilter)g).isIdentical((AndFilter)f);
		if (f instanceof OrFilter && g instanceof OrFilter) return ((OrFilter)f).isIdentical((OrFilter)g);

		// safe default
		return false;
	}
	
	public static boolean covers(Filter f, Filter g)
	{
		// scope integration
		if (f instanceof ScopeIdentifier && g instanceof ScopeIdentifier) return ((ScopeIdentifier)f).isCovering((ScopeIdentifier)g);
		if (f instanceof ScopeIdentifier) return ((ScopeIdentifier)f).isCovering(g);
		if (g instanceof ScopeIdentifier) return ((ScopeIdentifier)g).isCoveredBy(f);
		if (f instanceof ScopedFilter && g instanceof ScopedFilter) return ((ScopedFilter)f).isCovering((ScopedFilter)g);
		if (f instanceof ScopedFilter) return ((ScopedFilter)f).isCovering(g);
		if (g instanceof ScopedFilter) return ((ScopedFilter)g).isCoveredBy(f);
		
		// true and false filter
		if (f instanceof TrueFilter) return ((TrueFilter)f).isCovering(g);
		if (g instanceof TrueFilter) return ((TrueFilter)g).isCoveredBy(f);
		if (f instanceof FalseFilter) return ((FalseFilter)f).isCovering(g);
		if (g instanceof FalseFilter) return ((FalseFilter)g).isCoveredBy(f);
	
		// interval filter
		if (f instanceof IntervalFilter && g instanceof IntervalFilter) return ((IntervalFilter)f).isCovering((IntervalFilter)g);

		// and filter
		if (f instanceof AndFilter && g instanceof AttributeFilter) return ((AndFilter)f).isCovering((AttributeFilter)g);
		if (g instanceof AndFilter && f instanceof AttributeFilter) return ((AndFilter)g).isCoveredBy((AttributeFilter)f);
		if (f instanceof AndFilter && g instanceof AndFilter) return ((AndFilter)f).isCovering((AndFilter)g);

		// or filter
		if (f instanceof OrFilter && g instanceof AttributeFilter) return ((OrFilter)f).isCovering((AttributeFilter)g);
		if (g instanceof OrFilter && f instanceof AttributeFilter) return ((OrFilter)g).isCoveredBy((AttributeFilter)f);
		if (f instanceof OrFilter && g instanceof AndFilter) return ((OrFilter)f).isCovering((AndFilter)g);
		if (g instanceof OrFilter && f instanceof AndFilter) return ((OrFilter)g).isCoveredBy((AndFilter)f);
		if (f instanceof OrFilter && g instanceof OrFilter) return ((OrFilter)f).isCovering((OrFilter)g);

		// safe default
		return false;
	}
	
	public static Filter intersection(Filter f, Filter g)
	{
		// TODO parzy review of scope integration (especially ScopedFilter)
		// scope integration
		if (f instanceof ScopeIdentifier) return ((ScopeIdentifier)f).doIntersection(g);
		if (g instanceof ScopeIdentifier) return ((ScopeIdentifier)g).doIntersection(f);
		if (f instanceof ScopedFilter && g instanceof ScopedFilter) return ((ScopedFilter)f).doIntersection((ScopedFilter)g);
		if (f instanceof ScopedFilter) return ((ScopedFilter)f).doIntersection(g);
		if (g instanceof ScopedFilter) return ((ScopedFilter)g).doIntersection(f);

		// true and false filter
		if (f instanceof TrueFilter) return ((TrueFilter)f).doIntersection(g);
		if (g instanceof TrueFilter) return ((TrueFilter)g).doIntersection(f);
		if (f instanceof FalseFilter) return ((FalseFilter)f).doIntersection(g);
		if (g instanceof FalseFilter) return ((FalseFilter)g).doIntersection(f);

		// interval filter
		if (f instanceof IntervalFilter && g instanceof IntervalFilter) return ((IntervalFilter)f).doIntersection((IntervalFilter)g);

		// and filter
		if (f instanceof AndFilter && g instanceof AttributeFilter) return ((AndFilter)f).doIntersection((AttributeFilter)g);
		if (g instanceof AndFilter && f instanceof AttributeFilter) return ((AndFilter)g).doIntersection((AttributeFilter)f);
		if (f instanceof AndFilter && g instanceof AndFilter) return ((AndFilter)f).doIntersection((AndFilter)g);
		
		// or filter
		if (f instanceof OrFilter && g instanceof AttributeFilter) return ((OrFilter)f).doIntersection((AttributeFilter)g);
		if (g instanceof OrFilter && f instanceof AttributeFilter) return ((OrFilter)g).doIntersection((AttributeFilter)f);
		if (f instanceof OrFilter && g instanceof AndFilter) return ((OrFilter)f).doIntersection((AndFilter)g);
		if (g instanceof OrFilter && f instanceof AndFilter) return ((OrFilter)g).doIntersection((AndFilter)f);
		if (f instanceof OrFilter && g instanceof OrFilter) return ((OrFilter)f).doIntersection((OrFilter)g);
		
		// return a safe default
		return null;
	}
	
	public static Filter and(Filter f, Filter g)
	{
		// try to determine the intersection of both filters first
		Filter rst = intersection(f, g);
		if (rst!=null) return rst;
		
		// TODO parzy review of scope integration (especially ScopedFilter)
		// scope integration
		if (f instanceof ScopedFilter && g instanceof ScopedFilter) return ((ScopedFilter)f).doAnd((ScopedFilter)g);
		if (f instanceof ScopedFilter) return ((ScopedFilter)f).doAnd(g);
		if (g instanceof ScopedFilter) return ((ScopedFilter)g).doAnd(f);

		// attribute filter
		if (f instanceof BasicAttributeFilter && g instanceof BasicAttributeFilter) return ((BasicAttributeFilter)f).doAnd((BasicAttributeFilter)g);
			
		// finally, give up and throw an exception
		throw new IllegalArgumentException("filters could not be anded");
	}
	
	public static Filter union(Filter f, Filter g)
	{
		// TODO parzy review of scope integration (especially ScopedFilter)
		// scope integration
		if (f instanceof ScopeIdentifier) return ((ScopeIdentifier)f).doUnion(g);
		if (g instanceof ScopeIdentifier) return ((ScopeIdentifier)g).doUnion(f);
		if (f instanceof ScopedFilter && g instanceof ScopedFilter) return ((ScopedFilter)f).doUnion((ScopedFilter)g);
		if (f instanceof ScopedFilter) return ((ScopedFilter)f).doUnion(g);
		if (g instanceof ScopedFilter) return ((ScopedFilter)g).doUnion(f);
		
		// true and false filter
		if (f instanceof TrueFilter) return ((TrueFilter)f).doUnion(g);
		if (g instanceof TrueFilter) return ((TrueFilter)g).doUnion(f);
		if (f instanceof FalseFilter) return ((FalseFilter)f).doUnion(g);
		if (g instanceof FalseFilter) return ((FalseFilter)g).doUnion(f);

		// interval filter
		if (f instanceof IntervalFilter && g instanceof IntervalFilter) return ((IntervalFilter)f).doUnion((IntervalFilter)g);

		// and filter
		if (f instanceof AndFilter && g instanceof AttributeFilter) return ((AndFilter)f).doUnion((AttributeFilter)g);
		if (g instanceof AndFilter && f instanceof AttributeFilter) return ((AndFilter)g).doUnion((AttributeFilter)f);
		if (f instanceof AndFilter && g instanceof AndFilter) return ((AndFilter)f).doUnion((AndFilter)g);
		
		// or filter
		if (f instanceof OrFilter && g instanceof AttributeFilter) return ((OrFilter)f).doUnion((AttributeFilter)g);
		if (g instanceof OrFilter && f instanceof AttributeFilter) return ((OrFilter)g).doUnion((AttributeFilter)f);
		if (f instanceof OrFilter && g instanceof AndFilter) return ((OrFilter)f).doUnion((AndFilter)g);
		if (g instanceof OrFilter && f instanceof AndFilter) return ((OrFilter)g).doUnion((AndFilter)f);
		if (f instanceof OrFilter && g instanceof OrFilter) return ((OrFilter)f).doUnion((OrFilter)g);
		
		// return a safe default
		return null;
	}
		
	public static Filter or(Filter f, Filter g)
	{
		// try to determine the union of both filters first
		Filter rst = union(f, g);
		if (rst!=null) return rst;
		
		// TODO parzy review of scope integration (especially ScopedFilter)
		// scope integration
		if (f instanceof ScopedFilter && g instanceof ScopedFilter) return ((ScopedFilter)f).doOr((ScopedFilter)g);
		if (f instanceof ScopedFilter) return ((ScopedFilter)f).doOr(g);
		if (g instanceof ScopedFilter) return ((ScopedFilter)g).doOr(f);
		
		// attribute filter
		if (f instanceof BasicAttributeFilter && g instanceof BasicAttributeFilter) return ((BasicAttributeFilter)f).doOr((BasicAttributeFilter)g);
			
		// finally, give up and throw an exception
		throw new IllegalArgumentException("filters could not be ored");
	}	
}
