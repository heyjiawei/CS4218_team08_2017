package sg.edu.nus.comp.cs4218.impl.app;

import java.io.BufferedInputStream;
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
	private int charCount = 0;
	private int wordCount = 0;
	private int lineCount = 0;

	/**
	 * Runs application with specified input data and specified output stream.
	 */
	@Override
	public void run(String[] args, InputStream stdin, OutputStream stdout) 
			throws AbstractApplicationException {
		if (stdout == null) {
			throw new WcException("No output stream provided");
		}
		if (stdin == null) {
			throw new WcException("No input stream provided");
		}
		
		String countStr = "";
		boolean[] options = optionsSeparator(args);
		if (isFileDirectoryValid(args)) {
			countStr = callOption(args, options, null, false);
		}
		
		if (!isInputStreamEmpty(stdin)) {
			countStr = callOption(args, options, stdin, true);
			
		} else {
			throw new WcException("No input detected. Please add file(s) or ensure stdin is not empty");
		}
		
		try {
			stdout.write(countStr.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		for (int i = 1; i < args.length; i++) {
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
					//throw new WcException("Illegal Option. Please put \"-m\", \"-w\" or \"-l\"");
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
						startIndex = dashPosition + 1;
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

	
	private String callOption(String[] args, boolean[] options, 
								InputStream stdin, boolean isStdin) throws WcException {
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
//		boolean[] options = optionsSeparator(args);
		String commandLine = "";
		String countStr = "";
		for (int i = 0; i < args.length; i++) {
			commandLine += args[i] + " ";
		}
		commandLine.trim();
		
		if (isStdin) {
			if (!Arrays.asList(options).contains(false)) {
				countStr = printAllCountsInStdin(commandLine, stdin);
			}
			if (options[0]) { // -m
				countStr += printCharacterCountInStdin(commandLine, stdin) + " ";
			} 
			if (options[1]) { // -w
				countStr += printWordCountInStdin(commandLine, stdin) + " ";
			}
			if (options[2]) { // -l
				countStr += printNewlineCountInStdin(commandLine, stdin);
			}

		} else {
			if (!Arrays.asList(options).contains(false)) {
				countStr = printAllCountsInStdin(commandLine, stdin);
			}
			if (options[0]) { // -m
				countStr += printCharacterCountInFile(commandLine) + " ";
			} 
			if (options[1]) { // -w
				countStr += printWordCountInFile(commandLine) + " ";
			}
			if (options[2]) { // -l
				countStr += printNewlineCountInFile(commandLine);
			}
		}
		return countStr.trim();
	}

	private boolean isInputStreamEmpty(InputStream stdin) {
		try {
			if (stdin.read() > -1) {
				return false;
			} else {
				return true;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	// TODO handle "this bad file.txt"
	private boolean isFileDirectoryValid(String[] args) throws WcException {
		// Check if filename or directory is valid
		// check if args length >= 1
		// check if the args[args.length-1] contains " or ' => filename with space
		// loop backwards to check for " or ' => start of filename
		// wc -l "this bad filename".txt
		// check if args[args.length-1] is a valid file
		
		if (args.length == 0) {
			return false;
		} 
		String filename = args[args.length-1];
		File tmpDir = new File(filename);
	    boolean isDir = tmpDir.exists() && tmpDir.isDirectory();

		File file = new File(filename);
	    boolean isFile = file.exists() && file.isFile();
	    
	    if (isDir || isFile) {
	    	return true;
	    } else {
//	    	throw new WcException("No such file or directory");
	    	return false;
	    }
	}

	private String getFilename(String args) {
		// TODO Auto-generated method stub
		// assume filename is the last token
		String[] parts = args.split(" ");
		return parts[parts.length - 1];
	}
	
	private void processCountInFile(String input) {
		String filename = getFilename(input);
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Returns string containing the character count in file
	 * @param args String containing command and arguments
	 */
	@Override
	public String printCharacterCountInFile(String args) {
		processCountInFile(args);
		return String.valueOf(this.charCount);
	}

	/**
	 * Returns string containing the word count in file
	 * @param args String containing command and arguments
	 */
	@Override
	public String printWordCountInFile(String args) {
		processCountInFile(args);
		return String.valueOf(this.wordCount);
	}

	/**
	 * Returns string containing the newline count in file
	 * @param args String containing command and arguments
	 */
	@Override
	public String printNewlineCountInFile(String args) {
		processCountInFile(args);
		return String.valueOf(this.lineCount);
	}

	/**
	 * Returns string containing all counts in file
	 * @param args String containing command and arguments
	 */
	@Override
	public String printAllCountsInFile(String args) {
		processCountInFile(args);
		return this.charCount + " " + 
				this.wordCount + " " +
				this.lineCount;
	}
	
//	private String removeOptions(String args) {
//		int lineStartPos = -1;
//		String[] parts = args.split(" ");
//		for (int i = 0; i < parts.length; i++) {
//			if (parts[i].indexOf('-') == 0) {
//				continue;
//			} else {
//				lineStartPos = i;
//				break;
//			}
//		}
//		
//		String line = "";
//		for (int i = lineStartPos; i < parts.length; i++) {
//			line += parts[i] + " ";
//		}
//		return line.trim();
//	}
	
	private void processCountInStdin(InputStream stdin) {
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Returns string containing the character count in Stdin
	 * @param args String containing command and arguments
	 * @param stdin InputStream containing Stdin
	 */
	@Override
	public String printCharacterCountInStdin(String args, InputStream stdin) {
		processCountInStdin(stdin);
		return String.valueOf(this.charCount);
	}

	/**
	 * Returns string containing the word count in Stdin
	 * @param args String containing command and arguments
	 * @param stdin InputStream containing Stdin
	 */
	@Override
	public String printWordCountInStdin(String args, InputStream stdin) {
		processCountInStdin(stdin);
		return String.valueOf(this.wordCount);
	}

	/**
	 * Returns string containing the newline count in Stdin
	 * @param args String containing command and arguments
	 * @param stdin InputStream containing Stdin
	 */
	@Override
	public String printNewlineCountInStdin(String args, InputStream stdin) {
		processCountInStdin(stdin);
		return String.valueOf(this.lineCount);
	}

	/**
	 * Returns string containing all counts in Stdin
	 * @param args String containing command and arguments
	 * @param stdin InputStream containing Stdin
	 */
	@Override
	public String printAllCountsInStdin(String args, InputStream stdin) { 
		processCountInStdin(stdin);
		return this.charCount + " " + 
				this.wordCount + " " + 
				this.lineCount;
	}

}
