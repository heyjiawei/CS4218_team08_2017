package sg.edu.nus.comp.cs4218.impl.app;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import sg.edu.nus.comp.cs4218.exception.DateException;

@SuppressWarnings("PMD.LongVariable")
public class DateApplicationTest {
	private static final String DEFAULT_DATE_FORMAT = "EEE MMM dd HH:mm:ss zzz yyyy";
	
	DateApplication dateApp;
	String printedDate;
	InputStream inputStream;
	OutputStream outputStream;
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setUp() throws Exception {
		dateApp = new DateApplication();
	}
	
	@Test 
	public void testNullOutputStream() throws Exception {
		inputStream = new ByteArrayInputStream("".getBytes());
		thrown.expect(DateException.class);
		thrown.expectMessage("date: No output stream provided\n");
		dateApp.run(new String[]{""}, inputStream, null);
	}
	
	@Test
	public void testAdditionalArguments() throws Exception {
		inputStream = new ByteArrayInputStream("".getBytes());
		outputStream = new ByteArrayOutputStream();
		thrown.expect(DateException.class);
		thrown.expectMessage("date: illegal date format\n");
		dateApp.run(new String[]{"date", "today"}, inputStream, outputStream);
	}
	
	@Test
	public void testDateWithCurrentTimeDate() throws DateException {
		SimpleDateFormat dateFormatter = new SimpleDateFormat(DEFAULT_DATE_FORMAT, Locale.ENGLISH);
		outputStream = new ByteArrayOutputStream();
		dateApp.run(null, null, outputStream);
		Calendar cal = Calendar.getInstance();
		assertEquals(dateFormatter.format(cal.getTime()), outputStream.toString());
	}

	@Test
	public void testDateWithWaitOneSecond() throws DateException, InterruptedException {
		SimpleDateFormat dateFormatter = new SimpleDateFormat(DEFAULT_DATE_FORMAT, Locale.ENGLISH);
		outputStream = new ByteArrayOutputStream();
		dateApp.run(null, null, outputStream);
		Thread.sleep(1000);
		Calendar cal = Calendar.getInstance();
		assertNotSame(dateFormatter.format(cal.getTime()), outputStream.toString());
	}

}
