package de.eduras.eventingserver;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;

import de.eduras.eventingserver.Event.PacketType;
import de.eduras.eventingserver.exceptions.TooFewArgumentsExceptions;
import de.eduras.eventingserver.test.NoSuchClientException;

/**
 * A server that handles a game and its clients.
 * <p>
 * (fma)The common workflow for the server is as follows:<br>
 * First you use the default constructor to create a new server. <br>
 * Then you set the initial game and the logic (must implement
 * {@link GameLogicInterface}). <br>
 * At last use {@link #start()} to make your server listen to clients and start
 * working.(/fma)
 * </p>
 * 
 * @author illonis
 */
public class Server implements ServerInterface {

	private int port;
	private String name;

	EventHandler eventHandler;
	ServerSender serverSender;
	ServerReceiver serverReceiver;
	ServerDecoder decoder;
	final HashMap<Integer, ServerClient> clients;
	boolean running;
	InternalMessageHandler internalMessageHandler;
	ServerNetworkEventHandler networkEventHandler;
	private int maxClients;

	/**
	 * Creates a new server.
	 * 
	 * @param port
	 *            port server listens on.
	 * @param serverName
	 *            name of the server.
	 */
	public Server() {
		running = false;
		port = -1;
		name = "Unknown";
		serverReceiver = new ServerReceiver(this);
		serverSender = new ServerSender(this);
		clients = new HashMap<Integer, ServerClient>();
		decoder = new ServerDecoder(serverReceiver.inputBuffer, this);
		networkEventHandler = new DefaultServerNetworkEventHandler();
		maxClients = 5;
	}

	/**
	 * @return name of this server.
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * Handles a new connection and assigns a new ServerReceiver to it.
	 * 
	 * @param clientSocket
	 *            Socket to handle.
	 * @throws IOException
	 */
	private void handleConnection(Socket clientSocket) throws IOException {
		if (clients.size() == maxClients) {
			informServerFull(clientSocket);
			return;
		}
		final ServerClient client = addClient(clientSocket);

		// inform client of successful connection.
		try {
			serverSender.sendMessageToClient(client.getClientId(),
					InternalMessageHandler
							.createConnectionEstablishMessage(client
									.getClientId()), PacketType.TCP);
		} catch (NoSuchClientException e) {
			// can not happen
			e.printStackTrace();
			return;
		}

		// inform other clients of client's connection.
		serverSender.sendMessageToAll(InternalMessageHandler
				.createClientConnectedMessage(client.getClientId()),
				Event.PacketType.TCP);

		client.setConnected(true);
		serverReceiver.add(client);

		// call networkhandler
		networkEventHandler.onClientConnected(client.getClientId());
	}

	private void informServerFull(Socket clientSocket) {

		try {
			PrintWriter pw = new PrintWriter(clientSocket.getOutputStream(),
					true);
			pw.println(InternalMessageHandler.createServerFullMessage());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * A connection listener that listens for new connections and handles them.
	 * 
	 * @author illonis
	 * 
	 */
	private class ConnectionListener extends Thread {

		private final ServerSocket serverSocket;

		public ConnectionListener() throws IOException {
			setName("ConnectionListener");
			serverSocket = new ServerSocket(port);
		}

		/**
		 * Listens for new clients and passes them to client handler.
		 */
		@Override
		public void run() {

			while (running) {
				Socket client = null;
				try {
					client = serverSocket.accept();
					handleConnection(client);
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}
			try {
				serverSocket.close();
			} catch (IOException e) {
			}
		}
	}

	/**
	 * Removes client from serversender.
	 * 
	 * @param client
	 *            Client to remove.
	 * @param reason
	 */
	void kickClient(ServerClient client, String reason) {
		// inform clients
		serverSender.sendMessageToAll(
				InternalMessageHandler.createClientKickedMessage(
						client.getClientId(), reason), Event.PacketType.TCP);

		handleClientDisconnect(client);
	}

	/**
	 * Removes a client
	 * 
	 * @param client
	 *            The client.
	 */
	void handleClientDisconnect(ServerClient client) {

		if (!client.isConnected()) {
			return;
		}

		// interrupt client's activities correctly
		removeClient(client);
		client.setConnected(false);
		try {
			client.closeConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// inform clients
		serverSender.sendMessageToAll(InternalMessageHandler
				.createClientDisconnectedMessage(client.getClientId()),
				Event.PacketType.TCP);

		// inform callback
		networkEventHandler.onClientDisconnected(client.getClientId());
	}

	/**
	 * Returns the serverclient that handles given client.
	 * 
	 * @param ownerId
	 *            id of client.
	 * @return the client with given id.
	 * 
	 * @author illonis
	 * @throws NoSuchClientException
	 */
	ServerClient getClientById(int clientId) throws NoSuchClientException {
		ServerClient client = clients.get(clientId);
		if (client == null)
			throw new NoSuchClientException(clientId);
		return client;
	}

	/**
	 * Returns the port to which the server is bound.
	 * 
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Creates ServerClient of given socket and adds it to senderlist so it
	 * reveives messages from server.
	 * 
	 * @param client
	 *            Socket of the client to add.
	 * @return Returns the created ServerClient.
	 * @throws IOException
	 *             Is thrown if the socket is somehow broken.
	 */
	ServerClient addClient(Socket client) throws IOException {
		int clientId = getFreeClientId();

		ServerClient serverClient = new ServerClient(clientId, client);
		clients.put(clientId, serverClient);
		serverSender.addClient(serverClient);

		return serverClient;
	}

	/**
	 * Returns a number that has not been assigned to any client currently
	 * connected.
	 * 
	 * @return The free number.
	 * 
	 *         Returns -1 if there cannot be found a number within INT_MAX. I
	 *         guess this will never ever happen.
	 */
	private int getFreeClientId() {

		for (int i = 0; i >= 0; i++) {
			if (!clients.containsKey(new Integer(i))) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * Removes given client from senderlist so it does not receive any messages.
	 * 
	 * @param client
	 *            Client to remove.
	 */
	void removeClient(ServerClient client) {
		clients.remove(client.getClientId());
		serverSender.removeClient(client.getClientId());
	}

	@Override
	public boolean start(String name, int port) {

		this.name = name;
		this.port = port;

		running = true;

		serverSender.start();
		serverReceiver.start();
		decoder.start();

		try {
			ConnectionListener cl = new ConnectionListener();
			cl.start();
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	@Override
	public boolean stop() {
		running = false;
		return true;
	}

	@Override
	public void setPolicy(NetworkPolicy policy) {
		serverSender.networkPolicy = policy;
	}

	@Override
	public boolean sendEventToClient(Event event, int clientId)
			throws NoSuchClientException, IllegalArgumentException,
			TooFewArgumentsExceptions {
		serverSender.sendEventToClient(event, clientId);
		return true;
	}

	@Override
	public boolean sendEventToAll(Event event) throws IllegalArgumentException,
			TooFewArgumentsExceptions {
		serverSender.sendEventToAll(event);
		return true;
	}

	@Override
	public LinkedList<Integer> getClients() {
		return new LinkedList<Integer>(clients.keySet());
	}

	@Override
	public boolean kickClient(int clientId) {
		return kickClient(clientId, "");
	}

	@Override
	public boolean kickClient(int clientId, String reason) {
		if (!InternalMessageHandler.isCompatibleString(reason)) {
			return false;
		}

		ServerClient client;
		try {
			client = getClientById(clientId);
		} catch (NoSuchClientException e) {
			e.printStackTrace();
			return false;
		}
		kickClient(client, reason);
		return true;
	}

	@Override
	public boolean setEventHandler(EventHandler eventHandler) {
		this.eventHandler = eventHandler;
		return true;
	}

	@Override
	public boolean setNetworkEventHandler(ServerNetworkEventHandler handler) {
		this.networkEventHandler = handler;
		return true;
	}

	@Override
	public void setMaximumClients(int max) {
		maxClients = max;
	}

	@Override
	public int getMaximumClients() {
		return maxClients;
	}
}
