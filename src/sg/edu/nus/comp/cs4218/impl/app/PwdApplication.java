package sg.edu.nus.comp.cs4218.impl.app;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.PwdException;

/**
 * The pwd command reports the current working directory followed by a newline.
 * 
 * <p>
 * <b>Command format:</b> <code>pwd</code>
 * </p>
 */
public class PwdApplication implements Application {

	/**
	 * Runs the pwd application.
	 * 
	 * @param args
	 *            Array of arguments for the application, not used.
	 * @param stdin
	 *            An InputStream, not used.
	 * @param stdout
	 *            An OutputStream. The output of the command is written to this
	 *            OutputStream.
	 * 
	 * @throws PwdException
	 *             If an I/O exception occurs.
	 */
	public void run(String[] args, InputStream stdin, OutputStream stdout)
			throws PwdException {
		if (stdout == null) {
			throw new PwdException("No output stream provided");
		}
		try {
			String currentWorkingDirectoryString = Environment.currentDirectory;
			stdout.write(currentWorkingDirectoryString.getBytes());
			String newLine = System.getProperty("line.separator");
			stdout.write(newLine.getBytes());
		} catch (IOException e) {
			throw new PwdException("IOException");
		}
	}
}
