package de.eduras.eventingserver;

/**
 * This is the {@link NetworkEventHandler} that is set by default to every
 * client. It simply prints which event is occuring to stdout.
 * 
 * @author Florian Mai <florian.ren.mai@googlemail.com>
 * 
 */
class DefaultNetworkEventHandler implements NetworkEventHandler {

	@Override
	public void onClientDisconnected(int clientId) {
		System.out.println("Client #" + clientId + " disconnected.");
	}

	@Override
	public void onClientConnected(int clientId) {
		System.out.println("Client #" + clientId + " connected.");
	}

}
