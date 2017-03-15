package sg.edu.nus.comp.cs4218.impl.app;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.GrepException;
import sg.edu.nus.comp.cs4218.exception.WcException;

public class WcApplicationTest {
	private static final String FILE_SEPARATOR = File.separator;
	private static final String LINE_SEPARATOR = System.lineSeparator();
	private static final String MWLFILE = String.format("test_inputs%swc%swc_test.txt", 
			FILE_SEPARATOR, FILE_SEPARATOR);
	private static final String MWLFILE_NONEWLINEENDING = String.format("test_inputs%swc%s3_lines.txt", 
			FILE_SEPARATOR, FILE_SEPARATOR);
	private static final String EMPTYFILE = String.format("test_inputs%swc%sempty.txt", 
			FILE_SEPARATOR, FILE_SEPARATOR);
	private static final String SINGLEWORD = String.format("test_inputs%swc%ssingleWord.txt", 
			FILE_SEPARATOR, FILE_SEPARATOR);
	private static final String EMPTYFOLDER = String.format("test_inputs%swc%semptyFolder%s", 
			FILE_SEPARATOR, FILE_SEPARATOR, FILE_SEPARATOR);

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	WcApplication wcApp;
	InputStream in;
	OutputStream out;

	@Before
	public void setUp() throws Exception {
		wcApp = new WcApplication();
		in = new FileInputStream(MWLFILE);
		out = new ByteArrayOutputStream();
		PrintStream print = new PrintStream(out);
		System.setOut(print);
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testNullStdout() throws WcException {
		String[] args = {"-l", MWLFILE_NONEWLINEENDING};
		thrown.expect(WcException.class);
		thrown.expectMessage("wc: No output stream provided\n");
		wcApp.run(args, in, null);
	}
	
	@Test
	public void testNullStdinNullArgs() throws WcException {
		thrown.expect(WcException.class);
		thrown.expectMessage("wc: No input provided\n");
		wcApp.run(null, null, out);
	}
	
	@Test
	public void testPrintNewlineCountInFile() {
		String result = wcApp.printNewlineCountInFile(String.format("-L %s", MWLFILE));
		String expected = String.format("       3 %s%s", MWLFILE, LINE_SEPARATOR);
		assertEquals(expected, result);
	}
	
	@Test
	public void testPrintNewlineCountInFileInRun() throws WcException {
		String[] args = {"-l", MWLFILE_NONEWLINEENDING};
		wcApp.run(args, in, out);
		String expected = String.format("       2 %s%s", MWLFILE_NONEWLINEENDING, LINE_SEPARATOR);
		assertEquals(expected, out.toString());
	}
	
	@Test
	public void testPrintCharacterCountInFile() {
		String results = wcApp.printCharacterCountInFile(String.format("-m %s", MWLFILE));
		String expected = String.format("       30 %s%s", MWLFILE, LINE_SEPARATOR);
		assertEquals(expected, results);
	}
	
	@Test
	public void testPrintCharacterCountInFileInRun() throws WcException {
		String[] args = {"-M", MWLFILE_NONEWLINEENDING};
		wcApp.run(args, in, out);
		String expected = String.format("       29 %s%s", MWLFILE_NONEWLINEENDING, LINE_SEPARATOR);
		assertEquals(expected, out.toString());
	}
	
	@Test
	public void testPrintWordCountInFile() {
		String results = wcApp.printWordCountInFile(String.format("-w %s", MWLFILE));
		String expected = String.format("       6 %s%s", MWLFILE, LINE_SEPARATOR);
		assertEquals(expected, results);
	}

	@Test
	public void testPrintAllCountsInFileInRun() throws WcException {
		String[] args = {"-L", "-M", "-W", MWLFILE_NONEWLINEENDING};
		wcApp.run(args, in, out);
		String expected = String.format("       29       6       2 %s%s", MWLFILE_NONEWLINEENDING, LINE_SEPARATOR);
		assertEquals(expected, out.toString());
	}
	
	@Test 
	public void testPrintWcOfEmptyFileInRun() throws WcException {
		String[] args = {"-L", "-M", "-W", EMPTYFILE};
		wcApp.run(args, in, out);
		String expected = String.format("       0       0       0 %s%s", EMPTYFILE, LINE_SEPARATOR);
		assertEquals(expected, out.toString());
	}
	
	@Test
	public void testPrintCharCountInMultiFile() {
		String results = wcApp.printCharacterCountInFile(String.format("-m %s %s", MWLFILE, MWLFILE_NONEWLINEENDING));
		String expected = String.format("       30 %s%s", MWLFILE, LINE_SEPARATOR) + 
						String.format("       29 %s%s", MWLFILE_NONEWLINEENDING, LINE_SEPARATOR) + 
						String.format("       59 total%s", LINE_SEPARATOR);
		assertEquals(expected, results);
	}
	
	@Test
	public void testNoFlagMultiFileInRun() throws WcException {
		String[] args = {EMPTYFILE, MWLFILE_NONEWLINEENDING};
		wcApp.run(args, in, out);
		String expected = String.format("       0       0       0 %s%s", EMPTYFILE, LINE_SEPARATOR) + 
				String.format("       29       6       2 %s%s", MWLFILE_NONEWLINEENDING, LINE_SEPARATOR) + 
				String.format("       29       6       2 total%s", LINE_SEPARATOR);
		assertEquals(expected, out.toString());
	}
	
	@Test
	public void testJoinedOptionMultiFileInRun() throws WcException {
		String[] args = {"-ml", EMPTYFILE, MWLFILE_NONEWLINEENDING};
		wcApp.run(args, in, out);
		String expected = String.format("       0       0 %s%s", EMPTYFILE, LINE_SEPARATOR) + 
				String.format("       29       2 %s%s", MWLFILE_NONEWLINEENDING, LINE_SEPARATOR) + 
				String.format("       29       2 total%s", LINE_SEPARATOR);
		assertEquals(expected, out.toString());
	}
	
	@Test
	public void testDisjointedAllOptionMultiFileInRun() throws WcException {
		String[] args = {"-mL", "-w", EMPTYFILE, MWLFILE_NONEWLINEENDING};
		wcApp.run(args, in, out);
		String expected = String.format("       0       0       0 %s%s", EMPTYFILE, LINE_SEPARATOR) + 
				String.format("       29       6       2 %s%s", MWLFILE_NONEWLINEENDING, LINE_SEPARATOR) + 
				String.format("       29       6       2 total%s", LINE_SEPARATOR);
		assertEquals(expected, out.toString());
	}
	
	@Test
	public void testDisjointedAllOptionMultiFile() {
		String results = wcApp.printAllCountsInFile(String.format("-Lm -w %s %s", MWLFILE, MWLFILE_NONEWLINEENDING));
		String expected = String.format("       30       6       3 %s%s", MWLFILE, LINE_SEPARATOR) + 
						String.format("       29       6       2 %s%s", MWLFILE_NONEWLINEENDING, LINE_SEPARATOR) + 
						String.format("       59       12       5 total%s", LINE_SEPARATOR);
		assertEquals(expected, results);
	}
	
	@Test
	public void testWcWithRepeativeSingleFlagsInFile() {
		String results = wcApp.printWordCountInFile(String.format("-w -w %s", MWLFILE));
		String expected = String.format("       6 %s%s", MWLFILE, LINE_SEPARATOR);
		assertEquals(expected, results);
	}
	
	@Test
	public void testWcWithRepeativeSingleFlagsInFileInRun() throws WcException {
		String[] args = {"-w", "-w", "-W", MWLFILE};
		wcApp.run(args, in, out);
		String expected = String.format("       6 %s%s", MWLFILE, LINE_SEPARATOR);
		assertEquals(expected, out.toString());
	}
	
	@Test
	public void testWcWithDuplicateFlagsInFile() {
		String results = wcApp.printWordCountInFile(String.format("-wwwwwwwwwwww %s", MWLFILE));
		String expected = String.format("       6 %s%s", MWLFILE, LINE_SEPARATOR);
		assertEquals(expected, results);
	}
	
	@Test
	public void testWcWithDuplicateFlagsInFileInRun() throws WcException {
		String[] args = {"-wWw", MWLFILE};
		wcApp.run(args, in, out);
		String expected = String.format("       6 %s%s", MWLFILE, LINE_SEPARATOR);
		assertEquals(expected, out.toString());
	}
	
	@Test
	public void testWcWithDifferentFlagOrdersInFileInRun() throws WcException {
		String[] args1 = { "-wlm", MWLFILE };
		wcApp.run(args1, in, out);
		String output1 = out.toString();
		System.out.flush();
		String[] args2 = { "-lmw", MWLFILE };
		out = new ByteArrayOutputStream();
		wcApp.run(args2, in, out);
		String output2 = out.toString();
		String expected = String.format("       30       6       3 %s%s", MWLFILE, LINE_SEPARATOR);
		
		assertEquals(output1.toString(), output2.toString());
		assertEquals(expected, output1);
	}
	
	@Test
	public void testInvalidFlagInFileInRun() throws WcException {
		String[] args = { "-x", MWLFILE };
		thrown.expect(WcException.class);
		thrown.expectMessage("wc: Invalid Flag\n");
		wcApp.run(args, in, out);
	}
	
	@Test
	public void testWcWithMixValidAndInvalidFlagsInFileInRun() throws WcException {
		String[] args = { "-mxl", MWLFILE };
		thrown.expect(WcException.class);
		thrown.expectMessage("wc: Invalid Flag\n");
		wcApp.run(args, in, out);
	}
	
	@Test
	public void testInvalidFlagPositionInFileInRun() throws WcException {
		String[] args = { "-m", MWLFILE, "-l" };
		wcApp.run(args, null, out);
		String expected = String.format("       30 %s%s", MWLFILE, LINE_SEPARATOR) +
									"wc: -l Invalid file or directory\n" + 
									"       30 total\n";
		assertEquals(expected, out.toString());
	}
	
	@Test
	public void testInvalidFileInRun() throws WcException {
		String[] args = { "-m", "nsad.txt" };
		wcApp.run(args, null, out);
		String expected = "wc: nsad.txt Invalid file or directory\n";
		assertEquals(expected, out.toString());
	}
	
	
	@Test
	public void testInvalidFileAndValidFileMultiFileInRun() throws WcException {
		String[] args = { "-w", "nsad.txt", SINGLEWORD };
		wcApp.run(args, null, out);
		String expected = "wc: nsad.txt Invalid file or directory\n" + 
				String.format("       1 %s%s", SINGLEWORD, LINE_SEPARATOR) + 
								"       1 total\n";
		assertEquals(expected, out.toString());
	}
	
	@Test
	public void testEmptyDirectoryInRun() throws WcException {
		String[] args = { "-m", EMPTYFOLDER };
		wcApp.run(args, null, out);
		String expected = "wc: " + EMPTYFOLDER + " Invalid file or directory\n";
		assertEquals(expected, out.toString());
	}
	
	@Test 
	public void testFileThenOptionCommandArgPrintNewlineCountInFile() {
		String results = wcApp.printNewlineCountInFile(String.format("%s %s -Lm -w ", MWLFILE, MWLFILE_NONEWLINEENDING));
		String expected = String.format("       30       6       3 %s%s", MWLFILE, LINE_SEPARATOR) + 
						String.format("       29       6       2 %s%s", MWLFILE_NONEWLINEENDING, LINE_SEPARATOR) + 
						"wc: -Lm Invalid file or directory\n" + 
						"wc: -w Invalid file or directory\n" +
						String.format("       59       12       5 total%s", LINE_SEPARATOR);
		assertEquals(expected, results);
	}
	
	@Test
	public void testPrintCharacterCountInStdin() {
		String count = wcApp.printCharacterCountInStdin("-m", in);
		String expected = String.format("       30%s", LINE_SEPARATOR);
		assertEquals(expected, count);
	}
	
	@Test
	public void testPrintWordCountInStdin() {
		String count = wcApp.printWordCountInStdin("-w", in);
		String expected = String.format("       6%s", LINE_SEPARATOR);
		assertEquals(expected, count);
	}
	
	@Test
	public void testPrintNewlineCountInStdin() {
		String count = wcApp.printWordCountInStdin("-l", in);
		String expected = String.format("       3%s", LINE_SEPARATOR);
		assertEquals(expected, count);
	}

	@Test
	public void testPrintAllCountsInStdin() {
		String count = wcApp.printAllCountsInStdin("-m -w -l", in);
		String expected = String.format("       30       6       3%s", LINE_SEPARATOR);
		assertEquals(expected, count);
	}
	
	@Test
	public void testDisjointedAllOptionStdin() throws WcException {
		wcApp.run(new String[]{"-wl", "-m"}, in, out);
		String expected = String.format("       30       6       3%s", LINE_SEPARATOR);
		assertEquals(expected, out.toString());
	}
	
	@Test
	public void testNoOptionStdin() throws WcException {
		wcApp.run(new String[]{}, in, out);
		String expected = String.format("       30       6       3%s", LINE_SEPARATOR);
		assertEquals(expected, out.toString());
	}
	
	@Test
	public void testRepeativeSingleFlagsStdinInRun() throws WcException {
		String[] args = {"-w", "-w", "-W"};
		wcApp.run(args, in, out);
		String expected = String.format("       6%s", LINE_SEPARATOR);
		assertEquals(expected, out.toString());
	}
	
	@Test
	public void testDuplicateFlagsStdin() {
		String results = wcApp.printWordCountInStdin("-wwwwwwwwwwww", in);
		String expected = String.format("       6%s", LINE_SEPARATOR);
		assertEquals(expected, results);
	}
	
	@Test
	public void testDuplicateFlagsStdinInRun() throws WcException {
		String[] args = {"-wwWwwwwww"};
		wcApp.run(args, in, out);
		String expected = String.format("       6%s", LINE_SEPARATOR);
		assertEquals(expected, out.toString());
	}
	
	@Test
	public void testDifferentFlagOrdersStdinInRun() throws WcException, FileNotFoundException {
		String[] args1 = { "-wlm"};
		wcApp.run(args1, in, out);
		String output1 = out.toString();
		System.out.flush();
		out = new ByteArrayOutputStream();
		in = new FileInputStream(MWLFILE);
		String[] args2 = { "-lmw"};
		wcApp.run(args2, in, out);
		String output2 = out.toString();
		String expected = String.format("       30       6       3%s", LINE_SEPARATOR);
		
		assertEquals(output1.toString(), output2.toString());
		assertEquals(expected, output1);
	}
	
	@Test
	public void testInvalidFlagStdinInRun() throws WcException {
		String[] args = { "-x"};
		thrown.expect(WcException.class);
		thrown.expectMessage("wc: Invalid Flag\n");
		wcApp.run(args, in, out);
	}
	
	@Test
	public void testWcWithMixValidAndInvalidFlagsInRun() throws WcException {
		String[] args = { "-mxl"};
		thrown.expect(WcException.class);
		thrown.expectMessage("wc: Invalid Flag\n");
		wcApp.run(args, in, out);
	}
	
	@Test
	public void testEmptyStdinInRun() throws WcException, FileNotFoundException {
		String[] args = { "-mwl"};
		in = new FileInputStream(EMPTYFILE);
		thrown.expect(WcException.class);
		thrown.expectMessage("wc: Invalid File or Input stream empty\n");
		wcApp.run(args, in, out);
	}
	
}
