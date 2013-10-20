package rebeca.routing;

import rebeca.*;
import rebeca.scope.*;

/**
 * Defines a set of methods a client and a broker of a publish/subscribe
 * system must implement.
 */
public interface Destination extends EventProcessor
{
	// TODO parzy clarify usage of isBroker and isLocal
	/**
	 * Returns true if the object is a broker, otherwise false.
	 * @return true if the object is a broker, otherwise false.
	 */
	public boolean isBroker();
	
	/**
	 * Returns true if the object is a client, otherwise false. 
	 * @return true if the object is a client, otherwise false. 
	 */
	public boolean isLocal();	
	
	public ScopeSet scopeSet();
}
