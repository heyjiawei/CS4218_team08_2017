package sg.edu.nus.comp.cs4218.impl.app;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;

import sg.edu.nus.comp.cs4218.app.Sort;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.SortException;

public class SortApplication implements Sort {

	private final String newLine = System.getProperty("line.separator");
	private final String treatFirstWordAsNumberFlag = "-n";

	Comparator<String> genericStringComparator = new Comparator<String>() {
		@Override
		public int compare(String firstString, String secondString) {
			return firstString.compareTo(secondString);
		}
	};

	Comparator<String> stringContainingSpecialComparator = new Comparator<String>() {
		@Override
		public int compare(String firstString, String secondString) {
			String[] firstStringCharacters = firstString.split("");
			String[] secondStringCharacters = secondString.split("");
			int smallerStringLength = Math.min(firstString.length(), secondString.length());

			for (int i = 0; i < smallerStringLength; i++) {
				String firstStringCharacter = firstStringCharacters[i];
				String secondStringCharacter = secondStringCharacters[i];
				if (firstStringCharacter.equals(secondStringCharacter)) {
					continue;
				}
				// only the first character is a special character
				if (!isAlphaNumeric(firstStringCharacter) &&
						isAlphaNumeric(secondStringCharacter)) {
					return -1;
				// only the second character is a special character
				} else if (isAlphaNumeric(firstStringCharacter) &&
						!isAlphaNumeric(secondStringCharacter)) {
					return 1;
				// both characters are special or both characters are
				// nonspecial characters
				} else {
					return firstStringCharacter.compareTo(secondStringCharacter);
				}
			}
			// shorter sequence is smaller
			if (firstString.length() < secondString.length()) {
				return -1;
			} else if (firstString.length() > secondString.length()) {
				return 1;
			} else {
				return 0;
			}
		}
	};

	Comparator<String> firstWordAsNumberComparator = new Comparator<String>() {
		@Override
		public int compare(String firstString, String secondString) {
			Integer firstStringFirstWordAsInteger =
					getFirstWordOfStringAsInteger(firstString);
			Integer secondStringFirstWordAsInteger =
					getFirstWordOfStringAsInteger(secondString);
			// both strings have numbers as their first words
			if (firstStringFirstWordAsInteger != null &&
					secondStringFirstWordAsInteger != null) {
				int numberComparison = firstStringFirstWordAsInteger.
						compareTo(secondStringFirstWordAsInteger);
				// the numbers are the same, compare using the rest of the first word
				if (numberComparison == 0) {
					return stringContainingSpecialComparator.compare(firstString, secondString);
				} else {
					return numberComparison;
				}
			// only the first string has a number as its first word
			} else if (firstStringFirstWordAsInteger != null &&
					secondStringFirstWordAsInteger == null) {
				// special characters go before numbers
				if (secondString.length() > 0 &&
						!isAlphaNumeric(secondString.substring(0, 1))) {
					return 1;
				} else {
					return -1;
				}
			// only the second string has a number as its first word
			} else if (firstStringFirstWordAsInteger == null &&
					secondStringFirstWordAsInteger != null) {
				// special characters go before numbers
				if (firstString.length() > 0 &&
						!isAlphaNumeric(firstString.substring(0, 1))) {
					return -1;
				} else {
					return 1;
				}
			// both strings do not have numbers as their first words
			} else {
				return stringContainingSpecialComparator.compare(firstString, secondString);
			}
		}
	};

	@Override
	public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {
		if (stdout == null) {
			throw new SortException("No output stream provided");
		}
		if (args == null || args.length == 0) {
			if (stdin == null) {
				throw new SortException("No input provided");
			}
			ByteArrayOutputStream contents = new ByteArrayOutputStream();
			byte[] buffer = new byte[8192];
			int bytesRead;
			try {
				while ((bytesRead = stdin.read(buffer)) != -1) {
				    contents.write(buffer, 0, bytesRead);
				}
				String toSort = contents.toString();
				stdout.write(sortAll(toSort).getBytes());
			} catch (IOException e) {
				throw new SortException("IOException");
			}
		} else {
			String firstArgument = args[0];
			if (firstArgument == null) {
				throw new SortException("First argument provided is null");
			}
			// check whether first argument is -n flag
			if (firstArgument.equals(treatFirstWordAsNumberFlag)) {
				// assume second argument is filename
				if (args.length < 2 || args[1] == null) {
					throw new SortException("No file path provided");
				}
				try {
					String toSort = convertFileToString(args[1]);
					// add flag to get string sorted with first word as number
					toSort = treatFirstWordAsNumberFlag + newLine + toSort;
					stdout.write(sortAll(toSort).getBytes());
				} catch (IOException e) {
					throw new SortException("IOException");
				}
			} else {
				// assume first argument is filename
				try {
					String toSort = convertFileToString(firstArgument);
					stdout.write(sortAll(toSort).getBytes());
				} catch (IOException e) {
					throw new SortException("IOException");
				}
			}
		}
	}

	@Override
	public String sortStringsSimple(String toSort) {
		return sort(toSort, genericStringComparator);
	}

	@Override
	public String sortStringsCapital(String toSort) {
		return sort(toSort, genericStringComparator);
	}

	@Override
	public String sortNumbers(String toSort) {
		if (shouldTreatFirstWordOfLineAsNumber(toSort)) {
			toSort = removeFirstLineFromString(toSort);
			return sort(toSort, firstWordAsNumberComparator);
		} else {
			return sort(toSort, genericStringComparator);
		}
	}

	@Override
	public String sortSpecialChars(String toSort) {
		return sort(toSort, genericStringComparator);
	}

	@Override
	public String sortSimpleCapital(String toSort) {
		return sort(toSort, genericStringComparator);
	}

	@Override
	public String sortSimpleNumbers(String toSort) {
		if (shouldTreatFirstWordOfLineAsNumber(toSort)) {
			toSort = removeFirstLineFromString(toSort);
			return sort(toSort, firstWordAsNumberComparator);
		} else {
			return sort(toSort, genericStringComparator);
		}
	}

	@Override
	public String sortSimpleSpecialChars(String toSort) {
		return sort(toSort, stringContainingSpecialComparator);
	}

	@Override
	public String sortCapitalNumbers(String toSort) {
		if (shouldTreatFirstWordOfLineAsNumber(toSort)) {
			toSort = removeFirstLineFromString(toSort);
			return sort(toSort, firstWordAsNumberComparator);
		} else {
			return sort(toSort, genericStringComparator);
		}
	}

	@Override
	public String sortCapitalSpecialChars(String toSort) {
		return sort(toSort, stringContainingSpecialComparator);
	}

	@Override
	public String sortNumbersSpecialChars(String toSort) {
		if (shouldTreatFirstWordOfLineAsNumber(toSort)) {
			toSort = removeFirstLineFromString(toSort);
			return sort(toSort, firstWordAsNumberComparator);
		} else {
			return sort(toSort, stringContainingSpecialComparator);
		}
	}

	@Override
	public String sortSimpleCapitalNumber(String toSort) {
		if (shouldTreatFirstWordOfLineAsNumber(toSort)) {
			toSort = removeFirstLineFromString(toSort);
			return sort(toSort, firstWordAsNumberComparator);
		} else {
			return sort(toSort, genericStringComparator);
		}
	}

	@Override
	public String sortSimpleCapitalSpecialChars(String toSort) {
		return sort(toSort, genericStringComparator);
	}

	@Override
	public String sortSimpleNumbersSpecialChars(String toSort) {
		if (shouldTreatFirstWordOfLineAsNumber(toSort)) {
			toSort = removeFirstLineFromString(toSort);
			return sort(toSort, firstWordAsNumberComparator);
		} else {
			return sort(toSort, stringContainingSpecialComparator);
		}
	}

	@Override
	public String sortCapitalNumbersSpecialChars(String toSort) {
		if (shouldTreatFirstWordOfLineAsNumber(toSort)) {
			toSort = removeFirstLineFromString(toSort);
			return sort(toSort, firstWordAsNumberComparator);
		} else {
			return sort(toSort, stringContainingSpecialComparator);
		}
	}

	@Override
	public String sortAll(String toSort) {
		if (shouldTreatFirstWordOfLineAsNumber(toSort)) {
			toSort = removeFirstLineFromString(toSort);
			return sort(toSort, firstWordAsNumberComparator);
		} else {
			return sort(toSort, stringContainingSpecialComparator);
		}
	}

	/**
	 * Joins the given String array into a single string using the given
	 * delimiter.
	 *
	 * @param stringsToJoin
	 *            The strings to be joined together
	 * @param delimiter
	 *            The delimiter used to join the strings
	 * @return The strings joined with the given delimiter
	 */
	private String joinWithDelimiter(String[] stringsToJoin, String delimiter) {
		String sortedString = "";
		for (int i = 0; i < stringsToJoin.length - 1; i++) {
			sortedString += stringsToJoin[i];
			sortedString += newLine;
		}
		sortedString += stringsToJoin[stringsToJoin.length - 1];
		return sortedString;
	}

	/**
	 * Checks whether the given string is alphanumeric.
	 *
	 * @param string
	 *            The string that is to be checked
	 * @return Whether the given string is alphanumeric
	 */
	private boolean isAlphaNumeric(String string) {
		return string.matches("^[a-zA-Z0-9]+$");
	}

	/**
	 * Gets the first word of the given string as an integer. The first
	 * word is considered to be valid as an integer if it starts with an
	 * integer (e.g. 34 and 34ab are valid, a34b and ab34 are not)
	 *
	 * @param string
	 *            The string that is to be checked
	 * @return The first word as a integer if valid, and null if not
	 */
	private Integer getFirstWordOfStringAsInteger(String string) {
		String[] stringWords = string.split(newLine);
		if (stringWords.length == 0) {
			return null;
		}
		try {
			String firstWord = stringWords[0];
			String frontPartOfWordAsNumber = "";
			// extract front part of word that is a number
			for (int i = 0; i < firstWord.length(); i++) {
				String currentCharacter = firstWord.substring(i, i + 1);
				if (currentCharacter.matches("[0-9]")) {
					frontPartOfWordAsNumber += currentCharacter;
				} else {
					break;
				}
			}
			// word does not have number at front part
			if (frontPartOfWordAsNumber.equals("")) {
				return null;
			} else {
				return Integer.parseInt(frontPartOfWordAsNumber);
			}
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/**
	 * Removes the first line from a given string. It is assumed that the
	 * given string is contains lines separated by a newline character.
	 *
	 * @param string
	 *            The string from which the first line is to be removed.
	 * @return The string with the first line removed, if any.
	 */
	private String removeFirstLineFromString(String string) {
		String[] toSortArray = string.split(newLine);
		if (toSortArray.length < 2) {
			return "";
		}
		String[] arrayWithoutParameter = Arrays.copyOfRange(
				toSortArray, 1, toSortArray.length);
		return joinWithDelimiter(arrayWithoutParameter, newLine);
	}

	/**
	 * Determines whether the first word of each line in the lines given
	 * should be treated as a number when sorting. Returns true if the
	 * first line is "-n" and false otherwise.
	 *
	 * @param lines
	 *            The lines that are to be sorted, possibly including the
	 *            -n parameter as the first line
	 * @return Whether the first word of each line should be treated as a number
	 */
	private boolean shouldTreatFirstWordOfLineAsNumber(String lines) {
		String[] lineArray = lines.split(newLine);
		if (lineArray.length == 0) {
			return false;
		}
		String firstLine = lineArray[0];
		return firstLine.equals(treatFirstWordAsNumberFlag);
	}

	/**
	 * Takes in the string to sort, sorts it in the order specified by
	 * the given comparator, and returns the sorted string.
	 *
	 * @param toSort
	 *            The string to be sorted. Assumed to contain a number of
	 *            strings to sort, each separated by a new line character
	 * @param comparator
	 *            The comparator which determines how to order the elements during
	 *            sorting
	 */
	private String sort(String toSort, Comparator<String> comparator) {
		String[] strings = toSort.split(newLine);
		mergeSort(strings, 0, strings.length - 1, comparator);
		return joinWithDelimiter(strings, newLine);
	}

	/**
	 * Performs a mergesort on a subarray of the given array of strings, bounded
	 * by the given left boundary and right boundary indices in the given array.
	 * The order in which the elements are ordered is determined by the given
	 * comparator.
	 *
	 * @param strings
	 *            The array of strings to be sorted
	 * @param leftBound
	 *            The index of the main array at which the subarray starts
	 * @param rightBound
	 *            The index of the main array at which the subarray ends
	 * @param comparator
	 *            The comparator which determines how to order the elements during
	 *            sorting
	 */
	private void mergeSort(String[] strings, int leftBound, int rightBound,
			Comparator<String> comparator) {
		if (leftBound == rightBound) {
			return;
		}
		int midpoint = (leftBound + rightBound) / 2;

		mergeSort(strings, leftBound, midpoint, comparator);
		mergeSort(strings, midpoint + 1, rightBound, comparator);
		merge(strings, leftBound, rightBound, comparator);
	}

	/**
	 * Performs a merge on a subarray of the given array of strings, bounded
	 * by the given left boundary and right boundary indices in the given array.
	 * The merged subarray will be in a sorted order determined by the given
	 * comparator.
	 *
	 * @param strings
	 *            The array of strings to be sorted
	 * @param leftBound
	 *            The index of the main array at which the subarray starts
	 * @param rightBound
	 *            The index of the main array at which the subarray ends
	 * @param comparator
	 *            The comparator which determines how to order the elements during
	 *            sorting
	 */
	private void merge(String[] strings, int leftBound, int rightBound,
			Comparator<String> comparator) {
		if (leftBound == rightBound) {
			return;
		}

		int midpoint = (leftBound + rightBound) / 2 + 1;
		int numberOfElements = rightBound - leftBound + 1;
		String[] mergedPartition = new String[numberOfElements];
		int leftPartitionHeadIndex = leftBound;
		int leftPartitionUpperBound = midpoint - 1;
		int rightPartitionHeadIndex = midpoint;
		int rightPartitionUpperBound = rightBound;
	    String leftPartitionHeadValue;
		String rightPartitionHeadValue;
		leftPartitionHeadValue = strings[leftPartitionHeadIndex];
		rightPartitionHeadValue = strings[rightPartitionHeadIndex];
		boolean isLeftPartitionDone = false;
		boolean isRightPartitionDone = false;

		for (int i = 0; i < numberOfElements; i++) {
			int compareValue = comparator.compare(rightPartitionHeadValue, leftPartitionHeadValue);
			if (isRightPartitionDone || (compareValue > 0 && !isLeftPartitionDone)) {
				mergedPartition[i] = leftPartitionHeadValue;
				if (leftPartitionHeadIndex < leftPartitionUpperBound) {
					leftPartitionHeadIndex++;
					leftPartitionHeadValue = strings[leftPartitionHeadIndex];
				} else {
					isLeftPartitionDone = true;
				}
			} else {
				mergedPartition[i] = rightPartitionHeadValue;
				if (rightPartitionHeadIndex < rightPartitionUpperBound) {
					rightPartitionHeadIndex++;
					rightPartitionHeadValue = strings[rightPartitionHeadIndex];
				} else {
					isRightPartitionDone = true;
				}
			}
		}
		for (int j = 0; j < numberOfElements; j++) {
			strings[leftBound + j] = mergedPartition[j];
		}
	}

	/**
	 * Converts the file found at the given input file path string to
	 * a string.
	 *
	 * @param filePathString
	 *            The path to the file, represented as a string
	 * @return The string that the file has been converted to
	 * @throws IOException
	 *             If there is an error reading the file
	 */
	private String convertFileToString(String filePathString) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(filePathString));
		return new String(encoded, StandardCharsets.UTF_8);
	}
}
