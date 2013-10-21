package de.eduras.eventingserver.exceptions;

public class TooFewArgumentsExceptions extends Exception {

	public TooFewArgumentsExceptions(int wantedIndex, int highestIndex) {
		super("Tried to access argument #" + wantedIndex + " but only have "
				+ highestIndex + " arguments.");
	}
}
