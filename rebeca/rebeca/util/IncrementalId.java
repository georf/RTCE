/*
 * $id$
 */
package rebeca.util;

import java.util.UUID;

import rebeca.peersim.Copier;

/**
 * @author parzy
 *
 */
public class IncrementalId implements Id 
{

	/**
     * Explicit serialVersionUID for interoperability.
     */
    private static final long serialVersionUID = 20090111200930L;
	
    private static long counter;
    
    private long id;
    
    public static void setCounter(long value)
    {
    	counter = value;
    }
    
    private static synchronized long nextId()
    {
    	return counter++;
    }
    
    public IncrementalId()
    {
    	id = nextId();
    }
    
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Id id) 
	{
		if (id instanceof IncrementalId)
		{
			long otherId = ((IncrementalId)id).id;
			return this.id<otherId ? -1 : (this.id==otherId ? 0 : 1);
		}
		
		String thisClass = this.getClass().getName();
		String otherClass = id.getClass().getName();
		return thisClass.compareTo(otherClass);
	}

	public boolean equals(Object id)
	{
		if ( !(id instanceof IncrementalId))
		{
			return false;
		}
		long otherId = ((IncrementalId)id).id;
		return this.id == otherId;
	}
	
	public int hashCode()
	{
		return (int)(id ^ (id >>> 32));
	}
	
	/* (non-Javadoc)
	 * @see rebeca.sim.Copyable#copy(rebeca.sim.Copier)
	 */
	@Override
	public Object copy(Copier copier) throws CloneNotSupportedException 
	{
		return this;
	}

	public String toString()
	{
		return ""+id;
	}
	
}
