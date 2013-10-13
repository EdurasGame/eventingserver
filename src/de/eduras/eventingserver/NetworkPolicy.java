package de.eduras.eventingserver;

import java.util.Collection;

import de.eduras.eventingserver.Event.PacketType;

/**
 * This interface provides some functionality that can be used to determine
 * network behavior.
 * 
 * @author Florian Mai <florian.ren.mai@googlemail.com>
 * 
 */
public abstract class NetworkPolicy {

	/**
	 * Determines for each element of a collection of events whether the event
	 * shall be transmitted via UDP or TCP.
	 * 
	 * @param events
	 *            The collection of events to be split up into TCP and UDP
	 *            events.
	 * @param udpEvent
	 *            A collection that will be filled with UDP events after the
	 *            function returns.
	 * @param tcpEvent
	 *            A collection that will be filled with TCP events after the
	 *            function returns.
	 * 
	 *            The events collection will stay untouched. The behavior is
	 *            undefined, if one of the collections udpEvent or tcpEvent is
	 *            not empty when calling the function.
	 */
	public void determinePacketType(Collection<Event> events,
			Collection<Event> udpEvent, Collection<Event> tcpEvent) {
		for (Event singleEvent : events) {
			PacketType networkType = determinePacketType(singleEvent);
			if (networkType == PacketType.UDP)
				udpEvent.add(singleEvent);
			else
				tcpEvent.add(singleEvent);
		}
	}

	/**
	 * Determines for the given event whether it shall be transmitted via UDP or
	 * TCP.
	 * 
	 * @param event
	 * @return Returns UDP or TCP.
	 */
	public abstract PacketType determinePacketType(Event event);
}
