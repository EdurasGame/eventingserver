package de.eduras.eventingserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import Exceptions.ConnectionLostException;

import de.eduras.eventingserver.Event.PacketType;

/**
 * A client that connects to the game server and starts receiving and sending
 * events.
 * 
 * @author Florian Mai <florian.ren.mai@googlemail.com>
 * 
 */
public class Client {

	/**
	 * Connection timeout when connecting to server (in ms).
	 */
	public final static int CONNECT_TIMEOUT = 10000;

	private Socket socket;

	NetworkEventHandler networkEventHandler;

	private ClientSender sender;
	private ClientReceiver receiver;

	private int clientId;

	EventHandler eventHandler;

	/**
	 * Connects to a server on the given address and port.
	 * 
	 * @param hostAddress
	 *            The server's address.
	 * @param port
	 *            The server's port.
	 * @throws IOException
	 *             when connection establishing failed.
	 */
	public void connect(String hostAddress, int port) throws IOException {
		// EduLog.info("[CLIENT] Connecting to " + hostAddress.toString() +
		// " at " + port);
		socket = new Socket();
		InetSocketAddress iaddr = new InetSocketAddress(hostAddress, port);
		socket.connect(iaddr, CONNECT_TIMEOUT);
		receiver = new ClientReceiver(socket, this);
		receiver.start();
		sender = new ClientSender(socket);
		sender.setUdpSocket(receiver.getUdpSocket());

		// createEchoSocket();
	}

	/**
	 * Sends a message to Server
	 * 
	 * @param message
	 *            message to send
	 * @param packetType
	 *            tells whether the packet is a UDP or TCP packet
	 */
	public void sendMessage(String message, PacketType packetType) {
		try {
			sender.sendMessage(message, packetType);
		} catch (ConnectionLostException e) {
			connectionLost();
		}

	}

	/**
	 * Sets owner id to given id.
	 * 
	 * @param userId
	 *            new owner id.
	 */
	public void setOwnerId(int userId) {
		this.clientId = userId;
	}

	/**
	 * Returns current owner id.
	 * 
	 * @return current owner id.
	 */
	public int getOwnerId() {
		return clientId;
	}

	/**
	 * Sets the network event listener. This replaces any old listener.
	 * 
	 * @param listener
	 *            the new listener.
	 * 
	 * @author illonis
	 */
	public void setNetworkEventListener(NetworkEventHandler listener) {
		this.networkEventHandler = listener;
	}

	/**
	 * Invokes connection lost action.
	 */
	public void connectionLost() {
		networkEventHandler.onConnectionLost();
		receiver.interrupt();
	}

	/**
	 * Invokes disconnect action.
	 */
	public void disconnect() {
		networkEventHandler.onDisconnect();

		if (receiver != null)
			receiver.interrupt();
		if (socket != null)
			try {
				socket.close();
			} catch (IOException e) {
				// EduLog.passException(e);
				e.printStackTrace();
			}
	}

	/**
	 * Returns the number of the port this client's socket is bound to.
	 * 
	 * @return The number of the local port.
	 */
	public int getPortNumber() {
		return socket.getLocalPort();
	}

	public void setEventHandler(EventHandler handler) {
		this.eventHandler = handler;
	}
}
