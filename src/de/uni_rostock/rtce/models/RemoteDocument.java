package de.uni_rostock.rtce.models;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.editors.text.ILocationProvider;

import de.uni_rostock.rtce.Core;
import de.uni_rostock.rtce.log.Log;
import de.uni_rostock.rtce.models.unverified_change.UnverifiedChangeCollection;
import de.uni_rostock.rtce.models.unverified_change.UnverifiedChangeCollectionEntry;
import de.uni_rostock.rtce.rebeca.ConnectionException;
import de.uni_rostock.rtce.rebeca.IRemoteEventListener;
import de.uni_rostock.rtce.rebeca.RebecaController;
import de.uni_rostock.rtce.rebeca.events.ChangeEvent;
import de.uni_rostock.rtce.rebeca.events.GlobalChangeEvent;
import de.uni_rostock.rtce.rebeca.events.RtceEvent;
import de.uni_rostock.rtce.rebeca.events.StatusEvent;
import de.uni_rostock.rtce.rebeca.filters.RtceDocumentFilter;
import de.uni_rostock.rtce.rebeca.filters.RtceOwnerFilter;
import de.uni_rostock.rtce.rebeca.filters.RtceSupporterFilter;

/**
 * Class to handle remote documents.
 * 
 * @author Georg Limbach <georf@dev.mgvmedia.com>
 * 
 */
public class RemoteDocument extends Document implements IStorageEditorInput,
		IStorage, ILocationProvider {

	/**
	 * Returns the address of the given connection phrase
	 * 
	 * @param connectionPhrase
	 *            connection phrase (host:port/sha1)
	 * @return address to host and port
	 * @throws ConnectionException
	 *             if the given phrase not match
	 */
	public static InetSocketAddress getAddress(String connectionPhrase)
			throws ConnectionException {

		if (connectionPhrase.matches("^[a-zA-Z0-9_\\-\\.]+:[0-9]+/[a-z0-9]+$")) {
			String host = connectionPhrase.substring(0, connectionPhrase
					.indexOf(':'));
			String port = connectionPhrase.substring(connectionPhrase
					.indexOf(':') + 1, connectionPhrase.indexOf('/'));
			return new InetSocketAddress(host, Integer.valueOf(port));
		}

		throw new ConnectionException(ConnectionException.badConnectionPhrase);
	}

	/**
	 * Returns the document id of the given connection phrase
	 * 
	 * @param connectionPhrase
	 *            connection phrase (host:port/sha1)
	 * @return document id
	 * @throws ConnectionException
	 *             if the given phrase not match
	 */
	public static String getDocumentId(String connectionPhrase)
			throws ConnectionException {
		if (connectionPhrase.matches("^[a-zA-Z0-9_\\-\\.]+:[0-9]+/[a-z0-9]+$")) {

			return connectionPhrase
					.substring(connectionPhrase.indexOf('/') + 1);
		}

		throw new ConnectionException(ConnectionException.badConnectionPhrase);
	}

	/**
	 * filename of this document
	 */
	protected String fileName = "unset";

	/**
	 * storage of document content
	 */
	protected InputStream inputStream;

	/**
	 * The filter used for rebeca.
	 */
	private RtceSupporterFilter filter;

	/**
	 * The advertisements used for rebeca.
	 */
	private RtceOwnerFilter advertisement;

	/**
	 * Collection for all own unverified changes.
	 */
	private UnverifiedChangeCollection unverifiedChanges = new UnverifiedChangeCollection();

	/**
	 * Constructor
	 * 
	 * @param documentId
	 *            document id
	 */
	public RemoteDocument(String documentId) {

		setDocumentId(documentId);

		inputStream = new ByteArrayInputStream("".getBytes());
	}

	public void addUnverifiedChangeEvent(ChangeEvent event, String deletedText) {

		unverifiedChanges.add(new UnverifiedChangeCollectionEntry(
				getGlobalVersion(), event, deletedText));

	}

	/**
	 * Handles incoming change events ({@link IRemoteEventListener}). In this
	 * case only global changes are interesting.
	 */
	@Override
	public void change(RtceEvent event) {
		/*
		 * Handle global changes.
		 */
		if (event instanceof GlobalChangeEvent) {
			change((GlobalChangeEvent) event);

			if (unverifiedChanges.size() == 0) {
				verifyCheckSum((GlobalChangeEvent) event);
			}
		}
	}

	protected void change(GlobalChangeEvent event) {
		try {
			/*
			 * Get needed objects
			 */
			GlobalChangeEvent glEv = (GlobalChangeEvent) event;
			ChangeEvent ev = glEv.getChangeEvent();
			User localUser = Core.getInstance().getLocalUser();

			// find conflicts
			if (unverifiedChanges.size() != 0) {

				// is it our change
				if (localUser.getId().equals(ev.getUserId())) {
					unverifiedChanges.removeFirst();
					/*
					 * Set the global version
					 */
					setGlobalVersion(glEv.getGlobalVersion());

					return;
				} else {
					if (unverifiedChanges.isBefore(ev.getOffset())) {

						/**
						 * Wenn die Länge 0 ist, wurde nichts gelöscht, dürfte
						 * also nichts passieren
						 * 
						 * Man muss nur den offset korrigieren
						 */
						if (ev.getLength() == 0
								|| unverifiedChanges.isBefore(ev.getOffset()
										+ ev.getLength())) {

							/*
							 * Fire event to before applying the change
							 */
							firePropertyChange(
									KEY_DOCUMENT_APPLY_MODIFICATION_BEFORE,
									null, ev);

							/*
							 * Apply the modification
							 */
							int corretedOffset = unverifiedChanges
									.getCorrectedOffset(ev.getOffset());
							applyModification(corretedOffset, ev.getLength(),
									ev.getText());

							ev.setOffset(corretedOffset);
							unverifiedChanges.applyPatch(ev);

							/*
							 * Fire event to after applying the change
							 */
							firePropertyChange(
									KEY_DOCUMENT_APPLY_MODIFICATION_AFTER,
									null, ev);
							/*
							 * Set the global version
							 */
							setGlobalVersion(glEv.getGlobalVersion());
							return;
						}
						/*
						 * Fire event to before applying the change
						 */
						firePropertyChange(
								KEY_DOCUMENT_APPLY_MODIFICATION_BEFORE, null,
								ev);
						for (ChangeEvent backwards : unverifiedChanges
								.getBackwardsChangeEvents()) {

							/*
							 * Apply the modification
							 */
							applyModification(backwards.getOffset(), backwards
									.getLength(), backwards.getText());

						}
						int corretedOffset = unverifiedChanges
								.getCorrectedOffset(ev.getOffset());
						applyModification(corretedOffset, ev.getLength(), ev
								.getText());

						unverifiedChanges.applyPatch(ev);

						for (ChangeEvent forwards : unverifiedChanges
								.getChangeEvents()) {

							/*
							 * Apply the modification
							 */
							applyModification(forwards.getOffset(), forwards
									.getLength(), forwards.getText());

						}

						/*
						 * Fire event to after applying the change
						 */
						firePropertyChange(
								KEY_DOCUMENT_APPLY_MODIFICATION_AFTER, null, ev);
						/*
						 * Set the global version
						 */
						setGlobalVersion(glEv.getGlobalVersion());
						return;
					} else {
						ev.setOffset(unverifiedChanges.getCorrectedOffset(ev
								.getOffset()));
					}
				}
			}

			/*
			 * Set the global version
			 */
			setGlobalVersion(glEv.getGlobalVersion());

			/*
			 * Ignore own changes.
			 */
			if (ev.getUserId().equals(localUser.getId()))
				return;

			/*
			 * Fire event to before applying the change
			 */
			firePropertyChange(KEY_DOCUMENT_APPLY_MODIFICATION_BEFORE, null, ev);

			/*
			 * Apply the modification
			 */
			applyModification(ev.getOffset(), ev.getLength(), ev.getText());

			/*
			 * Fire event to after applying the change
			 */
			firePropertyChange(KEY_DOCUMENT_APPLY_MODIFICATION_AFTER, null, ev);

		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	protected void verifyCheckSum(GlobalChangeEvent glEv) {
		if (!glEv.getChecksum().equals(getChecksum())) {
			// get document
			IDocument document = getTextEditor().getDocumentProvider()
					.getDocument(getTextEditor().getEditorInput());
			Log.debug("|" + document.get() + "|checksum:"
					+ getChecksum() + "|get:" + glEv.getChecksum());
			RebecaController.getInstance().publish(
					StatusEvent.getRefreshRequest(getDocumentId(), Core
							.getInstance().getLocalUser()));
		} else {
			Log.debug("Checksum correct");
		}

	}

	/**
	 * Disconnect from this document
	 * 
	 * Destroys intern informations and remove the rebeca filter
	 */
	public void disconnect() {
		// send notification
		RebecaController.getInstance().publish(
				StatusEvent.getDisconnect(getDocumentId(), Core.getInstance()
						.getLocalUser()));

		// unsubscribe
		RebecaController.getInstance().removeSupporterFilters(
				(RtceDocumentFilter) getFilter(), getAdvertisement());

		// remove listener
		RebecaController.getInstance().removeRemoteEventListener(this);

		// close texteditor
		this.textEditor.close(false);

		// remove from list
		Core.getInstance().getRemoteDocuments().remove(this);

		// inform listeners
		firePropertyChange(KEY_DOCUMENT_DESTROY, null, null);

	}

	/**
	 * Interface IStorageEditorInput
	 */
	@Override
	public boolean exists() {
		// Resource doesn't exist in workspace
		return false;
	}

	/**
	 * Interface {@link IStorage}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Class adapter) {

		if (adapter == org.eclipse.ui.editors.text.ILocationProvider.class) {
			return this;
		} else if (adapter == java.lang.Object.class) {
			return this;
		}

		return Platform.getAdapterManager().getAdapter(this, adapter);
	}

	/**
	 * Interface {@link IStorage}
	 */
	public InputStream getContents() {
		return inputStream;
	}

	/**
	 * Returns the filename
	 * 
	 * @return filename
	 */
	@Override
	public String getFileName() {
		return fileName;
	}

	/**
	 * Interface {@link IStorage}
	 */
	@Override
	public IPath getFullPath() {
		return null;
	}

	/**
	 * Interface IStorageEditorInput
	 */
	@Override
	public ImageDescriptor getImageDescriptor() {
		return Core.getImageDescriptor("icons/application_x_mswinurl.png");
	}

	/**
	 * Interface IStorageEditorInput
	 */
	@Override
	public String getName() {
		return fileName;
	}

	/**
	 * Interface IStorageEditorInput
	 */
	@Override
	public IPersistableElement getPersistable() {
		return null;
	}

	/**
	 * Interface IStorageEditorInput
	 */
	@Override
	public IStorage getStorage() {
		return this;
	}

	/**
	 * Interface IStorageEditorInput
	 */
	@Override
	public String getToolTipText() {
		return "Remote file: " + getFileName();
	}

	/**
	 * Interface {@link IStorage}
	 */
	@Override
	public boolean isReadOnly() {
		return false;

	}

	/**
	 * Overrides whole content
	 * 
	 * @param content
	 */
	public void setContent(String content) {
		inputStream = new ByteArrayInputStream(content.getBytes());
	}

	/**
	 * Sets the filename
	 * 
	 * @param fileName
	 *            filename to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;

		// inform listeners
		firePropertyChange(KEY_FILENAME_CHANGED, null, null);
	}

	/**
	 * Interface {@link IRemoteEventListener}.
	 */
	@Override
	public void status(StatusEvent event) {

		if (event.getStatus().equals(StatusEvent.ConnectionResponse)) {
			setFileName(event.getDocumentName());
			setContent(event.getDocument());
			setOwner(new User(event.getName(), event.getShortName(), event
					.getUserId()));
			setGlobalVersion(event.getGlobalVersion());

			// inform listeners
			firePropertyChange(KEY_RECEIVED_RESPONSE, null, null);

		} else if (event.getStatus().equals(StatusEvent.Unshare)) {

			// TODO Message to user
			Log.info("Get an unshare event");

			// remove document id filter
			RebecaController.getInstance().removeSupporterFilters(getFilter(),
					getAdvertisement());

			// remove rebeca event filter
			RebecaController.getInstance().removeRemoteEventListener(this);

			// remove from list
			Core.getInstance().getRemoteDocuments().remove(this);

			// close texteditor
			textEditor.close(false);

		} else if (event.getStatus().equals(StatusEvent.RefreshResponse)) {
			if (unverifiedChanges.size() == 0) {
				setContent(event.getDocument());
				setGlobalVersion(event.getGlobalVersion());
			}
		}
	}

	/**
	 * @return the filter
	 */
	public RtceSupporterFilter getFilter() {
		return filter;
	}

	/**
	 * @param filter
	 *            the filter to set
	 */
	public void setFilter(RtceSupporterFilter filter) {
		this.filter = filter;
	}

	/**
	 * @return the advertisement
	 */
	public RtceOwnerFilter getAdvertisement() {
		return advertisement;
	}

	/**
	 * @param advertisement
	 *            the advertisement to set
	 */
	public void setAdvertisement(RtceOwnerFilter advertisement) {
		this.advertisement = advertisement;
	}

	/**
	 * @see ILocationProvider
	 */
	@Override
	public IPath getPath(Object element) {
		return null;
	}
}
