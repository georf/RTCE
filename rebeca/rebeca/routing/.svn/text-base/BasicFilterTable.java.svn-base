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
public class BasicFilterTable implements FilterTable 
{
	protected Collection<Filter> entries;
	protected Object lock;
	
	public BasicFilterTable()
	{
		entries = new LinkedHashSet<Filter>();
		lock = this;
	}
	
	protected BasicFilterTable(FilterTable table)
	{
		this(table.getFilters(), table.getLock());
	}
	
	private BasicFilterTable(Collection<Filter> entries, Object lock)
	{
		this.entries = entries;
		this.lock = lock;
	}
	
	public Object getLock()
	{
		return lock;
	}
	
	public Collection<Filter> getFilters()
	{
		return entries;
	}
	
	public Collection<Filter> getFilters(FilterSelector selector)
	{
		Collection<Filter> rst = new LinkedList<Filter>();
		
		synchronized (getLock())
		{
			for (Filter f : entries)
			{
				if (selector.select(f))
				{
					rst.add(f);
				}
			}
		}
		return rst;
	}
	
	public boolean add(Filter filter)
	{
		synchronized (getLock())
		{
			return entries.add(filter);
		}
	}
	
	public boolean remove(Filter filter)
	{
		synchronized (getLock())
		{
			return entries.remove(filter);
		}
	}
	
	public boolean removeFilters(FilterSelector selector)
	{
		boolean modified = false;
		synchronized (getLock())
		{
			for (Iterator<Filter> it = entries.iterator(); it.hasNext(); )
			{
				if (selector.select(it.next()))
				{
					it.remove();
					modified = true;
				}
			}
		}
		return modified;
	}
	
	public boolean containsFilters(FilterSelector selector)
	{
		synchronized (getLock())
		{
			for (Iterator<Filter> it = entries.iterator(); it.hasNext(); )
			{
				if (selector.select(it.next()))
				{
					return true;
				}
			}
		}
		return false;
	}
}
