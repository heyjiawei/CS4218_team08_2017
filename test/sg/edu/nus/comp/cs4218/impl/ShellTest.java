package sg.edu.nus.comp.cs4218.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;

public class ShellTest {

	ShellImpl shell;
	String output;

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

}
