package de.uni_rostock.rtce.models.change_collection;

import de.uni_rostock.rtce.rebeca.events.ChangeEvent;

/**
 * Represents an entry for a {@link IChangeCollection}, which stores a change
 * (an object of {@link ChangeEvent}) and the entries before and after this
 * entry. Although the global version, which identifies the entry, is stored
 * here.
 * 
 * @author jonas
 * 
 */
public interface IChangeCollectionEntry {

	/**
	 * Returns the entry, that is stored after this entry.
	 * 
	 * @return the next entry, null if this entry is the last.
	 */
	public IChangeCollectionEntry getAfter();

	/**
	 * Sets the entry, that is stored after this entry.
	 * 
	 * @param after
	 *            the next entry
	 */
	public void setAfter(IChangeCollectionEntry after);

	/**
	 * Returns the entry, that is stored before this entry.
	 * 
	 * @return the previous entry, null if this entry is the first.
	 */
	public IChangeCollectionEntry getBefore();

	/**
	 * Sets the entry, that is stored before this entry.
	 * 
	 * @param after
	 *            the previous entry
	 */
	public void setBefore(IChangeCollectionEntry before);

	/**
	 * Returns the change, that is stored in this entry.
	 * 
	 * @return the change
	 */
	public ChangeEvent getChangeEvent();

	/**
	 * Sets the change, that is stored in this entry.
	 * 
	 * @param changeEvent
	 *            the change
	 */
	public void setChangeEvent(ChangeEvent changeEvent);

	/**
	 * Returns the global version of this entry, which is used to identify this
	 * entry by the overlaying {@link IChangeCollection}.
	 * 
	 * @return the global version
	 */
	public Integer getGlobalVersion();

	/**
	 * Sets the global version of this entry, which is used to identify this
	 * entry by the overlaying {@link IChangeCollection}.
	 * 
	 * @param globalVersion
	 *            the gloval version
	 */
	public void setGlobalVersion(Integer globalVersion);

}
