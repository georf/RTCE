package de.uni_rostock.rtce.rebeca.filters;

import rebeca.Event;
import de.uni_rostock.rtce.rebeca.events.ChangeEvent;
import de.uni_rostock.rtce.rebeca.events.StatusEvent;

/**
 * Represents a filter that is important for an owner. <br>
 * This filter contains the subscriptions for the following events:<br>
 * <ul>
 *  <li>{@link ChangeEvent}</li>
 *  <li>{@link StatusEvent}</li>
 * </ul>
 * @author jonas
 *
 */
public class RtceOwnerFilter extends RtceDocumentFilter {

	private static final long serialVersionUID = 8988342450137544257L;

	public RtceOwnerFilter(String docId) {
		super(docId);
	}
	
	/**
	 * Returns true if the event is a change event or status event and if the
	 * document filter (see {@link RtceDocumentFilter#match(Event)} matches.
	 */
	@Override
	public boolean match(Event event) {
		if (super.match(event)) {
			return (event instanceof ChangeEvent) || (event instanceof StatusEvent);
		}

		return false;
	}

}
