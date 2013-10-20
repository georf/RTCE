/*
 * $Id$
 */
package rebeca;

import rebeca.*;
import rebeca.scope.*;

/**
 * @author parzy
 *
 */
public interface ComponentBroker 
{
	public void publish(Event event);
	public void subscribe(Filter filter);
	public void unsubscribe(Filter filter);
	public void advertise(Filter filter);
	public void unadvertise(Filter filter);
	public <T> T getInterface(Class<T> clazz);
	// TODO parzy may remove these interfaces again
	public void join(String name);
	public void leave(String name);
	public void open(Scope scope);
	public void close(String name);
	// TODO parzy attribute management
	public Object putAttribute(String attribute, Object value);
	public Object removeAttribute(String attribute);
	public Object getProperty(String property);
	
	// TODO parzy remove this hack ASAP
	public void putScopes(ScopeRepository scopes);
}
