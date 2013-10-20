/*
 * $id$
 */
package rebeca.config;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import org.apache.log4j.*;

/**
 * @author parzy
 * This class represents a Rebeca configuration.
 */
// TODO parzy this class is far from being complete.
public class PropertyConfiguration implements Configuration
{
	/** 
	 * Logger for Configuration class. 
	 */
	private static final Logger LOG = Logger.getLogger(PropertyConfiguration.class);

	public static final String PAR_PROFILE = "PROFILE";
	
	public static final String PAR_CLASS = "CLASS.";
	
	public static final String PAR_OBJECT = "OBJECT.";

	
	// TODO parzy add keywords
	public static final Set<String> GLOBAL_KEYWORDS = Collections
			.unmodifiableSet( new HashSet<String>( Arrays.asList( new String[]{
			PAR_PROFILE, //
			PAR_CLASS,
			PAR_OBJECT,
			})));
	
	public static final Set<String> POSITIVE_KEYWORDS = Collections
			.unmodifiableSet( new HashSet<String>( Arrays.asList( new String[]{
			"true", "yes", "on", "set", "1"
			})));
	
	public static final Set<String> NEGATIVE_KEYWORDS = Collections
			.unmodifiableSet( new HashSet<String>( Arrays.asList( new String[]{
			"false", "no", "off", "unset", "0"
			})));
	
	protected String basename;
	
	protected String extension;
	
	protected String[] profiles;
	
	protected Properties props;
	
	protected HashMap<String,Configurable> instanceCache;
	
	protected HashSet<String> profileCache;
	
	protected enum NumberType {INT, LONG, DOUBLE}; 
	
	
	
	// TODO parzy do not know whether this is really necessary
	private PropertyConfiguration() { /* prevents instantiation */ }

	// TODO parzy mhh, a configuration can include several profiles...
	public int getProfileId()
	{
		return 0;
	}
	
	
	
	
	// TODO parzy still to implement must it be a Configurable as return value?
	public Configurable getInstance(String key)
	{
		String name = getString(key);
		return name == null ? null : loadInstance(name);
	
	}
	
	public Configurable getInstance(String key, Configurable defaultValue)
	{
		Configurable inst = getInstance(key);
		return inst == null ? defaultValue : inst;
	}
	
	private Configurable loadInstance(String name)
	{
		int i = name.indexOf('_');
		String identifier = i < 0 ? name : name.substring(0, i);
		String profile = i < 0 ? "" : name.substring(i+1,name.length());
		return loadInstance(identifier, profile);
	}
	
	private Configurable loadInstance(String name, String profile)
	{
		Configurable inst = instanceCache.get( profile.length()==0 ? name : 
											   name + "_" + profile );
		if (inst != null)
		{
			return inst;
		}

		Object obj = getObject(PAR_OBJECT + name);
		if (obj == null)
		{
			return null;
		}
		
		if ( !(obj instanceof Configurable) )
		{
			LOG.error("Could not configure object '" + name + "'. " 
					+ obj.getClass().getName() + " does not implement the "
					+ "Configurable interface.");
			return null;
		}

		inst = (Configurable) obj;
		
		instanceCache.put(profile.length()==0 ? name : name+"_"+profile, inst );
		View config = new View(this, name, profile);
		inst.configure(config);
		return inst;
	}
		
	
	public PropertyConfiguration(String baseName, String... profile) 
	{
		
	}
	

	
	public void load()
	{
		// load base configuration 
		loadProfile("");
		
		// load activated and dependent profiles
		setProfiles();
		for (String p : profiles)
		{
			for (String q : unfoldProfile(p,true))
			{
				loadProfile(q);
			}
		}
	}
	
	public void loadProfile(String profile)
	{
		// do not read profiles twice
		if (profileCache.contains(profile))
		{
			return;
		}
		
		// read configuration file and handle exceptions
		Properties p = new Properties();
		String filename = getFileName(basename,profile);
		try
		{
			p.load(new FileReader(filename));
		}
		// has a separate configuration file been provided?  
		catch (FileNotFoundException e)
		{
			// only an info as a configuration file per profile is optional
			if (LOG.isInfoEnabled()) 
			{ 
				LOG.info("Could not find configuration '" + filename + "' for "
						+ "profile '" + profile + "'.", e);
			}
			return;
		}
		// I/O error occurred while reading the configuration
		catch (IOException e)
		{
			LOG.error("Could not read configuration '" + filename + "' for "
					+ "profile '" + profile +"'.", e);
			return;
		}
		// parse error occurred while reading the configuration
		catch (IllegalArgumentException e)
		{
			LOG.error("Could not parse configuration '" + filename + "' for "
					+ "profile '" + profile +"'.", e);
			return;
		}
		
		// iterate through all attributes read, enforce a correct profile label,
		// and add them to the configuration's properties 
		for (Map.Entry<Object,Object> e : p.entrySet() )
		{
			String key = (String)e.getKey();
			String value = (String)e.getValue();

			// handle property
			// determine attribute and profile extension
			int i = key.indexOf('_');
			String attribute = i<0 ? key : key.substring(0,i);
			String suffix = i<0 ? profile : key.substring(i+1);

			// check for global keywords which have no associated profile
			boolean global = false;
			for (String s : GLOBAL_KEYWORDS)
			{
				if (attribute.startsWith(s))
				{
					global = true;
					break;
				}
			}
			
			// validate profile extension for global keywords
			if (global && suffix.length() != 0)
			{
				LOG.warn("Profile extension for global attribute '" + attribute 
						+ "' in '" + filename + "found and ignored.");
			}
			
			// ensure a correct profile extension for non global keywords
			if (!global && !suffix.startsWith(profile))
			{
				// warn if attribute definition gets strange
				if(profile.startsWith(suffix))
				{
					LOG.warn("Suspicious attribute definition in '" + filename 
							+ "': attribute '" + key + "' is only valid for "
							+ "profile '" + profile + "_" + suffix + "'.");
				}
				// prepend correct profile 
				suffix = suffix.length()==0 ? profile : profile + "_" + suffix;
			}
			// construct a correctly labeled property 
			String property = global ? attribute : attribute + "_" + suffix;

			// handle property value
			// replace environment variables by their values
			value = replaceEnvironmentVariables(value);

			// insert property and value into configuration
			if (!props.containsKey(property))
			{
				props.put(property, value);
			}
			else
			{
				if (global)
				{
					LOG.warn("Duplicate definition for global attribute '" 
							+ property + "' in '" + filename + "' found and "
							+ "ignored.");
				}
				else
				{
					props.put(property, value);
					LOG.warn("Attribute '" + attribute + "' in '" + filename
							+ "' overrides definition of a parent profile.");		
				}
			}
		}
	}
	
	private static String getFileName(String configName, String profile)
	{
		if (profile.length()==0)
		{
			return configName;
		}
		
		int i = configName.lastIndexOf('.');
		String baseName = i<0? configName : configName.substring(0,i);
		String extension = i<0? "" : configName.substring(i);
		
		return baseName+profile+extension;
	}
	
	private static String replaceEnvironmentVariables(String s)
	{
		// check whether s contains references to environment variables
		if (s.indexOf('$')<0)		
		{
			return s;
		}
		
		// ensure correct termination by adding a whitespace
		s = s + ' '; 
		// replace environment variables by their values
		for (int i=-1, j=0; j < s.length(); j++)
		{
			switch (s.charAt(j))
			{
			// ignore escaped characters
			case '\\':
				j++;
				break;
			// start of new environment variable, end of old one
			case '$':
				if (i==-1)
				{
					i = j;
					break;
				}
			// end of environment variable
			case ',':
			case ';':
			case ' ':
			case '\t':
				if (i==-1)
				{
					break;
				}
				String start = s.substring(0,i);
				String var = s.substring(i+1,j);
				String end = s.substring(j); 
				String env = System.getenv(var);
				if (env == null)
				{
					env = "";
					LOG.warn("Referenced environment variable '" + var 
							+ "' is undefined on system.");
					}
				s = start + env + end;
				j = i + env.length()-1;
				i = -1;
			}
		}
		// remove added or inserted whitespace characters
		return s.trim();		
	}
	
		
	private static String[] unfoldProfile(String profile)
	{
		return unfoldProfile(profile,false);
	}
	
	/**
	 * Returns a list of fallback profiles.
	 * @param profile
	 * @param reverse
	 * @return
	 */
	private static String[] unfoldProfile(String profile, boolean reverse)
	{
		List<String> fallbacks = new LinkedList<String>();
		
		for (; profile != null; profile = getParentProfile(profile) )
		{
			if (!reverse)
			{
				fallbacks.add(profile);
			}
			else
			{
				fallbacks.add(0, profile);
			}
		}
		
		String[] profiles = new String[fallbacks.size()];
		return fallbacks.toArray(profiles);
	}
	
	private static String getParentProfile(String profile)
	{
		if (profile.length()==0)
		{
			return null;
		}
		
		int i = profile.lastIndexOf('_');
		return profile.substring(0, i<0?0:i);
	}
	
	private void setProfiles()
	{
		// do not override an existing profile setting 
		if (profiles != null)
		{
			return;
		}
		
		String[] a = getStringArray(PAR_PROFILE);
		if (a == null)
		{
			profiles = new String[]{"DEFAULT"};
			return;
		}
		profiles = a;
	}
	
	public void apply()
	{
		// find all classes and their identifiers (prefix) before any 
		// configuration starts as additional profiles may be loaded on 
		// demand later which may contain different class definitions
		List<String> keys = new ArrayList<String>();
		List<String> names = new ArrayList<String>();
		for (Map.Entry<Object,Object> e : props.entrySet())
		{
			String key = (String)e.getKey();    
			String value = (String)e.getValue();
			// triggered by class-keyword
			if (key.startsWith(PAR_CLASS + "."))
			{
				keys.add(key.substring(PAR_CLASS.length()+1));
				names.add(value);
			}
		}
		
		// now, configure all found classes
		for (int i = 0; i < keys.size(); i++)
		{
			String key = keys.get(i);   // class identifier in configuration
			String name = names.get(i); // java full qualified class name
			
			// get the java class
			Class<?> clazz;
			try
			{
				clazz = Class.forName(name);
			}
			catch (Exception e)
			{
				LOG.error("Could not load class '" + name + "' for prefix '"
						+ key + "'.", e);
				continue;
			}
			
			// get the configuration method
			Method method;
			try
			{
				method = clazz.getMethod(Config.METHODNAME,Configuration.class);
			}
			catch (Exception e)
			{
				LOG.error("Could not find configuration method '" 
						+ Config.METHODNAME + "(Configuration)' in class '" 
						+ name + "'.", e);
				continue;
			}
			
			// invoke configuration method for every activated profile
			Configuration config = forEntity(key);
			for (int j = 0; j < profiles.length; j++)
			{
				try
				{
					method.invoke(null, config.forProfile(profiles[j]));
				}
				catch (Exception e)
				{
					LOG.error("Could not successfully configure class '" + name
							+ "' for profile '" + profiles[j] + "'.", e);
				}
			}
		}
	}
	
	
	
	// Configuration implementation -------------------------------------------
	// ------------------------------------------------------------------------
	
	public String getString(String key)
	{
		return props.getProperty(key);
	}

	public String getString(String key, String defaultValue)
	{
		return props.getProperty(key, defaultValue);
	}
	
	public String[] getStringArray(String key)
	{
		String line = getString(key);
		return line == null ? null : splitString(line);
	}
	
	public String[] getStringArray(String key, String[] defaultValue)
	{
		String[] a = getStringArray(key);
		return a == null ? defaultValue : a;
	}
	
	private static String[] splitString(String s)
	{
		
		LinkedList<String> splits = new LinkedList<String>();

		for (int i = 0; i < s.length(); i++)
		{
			switch (s.charAt(i))
			{
			// ignore all escaped characters
			case '\\' :
				i++;
				break;
			// split prefix from string
			case ',' :
				splits.add(s.substring(0,i).trim());
				s = s.substring(i+1);
				i = 0;
			}
		}
		splits.add(s.trim());
		
		return splits.toArray(new String[splits.size()]);
	}
	
	public Class<?> getClass(String key)
	{
		String name = getString(key);
		return name == null ? null : loadClass(name);
	}
	
	public Class<?> getClass(String key, Class<?> defaultValue)
	{
		Class<?> clazz = getClass(key);
		return clazz == null ? defaultValue : clazz;
	}
	
	private Class<?> loadClass(String name)
	{
		try
		{
			return Class.forName(name);
		}
		catch (Exception e)
		{
			LOG.error("Could not load class '" + name + "'.", e);
		}
		return null;
	}
	
	public Object getObject(String key)
	{
		Class<?> clazz = getClass(key);
		return clazz == null ? null : createObject(clazz); 
	}

	public Object getObject(String key, Object defaultValue)
	{
		Object obj = getObject(key);
		return obj == null ? null : defaultValue; 
	}
	
	public Object createObject(Class<?> clazz)
	{
		Class<?>[] types = null;
		Constructor<?> constructor = null;
		try
		{
			constructor = clazz.getConstructor(types);
		}
		catch (Exception e)
		{
			LOG.error("Could not find default constructor '" + clazz.getName()
					+ "()'.", e);
			return null;
		}
		
		Object[] args = null;
		try
		{
			return constructor.newInstance(args);
		}
		catch (Exception e)
		{
			LOG.error("Could not create object of type '" + clazz.getName()
					+ "'", e);
			return null;
		}
	}
	
	public String getEntity()
	{
		return "";
	}
	
	public String getProfile()
	{
		return "";
	}
	
	public Configuration forEntity(String entity)
	{
		return new View(this, entity, "");
	}
	
	public Configuration forProfile(String profile)
	{
		return new View(this, "", profile);
	}
	
	public int getInt(String key)
	{
		Number number = parseNumber(getString(key), NumberType.INT);
		return number == null ? 0 : number.intValue();
	}
	public int getInt(String key, int defaultValue)
	{
		Number number = parseNumber(getString(key), NumberType.INT);
		return number == null ? defaultValue : number.intValue();
	}
	
	private Number parseNumber(String s, NumberType type)
	{
		if (s == null)
		{
			return null;
		}
		
		try
		{
			switch (type)
			{
			case INT:
				return Integer.valueOf(s);
			case LONG:
				return Long.valueOf(s);
			case DOUBLE:
				return Double.valueOf(s);
			default:
				throw new NumberFormatException("Unhandled number type '" 
						+ type + "'.");
			}
		}
		catch (Exception e)
		{
			LOG.error("Could not parse number '" + s + "'.");
			return null;
		}
	}
	
	public long getLong(String key)
	{
		Number number = parseNumber(getString(key), NumberType.LONG);
		return number == null ? 0L : number.longValue();
	}
	public long getLong(String key, long defaultValue)
	{
		Number number = parseNumber(getString(key), NumberType.LONG);
		return number == null ? defaultValue : number.longValue();
	}

	public double getDouble(String key)
	{
		Number number = parseNumber(getString(key), NumberType.DOUBLE);
		return number == null ? 0.0d : number.doubleValue();
	}
	public double getDouble(String key, double defaultValue)
	{
		Number number = parseNumber(getString(key), NumberType.DOUBLE);
		return number == null ? defaultValue : number.doubleValue();
	}
	

	public boolean getBool(String key)
	{
		Boolean bool = parseBool(getString(key));
		return bool == null ? false : bool.booleanValue();
	}
	public boolean getBool(String key, boolean defaultValue)
	{
		Boolean bool = parseBool(getString(key));
		return bool == null ? defaultValue : bool.booleanValue();
	}

	
	private Boolean parseBool(String s)
	{
		if (s == null)
		{
			return null;
		}
		
		s = s.toLowerCase();

		if (POSITIVE_KEYWORDS.contains(s))
		{
			return Boolean.TRUE;
		}
		
		if (NEGATIVE_KEYWORDS.contains(s))
		{
			return Boolean.FALSE;
		}
		
		LOG.error("Could not parse boolean value '" + s + "'.");
		return null;
	}
	
	private class View implements Configuration
	{
		PropertyConfiguration base;
		String prefix;
		String profile;
		String[] profiles;
		
		private View(PropertyConfiguration config, String entity, String profile)
		{
			this.base = config;
			this.prefix = entity;
			this.profile = profile;
			this.profiles = unfoldProfile(profile);
		}
		
		public String getString(String key)
		{
			for (String profile : profiles)
			{
				String property = (prefix.length() != 0 ? prefix + "." : "")
					+ key + (profile.length() != 0 ? "_" + profile : "");
				String value = base.getString(property);
				if (value != null)
				{
					return value;
				}
			}
			return null;
		}
		public String getString(String key, String defaultValue)
		{
			String value = getString(key);
			return value != null ? value : defaultValue;
		}
		public String[] getStringArray(String key)
		{
			String line = getString(key);
			return line != null ? splitString(line) : null;
		}
		public String[] getStringArray(String key, String[] defaultValue)
		{
			String[] values = getStringArray(key);
			return values != null ? values : defaultValue;
		}
		
		public Configuration forEntity(String entity)
		{
			if (prefix.length() != 0)
			{
				entity = prefix + "." + entity;
			}
			return new View(base, entity, profile);
		}
		
		public Configuration forProfile(String name)
		{
			if (profile.length() != 0)
			{
				name = this.profile + "_" + name;
			}
			return new View(base, prefix, name);
		}
		
		public Class<?> getClass(String key)
		{
			String name = getString(key);
			return name == null ? null : loadClass(name);
		}
		public Class<?> getClass(String key, Class<?> defaultValue)
		{
			Class<?> clazz = getClass(key);
			return clazz == null ? defaultValue : clazz;
		}
		
		public Object getObject(String key)
		{
			Class<?> clazz = getClass(key);
			return clazz == null ? null : createObject(clazz); 
		}
		public Object getObject(String key, Object defaultValue)
		{
			Object obj = getObject(key);
			return obj == null ? null : defaultValue; 
		}
		
		public Configurable getInstance(String key)
		{
			String name = getString(key);
			if (name == null)
			{
				return null;
			}
			int i = name.indexOf('_');
			String base = i < 0 ? name : name.substring(0, i);
			String suffix = i < 0 ? profile : name.substring(i+1);
			return loadInstance(base, suffix);
		}
		public Configurable getInstance(String key, Configurable defaultValue)
		{
			Configurable inst = getInstance(key);
			return inst == null ? defaultValue : inst;
		}
		
		public int getInt(String key)
		{
			Number number = parseNumber(getString(key), NumberType.INT);
			return number == null ? 0 : number.intValue();
		}
		public int getInt(String key, int defaultValue)
		{
			Number number = parseNumber(getString(key), NumberType.INT);
			return number == null ? defaultValue : number.intValue();
		}
		
		public long getLong(String key)
		{
			Number number = parseNumber(getString(key), NumberType.LONG);
			return number == null ? 0L : number.longValue();
		}
		public long getLong(String key, long defaultValue)
		{
			Number number = parseNumber(getString(key), NumberType.LONG);
			return number == null ? defaultValue : number.longValue();
		}

		public double getDouble(String key)
		{
			Number number = parseNumber(getString(key), NumberType.DOUBLE);
			return number == null ? 0.0d : number.doubleValue();
		}
		public double getDouble(String key, double defaultValue)
		{
			Number number = parseNumber(getString(key), NumberType.DOUBLE);
			return number == null ? defaultValue : number.doubleValue();
		}
		
		public boolean getBool(String key)
		{
			Boolean bool = parseBool(getString(key));
			return bool == null ? false : bool.booleanValue();
		}
		public boolean getBool(String key, boolean defaultValue)
		{
			Boolean bool = parseBool(getString(key));
			return bool == null ? defaultValue : bool.booleanValue();
		}
	}
}
