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
	 *            Array of arguments for the application. Only the first argument, which
	 *            is assumed to be the file path string to change directory to, is used.
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
			String targetDirectoryPathString = args[0];
			File targetDirectory = new File(targetDirectoryPathString);
			if (targetDirectory.isAbsolute()) {
				throwIfInvalidDirectory(targetDirectory);
				System.setProperty("user.dir", targetDirectoryPathString);
			} else {
				// Do some processing to get an absolute path from the given string
				String newWorkingDirectoryFileString =
						getAbsoluteDirectoryPathFromRelativePath(targetDirectoryPathString);
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

	/**
	 * Obtain an absolute directory path from the current working directory
	 * given a string representing a relative path.
	 *
	 * @param relativePathString
	 *            The string representing a relative path
	 * @return The string representing the absolute path of the directory
	 * @throws CdException
	 *             If the file is not a valid directory
	 */
	private String getAbsoluteDirectoryPathFromRelativePath(String relativePathString)
			throws CdException {
		String currentWorkingDirectoryString = System.getProperty("user.dir");
		File currentWorkingDirectory = new File(currentWorkingDirectoryString);
		File absolutePathDirectory = new File(currentWorkingDirectory,
				relativePathString);
		throwIfInvalidDirectory(absolutePathDirectory);
		return absolutePathDirectory.getPath();
	}
}
