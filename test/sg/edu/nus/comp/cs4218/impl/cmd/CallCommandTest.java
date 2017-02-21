package sg.edu.nus.comp.cs4218.impl.cmd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

import org.junit.*;
import org.junit.rules.ExpectedException;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CallCommandTest {

	private CallCommand call;;
	private ByteArrayInputStream in;
	private ByteArrayOutputStream out;

	private static final String TEST_STRING = "test in file";
	private static final String TEST_FILE_PATH = "test_callcommand";
	private static final String TEST_FILE_INPUT = "test_callcommand/input.txt";
	private static final String TEST_FILE_OUTPUT = "test_callcommand/output.txt";

	static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setUp() throws Exception {
		call = new CallCommand();
	}

	@After
	public void tearDown() throws Exception {
		new File(TEST_FILE_OUTPUT).delete();
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Files.createDirectories(Paths.get(TEST_FILE_PATH));
		try (PrintWriter out = new PrintWriter(TEST_FILE_INPUT)) {
			out.print(TEST_STRING);
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		new File(TEST_FILE_INPUT).delete();
		new File(TEST_FILE_PATH).delete();
	}

	@Test
	public void testParseEmptyString() throws ShellException {
		call.cmdline = "";

		thrown.expect(ShellException.class);
		thrown.expectMessage(ShellImpl.EXP_INVALID_APP);
		call.parse();
	}

	@Test
	public void testParseApp() throws ShellException {
		call.cmdline = "> out  echo   a b 'c' d  ";

		call.parse();

		assertEquals("echo", call.app);
	}

	@Test
	public void testParseArguments() throws ShellException {
		call.cmdline = "  echo   -a b > why 'c' d  ";
		String[] expected = { "-a", "b", "'c'", "d" };

		call.parse();

		assertArrayEquals(expected, call.argsArray);
	}

	@Test
	public void testParseInputRedirection() throws ShellException {
		call.cmdline = "  grep 'a' < text1.txt > result.txt";

		call.parse();

		assertEquals("text1.txt", call.inputStreamS);
	}

	@Test
	public void testParseOutputRedirection() throws ShellException {
		call.cmdline = "  grep 'a' < text1.txt > result.txt";

		call.parse();

		assertEquals("result.txt", call.outputStreamS);
	}

	@Test
	public void testEvaluateNoInputOutputStream()
			throws AbstractApplicationException, ShellException {
		call.app = "cat";

		in = new ByteArrayInputStream("test".getBytes());
		out = new ByteArrayOutputStream();

		call.evaluate(in, out);

		assertEquals("test", out.toString());
	}

	@Test
	public void testEvaluateInputOutputStream()
			throws AbstractApplicationException, ShellException, IOException {
		call.app = "cat";
		call.inputStreamS = TEST_FILE_INPUT;
		call.outputStreamS = TEST_FILE_OUTPUT;

		in = new ByteArrayInputStream("test".getBytes());
		out = new ByteArrayOutputStream();

		call.evaluate(in, out);

		String output = readFile(TEST_FILE_OUTPUT, Charset.defaultCharset());
		assertEquals("test in file", output);
	}
}
