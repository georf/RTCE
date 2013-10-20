package de.uni_rostock.rtce.models.unverified_change;

import de.uni_rostock.rtce.rebeca.events.ChangeEvent;

public class UnverifiedChangeCollectionEntry {
	private int globalVersion;
	private ChangeEvent changeEvent;
	private String deletedText;
	public UnverifiedChangeCollectionEntry(int version, ChangeEvent ev, String del) {
		globalVersion = version;
		changeEvent = ev;
		deletedText = del;
	}
	public int getGlobalVersion() {
		return globalVersion;
	}
	public void setGlobalVersion(int globalVersion) {
		this.globalVersion = globalVersion;
	}
	public ChangeEvent getChangeEvent() {
		return changeEvent;
	}
	public String getDeletedText() {
		return deletedText;
	}
	
	
}
