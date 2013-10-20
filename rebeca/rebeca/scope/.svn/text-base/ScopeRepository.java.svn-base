package rebeca.scope;

import java.util.*;

import org.apache.log4j.Logger;



// TODO parzy merge this class with the scope utils
public class ScopeRepository 
{
	/** 
	 * Logger for scope repository. 
	 */
	private static final Logger LOG = Logger.getLogger(ScopeRepository.class);
	
	protected HashMap<String,Scope> scopes;
	
	public ScopeRepository()
	{
		this.scopes = new HashMap<String,Scope>();
	}
	
	public ScopeRepository getInitialCopy()
	{
		ScopeRepository rep = new ScopeRepository();
		for (Scope s : getSubscopes(""))
		{
			rep.add(s);
		}
		return rep;
	}
	
	public boolean add(Scope s)
	{
		return add(s,false);
	}
	
	public boolean add(Scope s, boolean force)
	{
		synchronized (scopes)
		{
			if (force || !contains(s))
			{
				scopes.put(s.getName(), s);
				if (LOG.isDebugEnabled())
				{
					LOG.debug("scope '" + s + "' added to repository");
				}
				return true;
			}
		}
		return false;
	}
	
	public boolean addAll(Collection<Scope> scopes)
	{
		boolean modified = false;
		for (Scope s : scopes) 
		{
			modified |= add(s);
		}
		return modified;
	}
	
	public boolean addAll(Collection<Scope> scopes, boolean force)
	{
		boolean modified = false;
		for (Scope s : scopes) 
		{
			modified |= add(s,force);
		}
		return modified;
	}
	
	public boolean update(Scope s)
	{
		return update(s,false);
	}
	
	public boolean update(Scope s, boolean force)
	{
		synchronized (scopes)
		{
			if (force || contains(s))
			{
				scopes.put(s.getName(), s);
				return true;
			}
		}
		return false;
	}
	
	public Scope get(String name)
	{
		synchronized (scopes)
		{
			return scopes.get(name);
		}
	}
	
	public boolean contains(Scope s)
	{
		return contains(s.getName());
	}
	
	public boolean contains(String name)
	{
		synchronized (scopes)
		{
			return scopes.get(name) != null;
		}
	}
	
	public void clear()
	{
		scopes.clear();
	}
	
	public boolean remove(Scope s)
	{
		return remove(s.getName());
	}
	
	// Remove scope including all subscopes
	public boolean remove(String name)
	{
		synchronized (scopes)
		{
			for(Scope s : getSubscopes(name))
			{
				remove(s.getName());
			}
			return scopes.remove(name) != null;
		}
	}
	
	
	public List<Scope> getSubscopes(String name)
	{
		LinkedList<Scope> subscopes = new LinkedList<Scope>();
		if (name != "" && !name.endsWith(""+Scope.SCOPE_SEPARATOR));
		{
			name = name + Scope.SCOPE_SEPARATOR;
		}
		synchronized (scopes)
		{
			for (Scope s : scopes.values())
			{
				if (s.getName().startsWith(name) &&
					s.getName().indexOf(Scope.SCOPE_SEPARATOR,name.length()) == -1)
				{
					subscopes.add(s);
				}
			}
		}
		return subscopes;
	}
	
	public Scope getSuperscope(String name)
	{
		return get(getSuperscopeName(name));
	}
	
	public String getSuperscopeName(String name)
	{
		int i = name.lastIndexOf('.');
		return i>=0?name.substring(0,i):"";
	}
	
	
	// TODO parzy check if necessary
	public Collection<Scope> values()
	{
		synchronized (scopes)
		{
			return new ArrayList<Scope>(scopes.values());
		}
	}
}
