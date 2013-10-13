package de.eduras.eventingserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;

import de.eduras.eventingserver.Event.PacketType;

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

	ServerSender serverSender;
	ServerReceiver serverReceiver;
	final HashMap<Integer, ServerClient> clients;
	boolean running;
	InternalMessageHandler internalMessageHandler;

	/**
	 * Creates a new server.
	 * 
	 * @param port
	 *            port server listens on.
	 * @param serverName
	 *            name of the server.
	 */
	public Server() {
		port = -1;
		name = "Unknown";
		serverSender = new ServerSender(this);
		serverReceiver = new ServerReceiver();
		clients = new HashMap<Integer, ServerClient>();
	}

	/**
	 * @return name of this server.
	 */
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
		final ServerClient client = addClient(clientSocket);

		// inform client about clientconnection.
		serverSender.sendMessageToClient(client.getClientId(),
				InternalMessageHandler.CONNECTION_ESTABLISHED, PacketType.TCP);

		client.setConnected(true);

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
	 */
	public void kickClient(ServerClient client) {
		removeClient(client);
		client.isConnected();
	}

	/**
	 * Removes a player from the logic if the correlating client disconnected.
	 * 
	 * @param client
	 *            The client.
	 */
	public void handleClientDisconnect(ServerClient client) {

		kickClient(client);

		try {
			client.closeConnection();
		} catch (IOException e) {
		}
	}

	/**
	 * Returns the serverclient that handles given client.
	 * 
	 * @param ownerId
	 *            id of client.
	 * @return the client with given id.
	 * 
	 * @author illonis
	 */
	public ServerClient getClientById(int clientId) {
		return clients.get(clientId);
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
	}

	@Override
	public boolean start(String name, int port) {

		this.name = name;
		this.port = port;

		serverSender.start();
		serverReceiver.start();

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
		// TODO Auto-generated method stub

	}

	@Override
	public boolean sendEventToClient(Event event, int clientId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean sendEventToAll(Event event) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public LinkedList<Integer> getClients() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean kickClient(int clientId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setEventHandler(EventHandler eventHandler) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setNetworkEventHandler(NetworkEventHandler handler) {
		// TODO Auto-generated method stub
		return false;
	}
}
