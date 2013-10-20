package de.uni_rostock.rtce.models.change_collection;

import de.uni_rostock.rtce.rebeca.events.ChangeEvent;

/**
 * A Collection that stores global changes, which are stored in an item of
 * {@link IChangeCollectionEntry}.
 * 
 * @author jonas
 * 
 */
public interface IChangeCollection {

	/**
	 * Returns the first item of the collection.
	 * 
	 * @return the first item
	 */
	public IChangeCollectionEntry getFirst();

	/**
	 * Returns the last item of the collection.
	 * 
	 * @return the last item
	 */
	public IChangeCollectionEntry getLast();

	/**
	 * Gets a change by it's global version. Returns null if there is no change
	 * for the given global version.
	 * 
	 * @param globalVersion
	 *            the global version
	 * @return the change if there is one for the global version, null
	 *         otherwise.
	 */
	public IChangeCollectionEntry get(Integer globalVersion);

	/**
	 * Stores a change by the given global version.
	 * 
	 * @param changeEvent
	 *            the change
	 * @param globalVersion
	 *            the change's global version
	 * @throws GlobalVersionAlreadyUsedException
	 *             if the given global version is used already
	 */
	public void put(ChangeEvent changeEvent, Integer globalVersion)
			throws GlobalVersionAlreadyUsedException;

	/**
	 * Defines if this collection is empty.
	 * 
	 * @return True if the collection is empty, false otherwise.
	 */
	public boolean isEmpty();

	/**
	 * Deletes all entries of the collection.
	 */
	public void clear();

	/**
	 * Returns if there is an entry stored by the given global version.
	 * 
	 * @param globalVersion
	 *            the global version
	 * @return True if there exists an entry for the given global version, false
	 *         otherwise.
	 */
	public boolean contains(Integer globalVersion);

	/**
	 * Returns the number of entries in this collection.
	 * 
	 * @return the number of entries
	 */
	public int size();

}
