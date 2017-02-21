package sg.edu.nus.comp.cs4218.impl;


import sg.edu.nus.comp.cs4218.exception.ShellException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Parser {

	// For parsing purposes, all patterns will start from beginning of string
	private final String patternNonKeyword = "^\\s*[^\\n'\"`;|]+\\s*";
	private final String patternDash = "^\\s*(-[A-Za-z]*)\\s*";
	private final String patternSQ = "^\\s*(\'[^\\n']*\')\\s*";
	private final String patternBQ = "^\\s*(`[^\\n`]*`)\\s*";
	private final String patternDQ = "^\\s*(\"[^\\n\"`]*\")\\s*";
	private final String patternBQinDQ = "^\\s*(\"[^\\n\"`]*`[^\\n]*`[^\\n\"`]*\")\\s*";
	private final String patternUQ = "^\\s*([^\\s\"'`\\n;|<>]*)\\s*";

	private final String patternPipe = "^\\s*\\|";
	private final String patternSemicolon = "^\\s*;";
	private final String patternRedir = "^\\s*[<>]\\s*";

	private Pattern[] getCallCommandArgumentPatterns() {
		// Preserve order for performance
		return new Pattern[] {
				Pattern.compile(patternRedir),
				Pattern.compile(patternDash),
				Pattern.compile(patternSQ),
				Pattern.compile(patternBQ),
				Pattern.compile(patternDQ),
				Pattern.compile(patternBQinDQ),
				Pattern.compile(patternUQ),
		};
	}

	private Pattern[] getCallCommandPatterns() {
		// Preserve order for performance
		return new Pattern[] {
				Pattern.compile(patternNonKeyword),
				Pattern.compile(patternSQ),
				Pattern.compile(patternBQ),
				Pattern.compile(patternDQ),
				Pattern.compile(patternBQinDQ)
		};
	}

	private Matcher getPatternMatch(Pattern[] patterns, String subcommand) {
		for (Pattern pattern: patterns) {
			Matcher matcher = pattern.matcher(subcommand);
			if (matcher.find()) {
				return matcher;
			}
		}

		return null;
	}

	private void checkEmptyCommand(String cmd, String restOfCmd)
			throws ShellException {
		if (cmd.trim().isEmpty()) {
			throw new ShellException(ShellImpl.EXP_SYNTAX + restOfCmd);
		}
	}

	public String[][] parse(String cmdline) throws ShellException {
		Pattern[] compiledPatterns = getCallCommandPatterns();
		String substring = cmdline, command = "";
		int newEndIdx;
		ArrayList<String> callCommands = new ArrayList<>();
		ArrayList<String[]> sequences = new ArrayList<>();
		boolean finishedParsing = false;
		Pattern patternPipe = Pattern.compile(this.patternPipe),
				patternSemicolon = Pattern.compile(this.patternSemicolon);
		Matcher matcherPipe, matcherSemicolon;

		while (!finishedParsing) {
			matcherPipe = patternPipe.matcher(substring);
			matcherSemicolon = patternSemicolon.matcher(substring);

			if (matcherPipe.find()) {
				checkEmptyCommand(command, substring);
				callCommands.add(command.trim());
				command = "";
				substring = substring.substring(matcherPipe.end());
			} else if (matcherSemicolon.find()) {
				callCommands.add(command.trim());
				command = "";
				substring = substring.substring(matcherSemicolon.end());
				sequences.add(callCommands.toArray(new String[callCommands.size()]));
				// reset values since it is now a new sequence
				callCommands.clear();
			}

			Matcher matcher = getPatternMatch(compiledPatterns, substring);

			if (matcher == null) {
				finishedParsing = true;
			} else {
				newEndIdx = matcher.end();
				command += substring.substring(0, newEndIdx);
				substring = substring.substring(newEndIdx);
			}
		}

		callCommands.add(command.trim());
		sequences.add(callCommands.toArray(new String[callCommands.size()]));

		if (!substring.matches("^\\s*$")) {
			throw new ShellException(ShellImpl.EXP_SYNTAX + substring);
		}

		return sequences.toArray(new String[sequences.size()][]);
	}

	private void checkValidRedirection(Vector<String> atoms)
			throws ShellException {
		boolean hasInputRedir = false, hasOutputRedir = false;
		String atom;

		for (int index = 0; index < atoms.size(); index++) {
			atom = atoms.get(index);

			if (atom.equals("<")) {
				if (hasInputRedir) {
					throw new ShellException(ShellImpl.EXP_MULTIPLE_FILE_REDIR);
				}

				if (index == atoms.size() - 1 ||
						atoms.get(index + 1).equals(">")) {
					throw new ShellException(ShellImpl.EXP_SYNTAX + "<");
				}

				hasInputRedir = true;
			} else if (atom.equals(">")) {
				if (hasOutputRedir) {
					throw new ShellException(ShellImpl.EXP_MULTIPLE_FILE_REDIR);
				}

				if (index == atoms.size() - 1 ||
						atoms.get(index + 1).equals("<")) {
					throw new ShellException(ShellImpl.EXP_SYNTAX + ">");
				}

				hasOutputRedir = true;
			}
		}
	}

	public Vector<String> parseCallCommand(String cmdline)
			throws ShellException {
		Pattern[] compiledPatterns = getCallCommandArgumentPatterns();
		String substring = cmdline + ' ', atom;
		int newEndIdx;
		Vector<String> atoms = new Vector<>();

		while (!substring.isEmpty()) {
			Matcher matcher = getPatternMatch(compiledPatterns, substring);

			newEndIdx = matcher.end();
			atom = substring.substring(0, newEndIdx);
			substring = substring.substring(newEndIdx);
			if (!atom.endsWith(" ")) {
				throw new ShellException(ShellImpl.EXP_SYNTAX + substring);
			}
			atoms.add(atom.trim());
		}

		if (!substring.matches("^\\s*$")) {
			throw new ShellException(ShellImpl.EXP_SYNTAX + substring);
		}

		checkValidRedirection(atoms);

		return atoms;
	}
}
