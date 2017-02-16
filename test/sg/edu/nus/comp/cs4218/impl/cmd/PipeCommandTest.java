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
	public void test() throws AbstractApplicationException, ShellException {
		String cmd = "echo \"cd\" | grep c";
		
		shell.parseAndEvaluate(cmd, outputStream);
		
		output = outputStream.toString();
		
		assertEquals("cd", output);
		

	}

}
