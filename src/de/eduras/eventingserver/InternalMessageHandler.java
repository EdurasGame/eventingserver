package de.eduras.eventingserver;

import java.net.SocketAddress;
import java.util.LinkedList;

import de.eduras.eventingserver.Event.PacketType;
import de.eduras.eventingserver.exceptions.ConnectionLostException;
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

		while (rest.contains("&")) {
			// extract internal messages
			String[] parts = rest.split("&", 3);
			// we know that there are always 2*i "*"s , if any. so parts[1] must
			// contain the internal message and we just concat the rest
			internalMessages.add(parts[1]);
			rest = parts[0] + parts[2];
		}

		return new Pair<LinkedList<String>, String>(internalMessages, rest);
	}

	static String makeAnInternalMessage(String message) {
		return "&" + message + "&";
	}

	static String createConnectionEstablishMessage(int clientId) {
		return makeAnInternalMessage(CONNECTION_ESTABLISHED + "#" + clientId);
	}

	static String createClientConnectedMessage(int clientId) {
		return makeAnInternalMessage(CLIENT_CONNECTED + "#" + clientId);
	}

	static String createClientKickedMessage(int clientId, String reason) {
		return makeAnInternalMessage(CLIENT_KICKED + "#" + clientId + "#"
				+ reason);
	}

	static String createClientDisconnectedMessage(int clientId) {
		return makeAnInternalMessage(CLIENT_DISCONNECTED + "#" + clientId);
	}

	static String createUDPHIMessage(int clientId) {
		return makeAnInternalMessage(UDP_HI + "#" + clientId);
	}

	static String createUDPREADYMessage() {
		return makeAnInternalMessage(UDP_READY);
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
						continue;
					}

					server.serverSender.sendMessageToClient(clientId,
							InternalMessageHandler.createUDPREADYMessage(),
							PacketType.TCP);

					client.setUdpAddress((SocketAddress) someArgument);
					client.setUdpSetUp(true);
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
			}
		}
	}

	static void handleInternalMessagesClient(final Client client,
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
					continue;
				} catch (Exception e) {
					e.printStackTrace();
					continue;
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
					continue;
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
			}

			if (internalMessage.contains(CLIENT_KICKED)) {
				try {
					client.networkEventHandler.onClientKicked(Integer
							.parseInt(NetworkMessageSerializer
									.internalMessageGetArgument(
											internalMessage, 0)),
							NetworkMessageSerializer
									.internalMessageGetArgument(
											internalMessage, 1));
				} catch (NumberFormatException e) {
					e.printStackTrace();
					continue;
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
			}

			if (internalMessage.contains(CONNECTION_ESTABLISHED)) {
				client.connected = true;
				try {
					client.setOwnerId(Integer.parseInt(NetworkMessageSerializer
							.internalMessageGetArgument(internalMessage, 0)));
				} catch (NumberFormatException e) {
					e.printStackTrace();
					continue;
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
				class UDPInitializer extends Thread {
					@Override
					public void run() {
						while (!client.sender.isUDPSetUp) {
							try {
								client.sender.sendMessage(
										InternalMessageHandler
												.createUDPHIMessage(client
														.getClientId()),
										Event.PacketType.UDP);
							} catch (ConnectionLostException e) {
								client.connectionLost();
								return;
							}
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
								return;
							}
						}
					}
				}

				new UDPInitializer().start();
			}
		}
	}

	public static boolean isCompatibleString(String string)
			throws IllegalArgumentException {
		if (string.contains("#") || string.contains("&")) {
			throw new IllegalArgumentException("String argument " + string
					+ " contains # or &!");
		}
		return true;
	}
}
