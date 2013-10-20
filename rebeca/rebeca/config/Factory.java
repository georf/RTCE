/*
 * $id$
 */
package rebeca.config;

/**
 * @author parzy
 *
 */
public interface Factory<T> extends Configurable
{
	//public void configure(Configuration c);
	
	public T create();
	
	public T create(Object... args);
}
