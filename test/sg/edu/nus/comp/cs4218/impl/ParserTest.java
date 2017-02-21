package sg.edu.nus.comp.cs4218.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

import org.junit.Rule;
import org.junit.Test;

import org.junit.rules.ExpectedException;
import sg.edu.nus.comp.cs4218.exception.ShellException;

import java.util.Vector;

public class ParserTest {

	private final Parser parser = new Parser();

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testParseInvalidPipeSyntax() throws ShellException {
		String cmd = "echo \"cd\" | | |";

		thrown.expect(ShellException.class);
		thrown.expectMessage(ShellImpl.EXP_SYNTAX);
		parser.parse(cmd);
	}

	@Test
	public void testParseInvalidQuoteSyntax() throws ShellException {
		String cmd = "echo \"cd";

		thrown.expect(ShellException.class);
		thrown.expectMessage(ShellImpl.EXP_SYNTAX);
		parser.parse(cmd);
	}

	@Test
	public void testParseCallCommand() throws ShellException {
		String cmd = "echo";

		String[][] sequences = parser.parse(cmd);
		assertEquals(1, sequences.length);
	}

	@Test
	public void testParseNonKeyword() throws ShellException {
		String cmd = "echo";
		String[] callCommands = { cmd };

		String[][] sequences = parser.parse(cmd);
		assertArrayEquals(callCommands, sequences[0]);
	}

	@Test
	public void testParseSingleQuotes() throws ShellException {
		String cmd = "echo 'a`'";
		String[] callCommands = { cmd };

		String[][] sequences = parser.parse(cmd);
		assertArrayEquals(callCommands, sequences[0]);
	}

	@Test
	public void testParseDoubleQuotes() throws ShellException {
		String cmd = "echo \"a\"";
		String[] callCommands = { cmd };

		String[][] sequences = parser.parse(cmd);
		assertArrayEquals(callCommands, sequences[0]);
	}

	@Test
	public void testParseBackQuotes() throws ShellException {
		String cmd = "echo `a`";
		String[] callCommands = { cmd };

		String[][] sequences = parser.parse(cmd);
		assertArrayEquals(callCommands, sequences[0]);
	}

	@Test
	public void testParseBackQuotesInDoubleQuotes() throws ShellException {
		String cmd = "echo \"`echo a`\"";
		String[] callCommands = { cmd };

		String[][] sequences = parser.parse(cmd);
		assertArrayEquals(callCommands, sequences[0]);
	}

	@Test
	public void testParseUnclosedBackQuotesInDoubleQuotes() throws ShellException {
		String cmd = "echo \"echo a`\"";

		thrown.expect(ShellException.class);
		thrown.expectMessage(ShellImpl.EXP_SYNTAX);
		parser.parse(cmd);
	}

	@Test
	public void testParseBackQuotesInSingleQuotes() throws ShellException {
		String cmd = "echo 'echo `a`'";
		String[] callCommands = { cmd };

		String[][] sequences = parser.parse(cmd);
		assertArrayEquals(callCommands, sequences[0]);
	}

	@Test
	public void testParseMultipleArguments() throws ShellException {
		String cmd = "echo 'echo `a`' b \"c\" `d` ";
		String[] callCommands = { cmd.trim() };

		String[][] sequences = parser.parse(cmd);
		assertArrayEquals(callCommands, sequences[0]);
	}

	@Test
	public void testParsePipe() throws ShellException {
		String cmd = "echo hi | echo bye | echo yes";
		String[] callCommands = { "echo hi", "echo bye", "echo yes" };

		String[][] sequences = parser.parse(cmd);
		assertArrayEquals(callCommands, sequences[0]);
	}

	@Test
	public void testParsePipeInQuotes() throws ShellException {
		String cmd = "echo \"bye|bye\"";
		String[] callCommands = { "echo \"bye|bye\"" };

		String[][] sequences = parser.parse(cmd);
		assertArrayEquals(callCommands, sequences[0]);
	}

	@Test
	public void testParseSemicolon() throws ShellException {
		String cmd = "echo hi ; echo bye ; echo yes";
		String[][] correctSequences = { { "echo hi" }, { "echo bye" }, { "echo yes" } };

		String[][] sequences = parser.parse(cmd);

		assertEquals(correctSequences.length, sequences.length);
		for (int index = 0; index < correctSequences.length; index++) {
			assertArrayEquals(correctSequences[index], sequences[index]);
		}
	}

	@Test
	public void testParseSemicolonInQuotes() throws ShellException {
		String cmd = "echo \"bye;bye\"";
		String[] callCommands = { "echo \"bye;bye\"" };

		String[][] sequences = parser.parse(cmd);
		assertArrayEquals(callCommands, sequences[0]);
	}

	@Test
	public void testParseSemicolonWithPipes() throws ShellException {
		String cmd = "echo hi ; echo bye | echo no; echo yes";
		String[][] correctSequences = { { "echo hi" }, { "echo bye", "echo no" }, { "echo yes" } };

		String[][] sequences = parser.parse(cmd);

		assertEquals(correctSequences.length, sequences.length);
		for (int index = 0; index < correctSequences.length; index++) {
			assertArrayEquals(correctSequences[index], sequences[index]);
		}
	}

	@Test
	public void testParseCallCommandArguments() throws ShellException {
		String cmd = "echo a 'b' \"c\" `d` \"`echo 'e'`\"";
		String[] correctSequences = { "echo", "a", "'b'", "\"c\"", "`d`", "\"`echo 'e'`\"" };

		Vector<String> parsed = parser.parseCallCommand(cmd);
		String[] sequences = parsed.toArray(new String[parsed.size()]);

		assertArrayEquals(correctSequences, sequences);
	}

	@Test
	public void testParseCallCommandEmptyString() throws ShellException {
		String cmd = "";
		String[] correctSequences = { };

		Vector<String> parsed = parser.parseCallCommand(cmd);
		String[] sequences = parsed.toArray(new String[parsed.size()]);

		assertArrayEquals(correctSequences, sequences);
	}

	@Test
	public void testParseCallCommandInvalidArguments() throws ShellException {
		String cmd = "echo -1";

		thrown.expect(ShellException.class);
		thrown.expectMessage(ShellImpl.EXP_SYNTAX);
		parser.parseCallCommand(cmd);
	}

	@Test
	public void testParseCallCommandArgumentsNoWhitespace() throws ShellException {
		String cmd = "echo a'b'\"c\"`d`\"`echo 'e'`\"";

		thrown.expect(ShellException.class);
		thrown.expectMessage(ShellImpl.EXP_SYNTAX);
		parser.parseCallCommand(cmd);
	}

	@Test
	public void testParseCallCommandRedirection() throws ShellException {
		String cmd = "grep \"Interesting String\" < text1.txt > result.txt";
		String[] correctSequences = { "grep", "\"Interesting String\"", "<", "text1.txt", ">", "result.txt" };

		Vector<String> parsed = parser.parseCallCommand(cmd);
		String[] sequences = parsed.toArray(new String[parsed.size()]);

		assertArrayEquals(correctSequences, sequences);
	}

	@Test
	public void testParseCallCommandRedirectionNoWhitespace() throws ShellException {
		String cmd = "grep \"Interesting String\"<text1.txt>result.txt";

		thrown.expect(ShellException.class);
		thrown.expectMessage(ShellImpl.EXP_SYNTAX);
		parser.parseCallCommand(cmd);
	}

	@Test
	public void testParseCallCommandRedirectionNoInputFile() throws ShellException {
		String cmd = "grep \"Interesting String\" < ";

		thrown.expect(ShellException.class);
		thrown.expectMessage(ShellImpl.EXP_SYNTAX);
		parser.parseCallCommand(cmd);
	}

	@Test
	public void testParseCallCommandRedirectionNoOutputFile() throws ShellException {
		String cmd = "echo \"Interesting String\" > ";

		thrown.expect(ShellException.class);
		thrown.expectMessage(ShellImpl.EXP_SYNTAX);
		parser.parseCallCommand(cmd);
	}

	@Test
	public void testParseCallCommandRedirectionMultipleInputFile() throws ShellException {
		String cmd = "grep \"Interesting String\" < result.txt < result2.txt";

		thrown.expect(ShellException.class);
		thrown.expectMessage(ShellImpl.EXP_MULTIPLE_FILE_REDIR);
		parser.parseCallCommand(cmd);
	}

	@Test
	public void testParseCallCommandRedirectionMultipleOutputFile() throws ShellException {
		String cmd = "echo \"Interesting String\" > result.txt > result2.txt";

		thrown.expect(ShellException.class);
		thrown.expectMessage(ShellImpl.EXP_MULTIPLE_FILE_REDIR);
		parser.parseCallCommand(cmd);
	}

	@Test
	public void testParseCallCommandRedirectionBeforeApplication() throws ShellException {
		String cmd = "> result.txt < text1.txt grep \"Interesting String\"";
		String[] correctSequences = { ">", "result.txt", "<", "text1.txt", "grep", "\"Interesting String\"" };

		Vector<String> parsed = parser.parseCallCommand(cmd);
		String[] sequences = parsed.toArray(new String[parsed.size()]);

		assertArrayEquals(correctSequences, sequences);
	}
}
