package de.eduras.eventingserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * This class represents a client from the view of the server.
 * 
 * @author Florian Mai <florian.ren.mai@googlemail.com>
 * 
 */
public class ServerClient {

	private final int clientId;
	private final Socket socket;
	private final PrintWriter printWriter;
	private final BufferedReader bufferedReader;
	private boolean connected;
	private boolean udpSetUp;
	private SocketAddress udpAddress;

	/**
	 * Specifies the role of the client. If the client states to be spectator,
	 * it will receive all the info a player-client also gets, but it can't pass
	 * information to the server.
	 * 
	 * @author Florian Mai <florian.ren.mai@googlemail.com>
	 * 
	 */

	/**
	 * Creates a new ServerClient with the given id and that uses the given
	 * socket.
	 * 
	 * @param clientId
	 *            The client's id.
	 * @param socket
	 *            The client's socket.
	 * @throws IOException
	 *             Is thrown if the socket is somehow broken.
	 */
	ServerClient(int clientId, Socket socket) throws IOException {
		this.clientId = clientId;
		this.socket = socket;

		this.printWriter = new PrintWriter(socket.getOutputStream(), true);
		this.bufferedReader = new BufferedReader(new InputStreamReader(
				socket.getInputStream()));

		connected = false;
		udpSetUp = false;

	}

	/**
	 * Returns the id of the client.
	 * 
	 * @return The client's id.
	 */
	public int getClientId() {
		return clientId;
	}

	public SocketAddress getUdpAddress() {
		return udpAddress;
	}

	public void setUdpAddress(SocketAddress udpAddress) {
		this.udpAddress = udpAddress;
	}

	/**
	 * Returns the socket that belongs to the client.
	 * 
	 * @return The client's socket.
	 */
	Socket getSocket() {
		return socket;
	}

	/**
	 * Closes the socket.
	 * 
	 * @throws IOException
	 */
	void closeConnection() throws IOException {
		socket.close();
	}

	/**
	 * Returns a printwriter that sends on the socket.
	 * 
	 * @return The printwriter.
	 */
	PrintWriter getOutputStream() {
		return printWriter;
	}

	/**
	 * Returns a bufferedReader to read from the socket of the client.
	 * 
	 * @return The buffered reader.
	 */
	BufferedReader getInputStream() {
		return bufferedReader;
	}

	/**
	 * Returns the host address of the client as string.
	 * 
	 * @return The client's host address.
	 */
	public String getHostAddress() {
		return socket.getInetAddress().getHostAddress();
	}

	/**
	 * Returns whether the client is connected or not.
	 * 
	 * @return True if connected, false otherwise.
	 */
	public boolean isConnected() {
		return connected;
	}

	/**
	 * Sets the connected status.
	 * 
	 * @param connected
	 *            The new status.
	 */
	void setConnected(boolean connected) {
		this.connected = connected;
	}

	/**
	 * Tells whether the initial UDP messages has been received from this
	 * client.
	 * 
	 * @return True if yes.
	 */
	boolean isUdpSetUp() {
		return udpSetUp;
	}

	/**
	 * Sets the udpSetUp flag.
	 * 
	 * @param b
	 *            The new value.
	 */
	void setUdpSetUp(boolean b) {
		udpSetUp = b;

	}

}
