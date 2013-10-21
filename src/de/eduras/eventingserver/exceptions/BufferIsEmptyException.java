package de.eduras.eventingserver.exceptions;

import java.util.NoSuchElementException;

/**
 * This exception is thrown when you tried to read an element from a buffer that
 * is empty.
 * 
 * @author illonis
 * 
 */
public class BufferIsEmptyException extends NoSuchElementException {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a <code>BufferIsEmptyException</code> with <tt>null</tt> as
	 * its error message string.
	 */
	public BufferIsEmptyException() {
		super();
	}

	/**
	 * Constructs a <code>BufferIsEmptyException</code>, saving a reference to
	 * the error message string <tt>s</tt> for later retrieval by the
	 * <tt>getMessage</tt> method.
	 * 
	 * @param s
	 *            the detail message.
	 */
	public BufferIsEmptyException(String s) {
		super(s);
	}
}
