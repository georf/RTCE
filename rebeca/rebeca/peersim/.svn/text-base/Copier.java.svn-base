package rebeca.peersim;

public interface Copier 
{
	/**
	 * Introduced for message serialization in event-driven simulations. 
	 * Classes that implement this interface must provide a copy method which
	 * clones message objects for usage with peersim.  The cloned object is 
	 * usually a combination of a shallow copy of references to immutable
	 * members and a deep copy for mutable fields.
	 * @param obj the object to be copied
	 * @return a copy of the object to use in peersim simulations
	 */
	public Object copy(Object obj);
}
