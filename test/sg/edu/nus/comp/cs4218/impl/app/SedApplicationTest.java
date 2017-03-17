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

/*
 * Assumptions: 
 * 1) run function will call the correct functions with the correct 
 * inputs in the correct order separated by a space
 * 2) Run function will take inputs directly from shell unordered
 * 3) Args for run: unordered consisting of pattern and file
 * 4) Args for Interface functions: pattern [file]
 * 5) All Args passed do not contain command 'sed' in front
 * 6) Expression will not be surrounded with quotes (i.e. ‘, “ “, “””)
 * 7) Filenames will not be surrounded with quotes (i.e. ‘, “ “, “””)
 */

public class SedApplicationTest {
	private static final String FILESEPARATOR = File.separator;
	private static final String NEWLINE = System.lineSeparator();
	private static final String DIRECTORY = "test_inputs" + FILESEPARATOR + "sed" + FILESEPARATOR;
	private static final String TWOLINE = DIRECTORY + "two-lines.txt";
	private static final String EMPTY_FILE_PATH = DIRECTORY + "empty.txt";
	private static final String NUMBER_FILE_PATH = DIRECTORY + "number.txt";
	private static final String HELLO_WORLD = DIRECTORY + "hello world.txt";
	
	private static final String NULL_ARG = "sed: Null args" + NEWLINE;
	private static final String NO_REPLACEMENT = "sed: No replacement detected" + NEWLINE;
	private static final String INVALID_EXPRES = "sed: Invalid expression" + NEWLINE;
	private static final String NO_STDIN = "sed: No output stream provided" + NEWLINE;
	private static final String INVALID_FLAG = "sed: Invalid flag" + NEWLINE;
	private static final String WRONG_ARG_COUNT = "sed: Incorrect number of args" + NEWLINE;
	private static final String UNCOMPLETE_EXPRES = "sed: Unterminated Expression" + NEWLINE;
	private static final String INVALID_REGEX = "sed: Invalid regex pattern" + NEWLINE;
	private static final String[] TWOLINE_ORIGINAL = {"Hey, good to know <you>!" + NEWLINE,
								"This is a small file consists of {1+1+0} lines." + NEWLINE,
								"/* Hope this helps */ # no new line here" + NEWLINE};
	
	SedApplication sedApp;
	InputStream stdin;
	OutputStream out;
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setUp() throws Exception {
		sedApp = new SedApplication();
		out = new ByteArrayOutputStream();
	}

	@Test
	public void testSedWithNullArgument() throws SedException, FileNotFoundException {
		String args[] = null;
		stdin = new FileInputStream(HELLO_WORLD);
		thrown.expect(SedException.class);
		thrown.expectMessage(NULL_ARG);
		sedApp.run(args, stdin, out);
		// "error on sed command - fails to throw exception with null args";
	}
	
	@Test
	public void testSedWithEmptyArgument() throws SedException {
		String args[] = {};
		stdin = new ByteArrayInputStream("".getBytes());
		thrown.expect(SedException.class);
		thrown.expectMessage(NO_REPLACEMENT);
		sedApp.run(args, stdin, out);
		// "error on sed command - fails to throw exception with empty args";
	}
	
	@Test
	public void testSedWithLength1EmptyArgument() throws SedException {
		String args[] = {""};
		stdin = new ByteArrayInputStream("".getBytes());
		thrown.expect(SedException.class);
		thrown.expectMessage(NO_REPLACEMENT);
		sedApp.run(args, stdin, out);
		// "error on sed command - fails to throw exception with empty args";
	}
	
	@Test
	public void testSedWithSingleArgument() throws SedException {
		String args[] = { "arg1" };
		stdin = null;
		thrown.expect(SedException.class);
		thrown.expectMessage(INVALID_EXPRES);
		sedApp.run(args, stdin, out);
		// "error on sed command - fails to throw exception with insuffcient
		// args";
	}
	
	@Test
	public void testSedWithNullStdout() throws SedException {
		String args[] = { "s-c-a-g", EMPTY_FILE_PATH };
		stdin = new ByteArrayInputStream("".getBytes());
		out = null;
		thrown.expect(SedException.class);
		thrown.expectMessage(NO_STDIN);
		sedApp.run(args, stdin, out);
		// "error on sed command - fails to throw exception with empty stdout";
	}

	
	@Test
	public void testSedWithNullStdinAndNonExistentFile() throws SedException {
		String args[] = { "s|a|b|", "non-existent.txt" };
		stdin = null;
		thrown.expect(SedException.class);
		thrown.expectMessage("sed: Invalid File" + NEWLINE);
		sedApp.run(args, stdin, out);
		// String msg =
		// "error on sed command - fails to throw exception with null stdin and
		// non-existent file ";
	}

	@Test
	public void testSedWithEmptyFile() throws SedException {
		String args[] = { "s|a|b|", EMPTY_FILE_PATH };
		stdin = null;
		String expected = NEWLINE;
		sedApp.run(args, stdin, out);
		assertEquals(expected, out.toString());
	}

	@Test
	public void testSedWithTwoLineFile() throws SedException {
		String args[] = { "s|a|b|", TWOLINE };
		stdin = null;
		String expected = TWOLINE_ORIGINAL[0] + "This is b small file consists of {1+1+0} lines."
				+ NEWLINE + TWOLINE_ORIGINAL[2];
		sedApp.run(args, stdin, out);
		assertEquals(expected, out.toString());
	}
	
	@Test
	public void testSedWithFileThatNameIncludesSpace() throws SedException {
		String args[] = { "s|1|2|", HELLO_WORLD };
		stdin = null;
		String expected = "hello world!" + NEWLINE;
		sedApp.run(args, stdin, out);
		assertEquals(expected, out.toString());
	}

	@Test
	public void testSedWithEmptyFileInputStream() throws SedException {
		String args[] = { "s|a|b|" };
		stdin = new ByteArrayInputStream("".getBytes());
		String expected = NEWLINE;
		sedApp.run(args, stdin, out);
		assertEquals(expected, out.toString());
	}
	
	@Test
	public void testSedWithNumberFileInputStream() throws SedException, FileNotFoundException {
		String args[] = { "s*3*76*" };
		stdin = new FileInputStream(NUMBER_FILE_PATH);
		String expected = "01276456789" + NEWLINE;
		sedApp.run(args, stdin, out);
		assertEquals(expected, out.toString());
	}
	
	@Test
	public void testSedWithTwoLineFileInputStream() throws SedException, FileNotFoundException {
		// mock current directory to a fake non-root one
		String args[] = { "s|a|b|" };
		stdin = new FileInputStream(TWOLINE);
		String expected = TWOLINE_ORIGINAL[0] + "This is b small file consists of {1+1+0} lines."
				+ NEWLINE + TWOLINE_ORIGINAL[2];
		sedApp.run(args, stdin, out);
		assertEquals(expected, out.toString());
	}
	
	@Test
	public void testSedWithTwoLineFileInputStreamAndEmptyFile() throws SedException, FileNotFoundException {
		String args[] = { "s|a|b|", EMPTY_FILE_PATH };
		stdin = new FileInputStream(TWOLINE);
		String expected = NEWLINE;
		sedApp.run(args, stdin, out);
		assertEquals(expected, out.toString());
	}
	
	@Test
	public void testSedWithExtraArgs() throws SedException {
		String args[] = { "s|a|b|", EMPTY_FILE_PATH, "-l" };
		stdin = null;
		thrown.expect(SedException.class);
		thrown.expectMessage(WRONG_ARG_COUNT);
		sedApp.run(args, stdin, out);
	}

	@Test
	public void testSedWithInvalidReplacementOnIllegalFormatMissingS() throws SedException {
		String args[] = { "|0|1|", NUMBER_FILE_PATH };
		stdin = null;
		thrown.expect(SedException.class);
		thrown.expectMessage(INVALID_EXPRES);
		sedApp.run(args, stdin, out);
		// "error on sed command - incorrect output with invalid replacement
		// that has illegal format";
	}
	
	@Test
	public void testSedWithInvalidReplacementOnIllegalFormatSubstituteS1() throws SedException {
		String args[] = { "m|1|2|", NUMBER_FILE_PATH };
		stdin = null;
		thrown.expect(SedException.class);
		thrown.expectMessage(INVALID_EXPRES);
		sedApp.run(args, stdin, out);
		// "error on sed command - incorrect output with invalid replacement
		// that has illegal format";
	}
	
	@Test
	public void testSedWithInvalidReplacementOnIllegalFormatSubstituteS2() throws SedException {
		String args[] = { "ms1s2s", NUMBER_FILE_PATH };
		stdin = null;
		thrown.expect(SedException.class);
		thrown.expectMessage(INVALID_EXPRES);
		sedApp.run(args, stdin, out);
		// "error on sed command - incorrect output with invalid replacement
		// that has illegal format";
	}
	
	
	@Test
	public void testSedWithInvalidReplacementOnIllegalExtraFlag() throws SedException {
		// mock current directory to a fake non-root one
		String args[] = { "s|0|1|gg", NUMBER_FILE_PATH };
		stdin = null;
		thrown.expect(SedException.class);
		thrown.expectMessage(INVALID_FLAG);
		sedApp.run(args, stdin, out);
		// "error on sed command - incorrect output with invalid replacement
		// that has illegal format";
	}
	
	@Test
	public void testSedWithInvalidReplacementOnDifferentDelimiter() throws SedException {
		String args[] = { "s-0|1|g", NUMBER_FILE_PATH };
		stdin = null;
		thrown.expect(SedException.class);
		thrown.expectMessage(UNCOMPLETE_EXPRES);
		sedApp.run(args, stdin, out);
		// "error on sed command - incorrect output with invalid replacement
		// that has illegal format";
	}
	
	@Test
	public void testSedWithInvalidReplacementOnUnterminatedExpression1() throws SedException {
		String args[] = { "s|0|1g", NUMBER_FILE_PATH };
		stdin = null;
		thrown.expect(SedException.class);
		thrown.expectMessage(UNCOMPLETE_EXPRES);
		sedApp.run(args, stdin, out);
		// "error on sed command - incorrect output with invalid replacement
		// that has illegal format";
	}
	
	@Test
	public void testSedWithInvalidReplacementOnUnterminatedExpression2() throws SedException {
		String args[] = { "s|0| m| |g", NUMBER_FILE_PATH };
		stdin = null;
		thrown.expect(SedException.class);
		thrown.expectMessage(UNCOMPLETE_EXPRES);
		sedApp.run(args, stdin, out);
		// "error on sed command - incorrect output with invalid replacement
		// that has illegal format";
	}
	
	@Test
	public void testSedWithInvalidReplacementOnIllegalRegex() throws SedException {
		String args[] = { "s|[|1|", NUMBER_FILE_PATH };
		stdin = null;
		thrown.expect(SedException.class);
		thrown.expectMessage(INVALID_REGEX);
		sedApp.run(args, stdin, out);
		// "error on sed command - incorrect output with invalid replacement
		// that has illegal regrex";
	}

	@Test
	public void testSedWithGlobalReplacement() throws SedException, FileNotFoundException {
		String args[] = { "s.l.*.g" };
		stdin = new FileInputStream(TWOLINE);
		String expected = TWOLINE_ORIGINAL[0] + "This is a sma** fi*e consists of {1+1+0} *ines."
				+ NEWLINE + "/* Hope this he*ps */ # no new *ine here" + NEWLINE;
		sedApp.run(args, stdin, out);
		assertEquals(expected, out.toString());
	}
	
	@Test
	public void testSedWithDifferentSeparator1() throws SedException, FileNotFoundException {
		String args[] = { "ssls*sg" };
		stdin = new FileInputStream(TWOLINE);
		String expected = TWOLINE_ORIGINAL[0] + "This is a sma** fi*e consists of {1+1+0} *ines."
				+ NEWLINE + "/* Hope this he*ps */ # no new *ine here" + NEWLINE;
		sedApp.run(args, stdin, out);
		assertEquals(expected, out.toString());
	}
	
	@Test
	public void testSedWithDifferentSeparator2() throws SedException, FileNotFoundException {
		String args[] = { "s|l|*|" , TWOLINE};
		stdin = null;
		String expected = TWOLINE_ORIGINAL[0] + 
						"This is a sma*l file consists of {1+1+0} lines." + NEWLINE + 
						"/* Hope this he*ps */ # no new line here" + NEWLINE;
		sedApp.run(args, stdin, out);
		assertEquals(expected, out.toString());
	}
	
	@Test
	public void testSedWithEmptyReplacment() throws SedException, FileNotFoundException {
		String args[] = { "s|l||" };
		stdin = new FileInputStream(TWOLINE);
		String expected = TWOLINE_ORIGINAL[0] + 
				"This is a smal file consists of {1+1+0} lines."+ NEWLINE + 
				"/* Hope this heps */ # no new line here" + NEWLINE;
		sedApp.run(args, stdin, out);
		assertEquals(expected, out.toString());
	}
	
	@Test
	public void testSedWithComplexReplacement1() throws SedException, FileNotFoundException {
		String args[] = { "s|no| *&/s\\$|" };
		stdin = new FileInputStream(TWOLINE);
		String expected = "Hey, good to k *&/s$w <you>!" + NEWLINE + 
				TWOLINE_ORIGINAL[1] + 
				"/* Hope this helps */ #  *&/s$ new line here" + NEWLINE;
		sedApp.run(args, stdin, out);
		assertEquals(expected, out.toString());
	}
	
	@Test
	public void testSedWithComplexReplacement2() throws SedException, FileNotFoundException {
		String args[] = { "s|o|[^]|" };
		stdin = new FileInputStream(TWOLINE);
		String expected = "Hey, g[^]od to know <you>!" + NEWLINE + 
				"This is a small file c[^]nsists of {1+1+0} lines."+ NEWLINE + 
				"/* H[^]pe this helps */ # no new line here" + NEWLINE;
		sedApp.run(args, stdin, out);
		assertEquals(expected, out.toString());
	}
	
	@Test
	public void testSedWithComplexReplacement3() throws SedException, FileNotFoundException {
		String args[] = { "s|o   |% #$%^&|" };
		stdin = new FileInputStream(TWOLINE);
		String expected = TWOLINE_ORIGINAL[0] + 
				TWOLINE_ORIGINAL[1] + 
				TWOLINE_ORIGINAL[2];
		sedApp.run(args, stdin, out);
		assertEquals(expected, out.toString());
	}
	
	@Test
	public void testSedWithEmtpyRegexpAndEmptyReplacement() throws SedException, FileNotFoundException {
		String args[] = { "s|||" };
		stdin = new FileInputStream(TWOLINE);
		thrown.expect(SedException.class);
		thrown.expectMessage(INVALID_REGEX);
		sedApp.run(args, stdin, out);
		// String msg =
		// "error on sed command - fail to throw exception with empty regular
		// expression and empty replacement";
	}

	@Test
	public void testSedWithEmtpyRegexp() throws SedException, FileNotFoundException {
		String args[] = { "s||m|g" };
		stdin = new FileInputStream(TWOLINE);
		thrown.expect(SedException.class);
		thrown.expectMessage(INVALID_REGEX);
		sedApp.run(args, stdin, out);
		// String msg =
		// "error on sed command - fail to throw exception with empty regular
		// expression";
	}
	
	@Test
	public void testSedWithComplexRegexp1() throws SedException {
		String args[] = { "s|^This|r|g" , TWOLINE};
		stdin = null;
		String expected = TWOLINE_ORIGINAL[0] + 
				"r is a small file consists of {1+1+0} lines."+ NEWLINE + 
				TWOLINE_ORIGINAL[2];
		sedApp.run(args, stdin, out);
		assertEquals(expected, out.toString());
	}

	@Test
	public void testSedWithComplexRegexp2() throws SedException {
		String args[] = { "s|o{2,3}d*|r|" , TWOLINE};
		stdin = null;
		String expected = "Hey, gr to know <you>!" + NEWLINE + 
				TWOLINE_ORIGINAL[1] + 
				TWOLINE_ORIGINAL[2];
		sedApp.run(args, stdin, out);
		assertEquals(expected, out.toString());
	}
	
	@Test
	public void testSedWithComplexRegexp3() throws SedException {
		String args[] = { "s|[^a-zA-Z ]|-|g" , TWOLINE};
		stdin = null;
		String expected = "Hey- good to know -you--" + NEWLINE + 
				"This is a small file consists of ------- lines-"+ NEWLINE + 
				"-- Hope this helps -- - no new line here" + NEWLINE;
		sedApp.run(args, stdin, out);
		assertEquals(expected, out.toString());
	}
	
	@Test
	public void testSedWithComplexRegexp4() throws SedException {
		String args[] = { "s|.||g" , TWOLINE};
		stdin = null;
		String expected = NEWLINE + NEWLINE + NEWLINE;
		sedApp.run(args, stdin, out);
		assertEquals(expected, out.toString());
	}
	
	@Test
	public void testReplaceFirstSubStringInFile() throws SedException {
		stdin = null;
		String cmd = "s|o{2,3}d*|r| " + TWOLINE;
		String output = sedApp.replaceFirstSubStringInFile(cmd);
		String expected = "Hey, gr to know <you>!" + NEWLINE + 
				TWOLINE_ORIGINAL[1] + 
				TWOLINE_ORIGINAL[2];
		assertEquals(expected, output);
	}
	
	@Test
	public void testReplaceAllSubstringsInFile() throws SedException {
		String cmd = "s|.||g " + TWOLINE;
		stdin = null;
		String expected = NEWLINE + NEWLINE + NEWLINE;
		String output = sedApp.replaceAllSubstringsInFile(cmd);
		assertEquals(expected, output);
	}
	
	@Test
	public void testReplaceFirstSubStringFromStdin() throws SedException, FileNotFoundException {
		String cmd = "s|o{2,3}d*|r|";
		stdin = new FileInputStream(TWOLINE);
		String output = sedApp.replaceFirstSubStringFromStdin(cmd, stdin);
		String expected = "Hey, gr to know <you>!" + NEWLINE + 
				TWOLINE_ORIGINAL[1] + 
				TWOLINE_ORIGINAL[2];
		assertEquals(expected, output);
	}
	
	@Test
	public void testReplaceAllSubstringsInStdin() throws SedException, FileNotFoundException {
		String cmd = "s|.||g";
		stdin = new FileInputStream(TWOLINE);
		String output = sedApp.replaceAllSubstringsInStdin(cmd, stdin);
		String expected = NEWLINE + NEWLINE + NEWLINE;
		assertEquals(expected, output);
	}
//	
//  Unable to match quotes
//	@Test 
//	public void testReplaceFirstSubStringInFile() throws SedException {
//		String cmd = "\"s|o||\"  " + TWOLINE;
//		String expected = "Hey, god to know <you>!" + NEWLINE + "This is a small file cnsists of {1+1+0} lines."
//				+ NEWLINE + "/* Hpe this helps */ # no new line here" + NEWLINE;
//
//		String msg = "error on sed command - incorrect output with method replaceFirstSubStringInFile";
//		assertEquals(expected, sedApp.replaceFirstSubStringInFile(cmd));
//	}
//
//	@Test 
//	public void testReplaceAllSubstringsInFile() throws SedException {
//		String cmd = "sed \"s|o||g\"  " + TWOLINE;
//		String expected = "Hey, gd t knw <yu>!" + NEWLINE + "This is a small file cnsists f {1+1+0} lines." + NEWLINE
//				+ "/* Hpe this helps */ # n new line here" + NEWLINE;
//
//		String msg = "error on sed command - incorrect output with method replaceAllSubstringsInFile";
//		assertEquals(msg, expected, sedApp.replaceAllSubstringsInFile(cmd));
//	}

}
