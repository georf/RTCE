package rebeca.scope;

import java.io.*;
import java.util.*;

import rebeca.*;
import rebeca.filter.*;

public interface ScopeSet extends Set<String>, Serializable 
{
	
	public static final int LEAF_SET = 0;
	public static final int PREORDER_SET = 1;
	public static final int POSTORDER_SET = 2;
	
//	public Event upwardEvent(Event e, ScopeSet otherScopes);
//	public Filter upwardSubscriptionFilter(Filter f, ScopeSet otherScopes);
//	public Filter upwardAdvertisementFilter(Filter f, ScopeSet otherScopes);
//	
//	public Filter upward(ScopedFilter f, ScopeIdentifier ide);
	
	public boolean identical(ScopeSet s);
	
	public boolean subscopeSet(ScopeSet s);
	
	public boolean superscopeSet(ScopeSet s);
	
//	public ScopeSet determineSuperscopeSet(ScopeSet otherScopes);
	
	public Iterator<String> iterator(int type);
	
	public <T> T[] toArray(T[] a, int type);
	
	public Object[] toArray(int type);
	
	public boolean add(Scope s);
}
