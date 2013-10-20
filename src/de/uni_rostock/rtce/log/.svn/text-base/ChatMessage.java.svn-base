package de.uni_rostock.rtce.log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import de.uni_rostock.rtce.Core;
import de.uni_rostock.rtce.models.User;

/**
 * A ChatMessage containing a chat message
 * 
 * @author Georg Limbach <georf@dev.mgvmedia.com>
 */
public class ChatMessage extends LogElement {

	public User user;

	/**
	 * Sets the user to current user
	 * 
	 * @param message
	 */
	public ChatMessage(String message) {
		super(message);
		this.user = Core.getInstance().getLocalUser();
	}

	public ChatMessage(String message, User user) {
		super(message);
		this.user = user;
	}

	public String toString() {
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		return dateFormat.format(date) + " " + user.getShortName() + ": "
				+ message;
	}

}
