/**
 * 
 */
package rebeca.peersim;

import org.apache.log4j.Logger;

import peersim.config.*;
import peersim.core.*;
import peersim.edsim.*;

import rebeca.*;
import rebeca.broker.*;
import rebeca.component.*;

/**
 * @author parzy
 *
 */
public class EventMonitoring extends ProtocolEngine implements MonitoringEngine
{

	public class MonitoringConnector extends BasicMonitoringEngine
	{
		// constructors -------------------------------------------------------
		// --------------------------------------------------------------------

		public MonitoringConnector() 
		{
			super();
		}

		public MonitoringConnector(Broker broker) 
		{
			super(broker);
			this.getKey();
		}

			
		// broker hook delegation
		@Override 
		public void beforeProcessing(Event event, EventProcessor source) { 
			EventMonitoring.this.beforeProcessing(event,source); 
		}
		@Override 
		public void afterProcessing(Event event, EventProcessor source) {
			EventMonitoring.this.afterProcessing(event,source); 
		}
		@Override 
		public void afterReceiving(Event event, EventSink sink) {
			EventMonitoring.this.afterReceiving(event,sink); 
		}
		@Override 
		public void beforeSending(Event event, EventSink sink) {
			EventMonitoring.this.beforeSending(event,sink); 
		}
		
		// component hooks
		@Override
		public void afterPlugging(ComponentSink sink) {
			EventMonitoring.this.afterPlugging(sink);
		}
		@Override 
		public void beforeActivation(ComponentSink sink) { 
			EventMonitoring.this.beforeActivation(sink); 
		}
		@Override 
		public void afterActivation(ComponentSink sink) { 
			EventMonitoring.this.afterActivation(sink); 
		}
		@Override 
		public void beforeConsuming(Event event, ComponentSink sink) {
			EventMonitoring.this.beforeConsuming(event,sink); 
		}
		@Override 
		public void afterProducing(Event event, ComponentSink sink) {
			EventMonitoring.this.afterProducing(event,sink); 
		}
		@Override 
		public void beforePassivation(ComponentSink sink) { 
			EventMonitoring.this.beforePassivation(sink); 
		}
		@Override 
		public void afterPassivation(ComponentSink sink) { 
			EventMonitoring.this.afterPassivation(sink); 
		}
		@Override
		public void beforeUnplugging(ComponentSink sink) {
			EventMonitoring.this.beforeUnplugging(sink);
		}
	}
	
	public class Tick
	{
		boolean cancelled;
		
		public void cancel()
		{
			cancelled = true;
		}
		
		public boolean isCancelled()
		{
			return cancelled;
		}
	}
	
	// parameters -------------------------------------------------------------
	// ------------------------------------------------------------------------

//	private static final String PAR_BROKERING = "brokering";
	private static final String PAR_START = "start";
	private static final String PAR_STOP = "stop";
	private static final String PAR_INTERVAL = "interval";
	private static final String PAR_FILE = "file";
	
	private static final Logger LOG = Logger.getLogger(MonitoringEngine.class);
	
	// fields
	
	protected static Tick next;
	
//	protected static int brokering;
	
	protected static long start;
	protected static long stop;
	protected static long interval;
	
	protected static String file;
	
	// instantiation ----------------------------------------------------------
	// ------------------------------------------------------------------------
	
	public EventMonitoring(String name) 
	{
		super(name);
		
		// override engine
		engine = new MonitoringConnector();

		// brokering protocol 
//		brokering = Configuration.getPid(name+"."+PAR_BROKERING, -1);
		
		// event processing
		start = Configuration.getLong(name+"."+PAR_START, 0L);
		stop = Configuration.getLong(name+"."+PAR_STOP,CommonState.getEndTime());
		interval = Configuration.getLong(name+"."+PAR_INTERVAL, stop-start);
		next = null;
		
		// results
		file = Configuration.getString(name+"."+PAR_FILE,null);	
	}
	
	@Override
	public EventMonitoring clone()
	{
		// object cloning
		EventMonitoring clone = (EventMonitoring)super.clone();
		clone.node = CommonState.getNode();
		clone.engine = new MonitoringConnector();
		return clone;
	}
	
	@Override
	public void init()
	{
		super.init();
		
		// event processing initialization
		if (next == null && node!=Network.prototype)
		{
			next = new Tick();
			schedule(0,next);
		}
	}
	
	
	// event processing -------------------------------------------------------
	// ------------------------------------------------------------------------
	
	@Override
	public void processEvent(Node node, int pid, Object event) 
	{
		// check event type
		if ( !(event instanceof Tick) )
		{
			LOG.error("unhandled event '" + event + "'");
		}
		
		// measuring
		long time = CommonState.getTime();
		if (time <= start && start < time+interval) prepare();
		if (start < time && time <= stop) measure();
		if (time <= stop && stop < time+interval) conclude();
		if (time <= stop && stop < time+interval) report();
		
//		if (start < time && time <= stop)
//		{
//			if (time-interval <= start) prepare();
//			measure();
//			if (stop < time+interval) conclude();
//			if (stop < time+interval) report();
//		}
		
		// house keeping and next event
		cleanup();
		schedule(interval, event);
	}
	
	public void schedule(long delay, Object event)
	{
		EDSimulator.add(delay, event, node, pid);
	}
	
	
	// helpers ----------------------------------------------------------------
	// ------------------------------------------------------------------------
	
//	public Broker getBroker()
//	{
//		return engine.getBroker();
//	}
	
	
	public <T> T getEngine(Class<T> key)
	{
		Broker b = getBroker();
		return b!=null ? b.getEngine(key) : null;
	}
	
	
	// hooks for subclasses ---------------------------------------------------
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
	
	// measurement hooks
	public void prepare() { };
	public void measure() { };
	public void conclude() { };
	public void report() { };
	public void cleanup() { };
}
