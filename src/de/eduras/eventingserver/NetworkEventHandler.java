package de.eduras.eventingserver;

public interface NetworkEventHandler {

	public void onConnectionLost();

	public void onDisconnect();

}
