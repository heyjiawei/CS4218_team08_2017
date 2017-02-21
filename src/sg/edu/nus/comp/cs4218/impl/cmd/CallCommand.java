package sg.edu.nus.comp.cs4218.impl.cmd;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.Parser;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;

/**
 * A Call Command is a sub-command consisting of at least one non-keyword and
 * quoted (if any).
 * 
 * <p>
 * <b>Command format:</b> <code>(&lt;non-Keyword&gt; | &lt;quoted&gt;)*</code>
 * </p>
 */

public class CallCommand implements Command {
	public static final String EXP_INVALID_APP = "Invalid app.";
	public static final String EXP_SYNTAX = "Invalid syntax encountered.";
	public static final String EXP_REDIR_PIPE = "File output redirection and pipe "
			+ "operator cannot be used side by side.";
	public static final String EXP_SAME_REDIR = "Input redirection file same as "
			+ "output redirection file.";
	public static final String EXP_STDOUT = "Error writing to stdout.";
	public static final String EXP_NOT_SUPPORTED = " not supported yet";

	String app;
	String cmdline, inputStreamS, outputStreamS;
	String[] argsArray;
	Boolean error;
	String errorMsg;

	public CallCommand(String cmdline) {
		this.cmdline = cmdline.trim();
		app = inputStreamS = outputStreamS = "";
		error = false;
		errorMsg = "";
		argsArray = new String[0];
	}

	public CallCommand() {
		this("");
	}

	/**
	 * Evaluates sub-command using data provided through stdin stream. Writes
	 * result to stdout stream.
	 * 
	 * @param stdin
	 *            InputStream to get data from.
	 * @param stdout
	 *            OutputStream to write resultant data to.
	 * 
	 * @throws AbstractApplicationException
	 *             If an exception happens while evaluating the sub-command.
	 * @throws ShellException
	 *             If an exception happens while evaluating the sub-command.
	 */
	@Override
	public void evaluate(InputStream stdin, OutputStream stdout)
			throws AbstractApplicationException, ShellException {
		if (error) {
			throw new ShellException(errorMsg);
		}

		InputStream inputStream;
		OutputStream outputStream;

		argsArray = ShellImpl.processBQ(argsArray);

		if (("").equals(inputStreamS)) {// empty
			inputStream = stdin;
		} else { // not empty
			inputStream = ShellImpl.openInputRedir(inputStreamS);
		}
		if (("").equals(outputStreamS)) { // empty
			outputStream = stdout;
		} else {
			outputStream = ShellImpl.openOutputRedir(outputStreamS);
		}
		ShellImpl.runApp(app, argsArray, inputStream, outputStream);
		ShellImpl.closeInputStream(inputStream);
		ShellImpl.closeOutputStream(outputStream);
	}

	/**
	 * Parses and splits the sub-command to the call command into its different
	 * components, namely the application name, the arguments (if any), the
	 * input redirection file path (if any) and output redirection file path (if
	 * any).
	 * 
	 * @throws ShellException
	 *             If an exception happens while parsing the sub-command, or if
	 *             the input redirection file path is same as that of the output
	 *             redirection file path.
	 */
	public void parse() throws ShellException {
		Vector<String> cmdVector = new Parser().parseCallCommand(this.cmdline);

		if (cmdVector.isEmpty()) {
			// Should not happen if cmdline was correctly parsed by Parser.
			throw new ShellException(ShellImpl.EXP_INVALID_APP);
		}

		this.app = cmdVector.get(0);

		Vector<String> argsVector = new Vector<>();
		Iterator<String> cmdIterator = cmdVector.iterator();

		this.app = cmdIterator.next();

		String arg;

		while (cmdIterator.hasNext()) {
			arg = cmdIterator.next();

			if (arg.equals("<")) {
				this.inputStreamS = cmdIterator.next();
			} else if(arg.equals(">")) {
				this.outputStreamS = cmdIterator.next();
			} else {
				argsVector.add(arg);
			}
		}

		this.argsArray = argsVector.toArray(new String[argsVector.size()]);
	}

	/**
	 * Terminates current execution of the command (unused for now)
	 */
	@Override
	public void terminate() {
		// TODO Auto-generated method stub

	}

}
