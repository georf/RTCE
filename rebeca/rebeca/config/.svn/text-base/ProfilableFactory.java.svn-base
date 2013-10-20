/*
 * $id$
 */
package rebeca.config;

/**
 * @author parzy
 *
 */
public class ProfilableFactory<T> implements Factory<T>
{
	// TODO parzy check type safety
	/**
	 * Array of factories to store a reeference to one for every active profile.
	 */
	@SuppressWarnings("unchecked")
	private Factory<T>[] factories = new Factory[Config.MAX_PROFILES];
	
	private boolean profiles = false;
	
	public ProfilableFactory(Factory<T> defaultFactory)
	{
		factories[0] = defaultFactory;
	}
	
	public void setFactory(Factory<T> f, int profile)
	{
		factories[profile] = f;
	}
	
	protected Factory<T> getFactory()
	{
		// TODO parzy implement the code to determine the current profile
		return profiles ? factories[100] : factories[0];
	}
	
	// Factory implementation -------------------------------------------------
	// ------------------------------------------------------------------------
	
	public void configure(Configuration c) { /* unnecessary */}
	
	public T create()
	{
		return getFactory().create();
	}
	
	public T create(Object... args)
	{
		return getFactory().create(args);
	}
}
