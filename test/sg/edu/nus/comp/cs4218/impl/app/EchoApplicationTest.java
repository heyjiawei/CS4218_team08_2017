package sg.edu.nus.comp.cs4218.impl.app;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import sg.edu.nus.comp.cs4218.exception.EchoException;

public class EchoApplicationTest {

	private InputStream inputStream = null;
	private ByteArrayOutputStream outputStream;
	private EchoApplication echoApplication;
	private final String lineSeparator = System.getProperty("line.separator");

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		echoApplication = new EchoApplication();
		outputStream = new ByteArrayOutputStream();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test(expected = EchoException.class)
	public void testThrowWhenArgsNull() throws EchoException {
		String[] args = null;
		echoApplication.run(args, inputStream, outputStream);
	}

	@Test(expected = EchoException.class)
	public void testThrowWhenOutputStreamNull() throws EchoException {
		String[] args = {};
		outputStream = null;
		echoApplication.run(args, inputStream, outputStream);
	}

	@Test(expected = EchoException.class)
	public void testNullArgument() throws EchoException {
		String[] args = {null};
		echoApplication.run(args, inputStream, outputStream);
	}

	@Test
	public void testNoArguments() throws EchoException {
		String[] args = {};
		echoApplication.run(args, inputStream, outputStream);
		assertEquals("\n", outputStream.toString());
	}

	@Test
	public void testOneArgument() throws EchoException {
		String testArgument = "test argument";
		String[] args = {testArgument};
		echoApplication.run(args, inputStream, outputStream);
		assertEquals(testArgument + lineSeparator, outputStream.toString());
	}

	@Test
	public void testMultipleArguments() throws EchoException {
		String firstTestArgument = "first test argument";
		String secondTestArgument = "second test argument";
		String[] args = {firstTestArgument, secondTestArgument};
		echoApplication.run(args, inputStream, outputStream);
		String expectedResult = firstTestArgument + " " +
				secondTestArgument + lineSeparator;
		assertEquals(expectedResult, outputStream.toString());
	}
}
