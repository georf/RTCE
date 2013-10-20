/**
 * 
 */
package rebeca.peersim;

import java.util.*;

import org.apache.log4j.Logger;

import peersim.config.*;
import peersim.core.*;
import peersim.edsim.*;

import rebeca.*;
import rebeca.component.*;
import rebeca.math.*;


/**
 * @author parzy
 *
 */
public abstract class ApplicationProtocol implements EDProtocol
{
	// parameters, default values, and logger ---------------------------------
	// ------------------------------------------------------------------------

	private static final String PAR_SIZE = "size";
	private static final String PAR_SEED = "seed";
	private static final String PAR_BIRTH = "birth";
	private static final String PAR_DEATH = "death";
	private static final String PAR_PROFILE = "profile";
	private static final String PAR_COMPONENT = "component";
	private static final String PAR_ALLOCATION = "allocation";
	private static final String PAR_PLACEMENT = "placement";
	private static final String PAR_SELECTION = "selection";
	private static final String PAR_BROKERING = "brokering";
	private static final String PAR_START = "start";
	
	protected static final long ALLOCATION_SEED = -5636121963752100344L;
	protected static final long BIRTH_SEED = 16180523144312L; 
	protected static final long CLIENT_SEED = -2509471684403106691L;
	protected static final long DEATH_SEED = 19480310071545L;
	protected static final long PLACEMENT_SEED = -6461430860393812107L;
	protected static final long SELECTION_SEED = 6287546875467792594L;
		
	private static final Logger LOG = Logger.getLogger(ApplicationProtocol.class);
	
	// classes for profiles, events, and components ---------------------------
	// ------------------------------------------------------------------------
	
	public abstract static class Profile implements Cloneable
	{
		protected String prefix;
		protected int number;
		protected int pid;

		// instantiation ------------------------------------------------------
		// --------------------------------------------------------------------
		
		public Profile()
		{
			this(0);
		}
		
		public Profile(int number)
		{
			this.number = number;
			this.pid = -1;
		}
		
		public Profile(String prefix)
		{
			this(0);
			this.pid = CommonState.getPid();
			this.prefix = prefix;
		}
		
		public Profile clone(int number)
		{
			try
			{
				Profile clone = (Profile)super.clone();
				clone.number = number;
				return clone;
			}
			catch (CloneNotSupportedException e)
			{
				return null;
			}
		}
		
		// getters and setters ------------------------------------------------
		// --------------------------------------------------------------------
		
		public int getPid()
		{
			return pid;
		}
		
		public void setPid(int pid)
		{
			this.pid = pid;
		}
		
		/**
		 * @return the number
		 */
		public int getNumber() 
		{
			return number;
		}

		/**
		 * @param number the number to set
		 */
		public void setNumber(int number) 
		{
			this.number = number;
		}
	}
	
	protected abstract static class ComponentEvent
	{
		private ApplicationComponent component;
		private boolean canceled;
		
		public ComponentEvent()
		{
			this(null);
		}
		
		public ComponentEvent(ApplicationComponent component)
		{
			this.component = component;
			this.canceled = false;
		}
		
		/**
		 * @return the component
		 */
		public ApplicationComponent getComponent() 
		{
			return component;
		}

		/**
		 * @param component the component to set
		 */
		public void setComponent(ApplicationComponent component) 
		{
			this.component = component;
		}

		public void cancel()
		{
			canceled = true;
		}
		
		public boolean isCancelled()
		{
			return canceled;
		}
	}
	
	protected static class BirthEvent extends ComponentEvent { }
	
	protected static class DeathEvent extends ComponentEvent
	{
		public DeathEvent(ApplicationComponent component)
		{
			super(component);
		}
	}
	
	public abstract static class ApplicationComponent extends AbstractComponent
	implements Cloneable
	{	
		// fields -------------------------------------------------------------
		// --------------------------------------------------------------------
		
		protected String prefix;
		protected Node node;
		protected Profile application;
		
		
		// instantiation ------------------------------------------------------
		// --------------------------------------------------------------------
		
		public ApplicationComponent()
		{
			this(null,null);
		}

		public ApplicationComponent(Profile profile)
		{
			this(profile,null);
		} 
		
		public ApplicationComponent(Node node)
		{
			this(null,node);
		}
		
		public ApplicationComponent(Profile profile, Node node)
		{
			this.prefix = null;
			this.node = node;
			this.application = profile;
		}
		
		public ApplicationComponent(String prefix)
		{
			this(null,null);
			this.prefix = prefix;
		}

		public ApplicationComponent clone(Profile profile, Node node)
		{
			try
			{
				ApplicationComponent clone = (ApplicationComponent)super.clone();
				clone.node = node;
				clone.application = profile;
				return clone;
			}
			catch (CloneNotSupportedException e)
			{
				return null;
			}
		}
		
		// getters and setters ------------------------------------------------
		// --------------------------------------------------------------------
		
		public Node getNode()
		{
			return node;
		}
		
		public Profile getApplicationProfile()
		{
			return application;
		}
		
		// Rebeca & PeerSim event handling ------------------------------------
		// --------------------------------------------------------------------
		
		// TODO parzy implement this method in subclasses in order to make this class static
		public void notify(Event event)
		{
			if (LOG.isInfoEnabled())
			{
				LOG.info("'" + this + "' received event '" + event + "'");
			}
		}
		
		public void handle(Node node, int pid, ComponentEvent event)
		{
			LOG.error("unhandled PeerSim event '" + event + "'");
		}
	}

	public abstract static class ProbabilityDensity
	{
		private static final String PAR_SEED = "seed";
	
		protected Random r;

		protected ProbabilityDensity() { }

		protected ProbabilityDensity(Random r)
		{
			this.r = r;
		}

		protected ProbabilityDensity(long seed)
		{
			r = new Random(scramble(seed));
		}

		protected ProbabilityDensity(String prefix)
		{
			long seed = Configuration.getLong(prefix + "." + PAR_SEED, CommonState.r.getLastSeed());
			r = new Random(scramble(seed));
		}

		public void setRandom(Random r)
		{
			this.r = r;
		}

		public void setSeed(long seed)
		{
			r.setSeed(scramble(seed));
		}

		protected long scramble(long seed)
		{
			return seed ^ 273508688945849502L;
		}

		public void shuffle(double[] p)
		{
			for (int i=0; i<p.length; i++)
			{
				int j = r.nextInt(p.length-i);
				double tmp = p[i]; p[i] = p[i+j]; p[i+j] = tmp;
			}
		}

		public abstract void probabilities(double[] p);
	}
	
	public abstract static class Allocation
	{
		
		protected Random r;
		
		private static final String PAR_SEED = "seed";
		
		protected Allocation() { }
		
		protected Allocation(Random r)
		{
			this.r = r;
		}
		
		protected Allocation(long seed)
		{
			this.r = new Random(scramble(seed));
		}
		
		public Allocation(String prefix)
		{
			long seed = Configuration.getLong(prefix + "." + PAR_SEED, CommonState.r.getLastSeed());
			r = new Random(scramble(seed));
		}
		
		public void setRandom(Random r)
		{
			this.r = r;
		}
		
		public void setSeed(long seed)
		{
			r.setSeed(scramble(seed));
		}
		
		protected long scramble(long seed)
		{
			return seed ^ 1299972159396458777L;
		}
			
		public abstract MultiDistribution multiDistribution(Profile[] profiles, double[] p, Node[] nodes, double[] q);
		
	}

	
	// basic protocol fields --------------------------------------------------
	// ------------------------------------------------------------------------
	
	protected Logger log; 
	protected String prefix;
	
	protected Node[] nodes;
	protected Profile[] profiles;
	protected int size;
	protected double[] selections;
	protected double[] placements;
	

	protected ProbabilityDensity selection;
	protected ProbabilityDensity placement;
	protected Allocation allocation;
	
	protected long seed;
	protected Distribution death;
	protected Distribution birth;
	protected MultiDistribution client;
	
	
	protected Profile profile;
	protected ApplicationComponent component;
	
	protected BirthEvent next;
	protected long start;

	// pids
	protected int pid;
	protected int brokering;
	
	// instantiation ----------------------------------------------------------
	// ------------------------------------------------------------------------
	
	public ApplicationProtocol(String prefix)
	{
		// basic settings
		this.prefix = prefix;
		log = Logger.getLogger(ApplicationProtocol.class);
		pid = CommonState.getPid();
		brokering = Configuration.getPid(prefix + "." + PAR_BROKERING);
		seed = scramble( Configuration.getLong(prefix + "." + PAR_SEED, CommonState.r.getLastSeed()) );

		// get profile and selection
		size = Configuration.getInt(prefix + "." + PAR_SIZE,1);
		profile = (Profile)Configuration.getInstance(prefix + "." + PAR_PROFILE, defaultProfile());
		selection = (ProbabilityDensity)Configuration.getInstance(prefix + "." + PAR_SELECTION, defaultSelection());
		if (!Configuration.contains(prefix + "." + PAR_SELECTION + "." + PAR_SEED))
		{
			selection.setSeed(seed ^ SELECTION_SEED);
		} 
		
		// get component and placement
		component = (ApplicationComponent)Configuration.getInstance(prefix + "." + PAR_COMPONENT, defaultComponent());
		placement = (ProbabilityDensity)Configuration.getInstance(prefix + "." + PAR_PLACEMENT, defaultPlacement());
		if (!Configuration.contains(prefix + "." + PAR_PLACEMENT + "." + PAR_SEED))
		{
			placement.setSeed(seed ^ PLACEMENT_SEED);
		}
		
		// get allocation
		allocation = (Allocation)Configuration.getInstance(prefix + "." + PAR_ALLOCATION, defaultAllocation());
		if (!Configuration.contains(prefix + "." + PAR_ALLOCATION + "." + PAR_SEED))
		{
			allocation.setSeed(seed ^ ALLOCATION_SEED);
		}
		
		// get birth and death rates
		birth = (Distribution)Configuration.getInstance(prefix + "." + PAR_BIRTH, defaultBirthDistribution());	
		if (!Configuration.contains(prefix + "." + PAR_BIRTH + "." + PAR_SEED))
		{
			birth.setSeed(seed ^ BIRTH_SEED);
		}
		death = (Distribution)Configuration.getInstance(prefix + "." + PAR_DEATH, defaultDeathDistribution());
		if (!Configuration.contains(prefix + "." + PAR_DEATH + "." + PAR_SEED))
		{
			birth.setSeed(seed ^ DEATH_SEED);
		}
						
		// get start time but schedule no event yet
		start = Configuration.getLong(prefix + "." + PAR_START, 0);
		next = null;
		
		// nodes, profiles, and client distribution are also left for lazy initiatlization
		nodes = null;
		profiles = null;
		client = null;	
	}
	
	@Override
	public Object clone()
	{
		activate();
		return this;
	}

	public void activate()
	{
		// if not initialized
		if (next==null)
		{
			// schedule first birth event
			long delta = Math.max(start-CommonState.getTime(),0);
			next = new BirthEvent();
			Node node = CommonState.getNode();
			schedule(delta,next,node);
		}	
	}
	
	// randomness -------------------------------------------------------------
	// ------------------------------------------------------------------------
	
	protected long scramble(long seed)
	{
		return seed ^ 7610477939514324911L;
	}

	public long getSeed()
	{
		return seed;
	}
	
	public void setSeed(long seed)
	{
		this.seed = seed;
	}
	
	
	// profile management -----------------------------------------------------
	// ------------------------------------------------------------------------	

	/**
	 * Returns the default profile instance as prototype.
	 */
	protected abstract Profile defaultProfile();
	
	/**
	 * Returns the profile prototype.
	 * @return the profile prototype
	 */
	public Profile getProfile()
	{
		return profile;
	}
	
	/**
	 * Sets the profile prototype
	 * @param profile profile prototype
	 */
	public void setProfile(Profile profile)
	{
		this.profile = profile;
		// TODO: profile initialization
	}
	
	/**
	 * Gets the number of profiles used for creating components.
	 * @return the number of component profiles.
	 */
	public int getSize()
	{
		return size;
	}

	/**
	 * Sets the number of profiles for creating components.
	 * @param size number of component profiles.
	 */
	public void setSize(int size)
	{
		this.size = size;
	}

	protected abstract ProbabilityDensity defaultSelection();
	
	public ProbabilityDensity getSelection()
	{
		return selection;
	}
	
	public void setSelection(ProbabilityDensity selection)
	{
		this.selection = selection;
	}
	
	public Profile[] profiles()
	{
		if (profiles==null) createProfiles();
		return profiles;
	}
	
	public void createProfiles()
	{
		profiles = new Profile[size];
		for (int i=0; i<size; i++)
		{
			profiles[i] = profile.clone(i);
		}
		selection.probabilities((selections = new double[size]));
	}
	
	/**
	 * Returns the profile at the specified index
	 * @param number position in the profile array.
	 * @return the profile at the given index
	 */
	public Profile getProfile(int number)
	{
		return profiles()[number];
	}
	
	/**
	 * 
	 * @param number
	 * @param profile
	 */
	public void setProfile(int number, Profile profile)
	{
		profiles()[number] = profile;
	}
	

	// network management -----------------------------------------------------
	// ------------------------------------------------------------------------
	
	protected abstract ProbabilityDensity defaultPlacement();
	
	public ProbabilityDensity getPlacement()
	{
		return placement;
	}
	
	public void setPlacement(ProbabilityDensity placement)
	{
		this.placement = placement;
	}

	public Node[] nodes()
	{
		if (nodes==null) createNodes();
		return nodes;
	}
	
	public void createNodes()
	{
		nodes = new Node[Network.size()];
		for (int i=0; i < Network.size(); i++)
		{
			nodes[i] = Network.get(i);
		}
		placement.probabilities((placements = new double[Network.size()]));
	}
	
	
	// component management and distribution ----------------------------------
	// ------------------------------------------------------------------------

	protected abstract ApplicationComponent defaultComponent();

	public ApplicationComponent getComponent()
	{
		return component;
	}
	
	public void setCompoment(ApplicationComponent component)
	{
		this.component = component;
	}
	
	public ApplicationComponent createComponent(Profile profile, Node node)
	{
		return component.clone(profile,node);
	}	
	
	protected abstract Allocation defaultAllocation();
	
	public Allocation getAllocation()
	{
		return allocation;
	}
	
	public void setAllocation(Allocation allocation)
	{
		this.allocation = allocation;
	}
	
	public MultiDistribution client()
	{
		if (client==null) createMultiDistribution();
		return client;
	}
	
	public void createMultiDistribution()
	{
		client = allocation.multiDistribution(profiles(), selections, nodes(), placements);
		client.setSeed(seed ^ CLIENT_SEED);
	}

	public double getProbability(int node, int profile)
	{
		return client().getProbability(node,profile);
	}
	
	public void setProbability(double p, int node, int profile)
	{
		client().setProbability(p, node, profile);
	}
		

	// birth and death rates --------------------------------------------------
	// ------------------------------------------------------------------------
	
	public Distribution getBirthDistribution()
	{
		return birth;
	}
	
	public void setBirthDistribution(Distribution birth)
	{
		this.birth = birth;
	}

	protected abstract Distribution defaultBirthDistribution();
	
	public Distribution getDeathDistribution()
	{
		return death;
	}
	
	public void setDeathDistribution(Distribution death)
	{
		this.death = death;
	}

	protected abstract Distribution defaultDeathDistribution();


	// PeerSim event handling and scheduling ----------------------------------
	// ------------------------------------------------------------------------
	
	@Override
	public void processEvent(Node node, int pid, Object event) 
	{
		// sanity check
		if ( !(event instanceof ComponentEvent) )
		{
			log.error("unhandled PeerSim event '" + event + "'");
			return;
		}
		
		// ignore canceled events
		ComponentEvent e = (ComponentEvent)event;	
		if ( e.isCancelled() ) return;
		
		// dispatch types of component events
		handle(node,pid, e);
	}

	public void handle(Node node, int pid, ComponentEvent event)
	{
		if (event instanceof BirthEvent)
		{
			handle(node, pid,(BirthEvent)event);
		}
		else if (event instanceof DeathEvent)
		{
			handle(node, pid,(DeathEvent)event);
		}
		else
		{
			ApplicationComponent component = event.getComponent();
			component.handle(node, pid, event);
		}	
	}
	
	public void handle(Node node, int pid, BirthEvent event)
	{
		// determine next profile class and hosting broker
		Object[] vector = client().next();
		Profile profile = (Profile)vector[0];
		Node host = (Node)vector[1];
		Broker broker = (Broker)host.getProtocol(brokering);
		
		// create and connect component
		ApplicationComponent component = createComponent(profile,host);
		if (LOG.isInfoEnabled()) LOG.info("created component '" + component 
				+ " at time " + CommonState.getTime());
		broker.plug(component);
		
		// schedule component removal
		double lifetime = death.getValue();
		DeathEvent unplug = new DeathEvent(component);
		schedule((long)lifetime,unplug,host);
		
		// schedule birth of next component
		double delay = birth.getValue();
		next = new BirthEvent();
		schedule((long)delay,next,node);
	}
	
	public void handle(Node node, int pid, DeathEvent event)
	{
		// unplug component
		Broker broker = (Broker)node.getProtocol(brokering);
		Component component = event.getComponent();
		broker.unplug(component);
		if (LOG.isInfoEnabled()) LOG.info("killed component '" + component 
				+ " at time " + CommonState.getTime());
	}
	
	protected void schedule(long delay, Object event, Node node)
	{
		EDSimulator.add(delay, event, node, pid);
	}
	
	
	
	
	
	// unused
	
//	/**
//	 * Creates a profile by cloning the prototype.
//	 * @param number profile number
//	 * @return a new profile instance with the specified profile number
//	 */
//	protected Profile createProfile(int number)
//	{
//		return profile.clone(number);
//	}
//	


}
