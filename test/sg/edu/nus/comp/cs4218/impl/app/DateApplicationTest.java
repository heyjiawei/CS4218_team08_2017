package sg.edu.nus.comp.cs4218.impl.app;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import sg.edu.nus.comp.cs4218.exception.DateException;

public class DateApplicationTest {
	private static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
	
	DateApplication dateApp;
	String printedDate;
	InputStream in;
	OutputStream out;
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setUp() throws Exception {
		dateApp = new DateApplication();
	}
	
	@Test 
	public void testNullOutputStream() throws Exception {
		in = new ByteArrayInputStream("".getBytes());
		thrown.expect(DateException.class);
		thrown.expectMessage("date: No output stream provided\n");
		dateApp.run(new String[]{""}, in, null);
	}
	
	@Test
	public void testAdditionalArguments() throws Exception {
		in = new ByteArrayInputStream("".getBytes());
		out = new ByteArrayOutputStream();
		thrown.expect(DateException.class);
		thrown.expectMessage("date: illegal date format\n");
		dateApp.run(new String[]{"date", "today"}, in, out);
	}
	
	@Test
	public void testDateWithCurrentTimeDate() throws DateException {
		out = new ByteArrayOutputStream();
		dateApp.run(null, null, out);
		Calendar cal = Calendar.getInstance();
		assertEquals(DEFAULT_DATE_FORMAT.format(cal.getTime()), out.toString());
	}

	@Test
	public void testDateWithWaitOneSecond() throws DateException, InterruptedException {
		out = new ByteArrayOutputStream();
		dateApp.run(null, null, out);
		Thread.sleep(1000);
		Calendar cal = Calendar.getInstance();
		assertNotSame(DEFAULT_DATE_FORMAT.format(cal.getTime()), out.toString());
	}

}
