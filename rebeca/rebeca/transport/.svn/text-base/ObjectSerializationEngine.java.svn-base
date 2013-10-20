/*
 * $Id$
 */
package rebeca.transport;

import java.io.*;
import java.net.*;

import org.apache.log4j.Logger;

import rebeca.*;
import rebeca.broker.BasicBrokerEngine;
import rebeca.broker.EventSink;
import rebeca.broker.PluggableEventSink;

/**
 * @author parzy
 *
 */
public class ObjectSerializationEngine extends BasicBrokerEngine 
implements SerializationEngine
{
	// constants and parameters -----------------------------------------------
	// ------------------------------------------------------------------------

	public static final Class<SerializationEngine> KEY = SerializationEngine.class;
	
	/** 
	 * Logger for ProcessingEngines. 
	 */
	private static final Logger LOG = Logger.getLogger(KEY);
	
	private static final int RESET = 20; 
	
	
	// fields -----------------------------------------------------------------
	// ------------------------------------------------------------------------
	
	
	// constructors and initialization ----------------------------------------
	// ------------------------------------------------------------------------
	
	public ObjectSerializationEngine()
	{
		this(null);
	}
	
	public ObjectSerializationEngine(Broker broker)
	{
		super(broker);
	}
	
	// getters and setters ----------------------------------------------------
	// ------------------------------------------------------------------------
	
	public Object getKey()
	{
		return KEY;
	}
	
	
	// plugging ---------------------------------------------------------------
	// ------------------------------------------------------------------------
	
	public Object plug(Object obj)
	{
		if (obj instanceof EventSink)
		{
			return plug((EventSink)obj);
		}
		return obj;
	}
	
	public EventSink plug(EventSink sink)
	{
		try
		{
			if (!sink.isLocal())
			{
				if (LOG.isDebugEnabled())
				{
					LOG.debug("plugging java object serialization into sink '"
							+ sink.getSink() + "'");
				}
				return new SerializationSink(sink);
			}
		}
		catch (IOException e)
		{
			LOG.error("exception occured when plugging java object "
					+ "serialization into sink '" + sink.getSink() + "'", e);
		}
		return sink;
	}
	
	protected class SerializationSink extends PluggableEventSink
	{
		protected class InputThread extends Thread
		{
			//public boolean stop = false;
			public InputThread()
			{
				super("Input");
			}
			public InputThread(String name)
			{
				super(name);
			}
			public void run()
			{
				if (LOG.isDebugEnabled())
				{
					LOG.debug("sink '" + getSink() + "' activated: starting to "
							+ "receive events");
				}
				for (Object obj = null; !stop; )
				{
					try
					{
						// deserialize object
						obj = ois.readObject();
						
						// TODO parzy remove the block below if an EOF is always signaled by an EOFException 
						if (obj == null)
						{
							if (LOG.isInfoEnabled())
							{
								LOG.info("connection '" + getSink() + "' "
										+"closed by remote host");
							}
							finish();
							break;
						}
						
						// and process it
						if (LOG.isDebugEnabled())
						{
							LOG.debug("received event '" + obj + "'");
						}
						in((Event)obj);
					}
					catch (SocketTimeoutException e)
					{
						// nothing to worry about
					}
					catch (ClassCastException e)
					{
						LOG.error("received object '" + obj + "' is not an event",e);
					}
					catch (ClassNotFoundException e)
					{
						LOG.error("could not deserialize received event",e);
					}
					// remote host closed connection
					catch (EOFException e)
					{
						if (LOG.isInfoEnabled())
						{
							LOG.info("connection '" + getSink() + "' "
									+"closed by remote host");
						}
						finish();
						break;
					}
					// close connection whenever unexpected exceptions are thrown
					catch (IOException e)
					{
						LOG.error("exception occured while reading from sink '" + 
								getSink() + "'", e);
						finish();
						break;
					}
				}
				
				// notify main thread
				synchronized (SerializationSink.this)
				{
					wait = false;
					SerializationSink.this.notifyAll();
				}
			}
		}
		
		
		int counter = 0;
		ObjectInputStream ois = null;
		ObjectOutputStream oos = null;
		InputThread input = null;
		volatile boolean stop = false;
		volatile boolean wait = false;
		
		public SerializationSink(EventSink sink) throws IOException
		{
			super(sink);
			
			// require an input/output stream for I/O 
			if ( !(sink instanceof StreamSink) )
			{
				throw new IllegalArgumentException("Sink " + sink 
						+ " is no TransportSink.");
			}
		}
		
		
		@Override
		public void init()
		{
			// initialize underlying sinks first
			super.init();
			try
			{
				// afterwards create (de)serializing streams
				StreamSink sink = (StreamSink)out;
				this.oos = new ObjectOutputStream(sink.getOutputStream());
				this.ois = new ObjectInputStream(sink.getInputStream());
			}
			catch (IOException e)
			{
				LOG.error("input and output streams unavailable when " 
						+ "initializing serialization sink '" + getSink() + "': " + e);
			}
		}
		
		@Override
		public void activate()
		{
			// activate underlying sinks first
			super.activate();
		
			// afterwards activate input thread
			if (LOG.isDebugEnabled())
			{
				LOG.debug("strating to receive events from '" + getSink() + "'");
			}
			stop = false;
			wait = true;
			input = new InputThread("Input("+getSink()+")");
			input.start();
		}
		
		@Override
		public void passivate()
		{
			// request input thread to stop
			stop = true;
			if (input != null) input.interrupt();
			
			// wait until input thread has stopped
			synchronized (SerializationSink.this)
			{
				while (wait) try {SerializationSink.this.wait();} catch (InterruptedException e) { } 
			}

			// afterwards passivate underlying sinks
			super.passivate();
		}
		
		@Override
		public void exit()
		{
			try
			{
				if (ois != null) { ois.close(); }
				if (oos != null) { oos.close(); }
			}
			catch (IOException e)
			{
				LOG.error("exception occured when closing sink", e);
			}
			
			// finalize underlying sinks
			super.exit();
		}
		
		@Override
		public void out(Event event)
		{
			try
			{
				// resetting object event stream prevents a memory leak
				if (counter > RESET)
				{
					if (LOG.isDebugEnabled())
					{
						LOG.debug("resetting event object stream of '" 
								+ getSink() + "'");
					}
					counter = 0;
					oos.reset();
				}
				
				// serialize object and send it
				if (LOG.isDebugEnabled())
				{
					LOG.debug("serializing and sending event '" + event 
							+ "' to '" + getSink() + "'");
				}
				oos.writeObject(event);
				counter++;
			}
			catch (NotSerializableException e)
			{
				LOG.error("event '" + event + "' is not serializable",e);
			}
			// close connection whenever unexpected exceptions are thrown
			catch (IOException e)
			{
				LOG.error("exception occured when serializing and sending event '"
						+ event + "' to '" + getSink() + "'", e);
				finish();
			}
		}
		
		private void finish()
		{
			if (!stop)
			{
				stop = true;
				broker.unplug(getSink());
			}			
		}
	}
}
