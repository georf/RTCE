package de.uni_rostock.rtce.models.change_collection;

import de.uni_rostock.rtce.rebeca.events.ChangeEvent;

/**
 * @see {@link IChangeCollectionEntry}
 *
 */
public class ChangeCollectionEntry implements IChangeCollectionEntry {
	
	/**
	 * The entry before this. 
	 */
	protected IChangeCollectionEntry before;
	
	/**
	 * The entry after this.
	 */
	protected IChangeCollectionEntry after;
	
	/**
	 * The change.
	 */
	protected ChangeEvent changeEvent;
	
	/**
	 * The global version.
	 */
	protected Integer globalVersion;

	/**
	 * Standard constructor.
	 * @param changeEvent the change
	 * @param globalVersion the global version
	 */
	public ChangeCollectionEntry(ChangeEvent changeEvent,
			Integer globalVersion) {
		super();
		this.changeEvent = changeEvent;
		this.globalVersion = globalVersion;
	}
	
	/**
	 * @return the before
	 */
	public IChangeCollectionEntry getBefore() {
		return before;
	}
	/**
	 * @param before the before to set
	 */
	public void setBefore(IChangeCollectionEntry before) {
		this.before = before;
	}
	/**
	 * @return the after
	 */
	public IChangeCollectionEntry getAfter() {
		return after;
	}
	/**
	 * @param after the after to set
	 */
	public void setAfter(IChangeCollectionEntry after) {
		this.after = after;
	}
	/**
	 * @return the changeEvent
	 */
	public ChangeEvent getChangeEvent() {
		return changeEvent;
	}
	/**
	 * @param changeEvent the changeEvent to set
	 */
	public void setChangeEvent(ChangeEvent changeEvent) {
		this.changeEvent = changeEvent;
	}
	/**
	 * @return the globalVersion
	 */
	public Integer getGlobalVersion() {
		return globalVersion;
	}
	/**
	 * @param globalVersion the globalVersion to set
	 */
	public void setGlobalVersion(Integer globalVersion) {
		this.globalVersion = globalVersion;
	}
	
	
}
