package sg.edu.nus.comp.cs4218.impl;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;

public class ShellTest {

	private ShellImpl shell;
	private String output;
	private final String newLine = System.getProperty("line.separator");

	@Before
	public void setUp() throws Exception {
		shell = new ShellImpl();
	}

	@Test
	public void testPipeTwoCommands() throws AbstractApplicationException, ShellException {
		String cmd = "echo \"cd\" | wc";

		output = shell.pipeTwoCommands(cmd);

		assertEquals("3 1 1", output);
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
	public void testProcessBQ() throws AbstractApplicationException, ShellException {
		String[] argsArray = { "echo", "'echo'", "`echo hi`", "\"abc `echo def`\"", "'abc `echo def`'" };
		String[] correctOutput = { "echo", "echo", "hi", "abc def", "abc `echo def`" };

		String[] processed = ShellImpl.processBQ(argsArray);

		assertArrayEquals(correctOutput, processed);
	}
}
