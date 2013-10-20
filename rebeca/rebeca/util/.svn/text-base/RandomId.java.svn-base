package rebeca.util;

import java.util.*;

import rebeca.peersim.*;

public class RandomId implements Id {

	/**
     * Explicit serialVersionUID for interoperability.
     */
    private static final long serialVersionUID = 20081225102119L;
	
	protected UUID id;
	
	public RandomId()
	{
		id = UUID.randomUUID();
	}

	public boolean equals(Object id)
	{
		if ( !(id instanceof RandomId) )
		{
			return false;
		}
		
		UUID otherId = ((RandomId)id).id;
		return this.id.equals(otherId);
	}

	public int hashCode()
	{
		return id.hashCode();
	}
	
	public int compareTo(Id id) 
	{
		// compare ids if id is another unique id
		if (id instanceof RandomId)
		{
			UUID otherId = ((RandomId)id).id;
			return this.id.compareTo(otherId);
		}
		
		// compare class names otherwise
		String thisClass = this.getClass().getName();
		String otherClass = id.getClass().getName();
		return thisClass.compareTo(otherClass);
	}

	@Override
	public Object copy(Copier copier) throws CloneNotSupportedException 
	{
		return this;
	}

	@Override
	public String toString()
	{
		return id.toString();
	}
}
