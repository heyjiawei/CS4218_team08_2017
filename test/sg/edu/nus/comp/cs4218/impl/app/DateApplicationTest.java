package sg.edu.nus.comp.cs4218.impl.app;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import sg.edu.nus.comp.cs4218.exception.DateException;

public class DateApplicationTest {
	DateApplication dateApp;
	String printedDate;
	InputStream in;
	OutputStream out;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		dateApp = new DateApplication();
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test (expected = DateException.class)
	public void testNullOutputStream() throws Exception {
		in = new ByteArrayInputStream("".getBytes());
		dateApp.run(new String[]{""}, in, null);
	}
	
	@Test (expected = DateException.class)
	public void testNullInputStream() throws Exception {
		out = new ByteArrayOutputStream();
		dateApp.run(new String[]{""}, null, out);
	}
	
	@Test (expected = DateException.class)
	public void testAdditionalArguments() throws Exception {
		in = new ByteArrayInputStream("".getBytes());
		out = new ByteArrayOutputStream();
		dateApp.run(new String[]{"date", "today"}, in, out);
	}

	@Test
	public void testPrintCurrentDate() {
		DateFormat dateformat = new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy", 
													Locale.ENGLISH);
		java.util.Date currentDate = new java.util.Date();
		dateformat.format(currentDate);
		Boolean isSame = false;
		try {
			in = new ByteArrayInputStream("".getBytes());
			out = new ByteArrayOutputStream();
			dateApp.run(new String[]{}, in, out);
			byte[] byteArray = ((ByteArrayOutputStream) out).toByteArray();
			java.util.Date appDate = dateformat.parse(new String(byteArray));
			isSame = (currentDate.getTime() - appDate.getTime()) < 5000;
		} catch (ParseException| DateException e) {
			e.printStackTrace();
		}
		assertTrue(isSame);
	}

}
