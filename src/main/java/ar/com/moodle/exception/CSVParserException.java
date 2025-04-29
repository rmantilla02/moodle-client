package ar.com.moodle.exception;

public class CSVParserException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CSVParserException(String message, Throwable cause) {
		super(message, cause);
	}

	public CSVParserException(String message) {
		super(message);
	}

}
