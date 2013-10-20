/*
 * $Id$
 */
package rebeca.filter;

import org.apache.log4j.*;
import rebeca.*;
import rebeca.namevalue.*;

/**
 * @author parzy
 *
 */
public class BasicAttributeFilter extends BasicFilter implements AttributeFilter 
{
	// serial version UID
	private static final long serialVersionUID = 6683219672481628914L;

	/** 
	 * Basic logger for attribute filters. 
	 */
	private static final Logger LOG = Logger.getLogger(BasicAttributeFilter.class);
	
	protected String attribute;
	
	// TODO parzy add validation of args for each constructor
	
	public BasicAttributeFilter(String attribute)
	{
		this.attribute = attribute;
	}
	
	public BasicAttributeFilter(String... attributes)
	{
		// encode a multi-attribute by separating each attribute by "|"
		String ma = "";
		for (String a : attributes)
		{
			ma = ma + a + "|";
		}
		this.attribute = ma.length() > 0 ? ma.substring(0, ma.length()-1) : ma;
	}
	
	public String getAttribute() 
	{
		return attribute;
	}

	public boolean hasAttribute(String attribute)
	{
		if (isMultiAttributeFilter() || isMultiAttribute(attribute))
		{
			return hasCommonAttribute(attribute);
		}
		return this.attribute.equals(attribute);
	}
	
	private Boolean multi = false;
	private boolean isMultiAttributeFilter()
	{
		if (multi == null)
		{
			multi = isMultiAttribute(attribute);
		}
		return multi.booleanValue();
	}
	
	private boolean isMultiAttribute(String attribute)
	{
		return attribute.indexOf('|') < 0;
	}
	
	private String[] attributes;
	private boolean hasCommonAttribute(String attribute)
	{
		if (this.attributes == null)
		{
			this.attributes = this.attribute.split("\\|");
		}
	
		String[] attributes = attribute.split("\\|");
		for (String s : this.attributes)
		{
			for (String t : attributes)
			{
				if (s.equals(t))
				{
					return true;
				}
			}
		}
		return false;
	}
	
	
	public boolean match(Event e)
	{
		if (! (e instanceof AttributeSet) )
		{
			return false;
		}
		
		AttributeSet s = (AttributeSet)e;
		
		if (isMultiAttributeFilter())
		{
			Object[] values = getValues(s);
			return values == null ? false : match(values);
		}
		
		if (!s.containsAttribute(attribute))
		{
			return false;
		}
		return match(s.get(attribute));
	}
	
	private Object[] getValues(AttributeSet s)
	{
		if (attributes == null)
		{
			attributes = attribute.split("\\|");
		}
		
		Object[] values = new Value[attributes.length];
		for (int i=0; i<attributes.length; i++)
		{
			if (!s.containsAttribute(attributes[i]))
			{
				return null;
			}
			values[i] = s.get(attributes[i]);
		}
		return values;
	}
	
	protected boolean match(Object value)
	{
		return true;
	}
	
	protected boolean match(Object... values)
	{
		return true;
	}

	// and/or integration -----------------------------------------------------
	// ------------------------------------------------------------------------
	
	public Filter doAnd(BasicAttributeFilter f)
	{
		return new AndFilter(this,f);
	}
	
	public Filter doOr(BasicAttributeFilter f)
	{
		return new OrFilter(new AndFilter(this),new AndFilter(f));
	}
	
}
