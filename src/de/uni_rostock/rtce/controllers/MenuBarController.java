package de.uni_rostock.rtce.controllers;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import de.uni_rostock.rtce.Core;

/**
 * Controls the RTCE's menu and icon bar entries
 * 
 * @see IWorkbenchWindowActionDelegate
 * @author Sebastian Gaul <sebastian@dev.mgvmedia.com>
 */
public class MenuBarController implements IWorkbenchWindowActionDelegate {

	private final String ADD_SHARED_ID = "de.uni_rostock.rtce.menu.add_shared";
	private final String REMOVE_SHARED_ID = "de.uni_rostock.rtce.menu.remove_shared";
	private final String ADD_REMOTE_ID = "de.uni_rostock.rtce.menu.add_remote";
	private final String REMOVE_REMOTE_ID = "de.uni_rostock.rtce.menu.remove_remote";

	private boolean actionsPassed = false;

	/**
	 * The constructor.
	 */
	public MenuBarController() {
	}

	/**
	 * The action has been activated. The argument of the method represents the
	 * 'real' action sitting in the workbench UI.
	 * 
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action) {
		UserActionController uac = Core.getInstance().getUserActionController();
		if (action.getId().equals(ADD_SHARED_ID)) {
			uac.executeAddSharedDocument(null);
		} else if (action.getId().equals(REMOVE_SHARED_ID)) {
			uac.executeRemoveSharedDocument(null);
		} else if (action.getId().equals(ADD_REMOTE_ID)) {
			uac.executeAddRemoteDocument(null);
		} else if (action.getId().equals(REMOVE_REMOTE_ID)) {
			uac.executeRemoveRemoteDocument(null);
		}
	}

	/**
	 * Selection in the workbench has been changed. We can change the state of
	 * the 'real' action here if we want, but this can only happen after the
	 * delegate has been created.
	 * 
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	public void selectionChanged(IAction action, ISelection selection) {

		if (actionsPassed)
			return;

		UserActionController uac = Core.getInstance().getUserActionController();

		if (action.getId().equals(ADD_SHARED_ID)) {
			uac.addAddSharedAction(action);
		} else if (action.getId().equals(REMOVE_SHARED_ID)) {
			uac.addRemoveSharedAction(action);
		} else if (action.getId().equals(ADD_REMOTE_ID)) {
			uac.addAddRemoteAction(action);
		} else if (action.getId().equals(REMOVE_REMOTE_ID)) {
			uac.addRemoveRemoteAction(action);
		}

		actionsPassed = true;
	}

	/**
	 * We can use this method to dispose of any system resources we previously
	 * allocated.
	 * 
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose() {
	}

	/**
	 * We will cache window object in order to be able to provide parent shell
	 * for the message dialog.
	 * 
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	public void init(IWorkbenchWindow window) {
	}

}