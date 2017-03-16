package sg.edu.nus.comp.cs4218.impl.cmd;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.Environment;
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

	String app;
	String cmdline, inputStreamS, outputStreamS;
	String[] argsArray;

	public CallCommand(String cmdline) {
		this.cmdline = cmdline.trim();
		app = inputStreamS = outputStreamS = "";
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
		// Get current directory for globbing
		String currentDir = Environment.currentDirectory;

		Vector<String> cmdVector = new Parser().parseCallCommand(this.cmdline);

		if (cmdVector.isEmpty()) {
			// Should not happen if cmdline was correctly parsed by Parser.
			throw new ShellException(ShellImpl.EXP_INVALID_APP);
		}

		Vector<String> argsVector = new Vector<>();
		Iterator<String> cmdIterator = cmdVector.iterator();

		boolean isAppSet = false;
		String arg;

		while (cmdIterator.hasNext()) {
			arg = cmdIterator.next();

			if ("<".equals(arg)) {
				this.inputStreamS = cmdIterator.next();
			} else if(">".equals(arg)) {
				this.outputStreamS = cmdIterator.next();
			} else if (arg.contains("*")) {
				processGlob(currentDir, argsVector, arg);
			} else if (isAppSet) {
				argsVector.add(arg);
			} else {
				this.app = arg;
				isAppSet = true;
			}
		}

		if (!this.inputStreamS.isEmpty() &&
				this.inputStreamS.equals(this.outputStreamS)) {
			throw new ShellException(ShellImpl.EXP_SAME_REDIR);
		}

		this.argsArray = argsVector.toArray(new String[argsVector.size()]);
	}

	private void processGlob(String currentDir, Vector<String> argsVector, String arg) {
		// perform globbing on the argument
		PathMatcher matcher;
		matcher = FileSystems.getDefault().getPathMatcher("glob:" + currentDir + File.separator + arg);
		Path folder = new File(currentDir).toPath();

		List<File> arrayListFiles = new ArrayList<File>();
		arrayListFiles = getFileNames(arrayListFiles, folder);
		File[] listOfFiles = new File[arrayListFiles.size()];
		listOfFiles = arrayListFiles.toArray(listOfFiles);

		// Sort files by name
		Arrays.sort(listOfFiles, new Comparator<File>() {
			@Override
			public int compare(File file1, File file2) {
				return ((File) file1).getPath().compareTo(((File) file2).getPath());
			}
		});

		for (int i = 0; i < listOfFiles.length; i++) {
			// ignore hidden files that start with "."
			if (listOfFiles[i].isFile() && !listOfFiles[i].getName().startsWith(".")) {
				// match the globbing pattern

				if(matcher.matches(listOfFiles[i].toPath())) {
					// insert args separated by space
					// first arg does not have space in front
					String relative = new File(currentDir).toURI().relativize(new File(listOfFiles[i].getAbsolutePath()).toURI()).getPath();
					argsVector.add(relative);
				} else {
				}
			} else if (listOfFiles[i].isDirectory() && matcher.matches(listOfFiles[i].toPath())) {
				// insert args separated by space
				// first arg does not have space in front
				String relative = new File(currentDir).toURI().relativize(new File(listOfFiles[i].getAbsolutePath()).toURI()).getPath();
				if(relative.endsWith(File.separator)) {
					relative = relative.substring(0, relative.length() - 1);
				}
				argsVector.add(relative);
			}
		}
	}

	private List<File> getFileNames(List<File> files, Path dir) {
		try(DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
			for (Path path : stream) {
				if(path.toFile().isDirectory()) {
					files.add(path.toAbsolutePath().toFile());
					getFileNames(files, path);
				} else {
					files.add(path.toAbsolutePath().toFile());
				}
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
		return files;
	} 

	/**
	 * Terminates current execution of the command (unused for now)
	 */
	@Override
	public void terminate() {
		// TODO Auto-generated method stub

	}

}
