package rebeca.scope;

import java.io.*;
import java.util.*;

import rebeca.Event;
import rebeca.Filter;
import rebeca.filter.*;
import rebeca.namevalue.*;

public interface Scope extends Comparable<Scope>, Serializable 
{
	public static final char SCOPE_SEPARATOR = '.'; 
	
	public String getName();
	
	public Filter getComponentSelector();
	
	public Filter getInFilter();
	public Filter getOutFilter();

	// TODO parzy check usage of mappings 
	public Mapping getInMapping();
	public Mapping getOutMapping();
	
//	public AttributeValuePair[] getAttributes();
	public AttributeSet getAttributes();
	
//	public AttributeValuePair[] getTransformations();
	
	/**
	 * @deprecated
	 */
	public String getSuperscope();
	
	//public String[] getImports();
	
	
	/// newly added
	public boolean match(AttributeSet s);
	
	
	
}
