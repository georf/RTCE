package rebeca;

import java.util.*;
import rebeca.broker.*;

/**
 * Alternative broker interface?
 * 
 * Please note, that all returned lists of contacts, sinks, and components are 
 * copies and no views. Thus, the lists are not linked with the broker state 
 * anymore. Added or removed contacts, sinks and components are not reflected
 * by these lists.
 * 
 * @author parzy
 *
 */
public interface Broker 
{	
	// boker management
	// public void init();
	public void init(ConfigurationEngine engine);
	public void startup();
	public void shutdown();
	
	// management of broker engines
	public void registerEngine(Object key, BrokerEngine engine);
	public void registerEngine(BrokerEngine engine);
	public BrokerEngine getEngine(Object key);
	public <T> T getEngine(Class<T> key);
	public List<BrokerEngine> getEngines();
	public void deregisterEngine(BrokerEngine engine);
	public void deregisterEngine(Object key);
	
	// management of event sinks and processors
	public void registerSink(EventSink sink);
	public EventSink getSink(ContactProfile profile);
	public EventProcessor getDestination(ContactProfile profile);
	public void deregisterSink(ContactProfile profile);
	public void deregisterSink(EventSink sink);
	public void deregisterSink(EventProcessor destination);
	public List<EventProcessor> getDestinations();
	public List<EventSink> getSinks();
	public List<ContactProfile> getContacts();
	public ContactProfile connect(ContactProfile profile);
	
	// TODO parzy maybe split interface in a basic broker with minimal functionality and an extended one which can host components 
	// management of components
	public void registerComponent(Component component);
	public Component getComponent(ContactProfile profile);
	public List<Component> getComponents();
	public void deregisterComponent(ContactProfile profile);
	public void deregisterComponent(Component component);
	
	// object plugging
	public Object plug(Object obj);
	public Object unplug(Object obj);	
}
