package de.uni_rostock.rtce.log;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import de.uni_rostock.rtce.Core;
import de.uni_rostock.rtce.rebeca.RebecaController;
import de.uni_rostock.rtce.rebeca.events.ChatEvent;

/**
 * Eclipse View to present log elements and generate chat messages
 * 
 * @author Sebastian Gaul <sebastian@dev.mgvmedia.com>
 * @author Georg Limbach <georf@dev.mgvmedia.com>
 */
public class LogView extends ViewPart implements Observer {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "de.uni_rostock.rtce.log.LogView";

	private ListViewer viewer;

	private int logLength = 50;

	public LogView() {
		super();
	}

	public void init(IViewSite site) throws PartInitException {
		super.init(site);
		Log.getInstance().addObserver(this);
	}

	public void createPartControl(Composite parent) {

		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		parent.setLayout(layout);

		viewer = new ListViewer(parent, GridData.FILL_BOTH);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		viewer.getControl().setLayoutData(gd);
		viewer.setContentProvider(new ArrayContentProvider());
		update(Log.getInstance(), null);

		Label chatLabel = new Label(parent, SWT.NONE);
		chatLabel.setText("Chat: ");
		final Text chat = new Text(parent, SWT.BORDER);
		chat.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		chat.addListener(SWT.Traverse, new Listener() {
			@Override
			public void handleEvent(Event arg) {
				if (arg.detail == SWT.TRAVERSE_RETURN
						&& chat.getText().length() > 0) {

					String text = chat.getText(0, chat.getText().length());

					chat.setText("");

					Log.log(new ChatMessage(text));

					// send message
					RebecaController.getInstance().publish(
							new ChatEvent(Core.getInstance().getLocalUser(),
									text));
				}
			}
		});
	}

	public void setFocus() {
		viewer.getControl().setFocus();
	}

	public void dispose() {
		super.dispose();
	}

	@Override
	public synchronized void update(Observable o, Object arg) {
		if (Log.class.isInstance(o)) {
			Log log = (Log) o;
			List<String> input = log.getLatestStrings();
			if (input.size() > logLength) {
				input = input.subList(input.size() - logLength, input.size());
			}
			viewer.setInput(input);
			if (input.size() > 0) {
				viewer.reveal(input.get(input.size() - 1));
			}
		}
	}
}
