/*
 * $id$
 */
package rebeca.namevalue;

import java.util.*;

import rebeca.Event;

/**
 * @author parzy
 *
 */
public class NameValueEvent extends Event implements AttributeSet {

	private static final long serialVersionUID = 20090615134512L;
	
	protected HashMap<String,Object> map = new LinkedHashMap<String,Object>();
	
	
	public Object clone()
	{
		NameValueEvent clone = (NameValueEvent)super.clone();
		clone.map = new LinkedHashMap<String,Object>(map);
		return clone;
	}
	
	
	/* (non-Javadoc)
	 * @see rebeca.namevalue.AttributeSet#containsAttribute(java.lang.String)
	 */
	@Override
	public boolean containsAttribute(String attribute) 
	{
		return map.containsKey(attribute);
	}

	public boolean containsValue(Object value) 
	{
		return map.values().contains(value);
	}

	
	/* (non-Javadoc)
	 * @see rebeca.namevalue.AttributeSet#delete(java.lang.String)
	 */
	@Override
	public Object delete(String attribute) 
	{
		return map.remove(attribute);
	}

	/* (non-Javadoc)
	 * @see rebeca.namevalue.AttributeSet#get(java.lang.String)
	 */
	@Override
	public Object get(String attribute) 
	{
		return map.get(attribute);
	}

	/* (non-Javadoc)
	 * @see rebeca.namevalue.AttributeSet#put(java.lang.String, rebeca.namevalue.Value)
	 */
	@Override
	public Object put(String attribute, Object value) 
	{
		return map.put(attribute, value);
	}

	/* (non-Javadoc)
	 * @see java.util.Set#add(java.lang.Object)
	 */
	@Override
	public boolean add(AttributeValuePair pair)
	{
		if (map.containsKey(pair.getAttribute()))
		{
			return false;
		}
		map.put(pair.getAttribute(), pair.getValue());
		return true;
	}

	/* (non-Javadoc)
	 * @see java.util.Set#addAll(java.util.Collection)
	 */
	@Override
	public boolean addAll(Collection<? extends AttributeValuePair> c) 
	{
		boolean result = false;
		for (AttributeValuePair pair : c)
		{	
			result = add(pair) | result;
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see java.util.Set#clear()
	 */
	@Override
	public void clear() 
	{
		map.clear();
	}

	/* (non-Javadoc)
	 * @see java.util.Set#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(Object o) 
	{
		return map.entrySet().contains(o);
	}

	/* (non-Javadoc)
	 * @see java.util.Set#containsAll(java.util.Collection)
	 */
	@Override
	public boolean containsAll(Collection<?> c) 
	{
		return map.entrySet().containsAll(c);
	}

	/* (non-Javadoc)
	 * @see java.util.Set#isEmpty()
	 */
	@Override
	public boolean isEmpty() 
	{
		// TODO Auto-generated method stub
		return map.isEmpty();
	}

	/* (non-Javadoc)
	 * @see java.util.Set#iterator()
	 */
	@Override
	public Iterator<AttributeValuePair> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	static class EntryAttributeValueIterator implements Iterator<AttributeValuePair>
	{
		Iterator<Map.Entry<String, Object>> it;
		
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

	
	/* (non-Javadoc)
	 * @see java.util.Set#remove(java.lang.Object)
	 */
	@Override
	public boolean remove(Object o) 
	{
		if ( o instanceof AttributeValuePair )
		{
			AttributeValuePair pair = (AttributeValuePair)o;
			if (map.containsKey(pair.getKey()))
			{
				Object value = map.get(pair.getKey());
				if ((value == null && pair.getValue() == null) ||
					(value != null && value.equals(pair.getValue())) )
				{
					map.remove(pair.getKey());
					return true;
				}
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see java.util.Set#removeAll(java.util.Collection)
	 */
	@Override
	public boolean removeAll(Collection<?> c) 
	{
		boolean result = false;
		for(Object o : c)
		{
			result = remove(o) | result;
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see java.util.Set#retainAll(java.util.Collection)
	 */
	@Override
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
	
	/* (non-Javadoc)
	 * @see java.util.Set#size()
	 */
	@Override
	public int size() 
	{
		return map.size();
	}

	/* (non-Javadoc)
	 * @see java.util.Set#toArray()
	 */
	@Override
	public Object[] toArray() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.util.Set#toArray(T[])
	 */
	@Override
	public <T> T[] toArray(T[] a) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String toString()
	{
		Iterator<Map.Entry<String,Object>> it = map.entrySet().iterator();
		if ( !it.hasNext() )
		{
			return "NameValueEvent()";
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("NameValueEvent(");
		for(;;)
		{
			Map.Entry<String,Object> e = it.next();
			String n = e.getKey();
			Object v = e.getValue();
			sb.append(n).append('=').append(v!=this ? v : "this");
			if (!it.hasNext())
			{
//				return sb.append(')').toString();
				sb.append(")@");
				sb.append(this.id);
				return sb.toString();
			}
			sb.append(',');
		}
	}		
}
