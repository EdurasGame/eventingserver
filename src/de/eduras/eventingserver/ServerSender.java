package de.eduras.eventingserver;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.HashMap;

import de.eduras.eventingserver.Event.PacketType;
import de.eduras.eventingserver.exceptions.BufferIsEmptyException;
import de.eduras.eventingserver.exceptions.TooFewArgumentsExceptions;
import de.eduras.eventingserver.test.NoSuchClientException;

/**
 * A class that sends collected messages every {@value #SEND_INTERVAL} ms.
 * 
 * @author illonis
 * 
 */
class ServerSender extends Thread {

	/**
	 * Message send interval
	 */
	private final static int SEND_INTERVAL = 33;

	private DatagramSocket udpSocket;
	private final Server server;
	private HashMap<Integer, BufferSenderForClient> bufferSenders;

	NetworkPolicy networkPolicy;

	/**
	 * Creates a new ServerSender that sends messages from given Buffer.
	 * 
	 * @param server
	 *            Target server.
	 */
	public ServerSender(Server server) {
		try {
			this.udpSocket = new DatagramSocket();
		} catch (SocketException e) {
			server.stop();
		}
		this.setName("ServerSender");

		networkPolicy = new DefaultNetworkPolicy();
		this.server = server;
		bufferSenders = new HashMap<Integer, BufferSenderForClient>();
	}

	void addClient(ServerClient client) {
		BufferSenderForClient newBufferSender = new BufferSenderForClient(
				client);
		bufferSenders.put(client.getClientId(), newBufferSender);
		newBufferSender.start();
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
	 * @throws NoSuchClientException
	 */
	void sendMessageToClient(int clientId, String message, PacketType packetType)
			throws NoSuchClientException {
		if (packetType == PacketType.TCP) {
			PrintWriter pw = server.getClientById(clientId).getOutputStream();
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

	void sendMessageToAll(String message, PacketType packetType) {
		for (ServerClient client : server.clients.values()) {
			try {
				sendMessageToClient(client.getClientId(), message, packetType);
			} catch (NoSuchClientException e) {
				e.printStackTrace();
				continue;
			}
		}
	}

	@Override
	public void run() {
		while (server.running) {
			for (BufferSenderForClient bufferSender : bufferSenders.values()) {
				synchronized (bufferSender) {
					bufferSender.notify();
				}
			}
			try {
				Thread.sleep(SEND_INTERVAL);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Sends an event to all clients.
	 * 
	 * @param event
	 *            The event
	 * @throws IllegalArgumentException
	 *             Thrown if an argument in the event is illegal.
	 * @throws TooFewArgumentsExceptions
	 */
	public void sendEventToAll(Event event) throws IllegalArgumentException,
			TooFewArgumentsExceptions {
		String eventAsString;
		eventAsString = NetworkMessageSerializer.serializeEvent(event);

		for (BufferSenderForClient bufferSender : bufferSenders.values()) {

			if (networkPolicy.determinePacketType(event) == PacketType.TCP) {
				bufferSender.appendToTCPBuffer(eventAsString);
			} else {
				bufferSender.appendToUDPBuffer(eventAsString);
			}

		}
	}

	/**
	 * Sends an event to the specified client.
	 * 
	 * @param event
	 *            The event to send to the client.
	 * @param clientId
	 *            The client's identifier.
	 * @throws NoSuchClientException
	 * @throws IllegalArgumentException
	 *             Thrown when an argument in the given event is illegal.
	 * @throws TooFewArgumentsExceptions
	 */
	public void sendEventToClient(Event event, int clientId)
			throws NoSuchClientException, IllegalArgumentException,
			TooFewArgumentsExceptions {
		String eventAsString;
		eventAsString = NetworkMessageSerializer.serializeEvent(event);
		PacketType packetType = networkPolicy.determinePacketType(event);
		BufferSenderForClient bufferSender = bufferSenders.get(clientId);
		if (packetType == PacketType.TCP) {
			bufferSender.appendToTCPBuffer(eventAsString);
		} else {
			bufferSender.appendToUDPBuffer(eventAsString);
		}
	}

	class BufferSenderForClient extends Thread {
		private ServerClient client;
		private final Buffer outputBufferUDP;
		private final Buffer outputBufferTCP;

		BufferSenderForClient(ServerClient client) {
			this.client = client;

			this.outputBufferUDP = new Buffer();
			this.outputBufferTCP = new Buffer();

			this.setName("BufferSenderForClient#" + client.getClientId());
		}

		void appendToUDPBuffer(String eventAsString) {
			outputBufferUDP.append(eventAsString);
		}

		void appendToTCPBuffer(String eventAsString) {
			outputBufferTCP.append(eventAsString);
		}

		@Override
		public void run() {
			while (server.running) {
				try {
					synchronized (this) {
						BufferSenderForClient.this.wait();
					}
					sendAllMessages();
				} catch (InterruptedException e) {
					// gets interupted, when the client disconnects
					e.printStackTrace();
					break;
				}
			}
		}

		/**
		 * Retrieves all messages from outputBuffer and sends them to all
		 * clients.
		 */
		private void sendAllMessages() {
			sendTCPBufferContent();
			sendUDPBufferContent();
		}

		private void sendTCPBufferContent() {
			try {
				String[] s = outputBufferTCP.getAll();
				// String[] filtereds =
				// NetworkOptimizer.filterObsoleteMessages(s);
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
				// String[] filtereds =
				// NetworkOptimizer.filterObsoleteMessages(s);
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
		 * Sends a serialized message to all receivers as TCP message.
		 * 
		 * @param message
		 *            Message to send.
		 */
		private void sendTCPMessage(String message) {
			ServerClient serverClient = client;
			PrintWriter pw = serverClient.getOutputStream();
			pw.println(message);
		}

		/**
		 * Sends a serialized message to all receivers as UDP message.
		 * 
		 * @param message
		 */
		private void sendUDPMessage(String message) {
			if (!client.isUdpSetUp())
				return;
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

	public void removeClient(int clientId) {
		bufferSenders.get(clientId).interrupt();
		bufferSenders.remove(clientId);
	}
}
