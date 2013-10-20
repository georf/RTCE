/*
 * $id$
 */
package rebeca.broker;

import rebeca.*;

/**
 * @author parzy
 *
 */
public class PluggableEventSink implements EventSink 
{

	protected EventSink in;
	protected EventSink out;
	
	public PluggableEventSink()
	{
		this(null,null);
	}
	
	public PluggableEventSink(EventSink out)
	{
		this(out,null);
	}
	
	public PluggableEventSink(EventSink out, EventSink in)
	{
		this.out = out;
		this.in = in;
		
		if (out != null)
		{
			out.setInputSink(this);
		}
		
		if (in != null)
		{
			in.setOutputSink(this);
		}
	}
	
	/* (non-Javadoc)
	 * @see rebeca.scope.EventSink#getEndPoint()
	 */
	@Override
	public EventSink getSink() 
	{
		if (out != null)
		{
			return out.getSink();
		}
		return this;
	}

	/* (non-Javadoc)
	 * @see rebeca.scope.EventSink#getProcessor()
	 */
	@Override
	public EventProcessor getProcessor() 
	{
		if (in != null)
		{
			return in.getProcessor();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see rebeca.scope.EventSink#in(rebeca.Event)
	 */
	@Override
	public void in(Event event) 
	{
		if (in != null)
		{
			in.in(event);
		}
	}

	/* (non-Javadoc)
	 * @see rebeca.scope.EventSink#out(rebeca.Event)
	 */
	@Override
	public void out(Event event) 
	{
		if (out != null)
		{
			out.out(event);
		}
	}

	/* (non-Javadoc)
	 * @see rebeca.scope.EventSink#setInputSink(rebeca.scope.EventSink)
	 */
	@Override
	public void setInputSink(EventSink sink) 
	{
		this.in = sink;
		// TODO parzy connect the sinks
	}

	/* (non-Javadoc)
	 * @see rebeca.scope.EventSink#setOutputSing(rebeca.scope.EventSink)
	 */
	@Override
	public void setOutputSink(EventSink sink) 
	{
		this.out = sink;
		// TODO parzy maybe connect the sinks
	}
	
	
	// TODO parzy maybe we find a better solution here
	@Override
	public boolean equals(Object obj)
	{
		// TODO parzy maybe use out instead?
		if (in != null)
		{
			return in.equals(obj);
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode()
	{
		// TODO parzy maybe use out instead?
		if (in != null)
		{
			return in.hashCode();
		}
		return super.hashCode();
	}
	
	@Override
	public void activate()
	{
		if (out != null)
		{
			out.activate();
		}
	}
	
	@Override
	public void passivate()
	{
		if (out != null)
		{
			out.passivate();
		}
	}
	
	@Override
	public void init()
	{
		if (out != null)
		{
			out.init();
		}
	}
	
	@Override
	public void exit()
	{
		if (out != null)
		{
			out.exit();
		}
	}
	
	@Override
	public boolean isLocal()
	{
		if (out != null)
		{
			return out.isLocal();
		}
		return false;	
	}
	
	@Override 
	public ContactProfile getContactProfile()
	{
		if (out != null)
		{
			return out.getContactProfile();
		}
		return null;
	}
	
	@Override
	public String toString()
	{
		return out!=null ? out.toString() : super.toString();
	}
}
