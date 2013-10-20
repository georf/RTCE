package rebeca.event;

import rebeca.*;
import rebeca.scope.*;

public class Advertisement extends Subscription 
{
	private static final long serialVersionUID = 20080923072721L;
	
	protected Advertisement()
	{
		super();
	}
	
	Advertisement(Filter f)
	{
		super(f);
	}
	
	Advertisement(Filter f, ScopeSet s)
	{
		super(f,s);
	}
	
	@Override
	public String toString()
	{
		return "Advertisement(" + filter + ")@" + id;
	}
}
