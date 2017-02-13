package sg.edu.nus.comp.cs4218.impl.app;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SedApplicationTest {
	
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
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		fail("Not yet implemented");
	}

}
