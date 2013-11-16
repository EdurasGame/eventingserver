package de.eduras.eventingserver;

/**
 * This interface specifies methods that process incoming {@link Event}s.
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
