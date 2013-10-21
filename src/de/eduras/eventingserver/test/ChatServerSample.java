package de.eduras.eventingserver.test;

import de.eduras.eventingserver.Server;
import de.eduras.eventingserver.ServerInterface;

public class ChatServerSample {

	public static void main(String[] args) {
		ServerInterface server = new Server();
		server.setEventHandler(new ChatEventHandlerServer(server));
		server.setNetworkEventHandler(new ChatNetworkEventHandler());

		server.start("Chatserver", 6666);
	}

}
