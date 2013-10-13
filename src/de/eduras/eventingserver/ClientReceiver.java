package de.eduras.eventingserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * Receives incoming messages for the client.
 * 
 * @author Florian Mai <florian.ren.mai@googlemail.com>
 * 
 */
class ClientReceiver extends Thread {

	private BufferedReader messageReader = null;

	private boolean connectionAvailable = true;

	private final Client client;
	private final Buffer inputBuffer;
	private ClientParser p;

	private DatagramSocket udpSocket;

	/**
	 * Retrieves messages from server.
	 * 
	 * @param socket
	 *            The socket receiving on.
	 * @param client
	 *            The associated client.
	 */
	public ClientReceiver(Socket socket, Client client) {

		this.client = client;
		inputBuffer = new Buffer();
		setName("ClientReceiver for #" + client.getOwnerId());
		try {
			messageReader = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
		} catch (IOException e) {
			// EduLog.passException(e);
			e.printStackTrace();
		}

		try {
			udpSocket = new DatagramSocket(client.getPortNumber());
		} catch (SocketException e) {
			connectionAvailable = false;
			// EduLog.errorLF("Client.networking.udpopenerror",
			// client.getPortNumber());
			// EduLog.passException(e);
			e.printStackTrace();
			interrupt();
			return;
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
		p = new ClientParser(inputBuffer, client);
		p.start();

		UDPMessageReceiver udpMessageReceiver = new UDPMessageReceiver();
		udpMessageReceiver.start();

		while (connectionAvailable) {
			try {
				String messages = messageReader.readLine();
				if (messages != null) {
					// EduLog.infoLF("Client.networking.msgreceive", messages);
					processMessages(messages);
				}
			} catch (IOException e) {
				connectionAvailable = false;
				// EduLog.error("Client.networking.tcpclose");
				// EduLog.errorL("Client.networking.tcpclose");
				// EduLog.passException(e);
				e.printStackTrace();
				interrupt();
				return;
			}
		}
	}

	@Override
	public void interrupt() {
		if (p != null)
			p.interrupt();
		super.interrupt();
	}

	/**
	 * Listens to UDP messages and processes them.
	 * 
	 * @author Florian Mai <florian.ren.mai@googlemail.com>
	 * 
	 */
	class UDPMessageReceiver extends Thread {

		private static final int MAX_UDP_SIZE = 1024;

		@Override
		public void run() {

			while (connectionAvailable) {
				DatagramPacket packet = new DatagramPacket(
						new byte[MAX_UDP_SIZE], MAX_UDP_SIZE);
				try {
					udpSocket.receive(packet);
					String messages = new String(packet.getData(), 0,
							packet.getLength());
					processMessages(messages);
				} catch (IOException e) {
					connectionAvailable = false;
					// EduLog.errorL("Client.networking.udpclose");
					// EduLog.passException(e);
					interrupt();
				}
			}
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
