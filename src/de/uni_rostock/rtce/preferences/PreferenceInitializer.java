package de.uni_rostock.rtce.preferences;

import org.eclipse.core.runtime.internal.adaptor.EclipseEnvironmentInfo;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import de.uni_rostock.rtce.Core;
import de.uni_rostock.rtce.rebeca.RebecaController;

/**
 * Initial values RTCE's preferences
 * 
 * @author Sebastian Gaul <sebastian@dev.mgvmedia.com>
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#
	 * initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Core.getDefault().getPreferenceStore();
		String name = EclipseEnvironmentInfo.getDefault().getProperty(
				"user.name");
		store.setDefault(PreferenceConstants.USER_NAME, name);
		if (name.length() > 5) {
			store.setDefault(PreferenceConstants.SHORT_USER_NAME,
					name.substring(0, 5));
		} else {
			store.setDefault(PreferenceConstants.SHORT_USER_NAME, name);
		}
		if (RebecaController.getInstance().getAvailableIpAddresses().size() > 0) {
			store.setDefault(PreferenceConstants.DEFAULT_IP_ADDRESS,
					RebecaController.getInstance().getAvailableIpAddresses()
							.get(0).getInetAddress().getHostAddress());
		}
	}
}
