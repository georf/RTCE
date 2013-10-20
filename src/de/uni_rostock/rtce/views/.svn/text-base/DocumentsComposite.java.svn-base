package de.uni_rostock.rtce.views;

import java.util.Observable;
import java.util.Observer;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.ViewPart;

import de.uni_rostock.rtce.models.Document;
import de.uni_rostock.rtce.models.DocumentCollection;
import de.uni_rostock.rtce.models.RemoteDocument;
import de.uni_rostock.rtce.models.SharedDocument;

/**
 * Generic Eclipse View to present and modify DocumentCollections
 * 
 * <pre>
 *   |Tab title\    [Button]  <- Button to add new documents to the list
 *   -----------------------
 *   | User 1  Document 1  |  <- One cell for Document table
 *   | User 2  Document 2  |
 *   | User 3  Document 3  |
 *   -----------------------
 *   |           [Unshare] |  <- Another one for the button to remove
 *   -----------------------     list items
 * </pre>
 * 
 * @author Sebastian Gaul <sebastian@dev.mgvmedia.com>
 */
public abstract class DocumentsComposite extends ViewPart implements Observer {

	protected Composite parent;

	/**
	 * JFace table widget to list Documents from DocumentCollections
	 */
	protected TableViewer documentTableViewer;

	/**
	 * Button used to remove a Document from table and related DocumentList
	 */
	protected Button unshareButton;
	
	protected boolean showNumberOfSupporters = false;

	/**
	 * Draws the view
	 */
	public void createPartControl(Composite parent) {

		this.parent = parent;

		/*
		 * Create a simple GridLayout as seen in class description
		 */
		GridLayout viewLayout = new GridLayout();
		viewLayout.numColumns = 1;
		parent.setLayout(viewLayout);

		/*
		 * Append Document table to the view allowing multiple lines and scroll
		 * bars; maximize table size
		 */
		documentTableViewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL);
		documentTableViewer.getControl().setLayoutData(
				new GridData(GridData.FILL_BOTH));
		
		/*
		 * Define columns' titles and widths
		 */
		String[] titles = { "Name", "Supporters", "Document" };
		int[] bounds = { 110, -1, 200 };

		/*
		 * Create first column, set label provider to fill table cell
		 */
		TableViewerColumn col = createTableViewerColumn(titles[0], bounds[0]);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Document d = (Document) element;
				if (d.getOwner() != null) {
					return d.getOwner().getName();
				} else {
					return "";
				}
			}
		});
		
		/*
		 * Create supporter number column, set label provider to fill table cell
		 */
		if (showNumberOfSupporters) {
			col = createTableViewerColumn(titles[1], bounds[1]);
			col.setLabelProvider(new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					if (element instanceof SharedDocument) {
						SharedDocument d = (SharedDocument) element;
						return "" + d.getSupporterCount();
					}
					return "";
				}
			});
		}

		/*
		 * Create file name column, set label provider to fill table cell
		 */
		col = createTableViewerColumn(titles[2], bounds[2]);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Document d = (Document) element;
				return d.getFileName();
			}
		});

		/*
		 * Need some final variables for access inside anonymous classes 
		 */
		final DocumentsComposite instance = this;
		final Table documentTable = documentTableViewer.getTable();
		
		/*
		 * Table display properties: show a header, draw lines betwenn cells
		 */
		documentTable.setHeaderVisible(true);
		documentTable.setLinesVisible(true);
		
		/*
		 * Enable/disable unshare button if at least one/none Document is 
		 * selected
		 */
		documentTable.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent sel) {
				if (instance.documentTableViewer.getTable().getSelectionCount() > 0) {
					instance.unshareButton.setEnabled(true);
				} else {
					instance.unshareButton.setEnabled(false);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// Nothing here...
			}
		});

		/*
		 * Set ArrayContentProvider and initial documents
		 */
		documentTableViewer.setContentProvider(new ArrayContentProvider());
		documentTableViewer.setInput(getInitialDocuments().toArray());

		GridData rightAlignment = new GridData();
		rightAlignment.horizontalAlignment = GridData.END;
		
		/*
		 * Create the button, set text and disable it by default
		 */
		unshareButton = new Button(parent, SWT.PUSH);
		unshareButton.setLayoutData(rightAlignment);
		unshareButton.setText("Unshare selection");
		unshareButton.setEnabled(false);
		
		/*
		 * After the button was clicked, all selected items form the Document
		 * table are removed. We need to distinguish between remote and shared
		 * documents here. 
		 */
		unshareButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				TableItem[] docs = documentTable.getSelection();
				for (TableItem ti : docs) {
					if (ti.getData() instanceof SharedDocument) {
						SharedDocument doc = (SharedDocument) ti.getData();
						doc.unshare();
					} else if (ti.getData() instanceof RemoteDocument) {
						RemoteDocument doc = (RemoteDocument) ti.getData();
						doc.disconnect();
					}
				}

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// Nothing here...
			}
		});

		/*
		 * Child classes can make further changes
		 */
		afterCreation(parent);

	}

	/**
	 * Creates a TableViewerColumn with given title and bound
	 * 
	 * @param title  Title shown at the top of the table
	 * @param bound  Width of the column
	 * @return  A column for a TableViewer
	 */
	private TableViewerColumn createTableViewerColumn(String title, int bound) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(
				documentTableViewer, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		if (bound < 0) {
			column.pack();
		} else {
			column.setWidth(bound);
		}
		column.setResizable(true);
		column.setMoveable(true);
		return viewerColumn;
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		documentTableViewer.getControl().setFocus();
	}
	
	/**
	 * Adapt changes of the underlying models (e.g. the DocumentCollection)
	 */
	@Override
	public void update(Observable o, Object arg) {
		if (DocumentCollection.class.isInstance(o)) {
			DocumentCollection<Document> dc = (DocumentCollection<Document>) o;
			documentTableViewer.setInput(dc.toArray());
		}

	}

	/**
	 * The initial DocumentCollection is determined by subclasses
	 * 
	 * @return  The initial DocumentCollection
	 */
	@SuppressWarnings("rawtypes")
	protected abstract DocumentCollection getInitialDocuments();

	/**
	 * Allows subclasses to realize further changes to the created view
	 * 
	 * @param parent  The Eclipse View Composite
	 */
	protected abstract void afterCreation(Composite parent);

}
