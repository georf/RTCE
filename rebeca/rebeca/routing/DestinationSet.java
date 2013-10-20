/*
 * $id$
 */
package rebeca.routing;

import java.util.*;

/**
 * The class represents a set of destinations.  Additionally, an arbitrary value 
 * (for example a filter or a message) can be mapped to each of the destinations 
 * in the set.  When a new destination is added without specifying the value, 
 * the value can either be cloned from a previously given prototype value or 
 * simply be linked to singleton value previously specified for the whole 
 * destination set.
 * @author parzy
 */
public class DestinationSet<V> implements Set<Destination> 
{
	/**
	 * The interface a prototype value must implement in order to allow object
	 * cloning.  Unnecessary when the value of the destination set is a 
	 * singleton.
	 * @author parzy
	 */
	public interface Value extends Cloneable
	{
		/**
		 * Returns a copy of the object implementing this interface.  However,
		 * whether a shallow or deep copy is returned is not specified.  Please
		 * note, that no ConeNotSupportedException is thrown.  However, if an
		 * object cannot be cloned, a CloneNotSupportedException can be nested
		 * inside a RuntimeException.
		 * @return a copy of the object implementing this interface
		 */
		public Object clone(); 
	}
	
	/**
	 * The mapped value.
	 */
	private V value;
	
	/**
	 * Flag indicating whether the set is restricted to at most one destination.
	 * In this case, no addition of destinations is allowed anymore.
	 */
	private boolean restricted;
	
	/**
	 * Flag indicating whether newly added destinations are mapped to the 
	 * prototype instance or whether an own instance is cloned from the 
	 * prototype.
	 */
	private boolean singleton;

	/**
	 * The HashMap backing this set implementation.
	 */
	private HashMap<Destination,V> destinations;
	
	/**
	 * Creates an empty destination set.
	 */
	public DestinationSet()
	{
		this(null);
	}
	
	/**
	 * Creates an empty destination set.  Newly added destinations are mapped
	 * to the given value.
	 * @param value value to which newly added destinations are mapped
	 */
	public DestinationSet(V value)
	{
		this(value,true);
	}
	
	/**
	 * Creates an empty destination set.  Newly added destinations are mapped
	 * to the given value when working in singleton mode.  Otherwise a new
	 * object instance is cloned from a prototype. 
	 * @param value value to which newly added destinations are mapped
	 * @param singleton if true all newly added destinations are mapped to the 
	 *        same single value instance, otherwise a new instance value 
	 *        instance is cloned whenever a new destination is added.   
	 */
	public DestinationSet(V value, boolean singleton)
	{
		this.value = value;
		this.singleton = singleton;
		this.restricted = false;
		this.destinations = new LinkedHashMap<Destination,V>();
	}
	
	/**
	 * Adds a destination to the set if it is not already present.
	 * @return true if the set has been modified, otherwise false.
	 */
	@SuppressWarnings("unchecked")
	public boolean add(Destination destination) 
	{
		// Are we allowed to add new values?
		if (restricted)
		{
			return false;
		}
		
		// Do we simply have to link the singleton
		if(singleton)
		{
			return destinations.put(destination, value) != null;
		}

		if (value instanceof Value)
		{ 	// or clone an own instance of the value
			return destinations.put(destination,(V)((Value)value).clone())!=null;
		}
		
		throw new UnsupportedOperationException(new CloneNotSupportedException());
		
	}

	/**
	 * Adds all destinations (if not yet present) contained in the collection.
	 * @param c the collection containing the destinations to add
	 * @return true if the set has been modified.
	 */
	public boolean addAll(Collection<? extends Destination> c) 
	{
		// Are we allowed to add new values?
		if (restricted)
		{
			return false;
		}
		
		// Simply add every single destination
		boolean modified = false;
		for (Destination d : c)
		{
			modified = add(d) | modified;
		}
		return modified;
	}

	/**
	 * Adds all destinations (if not yet present) contained in the array.
	 * @param a the array containing the destinations to add
	 * @return true if the set has been modified.
	 */
	public boolean addAll(Destination[] a)
	{
		// Are we allowed to add new values?
		if (restricted)
		{
			return false;
		}
		
		// Simply add every single destination
		boolean modified = false;
		for (Destination d : a)
		{
			modified = add(d) | modified;
		}
		return modified;
	}

	/**
	 * Removes all destinations from this set.
	 */
	public void clear() 
	{
		destinations.clear();
		restricted = false;
	}

	/**
	 * Returns true if the set contains the specified destination.
	 * @param o the destination whose presence in this set is to be tested.
	 * @return true if the set contains the specified destination.
	 */
	public boolean contains(Object o) 
	{
		return destinations.containsKey(o);
	}

	/**
	 * Returns true if this set contains all of the destinations of the 
	 * specified collection.
	 * @param collection to be checked for containment in this set. 
	 * @return true if this set contains all of the elements of the specified 
	 * 		   collection. 
	 */
	public boolean containsAll(Collection<?> c) 
	{
		// Simply test every single destination
		for (Object o : c)
		{
			if (!contains(o))
			{
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Returns an iterator over the entries in this set.  An entry contains
	 * the destination as well as a mapped value.
	 * @return an iterator over the entries in this set.
	 */
	public Iterator<Map.Entry<Destination, V>> entryIterator()
	{
		return destinations.entrySet().iterator();
	}

	/**
	 * Returns the set of mappings of destinations to corresponding values. 
	 * @return a set of mappings of destinations to values.
	 */
	public Set<Map.Entry<Destination, V>> entrySet()
	{
		return destinations.entrySet();
	}

	
	/**
	 * Returns the mapped value to the specified destination.
	 * @param destination the destination
	 * @return the mapped value to the destination.
	 */
	public V get(Destination destination)
	{
		return destinations.get(destination);
	}
	
	/**
	 * Returns the prototype value or singleton value of this set, respectively.
	 * @return the set's prototype or singleton value.
	 */
	public V getValue()
	{
		return value;
	}

	/**
	 * Returns true if this set contains no destinations.
	 * @return true if this set is empty. 
	 */ 
	public boolean isEmpty() 
	{
		return destinations.isEmpty();
	}

	/**
	 * Returns true whether the same singleton value is mapped to each newly
	 * added destination.  If false is returned, however, a new instance is 
	 * cloned and mapped instead.
	 * @return true whether a single value instance is mapped to every newly
	 *         added destination.
	 */
	public boolean isSingleton()
	{
		return singleton;
	}

	/**
	 * Returns an iterator over the destinations in this set.
	 * @return an iterator over the destinations in this set.
	 */
	public Iterator<Destination> iterator() 
	{
		return destinations.keySet().iterator();
	}
	
	/**
	 * Maps the specified value to the specified destination in this "set".
	 * @param d the destination
	 * @param v the value
	 * @return the value previously mapped to the destination or null if the
	 *         destination is new.
	 */
	public V put(Destination d, V v)
	{
		return destinations.put(d, v);
	}
	
	/**
	 * Removes the specified destination from this set if it is present.
	 * @param o the destination to remove.
	 * @return true if the set contained the specified destination. 
	 */
	public boolean remove(Object o) 
	{
		return destinations.remove(o) != null;
	}

	/**
	 * Removes from this set all of its destinations that are contained in the 
	 * specified collection.
	 * @param c collection that defines which elements will be removed from this 
	 *        set.
	 * @return true if this set changed as a result of the call.   
	 */
	public boolean removeAll(Collection<?> c) 
	{
		// Simply remove every single object from c
		boolean modified = false;
		for (Object o : c)
		{
			modified = remove(o) | modified;
		}
		return modified;
	}
	
	/**
	 * Removes from this set all of its destinations that are contained in the 
	 * specified array.
	 * @param a array that defines which elements will be removed from this 
	 *        set.
	 * @return true if this set changed as a result of the call.   
	 */
	public boolean removeAll(Object[] a) 
	{
		// Simply remove every single object from a
		boolean modified = false;
		for (Object o : a)
		{
			modified = remove(o) | modified;
		}
		return modified;
	}
	
	/**
	 * Restricts the set to the specified destination.
	 * If the specified destination is not a member of the set, 
	 * the result is an empty set.
	 * @param destination the specified destination.
	 */
	public void restrictTo(Destination d)
	{
		// Save the associated value of the destination i fcontained
		V value = null;
		if (destinations.containsKey(d))
		{
			value = destinations.get(d);
		}
		else
		{
			d = null;
		}
		// clear the destination set
		destinations.clear();
		// and add the destination with its value again 
		if (d != null)
		{
			destinations.put(d, value);
		}
		// finally, mark the set as restricted
		restricted=true;
	}
	
	/**
	 * Retains only the destinations in this set that are contained in the 
	 * specified collection.
	 * @param c collection that defines which elements this set will retain.
	 * @return true if this set changed as a result of the call.  
	 */
	public boolean retainAll(Collection<?> c) 
	{
		boolean modified = false;
		for (Iterator<Map.Entry<Destination, V>> it = destinations.entrySet().iterator();
				it.hasNext();)
		{
			if (!c.contains(it.next().getKey()))
			{
				it.remove();
				modified = true;
			}
		}
		return modified;
	}
	
	/**
	 * Retains only the destinations in this set that are contained in the 
	 * specified array.
	 * @param a array that defines which elements this set will retain.
	 * @return true if this set changed as a result of the call.  
	 */
	public boolean retainAll(Object[] a) 
	{
		boolean modified = false;
		for (Iterator<Map.Entry<Destination, V>> it = destinations.entrySet().iterator();
				it.hasNext();)
		{
			if (arrayContains(a,it.next().getKey()))
			{
				it.remove();
				modified = true;
			}
		}
		return modified;
	}
	
	// little helper for the method above
	private boolean arrayContains(Object[] a, Object o)
	{
		for (Object x : a)
		{
			if ( x == null ? x == o : x.equals(o))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Defines how values are mapped to destinations. In singleton mode the
	 * very same value instance is mapped to all destinations. In prototype
	 * mode a new value instance is cloned first before this instance is mapped
	 * to a destination.
	 * @param singleton sets the singleton mode.
	 */
	@SuppressWarnings("unchecked")
	public void setSingleton(boolean singleton)
	{
		// Do we have anything to do?
		if (this.singleton == singleton)
		{
			return;
		}
		
		// For every entry in the destination set
		for (Map.Entry<Destination,V> entry : destinations.entrySet())
		{
			if (singleton)
			{	// link the singleton
				entry.setValue(value);
			}
			else
			{
				if (value instanceof Value)
				{ 	// or clone an own instance
					entry.setValue( (V)((Value)value).clone() );
				}
				else
				{
					throw new UnsupportedOperationException(
							new CloneNotSupportedException());
				}
			}
		}
	}
	
	/**
	 * Sets the singleton or prototype value, respectively.
	 * @param value the singleton or prototype value.
	 */
	public void setValue(V value)
	{
		this.value = value;
	}
	
	/**
	 * Returns the number of destinations in this set.
	 * @return the number of destinations in this set (its cardinality).
	 */
	public int size() 
	{
		return destinations.size();
	}

	/**
	 * Returns an array containing all of the destinations in this set.
	 * @return an array containing the destinations of this set. 
	 */
	public Object[] toArray() 
	{
		return destinations.keySet().toArray();
	}

	/**
	 * Returns an array containing all of the destinations in this set.
	 * @param a the array into which the elements of this set are to be stored,
	 *        if it is big enough; otherwise, a new array of the same runtime 
	 *        type is allocated for this purpose.
	 * @return an array containing the destinations of this set. 
	 */
	public <T> T[] toArray(T[] a) 
	{
		return destinations.keySet().toArray(a);
	}
	
	/**
	 * Returns an iterator over all mapped values.
	 * @return an iterator over all mapped values.
	 */
	public Iterator<V> valueIterator()
	{
		return destinations.values().iterator();
	}
}
