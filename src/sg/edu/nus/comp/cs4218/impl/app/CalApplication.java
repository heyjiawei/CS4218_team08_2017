package sg.edu.nus.comp.cs4218.impl.app;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import sg.edu.nus.comp.cs4218.app.Cal;
import sg.edu.nus.comp.cs4218.exception.CalException;

@SuppressWarnings({ "PMD.GodClass", "deprecation" })
public class CalApplication implements Cal {

	@SuppressWarnings("PMD.FinalFieldCouldBeStatic")
	private final String SPACE_BTW_DATES = " ";
	@SuppressWarnings("PMD.FinalFieldCouldBeStatic")
	private final String SPACE_BTW_MONTHS = "  ";
	// Each row is a week, 7 days has width of 2 space each.
	private final int WIDTH_OF_MONTH = 14 + 6 * SPACE_BTW_DATES.length() +
			SPACE_BTW_MONTHS.length();

	private final String NEWLINE = System.getProperty("line.separator");

	private final String MONTH_REGEX = "^\\s*((jan(uary)?)|(feb(ruary)?)|" +
			"(mar(ch)?)|(apr(il)?)|may|june?|july?|(aug(ust)?)|sep(t|tember)?|" +
			"(oct(ober)?)|(nov(ember)?)|(dec(ember)?)|1[012]|0?[1-9])\\s*$";
	@SuppressWarnings("PMD.FinalFieldCouldBeStatic")
	private final String YEAR_REGEX = "^\\s*\\d\\d\\d\\d\\s*$";

	private final String[] MONTH_NAMES = {
			"January", "February", "March", "April", "May", "June",
			"July", "August", "September", "October", "November", "December"
	};
	private final String[] DAY_HEADERS = { "Su", "Mo", "Tu", "We", "Th", "Fr", "Sa" };

	private boolean startWithMonday = false;
	private Integer month = null;
	private Integer year = null;

	private Date mockDate = null;

	public CalApplication() {
		// Default constructor
	}

	public CalApplication(Date mockDate) {
		this.mockDate = mockDate;
	}

	private void reset() {
		this.startWithMonday = false;
		this.month = null;
		this.year = null;
	}

	private Integer parseYear(String year) {
		return Integer.parseInt(year);
	}

	@SuppressWarnings("PMD.PreserveStackTrace")
	private Integer parseMonth(String month) throws CalException {
		try {
			return Integer.parseInt(month);
		} catch (java.lang.NumberFormatException e) {
			String abbr = month.substring(0, 3).trim();

			switch (abbr) {
				case "jan":
					return 1;
				case "feb":
					return 2;
				case "mar":
					return 3;
				case "apr":
					return 4;
				case "may":
					return 5;
				case "jun":
					return 6;
				case "jul":
					return 7;
				case "aug":
					return 8;
				case "sep":
					return 9;
				case "oct":
					return 10;
				case "nov":
					return 11;
				case "dec":
					return 12;
				default:
					throw new CalException("Unknown month " + month);
			}
		}
	}

	private void parseArgs(String... args) throws CalException {
		boolean hasYear = false, hasMonth = false, hasMondayFlag = false;
		for (String arg: args) {
			String trimmed = arg.trim().toLowerCase();
			if (!hasMondayFlag && "-m".equals(trimmed)) {
				this.startWithMonday = true;
				hasMondayFlag = true;
			} else if (!hasYear && trimmed.matches(YEAR_REGEX)) {
				this.year = parseYear(trimmed);
				hasYear = true;
			} else if (!hasMonth && trimmed.matches(MONTH_REGEX)) {
				this.month = parseMonth(trimmed);
				hasMonth = true;
			} else {
				throw new CalException("Unknown arg " + arg);
			}
		}

		if (this.year == null) {
			if (this.month == null) {
				Date currentDate = new Date();
				if (this.mockDate != null) {
					currentDate = this.mockDate;
				}
				this.year = currentDate.getYear() + 1900;
				this.month = currentDate.getMonth() + 1;
			} else {
				throw new CalException("Invalid year");
			}
		}
	}

	private void addDayHeader(StringBuilder stringBuilder) {
		int start = this.startWithMonday ? 1 : 0;
		char space = 32;
		for (int i = start; i < this.DAY_HEADERS.length; i++) {
			stringBuilder.append(this.DAY_HEADERS[i]);
			stringBuilder.append(space);
		}
		for (int i = 0; i < start; i++) {
			stringBuilder.append(this.DAY_HEADERS[i]);
			stringBuilder.append(space);
		}
	}

	private void zeroPadStart(StringBuilder stringBuilder, int day) {
		String space = "  " + SPACE_BTW_DATES;
		for (int i = 0; i < day; i++) {
			stringBuilder.append(space);
		}
	}

	private void padMonthHeader(StringBuilder stringBuilder, int month,
								boolean yearInHeader) {
		String monthName = this.MONTH_NAMES[month];
		if (yearInHeader) {
			monthName += " " + this.year;
		}

		// Pad the name of month nicely so that it is centered
		int leftPad = (this.WIDTH_OF_MONTH - this.SPACE_BTW_MONTHS.length() -
				monthName.length()) / 2;
		stringBuilder.append(
				String.format("%1$" + (monthName.length() + leftPad) + "s",
						monthName));
	}

	private String getMonthOutput(int year, int month, boolean yearInHeader) {
		StringBuilder stringBuilder = new StringBuilder();

		Date date = new Date( year - 1900, month, 1);
		this.padMonthHeader(stringBuilder, date.getMonth(), yearInHeader);
		stringBuilder.append(this.NEWLINE);

		// Add the row of days
		this.addDayHeader(stringBuilder);
		stringBuilder.append(this.NEWLINE);

		// Adjust the day so that it can be used as an index
		int currentIndex = date.getDay();
		if (this.startWithMonday) {
			currentIndex = (date.getDay() + 6) % 7;
		}
		this.zeroPadStart(stringBuilder, currentIndex);

		// Add each day one by one, until the next month is reached
		int dateOfMonth;
		while (date.getMonth() == month) {
			dateOfMonth = date.getDate();
			stringBuilder.append(String.format("%1$2d", dateOfMonth));

			// Reset index when the row has 7 items
			if (currentIndex == 6) {
				stringBuilder.append(this.NEWLINE);
				currentIndex = 0;
			} else {
				stringBuilder.append(SPACE_BTW_DATES);
				currentIndex++;
			}

			date.setDate(dateOfMonth + 1);
		}

		return stringBuilder.toString();
	}

	private void padYearHeader(StringBuilder stringBuilder, int columns, int year) {
		int leftPadded =
				(columns * WIDTH_OF_MONTH - SPACE_BTW_MONTHS.length() + 4) / 2;
		stringBuilder.append(String.format("%1$" + leftPadded + "d", year) +
				this.NEWLINE + this.NEWLINE);
	}

	private String getYearOutput(int year) {
		StringBuilder stringBuilder = new StringBuilder();

		int columns = 3;
		int rows = (12 + columns - 1) / columns;
		String[][] linesInRow = new String[columns][];
		String[] colLines;

		this.padYearHeader(stringBuilder, columns, year);

		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < columns; col++) {
				month = row * columns + col;
				if (month > 11) {
					break;
				}

				linesInRow[col] =
						getMonthOutput(year, month, false).split("\\n");
			}

			boolean isRowDone = false;
			int currentLine = 0;
			while (!isRowDone) {
				isRowDone = true;

				for (int col = 0; col < columns; col++) {
					month = row * columns + col;
					if (month > 11) {
						break;
					}

					colLines = linesInRow[col];
					stringBuilder.append(String.format("%1$-" + WIDTH_OF_MONTH + "s",
							currentLine < colLines.length ? colLines[currentLine] : ""));
					isRowDone = isRowDone && currentLine >= colLines.length - 1;
				}

				currentLine++;
				stringBuilder.append(this.NEWLINE);
			}

			stringBuilder.append(this.NEWLINE);
		}

		return stringBuilder.toString();
	}

	private String getOutput() {
		if (this.month == null) {
			return getYearOutput(this.year);
		} else {
			return getMonthOutput(this.year, this.month - 1, true);
		}
	}

	@SuppressWarnings("PMD.PreserveStackTrace")
	private void outputWithoutTrailingSpace(String output, OutputStream stdout)
			throws CalException {
		try {
			String[] lines = output.split("\\n");
			for (String line: lines) {
				stdout.write(line.replaceAll("\\s+$", "").getBytes());
				stdout.write(this.NEWLINE.getBytes());
			}
		} catch (IOException e) {
			throw new CalException("Unable to write to output stream");
		}
	}

	@Override
	public void run(String[] args, InputStream stdin, OutputStream stdout)
			throws CalException {
		reset();
		parseArgs(args);
		String output = getOutput();
		outputWithoutTrailingSpace(output, stdout);
	}

	@Override
	public String printCal(String args) {
		return parseAndEvaluate(args);
	}

	@Override
	public String printCalWithMondayFirst(String args) {
		return parseAndEvaluate(args);
	}

	@Override
	public String printCalForMonthYear(String args) {
		return parseAndEvaluate(args);
	}

	@Override
	public String printCalForYear(String args) {
		return parseAndEvaluate(args);
	}

	@Override
	public String printCalForMonthYearMondayFirst(String args) {
		return parseAndEvaluate(args);
	}

	@Override
	public String printCalForYearMondayFirst(String args) {
		return parseAndEvaluate(args);
	}

	private String parseAndEvaluate(String args) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		try {
			String[] splittedArguments = args == null ?
					new String[0] : args.split("\\s+");
			this.run(splittedArguments, null, out);
			return out.toString();
		} catch (CalException e) {
			return e.getMessage();
		}
	}
}
