package de.uni_rostock.rtce.preferences;

import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
import de.uni_rostock.rtce.Core;
import de.uni_rostock.rtce.rebeca.IpAddressDescriptor;
import de.uni_rostock.rtce.rebeca.RebecaController;

/**
 * RTCE's preferences page
 * 
 * @author Sebastian Gaul <sebastian@dev.mgvmedia.com>
 * 
 */
public class RtcePreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {

	private StringFieldEditor nameField;
	private StringFieldEditor shortNameField;
	private ComboFieldEditor ipSelectionField;

	/**
	 * Constructs preference page
	 */
	public RtcePreferencePage() {
		setPreferenceStore(Core.getDefault().getPreferenceStore());
		setDescription("Your settings for collaborative work (Real-Time Collaboration for Eclipse)");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

	/**
	 * Adds required preference fields to the page
	 */
	@Override
	protected Control createContents(Composite parent) {
		Composite top = new Composite(parent, SWT.LEFT);

		// Sets the layout data for the top composite's
		// place in its parent's layout.
		top.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// Sets the layout for the top composite's
		// children to populate.
		top.setLayout(new GridLayout());

		Group personal = new Group(parent, SWT.NULL);
		personal.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		personal.setText("Information about Yourself");

		nameField = new StringFieldEditor(PreferenceConstants.USER_NAME,
				"Your name:", personal);
		nameField.setEmptyStringAllowed(false);
		nameField.setPreferenceStore(getPreferenceStore());
		nameField.load();

		shortNameField = new StringFieldEditor(
				PreferenceConstants.SHORT_USER_NAME, "Your name (short):", 5,
				personal);
		shortNameField.setEmptyStringAllowed(false);
		shortNameField.setTextLimit(5);
		shortNameField.setPreferenceStore(getPreferenceStore());
		shortNameField.load();

		Group network = new Group(parent, SWT.NULL);
		network.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		network.setText("Network Settings");

		List<IpAddressDescriptor> availableIpAddresses = RebecaController
				.getInstance().getAvailableIpAddresses();
		List<String[]> entryList = new LinkedList<String[]>();
		for (int i = 0; i < availableIpAddresses.size(); i++) {
			if (availableIpAddresses.get(i) == null
					|| availableIpAddresses.get(i).getInetAddress() == null
					|| availableIpAddresses.get(i).getInetAddress()
							.getHostAddress() == null)
				continue;
			String[] content = new String[2];
			content[0] = availableIpAddresses.get(i).getFriendlyName();
			content[1] = availableIpAddresses.get(i).getInetAddress()
					.getHostAddress();
			entryList.add(content);
		}
		String[][] entries = new String[entryList.size()][2];
		int i = 0;
		for (String[] strings : entryList) {
			entries[i++] = strings;
		}
		ipSelectionField = new ComboFieldEditor(
				PreferenceConstants.DEFAULT_IP_ADDRESS,
				"The default network for collaboration connections", entries,
				network);
		ipSelectionField.setPreferenceStore(getPreferenceStore());
		ipSelectionField.load();

		return parent;
	}

	protected void performDefaults() {
		nameField.loadDefault();
		shortNameField.loadDefault();
		ipSelectionField.loadDefault();
		super.performDefaults();
	}

	public boolean performOk() {
		nameField.store();
		shortNameField.store();
		ipSelectionField.store();
		return super.performOk();
	}

}