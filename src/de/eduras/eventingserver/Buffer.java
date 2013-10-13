package de.eduras.eventingserver;

import java.util.NoSuchElementException;
import java.util.concurrent.LinkedBlockingQueue;

import Exceptions.BufferIsEmptyException;

/**
 * A Buffer is a thread-safe linked list of Strings. Elements are returned in
 * same order they where added.
 * 
 * @author illonis
 * 
 */
public class Buffer {

	/**
	 * <b>Depreciated:</b> Do not use this object anymore as it can produce
	 * deadlocks. Buffers are now synchronized by default. Use this object to
	 */
	@Deprecated
	public final static Object SYNCER = new Object();

	private LinkedBlockingQueue<String> list;

	/**
	 * Creates a new empty buffer.
	 */
	public Buffer() {
		list = new LinkedBlockingQueue<String>();
	}

	/**
	 * Returns and removes the first element of buffer. The first element is
	 * that element that was added before all other elements.<br>
	 * Elements are returned in same order they where added.<br>
	 * <b>Note:</b> This method does not wait until an element is available. If
	 * you want to wait for an element available, use {@link #getNext()}.
	 * 
	 * @return First element of buffer.
	 * @throws BufferIsEmptyException
	 *             Thrown if list is empty.
	 */
	public String getNextIfAvailable() throws BufferIsEmptyException {
		try {
			return list.poll();
		} catch (NoSuchElementException e) {
			throw new BufferIsEmptyException();
		}
	}

	/**
	 * Waits until an element is available and returns and removes first element
	 * from buffer. The first element is that element that was added before all
	 * other elements.<br>
	 * Elements are returned in same order they where added.<br>
	 * <b>Note:</b> This method blocks and waits until an element is available.
	 * If you do not want to wait for an element available, use
	 * {@link #getNextIfAvailable()}.
	 * 
	 * @return First element of buffer.
	 * @throws InterruptedException
	 *             if interrupted while waiting
	 */
	public String getNext() throws InterruptedException {
		return list.take();
	}

	/**
	 * Appends a string at the end of this buffer.
	 * 
	 * @param string
	 *            String to add.
	 */
	public void append(String string) {
		try {
			list.put(string);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns all strings that are in this buffer as an array and removes them
	 * from buffer.
	 * 
	 * @return A string array containing all strings.
	 * @throws BufferIsEmptyException
	 *             Thrown if list is empty.
	 */
	public synchronized String[] getAll() throws BufferIsEmptyException {
		if (list.size() == 0)
			throw new BufferIsEmptyException();
		String[] msgs = new String[list.size()];
		list.toArray(msgs);
		list.clear();
		return msgs;
	}

	/**
	 * Returns all strings that are in this buffer as a single string and
	 * removes them from buffer.
	 * 
	 * @return A string containing all strings concatenated.
	 * @throws BufferIsEmptyException
	 *             Thrown if list is empty.
	 */
	public synchronized String getAllAsSingleMessage()
			throws BufferIsEmptyException {
		if (list.size() == 0)
			throw new BufferIsEmptyException();
		StringBuilder sb = new StringBuilder();
		while (true) {
			try {
				sb.append(getNextIfAvailable());
			} catch (BufferIsEmptyException e) {
				break;
			}
		}
		return sb.toString();
	}
}