package sg.edu.nus.comp.cs4218.impl.app;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import sg.edu.nus.comp.cs4218.app.Wc;
import sg.edu.nus.comp.cs4218.exception.WcException;

/**
 * The wc command prints the number of bytes, words, and lines in given files
 * (followed by a newline).
 * 
 * <p>
 * <b>Command format:</b> <code>wc [OPTIONS] [FILE]...</code>
 * <dl>
 * <dt>OPTIONS</dt>
 * <dd>-m : Print only the character counts</dd>
 * <dd>-w : Print only the word counts</dd>
 * <dd>-l : Print only the newline counts</dd>
 * </dl>
 * <dl>
 * <dt>FILE</dt>
 * <dd>the name of the file(s). If no files are specified, use stdin.</dd>
 * </dl>
 * </p>
 */

 @SuppressWarnings("PMD.GodClass")
public class WcApplication implements Wc {
	private static final String TAB = "       ";
	private int charCount = 0;
	private int wordCount = 0;
	private int lineCount = 0;

	private int charCountTotal = 0;
	private int wordCountTotal = 0;
	private int lineCountTotal = 0;

	/**
	 * Runs the wc application with the specified arguments.
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
	 * @throws WcException
	 *             If the file(s) specified do not exist or are unreadable or if
	 *             there are no inputs
	 *
	 */
	@Override
	public void run(String[] args, InputStream stdin, OutputStream stdout) throws WcException {
		if (stdout == null) {
			throw new WcException("No output stream provided\n");
		}

		if (args == null && stdin == null) {
			throw new WcException("No input provided\n");
		}

		boolean[] flags = getFlags(args);
		String[] filenames = getFiles(args);

		String countStr;
		if (filenames.length > 0) {
			countStr = getWc(filenames, flags, null, false);

		} else if (stdin == null || isInputStreamEmpty(stdin)) {
			throw new WcException("Invalid File or Input stream empty\n");
		} else {
			countStr = getWc(null, flags, stdin, true) + "\n";
		}

		try {
			stdout.write(countStr.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns string containing the character count in file
	 * 
	 * @param args
	 *            String containing command and arguments
	 */
	@Override
	public String printCharacterCountInFile(String args) {
		return parseAndEvaluate(args, null);
	}

	/**
	 * Returns string containing the word count in file
	 * 
	 * @param args
	 *            String containing command and arguments
	 */
	@Override
	public String printWordCountInFile(String args) {
		return parseAndEvaluate(args, null);
	}

	/**
	 * Returns string containing the newline count in file
	 * 
	 * @param args
	 *            String containing command and arguments
	 */
	@Override
	public String printNewlineCountInFile(String args) {
		return parseAndEvaluate(args, null);
	}

	/**
	 * Returns string containing all counts in file
	 * 
	 * @param args
	 *            String containing command and arguments
	 */
	@Override
	public String printAllCountsInFile(String args) {
		return parseAndEvaluate(args, null);
	}

	/**
	 * Returns string containing the character count in Stdin
	 * 
	 * @param args
	 *            String containing command and arguments
	 * @param stdin
	 *            InputStream containing Stdin
	 */
	@Override
	public String printCharacterCountInStdin(String args, InputStream stdin) {
		return parseAndEvaluate(args, stdin);
	}

	/**
	 * Returns string containing the word count in Stdin
	 * 
	 * @param args
	 *            String containing command and arguments
	 * @param stdin
	 *            InputStream containing Stdin
	 */
	@Override
	public String printWordCountInStdin(String args, InputStream stdin) {
		return parseAndEvaluate(args, stdin);
	}

	/**
	 * Returns string containing the newline count in Stdin
	 * 
	 * @param args
	 *            String containing command and arguments
	 * @param stdin
	 *            InputStream containing Stdin
	 */
	@Override
	public String printNewlineCountInStdin(String args, InputStream stdin) {
		return parseAndEvaluate(args, stdin);
	}

	/**
	 * Returns string containing all counts in Stdin
	 * 
	 * @param args
	 *            String containing command and arguments
	 * @param stdin
	 *            InputStream containing Stdin
	 */
	@Override
	public String printAllCountsInStdin(String args, InputStream stdin) {
		return parseAndEvaluate(args, stdin);
	}

	/**
	 * Retrieve filenames from args. Assumes filenames do not contain "-" in
	 * front
	 * 
	 * @param args
	 *            String[] containing flags and filenames
	 * @return String[] containing filenames
	 */
	private String[] getFiles(String... args) {
		int fileIndexStart = -1;
		for (int i = 0; i < args.length; i++) {
			if (args[i].indexOf('-') != 0 && args[i].length() > 0) {
				fileIndexStart = i;
				break;
			}
		}

		if (fileIndexStart > -1) {
			ArrayList<String> filenames = new ArrayList<String>();
			for (int i = fileIndexStart; i < args.length; i++) {
				filenames.add(args[i]);
			}
			return filenames.toArray(new String[filenames.size()]);

		} else {
			return new String[0];
		}

	}

	/**
	 * Retrieve and validate flags from args. A flag contains "-" in front of
	 * it. Flags will be represented by a boolean array, in the order of -m -w
	 * -l This means boolean[] arr[0] refers to -m and so forth. boolean array
	 * will always be of size 3
	 * 
	 * @param args
	 * @return boolean[], boolean[] arr[0] returns true if -m exists
	 * @throws WcException
	 */
	private boolean[] getFlags(String... args) throws WcException {
		boolean[] flags = new boolean[3];
		for (int i = 0; i < args.length; i++) {
			if (args[i].indexOf('-') == 0) {
				flagSeparator(args[i].trim(), flags);
			} else {
				break;
			}
		}

		if (!(flags[0] || flags[1] || flags[2])) {
			Arrays.fill(flags, true);
		}
		return flags;
	}

	/**
	 * Separates flags from -lw to -l -w in boolean array
	 * 
	 * @param str
	 *            flag(s)
	 * @param flags
	 *            boolean[] will be mutated. boolean[] arr[0] returns true if -m
	 *            exists
	 * @throws WcException
	 */
	private void flagSeparator(String str, boolean... flags) throws WcException {
		int dashPosition = str.indexOf('-');
		int startIndex = dashPosition + 1;
		while (startIndex > -1 && startIndex < str.length()) {
			String nextChar = str.substring(startIndex, startIndex + 1);
			if ("m".equalsIgnoreCase(nextChar)) {
				flags[0] = true;
				startIndex = startIndex + 1;

			} else if ("w".equalsIgnoreCase(nextChar)) {
				flags[1] = true;
				startIndex = startIndex + 1;

			} else if ("l".equalsIgnoreCase(nextChar)) {
				flags[2] = true;
				startIndex = startIndex + 1;

				// } else if (startIndex + 1 == str.length()) {
				// break;

			} else {
				throw new WcException("Invalid Flag\n");

			}
		}
	}

	/**
	 * Process to get wc from stdin or file
	 * 
	 * @param args
	 *            String[] containing filenames
	 * @param options
	 *            boolean[] with index marking the existence of tag -m -w -l
	 *            respectively
	 * @param stdin
	 *            InputStream containing input
	 * @param isStdin
	 *            boolean, true if it should be evaluating a stdin, false
	 *            otherwise
	 * @return String containing wc. String is formatted respective to stdin,
	 *         file(s)
	 */
	private String getWc(String[] args, boolean[] options, InputStream stdin, boolean isStdin) {
		String output = "";
		if (isStdin) {
			resetStates();
			output += processWcInStdin(stdin, options);
		} else {
			for (int i = 0; i < args.length; i++) {
				resetStates();
				output += processWcInFile(args[i], options) + "\n";
			}
			if (args.length > 1) {
				output += buildTotalString(options);
			}
		}
		return output;
	}

	/**
	 * Calculate Wc in stdin
	 * 
	 * @param stdin
	 *            InputStream containing input
	 * @param flags
	 *            boolean[] with index marking the existence of tag -m -w -l
	 *            respectively
	 * @return String containing wc of respective flag(s)
	 */
	private String processWcInStdin(InputStream stdin, boolean... flags) {
		BufferedInputStream inputStream = new BufferedInputStream(stdin);
		byte[] buffer = new byte[1024];
		int readChars = 0;
		try {
			while ((readChars = inputStream.read(buffer)) != -1) {
				String line = new String(buffer, 0, readChars);
				this.charCount += line.length();
				String[] parts = line.replaceAll("\\s+", " ").split(" ");

				for (int i = 0; i < parts.length; i++) {
					if (parts[i].length() > 0) {
						this.wordCount++;
					}
				}

				int pos = -1;
				while ((pos = line.indexOf('\n')) != -1) {
					this.lineCount++;
					line = line.substring(pos + 1);
				}
			}
			inputStream.close();

			this.charCountTotal += this.charCount;
			this.wordCountTotal += this.wordCount;
			this.lineCountTotal += this.lineCount;

		} catch (IOException e) {
			e.printStackTrace();
		}

		return builtWcString(null, flags);
	}

	/**
	 * Count the number of newlines in file
	 * 
	 * @param filename
	 *            String
	 * @return int number of newlines in file
	 * @throws IOException
	 */
	private int countLines(String filename) throws IOException {
		InputStream inputStream = new BufferedInputStream(new FileInputStream(filename));
		try {
			byte[] buffer = new byte[1024];
			int count = 0;
			int readChars = 0;
			boolean empty = true;
			while ((readChars = inputStream.read(buffer)) != -1) {
				empty = false;
				for (int i = 0; i < readChars; ++i) {
					if (buffer[i] == '\n') {
						++count;
					}
				}
			}
			return (count == 0 && !empty) ? 1 : count;
		} finally {
			inputStream.close();
		}
	}

	/**
	 * Calculate Wc in file(s).
	 * 
	 * @param filename(s)
	 * @param flags
	 *            boolean[] with index marking the existence of tag -m -w -l
	 *            respectively
	 * @return String containing wc of respective flag(s)
	 */
	private String processWcInFile(String filename, boolean... flags) {
		BufferedReader reader;
		try {
			if (isFileValid(filename)) {
				reader = new BufferedReader(new FileReader(filename));
				String line;
				while ((line = reader.readLine()) != null) {
					this.charCount += line.getBytes().length;
					String[] parts = line.replaceAll("\\s+", " ").split(" ");
					for (int i = 0; i < parts.length; i++) {
						if (parts[i].length() > 0) {
							this.wordCount += 1;
						}
					}
				}
				reader.close();
				this.lineCount = countLines(filename);
				this.charCount += this.lineCount;

				this.charCountTotal += this.charCount;
				this.wordCountTotal += this.wordCount;
				this.lineCountTotal += this.lineCount;

			} else {
				return "wc: " + filename + " Invalid file or directory";
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return builtWcString(filename, flags);
	}

	/**
	 * Checks if file is valid
	 * 
	 * @param filename
	 *            String
	 * @return boolean true if is file and file exists, false otherwise
	 */
	private boolean isFileValid(String filename) {
		File file = new File(filename);
		return file.exists() && file.isFile();
	}

	/**
	 * Builds the last line, the "Total" line when multiple files are passed to
	 * wc
	 * 
	 * @param options
	 *            boolean[] with index marking the existence of tag -m -w -l
	 *            respectively
	 * @return String formatted accordingly to a single file, stdin or multiple
	 *         files
	 */
	private String buildTotalString(boolean... options) {
		StringBuilder output = new StringBuilder();
		if (options[0]) {
			output.append(TAB);
			output.append(this.charCountTotal);
		}
		if (options[1]) {
			output.append(TAB);
			output.append(this.wordCountTotal);
		}
		if (options[2]) {
			output.append(TAB);
			output.append(this.lineCountTotal);
		}
		output.append(" total\n");
		return output.toString();
	}

	/**
	 * Reset private variables
	 */
	private void resetStates() {
		this.charCount = 0;
		this.wordCount = 0;
		this.lineCount = 0;
	}

	/**
	 * Builds single wc string that tells the characters, words and lines in a
	 * file or stdin
	 * 
	 * @param filename
	 *            String
	 * @param options
	 *            boolean[] with index marking the existence of tag -m -w -l
	 *            respectively
	 * @return String a line of wc in a file
	 */
	private String builtWcString(String filename, boolean... options) {
		StringBuilder output = new StringBuilder();
		if (options[0]) {
			output.append(TAB);
			output.append(this.charCount);
		}
		if (options[1]) {
			output.append(TAB);
			output.append(this.wordCount);
		}
		if (options[2]) {
			output.append(TAB);
			output.append(this.lineCount);
		}
		if (filename != null) {
			output.append(' ');
			output.append(filename);
		}
		return output.toString();
	}

	/**
	 * Checks if the provided input stream is empty
	 * 
	 * @param stdin
	 *            InputStream. The input for the command is read from this
	 *            InputStream if no files are specified.
	 * @return boolean true if input stream is empty, false otherwise
	 */
	private boolean isInputStreamEmpty(InputStream stdin) {
		try {
			return stdin.available() == 0;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * Parses command line args from interface to run function
	 * 
	 * @param args
	 *            String command line arguments without 'wc ' in front
	 * @param stdin
	 *            InputStream
	 * @return String containing wc
	 */
	private String parseAndEvaluate(String args, InputStream stdin) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		try {
			WcApplication app = new WcApplication();
			String[] splittedArguments = args == null ? new String[0] : args.split("\\s+");
			app.run(splittedArguments, stdin, out);
			return out.toString();
		} catch (WcException e) {
			return e.getMessage();
		}
	}

}
