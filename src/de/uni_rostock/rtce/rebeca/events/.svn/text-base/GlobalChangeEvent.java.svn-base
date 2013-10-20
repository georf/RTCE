package de.uni_rostock.rtce.rebeca.events;

import de.uni_rostock.rtce.models.User;

public class GlobalChangeEvent extends RtceEvent {

	private static final long serialVersionUID = -6402175144169710654L;

	public static final String KEY_LOCAL_VERSION = "key_local_version";
	public static final String KEY_GLOBAL_VERSION = "key_global_version";
	public static final String KEY_CHANGE_EVENT = "key_change_event";
	public static final String KEY_CHECKSUM = "key_checksum";

	public GlobalChangeEvent(ChangeEvent changeEvent, User user,
			int globalVersion, String checksum) {
		super(changeEvent.getDocumentId(), user);
		put(KEY_GLOBAL_VERSION, globalVersion);
		put(KEY_CHANGE_EVENT, changeEvent);
		put(KEY_CHECKSUM, checksum);
	}

	public int getGlobalVersion() {
		return (Integer) get(KEY_GLOBAL_VERSION);
	}

	public ChangeEvent getChangeEvent() {
		return (ChangeEvent) get(KEY_CHANGE_EVENT);
	}
	
	public String getChecksum() {
		return (String) get(KEY_CHECKSUM);
	}

	public String toString() {
		ChangeEvent c = getChangeEvent();
		return "|O:" + c.getOffset() + "|L:" + c.getLength() + "|T:"
				+ c.getText() + "|Ma:" + getShortName() + "|Sl:"
				+ c.getShortName() + "|Gv:" + getGlobalVersion() + "|";
	}
}
