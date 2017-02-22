package sg.edu.nus.comp.cs4218.impl.app;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import sg.edu.nus.comp.cs4218.app.Date;
import sg.edu.nus.comp.cs4218.exception.DateException;

/**
 * The date command prints out the date string in the format
 * [week day] [month] [day] [hh:mm:ss] [time zone] [year]
 * 
 * <p>
 * <b>Command format:</b> <code>date</code>
 * </p>
 */

public class DateApplication implements Date {

	/**
	 * Runs the date application with the specified arguments.
	 * 
	 * @param args
	 *            Array of arguments for the application. Each array element is
	 *            the path to a file. If no files are specified stdin is used.
	 * @param stdin
	 *            An InputStream. The input for the command is read from this
	 *            InputStream if no files are specified.
	 * @param stdout
	 *            An OutputStream. The output of the command is written to this
	 *            OutputStream.
	 * 
	 * @throws DateException
	 *             If there are args provided
	 */
	@Override
	public void run(String[] args, InputStream stdin, OutputStream stdout) throws DateException {
		if (stdout == null) {
			throw new DateException("No output stream provided");
		}
		if (stdin == null) {
			throw new DateException("No input stream provided");
		}
		if (args.length > 0) {
			throw new DateException("illegal date format");
		}
		String currentDate = printCurrentDate("date");
		try {
			stdout.write(currentDate.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Print the current date and time
	 * @param args String containing command
	 */
	@Override
	public String printCurrentDate(String args) {
		DateFormat dateformat = new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy", 
													Locale.ENGLISH);

		java.util.Date currentDate = new java.util.Date();
		return dateformat.format(currentDate);
	}

}
