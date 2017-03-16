package sg.edu.nus.comp.cs4218.impl.cmd;

import static org.junit.Assert.*;

import org.junit.Test;

import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;

public class PipeCommandTest {
	
	@Test
	public void testSplitCommand() throws AbstractApplicationException, ShellException {
		String cmd = "echo \"cd\" | grep c";
		PipeCommand pipeCommand = new PipeCommand(cmd);
		pipeCommand.parse();
		
		assertEquals("echo \"cd\"", pipeCommand.firstSequence);
		assertEquals("grep c", pipeCommand.restSequence);
		
		
		cmd = "echo \"test\" | cat | cat";
		pipeCommand = new PipeCommand(cmd);
		pipeCommand.parse();
		
		assertEquals("echo \"test\"", pipeCommand.firstSequence);
		assertEquals("cat | cat", pipeCommand.restSequence);

	}
	
	@Test
	public void testFindPipeOperatorPos() throws AbstractApplicationException, ShellException {
		String cmd = "echo \"cd\" | grep c";
		int firstPipeOpPos = PipeCommand.findFirstPipeOperatorPosition(cmd);
		assertEquals(10, firstPipeOpPos);
		
		cmd = "echo \"c|d\" | grep c";
		firstPipeOpPos = PipeCommand.findFirstPipeOperatorPosition(cmd);
		assertEquals(11, firstPipeOpPos);
		
		cmd = "echo \"c|d\"";
		firstPipeOpPos = PipeCommand.findFirstPipeOperatorPosition(cmd);
		assertEquals(-1, firstPipeOpPos);
		
		cmd = "echo 'c|d' | grep c";
		firstPipeOpPos = PipeCommand.findFirstPipeOperatorPosition(cmd);
		assertEquals(11, firstPipeOpPos);
		

	}

}
