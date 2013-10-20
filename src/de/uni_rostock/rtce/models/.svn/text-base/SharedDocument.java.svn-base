package de.uni_rostock.rtce.models;

import java.net.InetSocketAddress;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.texteditor.ITextEditor;

import de.uni_rostock.rtce.Core;
import de.uni_rostock.rtce.models.change_collection.ChangeCollection;
import de.uni_rostock.rtce.models.change_collection.IChangeCollection;
import de.uni_rostock.rtce.rebeca.IRemoteEventListener;
import de.uni_rostock.rtce.rebeca.IpAddressDescriptor;
import de.uni_rostock.rtce.rebeca.RebecaController;
import de.uni_rostock.rtce.rebeca.events.ChangeEvent;
import de.uni_rostock.rtce.rebeca.events.RtceEvent;
import de.uni_rostock.rtce.rebeca.events.StatusEvent;
import de.uni_rostock.rtce.rebeca.filters.RtceOwnerFilter;
import de.uni_rostock.rtce.rebeca.filters.RtceSupporterFilter;

public class SharedDocument extends Document {

	public static final String KEY_GLOBAL_VERSION_INCR = "key_global_version_incr";

	/**
	 * selected ip address descriptor
	 */
	protected IpAddressDescriptor ipAdress;

	/**
	 * list with supporters
	 */
	protected ArrayList<User> supporters = new ArrayList<User>();

	/**
	 * Constructor, that generates a new document id.
	 * 
	 * @param editor
	 *            texteditor to share
	 */
	public SharedDocument(ITextEditor editor) {

		/*
		 * Generate id or use a randome one if SHA1 is not implemented.
		 */
		String docId = "";
		try {
			docId = Core.SHA1((new Random()).nextInt()
					+ editor.getEditorInput().getName());
		} catch (NoSuchAlgorithmException e) {

			// If we have no Algorithm we take random numbers

			byte[] buffer = new byte[40];
			(new Random()).nextBytes(buffer);
			docId = Core.convertToHex(buffer);

		}

		setDocumentId(docId);
		setTextEditor(editor);

		owner = Core.getInstance().getLocalUser();
	}

	/**
	 * The filter used for rebeca.
	 */
	private RtceOwnerFilter filter;

	/**
	 * The advertisements used for rebeca.
	 */
	private RtceSupporterFilter advertisement;

	/**
	 * The ChangeCollection that contains all applied changes.
	 */
	private IChangeCollection changeCollection = new ChangeCollection();

	/**
	 * Handles incoming change events ({@link IRemoteEventListener}). In this
	 * case only changes from the supporters are interesting.
	 */
	@Override
	public void change(RtceEvent event) {
		if (event instanceof ChangeEvent) {
			try {
				ChangeEvent ev = (ChangeEvent) event;

				/*
				 * Fire event before the change is applied
				 */
				firePropertyChange(KEY_DOCUMENT_APPLY_MODIFICATION_BEFORE,
						null, ev);

				/*
				 * Apply the modification
				 */
				applyModification(ev.getOffset(), ev.getLength(), ev.getText());

				/*
				 * Fire event after the change if applied
				 */
				firePropertyChange(KEY_DOCUMENT_APPLY_MODIFICATION_AFTER, null,
						ev);

			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Returns the connection phrase for this document
	 * 
	 * @return connection phrase
	 */
	public String getConnectionPhrase() {
		if (ipAdress == null) {
			List<IpAddressDescriptor> addresses = RebecaController
					.getInstance().getAvailableIpAddresses();
			ipAdress = addresses.get(addresses.size());
		}
		InetSocketAddress add = new InetSocketAddress(ipAdress.getInetAddress()
				.getHostAddress(), RebecaController.getInstance()
				.getListenPort());
		return add.getAddress().getHostAddress() + ":" + add.getPort() + "/"
				+ getDocumentId();
	}

	/**
	 * Returns the filename of the current document
	 * 
	 * @return filename
	 */
	@Override
	public String getFileName() {
		return textEditor.getEditorInput().getName();
	}

	/**
	 * Returns the supporter count
	 * 
	 * @return supporter count
	 */
	public int getSupporterCount() {
		return supporters.size();
	}

	/**
	 * Sets a temporary IP address chosen by the user
	 * 
	 * @param address
	 */
	public void setSelectedIpAddress(IpAddressDescriptor address) {
		ipAdress = address;
	}

	/**
	 * Interface {@link IRemoteEventListener}
	 */
	@Override
	public void status(StatusEvent event) {
		if (event.getStatus().equals(StatusEvent.ConnectionRequest)) {

			// add supporter to list if not inside
			boolean inside = false;
			for (User u : supporters) {
				if (u.getId().equals(event.getUserId())) {
					inside = true;
					break;
				}
			}
			if (!inside) {
				User supporter = new User(event.getName(),
						event.getShortName(), event.getUserId());
				supporters.add(supporter);

				// inform listeners
				firePropertyChange(KEY_SUPPORTER_COUNT_CHANGED, supporters
						.size() - 1, supporters.size());
			}

			// get document
			IDocument document = getTextEditor().getDocumentProvider()
					.getDocument(getTextEditor().getEditorInput());

			// send response
			RebecaController.getInstance().publish(
					StatusEvent.getConnectionResponse(getDocumentId(), Core
							.getInstance().getLocalUser(), getFileName(),
							document.get(), globalVersion));

			// send global version number with an empty change
			// publish a ChangeEvent
			User user = Core.getInstance().getLocalUser();
			nextInternalChangeEvent(getDocumentId(), user, 0, 0, "");

		} else if (event.getStatus().equals(StatusEvent.Disconnect)) {

			/*
			 * A supporter disconneced from us. We must delete him from list.
			 */

			for (int i = 0; i < supporters.size(); i++) {
				if (supporters.get(i).getId().equals(event.getUserId())) {
					supporters.remove(i);

					firePropertyChange(KEY_SUPPORTER_COUNT_CHANGED, supporters
							.size() + 1, supporters.size());

					break;
				}
			}

		} else if (event.getStatus().equals(StatusEvent.RefreshRequest)) {

			// get document
			IDocument document = getTextEditor().getDocumentProvider()
					.getDocument(getTextEditor().getEditorInput());
			
			// send refresh
			RebecaController.getInstance().publish(
					StatusEvent.getRefreshResponse(getDocumentId(), Core
							.getInstance().getLocalUser(), document.get(), globalVersion));
		}
	}

	/**
	 * Unshare this document
	 * 
	 * Destroys intern informations and remove the rebeca filter Remove the
	 * event listener for changes
	 */
	public void unshare() {
		// send unshare event
		RebecaController.getInstance().publish(
				StatusEvent.getUnshare(getDocumentId(), Core.getInstance()
						.getLocalUser()));

		// remove document id filter
		RebecaController.getInstance().removeOwnerFilters(getFilter(),
				getAdvertisement());

		// remove rebeca event listener
		RebecaController.getInstance().removeRemoteEventListener(this);

		// remove from list
		Core.getInstance().getSharedDocuments().remove(this);

		// inform listeners
		firePropertyChange(KEY_DOCUMENT_DESTROY, null, null);
	}

	/**
	 * @return the filter
	 */
	public RtceOwnerFilter getFilter() {
		return filter;
	}

	/**
	 * @param filter
	 *            the filter to set
	 */
	public void setFilter(RtceOwnerFilter filter) {
		this.filter = filter;
	}

	/**
	 * @return the advertisement
	 */
	public RtceSupporterFilter getAdvertisement() {
		return advertisement;
	}

	/**
	 * @param advertisement
	 *            the advertisement to set
	 */
	public void setAdvertisement(RtceSupporterFilter advertisement) {
		this.advertisement = advertisement;
	}

	/**
	 * @return the changeCollection
	 */
	public IChangeCollection getChangeCollection() {
		return changeCollection;
	}

}
