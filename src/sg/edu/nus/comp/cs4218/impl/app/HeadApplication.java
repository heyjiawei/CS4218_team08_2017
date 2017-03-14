package sg.edu.nus.comp.cs4218.impl.app;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.HeadException;
import sg.edu.nus.comp.cs4218.exception.TailException;

/**
 * The head command prints the first N lines of the file (or input stream).
 * If there are less than N lines, print existing lines without raising an exception.
 * 
 * <p>
 * <b>Command format:</b> <code>head [OPTIONS] [FILE]</code>
 * <dl>
 * <dt>OPTIONS</dt>
 * <dd>“-n 15” means printing 15 lines. Print first 10 lines if not specified.</dd>
 * <dt>FILE</dt>
 * <dd>the name of the file. If no files are specified, use stdin.</dd>
 * </dl>
 * </p>
 */
public class HeadApplication implements Application {

	final private int DEFAULT_NUMBER_OF_LINES_TO_PRINT = 10;

	/**
	 * Runs the head application with the specified arguments.
	 * 
	 * @param args
	 *            Array of arguments for the application. Only up to two arguments
	 *            are used, both of which are optional. The first argument, if it
	 *            is a valid integer, is assumed to be the number of lines to print.
	 *            If not, it is assumed to be the path of the file to read from.
	 *            If the first argument is a valid integer, the second argument is
	 *            assumed to be the path of the file to read from.
	 * @param stdin
	 *            An InputStream. The input for the command is read from this
	 *            InputStream if no files are specified.
	 * @param stdout
	 *            An OutputStream. The output of the command is written to this
	 *            OutputStream.
	 * 
	 * @throws HeadException
	 *             If the file specified does not exist or are unreadable.
	 */
	@Override
	public void run(String[] args, InputStream stdin, OutputStream stdout)
			throws HeadException {
		if (stdout == null) {
			throw new HeadException("No output stream provided");
		}
		Integer numberOfLinesToPrint = DEFAULT_NUMBER_OF_LINES_TO_PRINT;
		if (args == null || args.length == 0) {
			// print default number of lines from inputstream
			printLinesFromInputStreamToOutputStream(numberOfLinesToPrint,
					stdin, stdout);
		} else {
			String firstArgument = args[0];
			if (firstArgument == null) {
				throw new HeadException("First argument provided is null");
			}
			try {
				numberOfLinesToPrint = Integer.parseInt(firstArgument);
				
				if (numberOfLinesToPrint < 0) {
					throw new HeadException("Illegal line count: "
							+ numberOfLinesToPrint);
				}

				// assume first argument is number of lines to print
				if (args.length > 1) {
					// get path of file to read from
					String filePathString = args[1];

					if (filePathString == null) {
						throw new HeadException("Second argument provided is null");
					}

					printLinesFromFileToOutputStream(numberOfLinesToPrint,
							filePathString, stdout);
				} else {
					printLinesFromInputStreamToOutputStream(numberOfLinesToPrint,
							stdin, stdout);
				}
			} catch (NumberFormatException e) {
				// first argument is assumed to be the path of the file to read from
				// since it is not an integer
				String filePathString = args[0];
				printLinesFromFileToOutputStream(numberOfLinesToPrint,
						filePathString, stdout);
			}
		}
	}

	/**
	 * Checks if a file is readable.
	 * 
	 * @param filePath
	 *            The path to the file
	 * @return True if the file is readable.
	 * @throws HeadException
	 *             If the file is not readable
	 */
	boolean checkIfFileIsReadable(Path filePath) throws HeadException {
		
		if (Files.isDirectory(filePath)) {
			throw new HeadException("This is a directory");
		}
		if (Files.exists(filePath) && Files.isReadable(filePath)) {
			return true;
		} else {
			throw new HeadException("Could not read file");
		}
	}
	
	/**
	 * Prints N lines from the given input stream to the given output stream.
	 * If there are less than N lines, print existing lines without raising an exception.
	 * 
	 * @param numberOfLines
	 *            The number of lines (N) to print.
	 * @param stdin
	 *            An InputStream which provides the input for this function.
	 * @param stdout
	 *            An OutputStream. The output of the function is written to this
	 *            OutputStream.
	 * 
	 * @throws HeadException
	 *             If the file is not readable
	 */
	private void printLinesFromInputStreamToOutputStream(int numberOfLines,
			InputStream stdin, OutputStream stdout) throws HeadException {
		if (stdin == null) {
			throw new HeadException("No input stream provided");
		}
		try {
			InputStreamReader stdinReader = new InputStreamReader(stdin, "UTF-8");
			BufferedReader bufferedReader = new BufferedReader(stdinReader);
			printLinesFromBufferedReaderToOutputStream(numberOfLines,
					bufferedReader, stdout);
		} catch (Exception exIO) {
			throw new HeadException("Exception Caught");
		}
	}
	
	/**
	 * Prints N lines from the file at the given file path to the given output stream.
	 * If there are less than N lines, print existing lines without raising an exception.
	 * 
	 * @param numberOfLines
	 *            The number of lines (N) to print.
	 * @param filePathString
	 *            The string representing the path from which the lines to print
	 *            are read from.
	 * @param stdout
	 *            An OutputStream. The output of the function is written to this
	 *            OutputStream.
	 * 
	 * @throws HeadException
	 *             If the file is not readable or writing to the output stream
	 *             fails.
	 */
	private void printLinesFromFileToOutputStream(int numberOfLines,
			String filePathString, OutputStream stdout) throws HeadException {
		Path pathToReadFrom = getReadableFilePath(filePathString);
		try {
			FileReader fileReader = new FileReader(pathToReadFrom.toString());
		    BufferedReader bufferedReader = new BufferedReader(fileReader);
		    printLinesFromBufferedReaderToOutputStream(numberOfLines,
                    bufferedReader, stdout);
		    bufferedReader.close();
		} catch (IOException e) {
			throw new HeadException(
					"Could not write to output stream");
		}
	}
	
	/**
	 * Prints the last N lines from the given BufferedReader to the given output stream.
	 * If there are less than N lines, print existing lines without raising an exception.
	 *
	 * @param numberOfLines
	 *            The number of lines (N) to print.
	 * @param bufferedReader
	 *            A BufferedReader. The lines to print are read from this
	 *            BufferedReader.
	 * @param stdout
	 *            An OutputStream. The output of the function is written to this
	 *            OutputStream.
	 *
	 * @throws TailException
	 *             If the file is not readable or writing to the output stream
	 *             fails.
	 */
	private void printLinesFromBufferedReaderToOutputStream(int numberOfLines,
			BufferedReader bufferedReader, OutputStream stdout) throws IOException {
		String lineToPrint;
		String newLine = System.getProperty("line.separator");
	    for (int i = 0; i < numberOfLines; i++) {
			if ((lineToPrint = bufferedReader.readLine()) != null) {
				if (i != 0) {
					stdout.write(newLine.getBytes());
				}
				stdout.write(lineToPrint.getBytes());
			} else {
				break;
			}
		}
	}

	/**
	 * Returns a file path from the given path string if the path is for
	 * a readable file.
	 * 
	 * @param filePathString
	 *            The string representing the file path.
	 * 
	 * @throws HeadException
	 *             If the file is not readable.
	 */
	private Path getReadableFilePath(String filePathString) throws HeadException {
		// Consider only the first token of the string as the filepath
		String[] filePathStringTokens = filePathString.split(" ");
		if (filePathStringTokens.length == 0) {
			throw new HeadException("No file path provided");
		}
		filePathString = filePathStringTokens[0];

		Path currentDir = Paths.get(Environment.currentDirectory);
		Path filePath = currentDir.resolve(filePathString);
		Boolean isFileReadable = checkIfFileIsReadable(filePath);
		if (isFileReadable) {
			return filePath;
		} else {
			return null;
		}
	}
}
