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
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import sg.edu.nus.comp.cs4218.app.Sed;
import sg.edu.nus.comp.cs4218.exception.SedException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.Parser;

/**
 * Copies input file (or input stream) to stdout performing string replacement. 
 * For each line containing a match to a specified pattern (in JAVA format), 
 * replaces the matched substring with the specified string.
 * 
 * <p>
 * <b>Command format:</b> <code>sed REPLACEMENT [FILE]</code>
 * <dl>
 * <dt>REPLACEMENT</dt>
 * <dd>s/regexp/replacement/ –replace the first(in each line) substring 
 * matched by regexp with the string replacement.</dd>
 * <dd>s/regexp/replacement/g -replace all the substrings matched by 
 * regexp with the string replacement.</dd>
 * </dl>
 * <dl>
 * <dt>FILE</dt>
 * <dd>the name of the file(s). If no files are specified, use stdin.</dd>
 * </dl>
 * </p>
 */

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
	 *             If the file(s) specified do not exist or are unreadable,
	 *             if there are no inputs, if replacement (regex or replaced 
	 *             characters or flags) is incorrect
	 *
	 */
	@Override
	public void run(String[] args, InputStream stdin, OutputStream stdout) throws SedException {
		if (args == null) {
			throw new SedException("Null args\n");
		}
		if (args == null || args.length == 0 || 
			(args.length > 0 && args[0].length() == 0)) {
			throw new SedException("No replacement detected\n");
		}
		if (args.length > 2) {
			throw new SedException("Incorrect number of args\n");
		}
//		if (stdin == null) {
//			throw new SedException("No input stream provided\n");
//		}
		if (stdout == null) {
			throw new SedException("No output stream provided\n");
		}
		
//		String commandLine = "";
//		for (int i = 0; i < args.length; i++) {
//			commandLine += args[i] + " ";
//		}
//		commandLine = commandLine.trim();
		this.delimiter = getDelimiter(args);
		if (hasSufficientDelimiter(args[0]) == false) {
			throw new SedException("Unterminated Expression\n");
		}
//		String[] expressionParts = split(args[0], this.delimiter);
		if (isValidRegex(args[0]) == false) {
			throw new SedException("Invalid regex pattern\n");
		}
		if (isValidReplacement(args[0]) == false) {
			throw new SedException("Invalid replacement string\n");
		}
		boolean replaceAll = isReplaceAll(args[0]);
		String output = "";
		if (args.length == 2) {
			output = replaceStringInFile(args, replaceAll);
		} else if (stdin != null 
//				&& 
//				isInputStreamEmpty(stdin) == false
				) {
			output = replaceStringInStdin(args, stdin, replaceAll);
		} else {
			throw new SedException("Unknown Error\n");
		}
//		if (hasSufficientDelimiter(args[0]) &&
//			isValidRegex(args[0]) && 
//			isValidReplacement(args[0])) {
//			if (isFileDirectoryValid(args)) {
//				if (args[0].endsWith("g")) {
//					output = replaceAllSubstringsInFile(commandLine);
//				} else if (args[0].endsWith(this.delimiter)) {
//					output = replaceFirstSubStringInFile(commandLine);
//				} else {
//					throw new SedException("Invalid flag");
//				}
//				
//			} else if (!isInputStreamEmpty(stdin)) {
//				if (args[0].endsWith("g")) {
//					output = replaceAllSubstringsInStdin(commandLine, stdin);
//				} else if (args[0].endsWith(delimiter)) {
//					output = replaceFirstSubStringFromStdin(commandLine, stdin);
//				} else {
//					throw new SedException("Invalid flag");
//				}
//				
//			} else {
//				throw new SedException("No input detected. Please ensure filename is correct or stdin is not empty");
//			}
//		}
//		if (!hasSufficientDelimiter(args[0])) {
//			throw new SedException("Incorrect delimiter count");
//		}
//		if (!isValidReplacement(args[0])) {
//			output = replaceSubstringWithInvalidReplacement(commandLine);
//		}
//		if (!isValidRegex(args[0])) {
//			output = replaceSubstringWithInvalidRegex(commandLine);
//		}
		try {
			if (output.length() == 0) {
				output += "\n";
			} 
			stdout.write(output.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String replaceStringInStdin(String[] args, InputStream stdin, boolean replaceAll) {
		String output = "";
		String line = "";
		
		BufferedInputStream is = new BufferedInputStream(stdin);
		byte[] c = new byte[1024];
        int readChars = 0;
		try {
			String[] expressionParts = split(args[0], this.delimiter);
			Pattern pattern = Pattern.compile(expressionParts[1]);
			String replacement = expressionParts[2];
			
			while ((readChars = is.read(c)) != -1) {
				line = new String(c, 0, readChars);
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
				
//				if (line.lastIndexOf('\n') < (line.length() - 1)) {
//					output = output.substring(0, output.length()-1);
//				}
			}
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return output;
	}

	private boolean isFileValid(String args) {
		File file = new File(args);
	    return file.exists() && file.isFile();
	}
	
	private String replaceStringInFile(String[] args, boolean replaceAll) throws SedException {
		String filename = args[args.length - 1];
		if (isFileValid(filename) == false) {
			throw new SedException("Invalid File\n");
		}
		
		String output = "";
		String line;
		
		BufferedReader reader;
		char[] c = new char[1024];
		int readChars = 0;
		try {
			String[] expressionParts = split(args[0], this.delimiter);
			Pattern pattern = Pattern.compile(expressionParts[1]);
			String replacement = expressionParts[2];
			
			reader = new BufferedReader(new FileReader(filename));
			while ((readChars = reader.read(c)) != -1) {
				line = new String(c, 0, readChars);
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
				
//				if (line.lastIndexOf('\n') < (line.length() - 1)) {
//					output = output.substring(0, output.length()-1);
//				}
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return output;
	}

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
	 * @param args String containing command and arguments
	 * @return boolean true if there are 3 delimiters in replacement, false otherwise
	 * @throws SedException if it is an invalid delimiter
	 */
	private boolean hasSufficientDelimiter(String string) throws SedException{
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
		
		if (delimiterCount == 3) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Checks if the replacement string is valid (does not contain backslash)
	 * @param string String containing command and arguments
	 * @return boolean true if the replacement string is valid, false otherwise
	 * @throws SedException if it is an invalid delimiter
	 */
	private boolean isValidReplacement(String string) {
		String[] expressionParts = split(string, this.delimiter);
		String replacement = "";
		if (expressionParts.length >= 3) {
			replacement = expressionParts[2];
		}
//		try {
//			Pattern.compile(replacement);
//		} catch (PatternSyntaxException e) {
//			return false;
//		}
		if (replacement.contains(this.delimiter)) {
			return false;
		} else {
			return true;
		}
		
	}

	/**
	 * Checks if the regex string is valid (does not contain backslash)
	 * @param string String containing command and arguments
	 * @return boolean true if the regex string is valid, false otherwise
	 * @throws SedException if it is an invalid delimiter
	 */
	private boolean isValidRegex(String string) throws SedException {
		boolean verdict = true;
		try {
//			getRegexPattern(string, this.delimiter);
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
	 * Creates and return a pattern object from the regex pattern
	 * @param args String containing command and arguments
	 * @param delimiter String delimiter in this command
	 * @return Pattern object consisting of regex pattern
	 * @throws PatternSyntaxException if regex pattern is invalid
	 */
//	private Pattern getRegexPattern(String args, String delimiter) throws PatternSyntaxException {
//		String[] parts = split(args, delimiter);
//		return Pattern.compile(parts[1]);
//	}

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
//	private boolean isFileDirectoryValid(String[] args) {
//		if (args.length <= 1) {
//			return false;
//		} 
//		String filename = args[args.length-1];
//
//		File file = new File(filename);
//	    boolean isFile = file.exists() && file.isFile();
//	    
//	    if (isFile) {
//	    	return true;
//	    } else {
//	    	return false;
//	    }
//	}
	
	/**
	 * Retrieve delimiter from command string.
	 * Checks if delimiter character is not [\n\\s\\\r\t]
	 * @param args String containing command and arguments
	 * @return sets delimiter and returns delimiter
	 * @throws SedException if delimiter character is any of the following: [\n\\s\\\r\t]
	 */
	private String getDelimiter(String[] args) throws SedException {
		int indexOfS = args[0].indexOf('s');
		if ((indexOfS == 0) && 
			args[0].length() >= 2) {
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
	 * Splits the given string with the provided delimiter. Different from String.split
	 * as delimiter is a String rather than regex
	 * @param args String containing replacement
	 * @param delimiter String 
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
			if (nextPos != -1) {
				parts.add(expression.substring(pos + 1, nextPos));
				expression = expression.substring(nextPos);
			} else {
				if (expression.length() == 1 && expression.equals(this.delimiter)) {
					parts.add("");
				} else if (expression.length() > 1) {
					parts.add(expression.substring(1));
				}
				break;
			}
		}
		return parts.toArray(new String[parts.size()]);
	}
	
	/**
	 * Perform string replacement on input stream string
	 * @param args String containing command and arguments
	 * @param stdin InputStream containing string
	 * @param isReplaceFirst boolean true if there is no flag, false if flag 'g' exists
	 * @return replaced string
	 */
//	private String replaceFromStdin(String args, InputStream stdin, boolean isReplaceFirst) {
//		String output = "";
//		String line = "";
//		
//		BufferedInputStream is = new BufferedInputStream(stdin);
//		byte[] c = new byte[1024];
//        int readChars = 0;
//		try {
//			String delimiter = getDelimiter(args);
//			Pattern pattern = getRegexPattern(args, delimiter);
//			String replacement = split(args, delimiter)[1];
//			
//			while ((readChars = is.read(c)) != -1) {
//				line = new String(c, 0, readChars);
//				String[] parts = line.split("\n");
//				
//				for (int i = 0; i < parts.length; i++) {
//					Matcher matcher = pattern.matcher(parts[i]);
//					if (isReplaceFirst) {
//						output += matcher.replaceFirst(replacement);
//					} else {
//						output += matcher.replaceAll(replacement);
//					}
//					
//					output += "\n";
//				}
//				
//				if (line.lastIndexOf('\n') < (line.length() - 1)) {
//					output = output.substring(0, output.length()-1);
//				}
//			}
//			is.close();
//		} catch (IOException | SedException e) {
//			e.printStackTrace();
//		}
//		return output;
//	}
	
	/**
	 * Perform string replacement on string read from file
	 * @param args String containing command and arguments
	 * @param isReplaceFirst boolean true if there is no flag, false if flag 'g' exists
	 * @return replaced string
	 */
//	private String replaceFromFile(String args, boolean isReplaceFirst) {
//		String[] argsParts = args.split("\\s+");
//		String filename = argsParts[argsParts.length - 1];
//		String output = "";
//		String line;
//		
//		BufferedReader reader;
//		char[] c = new char[1024];
//		int readChars = 0;
//		try {
//			String delimiter = getDelimiter(args);
//			Pattern pattern = getRegexPattern(args, delimiter);
//			String replacement = split(args, delimiter)[1];
//			reader = new BufferedReader(new FileReader(filename));
//			while ((readChars = reader.read(c)) != -1) {
//				line = new String(c, 0, readChars);
//				String[] parts = line.split("\n");
//				
//				for (int i = 0; i < parts.length; i++) {
//					Matcher matcher = pattern.matcher(parts[i]);
//					if (isReplaceFirst) {
//						output += matcher.replaceFirst(replacement);
//					} else {
//						output += matcher.replaceAll(replacement);
//					}
//					
//					output += "\n";
//				}
//				
//				if (line.lastIndexOf('\n') < (line.length() - 1)) {
//					output = output.substring(0, output.length()-1);
//				}
//			}
//			reader.close();
//		} catch (IOException | SedException e) {
//			e.printStackTrace();
//		}
//		
//		return output;
//	}

	/**
	 * Returns string containing lines with the first matched substring replaced
	 * in file
	 * @param args String containing command and arguments
	 */
	@Override
	public String replaceFirstSubStringInFile(String args) {
		return parseAndEvaluate(args, null);
	}

	/**
	 * Returns string containing lines with all matched substring replaced in
	 * file
	 * @param args String containing command and arguments
	 */
	@Override
	public String replaceAllSubstringsInFile(String args) {
		return parseAndEvaluate(args, null);
	}

	/**
	 * Returns string containing lines with first matched substring replaced in
	 * Stdin
	 * @param args String containing command and arguments
	 * @param stdin InputStream containing Stdin
	 */
	@Override
	public String replaceFirstSubStringFromStdin(String args, InputStream stdin) {
		return parseAndEvaluate(args, stdin);
	}

	/**
	 * Returns string containing lines with all matched substring replaced in
	 * Stdin
	 * @param args String containing command and arguments
	 * @param stdin InputStream containing Stdin
	 */
	@Override
	public String replaceAllSubstringsInStdin(String args, InputStream stdin) {
		return parseAndEvaluate(args, stdin);
	}

	/**
	 * Returns string containing lines when invalid replacement string is
	 * provided
	 * @param args String containing command and arguments
	 */
	@Override
	public String replaceSubstringWithInvalidReplacement(String args) {
		return "sed: Invalid replacement string\n";
	}

	/**
	 * Returns string containing lines when invalid regex is provided
	 * @param args String containing command and arguments
	 */
	@Override
	public String replaceSubstringWithInvalidRegex(String args) {
		return "sed: Invalid regex pattern\n";
	}

	private String parseAndEvaluate(String args, InputStream stdin) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Parser parser = new Parser();
		try {
			SedApplication app = new SedApplication();
			Vector<String> parsed = parser.parseCallCommand(args);
			
			String[] splittedArguments = args == null ?
					new String[0] : args.split("\\s+");
			app.run(splittedArguments, stdin, out);
			return out.toString();
		} catch (SedException | ShellException e) {
			return e.getMessage();
		}
	}
}
