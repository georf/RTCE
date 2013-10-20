package de.uni_rostock.rtce.log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A LogElement containing a message
 * 
 * @author Sebastian Gaul <sebastian@dev.mgvmedia.com>
 */
public class LogElement {

	/**
	 * The Log Element's content
	 */
	protected String message;
	
	/**
	 * Date of creation
	 */
	protected Date date;
	
	/**
	 * Creates a new element
	 * @param message The element's content
	 */
	public LogElement(String message) {
		super();
		this.message = message;
		this.date = new Date();
	}

	/**
	 * The element's message
	 * @return String message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * The element's message
	 * @param message
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * The element's creation date
	 * @return
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * The element's creation date
	 * @param date
	 */
	public void setDate(Date date) {
		this.date = date;
	}
	
	/**
	 * Transforms the object into a String
	 * 
	 * <p>The string contains the creation date and the message of the Log
	 * Element.</p>
	 */
	public String toString() {
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		return dateFormat.format(date) + " " + message;
	}

}
