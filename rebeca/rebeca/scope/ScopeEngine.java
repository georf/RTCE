/*
 * $Id$
 */
package rebeca.scope;

import java.util.*;

import rebeca.*;
import rebeca.filter.*;
import rebeca.routing.*;

/**
 * @author parzy
 *
 */
public interface ScopeEngine extends BrokerEngine 
{
	// scope management
//	public void addScope(Scope scope);
//	public void removeScope(Scope scope);
//	public void removeScope(String name);
	public Scope getScope(String name);
//	public List<Scope> getSubscopes(String name);
//	public Scope getSuperscope(String name);
//	public String getSuperscopeName(String name);
	
	// engine interface
	public RoutingTable getSubscriptionTable();
	public RoutingTable getAdvertisementTable();
	
	// scope configuration
	public ScopeRepository getInitialScopes();
	public List<String> getDefaultScopes();
	public void setDefaultScopes(List<String> defaults);
	public void addDefault(String name);
	public void removeDefault(String name);
	
	// event and filter upwarding
	public Event upwardEvent(Event event, String name);
	public Event upwardEvent(Event event, ScopeSet set);
	public Filter upwardFilter(Filter filter, String name);
	public Filter upwardFilter(Filter filter, ScopeSet set);	
	public Filter upwardFilter(Filter filter, ScopeSet filterScopes, 
			ScopeSet otherScopes, ScopedFilter.Type type);
	
	// integration into filter framework
	public void setScopeEngine(Event event);
	public void unsetScopeEngine(Event event);
}
