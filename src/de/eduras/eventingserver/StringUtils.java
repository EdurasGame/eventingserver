package de.eduras.eventingserver;

import java.util.LinkedList;

public class StringUtils {

	public static String[] splitIntoStringsStartingWith(String stringToSplit,
			String splitBy) {
		Integer[] startingIndices = findOccurences(stringToSplit, splitBy);
		String[] result = new String[startingIndices.length];

		int lastIndex = 0;
		for (int i = 0; i < startingIndices.length; i++) {

			int endsAt = stringToSplit.length();
			if (i < startingIndices.length - 1) {
				endsAt = startingIndices[i + 1];
			}

			result[i] = stringToSplit.substring(lastIndex, endsAt);
			lastIndex = endsAt;
		}
		return result;
	}

	public static Integer[] findOccurences(String stringToSearchIn,
			String stringToSearchFor) {
		LinkedList<Integer> startingIndices = new LinkedList<Integer>();
		int lastIndex = 0;

		while ((lastIndex = stringToSearchIn.indexOf(stringToSearchFor,
				lastIndex)) != -1) {
			startingIndices.add(new Integer(lastIndex));
			lastIndex += stringToSearchFor.length();
		}

		return startingIndices.toArray(new Integer[startingIndices.size()]);
	}
}
