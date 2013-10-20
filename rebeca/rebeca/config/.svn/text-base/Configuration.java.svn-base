/*
 * $id$
 */
package rebeca.config;

/**
 * @author parzy
 *
 */
public interface Configuration 
{
	public String getString(String key);
	public String getString(String key, String defaultValue);
	
	public String[] getStringArray(String key);
	public String[] getStringArray(String key, String[] defaultValue);
	
	public Configuration forEntity(String entity);
	public Configuration forProfile(String profile);
	
	public Class<?> getClass(String key);
	public Class<?> getClass(String key, Class<?> defaultValue);
	
	public Object getObject(String key);
	public Object getObject(String key, Object defaultValue);
	
	public Configurable getInstance(String key);
	public Configurable getInstance(String key, Configurable defaultValue);

	public int getInt(String key);
	public int getInt(String key, int defaulValue);

	public long getLong(String key);
	public long getLong(String key, long defaulValue);

	public double getDouble(String key);
	public double getDouble(String key, double defaulValue);

	public boolean getBool(String key);
	public boolean getBool(String key, boolean defaulValue);

	
//	public Configuration forProfile(int profile);
//
//	public String getEntity();
//	public int getProfile();
//	public String getProfileName();
}
