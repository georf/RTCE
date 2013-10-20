/*
 * $Id$
 */
package rebeca.component;

import rebeca.*;

/**
 * @author parzy
 *
 */
public interface Advertiser 
{
	public void advertise(Filter filter);
	public void unadvertise(Filter filter);
}

