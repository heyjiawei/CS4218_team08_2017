package sg.edu.nus.comp.cs4218.impl;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.*;

import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.Shell;

public class ShellGlobbingTest {

	private Shell shell;
	private String output;
	private String initialDirectory;

	private static final String NEW_LINE = System.getProperty("line.separator");
	private static final String TEST_FILE_PATH = "test_globbing/";
	private static final String[] TEST_SUBDIR_PATHS = {
			"test_globbing/_npu_",
			"test_globbing/_tpu_"
	};
	private static final String[] TEST_FILES = {
			"test_globbing/.input",
			"test_globbing/input1.txt",
			"test_globbing/input2.txt",
			"test_globbing/output1.txt",
			"test_globbing/output2.txt",
			"test_globbing/_npu_/single_file.txt",
			"test_globbing/_tpu_/a",
			"test_globbing/_tpu_/ab",
			"test_globbing/_tpu_/abc"
	};

	private static void deleteDirectory(File dir) {
		if (dir.isDirectory()) {
			File[] children = dir.listFiles();
			for (int i = 0; i < children.length; i++) {
				deleteDirectory(children[i]);
			}
		}

		dir.delete();
	}

	@Before
	public void setUp() throws Exception {
		initialDirectory = Environment.currentDirectory;
		shell = new ShellImpl();
		System.setProperty("user.dir", new File(TEST_FILE_PATH).getCanonicalPath());
	}

	@After
	public void tearDown() throws Exception {
		System.setProperty("user.dir", initialDirectory);
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		deleteDirectory(new File(TEST_FILE_PATH));
		Files.createDirectories(Paths.get(TEST_FILE_PATH));
		for (String subdirectories : TEST_SUBDIR_PATHS) {
			Files.createDirectories(Paths.get(subdirectories));
		}
		for (String file : TEST_FILES) {
			Files.createFile(Paths.get(file));
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		deleteDirectory(new File(TEST_FILE_PATH));
	}

	@Test
	public void testGlobNoPaths() throws IOException {
		System.setProperty("user.dir", new File("test_globbing/_tpu_").getCanonicalPath());
		String cmd = " echo * ";

		output = shell.globNoPaths(cmd);

		assertEquals("a ab abc" + NEW_LINE, output);
	}

	@Test
	public void testGlobOneFile() {
		String cmd = " echo _npu_/* ";

		output = shell.globOneFile(cmd);

		assertEquals("_npu_/single_file.txt" + NEW_LINE, output);
	}

	@Test
	public void testGlobFilesDirectories() {
		String cmd = " echo **/* ";

		output = shell.globFilesDirectories(cmd);

		assertEquals("_npu_ _npu_/single_file.txt _tpu_ _tpu_/a _tpu_/ab _tpu_/abc input1.txt input2.txt output1.txt output2.txt" + NEW_LINE, output);
	}

	@Test
	public void testGlobMultipleDirectories() {
		String cmd = " echo _npu_/* _tpu_/*";

		output = shell.globFilesDirectories(cmd);

		assertEquals("_npu_ _npu_/single_file.txt _tpu_ _tpu_/a _tpu_/ab _tpu_/abc" + NEW_LINE, output);
	}

	@Test
	public void testGlobWithException() {
		String cmd = " head _*_ ";

		output = shell.globWithException(cmd);

		assertEquals("head: This is a directory", output);
	}

	@Test
	public void testGlobFront() {
		String cmd = " echo *.txt ";

		output = shell.globFilesDirectories(cmd);

		assertEquals("input1.txt input2.txt output1.txt output2.txt" + NEW_LINE, output);
	}

	@Test
	public void testGlobBack() {
		String cmd = " echo input* ";

		output = shell.globFilesDirectories(cmd);

		assertEquals("input1.txt input2.txt" + NEW_LINE, output);
	}

	@Test
	public void testGlobFrontCenterBack() {
		String cmd = " echo *p*u* ";

		output = shell.globFilesDirectories(cmd);

		assertEquals("_npu_ _tpu_ input1.txt input2.txt output1.txt output2.txt" + NEW_LINE, output);
	}

	@Test
	public void testGlobHidden() {
		String cmd = " echo .* ";

		output = shell.globOneFile(cmd);

		assertEquals(".input" + NEW_LINE, output);
	}
}
