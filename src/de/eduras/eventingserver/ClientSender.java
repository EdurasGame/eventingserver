package de.eduras.eventingserver;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;

import de.eduras.eventingserver.Event.PacketType;
import de.eduras.eventingserver.exceptions.ConnectionLostException;

/**
 * Sends messages/events to the server.
 * 
 * @author Florian Mai <florian.ren.mai@googlemail.com>
 * 
 */
class ClientSender {

	private Socket socket = null;
	private boolean active;
	private PrintWriter messageWriter = null;
	private DatagramSocket udpSocket;

	boolean isUDPSetUp = false;

	/**
	 * Creates a new ClientSender that sends messages via the given socket.
	 * 
	 * @param socket
	 *            The socket to send messages via.
	 */
	public ClientSender(Socket socket) {

		this.socket = socket;
		active = true;
		try {
			this.messageWriter = new PrintWriter(this.socket.getOutputStream(),
					true);
		} catch (IOException e) {
			active = false;
			// EduLog.passException(e);
			e.printStackTrace();
		}
	}

	/**
	 * Sends a message via the socket if available.
	 * 
	 * @param message
	 *            The message to send.
	 * @param packetType
	 *            tells if the message is a UDP or TCP message.
	 * @throws ConnectionLostException
	 *             when connection to server is lost. The client sender will not
	 *             accept any messages anymore.
	 */
	void sendMessage(String message, PacketType packetType)
			throws ConnectionLostException {
		if (active) {
			switch (packetType) {
			case TCP:
				// EduLog.info("[CLIENT] Sending message: " + message);
				messageWriter.println(message);
				if (messageWriter.checkError()) {
					// EduLog.errorL("Client.networking.senderror");
					active = false;
					close();
					throw new ConnectionLostException();
				}
				break;
			case UDP:
				byte[] data = message.getBytes();
				InetSocketAddress address = new InetSocketAddress(
						socket.getInetAddress(), socket.getPort());
				DatagramPacket udpPacket;
				try {
					udpPacket = new DatagramPacket(data, data.length, address);
					udpSocket.send(udpPacket);
				} catch (IOException e) {
					// EduLog.passException(e);
					active = false;
					close();
					throw new ConnectionLostException();
				}
				break;
			default:
			}

		}
	}

	/**
	 * Closes the output connection.
	 */
	public void close() {
		messageWriter.close();
	}

	/**
	 * Sets the socket on which UDP messages are sent.
	 * 
	 * @param udpSocket2
	 *            The socket to send UDP messages on.
	 */
	void setUdpSocket(DatagramSocket udpSocket2) {
		udpSocket = udpSocket2;
		new UDPInitializer().start();
	}

	class UDPInitializer extends Thread {
		@Override
		public void run() {
			while (!isUDPSetUp) {
				try {
					sendMessage(InternalMessageHandler.createUDPHIMessage(),
							Event.PacketType.UDP);
				} catch (ConnectionLostException e) {
					// TODO: damn, need to stop the client some day.
					e.printStackTrace();
					return;
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
					return;
				}
			}
		}
	}

}
