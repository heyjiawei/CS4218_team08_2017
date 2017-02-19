package sg.edu.nus.comp.cs4218.impl.cmd;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Scanner;

import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;

public class PipeCommand implements Command {

	
	String subsequence;
	String firstSequence;
	String restSequence;
	
	public PipeCommand(String subsequence) {
		this.subsequence = subsequence.trim();
	}

	@Override
	public void evaluate(InputStream stdin, OutputStream stdout) throws AbstractApplicationException, ShellException {
		ByteArrayOutputStream firstOutputStream = new ByteArrayOutputStream();
		System.out.println(firstSequence);
		CallCommand call = new CallCommand(firstSequence);
		call.parse();
		call.evaluate(stdin, firstOutputStream);
		System.out.println(firstOutputStream.toString().getBytes()[2]);
		System.out.println("\n".getBytes()[0]);
		ByteArrayInputStream inForRest = new ByteArrayInputStream(firstOutputStream.toByteArray());
		
		System.out.println(restSequence);
		
		int nextPipeOpPos = findFirstPipeOperatorPosition(restSequence);
		if (nextPipeOpPos != -1) {
			// recursively execute pipe command
			PipeCommand call2 = new PipeCommand(restSequence);
			call2.parse();
			call2.evaluate(inForRest, stdout);
		} else {
			CallCommand call2 = new CallCommand(restSequence);
			call2.parse();
			call2.evaluate(inForRest, stdout);
		}
	}
	
	static String convertStreamToString(InputStream is) {
	    java.util.Scanner s = new Scanner(is).useDelimiter("\\A");
	    return s.hasNext() ? s.next() : "";
	}

	@Override
	public void terminate() {

	}

	public void parse() {
		
		this.splitCommand(this.subsequence);
	}
	
	// Split the command by first pipe operator not inside quote
	public void splitCommand(String s) {
		int firstPipeOpPos = findFirstPipeOperatorPosition(this.subsequence);
		this.firstSequence = this.subsequence.substring(0, firstPipeOpPos).trim();
		this.restSequence = this.subsequence.substring(firstPipeOpPos + 1).trim();
	}
	
	// Find the first pipe operator in a string that is not inside a single
	// or double quote. 
	// Assumes all quotes are in valid pairs.
	// Returns -1 if no pipe operator is found.
	public static int findFirstPipeOperatorPosition(String s) {
		Boolean isInSingleQuote = false;
		Boolean isInDoubleQuote = false;
		int firstPipeOpPos = -1;
		for (int i = 0; i < s.length(); i++){
		    char character = s.charAt(i);
		    if (character == '\'') {
		    	isInSingleQuote = !isInSingleQuote;
		    } else if (character == '\"') {
		    	isInDoubleQuote = !isInDoubleQuote;
		    } else if (character == '|' && !isInDoubleQuote && !isInSingleQuote) {
		    	firstPipeOpPos = i;
		    	break;
		    }
		}
		return firstPipeOpPos;
	}

}
