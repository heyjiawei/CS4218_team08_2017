package sg.edu.nus.comp.cs4218.impl;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;

import org.junit.*;
import org.junit.rules.ExpectedException;

import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.Shell;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.CatException;
import sg.edu.nus.comp.cs4218.exception.ShellException;

@SuppressWarnings("PMD.LongVariable")
public class IntegrationTest {
	private static final String DATE = "date";
	private static final String PWD = "pwd";
	private static final String CAT = "cat ";
	private final String FILE_SEPARATOR = File.separator;
	private ByteArrayOutputStream outputStream;
	private final String testInputFilesPath = "test_inputs" + FILE_SEPARATOR;
	private final String catTestFilesPath = testInputFilesPath + "cat" + FILE_SEPARATOR;
	private final String sedTestFilePath = testInputFilesPath + "sed" + FILE_SEPARATOR + "two-lines.txt";
	private final String tailTestFilesPath = testInputFilesPath + "tail" + FILE_SEPARATOR;
	private final String headTestFilesPath = testInputFilesPath + "head" + FILE_SEPARATOR;
	private final String sortTestFilesPath = testInputFilesPath + "sort" + FILE_SEPARATOR;
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
		String cmd = CAT + sedTestFilePath + " | wc";
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
		String cmd = CAT + catTestFilesPath + "lorem_ipsum_short*";
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
	
	@Test
	public void testPipeCalGrep() {
		String cmd = "cal | grep \\d";
		output = shell.pipeTwoCommands(cmd);
		String expected = "     March 2017" + newLine +
						"          1  2  3  4" + newLine +
						" 5  6  7  8  9 10 11" + newLine +
						"12 13 14 15 16 17 18" + newLine +
						"19 20 21 22 23 24 25" + newLine +
						"26 27 28 29 30 31" + newLine;
		assertEquals(expected, output);
	}
	
	@Test
	public void testPipeCatGrep() {
		String cmd = CAT+ sedTestFilePath + " | grep Hope";
		output = shell.pipeTwoCommands(cmd);
		String expected = "/* Hope this helps */ # no new line here" + newLine;
		assertEquals(expected, output);
	}
	
	@Test
	public void testPipeDateGrep() {
		String cmd = "date | grep :";
		output = shell.pipeTwoCommands(cmd);
		Calendar cal = Calendar.getInstance();
		assertEquals(cal.getTime() + newLine, output);
	}

	@Test
	public void testPipeCatToTail() throws IOException {
		String inputFilePathString = tailTestFilesPath + "lorem_ipsum_16_lines.txt";
		String expectedOutputFilePathString = tailTestFilesPath +
				"last_5_lines_from_lorem_ipsum_16_lines.txt";
		String cmd = CAT + inputFilePathString + " | tail -n 5";
		output = shell.pipeTwoCommands(cmd);
		String expected = convertFileToString(expectedOutputFilePathString);
		assertEquals(expected, output);
	}

	@Test
	public void testPipeCatToHead() throws IOException {
		String inputFilePathString = headTestFilesPath + "lorem_ipsum_16_lines.txt";
		String expectedOutputFilePathString = headTestFilesPath +
				"first_5_lines_from_lorem_ipsum_16_lines.txt";
		String cmd = CAT + inputFilePathString + " | head -n 5";
		output = shell.pipeTwoCommands(cmd);
		String expected = convertFileToString(expectedOutputFilePathString);
		assertEquals(expected, output);
	}

	@Test
	public void testPipePwdToCat() throws IOException {
		String cmd = "pwd | cat";
		output = shell.pipeTwoCommands(cmd);
		assertEquals(Environment.currentDirectory + newLine, output);
	}

	@Test
	public void testPipeEchoToCat() throws IOException {
		String testString = "test string!";
		String cmd = "echo " + testString + "| cat";
		output = shell.pipeTwoCommands(cmd);
		assertEquals(testString + newLine, output);
	}

	@Test
	public void testPipeSortToCat() throws IOException {
		String inputFilePathString = sortTestFilesPath + "all.txt";
		String expectedOutputFilePathString = sortTestFilesPath +
				"all_sorted.txt";
		String cmd = "sort " + inputFilePathString + " | cat";
		output = shell.pipeTwoCommands(cmd);
		String expected = convertFileToString(expectedOutputFilePathString);
		assertEquals(expected, output);
	}

	@Test
	public void testPipeDateToCat() throws IOException, AbstractApplicationException,
		ShellException {
		String cmd0 = DATE;
		String cmd1 = "date | cat";
		shell.parseAndEvaluate(cmd0, outputStream);
		String dateResult = outputStream.toString();
		outputStream.reset();
		shell.parseAndEvaluate(cmd1, outputStream);
		String pipedResult = outputStream.toString();
		assertEquals(dateResult, pipedResult);
	}

	@Test
	public void testPipeDateToHead() throws IOException, AbstractApplicationException,
		ShellException {
		String cmd0 = DATE;
		String cmd1 = "date | head -n 1";
		shell.parseAndEvaluate(cmd0, outputStream);
		String dateResult = outputStream.toString();
		outputStream.reset();
		shell.parseAndEvaluate(cmd1, outputStream);
		String pipedResult = outputStream.toString();
		assertEquals(dateResult, pipedResult);
	}

	@Test
	public void testPipePwdToHead() throws IOException, AbstractApplicationException,
		ShellException {
		String cmd0 = PWD;
		String cmd1 = PWD + " | head -n 1";
		shell.parseAndEvaluate(cmd0, outputStream);
		String pwdResult = outputStream.toString();
		outputStream.reset();
		shell.parseAndEvaluate(cmd1, outputStream);
		String pipedResult = outputStream.toString();
		assertEquals(pwdResult, pipedResult + newLine);
	}

	@Test
	public void testPipeDateToTail() throws IOException, AbstractApplicationException,
		ShellException {
		String cmd0 = DATE;
		String cmd1 = "date | tail -n 1";
		shell.parseAndEvaluate(cmd0, outputStream);
		String dateResult = outputStream.toString();
		outputStream.reset();
		shell.parseAndEvaluate(cmd1, outputStream);
		String pipedResult = outputStream.toString();
		assertEquals(dateResult, pipedResult);
	}

	@Test
	public void testPipePwdToTail() throws IOException, AbstractApplicationException,
		ShellException {
		String cmd0 = PWD;
		String cmd1 = PWD + " | tail -n 1";
		shell.parseAndEvaluate(cmd0, outputStream);
		String pwdResult = outputStream.toString();
		outputStream.reset();
		shell.parseAndEvaluate(cmd1, outputStream);
		String pipedResult = outputStream.toString();
		assertEquals(pwdResult, pipedResult + newLine);
	}

	@Test
	public void testPipeDateToWc() throws IOException, AbstractApplicationException,
		ShellException {
		String cmd0 = DATE;
		String cmd1 = "date | wc";
		shell.parseAndEvaluate(cmd0, outputStream);
		String dateResult = outputStream.toString();
		int wordCount = dateResult.split("\\s+").length;
		outputStream.reset();
		shell.parseAndEvaluate(cmd1, outputStream);
		String pipedResult = outputStream.toString();
		assertEquals("       " + dateResult.getBytes().length + "       " + wordCount +
				"       0\n", pipedResult);
	}

	@Test
	public void testCommandSubstitutionEchoSort() throws IOException {
		String inputFilePathString = sortTestFilesPath + "numbers.txt";
		String expectedOutputFilePathString = sortTestFilesPath +
				"numbers_sorted.txt";
		String cmd = "echo `sort " + inputFilePathString + "`";
		output = shell.pipeTwoCommands(cmd);
		String expected = convertFileToString(expectedOutputFilePathString);
		assertEquals(expected + newLine, output);
	}

	@Test
	public void testHeadGrepSed() {
		String cmd = "head " + catTestFilesPath + 
				"lorem_ipsum_separated_by_empty_lines.txt | grep lorem | sed s.lorem.LOREM.g";
		output = shell.pipeTwoCommands(cmd);
		String expected = "Neque porro quisquam est, qui doLOREM ipsum quia "
						+ "dolor sit amet, consectetur, adipisci velit, sed quia "
						+ "non numquam eius modi tempora incidunt ut labore et"
						+ " dolore magnam aliquam quaerat voluptatem." 
						+ newLine 
						+ "Quis autem vel eum iure reprehenderit qui in ea voluptate "
						+ "velit esse quam nihil molestiae consequatur, vel illum qui "
						+ "doLOREM eum fugiat quo voluptas nulla pariatur?"
						+ newLine;
		assertEquals(expected, output);
	}
	
	@Test
	public void testTailGrepWc() {
		String cmd = "tail " + catTestFilesPath + 
				"lorem_ipsum_separated_by_empty_lines.txt | grep \\? | wc -l";
		output = shell.pipeTwoCommands(cmd);
		String expected = "       2" + newLine;
		assertEquals(expected, output);
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
