package de.eduras.eventingserver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;

import de.eduras.eventingserver.Event.PacketType;

class ServerReceiver {

	HashMap<Integer, ServerTCPReceiver> serverTCPReceivers;
	UDPMessageReceiver serverUDPReceiver;
	Server server;
	Buffer inputBuffer;

	public ServerReceiver() {
		serverTCPReceivers = new HashMap<Integer, ServerTCPReceiver>();
		serverUDPReceiver = new UDPMessageReceiver();
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
				server.stop();
			}

			while (server.running) {
				DatagramPacket packet = new DatagramPacket(
						new byte[MAX_UDP_SIZE], MAX_UDP_SIZE);
				try {
					udpSocket.receive(packet);
					String messages = new String(packet.getData(), 0,
							packet.getLength());
					if (!InternalMessageHandler.isInternalMessage(messages)) {
						inputBuffer.append(messages);
					} else {
						int clientId = server.internalMessageHandler
								.extractClientId(messages);
						ServerClient client = server.getClientById(clientId);
						if (client == null || client.isUdpSetUp()) {
							throw new Exception(
									"The message has an invalid format.");
						}

						server.serverSender.sendMessageToClient(clientId,
								InternalMessageHandler.UDP_READY,
								PacketType.TCP);

						client.setUdpAddress(packet.getSocketAddress());
						client.setUdpSetUp(true);
					}
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
