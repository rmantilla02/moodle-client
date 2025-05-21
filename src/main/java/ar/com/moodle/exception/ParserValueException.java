package ar.com.moodle.exception;

public class ParserValueException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ParserValueException(String message, Throwable cause) {
		super(message, cause);
	}

	public ParserValueException(String message) {
		super(message);
	}

}
