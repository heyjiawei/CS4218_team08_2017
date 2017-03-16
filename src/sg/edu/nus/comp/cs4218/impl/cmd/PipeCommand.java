package sg.edu.nus.comp.cs4218.impl.cmd;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
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
		CallCommand call = new CallCommand(firstSequence);
		call.parse();
		call.evaluate(stdin, firstOutputStream);
		ByteArrayInputStream inForRest = new ByteArrayInputStream(firstOutputStream.toByteArray());

		System.out.println(restSequence);

		int nextPipeOpPos = findFirstPipeOperatorPosition(restSequence);
		if (nextPipeOpPos == -1) {
			CallCommand call2 = new CallCommand(restSequence);
			call2.parse();
			call2.evaluate(inForRest, stdout);
		} else {
			// recursively execute pipe command
			PipeCommand call2 = new PipeCommand(restSequence);
			call2.parse();
			call2.evaluate(inForRest, stdout);
		}
	}

	static String convertStreamToString(InputStream inputStream) {
		java.util.Scanner scanner = new Scanner(inputStream);
		scanner.useDelimiter("\\A");
		String next = scanner.hasNext() ? scanner.next() : "";
		scanner.close();
		return next;
	}

	public void parse() {
		this.splitCommand(this.subsequence);
	}

	// Split the command by first pipe operator not inside quote
	public void splitCommand(String subsequence) {
		int firstPipeOpPos = findFirstPipeOperatorPosition(subsequence);
		this.firstSequence = this.subsequence.substring(0, firstPipeOpPos).trim();
		this.restSequence = this.subsequence.substring(firstPipeOpPos + 1).trim();
	}

	// Find the first pipe operator in a string that is not inside a single
	// or double quote.
	// Assumes all quotes are in valid pairs.
	// Returns -1 if no pipe operator is found.
	public static int findFirstPipeOperatorPosition(String subsequence) {
		Boolean isInSingleQuote = false;
		Boolean isInDoubleQuote = false;
		int firstPipeOpPos = -1;
		for (int i = 0; i < subsequence.length(); i++){
			char character = subsequence.charAt(i);
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

	@Override
	public void terminate() {
		// TODO Auto-generated method stub
	}
}
