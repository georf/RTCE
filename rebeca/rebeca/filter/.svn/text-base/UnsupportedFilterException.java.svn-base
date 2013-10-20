/*
 * $Id: UnsupportedFilterException.java 99 2009-02-13 16:50:24Z parzy $
 */
package rebeca.filter;

/**
 * Thrown to indicate that a filter object could not be adequately handled by 
 * Rebeca's filter framework, that is, no method could be dispatched which
 * takes objects of the causing filter type as arguments.
 * 
 * @author parzy
 */
public class UnsupportedFilterException extends RuntimeException 
{
	/**
	 * Serial id to check compatibility.
	 */
	private static final long serialVersionUID = 20090114220736L;

	/**
     * Constructs an UnsupportedFilterException with no detail message.
     */
    public UnsupportedFilterException() { }

    /**
     * Constructs an UnsupportedFilterException with the specified
     * detail message.
     * @param message the detail message
     */
    public UnsupportedFilterException(String message) 
    {
    	super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and
     * cause.
     * <p>Note that the detail message associated with <code>cause</code> is
     * <i>not</i> automatically incorporated in this exception's detail
     * message.
     * @param  message the detail message (which is saved for later retrieval
     *         by the {@link Throwable#getMessage()} method).
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link Throwable#getCause()} method).  (A <tt>null</tt> value
     *         is permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     */
    public UnsupportedFilterException(String message, Throwable cause) 
    {
        super(message, cause);
    }
 
    /**
     * Constructs a new exception with the specified cause and a detail
     * message of <tt>(cause==null ? null : cause.toString())</tt> (which
     * typically contains the class and detail message of <tt>cause</tt>).
     * This constructor is useful for exceptions that are little more than
     * wrappers for other throwables (for example, {@link
     * java.security.PrivilegedActionException}).
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link Throwable#getCause()} method).  (A <tt>null</tt> value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     */
    public UnsupportedFilterException(Throwable cause) 
    {
        super(cause);
    }
}
