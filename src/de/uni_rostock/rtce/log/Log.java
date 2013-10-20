package de.uni_rostock.rtce.log;

import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

import de.uni_rostock.rtce.debug.DebugHelper;

/**
 * A generic Log to store and show different types of messages
 * 
 * This log handles all elements derived from LogElement.
 * 
 * @see LogElement
 * 
 * @author Sebastian Gaul <sebastian@dev.mgvmedia.com>
 * @author Georg Limbach <georf@dev.mgvmedia.com>
 */
public class Log extends Observable {

	/**
	 * List of handled LogElements
	 */
	private List<LogElement> log = new LinkedList<LogElement>();
	
	/**
	 * List's maximum length
	 * @see Log#truncateList()
	 */
	private int maxLength = 200;

	/**
	 * Instance for singleton
	 * @see Log#getInstance()
	 */
	private static Log main;
	
	/**
	 * A dialog to show error messages
	 * @see LogDialog
	 */
	private LogDialog dialog;
	
	/**
	 * Separated list for error messages
	 */
	public List<ErrorMessage> errorQueue  = new LinkedList<ErrorMessage>();

	/**
	 * Creates a global instance
	 * @see Log#getInstance()
	 */
	public Log() {
		super();
		// Create a global instance
		if (main == null) {
			main = this;
		}
		if (dialog == null) {
			dialog = new LogDialog();
		}
	}

	/**
	 * Adds a new String message to the log
	 * @param message Message to be logged
	 */
	public void addLog(String message) {
		addLog(new LogElement(message));
	}

	/**
	 * Adds a new element to the log
	 * @param le The new log element
	 */
	public synchronized void addLog(LogElement le) {
		if (le instanceof ErrorMessage) {
			errorQueue.add((ErrorMessage)le);
		}
		log.add(le);
		truncateList();
		setChanged();
		notifyObservers();
	}

	/**
	 * Truncates Log's list to a given maximum length
	 * @see Log#maxLength
	 */
	public void truncateList() {
		if (log.size() > maxLength) {
			log = log.subList(1, log.size());
		}
	}

	/**
	 * Latest log messages as list of String
	 * @return Log messages
	 */
	public List<String> getLatestStrings() {
		LinkedList<String> out = new LinkedList<String>();
		for (LogElement le : log) {
			out.add(le.toString());
		}
		return out;
	}

	/**
	 * Instance for use as singleton
	 * @return Global instance
	 */
	public static Log getInstance() {
		if (main == null) {
			main = new Log();
		}
		return main;
	}

	/**
	 * Static way to add a messages as String
	 * @param message  Log message
	 */
	public static void log(String message) {
		getInstance().addLog(message);
	}

	/**
	 * Static way to add a LogElement
	 * @param le A new log element
	 */
	public static void log(LogElement le) {
		getInstance().addLog(le);
	}

	/**
	 * Writes the message to stdout
	 * 
	 * @param message
	 */
	public static void debug(String message) {
		if (DebugHelper.getDebugStatus())
			System.out.println(message);
	}

	/**
	 * Adds a debug list item to log view
	 * 
	 * @param message
	 */
	public static void info(String message) {
		getInstance().addLog(new DebugMessage(message));
	}

}
