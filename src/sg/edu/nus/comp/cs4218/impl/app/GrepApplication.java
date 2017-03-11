package sg.edu.nus.comp.cs4218.impl.app;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import sg.edu.nus.comp.cs4218.app.Grep;
import sg.edu.nus.comp.cs4218.exception.GrepException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.Parser;

public class GrepApplication implements Grep {
	private boolean isError = false;
	
	@Override
	public void run(String[] args, InputStream stdin, OutputStream stdout) throws GrepException {
		if (stdout == null) {
			throw new GrepException("No output stream provided");
		}
		
		if (stdin == null) {
			throw new GrepException("No input stream provided");
		}
		
		String output = "No Input provided (via file or stdin) or no such file or directory";
		String commandLine = joinCommandArgs(args);
		if (args.length == 2) {
			output = grepFromOneFile(commandLine);
		
		} else if (args.length > 2) {
			output = grepFromMultipleFiles(commandLine);
		
		} else if (!isInputStreamEmpty(stdin)) {
			output = grepFromStdin(commandLine, stdin);
		}
		
		if (this.isError) {
			throw new GrepException(output);
		
		} else {
			try {
				stdout.write(output.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private String joinCommandArgs(String[] args) {
		String commandLine = "";
		for (int i = 0; i < args.length; i++) {
			commandLine += args[i] + " ";
		}
		commandLine = commandLine.trim();
		return commandLine;
	}

	@Override
	public String grepFromStdin(String args, InputStream stdin) {
		if (isInputStreamEmpty(stdin)) {
			this.isError = true;
			return "No input stream provided";
		}
		Pattern regex = getRegexPattern(args);
		StringBuilder output = new StringBuilder();
		
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(stdin));
			String line;
			while((line = in.readLine()) != null) {
				Matcher matcher = regex.matcher(line);
			    if (matcher.find()) {
			    	output.append(line + "\n");
			    }
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (output.length() > 0) {
			return output.toString();
		} else {
			return "Pattern Not Found In Stdin!\n";
		}
	}

	private Pattern getRegexPattern(String args) throws PatternSyntaxException {
		return Pattern.compile(args);
	}

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
	
	private boolean isFileValid(String args) {
		File file = new File(args);
	    return file.exists() && file.isFile();
	}

	@Override
	public String grepFromOneFile(String args) {
		StringBuilder output = new StringBuilder();
		Parser parser = new Parser();
		try {
			Vector<String> argsList = parser.parseCallCommand(args);
			if (argsList.size() != 2) {
				this.isError = true;
				return "Incorrect Arguments or Invalid File\n";
			}
			
			if (!isFileValid(argsList.lastElement())) {
				this.isError = true;
				return "No such file or directory\n";
			}
			Pattern regex = getRegexPattern(argsList.firstElement());
			BufferedReader reader = new BufferedReader(new FileReader(argsList.lastElement()));
			String line;
			while ((line = reader.readLine()) != null) {
				Matcher matcher = regex.matcher(line);
			    if (matcher.find()) {
			    	output.append(line + "\n");
			    }
			}
			reader.close();
		} catch (ShellException | IOException e) {
			e.printStackTrace();
		}
		if (output.length() > 0) {
			return output.toString();
		} else {
			return "Pattern Not Found In File!\n";
		}
	}

	@Override
	public String grepFromMultipleFiles(String args) {
		StringBuilder output = new StringBuilder();
		Parser parser = new Parser();
		try {
			Vector<String> argsList = parser.parseCallCommand(args);
			if (argsList.size() <= 1) {
				this.isError = true;
				return "Incorrect Arguments\n";
			}
			Pattern regex = getRegexPattern(argsList.firstElement());
			BufferedReader reader;
			String line;
			for (int i = 1; i < argsList.size(); i++) {
				if (isFileValid(argsList.get(i))) {
					reader = new BufferedReader(new FileReader(argsList.get(i)));
					 while ((line = reader.readLine()) != null) {
						Matcher matcher = regex.matcher(line);
					    if (matcher.find()) {
					    	output.append(argsList.get(i) + ":");
					    	output.append(line + "\n");
					    }
					}
					reader.close();
				}
			}
		} catch (ShellException | IOException e) {
			e.printStackTrace();
		}
		if (output.length() > 0) {
			return output.toString();
		} else {
			return "Pattern Not Found In File!\n";
		}
	}

	@Override
	public String grepInvalidPatternInStdin(String args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String grepInvalidPatternInFile(String args) {
		// TODO Auto-generated method stub
		return null;
	}

}
