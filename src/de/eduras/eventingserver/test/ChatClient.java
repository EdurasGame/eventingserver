package de.eduras.eventingserver.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import de.eduras.eventingserver.Client;
import de.eduras.eventingserver.ClientInterface;
import de.eduras.eventingserver.Event;
import de.eduras.eventingserver.exceptions.TooFewArgumentsExceptions;

public class ChatClient {
	public static void main(String[] args) {
		ClientInterface client = new Client();
		client.setEventHandler(new ChatEventHandlerClient(client));
		client.setNetworkPolicy(new ChatPolicy());
		client.connect("localhost", 6666);

		BufferedReader userInputReader = new BufferedReader(
				new InputStreamReader(System.in));

		System.out.println("Hi! What's your name, Sir?");
		String name;
		try {
			name = userInputReader.readLine();
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}

		boolean running = true;
		while (running) {

			String userInput;
			try {
				userInput = userInputReader.readLine();
			} catch (IOException e) {
				e.printStackTrace();
				continue;
			}
			if (userInput.startsWith("/")) {
				if (userInput.equals("/quit")) {
					running = false;
				}
				if (userInput.equals("/ping")) {
					Event event = new Event(ChatEventHandlerServer.DELAY_PLS);
					event.putArgument(client.getClientId());
					event.putArgument(System.currentTimeMillis());
					try {
						client.sendEvent(event);
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (TooFewArgumentsExceptions e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else {
				Event messageEvent = new Event(
						ChatEventHandlerServer.MESSAGE_SENT_EVENT);
				messageEvent.putArgument(name);
				messageEvent.putArgument(userInput);
				try {
					client.sendEvent(messageEvent);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (TooFewArgumentsExceptions e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		client.disconnect();
	}
}
