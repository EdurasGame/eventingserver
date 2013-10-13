package de.eduras.eventingserver;

import java.util.LinkedList;

/**
 * {@link ServerDecoder} is used to handle received messages from clients that
 * wait in a input buffer and translate them into GameEvents to hand them on to
 * logic.
 * 
 * @author illonis
 * 
 */
public class ServerDecoder extends Thread {

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
		while (true) {
			try {
				String s = inputBuffer.getNext();
				decodeMessage(s);
			} catch (InterruptedException e) {
				// EduLog.passException(e);
				e.printStackTrace();
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
		LinkedList<Event> deserializedMessages = NetworkMessageSerializer
				.deserializeEvent(message);
		// EduLog.info("[ServerDecoder] Decoded " + deserializedMessages.size()
		// + " messages from: " + message);

		if (InternalMessageHandler.isInternalMessage(message)) {
			// TODO: do something
		} else {

			for (Event event : deserializedMessages) {
				server.eventHandler.handleEvent(event);
			}
		}
	}
}