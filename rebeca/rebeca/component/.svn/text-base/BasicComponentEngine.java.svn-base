/*
 * $Id$
 */
package rebeca.component;

import java.util.*;

import org.apache.log4j.Logger;

import rebeca.*;
import rebeca.broker.*;
import rebeca.scope.*;

/**
 * @author parzy
 *
 */
public class BasicComponentEngine extends BasicBrokerEngine 
implements ComponentEngine 
{
	// Constants and Parameters -----------------------------------------------
	// ------------------------------------------------------------------------
	
	// TODO parzy maybe define it within the interface MatchingEngine
	public static final Class<ComponentEngine> KEY = ComponentEngine.class;

	/** 
	 * Logger for ProcessingEngines. 
	 */
	private static final Logger LOG = Logger.getLogger(KEY);

	
	// constructors and initialization ----------------------------------------
	// ------------------------------------------------------------------------
	
	public BasicComponentEngine()
	{
		this((Broker)null);
	}
	
	public BasicComponentEngine(Broker broker)
	{
		super(broker);
	}
	
	/**
	 * PeerSim convenience constructor ignores arguments and calls the default 
	 * constructor.
	 * @param prefix ignored
	 */
	public BasicComponentEngine(String prefix)
	{
		this();
	}

	
	
	// getters and setters ----------------------------------------------------
	// ------------------------------------------------------------------------
	
	@Override
	public Object getKey() 
	{
		return KEY;
	}
	
	// activation and passivation ---------------------------------------------
	// ------------------------------------------------------------------------
	
	// unplug all components 
	public void passivate()
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("unplugging all components for shutdown");
		}
		
		// iterate over all components in reverse order
		List<Component> components = broker.getComponents();
		ListIterator<Component> lit = components.listIterator(components.size());
		while (lit.hasPrevious())
		{
			Component component = lit.previous();
			ContactProfile profile = component.getContactProfile();
			broker.unplug(component);
			// TODO parzy replace the busy waiting 
			// wait until all sink is deregistered
			while (broker.getSink(profile)!=null) { Thread.yield(); };
		}		
	}
	
	
	// component connections --------------------------------------------------
	// ------------------------------------------------------------------------
	
	public ContactProfile connect(ContactProfile profile)
	{
		if (profile instanceof ComponentContactProfile)
		{
			return connect((ComponentContactProfile)profile);
		}
		LOG.error("unknown contact profile '" + profile 
				+ "': no connection established");
		return null;
	}
	
	public ContactProfile connect(ComponentContactProfile profile)
	{
		Component component = profile.getComponent();
		plug(component);
		return profile;
	}
	
	
	// EventSinks -------------------------------------------------------------
	// ------------------------------------------------------------------------
	
	public Object plug(Object obj)
	{
		if (obj instanceof Component)
		{
			return plug((Component)obj);
		}
		if (obj instanceof ComponentSink)
		{
			return plug((ComponentSink)obj);
		}
		return obj;
	}
	
	public ComponentSink plug(Component component)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("creating pluggable component sink providing a broker "
					+ "interface for component '" + component + "'");
		}
		return new ComponentConnector(component);
	}
	
	public EventSink plug(ComponentSink sink)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("connecting component sink '" + sink + "' to a broker " 
					+ "sink");
		}
		SinkConnector connector = new SinkConnector(sink);
		return connector.getBrokerSink();
	}
	
	protected static class ComponentConnector extends PluggableComponentSink
	implements ComponentBroker
	{
		
		protected Component component;
		protected Publisher publisher;
		protected Subscriber subscriber;
		protected Advertiser advertiser;
		protected ScopeManager scoper;
		
		public ComponentConnector(Component component)
		{
			this.component = component;
			// TODO parzy maybe revise later
			component.setBroker(this);
		}
		
		@Override
		public void in(Event event)
		{
			component.notify(event);
		}
		
		@Override
		public ContactProfile getContactProfile()
		{
			return component.getContactProfile();
		}
		
		@Override
		public Component getComponent()
		{
			return component;
		}
		
		@Override @SuppressWarnings("unchecked")
		public <T> T getComponentInterface(Class<T> clazz)
		{
			if (clazz != null && clazz.isAssignableFrom(component.getClass()))
			{
				return (T)component;
			}
			
			return super.getComponentInterface(clazz);
		}
		
		@Override @SuppressWarnings("unchecked")
		public <T> T getInterface(Class<T> clazz)
		{
			if (clazz != null && clazz.isAssignableFrom(component.getClass()))
			{
				return (T)component;
			}
			
			return super.getInterface(clazz);
		}
		
		@Override
		public void init()
		{
			// prepare delegation of broker functionality
			publisher = getBrokerInterface(Publisher.class);
			if (publisher == null)
			{
				LOG.warn("initializing component connector: "
						+ "no publisher interface found");
			} 
			else if (LOG.isDebugEnabled())
			{
				LOG.debug("initializing component connector: "
						+ "publisher interface connected");
			}
			subscriber = getBrokerInterface(Subscriber.class);
			if (subscriber == null)
			{
				LOG.warn("initializing component connector: "
						+ "no subscriber interface found");
			}
			else if (LOG.isDebugEnabled())
			{
				LOG.debug("initializing component connector: "
						+ "subscriber interface connected");
			}
			advertiser = getBrokerInterface(Advertiser.class);
			if (advertiser == null)
			{
				LOG.warn("initializing component connector: "
						+ "no advertiser interface found");
			}
			else if (LOG.isDebugEnabled())
			{
				LOG.debug("initializing component connector: "
						+ "advertiser interface connected");
			}
			scoper = getBrokerInterface(ScopeManager.class);
			if (scoper == null)
			{
				LOG.warn("initializing component connector: "
						+ "no scope managing interface found");
			}
			else if (LOG.isDebugEnabled())
			{
				LOG.debug("initializing component connector: "
						+ "scope managing interface connected");
			}
			
			// initialize component
			if (LOG.isDebugEnabled())
			{
				LOG.debug("initializing component '" + component + "'");
			}
			component.init();
		}
		
		@Override
		public void activate()
		{
			// activate component
			if (LOG.isDebugEnabled())
			{
				LOG.debug("activating component '" + component + "'");
			}
			component.activate();
		}
		
		@Override
		public void passivate()
		{
			// passivate component
			if (LOG.isDebugEnabled())
			{
				LOG.debug("passivating component '" + component + "'");
			}
			component.passivate();
		}

		@Override
		public void exit()
		{
			// exitcomponent
			if (LOG.isDebugEnabled())
			{
				LOG.debug("exiting component '" + component + "'");
			}
			component.exit();
		}

		// visualization ------------------------------------------------------
		// --------------------------------------------------------------------
		
		@Override
		public String toString()
		{
			return component.toString();
		}
		
		// ComponentBroker implementation -------------------------------------
		// --------------------------------------------------------------------
		
		@Override
		public void publish(Event event)
		{
			if (LOG.isDebugEnabled()) LOG.debug("publishing event '" + event + "'");
			out(event);
		}
		
		@Override
		public void subscribe(Filter filter)
		{
			if (subscriber != null)
			{
				if (LOG.isDebugEnabled()) LOG.debug("subscribing for '" + filter + "'");
				subscriber.subscribe(filter);
			}
		}
		
		@Override
		public void unsubscribe(Filter filter)
		{
			if (subscriber != null)
			{
				subscriber.unsubscribe(filter);
			}
		}

		@Override
		public void advertise(Filter filter)
		{
			if (advertiser != null)
			{
				advertiser.advertise(filter);
			}
		}

		@Override
		public void unadvertise(Filter filter)
		{
			if (advertiser != null)
			{
				advertiser.unadvertise(filter);
			}
		}
		
		public void join(String name)
		{
			if (scoper != null)
			{
				scoper.join(name);
			}
		}

		public void leave(String name)
		{
			if (scoper != null)
			{
				scoper.leave(name);
			}
		}

		public void open(Scope scope)
		{
			if (scoper != null)
			{
				scoper.open(scope);
			}
		}

		public void close(String name)
		{
			if (scoper != null)
			{
				scoper.close(name);
			}
		}
		
		public Object putAttribute(String attribute, Object value)
		{
			return scoper != null ? scoper.putAttribute(attribute,value) : null;
		}
		
		public Object removeAttribute(String attribute)
		{
			return scoper != null ? scoper.removeAttribute(attribute) : null;
		}
		
		public Object getProperty(String property)
		{
			return scoper != null ? scoper.getProperty(property) : null;
		}
		
		// TODO parzy remove ASAP
		public void putScopes(ScopeRepository scopes)
		{
			if (scoper != null) scoper.putScopes(scopes);
		}
	}
	

	
	protected static class SinkConnector 
	{
		protected BrokerSink bs;
		protected ConnectorSink cs;
		protected ComponentContactProfile profile;
		
		// TODO parzy revise activate/deactivate
		protected boolean active;
		
		protected class BrokerSink extends PluggableEventSink
		{
			public BrokerSink()
			{
				super();
			}
			
			@Override
			public void out(Event event)
			{
				if (active) cs.in(event);
			}
			
			@Override
			public boolean isLocal()
			{
				return cs.isLocal();
			}
			
			@Override
			public void init()
			{
				cs.init();
			}
			@Override
			public void exit()
			{
				cs.exit();
			}
			@Override
			public void activate()
			{
				active = true;
				cs.activate();
			}
			@Override
			public void passivate()
			{
				active = false;
				cs.passivate();
			}
			
			@Override
			public EventSink getSink()
			{
				return this;
			}
			
			@Override
			public String toString()
			{
				// TODO parzy maybe revise the visualization
				return cs.getComponent().toString();
			}
			
			@Override
			public ContactProfile getContactProfile()
			{
				return cs.getContactProfile();
			}
		}
		
		protected class ConnectorSink extends PluggableComponentSink
		{
			public ConnectorSink(ComponentSink sink)
			{
				super(sink);
			}
			
			@Override 
			public void out(Event event)
			{
				if (active) 
				{
					bs.in(event);
				}
				else
				{
					if (LOG.isDebugEnabled()) LOG.error("'" + this 
							+ "' dropped '" + event +"'");
				}
			}
			
			@Override
			public EventProcessor getProcessor()
			{
				return bs.getProcessor();
			}
		}
	
		public SinkConnector(ComponentSink sink)
		{
			cs = new ConnectorSink(sink);
			bs = new BrokerSink();
			profile = new ComponentContactProfile(sink.getComponent()); 
			active = false;
		}
		
		public EventSink getBrokerSink()
		{
			return bs;
		}
		
		public ComponentSink getComponentSink()
		{
			return cs;
		}
	}
}
