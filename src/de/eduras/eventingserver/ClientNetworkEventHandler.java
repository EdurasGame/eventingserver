package de.eduras.eventingserver;

/**
 * In addtion to the functions already defined in {@link NetworkEventHandler},
 * this class defines functions that are called when a certain network event
 * happens. All the functions here are specifically interesting to the client.
 * 
 * @author Florian Mai <florian.ren.mai@googlemail.com>
 * 
 */
public interface ClientNetworkEventHandler extends NetworkEventHandler {

	/**
	 * Indicates that the client has lost the connection for some network or IO
	 * error.
	 */
	public void onConnectionLost();

	/**
	 * Indicates that the client disconnected or was disconnected from the
	 * server.
	 */
	public void onDisconnected();

	/**
	 * Indicates that a client was kicked for some reason.
	 * 
	 * @param clientId
	 *            The id of the client that was kicked.
	 * @param reason
	 *            The reason why it was kicked.
	 */
	public void onClientKicked(int clientId, String reason);

	/**
	 * Called when a client attempts to connect to the server but it is full.
	 */
	public void onServerIsFull();

}
