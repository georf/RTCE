/*
 * $Id$
 */
package rebeca.peersim;

import java.util.*;

import org.apache.log4j.Logger;

import peersim.config.*;
import peersim.core.*;
import peersim.edsim.*;

import rebeca.*;
import rebeca.broker.*;
import rebeca.component.*;
import rebeca.math.*;


/**
 * @author parzy
 *
 */
public class EventProcessing extends ProtocolEngine implements ProcessingEngine
{
	// constants and parameters -----------------------------------------------
	//-------------------------------------------------------------------------

	/** 
	 * Logger for ProcessingEngines. 
	 */
	private static final Logger LOG = Logger.getLogger(ProcessingEngine.class);
	
	/**
	 * String name of the parameter used to scale the processing delay. The 
	 * default is 1.0.
	 */
	private static final String PAR_SCALE = "scale";
	
	/**
	 * The name of the parameter to configure the class to use for generating
	 * the processing delay of events.
	 */
	private static final String PAR_DISTRIBUTION = "distribution";
	
	/** 
	 * Parameter defining the employed processing engine.
	 * @config
	 */	
	private static final String PAR_ENGINE = "engine";
	
	
	private static final Distribution DEFAULT_DISTRIBUTION = new ConstantDistribution(); 
	
	// Fields -----------------------------------------------------------------
	//-------------------------------------------------------------------------

	/**
	 * Factor with which the processing delay is multiplied.  It provides a
	 * convenient possibility to fine tune the processing delay regarding its 
	 * proportion to other delays.
	 */
	protected final double scale;
	
	/**
	 * Distribution used to generate processing delays for events.
	 */
	protected final Distribution distribution;
	
	/**
	 * The event that is currently processed.
	 */
	protected ProcessingEvent scheduledEvent;

	
	// initialization ---------------------------------------------------------
	// ------------------------------------------------------------------------

	/**
	 * Reads configuration parameter.
	 */
	@SuppressWarnings("unchecked")
	public EventProcessing(String prefix)
	{
		// store prefix, protocol id, and hosting node
		this.prefix = prefix;
		this.pid = CommonState.getPid();
		this.node = CommonState.getNode();
		
		// get parameters from configuration
		this.engine = (ProcessingEngine)Configuration.getInstance(prefix + "." 
				+ PAR_ENGINE, new BasicProcessingEngine());
		this.scale = Configuration.getDouble(prefix + "." + PAR_SCALE, 1.0d);
		this.distribution = (Distribution)Configuration.getInstance(prefix + "." 
				+ PAR_DISTRIBUTION, DEFAULT_DISTRIBUTION);
		
		// Initialize remaining instance members.
		this.scheduledEvent = null;
	}
	
	// EDProtocol implementation ----------------------------------------------
	// ------------------------------------------------------------------------
	
	@Override
	public Object clone()
	{
		EventProcessing clone = (EventProcessing)super.clone();
		clone.node = CommonState.getNode();
		// TODO parzy maybe support cloning for engines
		clone.engine = (ProcessingEngine)Configuration.getInstance(prefix 
					+ "." + PAR_ENGINE, new BasicProcessingEngine());
		clone.scheduledEvent = null;
		return clone;
	}

	// overridden BrokerEngine methods ----------------------------------------
	// ------------------------------------------------------------------------

	@Override
	public void activate() { /* no threads need to be started */ }
	@Override
	public void passivate() { /* no threads need to be stopped */ }
	
	// ProcessingEngine basic implementation ----------------------------------
	// ------------------------------------------------------------------------
	
	@Override
	public Queue<EventSourceTuple> getQueue()
	{
		return ((ProcessingEngine)engine).getQueue();
	}
	
	@Override
	public void setQueue(Queue<EventSourceTuple> queue)
	{
		((ProcessingEngine)engine).setQueue(queue);
	}
	
	@Override
	public BrokerEngine registerEventClass(Class<?> clazz, BrokerEngine engine)
	{
		return ((ProcessingEngine)this.engine).registerEventClass(clazz, engine);
	}
	
	@Override
	public BrokerEngine deregisterEventClass(Class<?> clazz)
	{
		return ((ProcessingEngine)engine).deregisterEventClass(clazz);
	}
	
	@Override
	public BrokerEngine deregisterEventClass(BrokerEngine engine)
	{
		return ((ProcessingEngine)this.engine).deregisterEventClass(engine);
	}
	
	@Override
	public Set<Class<?>> eventClassSet()
	{
		return ((ProcessingEngine)this.engine).eventClassSet();
	}
	
	@Override
	public void handle(EventSourceTuple tuple)
	{
		((ProcessingEngine)engine).handle(tuple);
	}
	
	public void handle(Event event, EventProcessor source)
	{
		((ProcessingEngine)engine).handle(event, source);
	}
	
	
//	@Override
	public BrokerEngine getEngine()
	{
		return engine;
	}
	
	// processing logic -------------------------------------------------------
	// ------------------------------------------------------------------------
	
	protected class ProcessingEvent 
	{
		Object event = null;
		boolean canceled = false;
		
		public ProcessingEvent(Object event)
		{
			this.event = event;
		}
		
		public Object getNestedEvent()
		{
			return event;
		}
		
		public boolean isCanceled()
		{
			return canceled;
		}
		
		public void cancel()
		{
			canceled = true;
			event = null;
		}
	}
	
	public void process(Event event, EventProcessor source)
	{
		((ProcessingEngine)engine).process(event, source);
		schedule();
	}
	
	public void process(EventSourceTuple tuple)
	{
		((ProcessingEngine)engine).process(tuple);
		schedule();
	}
	
	public void processEvent(Node node, int pid, Object event)
	{
		// only handle own processing events
		if (! (event instanceof ProcessingEvent))
		{
			return;
		}

		// Check whether the event has been canceled meanwhile (since we 
		// can not remove events from the simulator once they have been 
		// scheduled).
		ProcessingEvent e = (ProcessingEvent)event;
		if ( !e.isCanceled() )
		{	
			// Handle a completely processed event
			((ProcessingEngine)engine).handle((EventSourceTuple)e.getNestedEvent());
			// Clean up afterwards.
			scheduledEvent = null;
		}

		// schedule next queued event
		schedule();
	}
	
	protected void schedule()
	{
		ProcessingEngine engine = (ProcessingEngine)this.engine;
		// simply return if the engine is still processing another event
		// or its event queue is empty
		if (isBusy() || engine.getQueue().isEmpty())
		{
			return;
		}
		
		// Determine the next event and schedule it.
		scheduledEvent = new ProcessingEvent(engine.getQueue().poll());
		long delay = (long)(scale*distribution.getValue());
		EDSimulator.add(delay, scheduledEvent, node, pid);	
	}

	public boolean isBusy() 
	{
		return scheduledEvent != null;
	}

	
	// plugging ---------------------------------------------------------------
	// ------------------------------------------------------------------------
	
	protected class ProxyEventProcessor extends PluggableEventSink 
	implements EventProcessor
	{
		public ProxyEventProcessor(EventSink sink)
		{
			super(sink);
		}
		
		public void process(Event e)
		{
			out(e);
		}
		
		public boolean isLocal()
		{
			return false;
		}
		
		@Override
		public void in(Event event)
		{
			EventProcessing.this.process(event,this);
		}
		
		@Override
		public EventProcessor getProcessor()
		{
			return this;
		}
	}
	
	@Override
	public Object plug(Object obj)
	{
		if (obj instanceof ComponentSink)
		{
			// TODO parzy maybe implement a queue?
			return obj;
		}
		if (obj instanceof EventSink)
		{
			return new ProxyEventProcessor((EventSink)obj);
		}
		return obj;
	}
}
