package de.eduras.eventingserver;


public interface ClientInterface {

	/**
	 * Connect to the server at {@value hostname}:{@value port}.
	 * 
	 * @param hostname
	 *            The hostname/IP of the server to connect to.
	 * @param port
	 *            The port of the server to connect to.
	 * @return Success flag.
	 */
	public boolean connect(String hostname, int port);

	/**
	 * Disconnect from the server currently connected to.
	 * 
	 * @return Success flag.
	 */
	public boolean disconnect();

	/**
	 * Get the id this client is referenced with on the server currently
	 * connected to.
	 * 
	 * @return Some integer >= 0 or -1 if the client is not connected currently.
	 */
	public int getClientId();

	/**
	 * Send an {@link Event} to the server.
	 * 
	 * @param event
	 *            The event to send.
	 * @return Success flag.
	 */
	public boolean sendEvent(Event event);

}
