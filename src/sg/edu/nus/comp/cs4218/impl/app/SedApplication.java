package sg.edu.nus.comp.cs4218.impl.app;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sg.edu.nus.comp.cs4218.app.Sed;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;

public class SedApplication implements Sed {

	/**
	 * Runs application with specified input data and specified output stream.
	 */
	@Override
	public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {
		// TODO Auto-generated method stub
		/*
		 * args contains s/regex/replacement <optional filename>
		 * stdin contains input file or input stream
		 * checks validity (format) of command
		 * 1 check command is valid - 
		 * 1a check args length equal or larger than 2 
		 * (sed 's/day/ /' or sed s/day/night/g < filename)
		 * must take quoted string as 1 string
		 * 2 check stdin for null, return exception if stdin is null
		 * 3 check stdout for null, return exception if stdout is null
		 * 4 prepare replaced var to store output
		 * 5 if file, just loop once, else, for each line in stdin
		 * 5a check if args contains g at the end (sed s/is/are/ or sed s/is/are/g)
		 * 5b switch case to relevant function
		 * 5c store replaced string in replaced var and write to stdout
		 * (make sure replaced var is appended to stdout)
		 */
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
	
	private String getDelimiter(String args) {
		int indexOfS = args.indexOf('s');
		return args.substring(indexOfS + 1, indexOfS + 2);
	}
	
	private Pattern getRegexPattern(String args, String delimiter) {
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
		// TODO Auto-generated method stub
		/*
		 * invalid replacement string contains separation symbol inside  
		 * 
		 */
		return null;
	}

	/**
	 * Returns string containing lines when invalid regex is provided
	 * @param args String containing command and arguments
	 */
	@Override
	public String replaceSubstringWithInvalidRegex(String args) {
		// TODO Auto-generated method stub
		return null;
	}

}
