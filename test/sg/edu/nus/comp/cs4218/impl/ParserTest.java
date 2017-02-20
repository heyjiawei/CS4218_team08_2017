package sg.edu.nus.comp.cs4218.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

import org.junit.Rule;
import org.junit.Test;

import org.junit.rules.ExpectedException;
import sg.edu.nus.comp.cs4218.exception.ShellException;

public class ParserTest {

	private final String newLine = System.getProperty("line.separator");

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testParseInvalidSyntax() throws ShellException {
		String cmd = "echo \"cd\" | | |";

		thrown.expect(ShellException.class);
		thrown.expectMessage(ShellImpl.EXP_SYNTAX);
		Parser.parse(cmd);
	}

	@Test
	public void testParseCallCommand() throws ShellException {
		String cmd = "echo";

		String[][] sequences = Parser.parse(cmd);
		assertEquals(1, sequences.length);
	}

	@Test
	public void testParseNonKeyword() throws ShellException {
		String cmd = "echo";
		String[] callCommands = { cmd };

		String[][] sequences = Parser.parse(cmd);
		assertArrayEquals(callCommands, sequences[0]);
	}

	@Test
	public void testParseSingleQuotes() throws ShellException {
		String cmd = "echo 'a'";
		String[] callCommands = { cmd };

		String[][] sequences = Parser.parse(cmd);
		assertArrayEquals(callCommands, sequences[0]);
	}

	@Test
	public void testParseDoubleQuotes() throws ShellException {
		String cmd = "echo \"a\"";
		String[] callCommands = { cmd };

		String[][] sequences = Parser.parse(cmd);
		assertArrayEquals(callCommands, sequences[0]);
	}

	@Test
	public void testParseBackQuotes() throws ShellException {
		String cmd = "echo `a`";
		String[] callCommands = { cmd };

		String[][] sequences = Parser.parse(cmd);
		assertArrayEquals(callCommands, sequences[0]);
	}

	@Test
	public void testParseBackQuotesInDoubleQuotes() throws ShellException {
		String cmd = "echo \"echo `a`\"";
		String[] callCommands = { cmd };

		String[][] sequences = Parser.parse(cmd);
		assertArrayEquals(callCommands, sequences[0]);
	}

	@Test
	public void testParseBackQuotesInSingleQuotes() throws ShellException {
		String cmd = "echo 'echo `a`'";
		String[] callCommands = { cmd };

		String[][] sequences = Parser.parse(cmd);
		assertArrayEquals(callCommands, sequences[0]);
	}

	@Test
	public void testParseMultipleArguments() throws ShellException {
		String cmd = "echo 'echo `a`' b \"c\" `d` ";
		String[] callCommands = { cmd.trim() };

		String[][] sequences = Parser.parse(cmd);
		assertArrayEquals(callCommands, sequences[0]);
	}

	@Test
	public void testParsePipe() throws ShellException {
		String cmd = "echo hi | echo bye | echo yes";
		String[] callCommands = { "echo hi", "echo bye", "echo yes" };

		String[][] sequences = Parser.parse(cmd);
		assertArrayEquals(callCommands, sequences[0]);
	}

	@Test
	public void testParsePipeInQuotes() throws ShellException {
		String cmd = "echo \"bye|bye\"";
		String[] callCommands = { "echo \"bye|bye\"" };

		String[][] sequences = Parser.parse(cmd);
		assertArrayEquals(callCommands, sequences[0]);
	}

	@Test
	public void testParseSemicolon() throws ShellException {
		String cmd = "echo hi ; echo bye ; echo yes";
		String[][] correctSequences = { { "echo hi" }, { "echo bye" }, { "echo yes" } };

		String[][] sequences = Parser.parse(cmd);

		assertEquals(correctSequences.length, sequences.length);
		for (int index = 0; index < correctSequences.length; index++) {
			assertArrayEquals(correctSequences[index], sequences[index]);
		}
	}

	@Test
	public void testParseSemicolonInQuotes() throws ShellException {
		String cmd = "echo \"bye;bye\"";
		String[] callCommands = { "echo \"bye;bye\"" };

		String[][] sequences = Parser.parse(cmd);
		assertArrayEquals(callCommands, sequences[0]);
	}

	@Test
	public void testParseSemicolonWithPipes() throws ShellException {
		String cmd = "echo hi ; echo bye | echo no; echo yes";
		String[][] correctSequences = { { "echo hi" }, { "echo bye", "echo no" }, { "echo yes" } };

		String[][] sequences = Parser.parse(cmd);

		assertEquals(correctSequences.length, sequences.length);
		for (int index = 0; index < correctSequences.length; index++) {
			assertArrayEquals(correctSequences[index], sequences[index]);
		}
	}
}
