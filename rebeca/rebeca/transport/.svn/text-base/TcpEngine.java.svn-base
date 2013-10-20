/*
 * $Id$
 */
package rebeca.transport;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.ListIterator;

import org.apache.log4j.Logger;

import rebeca.*;
import rebeca.broker.BasicBrokerEngine;
import rebeca.broker.EventSink;
import rebeca.broker.PluggableEventSink;

/**
 * @author parzy
 *
 */
public class TcpEngine extends BasicBrokerEngine implements TransportEngine
{

	// Constants --------------------------------------------------------------
	// ------------------------------------------------------------------------
	
	public static final Class<TransportEngine> KEY = TransportEngine.class;
	
	/** 
	 * Logger for transport engines. 
	 */
	private static final Logger LOG = Logger.getLogger(KEY);

	
	public static final int SERVER_PORT = 4711;
	public static final int TIMEOUT = 3000;
	
	
	// Fields -----------------------------------------------------------------
	// ------------------------------------------------------------------------
	
	protected Thread server;
	protected int port;
	
	/**
	 * Boolean to request stopping of server thread.
	 */
	protected volatile boolean stop;
	
	/**
	 * Boolean used by server thread to signal that it has finished.
	 */
	protected volatile boolean wait;
	
	protected class ServerThread implements Runnable
	{
		ServerSocket socket;
		
		public void run()
		{
			try
			{
				socket = new ServerSocket(port);
				socket.setSoTimeout(TIMEOUT);

				while (!stop)
				{
					try
					{
						// accept incomming connections
						Socket s = socket.accept();
						s.setSoTimeout(TIMEOUT);
						if (LOG.isInfoEnabled())
						{
							LOG.info("accepting new connection from '" 
									+ s.getRemoteSocketAddress() + "'");
						}
						// create a connection sink and plug it in
						TransportSink sink = new TransportSink(s);
						broker.plug(sink);
					}
					catch (SocketTimeoutException e) { /* nothing to worry */}
				}
				// free resources
				socket.close();
			}
			catch (IOException e)
			{	
				LOG.error("can not accept new connection(s)", e);
			}
			
			// notify main thread
			synchronized (server)
			{
				wait = false;
				server.notifyAll();
			}
		}
	}
	
	
	
	// constructors and initialization ----------------------------------------
	// ------------------------------------------------------------------------
	
	public TcpEngine()
	{
		this(null,SERVER_PORT);
	}
	
	public TcpEngine(Broker broker)
	{
		this(broker,SERVER_PORT);
	}
	
	public TcpEngine(int port)
	{
		this(null,port);
	}
	
	public TcpEngine(Broker broker, int port)
	{
		super(broker);
		this.port = port;
		this.server = port != -1 ? new Thread(new ServerThread(),"TcpEngine-Listen") : null;
	}
	
	// activation and passivation ---------------------------------------------
	// ------------------------------------------------------------------------
	
	@Override
	public void activate()
	{
		if (server != null)
		{
			stop = false;
			wait = true;
			server.start();
		}
	}
	
	@Override
	public void passivate()
	{
		// first, finish server thread accepting new connections
		if (server != null)
		{
			stop = true;
			server.interrupt();
			
			// wait until server thread has finished cleanly
			synchronized (server)
			{
				while (wait)
				{
					try { server.wait(); } catch (InterruptedException e) { }
				}
			}
		}
		
		// second, close all open connections in reverse order
		List<EventSink> sinks = broker.getSinks();
		ListIterator<EventSink> lit = sinks.listIterator(sinks.size());
		while (lit.hasPrevious())
		{
			EventSink sink = lit.previous();
			ContactProfile profile = sink.getContactProfile();
			broker.unplug(sink);
			// TODO parzy replace the busy waiting 
			// wait until all sink is deregistered
			while (broker.getSink(profile)!=null) { Thread.yield(); };
		}
	}
	
	
	// setters and getters ----------------------------------------------------
	// ------------------------------------------------------------------------
	
	@Override
	public Object getKey()
	{
		return KEY;
	}
	
	public int getServerPort()
	{
		return port;
	}
	
	public void setServerPort(int port)
	{
		this.port = port;
		
		if (server == null && port != -1)
		{
			server = new Thread(new ServerThread());
		}
	}
	
	// 
	// 
	
	@Override
	public void process(Event event, EventProcessor source) {}
	
	public ContactProfile connect(ContactProfile profile)
	{
		if (profile instanceof InetContactProfile)
		{
			return connect((InetContactProfile)profile);
		}
		LOG.error("unknown contact profile '" + profile 
				+ "': no connection established");
		return null;
	}
	
	public ContactProfile connect(InetContactProfile profile)
	{
		try
		{
			// create, configure, and connect a new socket
			Socket s = new Socket();
			s.setSoTimeout(TIMEOUT);
			s.connect(profile.getRemoteSocketAddress());
		
			// create an appropriate event sink and register it
			EventSink sink = new TransportSink(s);
			broker.plug(sink);
			return sink.getContactProfile();
		}
		catch(IOException e)
		{
			LOG.error("could not connect to '" + profile + "'", e);
			return null;
		}
	}
	
	
	
	
	
	protected class TransportSink extends PluggableEventSink 
	implements StreamSink
	{
		protected Socket socket;
		protected InetContactProfile profile;
		
		public TransportSink(Socket socket)
		{
			this.socket = socket;
			this.profile = new InetContactProfile(
					(InetSocketAddress)socket.getLocalSocketAddress(),
						(InetSocketAddress)socket.getRemoteSocketAddress() );
		}
		
		public InputStream getInputStream() throws IOException
		{
			return socket.getInputStream();
		}
		
		public OutputStream getOutputStream() throws IOException
		{
			return socket.getOutputStream();
		}
		
		@Override
		public void exit()
		{
			try
			{
				LOG.info("closing connection to '" + profile.getRemoteSocketAddress() + "'");
				socket.close();
			}
			catch (IOException e)
			{
				LOG.error("exception occured when closing socket to '" + profile.getRemoteSocketAddress() + "'");
			}
		}
		
		@Override
		public ContactProfile getContactProfile()
		{
			return profile;
		}
		
		@Override
		public String toString()
		{
			return profile.toString();
		}
	}
}
