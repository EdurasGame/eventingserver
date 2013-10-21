package de.eduras.eventingserver;

import java.util.LinkedList;

import de.eduras.eventingserver.test.NoSuchClientException;

public interface ServerInterface {

	/**
	 * Start the server.
	 * 
	 * @param name
	 *            The name of the server.
	 * @param port
	 *            The port on which the server will work.
	 * @return Success flag
	 */
	public boolean start(String name, int port);

	/**
	 * Stops the server.
	 * 
	 * @return Success flag.
	 */
	public boolean stop();

	/**
	 * Set the {@link NetworkPolicy} on the server.
	 * 
	 * @param policy
	 *            The new policy.
	 */
	public void setPolicy(NetworkPolicy policy);

	/**
	 * Sends an {@link Event} to the client identified by the given id.
	 * 
	 * @param event
	 *            The event to send.
	 * @param clientId
	 *            The client's id.
	 * @return Success flag.
	 */
	public boolean sendEventToClient(Event event, int clientId)
			throws NoSuchClientException;

	/**
	 * Sends an {@link Event} to all registered clients.
	 * 
	 * @param event
	 *            The event to send.
	 * @return
	 */
	public boolean sendEventToAll(Event event);

	/**
	 * Returns a {@link LinkedList} of ids of all clients currently connected to
	 * the server.
	 * 
	 * @return The list of ids.
	 */
	public LinkedList<Integer> getClients();

	/**
	 * Terminates the connection to the client with the given id.
	 * 
	 * @param clientId
	 *            The id of the client to kick.
	 * @return Success flag.
	 */
	public boolean kickClient(int clientId);

	/**
	 * Sets the {@link EventHandler} to which events are passed.
	 * 
	 * @param eventHandler
	 *            The new handler.
	 * @return Success flag.
	 */
	public boolean setEventHandler(EventHandler eventHandler);

	/**
	 * Sets the {@link NetworkEventHandler} whose callbacks are called when
	 * something on the network happens.
	 * 
	 * @param handler
	 *            The handler.
	 * @return Success flag.
	 */
	public boolean setNetworkEventHandler(NetworkEventHandler handler);

}
