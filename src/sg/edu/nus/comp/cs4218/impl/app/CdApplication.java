package sg.edu.nus.comp.cs4218.impl.app;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.exception.CatException;
import sg.edu.nus.comp.cs4218.exception.CdException;

/**
 * The echo command writes its arguments separated by spaces and terminates by a
 * newline on the standard output.
 * 
 * <p>
 * <b>Command format:</b> <code>echo [ARG]...</code>
 * </p>
 */
public class CdApplication implements Application {

	/**
	 * Runs the cd application with the specified arguments.
	 * 
	 * @param args
	 *            Array of arguments for the application.
	 * @param stdin
	 *            An InputStream, not used.
	 * @param stdout
	 *            An OutputStream, not used.
	 * 
	 * @throws CdException
	 *             If an exception occurs.
	 */
	public void run(String[] args, InputStream stdin, OutputStream stdout) throws CdException {
		if (args == null) {
			throw new CdException("Null arguments");
		}
		// Go to $HOME directory if no arguments (like in standard shell)
		if (args.length == 0) {
			String homeDirectoryString = System.getProperty("user.home");
			System.setProperty("user.dir", homeDirectoryString);
		} else {
			String filePathString = args[0];
			File file = new File(filePathString);
			if (file.isAbsolute()) {
				throwIfInvalidDirectory(file);
				System.setProperty("user.dir", filePathString);
			} else {
				String currentWorkingDirectoryString = System.getProperty("user.dir");
				File currentWorkingDirectoryFile = new File(currentWorkingDirectoryString);
				File newWorkingDirectoryFile = new File(currentWorkingDirectoryFile,
						filePathString);
				throwIfInvalidDirectory(file);
				String newWorkingDirectoryFileString = newWorkingDirectoryFile.getPath();
				System.setProperty("user.dir", newWorkingDirectoryFileString);
			}
		}
	}
	
	/**
	 * Checks if a file is a valid directory.
	 * 
	 * @param file
	 *            The file
	 * @throws CdException
	 *             If the file is not a valid directory
	 */
	private void throwIfInvalidDirectory(File file) throws CdException {
		if (!file.isDirectory()) {
			throw new CdException("String given is an invalid directory.");
		}
	}
}
