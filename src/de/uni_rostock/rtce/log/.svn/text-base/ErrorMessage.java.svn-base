package de.uni_rostock.rtce.log;

/**
 * A LogElement representing an Exception
 * 
 * @see Exception 
 * @author Sebastian Gaul <sebastian@dev.mgvmedia.com>
 */
public class ErrorMessage extends LogElement {

	private Exception exception;

	public ErrorMessage(Exception exception) {
		super("");
		this.exception = exception;
	}
	
	public String getMessage() {
		return exception.getMessage();
	}	
	
	public Exception getException() {
		return exception;
	}

}
