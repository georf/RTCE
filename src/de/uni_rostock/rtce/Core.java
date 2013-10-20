package de.uni_rostock.rtce;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.eclipse.core.runtime.internal.adaptor.EclipseEnvironmentInfo;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.texteditor.ITextEditor;
import org.osgi.framework.BundleContext;

import de.uni_rostock.rtce.controllers.UserActionController;
import de.uni_rostock.rtce.models.Document;
import de.uni_rostock.rtce.models.DocumentCollection;
import de.uni_rostock.rtce.models.EditorState;
import de.uni_rostock.rtce.models.RemoteDocument;
import de.uni_rostock.rtce.models.SharedDocument;
import de.uni_rostock.rtce.models.User;
import de.uni_rostock.rtce.preferences.PreferenceConstants;

/**
 * The activator class controls the plug-in life cycle
 * 
 * @author Georg Limbach <georf@dev.mgvmedia.com>
 */
public class Core extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "de.uni-rostock.RTCE";

	// The shared instance
	private static Core plugin;

	private User localUser;

	protected DocumentCollection<RemoteDocument> remoteDocuments = new DocumentCollection<RemoteDocument>();
	protected DocumentCollection<SharedDocument> sharedDocuments = new DocumentCollection<SharedDocument>();

	private EditorState editorState = new EditorState();

	/**
	 * Manages and executes user actions like share/unshare/connect/disconnect
	 */
	private UserActionController userActionController;

	/**
	 * The constructor
	 */
	public Core() {
		// use start();
	}

	public static Core getInstance() {
		return plugin;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		localUser = new User(getPreferenceStore().getString(
				PreferenceConstants.USER_NAME), getPreferenceStore().getString(
				PreferenceConstants.SHORT_USER_NAME));
		userActionController = new UserActionController(editorState);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Core getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	public User getLocalUser() {
		return localUser;
	}

	public EditorState getEditorState() {
		return editorState;
	}

	public DocumentCollection<SharedDocument> getSharedDocuments() {
		return sharedDocuments;
	}

	public DocumentCollection<RemoteDocument> getRemoteDocuments() {
		return remoteDocuments;
	}

	/**
	 * @see Core#userActionController
	 * @return the userActionController
	 */
	public UserActionController getUserActionController() {
		return userActionController;
	}

	/**
	 * @see Core#userActionController
	 * @param userActionController
	 *            the userActionController to set
	 */
	public void setUserActionController(
			UserActionController userActionController) {
		this.userActionController = userActionController;
	}

	/**
	 * If the current editor shows a document, this method returns this
	 * document. Otherwise returns null.
	 * 
	 * @return current document or null
	 */
	public Document getCurrentDocument() {
		IEditorPart editorPart = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getActiveEditor();

		// if current editor is a texteditor
		if (editorPart instanceof ITextEditor) {

			DocumentCollection<SharedDocument> sdocs = Core.getInstance()
					.getSharedDocuments();

			// if current editor is shared we will unshare
			if (sdocs.contains((ITextEditor) editorPart)) {

				SharedDocument shared = (SharedDocument) sdocs
						.getByTextEditor((ITextEditor) editorPart);
				return shared;
			}

			DocumentCollection<RemoteDocument> rdocs = Core.getInstance()
					.getRemoteDocuments();

			// if current editor is shared we will unshare
			if (rdocs.contains((ITextEditor) editorPart)) {

				RemoteDocument shared = (RemoteDocument) rdocs
						.getByTextEditor((ITextEditor) editorPart);
				return shared;
			}
		}
		return null;
	}

	/**
	 * Convert a given byte array to hex string
	 * 
	 * @param data
	 * @return hex
	 */
	public static String convertToHex(byte[] data) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			int halfbyte = (data[i] >>> 4) & 0x0F;
			int two_halfs = 0;
			do {
				if ((0 <= halfbyte) && (halfbyte <= 9))
					buf.append((char) ('0' + halfbyte));
				else
					buf.append((char) ('a' + (halfbyte - 10)));
				halfbyte = data[i] & 0x0F;
			} while (two_halfs++ < 1);
		}
		return buf.toString();
	}

	/**
	 * Generate the sha1 value of given text.
	 * 
	 * @param text
	 * @return string of hex
	 * @throws NoSuchAlgorithmException
	 */
	public static String SHA1(String text) throws NoSuchAlgorithmException {
		MessageDigest md;
		md = MessageDigest.getInstance("SHA-1");
		byte[] sha1hash = new byte[40];
		md.update(text.getBytes(), 0, text.length());
		sha1hash = md.digest();
		return convertToHex(sha1hash);
	}

	/**
	 * Returns the currently displayed ITextEditor instance
	 * 
	 * @return null if is not a text editor
	 */
	public ITextEditor getCurrentTextEditor() {
		IWorkbench workbench = PlatformUI.getWorkbench();
		if (workbench == null)
			return null;

		IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
		if (workbenchWindow == null)
			return null;

		IWorkbenchPage activePage = workbenchWindow.getActivePage();
		if (activePage == null)
			return null;

		IWorkbenchPart editorPart = activePage.getActiveEditor();
		if (editorPart == null)
			return null;

		if (!(editorPart instanceof ITextEditor))
			return null;

		return (ITextEditor) editorPart;
	}
}
