package de.uni_rostock.rtce.log;

import java.util.Observable;
import java.util.Observer;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;

import de.uni_rostock.rtce.Core;

/**
 * An error dialog showing Log's error messages as dialog
 * 
 * @author Sebastian Gaul <sebastian@dev.mgvmedia.com>
 */
public class LogDialog implements Observer {

	public LogDialog() {
		Log.getInstance().addObserver(this);
		// TESTME Log.log(new ErrorMessage(new ConnectionException(31)));
	}

	@Override
	public void update(Observable o, Object arg) {
		if (Log.class.isInstance(o)) {
			Log log = (Log) o;
			for (ErrorMessage msg : log.errorQueue) {
				Status status = new Status(IStatus.ERROR, Core.PLUGIN_ID, 0,
						"Status Error Message", msg.getException());
				ErrorDialog.openError(Core.getInstance().getWorkbench()
						.getActiveWorkbenchWindow().getShell(), "RTCE Error",
						"Real-Time Collaboration Plugin reported an error:", status);
				log.errorQueue.remove(msg);
			}
		}
	}

}
