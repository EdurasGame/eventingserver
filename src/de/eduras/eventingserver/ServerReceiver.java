package de.eduras.eventingserver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.LinkedList;

import de.eduras.eventingserver.utils.Pair;

class ServerReceiver {

	HashMap<Integer, ServerTCPReceiver> serverTCPReceivers;
	UDPMessageReceiver serverUDPReceiver;
	Server server;
	Buffer inputBuffer;

	public ServerReceiver(Server server) {
		this.server = server;
		serverTCPReceivers = new HashMap<Integer, ServerTCPReceiver>();
		serverUDPReceiver = new UDPMessageReceiver();
		inputBuffer = new Buffer();
	}

	void start() {
		serverUDPReceiver.start();
	}

	/**
	 * Receives UDP messages and processes them.
	 * 
	 * @author Florian Mai <florian.ren.mai@googlemail.com>
	 * 
	 */
	class UDPMessageReceiver extends Thread {

		private static final int MAX_UDP_SIZE = 1024;

		public UDPMessageReceiver() {
			super("UDPMessageReceiver");
		}

		@Override
		public void run() {
			DatagramSocket udpSocket = null;
			try {
				udpSocket = new DatagramSocket(server.getPort());
			} catch (SocketException e) {
				e.printStackTrace();
				server.stop();
			}

			while (server.running) {
				DatagramPacket packet = new DatagramPacket(
						new byte[MAX_UDP_SIZE], MAX_UDP_SIZE);
				try {
					udpSocket.receive(packet);
					String messages = new String(packet.getData(), 0,
							packet.getLength());

					Pair<LinkedList<String>, String> internalAndRest = InternalMessageHandler
							.extractInternalMessage(messages);

					// handle internal
					InternalMessageHandler.handleInternalMessagesServer(server,
							internalAndRest.getFirst(), null);

					inputBuffer.append(internalAndRest.getSecond());

				} catch (IOException e) {
					server.stop();
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	void add(ServerClient client) {
		ServerTCPReceiver sr = new ServerTCPReceiver(this, client);
		sr.start();
		serverTCPReceivers.put(client.getClientId(), sr);
	}
}
