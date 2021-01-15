package osi.exception;

/**
 * <p>
 * Thrown to indicate that the application has attempted to decode a stream of
 * encoded data, but it is impossible to decode the data.
 */
public class DecodeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a {@code DecodeException} with the specified detail message.
	 * 
	 * @param message - the detail message. The detail message is saved for later
	 *                retrieval by the {@link Throwable.getMessage} method.
	 */
	public DecodeException(String message) {
		super(message);
	}
}