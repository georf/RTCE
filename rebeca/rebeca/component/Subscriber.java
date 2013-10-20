/*
 * $Id$
 */
package rebeca.component;

import rebeca.*;

/**
 * @author parzy
 *
 */
public interface Subscriber 
{
	public void subscribe(Filter filter);
	public void unsubscribe(Filter filter);
}
