package sg.edu.nus.comp.cs4218.impl.app;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.CatException;

/**
 * The cat command concatenates the content of given files and prints on the
 * standard output.
 * 
 * <p>
 * <b>Command format:</b> <code>cat [FILE]...</code>
 * <dl>
 * <dt>FILE</dt>
 * <dd>the name of the file(s). If no files are specified, use stdin.</dd>
 * </dl>
 * </p>
 */
public class CatApplication implements Application {

	/**
	 * Runs the cat application with the specified arguments.
	 * 
	 * @param args
	 *            Array of arguments for the application. Each array element is
	 *            the path to a file. If no files are specified stdin is used.
	 * @param stdin
	 *            An InputStream. The input for the command is read from this
	 *            InputStream if no files are specified.
	 * @param stdout
	 *            An OutputStream. The output of the command is written to this
	 *            OutputStream.
	 * 
	 * @throws CatException
	 *             If the file(s) specified do not exist or are unreadable.
	 */
	@Override
	@SuppressWarnings("PMD.PreserveStackTrace")
	public void run(String[] args, InputStream stdin, OutputStream stdout)
			throws CatException {
		if (stdout == null) {
			throw new CatException("No output stream provided");
		}
		if (args == null || args.length == 0) {
			if (stdin == null) {
				throw new CatException("Null Pointer Exception");
			}
			try {
				int intCount;
				while ((intCount = stdin.read()) != -1) {
					stdout.write(intCount);
				}
			} catch (Exception exIO) {
				throw new CatException("Exception Caught");
			}
		} else {
			catFiles(args, stdout);
		}
	}

	@SuppressWarnings("PMD.PreserveStackTrace")
	private void catFiles(String[] args, OutputStream stdout) throws CatException {
		int numOfFiles = args.length;

		Path filePath;
		ArrayList<Path> filePathList = new ArrayList<Path>(numOfFiles);
		Path currentDir = Paths.get(Environment.currentDirectory);
		boolean isFileReadable = false;

		for (int i = 0; i < numOfFiles; i++) {
			filePath = currentDir.resolve(args[i]);
			try {
				isFileReadable = checkIfFileIsReadable(filePath);
			} catch (CatException e) {
				// Try to continue to print any valid files
				if (numOfFiles > 1) {
					continue;
				} else {
					throw e;
				}
			}
			if (isFileReadable) {
				filePathList.add(filePath);
			}
		}

		// file could be read. perform cat command
		if (filePathList.isEmpty()) {
			throw new CatException("No valid input files provided.");
		} else {
			for (int j = 0; j < filePathList.size(); j++) {
				try {
					byte[] byteFileArray = Files
							.readAllBytes(filePathList.get(j));
					stdout.write(byteFileArray);
				} catch (IOException e) {
					throw new CatException(
							"Could not read from file or "
							+ "could not write to output stream");
				}
			}
		}
	}

	/**
	 * Checks if a file is readable.
	 * 
	 * @param filePath
	 *            The path to the file
	 * @return True if the file is readable.
	 * @throws CatException
	 *             If the file is not readable
	 */
	boolean checkIfFileIsReadable(Path filePath) throws CatException {
		if (Files.isDirectory(filePath)) {
			throw new CatException("This is a directory");
		}
		if (Files.exists(filePath) && Files.isReadable(filePath)) {
			return true;
		} else {
			throw new CatException("Could not read file");
		}
	}
}
