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
import java.util.LinkedList;
import java.util.List;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.TailException;

/**
 * The tail command prints the last N lines of the file (or input stream).
 * If there are less than N lines, print existing lines without raising an exception.
 * 
 * <p>
 * <b>Command format:</b> <code>tail [OPTIONS] [FILE]</code>
 * <dl>
 * <dt>OPTIONS</dt>
 * <dd>“-n 15” means printing 15 lines. Print last 10 lines if not specified.</dd>
 * <dt>FILE</dt>
 * <dd>the name of the file. If no files are specified, use stdin.</dd>
 * </dl>
 * </p>
 */
public class TailApplication implements Application {

	final private int DEFAULT_NUMBER_OF_LINES_TO_PRINT = 10;

	/**
	 * Runs the tail application with the specified arguments.
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
	 * @throws TailException
	 *             If the file specified does not exist or are unreadable.
	 */
	@Override
	public void run(String[] args, InputStream stdin, OutputStream stdout)
			throws TailException {
		if (stdout == null) {
			throw new TailException("No output stream provided");
		}
		Integer numberOfLinesToPrint = DEFAULT_NUMBER_OF_LINES_TO_PRINT;
		if (args == null || args.length == 0) {
			// print default number of lines from inputstream
			printLastNLinesFromInputStreamToOutputStream(numberOfLinesToPrint,
					stdin, stdout);
		} else {
			String firstArgument = args[0];
			if (firstArgument == null) {
				throw new TailException("First argument provided is null");
			}
			try {
				numberOfLinesToPrint = Integer.parseInt(firstArgument);
				
				if (numberOfLinesToPrint < 0) {
					throw new TailException("Illegal line count: "
							+ numberOfLinesToPrint);
				}

				// assume first argument is number of lines to print
				if (args.length > 1) {
					// get path of file to read from
					String filePathString = args[1];

					if (filePathString == null) {
						throw new TailException("Second argument provided is null");
					}

					printLastNLinesFromFileToOutputStream(numberOfLinesToPrint,
							filePathString, stdout);
				} else {
					printLastNLinesFromInputStreamToOutputStream(
							numberOfLinesToPrint, stdin, stdout);
				}
			} catch (NumberFormatException e) {
				// first argument is assumed to be the path of the file to read from
				// since it is not an integer
				String filePathString = args[0];
				printLastNLinesFromFileToOutputStream(numberOfLinesToPrint,
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
	 * @throws TailException
	 *             If the file is not readable
	 */
	boolean checkIfFileIsReadable(Path filePath) throws TailException {
		
		if (Files.isDirectory(filePath)) {
			throw new TailException("This is a directory");
		}
		if (Files.exists(filePath) && Files.isReadable(filePath)) {
			return true;
		} else {
			throw new TailException("Could not read file");
		}
	}
	
	/**
	 * Prints the last N lines from the given input stream to the given output stream.
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
	 * @throws TailException
	 *             If the file is not readable
	 */
	private void printLastNLinesFromInputStreamToOutputStream(int numberOfLines,
			InputStream stdin, OutputStream stdout) throws TailException {
		if (stdin == null) {
			throw new TailException("No input stream provided");
		}
		if (stdout == null) {
			throw new TailException("No output stream provided");
		}
		try {
			InputStreamReader stdinReader = new InputStreamReader(stdin, "UTF-8");
			BufferedReader bufferedReader = new BufferedReader(stdinReader);
			printLastNLinesFromBufferedReaderToOutputStream(numberOfLines,
		    		bufferedReader, stdout);
		} catch (IOException e) {
			throw new TailException("IOException");
		}
	}
	
	/**
	 * Prints the last N lines from the file at the given path to the given output stream.
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
	 * @throws TailException
	 *             If the file is not readable or writing to the output stream
	 *             fails.
	 */
	private void printLastNLinesFromFileToOutputStream(int numberOfLines,
			String filePathString, OutputStream stdout) throws TailException {
		if (filePathString == null) {
			throw new TailException("No file path provided");
		}
		if (stdout == null) {
			throw new TailException("No output stream provided");
		}
		Path pathToReadFrom = getReadableFilePath(filePathString);
		try {
			FileReader fileReader = new FileReader(pathToReadFrom.toString());
		    BufferedReader bufferedReader = new BufferedReader(fileReader);
		    printLastNLinesFromBufferedReaderToOutputStream(numberOfLines,
		    		bufferedReader, stdout);
			bufferedReader.close();
		} catch (IOException e) {
			throw new TailException("IOException");
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
	private void printLastNLinesFromBufferedReaderToOutputStream(int numberOfLines,
			BufferedReader bufferedReader, OutputStream stdout) throws IOException {
		// get all the lines from the file and keep the last N
		// where N is equivalent to numberOfLines
		List<String> lastNLines = new LinkedList<>();
		String lineFromInput = bufferedReader.readLine();
		while (lineFromInput != null) {
			lastNLines.add(lineFromInput);
			if (lastNLines.size() > numberOfLines) {
				lastNLines.remove(0);
			}
			lineFromInput = bufferedReader.readLine();
		}

		// print out the last N lines (if there are that many)
		String lineToPrint;
		String newLine = System.getProperty("line.separator");
		Integer numberOfLinesThatWillBePrinted = lastNLines.size();
		for (int i = 0; i < numberOfLinesThatWillBePrinted; i++) {
			if (i != 0) {
				stdout.write(newLine.getBytes());
			}
			lineToPrint = lastNLines.remove(0);
			stdout.write(lineToPrint.getBytes());
		}
	}
	
	/**
	 * Returns a file path from the given path string if the path is for
	 * a readable file.
	 * 
	 * @param filePathString
	 *            The string representing the file path.
	 * 
	 * @throws TailException
	 *             If the file is not readable.
	 */
	private Path getReadableFilePath(String filePathString) throws TailException {
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
