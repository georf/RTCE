package de.uni_rostock.rtce.rebeca;

/**
 * Exception thrown whenever an connection error occurs
 * 
 * @author Georg Limbach <georf@dev.mgvmedia.com>
 * @author Sebastian Gaul <sebastian@dev.mgvmedia.com>
 */
public class ConnectionException extends Exception {

	private static final long serialVersionUID = 3764478664271312236L;

	public static final int portUnavailable = 21;
	public static final int timeout = 31;
	public static final int badConnectionPhrase = 41;

	protected int errorCode;

	public ConnectionException(int errorCode) {
		super("Connection error: " + errorCode);

		this.errorCode = errorCode;
	}

	public int getErrorCode() {
		return errorCode;
	}
	
	/**
	 * Returns a user-friendly error message
	 */
	public String getMessage() {
		switch (errorCode) {
		case portUnavailable:
			return "Unable to open a network port. Please check your system and application settings.";
		case badConnectionPhrase:
			return "Entered connection phrase is invalid. Please check again.";
		case timeout:
			return "Unable to connect to desired host.";
		default:
			return "An unknown problem has occured.";
		}
	}

}
