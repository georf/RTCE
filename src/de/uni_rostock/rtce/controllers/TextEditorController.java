package de.uni_rostock.rtce.controllers;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;

import de.uni_rostock.rtce.Core;
import de.uni_rostock.rtce.log.Log;
import de.uni_rostock.rtce.models.Document;
import de.uni_rostock.rtce.models.RemoteDocument;
import de.uni_rostock.rtce.models.SharedDocument;
import de.uni_rostock.rtce.models.User;
import de.uni_rostock.rtce.models.change_collection.GlobalVersionAlreadyUsedException;
import de.uni_rostock.rtce.models.change_collection.IChangeCollection;
import de.uni_rostock.rtce.rebeca.ConnectionException;
import de.uni_rostock.rtce.rebeca.RebecaController;
import de.uni_rostock.rtce.rebeca.events.ChangeEvent;
import de.uni_rostock.rtce.rebeca.events.GlobalChangeEvent;
import de.uni_rostock.rtce.rebeca.events.StatusEvent;
import de.uni_rostock.rtce.rebeca.filters.RtceOwnerFilter;
import de.uni_rostock.rtce.rebeca.filters.RtceSupporterFilter;

public class TextEditorController implements IDocumentListener,
		IPropertyChangeListener {

	/**
	 * The document/model.
	 */
	Document document;

	/**
	 * last deleted text
	 */
	protected String deletedText = "";

	/**
	 * Call this constructor if the editor is already open (<b>the instance is
	 * the owner</b>).
	 * 
	 * @param editor
	 */
	public TextEditorController(ITextEditor editor) {
		editor.getDocumentProvider().getDocument(editor.getEditorInput())
				.addDocumentListener(this);

		/*
		 * Create a new model and add listeners
		 */
		document = new SharedDocument(editor);
		document.addPropertyChangeListener(this);
		document.addPropertyChangeListener(Core.getInstance().getEditorState());

		/*
		 * Get the Rebeca controller and do some init stuff (add listeners, ...)
		 */
		RebecaController c = RebecaController.getInstance();
		c.addEventListener(document);
		Core.getInstance().getSharedDocuments().add((SharedDocument) document);

		/*
		 * Add filters and advertisements for the document
		 */
		String documentId = document.getDocumentId();
		SharedDocument sharedDocument = (SharedDocument) document;
		sharedDocument.setFilter(new RtceOwnerFilter(documentId));
		sharedDocument.setAdvertisement(new RtceSupporterFilter(documentId));
		c.addOwnerFilters((RtceOwnerFilter) sharedDocument.getFilter(),
				(RtceSupporterFilter) sharedDocument.getAdvertisement());
	}

	/**
	 * Call this constructor if you want to connect to an other document him
	 * (<b>the instance is a supporter</b>).
	 * 
	 * @param connectionPhrase
	 *            connection phrase (host:port/sha1)
	 * @throws ConnectionException
	 */
	public TextEditorController(String connectionPhrase)
			throws ConnectionException {

		/*
		 * Create a new model, set the document's id and add listeners
		 */
		// get document id
		String docId = RemoteDocument.getDocumentId(connectionPhrase);
		document = new RemoteDocument(docId);
		document.addPropertyChangeListener(this);
		document.addPropertyChangeListener(Core.getInstance().getEditorState());

		/*
		 * Get the Rebeca controller and do some init stuff (add listeners, ...)
		 */
		RebecaController c = RebecaController.getInstance();
		c.addEventListener(document);

		/*
		 * Add filters and advertisements for the document
		 */
		RemoteDocument remoteDocument = (RemoteDocument) document;
		remoteDocument.setFilter(new RtceSupporterFilter(docId));
		remoteDocument.setAdvertisement(new RtceOwnerFilter(docId));
		c.addSupporterFilters(remoteDocument.getFilter(), remoteDocument
				.getAdvertisement());

		// Connect to broker
		c.connect(RemoteDocument.getAddress(connectionPhrase));

		// We must set pseudo data for the list
		((RemoteDocument) document).setFileName("loading ...");
		((RemoteDocument) document).setOwner(new User("loading ...", "..."));

		// Add to list
		Core.getInstance().getRemoteDocuments().add((RemoteDocument) document);

		// Send connection request
		c.publish(StatusEvent.getConnectionRequest(RemoteDocument
				.getDocumentId(connectionPhrase), Core.getInstance()
				.getLocalUser()));

	}

	/**
	 * Interface {@link IDocumentListener}
	 */
	@Override
	public void documentAboutToBeChanged(DocumentEvent event) {
		if (document instanceof RemoteDocument) {
			if (event.getLength() != 0) {
				try {
					deletedText = event.getDocument().get(event.getOffset(),
							event.getLength());
				} catch (BadLocationException e) {
					deletedText = "";
				}
			}
		}
	}

	/**
	 * Interface {@link IDocumentListener}
	 * 
	 * Called from IDocument to inform about changed events.
	 * 
	 * Internal it call the model's nextInternalChangeEvent() method to store
	 * the internal change event
	 */
	@Override
	public void documentChanged(DocumentEvent event) {
		/*
		 * LOCAL CHANGE
		 */
		// publish a ChangeEvent
		User user = Core.getInstance().getLocalUser();
		document.nextInternalChangeEvent(document.getDocumentId(), user, event
				.getOffset(), event.getLength(), event.getText());
	}

	/**
	 * Interface {@link IPropertyChangeListener}.
	 * 
	 * Called to inform about changes.
	 */
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		
		/*
		 * Catch the events for SharedDocument
		 */
		if (document instanceof SharedDocument) {

			
			if (event.getProperty().equals(
					SharedDocument.KEY_LOCAL_CHANGE_EVENT_APPLIED)
					|| event.getProperty().equals(
							Document.KEY_DOCUMENT_APPLY_MODIFICATION_AFTER)) {

				/*
				 * Retrieve the change
				 */
				ChangeEvent ev = (ChangeEvent) event.getNewValue();
				SharedDocument sharedDocument = (SharedDocument) document;

				/*
				 * Increase the global version
				 */
				Integer globalVersion = sharedDocument.incrGlobalVersion();

				/*
				 * Put the event into the collection of all changes. This step
				 * includes the correction of the change's offset.
				 */
				IChangeCollection changeCollection = sharedDocument
						.getChangeCollection();
				try {
					changeCollection.put(ev, globalVersion);
				} catch (GlobalVersionAlreadyUsedException e) {
					// this line should never be executed
					Log.log(e.getMessage());
					Log.debug("uuuups, this shouldn't happen");
				}
				/*
				 * Publish the change as a global change.
				 */
				GlobalChangeEvent glEv = new GlobalChangeEvent(ev, document
						.getOwner(), document.getGlobalVersion(), document
						.getChecksum());
				RebecaController.getInstance().publish(glEv);

			}

			
			/*
			 * Catch the events for RemoteDocument
			 */
		} else if (document instanceof RemoteDocument) {
			
			if (event.getProperty()
					.equals(RemoteDocument.KEY_RECEIVED_RESPONSE)) {
				try {
					IWorkbenchWindow window = PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow();
					IWorkbenchPage page = window.getActivePage();

					IEditorPart editor = page.openEditor(
							(RemoteDocument) document,
							"org.eclipse.ui.DefaultTextEditor");

					if (editor instanceof ITextEditor) {
						// Set Texteditor
						document.setTextEditor((ITextEditor) editor);

						// Add Eventlistener
						((ITextEditor) editor).getDocumentProvider()
								.getDocument(editor.getEditorInput())
								.addDocumentListener(this);

					}
				} catch (PartInitException e) {
				}
			}
			/**
			 * Used internally to publish change events that occur if something
			 * has changed in the local editor.
			 */
			if (event.getProperty().equals(
					Document.KEY_LOCAL_CHANGE_EVENT_APPLIED)) {
				ChangeEvent changeEvent = (ChangeEvent) event.getNewValue();
				RebecaController.getInstance().publish(changeEvent);

				((RemoteDocument) document).addUnverifiedChangeEvent(
						changeEvent, deletedText);
				deletedText = "";
			}
		}
		

		
		/*
		 * Catch the events for all Documents
		 */
		
		if (event.getProperty().equals(
				Document.KEY_DOCUMENT_APPLY_MODIFICATION_BEFORE)) {

			/*
			 * Remove the TextEditorController as listener from the document
			 */
			IDocument doc = document.getIDocument();
			if (doc != null) {
				doc.removeDocumentListener(this);
			}

		} else if (event.getProperty().equals(
				Document.KEY_DOCUMENT_APPLY_MODIFICATION_AFTER)) {

			/*
			 * Add the TextEditorController as listener to the document
			 */
			IDocument doc = document.getIDocument();
			if (doc != null) {
				doc.addDocumentListener(this);
			}

		}

	}

}
