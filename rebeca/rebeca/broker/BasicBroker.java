/*
 * $Id$
 */
package rebeca.broker;

import java.util.*;

import org.apache.log4j.Logger;

import rebeca.*;

/**
 * @author parzy
 *
 */
public class BasicBroker implements Broker 
{
	// constants --------------------------------------------------------------
	// ------------------------------------------------------------------------
	
	/** 
	 * Logger for ProcessingEngines. 
	 */
	private static final Logger LOG = Logger.getLogger(Broker.class);

	
	// fields -----------------------------------------------------------------
	// ------------------------------------------------------------------------
	
	protected Map<Object,BrokerEngine> engines;
	protected Map<ContactProfile,EventSink> sinks;
	protected Map<ContactProfile,EventProcessor> destinations;
	protected Map<ContactProfile,Component> components;
	
	// constructors -----------------------------------------------------------
	// ------------------------------------------------------------------------
	
	public BasicBroker()
	{
		this.engines = new LinkedHashMap<Object,BrokerEngine>();
		this.sinks = new LinkedHashMap<ContactProfile,EventSink>();
		this.destinations = new LinkedHashMap<ContactProfile,EventProcessor>();
		this.components = new LinkedHashMap<ContactProfile,Component>();
	}

	public BasicBroker(ConfigurationEngine config)
	{
		this();
		registerEngine(config);
	}
	
	/**
	 * Convenient constructor used by PeerSim which simply calls the no 
	 * arguments constructor.
	 * @param prefix ignored
	 */
	public BasicBroker(String prefix)
	{
		this();
	}
	
	// engine management ------------------------------------------------------
	// ------------------------------------------------------------------------

	public void registerEngine(BrokerEngine engine)
	{
		registerEngine(engine.getKey(), engine);
	}
	
	public void registerEngine(Object key, BrokerEngine engine)
	{
		engine.setBroker(this);
		synchronized (engines) { engines.put(key,engine); }
		if (LOG.isDebugEnabled())
		{
			LOG.debug("registered engine '" + engine + "' for key '" + key 
					+ "'");
		}
	}
	
	public BrokerEngine getEngine(Object key)
	{
		synchronized (engines)
		{
			return engines.get(key);
		}
	}
	public List<BrokerEngine> getEngines()
	{
		synchronized (engines)
		{
			return new ArrayList<BrokerEngine>(engines.values());
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getEngine(Class<T> key)
	{
		synchronized (engines)
		{
			return (T)engines.get(key);
		}
	}
	
	public void deregisterEngine(BrokerEngine engine)
	{
		deregisterEngine(engine.getKey());
	}

	public void deregisterEngine(Object key)
	{
		BrokerEngine engine = null;
		synchronized (engines)
		{
			engine = engines.remove(key);
		}
		if (engine != null)
		{
			engine.setBroker(null);
		}
	}
	
	// connection management --------------------------------------------------
	// ------------------------------------------------------------------------
	
	public void registerSink(EventSink sink)
	{
		
		// register the sink
		EventSink bottom = sink.getSink();
		EventProcessor top = sink.getProcessor();
		ContactProfile profile = sink.getContactProfile();
		synchronized (sinks)
		{
			sinks.put(profile,bottom);
		}
		synchronized (destinations)
		{
			destinations.put(profile,top);
		}
	}
	
	public EventSink getSink(ContactProfile profile)
	{
		synchronized (sinks)
		{
			return sinks.get(profile);
		}
	}
	public EventProcessor getDestination(ContactProfile profile)
	{
		synchronized (destinations)
		{
			return destinations.get(profile);
		}
	}
	public List<EventProcessor> getDestinations()
	{
		synchronized (destinations)
		{
			return new ArrayList<EventProcessor>(destinations.values());
		}
	}
	public List<ContactProfile> getContacts()
	{
		synchronized (sinks)
		{
			return new ArrayList<ContactProfile>(sinks.keySet());
		}
	}
	public List<EventSink> getSinks()
	{
		synchronized (sinks)
		{
			return new ArrayList<EventSink>(sinks.values());
		}
	}
	
	public void deregisterSink(EventSink sink)
	{
		deregisterSink(sink.getContactProfile());
	}
	
	public void deregisterSink(EventProcessor destination)
	{
		for(Map.Entry<ContactProfile, EventProcessor> e : destinations.entrySet())
		{
			if (destination.equals(e.getValue()))
			{
				deregisterSink(e.getKey());
				return;
			}
		}
	}
	
	public void deregisterSink(ContactProfile profile)
	{
		synchronized (sinks)
		{
			sinks.remove(profile);
		}
		synchronized (destinations)
		{
			destinations.remove(profile);
		}
	}

	
	public ContactProfile connect(ContactProfile profile)
	{
		// connect using appropriate engine 
		Connectable engine = (Connectable)getEngine(profile.getEngineKey());
		if (engine != null)
		{
			return engine.connect(profile);
		}
		
		// no suited engine available 
		LOG.error("no appropriate engine available to establish a connection " 
				+ "to '"+profile+"'");
		return null;
	}
	
	 
	// component management ---------------------------------------------------
	// ------------------------------------------------------------------------
	
	public void registerComponent(Component component)
	{
		ContactProfile profile = component.getContactProfile();
		synchronized (components)
		{
			components.put(profile, component);
		}
	}
	
	public Component getComponent(ContactProfile profile)
	{
		synchronized (components)
		{
			return components.get(profile);
		}
	}
	public List<Component> getComponents()
	{
		synchronized (components)
		{
			return new ArrayList<Component>(components.values());
		}
	}
	
	public void deregisterComponent(Component component)
	{
		deregisterComponent(component.getContactProfile());
	}
	public void deregisterComponent(ContactProfile profile)
	{
		synchronized (components)
		{
			//Component component = components.remove(profile);
			components.remove(profile);
		}
	}
	
	
	// configuration delegation -----------------------------------------------
	// ------------------------------------------------------------------------
	
	public void init(ConfigurationEngine config)
	{
		// register configuration and initialize broker
		if (LOG.isDebugEnabled())
		{
			LOG.debug("registering configuration '" + config 
					+ "' and initializing broker '" + this + "'");
		}
		registerEngine(config);
		config.init();
	}
	
	public void startup()
	{
		// delegate plugging of engines
		ConfigurationEngine config = getEngine(ConfigurationEngine.class);
		if (config == null)
		{
			LOG.error("no configuration engine available for startup");
			return;
		}
		if (LOG.isDebugEnabled())
		{
			LOG.debug("starting broker '" + this + "'");
		}
		config.startup();
	}
	
	public void shutdown()
	{
		// delegate unplugging of engines
		ConfigurationEngine config = getEngine(ConfigurationEngine.class);
		if (config == null)
		{
			LOG.error("no configuration engine available for shutdown");
			return;
		}
		if (LOG.isDebugEnabled())
		{
			LOG.debug("stopping broker '" + this + "'");
		}
		config.shutdown();
	}
	
	public Object plug(Object obj)
	{
		if (LOG.isInfoEnabled())
		{
			LOG.info("plugging in object '" + obj + "'");
		}
		// let the configuration engine handle all details of plugging
		ConfigurationEngine config = getEngine(ConfigurationEngine.class);
		if (config == null)
		{
			LOG.error("no configuration engine available for plugging object '" 
					+ obj + "'");
			return null;
		}
		return config.plug(obj);
	}
	
	public Object unplug(Object obj)
	{
		ConfigurationEngine config = getEngine(ConfigurationEngine.class);
		if (config == null)
		{
			LOG.error("no configuration engine available for unplugging " 
					+ "object '" + obj + "'");
			return null;
		}
		
		return config.unplug(obj);
	}
}
