/**
 * 
 */
package rebeca.util;

import java.lang.ref.*;
import java.util.*;


/**
 * @author parzy
 *
 */
public class ElementPropertyValueMap<E,P,V> 
{
	/**
     * The default initial capacity - MUST be a power of two.
     */
    static final int INITIAL_CAPACITY = 64; //4;
    
    /**
     * The maximum capacity, used if a higher value is implicitly specified
     * by either of the constructors with arguments.
     * MUST be a power of two <= 1<<30.
     */
    static final int MAXIMUM_CAPACITY = 1 << 30;

    /**
     * The load factor used when none specified in constructor.
     */
    static final float LOAD_FACTOR = 0.75f;
    
	private interface Item<E,P,V>
	{
		V getValue();
    	E getElement();
    	P getProperty();
    	V setValue(V value);
    	
    	Map.Entry<E,V> getElementValueMapping();
    	Map.Entry<P,V> getPropertyValueMapping();

    	int hashCode();
	   	boolean equals(Object obj);
		boolean match(Object o, Object p);
		void remove();
		
		Item<E,P,V> getNext();	
		void setNext(Item<E,P,V> next);
		
		Item<E,P,V> getNextElement();
		void setNextElement(Item<E,P,V> next);
		Item<E,P,V> getPreviousElement();
		void setPreviousElement(Item<E,P,V> previous);

		Item<E,P,V> getNextProperty();
		void setNextProperty(Item<E,P,V> next);
		Item<E,P,V> getPreviousProperty();
		void setPreviousProperty(Item<E,P,V> previous);
	}	
	
	protected class Entry implements Item<E,P,V>
	{	
		// item containing the element key
		protected PropertyList properties;
		// item containing the property key
		protected ElementList elements;
		// the stored value
		protected V value;
		
		// reference to next item in the map's bucket chain
		protected Item<E,P,V> next;
		// references to next/previous item in the element list
		protected Item<E,P,V> nextElement, prevElement;
		// references to next/previous item in the property list
		protected Item<E,P,V> nextProperty, prevProperty;
		
		// constructor
		Entry(ElementList elements, PropertyList properties)
		{
			this.elements = elements;
			this.properties = properties;
			
			this.value = null;
			this.next = null; 
			this.nextElement = this.prevElement = null;
			this.nextProperty = this.prevProperty = null;
		}
		
		
		// basic getters and setters
		
		@Override @SuppressWarnings("unchecked")
		public E getElement()
		{
			return (E)properties.get();
		}
		
		@Override @SuppressWarnings("unchecked")
		public P getProperty()
		{
			return (P)elements.get();
		}

		@Override
		public V getValue()
		{
			return value;
		}
		
		@Override
		public V setValue(V value)
		{
			V rst = this.value;
			this.value = value;
			return rst;
		}
		
		protected Map.Entry<E,V> elementValueMapping = null;
		
		@Override
		public Map.Entry<E,V> getElementValueMapping()
		{
			return elementValueMapping!=null ? 
					elementValueMapping : (elementValueMapping=new ElementValueMapping()); 
		}
		
		protected class ElementValueMapping implements Map.Entry<E,V>
		{
			@Override public E getKey() { return getElement(); }
			@Override public V getValue() { return Entry.this.getValue(); }
			@Override public V setValue(V value) { return Entry.this.setValue(value); }
			@Override public String toString() { return getKey()+"="+getValue(); }
		}
	
		protected Map.Entry<P,V> propertyValueMapping;
	
		@Override
		public Map.Entry<P,V> getPropertyValueMapping()
		{
			return propertyValueMapping!=null ? 
					propertyValueMapping : (propertyValueMapping=new PropertyValueMapping()); 
		}
	
		protected class PropertyValueMapping implements Map.Entry<P,V>
		{
			@Override public P getKey() { return Entry.this.getProperty(); }
			@Override public V getValue() { return Entry.this.getValue(); }
			@Override public V setValue(V value) { return Entry.this.setValue(value); }
			@Override public String toString() { return getKey()+"="+getValue(); }
		}
		
		/**
		 * @return list of elements sharing the same property
		 */
		protected ElementList getElementList()
		{
			return elements;
		}
	
		/**
		 * @return list of properties set for same element
		 */
		protected PropertyList getPropertyList()
		{
			return properties;
		}

		// Tests
		public int hashCode()
		{
			return hash(properties,elements);
		}
		
		public boolean match(Object element, Object property)
		{
			return element==this || this.properties.match(element, null) && this.elements.match(null,property);
		}
		
		// TODO do we need to implement equals
		
		public void remove()
		{
			ElementPropertyValueMap.this.removeEntry(properties, elements);
		}
		
		@Override
		public String toString()
		{
			return "("+getElement()+", "+getProperty()+")="+getValue();
		}
		
		// Item implementation ------------------------------------------------
		// --------------------------------------------------------------------
		
		// TODO try to remove public
		@Override public Item<E,P,V> getNext() { return next; }
		@Override public void setNext(Item<E,P,V> next) {this.next = next;}
		@Override public Item<E,P,V> getNextElement() { return nextElement; }
		@Override public void setNextElement(Item<E,P,V> next) { nextElement = next; }
		@Override public Item<E,P,V> getNextProperty() { return nextProperty; }
		@Override public void setNextProperty(Item<E,P,V> next) {nextProperty = next; }
		@Override public Item<E,P,V> getPreviousElement() { return prevElement; }
		@Override public void setPreviousElement(Item<E,P,V> prev) { prevElement = prev; }
		@Override public Item<E,P,V> getPreviousProperty() { return prevProperty; }
		@Override public void setPreviousProperty(Item<E,P,V> prev) { prevProperty = prev; } 
	}

	private abstract class ItemList extends WeakReference<Object> implements Item<E,P,V>
	{	
		protected int length;
		// stores key's hash value as weak references may disappear
		protected int hash;
		// reference to next item in the map's bucket chain
		protected Item<E,P,V> next;
		// references to next/previous item in the element list
		protected Item<E,P,V> nextElement, prevElement;
		// references to next/previous item in the property list
		protected Item<E,P,V> nextProperty, prevProperty;
		
		ItemList(Object o)
		{
			super(o,queue);

			hash = o.hashCode();
			length = 0;
			
			this.next = null;
			this.prevElement = this.nextElement = this;
			this.nextProperty = this.prevProperty = this;
		}
		
		// an object is returned that is either an instanceof 
		@Override @SuppressWarnings("unchecked") 
		public E getElement() { return (E)get(); }
		@Override @SuppressWarnings("unchecked")
		public P getProperty() { return (P)get(); }
		
		@Override public V getValue() { throw new UnsupportedOperationException(); }
		@Override public V setValue(V value) { throw new UnsupportedOperationException(); } 
		@Override public Map.Entry<E,V> getElementValueMapping() { throw new UnsupportedOperationException(); }
		@Override public Map.Entry<P,V> getPropertyValueMapping() { throw new UnsupportedOperationException(); }
		
		public int hashCode()
		{
			return hash;
		}
		
		int size()
		{
			return length;
		}
		
		public boolean isEmpty()
		{
			return length==0;
		}
		
		abstract void addItem(Item<E,P,V> item);
		abstract void removeItem(Item<E,P,V> item);
		abstract void clearList();
		
		// TODO try to remove public
		@Override public Item<E,P,V> getNext() { return next; }
		@Override public void setNext(Item<E,P,V> next) {this.next = next;}
		@Override public Item<E,P,V> getNextElement() { return nextElement; }
		@Override public void setNextElement(Item<E,P,V> next) { nextElement = next; }
		@Override public Item<E,P,V> getNextProperty() { return nextProperty; }
		@Override public void setNextProperty(Item<E,P,V> next) {nextProperty = next; }
		@Override public Item<E,P,V> getPreviousElement() { return prevElement; }
		@Override public void setPreviousElement(Item<E,P,V> prev) { prevElement = prev; }
		@Override public Item<E,P,V> getPreviousProperty() { return prevProperty; }
		@Override public void setPreviousProperty(Item<E,P,V> prev) { prevProperty = prev; }
	}

	// list containing element value mappings and having a property as key
	private class ElementList extends ItemList
	{
		ElementList(Object property)
		{
			super(property);
		}
		
		@Override
		public void remove()
		{
			ElementPropertyValueMap.this.removeElementList(this);
		}
		
		// e is list or key of list, p must be null
		public boolean match(Object e, Object p)
		{
			return e==null && p!=null && (p==this || p.equals(get())); 
		}
		
		@Override
		void addItem(Item<E,P,V> item)
		{
			// insert into element list
			item.setPreviousElement(prevElement);
			prevElement.setNextElement(item);
			item.setNextElement(this);
			this.setPreviousElement(item);
			length++;
		}
		
		@Override
		void removeItem(Item<E,P,V> item)
		{
			// remove from element list
			Item<E,P,V> prev = item.getPreviousElement();
			Item<E,P,V> next = item.getNextElement();
	    	prev.setNextElement(next);
	    	next.setPreviousElement(prev);
	    	length--;
		}
		
		@Override
		void clearList()
		{
			Item<E,P,V> cur = this.getNextElement();  
			while (cur != this)
			{
				Item<E,P,V> next = cur.getNextElement();
				cur.remove();
				cur = next;
			}
		}
		
		@Override
		public String toString()
		{
			return "elements of " + get();
		}
	}
	
	// list containing property value mappings and having an element as key
	private class PropertyList extends ItemList
	{
		PropertyList(Object element)
		{
			super(element);
		}
		
		@Override
		public void remove()
		{
			ElementPropertyValueMap.this.removePropertyList(this);
		}

		// p is list or key of list, e must be null
		public boolean match(Object e, Object p)
		{
			return p==null && e!=null && (e==this || e.equals(get())); 
		}

		
		@Override
		void addItem(Item<E,P,V> item)
		{
			// insert into element list
			item.setPreviousProperty(prevProperty);
			prevProperty.setNextProperty(item);
			item.setNextProperty(this);
			this.setPreviousProperty(item);
			length++;
		}
		
		@Override
		void removeItem(Item<E,P,V> item)
		{
			// remove from element list
			Item<E,P,V> prev = item.getPreviousProperty();
			Item<E,P,V> next = item.getNextProperty();
	    	prev.setNextProperty(next);
	    	next.setPreviousProperty(prev);
	    	length--;
		}
		
		@Override
		void clearList()
		{
			Item<E,P,V> cur = this.getNextProperty();  
			while (cur != this)
			{
				Item<E,P,V> next = cur.getNextProperty();
				cur.remove();
				cur = next;
			}
		}
		
		@Override
		public String toString()
		{
			return "properties of " + get();
		}
	}

	
    /**
     * The table, resized as necessary. Length MUST Always be a power of two.
     */
    Item<E,P,V>[] table;

    /**
     * The number of key-value mappings contained in this map.
     */
    int size;

    /**
     * The next size value at which to resize (capacity * load factor).
     * @serial
     */
    int threshold;

    int maximumCapacity;
    
    /**
     * The load factor for the hash table.
     *
     * @serial
     */
    final float loadFactor;
    
    /**
     * Reference queue for cleared WeakEntries
     */
    final ReferenceQueue<Object> queue;
    
    final ElementList elements;
    
    final PropertyList properties;
    
    
    // constructors -----------------------------------------------------------
    // ------------------------------------------------------------------------
    
    @SuppressWarnings("unchecked")
    public ElementPropertyValueMap( int initialCapacity, int maximumCapacity, 
    		                        float loadFactor )
    {
    	// adjust capacity args
    	initialCapacity = Math.max(initialCapacity,INITIAL_CAPACITY);
    	this.maximumCapacity = Math.min(maximumCapacity,MAXIMUM_CAPACITY);
    	
    	// check load factor
        if (loadFactor <= 0 || Float.isNaN(loadFactor))
        {
            throw new IllegalArgumentException("illegal load factor: "
            		+ loadFactor);
        }
        
        // set capacity to a power of 2 which is greater than minimum capacity
        int capacity = 1;
        while (capacity < initialCapacity) { capacity <<= 1; }

        // initialize hash table related fields
        this.table = new Item[capacity];
        this.loadFactor = loadFactor;
        this.threshold = (int)(capacity * loadFactor);
        this.size = 0;
        
        // initialize remaining fields
        this.queue = new ReferenceQueue<Object>();
        this.elements = new ElementList(this);
        this.properties = new PropertyList(this);
    }
    
    public ElementPropertyValueMap( int initialCapacity, float loadFactor )
    {
    	this(initialCapacity, MAXIMUM_CAPACITY, loadFactor);
    }
    
    public ElementPropertyValueMap( int initialCapacity )
    {
    	this(initialCapacity, MAXIMUM_CAPACITY, LOAD_FACTOR);
    }
    
    public ElementPropertyValueMap()
    {
    	this(INITIAL_CAPACITY, MAXIMUM_CAPACITY, LOAD_FACTOR);
    }
    
    
    
    
    int hash(Object o, Object p)
    { 
    	return hash(o)^hash(p);
    }
    
    int hash(Object o)
    {
    	return o==null ? 0 : hash(o.hashCode());
    }
    
    int hash(int hash)
    {
    	// TODO parzy check and change formula
    	//hash ^= (hash >>> 20) ^ (hash >>> 12);
        //return hash ^ (hash >>> 7) ^ (hash >>> 4);
    	return hash;
    }
    
    int index(int hash)
    {
//    	int rst = index(hash,table.length);
//    	System.out.println("hash is "+hash+". index is "+rst+". legth is "+table.length+". threshold is "+threshold);
//    	return rst;
    	return index(hash,table.length);
    }
    
    int index(int hash, int length)
    {
    	return hash & (length-1);
    }
    
    public int size()
    {
    	return size;
    }
    
    // public business methods ------------------------------------------------
    // ------------------------------------------------------------------------
    
    public V put(E element, P property, V value)
    {
    	cleanup();
    	return putEntry(element,property,value);
    }
    
    public V get(Object element, Object property)
    {
    	cleanup();
    	Entry entry = getEntry(element,property,false);
    	return entry!=null ? entry.getValue() : null;
    }

    public boolean contains(Object element, Object property)
    {
    	cleanup();
    	return getEntry(element,property,false) != null;
    }
    
    public boolean containsElement(Object element)
    {
    	cleanup();
    	return getPropertyList(element,false) != null;
    }

    public boolean containsProperty(Object property)
    {
    	cleanup();
    	return getElementList(property,false) != null;
    }

    public V remove(Object element, Object property)
    {
    	cleanup();
    	Entry entry = removeEntry(element,property);
    	return entry!=null ? entry.getValue() : null;
    }
    
    public boolean removeElement(Object element)
    {
    	cleanup();
    	return removePropertyList(element) != null;
    }
    
    public boolean removeProperty(Object property)
    {
    	cleanup();
    	return removeElementList(property) != null;
    }
    
    
    // querying and inserting (new) entries -----------------------------------
    // ------------------------------------------------------------------------
    
    protected V putEntry(Object e, Object p, V value)
    {
    	// get entry item or create a new one
    	Entry entry = getEntry(e,p,true);
    	
    	// update value and return the old one
    	V rst = entry.getValue();
    	entry.setValue(value);
    	return rst;
    }
    
    // e is either the element key or the properties list containing the element key
    // p is either the property key or the elements list containing the property key
    protected Entry getEntry(Object e, Object p, boolean create)
    {
    	// return item when found or creation of a new entry is not desired
    	Entry entry = (Entry)getItem(e,p);
    	if (entry != null || !create)
    	{
    		return entry;
    	}
    		
    	// otherwise get/create corresponding element/property lists
    	ElementList elements = getElementList(p,true);
    	PropertyList properties = getPropertyList(e,true);
    	
    	// create and add the entry	
        entry = new Entry(elements,properties);
    	addItem(entry);
//    	int h1 = hash(e,p); int h2 = hash(entry); assert(h1==h2);
    	    	
    	// link entry into element/property lists
    	elements.addItem(entry);
    	properties.addItem(entry);
    	return entry;
    }

    protected ElementList getElementList(Object key, boolean create)
    {
    	// check whether the provided key is the list itself
    	if (key instanceof ElementPropertyValueMap.ElementList)
    	{
    		return (ElementList)key;
    	}
    	
    	// return list when found or creation of a new one is not desired
    	ElementList list = (ElementList)getItem(null,key);
    	if (list != null || !create)
    	{
    		return list;
    	}

    	// otherwise create a new element list
    	list = new ElementList(key);
    	addItem(list);
    	properties.addItem(list);
    	return list;	
    }

    protected PropertyList getPropertyList(Object key, boolean create)
    {
    	// check whether the provided key is the list itself
    	if (key instanceof ElementPropertyValueMap.PropertyList)
    	{
    		return (PropertyList)key;
    	}
    	
    	// return list when found or creation of a new one is not desired
    	PropertyList list = (PropertyList)getItem(key,null);
    	if (list != null || !create)
    	{
    		return list;
    	}

    	// otherwise create a new element list
    	list = new PropertyList(key);
    	addItem(list);
    	elements.addItem(list);
    	return list;	
    }
	
    protected Item<E,P,V> getItem(Object e, Object p)
    {
    	// hash and bucket calculation
    	int hash = hash(e,p);
    	int index = index(hash);
    	
    	// iterate over bucket's chain of items
    	for (Item<E,P,V> item = table[index]; item != null; item=item.getNext())
    	{
    		// return item when found
    		if (item.match(e,p))
    		{
    			return item;
    		}
    	}
     	return null;
    }
    
    protected void addItem(Item<E,P,V> item)
    {
    	// TODO parzy maybe use earlier calculation results
    	// hash and bucket calculation
    	int hash = hash(item);      
    	int index = index(hash);
    	
    	// insert item into bucket's chain
    	item.setNext(table[index]);
    	table[index] = item;
    
    	// increment size and check capacity
    	size++;    	
    	if (size>threshold)
    	{
    		resize(table.length*2);
    	}
    }
    
    
    // removing entries -------------------------------------------------------
    // ------------------------------------------------------------------------  
    
    protected Item<E,P,V> removeItem(Object e, Object p)
    {
       	// hash and bucket calculation
    	int hash = hash(e,p);
    	int index = index(hash);
    	
    	Item<E,P,V> cur, prev, next;
    	cur = prev = table[index];
    	while (cur != null)
    	{
       		next = cur.getNext();
       	 	if (cur.match(e,p))
    		{
       	 		if (prev == cur)
       	 		{ 
       	 			table[index] = next;
       	 		}
       	 		else
       	 		{
       	 			prev.setNext(next);
       	 		}
       	 		size--;
       	 		
       	 		return (Item<E,P,V>)cur;
    		}
    		prev = cur; 
    		cur = next;
    	}
       	return null;
    }	
    
    protected Entry removeEntry(Object e, Object p)
    {
    	// remove entry from map
    	Entry entry = (Entry)removeItem(e,p);
    	if (entry == null)
    	{
    		return null;
    	}
    	
    	// remove entry from element and property list
    	ElementList elements = entry.getElementList();
    	elements.removeItem(entry);
    	PropertyList properties = entry.getPropertyList();
    	properties.removeItem(entry);
    	
    	return entry;
    }

    protected ElementList removeElementList(Object o)
    {
    	// remove list from map 
    	ElementList list = (ElementList)removeItem(o,null);
    	if (list == null)
    	{
    		return null;
    	}

    	// ensure that all entries referenced by list have been removed
    	if (!list.isEmpty())
    	{
    		list.clearList();
    	}
    	
    	// remove list with property key from global list of properties
    	properties.removeItem(list);
    	return list;
    }

    protected PropertyList removePropertyList(Object o)
    {
    	// remove list from map 
    	PropertyList list = (PropertyList)removeItem(o,null);
    	if (list == null)
    	{
    		return null;
    	}

    	// ensure that all entries referenced by list have been removed
    	if (!list.isEmpty())
    	{
    		list.clearList();
    	}
    	
    	// remove list with element key from global list of elements
    	elements.removeItem(list);
    	return list;
    }

    
    // view foundations -------------------------------------------------------
    // ------------------------------------------------------------------------
    
    private abstract class ItemSet<T> extends AbstractSet<T>
    {   
    	protected ItemList list;
    	
    	ItemSet(ItemList list)
    	{
    		this.list = list;
    	}
    	
    	@Override
    	public int size()
    	{
    		return list.size();
    	}
    }
    
    private abstract class ItemIterator
    {
		ItemList head;
		Item<E,P,V> last, next;
		
		// strong references to avoid disappearance of keys
		Object nextElement;
		Object nextProperty;
		
		Object lastElement;
		Object lastProperty;
		
		public ItemIterator(ItemList head)
		{
			this.head = head;
			this.next = head;
			this.last = null;
			this.nextElement = this.lastElement = null;
			this.nextProperty = this.lastProperty = null;
			// now set next to the first item
			this.next = peekItem();
		}
		
		public boolean hasNext()
		{
			cleanup();
			while (next != head)
			{
				// check whether both keys exist and establish a strong reference
				nextElement = next.getElement();
				nextProperty = next.getProperty();
				if (nextElement != null && nextProperty != null)
				{
					return true;
				}
				next = peekItem();
			}
			return false;
		}

		abstract Item<E,P,V> peekItem();
		
		Item<E,P,V> nextItem()
		{
			// hasNext also calls cleanup
			if (!hasNext())
			{
				throw new NoSuchElementException();
			}
			
			// store reference to returned item including strong references to keys
			last = next;
			lastElement = last.getElement();
			lastProperty = last.getProperty();
			
			// forward next and return last element
			next = peekItem();
			return last;
		}
		
		public void remove()
		{
			cleanup();
			last.remove();
			last = null;
		}
    }
   
    // basic views ------------------------------------------------------------
    // ------------------------------------------------------------------------
    
    ElementSet elementSet = null;
    
    public Set<E> elementSet()
    {
    	return elementSet!=null ? elementSet : (elementSet=new ElementSet(elements));
    }
    
    private class ElementSet extends ItemSet<E>
    {   
    	ElementSet(ItemList list)
    	{
    		super(list);
    	}
    	
    	@Override
    	public Iterator<E> iterator()
    	{
    		return new ElementIterator(list);
    	}
    	
    	@Override
    	public boolean contains(Object element)
    	{
    		return ElementPropertyValueMap.this.containsElement(element);
    	}
    	
    	@Override
    	public boolean remove(Object element)
    	{
    		return ElementPropertyValueMap.this.removeElement(element);
    	}
    }
    
    private class ElementIterator extends ItemIterator implements Iterator<E>
    {
    	ElementIterator(ItemList head)
    	{
    		super(head);
    	}
    	
    	Item<E,P,V> peekItem()
    	{
    		return next.getNextElement();
    	}
    	
    	public E next()
    	{
    		return nextItem().getElement();
    	}
    }
    
    PropertySet propertySet = null;
    
    public Set<P> propertySet()
    {
    	return propertySet!=null ? propertySet : (propertySet=new PropertySet(properties));
    }
    
    private class PropertySet extends ItemSet<P>
    {   
    	PropertySet(ItemList list)
    	{
    		super(list);
    	}
    	
    	@Override
    	public Iterator<P> iterator()
    	{
    		return new PropertyIterator(list);
    	}
    	
     	@Override
    	public boolean contains(Object property)
    	{
    		return ElementPropertyValueMap.this.containsProperty(property);
    	}
    	
    	@Override
    	public boolean remove(Object property)
    	{
    		return ElementPropertyValueMap.this.removeProperty(property);
    	}
    }
    
    private class PropertyIterator extends ItemIterator implements Iterator<P>
    {
    	PropertyIterator(ItemList head)
    	{
    		super(head);
    	}
    	
    	Item<E,P,V> peekItem()
    	{
    		return next.getNextProperty();
    	}
    	
    	public P next()
    	{
    		return nextItem().getProperty();
    	}
    }
   
    
    // advanced views ---------------------------------------------------------
    // ------------------------------------------------------------------------
    
    public Map<E,V> elementMap(P property)
    {
      	ItemList list = getElementList(property,true);
    	return new ElementValueMap(list);
    }
    
    private class ElementValueMap extends AbstractMap<E,V>
    {
    	private ItemList list;
    	private Set<Map.Entry<E,V>> set;
    	
    	ElementValueMap(ItemList list)
    	{
    		this.list = list;
    		this.set = null;
    	}
    	
    	@Override
    	public Set<Map.Entry<E,V>> entrySet()
    	{
    		return set!=null ? set : (set = new ElementValueSet(list));
    	}
    	
		@Override
    	public V put(E element, V value)
    	{
			cleanup();
    		return ElementPropertyValueMap.this.putEntry(element, list, value);
    	}
    	
		@Override
    	public boolean containsKey(Object element)
    	{
			cleanup();
    		return ElementPropertyValueMap.this.getEntry(element,list,false) != null;
    	}
		
    	@Override
    	public V get(Object element)
    	{
    		cleanup();
    		Item<E,P,V> item = ElementPropertyValueMap.this.getEntry(element, list, false);
    		return item != null ? item.getValue() : null;
    	}
    	
		@Override
		public V remove(Object element) 
		{		
			cleanup();
			Item<E,P,V> item = ElementPropertyValueMap.this.removeEntry(element, list);
    		return item != null ? item.getValue() : null;
		}
    }
    
    private class ElementValueSet extends ItemSet<Map.Entry<E,V>>
    {   
    	ElementValueSet(ItemList list)
    	{
    		super(list);
    	}
    	
    	@Override
    	public Iterator<Map.Entry<E,V>> iterator()
    	{
    		return new ElementValueIterator(list);
    	}
    }
    
    private class ElementValueIterator extends ItemIterator implements Iterator<Map.Entry<E,V>>
    {
    	ElementValueIterator(ItemList head)
    	{
    		super(head);
    	}
    	
    	Item<E,P,V> peekItem()
    	{
    		return next.getNextElement();
    	}
    	
    	public Map.Entry<E,V> next()
    	{
    		return nextItem().getElementValueMapping();
    	}
    }
    
    public Map<P,V> propertyMap(E element)
    {
    	ItemList list = getPropertyList(element,true);
    	return new PropertyValueMap(list);
    }
    
    private class PropertyValueMap extends AbstractMap<P,V>
    {
    	private ItemList list;
    	private Set<Map.Entry<P,V>> set;
    	
    	PropertyValueMap(ItemList list)
    	{
    		this.list = list;
    		this.set = null;
    	}
    	
    	@Override
    	public Set<Map.Entry<P,V>> entrySet()
    	{
    		return set!=null ? set : (set = new PropertyValueSet(list));
    	}
    	
		@Override
    	public V put(P property, V value)
    	{
			cleanup();
    		return ElementPropertyValueMap.this.putEntry(list, property, value);
    	}
    	
		@Override
    	public boolean containsKey(Object property)
    	{
			cleanup();
    		return ElementPropertyValueMap.this.getEntry(list,property,false) != null;
    	}
		
    	@Override
    	public V get(Object property)
    	{
    		cleanup();
    		Item<E,P,V> item = ElementPropertyValueMap.this.getEntry(list,property,false);
    		return item != null ? item.getValue() : null;
    	}
    	
		@Override
		public V remove(Object property) 
		{	
			cleanup();
			Item<E,P,V> item = ElementPropertyValueMap.this.removeEntry(list,property);
    		return item != null ? item.getValue() : null;
		}
    }
    
    private class PropertyValueSet extends ItemSet<Map.Entry<P,V>>
    {   
    	PropertyValueSet(ItemList list)
    	{
    		super(list);
    	}
    	
    	@Override
    	public Iterator<Map.Entry<P,V>> iterator()
    	{
    		return new PropertyValueIterator(list);
    	}
    }
    
    private class PropertyValueIterator extends ItemIterator implements Iterator<Map.Entry<P,V>>
    {
    	PropertyValueIterator(ItemList head)
    	{
    		super(head);
    	}
    	
    	Item<E,P,V> peekItem()
    	{
    		return next.getNextProperty();
    	}
    	
    	public Map.Entry<P,V> next()
    	{
    		return nextItem().getPropertyValueMapping();
    	}
    }

 
    // house keeping ----------------------------------------------------------
    // ------------------------------------------------------------------------
     
    @SuppressWarnings("unchecked")
    protected void cleanup()
    {
    	Item<E,P,V> item;
    	while ( (item=(Item<E,P,V>)queue.poll()) != null)
    	{
    		item.remove();
    	}
    }
    
    void resize(int capacity)
    {
    	if (table.length >= maximumCapacity)
    	{
    		threshold = Integer.MAX_VALUE;
    		return;
    	}
    	
    	@SuppressWarnings("unchecked")
    	Item<E,P,V>[] newTable = new Item[capacity];
    	transfer(table,newTable);
    	table = newTable;
    	threshold = (int)(capacity*loadFactor);
    }
    
    void transfer(Item<E,P,V>[] src, Item<E,P,V>[] dst)
    {
    	Item<E,P,V> cur, next;
    	for (int i=0; i<src.length; i++)
    	{
    		cur = src[i];
    		src[i] = null;
    		
    		while(cur != null)
    		{
    			next = cur.getNext();
    			int hash = hash(cur);
    			int index = index(hash,dst.length);
    			cur.setNext(dst[index]);
    			dst[index] = cur;
    			cur = next;
    		}
    	}
    }
}    
    
