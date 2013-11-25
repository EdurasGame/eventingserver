package de.eduras.eventingserver;

import java.util.LinkedList;

import de.eduras.eventingserver.exceptions.GivenParametersDoNotFitToEventException;
import de.eduras.eventingserver.exceptions.InvalidMessageFormatException;
import de.eduras.eventingserver.exceptions.MessageNotSupportedException;
import de.eduras.eventingserver.exceptions.TooFewArgumentsExceptions;

class NetworkMessageSerializer {

	public static UserSpecificTypeParser userSpecificParser;

	public static String serializeEvent(Event event)
			throws IllegalArgumentException, TooFewArgumentsExceptions {

		if (event == null) {
			throw new IllegalArgumentException(
					"The given event must not be null!");
		}

		String eventString = "##";
		eventString += event.getEventNumber();

		int numArgs = event.getNumberOfArguments();
		for (int i = 0; i < numArgs; i++) {
			Object argument = event.getArgument(i);

			if (argument == null) {
				throw new IllegalArgumentException(
						"You are trying to parse a null argument for event #"
								+ event.getEventNumber() + ", argument #" + i);
			}

			// delimiter
			eventString += "#";

			String type = "";
			String argumentString = "";

			if (argument instanceof Boolean) {
				type += "B";
				argumentString = argument.toString();
			}

			if (argument instanceof Double) {
				type += "D";
				argumentString = argument.toString();
			}

			if (argument instanceof Integer) {
				type += "I";
				argumentString = argument.toString();
			}

			if (argument instanceof Long) {
				type += "L";
				argumentString = argument.toString();
			}

			if (argument instanceof String) {
				if (InternalMessageHandler
						.isCompatibleString((String) argument)) {
					type += "S";
					argumentString = argument.toString();
				}
			}

			if (argument instanceof Float) {
				type += "F";
				argumentString = argument.toString();
			}

			if (argument instanceof byte[]) {
				type += "A";
				argumentString = byteArrayToString((byte[]) argument);
			}

			if (type == "" && !(userSpecificParser == null)) {
				try {
					argumentString = userSpecificParser
							.parseObjectToString(argument);
					type = "U";
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
					type = "";
				}
			}

			if (type == "") {
				throw new IllegalArgumentException(
						"The argument "
								+ argument
								+ " has an unknown type. Define a specific handler for it!");
			}

			eventString += type + argumentString;
		}

		return eventString;
	}

	private static String byteArrayToString(byte[] bytes) {
		String bytesAsString = "";

		for (int i = 0; i < bytes.length; i++) {
			bytesAsString += "b" + bytes[i];
		}
		return bytesAsString;
	}

	public static LinkedList<Event> deserializeEvent(String eventStr) {

		LinkedList<Event> events = new LinkedList<Event>();
		if (eventStr.isEmpty())
			return events;
		// EduLog.info("[DESERIALIZE] orig: " + eventString);
		String[] messages;
		messages = eventStr.substring(2).split("##");

		for (String msg : messages) {

			if (msg == null)
				continue;

			// EduLog.info("message: " + msg);

			Event event;
			try {
				event = deserializeMessage(msg);
			} catch (InvalidMessageFormatException
					| GivenParametersDoNotFitToEventException
					| MessageNotSupportedException e) {
				e.printStackTrace();
				continue;
			}
			events.add(event);

		}

		return events;
	}

	/**
	 * (jme) Deserializes given single message and returns a {@link Event}
	 * representing it.<br>
	 * See {@link #deserialize(String)}.
	 * 
	 * @param msg
	 *            Message to deserialized.
	 * @return An Event describing serialized message.
	 * @throws InvalidMessageFormatException
	 *             Thrown if message has an invalid format. Especially when it
	 *             has no or too less arguments.
	 * @throws GivenParametersDoNotFitToEventException
	 *             Thrown if generation of gameevent failed.
	 * @throws MessageNotSupportedException
	 *             Thrown if retrieved message is not supported.
	 * 
	 */
	private static Event deserializeMessage(String msg)
			throws InvalidMessageFormatException,
			GivenParametersDoNotFitToEventException,
			MessageNotSupportedException {
		if (msg.isEmpty())
			throw new InvalidMessageFormatException(
					"Message is empty (length 0)", msg);
		String[] args = msg.split("#");
		if (args.length < 1)
			throw new InvalidMessageFormatException(
					"Message has not enough arguments (less than one).", msg);

		// try to extract event type
		int typeInt;
		try {
			typeInt = Integer.parseInt(args[0]);
		} catch (NumberFormatException e) {
			throw new InvalidMessageFormatException(
					"Event id of message is no valid integer value: " + args[0]
							+ " Original message: " + msg + " Error: "
							+ e.getMessage(), msg);
		}

		Event event = new Event(typeInt);

		for (int i = 1; i < args.length; i++) {
			String singleArgumentStr = args[i];

			char type = singleArgumentStr.charAt(0);
			String objectStr = singleArgumentStr.substring(1);

			Object argumentAsObject = null;
			switch (type) {
			case 'B':
				argumentAsObject = Boolean.parseBoolean(objectStr);
				break;
			case 'I':
				argumentAsObject = Integer.parseInt(objectStr);
				break;
			case 'S':
				argumentAsObject = objectStr;
				break;
			case 'F':
				argumentAsObject = Float.parseFloat(objectStr);
				break;
			case 'D':
				argumentAsObject = Double.parseDouble(objectStr);
				break;
			case 'L':
				argumentAsObject = Long.parseLong(objectStr);
				break;
			case 'A':
				argumentAsObject = stringToByteArray(objectStr);
				break;
			case 'U':
				argumentAsObject = userSpecificParser
						.parseStringToObject(objectStr);
				break;
			default:
				break;
			}

			if (argumentAsObject == null) {
				throw new IllegalArgumentException("Can not parse the string '"
						+ singleArgumentStr
						+ " to an argument'. Define a specific handler on it!");
			}

			event.putArgument(argumentAsObject);
		}
		return event;
	}

	private static byte[] stringToByteArray(String objectStr) {
		String[] bytesAsString = objectStr.split("b");
		int length = Integer.parseInt(bytesAsString[0]);

		byte[] bytes = new byte[length];
		for (int i = 0; i < length; i++) {
			bytes[i] = Byte.parseByte(bytesAsString[i + 1], 10);
		}
		return bytes;
	}

	public static String internalMessageGetArgument(String messages, int i)
			throws Exception {

		String[] args = messages.split("#");

		if (args.length <= i + 1 || i < 0) {
			throw new Exception("There is no such argument.");
		}

		return args[i + 1];
	}

	/**
	 * Concatenates given strings.
	 * 
	 * @param messages
	 *            Messages to concatenate
	 * @return concatenated messages
	 */

	public static String concatenate(String... messages) {
		StringBuilder b = new StringBuilder();
		for (String s : messages) {
			b.append(s);
		}
		return b.toString();
	}

	/**
	 * Set the user specific parser that will be used if an argument is not one
	 * of the basic types.
	 * 
	 * @param parser
	 */
	public static void setUserSpecificParser(UserSpecificTypeParser parser) {
		userSpecificParser = parser;
	}
}
