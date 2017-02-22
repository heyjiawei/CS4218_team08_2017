package sg.edu.nus.comp.cs4218.impl;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import sg.edu.nus.comp.cs4218.Shell;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;

public class ShellTest {
	ByteArrayOutputStream outputStream;
	private Shell shell;
	private String output;
	private final String newLine = System.getProperty("line.separator");

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Before
	public void setUp() throws Exception {
		shell = new ShellImpl();
		outputStream = new ByteArrayOutputStream();
	}
	
	@Test
	public void testBasicShell() throws AbstractApplicationException, ShellException {
		String cmd = "echo cat";
		shell.parseAndEvaluate(cmd, outputStream);
		output = outputStream.toString();
		assertEquals("cat" + newLine, output);
	}
	
	@Test
	public void testCommandSubstitution() throws AbstractApplicationException, ShellException {
		String cmd = "echo `echo cat`";
		output = shell.performCommandSubstitution(cmd);
		assertEquals("cat" + newLine, output);
	}
	
	@Test
	public void testInvalidCommandSubstitution() throws AbstractApplicationException, ShellException {
		String cmd = "echo `echo `echo cat` cat`";
		output = shell.performCommandSubstitution(cmd);
		assertEquals("shell: Invalid syntax encountered near echo cat` cat` ", output);
		
	}
	
	@Test
	public void testCommandSubstitutionWithPipe() throws AbstractApplicationException, ShellException {
		String cmd = "echo `echo cat | wc -m`";
		shell.parseAndEvaluate(cmd, outputStream);
		output = outputStream.toString();
		assertEquals("4" + newLine, output);
	}
	
	@Test
	public void testPipeTwoCommands() throws AbstractApplicationException, ShellException {
		String cmd = "echo \"cd\" | wc";
		output = shell.pipeTwoCommands(cmd);
		assertEquals("3 1 1" + newLine, output);
	}

	@Test
	public void testPipeMultipleCommands() throws AbstractApplicationException, ShellException {
		String cmd = "echo \"cd\" | head | tail";
		output = shell.pipeMultipleCommands(cmd);
		assertEquals("cd", output);
	}

	@Test
	public void testMultiplePipeOutput() throws AbstractApplicationException, ShellException {
		String cmd = "echo hi | echo bye | echo yes";
		output = shell.pipeMultipleCommands(cmd);
		assertEquals("yes" + newLine, output);
	}

	@Test
	public void testPipeWithException() throws AbstractApplicationException, ShellException {
		String cmd = "head '   ' | echo unreachable";
		output = shell.pipeWithException(cmd);
		assertEquals("head: Could not read file", output);
	}

	@Test
	public void testProcessBQ() throws AbstractApplicationException, ShellException {
		String[] argsArray = { "echo", "'echo'", "`echo hi`", "\"abc `echo def`\"", "'abc `echo def`'" };
		String[] correctOutput = { "echo", "echo", "hi", "abc def", "abc `echo def`" };

		String[] processed = ShellImpl.processBQ(argsArray);

		assertArrayEquals(correctOutput, processed);
	}
}
