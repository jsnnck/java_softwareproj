package osi.exception;

/**
 * thrown to indicate that the application has attempted to request a service
 * from the lower layer or assigning the source address
 */
public class TransportException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a {@code TransportException} with the specified detail message.
	 * 
	 * @param message - the detail message. The detail message is saved for later
	 *                retrieval by the {@link Throwable.getMessage} method.
	 */
	public TransportException(String message) {
		super(message);
	}
}
