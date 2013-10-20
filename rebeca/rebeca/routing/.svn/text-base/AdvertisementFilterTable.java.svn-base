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
public class AdvertisementFilterTable extends BasicFilterTable 
{	
	
	
	public AdvertisementFilterTable()
	{
		this(new BasicFilterTable());
	}
		public AdvertisementFilterTable(FilterTable table)
	{
		this.entries = table.getFilters();
		this.lock = table.getLock();
	}
	
	protected static class OverlappingSelector implements FilterSelector
	{
		protected Filter filter;
		
		public OverlappingSelector(Filter filter)
		{
			this.filter = filter;
		}
		
		public boolean select(Filter filter)
		{
			return this.filter.overlaps(filter);
		}
	}
	
	public static OverlappingSelector getOverlappingSelector(Filter filter)
	{
		return new OverlappingSelector(filter);
	}
	
	protected class ServingSelector implements FilterSelector
	{
		protected Filter filter;
		
		public ServingSelector(Filter filter)
		{
			this.filter = filter;
		}
		
		public boolean select(Filter filter)
		{
			if (!this.filter.overlaps(filter))
			{
				return false;
			}
			
			for (Filter f : entries)
			{
				if (f.overlaps(filter))
				{
					return false;
				}
			}
			return true;
		}
	}
	
	public ServingSelector getServingSelector(Filter filter)
	{
		return new ServingSelector(filter);
	}
	
//	public Collection<Filter> getOverlappingFilters(Filter filter)
//	{
//		FilterSelector selector = new OverlappingSelector(filter);
//		return this.getFilters(selector);
//	}
}
