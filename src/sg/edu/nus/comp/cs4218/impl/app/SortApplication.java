package sg.edu.nus.comp.cs4218.impl.app;

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

public class SortApplication implements Sort {

	private final String newLine = System.getProperty("line.separator");

	Comparator<String> genericStringComparator = new Comparator<String>() {
		@Override
		public int compare(String firstString, String secondString) {
			return firstString.compareTo(secondString);
		}
	};

	@Override
	public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {
		// TODO Auto-generated method stub
		
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
					return firstStringFirstWordAsInteger.
							compareTo(secondStringFirstWordAsInteger);
				// only the first string has a number as its first word
				} else if (firstStringFirstWordAsInteger != null &&
						secondStringFirstWordAsInteger == null) {
					return 1;
				// only the second string has a number as its first word
				} else if (firstStringFirstWordAsInteger == null &&
						secondStringFirstWordAsInteger != null) {
					return -1;
				// both strings do not have numbers as their first words
				} else {
					return firstString.compareTo(secondString);
				}
			}
		};
		if (shouldTreatFirstWordOfLineAsNumber(toSort)) {
			// remove the -n parameter before sorting
			String[] toSortArray = toSort.split(newLine);
			String[] arrayWithoutParameter = Arrays.copyOfRange(
					toSortArray, 1, toSortArray.length);
			toSort = String.join(newLine, arrayWithoutParameter);

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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String sortSimpleSpecialChars(String toSort) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String sortCapitalNumbers(String toSort) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String sortCapitalSpecialChars(String toSort) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String sortNumbersSpecialChars(String toSort) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String sortSimpleCapitalNumber(String toSort) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String sortSimpleCapitalSpecialChars(String toSort) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String sortSimpleNumbersSpecialChars(String toSort) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String sortCapitalNumbersSpecialChars(String toSort) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String sortAll(String toSort) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Gets the first word of the given string as an integer.
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
			return Integer.parseInt(stringWords[0]);
		} catch (NumberFormatException e) {
			return null;
		}
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
		return firstLine.equals("-n");
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
		return String.join(newLine, strings);
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
	private String[] convertFileToStringArray(String filePathString) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(filePathString));
		return new String(encoded, StandardCharsets.UTF_8).split(newLine);
	}
}
