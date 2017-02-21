package sg.edu.nus.comp.cs4218.impl;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;

import org.junit.Before;
import org.junit.Test;

import sg.edu.nus.comp.cs4218.Shell;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;

public class ShellTest {

	Shell shell;
	String output;
	ByteArrayOutputStream outputStream;
	String lineSeparator = System.getProperty("line.separator");

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
		
		assertEquals("cat" + lineSeparator, output);
	}
	
	@Test
	public void testCommandSubstitution() throws AbstractApplicationException, ShellException {
		String cmd = "echo `echo cat`";
		
		shell.parseAndEvaluate(cmd, outputStream);
		
		output = outputStream.toString();
		
		assertEquals("cat" + lineSeparator, output);
	}
	
	@Test
	public void testCommandSubstitutionWithPipe() throws AbstractApplicationException, ShellException {
		String cmd = "echo `echo cat | wc -m`";
		
		shell.parseAndEvaluate(cmd, outputStream);
		
		output = outputStream.toString();
		
		assertEquals("4" + lineSeparator, output);
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
