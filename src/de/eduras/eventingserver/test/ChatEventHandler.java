package de.eduras.eventingserver.test;

import de.eduras.eventingserver.Event;
import de.eduras.eventingserver.EventHandler;
import de.eduras.eventingserver.exceptions.TooFewArgumentsExceptions;

public class ChatEventHandler implements EventHandler {

	public static final int MESSAGE_SENT_EVENT = 10;

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
			break;
		}

	}
}
