package de.uni_rostock.rtce.controllers;

import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.widgets.Shell;

import de.uni_rostock.rtce.Core;
import de.uni_rostock.rtce.models.Document;
import de.uni_rostock.rtce.models.EditorState;
import de.uni_rostock.rtce.models.RemoteDocument;
import de.uni_rostock.rtce.models.SharedDocument;
import de.uni_rostock.rtce.rebeca.ConnectionException;
import de.uni_rostock.rtce.rebeca.RebecaController;
import de.uni_rostock.rtce.views.ConnectionPhraseForIpDialog;

/**
 * Manages and executes user actions like share and connecet to remote documents
 * 
 * <p>
 * Managing includes enabling and disabling specific actions depending on
 * editor's current state. Therefore EditorState is observed.
 * 
 * @author Sebastian Gaul <sebastian@dev.mgvmedia.com>
 */
public class UserActionController implements Observer {

	/**
	 * Share document action from menu bar's collaboration action set
	 * 
	 * Use it to disable related menu items including toolbar icons
	 */
	private List<IAction> addSharedAction = new LinkedList<IAction>();

	/**
	 * Unshare document action from menu bar's collaboration action set
	 * 
	 * Use it to disable related menu items including toolbar icons
	 */
	private List<IAction> removeSharedAction = new LinkedList<IAction>();

	/**
	 * Connect to remote document action from menu bar's collaboration action
	 * set
	 * 
	 * Use it to disable related menu items including toolbar icons
	 */
	private List<IAction> addRemoteAction = new LinkedList<IAction>();

	/**
	 * Disconnect from remote document action from menu bar's collaboration
	 * action set
	 * 
	 * Use it to disable related menu items including toolbar icons
	 */
	private List<IAction> removeRemoteAction = new LinkedList<IAction>();

	private static boolean failed;

	private static String lastPhrase;

	/**
	 * Create instance and start observing Core's EditorState
	 */
	public UserActionController(EditorState observable) {
		observable.addObserver(this);
	}

	/**
	 * @see UserActionController#addSharedAction
	 * @param addShareAction
	 *            the addShareAction to set
	 */
	public void addAddSharedAction(IAction addShareAction) {		
		this.addSharedAction.add(addShareAction);
		update(null, null);
	}

	/**
	 * @see UserActionController#removeSharedAction
	 * @param removeShareAction
	 *            the removeShareAction to set
	 */
	public void addRemoveSharedAction(IAction removeShareAction) {
		this.removeSharedAction.add(removeShareAction);
		update(null, null);
	}

	/**
	 * @see UserActionController#addRemoteAction
	 * @param addRemoteAction
	 *            the addRemoteAction to set
	 */
	public void addAddRemoteAction(IAction addRemoteAction) {
		this.addRemoteAction.add(addRemoteAction);
		update(null, null);
	}

	/**
	 * @see UserActionController#removeRemoteAction
	 * @param removeRemoteAction
	 *            the removeRemoteAction to set
	 */
	public void addRemoveRemoteAction(IAction removeRemoteAction) {
		this.removeRemoteAction.add(removeRemoteAction);
		update(null, null);
	}

	/**
	 * Enables/disables user actions depending on current EditorState
	 */
	@Override
	public void update(Observable o, Object arg) {
		EditorState es = Core.getInstance().getEditorState();

		if (es.isShareable()) {
			setAddSharedStatus(true);
		} else {
			setAddSharedStatus(false);
		}

		if (es.isShared()) {
			setRemoveSharedStatus(true);
		} else {
			setRemoveSharedStatus(false);
		}

		if (es.isRemote()) {
			setRemoveRemoteStatus(true);
		} else {
			setRemoveRemoteStatus(false);
		}

	}

	/**
	 * Enables/disables specific actions
	 * 
	 * @param status
	 *            Set true to enable action
	 */
	public void setAddSharedStatus(boolean status) {
		for (IAction action : addSharedAction) {
			action.setEnabled(status);
		}
	}

	/**
	 * Enables/disables specific actions
	 * 
	 * @param status
	 *            Set true to enable action
	 */
	public void setRemoveSharedStatus(boolean status) {
		for (IAction action : removeSharedAction) {
			action.setEnabled(status);
		}
	}

	/**
	 * Enables/disables specific actions
	 * 
	 * @param status
	 *            Set true to enable action
	 */
	public void setAddRemoteStatus(boolean status) {
		for (IAction action : addRemoteAction) {
			action.setEnabled(status);
		}
	}

	/**
	 * Enables/disables specific actions
	 * 
	 * @param status
	 *            Set true to enable action
	 */
	public void setRemoveRemoteStatus(boolean status) {
		for (IAction action : removeRemoteAction) {
			action.setEnabled(status);
		}
	}

	/**
	 * Callback interface for user actions
	 * 
	 * @author Sebastian Gaul <sebastian@dev.mgvmedia.com>
	 */
	public interface IUserActionResultHandler {

		public void onSuccess();

		public void onError(Exception exception);

	}

	/**
	 * Tries to share currently selected editor
	 * 
	 * @param callback
	 *            Set null if no callback is necessary
	 */
	public void executeAddSharedDocument(IUserActionResultHandler callback) {
		/*
		 * SharedDocuments open a dialog after this button was clicked. Inside,
		 * the user can select an IP address and find out the related connection
		 * phrase for the document.
		 */
		Core.getInstance().getEditorState().share();
		Document document = Core.getInstance().getCurrentDocument();
		if (document != null && document instanceof SharedDocument) {
			ConnectionPhraseForIpDialog dialog = new ConnectionPhraseForIpDialog(
					Core.getInstance().getWorkbench()
							.getActiveWorkbenchWindow().getShell(),
					RebecaController.getInstance().getAvailableIpAddresses(),
					(SharedDocument) document);
			dialog.open();
			if (callback != null) {
				callback.onSuccess();
			}
		} else if (callback != null) {
			callback.onError(new Exception("Current editor is not shareable."));
		}
	}
	
	/**
	 * Revokes sharing of currently selected editor
	 * 
	 * @param callback Set null if no callback is necessary
	 */
	public void executeRemoveSharedDocument(IUserActionResultHandler callback) {
		Document document = Core.getInstance().getCurrentDocument();
		if (document != null && document instanceof SharedDocument) {
			((SharedDocument) document).unshare();
			if (callback != null) {
				callback.onSuccess();
			}
		} else {
			if (callback != null) {
				callback.onError(new Exception("Selected document is not shared."));
			}	
		}	
	}
	
	/**
	 * Opens a new remote document
	 * 
	 * @param callback Set null if no callback is necessary
	 */
	public void executeAddRemoteDocument(IUserActionResultHandler callback) {
		Shell shell = Core.getInstance().getWorkbench().getActiveWorkbenchWindow().getShell();
		String mess = "Please enter the connection phrase of the desired remote document.";
		String value = "";
		Clipboard clipboard = new Clipboard(shell.getDisplay());
		value = (String) clipboard.getContents(TextTransfer.getInstance());
		if (failed) {
			mess = "Unable to establish connection to remote docment. Please check the given connection phrase.";
			value = lastPhrase;
		}
		InputDialog dialog = new InputDialog(shell,
				"Connect to remote document", mess, value, null);
		int status = dialog.open();
		if (status == Dialog.OK) {
			try {
				new TextEditorController(dialog.getValue());
				failed = false;
				lastPhrase = "";
				if (callback != null) {
					callback.onSuccess();
				}
			} catch (ConnectionException e) {
				failed = true;
				lastPhrase = dialog.getValue();
				executeAddRemoteDocument(callback);
			}
		} else if (status == Dialog.CANCEL) {
			failed = false;
			lastPhrase = "";
			if (callback != null) {
				callback.onError(new Exception("Action canceld by user."));
			}
		}
		if (callback != null) {
			callback.onError(new Exception("Action failed for unknown reason."));
		}
	}
	

	/**
	 * Closes currently selected remote document
	 * 
	 * @param callback Set null if no callback is necessary
	 */
	public void executeRemoveRemoteDocument(IUserActionResultHandler callback) {
		Document document = Core.getInstance().getCurrentDocument();
		if (document != null && document instanceof RemoteDocument) {
			((RemoteDocument) document).disconnect();
		} else {
			if (callback != null) {
				callback.onError(new Exception("No remote document selected."));
			}
		}
	}

}
