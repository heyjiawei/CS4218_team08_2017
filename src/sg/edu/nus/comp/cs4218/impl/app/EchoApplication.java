package sg.edu.nus.comp.cs4218.impl.app;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.exception.EchoException;

/**
 * The echo command writes its arguments separated by spaces and terminates by a
 * newline on the standard output.
 * 
 * <p>
 * <b>Command format:</b> <code>echo [ARG]...</code>
 * </p>
 */
public class EchoApplication implements Application {

	/**
	 * Runs the echo application with the specified arguments.
	 * 
	 * @param args
	 *            Array of arguments for the application.
	 * @param stdin
	 *            An InputStream. Input can be passed to the application via
	 *            stdin.
	 * @param stdout
	 *            An OutputStream. Elements of args will be output to stdout,
	 *            separated by a space character.
	 * 
	 * @throws EchoException
	 *             If an I/O exception occurs.
	 */
	@SuppressWarnings("PMD.PreserveStackTrace")
	public void run(String[] args, InputStream stdin, OutputStream stdout)
			throws EchoException {
		if (args == null) {
			throw new EchoException("Null arguments");
		}
		if (stdout == null) {
			throw new EchoException("OutputStream not provided");
		}
		try {
			if (args != null && args.length > 0) {
				for (int i = 0; i < args.length; i++) {
					String argument = args[i];
					if (argument == null) {
						throw new EchoException("Null argument provided.");
					}
					if (i > 0) {
						stdout.write(" ".getBytes());
					}
					stdout.write(argument.getBytes());
				}
			}
			stdout.write("\n".getBytes());
		} catch (IOException e) {
			throw new EchoException("IOException");
		}
	}
}
