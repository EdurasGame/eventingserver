package de.eduras.eventingserver;

import java.util.LinkedList;

import de.eduras.eventingserver.exceptions.TooFewArgumentsExceptions;
import de.eduras.eventingserver.test.NoSuchClientException;

/**
 * This interface defines the API of the server.
 * 
 * @author Florian Mai <florian.ren.mai@googlemail.com>
 * 
 */
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
	 * @throws TooFewArgumentsExceptions
	 * @throws Thrown
	 *             if the argument contains an illegal argument.
	 */
	public boolean sendEventToClient(Event event, int clientId)
			throws NoSuchClientException, IllegalArgumentException,
			TooFewArgumentsExceptions;

	/**
	 * Sends an {@link Event} to all registered clients.
	 * 
	 * @param event
	 *            The event to send.
	 * @return
	 * @throws IllegalArgumentException
	 *             Thrown if the event contains an illegal argument.
	 * @throws TooFewArgumentsExceptions
	 */
	public boolean sendEventToAll(Event event) throws IllegalArgumentException,
			TooFewArgumentsExceptions;

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
	 * 
	 *         This function has the same effect as kickClient(int,String) with
	 *         an empty string.
	 */
	public boolean kickClient(int clientId);

	/**
	 * Terminates the connection to the client with the given id. The client
	 * will receive the given 'reason' string as an explanation why.
	 * 
	 * @param clientId
	 *            The id of the client to kick.
	 * @param reason
	 *            The message that will be shown to the client explaining why he
	 *            has been kicked.
	 * @return Success flag.
	 */
	public boolean kickClient(int clientId, String reason);

	/**
	 * Sets the {@link EventHandler} to which events are passed.
	 * 
	 * @param eventHandler
	 *            The new handler.
	 * @return Success flag.
	 */
	public boolean setEventHandler(EventHandler eventHandler);

	/**
	 * Sets the {@link ServerNetworkEventHandler} whose callbacks are called
	 * when something on the network happens.
	 * 
	 * @param handler
	 *            The handler.
	 * @return Success flag.
	 */
	public boolean setNetworkEventHandler(ServerNetworkEventHandler handler);

	/**
	 * Returns the name of the server.
	 * 
	 * @return The name
	 */
	public String getName();

	/**
	 * Sets the maximum number of clients that can connect. All others will
	 * immediately be kicked. If set to a negative number it is assumed to be
	 * unlimited.
	 * 
	 * @param max
	 *            The new maximum of clients.
	 */
	public void setMaximumClients(int max);

	/**
	 * Returns the maximum number of clients that can connect.
	 * 
	 * @return The maximum number of clients.
	 */
	public int getMaximumClients();

}
