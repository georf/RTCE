package de.uni_rostock.rtce.rebeca.events;

import de.uni_rostock.rtce.models.User;
import de.uni_rostock.rtce.rebeca.RebecaCloneable;
import rebeca.namevalue.NameValueEvent;

public abstract class RtceEvent extends NameValueEvent implements RebecaCloneable {

	private static final long serialVersionUID = 8689638474646064750L;

	// Constants
	public static final String KEY_RTCE_VERSION = "RtceVersion";
	public static final String KEY_DOCUMENT_ID = "DocumentId";
	public static final String KEY_USER_ID = "UserId";
	public static final String KEY_NAME = "Name";
	public static final String KEY_SHORT_NAME = "ShortName";
	public static final String KEY_NONE_RTCE = "----";

	// Protocol version
	protected static final String RTCE_VERSION = "1.0";

	public RtceEvent(String documentId, User user) {

		put(KEY_RTCE_VERSION, RTCE_VERSION);

		if (documentId != null) {
			put(KEY_DOCUMENT_ID, documentId);
		} else {
			put(KEY_DOCUMENT_ID, KEY_NONE_RTCE);
		}

		put(KEY_USER_ID, user.getId());
		put(KEY_NAME, user.getName());
		put(KEY_SHORT_NAME, user.getShortName());
	}

	public String getDocumentId() {
		return (String) get(KEY_DOCUMENT_ID);
	}

	public String getName() {
		return (String) get(KEY_NAME);
	}

	public String getShortName() {
		return (String) get(KEY_SHORT_NAME);
	}

	public String getUserId() {
		return (String) get(KEY_USER_ID);
	}

	public User getUser() {
		return new User(getName(), getShortName(), getUserId());
	}
	
	public Object clone() {
		return super.clone();
	}
}
