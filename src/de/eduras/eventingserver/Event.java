package de.eduras.eventingserver;

import java.util.LinkedList;

import de.eduras.eventingserver.exceptions.TooFewArgumentsExceptions;

/**
 * An Event abstracts from a network message. An Event can contain as many
 * information as it wants. It is serialized and deserialized automatically, but
 * you will have to process the information by yourself. For a more detailed
 * description of how you should set up Events have a look on the 'Eventing
 * Server' chapter of the Eduras documentation.
 * 
 * @author Florian Mai <florian.ren.mai@googlemail.com>
 * 
 */
public class Event {

	/**
	 * Indicates what transmission technique to use; UDP or TCP.
	 * 
	 * @author Florian Mai <florian.ren.mai@googlemail.com>
	 * 
	 */
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

	/**
	 * Returns the event number.
	 * 
	 * @return The event's number.
	 */
	public int getEventNumber() {
		return eventNumber;
	}

	/**
	 * Add an argument to the event.
	 * 
	 * @param object
	 *            The argument to be added to the event.
	 */
	public void putArgument(Object object) {
		arguments.add(object);
	}

	/**
	 * Returns the total number of arguments in the event.
	 * 
	 * @return The total number of arguments.
	 */
	public int getNumberOfArguments() {
		return arguments.size();
	}

	/**
	 * Returns the i-th argument of the event.
	 * 
	 * @param i
	 *            The index.
	 * @return The argument at index i.
	 * @throws TooFewArgumentsExceptions
	 *             Called when i >= getNumberOfArguments
	 */
	public Object getArgument(int i) throws TooFewArgumentsExceptions {
		if (i >= arguments.size()) {
			throw new TooFewArgumentsExceptions(i, arguments.size());
		}
		return arguments.get(i);
	}
}
