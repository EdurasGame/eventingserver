package de.eduras.eventingserver;

import java.util.LinkedList;

public class NetworkMessageSerializer {
	public static String serializeEvent(Event event) {
		return null;
	}

	public static LinkedList<Event> deserializeEvent(String eventStr) {
		return null;
	}

	public static String internalMessageGetArgument(String messages, int i)
			throws Exception {

		String[] args = messages.split("#");

		if (args.length <= i + 1 || i < 0) {
			throw new Exception("There is no such argument.");
		}

		return args[i + 1];
	}

	/**
	 * Concatenates given strings.
	 * 
	 * @param messages
	 *            Messages to concatenate
	 * @return concatenated messages
	 */

	public static String concatenate(String... messages) {
		StringBuilder b = new StringBuilder();
		for (String s : messages) {
			b.append(s);
		}
		return b.toString();
	}
}
