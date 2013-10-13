package de.eduras.eventingserver;

class InternalMessageHandler {

	public static String CONNECTION_ESTABLISHED = "CONNECTION_ESTABLISHED";
	public static String UDP_HI = "UDP_HI";
	public static String UDP_READY = "UDP_READY";

	private Server server;

	public static boolean isInternalMessage(String messages) {
		return messages.contains(CONNECTION_ESTABLISHED);
	}

	int extractClientId(String messages) throws NumberFormatException,
			Exception {

		if (messages.contains(UDP_HI)) {
			return Integer.parseInt(NetworkMessageSerializer
					.internalMessageGetArgument(messages, 0));

		}

		throw new Exception("The message has an invalid format.");

	}
}
