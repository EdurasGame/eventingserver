package de.eduras.eventingserver;

public class DefaultClientNetworkEventHandler extends
		DefaultNetworkEventHandler implements ClientNetworkEventHandler {

	@Override
	public void onConnectionLost() {
		System.out.println("The client lost the connection.");
	}

	@Override
	public void onDisconnected() {
		System.out.println("The client disconnected.");
	}

	@Override
	public void onClientKicked(int clientId) {
		System.out.println("Client #" + clientId
				+ " was kicked from the server.");
	}
}
