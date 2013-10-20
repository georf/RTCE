/*
 * $Id: Event.java 2086 2010-10-20 17:34:30Z parzy $
 * 
 *  This file is part of the REBECA System.
 *  See http://www.gkec.informatik.tu-darmstadt.de/rebeca (broken)
 *
 *  Authors: Gero   Muehl <gmuehl@cs.tu-berlin.de>
 *           Ludger Fiege <fiege@gkec.tu-darmstadt.de>
 *
 *  Copyright (C) 2000-2001
 *
 *  You can redistribute it and/or modify this software under the
 *  terms of the GNU General Public License as published by the Free
 *  Software Foundation; either version 2, or (at your option) any
 *  later version.
 */


package rebeca;

import java.io.*;
import org.apache.log4j.*;
import rebeca.scope.*;
import rebeca.util.*;

/** 
 * The base class for all events. 
 */
public class Event implements Cloneable, Serializable 
{
	// constants --------------------------------------------------------------
	// ------------------------------------------------------------------------

    public static final int DELETE = 2;

    public static final int MANIPULATOR_DELETE = 8;
    public static final int MANIPULATOR_UPDATE = 4;
    public static final int UPDATE = 1;

	private static final long serialVersionUID=-7626857970398187287L;
	private static final Logger LOG = Logger.getLogger(Event.class);

	
	// fields -----------------------------------------------------------------
	// ------------------------------------------------------------------------
	
	protected Id id;
    protected ScopeSet scopes;
    
    public Event()
    {
    	this.id = new RandomId();
    }
    
    public Event(ScopeSet scopes)
    {	
    	this();
    	this.scopes = new BasicScopeSet(scopes);
    }
    
    public ScopeSet getScopes()
    {
    	return scopes;
    }
    
    public void setScopes(ScopeSet scopes)
    {
    	this.scopes = scopes;
    }
    
    public Id getId()
    {
    	return id;
    }
    
    @Override
    public Object clone()
    {
    	try
    	{
    		return super.clone();
    	}
    	// should never happen
    	catch (CloneNotSupportedException e) 
    	{  
    		return null;
    	}
    }
    
    
    
    // old stuff 
    
    // Default is update
    protected int type = UPDATE;
    

    /**
     * Get the type of the event.  This is not a Java type.  The types
     * MANIPULATOR, ... are defined via integer constants.
     */
    public int getType() {
        return type;
    }

    /**
     * Is the event of same type or subtype as the specified class?
     * This is a convenience method to determine the actual type of an
     * <code>Event</code>.
     * 
     * @return Returns <code>true</code> if the event can be casted
     * to the specified class, returns <code>false</code> otherwise.
     */
    public boolean isA(Class<? extends Event> cl) {
        return cl.isInstance(this);
    }

    public boolean isManipulator() {
        return (type == MANIPULATOR_UPDATE || type == MANIPULATOR_DELETE);
    }

    public boolean isNormal() {
        return (type == UPDATE || type == DELETE);
    }

    /**
     * Is the event of same type as the specified class?  This is a
     * convenience method to determine the actual type of an
     * <code>Event</code>.
     * 
     * @return Returns <code>true</code> if the event can be casted
     * to the specified class, returns <code>false</code> otherwise.
     */
    public boolean isOfType(Class<? extends Event> c) {
        return this.getClass().equals(c);
    }

    /** Dummy method! */
    public void print() {
        if(LOG.isInfoEnabled())
            LOG.info(toString());
    }

    /**
     * Set the type of the event.  This is not a Java type.  The types
     * MANIPULATOR, ... are defined via integer constants.
     */
    public void setType(int t) {
        type = t;
    }
}
