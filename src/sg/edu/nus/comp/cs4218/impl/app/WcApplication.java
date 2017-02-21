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
	private String filename = null;
	private boolean isCountProcessed = false;

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
			
		} else if (!isInputStreamEmpty(stdin)) {
			countStr = callOption(args, options, stdin, true);
			
		} else {
			throw new WcException("No input detected. Please ensure filename is correct or stdin is not empty");
		}
		
		try {
			stdout.write(countStr.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Separate joined options e.g. "-lwm" to individual options e.g. "-l -w -m"
	 * Individual options are represented by a boolean array, 
	 * in the respective position -m -w -l
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
					//throw new WcException("Illegal Option. Please put \"-m\", \"-w\" or \"-l\"");
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
					throw new WcException("Invalid Option. -" + nextChar + " does not exist");
				}	
			}
		}
		
		return options;
	}

	
	private String callOption(String[] args, boolean[] options, 
								InputStream stdin, boolean isStdin) throws WcException {
		String commandLine = "";
		String countStr = "";
		for (int i = 0; i < args.length; i++) {
			commandLine += args[i] + " ";
		}
		commandLine.trim();
		
		if (isStdin) {
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
	
	private boolean isFileDirectoryValid(String[] args) {		
		if (args.length == 0) {
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

	private String getFilename(String args) {
		if (this.filename == null) {
			String[] parts = args.split(" ");
			return parts[parts.length - 1];
		} else {
			return this.filename;
		}
	}
	
	private void processCountInFile(String input) {
		if (this.isCountProcessed) {
			return;
		}
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
			this.isCountProcessed = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void processCountInStdin(InputStream stdin) {
		if (this.isCountProcessed) {
			return;
		}
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
			this.isCountProcessed = true;
		} catch (IOException e) {
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
