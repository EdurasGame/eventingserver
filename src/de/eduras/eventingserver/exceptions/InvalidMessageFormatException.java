package de.eduras.eventingserver.exceptions;

/**
 * Exception thrown when a serialized message cannot be deserialized because it
 * has an invalid format.
 * 
 * @author illonis
 * 
 */
public class InvalidMessageFormatException extends Exception {

	private static final long serialVersionUID = 1L;
	private String invalidMessage;

	/**
	 * Creates a new <code>InvalidMessageFormatException</code>.
	 * 
	 * @param msg
	 *            The detail message. The detail message is saved for later
	 *            retrieval by the {@link #getMessage()} method.
	 * @param invalidMessage
	 *            Original message that was invalid.
	 */
	public InvalidMessageFormatException(String msg, String invalidMessage) {
		super(msg);
		this.invalidMessage = invalidMessage;
	}

	/**
	 * Returns the original message that was invalid.
	 * 
	 * @return original message that was invalid.
	 */
	public String getInvalidMessage() {
		return invalidMessage;
	}
}