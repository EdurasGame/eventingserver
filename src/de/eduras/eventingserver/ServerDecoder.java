package de.eduras.eventingserver;

import java.util.LinkedList;
import java.util.logging.Logger;

import de.eduras.eventingserver.utils.Pair;
import de.illonis.edulog.EduLog;

/**
 * {@link ServerDecoder} is used to handle received messages from clients that
 * wait in a input buffer and translate them into GameEvents to hand them on to
 * logic.
 * 
 * @author illonis
 * 
 */
class ServerDecoder extends Thread {

	private final static Logger L = EduLog.getLoggerFor(ServerDecoder.class
			.getName());

	private final Buffer inputBuffer;
	private final Server server;

	/**
	 * Creates a new {@link ServerDecoder} that pulls messages from given
	 * inputbuffer and parses them to logic or networkEventListener respectivly.
	 * 
	 * @param inputBuffer
	 *            Buffer to read messages from at specific interval.
	 * @param logic
	 *            Logic to push gameevents into.
	 * @param networkEventListener
	 *            The listener to forward networkEvents to.
	 */
	public ServerDecoder(Buffer inputBuffer, Server server) {
		setName("ServerDecoder");
		this.inputBuffer = inputBuffer;
		this.server = server;
	}

	@Override
	public void run() {
		// EduLog.info("[ServerDecoder] Started serverlogic.");
		readFromInputBuffer();
	}

	/**
	 * Reads repeatedly from input buffer and decodes those messages.<br>
	 * This does not need a wait implementation because reading from input
	 * buffer is blocking.
	 */
	private void readFromInputBuffer() {
		while (server.running) {
			try {
				String s = inputBuffer.getNext();
				decodeMessage(s);
			} catch (InterruptedException e) {
				L.severe("Interrupted when reading buffer: " + e.getMessage());
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
		// EduLog.info("[ServerDecoder] Decoded " + deserializedMessages.size()
		// + " messages from: " + message);

		// check for internal messages
		Pair<LinkedList<String>, String> internalAndRest = InternalMessageHandler
				.extractInternalMessage(message);
		InternalMessageHandler.handleInternalMessagesServer(server,
				internalAndRest.getFirst(), null);

		// handle events
		LinkedList<Event> deserializedMessages = NetworkMessageSerializer
				.deserializeEvent(internalAndRest.getSecond());
		for (Event event : deserializedMessages) {
			server.eventHandler.handleEvent(event);
		}

	}
}