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
import org.junit.Rule;
import org.junit.Test;

import org.junit.rules.ExpectedException;

import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.CatException;

@SuppressWarnings({ "PMD.LongVariable", "PMD.AvoidDuplicateLiterals" })
public class CatApplicationTest {

	private InputStream inputStream;
	private ByteArrayOutputStream outputStream;
	private CatApplication catApplication;
	private static final String TEST_FILES_PATH = "test_inputs/cat/";
	private final String newLine = System.getProperty("line.separator");

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setUp() throws Exception {
		catApplication = new CatApplication();
	}

	@Test
	public void testThrowWhenStdOutputNull() throws CatException {
		String[] args = {"test", "arguments"};
		inputStream = new ByteArrayInputStream("".getBytes());
		outputStream = null;

		thrown.expect(CatException.class);
		thrown.expectMessage("No output stream provided");
		catApplication.run(args, inputStream, outputStream);
	}

	@Test
	public void testThrowWhenArgsAndStdInputNull() throws CatException {
		String[] args = null;
		inputStream = null;
		outputStream = new ByteArrayOutputStream();

		thrown.expect(CatException.class);
		thrown.expectMessage("Null Pointer Exception");
		catApplication.run(args, inputStream, outputStream);
	}

	@Test
	public void testThrowWhenIllegalFilePathGiven() throws CatException {
		String[] args = {"illegalFilePath*?"};
		inputStream = null;
		outputStream = new ByteArrayOutputStream();

		thrown.expect(CatException.class);
		thrown.expectMessage("Could not read file");
		catApplication.run(args, inputStream, outputStream);
	}

	@Test
	public void testThrowWhenDirectoryGivenAsFilePath() throws CatException {
		String[] args = {"/"};
		inputStream = null;
		outputStream = new ByteArrayOutputStream();

		thrown.expect(CatException.class);
		thrown.expectMessage("This is a directory");
		catApplication.run(args, inputStream, outputStream);
	}

	@Test
	public void testThrowWhenNonexistentFilePathGiven() throws CatException {
		String nonexistentFilePath = TEST_FILES_PATH + "nonExistentFilePath";
		String[] args = {nonexistentFilePath};
		inputStream = null;
		outputStream = new ByteArrayOutputStream();

		thrown.expect(CatException.class);
		thrown.expectMessage("Could not read file");
		catApplication.run(args, inputStream, outputStream);
	}

	@Test
	public void testProvideOneValidFilePath() throws CatException, IOException {
		String filePathString = TEST_FILES_PATH + "lorem_ipsum_short.txt";
		String fileString;
		String[] args = {filePathString};
		inputStream = null;
		outputStream = new ByteArrayOutputStream();
		catApplication.run(args, inputStream, outputStream);
		fileString = convertFileToString(filePathString);
		assertEquals(fileString, outputStream.toString());
	}

	@Test
	public void testProvideTwoValidFilePaths() throws CatException, IOException {
		String firstFilePathString = TEST_FILES_PATH + "lorem_ipsum_short.txt";
		String secondFilePathString = TEST_FILES_PATH + "lorem_ipsum_short_two.txt";
		String firstFileString;
		String secondFileString;
		String[] args = {firstFilePathString, secondFilePathString};
		inputStream = null;
		outputStream = new ByteArrayOutputStream();
		catApplication.run(args, inputStream, outputStream);
		firstFileString = convertFileToString(firstFilePathString);
		secondFileString = convertFileToString(secondFilePathString);
		assertEquals(firstFileString + secondFileString, 
				outputStream.toString());
	}

	@Test
	public void testPrintValidWhenProvideOneValidAndOneNonExistentFilePath()
			throws CatException, IOException {
		String firstFilePathString = TEST_FILES_PATH + "nonexistent_file.txt";
		String secondFilePathString = TEST_FILES_PATH + "lorem_ipsum_short.txt";
		String[] args = {firstFilePathString, secondFilePathString};
		inputStream = null;
		outputStream = new ByteArrayOutputStream();
		catApplication.run(args, inputStream, outputStream);
		String secondFileString = convertFileToString(secondFilePathString);
		assertEquals(secondFileString, outputStream.toString());
	}
	
	@Test
	public void testValidFileWithMultipleLines()
			throws CatException, IOException {
		String filePathString = TEST_FILES_PATH + "lorem_ipsum_two_lines.txt";
		String fileString;
		String[] args = {filePathString};
		inputStream = null;
		outputStream = new ByteArrayOutputStream();
		catApplication.run(args, inputStream, outputStream);
		fileString = convertFileToString(filePathString);
		assertEquals(fileString, outputStream.toString());
	}
	
	@Test
	public void testValidFileSeparatedByEmptyLines()
			throws CatException, IOException {
		String filePathString = TEST_FILES_PATH +
				"lorem_ipsum_separated_by_empty_lines.txt";
		String fileString;
		String[] args = {filePathString};
		inputStream = null;
		outputStream = new ByteArrayOutputStream();
		catApplication.run(args, inputStream, outputStream);
		fileString = convertFileToString(filePathString);
		assertEquals(fileString, outputStream.toString());
	}
	
	@Test
	public void testValidEmptyFile() throws CatException, IOException {
		String filePathString = TEST_FILES_PATH + "empty.txt";
		String fileString;
		String[] args = {filePathString};
		inputStream = null;
		outputStream = new ByteArrayOutputStream();
		catApplication.run(args, inputStream, outputStream);
		fileString = convertFileToString(filePathString);
		assertEquals(fileString, outputStream.toString());
	}
	
	@Test
	public void testInputFromStdIn() throws CatException, IOException {
		String threeNewLines = new String(new char[3]).replace("\0", newLine);
		String testString = "test string" + threeNewLines + " string test";
		String[] args = {};
		inputStream = new ByteArrayInputStream(testString.getBytes());;
		outputStream = new ByteArrayOutputStream();
		catApplication.run(args, inputStream, outputStream);
		assertEquals(testString, outputStream.toString());
	}
	
	@Test
	public void testUnicodeInput() throws CatException, IOException {
		String testString = "ږ ڗ ژАБВञटḥḦḧ✉✌✍讀拏";
		String[] args = {};
		inputStream = new ByteArrayInputStream(testString.getBytes());;
		outputStream = new ByteArrayOutputStream();
		catApplication.run(args, inputStream, outputStream);
		assertEquals(testString, outputStream.toString());
	}
	
	@Test
	public void testOneValidAbsoluteFilePath() throws CatException, IOException {
		String currentDirectory = Environment.currentDirectory;
		String filePathString = TEST_FILES_PATH + "lorem_ipsum_short.txt";
		String absoluteFilePathString = currentDirectory +
				File.separator + filePathString;
		String fileString;
		String[] args = {absoluteFilePathString};
		inputStream = null;
		outputStream = new ByteArrayOutputStream();
		catApplication.run(args, inputStream, outputStream);
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
