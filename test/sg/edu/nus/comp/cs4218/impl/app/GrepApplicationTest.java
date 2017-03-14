package sg.edu.nus.comp.cs4218.impl.app;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import sg.edu.nus.comp.cs4218.exception.GrepException;
import sg.edu.nus.comp.cs4218.impl.app.GrepApplication;

/*
 * Assumptions: 
 * 1) run function will call the correct functions with the correct 
 * inputs in the correct order separated by a space
 * 2) Run function will take inputs directly from shell unordered
 * 3) Args for run: unordered consisting of pattern and files
 * 4) Args for grepFromOneFile: pattern, file
 * 5) Args for grepFromMultipleFiles: pattern, file, file, ...
 * 6) Args for grepFromStdin: pattern (Stdin will be parsed from run)
 */

public class GrepApplicationTest {
	private static final String NOMATCHFILE = "Pattern Not Found In File!\n";
	private static final String REGEXPATTERNOUT = "Hello Hello\nABC Hello\n";
	private static final String REGEXPATTERN = ".*ell";
	private static final String ABCSINGLEFILEOUT = "ABC Hello\nABCDEFGHI\n";
	private static final String ABCPATTERN = "ABC";
	private static final String HIEPATTERN = "hie";
	private static final String NOMATCHSTDIN = "Pattern Not Found In Stdin!\n";
	private static final String FILEDIRECTORYDONOTEXIST = "No such file or directory\n";
	private static final String NOPATTERN = "grep: No pattern provided\n";
	private static final String INVALIDPATTERN = "grep: Invalid Pattern\n";
	private static final String INVALIDCMD = "grep: Invalid Command\n";
	private GrepApplication grepApp;
	private String[] args;
	private FileInputStream stdin;
	private String fileName;
	private String fileName2;
	private String fileName3;
	private String directory;
	private String invalidFile;
	private ByteArrayOutputStream baos;
	PrintStream print;
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setUp() throws FileNotFoundException {
		grepApp = new GrepApplication();
		stdin = new FileInputStream("test_inputs/grep/greptestdoc.txt");
		fileName = "test_inputs/grep/greptestdoc.txt";
		fileName2 = "test_inputs/grep/greptestdoc2.txt";
		fileName3 = "test_inputs/grep/testdoc.txt";
		invalidFile = "test_inputs/grep/abjkcsnakjc.txt";
		directory = "test_inputs/grep/";
		baos = new ByteArrayOutputStream();
		print = new PrintStream(baos);
		System.setOut(print);
	}

	@Test
	public void grepStdInNoMatchesFromRun() throws GrepException {
		args = new String[1];
		args[0] = HIEPATTERN;
		grepApp.run(args, stdin, System.out);
		System.out.flush();
		assertEquals(NOMATCHSTDIN, baos.toString());
	}

	@Test
	public void grepStdInNoMatches() throws GrepException {
		args = new String[1];
		args[0] = HIEPATTERN;
		assertEquals(NOMATCHSTDIN, grepApp.grepFromStdin(args[0], stdin));
	}

	@Test
	public void grepStdInMatchesFromRun() throws GrepException {
		args = new String[1];
		args[0] = ABCPATTERN;
		grepApp.run(args, stdin, System.out);
		System.out.flush();
		assertEquals(ABCSINGLEFILEOUT, baos.toString());
	}

	@Test
	public void grepStdInMatches() throws GrepException {
		args = new String[1];
		args[0] = ABCPATTERN;
		assertEquals(ABCSINGLEFILEOUT, grepApp.grepFromStdin(args[0], stdin));
	}
	
	@Test
	public void grepStdInRegexMatchesFromRun() throws GrepException {
		args = new String[1];
		args[0] = REGEXPATTERN;
		grepApp.run(args, stdin, System.out);
		System.out.flush();
		assertEquals(REGEXPATTERNOUT, baos.toString());
	}

	@Test
	public void grepStdInRegexMatches() throws GrepException {
		args = new String[1];
		args[0] = REGEXPATTERN;
		assertEquals(REGEXPATTERNOUT, grepApp.grepFromStdin(args[0], stdin));
	}
	
	@Test
	public void grepSingleFileNoMatchesFromRun() throws GrepException {
		args = new String[2];
		args[0] = HIEPATTERN;
		args[1] = fileName;
		grepApp.run(args, stdin, System.out);
		System.out.flush();
		assertEquals(NOMATCHFILE, baos.toString());
	}
		
	@Test
	public void grepSingleFileNoMatches() throws GrepException {
		args = new String[2];
		args[0] = HIEPATTERN;
		args[1] = fileName;
		assertEquals(NOMATCHFILE, grepApp.grepFromOneFile(args[0] + " " + args[1]));
	}
		
	@Test
	public void grepSingleFileMultipleMatchesInALineFromRun() throws GrepException {
		args = new String[2];
		args[0] = "h";
		args[1] = fileName3;
		grepApp.run(args, stdin, System.out);
		System.out.flush();
		assertEquals("Boisterous he on understood attachment as entreaties ye devonshire.\n"
				+ "Extremely ham any his departure for contained curiosity defective.\n"
				+ "Way now instrument had eat diminution melancholy expression sentiments stimulated.\n"
				+ "Mrs interested now his affronting inquietude contrasted cultivated.\n"
				+ "Lasting showing expense greater on colonel no.\n"
				+ "Prepared do an dissuade be so whatever steepest.\n"
				+ "Yet her beyond looked either day wished nay.\n"
				+ "Now curiosity you explained immediate why behaviour.\n"
				+ "An dispatched impossible of of melancholy favourable.\n"
				+ "Our quiet not heart along scale sense timed.\n"
				+ "Consider may dwelling old him her surprise finished families graceful.\n"
				+ "Is at purse tried jokes china ready decay an.\n" + "Small its shy way had woody downs power.\n"
				+ "Procured shutters mr it feelings.\n" + "To or three offer house begin taken am at.\n"
				+ "As dissuade cheerful overcame so of friendly he indulged unpacked.\n"
				+ "An seeing feebly stairs am branch income me unable.\n"
				+ "Celebrated contrasted discretion him sympathize her collecting occasional.\n"
				+ "Do answered bachelor occasion in of offended no concerns.\n"
				+ "Supply worthy warmth branch of no ye.\n" + "Though wished merits or be.\n"
				+ "Alone visit use these smart rooms ham.\n" + "Course sir people worthy horses add entire suffer.\n"
				+ "Strictly numerous outlived kindness whatever on we no on addition.\n"
				+ "Are sentiments apartments decisively the especially alteration.\n"
				+ "Thrown shy denote ten ladies though ask saw.\n" + "Or by to he going think order event music.\n"
				+ "Led income months itself and houses you. After nor you leave might share court balls.\n",
				baos.toString());
	}
	
	@Test
	public void grepSingleFileMatchesFromRun() throws GrepException {
		args = new String[2];
		args[0] = ABCPATTERN;
		args[1] = fileName;
		grepApp.run(args, stdin, System.out);
		System.out.flush();
		assertEquals(ABCSINGLEFILEOUT, baos.toString());
	}
	
	@Test
	public void grepSingleFileMatches() throws GrepException {
		args = new String[2];
		args[0] = ABCPATTERN;
		args[1] = fileName;
		assertEquals(ABCSINGLEFILEOUT, grepApp.grepFromOneFile(args[0] + " " + args[1]));
	}
	
	@Test
	public void grepSingleFileRegexMatchesFromRun() throws GrepException {
		args = new String[2];
		args[0] = REGEXPATTERN;
		args[1] = fileName;
		grepApp.run(args, stdin, System.out);
		System.out.flush();
		assertEquals(REGEXPATTERNOUT, baos.toString());
	}
	
	@Test
	public void grepSingleFileRegexMatches() throws GrepException {
		args = new String[2];
		args[0] = REGEXPATTERN;
		args[1] = fileName;
		assertEquals(REGEXPATTERNOUT, grepApp.grepFromOneFile(args[0] + " " + args[1]));
	}
	
	@Test
	public void grepMultipleFileNoMatchesFromRun() throws GrepException {
		args = new String[3];
		args[0] = HIEPATTERN;
		args[1] = fileName;
		args[2] = fileName2;
		grepApp.run(args, stdin, System.out);
		System.out.flush();
		assertEquals(NOMATCHFILE, baos.toString());
	}

	@Test
	public void grepMultipleFileNoMatches() throws GrepException {
		args = new String[3];
		args[0] = HIEPATTERN;
		args[1] = fileName;
		args[2] = fileName2;
		assertEquals(NOMATCHFILE, grepApp.grepFromMultipleFiles(args[0] + " " + args[1] + " " + args[2]));
	}
	
	@Test
	public void grepMultipleFileMatchesFromRun() throws GrepException {
		args = new String[3];
		args[0] = "DEF";
		args[1] = fileName;
		args[2] = fileName2;
		grepApp.run(args, stdin, System.out);
		System.out.flush();
		String expected = args[1] + ":ABCDEFGHI\n" 
						+ args[2] + ":DEF\n";
		assertEquals(expected, baos.toString());
	}
	
	@Test
	public void grepMultipleFileMatches() throws GrepException {
		args = new String[3];
		args[0] = "DEF";
		args[1] = fileName;
		args[2] = fileName2;
		String expected = args[1] + ":ABCDEFGHI\n" 
						+ args[2] + ":DEF\n";
		assertEquals(expected, grepApp.grepFromMultipleFiles(args[0] + " " + args[1] + " " + args[2]));
	}
	
	@Test
	public void grepMultipleFileInvalidMatches() throws GrepException {
		args = new String[4];
		args[0] = "DEF";
		args[1] = fileName2;
		args[2] = invalidFile;
		args[3] = fileName;
		String command = args[0] + " " + args[1] + " " + args[2] + " " + args[3];
		String expected = args[1] + ":DEF\n" 
						+ args[2] + ":" + FILEDIRECTORYDONOTEXIST
						+ args[3] + ":ABCDEFGHI\n";
		assertEquals(expected, grepApp.grepFromMultipleFiles(command));
	}
	
	@Test
	public void grepMultipleFileRegexMatchesFromRun() throws GrepException {
		args = new String[3];
		args[0] = REGEXPATTERN;
		args[1] = fileName;
		args[2] = fileName2;
		grepApp.run(args, stdin, System.out);
		System.out.flush();
		String expected = args[1] + ":Hello Hello\n" 
						+ args[1] + ":ABC Hello\n" 
						+ args[2] + ":ello milo\n";
		assertEquals(expected, baos.toString());
	}
	
	@Test
	public void grepMultipleFileRegexMatches() throws GrepException {
		args = new String[3];
		args[0] = REGEXPATTERN;
		args[1] = fileName;
		args[2] = fileName2;
		String command = args[0] + " " + args[1] + " " + args[2];
		String expected = args[1] + ":Hello Hello\n" 
						+ args[1] + ":ABC Hello\n" 
						+ args[2] + ":ello milo\n";
		assertEquals(expected, grepApp.grepFromMultipleFiles(command));
	}
	
	@Test
	public void grepUnorderedInputFromRun() throws GrepException {
		args = new String[3];
		args[0] = fileName;
		args[1] = REGEXPATTERN;
		args[2] = fileName2;
		grepApp.run(args, stdin, System.out);
		System.out.flush();
		assertEquals(args[1] + ":" + FILEDIRECTORYDONOTEXIST, baos.toString());
	}
	
	@Test
	public void grepMultipleFileDirectoryFromRun() throws GrepException {
		args = new String[3];
		args[0] = REGEXPATTERN;
		args[1] = directory;
		args[2] = fileName;
		
		grepApp.run(args, stdin, System.out);
		System.out.flush();
		String expected = args[1] + ":" + FILEDIRECTORYDONOTEXIST
						+ args[2] + ":Hello Hello\n" 
						+ args[2] + ":ABC Hello\n";
		assertEquals(expected, baos.toString());
	}
	
	@Test
	public void grepMultipleFileWithInvalidFileFromRun() throws GrepException {
		args = new String[3];
		args[0] = REGEXPATTERN;
		args[1] = ABCPATTERN;
		args[2] = fileName;
		grepApp.run(args, stdin, System.out);
		System.out.flush();
		String expected = args[1] + ":" + FILEDIRECTORYDONOTEXIST 
						+ args[2] + ":Hello Hello\n" 
						+ args[2] + ":ABC Hello\n";
		assertEquals(expected, baos.toString());
	}
	
	@Test
	public void grepSingleFileInvalidDirectoryFromRun() throws GrepException {
		args = new String[2];
		args[0] = REGEXPATTERN;
		args[1] = directory;
		grepApp.run(args, stdin, System.out);
		System.out.flush();
		assertEquals(args[1] + ":" + FILEDIRECTORYDONOTEXIST, baos.toString());
	}

	@Test
	public void grepNoPatternEmptyStringStdinFromRun() throws GrepException {
		args = new String[1];
		args[0] = "";
		thrown.expect(GrepException.class);
		thrown.expectMessage(NOPATTERN);
		grepApp.run(args, stdin, System.out);
		System.out.flush();
	}

	@Test
	public void grepNoPatternNullArgStdInFromRun() throws GrepException {
		args = new String[0];
		thrown.expect(GrepException.class);
		thrown.expectMessage(INVALIDCMD);
		grepApp.run(args, stdin, System.out);
		System.out.flush();
	}
	
	@Test
	public void InvalidgrepNoPatternMultipleFileFromRun() throws GrepException {
		args = new String[3];
		args[0] = "";
		args[1] = fileName;
		args[2] = fileName2;
		thrown.expect(GrepException.class);
		thrown.expectMessage(NOPATTERN);
		grepApp.run(args, stdin, System.out);
		System.out.flush();
	}
	
	@Test 
	public void invalidNoPatternProvidedFromRun() throws GrepException {
		args = new String[3];
		thrown.expect(GrepException.class);
		thrown.expectMessage(NOPATTERN);
		grepApp.run(args, null, System.out);
	}
	
	@Test
	public void invalidRegexFromRun() throws GrepException {
		args = new String[3];
		args[0] = "\\";
		
		thrown.expect(GrepException.class);
		thrown.expectMessage(INVALIDPATTERN);
		grepApp.run(args, stdin, System.out);
	}
	
	@Test
	public void invalidRegexFileFromRun() throws GrepException {
		args = new String[3];
		args[0] = "\\";
		args[1] = fileName2;
		args[2] = fileName;
		thrown.expect(GrepException.class);
		thrown.expectMessage(INVALIDPATTERN);
		grepApp.run(args, null, System.out);
	}

}
