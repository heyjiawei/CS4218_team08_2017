package sg.edu.nus.comp.cs4218.impl.app;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.GrepException;

public class GrepApplicationTest {
	GrepApplication grepApp;
	InputStream in;
	OutputStream out;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		PrintWriter writer = new PrintWriter("grep_test.txt", "UTF-8");
	    writer.println("4 lines\n");
	    writer.println("6 WORDS\n");
	    writer.println("31 characters");
	    writer.close();
	    
	    writer = new PrintWriter("grep_mult_test_ouput.txt", "UTF-8");
	    writer.println("grep_test.txt:4 lines");
	    writer.println("grep_test.txt:");
	    writer.println("grep_test.txt:6 WORDS");
	    writer.println("grep_test.txt:");
	    writer.println("grep_test.txt:31 characters");
	    writer.println("grep: not exist.txt: No such file or directory");
	    
	    File file = new File(System.getProperty("user.dir") + 
	    				"/testing_grep_folder/grep_test.txt");
	    if (file.getParentFile().mkdir()) {
	        file.createNewFile();
	    } else {
	        throw new IOException("Failed to create directory " + file.getParent());
	    }
	    
	    File dir = new File(file.getParentFile(), file.getName());
        writer = new PrintWriter(dir);
        writer.println("4 lines\n");
	    writer.println("6 WORDS\n");
	    writer.println("31 characters");
        writer.close();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		new File("grep_test.txt").delete(); 
		new File("grep_mult_test_ouput.txt").delete();
		
		// Delete folder and files within
		File folder = new File(System.getProperty("user.dir") + "/testing_grep_folder/");
		File[] folderFiles = folder.listFiles();
	    if (folderFiles != null) {
	        for (File f: folderFiles) {
	            f.delete();
	        }
	    }
	    folder.delete();
	}

	@Before
	public void setUp() throws Exception {
		grepApp = new GrepApplication();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGrepFromStdin() {
		in = new ByteArrayInputStream("4 lines\n\n6 WORDS\n\n31 characters".getBytes());
		String output = grepApp.grepFromStdin("\n* ", in);
		String expected = getOutputFromFile("grep_test.txt");
		assertEquals(expected, output);
	}
	
	@Test
	public void testGrepFromOneFile() {
		String output = grepApp.grepFromOneFile("ch. grep_test.txt");
		assertEquals("31 characters", output);
	}
	
	@Test
	public void testGrepFromMultipleFiles() {
		String output = grepApp.grepFromMultipleFiles("ch. grep_test.txt testing_grep_folder/grep_test.txt");
		assertEquals("grep_test.txt:31 characters\ntesting_grep_folder/grep_test.txt:31 characters", output);
	}
	
	@Test
	public void testGrepInvalidPatternInStdin() {
		in = new ByteArrayInputStream("\\4 lines\n\n\\6 WORDS\n\n\\31 characters".getBytes());
		String output = grepApp.grepInvalidPatternInStdin("\\ ");
		assertEquals("grep: Invalid Pattern", output);
	}

	@Test
	public void testGrepInvalidPatternInFile() {
		String output = grepApp.grepInvalidPatternInFile("\\ grep_test.txt");
		assertEquals("grep: Invalid Pattern", output);
	}
	
	@Test
	public void testRunGrepFromStdinNoOutput() {
		in = new ByteArrayInputStream("4 lines\n\n6 WORDS\n\n31 characters".getBytes());
		out = new ByteArrayOutputStream();
		try {
			grepApp.run(new String[]{"*z "}, in, out);
		} catch (AbstractApplicationException e) {
			e.printStackTrace();
		}
		byte[] byteArray = ((ByteArrayOutputStream) out).toByteArray();
		assertEquals("", new String(byteArray));
	}
	
	@Test
	public void testRunGrepOnExistantFileAndNonExistantFile() {
		in = new ByteArrayInputStream("".getBytes());
		out = new ByteArrayOutputStream();
		try {
			grepApp.run(new String[]{"\\s* ", "grep_test.txt", "not exist.txt"}, in, out);
		} catch (AbstractApplicationException e) {
			e.printStackTrace();
		}
		byte[] byteArray = ((ByteArrayOutputStream) out).toByteArray();
		String expected = getOutputFromFile("grep_mult_test_ouput.txt");
		assertEquals(expected, new String(byteArray));
	}

	@Test (expected = GrepException.class)
	public void testThrowInvalidFile() throws AbstractApplicationException {
		in = new ByteArrayInputStream("".getBytes());
		out = new ByteArrayOutputStream();
		grepApp.run(new String[]{"\\s* ", "-test.txt"}, in, out);
	}
	
	private String getOutputFromFile(String filename) {
		String line = "";
		char[] c = new char[1024];
		int readChars = 0;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			while ((readChars = reader.read(c)) != -1) {
				line += new String(c, 0, readChars);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return line;
	}
}
