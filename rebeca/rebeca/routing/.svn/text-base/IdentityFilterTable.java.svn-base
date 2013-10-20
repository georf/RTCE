/*
 * $Id$
 */
package rebeca.routing;

import rebeca.*;

/**
 * @author parzy
 *
 */
public class IdentityFilterTable extends BasicFilterTable 
{
	protected static class IdentitySelector implements FilterSelector
	{
		Filter filter;
		
		public IdentitySelector(Filter filter)
		{
			this.filter = filter;
		}
		
		public boolean select(Filter filter)
		{
			return this.filter.identical(filter);
		}
	}
	
	public static FilterSelector getIdentitySelector(Filter filter)
	{
		return new IdentitySelector(filter);
	}
	
	public IdentityFilterTable()
	{
		super();
	}
	
	public IdentityFilterTable(FilterTable table)
	{
		super(table);
	}
	
		
	@Override
	public boolean add(Filter filter)
	{
		if (! getFilters(new IdentitySelector(filter)).isEmpty() )
		{
			return false;
		}
		return entries.add(filter);
	}
	
	@Override
	public boolean remove(Filter filter)
	{
		FilterSelector selector = new IdentitySelector(filter);
		return removeFilters(selector);
	}
	
//	public boolean containsIdenticalFilters(Filter filter)
//	{
//		FilterSelector selector = new IdentitySelector(filter);
//		return !this.getFilters(selector).isEmpty();
//	}
}
