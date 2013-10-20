package de.uni_rostock.rtce.views;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;

import de.uni_rostock.rtce.Core;
import de.uni_rostock.rtce.controllers.UserActionController;
import de.uni_rostock.rtce.models.DocumentCollection;

/**
 * Extends a DocumentsComposite to present remote documents, i.e. documents
 * that are provided by a sharing partner and not stored locally 
 * 
 * @see DocumentsComposite
 * @author Sebastian Gaul <sebastian@dev.mgvmedia.com>
 */
public class RemoteDocumentsComposite extends DocumentsComposite {

	protected Action addRemoteDocumentButton;

	/**
	 * The constructor
	 */
	public RemoteDocumentsComposite() {
		Core.getInstance().getRemoteDocuments().addObserver(this);
	}

	/**
	 * Returns all RemoteDocuments to initiate the view's table
	 */
	@Override
	protected DocumentCollection getInitialDocuments() {
		return Core.getInstance().getRemoteDocuments();
	}

	/**
	 * Adds a button to add new remote documents
	 */
	@Override
	protected void afterCreation(final Composite parent) {
		
		unshareButton.setText("Disconnect selection");

		addRemoteDocumentButton = new Action() {
			@Override
			public void run() {
				UserActionController uac = Core.getInstance().getUserActionController();
				uac.executeAddRemoteDocument(null);
			}
		};
		addRemoteDocumentButton.setText("Connect to remote document");
		addRemoteDocumentButton.setImageDescriptor(Core
				.getImageDescriptor("icons/add_remote.png"));
		addRemoteDocumentButton.setEnabled(true);

		IActionBars bars = getViewSite().getActionBars();
		bars.getToolBarManager().add(addRemoteDocumentButton);
	}
}