package de.eduras.eventingserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.eduras.eventingserver.exceptions.ConnectionLostException;
import de.illonis.edulog.EduLog;

/**
 * Receives incoming messages for the client.
 * 
 * @author Florian Mai <florian.ren.mai@googlemail.com>
 * 
 */
class ClientReceiver extends Thread {

	private final static Logger L = EduLog.getLoggerFor(ClientReceiver.class
			.getName());

	private BufferedReader messageReader = null;

	private final Client client;
	private final Buffer inputBuffer;
	private ClientParser clientParser;
	private UDPMessageReceiver udpMessageReceiver;

	private DatagramSocket udpSocket;

	/**
	 * Retrieves messages from server.
	 * 
	 * @param socket
	 *            The socket receiving on.
	 * @param client
	 *            The associated client.
	 * @throws ConnectionLostException
	 */
	public ClientReceiver(Socket socket, Client client)
			throws ConnectionLostException {

		this.client = client;
		inputBuffer = new Buffer();
		setName("ClientReceiver for #" + client.getOwnerId());
		try {
			messageReader = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
		} catch (IOException e) {
			L.log(Level.SEVERE,
					"IOException appeared when initializing client.", e);
		}

		try {
			udpSocket = new DatagramSocket(client.getLocalPortNumber());
		} catch (SocketException e) {
			L.log(Level.SEVERE,
					"SocketException appeared when opening UDP socket on client.",
					e);
			interrupt();
			throw new ConnectionLostException();
		}

	}

	/**
	 * Returns the DatagramSocket the receiver receives udp messages on.
	 * 
	 * @return The DatagramSocket.
	 */
	public DatagramSocket getUdpSocket() {
		return udpSocket;
	}

	@Override
	public void run() {
		clientParser = new ClientParser(inputBuffer, client);
		clientParser.start();

		udpMessageReceiver = new UDPMessageReceiver();
		udpMessageReceiver.start();

		while (!interrupted()) {
			try {
				String messages = messageReader.readLine();
				if (messages != null) {
					L.finest("Received message via TCP: " + messages);
					processMessages(messages);
				}
			} catch (IOException e) {
				L.log(Level.SEVERE,
						"IOException appeared when receiving TCP data on client.",
						e);
				client.connectionLost();
				interrupt();
				return;
			}
		}
	}

	@Override
	public void interrupt() {
		if (clientParser != null) {
			clientParser.interrupt();
		}
		if (udpMessageReceiver != null) {
			udpMessageReceiver.interrupt();
		}
		super.interrupt();
	}

	/**
	 * Listens to UDP messages and processes them.
	 * 
	 * @author Florian Mai <florian.ren.mai@googlemail.com>
	 * 
	 */
	class UDPMessageReceiver extends Thread {

		public static final int MAX_UDP_SIZE = 1024;

		public UDPMessageReceiver() {
			setName("UDPMessageReceiver for #" + client.getClientId());
		}

		@Override
		public void run() {

			while (!interrupted()) {
				DatagramPacket packet = new DatagramPacket(
						new byte[MAX_UDP_SIZE], MAX_UDP_SIZE);
				try {
					udpSocket.receive(packet);

					String messages = new String(packet.getData(), 0,
							packet.getLength());
					processMessages(messages);
				} catch (IOException e) {
					L.log(Level.SEVERE,
							"IOException appeared when receiving UDP data on client.",
							e);
					client.connectionLost();
					interrupt();
					return;
				}
			}
			udpSocket.close();
		}

		@Override
		public void interrupt() {
			super.interrupt();
			udpSocket.close();
		}
	}

	/**
	 * Forwards messages to the ClientLogic, where they are deserialized and
	 * forwarded to the GameLogic.
	 * 
	 * @param messages
	 *            The message(s)-string to be forwarded.
	 */
	private void processMessages(String messages) {
		inputBuffer.append(messages);

	}
}
