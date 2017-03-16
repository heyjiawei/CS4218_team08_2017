package sg.edu.nus.comp.cs4218.impl;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.Shell;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;

public class IntegrationTest {

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
	public void testCommandSubstitutionAndPipe() throws AbstractApplicationException, ShellException {
		String cmd = "echo `echo cat | wc -m`";
		shell.parseAndEvaluate(cmd, outputStream);
		output = outputStream.toString();
		assertEquals("       4" + newLine, output);
	}
	
	@Test
	public void testPipeEchoWc() throws AbstractApplicationException, ShellException {
		String cmd = "echo \"cd\" | wc";
		output = shell.pipeTwoCommands(cmd);
		assertEquals("       3       1       1" + newLine, output);
	}

	@Test
	public void testPipeEchoHeadTail() throws AbstractApplicationException, ShellException {
		String cmd = "echo \"cd\" | head | tail";
		output = shell.pipeMultipleCommands(cmd);
		assertEquals("cd", output);
	}

	@Test
	public void testPwdAfterCd() throws ShellException, AbstractApplicationException {
		String currentDirectory = Environment.currentDirectory;
		Path currentPath = Paths.get(currentDirectory);
		Path parentPath = currentPath.getParent();
		String cdCommand = "cd ..";
		String pwdCommand = "pwd";
		shell.parseAndEvaluate(cdCommand, outputStream);
		shell.parseAndEvaluate(pwdCommand, outputStream);
		output = outputStream.toString();
		assertEquals(parentPath.toString() + newLine, output);
	}
}
