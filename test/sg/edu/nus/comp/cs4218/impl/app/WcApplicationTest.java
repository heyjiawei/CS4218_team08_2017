package sg.edu.nus.comp.cs4218.impl.app;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
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
	private static final String MWLDIRECTORYFILE = String.format("test_inputs%swc%stesting_wc_folder%swc test.txt", 
			FILE_SEPARATOR, FILE_SEPARATOR, FILE_SEPARATOR);
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
	public void testPrintAllCountsInFile() throws WcException {
		String[] args = {"-L", "-M", "-W", MWLFILE_NONEWLINEENDING};
		wcApp.run(args, in, out);
		String expected = String.format("       29       6       2 %s%s", MWLFILE_NONEWLINEENDING, LINE_SEPARATOR);
		assertEquals(expected, out.toString());
	}
	
	@Test 
	public void testPrintWcOfEmptyFile() throws WcException {
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
	public void testNoFlagMultiFile() throws WcException {
		String[] args = {EMPTYFILE, MWLFILE_NONEWLINEENDING};
		wcApp.run(args, in, out);
		String expected = String.format("       0       0       0 %s%s", EMPTYFILE, LINE_SEPARATOR) + 
				String.format("       29       6       2 %s%s", MWLFILE_NONEWLINEENDING, LINE_SEPARATOR) + 
				String.format("       29       6       2 total%s", LINE_SEPARATOR);
		assertEquals(expected, out.toString());
	}
	
	@Test
	public void testJoinedOptionMultiFile() throws WcException {
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
	public void testWcWithRepeativeSingleFlags() {
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
	public void testWcWithDuplicateFlags() {
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
	public void testWcWithDifferentFlagOrders() throws WcException {
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
	public void testInvalidFlagInRun() throws WcException {
		String[] args = { "-x", MWLFILE };
		thrown.expect(WcException.class);
		thrown.expectMessage("wc: Invalid Flag\n");
		wcApp.run(args, in, out);
	}
	
	@Test
	public void testWcWithMixValidAndInvalidFlagsInRun() throws WcException {
		String[] args = { "-mxl", MWLFILE };
		thrown.expect(WcException.class);
		thrown.expectMessage("wc: Invalid Flag\n");
		wcApp.run(args, in, out);
	}
	
	@Test
	public void testInvalidFlagPosition() throws WcException {
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
//	
//	@Test
//	public void testPrintCharacterCountInStdin() {
//		in = new ByteArrayInputStream("4 lines\n\n6 words\n\n31 characters".getBytes());
//		String count = wcApp.printCharacterCountInStdin("-m wc_test.txt", in);
//		assertEquals("31", count);
//	}
//	
//	@Test
//	public void testPrintWordCountInStdin() {
//		in = new ByteArrayInputStream("4 lines\n\n6 words\n\n31 characters".getBytes());
//		String count = wcApp.printWordCountInStdin("-m wc_test.txt", in);
//		assertEquals("6", count);
//	}
//	
//	@Test
//	public void testPrintNewlineCountInStdin() {
//		in = new ByteArrayInputStream("4 lines\n\n6 words\n\n31 characters".getBytes());
//		String count = wcApp.printNewlineCountInStdin("-m wc_test.txt", in);
//		assertEquals("4", count);
//	}
//
//	@Test
//	public void testPrintAllCountsInStdin() {
//		in = new ByteArrayInputStream("4 lines\n\n6 words\n\n31 characters".getBytes());
//		String count = wcApp.printAllCountsInStdin("-m wc_test.txt", in);
//		assertEquals("31 6 4", count);
//	}
//	
//	@Test
//	public void testDisjointedAllOptionSimpleFile() throws AbstractApplicationException {
//		in = new ByteArrayInputStream("".getBytes());
//		out = new ByteArrayOutputStream();
//		wcApp.run(new String[]{"-wl", "-m", "wc_test.txt"}, in, out);
//		byte[] byteArray = ((ByteArrayOutputStream) out).toByteArray();
//		
//		assertEquals("31 6 4\n", new String(byteArray));
//	}
//	
//	@Test
//	public void testNoOptionSimpleFile() throws AbstractApplicationException {
//		in = new ByteArrayInputStream("".getBytes());
//		out = new ByteArrayOutputStream();
//		wcApp.run(new String[]{"wc_test.txt"}, in, out);
//		byte[] byteArray = ((ByteArrayOutputStream) out).toByteArray();
//		
//		assertEquals("31 6 4\n", new String(byteArray));
//	}
//
//	@Test
//	public void testDisjointedAllOptionStdin() throws AbstractApplicationException {
//		in = new ByteArrayInputStream("1 newline of code,\n x characters\n".getBytes());
//		out = new ByteArrayOutputStream();
//		wcApp.run(new String[]{"-wm", "-l"}, in, out);
//		byte[] byteArray = ((ByteArrayOutputStream) out).toByteArray();
//		
//		assertEquals("33 6 2\n", new String(byteArray));
//	}
//	
//	@Test
//	public void testNoOptionStdin() throws AbstractApplicationException {
//		in = new ByteArrayInputStream("1 newline of code,\n x characters\n".getBytes());
//		out = new ByteArrayOutputStream();
//		wcApp.run(new String[]{}, in, out);
//		byte[] byteArray = ((ByteArrayOutputStream) out).toByteArray();
//		
//		assertEquals("33 6 2\n", new String(byteArray));
//		
//	}
//	
//	@Test
//	public void testReadFromDir() {
//		in = new ByteArrayInputStream("".getBytes());
//		out = new ByteArrayOutputStream();
//		try {
//			wcApp.run(new String[]{"-lmw", "testing_wc_folder/wc file.txt"}, in, out);
//		} catch (AbstractApplicationException e) {
//			e.printStackTrace();
//		}
//		byte[] byteArray = ((ByteArrayOutputStream) out).toByteArray();
//		assertEquals("31 6 4\n", new String(byteArray));
//	}
	
//	@Test 
//	public void testEmptyFileWc() {
//		
//	}
	
//
//	@Test
//	public void testRepeatedOptions() {
//		in = new ByteArrayInputStream("".getBytes());
//		out = new ByteArrayOutputStream();
//		try {
//			wcApp.run(new String[]{"-mm", "testing_wc_folder/wc file.txt"}, in, out);
//		} catch (AbstractApplicationException e) {
//			e.printStackTrace();
//		}
//		byte[] byteArray = ((ByteArrayOutputStream) out).toByteArray();
//		assertEquals("31\n", new String(byteArray));
//	}
//
//	@Test (expected = WcException.class)
//	public void testThrowNoStdinInputProvidedException() throws Exception {
//		in = new ByteArrayInputStream("".getBytes());
//		out = new ByteArrayOutputStream();
//		wcApp.run(new String[]{""}, in, out);
//	}
//	
//	@Test (expected = WcException.class)
//	public void testThrowInvalidFileNameException() throws Exception {
//		in = new ByteArrayInputStream("".getBytes());
//		out = new ByteArrayOutputStream();
//		wcApp.run(new String[]{"this"}, in, out);
//	}
//	
//	@Test (expected = WcException.class)
//	public void testThrowInvalidOptionException() throws Exception {
//		in = new ByteArrayInputStream("".getBytes());
//		out = new ByteArrayOutputStream();
//		wcApp.run(new String[]{"-lm -c", "wc_test.txt"}, in, out);
//	}
}
