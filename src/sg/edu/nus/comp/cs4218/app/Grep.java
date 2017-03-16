package sg.edu.nus.comp.cs4218.app;

import java.io.InputStream;

import sg.edu.nus.comp.cs4218.Application;

public interface Grep extends Application { // NOPMD - Provided interface

	/**
	 * Returns string containing lines which match the specified pattern in
	 * Stdin
	 * @param args String containing command and arguments
	 * @param stdin InputStream containing stdin
	 */
	public String grepFromStdin(String args, InputStream stdin);

	/**
	 * Returns string containing lines which match the specified pattern in the
	 * given file
	 * @param args  String containing command and arguments
	 */
	public String grepFromOneFile(String args);

	/**
	 * Returns string containing lines which match the specified pattern in the
	 * given files
	 * @param args  String containing command and arguments
	 */
	public String grepFromMultipleFiles(String args);

	/**
	 * Returns string when invalid pattern is specified in grep from Stdin
	 * @param args String containing command and arguments
	 */
	public String grepInvalidPatternInStdin(String args, InputStream stdin);

	
	/**
	 * Returns string when invalid pattern is specified in grep from file
	 * @param args String containing command and arguments
	 */
	public String grepInvalidPatternInFile(String args);
}
