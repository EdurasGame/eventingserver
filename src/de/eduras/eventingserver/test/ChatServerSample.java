package de.eduras.eventingserver.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

import de.eduras.eventingserver.Event;
import de.eduras.eventingserver.Server;
import de.eduras.eventingserver.ServerInterface;
import de.eduras.eventingserver.exceptions.TooFewArgumentsExceptions;
import de.illonis.edulog.EduLog;

public class ChatServerSample {

	public static void main(String[] args) {
		SimpleDateFormat simpleDate = new SimpleDateFormat("y-M-d-H-m-s");

		try {
			EduLog.init(simpleDate.format(new Date()) + "-server.log", 2097152);
		} catch (IOException e) {
			e.printStackTrace();
		}

		EduLog.setConsoleLogLimit(Level.SEVERE);

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
					int clientId = Integer.parseInt(userInput.split(" ")[0]);
					String reason = userInput.split(" ")[1];
					server.kickClient(clientId, reason);
				}
				if (userInput.equals("/help")) {
					System.out.println("Available commands:");
					System.out.println("/stop");
					System.out.println("/clients");
					System.out.println("/kick <clientId>");
				}
				if (userInput.equals("/udpdata")) {

					for (int i = 0; i < 50; i++) {
						Event massUdpData = new Event(
								ChatEventHandlerServer.MASS_UDP);
						String spamString = i
								+ "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
								+ i;
						massUdpData.putArgument(spamString);

						Event massUdpData2 = new Event(
								ChatEventHandlerServer.MASS_UDP);
						massUdpData2.putArgument(i
								+ "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb" + i);

						Event massUdpData3 = new Event(
								ChatEventHandlerServer.MASS_UDP);
						massUdpData3
								.putArgument(i
										+ "cccccccccccccccccccccccccccccccccccccccccccc"
										+ i);

						try {
							server.sendEventToAll(massUdpData);
							server.sendEventToAll(massUdpData2);
							server.sendEventToAll(massUdpData3);
						} catch (IllegalArgumentException
								| TooFewArgumentsExceptions e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}

			} else {
				System.out
						.println("This is not a command. Type /help to see commands.");
			}
		}

		server.stop();
	}
}
