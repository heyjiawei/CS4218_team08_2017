package sg.edu.nus.comp.cs4218.impl.app;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.WcException;

public class WcApplicationTest {
	private static final String FILE_SEPARATOR = File.separator;
	private static final String LINE_SEPARATOR = System.lineSeparator();
	private static final String MWLFILE = String.format("test_inputs%swc%swc_test.txt", 
			FILE_SEPARATOR, FILE_SEPARATOR);
	private static final String MWLDIRECTORYFILE = String.format("test_inputs%swc%stesting_wc_folder%swc test.txt", 
			FILE_SEPARATOR, FILE_SEPARATOR, FILE_SEPARATOR);
	private static final String EMPTYFILE = String.format("test_inputs%swc%sempty.txt", 
			FILE_SEPARATOR, FILE_SEPARATOR);
	private static final String SINGLEWORD = String.format("test_inputs%swc%ssingleWord.txt", 
			FILE_SEPARATOR, FILE_SEPARATOR);
	private static final String EMPTYFOLDER = String.format("test_inputs%swc%semptyFolder%s", 
			FILE_SEPARATOR, FILE_SEPARATOR, FILE_SEPARATOR);

	WcApplication wcApp;
	InputStream in;
	OutputStream out;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
//		try{
//		    PrintWriter writer = new PrintWriter("wc_test.txt", "UTF-8");
//		    writer.println("4 lines\n");
//		    writer.println("6 WORDS\n");
//		    writer.println("31 characters");
//		    writer.close();
//		    
//		    File file = new File(System.getProperty("user.dir") + 
//		    				"/testing_wc_folder/wc file.txt");
//		    if (file.getParentFile().mkdir()) {
//		        file.createNewFile();
//		    } else {
//		        throw new IOException("Failed to create directory " + file.getParent());
//		    }
//		    
//		    File dir = new File(file.getParentFile(), file.getName());
//	        writer = new PrintWriter(dir);
//	        writer.println("4 lines\n");
//		    writer.println("6 WORDS\n");
//		    writer.println("31 characters");
//	        writer.close();
//		    
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
//		File file = new File("wc_test.txt");    
//		file.delete();
//		
//		// Delete folder and files within
//		File folder = new File(System.getProperty("user.dir") + "/testing_wc_folder/");
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
		wcApp = new WcApplication();
		in = new FileInputStream(MWLFILE);
		out = new ByteArrayOutputStream();
		PrintStream print = new PrintStream(out);
		System.setOut(print);
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testPrintCharacterCountInFile() {
		String charCount = wcApp.printCharacterCountInFile("-m wc_test.txt");
		assertEquals("31", charCount);
	}
	
	@Test
	public void testPrintNewlineCountInFile() {
		String lineCount = wcApp.printNewlineCountInFile("-L wc_test.txt");
		assertEquals("4", lineCount);
	}
	
	@Test
	public void testPrintWordCountInFile() {
		String wordCount = wcApp.printWordCountInFile("-w wc_test.txt");
		assertEquals("6", wordCount);
	}

	@Test
	public void testPrintAllCountsInFile() {
		String count = wcApp.printAllCountsInFile("-LMW wc_test.txt");
		assertEquals("31 6 4", count);  
	}
	
	@Test
	public void testPrintCharacterCountInStdin() {
		in = new ByteArrayInputStream("4 lines\n\n6 words\n\n31 characters".getBytes());
		String count = wcApp.printCharacterCountInStdin("-m wc_test.txt", in);
		assertEquals("31", count);
	}
	
	@Test
	public void testPrintWordCountInStdin() {
		in = new ByteArrayInputStream("4 lines\n\n6 words\n\n31 characters".getBytes());
		String count = wcApp.printWordCountInStdin("-m wc_test.txt", in);
		assertEquals("6", count);
	}
	
	@Test
	public void testPrintNewlineCountInStdin() {
		in = new ByteArrayInputStream("4 lines\n\n6 words\n\n31 characters".getBytes());
		String count = wcApp.printNewlineCountInStdin("-m wc_test.txt", in);
		assertEquals("4", count);
	}

	@Test
	public void testPrintAllCountsInStdin() {
		in = new ByteArrayInputStream("4 lines\n\n6 words\n\n31 characters".getBytes());
		String count = wcApp.printAllCountsInStdin("-m wc_test.txt", in);
		assertEquals("31 6 4", count);
	}
	
	@Test
	public void testDisjointedAllOptionSimpleFile() throws AbstractApplicationException {
		in = new ByteArrayInputStream("".getBytes());
		out = new ByteArrayOutputStream();
		wcApp.run(new String[]{"-wl", "-m", "wc_test.txt"}, in, out);
		byte[] byteArray = ((ByteArrayOutputStream) out).toByteArray();
		
		assertEquals("31 6 4\n", new String(byteArray));
	}
	
	@Test
	public void testNoOptionSimpleFile() throws AbstractApplicationException {
		in = new ByteArrayInputStream("".getBytes());
		out = new ByteArrayOutputStream();
		wcApp.run(new String[]{"wc_test.txt"}, in, out);
		byte[] byteArray = ((ByteArrayOutputStream) out).toByteArray();
		
		assertEquals("31 6 4\n", new String(byteArray));
	}

	@Test
	public void testDisjointedAllOptionStdin() throws AbstractApplicationException {
		in = new ByteArrayInputStream("1 newline of code,\n x characters\n".getBytes());
		out = new ByteArrayOutputStream();
		wcApp.run(new String[]{"-wm", "-l"}, in, out);
		byte[] byteArray = ((ByteArrayOutputStream) out).toByteArray();
		
		assertEquals("33 6 2\n", new String(byteArray));
	}
	
	@Test
	public void testNoOptionStdin() throws AbstractApplicationException {
		in = new ByteArrayInputStream("1 newline of code,\n x characters\n".getBytes());
		out = new ByteArrayOutputStream();
		wcApp.run(new String[]{}, in, out);
		byte[] byteArray = ((ByteArrayOutputStream) out).toByteArray();
		
		assertEquals("33 6 2\n", new String(byteArray));
		
	}
	
	@Test
	public void testReadFromDir() {
		in = new ByteArrayInputStream("".getBytes());
		out = new ByteArrayOutputStream();
		try {
			wcApp.run(new String[]{"-lmw", "testing_wc_folder/wc file.txt"}, in, out);
		} catch (AbstractApplicationException e) {
			e.printStackTrace();
		}
		byte[] byteArray = ((ByteArrayOutputStream) out).toByteArray();
		assertEquals("31 6 4\n", new String(byteArray));
	}

	@Test
	public void testRepeatedOptions() {
		in = new ByteArrayInputStream("".getBytes());
		out = new ByteArrayOutputStream();
		try {
			wcApp.run(new String[]{"-mm", "testing_wc_folder/wc file.txt"}, in, out);
		} catch (AbstractApplicationException e) {
			e.printStackTrace();
		}
		byte[] byteArray = ((ByteArrayOutputStream) out).toByteArray();
		assertEquals("31\n", new String(byteArray));
	}

	@Test (expected = WcException.class)
	public void testThrowNoStdinInputProvidedException() throws Exception {
		in = new ByteArrayInputStream("".getBytes());
		out = new ByteArrayOutputStream();
		wcApp.run(new String[]{""}, in, out);
	}
	
	@Test (expected = WcException.class)
	public void testThrowInvalidFileNameException() throws Exception {
		in = new ByteArrayInputStream("".getBytes());
		out = new ByteArrayOutputStream();
		wcApp.run(new String[]{"this"}, in, out);
	}
	
	@Test (expected = WcException.class)
	public void testThrowInvalidOptionException() throws Exception {
		in = new ByteArrayInputStream("".getBytes());
		out = new ByteArrayOutputStream();
		wcApp.run(new String[]{"-lm -c", "wc_test.txt"}, in, out);
	}
}
