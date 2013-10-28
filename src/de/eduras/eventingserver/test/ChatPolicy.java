package de.eduras.eventingserver.test;

import de.eduras.eventingserver.Event;
import de.eduras.eventingserver.Event.PacketType;
import de.eduras.eventingserver.NetworkPolicy;

public class ChatPolicy extends NetworkPolicy {

	@Override
	public PacketType determinePacketType(Event event) {
		switch (event.getEventNumber()) {
		case ChatEventHandlerServer.DELAY_PLS:
			return PacketType.UDP;
		}
		return PacketType.TCP;
	}

}
