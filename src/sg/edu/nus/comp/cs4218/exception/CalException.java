package sg.edu.nus.comp.cs4218.exception;

public class CalException extends AbstractApplicationException {

	private static final long serialVersionUID = -6297822670731803186L;

	public CalException(String message) {
		super("cal: " + message);
	}

}
