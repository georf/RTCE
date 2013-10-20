/*
 * $id$
 */
package rebeca.config;

import java.util.*;

/**
 * @author parzy
 * A profile is a specific configuration subset, for example, one may create a 
 * specific configuration profile for a client instance and different profile 
 * for a server instance.
 */
// TODO parzy this class is far from being complete
public class Profile 
{
	/**
	 * Maximum number of different Rebeca configuration profiles that are 
	 * supported in one JVM. 
	 */
	public static final int MAX_PROFILES = 16;
	
	List l;
}
