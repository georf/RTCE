package de.uni_rostock.rtce.rebeca.events;

/**
 * @author georf
 */

import de.uni_rostock.rtce.models.User;

public class ChatEvent extends RtceEvent {

	private static final long serialVersionUID = -3461421033412965L;

	// Constants
	public static final String KEY_TEXT = "Text";

	/**
	 * 
	 * @param user
	 * @param text
	 */
	public ChatEvent(User user, String text) {

		super(null, user);

		put(KEY_TEXT, text);

	}

	public String getText() {
		return (String) get(KEY_TEXT);
	}

}
