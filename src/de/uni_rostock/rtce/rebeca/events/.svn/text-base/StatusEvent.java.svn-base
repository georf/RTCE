package de.uni_rostock.rtce.rebeca.events;

import de.uni_rostock.rtce.Core;
import de.uni_rostock.rtce.models.User;

public class StatusEvent extends RtceEvent {

	private static final long serialVersionUID = 50479640093803570L;

	public static final String ConnectionRequest = "ConnectionRequest";
	public static final String ConnectionResponse = "ConnectionResponse";
	public static final String Unshare = "Unshare";
	public static final String Disconnect = "Disconnect";
	public static final String Noop = "Noop";
	public static final String RefreshRequest = "RefreshRequest";
	public static final String RefreshResponse = "RefreshResponse";

	public static final String KEY_STATUS = "Status";
	public static final String KEY_DOCUMENT = "Document";
	public static final String KEY_DOCUMENT_NAME = "DocumentName";
	public static final String KEY_GLOBAL_VERSION = "GlobalVersion";
	public static final String KEY_NODOC = "nodoc";

	protected StatusEvent(String documentId, User user, String status) {
		super(documentId, user);
		put(KEY_STATUS, status);
	}

	public String getStatus() {
		return (String) get(KEY_STATUS);
	}

	public String getDocumentName() {
		return (String) get(KEY_DOCUMENT_NAME);
	}

	public String getDocument() {
		return (String) get(KEY_DOCUMENT);
	}

	public Integer getGlobalVersion() {
		return (Integer) get(KEY_GLOBAL_VERSION);
	}

	public static StatusEvent getConnectionRequest(String documentId, User user) {

		return new StatusEvent(documentId, user, ConnectionRequest);
	}

	public static StatusEvent getConnectionResponse(String documentId,
			User user, String filename, String content, Integer globalVersion) {

		StatusEvent help = new StatusEvent(documentId, user, ConnectionResponse);

		help.put(KEY_DOCUMENT_NAME, filename);
		help.put(KEY_DOCUMENT, content);
		help.put(KEY_GLOBAL_VERSION, globalVersion);

		return help;
	}

	public static StatusEvent getUnshare(String documentId, User user) {
		return new StatusEvent(documentId, user, Unshare);
	}

	public static StatusEvent getDisconnect(String documentId, User user) {
		return new StatusEvent(documentId, user, Disconnect);
	}

	public static StatusEvent getNoop() {
		return new StatusEvent(KEY_NODOC, Core.getInstance().getLocalUser(),
				Noop);
	}

	public static StatusEvent getRefreshRequest(String documentId, User user) {
		return new StatusEvent(documentId, user, RefreshRequest);
	}

	public static StatusEvent getRefreshResponse(String documentId, User user,
			String content, Integer globalVersion) {

		StatusEvent help = new StatusEvent(documentId, user, RefreshResponse);

		help.put(KEY_DOCUMENT, content);
		help.put(KEY_GLOBAL_VERSION, globalVersion);

		return help;
	}

	public String toString() {
		return "|" + getStatus() + "|" + getShortName() + "|";
	}
}
