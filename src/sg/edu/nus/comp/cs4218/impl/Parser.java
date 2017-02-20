package sg.edu.nus.comp.cs4218.impl;


import sg.edu.nus.comp.cs4218.exception.ShellException;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

	private static Pattern[] getCallCommandPatterns() {
		String patternNonKeyword = "\\s*[^\\n'\"`;|]+\\s*";
		String patternSQ = "\\s*'[^\n']*'\\s*";
		String patternBQ = "\\s*`[^\\n`]*`\\s*";
		String patternDQ = "\\s*\"[^\\n\"`]*\"\\s*"; // Double quoted content
		String patternBQinDQ = "\\s*\"[^\\n\"`]*`[^\\n]*`[^\\n\"`]*\"\\s*";

		// Preserve order for performance
		return new Pattern[] {
				Pattern.compile(patternNonKeyword),
				Pattern.compile(patternSQ),
				Pattern.compile(patternBQ),
				Pattern.compile(patternDQ),
				Pattern.compile(patternBQinDQ)
		};
	}

	private static Matcher getPatternMatch(Pattern[] patterns, String subcommand) {
		for (Pattern pattern: patterns) {
			Matcher matcher = pattern.matcher(subcommand);
			if (matcher.find() && matcher.start() == 0) {
				return matcher;
			}
		}

		return null;
	}

	public static String[][] parse(String cmdline) throws ShellException {
		Pattern[] compiledPatterns = getCallCommandPatterns();
		String substring = cmdline.trim(), command = "";
		int newEndIdx = 0;
		ArrayList<String> callCommands = new ArrayList<>();
		ArrayList<String[]> sequences = new ArrayList<>();
		boolean finishedParsing = false, reachedPipe, reachedSemicolon;

		while (!finishedParsing) {
			reachedPipe = substring.startsWith("|");
			reachedSemicolon = substring.startsWith(";");

			if (reachedPipe || reachedSemicolon) {
				callCommands.add(command.trim());
				command = "";
				substring = substring.substring(1).trim();
				if (reachedSemicolon) {
					sequences.add(callCommands.toArray(new String[callCommands.size()]));
					// reset values since it is now a new sequence
					callCommands.clear();
				}
			}

			Matcher matcher = getPatternMatch(compiledPatterns, substring);

			if (matcher == null) {
				finishedParsing = true;
			} else {
				newEndIdx = matcher.end();
				command += substring.substring(0, newEndIdx);
				substring = substring.substring(newEndIdx).trim();
			}
		}

		callCommands.add(command.trim());
		sequences.add(callCommands.toArray(new String[callCommands.size()]));

		if (!substring.matches("^\\s*$")) {
			throw new ShellException(ShellImpl.EXP_SYNTAX);
		}

		return sequences.toArray(new String[sequences.size()][]);
	}
}
