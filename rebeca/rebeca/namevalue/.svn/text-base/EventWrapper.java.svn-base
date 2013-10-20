/**
 * 
 */
package rebeca.namevalue;

import java.util.Collection;
import java.util.Iterator;

import rebeca.Event;
import rebeca.scope.ScopeSet;

/**
 * @author parzy
 *
 */
public class EventWrapper extends Event implements AttributeSet {

	protected AttributeSet set;
	
	public EventWrapper() 
	{
		super();
	}

	public EventWrapper(AttributeSet set)
	{
		this();
		setAttributes(set);
	}
	
	/**
	 * @return the set
	 */
	public AttributeSet getAttributes() {
		return set;
	}

	/**
	 * @param set the set to set
	 */
	public void setAttributes(AttributeSet set) {
		this.set = set;
	}

	// delegate methods -------------------------------------------------------
	// ------------------------------------------------------------------------
	@Override public boolean containsAttribute(String attribute) { return set.containsAttribute(attribute); }
	@Override public boolean containsValue(Object value) { return set.containsValue(value); }
	@Override public Object delete(String attribute) { return set.delete(attribute); }
	@Override public Object get(String attribute) { return set.get(attribute); }
	@Override public Object put(String attribute, Object value) { return set.put(attribute, value); }
	@Override public boolean add(AttributeValuePair e) { return set.add(e); }
	@Override public boolean addAll(Collection<? extends AttributeValuePair> c) { return set.addAll(c); }
	@Override public void clear() { set.clear(); }
	@Override public boolean contains(Object o) { return set.contains(o); }
	@Override public boolean containsAll(Collection<?> c) { return set.containsAll(c); }
	@Override public boolean isEmpty() { return set.isEmpty(); }
	@Override public Iterator<AttributeValuePair> iterator() { return set.iterator(); }
	@Override public boolean remove(Object o) { return set.remove(o); }
	@Override public boolean removeAll(Collection<?> c) { return set.removeAll(c); }
	@Override public boolean retainAll(Collection<?> c) { return set.retainAll(c); }
	@Override public int size() { return set.size(); }
	@Override public Object[] toArray() { return set.toArray(); }
	@Override public <T> T[] toArray(T[] a) { return set.toArray(a); }
}
