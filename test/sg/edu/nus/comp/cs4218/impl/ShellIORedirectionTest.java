package sg.edu.nus.comp.cs4218.impl;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sg.edu.nus.comp.cs4218.Shell;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;

public class ShellIORedirectionTest {
	ByteArrayOutputStream outputStream;
	private Shell shell;
	private String output;
	private final String newLine = System.getProperty("line.separator");
	
	private static final String testFilePath = "test_ioredirection/";
	private static final String testFileInput = "test_ioredirection/input.txt";
	private static final String testFileOutput = "test_ioredirection/output.txt";

	static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
	
	@Before
	public void setUp() throws Exception {
		shell = new ShellImpl();
		outputStream = new ByteArrayOutputStream();
		Files.createDirectories(Paths.get(testFilePath));
		try(PrintWriter out = new PrintWriter(testFileInput)) {
		    out.println("test");
		}
	}
	
	@After
	public void tearDown() throws Exception {
		File tempOutputFile = new File(testFileOutput);
		tempOutputFile.delete();
		File tempInputFile = new File(testFileInput);
		tempInputFile.delete();
		File tempFileDir = new File(testFilePath);
		tempFileDir.delete();
	}
	
	@Test
	public void testShellIORedirectionOutput() throws AbstractApplicationException, ShellException, IOException {
		String cmd = "echo test"  + " > " + testFileOutput;
		
		shell.parseAndEvaluate(cmd, outputStream);
		
		output = readFile(testFileOutput, Charset.defaultCharset());
		
		assertEquals("test" + newLine, output);
	}
	
	@Test
	public void testShellIORedirectionInput() throws AbstractApplicationException, ShellException {
		String cmd = "echo"  + " < " + testFileInput;
		
		shell.parseAndEvaluate(cmd, outputStream);
		
		output = outputStream.toString();
		
		assertEquals("test" + newLine, output);
	}
}
