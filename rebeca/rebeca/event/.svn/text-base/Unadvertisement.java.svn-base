package rebeca.event;

import java.util.*;

import rebeca.*;
import rebeca.scope.*;

public class Unadvertisement extends Unsubscription
{
	public static final long serialVersionUID = 20080923073434L;
	
	protected Unadvertisement()
	{
		super();
	}
	
	Unadvertisement(Filter f)
	{
		super(f);
		//this(f,null, new BasicScopeSet());
	}
	
	Unadvertisement(Filter f, Collection<? extends Subscription> c)
	{
		super(f,c);
	}
	
	Unadvertisement(Filter f, ScopeSet s)
	{
		super(f,s);
		//this(f,null, s);
	}
	
	Unadvertisement(Filter f, Collection<? extends Subscription> c, ScopeSet s)
	{
		super(f,c,s);
	}
	
	@Override
	public String toString()
	{
		return "Unadvertisement(" + filter + ")@" + id;
	}
}

//public class Unadvertisement extends Advertisement 
//{
//	public static final long serialVersionUID = 20080923073434L;
//	
//	private Collection<Advertisement> uncovered;
//	
//	public Unadvertisement(Filter f)
//	{
//		this(f,null);
//	}
//	
//	
//	public Unadvertisement(Filter f, Collection<? extends Advertisement> c)
//	{
//		super(f);
//		this.uncovered = new LinkedList<Advertisement>();
//		if (c != null)
//		{
//			this.uncovered.addAll(c);
//		}
//	}
//	
//	
//	public Collection<? extends Advertisement> getUncovered()
//	{
//		return new LinkedList<Advertisement>(uncovered);
//	}
//	
//
//	public boolean addUncovered(Advertisement a)
//	{
//		return uncovered.add(a);
//	}
//	
//	public void clearUncovered()
//	{
//		uncovered.clear();
//	}
//
//	
//	public boolean addUncovered(Filter f)
//	{
//		Advertisement a = new Advertisement(f);
//		a.setScopes(this.getScopes());
//		return uncovered.add(a);
//	}
//	
//	public boolean addAllUncovered(Collection<?> c)
//	{
//		boolean rst = false;
//		
//		for (Object o : c)
//		{
//			if (o instanceof Filter)
//			{
//				rst = addUncovered((Filter)o);
//				continue;
//			}
//			if (o instanceof Advertisement)
//			{
//				rst = addUncovered((Advertisement)o);
//				continue;
//			}
//			
//			throw new IllegalArgumentException();
//		}
//		return rst;
//	}
//}
