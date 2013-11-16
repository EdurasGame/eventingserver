package de.eduras.eventingserver.test;

import de.eduras.eventingserver.ClientNetworkEventHandler;

public class ChatNetworkEventHandlerClient implements ClientNetworkEventHandler {

	@Override
	public void onConnectionLost() {
		System.out.println("You lost the connection.");
	}

	@Override
	public void onDisconnected() {
		System.out.println("You disconnected.");

	}

	@Override
	public void onClientDisconnected(int clientId) {
		System.out.println("Client with id #" + clientId + " disconnected.");

	}

	@Override
	public void onClientConnected(int clientId) {
		System.out.println("Client with id #" + clientId + " connected.");

	}

	@Override
	public void onClientKicked(int clientId, String reason) {
		System.out.println("You were kicked because " + reason);
	}
}
