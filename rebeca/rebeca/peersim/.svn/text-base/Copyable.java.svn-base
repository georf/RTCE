package rebeca.peersim;

/**
 * Introduced as replacement for expensive serializations in event-driven 
 * simulations.  Messages, for example notifications or subscriptions, are
 * encouraged to implement this interface and to provide a publicly available
 * copy method that clones the message.  Whether a deep or a shallow copy is
 * needed depends on the usage with the peersim simulator.  Please note that the 
 * annoying checked <code>CloneNotSupportedException</code> must still be 
 * handled since a subclass need a means of telling that it does not support 
 * copying even if its superclass does.
 * @author parzy
 */
public interface Copyable 
{	
	/**
	 * Creates a copy of this object instance.  Please note that it is up
	 * to the implementing class to create a deep copy or a shallow copy of
	 * this object.  The passed <code>copier</code> may be used to aid copying
	 * members that the object does not know how to clone, for example, 
	 * arbitrary entries in a list.  (As an adaptation of the visitor pattern
	 * the object may call the <code>copier</code> for assistance.)
	 * @param a copier instance the class may use to copy unknown object members
	 * @return a copy of the object
	 * @throws CloneNotSupportedException if the object does not support copying
	 */
	public Object copy(Copier copier) throws CloneNotSupportedException;
}
