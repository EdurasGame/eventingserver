package de.eduras.eventingserver;

import de.eduras.eventingserver.Event.PacketType;

/**
 * This NetworkPolicy is simple: Every event is sent as TCP packet.
 * 
 * @author Florian Mai <florian.ren.mai@googlemail.com>
 * 
 */
class DefaultNetworkPolicy extends NetworkPolicy {

	@Override
	public PacketType determinePacketType(Event event) {
		return PacketType.TCP;
	}
}
