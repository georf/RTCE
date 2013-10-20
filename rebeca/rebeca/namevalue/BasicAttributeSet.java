/*
 * $Id$
 */
package rebeca.namevalue;

import java.io.*;
import java.util.*;

/**
 * @author parzy
 *
 */
public class BasicAttributeSet implements AttributeSet, Serializable 
{

	private static final long serialVersionUID = 20090624185956L;
	
	private HashMap<String,Object> map;
	
	public BasicAttributeSet()
	{
		map = new LinkedHashMap<String,Object>();
	}
	
	public BasicAttributeSet(Collection<AttributeValuePair> c)
	{
		map = new LinkedHashMap<String,Object>();
		for (AttributeValuePair pair : c)
		{
			map.put(pair.getKey(), pair.getValue());
		}
	}
	
	public BasicAttributeSet(AttributeValuePair[] a)
	{
		map = new LinkedHashMap<String,Object>();
		for (AttributeValuePair pair : a)
		{
			map.put(pair.getKey(), pair.getValue());
		}
	}

	public boolean containsAttribute(String attribute) 
	{
		return map.containsKey(attribute);
	}

	// TODO: parzy just for debugging, remove later
	public Set<String> keySet()
	{
		return map.keySet();
	}
	
	public boolean containsValue(Object value) 
	{
		return map.values().contains(value);
	}

	public Object delete(String attribute) 
	{
		return map.remove(attribute);
	}

	public Object get(String attribute) 
	{
		return map.get(attribute);
	}

	public Object put(String attribute, Object value) 
	{
		return map.put(attribute, value);
	}

	public boolean add(AttributeValuePair pair)
	{
		if (map.containsKey(pair.getAttribute()))
		{
			return false;
		}
		map.put(pair.getAttribute(), pair.getValue());
		return true;
	}

	public boolean addAll(Collection<? extends AttributeValuePair> c) 
	{
		boolean modified = false;
		for (AttributeValuePair pair : c)
		{	
			if ( add(pair) )
			{
				modified = true;
			}
		}
		return modified;
	}

	public void clear() 
	{
		map.clear();
	}

	public boolean contains(Object o) 
	{
		return map.entrySet().contains(o);
	}

	public boolean containsAll(Collection<?> c) 
	{
		return map.entrySet().containsAll(c);
	}

	public boolean isEmpty() 
	{
		return map.isEmpty();
	}

	public Iterator<AttributeValuePair> iterator() 
	{
		return new EntryAttributeValueIterator(map.entrySet().iterator());
	}
	
	static class EntryAttributeValueIterator implements Iterator<AttributeValuePair>
	{
		Iterator<Map.Entry<String, Object>> it;
		
		EntryAttributeValueIterator(Iterator<Map.Entry<String,Object>> it)
		{
			this.it = it;
		}
		public boolean hasNext()
		{
			return it.hasNext();
		}
		public AttributeValuePair next()
		{
			return new AttributeValueEntry(it.next());
		}
		public void remove()
		{
			it.remove();
		}
	}
	
	static class AttributeValueEntry implements AttributeValuePair 
	{
		Map.Entry<String, Object> entry;
		
		AttributeValueEntry(Map.Entry<String, Object> entry)
		{
			this.entry = entry;
		}
		
		public Object getValue()
		{
			return entry.getValue();
		}
		public Object setValue(Object value)
		{
			return entry.setValue(value);
		}
		public String getKey()
		{
			return entry.getKey();
		}
		public String getAttribute()
		{
			return entry.getKey();
		}
		public String getName()
		{
			return entry.getKey();
		}
		public boolean equals(Object o)
		{
			return entry.equals(o);
		}
		public int hashCode(Object o)
		{
			return entry.hashCode();
		}
	}

	public boolean remove(Object o) 
	{
		if ( o instanceof AttributeValuePair )
		{
			AttributeValuePair pair = (AttributeValuePair)o;
			if (map.containsKey(pair.getKey()))
			{
				Object value = map.get(pair.getKey());
				if ( value == null ? pair.getValue() == null
					               : value.equals(pair.getValue()) )
				{
					map.remove(pair.getKey());
					return true;
				}
			}
		}
		return false;
	}

	public boolean removeAll(Collection<?> c) 
	{
		boolean modified = false;
		for (Object o : c)
		{	
			if ( remove(o) )
			{
				modified = true;
			}
		}
		return modified;
	}

	public boolean retainAll(Collection<?> c) 
	{
		boolean modified = false;
		Iterator<Map.Entry<String,Object>> it = map.entrySet().iterator(); 
		while (it.hasNext()) 
		{
			if (!c.contains(it.next()))
			{
				it.remove();
				modified = true;
			}
		}
		return modified;
	}

	public int size() 
	{
		return map.size();
	}

	public Object[] toArray() 
	{
		Object[] r = new Object[size()];
	    Iterator<AttributeValuePair> it = iterator();
		for (int i = 0; i < r.length; i++) 
		{
		    r[i] = it.next();
		}
		return r;
	}

	@SuppressWarnings("unchecked")
	public <T> T[] toArray(T[] a) {
		int size = size();
		T[] r = a.length >= size ? a : 
				(T[])java.lang.reflect.Array
		        .newInstance(a.getClass().getComponentType(), size);
		Iterator<AttributeValuePair> it = iterator();
		for (int i = 0; i < r.length; i++) {
			if (! it.hasNext()) 
			{
				r[i] = null; // null-terminate
				return r;
			}
			r[i] = (T)it.next();
		}
		return r;
	}
	
	@SuppressWarnings("unchecked")
	public Object clone()
	{
		BasicAttributeSet clone = null;
		try 
		{
			clone = (BasicAttributeSet)super.clone();
			clone.map = (HashMap<String,Object>)this.map.clone();
		}
		catch (CloneNotSupportedException e) { /* never happens */ }
		
		return clone;
	}
	
}
