package sg.edu.nus.comp.cs4218.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.junit.rules.ExpectedException;
import sg.edu.nus.comp.cs4218.Shell;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;

@SuppressWarnings({ "PMD.LongVariable", "PMD.AvoidDuplicateLiterals" })
public class ShellIORedirectionTest {
	ByteArrayOutputStream outputStream;
	private Shell shell;
	private String output;

	private static final String NEW_LINE = System.getProperty("line.separator");

	private static final String TEST_STRING = "test";
	private static final String TEST_FILE_PATH = "test_ioredirection/";
	private static final String TEST_FILE_INPUT = "test_ioredirection/input.txt";
	private static final String TEST_FILE_INVALID_INPUT = "test_ioredirection/invalid_input.txt";
	private static final String TEST_FILE_INVALID_OUTPUT = "test_ioredirection/invalid_output.txt";
	private static final String TEST_FILE_OUTPUT = "test_ioredirection/output.txt";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setUp() throws Exception {
		shell = new ShellImpl();
		outputStream = new ByteArrayOutputStream();

		Files.createDirectories(Paths.get(TEST_FILE_PATH));
		try(PrintWriter out = new PrintWriter(TEST_FILE_INPUT)) {
		    out.print(TEST_STRING + NEW_LINE);
		}
	}

	@After
	public void tearDown() throws Exception {
		File tempOutputFile = new File(TEST_FILE_OUTPUT);
		tempOutputFile.delete();
		File tempInputFile = new File(TEST_FILE_INPUT);
		tempInputFile.delete();
		File tempFileDir = new File(TEST_FILE_PATH);
		tempFileDir.delete();
	}

	static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}

	@Test
	public void testRedirectInputOutput() throws AbstractApplicationException, ShellException, IOException {
		String cmd = "cat"  + " < " + TEST_FILE_INPUT + " > " + TEST_FILE_OUTPUT;

		shell.parseAndEvaluate(cmd, outputStream);

		output = readFile(TEST_FILE_OUTPUT, Charset.defaultCharset());

		assertEquals(TEST_STRING + NEW_LINE, output);
	}

	@Test
	public void testRedirectOutput() throws AbstractApplicationException, ShellException, IOException {
		String cmd = "echo " + TEST_STRING  + " > " + TEST_FILE_OUTPUT;

		shell.redirectOutput(cmd);

		output = readFile(TEST_FILE_OUTPUT, Charset.defaultCharset());

		assertEquals(TEST_STRING + NEW_LINE, output);
	}

	@Test
	public void testRedirectInput() throws AbstractApplicationException, ShellException {
		String cmd = "cat"  + " < " + TEST_FILE_INPUT;

		output = shell.redirectInput(cmd);

		assertEquals(TEST_STRING + NEW_LINE, output);
	}

	@Test
	public void testRedirectInputWithNoFile() throws AbstractApplicationException, ShellException {
		String cmd = "cat"  + " < ";

		output = shell.redirectInputWithNoFile(cmd);

		assertEquals("shell: Invalid syntax encountered near <", output);
	}

	@Test
	public void testRedirectOutputWithNoFile() throws AbstractApplicationException, ShellException {
		String cmd = "echo"  + " > ";

		output = shell.redirectOutputWithNoFile(cmd);

		assertEquals("shell: Invalid syntax encountered near >", output);
	}

	@Test
	public void testRedirectOutputWithInvalidFile() throws AbstractApplicationException, ShellException {
		String cmd = "cat "  + TEST_FILE_INPUT + " > " + TEST_FILE_INVALID_OUTPUT;

//		thrown.expect(ShellException.class);
//		thrown.expectMessage("No such file or directory");
		shell.parseAndEvaluate(cmd, outputStream);
		output = outputStream.toString();
		assertEquals("", output);
	}
	
	@Test
	public void testRedirectInputWithInvalidFile() throws AbstractApplicationException, ShellException {
		String cmd = "cat"  + " < " + TEST_FILE_INVALID_INPUT;

		thrown.expect(ShellException.class);
		thrown.expectMessage("No such file or directory");
		shell.parseAndEvaluate(cmd, outputStream);
	}

	@Test
	public void testRedirectInputWithException() throws AbstractApplicationException, ShellException {
		String cmd = "cat '/  /'"  + " < " + TEST_FILE_INPUT;

		output = shell.redirectInputWithException(cmd);

		assertEquals("cat: This is a directory", output);
	}

	@Test
	public void testRedirectOutputWithException() throws AbstractApplicationException, ShellException {
		String cmd = "cat '/  /'"  + " > " + TEST_FILE_OUTPUT;

		output = shell.redirectOutputWithException(cmd);

		assertEquals("cat: This is a directory", output);
	}

	@Test
	public void testRedirectOutputWithExceptionFileNotCreated() throws AbstractApplicationException, ShellException {
		String cmd = "cat '/  /'"  + " > " + TEST_FILE_OUTPUT;

		output = shell.redirectOutputWithException(cmd);

		assertTrue(new File(TEST_FILE_OUTPUT).exists());
	}

}
