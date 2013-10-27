package de.eduras.eventingserver.utils;

/**
 * A pair holding two objects of given type.
 * 
 * @author illonis
 * 
 * @param <F>
 *            type of first object.
 * @param <S>
 *            type of second object.
 */
public class Pair<F, S> {

	private final F first;
	private final S second;

	/**
	 * Creates a new pair with given object.
	 * 
	 * @param first
	 *            first object.
	 * @param second
	 *            second object.
	 */
	public Pair(F first, S second) {
		this.first = first;
		this.second = second;
	}

	/**
	 * Returns first object.
	 * 
	 * @return the first object.
	 * 
	 * @author illonis
	 */
	public F getFirst() {
		return first;
	}

	/**
	 * Returns second object.
	 * 
	 * @return the second object.
	 * 
	 * @author illonis
	 */
	public S getSecond() {
		return second;
	}

}
