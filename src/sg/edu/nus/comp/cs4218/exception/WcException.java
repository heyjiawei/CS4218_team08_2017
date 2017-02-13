package sg.edu.nus.comp.cs4218.exception;

public class WcException extends AbstractApplicationException {

	// Need serial number?

	public WcException(String message) {
		super("wc: " + message);
	}
}
