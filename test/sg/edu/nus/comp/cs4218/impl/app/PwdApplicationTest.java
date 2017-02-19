package sg.edu.nus.comp.cs4218.impl.app;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.PwdException;

public class PwdApplicationTest {

	private InputStream inputStream = null;
	private ByteArrayOutputStream outputStream = null;
	private PwdApplication pwdApplication;
	private final String initialWorkingDirectory = Environment.currentDirectory;
	private final String newLine = System.getProperty("line.separator");

	@Before
	public void setUp() throws Exception {
		pwdApplication = new PwdApplication();
	}

	@After
	public void tearDown() throws Exception {
		Environment.currentDirectory = initialWorkingDirectory;
		System.setProperty("user.dir", initialWorkingDirectory);
	}

	@Test(expected = PwdException.class)
	public void testThrowWhenStdOutputNull() throws PwdException {
		String[] args = null;
		pwdApplication.run(args, inputStream, outputStream);
	}

	@Test
	public void testPwd() throws PwdException {
		String[] args = null;
		outputStream = new ByteArrayOutputStream();
		pwdApplication.run(args, inputStream, outputStream);
		assertEquals(Environment.currentDirectory + newLine, outputStream.toString());
	}

	@Test
	public void testPwdAfterChangingEnvironmentCurrentDirectory() throws PwdException {
		String[] args = null;
		outputStream = new ByteArrayOutputStream();
		Environment.currentDirectory = "~";
		pwdApplication.run(args, inputStream, outputStream);
		assertEquals(Environment.currentDirectory + newLine, outputStream.toString());
	}
}
