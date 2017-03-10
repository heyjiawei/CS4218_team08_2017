package sg.edu.nus.comp.cs4218.impl.app;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
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
	private boolean isError = false;
	
	@Override
	public void run(String[] args, InputStream stdin, OutputStream stdout) throws GrepException {
		if (stdout == null) {
			throw new GrepException("No output stream provided");
		}
		if (stdin == null) {
			throw new GrepException("No input stream provided");
		}
		String output = "No input provided";
		if (!isInputStreamEmpty(stdin)) {
			// Assume commandline args are valid for now
			output = grepFromStdin(args[0], stdin);
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

	@Override
	public String grepFromOneFile(String args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String grepFromMultipleFiles(String args) {
		// TODO Auto-generated method stub
		return null;
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
