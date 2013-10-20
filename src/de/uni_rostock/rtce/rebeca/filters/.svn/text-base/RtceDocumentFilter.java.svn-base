package de.uni_rostock.rtce.rebeca.filters;

import de.uni_rostock.rtce.rebeca.RebecaCloneable;
import de.uni_rostock.rtce.rebeca.events.RtceEvent;
import rebeca.Event;
import rebeca.filter.BasicFilter;

/**
 * Handles the document id as rebeca filter
 * 
 * @author Georg Limbach <georf@dev.mgvmedia.com>
 * 
 */
public class RtceDocumentFilter extends BasicFilter implements RebecaCloneable {

	private static final long serialVersionUID = -5665622260384176766L;

	protected String documentId;

	public RtceDocumentFilter(String docId) {
		super();
		
		if (docId != null) {
			documentId = docId;
		} else {
			documentId = RtceEvent.KEY_NONE_RTCE;
		}
	}

	/**
	 * Method for rebeca.Filter
	 * 
	 * Returns true if the document id match, false otherwise.
	 * 
	 * @return filter match
	 */
	public boolean match(Event event) {

		if (event instanceof RtceEvent) {
			return (((RtceEvent) event).getDocumentId().equals(documentId));

		}

		return false;
	}

	/**
	 * Getter for document id
	 * 
	 * @return document id
	 */
	public String getDocumentId() {
		return documentId;
	}
	
	public RtceDocumentFilter clone() {
		return new RtceDocumentFilter(getDocumentId());
	}
}
