package de.uni_rostock.rtce.rebeca;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import org.eclipse.core.commands.common.EventManager;
import org.eclipse.swt.widgets.Display;

import rebeca.ContactProfile;
import rebeca.broker.BasicBroker;
import rebeca.transport.InetContactProfile;
import de.uni_rostock.rtce.debug.DebugHelper;
import de.uni_rostock.rtce.log.ChatMessage;
import de.uni_rostock.rtce.log.ErrorMessage;
import de.uni_rostock.rtce.log.Log;
import de.uni_rostock.rtce.rebeca.events.ChangeEvent;
import de.uni_rostock.rtce.rebeca.events.ChatEvent;
import de.uni_rostock.rtce.rebeca.events.GlobalChangeEvent;
import de.uni_rostock.rtce.rebeca.events.RtceEvent;
import de.uni_rostock.rtce.rebeca.events.StatusEvent;
import de.uni_rostock.rtce.rebeca.filters.RtceChatFilter;
import de.uni_rostock.rtce.rebeca.filters.RtceDocumentFilter;

/**
 * Controller for the Rebeca instance.
 * 
 * Handles connections, events, publications and subscriptions.
 * 
 * @author Georg Limbach <georf@dev.mgvmedia.com>
 * 
 */
public class RebecaController extends EventManager {

	/**
	 * Used to signal a change event. <b>Run this helper class only in the swt
	 * thread!</b>
	 */
	protected class FireChangeEvent implements Runnable {

		private final RtceEvent event;
		private final IRemoteEventListener listener;

		public FireChangeEvent(RtceEvent ev, IRemoteEventListener lis) {
			event = ev;
			listener = lis;
		}

		@Override
		public void run() {
			listener.change(event);
		}
	}

	/**
	 * Used to signal a status event. <b>Run this helper class only in the swt
	 * thread!</b>
	 * 
	 * @author georf
	 */
	protected class FireStatusEvent implements Runnable {

		private final StatusEvent event;
		private final IRemoteEventListener listener;

		public FireStatusEvent(StatusEvent ev, IRemoteEventListener lis) {
			event = ev;
			listener = lis;
		}

		@Override
		public void run() {
			listener.status(event);
		}
	}

	/**
	 * Used to signal a chat event. <b>Run this helper class only in the swt
	 * thread!</b>
	 * 
	 * @author georf
	 */
	protected class FireChatEvent implements Runnable {

		private final ChatEvent event;

		public FireChatEvent(ChatEvent e) {
			event = e;
		}

		@Override
		public void run() {
			Log.log(new ChatMessage(event.getText(), event.getUser()));
		}
	}

	/**
	 * Used to signal a error message. <b>Run this helper class only in the swt
	 * thread!</b>
	 */
	protected class ErrorMessageAction implements Runnable {

		private final int code;

		public ErrorMessageAction(final int c) {
			code = c;
		}

		@Override
		public void run() {
			Log.log(new ErrorMessage(new ConnectionException(code)));
		}
	}

	/**
	 * Storage for rebeca actions.
	 * 
	 * @author Georg Limbach <georf@dev.mgvmedia.com>
	 */
	private class RebecaAction {
		public static final String CONNECT = "connect";
		public static final String PUBLISH = "publish";
		public static final String ADD_FILTER = "add_filter";
		public static final String REMOVE_FILTER = "remove_filter";
		public static final String ADD_ADVERTISEMENT = "add_advertisement";
		public static final String REMOVE_ADVERTISEMENT = "remove_advertisement";

		private final String action;
		private final Object load;

		public RebecaAction(final String act, Object l) {
			action = act;
			
			if (l instanceof RebecaCloneable) {
				load = ((RebecaCloneable) l).clone();
			} else {
				load = l;
			}
		}

		public String getAction() {
			return action;
		}

		public Object getLoad() {
			return load;
		}
	}

	/**
	 * Class for worker thread.
	 * 
	 * Working on the action queue. If something fails it removes all following
	 * actions with the the same document id and alert the view.
	 * 
	 * @author Georg Limbach <georf@dev.mgvmedia.com>
	 * 
	 */
	protected class Worker implements Runnable {

		/**
		 * Adds a document id to filter list
		 * 
		 * @param documentId
		 *            Document id to handle
		 */
		protected void addFilter(RtceDocumentFilter f) {
			// add filter
			Log.debug("add filter: " + f.getDocumentId() + " ("
					+ f.getClass().getSimpleName() + ")");
			component.subscribe(f);
		}

		protected void addAdvertisement(RtceDocumentFilter f) {
			// add advertisement
			Log.debug("add advertisement: " + f.getDocumentId() + " ("
					+ f.getClass().getSimpleName() + ")");
			component.advertise(f);
		}

		/**
		 * Connect to an other client
		 * 
		 * Does nothing, if we already connected.
		 * 
		 * @param addr
		 *            address to connect
		 * @return false if you try to connect the local Eclipse
		 * @throws ConnectionException
		 *             if we couldn't connect
		 */
		protected void connect(InetSocketAddress addr)
				throws ConnectionException {

			Log.debug("connect to: " + addr);

			// are we connected to this machine?
			List<ContactProfile> contacts = broker.getContacts();
			for (ContactProfile contact : contacts) {
				if (contact instanceof InetContactProfile) {
					if (addr.equals(((InetContactProfile) contact)
							.getRemoteSocketAddress())) {

						// We are connected
						return;
					}
				}
			}

			// perhaps its a local host address
			if (addr.getPort() == port) {

				List<IpAddressDescriptor> locals = getAvailableIpAddresses();
				for (IpAddressDescriptor local : locals) {

					if (local.getInetAddress().equals(addr.getAddress())) {
						// Its the local eclipse
						return;
					}

				}
			}

			// Connect to the broker
			broker.connect(new InetContactProfile((InetSocketAddress) null,
					addr));

			// waiting for connection
			try {
				for (int i = 0; i < REBECA_CONNECTION_TIMEOUT; i++) {
					// Sleep 1 second and check for connection
					Thread.sleep(1000);

					contacts = broker.getContacts();
					for (ContactProfile cp : contacts) {
						if (cp instanceof InetContactProfile) {
							if (((InetContactProfile) cp)
									.getRemoteSocketAddress().equals(addr)) {
								// we are connected
								return;
							}
						}
					}
				}

				throw new ConnectionException(REBECA_CONNECTION_TIMEOUT);

			} catch (InterruptedException e) {
			}

			return;
		}

		/**
		 * Initiation of Rebeca
		 * 
		 * @throws ConnectionException
		 *             if something with the connection fails
		 */
		protected void init() throws ConnectionException {
			if (broker != null)
				return;

			if (port == -1) {
				throw new ConnectionException(
						ConnectionException.portUnavailable);
			}

			Log.debug("rebeca init: " + port);

			broker = new BasicBroker();
			broker.init(new RtceConfiguration(port));

			// close resevered port
			try {
				reservedConnection.close();
			} catch (IOException e1) {
			}

			reservedConnection = null;

			broker.startup();

			component = new RtceComponent();

			broker.plug(component);

			// waiting because Rebeca needs time
			try {
				Thread.sleep(REBECA_INITIATION_TIME);
			} catch (InterruptedException e) {
			}

			// register for chat events
			addFilter(new RtceChatFilter());
			addAdvertisement(new RtceChatFilter());
		}

		/**
		 * Publish the event
		 * 
		 * @param event
		 */
		protected void publish(RtceEvent event) {
			
			// debug output
			DebugHelper.debugEventPut(event);
			
			/*
			 * This for testing
			 */
			DebugHelper.delay(1000);
			
			/*
			 * Publish the event
			 */
			component.publish(event);
		}

		/**
		 * Removes a document id from filter list
		 * 
		 * @param documentId
		 *            Document id to remove from list
		 */
		protected void removeFilter(RtceDocumentFilter f) {
			// add filter
			Log.debug("remove doc filter: " + f.getDocumentId() + " ("
					+ f.getClass().getSimpleName() + ")");
			component.unsubscribe(f);
		}

		protected void removeAdvertisement(RtceDocumentFilter f) {
			// add advertisement
			Log.debug("remove doc advertisement: " + f.getDocumentId()
					+ " (" + f.getClass().getSimpleName() + ")");
			component.unadvertise(f);
		}

		/**
		 * Thread main method
		 */
		@Override
		public void run() {
			while (true) {
				try {

					// take next action and handle it
					RebecaAction action = actionQueue.take();

					// checking for initiation
					try {
						init();
					} catch (ConnectionException e) {
						Display.getDefault().asyncExec(
								new ErrorMessageAction(e.getErrorCode()));
					}

					// handle action
					if (action.getAction().equals(RebecaAction.PUBLISH)) {

						publish((RtceEvent) action.getLoad());

					} else if (action.getAction().equals(
							RebecaAction.ADD_FILTER)) {

						addFilter((RtceDocumentFilter) action.getLoad());

					} else if (action.getAction().equals(
							RebecaAction.REMOVE_FILTER)) {

						removeFilter((RtceDocumentFilter) action.getLoad());

					} else if (action.getAction().equals(
							RebecaAction.ADD_ADVERTISEMENT)) {

						addAdvertisement((RtceDocumentFilter) action.getLoad());

					} else if (action.getAction().equals(
							RebecaAction.REMOVE_ADVERTISEMENT)) {

						removeAdvertisement((RtceDocumentFilter) action
								.getLoad());

					} else if (action.getAction().equals(RebecaAction.CONNECT)) {

						try {
							connect((InetSocketAddress) action.getLoad());
						} catch (ConnectionException e) {
							Display.getDefault().asyncExec(
									new ErrorMessageAction(e.getErrorCode()));
						}
					}

				} catch (InterruptedException e) {
				}
			}
		}
	}

	/**
	 * Concurrent Queue of {@link RebecaAction} instances.
	 */
	protected BlockingQueue<RebecaAction> actionQueue = new LinkedBlockingDeque<RebecaAction>();

	/**
	 * Rebeca needs after initiation some time for internal events
	 * 
	 * After testing we decided for 300 milliseconds
	 */
	public static final int REBECA_INITIATION_TIME = 300;

	/**
	 * Connection timeout in seconds
	 */
	public static final int REBECA_CONNECTION_TIMEOUT = 10;

	/**
	 * Port sample count
	 */
	public static final int REBECA_PORT_SAMPLE_COUNT = 20;

	protected BasicBroker broker;
	protected RtceComponent component;

	/**
	 * We need only one instance. Use {@link #getInstance()} instead of.
	 */
	protected static RebecaController instance;

	/**
	 * Returns the current Rebeca instance
	 * 
	 * @return Rebeca instance
	 */
	public static RebecaController getInstance() {
		if (instance == null) {
			new RebecaController();
		}

		return instance;
	}

	/**
	 * Listen port
	 */
	protected int port;

	/**
	 * Reserved connection
	 */
	protected ServerSocket reservedConnection;

	/**
	 * Constructor that do some init stuff, like setting the port and starting
	 * the {@link Worker} thread.
	 */
	protected RebecaController() {
		instance = this;

		// reserve port

		// search a random available port
		for (int i = 0; i < REBECA_PORT_SAMPLE_COUNT; i++) {
			int p = (new Random()).nextInt(50000) + 2000;
			if (portAvailable(p)) {
				port = p;
				break;
			}
		}
		Worker worker = new Worker();
		new Thread(worker).start();
	}

	/**
	 * Checks whether port is available or not.
	 * 
	 * @param port
	 *            Tcp port to check
	 * @return port is available
	 */
	private boolean portAvailable(int port) {
		boolean available = true;
		try {
			reservedConnection = new ServerSocket(port);
		} catch (IOException e) {
			available = false;
		}
		return available;
	}

	/**
	 * Adds the necessary filters and advertisements for a supporter.
	 * 
	 * @param documentId
	 *            the document id
	 */
	public void addSupporterFilters(RtceDocumentFilter filter,
			RtceDocumentFilter advertisement) {
		actionQueue.add(new RebecaAction(RebecaAction.ADD_FILTER, filter));
		actionQueue.add(new RebecaAction(RebecaAction.ADD_ADVERTISEMENT,
				advertisement));
	}

	/**
	 * Adds the necessary filters and advertisements for an owner.
	 * 
	 * @param documentId
	 */
	public void addOwnerFilters(RtceDocumentFilter filter,
			RtceDocumentFilter advertisement) {
		actionQueue.add(new RebecaAction(RebecaAction.ADD_FILTER, filter));
		actionQueue.add(new RebecaAction(RebecaAction.ADD_ADVERTISEMENT,
				advertisement));
	}

	/**
	 * add a listener
	 * 
	 * @param listener
	 *            Listener to add
	 */

	public void addEventListener(IRemoteEventListener listener) {
		addListenerObject(listener);
	}

	/**
	 * Connect to a other client
	 * 
	 * Does nothing, if we already connected.
	 * 
	 * @param address
	 *            address to connect
	 * @return false if you try to connect the local Eclipse
	 */
	public void connect(InetSocketAddress address) {
		actionQueue.add(new RebecaAction(RebecaAction.CONNECT, address));

	};

	/**
	 * Fire a ChangeEvent to any listeners
	 * 
	 * @param event
	 */
	protected final void fireChangeEvent(final RtceEvent event) {
		final Object[] list = getListeners();
		for (int i = 0; i < list.length; ++i) {
			if (((IRemoteEventListener) list[i]).match(event.getDocumentId())) {
				Display.getDefault().asyncExec(
						new FireChangeEvent(event,
								(IRemoteEventListener) list[i]));
			}
		}
	}

	/**
	 * Notifies any event listeners. Only listeners registered at the time this
	 * method is called are notified.
	 * 
	 * The method only fire if the event is a valid one.
	 * 
	 * @param event
	 *            the event to fire
	 * 
	 */
	protected final void fireEvent(final RtceEvent event) {
		
		// debug output
		DebugHelper.debugEventGet(event);
		
		if (event instanceof ChatEvent) {
			// Syncronized
			Display.getDefault()
					.asyncExec(new FireChatEvent((ChatEvent) event));
		}

		else if (event instanceof StatusEvent) {
			fireStatusEvent((StatusEvent) event);
		}

		else if (event instanceof ChangeEvent) {
			fireChangeEvent(event);
		}

		else if (event instanceof GlobalChangeEvent) {
			fireChangeEvent(event);
		}
	}

	/**
	 * Fire a StatusEvent to any listeners
	 * 
	 * @param event
	 */
	protected final void fireStatusEvent(final StatusEvent event) {
		final Object[] list = getListeners();
		for (int i = 0; i < list.length; ++i) {
			if (((IRemoteEventListener) list[i]).match(event.getDocumentId())) {
				// Syncronized
				Display.getDefault().asyncExec(
						new FireStatusEvent(event,
								(IRemoteEventListener) list[i]));
			}
		}
	}

	/**
	 * Returns all available ip addresses as {@link IpAddressDescriptor} list
	 * ordered by priority
	 * 
	 * @return list of ips
	 */
	public List<IpAddressDescriptor> getAvailableIpAddresses() {
		List<IpAddressDescriptor> l = new ArrayList<IpAddressDescriptor>();

		try {
			Enumeration<NetworkInterface> networkInterfaces = NetworkInterface
					.getNetworkInterfaces();

			while (networkInterfaces.hasMoreElements()) {
				NetworkInterface networkInterface = networkInterfaces
						.nextElement();

				Enumeration<InetAddress> addresses = networkInterface
						.getInetAddresses();

				InetAddress best = null;

				while (addresses.hasMoreElements()) {
					InetAddress a = addresses.nextElement();
					if (a instanceof Inet4Address) {
						best = a;
					} else if (best == null) {
						best = a;
					}
				}

				IpAddressDescriptor current = new IpAddressDescriptor(best,
						networkInterface.getDisplayName());

				boolean inserted = false;
				for (int i = 0; i < l.size(); i++) {
					if (l.get(i).getNetworkType() < current.getNetworkType()) {
						l.add(i, current);
						inserted = true;
						break;
					}
				}
				if (!inserted)
					l.add(current);
			}
		} catch (SocketException e) {
			try {
				l.add(new IpAddressDescriptor(InetAddress.getLocalHost(),
						"loopback"));
			} catch (UnknownHostException e2) {

			}
		}
		return l;
	}

	/**
	 * Returns the port of Rebeca
	 * 
	 * 
	 * @return port
	 */
	public int getListenPort() {

		return port;
	}

	/**
	 * Publish an event by component
	 * 
	 * @param event
	 *            event to publish
	 */
	public void publish(RtceEvent event) {
		actionQueue.add(new RebecaAction(RebecaAction.PUBLISH, event));
	}

	/**
	 * Removes the filters and advertisements, that are necessary for a
	 * supporter.
	 * 
	 * @param documentId
	 */
	public void removeSupporterFilters(RtceDocumentFilter filter,
			RtceDocumentFilter advertisement) {
		actionQueue.add(new RebecaAction(RebecaAction.REMOVE_FILTER, filter));

		actionQueue.add(new RebecaAction(RebecaAction.REMOVE_ADVERTISEMENT,
				advertisement));
	}

	/**
	 * Removes the filters and advertisements, that are necessary for an owner.
	 * 
	 * @param documentId
	 */
	public void removeOwnerFilters(RtceDocumentFilter filter,
			RtceDocumentFilter advertisement) {
		actionQueue.add(new RebecaAction(RebecaAction.REMOVE_FILTER, filter));

		actionQueue.add(new RebecaAction(RebecaAction.REMOVE_ADVERTISEMENT,
				advertisement));
	}

	/**
	 * Removes a listener from list
	 * 
	 * @param listener
	 */
	public void removeRemoteEventListener(final IRemoteEventListener listener) {
		removeListenerObject(listener);
	}
}
