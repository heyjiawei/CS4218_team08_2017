package sg.edu.nus.comp.cs4218.impl;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.Shell;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.CatException;
import sg.edu.nus.comp.cs4218.exception.ShellException;

public class IntegrationTest {
	private final String FS = File.separator;
	private ByteArrayOutputStream outputStream;
	private final String catTestFilesPath = "test_inputs" + FS + "cat" + FS;
	private final String sedTestFilePath = "test_inputs" + FS + "sed" + FS + "two-lines.txt";
	private Shell shell;
	private String output;
	private final String newLine = System.getProperty("line.separator");
	private static String initialDirectory;
	

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		initialDirectory = Environment.currentDirectory;
	}
	
	@Before
	public void setUp() throws Exception {
		shell = new ShellImpl();
		outputStream = new ByteArrayOutputStream();
	}
	
	@After
	public void tearDown() throws Exception {
		Environment.currentDirectory = initialDirectory;
	}
	
	@Test
	public void testCommandSubstitutionPipeEchoWc() throws AbstractApplicationException, ShellException {
		String cmd = "echo `echo cat | wc -m`";
		shell.parseAndEvaluate(cmd, outputStream);
		output = outputStream.toString();
		assertEquals("       4" + newLine, output);
	}
	
	@Test
	public void testCommandSubstitutionPwdEcho() throws AbstractApplicationException, ShellException {
		String cmd = "echo `pwd`";
		shell.parseAndEvaluate(cmd, outputStream);
		output = outputStream.toString();
		assertEquals(Environment.currentDirectory + newLine, output);
	}
	
	@Test
	public void testCommandSubstitutionPwdWcNegative() throws AbstractApplicationException, ShellException {
		String cmd = "wc `pwd`";
		shell.parseAndEvaluate(cmd, outputStream);
		output = outputStream.toString();
		assertEquals("wc: " + Environment.currentDirectory + " Invalid file or directory" + newLine, output);
	}
	
	@Test
	public void testPipeSortWc() throws AbstractApplicationException, ShellException {
		String cmd = "sort " + sedTestFilePath + " | wc";
		shell.parseAndEvaluate(cmd, outputStream);
		output = outputStream.toString();
		assertEquals("       113       24       2" + newLine, output);
	}
	
	@Test
	public void testPipeCatWc() throws AbstractApplicationException, ShellException {
		String cmd = "cat "+ sedTestFilePath + " | wc";
		output = shell.pipeTwoCommands(cmd);
		assertEquals("       113       24       2" + newLine, output);
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
	public void testGlobbingWithCat() throws CatException, IOException {
		String firstFilePathString = catTestFilesPath + "lorem_ipsum_short.txt";
		String secondFilePathString = catTestFilesPath + "lorem_ipsum_short_two.txt";
		String cmd = "cat " + catTestFilesPath + "lorem_ipsum_short*";
		output = shell.pipeMultipleCommands(cmd);
		String firstFileString;
		String secondFileString;
		firstFileString = convertFileToString(firstFilePathString);
		secondFileString = convertFileToString(secondFilePathString);
		assertEquals(firstFileString + secondFileString, output);
	}
	
	@Test
	public void testSemicolonPwdAfterCd() throws ShellException, AbstractApplicationException {
		String currentDirectory = Environment.currentDirectory;
		Path currentPath = Paths.get(currentDirectory);
		Path parentPath = currentPath.getParent();
		String command = "cd ..;pwd";
		shell.parseAndEvaluate(command, outputStream);
		output = outputStream.toString();
		assertEquals(parentPath.toString() + newLine, output);
	}
	
	/**
	 * Converts the file found at the given input file path string to
	 * a string.
	 * 
	 * @param filePathString
	 *            The path to the file, represented as a string
	 * @return The string that the file has been converted to
	 * @throws IOException
	 *             If there is an error reading the file
	 */
	private String convertFileToString(String filePathString) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(filePathString));
		return new String(encoded, StandardCharsets.UTF_8);
	}

}
