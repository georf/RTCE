/*
 * $Id$
 */
package rebeca.broker;

import java.util.*;

import org.apache.log4j.Logger;

import rebeca.*;
import rebeca.component.*;
import rebeca.routing.*;
import rebeca.scope.*;
import rebeca.transport.*;

/**
 * @author parzy
 *
 */
public class ExtendableConfigurationEngine extends BasicBrokerEngine 
implements ConfigurationEngine 
{
	protected static class PlugEvent extends Event
	{
		private static final long serialVersionUID = 20090406131124l;

		protected Object obj;
		
		public PlugEvent(Object obj)
		{
			this.obj = obj;
		}
		
		public Object getObject()
		{
			return obj;
		}
		
		@Override
		public String toString()
		{
			return "PlugEvent(" + obj + ")";
		}
	}
	protected static class UnplugEvent extends PlugEvent
	{
		private static final long serialVersionUID = 20090407085813l;
		public UnplugEvent(Object obj)
		{ 
			super(obj);
		}
		@Override
		public String toString()
		{
			return "UnplugEvent(" + obj + ")";
		}
	}
	
	
	// constants --------------------------------------------------------------
	// ------------------------------------------------------------------------
	
	public static final Class<ConfigurationEngine> KEY = ConfigurationEngine.class;

	/** 
	 * Logger for ProcessingEngines. 
	 */
	private static final Logger LOG = Logger.getLogger(KEY);

	
	// Fields -----------------------------------------------------------------
	// ------------------------------------------------------------------------

	
	// constructors -----------------------------------------------------------
	// ------------------------------------------------------------------------
	
	public ExtendableConfigurationEngine()
	{
		this(null);
	}

	public ExtendableConfigurationEngine(Broker broker)
	{
		super(broker);
	}
	
	// Getters and Setters ----------------------------------------------------
	// ------------------------------------------------------------------------
	
	@Override
	public Object getKey()
	{
		return KEY;
	}

	// initialization logic ---------------------------------------------------
	// ------------------------------------------------------------------------
	
	@Override
	public void init() { /* for broker initialization stuff */ }
	
	@Override
	public void startup()
	{
		// plugging in configured engines
		if (LOG.isDebugEnabled())
		{
			LOG.debug("starting broker by plugging configured engines");
		}
		configure(broker);
		
		// register this engine for handling new (Un)PlugEvents
		ProcessingEngine processing = broker.getEngine(ProcessingEngine.class);
		if (processing == null)
		{
			LOG.error("no processing engine available for registering event "
					+ "handlers");
			return;
		}
		processing.registerEventClass(PlugEvent.class, this);
	}
	
	@Override
	public void shutdown()
	{
		// unplugging engines in reverse order of registration
		if (LOG.isDebugEnabled())
		{
			LOG.debug("stopping broker by unplugging engines in reverse order");
		}
		
		// iterate over all broker engines in reverse order
		List<BrokerEngine> engines = broker.getEngines();
		ListIterator<BrokerEngine> lit = engines.listIterator(engines.size());
		while (lit.hasPrevious())
		{
			BrokerEngine engine = lit.previous();
			broker.unplug(engine);
		}
	}
	
	// (un)plugging infrastructure  -------------------------------------------
	// ------------------------------------------------------------------------
	
	public Object plug(Object obj)
	{
		// immediate, asynchronous handling of plugging calls 
		if (obj instanceof BrokerEngine)
		{
			return doPlug((BrokerEngine)obj);
		}
		if (obj instanceof Component)
		{
			return doPlug((Component)obj);
		}
		
		// deferred, synchronous handling of plugging calls 
		defer(new PlugEvent(obj));
		return obj;
	}
	
	public Object unplug(Object obj)
	{
		// immediate, asynchronous handling of unplugging calls 
		if (obj instanceof BrokerEngine)
		{
			return doUnplug((BrokerEngine)obj);
		}
		if (obj instanceof Component)
		{
			return doUnplug((Component)obj);
		}
		
		// deferred, synchronous handling of unplugging calls 
		defer(new UnplugEvent(obj));
		return obj;
	}
	
	private void defer(PlugEvent event)
	{
		// schedule (un)plugging event for deferred, synchronous handling
		ProcessingEngine processing = broker.getEngine(ProcessingEngine.class);
		if (processing == null)
		{
			LOG.error("no processing engine found for scheduling (un)plugging " 
					+ "event of object '" + event.getObject() + "'");
			return;
		}
		processing.process(event,null);
	}
	
	public void process(Event event, EventProcessor source)
	{
		if ( event instanceof UnplugEvent )
		{
			doUnplug(((UnplugEvent)event).getObject());
			return;
		}
		if ( event instanceof PlugEvent )
		{
			doPlug(((PlugEvent)event).getObject());
			return;
		}
		
		LOG.error("received unhandled event '" + event + "' from '" + source + "'");
		return;
	}
	
	public Object doPlug(Object obj)
	{
		if (obj instanceof BrokerEngine)
		{
			return doPlug((BrokerEngine)obj);
		}
		if (obj instanceof Component)
		{
			return doPlug((Component)obj);
		}
		if (obj instanceof EventSink)
		{
			return doPlug((EventSink)obj);
		}
		
		LOG.warn("no configuration for pluggin in object '" + obj + "'");
		return obj;
	}
	
	public Object doUnplug(Object obj)
	{
		if (obj instanceof BrokerEngine)
		{
			return doUnplug((BrokerEngine)obj);
		}
		if (obj instanceof EventSink)
		{
			return doUnplug((EventSink)obj);
		}
		if (obj instanceof Component)
		{
			return doUnplug((Component)obj);
		}
		
		LOG.warn("no configuration for pluggin in object '" + obj + "'");
		return obj;
	}
	
	
	// real (un)plugging logic ------------------------------------------------
	// ------------------------------------------------------------------------
	
	protected BrokerEngine doPlug(BrokerEngine engine)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("plugging in engine '" + engine + "'");
		}
		
		// register and initialize engine
		broker.registerEngine(engine);
		engine.init();
		engine.activate();
		return engine;
	}
	
	protected EventSink doPlug(EventSink sink)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("plugging in sink '" + sink + "'");
		}
		
		// configure, initialize, activate, and register the sink
		sink = configure(sink);
		sink.init();
		sink.activate();
		broker.registerSink(sink);
		return sink;
	}
	
	protected EventSink doPlug(Component component)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("plugging in component '" + component + "'");
		}
		
		// plug the component sink 
		EventSink sink = configure(component);
		plug(sink);
		
		// register the component
		broker.registerComponent(component);
		
		return null;
	}
	
	protected BrokerEngine doUnplug(BrokerEngine engine)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("unplugging engine '" + engine + "'");
		}
		
		// stop and deregister engine
		engine.passivate();
		engine.exit();
		broker.deregisterEngine(engine);
		return engine;
	}
	
	protected Component doUnplug(Component component)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("unplugging component '" + component + "'");
		}
		
		// get appropriate event sink
		ContactProfile profile = component.getContactProfile();
		EventSink sink = broker.getSink(profile);

		// and unplug it
		if (sink != null)
		{
			unplug(sink);
		}
		broker.deregisterComponent(profile);
		return component;
	}
	
	protected EventSink doUnplug(EventSink sink)
	{
		// ensure we have the top sink
		EventProcessor proc = sink.getProcessor();
		sink = (EventSink)proc;
		
		// deactivate, destroy and deregister the sink
		sink.passivate();
		sink.exit();
		broker.deregisterSink(sink);
		
		return sink;
	}
	
	// configuration logic ----------------------------------------------------
	// ------------------------------------------------------------------------
	
	public Object configure(Broker broker)
	{
		// processing
		plug(new BasicProcessingEngine());
		// matching
		plug(new BasicMatchingEngine());
		// routing
		plug(new SimpleRouting());
		// advertising
		plug(new SimpleAdvertising());
		// serialization
		plug(new ObjectSerializationEngine());
		// transport
		plug(new TcpEngine());
		// components
		plug(new BasicComponentEngine());
		
		return broker;
	}
	
	
	public EventSink configure(EventSink sink)
	{
		// consider serialization
		SerializationEngine serialization = broker.getEngine(SerializationEngine.class);
		if (serialization != null) sink = (EventSink)serialization.plug(sink);
		
		// consider monitoring/measuring
		MonitoringEngine monitoring = broker.getEngine(MonitoringEngine.class);
		if (monitoring != null) sink = (EventSink)monitoring.plug(sink);
		
		// consider scoping
		ScopeEngine scoping = broker.getEngine(ScopeEngine.class);
		if (scoping != null) sink = (EventSink)scoping.plug(sink);
		
		// consider advertisements
		AdvertisementEngine advertising = broker.getEngine(AdvertisementEngine.class);
		if (advertising != null) sink = (EventSink)advertising.plug(sink);

		// consider subscriptions
		RoutingEngine routing  = broker.getEngine(RoutingEngine.class);
		if (routing != null) sink = (EventSink)routing.plug(sink);

		// consider matching
		MatchingEngine matching = broker.getEngine(MatchingEngine.class);
		if (matching != null) sink = (EventSink)matching.plug(sink);

		// consider processing
		ProcessingEngine processing = broker.getEngine(ProcessingEngine.class);
		if (processing != null) sink = (EventSink)processing.plug(sink);

		return sink;
	}
	
	public EventSink configure(Component component)
	{
		// start with the component engine
		ComponentEngine components = broker.getEngine(ComponentEngine.class);
		if (components == null)
		{
			throw new RuntimeException("No component engine found.");
		}
		ComponentSink sink = (ComponentSink)components.plug(component);
		
//		// consider monitoring
//		MonitoringEngine monitoring = broker.getEngine(MonitoringEngine.class);
//		if (monitoring != null) sink = (ComponentSink)monitoring.plug(sink);

		// consider processing
		ProcessingEngine processing = broker.getEngine(ProcessingEngine.class);
		if (processing != null)	sink = (ComponentSink)processing.plug(sink);

		// consider matching
		MatchingEngine matching = broker.getEngine(MatchingEngine.class);
		if (matching != null) sink = (ComponentSink)matching.plug(sink);

		// consider subscriptions
		RoutingEngine routing = broker.getEngine(RoutingEngine.class);
		if (routing != null) sink = (ComponentSink)routing.plug(sink);

		// consider advertisements
		AdvertisementEngine advertising = broker.getEngine(AdvertisementEngine.class);
		if (advertising != null) sink = (ComponentSink)advertising.plug(sink);
		
		// consider monitoring
		MonitoringEngine monitoring = broker.getEngine(MonitoringEngine.class);
		if (monitoring != null) sink = (ComponentSink)monitoring.plug(sink);
		
		// consider scoping
		ScopeEngine scoping = broker.getEngine(ScopeEngine.class);
		if (scoping != null) sink = (ComponentSink)scoping.plug(sink);

		// return a real EventSink
		return (EventSink)components.plug(sink);
	}
}
