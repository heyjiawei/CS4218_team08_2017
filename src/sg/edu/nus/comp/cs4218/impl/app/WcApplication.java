package sg.edu.nus.comp.cs4218.impl.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Scanner;

import sg.edu.nus.comp.cs4218.app.Wc;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.WcException;

public class WcApplication implements Wc {

	/**
	 * Runs application with specified input data and specified output stream.
	 */
	@Override
	public void run(String[] args, InputStream stdin, OutputStream stdout) 
			throws AbstractApplicationException {
		// TODO Auto-generated method stub
		/*
		 * 1 Check for args.length > 0
		 * 2 check stdin != null
		 * 3 check stdout != null
		 * 4 check for invalid command
		 * 5 create var count
		 * 6 if else to send args and/or stdin to relevant function
		 * 7 append results to count var
		 * 8 write to stdout
		 */
		if (stdout == null) {
			throw new WcException("No output stream provided");
		}
		if (stdin == null) {
			// is the checking of stdin == null necessary?
			// Will there be a case InputStream will not be passed?
			throw new WcException("No input stream provided");
		}
		// Check if filename is provided
		// 1 check if args length > 1
		// 2 assume last position in args[] contain filename
		// 3 check that it is a file (isfile)
		// throw error if file cannot be found
		String countStr = "";
		if (isFilenameProvided(args)) {
			if (isFileDirectoryValid(args)) {
				countStr = getCount(args, null, false);
			}
		
		} else if (!isInputStreamEmpty(stdin)) {
			countStr = getCount(args, stdin, true);
			
		} else {
			throw new WcException("No input detected. Please add file(s) or ensure stdin is not empty");
		}
		
		//write to stdout
		PrintWriter writer = new PrintWriter(stdout);
		writer.println(countStr);
	}
	
	private boolean[] optionsSeparator(String[] args) throws WcException {
		// separates the options to -m, -w, -l respectively
		// if -wlm is given, returns -m -w -l
		// returns in the order to be printed 
		// removes recurring options
		// throws error if an invalid option is given
		/*
		 * create boolean array [-m, -w, -l]
		 * paste args together and store args length
		 * 1 find -l, -m, -w
		 * 1a if it does not exist,
		 * 1ai check if there exist - character. if it does, throw exception
		 * 1aii the command is in the form wc. in this case, 
		 * return -m -w -l
		 * 1b if it does, check boolean array of that option. 
		 * 2 check if there exist a character after that by checking against string length
		 * 2a if the next character is m, w or l, 
		 * set boolean array of that option to true
		 * 2b if it is a space character, the next option might not exist.
		 * find -l, -m, -w
		 * 2bi if it does not exist, check if there exist - character. 
		 * if it does, throw exception
		 * 2bii if - does not exist, you can return according to boolean array
		 * 2c if there is no character after that (reached string length), return -l, -m, -w
		 * depending on the boolean array
		 */
		boolean[] options = new boolean[3];
		String command = "";
		for (int i = 0; i < args.length-1; i++) {
			command += args[i] + " ";
		}
		int startIndex = 0;
		int dashPosition = command.indexOf('-');
		if (dashPosition == -1) {
			Arrays.fill(options, true);
			return options;
			
		} else {
			startIndex = dashPosition;
			while (startIndex > -1 && startIndex < command.length()) {
				if (startIndex + 1 == command.length()) {
					throw new WcException("Illegal Option. Please put \"-m\", \"-w\" or \"-l\"");
				}
				
				String nextChar = command.substring(startIndex, startIndex + 1);
				if ("m".equals(nextChar)) {
					options[0] = true;
					startIndex = startIndex + 1;
					
				} else if ("w".equals(nextChar)) {
					options[1] = true;
					startIndex = startIndex + 1;
					
				} else if ("l".equals(nextChar)) {
					options[2] = true;
					startIndex = startIndex + 1;
				
				} else if (" ".equals(nextChar)) {
					dashPosition = command.indexOf("-", startIndex);
					if (dashPosition > -1) {
						startIndex = dashPosition;
					} else {
						break;
					}
				} else {
					throw new WcException("Invalid Option. -" + nextChar + " does not exist");
				}	
			}
		}
		
		return options;
	}

	/**
	 * Naively assume that filename is in last arg position
	 * @param args
	 * @param stdin
	 * @param isStdin
	 * @return
	 * @throws WcException
	 */
	private String getCount(String[] args, InputStream stdin, boolean isStdin) throws WcException {
		// TODO Auto-generated method stub
		/*
		 * Reads the options, calls the necessary function
		 * and returns appended count string
		 * -check isStdin. If false, open file from last args
		 * -if true, retrieve string from stdin
		 * 1. for either input (stdin or args)
		 * if-else with separatedOptions. if true, call that function and
		 * append to countStr
		 * 2. remove trailing whitespaces from countStr before returning countStr
		 */
		boolean[] options = optionsSeparator(args);
		String countStr = "";
		if (isStdin) {
			if (options[0]) { // -m
				countStr += printCharacterCountInStdin(stdin) + " ";
			} 
			if (options[1]) { // -w
				countStr += printWordCountInStdin(stdin) + " ";
			}
			if (options[2]) { // -l
				countStr += printNewlineCountInStdin(stdin);
			}
		} else {
			String filename = args[args.length-1];
			if (options[0]) { // -m
				countStr += printCharacterCountInFile(filename) + " ";
			} 
			if (options[1]) { // -w
				countStr += printWordCountInFile(filename) + " ";
			}
			if (options[2]) { // -l
				countStr += printNewlineCountInFile(filename);
			}
		}
		return countStr.trim();
	}

	private boolean isInputStreamEmpty(InputStream stdin) {
		try {
			if (stdin.read() > -1) {
				return true;
			} else {
				return false;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Naive implementation of checking if arguments contain filename
	 * Currently assumes that last arg is filename
	 * Uses "." to check if a file is provided
	 * @param args
	 * @return
	 */
	private boolean isFilenameProvided(String[] args) {
		// TODO Auto-generated method stub
		// Check if args length is correct for command with filename
		// 
		// 1 check if args length > 1
		// 2 assume last position in args[] contain filename
		// 3 check that it is a file (isfile)
		// throw error if file cannot be found
		/*
		 * testing considerations:
		 * wc _ _ where filename has a space character (wrong filename)
		 * but the last _ is a legit filename
		 * wc 'test 2.txt' should work for filename test 2.txt
		 * wc test q1.txt where q1.txt exist
		 * wc _ filename with space character escaped (correct filename)
		 */
		if (args.length > 1) {
			String filename = args[args.length-1];
			if (filename.contains(".")) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isFileDirectoryValid(String[] args) throws WcException {
		// Check if filename or directory is valid
		String filename = args[args.length-1];
		File tmpDir = new File(filename);
	    boolean isDir = tmpDir.exists() && tmpDir.isDirectory();

		File file = new File(filename);
	    boolean isFile = file.exists() && file.isFile();
	    
	    if (isDir || isFile) {
	    	return true;
	    } else {
	    	throw new WcException("No such file or directory");
	    }
	}

	/**
	 * Returns string containing the character count in file
	 * @param filename String filename of file to be read
	 */
	@Override
	public String printCharacterCountInFile(String filename) {
		// TODO Auto-generated method stub
		/*
		 * should receive arg: wc -m filename.txt
		 * 1 retrieve filename
		 * 2 count character in file
		 * 3 print out character count
		 */
		int charCount = 0;
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(filename));
			String line = "";
			while ((line = reader.readLine()) != null) {
				charCount += line.getBytes().length;
			}
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return String.valueOf(charCount);
	}

	/**
	 * Returns string containing the word count in file
	 * @param filename String filename of file to be read
	 */
	@Override
	public String printWordCountInFile(String filename) {
		// TODO Auto-generated method stub
		int wordCount = 0;
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(filename));
			String line = "";
			while ((line = reader.readLine()) != null) {
				wordCount += line.replaceAll("\\s+", " ").split(" ").length;
			}
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return String.valueOf(wordCount);
	}

	/**
	 * Returns string containing the newline count in file
	 * @param filename String filename of file to be read
	 */
	@Override
	public String printNewlineCountInFile(String filename) {
		// TODO Auto-generated method stub
		int lineCount = 0;
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(filename));
			String line = "";
			while ((line = reader.readLine()) != null) {
				lineCount ++;
			}
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return String.valueOf(lineCount);
	}

	/**
	 * Returns string containing all counts in file
	 * @param args String containing command and arguments
	 */
	@Override
	public String printAllCountsInFile(String args) { // Can I ignore this method?
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Returns string containing the character count in Stdin
	 * @param args String containing command and arguments
	 */
	@Override
	public String printCharacterCountInStdin(InputStream stdin) {
		// TODO Auto-generated method stub
		int charCount = 0;
		try {
			while (stdin.read() > -1) {
				charCount ++;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return String.valueOf(charCount);
	}

	/**
	 * Returns string containing the word count in Stdin
	 * @param args String containing command and arguments
	 */
	@Override
	public String printWordCountInStdin(InputStream stdin) {
		// TODO Auto-generated method stub
		int wordCount = 0;
		Scanner scanner = new Scanner(stdin);
		scanner.useDelimiter("\\A");
		while (scanner.hasNext()) {
			wordCount++;
			scanner.next();
		}
		scanner.close();
		return String.valueOf(wordCount);
	}

	/**
	 * Returns string containing the newline count in Stdin
	 * @param args String containing command and arguments
	 */
	@Override
	public String printNewlineCountInStdin(InputStream stdin) {
		// TODO Auto-generated method stub
		int lineCount = 0;
		Scanner scanner = new Scanner(stdin);
		scanner.useDelimiter("\\A");
		while (scanner.hasNextLine()) {
			lineCount++;
			scanner.nextLine();
		}
		scanner.close();
		return String.valueOf(lineCount);
	}

	/**
	 * Returns string containing all counts in Stdin
	 * @param args String containing command and arguments
	 */
	@Override
	public String printAllCountsInStdin(InputStream stdin) { // Can I ignore this method?
		// TODO Auto-generated method stub
		return null;
	}

}
