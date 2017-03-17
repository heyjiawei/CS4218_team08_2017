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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import sg.edu.nus.comp.cs4218.app.Sed;
import sg.edu.nus.comp.cs4218.exception.SedException;

/**
 * Copies input file (or input stream) to stdout performing string replacement.
 * For each line containing a match to a specified pattern (in JAVA format),
 * replaces the matched substring with the specified string.
 * 
 * <p>
 * <b>Command format:</b> <code>sed REPLACEMENT [FILE]</code>
 * <dl>
 * <dt>REPLACEMENT</dt>
 * <dd>s/regexp/replacement/ –replace the first(in each line) substring matched
 * by regexp with the string replacement.</dd>
 * <dd>s/regexp/replacement/g -replace all the substrings matched by regexp with
 * the string replacement.</dd>
 * </dl>
 * <dl>
 * <dt>FILE</dt>
 * <dd>the name of the file(s). If no files are specified, use stdin.</dd>
 * </dl>
 * </p>
 */


@SuppressWarnings("PMD.GodClass")
public class SedApplication implements Sed {
	private String delimiter = null;

	/**
	 * Runs the sed application with the specified arguments.
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
	public void run(String[] args, InputStream stdin, OutputStream stdout) throws SedException {
		if (args == null) {
			throw new SedException("Null args\n");
		}
		if (args == null || args.length == 0 || (args.length > 0 && args[0].length() == 0)) {
			throw new SedException("No replacement detected\n");
		}
		if (args.length > 2) {
			throw new SedException("Incorrect number of args\n");
		}
		if (stdout == null) {
			throw new SedException("No output stream provided\n");
		}

		this.delimiter = getDelimiter(args);
		if (!hasSufficientDelimiter(args[0])) {
			throw new SedException("Unterminated Expression\n");
		}
		if (!isValidRegex(args[0])) {
			throw new SedException("Invalid regex pattern\n");
		}
		if (!isValidReplacement(args[0])) {
			throw new SedException("Invalid replacement string\n");
		}
		boolean replaceAll = isReplaceAll(args[0]);
		String output = "";
		if (args.length == 2) {
			output = replaceStringInFile(args, replaceAll);
		} else {
			if (stdin == null) {
				throw new SedException("Unknown Error\n");
			} else {
				output = replaceStringInStdin(args, stdin, replaceAll);
			}
		}

		try {
			if (output.length() == 0) {
				output += "\n";
			}
			stdout.write(output.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Conduct sed on stdin
	 * @param args Array containing expression
	 * @param stdin InputStream containing input
	 * @param replaceAll boolean true if flag g is present, false otherwise
	 * @return String replaced string
	 */
	private String replaceStringInStdin(String[] args, InputStream stdin, boolean replaceAll) {
		String output = "";
		String line = "";

		BufferedInputStream bufferedIs = new BufferedInputStream(stdin);
		byte[] byteArr = new byte[1024];
		int readChars = 0;
		try {
			String[] expressionParts = split(args[0], this.delimiter);
			Pattern pattern = Pattern.compile(expressionParts[1]);
			String replacement = expressionParts[2];

			while ((readChars = bufferedIs.read(byteArr)) != -1) {
				line = new String(byteArr, 0, readChars);
				String[] parts = line.split("\n");

				for (int i = 0; i < parts.length; i++) {
					Matcher matcher = pattern.matcher(parts[i]);
					if (replaceAll) {
						output += matcher.replaceAll(replacement);
					} else {
						output += matcher.replaceFirst(replacement);
					}

					output += "\n";
				}

			}
			bufferedIs.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return output;
	}

	/**
	 * Conduct Sed on file(s) provided
	 * 
	 * @param Array
	 *            containing expression and files
	 * @param replaceAll
	 *            if flag g is in expression
	 * @return replaced strings in file
	 * @throws SedException
	 */
	private String replaceStringInFile(String[] args, boolean replaceAll) throws SedException {
		String filename = args[args.length - 1];
		if (!isFileValid(filename)) {
			throw new SedException("Invalid File\n");
		}
	
		String output = "";
		String line;
	
		BufferedReader reader;
		char[] charArr = new char[1024];
		int readChars = 0;
		try {
			String[] expressionParts = split(args[0], this.delimiter);
			Pattern pattern = Pattern.compile(expressionParts[1]);
			String replacement = expressionParts[2];
	
			reader = new BufferedReader(new FileReader(filename));
			while ((readChars = reader.read(charArr)) != -1) {
				line = new String(charArr, 0, readChars);
				String[] parts = line.split("\n");
	
				for (int i = 0; i < parts.length; i++) {
					Matcher matcher = pattern.matcher(parts[i]);
					if (replaceAll) {
						output += matcher.replaceAll(replacement);
					} else {
						output += matcher.replaceFirst(replacement);
					}
	
					output += "\n";
				}
	
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		return output;
	}

	/**
	 * Return true if args is a valid, exising file
	 * 
	 * @param args
	 * @return boolean true if file and file exists, false otherwise
	 */
	private boolean isFileValid(String args) {
		File file = new File(args);
		return file.exists() && file.isFile();
	}

	/**
	 * Checks if the flag g is at the back of the expression and returns true if
	 * it exists, false otherwise
	 * 
	 * @param string
	 * @return boolean true if flag g is at the back of the expression, false
	 *         otherwise
	 * @throws SedException
	 */
	private boolean isReplaceAll(String string) throws SedException {
		String[] parts = split(string, this.delimiter);
		String flag = parts[parts.length - 1];
		if ("g".equalsIgnoreCase(flag)) {
			return true;
		} else if (flag.length() == 0) {
			return false;
		} else {
			throw new SedException("Invalid flag\n");
		}
	}

	/**
	 * Checks if the number of delimiters in replacement is correct
	 * 
	 * @param args
	 *            String containing expression
	 * @return boolean true if there are 3 delimiters in replacement, false
	 *         otherwise
	 * @throws SedException
	 *             if it is an invalid delimiter
	 */
	private boolean hasSufficientDelimiter(String string) throws SedException {
		int delimiterCount = 0;
		int delimiterIndex;
		String tmpStr = "";
		if (string.length() >= 2) {
			tmpStr = string.substring(1);
		}
		while ((delimiterIndex = tmpStr.indexOf(this.delimiter)) > -1) {
			delimiterCount++;
			tmpStr = tmpStr.substring(delimiterIndex + 1);
		}

		return delimiterCount == 3 ? true : false;
	}

	/**
	 * Checks if the replacement string is valid
	 * 
	 * @param string
	 *            String containing expression
	 * @return boolean true if the replacement string is valid, false otherwise
	 */
	private boolean isValidReplacement(String string) {
		String[] expressionParts = split(string, this.delimiter);
		String replacement = "";
		if (expressionParts.length >= 3) {
			replacement = expressionParts[2];
		}
		return replacement.contains(this.delimiter) ? false : true;

	}

	/**
	 * Checks if the regex string is valid
	 * 
	 * @param string
	 *            String containing expression
	 * @return boolean true if the regex string is valid, false otherwise
	 */
	private boolean isValidRegex(String string) {
		boolean verdict = true;
		try {
			String[] parts = split(string, this.delimiter);
			if (parts.length > 2 && parts[1].length() == 0) {
				verdict = false;
			}
			Pattern.compile(parts[1]);
		} catch (PatternSyntaxException e) {
			verdict = false;
		}
		return verdict;
	}

	/**
	 * Retrieve delimiter from command string. Checks if delimiter character is
	 * not [\n\\s\\\r\t]
	 * 
	 * @param args
	 *            String containing command and arguments
	 * @return sets delimiter and returns delimiter
	 * @throws SedException
	 *             if delimiter character is any of the following: [\n\\s\\\r\t]
	 *             or if it is an invalid expression
	 */
	private String getDelimiter(String... args) throws SedException {
		int indexOfS = args[0].indexOf('s');
		if ((indexOfS == 0) && args[0].length() >= 2) {
			String delimiter = args[0].substring(1, 2);
			if (delimiter.matches("[\n\\s\\\r\t]")) {
				throw new SedException("Invalid delimiter\n");
			} else {
				return delimiter;
			}
		} else {
			throw new SedException("Invalid expression\n");
		}
	}

	/**
	 * Splits the given string with the provided delimiter. Different from
	 * String.split as delimiter is a String rather than regex
	 * 
	 * @param args
	 *            String containing replacement
	 * @param delimiter
	 *            String
	 * @return String[] after splitting with delimiter
	 */
	private String[] split(String string, String delimiter) {
		String expression = string.substring(1);
		int pos = -1;
		int nextPos = -1;
		ArrayList<String> parts = new ArrayList<String>();
		parts.add("s");
		while ((pos = expression.indexOf(delimiter)) != -1) {
			nextPos = expression.indexOf(delimiter, pos + 1);
			if (nextPos == -1) {
				if (expression.length() == 1 && expression.equals(this.delimiter)) {
					parts.add("");
				} else if (expression.length() > 1) {
					parts.add(expression.substring(1));
				}
				break;
			} else {
				parts.add(expression.substring(pos + 1, nextPos));
				expression = expression.substring(nextPos);
			}
		}
		return parts.toArray(new String[parts.size()]);
	}

	/**
	 * Returns string containing lines with the first matched substring replaced
	 * in file
	 * 
	 * @param args
	 *            String containing command and arguments
	 */
	@Override
	public String replaceFirstSubStringInFile(String args) {
		return parseAndEvaluate(args, null);
	}

	/**
	 * Returns string containing lines with all matched substring replaced in
	 * file
	 * 
	 * @param args
	 *            String containing command and arguments
	 */
	@Override
	public String replaceAllSubstringsInFile(String args) {
		return parseAndEvaluate(args, null);
	}

	/**
	 * Returns string containing lines with first matched substring replaced in
	 * Stdin
	 * 
	 * @param args
	 *            String containing command and arguments
	 * @param stdin
	 *            InputStream containing Stdin
	 */
	@Override
	public String replaceFirstSubStringFromStdin(String args, InputStream stdin) {
		return parseAndEvaluate(args, stdin);
	}

	/**
	 * Returns string containing lines with all matched substring replaced in
	 * Stdin
	 * 
	 * @param args
	 *            String containing command and arguments
	 * @param stdin
	 *            InputStream containing Stdin
	 */
	@Override
	public String replaceAllSubstringsInStdin(String args, InputStream stdin) {
		return parseAndEvaluate(args, stdin);
	}

	/**
	 * Returns string containing lines when invalid replacement string is
	 * provided
	 * 
	 * @param args
	 *            String containing command and arguments
	 */
	@Override
	public String replaceSubstringWithInvalidReplacement(String args) {
		return parseAndEvaluate(args, null);
	}

	/**
	 * Returns string containing lines when invalid regex is provided
	 * 
	 * @param args
	 *            String containing command and arguments
	 */
	@Override
	public String replaceSubstringWithInvalidRegex(String args) {
		return parseAndEvaluate(args, null);
	}

	/**
	 * Parses command line args from interface to run function
	 * @param args String command line arguments without 'sed ' in front
	 * @param stdin InputStream
	 * @return String replaced string
	 */
	private String parseAndEvaluate(String args, InputStream stdin) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			SedApplication app = new SedApplication();
			String[] splittedArguments = args == null ? new String[0] : args.split("\\s+");
			app.run(splittedArguments, stdin, out);
			return out.toString();
		} catch (SedException e) {
			return e.getMessage();
		}
	}
}
