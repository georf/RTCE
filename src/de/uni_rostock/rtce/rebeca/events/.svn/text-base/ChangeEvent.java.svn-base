package de.uni_rostock.rtce.rebeca.events;

import de.uni_rostock.rtce.models.User;

public class ChangeEvent extends RtceEvent {

	private static final long serialVersionUID = -5261421033038129955L;

	/**
	 * 
	 * @param documentId
	 * @param name
	 * @param shortName
	 * @param offset
	 * @param length
	 * @param text
	 */
	public ChangeEvent(String documentId, User user, int offset, int length,
			String text, int localVersion, int refGlobalVersion) {

		super(documentId, user);

		put("Offset", offset);
		put("Length", length);
		put("Text", text);
		put("LocalVersion", localVersion);
		put("refGlobalVersion", refGlobalVersion);

	}

	public int getOffset() {
		return (Integer) get("Offset");
	}

	public int getLength() {
		return (Integer) get("Length");
	}

	public String getText() {
		return (String) get("Text");
	}

	public int getLocalVersion() {
		return (Integer) get("LocalVersion");
	}

	public int getReferenceGlobalVersion() {
		return (Integer) get("refGlobalVersion");
	}

	public void setOffset(int offset) {
		put("Offset", offset);
	}

	public String toString() {
		return "|O:" + getOffset() + "|L:" + getLength() + "|T:" + getText()
				+ "|N:" + getShortName() + "|Gv:" + getReferenceGlobalVersion()
				+ "|Lv:" + getLocalVersion() + "|";
	}

}
