/*
 * $Id: NameValuePair.java 96 2009-02-02 19:37:35Z parzy $
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


package rebeca.namevalue;

import java.util.*;

public class NameValuePair implements AttributeValuePair{

    private String name;
    private Object value;

    public NameValuePair() 
    {
    	this(null,null);
    }

    public NameValuePair(String name, Object value) 
    {
        this.name = name;
        this.value = value;
    }
    
    public String getAttribute()
    {
    	return name;
    }

    public String getKey()
    {
    	return name;
    }

    public String getName() 
    {
        return name;
    }

    public Object getValue() 
    {
        return value;
    }
    
    public Object setValue(Object value)
    {
    	Object rst = this.value;
    	this.value = value;
    	return rst;
    }
    
    public boolean equals(Object o)
    {
    	 if (o instanceof Map.Entry)
    	 {
    		 Map.Entry<?,?> e = (Map.Entry<?,?>)o;
    		 Object k1 = getKey();
    		 Object k2 = e.getKey();
    		 if (  k1 == k2 || 
    			  (k1 != null && k1.equals(k2)) )
    		 {
    			 Object v1 = getValue();
    			 Object v2 = e.getValue();
    			 return  v1 == v2 || 
    			   	    (v1 != null && v1.equals(v2));
    		 }
         }
         return false;
    }
    
    public int hashCode()
    {
    	return (  getKey()==null ? 0 : getKey().hashCode()  ) ^
        	   (getValue()==null ? 0 : getValue().hashCode());
    }
}
