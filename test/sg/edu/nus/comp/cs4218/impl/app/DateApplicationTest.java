package sg.edu.nus.comp.cs4218.impl.app;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.DateException;

public class DateApplicationTest {
	DateApplication dateApp;
	String printedDate;
	InputStream in;
	OutputStream out;

//	@BeforeClass
//	public static void setUpBeforeClass() throws Exception {
//	}
//
//	@AfterClass
//	public static void tearDownAfterClass() throws Exception {
//	}

	@Before
	public void setUp() throws Exception {
		dateApp = new DateApplication();
	}

//	@After
//	public void tearDown() throws Exception {
//	}
	
	@Test (expected = DateException.class)
	public void testNullOutputStream() throws Exception {
		in = new ByteArrayInputStream( "date".getBytes() );
		dateApp.run(new String[]{"date"}, in, null);
	}
	
	@Test (expected = DateException.class)
	public void testNullInputStream() throws Exception {
		
	}

//	@Test
//	public void testPrintCurrentDate() {
//		 How to test this? Check all except time?
//		fail("Not yet implemented");
//		printedDate = dateApp.printCurrentDate("date");
//		System.out.print(printedDate);
//	}

}
