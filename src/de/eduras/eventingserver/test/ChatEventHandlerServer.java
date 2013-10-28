package de.eduras.eventingserver.test;

import de.eduras.eventingserver.Event;
import de.eduras.eventingserver.EventHandler;
import de.eduras.eventingserver.ServerInterface;
import de.eduras.eventingserver.exceptions.TooFewArgumentsExceptions;

public class ChatEventHandlerServer implements EventHandler {

	public static final int MESSAGE_SENT_EVENT = 10;
	public static final int DELAY_PLS = 11;
	ServerInterface server;

	public ChatEventHandlerServer(ServerInterface server) {
		this.server = server;
	}

	@Override
	public void handleEvent(Event event) {
		switch (event.getEventNumber()) {
		case MESSAGE_SENT_EVENT:
			try {
				String clientName = (String) event.getArgument(0);
				String message = (String) event.getArgument(1);
				System.out.println(clientName + ": " + message);
			} catch (TooFewArgumentsExceptions e) {
				e.printStackTrace();
			}
			server.sendEventToAll(event);
			break;
		case DELAY_PLS:
			int clientId = -1;
			try {
				clientId = (int) event.getArgument(0);
			} catch (TooFewArgumentsExceptions e) {
				e.printStackTrace();
			}
			try {
				server.sendEventToClient(event, clientId);
			} catch (IllegalArgumentException | NoSuchClientException e) {
				e.printStackTrace();
			}
			break;
		}
	}
}
