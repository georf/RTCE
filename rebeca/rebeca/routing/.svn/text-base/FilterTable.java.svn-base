/*
 * $Id$
 */
package rebeca.routing;

import java.util.Collection;

import rebeca.*;

/**
 * @author parzy
 *
 */
public interface FilterTable 
{
	public interface FilterSelector
	{
		public boolean select(Filter filter);
	}
	
	public Object getLock();
	
	public Collection<Filter> getFilters();
	
	public Collection<Filter> getFilters(FilterSelector selector);
	
	public boolean containsFilters(FilterSelector selector);
	
	public boolean add(Filter filter);
	
	public boolean remove(Filter filter);
}
