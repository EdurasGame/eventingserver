package de.eduras.eventingserver.server;

import java.util.LinkedList;

import de.eduras.eventingserver.Event;
import de.eduras.eventingserver.EventHandler;
import de.eduras.eventingserver.NetworkEventHandler;
import de.eduras.eventingserver.NetworkPolicy;

public class Server implements ServerInterface {

	@Override
	public boolean start(String name, int port) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean stop() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setPolicy(NetworkPolicy policy) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean sendEventToClient(Event event, int clientId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean sendEventToAll(Event event) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public LinkedList<Integer> getClients() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean kickClient(int clientId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setEventHandler(EventHandler eventHandler) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setNetworkEventHandler(NetworkEventHandler handler) {
		// TODO Auto-generated method stub
		return false;
	}

}
