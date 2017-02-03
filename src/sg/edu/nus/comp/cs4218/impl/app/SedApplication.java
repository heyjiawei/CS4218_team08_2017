package sg.edu.nus.comp.cs4218.impl.app;

import java.io.InputStream;
import java.io.OutputStream;

import sg.edu.nus.comp.cs4218.app.Sed;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;

public class SedApplication implements Sed {

	@Override
	public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {
		// TODO Auto-generated method stub

	}

	@Override
	public String replaceFirstSubStringInFile(String args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String replaceAllSubstringsInFile(String args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String replaceFirstSubStringFromStdin(String args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String replaceAllSubstringsInStdin(String args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String replaceSubstringWithInvalidReplacement(String args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String replaceSubstringWithInvalidRegex(String args) {
		// TODO Auto-generated method stub
		return null;
	}

}
