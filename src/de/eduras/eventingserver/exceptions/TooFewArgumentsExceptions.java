package de.eduras.eventingserver.exceptions;

/**
 * This exception is thrown when you try to access an argument of an event that
 * does not exist. Most of the time, this will be the case when you give an i to
 * the getArgument(i) that is negative number or a number that is >= the number
 * of arguments in the event.
 * 
 * @author Florian Mai <florian.ren.mai@googlemail.com>
 * 
 */
public class TooFewArgumentsExceptions extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TooFewArgumentsExceptions(int wantedIndex, int highestIndex) {
		super("Tried to access argument #" + wantedIndex + " but only have "
				+ highestIndex + " arguments.");
	}
}
