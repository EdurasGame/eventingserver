package de.eduras.eventingserver;

public class SplitByLastFullMessagePolicy implements SplitPolicy {

	@Override
	public int determineSplitLocation(int maxLocation, String stringToSplit)
			throws CannotSplitException {
		String[] singleMessages = StringUtils.splitIntoStringsStartingWith(
				stringToSplit, "##");

		int lengthTotal = 0;
		for (int i = 0; i < singleMessages.length; i++) {

			int lengthWithNextString = lengthTotal + singleMessages[i].length();

			if (lengthWithNextString > maxLocation) {
				break;
			}

			lengthTotal = lengthWithNextString;
		}

		if (lengthTotal == 0) {
			throw new CannotSplitException(stringToSplit, maxLocation);
		}
		return lengthTotal;
	}
}
