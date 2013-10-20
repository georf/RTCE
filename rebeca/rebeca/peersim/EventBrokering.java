/*
 * $id$
 */
package rebeca.peersim;

import java.util.*;

import org.apache.log4j.*;

import peersim.config.*;
import peersim.core.*;
import peersim.edsim.*;

import rebeca.*;
import rebeca.broker.*;
import rebeca.component.*;
import rebeca.routing.AdvertisementEngine;
import rebeca.routing.BasicMatchingEngine;
import rebeca.routing.MatchingEngine;
import rebeca.routing.RoutingEngine;
import rebeca.routing.SimpleAdvertising;
import rebeca.routing.SimpleRouting;
import rebeca.transport.ObjectSerializationEngine;
import rebeca.transport.TcpEngine;

//import rebeca.scope.*;

/**
 * @author parzy
 *
 */
public class EventBrokering implements Broker, Control, EDProtocol 
{

	private static final Logger LOG = Logger.getLogger(EventBrokering.class);

	
	/** 
	 * Name of parameter to specify the used broker implementation.
	 * @config
	 */	
	private static final String PAR_BROKER = "broker";

	/**
	 * The name of this brokering protocol in configuration. Only neccessary
	 * when used as initializer.
	 * @config
	 */
	public static final String PAR_BROKERING = "brokering";

	
	/**
	 * The name of the underlying protocol that implements the processing of
	 * events inside a broker.
	 * @config
	 */
	public static final String PAR_PROCESSING = "processing";

	/**
	 * The name of the underlying protocol responsible for matching events.
	 * @config
	 */
	public static final String PAR_MATCHING = "matching";

	
	/**
	 * The name of the underlying protocol that implements the routing of
	 * events between brokers.
	 * @config
	 */
	public static final String PAR_ROUTING = "routing";
	
	/**
	 * The name of the underlying protocol that implements the routing of
	 * events between brokers.
	 * @config
	 */
	public static final String PAR_ADVERTISING = "advertising";
	
	
	/**
	 * The name of the serialization protocol that is used to serialize objects
	 * to messages (or message objects in simulations). 
	 * @config
	 */
	public static final String PAR_SERIALIZATION = "serialization";
	
	
	/**
	 * The name of the connection and transport protocol that is used to create 
	 * links between brokers and move messages along on them.
	 * @config
	 */
	public static final String PAR_TRANSPORT = "transport";
	
	/**
	 * The name of the connection and transport protocol that is used to create 
	 * links between brokers and move messages along on them.
	 * @config
	 */
	public static final String PAR_COMPONENTS = "components";
	
	
	public static final String PAR_MONITORING = "monitoring";
	
	public static final String PAR_SCOPING = "scoping";
	
	// fields -----------------------------------------------------------------
	// ------------------------------------------------------------------------
	
	
	/** Own node. */
	protected Node node;
	
	// TODO parzy maybe make the identifier dynamic and use a different initialization scheme
	/** Own protocol identifier. */
	protected int pid;
	
	/** peersim protocol prefix */
	protected String prefix;
	
	/** Used broker implementation. */
	protected Broker broker;

	// other pids
	protected int processing;
	protected int matching;
	protected int routing;
	protected int advertising;
	protected int serialization;
	protected int transport;
	protected int components;
	protected int monitoring;
	protected int scoping;
	protected int brokering;

	// initialization ---------------------------------------------------------
	// ------------------------------------------------------------------------
	
	/**
	 * Reads configuration parameter.
	 */
	public EventBrokering(String name)
	{
		// default initializer constructor
		if (!name.startsWith("protocol"))
		{
			pid = Configuration.getPid(name + "." + PAR_BROKERING);
			return;
		}
		
		// determine own node and protocol identifier
		prefix = name;
		node = CommonState.getNode();
		pid = CommonState.getPid();

		// create an instance of the given broker implementation
		broker = (Broker)Configuration.getInstance(prefix+"."+PAR_BROKER,
				new BasicBroker());
		
		// get pids of engine protocols
		processing = Configuration.getPid(prefix+"."+PAR_PROCESSING,-1);
		matching = Configuration.getPid(prefix+"."+PAR_MATCHING,-1);
		routing = Configuration.getPid(prefix+"."+PAR_ROUTING,-1);
		advertising = Configuration.getPid(prefix+"."+PAR_ADVERTISING,-1);
		scoping = Configuration.getPid(prefix+"."+PAR_SCOPING,-1);
		serialization = Configuration.getPid(prefix+"."+PAR_SERIALIZATION,-1);
		transport = Configuration.getPid(prefix+"."+PAR_TRANSPORT,-1);
		monitoring = Configuration.getPid(prefix+"."+PAR_MONITORING,-1);
		components = Configuration.getPid(prefix+"."+PAR_COMPONENTS,-1);
	}
	
	// EDProtocol implementation ----------------------------------------------
	// ------------------------------------------------------------------------
	@Override
	public Object clone()
	{
		try
		{
			EventBrokering clone = (EventBrokering)super.clone();
			clone.node = CommonState.getNode();
			// TODO parzy maybe support cloning for AdvertisementEngines
			clone.broker = (Broker)Configuration.getInstance(prefix+"."
					+PAR_BROKER, new BasicBroker());
			return clone;
		}
		catch (CloneNotSupportedException e)
		{
			// should never happen
			return null;
		}
	}

	@Override
	public void processEvent(Node node, int pid, Object event) { }

	
	// PeerSim Control implementation -----------------------------------------
	// ------------------------------------------------------------------------
	
	@Override
	public boolean execute()
	{
		for (int i=0; i<Network.size(); i++)
		{
			Node n = Network.get(i);
			EventBrokering b = (EventBrokering)n.getProtocol(pid);
			if (LOG.isDebugEnabled()) LOG.debug("initializing broker #"+i);
			b.init();
			if (LOG.isDebugEnabled()) LOG.debug("starting broker #"+i);
			b.startup();
		}
		if (LOG.isInfoEnabled())
		{
			LOG.info("all brokers initialized and started");
		}
		return false;
	}
	
	public void init()
	{
		broker.init(defaultConfiguration());
	}
	
	public void init(ConfigurationEngine config)
	{
		broker.init(config);
	}
	
	// Broker implementation --------------------------------------------------
	// ------------------------------------------------------------------------
	
	@Override
	public ContactProfile connect(ContactProfile profile) {	
		return broker.connect(profile); 
	}
	@Override
	public void deregisterComponent(ContactProfile profile) {
		broker.deregisterComponent(profile);
	}
	@Override
	public void deregisterComponent(Component component) {
		broker.deregisterComponent(component);
	}
	@Override
	public void deregisterEngine(BrokerEngine engine) {
		broker.deregisterEngine(engine);
	}
	@Override
	public void deregisterEngine(Object key) {
		broker.deregisterEngine(key);
	}
	@Override
	public void deregisterSink(ContactProfile profile) {
		broker.deregisterSink(profile);
	}
	@Override
	public void deregisterSink(EventSink sink) {
		broker.deregisterSink(sink);
	}
	@Override
	public void deregisterSink(EventProcessor destination) {
		broker.deregisterSink(destination);
	}
	@Override
	public Component getComponent(ContactProfile profile) {
		return broker.getComponent(profile);
	}
	@Override
	public List<Component> getComponents() {
		return broker.getComponents();
	}
	@Override
	public List<ContactProfile> getContacts() {
		return broker.getContacts();
	}
	@Override
	public EventProcessor getDestination(ContactProfile profile) {
		return broker.getDestination(profile);
	}
	@Override
	public List<EventProcessor> getDestinations() {
		return broker.getDestinations();
	}
	@Override
	public BrokerEngine getEngine(Object key) {
		return broker.getEngine(key);
	}
	@Override
	public <T> T getEngine(Class<T> clazz) {
		return broker.getEngine(clazz);
	}
	@Override
	public List<BrokerEngine> getEngines() {
		return broker.getEngines();
	}
	@Override
	public EventSink getSink(ContactProfile profile) {
		return broker.getSink(profile);
	}
	@Override
	public List<EventSink> getSinks() {
		return broker.getSinks();
	}
	@Override
	public void registerComponent(Component component) {
		broker.registerComponent(component);
	}
	@Override
	public void registerEngine(Object key, BrokerEngine engine) {
		broker.registerEngine(key, engine);
	}
	@Override
	public void registerEngine(BrokerEngine engine) {
		broker.registerEngine(engine);
	}
	@Override
	public void registerSink(EventSink sink) {
		broker.registerSink(sink);
	}
	@Override
	public Object plug(Object obj) {
		return broker.plug(obj);
	}
	@Override
	public void shutdown() {
		broker.shutdown();
	}
	@Override
	public void startup() {
		broker.startup();
	}
	@Override
	public Object unplug(Object obj) {
		return broker.unplug(obj);
	}


	// configuration ----------------------------------------------------------
	// ------------------------------------------------------------------------
	
	protected BrokerConfiguration defaultConfiguration()
	{
		return new BrokerConfiguration(this);
	}
	
	public class BrokerConfiguration extends ExtendableConfigurationEngine
	{
		// delegate object creation
		public BrokerConfiguration() { super(); }
		public BrokerConfiguration(Broker broker) { super(broker); }
			
		public Object configure(Broker broker)
		{
			LOG.debug("configuration called "+advertising);
			
			// plugging in all specified engines 
			if (processing != -1) broker.plug( node.getProtocol(processing) );
			if (matching != -1) broker.plug( node.getProtocol(matching) );
			if (routing != -1) broker.plug( node.getProtocol(routing) );
			if (advertising != -1) broker.plug( node.getProtocol(advertising) );
			if (scoping != -1) broker.plug( node.getProtocol(scoping) );
			if (serialization != -1) broker.plug( node.getProtocol(serialization) );
			if (transport != -1) broker.plug( node.getProtocol(transport) );
			if (monitoring != -1) broker.plug( node.getProtocol(monitoring) );
			if (components != -1) broker.plug( node.getProtocol(components) );
			
			return broker;
		}
	}
}
