package sg.edu.nus.comp.cs4218.impl.app;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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

import sg.edu.nus.comp.cs4218.exception.CatException;

public class CatApplicationTest {

	private InputStream inputStream;
	private ByteArrayOutputStream outputStream;
	private CatApplication catApplication;
	private final String testFilesPath = "test_inputs/cat/";
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		catApplication = new CatApplication();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test(expected = CatException.class)
	public void testThrowWhenStdOutputNull() throws CatException {
		String[] args = {"test", "arguments"};
		inputStream = new ByteArrayInputStream("".getBytes());
		outputStream = null;
		catApplication.run(args, inputStream, outputStream);
	}

	@Test(expected = CatException.class)
	public void testThrowWhenArgsAndStdInputNull() throws CatException {
		String[] args = null;
		inputStream = null;
		outputStream = new ByteArrayOutputStream();
		catApplication.run(args, inputStream, outputStream);
	}

	@Test(expected = CatException.class)
	public void testThrowWhenIllegalFilePathGiven() throws CatException {
		String[] args = {"illegalFilePath*?"};
		inputStream = null;
		outputStream = new ByteArrayOutputStream();
		catApplication.run(args, inputStream, outputStream);
	}

	@Test(expected = CatException.class)
	public void testThrowWhenDirectoryGivenAsFilePath() throws CatException {
		String[] args = {"/"};
		inputStream = null;
		outputStream = new ByteArrayOutputStream();
		catApplication.run(args, inputStream, outputStream);
	}

	@Test(expected = CatException.class)
	public void testThrowWhenNonexistentFilePathGiven() throws CatException {
		String[] args = {"nonExistentFilePath"};
		inputStream = null;
		outputStream = new ByteArrayOutputStream();
		catApplication.run(args, inputStream, outputStream);
	}

	@Test
	public void testProvideOneValidFilePath() throws CatException, IOException {
		String filePathString = testFilesPath + "lorem_ipsum_short.txt";
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
		String firstFilePathString = testFilesPath + "lorem_ipsum_short.txt";
		String secondFilePathString = testFilesPath + "lorem_ipsum_short_two.txt";
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

	@Test(expected = CatException.class)
	public void testThrowWhenProvideOneValidAndOneNonExistentFilePath()
			throws CatException, IOException {
		String firstFilePathString = testFilesPath + "lorem_ipsum_short.txt";
		String secondFilePathString = testFilesPath + "nonexistent_file.txt";
		String[] args = {firstFilePathString, secondFilePathString};
		inputStream = null;
		outputStream = new ByteArrayOutputStream();
		catApplication.run(args, inputStream, outputStream);
	}
	
	@Test
	public void testValidFileWithMultipleLines()
			throws CatException, IOException {
		String filePathString = testFilesPath + "lorem_ipsum_two_lines.txt";
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
		String filePathString = testFilesPath +
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
		String filePathString = testFilesPath + "empty.txt";
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
		String testString = "test string\n\n\n string test\n";
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
