package de.eduras.eventingserver;

/**
 * Through this interface a user can specify how to parse custom objects.
 * 
 * @author Florian Mai <florian.ren.mai@googlemail.com>
 * 
 */
public interface UserSpecificTypeParser {

	/**
	 * Returns a string that represents the state of the given object.
	 * 
	 * @param objectOfSomeType
	 *            Some object you may want to parse.
	 * @return The object represented as a string.
	 * @throws IllegalArgumentException
	 *             You MUST throw this exception if you are not going to be able
	 *             to parse the given object.
	 */
	public String parseObjectToString(Object objectOfSomeType)
			throws IllegalArgumentException;

	/**
	 * Parses the given string to the object it represents.
	 * 
	 * @param objectStr
	 *            The string to be parsed.
	 * @return The object it represents.
	 */
	public Object parseStringToObject(String objectStr);

}
