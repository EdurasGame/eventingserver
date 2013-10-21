package de.eduras.eventingserver.test;

import de.eduras.eventingserver.ClientInterface;
import de.eduras.eventingserver.Event;
import de.eduras.eventingserver.EventHandler;
import de.eduras.eventingserver.exceptions.TooFewArgumentsExceptions;

public class ChatEventHandlerClient implements EventHandler {

	ClientInterface client;

	public ChatEventHandlerClient(ClientInterface client) {
		this.client = client;
	}

	@Override
	public void handleEvent(Event event) {

		switch (event.getEventNumber()) {

		case ChatEventHandlerServer.MESSAGE_SENT_EVENT:
			String clientName = "";
			String message = "";
			try {
				clientName = (String) event.getArgument(0);
				message = (String) event.getArgument(1);
			} catch (TooFewArgumentsExceptions e) {
				e.printStackTrace();
			}

			System.out.println(clientName + ": " + message);
			break;
		}

	}
}
