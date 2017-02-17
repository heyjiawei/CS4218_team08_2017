package sg.edu.nus.comp.cs4218.impl.cmd;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;

public class PipeCommandTest {

	ShellImpl shell;
	BufferedReader bReader;
	ByteArrayOutputStream outputStream;
	String output;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		shell = new ShellImpl();
		outputStream = new ByteArrayOutputStream();

	}

	@After
	public void tearDown() throws Exception {
	}

	
	@Test
	public void testPipeOne() throws AbstractApplicationException, ShellException {
		String cmd = "echo \"cd\" | wc";
		
		shell.parseAndEvaluate(cmd, outputStream);
		
		output = outputStream.toString();
		
		assertEquals("3 1 1", output);
	}
	
	@Test
	public void testPipeTwo() throws AbstractApplicationException, ShellException {
		String cmd = "echo \"cd\" | head | tail";
		
		shell.parseAndEvaluate(cmd, outputStream);
		
		output = outputStream.toString();
		
		assertEquals("cd\n", output);
	}
	
	@Test
	public void testSplitCommand() throws AbstractApplicationException, ShellException {
		String cmd = "echo \"cd\" | grep c";
		PipeCommand pipeCommand = new PipeCommand(cmd);
		pipeCommand.parse();
		
		assertEquals("echo \"cd\"", pipeCommand.firstSequence);
		assertEquals("grep c", pipeCommand.restSequence);
		
		
		cmd = "echo \"test\" | cat | cat";
		pipeCommand = new PipeCommand(cmd);
		pipeCommand.parse();
		
		assertEquals("echo \"test\"", pipeCommand.firstSequence);
		assertEquals("cat | cat", pipeCommand.restSequence);

	}
	
	@Test
	public void testFindPipeOperatorPos() throws AbstractApplicationException, ShellException {
		String cmd = "echo \"cd\" | grep c";
		int firstPipeOpPos = PipeCommand.findFirstPipeOperatorPosition(cmd);
		assertEquals(10, firstPipeOpPos);
		
		cmd = "echo \"c|d\" | grep c";
		firstPipeOpPos = PipeCommand.findFirstPipeOperatorPosition(cmd);
		assertEquals(11, firstPipeOpPos);
		
		cmd = "echo \"c|d\"";
		firstPipeOpPos = PipeCommand.findFirstPipeOperatorPosition(cmd);
		assertEquals(-1, firstPipeOpPos);
		
		cmd = "echo 'c|d' | grep c";
		firstPipeOpPos = PipeCommand.findFirstPipeOperatorPosition(cmd);
		assertEquals(11, firstPipeOpPos);
		

	}

}
