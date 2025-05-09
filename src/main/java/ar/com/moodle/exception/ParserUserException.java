package ar.com.moodle.exception;

public class ParserUserException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ParserUserException(String message, Throwable cause) {
		super(message, cause);
	}

	public ParserUserException(String message) {
		super(message);
	}

}
