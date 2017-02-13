package sg.edu.nus.comp.cs4218.impl.app;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import sg.edu.nus.comp.cs4218.app.Date;
import sg.edu.nus.comp.cs4218.exception.DateException;

public class DateApplication implements Date {
	private java.util.Date currentDate;

	/**
	 * Runs application with specified input data and specified output stream.
	 */
	@Override
	public void run(String[] args, InputStream stdin, OutputStream stdout) throws DateException {
		// TODO Auto-generated method stub
		// 1 check if command is correct in run function
		// 2 if command is right
		// if args is null or length 0 and stdin is null throw error - no arguments provided
		// else check if args get datestring from printCurrentDate 
		
		// 3 get datestring from printCurrentDate
		// 3a write to stdout
		// 3b otherwise, throw error by writing to stderr
		if (stdout == null) {
			throw new DateException("No output stream provided");
		}
		if (stdin == null) {
			// is the checking of stdin == null necessary?
			// Will there be a case InputStream will not be passed?
			throw new DateException("No input stream provided");
		}
		if (args.length == 1 && args[0].equalsIgnoreCase("date")) {
			String currentDate = printCurrentDate(args[0]);
			PrintWriter writer = new PrintWriter(stdout);
			writer.println(currentDate);
			
		} else {
			throw new DateException("illegal date format");
		}
	}
	
	/**
	 * Print the current date and time
	 * @param args String containing command
	 */
	@Override
	public String printCurrentDate(String args) {
		// TODO Auto-generated method stub
		// test if locale.english gives the right ans
		DateFormat dateformat = new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy", 
													Locale.ENGLISH);

		currentDate = new java.util.Date();
	    String strDate = dateformat.format(currentDate);
		return strDate;
	}

}
