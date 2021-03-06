package de.eduras.eventingserver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.eduras.eventingserver.utils.Pair;
import de.illonis.edulog.EduLog;

class ServerReceiver {

	private final static Logger L = EduLog.getLoggerFor(ServerReceiver.class
			.getName());

	HashMap<Integer, ServerTCPReceiver> serverTCPReceivers;
	UDPMessageReceiver serverUDPReceiver;
	Server server;
	Buffer inputBuffer;
	DatagramSocket udpSocket;

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

		public UDPMessageReceiver() {
			super("UDPMessageReceiver");
		}

		@Override
		public void run() {
			try {
				udpSocket = new DatagramSocket(server.getPort());
			} catch (SocketException e) {
				L.severe("Error when opening DatagramSocket on ServerReceiver: "
						+ e.getMessage());
				server.stop();
			}

			while (server.running) {
				DatagramPacket packet = new DatagramPacket(
						new byte[ClientReceiver.UDPMessageReceiver.MAX_UDP_SIZE],
						ClientReceiver.UDPMessageReceiver.MAX_UDP_SIZE);
				try {
					udpSocket.receive(packet);
					String messages = new String(packet.getData(), 0,
							packet.getLength());

					Pair<LinkedList<String>, String> internalAndRest = InternalMessageHandler
							.extractInternalMessage(messages);

					// handle internal
					InternalMessageHandler.handleInternalMessagesServer(server,
							internalAndRest.getFirst(),
							packet.getSocketAddress());

					inputBuffer.append(internalAndRest.getSecond());

				} catch (IOException e) {
					server.stop();
				} catch (NumberFormatException e) {
					L.severe(e.getMessage());
					continue;
				} catch (Exception e) {
					L.log(Level.SEVERE,
							"Exception occuren when handling internal messages in client.",
							e);
					continue;
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
