package de.eduras.eventingserver.test;

public class NoSuchClientException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int clientId;

	public NoSuchClientException(int clientId) {
		super("No client found for clientId #" + clientId);
	}

	public int getClientId() {
		return clientId;
	}

}
