package de.eduras.eventingserver;

import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.eduras.eventingserver.utils.Pair;
import de.illonis.edulog.EduLog;

/**
 * Processes messages that arrive at the client.
 * 
 * @author Florian Mai <florian.ren.mai@googlemail.com>
 * 
 *         More detailed, the incoming message is deserialized and forwarded to
 *         the gamelogic or to the network listener.
 */
class ClientParser extends Thread {

	private final static Logger L = EduLog.getLoggerFor(ClientParser.class
			.getName());

	Client client;
	private final Buffer inputBuffer;

	/**
	 * Creates a new ClientLogic that deserializes the given message into an
	 * event and forwards it to the given GameLogic or to the given
	 * NetworkEventListener.
	 * 
	 * @param inputBuffer
	 *            The input buffer that this parser will read from.
	 * @param client
	 *            The client which belongs to the clientLogic.
	 */
	public ClientParser(Buffer inputBuffer, Client client) {
		setName("ClientParser");
		this.client = client;
		this.inputBuffer = inputBuffer;
	}

	@Override
	public void run() {
		// EduLog.info("[ClientParser] Started.");
		readFromInputBuffer();
	}

	/**
	 * Reads repeatedly from input buffer and decodes those messages.<br>
	 * This does not need a wait implementation because reading from input
	 * buffer is blocking.
	 */
	private void readFromInputBuffer() {
		while (client.isConnected()) {
			try {
				String s = inputBuffer.getNext();
				decodeMessage(s);
			} catch (InterruptedException e) {
				// EduLog.info("ClientParser interrupted.");
				L.log(Level.WARNING, "Interrupted when reading buffer.", e);
				client.connectionLost();
				break;
			}
		}
	}

	/**
	 * Decodes given messages into one or more events and notices logic.
	 * 
	 * @param message
	 *            message to decode.
	 */
	private void decodeMessage(String message) {
		if (message.isEmpty())
			return;

		L.fine("Decoding message: " + message);

		Pair<LinkedList<String>, String> internalAndEvents = InternalMessageHandler
				.extractInternalMessage(message);
		InternalMessageHandler.handleInternalMessagesClient(client,
				internalAndEvents.getFirst(), null);

		LinkedList<Event> deserializedMessages = NetworkMessageSerializer
				.deserializeEvent(internalAndEvents.getSecond());
		for (Event event : deserializedMessages) {
			client.eventHandler.handleEvent(event);
		}

	}
}