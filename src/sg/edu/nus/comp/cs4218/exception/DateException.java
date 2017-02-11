package sg.edu.nus.comp.cs4218.exception;

public class DateException extends AbstractApplicationException {
	
	// Need serial number?

	public DateException(String message) {
		super("date: " + message);
	}
}
