package de.eduras.eventingserver;

/**
 * Implement this interface to react on network events.
 * 
 * @author Florian Mai <florian.ren.mai@googlemail.com>
 * 
 */
public interface NetworkEventHandler {

	/**
	 * A client disconnected.
	 * 
	 * @param clientId
	 *            The client's id.
	 */
	public void onClientDisconnected(int clientId);

	/**
	 * A client connected.
	 * 
	 * @param clientId
	 *            The client's id.
	 */
	public void onClientConnected(int clientId);

}
