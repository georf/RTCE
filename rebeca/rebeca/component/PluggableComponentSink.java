/*
 * $Id$
 */
package rebeca.component;

import rebeca.*;
import rebeca.broker.PluggableEventSink;

/**
 * @author parzy
 *
 */
public class PluggableComponentSink extends PluggableEventSink 
implements ComponentSink
{
	// TODO parzy finish implementing the whole class
	public PluggableComponentSink()
	{
		this(null,null);
	}
	
	public PluggableComponentSink(ComponentSink in)
	{
		this(in,null);
	}
	
	public PluggableComponentSink(ComponentSink in, ComponentSink out)
	{
		this.in = in;
		this.out = out;
		
		if (in != null)
		{
			in.setOutputSink(this);
		}
		
		if (out != null)
		{
			out.setInputSink(this);
		}
	}
	
	
	@Override
	public Component getComponent() 
	{
		if (in instanceof PluggableComponentSink)
		{
			return ((PluggableComponentSink)in).getComponent();
		}
		return null;
	}

	private <T> boolean isA(Class<T> clazz)
	{
		return clazz != null && clazz.isAssignableFrom(this.getClass());
	}
	
	@Override @SuppressWarnings("unchecked")
	public <T> T getComponentInterface(Class<T> clazz)
	{
		if (this.isA(clazz))
		{
			return (T)this;
		}
		
		if (in instanceof PluggableComponentSink)
		{
			return ((PluggableComponentSink)in).getComponentInterface(clazz);
		}
		return null;
	}

	@Override @SuppressWarnings("unchecked")
	public <T> T getBrokerInterface(Class<T> clazz)
	{
		if (this.isA(clazz))
		{
			return (T)this;
		}

		if (out instanceof PluggableComponentSink)
		{
			return ((PluggableComponentSink)out).getBrokerInterface(clazz);
		}
		return null;
	}

	@Override @SuppressWarnings("unchecked")
	public <T> T getInterface(Class<T> clazz) 
	{
		if (this.isA(clazz))
		{
			return (T)this;
		}

		T i = null;
		if (in instanceof PluggableComponentSink)
		{
			i = ((PluggableComponentSink)in).getComponentInterface(clazz);
			if (i != null)
			{
				return i;
			}
		}
		if (out instanceof PluggableComponentSink)
		{
			i = ((PluggableComponentSink)out).getBrokerInterface(clazz);
			if (i != null)
			{
				return i;
			}
		}
		return null;
	}
	
	// EventSink implementation -----------------------------------------------
	// ------------------------------------------------------------------------
	
	@Override
	public EventProcessor getProcessor()
	{
		if (out != null)
		{
			return out.getProcessor();
		}
		return null;
	}
	
	@Override
	public boolean isLocal()
	{
		if (in != null)
		{
			return in.isLocal();
		}
		return true;
	}
	
	@Override 
	public void init()
	{
		if (in != null)
		{
			in.init();
		}
	}
	
	@Override
	public void exit()
	{
		if (in != null)
		{
			in.exit();
		}
	}
	
	@Override 
	public void activate()
	{
		if (in != null)
		{
			in.activate();
		}
	}
	
	@Override 
	public void passivate()
	{
		if (in != null)
		{
			in.passivate();
		}
	}
	
	@Override
	public ContactProfile getContactProfile()
	{
		return in!=null ? in.getContactProfile() : null;
	}
	
	@Override
	public String toString()
	{
		return in!=null ? in.toString() : 
			   getClass().getName() + "@" + Integer.toHexString(hashCode());
	}
}
