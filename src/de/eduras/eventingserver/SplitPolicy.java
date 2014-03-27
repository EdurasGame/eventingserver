package de.eduras.eventingserver;

public interface SplitPolicy {

	/**
	 * Splits the given string into two parts such that the first and the second
	 * part can be transmitted in two separate network packets and don't lose
	 * their semantics. The first part is chosen to be as big as possible (at
	 * least length 1) but smaller or equal to maxLocation.
	 * 
	 * @param maxLength
	 *            Determines the maximum length of the first part.
	 * @param stringToSplit
	 *            The string to be split in two parts.
	 * @return Returns the index within the string where to split.
	 * @throws CannotSplitException
	 *             Thrown if there is no way to split the string such that the
	 *             first part is non-empty and smaller or equal to maxLength and
	 *             both parts are semantically independent.
	 */
	public int determineSplitLocation(int maxLength, String stringToSplit)
			throws CannotSplitException;
}
