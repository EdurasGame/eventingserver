package de.eduras.eventingserver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.illonis.edulog.EduLog;

public class NetworkUtilities {

	private final static Logger L = EduLog.getLoggerFor(NetworkUtilities.class
			.getName());

	public static void sendAllDataInPacketsOfMaxSize(DatagramSocket udpSocket,
			String message, int maxSize, SocketAddress addressee,
			SplitPolicy splitPolicy) throws IOException {
		DatagramPacket udpPacket;
		byte[] data = message.getBytes();
		final int numberOfBytesPerChar = 1;

		while (data.length > 0) {

			// if no split policy is defined, just always fill the packets at
			// max
			int lengthOfStringToSendThisTime = maxSize / numberOfBytesPerChar;
			if (data.length > maxSize) {
				if (splitPolicy != null) {
					try {
						lengthOfStringToSendThisTime = splitPolicy
								.determineSplitLocation(maxSize
										/ numberOfBytesPerChar, message);
					} catch (CannotSplitException e) {
						L.log(Level.SEVERE, "Cannot split message " + message,
								e);
						return;
					}
				}
			}

			int bytesToSendThisTime = Math.min(numberOfBytesPerChar
					* lengthOfStringToSendThisTime, data.length);

			udpPacket = new DatagramPacket(data, 0, bytesToSendThisTime,
					addressee);

			udpSocket.send(udpPacket);

			if (bytesToSendThisTime != data.length) {
				message = message.substring(lengthOfStringToSendThisTime);
				data = message.getBytes();
				if (!message.startsWith("##")) {
					L.severe("Something went wrong with splitting messages. Message : "
							+ message);
				}

			} else {
				break;
			}

		}
	}
}
