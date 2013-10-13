package de.eduras.eventingserver;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;

import Exceptions.BufferIsEmptyException;

import de.eduras.eventingserver.Event.PacketType;

/**
 * A class that sends collected messages every {@value #SEND_INTERVAL} ms.
 * 
 * @author illonis
 * 
 */
public class ServerSender extends Thread {

	/**
	 * Message send interval
	 */
	private final static int SEND_INTERVAL = 33;

	private DatagramSocket udpSocket;
	private final Buffer outputBufferUDP;
	private final Buffer outputBufferTCP;
	private final Server server;

	NetworkPolicy networkPolicy;

	/**
	 * Creates a new ServerSender that sends messages from given Buffer.
	 * 
	 * @param server
	 *            Target server.
	 */
	public ServerSender(Server server) {
		this.outputBufferUDP = new Buffer();
		this.outputBufferTCP = new Buffer();
		try {
			this.udpSocket = new DatagramSocket();
		} catch (SocketException e) {
			server.stop();
		}
		this.setName("ServerSender");

		this.server = server;
	}

	/**
	 * Sends a serialized message to all receivers as TCP message.
	 * 
	 * @param message
	 *            Message to send.
	 */
	private void sendTCPMessage(String message) {
		for (ServerClient serverClient : server.clients.values()) {
			PrintWriter pw = serverClient.getOutputStream();
			pw.println(message);
		}
	}

	/**
	 * Sends a serialized message to all receivers as UDP message.
	 * 
	 * @param message
	 */
	private void sendUDPMessage(String message) {
		for (ServerClient client : server.clients.values()) {
			SocketAddress clientAddress = client.getUdpAddress();
			byte[] messageAsBytes = message.getBytes();
			try {
				DatagramPacket packet = new DatagramPacket(messageAsBytes,
						messageAsBytes.length, clientAddress);
				udpSocket.send(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}

			// EduLog.info("Server.networking.msgsend");
		}
	}

	/**
	 * Sends a message to a single client identified by its id.
	 * 
	 * @param clientId
	 *            The id of the client.
	 * @param message
	 *            the serialized message that should be sent.
	 * @param packetType
	 *            Tells whether the message is sent via UDP or TCP
	 */
	void sendMessageToClient(int clientId, String message, PacketType packetType) {
		if (packetType == PacketType.TCP) {
			PrintWriter pw = server.clients.get(clientId).getOutputStream();
			pw.println(message);
		} else {
			ServerClient serverClient = server.clients.get(clientId);
			byte[] messageAsBytes = message.getBytes();
			InetAddress clientaddress = serverClient.getSocket()
					.getInetAddress();
			int port = serverClient.getSocket().getPort();
			DatagramPacket packet = new DatagramPacket(messageAsBytes,
					messageAsBytes.length, clientaddress, port);
			try {
				udpSocket.send(packet);
			} catch (IOException e) {
				// EduLog.passException(e);
				e.printStackTrace();
			}

		}

	}

	@Override
	public void run() {
		while (server.running) {
			sendAllMessages();
			try {
				Thread.sleep(SEND_INTERVAL);
			} catch (InterruptedException e) {
				e.printStackTrace();
				// EduLog.passException(e);
			}
		}
	}

	/**
	 * Retrieves all messages from outputBuffer and sends them to all clients.
	 */
	private void sendAllMessages() {
		sendTCPBufferContent();
		sendUDPBufferContent();
	}

	private void sendTCPBufferContent() {
		try {
			String[] s = outputBufferTCP.getAll();
			// String[] filtereds = NetworkOptimizer.filterObsoleteMessages(s);
			String message = NetworkMessageSerializer.concatenate(s);

			if (message.equals("")) {
				// EduLog.warning("Message empty!!");
				return;
			}
			// EduLog.infoL("Server.networking.sendall");
			sendTCPMessage(message);
		} catch (BufferIsEmptyException e) {
			// do nothing if there is no message.
		}
	}

	private void sendUDPBufferContent() {
		try {
			String[] s = outputBufferUDP.getAll();
			// String[] filtereds = NetworkOptimizer.filterObsoleteMessages(s);
			String message = NetworkMessageSerializer.concatenate(s);

			if (message.equals("")) {
				// EduLog.warning("Message empty!!");
				return;
			}
			sendUDPMessage(message);
			// EduLog.infoL("Server.networking.sendall");
		} catch (BufferIsEmptyException e) {
			// do nothing if there is no message.
		}
	}

	/**
	 * Sends an event to all clients.
	 * 
	 * @param event
	 *            The event
	 */
	public void sendEventToAll(Event event) {
		String eventAsString;
		eventAsString = NetworkMessageSerializer.serializeEvent(event);

		if (networkPolicy.determinePacketType(event) == PacketType.TCP) {
			outputBufferTCP.append(eventAsString);
		} else {
			outputBufferUDP.append(eventAsString);
		}
	}

	/**
	 * Sends an event to the specified client.
	 * 
	 * @param event
	 *            The event to send to the client.
	 * @param clientId
	 *            The client's identifier.
	 */
	public void sendEventToClient(Event event, int clientId) {
		String eventAsString;
		eventAsString = NetworkMessageSerializer.serializeEvent(event);
		PacketType packetType = networkPolicy.determinePacketType(event);
		sendMessageToClient(clientId, eventAsString, packetType);

	}
}
