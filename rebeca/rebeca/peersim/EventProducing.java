/**
 * 
 */
package rebeca.peersim;

import java.lang.reflect.*;
import java.util.*;

import org.apache.log4j.*;

import peersim.config.*;
import peersim.core.*;
import peersim.edsim.*;

import rebeca.*;
import rebeca.filter.*;
import rebeca.math.*;
import rebeca.namevalue.*;
import rebeca.util.*;

/**
 * @author parzy
 *
 */
public class EventProducing extends ApplicationProtocol 
{	
	// parameters and constants -----------------------------------------------
	// ------------------------------------------------------------------------

	private static final String PAR_PROFILE = "profile";
	private static final String PAR_SEED = "seed";
	private static final String PAR_RATE = "rate";
	
	private static final long RATE_SEED = -2389046784858542656L;
	
	private static final Logger LOG = Logger.getLogger(EventProducing.class);

	
	// profiles, components, and events ---------------------------------------
	// ------------------------------------------------------------------------

	public static class ProducerProfile extends Profile
	{
		// fields -------------------------------------------------------------
		// --------------------------------------------------------------------

		protected Event event;
		protected Filter filter;
//		protected Random r;
		protected Distribution rate;
	
		
		// instantiation ------------------------------------------------------
		// --------------------------------------------------------------------
		
		public ProducerProfile()
		{
			this(0,0L);
		}
		
		public ProducerProfile(int number)
		{
			this(number,0L);
		}
		
		public ProducerProfile(long seed)
		{
			this(0,seed);
		}
		
		public ProducerProfile(int number, long seed)
		{
			super(number);
			event = createEvent(number);
			filter = createFilter(number);
			rate = defaultRate();
//			r = new Random(seed ^ RATE_SEED);
			rate.setSeed(seed);
			
		}
		
		public ProducerProfile(String prefix)
		{
			super(prefix);
			event = createEvent(number);
			filter = createFilter(number);
			rate = (Distribution)Configuration.getInstance(prefix + "." 
					+ PAR_RATE,defaultRate());
//			rate = (Distribution)Configuration.getInstance(prefix + "." 
//					+ PAR_RATE);
//			r = new Random(RATE_SEED);
		}
	
		@Override
		public Profile clone(int number)
		{
			ProducerProfile p = (ProducerProfile)super.clone(number);
			p.event = createEvent(number);
			p.filter = createFilter(number);
//			p.rate = (Distribution)Configuration.getInstance(prefix + "." + 
//					PAR_RATE,defaultRate());
//			p.rate.setRandom(r);
			return p;
		}

		
        // publication rate ---------------------------------------------------
		// --------------------------------------------------------------------
		
		protected Distribution defaultRate()
		{
			return new ExponentialDistribution();
		}
		
		public Distribution getRate()
		{
			return rate;
		}
		
		public void setRate(Distribution rate)
		{
			this.rate = rate;
		}

		public void setRandom(Random r)
		{
			rate.setRandom(r);
//			this.r = r;
		}
		
		public void setSeed(long seed)
		{
			rate.setSeed(seed);
//			r.setSeed(scramble(seed));
		}
		
		public double getValue()
		{
			return rate.getValue();
		}
		
//		public long scramble(long seed)
//		{
//			return seed^2709905290654515382L;
//		}
		
		// advertisement management -------------------------------------------
		// --------------------------------------------------------------------
		
		public Filter getFilter()
		{
			return filter;
		}
		
		public void setFilter(Filter filter)
		{
			this.filter = filter;
		}
		
		public Filter createFilter(int profile)
		{
			Interval interval = new Interval().from(profile).to(profile);
			return new IntervalFilter("event", interval);
		}
		
		public Filter copyFilter()
		{
			Filter copy = (Filter)filter.clone();
			try
			{
				// assumes that all filters are subclasses of BasicFilter
				Field field = BasicFilter.class.getDeclaredField("id");
				field.setAccessible(true);
				field.set(copy, new RandomId());
			} 
			catch (Exception e)
			{
				LOG.error("unable to change id of filter '" + copy + "'",e);
			}
			return copy;
		}
		
		
		// event management ---------------------------------------------------
		// --------------------------------------------------------------------
		
		/**
		 * @return the event
		 */
		public Event getEvent() 
		{
			return event;
		}

		/**
		 * @param event the event to set
		 */
		public void setEvent(Event event) 
		{
			this.event = event;
		}
		
		public Event createEvent(int profile)
		{
			NameValueEvent e = new NameValueEvent();
			e.put("event", profile);
			return e;
		}
		
		public Event copyEvent()
		{
			Event e = (Event)event.clone();
			// TODO parzy check working with event ids
			try
			{
				Field field = Event.class.getDeclaredField("id");
				field.setAccessible(true);
				field.set(e, new IncrementalId());
			} 
			catch (Exception x)
			{
				LOG.error("unable to change id when cloning event '" + e + "'",x);
			}
			return e;
		}
	}
	
	protected static class ProducingComponent extends ApplicationComponent
	{
		protected Filter advertisement;
		protected PublicationEvent next;
		protected Distribution rate;
		
		// instantiation ------------------------------------------------------
		// --------------------------------------------------------------------
		
		public ProducingComponent()
		{
			super();
		}
		
		public ProducingComponent(ProducerProfile profile)
		{
			super(profile);
			advertisement = profile.copyFilter();
			rate = profile.getRate();
		}
		
		public ProducingComponent(Node node)
		{
			super(node);
		}
		
		public ProducingComponent(ProducerProfile profile, Node node)
		{
			super(profile, node);
			advertisement = profile.copyFilter();
			rate = profile.getRate();
		}
		
		public ProducingComponent(String prefix)
		{
			super(prefix);
		}
		
		@Override
		public ProducingComponent clone(Profile profile, Node node)
		{
			return clone((ProducerProfile)profile,node);
		}
		
		public ProducingComponent clone(ProducerProfile profile, Node node)
		{
			ProducingComponent p = (ProducingComponent)super.clone(profile,node);
			p.advertisement = profile!=null ? profile.copyFilter() : null;
			p.rate = profile!=null ? profile.getRate() : null;
			p.next = null;
			return p;
		}
		

		// component (de)activation -------------------------------------------
		// --------------------------------------------------------------------
		
		@Override
		public void init()
		{
			// set component attributes
			broker.putAttribute("profile", application.getNumber());		
		}
		
		@Override 
		public void activate()
		{
			// advertise and
			broker.advertise(advertisement);
//			System.err.println("advertised: "+advertisement);
			
			// schedule first publication event for this component
			if (next != null)
			{
				next.cancel();
			}
			double delay = rate.getValue();
			next = new PublicationEvent(this);
			schedule((long)delay,next,node,application.getPid());
		}
		
		@Override
		public void passivate()
		{		
			// unadvertise and
			broker.unadvertise(advertisement);
			
			// cancel all old events
			if (next != null)
			{
				next.cancel();
			}
		}

		
		// event publication --------------------------------------------------
		// --------------------------------------------------------------------
		
		@Override
		public void handle(Node node, int pid, ComponentEvent event)
		{
			if (event instanceof PublicationEvent)
			{
				handle(node, pid, (PublicationEvent)event);
			}
			else
			{
				super.handle(node, pid, event);
			}
		}
		
		public void handle(Node node, int pid, PublicationEvent event)
		{
			// publish and schedule next publication
			publish();
			double delay = rate.getValue();
			next = event;
			schedule((long)delay,next,node,pid);
		}
		
		public void publish()
		{
			// publish a rebeca event
			Event e = ((ProducerProfile)application).copyEvent();
			broker.publish(e);
			if (LOG.isInfoEnabled()) LOG.info("'" + this + "' published event '" 
					+ e + "' at time " + CommonState.getTime());
		}
		
		protected void schedule(long delay, Object event, Node node, int pid)
		{
			EDSimulator.add(delay, event, node, pid);
		}

		
		// visualization ------------------------------------------------------
		// --------------------------------------------------------------------
		
		public String toString()
		{
			String app = application!=null ? ""+application.getNumber() : "?";
			String index = node!=null ? ""+node.getIndex() : "?";
			return "ProducingComponent[" + app + "]#" + index + "@" 
					+ Integer.toHexString(hashCode());
		}
	}
	
	
	public static class PublicationEvent extends ComponentEvent
	{
		protected ProducingComponent component;
		
		public PublicationEvent(ProducingComponent component)
		{
			this.component = component;
		}

		/**
		 * @return the component
		 */
		public ProducingComponent getComponent() 
		{
			return component;
		}
	}
	
	
	// instantiation ----------------------------------------------------------
	// ------------------------------------------------------------------------
	
	public EventProducing(String prefix)
	{
		super(prefix);
		
		// initialize random seed generator
		if (!Configuration.contains(prefix + "." + PAR_PROFILE + "." + PAR_SEED ))
		{
			((ProducerProfile)profile).setSeed(seed^RATE_SEED);
		}
	}
	
	@Override
	protected long scramble(long seed)
	{
		return seed ^ 6945087603498072566L;
	}
	
	// defaults ---------------------------------------------------------------
	// ------------------------------------------------------------------------
	
	@Override
	protected Allocation defaultAllocation() 
	{
		return new OuterAllocation(seed^ALLOCATION_SEED);
	}

	@Override
	protected Distribution defaultBirthDistribution() 
	{
		return new ExponentialDistribution(seed^BIRTH_SEED);
	}

	@Override
	protected ApplicationComponent defaultComponent() 
	{
		return new ProducingComponent(defaultProfile(), Network.prototype);
	}

	@Override
	protected Distribution defaultDeathDistribution() 
	{
		return new ExponentialDistribution(seed^DEATH_SEED);
	}

	@Override
	protected ProbabilityDensity defaultPlacement() 
	{
		return new UniformDensity(seed^PLACEMENT_SEED);
	}

	@Override
	protected ProducerProfile defaultProfile() 
	{
		return new ProducerProfile(prefix + "." + PAR_PROFILE);
//		ProducerProfile p = new ProducerProfile(prefix + "." + PAR_PROFILE);
//		LOG.error("Rate: "+p.getRate());
//		p.setPid(CommonState.getPid());
//		return p;
	}

	@Override
	protected ProbabilityDensity defaultSelection() 
	{
		return new UniformDensity(seed^SELECTION_SEED);
	}
	
	
	// event management -------------------------------------------------------
	// ------------------------------------------------------------------------
	
	public Event getEvent(int profile)
	{
		return ((ProducerProfile)getProfile(profile)).getEvent();
	}

	public void setEvent(int profile, Event event)
	{
		((ProducerProfile)getProfile(profile)).setEvent(event);
	}
	
	public Event createEvent(int profile)
	{
		NameValueEvent e = new NameValueEvent();
		e.put("profile", profile);
		return e;
	}
			
	public Event createEvent(Event event)
	{
		Event e = (Event)event.clone();
		try
		{
			Class<?> clazz = e.getClass();
			Field field = clazz.getDeclaredField("id");
			field.setAccessible(true);
			field.set(e, new RandomId());
		} 
		catch (Exception x)
		{
			LOG.error("unable to change id of event '" + e + "'",x);
		}
		return e;
	}
	
	
	// filter management ------------------------------------------------------
	// ------------------------------------------------------------------------
		
	public Filter getFilter(int profile)
	{
		return ((ProducerProfile)getProfile(profile)).getFilter();
	}

	public void setFilter(int profile, Filter filter)
	{
		((ProducerProfile)getProfile(profile)).setFilter(filter);
	}
	
	public void addEventType(int profile, Event event)
	{
		setFilter(profile, getFilter(profile).or(createFilter(event)));
	}
	
	public void addEventType(int profile, int type)
	{
		setFilter(profile, getFilter(profile).or(createFilter(type)));
	}
	
	public Filter createFilter(Event event)
	{
		if ( !(event instanceof NameValueEvent) )
		{
			LOG.error("event '" + event + "' is no name/value event");
			return null;
		}
		
		NameValueEvent e = (NameValueEvent)event;
		Object type = e.get("profile");
		
		if ( !(type instanceof Integer) )
		{
			LOG.error("event '" + e + "' does not contain the attribute 'profile'");
			return null;
		}
		
		Integer i = (Integer)type;
		Interval interval = new Interval().from(i).to(i);
		return new IntervalFilter("profile", interval);
	}
	
	public Filter createFilter(int number)
	{
		Interval interval = new Interval().from(number).to(number);
		return new IntervalFilter("profile", interval);
	}
	
	
	// publication rate management --------------------------------------------
	// ------------------------------------------------------------------------
	
	public Distribution getRate(int profile)
	{
		return ((ProducerProfile)getProfile(profile)).getRate();
	}
	
	public void setRate(int profile, Distribution rate)
	{
		((ProducerProfile)getProfile(profile)).setRate(rate);
	}
}	
