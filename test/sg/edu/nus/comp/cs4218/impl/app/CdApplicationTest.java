package sg.edu.nus.comp.cs4218.impl.app;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.CdException;

public class CdApplicationTest {
	
	private InputStream inputStream = null;
	private ByteArrayOutputStream outputStream = null;
	private CdApplication cdApplication;
	private final String testFilesPath = "test_inputs/cd/";
	private final String initialWorkingDirectory = Environment.currentDirectory;

	@Before
	public void setUp() throws Exception {
		cdApplication = new CdApplication();
	}
	
	@After
	public void tearDown() throws Exception {
		Environment.currentDirectory = initialWorkingDirectory;
	}

	@Test(expected = CdException.class)
	public void testThrowWhenArgsNull() throws CdException {
		String[] args = null;
		cdApplication.run(args, inputStream, outputStream);
	}
	
	@Test
	public void testRelativePathValidDirectory() throws CdException {
		String currentDirectoryFilePath = initialWorkingDirectory;
		String validDirectoryRelativePath = testFilesPath +
				"validDirectory";
		String[] args = {validDirectoryRelativePath};
		cdApplication.run(args, inputStream, outputStream);
		String expectedNewDirectoryPath = currentDirectoryFilePath +
				File.separator + validDirectoryRelativePath;
		assertEquals(expectedNewDirectoryPath, Environment.currentDirectory);
	}
	
	@Test
	public void testAbsolutePathValidDirectory() throws CdException {
		String currentDirectoryFilePath = Environment.currentDirectory;
		String validDirectoryRelativePath = testFilesPath +
				"validDirectory";
		String validDirectoryAbsolutePath = currentDirectoryFilePath +
				File.separator + validDirectoryRelativePath;
		String[] args = {validDirectoryAbsolutePath};
		cdApplication.run(args, inputStream, outputStream);
		String expectedNewDirectoryPath = validDirectoryAbsolutePath;
		assertEquals(expectedNewDirectoryPath, Environment.currentDirectory);
	}
	
	@Test
	public void testPathStartingWithTildeStartsFromHomeDirectory()
			throws CdException {
		String pathStartingWithTilde = "~";
		String[] args = {pathStartingWithTilde};
		cdApplication.run(args, inputStream, outputStream);
		String expectedNewDirectoryPath = System.getProperty("user.home");
		assertEquals(expectedNewDirectoryPath, Environment.currentDirectory);
	}
	
	@Test
	public void testNoArgsGoesToHomeDirectory() throws CdException {
		String[] args = {};
		cdApplication.run(args, inputStream, outputStream);
		String expectedNewDirectoryPath = System.getProperty("user.home");
		assertEquals(expectedNewDirectoryPath, Environment.currentDirectory);
	}
	
	@Test(expected = CdException.class)
	public void testValidPathButNotDirectory() throws CdException {
		String invalidDirectoryRelativePath = testFilesPath +
				"not_a_directory.txt";
		String[] args = {invalidDirectoryRelativePath};
		cdApplication.run(args, inputStream, outputStream);
	}

	@Test(expected = CdException.class)
	public void testThrowWhenPathStringInvalid() throws CdException {
		String[] args = {"^[^/./\\:*?\"<>|]+$"};
		cdApplication.run(args, inputStream, outputStream);
	}
	
	@Test(expected = CdException.class)
	public void testThrowWhenPathStringNonexistent() throws CdException {
		String nonexistentPath = testFilesPath + "nonexistent";
		String[] args = {nonexistentPath};
		cdApplication.run(args, inputStream, outputStream);
	}

}
