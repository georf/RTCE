package de.uni_rostock.rtce.views;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.RTFTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import de.uni_rostock.rtce.models.SharedDocument;
import de.uni_rostock.rtce.rebeca.IpAddressDescriptor;

/**
 * A dialog that allows users to select an IP and find out the resulting
 * connection phrase for a specific SharedDocument
 * 
 * <pre>
 * ------------------------------------------------------
 * | Share file.java                                  X |
 * ------------------------------------------------------
 * | Select the network you want to...                  |
 * |                                                    |
 * | Sharing Network:   [IP selection combo box]        |
 * |                                                    | 
 * | Description how to copy connection phrases         |
 * |                                                    |
 * | Connection Phrase: [Resulting connection phrase]   |
 * |                                                    |
 * |                       [Cancel] [Copy to Clipboard] |
 * ------------------------------------------------------
 * </pre>
 * 
 * @author Sebastian Gaul <sebastian@dev.mgvmedia.com>
 */
public class ConnectionPhraseForIpDialog extends Dialog {

	/**
	 * The list of available IP addresses
	 */
	private List<IpAddressDescriptor> availableAdresses;

	/**
	 * The document that will be shared and provides the connection phrase
	 */
	private SharedDocument document;

	/**
	 * Combo box that allows users to select an IP
	 */
	private Combo ipSelector;

	/**
	 * Text field showing the current connection phrase
	 */
	private Text connectionPhrase;

	/**
	 * Button that copies the connection phrase to the system's clipboard and
	 * closes the dialog
	 */
	private Button clipboardAndClose;

	/**
	 * ID constant for clipboard and close button
	 * 
	 * @see ConnectionPhraseForIpDialog#clipboardAndClose
	 */
	private final int CLIPBOARD_AND_CLOSE_BUTTON = 910000;

	private static int lastSelectedIp = 0;

	/**
	 * Default constructor
	 * 
	 * @param parentShell
	 *            The calling view's shell
	 * @param availableIpAddresses
	 *            List of available IP addresses to select
	 * @param affectedDocument
	 *            The document that will be shared
	 */
	public ConnectionPhraseForIpDialog(Shell parentShell,
			List<IpAddressDescriptor> availableIpAddresses,
			SharedDocument affectedDocument) {
		super(parentShell);
		availableAdresses = availableIpAddresses;
		document = affectedDocument;
	}

	protected Control createContents(Composite parent) {

		/*
		 * Draw dialog area and button bar
		 * 
		 * CreateContents also calls createDialogArea and
		 * createButtonsForButtonBar, which you can find below.
		 */
		Control composite = super.createContents(parent);

		/*
		 * Add listeners and set default values
		 */
		configureListeners(parent);
		if (lastSelectedIp >= availableAdresses.size()) {
			lastSelectedIp = 0;
		}
		ipSelector.select(lastSelectedIp);
		updateConnectionPhrase(availableAdresses.get(lastSelectedIp));

		return composite;
	}

	/**
	 * Writes the dialog's content
	 */
	protected Control createDialogArea(Composite parent) {

		/*
		 * The dialog's window title
		 */
		parent.getShell().setText("Share " + document.getFileName());

		/*
		 * Define the overall layout here
		 */
		Composite composite = (Composite) super.createDialogArea(parent);
		GridLayout threeCols = new GridLayout(3, false);
		threeCols.marginTop = 5;
		threeCols.marginLeft = 5;
		threeCols.marginRight = 5;
		composite.setLayout(threeCols);

		/*
		 * Set a maximum width for the dialog's content
		 */
		GridData widthLimit = new GridData();
		widthLimit.widthHint = 500;
		composite.setLayoutData(widthLimit);

		/*
		 * We'll need some column span properties
		 */
		GridData colSpan3 = new GridData(GridData.FILL_HORIZONTAL);
		colSpan3.horizontalSpan = 3;

		GridData colSpan2 = new GridData(GridData.FILL_HORIZONTAL);
		colSpan2.horizontalSpan = 2;

		/*
		 * The introducing text
		 */
		Label introduction = new Label(composite, SWT.None | SWT.WRAP);
		introduction.setLayoutData(GridDataFactory.copyData(colSpan3));
		introduction
				.setText("Select the network you want to use for the connection first:");

		/*
		 * IP selection combox box and its label
		 * 
		 * The read-only constant SWT.READ_ONLY enables a real select box
		 * without an integrated text input field.
		 */
		Label ipSelectorLabel = new Label(composite, SWT.None);
		ipSelectorLabel.setText("Sharing network:");
		ipSelector = new Combo(composite, SWT.READ_ONLY);
		ipSelector.setLayoutData(colSpan2);
		for (IpAddressDescriptor ip : availableAdresses) {
			ipSelector.add(ip.getFriendlyName());
		}

		/*
		 * Short description how clipboards work
		 */
		Label textBetween = new Label(composite, SWT.None | SWT.WRAP);
		textBetween.setLayoutData(GridDataFactory.copyData(colSpan3));
		textBetween
				.setText("You can copy the upcoming phrase to your clipboard. Use Ctrl+v (Windows, most Unix-/Linux-based systems) or the appropriate paste command of your system to insert it into an email, instant message or something similiar and send it to your sharing partner.");

		/*
		 * The connection phrase field and its label
		 */
		Label connectionPhraseLabel = new Label(composite, SWT.None);
		connectionPhraseLabel.setText("Connection phrase:");

		connectionPhrase = new Text(composite, SWT.SINGLE | SWT.BORDER);
		connectionPhrase.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		connectionPhrase.setEditable(false);

		return composite;
	}

	/**
	 * Creates the button bar at the bottom of the dialog
	 */
	protected void createButtonsForButtonBar(Composite parent) {
		/*
		 * The copy to clipboard button
		 */
		clipboardAndClose = createButton(parent, CLIPBOARD_AND_CLOSE_BUTTON,
				"Copy to Clipboard", true);

		createButton(parent, CANCEL, "Cancel", false);
	}

	/**
	 * Configures widget listeners used in the dialog
	 * 
	 * <ul>
	 * <li>IP select box changes document settings and updates the connection
	 * phrase widget
	 * <li>Clipboard button copies current connection phrase to system's
	 * clipboard
	 * </ul>
	 * 
	 * @param composite
	 */
	private void configureListeners(final Composite composite) {
		ipSelector.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent sel) {
				IpAddressDescriptor selectedAdress = availableAdresses
						.get(ipSelector.getSelectionIndex());
				updateConnectionPhrase(selectedAdress);
				lastSelectedIp = ipSelector.getSelectionIndex();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// Nothing here...
			}
		});

		clipboardAndClose.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				copyConnectionPhraseToClipboard(composite);
				getShell().close();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// Nothing here...
			}

		});
	}

	/**
	 * Updates the document after IP selection and updates the connection phrase
	 * output
	 * 
	 * @param address
	 */
	private void updateConnectionPhrase(IpAddressDescriptor address) {
		document.setSelectedIpAddress(address);
		connectionPhrase.setText(document.getConnectionPhrase());
	}

	/**
	 * Writes the document's current connection phrase to the System's clipboard
	 * 
	 * <p>
	 * Code is based on <a href=
	 * "http://www.java2s.com/Code/Java/SWT-JFace-Eclipse/CopyandPaste.htm"
	 * >&quot;Copy and Paste: Clipboard&quot; from java2s.com</a>.
	 * 
	 * @param composite
	 */
	private void copyConnectionPhraseToClipboard(final Composite composite) {
		Clipboard clipboard = new Clipboard(composite.getDisplay());
		String plainText = document.getConnectionPhrase();
		String rtfText = "{\\rtf1\\b " + plainText + "}";
		TextTransfer textTransfer = TextTransfer.getInstance();
		RTFTransfer rftTransfer = RTFTransfer.getInstance();
		clipboard.setContents(new String[] { plainText, rtfText },
				new Transfer[] { textTransfer, rftTransfer });
		clipboard.dispose();
	}
}
