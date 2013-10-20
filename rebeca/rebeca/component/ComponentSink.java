/*
 * $Id$
 */
package rebeca.component;

import rebeca.Component;
import rebeca.Event;
import rebeca.broker.EventSink;


/**
 * @author parzy
 *
 */
public interface ComponentSink extends EventSink
{	
	public Component getComponent();

	public <T> T getInterface(Class<T> clazz);
	public <T> T getBrokerInterface(Class<T> clazz);
	public <T> T getComponentInterface(Class<T> clazz);

}
