package sg.edu.nus.comp.cs4218.impl.app;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
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

public class SedApplicationTest {
	SedApplication sedApp;
	InputStream in;
	OutputStream out;
	
	/*
	 * 1 test with different limiters. E.g. instead of s/regex/replacement
	 * test with s.regex.replacement
	 * 2 invalid testing with limiter inside regex
	 * 3 invalid testing with limiter inside replacement string
	 * 4 inputstream reads from file
	 * 5 inputstream reads arguments
	 * 6 outputstream to file
	 * 7 outputstream to stdin (print on screen)
	 * 8 invaild regex expression
	 * 9 invalid replacement
	 * 10 surrounded with quotes e.g. sed 's/day/night/' <old >new
	 * 
	 */

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		try{
		    PrintWriter writer = new PrintWriter("sed_test.txt", "UTF-8");
		    writer.println("sunday monday tuesday");
		    writer.print("happyday sad day all-day");
		    writer.close();
		    
		    writer = new PrintWriter("sed_test_replace_first.txt", "UTF-8");
		    writer.println("sunshine monday tuesday");
		    writer.print("happyshine sad day all-day");
		    writer.close();
		    
		    writer = new PrintWriter("sed_test_replace_all.txt", "UTF-8");
		    writer.println("sunnight monnight tuesnight");
		    writer.print("happynight sad night all-night");
		    writer.close();
//		    
//		    File file = new File(System.getProperty("user.dir") + 
//		    				"/testing_sed_folder/sed file.txt");
//		    if (file.getParentFile().mkdir()) {
//		        file.createNewFile();
//		    } else {
//		        throw new IOException("Failed to create directory " + file.getParent());
//		    }
//		    
//		    File dir = new File(file.getParentFile(), file.getName());
//	        writer = new PrintWriter(dir);
//	        writer.println("sunday monday tuesday");
//		    writer.println("wednesday thursday friday");
//		    writer.println("saturday today everyday");
//		    writer.println("happyday sad day all-day");
//	        writer.close();
//		    
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		new File("sed_test.txt").delete();
		new File("sed_test_replace_first.txt").delete();
		new File("sed_test_replace_all.txt").delete();
		
		// Delete folder and files within
//		File folder = new File(System.getProperty("user.dir") + "/testing_sed_folder/");
//		File[] folderFiles = folder.listFiles();
//	    if (folderFiles != null) {
//	        for (File f: folderFiles) {
//	            f.delete();
//	        }
//	    }
//	    folder.delete();
	}

	@Before
	public void setUp() throws Exception {
		sedApp = new SedApplication();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testReplaceFirstSubStringInFile() {
		String output = sedApp.replaceFirstSubStringInFile("sxdayxshinex sed_test.txt");
		assertEquals(getOutputFromFile("sed_test_replace_first.txt"), output);
	}
	
	@Test
	public void testReplaceAllSubstringsInFile() {
		String output = sedApp.replaceAllSubstringsInFile("s!day!night!g sed_test.txt");
		assertEquals(getOutputFromFile("sed_test_replace_all.txt"), output);
	}
	
	@Test
	public void testReplaceFirstSubStringFromStdin() {
		in = new ByteArrayInputStream("sunday monday".getBytes());
		String output = sedApp.replaceFirstSubStringFromStdin("sxdayxshinex sed_test.txt", in);
		assertEquals("sunshine monday", output);
	}
	
	@Test
	public void testReplaceAllSubstringsInStdin() {
		in = new ByteArrayInputStream("sunday\nmonday".getBytes());
		String output = sedApp.replaceFirstSubStringFromStdin("sxdayxshinex sed_test.txt", in);
		assertEquals("sunshine\nmonshine", output);
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
