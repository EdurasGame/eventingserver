/**
 * 
 */
package de.eduras.eventingserver.exceptions;

/**
 * Exception thrown when a parameter, especially a GameEventNumber, does not fit
 * to the event the parameter was associated with.
 * 
 * @author Florian Mai <florian.ren.mai@googlemail.com>
 * 
 */
public class GivenParametersDoNotFitToEventException extends Exception {

	private static final long serialVersionUID = 1L;

	private int event;
	private String[] givenParameters;

	/**
	 * Creates a new GivenParametersDoNotFitToEventException.
	 * 
	 * @param eventNumber
	 *            Event that has been tried to create.
	 * @param parameters
	 *            Parameters that didn't fit to event.
	 */
	public GivenParametersDoNotFitToEventException(int eventNumber,
			String... parameters) {
		super();
		this.event = eventNumber;
		this.givenParameters = parameters;
	}

	/**
	 * Returns event that created exceptions.
	 * 
	 * @return Event that failed.
	 */
	public int getEvent() {
		return event;
	}

	/**
	 * Returns parameters that were used to initiate event.
	 * 
	 * @return Parameters that were used to initiate event.
	 */
	public String[] getGivenParameters() {
		return givenParameters;
	}
}