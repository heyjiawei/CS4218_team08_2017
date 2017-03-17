package sg.edu.nus.comp.cs4218.impl.app;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import sg.edu.nus.comp.cs4218.exception.SedException;

@SuppressWarnings({ "PMD.LongVariable", "PMD.AvoidDuplicateLiterals" })
public class SedApplicationTest {
	private static final String NEWLINE = System.lineSeparator();
	private static final String TEST_FILES_PATH = "test_inputs" + File.separator + "sed" + File.separator;
	private static final String TWO_LINE_FILE_PATH = TEST_FILES_PATH + "two-lines.txt";
	private static final String EMPTY_FILE_PATH = TEST_FILES_PATH + "empty.txt";
	private static final String NUMBER_FILE_PATH = TEST_FILES_PATH + "number.txt";
	private static final String HELLO_WORLD_FILE_PATH = TEST_FILES_PATH + "hello world.txt";
	
	SedApplication sedApp;
	InputStream inputStream;
	OutputStream outputStream;
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setUp() throws Exception {
		sedApp = new SedApplication();
		outputStream = new ByteArrayOutputStream();
	}

	@Test
	public void testSedWithNullArgument() throws SedException, FileNotFoundException {
		String args[] = null;
		inputStream = new FileInputStream(HELLO_WORLD_FILE_PATH);
		thrown.expect(SedException.class);
		thrown.expectMessage("sed: Null args" + NEWLINE);
		sedApp.run(args, inputStream, outputStream);
		// "error on sed command - fails to throw exception with null args";
	}
	
	@Test
	public void testSedWithEmptyArgument() throws SedException {
		String args[] = {};
		inputStream = new ByteArrayInputStream("".getBytes());
		thrown.expect(SedException.class);
		thrown.expectMessage("sed: No replacement detected" + NEWLINE);
		sedApp.run(args, inputStream, outputStream);
		// "error on sed command - fails to throw exception with empty args";
	}
	
	@Test
	public void testSedWithLength1EmptyArgument() throws SedException {
		String args[] = {""};
		inputStream = new ByteArrayInputStream("".getBytes());
		thrown.expect(SedException.class);
		thrown.expectMessage("sed: No replacement detected" + NEWLINE);
		sedApp.run(args, inputStream, outputStream);
		// "error on sed command - fails to throw exception with empty args";
	}
	
	@Test
	public void testSedWithSingleArgument() throws SedException {
		String args[] = { "arg1" };
		inputStream = null;
		thrown.expect(SedException.class);
		thrown.expectMessage("sed: Invalid expression" + NEWLINE);
		sedApp.run(args, inputStream, outputStream);
		// "error on sed command - fails to throw exception with insuffcient
		// args";
	}
	
	@Test
	public void testSedWithNullStdout() throws SedException {
		String args[] = { "s-c-a-g", EMPTY_FILE_PATH };
		inputStream = new ByteArrayInputStream("".getBytes());
		outputStream = null;
		thrown.expect(SedException.class);
		thrown.expectMessage("sed: No output stream provided" + NEWLINE);
		sedApp.run(args, inputStream, outputStream);
		// "error on sed command - fails to throw exception with empty stdout";
	}

	
	@Test
	public void testSedWithNullStdinAndNonExistentFile() throws SedException {
		String args[] = { "s|a|b|", "non-existent.txt" };
		inputStream = null;
		thrown.expect(SedException.class);
		thrown.expectMessage("sed: Invalid File" + NEWLINE);
		sedApp.run(args, inputStream, outputStream);
		// String msg =
		// "error on sed command - fails to throw exception with null stdin and
		// non-existent file ";
	}

	@Test
	public void testSedWithEmptyFile() throws SedException {
		String args[] = { "s|a|b|", EMPTY_FILE_PATH };
		inputStream = null;
		String expected = NEWLINE;
		sedApp.run(args, inputStream, outputStream);
		assertEquals(expected, outputStream.toString());
	}

	@Test
	public void testSedWithTwoLineFile() throws SedException {
		String args[] = { "s|a|b|", TWO_LINE_FILE_PATH };
		inputStream = null;
		String expected = "Hey, good to know <you>!" + NEWLINE + "This is b small file consists of {1+1+0} lines."
				+ NEWLINE + "/* Hope this helps */ # no new line here" + NEWLINE;
		sedApp.run(args, inputStream, outputStream);
		assertEquals(expected, outputStream.toString());
	}
	
	@Test
	public void testSedWithFileThatNameIncludesSpace() throws SedException {
		String args[] = { "s|1|2|", HELLO_WORLD_FILE_PATH };
		inputStream = null;
		String expected = "hello world!" + NEWLINE;
		sedApp.run(args, inputStream, outputStream);
		assertEquals(expected, outputStream.toString());
	}

	@Test
	public void testSedWithEmptyFileInputStream() throws SedException {
		String args[] = { "s|a|b|" };
		inputStream = new ByteArrayInputStream("".getBytes());
		String expected = NEWLINE;
		sedApp.run(args, inputStream, outputStream);
		assertEquals(expected, outputStream.toString());
	}
	
	@Test
	public void testSedWithNumberFileInputStream() throws SedException, FileNotFoundException {
		String args[] = { "s*3*76*" };
		inputStream = new FileInputStream(NUMBER_FILE_PATH);
		String expected = "01276456789" + NEWLINE;
		sedApp.run(args, inputStream, outputStream);
		assertEquals(expected, outputStream.toString());
	}
	
	@Test
	public void testSedWithTwoLineFileInputStream() throws SedException, FileNotFoundException {
		// mock current directory to a fake non-root one
		String args[] = { "s|a|b|" };
		inputStream = new FileInputStream(TWO_LINE_FILE_PATH);
		String expected = "Hey, good to know <you>!" + NEWLINE + "This is b small file consists of {1+1+0} lines."
				+ NEWLINE + "/* Hope this helps */ # no new line here" + NEWLINE;
		sedApp.run(args, inputStream, outputStream);
		assertEquals(expected, outputStream.toString());
	}
	
	@Test
	public void testSedWithTwoLineFileInputStreamAndEmptyFile() throws SedException, FileNotFoundException {
		String args[] = { "s|a|b|", EMPTY_FILE_PATH };
		inputStream = new FileInputStream(TWO_LINE_FILE_PATH);
		String expected = NEWLINE;
		sedApp.run(args, inputStream, outputStream);
		assertEquals(expected, outputStream.toString());
	}
	
	@Test
	public void testSedWithExtraArgs() throws SedException {
		String args[] = { "s|a|b|", EMPTY_FILE_PATH, "-l" };
		inputStream = null;
		thrown.expect(SedException.class);
		thrown.expectMessage("sed: Incorrect number of args" + NEWLINE);
		sedApp.run(args, inputStream, outputStream);
	}

	@Test
	public void testSedWithInvalidReplacementOnIllegalFormatMissingS() throws SedException {
		String args[] = { "|0|1|", NUMBER_FILE_PATH };
		inputStream = null;
		thrown.expect(SedException.class);
		thrown.expectMessage("sed: Invalid expression" + NEWLINE);
		sedApp.run(args, inputStream, outputStream);
		// "error on sed command - incorrect output with invalid replacement
		// that has illegal format";
	}
	
	@Test
	public void testSedWithInvalidReplacementOnIllegalFormatSubstituteS1() throws SedException {
		String args[] = { "m|1|2|", NUMBER_FILE_PATH };
		inputStream = null;
		thrown.expect(SedException.class);
		thrown.expectMessage("sed: Invalid expression" + NEWLINE);
		sedApp.run(args, inputStream, outputStream);
		// "error on sed command - incorrect output with invalid replacement
		// that has illegal format";
	}
	
	@Test
	public void testSedWithInvalidReplacementOnIllegalFormatSubstituteS2() throws SedException {
		String args[] = { "ms1s2s", NUMBER_FILE_PATH };
		inputStream = null;
		thrown.expect(SedException.class);
		thrown.expectMessage("sed: Invalid expression" + NEWLINE);
		sedApp.run(args, inputStream, outputStream);
		// "error on sed command - incorrect output with invalid replacement
		// that has illegal format";
	}
	
	
	@Test
	public void testSedWithInvalidReplacementOnIllegalExtraFlag() throws SedException {
		// mock current directory to a fake non-root one
		String args[] = { "s|0|1|gg", NUMBER_FILE_PATH };
		inputStream = null;
		thrown.expect(SedException.class);
		thrown.expectMessage("sed: Invalid flag" + NEWLINE);
		sedApp.run(args, inputStream, outputStream);
		// "error on sed command - incorrect output with invalid replacement
		// that has illegal format";
	}
	
	@Test
	public void testSedWithInvalidReplacementOnDifferentDelimiter() throws SedException {
		String args[] = { "s-0|1|g", NUMBER_FILE_PATH };
		inputStream = null;
		thrown.expect(SedException.class);
		thrown.expectMessage("sed: Unterminated Expression" + NEWLINE);
		sedApp.run(args, inputStream, outputStream);
		// "error on sed command - incorrect output with invalid replacement
		// that has illegal format";
	}
	
	@Test
	public void testSedWithInvalidReplacementOnUnterminatedExpression1() throws SedException {
		String args[] = { "s|0|1g", NUMBER_FILE_PATH };
		inputStream = null;
		thrown.expect(SedException.class);
		thrown.expectMessage("sed: Unterminated Expression" + NEWLINE);
		sedApp.run(args, inputStream, outputStream);
		// "error on sed command - incorrect output with invalid replacement
		// that has illegal format";
	}
	
	@Test
	public void testSedWithInvalidReplacementOnUnterminatedExpression2() throws SedException {
		String args[] = { "s|0| m| |g", NUMBER_FILE_PATH };
		inputStream = null;
		thrown.expect(SedException.class);
		thrown.expectMessage("sed: Unterminated Expression" + NEWLINE);
		sedApp.run(args, inputStream, outputStream);
		// "error on sed command - incorrect output with invalid replacement
		// that has illegal format";
	}
	
	@Test
	public void testSedWithInvalidReplacementOnIllegalRegex() throws SedException {
		String args[] = { "s|[|1|", NUMBER_FILE_PATH };
		inputStream = null;
		thrown.expect(SedException.class);
		thrown.expectMessage("sed: Invalid regex pattern" + NEWLINE);
		sedApp.run(args, inputStream, outputStream);
		// "error on sed command - incorrect output with invalid replacement
		// that has illegal regrex";
	}

	@Test
	public void testSedWithGlobalReplacement() throws SedException, FileNotFoundException {
		String args[] = { "s.l.*.g" };
		inputStream = new FileInputStream(TWO_LINE_FILE_PATH);
		String expected = "Hey, good to know <you>!" + NEWLINE + "This is a sma** fi*e consists of {1+1+0} *ines."
				+ NEWLINE + "/* Hope this he*ps */ # no new *ine here" + NEWLINE;
		sedApp.run(args, inputStream, outputStream);
		assertEquals(expected, outputStream.toString());
	}
	
	@Test
	public void testSedWithDifferentSeparator1() throws SedException, FileNotFoundException {
		String args[] = { "ssls*sg" };
		inputStream = new FileInputStream(TWO_LINE_FILE_PATH);
		String expected = "Hey, good to know <you>!" + NEWLINE + "This is a sma** fi*e consists of {1+1+0} *ines."
				+ NEWLINE + "/* Hope this he*ps */ # no new *ine here" + NEWLINE;
		sedApp.run(args, inputStream, outputStream);
		assertEquals(expected, outputStream.toString());
	}
	
	@Test
	public void testSedWithDifferentSeparator2() throws SedException, FileNotFoundException {
		String args[] = { "s|l|*|" , TWO_LINE_FILE_PATH};
		inputStream = null;
		String expected = "Hey, good to know <you>!" + NEWLINE + 
						"This is a sma*l file consists of {1+1+0} lines." + NEWLINE + 
						"/* Hope this he*ps */ # no new line here" + NEWLINE;
		sedApp.run(args, inputStream, outputStream);
		assertEquals(expected, outputStream.toString());
	}
	
	@Test
	public void testSedWithEmptyReplacment() throws SedException, FileNotFoundException {
		String args[] = { "s|l||" };
		inputStream = new FileInputStream(TWO_LINE_FILE_PATH);
		String expected = "Hey, good to know <you>!" + NEWLINE + 
				"This is a smal file consists of {1+1+0} lines."+ NEWLINE + 
				"/* Hope this heps */ # no new line here" + NEWLINE;
		sedApp.run(args, inputStream, outputStream);
		assertEquals(expected, outputStream.toString());
	}
	
	@Test
	public void testSedWithComplexReplacement1() throws SedException, FileNotFoundException {
		String args[] = { "s|no| *&/s\\$|" };
		inputStream = new FileInputStream(TWO_LINE_FILE_PATH);
		String expected = "Hey, good to k *&/s$w <you>!" + NEWLINE + 
				"This is a small file consists of {1+1+0} lines."+ NEWLINE + 
				"/* Hope this helps */ #  *&/s$ new line here" + NEWLINE;
		sedApp.run(args, inputStream, outputStream);
		assertEquals(expected, outputStream.toString());
	}
	
	@Test
	public void testSedWithComplexReplacement2() throws SedException, FileNotFoundException {
		String args[] = { "s|o|[^]|" };
		inputStream = new FileInputStream(TWO_LINE_FILE_PATH);
		String expected = "Hey, g[^]od to know <you>!" + NEWLINE + 
				"This is a small file c[^]nsists of {1+1+0} lines."+ NEWLINE + 
				"/* H[^]pe this helps */ # no new line here" + NEWLINE;
		sedApp.run(args, inputStream, outputStream);
		assertEquals(expected, outputStream.toString());
	}
	
	@Test
	public void testSedWithComplexReplacement3() throws SedException, FileNotFoundException {
		String args[] = { "s|o   |% #$%^&|" };
		inputStream = new FileInputStream(TWO_LINE_FILE_PATH);
		String expected = "Hey, good to know <you>!" + NEWLINE + 
				"This is a small file consists of {1+1+0} lines."+ NEWLINE + 
				"/* Hope this helps */ # no new line here" + NEWLINE;
		sedApp.run(args, inputStream, outputStream);
		assertEquals(expected, outputStream.toString());
	}
	
	@Test
	public void testSedWithEmtpyRegexpAndEmptyReplacement() throws SedException, FileNotFoundException {
		String args[] = { "s|||" };
		inputStream = new FileInputStream(TWO_LINE_FILE_PATH);
		thrown.expect(SedException.class);
		thrown.expectMessage("sed: Invalid regex pattern" + NEWLINE);
		sedApp.run(args, inputStream, outputStream);
		// String msg =
		// "error on sed command - fail to throw exception with empty regular
		// expression and empty replacement";
	}

	@Test
	public void testSedWithEmtpyRegexp() throws SedException, FileNotFoundException {
		String args[] = { "s||m|g" };
		inputStream = new FileInputStream(TWO_LINE_FILE_PATH);
		thrown.expect(SedException.class);
		thrown.expectMessage("sed: Invalid regex pattern" + NEWLINE);
		sedApp.run(args, inputStream, outputStream);
		// String msg =
		// "error on sed command - fail to throw exception with empty regular
		// expression";
	}
	
	@Test
	public void testSedWithComplexRegexp1() throws SedException {
		String args[] = { "s|^This|r|g" , TWO_LINE_FILE_PATH};
		inputStream = null;
		String expected = "Hey, good to know <you>!" + NEWLINE + 
				"r is a small file consists of {1+1+0} lines."+ NEWLINE + 
				"/* Hope this helps */ # no new line here" + NEWLINE;
		sedApp.run(args, inputStream, outputStream);
		assertEquals(expected, outputStream.toString());
	}

	@Test
	public void testSedWithComplexRegexp2() throws SedException {
		String args[] = { "s|o{2,3}d*|r|" , TWO_LINE_FILE_PATH};
		inputStream = null;
		String expected = "Hey, gr to know <you>!" + NEWLINE + 
				"This is a small file consists of {1+1+0} lines."+ NEWLINE + 
				"/* Hope this helps */ # no new line here" + NEWLINE;
		sedApp.run(args, inputStream, outputStream);
		assertEquals(expected, outputStream.toString());
	}
	
	@Test
	public void testSedWithComplexRegexp3() throws SedException {
		String args[] = { "s|[^a-zA-Z ]|-|g" , TWO_LINE_FILE_PATH};
		inputStream = null;
		String expected = "Hey- good to know -you--" + NEWLINE + 
				"This is a small file consists of ------- lines-"+ NEWLINE + 
				"-- Hope this helps -- - no new line here" + NEWLINE;
		sedApp.run(args, inputStream, outputStream);
		assertEquals(expected, outputStream.toString());
	}
	
	@Test
	public void testSedWithComplexRegexp4() throws SedException {
		String args[] = { "s|.||g" , TWO_LINE_FILE_PATH};
		inputStream = null;
		String expected = NEWLINE + NEWLINE + NEWLINE;
		sedApp.run(args, inputStream, outputStream);
		assertEquals(expected, outputStream.toString());
	}
	
	@Test
	public void testReplaceFirstSubStringInFile() throws SedException {
		inputStream = null;
		String cmd = "s|o{2,3}d*|r| " + TWO_LINE_FILE_PATH;
		String output = sedApp.replaceFirstSubStringInFile(cmd);
		String expected = "Hey, gr to know <you>!" + NEWLINE + 
				"This is a small file consists of {1+1+0} lines."+ NEWLINE + 
				"/* Hope this helps */ # no new line here" + NEWLINE;
		assertEquals(expected, output);
	}
	
	@Test
	public void testReplaceAllSubstringsInFile() throws SedException {
		String cmd = "s|.||g " + TWO_LINE_FILE_PATH;
		inputStream = null;
		String expected = NEWLINE + NEWLINE + NEWLINE;
		String output = sedApp.replaceAllSubstringsInFile(cmd);
		assertEquals(expected, output);
	}
	
	@Test
	public void testReplaceFirstSubStringFromStdin() throws SedException, FileNotFoundException {
		String cmd = "s|o{2,3}d*|r|";
		inputStream = new FileInputStream(TWO_LINE_FILE_PATH);
		String output = sedApp.replaceFirstSubStringFromStdin(cmd, inputStream);
		String expected = "Hey, gr to know <you>!" + NEWLINE + 
				"This is a small file consists of {1+1+0} lines."+ NEWLINE + 
				"/* Hope this helps */ # no new line here" + NEWLINE;
		assertEquals(expected, output);
	}
	
	@Test
	public void testReplaceAllSubstringsInStdin() throws SedException, FileNotFoundException {
		String cmd = "s|.||g";
		inputStream = new FileInputStream(TWO_LINE_FILE_PATH);
		String output = sedApp.replaceAllSubstringsInStdin(cmd, inputStream);
		String expected = NEWLINE + NEWLINE + NEWLINE;
		assertEquals(expected, output);
	}
//	
//  Unable to match quotes
//	@Test 
//	public void testReplaceFirstSubStringInFile() throws SedException {
//		String cmd = "\"s|o||\"  " + TWO_LINE_FILE_PATH;
//		String expected = "Hey, god to know <you>!" + NEWLINE + "This is a small file cnsists of {1+1+0} lines."
//				+ NEWLINE + "/* Hpe this helps */ # no new line here" + NEWLINE;
//
//		String msg = "error on sed command - incorrect output with method replaceFirstSubStringInFile";
//		assertEquals(expected, sedApp.replaceFirstSubStringInFile(cmd));
//	}
//
//	@Test 
//	public void testReplaceAllSubstringsInFile() throws SedException {
//		String cmd = "sed \"s|o||g\"  " + TWO_LINE_FILE_PATH;
//		String expected = "Hey, gd t knw <yu>!" + NEWLINE + "This is a small file cnsists f {1+1+0} lines." + NEWLINE
//				+ "/* Hpe this helps */ # n new line here" + NEWLINE;
//
//		String msg = "error on sed command - incorrect output with method replaceAllSubstringsInFile";
//		assertEquals(msg, expected, sedApp.replaceAllSubstringsInFile(cmd));
//	}

}
