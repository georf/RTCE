package de.uni_rostock.rtce.models;

import java.util.Observable;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;

import de.uni_rostock.rtce.Core;
import de.uni_rostock.rtce.controllers.TextEditorController;

public class EditorState extends Observable implements IWindowListener,
		IPartListener, IPageListener, IPropertyChangeListener {
	public enum State {
		Shareable, Unshareable, Disabled
	};

	protected State state = State.Disabled;

	public EditorState() {
		super();

		/**
		 * While the plugin is loading the windows aren't created, so we must
		 * wait for an event
		 */
		PlatformUI.getWorkbench().addWindowListener(this);
		for (IWorkbenchWindow w : PlatformUI.getWorkbench()
				.getWorkbenchWindows()) {
			w.addPageListener(this);
		}
	}

	/**
	 * Returns true if the current shown texteditor displays a
	 * {@link RemoteDocument}
	 * 
	 * @return false otherwise
	 */
	public boolean isRemote() {
		ITextEditor editor = Core.getInstance().getCurrentTextEditor();
		if (editor == null)
			return false;

		return (null != Core.getInstance().getRemoteDocuments()
				.getByTextEditor(editor));
	}

	/**
	 * Returns true if the current shown texteditor displays a
	 * {@link SharedDocument}
	 * 
	 * @return false otherwise
	 */
	public boolean isShared() {
		ITextEditor editor = Core.getInstance().getCurrentTextEditor();
		if (editor == null)
			return false;

		return (null != Core.getInstance().getSharedDocuments()
				.getByTextEditor(editor));
	}

	/**
	 * Returns true, if the current editor is shareble
	 * 
	 * @return false otherwise
	 */
	public boolean isShareable() {
		ITextEditor editor = Core.getInstance().getCurrentTextEditor();
		return (editor != null);
	}

	/**
	 * Returns the current state of the shown editor
	 * 
	 * @return
	 */
	public State getState() {
		return state;
	}

	/**
	 * Sets the current editor state. Notify all observers.
	 * 
	 * @param state
	 *            State to set
	 */
	protected void setState(State state) {
		this.state = state;
		setChanged();
		notifyObservers();
	}

	/**
	 * Implements pageListener
	 * 
	 * We don't use it.
	 */
	@Override
	public void pageActivated(IWorkbenchPage page) {
	}

	/**
	 * Implements pageListener
	 * 
	 * We don't use it.
	 */
	@Override
	public void pageClosed(IWorkbenchPage page) {
	}

	/**
	 * Implements pageListener
	 * 
	 * We don't use it.
	 */
	@Override
	public void pageOpened(IWorkbenchPage page) {
		page.addPartListener(this);
	}

	/**
	 * Implements windowListener
	 * 
	 * We don't use it.
	 */
	@Override
	public void windowActivated(IWorkbenchWindow window) {
	}

	/**
	 * Implements windowListener
	 * 
	 * We don't use it.
	 */
	@Override
	public void windowClosed(IWorkbenchWindow window) {
	}

	/**
	 * Implements windowListener
	 * 
	 * We don't use it.
	 */
	@Override
	public void windowDeactivated(IWorkbenchWindow window) {
	}

	/**
	 * Implements partListener
	 * 
	 * We use it only to add listeners.
	 */
	@Override
	public void windowOpened(IWorkbenchWindow window) {
		for (IWorkbenchPage p : window.getPages()) {
			p.addPartListener(this);
		}
		window.addPageListener(this);
	}

	/**
	 * Implements partListener
	 * 
	 * We use it to toggle the current state.
	 */
	@Override
	public void partActivated(IWorkbenchPart part) {

		if (part instanceof IEditorPart) {
			recalculate(part);
		}
	}

	protected void recalculate(IWorkbenchPart part) {
		if (part == null) {
			part = Core.getInstance().getCurrentTextEditor();
		}
		
		if (part != null && part instanceof ITextEditor) {
			if (Core.getInstance().getSharedDocuments()
					.getByTextEditor((ITextEditor) part) != null) {
				setState(State.Unshareable);
			} else {
				setState(State.Shareable);
			}
		} else {
			// Other editor
			setState(State.Disabled);
		}
	}
	
	/**
	 * Implements partListener
	 * 
	 * We don't use it.
	 */
	@Override
	public void partBroughtToTop(IWorkbenchPart part) {
		recalculate(null);
	}

	/**
	 * Implements partListener
	 * 
	 * We use it to unshare or disconnect clients.
	 */
	@Override
	public void partClosed(IWorkbenchPart part) {
		if (part instanceof ITextEditor) {
			Core plugin = Core.getInstance();

			RemoteDocument rdoc = (RemoteDocument) plugin.getRemoteDocuments()
					.getByTextEditor((ITextEditor) part);

			if (rdoc != null) {
				rdoc.disconnect();
			}

			SharedDocument sdoc = (SharedDocument) plugin.getSharedDocuments()
					.getByTextEditor((ITextEditor) part);

			if (sdoc != null) {
				sdoc.unshare();
			}
		}
		recalculate(null);
	}

	/**
	 * Implements partListener
	 * 
	 * We don't use it.
	 */
	@Override
	public void partDeactivated(IWorkbenchPart part) {
		recalculate(null);
	}

	/**
	 * Implements partListener
	 * 
	 * We don't use it.
	 */
	@Override
	public void partOpened(IWorkbenchPart part) {
		recalculate(null);
	}

	
	
	public void share() {
		ITextEditor editor = Core.getInstance().getCurrentTextEditor();
		
		if (editor == null)
			return;
		
		if (Core.getInstance().getSharedDocuments().getByTextEditor(editor) != null)
			return;

		new TextEditorController(editor);
		setState(State.Unshareable);

	}


	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(Document.KEY_DOCUMENT_DESTROY)) {
			partActivated(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor());
		}		
	}
}
