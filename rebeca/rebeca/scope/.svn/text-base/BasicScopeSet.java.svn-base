/*
 * $Id$
 */
package rebeca.scope;

import java.util.*;

import rebeca.*;
import rebeca.filter.ScopeIdentifier;
import rebeca.filter.ScopedFilter;

/**
 * @author parzy
 *
 */
public class BasicScopeSet implements ScopeSet 
{
	private static final long serialVersionUID = 20090501203453L;
	
	protected static final int DEFAULT_TYPE = ScopeSet.LEAF_SET;
	
	protected String[] scopes;
	
	// TODO parzy try removing the explicit typing
	protected int type;
	
	//protected int size;
	
	public BasicScopeSet()
	{
		this.scopes = new String[]{ "" };
		this.type = BasicScopeSet.DEFAULT_TYPE;
	}
	
	
	public BasicScopeSet(String[] scopes)
	{
		String s="";
		for (String t : scopes)
		{
			if (s.compareTo(t)>=0)
			{
				throw new IllegalArgumentException();
			}
			s=t;
		}
		this.scopes = new String[scopes.length];
		System.arraycopy(scopes, 0, this.scopes, 0, scopes.length);
		//this.size = scopes.length;
		this.type = DEFAULT_TYPE;
	}

	public BasicScopeSet(ScopeSet s)
	{
		this.scopes = s.toArray(new String[0], LEAF_SET);
		this.type = BasicScopeSet.DEFAULT_TYPE;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if ( !(o instanceof ScopeSet) )
		{
			return false;
		}
		
		ScopeSet scopes = (ScopeSet)o; 
		Iterator<String> it = this.iterator(ScopeSet.LEAF_SET);
		Iterator<String> jt = scopes.iterator(ScopeSet.LEAF_SET);
		while(it.hasNext() && jt.hasNext())		
		{
			String s = it.next(); String t = jt.next();
			if (!s.equals(t))
			{
				return false;
			}
		}
		return (!it.hasNext()) && (!jt.hasNext());
	}
	
//	protected Scope resolve(String name)
//	{
//		throw new UnsupportedOperationException();
//	}
	
	
//	public Event upward(Event e, ScopeSet otherScopes)
//	{
//		
//		
//		return null;
//	}
	

//	public Event upwardEvent(Event e, ScopeSet otherScopes) 
//	{
//		// initialize left and right
//		Iterator<String> it = otherScopes.iterator();
//		// TODO parzy replace "" by global scope constant
//		String left = it.hasNext() ? it.next() : "";
//		String right = it.hasNext() ? it.next() : "";
//
//		// iterate through all scopes e belongs to
//		for (String s : this) 
//		{
//			if (e == null) 
//			{
//				return null;
//			}
//			
//			// ensure that (left <= s < right) || (right == GLOBAL)
//			// TODO parzy replace ""
//			while (s.compareTo(right) >= 0 || right != "")
//			{
//				left = right;
//				// TODO parzy replace ""
//				right = it.hasNext() ? it.next() : "";
//			}
//
//			// determine least common super scope and transform event
//			String sup = determineSuperScopeName(s, left, right);
//			if (!s.equals(sup))
//			{
//				e = resolve(s).upwardEvent(e, sup);
//			}
//		}	 
//		return e;	
//	}
	
	
//	private String determineSuperScopeName(String s, String left, String right)
//	{
//		String x = determineSuperScopeName(s,left);
//		String y = determineSuperScopeName(s,right);
//		return x.length() > y.length() ? x : y;
//	}
//	
//	private String determineSuperScopeName(String s, String t)
//	{
//		// determine length of common prefix
//		int max = Math.min(s.length(), t.length());
//		int pos;
//		for (pos = 0; pos < max; pos++)
//		{
//			if (s.charAt(pos) != t.charAt(pos))
//			{
//				break;
//			}
//		}
//		
//		// return common super scope (possibly modified prefix)
//		if (pos > 0 && s.charAt(pos-1)=='.') 
//		{
//			pos -= 1;
//		}
//		return s.substring(0, pos);
//	}
	
//	public Filter upwardSubscriptionFilter(Filter f, ScopeSet otherScopes)
//	{
//		return upwardFilter(f, otherScopes, true);
//	}
//	
//	public Filter upwardAdvertisementFilter(Filter f, ScopeSet otherScopes)
//	{
//		return upwardFilter(f, otherScopes, false);
//	}
//	
//	// TODO parzy Maybe a boolean to distinguish between subscriptions and advertisements is not the best idea
//	public Filter upwardFilter(Filter f, ScopeSet otherScopes, boolean subscription) 
//	{
//		// initialize left and right
//		Iterator<String> it = otherScopes.iterator();
//		// TODO parzy replace "" by global scope constant
//		String left = it.hasNext() ? it.next() : "";
//		String right = it.hasNext() ? it.next() : "";
//
//		// iterate through all scopes f belongs to
//		for (String s : this) 
//		{
//			if (f == null) 
//			{
//				return null;
//			}
//			
//			// ensure that (left <= s < right) || (right == GLOBAL)
//			// TODO parzy replace ""
//			while (s.compareTo(right) >= 0 || right != "")
//			{
//				left = right;
//				// TODO parzy replace ""
//				right = it.hasNext() ? it.next() : "";
//			}
//
//			// determine least common super scope and transform event
//			String sup = determineSuperScopeName(s, left, right);
//			if (!s.equals(sup))
//			{
//				if (subscription)
//				{
//					f = resolve(s).upwardSubscriptionFilter(f, sup);
//				}
//				else
//				{
//					f = resolve(s).upwardAdvertisementFilter(f, sup);
//				}
//			}
//		}	 
//		return f;	
//	}
	
//	// TODO parzy check correctness?
//	public ScopeSet determineSuperscopeSet(ScopeSet otherScopes) 
//	{
//		ScopeSet set = new BasicScopeSet();
//		// initialize left and right
//		Iterator<String> it = otherScopes.iterator();
//		// TODO parzy replace "" by global scope constant
//		String left = it.hasNext() ? it.next() : "";
//		String right = it.hasNext() ? it.next() : "";
//
//		// iterate through all our scopes
//		for (String s : this) 
//		{
//			// ensure that (left <= s < right) || (right == GLOBAL)
//			// TODO parzy replace ""
//			while (s.compareTo(right) >= 0 || right != "")
//			{
//				left = right;
//				// TODO parzy replace ""
//				right = it.hasNext() ? it.next() : "";
//			}
//
//			// determine least common super scope and transform event
//			set.add(determineSuperScopeName(s, left, right));
//		}	 
//		return set;	
//	}
//	
//	public Subscription upwardSubscription(Subscription subscription, boolean advertisement)
//	{
//		
//		
//		return null;
//		
//	}
//	
//	
//	public Filter upward(ScopedFilter f, ScopeIdentifier ide)
//	{
//		// TODO parzy implement it
//		return null;
//	}
	
	
	
	// TODO parzy maybe rename it to equals?
	public boolean identical(ScopeSet s)
	{
		return equals(s);
	}
	
	
	public boolean subscopeSet(ScopeSet s)
	{
		return s.superscopeSet(this);
	}
	
	// TODO parzy maybe change sub and superscope set to make it more intuitiv
	/**
	 * Tests whether the given scope set s is a superscope set of this scope 
	 * set.
	 */
	public boolean superscopeSet(ScopeSet s)
	{
		Iterator<String> it = iterator();
		String sub = it.next();
		for (String sup : s)
		{
			while (!sub.startsWith(sup))
			{
				if (it.hasNext())
				{
					sub = it.next();
				}
				else
				{
					return false;
				}
			}
		}
		return true;
	}
	
	
	public boolean add(Scope s)
	{
		return add(s.getName());
	}
	
	@Override
	public boolean add(String s) 
	{
		int pos = Arrays.binarySearch(scopes, s);
		
		if (pos >= 0)
		{
			return false;
		}
		
		// Convert to expected insert position
		pos = -1-pos;

		// already containing a subscope?
		if ( pos < scopes.length && isSubscope(scopes[pos],s) )
		{
			return false;
		}
		
		// can we replace a superscope?
		if ( pos > 0 && isSubscope(s, scopes[pos-1]) )
		{
			scopes[pos-1] = s;
			return true;
		}

		// Insert the scope
		String[] tmp = scopes;
		scopes = new String[tmp.length+1];
		if (pos <= tmp.length)
		{
			System.arraycopy(tmp, pos, scopes, pos+1, tmp.length-pos);
		}
		scopes[pos] = s;
		if (pos > 0)
		{
			System.arraycopy(tmp, 0, scopes, 0, pos);
		}

		return true;
	}

	/* (non-Javadoc)
	 * @see java.util.Set#addAll(java.util.Collection)
	 */
	@Override
	public boolean addAll(Collection<? extends String> c) 
	{
		boolean modified = false;
		for (String s : c)
		{
			modified = add(s) || modified;
		}
		return modified;
	} 
	
	
	
	/* (non-Javadoc)
	 * @see java.util.SortedSet#comparator()
	 */
	public Comparator comparator() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.util.SortedSet#first()
	 */
	public String first() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.util.SortedSet#headSet(java.lang.Object)
	 */
	public SortedSet<String> headSet(String toElement) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.util.SortedSet#last()
	 */
	public String last() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.util.SortedSet#subSet(java.lang.Object, java.lang.Object)
	 */
	public SortedSet subSet(String fromElement, String toElement) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.util.SortedSet#tailSet(java.lang.Object)
	 */
	public SortedSet tailSet(String fromElement) {
		// TODO Auto-generated method stub
		return null;
	}


	/* (non-Javadoc)
	 * @see java.util.Set#clear()
	 */
	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.util.Set#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(Object o) 
	{
		if ( !(o instanceof String) )
		{
			return false;
		}
		
		// global scope is always contained
		String s = (String)o;
		if (s.length()==0)
		{
			return true;
		}
		
		// search scope in the array
		int pos = Arrays.binarySearch(scopes, s);		
		if (pos >= 0)
		{
			return true;
		}
		
		// test whether s is contained as superscope
		pos = -1-pos;
		if ( pos < scopes.length && 
			 scopes[pos].startsWith(s+Scope.SCOPE_SEPARATOR) )
		{
			return true;
		}
		
		return false;
	}

	/**
	 * Tests whether all members of the given collection are contained in the 
	 * set.
	 * 
	 * @param c collection of scopes for which their set membership is checked
	 * @return true when all members of the collection are contained in the set, 
	 *         otherwise false.
	 */
	public boolean containsAll(Collection<?> c) 
	{
		for (Object o : c)
		{
			if (!contains(o))
			{
				return false;
			}
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see java.util.Set#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}


	public Iterator<String> iterator() 
	{
		return iterator(this.type);
	}

	public Iterator<String> iterator(int type)
	{
		return new ScopeSetIterator(type);
	}
	
	class ScopeSetIterator implements Iterator<String>
	{
		
		int type;
		int pos;
		String cur;
		
		ScopeSetIterator(int type)
		{
			if ( type != ScopeSet.LEAF_SET && 
				 type != ScopeSet.PREORDER_SET &&
				 type != ScopeSet.POSTORDER_SET )
			{
				throw new IllegalArgumentException();
			}
			this.type = type;
			this.pos = -1;
			this.cur = "";
		}
		
		public boolean hasNext()
		{
			switch (type)
			{
			case ScopeSet.LEAF_SET:
				return pos+1 < scopes.length;
				
			case ScopeSet.PREORDER_SET:
				return (pos+1 < scopes.length) ||
				       (scopes.length > 0 && cur != scopes[pos]);

			case ScopeSet.POSTORDER_SET:
				return pos+1 < scopes.length || !cur.equals("");
				
			}
			throw new IllegalStateException();
		}
		
		public String next()
		{
			try
			{
				switch (type)
				{
				case ScopeSet.LEAF_SET:
					return scopes[++pos];

					
				case ScopeSet.PREORDER_SET:
					// first element?
					if (pos == -1)
					{
						pos = 0; cur = ""; // TODO parzy replace ""
						return cur;
					}
				
					// next scope path?
					if (cur == scopes[pos])
					{	
						cur = determineSuperScopeName(scopes[pos],scopes[pos+1]);
						pos++;
					}
				
					// determine next scope separator
					int cut = scopes[pos].indexOf(Scope.SCOPE_SEPARATOR, cur.length()+1);
					if (cut == -1)
					{   // a scope leaf is reached
						cur = scopes[pos];
					}
					else
					{   // still on the downward path
						cur = scopes[pos].substring(0, cut);
					}
					return cur;			
					
					
				case ScopeSet.POSTORDER_SET:					
					// first element?
					if (pos == -1)
					{
						return cur = scopes[pos=0];
					}
					// last element?
					if ( cur.equals("") )
					{ 
						throw new NoSuchElementException();
					}
					
					// determine next upper scope
					cur = determineSuperscopeName(cur);

					// check whether to change to next leaf
					if (pos+1<scopes.length && scopes[pos+1].startsWith(cur))
					{
						return cur = scopes[++pos];
					}
					return cur;
				}
			} 
			catch (IndexOutOfBoundsException e)
			{
				throw new NoSuchElementException();
			}
			
			throw new IllegalStateException();
		}
		
		private String determineSuperScopeName(String s, String t)
		{
			// determine length of common prefix
			int max = Math.min(s.length(), t.length());
			int pos;
			for (pos = 0; pos < max; pos++)
			{
				if (s.charAt(pos) != t.charAt(pos))
				{
					break;
				}
			}
			
			// return common super scope (possibly modified prefix)
			if (pos > 0 && s.charAt(pos-1)=='.') 
			{
				pos -= 1;
			}
			return s.substring(0, pos);
		}
	
			
		public void remove()
		{
			throw new UnsupportedOperationException();
		}	
	}
	
	public boolean remove(Object o) 
	{
		if ( !(o instanceof String) )
		{
			return false;
		}

		// determine string's (insert) position 
		String s = (String)o;
		int start = Arrays.binarySearch(scopes, s);		
		if (start < 0)
		{
			start = -1-start;
		}
			
		// count how many scope entries have to be considered?
		int end = start;
		while (  end<scopes.length && (s.equals(scopes[end]) || isSubscope(scopes[end],s)) )
		{
			end++;
		}
		
		// prefix or string not found? 
		if (start==end) return false;
		
		String superscope = determineSuperscopeName(s);
		if ( (start<=0 || !isSubscope(scopes[start-1],superscope)) && 
			 (end>=scopes.length || !isSubscope(scopes[end],superscope)) )
		{
			scopes[start]=superscope;
			start++;
		}
		
		// remove scopes by copying retaining scopes to a smaller array
		String[] tmp = new String[scopes.length-end+start];
		
		if (start > 0)
		{
			System.arraycopy(scopes, 0, tmp, 0, start);
		}
		if (end < scopes.length)
		{
			System.arraycopy(scopes, end, tmp, start, scopes.length-end);
		}
		scopes = tmp;
		return true;
	}

	public boolean removeAll(Collection<?> c) 
	{
		boolean modified = false;
		for (Object o : c)
		{
			modified = remove(o) || modified;
		}
		return modified;
	}

	/* (non-Javadoc)
	 * @see java.util.Set#retainAll(java.util.Collection)
	 */
	@Override
	public boolean retainAll(Collection c) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.util.Set#size()
	 */
	@Override
	public int size() 
	{
		return size(type);
	}
	
	public int size(int type)
	{
		switch (type)
		{
		case LEAF_SET:
			return scopes.length;
		default:
			int r = 0;
			for (Iterator<String> it = iterator(type); it.hasNext(); it.next()) r++;
			return r;
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Set#toArray()
	 */
	@Override
	public Object[] toArray() 
	{
		return toArray(type);
	}
	
	public Object[] toArray(int type)
	{
		return toArray(new Object[0], type);
	}

	/* (non-Javadoc)
	 * @see java.util.Set#toArray(T[])
	 */
	@Override
	public <T> T[] toArray(T[] a) 
	{
		return toArray(a,type);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T[] toArray(T[] a, int type)
	{
		switch (type)
		{
		case LEAF_SET:
	        T[] r = a.length >= scopes.length ? a : (T[])java.lang.reflect.Array
	        		.newInstance(a.getClass().getComponentType(),scopes.length);
	        System.arraycopy(scopes, 0, r, 0, scopes.length);
	        return r;
		default:
			int size = size(type);
            T[] s = a.length >= size ? a : (T[])java.lang.reflect.Array
        		.newInstance(a.getClass().getComponentType(),size);
            Iterator<String> it = iterator(type);
            for (int i = 0; i<size; i++)
            {
            	s[i] = (T)it.next();
            }
            return s;
		}
	}
	
	
	// visualization ----------------------------------------------------------
	// ------------------------------------------------------------------------
	
	public String toString()
	{
		String viz = "";
		for (Iterator<String> it = iterator(); it.hasNext();)
		{
			viz += it.next();
			if (it.hasNext())
			{
				viz += ",";
			}
		}
		return "{" + viz + "}";
	}
	
	// helpers ----------------------------------------------------------------
	// ------------------------------------------------------------------------
	
	public String determineSuperscopeName(String scope)
	{
		int i = scope.lastIndexOf('.');
		return i>0 ? scope.substring(0,i) : ""; 
	}
	
	public boolean isSubscope(String subscope, String superscope)
	{
		return superscope.length()==0 || subscope.startsWith(superscope+".");
		
	}
}
