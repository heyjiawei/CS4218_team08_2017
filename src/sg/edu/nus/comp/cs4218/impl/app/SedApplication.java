package sg.edu.nus.comp.cs4218.impl.app;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
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
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.SedException;

public class SedApplication implements Sed {
	private String delimiter = null;

	/**
	 * Runs application with specified input data and specified output stream.
	 */
	@Override
	public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {
		if (args.length == 0) {
			throw new SedException("No replacement detected.");
		}
		if (stdin == null) {
			throw new SedException("No input stream provided");
		}
		if (stdout == null) {
			throw new SedException("No output stream provided");
		}
		String output = "";
		String commandLine = "";
		for (int i = 0; i < args.length; i++) {
			commandLine += args[i] + " ";
		}
		commandLine = commandLine.trim();
		this.delimiter = getDelimiter(args[0]);
		if (hasSufficientDelimiter(args[0]) &&
			isValidRegex(args[0]) && 
			isValidReplacement(args[0])) {
			if (isFileDirectoryValid(args)) {
				if (args[0].endsWith("g")) {
					output = replaceAllSubstringsInFile(commandLine);
				} else if (args[0].endsWith(this.delimiter)) {
					output = replaceFirstSubStringInFile(commandLine);
				} else {
					throw new SedException("Invalid flag");
				}
				
			} else if (!isInputStreamEmpty(stdin)) {
				if (args[0].endsWith("g")) {
					output = replaceAllSubstringsInStdin(commandLine, stdin);
				} else if (args[0].endsWith(delimiter)) {
					output = replaceFirstSubStringFromStdin(commandLine, stdin);
				} else {
					throw new SedException("Invalid flag");
				}
				
			} else {
				throw new SedException("No input detected. Please ensure filename is correct or stdin is not empty");
			}
		}
		if (!hasSufficientDelimiter(args[0])) {
			throw new SedException("Incorrect delimiter count");
		}
		if (!isValidReplacement(args[0])) {
			output = replaceSubstringWithInvalidReplacement(commandLine);
		}
		if (!isValidRegex(args[0])) {
			output = replaceSubstringWithInvalidRegex(commandLine);
		}
		try {
			stdout.write(output.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean hasSufficientDelimiter(String string) throws SedException{
		String delimiter = getDelimiter(string);
		int delimiterCount = 0;
		int delimiterIndex;
		String tmpStr = string;
		while ((delimiterIndex = tmpStr.indexOf(delimiter)) > -1) {
			delimiterCount++;
			tmpStr = tmpStr.substring(delimiterIndex + 1);
		}
		
		if (delimiterCount == 3) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isValidReplacement(String string) throws SedException {
		String delimiter = getDelimiter(string);
		String replacement = split(string, delimiter)[1];
		try {
			Pattern.compile(replacement);
		} catch (PatternSyntaxException e) {
			return false;
		}
		return true;
	}

	private boolean isValidRegex(String string) throws SedException {
		String delimiter = getDelimiter(string);
		try {
			getRegexPattern(string, delimiter);
		} catch (PatternSyntaxException e) {
			return false;
		}
		return true;
	}

	private boolean isInputStreamEmpty(InputStream stdin) {
		try {
			if (stdin.read() > -1) {
				stdin.reset();
				return false;
			} else {
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	private boolean isFileDirectoryValid(String[] args) {
		if (args.length <= 1) {
			return false;
		} 
		String filename = args[args.length-1];

		File file = new File(filename);
	    boolean isFile = file.exists() && file.isFile();
	    
	    if (isFile) {
	    	return true;
	    } else {
	    	return false;
	    }
	}
	
	private String getDelimiter(String args) throws SedException {
		if (this.delimiter != null) {
			return this.delimiter;
		} else {
			int indexOfS = args.indexOf('s');
			if (indexOfS > -1) {
				String delimiter = args.substring(indexOfS + 1, indexOfS + 2);
				if (delimiter.matches("[\n\\s\\\r\t]")) {
					throw new SedException("Invalid delimiter. Delimter cannot consist newline or backslash");
				} else {
					this.delimiter = delimiter;
					return this.delimiter;
				}
			} else {
				throw new SedException("Invalid sed command. Cannot detect delimiter");
			}
		}
	}
	
	private Pattern getRegexPattern(String args, String delimiter) throws PatternSyntaxException {
		String[] parts = split(args, delimiter);
		return Pattern.compile(parts[0]);
	}
	
	private String[] split(String string, String delimiter) {
		int pos = -1;
		int nextPos = -1;
		ArrayList<String> parts = new ArrayList<String>();
		while ((pos = string.indexOf(delimiter)) != -1) {
			nextPos = string.indexOf(delimiter, pos + 1);
			if (nextPos != -1) {
				parts.add(string.substring(pos + 1, nextPos));
				string = string.substring(nextPos);
			} else {
				break;
			}
		}
		return parts.toArray(new String[parts.size()]);
	}
	
	private String replaceFromStdin(String args, InputStream stdin, boolean isReplaceFirst) {
		String output = "";
		String line = "";
		
		BufferedInputStream is = new BufferedInputStream(stdin);
		byte[] c = new byte[1024];
        int readChars = 0;
		try {
			String delimiter = getDelimiter(args);
			Pattern pattern = getRegexPattern(args, delimiter);
			String replacement = split(args, delimiter)[1];
			
			while ((readChars = is.read(c)) != -1) {
				line = new String(c, 0, readChars);
				String[] parts = line.split("\n");
				
				for (int i = 0; i < parts.length; i++) {
					Matcher matcher = pattern.matcher(parts[i]);
					if (isReplaceFirst) {
						output += matcher.replaceFirst(replacement);
					} else {
						output += matcher.replaceAll(replacement);
					}
					
					output += "\n";
				}
				
				if (line.lastIndexOf('\n') < (line.length() - 1)) {
					output = output.substring(0, output.length()-1);
				}
			}
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SedException e) {
			e.printStackTrace();
		}
		return output;
	}
	
	private String replaceFromFile(String args, boolean isReplaceFirst) {
		String[] argsParts = args.split("\\s+");
		String filename = argsParts[argsParts.length - 1];
		String output = "";
		String line;
		
		BufferedReader reader;
		char[] c = new char[1024];
		int readChars = 0;
		try {
			String delimiter = getDelimiter(args);
			Pattern pattern = getRegexPattern(args, delimiter);
			String replacement = split(args, delimiter)[1];
			reader = new BufferedReader(new FileReader(filename));
			while ((readChars = reader.read(c)) != -1) {
				line = new String(c, 0, readChars);
				String[] parts = line.split("\n");
				
				for (int i = 0; i < parts.length; i++) {
					Matcher matcher = pattern.matcher(parts[i]);
					if (isReplaceFirst) {
						output += matcher.replaceFirst(replacement);
					} else {
						output += matcher.replaceAll(replacement);
					}
					
					output += "\n";
				}
				
				if (line.lastIndexOf('\n') < (line.length() - 1)) {
					output = output.substring(0, output.length()-1);
				}
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SedException e) {
			e.printStackTrace();
		}
		
		return output;
	}

	/**
	 * Returns string containing lines with the first matched substring replaced
	 * in file
	 * @param args String containing command and arguments
	 */
	@Override
	public String replaceFirstSubStringInFile(String args) {
		return replaceFromFile(args, true);
	}

	/**
	 * Returns string containing lines with all matched substring replaced in
	 * file
	 * @param args String containing command and arguments
	 */
	@Override
	public String replaceAllSubstringsInFile(String args) {
		return replaceFromFile(args, false);
	}

	/**
	 * Returns string containing lines with first matched substring replaced in
	 * Stdin
	 * @param args String containing command and arguments
	 * @param stdin InputStream containing Stdin
	 */
	@Override
	public String replaceFirstSubStringFromStdin(String args, InputStream stdin) {
		return replaceFromStdin(args, stdin, true);
	}

	/**
	 * Returns string containing lines with all matched substring replaced in
	 * Stdin
	 * @param args String containing command and arguments
	 * @param stdin InputStream containing Stdin
	 */
	@Override
	public String replaceAllSubstringsInStdin(String args, InputStream stdin) {
		return replaceFromStdin(args, stdin, false);
	}

	/**
	 * Returns string containing lines when invalid replacement string is
	 * provided
	 * @param args String containing command and arguments
	 */
	@Override
	public String replaceSubstringWithInvalidReplacement(String args) {
		return "sed: Invalid replacement string. Replacement of a backslash";
	}

	/**
	 * Returns string containing lines when invalid regex is provided
	 * @param args String containing command and arguments
	 */
	@Override
	public String replaceSubstringWithInvalidRegex(String args) {
		return "sed: Invalid regex pattern. Regex consist of a backslash";
	}

}
