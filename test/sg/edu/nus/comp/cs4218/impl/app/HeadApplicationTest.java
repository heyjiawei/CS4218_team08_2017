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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import sg.edu.nus.comp.cs4218.exception.HeadException;

public class HeadApplicationTest {

	private InputStream inputStream;
	private ByteArrayOutputStream outputStream;
	private HeadApplication headApplication;
	private final String testFilesPath = "test_inputs/head/";

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
	public void testThrowWhenNullArgumentGiven() throws HeadException {
		String[] args = {null};
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
	
	@Test(expected = HeadException.class)
	public void testThrowWhenSpecifiedOutputLineCountInvalid() throws HeadException {
		String inputFilePathString = testFilesPath + "lorem_ipsum_16_lines.txt";
		String[] args = {"-5", inputFilePathString};
		inputStream = null;
		outputStream = new ByteArrayOutputStream();
		headApplication.run(args, inputStream, outputStream);
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
		String testString = "test string\n\n\n string test";
		String[] args = {};
		inputStream = new ByteArrayInputStream(testString.getBytes());;
		outputStream = new ByteArrayOutputStream();
		headApplication.run(args, inputStream, outputStream);
		assertEquals(testString, outputStream.toString());
	}
	
	@Test
	public void testInputFromStdInWithMoreLinesThanDefaultOutputLineCount()
			throws HeadException, IOException {
		String testString = "test string\n\n\n\n\n\n\n\n\n\n\n\n\nstring test";
		String expectedOutputString = "test string\n\n\n\n\n\n\n\n\n";
		String[] args = {};
		inputStream = new ByteArrayInputStream(testString.getBytes());;
		outputStream = new ByteArrayOutputStream();
		headApplication.run(args, inputStream, outputStream);
		assertEquals(expectedOutputString, outputStream.toString());
	}
	
	public void testInputFromStdInWithMoreLinesThanSpecifiedOutputLineCount()
			throws HeadException, IOException {
		String testString = "test string\n\n\n\n\n\n string test";
		String expectedOutputString = "test string\n\n\n\n\n";
		String[] args = {"6"};
		inputStream = new ByteArrayInputStream(testString.getBytes());;
		outputStream = new ByteArrayOutputStream();
		headApplication.run(args, inputStream, outputStream);
		assertEquals(expectedOutputString, outputStream.toString());
	}
	
	public void testInputFromStdInWithLessLinesThanSpecifiedOutputLineCount()
			throws HeadException, IOException {
		String testString = "test string\n\n\n string test";
		String[] args = {"20"};
		inputStream = new ByteArrayInputStream(testString.getBytes());;
		outputStream = new ByteArrayOutputStream();
		headApplication.run(args, inputStream, outputStream);
		assertEquals(testString, outputStream.toString());
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
