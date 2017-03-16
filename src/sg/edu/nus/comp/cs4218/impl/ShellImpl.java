package sg.edu.nus.comp.cs4218.impl;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.Shell;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.app.*;
import sg.edu.nus.comp.cs4218.impl.cmd.CallCommand;

/**
 * A Shell is a command interpreter and forms the backbone of the entire
 * program. Its responsibility is to interpret commands that the user type and
 * to run programs that the user specify in her command lines.
 *
 * <p>
 * <b>Command format:</b>
 * <code>&lt;Pipe&gt; | &lt;Sequence&gt; | &lt;Call&gt;</code>
 * </p>
 */

public class ShellImpl implements Shell {

	public static final String EXP_INVALID_APP = "Invalid app.";
	public static final String EXP_SYNTAX = "Invalid syntax encountered near ";
	public static final String EXP_REDIR_PIPE = "File output redirection and "
			+ "pipe operator cannot be used side by side.";
	public static final String EXP_MULTIPLE_FILE_REDIR =
			"Cannot redirect with multiple files";
	public static final String EXP_SAME_REDIR = "Input redirection file same "
			+ "as output redirection file.";
	public static final String EXP_STDOUT = "Error writing to stdout.";
	public static final String EXP_NOT_SUPPORTED = " not supported yet";

	private void evaluateSubsequence(String[] callCommands, OutputStream stdout)
			throws AbstractApplicationException, ShellException {
		if (callCommands.length == 1) {
			CallCommand call = new CallCommand(callCommands[0]);
			call.parse();
			call.evaluate(System.in, stdout);
		} else {
			evaluatePipe(callCommands, stdout);
		}
	}

	private void evaluatePipe(String[] callCommands, OutputStream stdout)
			throws AbstractApplicationException, ShellException {
		CallCommand call;
		InputStream pipeIn = System.in;
		ByteArrayOutputStream pipeOut;

		for (int index = 0; index < callCommands.length - 1; index++) {
			pipeOut = new ByteArrayOutputStream();

			call = new CallCommand(callCommands[index]);
			call.parse();
			call.evaluate(pipeIn, pipeOut);

			pipeIn = new ByteArrayInputStream(pipeOut.toByteArray());
		}

		call = new CallCommand(callCommands[callCommands.length - 1]);
		call.parse();
		call.evaluate(pipeIn, stdout);
	}

	public void parseAndEvaluate(String cmdline, OutputStream stdout)
			throws AbstractApplicationException, ShellException {
		String[][] sequences = new Parser().parse(cmdline);
		for (String[] subsequence : sequences) {
			evaluateSubsequence(subsequence, stdout);
		}
	}

	/**
	 * For the purpose of all the interface methods. Oh god why.
	 *
	 * @param args
	 *            String contain commands/arguments/syntax errors/everything
	 * @return
	 *            Regurgitate everything, includes error messages
	 */
	private String parseAndEvaluateToString(String args) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		try {
			Shell shell = new ShellImpl();
			shell.parseAndEvaluate(args, out);
		} catch (AbstractApplicationException|ShellException e) {
			return e.getMessage();
		}

		return out.toString();
	}

	/**
	 * Evaluate pipe call with two commands
	 * @param args
	 *            String containing the command, input arguments and the pipe
	 *            operator
	 * @return
	 *            String output
	 */
	public String pipeTwoCommands(String args) {
		return parseAndEvaluateToString(args);
	}

	/**
	 * Evaluate pipe call with more than two commands
	 * @param args
	 *            String containing the commands, input arguments and the pipe
	 *            operators
	 * @return
	 *            String output
	 */
	public String pipeMultipleCommands(String args) {
		return parseAndEvaluateToString(args);
	}

	/**
	 * Evaluate pipe call with one part generating an exception
	 * @param args
	 *            String containing the commands, input arguments and the pipe
	 *            operator/s
	 * @return
	 *            String output
	 */
	public String pipeWithException(String args) {
		return parseAndEvaluateToString(args);
	}

	/**
	 * Evaluate globbing with no files or directories
	 * @param args
	 *            String containing the commands, input arguments and the
	 *            asterisk (globbing operator)
	 * @return
	 *            String output
	 */
	public String globNoPaths(String args) {
		return parseAndEvaluateToString(args);
	}

	/**
	 * Evaluate globbing with one file (one path)
	 * @param args
	 *            String containing the commands, input arguments and the
	 *            asterisk (globbing operator)
	 * @return
	 *            String output
	 */
	public String globOneFile(String args) {
		return parseAndEvaluateToString(args);
	}

	/**
	 * Evaluate globbing with multiple files and directories (multiple paths)
	 * @param args
	 *            String containing the commands, input arguments and the
	 *            asterisk (globbing operator)
	 * @return
	 *            String output
	 */
	public String globFilesDirectories(String args) {
		return parseAndEvaluateToString(args);
	}

	/**
	 * Evaluate globbing with exception
	 * @param args
	 *            String containing the commands, input arguments and the
	 *            asterisk (globbing operator)
	 * @return
	 *            String output
	 */
	public String globWithException(String args) {
		return parseAndEvaluateToString(args);
	}

	/**
	 * Evaluate opening InputStream from file for input redirection
	 * @param args
	 *            String containing the commands, input arguments and the "<"
	 *            symbol (input redirection operator)
	 * @return
	 *            String output
	 */
	public String redirectInput(String args) {
		return parseAndEvaluateToString(args);
	}

	/**
	 * Evaluate opening OutputStream to file for output redirection
	 * @param args
	 *            String containing the commands, input arguments and the ">"
	 *            symbol (output redirection operator)
	 * @return
	 *            String output
	 */
	public String redirectOutput(String args) {
		return parseAndEvaluateToString(args);
	}

	/**
	 * Evaluate input redirection with no files
	 * @param args
	 *            String containing the commands, input arguments and the "<"
	 *            symbol (input redirection operator)
	 * @return
	 *            String output
	 */
	public String redirectInputWithNoFile(String args) {
		return parseAndEvaluateToString(args);
	}

	/**
	 * Evaluate output redirection with no files
	 * @param args
	 *            String containing the commands, input arguments and the ">"
	 *            symbol (output redirection operator)
	 * @return
	 *            String output
	 */
	public String redirectOutputWithNoFile(String args) {
		return parseAndEvaluateToString(args);
	}

	/**
	 * Evaluate input redirection with exception
	 * @param args
	 *            String containing the commands, input arguments and the "<"
	 *            symbol (input redirection operator)
	 * @return
	 *            String output
	 */
	public String redirectInputWithException(String args) {
		return parseAndEvaluateToString(args);
	}

	/**
	 * Evaluate output redirection with exception
	 * @param args
	 *            String containing the commands, input arguments and the ">"
	 *            symbol (output redirection operator)
	 * @return
	 *            String output
	 */
	public String redirectOutputWithException(String args) {
		return parseAndEvaluateToString(args);
	}

	/**
	 * Evaluate command substitution
	 * @param args
	 *            String containing the commands, input arguments surrounded by
	 *            backquotes
	 * @return
	 *            String output
	 */
	public String performCommandSubstitution(String args) {
		return parseAndEvaluateToString(args);
	}

	/**
	 * Evaluate command substitution with exception
	 * @param args
	 *            String containing the commands, input arguments surrounded by
	 *            backquotes
	 * @return
	 *            String output
	 */
	public String performCommandSubstitutionWithException(String args) {
		return parseAndEvaluateToString(args);
	}

	/**
	 * Searches for and processes the commands enclosed by back quotes for
	 * command substitution.If no back quotes are found, the argsArray from the
	 * input is returned unchanged. If back quotes are found, the back quotes
	 * and its enclosed commands substituted with the output from processing the
	 * commands enclosed in the back quotes.
	 *
	 * @param argsArray
	 *            String array of the individual commands.
	 *
	 * @return String array with the back quotes command processed.
	 *
	 * @throws AbstractApplicationException
	 *             If an exception happens while processing the content in the
	 *             back quotes.
	 * @throws ShellException
	 *             If an exception happens while processing the content in the
	 *             back quotes.
	 */
	public static String[] processBQ(String... argsArray)
			throws AbstractApplicationException, ShellException {
		// echo "this is space `echo "nbsp"`"
		// echo "this is space `echo "nbsp"` and `echo "2nd space"`"
		// Back quoted: any char except \n,`
		String[] resultArr = new String[argsArray.length];
		System.arraycopy(argsArray, 0, resultArr, 0, argsArray.length);
		String patternBQ = "`([^\\n`]*)`";
		Pattern patternBQp = Pattern.compile(patternBQ);

		for (int i = 0; i < argsArray.length; i++) {
			String arg = argsArray[i];
			boolean singleQuoted = arg.startsWith("'") && arg.endsWith("'");
			boolean doubleQuoted = arg.startsWith("\"") && arg.endsWith("\"");

			if (singleQuoted || doubleQuoted) {
				resultArr[i] = arg.substring(1, arg.length() - 1);
			}

			if (singleQuoted) {
				continue;
			}

			Matcher matcherBQ = patternBQp.matcher(arg);
			String replacedStr = new String(resultArr[i]);

			while (matcherBQ.find()) { // found backquoted
				String bqStr = matcherBQ.group(1);
				OutputStream bqOutputStream = new ByteArrayOutputStream();
				ShellImpl shell = new ShellImpl();
				shell.parseAndEvaluate(bqStr, bqOutputStream);

				ByteArrayOutputStream outByte = (ByteArrayOutputStream) bqOutputStream;
				byte[] byteArray = outByte.toByteArray();
				String bqResult = new String(byteArray).replace("\n", "")
						.replace("\r", "");

				// replace substring of back quote with result
				replacedStr = replacedStr.replace("`" + bqStr + "`",
						bqResult);
			}

			resultArr[i] = replacedStr;
		}
		return resultArr;
	}

	/**
	 * Static method to run the application as specified by the application
	 * command keyword and arguments.
	 *
	 * @param app
	 *            String containing the keyword that specifies what application
	 *            to run.
	 * @param argsArray
	 *            String array containing the arguments to pass to the
	 *            applications for running.
	 * @param inputStream
	 *            InputputStream for the application to get arguments from, if
	 *            needed.
	 * @param outputStream
	 *            OutputStream for the application to print its output to.
	 *
	 * @throws AbstractApplicationException
	 *             If an exception happens while running any of the
	 *             application(s).
	 * @throws ShellException
	 *             If an unsupported or invalid application command is detected.
	 */
	public static void runApp(String app, String[] argsArray,
			InputStream inputStream, OutputStream outputStream)
			throws AbstractApplicationException, ShellException {
		Application absApp = null;
		if (("cat").equalsIgnoreCase(app)) {// cat [FILE]...
			absApp = new CatApplication();
		} else if (("cd").equalsIgnoreCase(app)) {
			absApp = new CdApplication();
		} else if (("echo").equalsIgnoreCase(app)) {// echo [args]...
			absApp = new EchoApplication();
		} else if (("date").equalsIgnoreCase(app)) {
			absApp = new DateApplication();
		} else if (("head").equalsIgnoreCase(app)) {// head [OPTIONS] [FILE]
			absApp = new HeadApplication();
		} else if (("pwd").equalsIgnoreCase(app)) {
			absApp = new PwdApplication();
		} else if (("sed").equalsIgnoreCase(app)) {
			absApp = new SedApplication();
		} else if (("tail").equalsIgnoreCase(app)) {// tail [OPTIONS] [FILE]
			absApp = new TailApplication();
		} else if (("wc").equalsIgnoreCase(app)) {
			absApp = new WcApplication();
		} else if (("cal").equalsIgnoreCase(app)) {
			absApp = new CalApplication();
		} else if (("sort").equalsIgnoreCase(app)) {
			absApp = new SortApplication();
		} else { // invalid command
			throw new ShellException(app + ": " + EXP_INVALID_APP);
		}
		absApp.run(argsArray, inputStream, outputStream);
	}

	/**
	 * Static method to creates an inputStream based on the file name or file
	 * path.
	 *
	 * @param inputStreamS
	 *            String of file name or file path
	 *
	 * @return InputStream of file opened
	 *
	 * @throws ShellException
	 *             If file is not found.
	 */
	public static InputStream openInputRedir(String inputStreamS)
			throws ShellException {
		File inputFile = new File(inputStreamS);
		FileInputStream fInputStream = null;
		try {
			fInputStream = new FileInputStream(inputFile);
		} catch (FileNotFoundException e) {
			throw new ShellException(e.getMessage());
		}
		return fInputStream;
	}

	/**
	 * Static method to creates an outputStream based on the file name or file
	 * path.
	 *
	 * @param outputStreamS
	 *            String of file name or file path.
	 *
	 * @return OutputStream of file opened.
	 *
	 * @throws ShellException
	 *             If file destination cannot be opened or inaccessible.
	 */
	public static OutputStream openOutputRedir(String outputStreamS)
			throws ShellException {
		File outputFile = new File(outputStreamS);
		FileOutputStream fOutputStream = null;
		try {
			fOutputStream = new FileOutputStream(outputFile);
		} catch (FileNotFoundException e) {
			throw new ShellException(e.getMessage());
		}
		return fOutputStream;
	}

	/**
	 * Static method to close an inputStream.
	 *
	 * @param inputStream
	 *            InputStream to be closed.
	 *
	 * @throws ShellException
	 *             If inputStream cannot be closed successfully.
	 */
	public static void closeInputStream(InputStream inputStream)
			throws ShellException {
		if (inputStream != System.in) {
			try {
				inputStream.close();
			} catch (IOException e) {
				throw new ShellException(e.getMessage());
			}
		}
	}

	/**
	 * Static method to close an outputStream. If outputStream provided is
	 * System.out, it will be ignored.
	 *
	 * @param outputStream
	 *            OutputStream to be closed.
	 *
	 * @throws ShellException
	 *             If outputStream cannot be closed successfully.
	 */
	public static void closeOutputStream(OutputStream outputStream)
			throws ShellException {
		if (outputStream != System.out) {
			try {
				outputStream.close();
			} catch (IOException e) {
				throw new ShellException(e.getMessage());
			}
		}
	}

	/**
	 * Static method to write output of an outputStream to another outputStream,
	 * usually System.out.
	 *
	 * @param outputStream
	 *            Source outputStream to get stream from.
	 * @param stdout
	 *            Destination outputStream to write stream to.
	 * @throws ShellException
	 *             If exception is thrown during writing.
	 */
	public static void writeToStdout(OutputStream outputStream,
			OutputStream stdout) throws ShellException {
		if (outputStream instanceof FileOutputStream) {
			return;
		}
		try {
			stdout.write(((ByteArrayOutputStream) outputStream).toByteArray());
		} catch (IOException e) {
			throw new ShellException(EXP_STDOUT);
		}
	}

	/**
	 * Static method to pipe data from an outputStream to an inputStream, for
	 * the evaluation of the Pipe Commands.
	 *
	 * @param outputStream
	 *            Source outputStream to get stream from.
	 *
	 * @return InputStream with data piped from the outputStream.
	 *
	 * @throws ShellException
	 *             If exception is thrown during piping.
	 */
	public static InputStream outputStreamToInputStream(
			OutputStream outputStream) throws ShellException {
		return new ByteArrayInputStream(
				((ByteArrayOutputStream) outputStream).toByteArray());
	}

	/**
	 * Main method for the Shell Interpreter program.
	 *
	 * @param args
	 *            List of strings arguments, unused.
	 */

	public static void main(String... args) {
		ShellImpl shell = new ShellImpl();

		BufferedReader bReader = new BufferedReader(new InputStreamReader(
				System.in));
		String readLine = null;
		String currentDir;

		while (true) {
			try {
				currentDir = Environment.currentDirectory;
				System.out.print(currentDir + ">");
				readLine = bReader.readLine();
				if (readLine == null) {
					break;
				}
				if (("").equals(readLine.trim())) {
					continue;
				}
				shell.parseAndEvaluate(readLine, System.out);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}
}
