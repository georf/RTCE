package rebeca.event;

import java.util.*;

import rebeca.*;
import rebeca.scope.*;

public class Unsubscription extends Subscription 
{
	private static final long serialVersionUID = 20080921083928L;

	private Collection<Subscription> uncovered;
	
	protected Unsubscription()
	{
		this(null);
	}
	
	Unsubscription(Filter f)
	{
		this(f, (Collection<Subscription>)null);
	}
	
	Unsubscription(Filter f, Collection<? extends Subscription> c)
	{
		super(f);
		this.uncovered = new LinkedList<Subscription>();
		if (c != null)
		{
			this.uncovered.addAll(c);
		}
	}
	
	Unsubscription(Filter f, ScopeSet s)
	{
		this(f, null, s);
	}

	Unsubscription(Filter f, Collection<? extends Subscription> c, ScopeSet s)
	{
		super(f,s);
		this.uncovered = new LinkedList<Subscription>();
		if (c != null)
		{
			this.uncovered.addAll(c);
		}
	}
	
	public Collection<? extends Subscription> getUncovered()
	{
		return new LinkedList<Subscription>(uncovered);
	}
	

	public boolean addUncovered(Subscription s)
	{
		return uncovered.add(s);
	}
	
	
	public void clearUncoveredSubscriptions()
	{
		uncovered.clear();
	}
	
	public boolean addUncovered(Filter f)
	{
		Subscription s = new Subscription(f);
		s.setScopes(this.getScopes());
		return uncovered.add(s);
	}
	
	public boolean addAllUncovered(Collection<?> c)
	{
		boolean rst = false;
		for (Object o : c)
		{
			if (o instanceof Subscription)
			{
				rst = addUncovered((Subscription)o) | rst;
				continue;
			}
			if (o instanceof Filter)
			{
				rst = addUncovered((Filter)o) | rst;
				continue;
			}

			throw new IllegalArgumentException();
		}
		return rst;
	}
	
	@Override
	public String toString()
	{
		// TODO parzy visualize uncovered filters
		return "Unsubscription(" + filter + ")@" + id;
	}
}
