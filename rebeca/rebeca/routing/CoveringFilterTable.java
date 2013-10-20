/*
 * $Id$
 */
package rebeca.routing;

import java.util.*;

import rebeca.*;
import rebeca.routing.FilterTable.FilterSelector;

/**
 * @author parzy
 *
 */
public class CoveringFilterTable extends BasicFilterTable 
{
	// selects all filter table entries that are covering the selector's filter
	protected static class CoveringSelector implements FilterSelector
	{
		Filter filter;
		
		public CoveringSelector(Filter filter)
		{
			this.filter = filter;
		}
		
		public boolean select(Filter filter)
		{
			return filter.covers(this.filter);
		}
	}
	
	// selects all filter table entries that are covered by the selector's filter
	protected static class CoveredSelector implements FilterSelector
	{
		Filter filter;
		
		public CoveredSelector(Filter filter)
		{
			this.filter = filter;
		}
		
		public boolean select(Filter filter)
		{
			return this.filter.covers(filter);
		}
	}

	// selects all filter table entries that are covering the selector's filter
	public static CoveringSelector getCoveringSelector(Filter filter)
	{
		return new CoveringSelector(filter);
	}

	// selects all filter table entries that are covered by the selector's filter
	public static CoveredSelector getCoveredSelector(Filter filter)
	{
		return new CoveredSelector(filter);
	}
	
	
	public CoveringFilterTable()
	{
		super();
	}
	
	public CoveringFilterTable(FilterTable table)
	{
		super(table);
	}
	
	
//	public boolean containsCoveringFilters(Filter filter)
//	{
//		FilterSelector selector = new CoveringSelector(filter);
//		return !this.getFilters(selector).isEmpty();
//	}
//	
//	public Collection<Filter> getUncoveredFilters(Filter filter)
//	{
//		FilterSelector selector = new CoveredSelector(filter);
//		return this.getFilters(selector);
//	}
	
}
