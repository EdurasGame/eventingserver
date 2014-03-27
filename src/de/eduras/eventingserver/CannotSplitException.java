package de.eduras.eventingserver;

public class CannotSplitException extends Exception {

	public CannotSplitException(String stringToSplit, int maxLocation) {
		super("There is no way to split '" + stringToSplit
				+ "' before location " + maxLocation);
	}
}
