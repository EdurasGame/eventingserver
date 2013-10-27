package de.eduras.eventingserver;

import java.net.SocketAddress;
import java.util.LinkedList;

import de.eduras.eventingserver.Event.PacketType;
import de.eduras.eventingserver.utils.Pair;

class InternalMessageHandler {

	private static final String CONNECTION_ESTABLISHED = "CONNECTION_ESTABLISHED";
	private static final String UDP_HI = "UDP_HI";
	private static final String UDP_READY = "UDP_READY";
	private static final String CLIENT_CONNECTED = "CLIENT_CONNECTED";
	private static final String CLIENT_DISCONNECTED = "CLIENT_DISCONNECTED";
	private static final String CLIENT_KICKED = "CLIENT_KICKED";

	static Pair<LinkedList<String>, String> extractInternalMessage(
			String messages) {
		String rest = messages;
		LinkedList<String> internalMessages = new LinkedList<String>();

		while (rest.contains("*")) {
			// extract internal messages
			String[] parts = rest.split("*", 3);
			// we know that there are always 2*i "*"s , if any. so parts[1] must
			// contain the internal message and we just concat the rest
			internalMessages.add(parts[1]);
			rest = parts[0] + parts[2];
		}

		return new Pair<LinkedList<String>, String>(internalMessages, rest);
	}

	static String makeAnInternalMessage(String message) {
		return "*" + message + "*";
	}

	static String createConnectionEstablishMessage() {
		return makeAnInternalMessage(CONNECTION_ESTABLISHED);
	}

	static String createClientConnectedMessage(int clientId) {
		return makeAnInternalMessage(CLIENT_CONNECTED + "#" + clientId);
	}

	static String createClientKickedMessage(int clientId) {
		return makeAnInternalMessage(CLIENT_KICKED + "#" + clientId);
	}

	static String createClientDisconnectedMessage(int clientId) {
		return makeAnInternalMessage(CLIENT_DISCONNECTED + clientId);
	}

	static String createUDPHIMessage() {
		return makeAnInternalMessage(UDP_HI);
	}

	// TODO: this is solved very very badly. think about it some day. the
	// problem is to make the code look good and pass arguments that are not
	// included in the message like the socketaddress of the udp hi message :(
	static void handleInternalMessagesServer(Server server,
			LinkedList<String> messages, Object someArgument) {
		for (String internalMessage : messages) {

			if (internalMessage.contains(UDP_HI)) {
				int clientId;
				try {
					clientId = Integer.parseInt(NetworkMessageSerializer
							.internalMessageGetArgument(internalMessage, 0));

					ServerClient client = server.getClientById(clientId);
					if (client == null || client.isUdpSetUp()) {
						return;
					}

					server.serverSender.sendMessageToClient(clientId,
							InternalMessageHandler.UDP_READY, PacketType.TCP);

					client.setUdpAddress((SocketAddress) someArgument);
					client.setUdpSetUp(true);
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
			}
		}
	}

	static void handleInternalMessagesClient(Client client,
			LinkedList<String> messages, Object someArgument) {
		for (String internalMessage : messages) {

			if (internalMessage.contains(UDP_READY)) {
				client.sender.isUDPSetUp = true;
			}

			if (internalMessage.contains(CLIENT_CONNECTED)) {
				try {
					client.networkEventHandler.onClientConnected(Integer
							.parseInt(NetworkMessageSerializer
									.internalMessageGetArgument(
											internalMessage, 0)));
				} catch (NumberFormatException e) {
					e.printStackTrace();
					return;
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
			}

			if (internalMessage.contains(CLIENT_DISCONNECTED)) {
				try {
					client.networkEventHandler.onClientDisconnected(Integer
							.parseInt(NetworkMessageSerializer
									.internalMessageGetArgument(
											internalMessage, 0)));
				} catch (NumberFormatException e) {
					e.printStackTrace();
					return;
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
			}

			if (internalMessage.contains(CLIENT_KICKED)) {
				try {
					client.networkEventHandler.onClientKicked(Integer
							.parseInt(NetworkMessageSerializer
									.internalMessageGetArgument(
											internalMessage, 0)));
				} catch (NumberFormatException e) {
					e.printStackTrace();
					return;
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
			}
		}
	}
}
