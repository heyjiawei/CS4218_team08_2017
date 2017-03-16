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

public class GrepApplication implements Grep {

	private final String NEW_LINE = System.getProperty("line.separator");

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
	
	private boolean containsPattern(String... args) {
		return args.length != 0 && args[0] != null && !args[0].isEmpty();
	}

	@Override
	public String grepFromStdin(String args, InputStream stdin) {
		return parseAndEvaluate(args, stdin);
	}

	@SuppressWarnings("PMD.PreserveStackTrace")
	private String grepStdin(String args[], InputStream stdin) throws GrepException {
		if (isInputStreamEmpty(stdin)) {
			throw new GrepException("Inputstream empty");
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
		return parseAndEvaluate(args, null);
	}
	
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

	@Override
	public String grepFromMultipleFiles(String args) {
		return parseAndEvaluate(args, null);
	}

	@Override
	public String grepInvalidPatternInStdin(String args, InputStream stdin) {
		return parseAndEvaluate(args, stdin);
	}

	@Override
	public String grepInvalidPatternInFile(String args) {
		return parseAndEvaluate(args, null);
	}
	
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
