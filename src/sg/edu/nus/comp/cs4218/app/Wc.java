package sg.edu.nus.comp.cs4218.app;

import java.io.InputStream;

import sg.edu.nus.comp.cs4218.Application;

public interface Wc extends Application { // NOPMD - Provided interface

	/**
	 * Returns string containing the character count in file
	 * @param args String containing command and arguments
	 */
	public String printCharacterCountInFile(String args);

	/**
	 * Returns string containing the word count in file
	 * @param args String containing command and arguments
	 */
	public String printWordCountInFile(String args);

	/**
	 * Returns string containing the newline count in file
	 * @param args String containing command and arguments
	 */
	public String printNewlineCountInFile(String args);

	/**
	 * Returns string containing all counts in file
	 * @param args String containing command and arguments
	 */
	public String printAllCountsInFile(String args);

	/**
	 * Returns string containing the character count in Stdin
	 * @param args String containing command and arguments
	 * @param stdin InputStream containing Stdin
	 */
	public String printCharacterCountInStdin(String args, InputStream stdin);

	/**
	 * Returns string containing the word count in Stdin
	 * @param args String containing command and arguments
	 * @param stdin InputStream containing Stdin
	 */
	public String printWordCountInStdin(String args, InputStream stdin);

	/**
	 * Returns string containing the newline count in Stdin
	 * @param args String containing command and arguments
	 * @param stdin InputStream containing Stdin
	 */
	public String printNewlineCountInStdin(String args, InputStream stdin);

	/**
	 * Returns string containing all counts in args
	 * @param args String containing command and arguments
	 * @param stdin InputStream containing Stdin
	 */
	public String printAllCountsInStdin(String args, InputStream stdin);

}
