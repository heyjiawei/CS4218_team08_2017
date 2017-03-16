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

import sg.edu.nus.comp.cs4218.exception.TailException;

public class TailApplicationTest {

	private InputStream inputStream;
	private ByteArrayOutputStream outputStream;
	private TailApplication tailApplication;
	private final String testFilesPath = "test_inputs/tail/";
	private final String newLine = System.getProperty("line.separator");

	@Before
	public void setUp() throws Exception {
		tailApplication = new TailApplication();
	}

	@Test(expected = TailException.class)
	public void testThrowWhenStdOutputNull() throws TailException {
		String[] args = {"test", "arguments"};
		inputStream = new ByteArrayInputStream("".getBytes());
		outputStream = null;
		tailApplication.run(args, inputStream, outputStream);
	}

	@Test(expected = TailException.class)
	public void testThrowWhenArgsAndStdInputNull() throws TailException {
		String[] args = null;
		inputStream = null;
		outputStream = new ByteArrayOutputStream();
		tailApplication.run(args, inputStream, outputStream);
	}
	
	@Test(expected = TailException.class)
	public void testThrowWhenNoArgsAndStdInputNull() throws TailException {
		String[] args = {};
		inputStream = null;
		outputStream = new ByteArrayOutputStream();
		tailApplication.run(args, inputStream, outputStream);
	}

	@Test(expected = TailException.class)
	public void testThrowWhenIllegalFilePathGiven() throws TailException {
		String[] args = {"illegalFilePath*?"};
		inputStream = null;
		outputStream = new ByteArrayOutputStream();
		tailApplication.run(args, inputStream, outputStream);
	}
	
	@Test(expected = TailException.class)
	public void testThrowWhenNullArgumentGiven() throws TailException {
		String[] args = {null};
		inputStream = null;
		outputStream = new ByteArrayOutputStream();
		tailApplication.run(args, inputStream, outputStream);
	}

	@Test(expected = TailException.class)
	public void testThrowWhenDirectoryGivenAsFilePath() throws TailException {
		String[] args = {"/"};
		inputStream = null;
		outputStream = new ByteArrayOutputStream();
		tailApplication.run(args, inputStream, outputStream);
	}

	@Test(expected = TailException.class)
	public void testThrowWhenNonexistentFilePathGiven() throws TailException {
		String nonexistentFilePath = testFilesPath + "nonExistentFilePath";
		String[] args = {nonexistentFilePath};
		inputStream = null;
		outputStream = new ByteArrayOutputStream();
		tailApplication.run(args, inputStream, outputStream);
	}

	@Test(expected = TailException.class)
	public void testThrowWhenSecondArgumentGivenNull() throws TailException {
		String[] args = {"5", null};
		inputStream = null;
		outputStream = new ByteArrayOutputStream();
		tailApplication.run(args, inputStream, outputStream);
	}
	
	@Test
	public void testValidFilePathWithOneLineWithDefaultOutputLineCount()
			throws TailException, IOException {
		String inputFilePathString = testFilesPath + "lorem_ipsum_one_line.txt";
		String expectedOutputFileString;
		String[] args = {inputFilePathString};
		inputStream = null;
		outputStream = new ByteArrayOutputStream();
		tailApplication.run(args, inputStream, outputStream);
		expectedOutputFileString = 
				convertFileToString(inputFilePathString);
		assertEquals(expectedOutputFileString, outputStream.toString());
	}

	@Test
	public void testValidFilePathWithMultipleLinesWithDefaultOutputLineCount() 
			throws TailException, IOException {
		String inputFilePathString = testFilesPath + "lorem_ipsum_16_lines.txt";
		String expectedOutputFilePathString = testFilesPath +
				"last_10_lines_from_lorem_ipsum_16_lines.txt";
		String expectedOutputFileString;
		String[] args = {inputFilePathString};
		inputStream = null;
		outputStream = new ByteArrayOutputStream();
		tailApplication.run(args, inputStream, outputStream);
		expectedOutputFileString = 
				convertFileToString(expectedOutputFilePathString);
		assertEquals(expectedOutputFileString, outputStream.toString());
	}
	
	@Test(expected = TailException.class)
	public void testThrowWhenSpecifiedOutputLineCountInvalid() throws TailException {
		String inputFilePathString = testFilesPath + "lorem_ipsum_16_lines.txt";
		String[] args = {"-5", inputFilePathString};
		inputStream = null;
		outputStream = new ByteArrayOutputStream();
		tailApplication.run(args, inputStream, outputStream);
	}
	
	@Test
	public void testValidFilePathWithZeroSpecifiedOutputLineCount() 
			throws TailException, IOException {
		String inputFilePathString = testFilesPath + "lorem_ipsum_16_lines.txt";
		String[] args = {"0", inputFilePathString};
		inputStream = null;
		outputStream = new ByteArrayOutputStream();
		tailApplication.run(args, inputStream, outputStream);
		assertEquals("", outputStream.toString());
	}
	
	@Test
	public void testValidFilePathWithLessLinesThanSpecifiedOutputLineCount() 
			throws TailException, IOException {
		String inputFilePathString = testFilesPath + "lorem_ipsum_16_lines.txt";
		String expectedOutputFileString;
		String[] args = {"20", inputFilePathString};
		inputStream = null;
		outputStream = new ByteArrayOutputStream();
		tailApplication.run(args, inputStream, outputStream);
		expectedOutputFileString = 
				convertFileToString(inputFilePathString);
		assertEquals(expectedOutputFileString, outputStream.toString());
	}
	
	@Test
	public void testValidFilePathWithMoreLinesThanSpecifiedOutputLineCount()
			throws TailException, IOException {
		String inputFilePathString = testFilesPath + "lorem_ipsum_16_lines.txt";
		String expectedOutputFilePathString = testFilesPath +
				"last_5_lines_from_lorem_ipsum_16_lines.txt";
		String expectedOutputFileString;
		String[] args = {"5", inputFilePathString};
		inputStream = null;
		outputStream = new ByteArrayOutputStream();
		tailApplication.run(args, inputStream, outputStream);
		expectedOutputFileString = 
				convertFileToString(expectedOutputFilePathString);
		assertEquals(expectedOutputFileString, outputStream.toString());
	}
	
	@Test
	public void testValidFilePathWithEqualLinesToSpecifiedOutputLineCount()
			throws TailException, IOException {
		String inputFilePathString = testFilesPath + "lorem_ipsum_16_lines.txt";
		String expectedOutputFileString;
		String[] args = {"16", inputFilePathString};
		inputStream = null;
		outputStream = new ByteArrayOutputStream();
		tailApplication.run(args, inputStream, outputStream);
		expectedOutputFileString =
				convertFileToString(inputFilePathString);
		assertEquals(expectedOutputFileString, outputStream.toString());
	}

	@Test
	public void testValidFileSeparatedByEmptyLinesWithDefaultOutputLineCount()
			throws TailException, IOException {
		String filePathString = testFilesPath +
				"lorem_ipsum_separated_by_empty_lines.txt";
		String fileString;
		String[] args = {filePathString};
		inputStream = null;
		outputStream = new ByteArrayOutputStream();
		tailApplication.run(args, inputStream, outputStream);
		fileString = convertFileToString(filePathString);
		assertEquals(fileString, outputStream.toString());
	}
	
	@Test
	public void testValidEmptyFile() throws TailException, IOException {
		String filePathString = testFilesPath + "empty.txt";
		String fileString;
		String[] args = {filePathString};
		inputStream = null;
		outputStream = new ByteArrayOutputStream();
		tailApplication.run(args, inputStream, outputStream);
		fileString = convertFileToString(filePathString);
		assertEquals(fileString, outputStream.toString());
	}
	
	@Test
	public void testInputFromStdInWithLessLinesThanDefaultOutputLineCount()
			throws TailException, IOException {
		String threeNewLines = new String(new char[3]).replace("\0", newLine);
		String testString = "test string" + threeNewLines + "string test";
		String[] args = {};
		inputStream = new ByteArrayInputStream(testString.getBytes());;
		outputStream = new ByteArrayOutputStream();
		tailApplication.run(args, inputStream, outputStream);
		assertEquals(testString, outputStream.toString());
	}
	
	@Test
	public void testInputFromStdInWithMoreLinesThanDefaultOutputLineCount()
			throws TailException, IOException {
		String thirteenNewLines = new String(new char[13]).replace("\0", newLine);
		String testString = "test string" + thirteenNewLines + "string test";
		String nineNewLines = new String(new char[9]).replace("\0", newLine);
		String expectedOutputString = nineNewLines + "string test";
		String[] args = {};
		inputStream = new ByteArrayInputStream(testString.getBytes());;
		outputStream = new ByteArrayOutputStream();
		tailApplication.run(args, inputStream, outputStream);
		assertEquals(expectedOutputString, outputStream.toString());
	}

	@Test
	public void testInputFromStdInWithMoreLinesThanSpecifiedOutputLineCount()
			throws TailException, IOException {
		String sixNewLines = new String(new char[6]).replace("\0", newLine);
		String testString = "test string" + sixNewLines + "string test";
		String fiveNewLines = new String(new char[5]).replace("\0", newLine);
		String expectedOutputString = fiveNewLines + "string test";
		String[] args = {"6"};
		inputStream = new ByteArrayInputStream(testString.getBytes());;
		outputStream = new ByteArrayOutputStream();
		tailApplication.run(args, inputStream, outputStream);
		assertEquals(expectedOutputString, outputStream.toString());
	}

	@Test
	public void testInputFromStdInWithLessLinesThanSpecifiedOutputLineCount()
			throws TailException, IOException {
		String threeNewLines = new String(new char[3]).replace("\0", newLine);
		String testString = "test string" + threeNewLines + " string test";
		String[] args = {"20"};
		inputStream = new ByteArrayInputStream(testString.getBytes());;
		outputStream = new ByteArrayOutputStream();
		tailApplication.run(args, inputStream, outputStream);
		assertEquals(testString, outputStream.toString());
	}
	
	@Test
	public void testUnicodeInput() throws TailException, IOException {
		String testString = "ږ ڗ ژАБВञटḥḦḧ✉✌✍讀拏";
		String[] args = {};
		inputStream = new ByteArrayInputStream(testString.getBytes());;
		outputStream = new ByteArrayOutputStream();
		tailApplication.run(args, inputStream, outputStream);
		assertEquals(testString, outputStream.toString());
	}
	
	@Test
	public void testOneValidAbsoluteFilePath() throws TailException, IOException {
		String currentDirectory = System.getProperty("user.dir");
		String filePathString = testFilesPath + "lorem_ipsum_one_line.txt";
		String absoluteFilePathString = currentDirectory +
				File.separator + filePathString;
		String fileString;
		String[] args = {absoluteFilePathString};
		inputStream = null;
		outputStream = new ByteArrayOutputStream();
		tailApplication.run(args, inputStream, outputStream);
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
