package sg.edu.nus.comp.cs4218.impl.app;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import sg.edu.nus.comp.cs4218.app.Wc;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.GrepException;
import sg.edu.nus.comp.cs4218.exception.WcException;

/**
 * The wc command prints the number of bytes, words, and lines in given files 
 * (followed by a newline).
 * 
 * <p>
 * <b>Command format:</b> <code>wc [OPTIONS] [FILE]...</code>
 * <dl>
 * <dt>OPTIONS</dt>
 * <dd>-m : Print only the character counts </dd>
 * <dd>-w : Print only the word counts </dd>
 * <dd>-l : Print only the newline counts </dd>
 * </dl>
 * <dl>
 * <dt>FILE</dt>
 * <dd>the name of the file(s). If no files are specified, use stdin.</dd>
 * </dl>
 * </p>
 */

public class WcApplication implements Wc {
	private int charCount = 0;
	private int wordCount = 0;
	private int lineCount = 0;
	private int charCountTotal = 0;
	private int wordCountTotal = 0;
	private int lineCountTotal = 0;
	private String filename = null;
	private boolean isCountProcessed = false;

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
	 *             If the file(s) specified do not exist or are unreadable or 
	 *             if there are no inputs
	 *
	 */
	@Override
	public void run(String[] args, InputStream stdin, OutputStream stdout) 
			throws WcException {
		if (stdout == null) {
			throw new WcException("No output stream provided\n");
		}
//		if (stdin == null) {
//			throw new WcException("No input stream provided");
//		}
		if (args == null && stdin == null) {
			throw new WcException("No input provided\n");
		}
		
		boolean[] options = optionsSeparator(args);
		
		String countStr;
//		boolean[] options = optionsSeparator(args);
		if (containsFileDirectory(args)) {
			countStr = getWc(args, options, null, false) + "\n";
			
		} else if (stdin != null && !isInputStreamEmpty(stdin)) {
			countStr = getWc(args, options, stdin, true) + "\n";
			
		} else {
			throw new WcException("Input stream empty\n");
		}
		
		try {
			stdout.write(countStr.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Separate joined options e.g. "-lwm" to individual options e.g. "-l -w -m"
	 * Individual options are represented by a boolean array, 
	 * in the respective position -m -w -l
	 * 
	 * @param args String[] command line arguments
	 * @return boolean[] that marks true in the position of the respective option 
	 * @throws WcException when an invalid option is entered
	 */
	private boolean[] optionsSeparator(String[] args) throws WcException {
		boolean[] options = new boolean[3];
		String command = "";
		for (int i = 0; i < args.length; i++) {
			command += args[i] + " ";
		}

		int startIndex = 0;
		int dashPosition = command.indexOf('-');
		if (dashPosition == -1) {
			Arrays.fill(options, true);
			return options;
			
		} else {
			startIndex = dashPosition + 1;
			while (startIndex > -1 && startIndex < command.length()) {
				if (startIndex + 1 == command.length()) {
					break;
				}
				
				String nextChar = command.substring(startIndex, startIndex + 1);
				if ("m".equals(nextChar.toLowerCase())) {
					options[0] = true;
					startIndex = startIndex + 1;
					
				} else if ("w".equals(nextChar.toLowerCase())) {
					options[1] = true;
					startIndex = startIndex + 1;
					
				} else if ("l".equals(nextChar.toLowerCase())) {
					options[2] = true;
					startIndex = startIndex + 1;
				
				} else if (" ".equals(nextChar)) {
					dashPosition = command.indexOf("-", startIndex);
					if (dashPosition > -1) {
						startIndex = dashPosition + 1;
					} else {
						break;
					}
				} else {
					throw new WcException("Invalid Option\n");
				}	
			}
		}
		
		return options;
	}

	/**
	 * Calls the relevant interface functions depending on the option provided
	 * @param args String[] command line arguments
	 * @param options boolean[] that marks true in the position of the respective option 
	 * @param stdin InputStream. The input for the command is read from this
	 *  		InputStream if no files are specified.
	 * @param isStdin boolean true if an input stream is passed in, false otherwise
	 * @return String containing counts. Counts are returned in the order of -m -w -l 
	 * 			if all options are specified
	 */
	private String getWc(String[] args, boolean[] options, 
								InputStream stdin, boolean isStdin) {
//		String commandLine = "";
//		String countStr = "";
//		for (int i = 0; i < args.length; i++) {
//			commandLine += args[i] + " ";
//		}
//		commandLine.trim();
		
		String output = "";
		if (isStdin) {
			processWcInStdin(stdin);
			output += builtWcString(null, options);
//			if (options[0]) { // -m
//				countStr += printCharacterCountInStdin(commandLine, stdin) + " ";
//			} 
//			if (options[1]) { // -w
//				countStr += printWordCountInStdin(commandLine, stdin) + " ";
//			}
//			if (options[2]) { // -l
//				countStr += printNewlineCountInStdin(commandLine, stdin);
//			}

		} else {
			String[] filenames = getFilenames(args);
			for (int i = 0; i < filenames.length; i++) {
				resetStates();
				processWcInFile(filename);
				output += builtWcString(filenames[i], options);
			}
			
			output += buildTotalString(options);
//			if (options[0]) { // -m
//				countStr += printCharacterCountInFile(commandLine) + " ";
//			} 
//			if (options[1]) { // -w
//				countStr += printWordCountInFile(commandLine) + " ";
//			}
//			if (options[2]) { // -l
//				countStr += printNewlineCountInFile(commandLine);
//			}
		}
		
		return output;
	}

	private void processWcInStdin(InputStream stdin) {
		BufferedInputStream is = new BufferedInputStream(stdin);
		byte[] c = new byte[1024];
	    int readChars = 0;
		try {
			while ((readChars = is.read(c)) != -1) {
				String line = new String(c, 0, readChars);
				this.charCount += line.length();
				String[] parts = line.replaceAll("\\s+", " ").split(" ");
				
				for (int i = 0; i < parts.length; i++) {
					if (parts[i].length() > 0) {
						this.wordCount++;
					}
				}
				
				int pos = -1;
				while ((pos = line.indexOf("\n")) != -1) {
					this.lineCount++;
					line = line.substring(pos + 1);
				}
			}
			is.close();
			stdin.reset();
//			this.isCountProcessed = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void processWcInFile(String filename) {
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(filename));
			String line;
			while ((line = reader.readLine()) != null) {
				this.charCount += line.getBytes().length;
				this.lineCount++;
				String[] parts = line.replaceAll("\\s+", " ").split(" ");
				for (int i = 0; i < parts.length; i++) {
					if (parts[i].length() > 0) {
						this.wordCount += 1;
					}
				}
			}
			reader.close();
			this.lineCount -= 1;
			this.charCount += this.lineCount;
//			this.isCountProcessed = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.charCountTotal += this.charCount;
		this.wordCountTotal += this.wordCount;
		this.lineCountTotal += this.lineCount;
	}

	private String[] getFilenames(String[] args) {
		ArrayList<String> fileList = new ArrayList<String>();
		for (int i = 0; i < args.length; i++) {
			File file = new File(args[i]);
			if (file.exists() && file.isFile()) {
				fileList.add(args[i]);
			}
		}
		String[] filenames = null;
		return fileList.toArray(filenames);
	}

	private String buildTotalString(boolean[] options) {
		StringBuilder output = new StringBuilder();
		if (options[0]) {
			output.append("       " + this.charCountTotal);
		}
		if (options[1]) {
			output.append("       " + this.wordCountTotal);
		}
		if (options[2]) {
			output.append("       " + this.lineCountTotal);
		}
		output.append(" total\n");
		return output.toString();
	}

	private void resetStates() {
		this.charCount = 0;
		this.wordCount = 0;
		this.lineCount = 0;
	}

	private String builtWcString(String filename, boolean[] options) {
		StringBuilder output = new StringBuilder();
		if (options[0]) {
			output.append("       " + this.charCount);
		}
		if (options[1]) {
			output.append("       " + this.wordCount);
		}
		if (options[2]) {
			output.append("       " + this.lineCount);
		}
		if (filename == null) {
			output.append("\n");
		} else {
			output.append(" " + filename + "\n");
		}
		return output.toString();
	}
	
	/**
	 * Checks if the provided input stream is empty
	 * @param stdin InputStream. The input for the command is read from this
	 *  		InputStream if no files are specified.
	 * @return boolean true if input stream is empty, false otherwise
	 */
	private boolean isInputStreamEmpty(InputStream stdin) {
		try {
			if (stdin.available() > 0) {
				return false;
			} else {
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	/**
	 * Checks if the file provided is valid
	 * @param args String[] command line arguments
	 * @return boolean true if file is valid, false otherwise
	 */
	private boolean containsFileDirectory(String[] args) {		
		for (int i = 0; i < args.length; i++) {
			File file = new File(args[i]);
			boolean containsFile = file.exists() && file.isFile();
			if (containsFile) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Retrieves the filename of file to be read
	 * @param args String[] command line arguments
	 * @return String filename
	 */
//	private String getFilename(String args) {
//		if (this.filename == null) {
//			String[] parts = args.split(" ");
//			return parts[parts.length - 1];
//		} else {
//			return this.filename;
//		}
//	}
	
	/**
	 * Store character count, word count and line count read from file
	 * in private variables
	 * @param args String containing command and arguments
	 */
//	private void processCountInFile(String input) {
//		if (this.isCountProcessed) {
//			return;
//		}
//		String filename = getFilename(input);
//		BufferedReader reader;
//		try {
//			reader = new BufferedReader(new FileReader(filename));
//			String line;
//			while ((line = reader.readLine()) != null) {
//				this.charCount += line.getBytes().length;
//				this.lineCount++;
//				String[] parts = line.replaceAll("\\s+", " ").split(" ");
//				for (int i = 0; i < parts.length; i++) {
//					if (parts[i].length() > 0) {
//						this.wordCount += 1;
//					}
//				}
//			}
//			reader.close();
//			this.lineCount -= 1;
//			this.charCount += this.lineCount;
//			this.isCountProcessed = true;
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

	/**
	 * Store character count, word count and line count read from input stream
	 * in private variables
	 * @param stdin InputStream containing content
	 */
//	private void processCountInStdin(InputStream stdin) {
//		if (this.isCountProcessed) {
//			return;
//		}
//		BufferedInputStream is = new BufferedInputStream(stdin);
//		byte[] c = new byte[1024];
//	    int readChars = 0;
//		try {
//			while ((readChars = is.read(c)) != -1) {
//				String line = new String(c, 0, readChars);
//				this.charCount += line.length();
//				String[] parts = line.replaceAll("\\s+", " ").split(" ");
//				
//				for (int i = 0; i < parts.length; i++) {
//					if (parts[i].length() > 0) {
//						this.wordCount++;
//					}
//				}
//				
//				int pos = -1;
//				while ((pos = line.indexOf("\n")) != -1) {
//					this.lineCount++;
//					line = line.substring(pos + 1);
//				}
//			}
//			is.close();
//			stdin.reset();
////			this.isCountProcessed = true;
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

	/**
	 * Returns string containing the character count in file
	 * @param args String containing command and arguments
	 */
	@Override
	public String printCharacterCountInFile(String args) {
		return parseAndEvaluate(args, null);
//		processCountInFile(args);
//		return String.valueOf(this.charCount);
	}

	/**
	 * Returns string containing the word count in file
	 * @param args String containing command and arguments
	 */
	@Override
	public String printWordCountInFile(String args) {
		return parseAndEvaluate(args, null);
//		processCountInFile(args);
//		return String.valueOf(this.wordCount);
	}

	/**
	 * Returns string containing the newline count in file
	 * @param args String containing command and arguments
	 */
	@Override
	public String printNewlineCountInFile(String args) {
		return parseAndEvaluate(args, null);
//		processCountInFile(args);
//		return String.valueOf(this.lineCount);
	}

	/**
	 * Returns string containing all counts in file
	 * @param args String containing command and arguments
	 */
	@Override
	public String printAllCountsInFile(String args) {
		return parseAndEvaluate(args, null);
//		processCountInFile(args);
//		return this.charCount + " " + 
//				this.wordCount + " " +
//				this.lineCount;
	}
	
	/**
	 * Returns string containing the character count in Stdin
	 * @param args String containing command and arguments
	 * @param stdin InputStream containing Stdin
	 */
	@Override
	public String printCharacterCountInStdin(String args, InputStream stdin) {
		return parseAndEvaluate(args, stdin);
//		processCountInStdin(stdin);
//		return String.valueOf(this.charCount);
	}

	/**
	 * Returns string containing the word count in Stdin
	 * @param args String containing command and arguments
	 * @param stdin InputStream containing Stdin
	 */
	@Override
	public String printWordCountInStdin(String args, InputStream stdin) {
		return parseAndEvaluate(args, stdin);
//		processCountInStdin(stdin);
//		return String.valueOf(this.wordCount);
	}

	/**
	 * Returns string containing the newline count in Stdin
	 * @param args String containing command and arguments
	 * @param stdin InputStream containing Stdin
	 */
	@Override
	public String printNewlineCountInStdin(String args, InputStream stdin) {
		return parseAndEvaluate(args, stdin);
//		processCountInStdin(stdin);
//		return String.valueOf(this.lineCount);
	}

	/**
	 * Returns string containing all counts in Stdin
	 * @param args String containing command and arguments
	 * @param stdin InputStream containing Stdin
	 */
	@Override
	public String printAllCountsInStdin(String args, InputStream stdin) { 
		return parseAndEvaluate(args, stdin);
//		processCountInStdin(stdin);
//		return this.charCount + " " + 
//				this.wordCount + " " + 
//				this.lineCount;
	}
	
	private String parseAndEvaluate(String args, InputStream stdin) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		try {
			WcApplication app = new WcApplication();
			String[] splittedArguments = args == null ?
					new String[0] : args.split("\\s+");
			app.run(splittedArguments, stdin, out);
			return out.toString();
		} catch (WcException e) {
			return e.getMessage();
		}
	}

}
