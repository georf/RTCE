/*
 * $id$
 */
package rebeca.util;

import rebeca.peersim.Copier;

/**
 * @author parzy
 *
 */
public class UniqueName implements Id 
{
	private static final long serialVersionUID = 20090706174545L;
	
	protected String name;
	
	// TODO parzy handle null pointer appropriately
	public UniqueName(String name)
	{
		// prevent null pointers
		if (name == null)
		{
			throw new NullPointerException();
		}
		this.name = name;
	}
	
	@Override
	public boolean equals(Object id)
	{
		// check classes first
		if ( !(id instanceof UniqueName) )
		{
			return false;
		}
		
		// test names for equality afterwards
		String otherName = ((UniqueName)id).name;
		return this.name.equals(otherName);
	}
	
	@Override
	public int hashCode()
	{
		return name.hashCode();
	}
	
	@Override
	public String toString()
	{
		return name;
	}
	
	@Override
	public int compareTo(Id id) 
	{
		// compare names if id is another unique name
		if (id instanceof UniqueName)
		{
			String otherName = ((UniqueName)id).name;
			return this.name.compareTo(otherName);
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
}
