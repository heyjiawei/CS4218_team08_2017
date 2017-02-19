package sg.edu.nus.comp.cs4218.impl.app;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import sg.edu.nus.comp.cs4218.app.Sed;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;

public class SedApplication implements Sed {
	private String filename = null;

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
		if (isValidDelimiter(args) &&
			isValidRegex(args) && 
			isValidReplacement(args)) {
			if (isFileDirectoryValid(args)) {
				output = replaceStringInFile(args);
				
			} else if (!isInputStreamEmpty(stdin)) {
				output = replaceStringInStdin(args, stdin);
				
			} else {
				throw new SedException("No input detected. Please ensure filename is correct or stdin is not empty");
			}
		} //else {
//			String commandLine = "";
//			for (int i = 0; i < args.length; i++) {
//				commandLine += args[i] + " ";
//			}
//			if (!isValidRegex) { 
//				replaceSubstringWithInvalidRegex(commandLine.trim());
//			} else if (!isValidReplacement) {
//				replaceSubstringWithInvalidReplacement(commandLine.trim());
//			}
		//}
		try {
			stdout.write(output.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String replaceStringInStdin(String[] args, InputStream stdin) {
		// TODO Auto-generated method stub
		return null;
	}

	private String replaceStringInFile(String[] args) {
		// TODO Auto-generated method stub
		return null;
	}

	private boolean isValidDelimiter(String[] args) {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean isValidReplacement(String[] args) {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean isValidRegex(String[] args) {
		String delimiter = getDelimiter(args[0]);
		
		return false;
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
		this.filename = args[args.length-1];

		File file = new File(this.filename);
	    boolean isFile = file.exists() && file.isFile();
	    
	    if (isFile) {
	    	return true;
	    } else {
	    	return false;
	    }
	}

//	private String getInputFromFile(String filename) {
//		String line = "";
//		String output = "";
//		BufferedReader reader;
//		try {
//			reader = new BufferedReader(new FileReader(filename));
//			while ((line = reader.readLine()) != null) {
//				output += line;
//			}
//			reader.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return line;
//	}
	
//	private String getInputFromStdin(InputStream stdin) {
//		String line = "";
//		BufferedInputStream is = new BufferedInputStream(stdin);
//		byte[] c = new byte[1024];
//        int readChars = 0;
//		try {
//			while ((readChars = is.read(c)) != -1) {
//				line += new String(c, 0, readChars);
//			}
//			is.close();
////			stdin.reset();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return line;
//	}
	
	private String getDelimiter(String args) throws SedException {
		int indexOfS = args.indexOf('s');
		String delimiter = args.substring(indexOfS + 1, indexOfS + 2);
		if (delimiter.matches("[\n|\\]")) {
			throw new SedException("Invalid delimiter. Delimter consist of newline or backslash");
		} else {
			return delimiter;
		}
	}
	
	private Pattern getRegexPattern(String args, String delimiter) throws PatternSyntaxException {
		String[] parts = args.split(delimiter);
		return Pattern.compile(parts[1]);
	}
	
	private String replaceFromStdin(String args, InputStream stdin, boolean isReplaceFirst) {
		String output = "";
		String line = "";
		String delimiter = getDelimiter(args);
		Pattern pattern = getRegexPattern(args, delimiter);
		String replacement = args.split(delimiter)[2];
		
		BufferedInputStream is = new BufferedInputStream(stdin);
		byte[] c = new byte[1024];
        int readChars = 0;
		try {
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
			
//			while ((readChars = is.read(c)) != -1) {
//				line += new String(c, 0, readChars);
//				Matcher matcher = pattern.matcher(line);
//				String replacement = args.split(delimiter)[2];
//				
//				if (isReplaceFirst) {
//					output += matcher.replaceFirst(replacement);
//				} else {
//					output += matcher.replaceAll(replacement);
//				}
//				
//				if (line.indexOf('\n') != -1) {
//					output += "\n";
//				}
//			}
			is.close();
//			stdin.reset();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return output;
	}
	
	private String replaceFromFile(String args, boolean isReplaceFirst) {
		String[] argsParts = args.split("\\s+");
		String filename = argsParts[argsParts.length - 1];
		String output = "";
		String line;
		String delimiter = getDelimiter(args);
		Pattern pattern = getRegexPattern(args, delimiter);
		String replacement = args.split(delimiter)[2];
		
		BufferedReader reader;
		char[] c = new char[1024];
		int readChars = 0;
		try {
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
		}
		
		return output;
	}

//	private String replace(String args, InputStream stdin, boolean isReplaceFirst) {
//		String line;
//		if (stdin == null) {
//			String[] argsParts = args.split("\\s+");
//			line = getInputFromFile(argsParts[argsParts.length - 1]);
//			
//		} else {
//			line = getInputFromStdin(stdin);
//		}
//		
//		int indexOfS = args.indexOf('s');
//		String delimiter = args.substring(indexOfS, indexOfS + 1);
//		String[] parts = args.split(delimiter);
//		Pattern pattern = Pattern.compile(parts[1]);
//		Matcher matcher = pattern.matcher(line);
//		
//		if (isReplaceFirst) {
//			return matcher.replaceFirst(parts[2]);
//		} else {
//			return matcher.replaceAll(parts[2]);
//		}
//	}


	/**
	 * Returns string containing lines with the first matched substring replaced
	 * in file
	 * @param args String containing command and arguments
	 */
	@Override
	public String replaceFirstSubStringInFile(String args) {
		// TODO Auto-generated method stub
		/*
		 * args take in command from args string array. contains filename to read
		 * input from
		 * 1. retrieve delimiter. It will be after s in s/regex/replacement/
		 * you can use a private function for this
		 * 2. split string by delimiter
		 * 3. create regex
		 * 4. create replaced var
		 * 5. open file and read line by line
		 * 6. replace first substring in line and append to replaced var
		 * 7. returned replaced var
		 */
		return replaceFromFile(args, true);
	}

	/**
	 * Returns string containing lines with all matched substring replaced in
	 * file
	 * @param args String containing command and arguments
	 */
	@Override
	public String replaceAllSubstringsInFile(String args) {
		// TODO Auto-generated method stub
		/*
		 * args take in command from args string array. contains filename to read
		 * input from
		 * 1. retrieve delimiter. It will be after s in s/regex/replacement/
		 * you can use a private function for this
		 * 2. split string by delimiter
		 * 3. create regex
		 * 4. create replaced var
		 * 5. open file and read line by line
		 * 6. replace all substring in line and append to replaced var
		 * 7. returned replaced var
		 */
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
		// TODO Auto-generated method stub
		/* args = sed s/regex/replacement/ line read from stdin with new
		 * line denoted by \n
		 * 1. retrieve stdin and split stdin by \n (newline). 
		 * store in stdin string array var
		 * - if this doesn't work, need to pass stdin line by line from run
		 * 2. retrieve delimiter. It will be after s in s/regex/replacement/
		 * you can use a private function for this
		 * 3. split string by delimiter
		 * 4. create regex
		 * 5. for each in stdin array, replace first substring in line
		 * 6. after replacing all string in stdin array, join array with newline 
		 * and return new string
		 */
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
		// TODO Auto-generated method stub
		/* args = sed s/regex/replacement/ line read from stdin with new
		 * line denoted by \n
		 * 1. retrieve stdin and split stdin by \n (newline). 
		 * store in stdin string array var
		 * - if this doesn't work, need to pass stdin line by line from run
		 * 2. retrieve delimiter. It will be after s in s/regex/replacement/
		 * you can use a private function for this
		 * 3. split string by delimiter
		 * 4. create regex
		 * 5. for each in stdin array, replace all substring in line
		 * 6. after replacing all string in stdin array, join array with newline 
		 * and return new string
		 */
		return replaceFromStdin(args, stdin, true);
	}

	/**
	 * Returns string containing lines when invalid replacement string is
	 * provided
	 * @param args String containing command and arguments
	 */
	@Override
	public String replaceSubstringWithInvalidReplacement(String args) {
		return "Invalid replacement string";
	}

	/**
	 * Returns string containing lines when invalid regex is provided
	 * @param args String containing command and arguments
	 */
	@Override
	public String replaceSubstringWithInvalidRegex(String args) {
		return "Invalid regex pattern";
	}

}
