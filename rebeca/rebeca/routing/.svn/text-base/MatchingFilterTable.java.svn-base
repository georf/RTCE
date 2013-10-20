/*
 * $Id$
 */
package rebeca.routing;

import rebeca.*;

/**
 * @author parzy
 *
 */
public class MatchingFilterTable extends BasicFilterTable 
{
	protected class MatchingSelector implements FilterSelector
	{
		protected Event event;
		
		public MatchingSelector(Event event)
		{
			this.event = event;
		}
		
		public boolean select(Filter filter)
		{
			return filter.match(event);
		}
	}

	public MatchingFilterTable()
	{
		super();
	}
	
	public MatchingFilterTable(FilterTable table)
	{
		super(table);
	}
	
	public boolean match(Event event)
	{
		FilterSelector selector = new MatchingSelector(event);
		return !this.getFilters(selector).isEmpty();
	}
}
