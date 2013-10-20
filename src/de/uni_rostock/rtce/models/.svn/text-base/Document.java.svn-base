package de.uni_rostock.rtce.models;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.texteditor.ITextEditor;

import de.uni_rostock.rtce.debug.DebugHelper;
import de.uni_rostock.rtce.rebeca.IRemoteEventListener;
import de.uni_rostock.rtce.rebeca.events.ChangeEvent;

/**
 * Abstract class to hanle documents
 * 
 * @author Georg Limbach <georf@dev.mgvmedia.com>
 * 
 */
public abstract class Document extends AbstractModel implements
		IRemoteEventListener {

	/*
	 *  Keys for property change events
	 */
	public static final String KEY_DOCUMENT_APPLY_MODIFICATION_BEFORE = "key_document_apply_modification_before";
	public static final String KEY_DOCUMENT_APPLY_MODIFICATION_AFTER = "key_document_apply_modification_after";
	public static final String KEY_DOCUMENT_DESTROY = "key_document_destroy";
	public static final String KEY_DOCUMENT_ID_CHANGED = "key_document_id_changed";
	public static final String KEY_FILENAME_CHANGED = "key_filename_changed";
	public static final String KEY_OWNER_CHANGED = "key_owner_changed";
	public static final String KEY_RECEIVED_RESPONSE = "key_received_response";
	public static final String KEY_SUPPORTER_COUNT_CHANGED = "key_document_supporter_count_changed";
	public static final String KEY_LOCAL_CHANGE_EVENT_APPLIED = "key_local_change_event_applied";
	public static final String KEY_LOCAL_VERSION_INCR = "key_local_version_incr";

	/**
	 * The local version of this document.
	 */
	protected int localVersion = 0;
	
	/**
	 * The global version of this document.
	 */
	protected Integer globalVersion = 0;

	/**
	 * The last change event, that has been published.
	 */
	protected ChangeEvent lastChangeEvent;

	/**
	 * The document's id
	 */
	protected String documentId;

	/**
	 * owner of this document
	 */
	protected User owner;

	/**
	 * Linked text editor
	 */
	protected ITextEditor textEditor;

	/**
	 * Default no arg-constructor.
	 */
	public Document() {
	}

	/**
	 * Apply the modification to the text editor.
	 * 
	 * @param offset the change's offset (can not be negative)
	 * @param length the change's length
	 * @param text the change's text
	 * @return false if something fails
	 * @throws BadLocationException
	 */
	protected boolean applyModification(int offset, int length, String text)
			throws BadLocationException {
		IDocument doc = getIDocument();

		if (doc == null)
			return false;

		doc.replace(offset, length, text);		
		return true;
		
	}

	/**
	 * Returns the document id
	 * 
	 * @return document id
	 */
	public String getDocumentId() {
		return documentId;
	}

	/**
	 * Should return the filename
	 * 
	 * @return filename
	 */
	public abstract String getFileName();

	/**
	 * Returns the {@link IDocument} for this document. If something fails it
	 * returns null.
	 * 
	 * @return document or null
	 */
	public IDocument getIDocument() {
		if (textEditor == null)
			return null;
		if (textEditor.getDocumentProvider() == null)
			return null;

		return textEditor.getDocumentProvider().getDocument(
				textEditor.getEditorInput());
	}

	/**
	 * Returns the owner of this document
	 * 
	 * @return owner of this document
	 */
	public User getOwner() {
		return owner;
	}

	/**
	 * Returns the linked text editor
	 * 
	 * @return text editor
	 */
	public ITextEditor getTextEditor() {
		return textEditor;
	}

	/**
	 * Interface {@link IRemoteEventListener}
	 */
	public boolean match(String docId) {
		return (getDocumentId().equals(docId));
	}

	/**
	 * Sets the document id
	 * 
	 * @param documentId
	 *            document id
	 */
	public void setDocumentId(String documentId) {
		this.documentId = documentId;

		// inform listeners
		firePropertyChange(KEY_DOCUMENT_ID_CHANGED, null, documentId);
	}

	/**
	 * Sets the owner of this document
	 * 
	 * @param owner
	 *            owner to set
	 */
	public void setOwner(User owner) {
		this.owner = owner;

		// inform listeners
		firePropertyChange(KEY_OWNER_CHANGED, null, owner);
	}

	/**
	 * Sets the linked text editor
	 * 
	 * @param editor
	 */
	public void setTextEditor(ITextEditor editor) {
		textEditor = editor;
	}

	/**
	 * @return the lastChangeEvent
	 */
	public ChangeEvent getLastChangeEvent() {
		return lastChangeEvent;
	}

	public int getLocalVersion() {
		return this.localVersion;
	}
	
	/**
	 * Increments the local version by 1 (one), fires KEY_LOCAL_VERSION_INCR and
	 * returns the new local version.
	 * 
	 * @return
	 */
	private int incrLocalVersion() {
		int oldLocalVersion = new Integer(this.localVersion);
		this.localVersion++;
		firePropertyChange(KEY_LOCAL_VERSION_INCR, oldLocalVersion,
				this.localVersion);
		return this.localVersion;
	}

	/**
	 * Produces a new instance of {@link ChangeEvent} and sets the given
	 * attributes. Also it increases the local version by 1 (one) and set it in
	 * the ChangeEvent object, too. This object will be set as the last change
	 * event (as the lastChangeEvent attribute).<br>
	 * After that the KEY_LAST_CHANGE_EVENT_SET event will be fired and the
	 * {@link ChangeEvent} object is returned.
	 * 
	 * @param documentId
	 *            the document's id
	 * @param user
	 *            the current user
	 * @param offset
	 *            the offset of the change
	 * @param length
	 *            the length of the change
	 * @param text
	 *            the text of the change
	 * @return an object of {@link ChangeEvent} with the given attributes and
	 *         the current local version
	 */
	public ChangeEvent nextInternalChangeEvent(String documentId, User user,
			int offset, int length, String text) {
		
		/*
		 * Create and set the new change event. Fire then the corresponding
		 * event.  
		 */
		ChangeEvent oldLastChangeEvent = this.lastChangeEvent;
		incrLocalVersion();
		this.lastChangeEvent = new ChangeEvent(documentId, user, offset,
				length, text, this.localVersion, this.globalVersion);
		firePropertyChange(KEY_LOCAL_CHANGE_EVENT_APPLIED, oldLastChangeEvent,
				this.lastChangeEvent);
		return this.lastChangeEvent;
	}

	/**
	 * @return the globalVersion
	 */
	public Integer getGlobalVersion() {
		return globalVersion;
	}
	
	public Integer incrGlobalVersion(){
		this.globalVersion++;
		return this.globalVersion;
	}

	/**
	 * @param globalVersion the globalVersion to set
	 */
	public void setGlobalVersion(int globalVersion) {
		this.globalVersion = globalVersion;
	}

	
	public String getChecksum() {

		// get document
		String content = getTextEditor().getDocumentProvider()
				.getDocument(getTextEditor().getEditorInput()).get();
		
		return String.valueOf(content.hashCode());
	}
}
