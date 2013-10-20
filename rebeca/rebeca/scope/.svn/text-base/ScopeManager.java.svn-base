/*
 * $Id$
 */
package rebeca.scope;

/**
 * @author parzy
 *
 */
public interface ScopeManager 
{
	public void join(String name);
	public void leave(String name);
	public void open(Scope scope);
	public void close(String name);
	
	public Object putAttribute(String name, Object value);
	public Object removeAttribute(String name);
	public Object getProperty(String property);
	
	// TODO parzy remove this hack ASAP
	public void putScopes(ScopeRepository scopes);
}
