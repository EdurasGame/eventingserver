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
	public void onConnectionLost() {
		System.out.println("The connection was lost.");
	}

	@Override
	public void onDisconnect() {
		System.out.println("Client disconnected.");
	}

}
