package de.eduras.eventingserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import de.eduras.eventingserver.Event.PacketType;
import de.eduras.eventingserver.exceptions.ConnectionLostException;

/**
 * A client that connects to the game server and starts receiving and sending
 * events.
 * 
 * @author Florian Mai <florian.ren.mai@googlemail.com>
 * 
 */
public class Client implements ClientInterface {

	/**
	 * Connection timeout when connecting to server (in ms).
	 */
	public final static int CONNECT_TIMEOUT = 10000;

	private Socket socket;

	ClientNetworkEventHandler networkEventHandler;

	ClientSender sender;
	ClientReceiver receiver;

	private int clientId;
	private NetworkPolicy networkPolicy;

	EventHandler eventHandler;
	boolean connected;

	public Client() {
		clientId = -1;
		networkPolicy = new DefaultNetworkPolicy();
		networkEventHandler = new DefaultClientNetworkEventHandler();
		connected = false;
	}

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
	@Override
	public boolean connect(String hostAddress, int port) {
		// EduLog.info("[CLIENT] Connecting to " + hostAddress.toString() +
		// " at " + port);
		socket = new Socket();
		InetSocketAddress iaddr = new InetSocketAddress(hostAddress, port);
		try {
			socket.connect(iaddr, CONNECT_TIMEOUT);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		connected = true;
		try {
			receiver = new ClientReceiver(socket, this);
		} catch (ConnectionLostException e) {
			e.printStackTrace();
			networkEventHandler.onConnectionLost();
		}
		receiver.start();
		sender = new ClientSender(socket, this);
		sender.setUdpSocket(receiver.getUdpSocket());
		return true;
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

	public void setNetworkPolicy(NetworkPolicy policy) {
		this.networkPolicy = policy;
	}

	/**
	 * Invokes connection lost action.
	 */
	void connectionLost() {
		networkEventHandler.onConnectionLost();
		receiver.interrupt();
	}

	/**
	 * Invokes disconnect action.
	 */
	@Override
	public boolean disconnect() {
		if (receiver == null || socket == null)
			return false;

		if (networkEventHandler != null)
			networkEventHandler.onDisconnected();

		receiver.interrupt();

		try {
			socket.close();
		} catch (IOException e) {
			// EduLog.passException(e);
			e.printStackTrace();
			return false;
		}

		connected = false;
		return true;
	}

	/**
	 * Returns the number of the port this client's socket is bound to.
	 * 
	 * @return The number of the local port.
	 */
	int getPortNumber() {
		return socket.getLocalPort();
	}

	@Override
	public void setEventHandler(EventHandler handler) {
		this.eventHandler = handler;
	}

	@Override
	public int getClientId() {
		return clientId;
	}

	@Override
	public boolean sendEvent(Event event) {
		String eventAsString = NetworkMessageSerializer.serializeEvent(event);
		PacketType packetType = networkPolicy.determinePacketType(event);
		try {
			sender.sendMessage(eventAsString, packetType);
		} catch (ConnectionLostException e) {
			connectionLost();
			return false;
		}
		return true;
	}

	@Override
	public boolean isConnected() {
		return connected;
	}

	@Override
	public void setNetworkEventHandler(
			ClientNetworkEventHandler networkEventHandler) {
		this.networkEventHandler = networkEventHandler;
	}
}
