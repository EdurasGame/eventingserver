package de.eduras.eventingserver;

/**
 * 
 * @author Florian Mai <florian.ren.mai@googlemail.com>
 * 
 */
public interface EventHandler {

	/**
	 * Handles the given {@link Event}.
	 * 
	 * @param event
	 *            The event to handle.
	 */
	public void handleEvent(Event event);

}
