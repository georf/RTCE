/**
 * 
 */
package rebeca.broker;

import java.util.*;

import org.apache.log4j.Logger;

import rebeca.*;
import rebeca.component.*;


/**
 * @author parzy
 *
 */
public class BasicMonitoringEngine extends BasicBrokerEngine 
implements MonitoringEngine
{
	// Constants --------------------------------------------------------------
	// ------------------------------------------------------------------------
	
	public static final Class<MonitoringEngine> KEY = MonitoringEngine.class;

	/** 
	 * Logger for ProcessingEngines. 
	 */
	private static final Logger LOG = Logger.getLogger(KEY);

	
	// event sinks ------------------------------------------------------------
	// ------------------------------------------------------------------------
	
	protected class MonitoringSink extends PluggableEventSink
	{
		// constructors -------------------------------------------------------
		// --------------------------------------------------------------------
		
		public MonitoringSink() 
		{
			super();
		}

		public MonitoringSink(EventSink out) 
		{
			super(out);
		}
		
		public MonitoringSink(EventSink out, EventSink in) 
		{
			super(out, in);
		}

		// hooks --------------------------------------------------------------
		// --------------------------------------------------------------------
		
		@Override
		public void in(Event event) 
		{
			afterReceiving(event,this);
			super.in(event);
		}

		@Override
		public void out(Event event) 
		{
			beforeSending(event,this);
			super.out(event);
		}	
	}
	
	protected class MonitoringComponentSink extends PluggableComponentSink
	{
		// constructors -------------------------------------------------------
		// --------------------------------------------------------------------
		
		public MonitoringComponentSink() 
		{
			super();
		}

		public MonitoringComponentSink(ComponentSink in, ComponentSink out) 
		{
			super(in, out);
		}

		public MonitoringComponentSink(ComponentSink in) 
		{
			super(in);
		}

		
		// hooks --------------------------------------------------------------
		// --------------------------------------------------------------------
		
		@Override
		public void init()
		{
			super.init();
			afterPlugging(this);
		}
		
		@Override
		public void activate()
		{
			beforeActivation(this);
			super.activate();
			afterActivation(this);
		}
		
		@Override
		public void passivate()
		{
			beforePassivation(this);
			super.passivate();
			afterPassivation(this);
		}
		
		@Override
		public void exit()
		{
			super.exit();
			beforeUnplugging(this);
		}
		
		@Override
		public void in(Event event) 
		{
			beforeConsuming(event, this);
			super.in(event);
		}

		@Override
		public void out(Event event) 
		{
			afterProducing(event, this);
			super.out(event);
		}		
	}
	
	
	// fields -----------------------------------------------------------------
	// ------------------------------------------------------------------------

	/**
	 * Registered engines for handling event types.
	 */
	protected HashMap<Class<?>,BrokerEngine> engines;
	
	
	// constructors -----------------------------------------------------------
	// ------------------------------------------------------------------------
	
	public BasicMonitoringEngine() 
	{
		this(null);
	}

	/**
	 * @param broker
	 */
	public BasicMonitoringEngine(Broker broker) 
	{
		super(broker);
		
		engines = new HashMap<Class<?>, BrokerEngine>();
	}

	// getters and setters ----------------------------------------------------
	// ------------------------------------------------------------------------
	
	@Override
	public Object getKey()
	{
		return KEY;
	}
	
	// event processing logic -------------------------------------------------
	// ------------------------------------------------------------------------
	
//	@Override
//	public void init(Broker broker) 
//	{
//		super.init(broker);
	@Override
	public void init()
	{
		super.init();
		
		// get registered event classes from processing engine
		ProcessingEngine processing = broker.getEngine(ProcessingEngine.class);
		Collection<Class<?>> set = new ArrayList<Class<?>>(processing.eventClassSet());
		
		// deregister event class and add it to own dispatching logic
		for (Class<?> clazz : set)
		{
			BrokerEngine engine = processing.deregisterEventClass(clazz);
			engines.put(clazz, engine);
		}
		
		// and register own dispatching logic for all events instead
		processing.registerEventClass(Object.class, this);
	}
	
	@Override
	public void exit() 
	{
		
		// deregister this engine from further event processing 
		ProcessingEngine processing = broker.getEngine(ProcessingEngine.class);
		processing.deregisterEventClass(this);
		
		// restore old event registrations at processing engine
		for (Map.Entry<Class<?>,BrokerEngine> entry : engines.entrySet())
		{
			processing.registerEventClass(entry.getKey(), entry.getValue());
		}
				
		super.exit();
	}
	
	@Override
	public void process(Event event, EventProcessor source) 
	{		
		beforeProcessing(event,source);
		handle(event,source);
		afterProcessing(event,source);
	}

	public void handle(Event event, EventProcessor source)
	{
		Class<?> key;  // the event's type used as key to an appropriate handler
		BrokerEngine engine; // the handling engine

		// Determine the event's class 
		key = event != null ? event.getClass() : null;

		do
		{
			// TODO: Extend the lookup mechanism so that it includes interfaces.
			// Look up the handling protocol
			engine = engines.get(key);
			if (engine != null)
			{
				// and call the protocol's processing method.
				engine.process(event, source);
				break;
			}
			// Otherwise, try to find a handler for the event's superclass.
			key = key.getSuperclass();
		}
		while ( key != null );
		
		// log all events for which no processing engine could be determined
		if (engine == null) 
		{
			LOG.error("undispatched event '" + event + "' from '" + source);
		}
	}
		
	@Override
	public Object plug(Object obj) 
	{
		if (obj instanceof ComponentSink)
		{
			return plug((ComponentSink)obj);
		}
		if (obj instanceof EventSink)
		{
			return plug((EventSink)obj);
		}
		return obj;
	}
	
	public EventSink plug(EventSink sink)
	{
		return new MonitoringSink(sink);
	}
	
	public ComponentSink plug(ComponentSink sink)
	{
		return new MonitoringComponentSink(sink);
	}

	
	// overridable hooks ------------------------------------------------------
	// ------------------------------------------------------------------------

	// broker hooks
	public void beforeProcessing(Event event, EventProcessor source) { }
	public void afterProcessing(Event event, EventProcessor source) { }
	public void afterReceiving(Event event, EventSink sink) { }
	public void beforeSending(Event event, EventSink sink) { }
	
	// component hooks
	public void afterPlugging(ComponentSink sink) { }
	public void beforeActivation(ComponentSink sink) { }
	public void afterActivation(ComponentSink sink) { }
	public void beforeConsuming(Event event, ComponentSink sink) { }
	public void afterProducing(Event event, ComponentSink sink) { }
	public void beforePassivation(ComponentSink sink) { }
	public void afterPassivation(ComponentSink sink) { }
	public void beforeUnplugging(ComponentSink sink) { }
}
