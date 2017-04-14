package Hackathon;

import org.junit.*;
import org.junit.rules.ExpectedException;

import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.CatException;
import sg.edu.nus.comp.cs4218.exception.PwdException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.exception.SortException;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class Hackathon {
    private ByteArrayOutputStream outContent;
    private ShellImpl shell;
    
    @Rule
	public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        shell = new ShellImpl();
        outContent = new ByteArrayOutputStream();
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * Command Substitution does not work for command involving the reading of file.
     * Ref to Page 9 Section 7.1.8 for Command Substitution
     */
    @Test
    public void testSubCMDThroughShell() throws AbstractApplicationException, ShellException, IOException {
        String expected = "Raccoon a oh rarely exaggeratedly.\n";
        expected += "Away pounded paid much since.\n";
        expected += "Private rabbit alas dragonfly outside.\n";
        expected += "Interbred made the oh ahead.\n";
        expected += "Swung far much and tamarin.\n";
        expected += "Or since hey less rat.\n";
        expected += "And heinously yikes leopard.\n";
        expected += "Rat smoked goodness alas.\n";
        expected += "Some much heartless crud.\n";
        expected += "Without fox arose the less.";

        shell.parseAndEvaluate("cat `cat TestFiles/SubCMD-tail2.txt`", outContent);
        assertEquals(expected, outContent.toString());
    }

    /**
     * File with empty line did not have output after sorting.
     * Ref to Page 12 Section 7.2.9 for Sort
     */
    @Test
    public void testSortWithEmptyFileWithNewLine() throws AbstractApplicationException, ShellException, IOException {
        String expected = "\n";
        //Environment.currentDirectory = System.getProperty("user.dir");
        shell.parseAndEvaluate("sort -n result1.txt", outContent);
        assertEquals(expected, outContent.toString());
    }

    /**
     * Filename with space are unable to be read correctly.
     */
    // Invalid test case filed in bug report number 3
    // @Test
    // public void testEscapeKeyForSpaceNotIdentified() throws AbstractApplicationException, ShellException, IOException {
    //     String expected = "       9 co copy.txt\n";
    //     shell.parseAndEvaluate("wc -l co\\ copy.txt", outContent);
    //     assertEquals(expected, outContent.toString());
    // }

    /**
     * Missing file for sort
     * Ref to Page 12, Section 7.2.9 for Sort.
     */
    @Test
    public void testFileNotFound() throws AbstractApplicationException, ShellException, IOException {
    	thrown.expect(SortException.class);
        thrown.expectMessage("File missing.txt does not exist.");
    	shell.parseAndEvaluate("sort missing.txt", outContent);
    }

    /**
     * No option is allowed for pwd. However there is no error thrown when an option is used together with pwd.
     * Ref to Page 10, Section 7.2.3 where the command format is "pwd"
     */
    @Test
    public void testPwdExtraArgs() throws IOException, AbstractApplicationException, ShellException {
        String cmd = "pwd -potato";
        thrown.expect(PwdException.class);
        thrown.expectMessage("Pwd does not accept arguments.");
        shell.parseAndEvaluate(cmd, outContent);
    }

    /**
     * IO Redirection is stuck after not being able to find file. Should give error message such as "No such file or directory"
     * Ref to Page 8, Section 7.1.6 for IO Redirection where the semantics mentioned that "If the file specified for input redirection does not exist, throw an exception."
     */
    @Test
    public void testIORedirectoryFromWrongDirectory() throws AbstractApplicationException, ShellException, IOException {
    	thrown.expect(CatException.class);
		thrown.expectMessage("Could not read file");
    	shell.parseAndEvaluate("cat GlobbingTest1/* > TestFiles/SystemTesting/result.txt", outContent);
    }
}
