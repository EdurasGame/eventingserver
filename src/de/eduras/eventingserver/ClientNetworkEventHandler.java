package de.eduras.eventingserver;

public interface ClientNetworkEventHandler extends NetworkEventHandler {

	public void onConnectionLost();

	public void onDisconnected();

}
