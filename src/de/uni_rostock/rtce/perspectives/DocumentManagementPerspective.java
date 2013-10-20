package de.uni_rostock.rtce.perspectives;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * Implements a view highlighting RTCE's views
 * 
 * <p>
 * The perspective's views are arranged as followed:
 * </p>
 * 
 * <pre>
 * -----------------------------------
 * |Shared Docs     |Editor          |
 * |                |                |
 * |                |                |
 * -----------------------------------
 * |Remote Docs     |Chat + Log      |
 * |                |                |
 * |                |                |
 * -----------------------------------
 * </pre>
 * 
 * @author Sebastian Gaul <sebastian@dev.mgvmedia.com>
 */
public class DocumentManagementPerspective implements IPerspectiveFactory {

	private IPageLayout factory;

	private final Float LEFT_PANEL_WIDTH = 0.4f;
	private final Float BOTTOM_PANEL_HEIGHT = 0.5f;

	public final String LOG_VIEW_ID = "de.uni_rostock.rtce.log.LogView";
	public final String SHARED_DOCUMENTS_VIEW_ID = "de.uni_rostock.rtce.views.SharedDocumentsComposite";
	public final String REMOTE_DOCUMENTS_VIEW_ID = "de.uni_rostock.rtce.views.RemoteDocumentsComposite";

	public DocumentManagementPerspective() {
		super();
	}

	/**
	 * The init method called by Eclipse
	 */
	public void createInitialLayout(IPageLayout factory) {
		this.factory = factory;
		addViews();
		addViewShortcuts();
	}

	/**
	 * Arrange views as mentioned in class' description
	 * 
	 * @see DocumentManagementPerspective
	 */
	private void addViews() {

		IFolderLayout left = factory.createFolder("left", // NON-NLS-1
				IPageLayout.LEFT, LEFT_PANEL_WIDTH, factory.getEditorArea());
		left.addView(SHARED_DOCUMENTS_VIEW_ID);

		IFolderLayout right = factory.createFolder("right", // NON-NLS-1
				IPageLayout.BOTTOM, 0.5f, "left");
		right.addView(REMOTE_DOCUMENTS_VIEW_ID);

		IFolderLayout bottom = factory.createFolder(
				"bottom", // NON-NLS-1
				IPageLayout.BOTTOM, 1 - BOTTOM_PANEL_HEIGHT,
				factory.getEditorArea());
		bottom.addView(LOG_VIEW_ID);
	}

	/**
	 * Determines important views for this perspectives
	 * 
	 * <p>
	 * Views mentioned here can be selected from menu bar's item Window &gt;
	 * Show View directly.
	 */
	private void addViewShortcuts() {
		factory.addShowViewShortcut(SHARED_DOCUMENTS_VIEW_ID);
		factory.addShowViewShortcut(REMOTE_DOCUMENTS_VIEW_ID);
		factory.addShowViewShortcut(LOG_VIEW_ID);
	}

}
