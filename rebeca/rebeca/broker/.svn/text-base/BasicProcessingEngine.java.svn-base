/*
 * $Id$
 */
package rebeca.broker;

import java.util.*;
import java.util.concurrent.*;

import org.apache.log4j.Logger;

import rebeca.*;
import rebeca.component.*;

/**
 * @author parzy
 *
 */
public class BasicProcessingEngine extends BasicBrokerEngine 
implements ProcessingEngine
{
	// EventSourceTuple implementation ----------------------------------------
	// ------------------------------------------------------------------------
	
	protected static class Tuple implements ProcessingEngine.EventSourceTuple
	{
		public Event event;
		public EventProcessor source;
		
		public Tuple(Event event, EventProcessor source)
		{
			this.event = event;
			this.source = source;
		}
		
		public Event getEvent()
		{
			return event;
		}
		
		public EventProcessor getSource()
		{
			return source;
		}
	}

	// Constants --------------------------------------------------------------
	// ------------------------------------------------------------------------
	
	public static final Class<ProcessingEngine> KEY = ProcessingEngine.class;

	/** 
	 * Logger for ProcessingEngines. 
	 */
	private static final Logger LOG = Logger.getLogger(KEY);
	
	
	// Fields -----------------------------------------------------------------
	// ------------------------------------------------------------------------
	
	/**
	 * Boolean to request stopping the event processing.
	 */
	protected volatile boolean stop;
	
	/**
	 * Boolean used by processing thread to signal that they have finished.
	 */
	protected volatile boolean wait;
	
	/**
	 * Worker thread processing all queued events
	 */
	protected Thread worker;

	/**
	 * Queue to store unhandled events.
	 */
	protected Queue<EventSourceTuple> queue;

	/**
	 * Registered engines for handling event types.
	 */
	protected HashMap<Class<?>,BrokerEngine> engines;

	
	// constructors and initialization ----------------------------------------
	// ------------------------------------------------------------------------
	
	public BasicProcessingEngine()
	{
		this((Broker)null);
	}

	public BasicProcessingEngine(Broker broker)
	{
		super(broker);
		this.worker = new Thread(new Worker(), "Processing Engine");
		this.queue = new LinkedBlockingQueue<EventSourceTuple>();
		this.engines = new LinkedHashMap<Class<?>,BrokerEngine>();
	}
	
	/**
	 * PeerSim convenience constructor ignores arguments and calls the default 
	 * constructor.
	 * @param prefix ignored
	 */
	public BasicProcessingEngine(String prefix)
	{
		this();
	}

	
	// Getters and Setters ----------------------------------------------------
	// ------------------------------------------------------------------------
	
	@Override
	public Object getKey()
	{
		return KEY;
	}
	
	public Queue<EventSourceTuple> getQueue()
	{
		return queue;
	}
	
	public void setQueue(Queue<EventSourceTuple> queue)
	{
		this.queue = queue;
	}
	
	
	// Event processing -------------------------------------------------------
    // ------------------------------------------------------------------------	

	protected class Worker implements Runnable
	{
		@Override
		public void run()
		{
			while (!stop)
			{
				try
				{
					// take next event and handle it
					EventSourceTuple t = 
						((BlockingQueue<EventSourceTuple>)queue).take();
					try
					{
						handle(t);
					}
					catch (Exception e)
					{
						LOG.error("exception occured while processing event '" 
								+ t.getEvent() + "' from '" + t.getSource() 
								+ "'");
						e.printStackTrace();
					}
				}
				catch (InterruptedException e) { }
			}
			
			// notify main thread
			synchronized (worker)
			{
				wait = false;
				worker.notifyAll();
			}
		}
	}
	
	public void activate()
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("starting processing of events");
		}
		stop = false;
		wait = true;
		worker.start();
	}
	
	public void passivate()
	{
		// signal worker to stop
		stop = true;
		worker.interrupt();
		
		// wait until worker has finished cleanly
		synchronized (worker)
		{
			while (wait)
			{
				try { worker.wait(); } catch (InterruptedException e) { }
			}
		}
	}
		
	public void process(EventSourceTuple tuple)
	{
		try
		{
			// log event and its source
			if (LOG.isDebugEnabled())
			{
				LOG.debug("queueing event '" + tuple.getEvent() + "' from '" 
						+ tuple.getSource() +"'");
			}
			
			// queue event and its source
			((BlockingQueue<EventSourceTuple>)queue).put(tuple);
		}
		catch (InterruptedException e) 
		{
			// TODO parzy log it away
		}
	}
	
	public void process(Event event, EventProcessor source)
	{
		process(new Tuple(event, source));
	}
	
	
	// Event handling ---------------------------------------------------------
	// ------------------------------------------------------------------------
	@Override
	public BrokerEngine registerEventClass(Class<?> clazz, BrokerEngine engine)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Engine '" + engine + "' registers event class '" + clazz 
					+ "'");
		}
		return engines.put(clazz, engine);
	}
	
	public BrokerEngine deregisterEventClass(Class<?> clazz)
	{
		return engines.remove(clazz);
	}
	
	public BrokerEngine deregisterEventClass(BrokerEngine engine)
	{
		for (Iterator<BrokerEngine> it = engines.values().iterator(); it.hasNext(); )
		{
			if (engine == it.next())
			{
				it.remove();
			}
		}
		return engine;
	}
	
	public Set<Class<?>> eventClassSet()
	{
		return Collections.unmodifiableSet(engines.keySet());
	}
	
	public void handle(EventSourceTuple tuple)
	{
		handle(tuple.getEvent(), tuple.getSource());
	}
	
	public void handle(Event event, EventProcessor source)
	{
		// log event and its source
		if (LOG.isDebugEnabled())
		{
			LOG.debug("handling event '" + event + "' from '" + source +"'");
		}
		
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
			LOG.error("unhandled event '" + event + "' from '" + source);
		}
	}

	
	
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
	
	public Object plug(EventSink sink)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("plugging event processing handler into sink '" 
					+ sink.getSink() + "'");
		}
		return new ProxyEventProcessor(sink);
	}
	
	protected class ProxyEventProcessor extends PluggableEventSink 
	implements EventProcessor
	{
		public ProxyEventProcessor(EventSink sink)
		{
			super(sink);
		}
		
		public void process(Event e)
		{
			e = (Event)e.clone();
			out(e);
		}
		
		public boolean isLocal()
		{
			return false;
		}
		
		@Override
		public void in(Event event)
		{
			BasicProcessingEngine.this.process(event,this);
		}
		
		@Override
		public EventProcessor getProcessor()
		{
			return this;
		}
		
		@Override
		public String toString()
		{
			// TODO parzy maybe revise the visualization later
			return this.getSink().toString();
		}
	}
	
	
	public ComponentSink plug(ComponentSink sink)
	{
		return new ComponentEventProcessor(sink);
	}
	
	protected static class ComponentEventProcessor extends PluggableComponentSink
	{
		boolean queueing = false;
		boolean stop;
		BlockingQueue<Event> events;

		protected class Worker implements Runnable
		{
			public void run()
			{
				while (!stop)
				{
					try
					{
						if (in != null)
						{
							in.in(events.take());
						}
					}
					catch (InterruptedException e) { }
				}
			}
		}
		
		// TODO parzy make it configurable
		public ComponentEventProcessor(ComponentSink sink)
		{
			super(sink);
			this.queueing = false;
			this.stop = false;
			this.events = new LinkedBlockingQueue<Event>();
		}
		
		@Override
		public void in(Event event)
		{
			if (queueing)
			{
				events.add(event);
				return;
			}
			super.in(event);
		}
		
		@Override
		public void activate()
		{
			if (queueing)
			{
				new Thread(new Worker()).start();
			}
			super.activate();
		}
		
		@Override
		public void passivate()
		{
			if (queueing)
			{
				stop = true;
				// TODO parzy interrupt a waiting thread...
			}
			super.passivate();
		}
	}
}


