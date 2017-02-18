package sg.edu.nus.comp.cs4218.impl.app;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;

public class WcApplicationTest {
	WcApplication wcApp;
	InputStream in;
	OutputStream out;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		wcApp = new WcApplication();
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testPrintCharacterCountInFile() {
		String charCount = wcApp.printCharacterCountInFile("-m test.txt");
		assertEquals("31", charCount);
	}
	
	@Test
	public void testPrintNewlineCountInFile() {
		String lineCount = wcApp.printNewlineCountInFile("-l test.txt");
		assertEquals("4", lineCount);
	}
	
	@Test
	public void testPrintWordCountInFile() {
		String wordCount = wcApp.printWordCountInFile("-w test.txt");
		assertEquals("6", wordCount);
	}

	@Test
	public void testPrintAllCountsInFile() {
		String count = wcApp.printAllCountsInFile("-lmw test.txt");
		assertEquals("31 6 4", count);  
	}
	
	@Test
	public void testPrintCharacterCountInStdin() {
		in = new ByteArrayInputStream("4 lines\n\n6 words\n\n31 characters".getBytes());
		String count = wcApp.printCharacterCountInStdin("-m test.txt", in);
		assertEquals("31", count);
	}
	
	@Test
	public void testPrintWordCountInStdin() {
		in = new ByteArrayInputStream("4 lines\n\n6 words\n\n31 characters".getBytes());
		String count = wcApp.printWordCountInStdin("-m test.txt", in);
		assertEquals("6", count);
	}
	
	@Test
	public void testPrintNewlineCountInStdin() {
		in = new ByteArrayInputStream("4 lines\n\n6 words\n\n31 characters".getBytes());
		String count = wcApp.printNewlineCountInStdin("-m test.txt", in);
		assertEquals("4", count);
	}

	@Test
	public void testPrintAllCountsInStdin() {
		in = new ByteArrayInputStream("4 lines\n\n6 words\n\n31 characters".getBytes());
		String count = wcApp.printAllCountsInStdin("-m test.txt", in);
		assertEquals("31 6 4", count);
	}
	
//	@Test
//	public void testSingleOptionSimpleFile() throws AbstractApplicationException {
//		in = new ByteArrayInputStream("".getBytes());
//		out = new ByteArrayOutputStream();
//		wcApp.run(new String[]{"wc", "-m", "test.txt"}, in, out);
//		byte[] byteArray = ((ByteArrayOutputStream) out).toByteArray();
//		
//		assertEquals("31", new String(byteArray));
////		String charCount = wcApp.printCharacterCountInFile("wc -m testDir1/testDir2/test.txt");
//	}
//	
//	@Test
//	public void testJoinedOptionSimpleFile() throws AbstractApplicationException {
//		in = new ByteArrayInputStream("".getBytes());
//		out = new ByteArrayOutputStream();
//		wcApp.run(new String[]{"wc", "-lm", "test.txt"}, in, out);
//		byte[] byteArray = ((ByteArrayOutputStream) out).toByteArray();
//		
//		assertEquals("31 4", new String(byteArray));
//	}
//	
//	@Test
//	public void testDisjointedAllOptionSimpleFile() throws AbstractApplicationException {
//		in = new ByteArrayInputStream("".getBytes());
//		out = new ByteArrayOutputStream();
//		wcApp.run(new String[]{"wc", "-wl", "-m", "test.txt"}, in, out);
//		byte[] byteArray = ((ByteArrayOutputStream) out).toByteArray();
//		
//		assertEquals("31 6 4", new String(byteArray));
//	}
//	
//	@Test
//	public void testNoOptionSimpleFile() throws AbstractApplicationException {
//		in = new ByteArrayInputStream("".getBytes());
//		out = new ByteArrayOutputStream();
//		wcApp.run(new String[]{"wc", "test.txt"}, in, out);
//		byte[] byteArray = ((ByteArrayOutputStream) out).toByteArray();
//		
//		assertEquals("31 6 4", new String(byteArray));
//	}
//	
//	@Test
//	public void testSingleOptionStdin() throws AbstractApplicationException {
//		in = new ByteArrayInputStream("how many \n lines here \n do you see?".getBytes());
//		out = new ByteArrayOutputStream();
//		wcApp.run(new String[]{"wc", "-l"}, in, out);
//		byte[] byteArray = ((ByteArrayOutputStream) out).toByteArray();
//		
//		assertEquals("2", new String(byteArray));
//	}
//	
//	@Test
//	public void testJoinedOptionStdin() throws AbstractApplicationException {
//		in = new ByteArrayInputStream("1 newline of code,\n x characters\n".getBytes());
//		out = new ByteArrayOutputStream();
//		wcApp.run(new String[]{"wc", "-lm"}, in, out);
//		byte[] byteArray = ((ByteArrayOutputStream) out).toByteArray();
//		
//		assertEquals("33 2", new String(byteArray));
//	}
//
//	@Test
//	public void testDisjointedAllOptionStdin() throws AbstractApplicationException {
//		in = new ByteArrayInputStream("1 newline of code,\n x characters\n".getBytes());
//		out = new ByteArrayOutputStream();
//		wcApp.run(new String[]{"wc", "-wm", "-l"}, in, out);
//		byte[] byteArray = ((ByteArrayOutputStream) out).toByteArray();
//		
//		assertEquals("34 6 2", new String(byteArray));
//	}
//	
//	@Test
//	public void testNoOptionStdin() throws AbstractApplicationException {
//		in = new ByteArrayInputStream("1 newline of code,\n x characters\n".getBytes());
//		out = new ByteArrayOutputStream();
//		wcApp.run(new String[]{"wc"}, in, out);
//		byte[] byteArray = ((ByteArrayOutputStream) out).toByteArray();
//		
//		assertEquals("34 6 2", new String(byteArray));
//		
//	}
	
}
