/*
 * $Id$
 */
package rebeca.component;

import rebeca.Component;
import rebeca.ContactProfile;
import rebeca.transport.TransportEngine;

/**
 * @author parzy
 *
 */
public class ComponentContactProfile implements ContactProfile 
{
	protected Component component;
	
	public ComponentContactProfile(Component component)
	{
		this.component = component;
	}
	
	@Override
	public Object getEngineKey()
	{
		return ComponentEngine.class;
	}
	
	public Component getComponent()
	{
		return component;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof ComponentContactProfile))
		{
			return false;
		}
		
		ComponentContactProfile c = (ComponentContactProfile)obj;
		return component!=null ? component.equals(c.component) : c.component==null;
	}
	
	@Override
	public int hashCode()
	{
		return component != null ? component.hashCode() : 4711;
	}
}
