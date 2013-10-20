package de.uni_rostock.rtce.views;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;

import de.uni_rostock.rtce.Core;
import de.uni_rostock.rtce.models.DocumentCollection;

/**
 * Extends a DocumentsComposite to present a list of documents released for
 * sharing
 * 
 * @author Sebastian Gaul <sebastian@dev.mgvmedia.com>
 * 
 */
public class SharedDocumentsComposite extends DocumentsComposite {

	/**
	 * Button to show the currently edited document's connection phrase
	 */
	protected Action shareButton;

	/**
	 * The constructor
	 */
	public SharedDocumentsComposite() {
		showNumberOfSupporters = true;
		Core.getInstance().getSharedDocuments().addObserver(this);
	}

	/**
	 * Returns all SharedDocuments to initiate the view's table
	 */
	@Override
	protected DocumentCollection getInitialDocuments() {
		return Core.getInstance().getSharedDocuments();
	}

	/**
	 * Adds a button to show the currently edited document's connection phrase
	 */
	@Override
	protected void afterCreation(final Composite parent) {
		shareButton = new Action() {
			@Override
			public void run() {
				Core.getInstance().getUserActionController()
						.executeAddSharedDocument(null);
			}
		};
		shareButton.setText("Share Button");
		shareButton.setImageDescriptor(Core
				.getImageDescriptor("icons/add_shared.png"));
		shareButton.setEnabled(false);
		Core.getInstance().getUserActionController().addAddSharedAction(shareButton);

		IActionBars bars = getViewSite().getActionBars();
		bars.getToolBarManager().add(shareButton);
	}

}