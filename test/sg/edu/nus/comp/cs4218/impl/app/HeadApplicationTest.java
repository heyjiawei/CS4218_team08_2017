package sg.edu.nus.comp.cs4218.impl.app;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

import sg.edu.nus.comp.cs4218.exception.HeadException;

public class HeadApplicationTest {

	private InputStream inputStream;
	private ByteArrayOutputStream outputStream;
	private HeadApplication headApplication;
	private final String testFilesPath = "test_inputs/head/";
	private final String newLine = System.getProperty("line.separator");

	@Before
	public void setUp() throws Exception {
		headApplication = new HeadApplication();
	}

	@Test(expected = HeadException.class)
	public void testThrowWhenStdOutputNull() throws HeadException {
		String[] args = {"test", "arguments"};
		inputStream = new ByteArrayInputStream("".getBytes());
		outputStream = null;
		headApplication.run(args, inputStream, outputStream);
	}

	@Test(expected = HeadException.class)
	public void testThrowWhenArgsAndStdInputNull() throws HeadException {
		String[] args = null;
		inputStream = null;
		outputStream = new ByteArrayOutputStream();
		headApplication.run(args, inputStream, outputStream);
	}
	
	@Test(expected = HeadException.class)
	public void testThrowWhenNoArgsAndStdInputNull() throws HeadException {
		String[] args = {};
		inputStream = null;
		outputStream = new ByteArrayOutputStream();
		headApplication.run(args, inputStream, outputStream);
	}

	@Test(expected = HeadException.class)
	public void testThrowWhenIllegalFilePathGiven() throws HeadException {
		String[] args = {"illegalFilePath*?"};
		inputStream = null;
		outputStream = new ByteArrayOutputStream();
		headApplication.run(args, inputStream, outputStream);
	}
	
	@Test(expected = HeadException.class)
	public void testThrowWhenFirstArgumentGivenNull() throws HeadException {
		String[] args = {null};
		inputStream = null;
		outputStream = new ByteArrayOutputStream();
		headApplication.run(args, inputStream, outputStream);
	}

	@Test(expected = HeadException.class)
	public void testThrowWhenSecondArgumentGivenNull() throws HeadException {
		String[] args = {"5", null};
		inputStream = null;
		outputStream = new ByteArrayOutputStream();
		headApplication.run(args, inputStream, outputStream);
	}

	@Test(expected = HeadException.class)
	public void testThrowWhenDirectoryGivenAsFilePath() throws HeadException {
		String[] args = {"/"};
		inputStream = null;
		outputStream = new ByteArrayOutputStream();
		headApplication.run(args, inputStream, outputStream);
	}

	@Test(expected = HeadException.class)
	public void testThrowWhenNonexistentFilePathGiven() throws HeadException {
		String nonexistentFilePath = testFilesPath + "nonExistentFilePath";
		String[] args = {nonexistentFilePath};
		inputStream = null;
		outputStream = new ByteArrayOutputStream();
		headApplication.run(args, inputStream, outputStream);
	}

	@Test(expected = HeadException.class)
	public void testThrowWhenSpecifiedOutputLineCountInvalid() throws HeadException {
		String inputFilePathString = testFilesPath + "lorem_ipsum_16_lines.txt";
		String[] args = {"-5", inputFilePathString};
		inputStream = null;
		outputStream = new ByteArrayOutputStream();
		headApplication.run(args, inputStream, outputStream);
	}

	@Test
	public void testValidFilePathWithOneLineWithDefaultOutputLineCount()
			throws HeadException, IOException {
		String inputFilePathString = testFilesPath + "lorem_ipsum_one_line.txt";
		String expectedOutputFileString;
		String[] args = {inputFilePathString};
		inputStream = null;
		outputStream = new ByteArrayOutputStream();
		headApplication.run(args, inputStream, outputStream);
		expectedOutputFileString = 
				convertFileToString(inputFilePathString);
		assertEquals(expectedOutputFileString, outputStream.toString());
	}

	@Test
	public void testValidFilePathWithMultipleLinesWithDefaultOutputLineCount() 
			throws HeadException, IOException {
		String inputFilePathString = testFilesPath + "lorem_ipsum_16_lines.txt";
		String expectedOutputFilePathString = testFilesPath +
				"first_10_lines_from_lorem_ipsum_16_lines.txt";
		String expectedOutputFileString;
		String[] args = {inputFilePathString};
		inputStream = null;
		outputStream = new ByteArrayOutputStream();
		headApplication.run(args, inputStream, outputStream);
		expectedOutputFileString = 
				convertFileToString(expectedOutputFilePathString);
		assertEquals(expectedOutputFileString, outputStream.toString());
	}
	
	@Test
	public void testValidFilePathWithZeroSpecifiedOutputLineCount() 
			throws HeadException, IOException {
		String inputFilePathString = testFilesPath + "lorem_ipsum_16_lines.txt";
		String[] args = {"0", inputFilePathString};
		inputStream = null;
		outputStream = new ByteArrayOutputStream();
		headApplication.run(args, inputStream, outputStream);
		assertEquals("", outputStream.toString());
	}
	
	@Test
	public void testValidFilePathWithLessLinesThanSpecifiedOutputLineCount() 
			throws HeadException, IOException {
		String inputFilePathString = testFilesPath + "lorem_ipsum_16_lines.txt";
		String expectedOutputFileString;
		String[] args = {"20", inputFilePathString};
		inputStream = null;
		outputStream = new ByteArrayOutputStream();
		headApplication.run(args, inputStream, outputStream);
		expectedOutputFileString = 
				convertFileToString(inputFilePathString);
		assertEquals(expectedOutputFileString, outputStream.toString());
	}
	
	@Test
	public void testValidFilePathWithMoreLinesThanSpecifiedOutputLineCount() 
			throws HeadException, IOException {
		String inputFilePathString = testFilesPath + "lorem_ipsum_16_lines.txt";
		String expectedOutputFilePathString = testFilesPath +
				"first_5_lines_from_lorem_ipsum_16_lines.txt";
		String expectedOutputFileString;
		String[] args = {"5", inputFilePathString};
		inputStream = null;
		outputStream = new ByteArrayOutputStream();
		headApplication.run(args, inputStream, outputStream);
		expectedOutputFileString = 
				convertFileToString(expectedOutputFilePathString);
		assertEquals(expectedOutputFileString, outputStream.toString());
	}

	@Test
	public void testValidFilePathWithEqualLinesToSpecifiedOutputLineCount()
			throws HeadException, IOException {
		String inputFilePathString = testFilesPath + "lorem_ipsum_16_lines.txt";
		String expectedOutputFileString;
		String[] args = {"16", inputFilePathString};
		inputStream = null;
		outputStream = new ByteArrayOutputStream();
		headApplication.run(args, inputStream, outputStream);
		expectedOutputFileString =
				convertFileToString(inputFilePathString);
		assertEquals(expectedOutputFileString, outputStream.toString());
	}
	
	@Test
	public void testValidFileSeparatedByEmptyLinesWithDefaultOutputLineCount()
			throws HeadException, IOException {
		String filePathString = testFilesPath +
				"lorem_ipsum_separated_by_empty_lines.txt";
		String fileString;
		String[] args = {filePathString};
		inputStream = null;
		outputStream = new ByteArrayOutputStream();
		headApplication.run(args, inputStream, outputStream);
		fileString = convertFileToString(filePathString);
		assertEquals(fileString, outputStream.toString());
	}
	
	@Test
	public void testValidEmptyFile() throws HeadException, IOException {
		String filePathString = testFilesPath + "empty.txt";
		String fileString;
		String[] args = {filePathString};
		inputStream = null;
		outputStream = new ByteArrayOutputStream();
		headApplication.run(args, inputStream, outputStream);
		fileString = convertFileToString(filePathString);
		assertEquals(fileString, outputStream.toString());
	}
	
	@Test
	public void testInputFromStdInWithLessLinesThanDefaultOutputLineCount()
			throws HeadException, IOException {
		String threeNewLines = new String(new char[3]).replace("\0", newLine);
		String testString = "test string" + threeNewLines + "string test";
		String[] args = {};
		inputStream = new ByteArrayInputStream(testString.getBytes());;
		outputStream = new ByteArrayOutputStream();
		headApplication.run(args, inputStream, outputStream);
		assertEquals(testString, outputStream.toString());
	}

	@Test
	public void testInputFromStdInWithMoreLinesThanDefaultOutputLineCount()
			throws HeadException, IOException {
		String thirteenNewLines = new String(new char[13]).replace("\0", newLine);
		String testString = "test string" + thirteenNewLines + "string test";
		String nineNewLines = new String(new char[9]).replace("\0", newLine);
		String expectedOutputString = "test string" + nineNewLines;
		String[] args = {};
		inputStream = new ByteArrayInputStream(testString.getBytes());;
		outputStream = new ByteArrayOutputStream();
		headApplication.run(args, inputStream, outputStream);
		assertEquals(expectedOutputString, outputStream.toString());
	}

	@Test
	public void testInputFromStdInWithMoreLinesThanSpecifiedOutputLineCount()
			throws HeadException, IOException {
		String sixNewLines = new String(new char[6]).replace("\0", newLine);
		String testString = "test string" + sixNewLines + " string test";
		String fiveNewLines = new String(new char[5]).replace("\0", newLine);
		String expectedOutputString = "test string" + fiveNewLines;
		String[] args = {"6"};
		inputStream = new ByteArrayInputStream(testString.getBytes());;
		outputStream = new ByteArrayOutputStream();
		headApplication.run(args, inputStream, outputStream);
		assertEquals(expectedOutputString, outputStream.toString());
	}

	@Test
	public void testInputFromStdInWithLessLinesThanSpecifiedOutputLineCount()
			throws HeadException, IOException {
		String threeNewLines = new String(new char[3]).replace("\0", newLine);
		String testString = "test string" + threeNewLines + " string test";
		String[] args = {"20"};
		inputStream = new ByteArrayInputStream(testString.getBytes());;
		outputStream = new ByteArrayOutputStream();
		headApplication.run(args, inputStream, outputStream);
		assertEquals(testString, outputStream.toString());
	}

	@Test
	public void testNumberOfLinesFromArgsLinesFromStdIn()
			throws HeadException, IOException {
		String[] args = {"4"};
		String threeNewLines = new String(new char[3]).replace("\0", newLine);
		String expectedOutputString = "test string" + threeNewLines;
		String testString = expectedOutputString + newLine + " string test";
		inputStream = new ByteArrayInputStream(testString.getBytes());;
		outputStream = new ByteArrayOutputStream();
		headApplication.run(args, inputStream, outputStream);
		assertEquals(expectedOutputString, outputStream.toString());
	}

	@Test
	public void testUnicodeInput() throws HeadException, IOException {
		String testString = "ږ ڗ ژАБВञटḥḦḧ✉✌✍讀拏";
		String[] args = {};
		inputStream = new ByteArrayInputStream(testString.getBytes());;
		outputStream = new ByteArrayOutputStream();
		headApplication.run(args, inputStream, outputStream);
		assertEquals(testString, outputStream.toString());
	}
	
	@Test
	public void testOneValidAbsoluteFilePath() throws HeadException, IOException {
		String currentDirectory = System.getProperty("user.dir");
		String filePathString = testFilesPath + "lorem_ipsum_one_line.txt";
		String absoluteFilePathString = currentDirectory +
				File.separator + filePathString;
		String fileString;
		String[] args = {absoluteFilePathString};
		inputStream = null;
		outputStream = new ByteArrayOutputStream();
		headApplication.run(args, inputStream, outputStream);
		fileString = convertFileToString(absoluteFilePathString);
		assertEquals(fileString, outputStream.toString());
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
