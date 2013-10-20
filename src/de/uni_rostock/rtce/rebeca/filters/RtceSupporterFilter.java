package de.uni_rostock.rtce.rebeca.filters;

import de.uni_rostock.rtce.rebeca.events.GlobalChangeEvent;
import de.uni_rostock.rtce.rebeca.events.StatusEvent;

import rebeca.Event;

/**
 * Represents a filter that is important for a supporter. This filter contains
 * the following events:<br>
 * <ul>
 * <li>{@link GlobalChangeEvent}</li>
 * <li>{@link StatusEvent}</li>
 * </ul>
 * 
 * @author jonas
 * 
 */
public class RtceSupporterFilter extends RtceDocumentFilter {

	private static final long serialVersionUID = -1376919222721665420L;

	public RtceSupporterFilter(String docId) {
		super(docId);
	}

	/**
	 * Returns true if the event is a global change event and if the document
	 * filter (see {@link RtceDocumentFilter#match(Event)} matches.
	 */
	@Override
	public boolean match(Event event) {
		if (super.match(event)) {
			return (event instanceof GlobalChangeEvent)
					|| (event instanceof StatusEvent);
		}

		return false;
	}
}
