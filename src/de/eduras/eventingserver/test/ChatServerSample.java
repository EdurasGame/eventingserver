package de.eduras.eventingserver.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import de.eduras.eventingserver.Server;
import de.eduras.eventingserver.ServerInterface;

public class ChatServerSample {

	public static void main(String[] args) {
		ServerInterface server = new Server();
		server.setEventHandler(new ChatEventHandlerServer(server));
		server.setPolicy(new ChatPolicy());
		server.start("Chatserver", 6666);

		BufferedReader userInputReader = new BufferedReader(
				new InputStreamReader(System.in));
		boolean running = true;
		while (running) {

			System.out.println("Give a command:");

			String userInput;
			try {
				userInput = userInputReader.readLine();
			} catch (IOException e) {
				e.printStackTrace();
				continue;
			}

			if (userInput.startsWith("/")) {
				if (userInput.equals("/stop")) {
					System.out.println("Stopping Chatserver...");
					running = false;
				}
				if (userInput.equals("/clients")) {
					for (Integer clientId : server.getClients()) {
						System.out.println(clientId);
					}
				}
				if (userInput.startsWith("/kick")) {
					int clientId = Integer.parseInt(userInput.split(" ")[1]);
					String reason = userInput.split(" ")[2];
					server.kickClient(clientId, reason);
				}
				if (userInput.equals("/help")) {
					System.out.println("Available commands:");
					System.out.println("/stop");
					System.out.println("/clients");
					System.out.println("/kick <clientId>");
				}

			} else {
				System.out
						.println("This is not a command. Type /help to see commands.");
			}
		}

		server.stop();
	}
}
