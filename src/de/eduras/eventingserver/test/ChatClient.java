package de.eduras.eventingserver.test;

import de.eduras.eventingserver.Client;
import de.eduras.eventingserver.ClientInterface;
import de.eduras.eventingserver.Event;

public class ChatClient {
	public static void main(String[] args) {
		ClientInterface client = new Client();
		client.connect("localhost", 6666);
		String chatMessage = "Sample Chat message!";
		Event chatEvent = new Event(ChatEventHandler.MESSAGE_SENT_EVENT);
		chatEvent.putArgument("Jannis");
		chatEvent.putArgument(chatMessage);
		client.sendEvent(chatEvent);
	}
}
