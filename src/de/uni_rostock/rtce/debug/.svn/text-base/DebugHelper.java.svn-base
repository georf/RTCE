package de.uni_rostock.rtce.debug;

import de.uni_rostock.rtce.rebeca.events.ChangeEvent;
import de.uni_rostock.rtce.rebeca.events.GlobalChangeEvent;
import de.uni_rostock.rtce.rebeca.events.RtceEvent;
import de.uni_rostock.rtce.rebeca.events.StatusEvent;

/**
 * Enables some helping debugging functionalities. All helpers are only used if
 * if the vm command line switch - defined by the attribute
 * KEY_COMMAND_LINE_DEBUG - is present and set to true (for example
 * "-DrtceDebug=true").
 * 
 * @author Jonas
 * @author Georg Limbach <georf@dev.mgvmedia.com>
 * 
 */
public class DebugHelper {
	public static final String KEY_COMMAND_LINE_DEBUG = "rtceDebug";
	public static final String KEY_COMMAND_LINE_DELAY = "rtceDelay";

	/**
	 * Defines if the helper should use debug messages
	 */
	private static boolean debug = Boolean.getBoolean(KEY_COMMAND_LINE_DEBUG);

	/**
	 * Defines if the helper should use a delay
	 */
	private static boolean delay = Boolean.getBoolean(KEY_COMMAND_LINE_DELAY);

	/**
	 * 
	 * @return the debug status
	 */
	public static boolean getDebugStatus() {
		return debug;
	}

	/**
	 * Delays the current thread by the given milliseconds.
	 * 
	 * @param milliseconds
	 */
	public static void delay(long milliseconds) {
		if (!delay)
			return;

		try {
			Thread.sleep(milliseconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Debug output for an event to put into network
	 * 
	 * @param event
	 *            to show
	 */
	public static void debugEventPut(RtceEvent event) {
		debugEvent(event, "Put ");
	}

	/**
	 * Debug output for an event get from network
	 * 
	 * @param event
	 *            to show
	 */
	public static void debugEventGet(RtceEvent event) {
		debugEvent(event, "Get ");
	}

	/**
	 * 
	 * @param event
	 *            to show
	 * @param before
	 *            text in front
	 */
	private static void debugEvent(RtceEvent event, String before) {
		if (debug) {
			if (event instanceof StatusEvent) {
				System.out.println(before + "Status "
						+ ((StatusEvent) event).toString());
			}

			else if (event instanceof ChangeEvent) {
				System.out.println(before + "Change "
						+ ((ChangeEvent) event).toString());
			}

			else if (event instanceof GlobalChangeEvent) {
				System.out.println(before + "Global "
						+ ((GlobalChangeEvent) event).toString());
			}

		}
	}

}
