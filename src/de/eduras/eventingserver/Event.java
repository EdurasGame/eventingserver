package de.eduras.eventingserver;

import java.util.LinkedList;

public abstract class Event {

	/**
	 * 
	 * @author Florian Mai <florian.ren.mai@googlemail.com>
	 * 
	 */
	public enum EventNumber {

	}

	public enum PacketType {
		TCP, UDP;
	}

	private EventNumber eventNumber;

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
	public Event(EventNumber eventNumber) {
		this.eventNumber = eventNumber;
	}

}
