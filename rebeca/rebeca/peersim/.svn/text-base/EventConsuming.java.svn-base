/**
 * 
 */
package rebeca.peersim;

import java.lang.reflect.*;

import org.apache.log4j.*;

import peersim.core.*;

import rebeca.*;
import rebeca.filter.*;
import rebeca.math.*;
import rebeca.namevalue.*;
import rebeca.util.*;

/**
 * @author parzy
 *
 */
public class EventConsuming extends ApplicationProtocol 
{	
	// parameters and constants -----------------------------------------------
	// ------------------------------------------------------------------------

	private static final Logger LOG = Logger.getLogger(EventConsuming.class);

	
	// profiles and components ------------------------------------------------
	// ------------------------------------------------------------------------

	public static class ConsumerProfile extends Profile
	{
		protected Filter filter;
		
		// instantiation ------------------------------------------------------
		// --------------------------------------------------------------------
		
		public ConsumerProfile()
		{
			super(0);
		}
		
		public ConsumerProfile(int number)
		{
			super(number);
			this.filter = createFilter(number); 
		}
		
		public ConsumerProfile(int number, Filter filter)
		{
			super(number);
			this.filter = filter;
		}
		
		public ConsumerProfile(String prefix)
		{
			super(prefix);
		}
		
		public Profile clone(int number)
		{
			ConsumerProfile p = (ConsumerProfile)super.clone(number);
			p.filter = createFilter(number);
			return p;
		}
		
		
		// filter management --------------------------------------------------
		// --------------------------------------------------------------------
		
		/**
		 * @return the filter
		 */
		public Filter getFilter() 
		{
			return filter;
		}

		/**
		 * @param filter the filter to set
		 */
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
	}

	public static class ConsumingComponent extends ApplicationComponent
	{
		protected Filter subscription;
		
		// initialization -----------------------------------------------------
		// --------------------------------------------------------------------
		
		public ConsumingComponent() 
		{
			super();
		}

		public ConsumingComponent(ConsumerProfile profile) 
		{
			super(profile);
			subscription = profile.copyFilter();
		}
		
		public ConsumingComponent(Node node) 
		{
			super(node);
		}

		public ConsumingComponent(String prefix) 
		{
			super(prefix);
		}

		public ConsumingComponent(ConsumerProfile profile, Node node)
		{
			super(profile,node);
			subscription = profile.copyFilter();
		}

		@Override
		public ConsumingComponent clone(Profile profile, Node node)
		{
			return clone((ConsumerProfile)profile,node);
		}
		
		public ConsumingComponent clone(ConsumerProfile profile, Node node)
		{
			ConsumerProfile p = (ConsumerProfile)profile;
			ConsumingComponent c = (ConsumingComponent)super.clone(p, node);
			c.subscription = p.copyFilter();	
			return c;
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
			broker.subscribe(subscription);
		}
		
		@Override
		public void passivate()
		{
			broker.unsubscribe(subscription);
		}
			
		// component event handling -------------------------------------------
		// --------------------------------------------------------------------
		
		@Override
		public void notify(Event event)
		{
			if (LOG.isInfoEnabled()) LOG.info("'" + this + "' received event '" 
					+ event + "' at time " + CommonState.getTime());
		}
		
		
		// visualization ------------------------------------------------------
		// --------------------------------------------------------------------
		
		public String toString()
		{
			String app = application!=null ? ""+application.getNumber() : "?";
			String index = node!=null ? ""+node.getIndex() : "?";
			return "ConsumingComponent[" + app + "]#" + index + "@" 
					+ Integer.toHexString(hashCode());
		}
	}
	
	
	// instantiation ----------------------------------------------------------
	// ------------------------------------------------------------------------
	
	public EventConsuming(String prefix)
	{
		super(prefix);
	}
	
	@Override
	protected long scramble(long seed)
	{
		return seed ^ 3968785927618975905L;
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
		return new ConsumingComponent(defaultProfile(), Network.prototype);
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
	protected ConsumerProfile defaultProfile() 
	{
		return new ConsumerProfile(0);
	}

	@Override
	protected ProbabilityDensity defaultSelection() 
	{
		return new UniformDensity(seed^SELECTION_SEED);
	}
	
	
	// filter management ------------------------------------------------------
	// ------------------------------------------------------------------------
		
	public Filter getFilter(int profile)
	{
		return ((ConsumerProfile)getProfile(profile)).getFilter();
	}

	public void setFilter(int profile, Filter filter)
	{
		((ConsumerProfile)getProfile(profile)).setFilter(filter);
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
}
