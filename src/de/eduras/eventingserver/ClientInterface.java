package de.eduras.eventingserver;

import java.net.InetAddress;

import de.eduras.eventingserver.exceptions.TooFewArgumentsExceptions;

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
	 * @throws IllegalArgumentException
	 *             thrown when an argument in the event is illegal.
	 * @throws TooFewArgumentsExceptions
	 */
	public boolean sendEvent(Event event) throws IllegalArgumentException,
			TooFewArgumentsExceptions;

	/**
	 * Set the {@link EventHandler} that is called when an event arrives at the
	 * client.
	 * 
	 * @param eventHandler
	 *            The eventhandler to be called when an event arrives.
	 */
	public void setEventHandler(EventHandler eventHandler);

	/**
	 * Sets the network event listener. This replaces any old listener.
	 * 
	 * @param listener
	 *            the new listener.
	 * 
	 * @author illonis
	 */
	public void setNetworkEventHandler(
			ClientNetworkEventHandler networkEventHandler);

	/**
	 * Returns true, if the client is currently connected to a server and false
	 * otherwise.
	 * 
	 * @return connected-flag
	 */
	public boolean isConnected();

	/**
	 * Sets the {@link NetworkPolicy} of the client.
	 * 
	 * @param policy
	 *            The policy
	 */
	public void setNetworkPolicy(NetworkPolicy policy);

	/**
	 * Pings the server. OnPingReceived will be called eventually on your
	 * {@link NetworkEventHandler}.
	 */
	public void ping();

	/**
	 * Returns the server's connection details as InetAddress.
	 * 
	 * @return The server's address.
	 */
	public InetAddress getServerAddress();
}
