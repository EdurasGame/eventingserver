package de.eduras.eventingserver.exceptions;

/**
 * Exception thrown when a received message is not supported because there does
 * not exist any logic interpretation for it.
 * 
 * @author illonis
 * 
 */
public class MessageNotSupportedException extends Exception {

	private static final long serialVersionUID = 1L;
	private int gameEventNumber;
	private String eventMessage;

	/**
	 * Creates a new MessageNotSupportedException.
	 * 
	 * @param gameEventNumber
	 *            {@link GameEventNumber} that is not supported.
	 * @param eventMessage
	 *            original message string.
	 */
	public MessageNotSupportedException(int gameEventNumber, String eventMessage) {
		super();
		this.gameEventNumber = gameEventNumber;
		this.eventMessage = eventMessage;
	}

	/**
	 * Returns original message string of unsupported message.
	 * 
	 * @return original message string.
	 */
	public String getEventMessage() {
		return eventMessage;
	}

	/**
	 * Returns {@link GameEventNumber} of message.
	 * 
	 * @return {@link GameEventNumber} of message.
	 */
	public int getGameEventNumber() {
		return gameEventNumber;
	}
}
