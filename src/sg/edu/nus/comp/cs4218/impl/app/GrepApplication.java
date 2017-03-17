package sg.edu.nus.comp.cs4218.impl.app;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import sg.edu.nus.comp.cs4218.app.Grep;
import sg.edu.nus.comp.cs4218.exception.GrepException;
import sg.edu.nus.comp.cs4218.exception.SedException;

/**
 * Copies input file (or input stream) to stdout performing string replacement.
 * For each line containing a match to a specified pattern (in JAVA format),
 * replaces the matched substring with the specified string.
 * 
 * <p>
 * <b>Command format:</b> <code>grep PATTERN [FILE]...</code>
 * <dl>
 * <dt>PATTERN</dt>
 * <dd>specifies a regular expression in JAVA format.</dd>
 * </dl>
 * <dl>
 * <dt>FILE</dt>
 * <dd>the name of the file(s). If no files are specified, use stdin.</dd>
 * </dl>
 * </p>
 */

public class GrepApplication implements Grep {

	private final String NEW_LINE = System.getProperty("line.separator");

	/**
	 * Runs the grep application with the specified arguments.
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
	 * @throws SedException
	 *             
	 *
	 */
	@Override
	public void run(String[] args, InputStream stdin, OutputStream stdout) throws GrepException {
		if (args.length == 0) {
			throw new GrepException("Invalid Command");
		}
		
		if (stdout == null) {
			throw new GrepException("No output stream provided");
		}
		
		String output = "";
		if (!containsPattern(args)) {
			throw new GrepException("No pattern provided");
		} 
		
		if (args.length >= 2) {
			output = grepFile(args);
		
		} else if (args.length == 1) {
			output = grepStdin(args, stdin);
		} else {
			throw new GrepException("No Input provided (in file or stdin) or no there is such file or directory");
		}
		
		try {
			stdout.write(output.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Check if args contains a Pattern segment
	 * @param args
	 * @return true if it does, false otherwise
	 */
	private boolean containsPattern(String... args) {
		return args.length != 0 && args[0] != null && !args[0].isEmpty();
	}

	/**
	 * Perform Grep on stdin
	 * @param args Array containing expression to grep
	 * @param stdin InputStream
	 * @return String with expression
	 * @throws GrepException
	 */
	@SuppressWarnings("PMD.PreserveStackTrace")
	private String grepStdin(String args[], InputStream stdin) throws GrepException {
		if (isInputStreamEmpty(stdin)) {
			throw new GrepException("Inputstream empty + NEW_LINE");
		}
		StringBuilder output = new StringBuilder();
		try {
			Pattern regex = getRegexPattern(args[0]);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stdin));
			String line;
			while((line = bufferedReader.readLine()) != null) {
				Matcher matcher = regex.matcher(line);
				if (matcher.find()) {
					output.append(line);
					output.append(NEW_LINE);
				}
			}
			bufferedReader.close();
		} catch (IOException e) {
			throw new GrepException(e.getMessage());
		}
		
		if (output.length() > 0) {
			return output.toString();
		} else {
			return "Pattern Not Found In Stdin!" + NEW_LINE;
		}
	}

	/**
	 * Performs grep on provided files
	 * @param args Array containing expression and filenames
	 * @return String containing grep expression
	 * @throws GrepException
	 */
	@SuppressWarnings("PMD.PreserveStackTrace")
	private String grepFile(String... args) throws GrepException {
		if (args.length <= 1) {
			throw new GrepException("Incorrect Arguments");
		}
		
		StringBuilder output = new StringBuilder();
		try {
			Pattern regex = getRegexPattern(args[0]);
			BufferedReader reader;
			String line;
			
			for (int i = 1; i < args.length; i++) {
				if (isFileValid(args[i])) {
					reader = new BufferedReader(new FileReader(args[i]));
					 while ((line = reader.readLine()) != null) {
						Matcher matcher = regex.matcher(line);
					    if (matcher.find()) {
					    	if (args.length > 2) {
					    		output.append(args[i]);
					    		output.append(':');
					    	} 
					    	output.append(line);
					    	output.append(NEW_LINE);
					    }
					}
					reader.close();
				} else {
					output.append(args[i]);
					output.append(":No such file or directory");
					output.append(NEW_LINE);
				}
			}
			
		} catch (PatternSyntaxException e) {
			throw new GrepException("Invalid Pattern");
		} catch (IOException e) {
			throw new GrepException(e.getMessage());
		}
		
		if (output.length() > 0) {
			return output.toString();
		} else {
			return "Pattern Not Found In File!" + NEW_LINE;
		}
	}

	/**
	 * Retrieves regex pattern object
	 * @param args String containing regex expression
	 * @return Pattern object
	 * @throws PatternSyntaxException if an invalid regex is provided
	 */
	private Pattern getRegexPattern(String args) throws PatternSyntaxException {
		return Pattern.compile(args);
	}

	/**
	 * Checks if input stream is readable
	 * @param stdin InputStream
	 * @return boolean true if readable, false otherwise
	 */
	private boolean isInputStreamEmpty(InputStream stdin) {
		try {
			if (stdin.available() > 0) {
				return false;
			} 
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	/**
	 * Checks if provided filename is a file and an existing file as well
	 * @param args filename
	 * @return true if is file and file exists, false otherwise
	 */
	private boolean isFileValid(String args) {
		File file = new File(args);
	    return file.exists() && file.isFile();
	}

	/**
	 * Returns string containing lines which match the specified pattern in
	 * Stdin
	 * @param args String containing command and arguments
	 * @param stdin InputStream containing stdin
	 */
	@Override
	public String grepFromStdin(String args, InputStream stdin) {
		return parseAndEvaluate(args, stdin);
	}

	/**
	 * Returns string containing lines which match the specified pattern in the
	 * given file
	 * @param args  String containing command and arguments
	 */
	@Override
	public String grepFromOneFile(String args) {
		return parseAndEvaluate(args, null);
	}

	/**
	 * Returns string containing lines which match the specified pattern in the
	 * given files
	 * @param args  String containing command and arguments
	 */
	@Override
	public String grepFromMultipleFiles(String args) {
		return parseAndEvaluate(args, null);
	}

	/**
	 * Returns string when invalid pattern is specified in grep from Stdin
	 * @param args String containing command and arguments
	 */
	@Override
	public String grepInvalidPatternInStdin(String args, InputStream stdin) {
		return parseAndEvaluate(args, stdin);
	}

	/**
	 * Returns string when invalid pattern is specified in grep from file
	 * @param args String containing command and arguments
	 */
	@Override
	public String grepInvalidPatternInFile(String args) {
		return parseAndEvaluate(args, null);
	}
	
	/**
	 * Parses command line args from interface to run function
	 * @param args String command line arguments without 'grep ' in front
	 * @param stdin InputStream
	 * @return String containing grepped expression
	 */
	private String parseAndEvaluate(String args, InputStream stdin) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		try {
			GrepApplication app = new GrepApplication();
			String[] splittedArguments = args == null ?
					new String[0] : args.split("\\s+");
			app.run(splittedArguments, stdin, out);
			return out.toString();
		} catch (GrepException e) {
			return e.getMessage();
		}
	}

}
