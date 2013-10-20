package de.uni_rostock.rtce.rebeca.filters;

import rebeca.Event;
import de.uni_rostock.rtce.rebeca.events.ChatEvent;

/**
 * Represents a filter for chatting. This filter contains the following events:<br>
 * <ul>
 * <li>{@link ChatEvent}</li>
 * </ul>
 * 
 * @author georf
 * 
 */
public class RtceChatFilter extends RtceDocumentFilter {

	private static final long serialVersionUID = -13393234420L;

	public RtceChatFilter() {
		super(null);
	}

	@Override
	public boolean match(Event event) {
		if (super.match(event)) {
			return (event instanceof ChatEvent);
		}

		return false;
	}
}
