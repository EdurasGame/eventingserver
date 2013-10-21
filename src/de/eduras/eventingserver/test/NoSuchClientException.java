package de.eduras.eventingserver.test;

public class NoSuchClientException extends Exception {

	private int clientId;

	public NoSuchClientException(int clientId) {
		super("No client found for clientId #" + clientId);
	}

	public int getClientId() {
		return clientId;
	}

}
