package de.eduras.eventingserver;

import java.util.LinkedList;

import de.eduras.eventingserver.exceptions.TooFewArgumentsExceptions;

public class Event {

	public enum PacketType {
		TCP, UDP;
	}

	private int eventNumber;

	/**
	 * A list of arguments that the event contains.
	 */
	protected LinkedList<Object> arguments;

	/**
	 * Creates an {@link Event} with the given {@link EventNumber}.
	 * 
	 * @param eventNumber
	 *            The event number.
	 */
	public Event(int eventNumber) {
		arguments = new LinkedList<Object>();
		this.eventNumber = eventNumber;
	}

	public int getEventNumber() {
		return eventNumber;
	}

	public void putArgument(Object object) {
		arguments.add(object);
	}

	public int getNumberOfArguments() {
		return arguments.size();
	}

	public Object getArgument(int i) throws TooFewArgumentsExceptions {
		if (i >= arguments.size()) {
			throw new TooFewArgumentsExceptions(i, arguments.size());
		}
		return arguments.get(i);
	}
}
