package sg.edu.nus.comp.cs4218.impl.app;

import java.io.InputStream;
import java.io.OutputStream;

import sg.edu.nus.comp.cs4218.app.Grep;
import sg.edu.nus.comp.cs4218.exception.GrepException;

public class GrepApplication implements Grep {

	@Override
	public void run(String[] args, InputStream stdin, OutputStream stdout) throws GrepException {
		// TODO Auto-generated method stub

	}

	@Override
	public String grepFromStdin(String args, InputStream stdin) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String grepFromOneFile(String args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String grepFromMultipleFiles(String args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String grepInvalidPatternInStdin(String args, InputStream stdin) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String grepInvalidPatternInFile(String args) {
		// TODO Auto-generated method stub
		return null;
	}

}
