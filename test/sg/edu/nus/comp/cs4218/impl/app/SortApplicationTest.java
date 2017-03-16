package sg.edu.nus.comp.cs4218.impl.app;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.SortException;

// Assumptions made in tests:
// 1. 'Simple' letters means lowercase letters
// 2. The sorting methods only process strings that only contain the types of characters
//    that are contained in the return String. For example, sortStringSimple, which 
//    returns a string containing only simple letters, only takes in strings that contain
//    only simple letters.
// 3. When -n is specified, the first word of a line is treated as a number only if
//    the first part of it is an integer (e.g. 34 and 34abc will be recognized as
//    a number, but abc34 or a34bc will not be recognized). -n has to be specified
//    by being the first line in the input string.
@SuppressWarnings("PMD.LongVariable")
public class SortApplicationTest {

	private static final String ALL_TXT_FILE = "all.txt";
	private static final String TEST_FILES_PATH = "test_inputs/sort/";
	private static final String NEW_LINE = System.getProperty("line.separator");
	private InputStream inputStream;
	private ByteArrayOutputStream outputStream;
	private SortApplication sortApplication;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setUp() throws Exception {
		sortApplication = new SortApplication();
	}

	@Test
	public void testSortStringsSimple() throws IOException {
		String simpleStringsFilePath = TEST_FILES_PATH + "simple.txt";
		String sortedSimpleStringsFilePath = TEST_FILES_PATH + "simple_sorted.txt";
		String unsortedString = convertFileToString(simpleStringsFilePath);
		String sortedString = sortApplication.sortStringsSimple(unsortedString);
		String sortedSimpleString = convertFileToString(sortedSimpleStringsFilePath);
		assertEquals(sortedSimpleString, sortedString);
	}

	@Test
	public void testSortStringsSimpleEmptyString() {
		String sortedString = sortApplication.sortStringsSimple("");
		assertEquals("", sortedString);
	}

	@Test
	public void testSortStringsCapital() throws IOException {
		String capitalStringsFilePath = TEST_FILES_PATH + "capital.txt";
		String sortedCapitalStringsFilePath = TEST_FILES_PATH + "capital_sorted.txt";
		String unsortedCapitalString = convertFileToString(capitalStringsFilePath);
		String sortedString = sortApplication.sortStringsCapital(unsortedCapitalString);
		String sortedCapitalString = convertFileToString(sortedCapitalStringsFilePath);
		assertEquals(sortedCapitalString, sortedString);
	}

	@Test
	public void testSortStringsCapitalEmptyString() {
		String sortedString = sortApplication.sortStringsCapital("");
		assertEquals("", sortedString);
	}

	@Test
	public void testSortNumbers() throws IOException {
		String numbersFilePath = TEST_FILES_PATH + "numbers.txt";
		String sortedNumbersFilePath = TEST_FILES_PATH + "numbers_sorted.txt";
		String unsortedNumbersString = convertFileToString(numbersFilePath);
		String sortedString = sortApplication.sortNumbers(unsortedNumbersString);
		String sortedNumbersString = convertFileToString(sortedNumbersFilePath);
		assertEquals(sortedNumbersString, sortedString);
	}

	@Test
	public void testSortNumbersTreatFirstWordAsNumber() throws IOException {
		String numbersFilePath = TEST_FILES_PATH + "numbers.txt";
		String sortedNumbersFilePath = TEST_FILES_PATH + 
				"numbers_sorted_treating_first_word_as_number.txt";
		String unsortedNumbersString = convertFileToString(numbersFilePath);
		String sortedString = sortApplication.sortNumbers(
				"-n" + NEW_LINE + unsortedNumbersString);
		String sortedNumbersString = convertFileToString(sortedNumbersFilePath);
		assertEquals(sortedNumbersString, sortedString);
	}

	@Test
	public void testSortNumbersEmptyString() {
		String sortedString = sortApplication.sortNumbers("");
		assertEquals("", sortedString);
	}

	@Test
	public void testSortSpecialChars() throws IOException {
		String specialFilePath = TEST_FILES_PATH + "special.txt";
		String sortedSpecialsFilePath = TEST_FILES_PATH + "special_sorted.txt";
		String unsortedSpecialString = convertFileToString(specialFilePath);
		String sortedString = sortApplication.sortSpecialChars(unsortedSpecialString);
		String sortedSpecialString = convertFileToString(sortedSpecialsFilePath);
		assertEquals(sortedSpecialString, sortedString);
	}

	@Test
	public void testSortSpecialCharsEmptyString() {
		String sortedString = sortApplication.sortSpecialChars("");
		assertEquals("", sortedString);
	}

	@Test
	public void testSortSimpleCapital() throws IOException {
		String simpleCapitalFilePath = TEST_FILES_PATH + "simple_capital.txt";
		String sortedSimpleCapitalFilePath = TEST_FILES_PATH + "simple_capital_sorted.txt";
		String unsortedSimpleCapitalString = convertFileToString(simpleCapitalFilePath);
		String sortedString = sortApplication.sortSimpleCapital(unsortedSimpleCapitalString);
		String sortedSimpleCapitalString = convertFileToString(sortedSimpleCapitalFilePath);
		assertEquals(sortedSimpleCapitalString, sortedString);
	}

	@Test
	public void testSortSimpleCapitalEmptyString() {
		String sortedString = sortApplication.sortSimpleCapital("");
		assertEquals("", sortedString);
	}

	@Test
	public void testSortSimpleNumbers() throws IOException {
		String simpleNumbersFilePath = TEST_FILES_PATH + "simple_numbers.txt";
		String sortedSimpleNumbersFilePath = TEST_FILES_PATH +
				"simple_numbers_sorted.txt";
		String unsortedSimpleNumbersString = convertFileToString(simpleNumbersFilePath);
		String sortedString = sortApplication.sortSimpleNumbers(unsortedSimpleNumbersString);
		String sortedSimpleNumbersString = convertFileToString(sortedSimpleNumbersFilePath);
		assertEquals(sortedSimpleNumbersString, sortedString);
	}

	@Test
	public void testSortSimpleNumbersTreatFirstWordAsNumber() throws IOException {
		String simpleNumbersFilePath = TEST_FILES_PATH + "simple_numbers.txt";
		String sortedSimpleNumbersFilePath = TEST_FILES_PATH + 
				"simple_numbers_sorted_treating_first_word_as_number.txt";
		String unsortedSimpleNumbersString = convertFileToString(simpleNumbersFilePath);
		String sortedString = sortApplication.sortSimpleNumbers(
				"-n" + NEW_LINE + unsortedSimpleNumbersString);
		String sortedSimpleNumbersString = convertFileToString(sortedSimpleNumbersFilePath);
		assertEquals(sortedSimpleNumbersString, sortedString);
	}

	@Test
	public void testSortSimpleNumbersEmptyString() {
		String sortedString = sortApplication.sortSimpleNumbers("");
		assertEquals("", sortedString);
	}

	@Test
	public void testSortSimpleSpecialChars() throws IOException {
		String simpleSpecialFilePath = TEST_FILES_PATH + "simple_special.txt";
		String sortedSimpleSpecialFilePath = TEST_FILES_PATH + "simple_special_sorted.txt";
		String unsortedSimpleSpecialString = convertFileToString(simpleSpecialFilePath);
		String sortedString = sortApplication.sortSimpleSpecialChars(
				unsortedSimpleSpecialString);
		String sortedSimpleSpecialString = convertFileToString(sortedSimpleSpecialFilePath);
		assertEquals(sortedSimpleSpecialString, sortedString);
	}

	@Test
	public void testSortSimpleSpecialCharsEmptyString() {
		String sortedString = sortApplication.sortSimpleSpecialChars("");
		assertEquals("", sortedString);
	}

	@Test
	public void testSortCapitalNumbers() throws IOException {
		String capitalNumbersFilePath = TEST_FILES_PATH + "capital_numbers.txt";
		String sortedCapitalNumbersFilePath = TEST_FILES_PATH +
				"capital_numbers_sorted.txt";
		String unsortedCapitalNumbersString = convertFileToString(capitalNumbersFilePath);
		String sortedString = sortApplication.sortCapitalNumbers(unsortedCapitalNumbersString);
		String sortedCapitalNumbersString = convertFileToString(sortedCapitalNumbersFilePath);
		assertEquals(sortedCapitalNumbersString, sortedString);
	}

	@Test
	public void testSortCapitalNumbersTreatFirstWordAsNumber() throws IOException {
		String capitalNumbersFilePath = TEST_FILES_PATH + "capital_numbers.txt";
		String sortedCapitalNumbersFilePath = TEST_FILES_PATH + 
				"capital_numbers_sorted_treating_first_word_as_number.txt";
		String unsortedCapitalNumbersString = convertFileToString(capitalNumbersFilePath);
		String sortedString = sortApplication.sortCapitalNumbers(
				"-n" + NEW_LINE + unsortedCapitalNumbersString);
		String sortedCapitalNumbersString = convertFileToString(sortedCapitalNumbersFilePath);
		assertEquals(sortedCapitalNumbersString, sortedString);
	}

	@Test
	public void testSortCapitalNumbersEmptyString() {
		String sortedString = sortApplication.sortCapitalNumbers("");
		assertEquals("", sortedString);
	}

	@Test
	public void testSortCapitalSpecialChars() throws IOException {
		String capitalSpecialFilePath = TEST_FILES_PATH + "capital_special.txt";
		String sortedCapitalSpecialFilePath = TEST_FILES_PATH +
				"capital_special_sorted.txt";
		String unsortedCapitalSpecialString = convertFileToString(capitalSpecialFilePath);
		String sortedString = sortApplication.sortCapitalSpecialChars(
				unsortedCapitalSpecialString);
		String sortedCapitalSpecialString = convertFileToString(
				sortedCapitalSpecialFilePath);
		assertEquals(sortedCapitalSpecialString, sortedString);
	}

	@Test
	public void testSortCapitalSpecialCharsEmptyString() {
		String sortedString = sortApplication.sortCapitalSpecialChars("");
		assertEquals("", sortedString);
	}

	@Test
	public void testSortNumbersSpecialChars() throws IOException {
		String numbersSpecialFilePath = TEST_FILES_PATH + "numbers_special.txt";
		String sortedNumbersSpecialFilePath = TEST_FILES_PATH +
				"numbers_special_sorted.txt";
		String unsortedNumbersSpecialString = convertFileToString(numbersSpecialFilePath);
		String sortedString = sortApplication.sortNumbersSpecialChars(
				unsortedNumbersSpecialString);
		String sortedNumbersSpecialString = convertFileToString(
				sortedNumbersSpecialFilePath);
		assertEquals(sortedNumbersSpecialString, sortedString);
	}

	@Test
	public void testSortNumbersSpecialCharsTreatFirstWordAsNumber() throws IOException {
		String numbersSpecialFilePath = TEST_FILES_PATH + "numbers_special.txt";
		String sortedNumbersSpecialFilePath = TEST_FILES_PATH + 
				"numbers_special_sorted_treating_first_word_as_number.txt";
		String unsortedNumbersSpecialString = convertFileToString(numbersSpecialFilePath);
		String sortedString = sortApplication.sortNumbersSpecialChars(
				"-n" + NEW_LINE + unsortedNumbersSpecialString);
		String sortedNumbersSpecialString = convertFileToString(
				sortedNumbersSpecialFilePath);
		assertEquals(sortedNumbersSpecialString, sortedString);
	}

	@Test
	public void testSortNumbersSpecialCharsEmptyString() {
		String sortedString = sortApplication.sortNumbersSpecialChars("");
		assertEquals("", sortedString);
	}

	@Test
	public void testSortSimpleCapitalNumber() throws IOException{
		String simpleCapitalNumberFilePath = TEST_FILES_PATH + "simple_capital_number.txt";
		String sortedSimpleCapitalNumberFilePath = TEST_FILES_PATH +
				"simple_capital_number_sorted.txt";
		String unsortedSimpleCapitalNumberString =
				convertFileToString(simpleCapitalNumberFilePath);
		String sortedString = sortApplication.sortSimpleCapitalNumber(
				unsortedSimpleCapitalNumberString);
		String sortedSimpleCapitalNumberString = convertFileToString(
				sortedSimpleCapitalNumberFilePath);
		assertEquals(sortedSimpleCapitalNumberString, sortedString);
	}

	@Test
	public void testSortSimpleCapitalNumberTreatFirstWordAsNumber() throws IOException {
		String simpleCapitalNumberFilePath = TEST_FILES_PATH + "simple_capital_number.txt";
		String sortedSimpleCapitalNumberFilePath = TEST_FILES_PATH + 
				"simple_capital_number_sorted_treating_first_word_as_number.txt";
		String unsortedSimpleCapitalNumberString =
				convertFileToString(simpleCapitalNumberFilePath);
		String sortedString = sortApplication.sortSimpleCapitalNumber(
				"-n" + NEW_LINE + unsortedSimpleCapitalNumberString);
		String sortedSimpleCapitalNumberString = convertFileToString(
				sortedSimpleCapitalNumberFilePath);
		assertEquals(sortedSimpleCapitalNumberString, sortedString);
	}

	@Test
	public void testSortSimpleCapitalNumberEmptyString() {
		String sortedString = sortApplication.sortSimpleCapitalNumber("");
		assertEquals("", sortedString);
	}

	@Test
	public void testSortSimpleCapitalSpecialChars() throws IOException {
		String simpleCapitalSpecialFilePath = TEST_FILES_PATH + "simple_capital_special.txt";
		String sortedSimpleCapitalSpecialFilePath = TEST_FILES_PATH +
				"simple_capital_special_sorted.txt";
		String unsortedSimpleCapitalSpecialString =
				convertFileToString(simpleCapitalSpecialFilePath);
		String sortedString = sortApplication.sortSimpleCapitalSpecialChars(
				unsortedSimpleCapitalSpecialString);
		String sortedSimpleCapitalSpecialString = convertFileToString(
				sortedSimpleCapitalSpecialFilePath);
		assertEquals(sortedSimpleCapitalSpecialString, sortedString);
	}

	@Test
	public void testSortSimpleCapitalSpecialCharsEmptyString() {
		String sortedString = sortApplication.sortSimpleCapitalSpecialChars("");
		assertEquals("", sortedString);
	}

	@Test
	public void testSortSimpleNumbersSpecialChars() throws IOException {
		String simpleNumbersSpecialFilePath = TEST_FILES_PATH + "simple_numbers_special.txt";
		String sortedSimpleNumbersSpecialFilePath = TEST_FILES_PATH +
				"simple_numbers_special_sorted.txt";
		String unsortedSimpleNumbersSpecialString = convertFileToString(
				simpleNumbersSpecialFilePath);
		String sortedString = sortApplication.sortSimpleNumbersSpecialChars(
				unsortedSimpleNumbersSpecialString);
		String sortedSimpleNumbersSpecialString = convertFileToString(
				sortedSimpleNumbersSpecialFilePath);
		assertEquals(sortedSimpleNumbersSpecialString, sortedString);
	}

	@Test
	public void testSortSimpleNumbersSpecialTreatFirstWordAsNumber() throws IOException {
		String simpleNumbersSpecialFilePath = TEST_FILES_PATH + "simple_numbers_special.txt";
		String sortedSimpleNumbersSpecialFilePath = TEST_FILES_PATH +
				"simple_numbers_special_treat_first_word_as_number_sorted.txt";
		String unsortedSimpleNumbersSpecialString = convertFileToString(
				simpleNumbersSpecialFilePath);
		String sortedString = sortApplication.sortSimpleNumbersSpecialChars(
				"-n" + NEW_LINE + unsortedSimpleNumbersSpecialString);
		String sortedSimpleNumbersSpecialString = convertFileToString(
				sortedSimpleNumbersSpecialFilePath);
		assertEquals(sortedSimpleNumbersSpecialString, sortedString);
	}

	@Test
	public void testSortSimpleNumbersSpecialCharsEmptyString() {
		String sortedString = sortApplication.sortSimpleNumbersSpecialChars("");
		assertEquals("", sortedString);
	}

	@Test
	public void testSortCapitalNumbersSpecialChars() throws IOException {
		String capitalNumbersSpecialFilePath = TEST_FILES_PATH + "capital_numbers_special.txt";
		String sortedCapitalNumbersSpecialFilePath = TEST_FILES_PATH +
				"capital_numbers_special_sorted.txt";
		String unsortedCapitalNumbersSpecialString = convertFileToString(
				capitalNumbersSpecialFilePath);
		String sortedString = sortApplication.sortCapitalNumbersSpecialChars(
				unsortedCapitalNumbersSpecialString);
		String sortedCapitalNumbersSpecialString = convertFileToString(
				sortedCapitalNumbersSpecialFilePath);
		assertEquals(sortedCapitalNumbersSpecialString, sortedString);
	}

	@Test
	public void testSortCapitalNumbersSpecialCharsTreatFirstWordAsNumber()
			throws IOException {
		String capitalNumbersSpecialFilePath = TEST_FILES_PATH + "capital_numbers_special.txt";
		String sortedCapitalNumbersSpecialFilePath = TEST_FILES_PATH +
				"capital_numbers_special_treat_first_word_as_number_sorted.txt";
		String unsortedCapitalNumbersSpecialString = convertFileToString(
				capitalNumbersSpecialFilePath);
		String sortedString = sortApplication.sortCapitalNumbersSpecialChars(
				"-n" + NEW_LINE + unsortedCapitalNumbersSpecialString);
		String sortedCapitalNumbersSpecialString = convertFileToString(
				sortedCapitalNumbersSpecialFilePath);
		assertEquals(sortedCapitalNumbersSpecialString, sortedString);
	}

	@Test
	public void testSortCapitalNumbersSpecialCharsEmptyString() {
		String sortedString = sortApplication.sortCapitalNumbersSpecialChars("");
		assertEquals("", sortedString);
	}

	@Test
	public void testSortAll() throws IOException {
		String allFilePath = TEST_FILES_PATH + ALL_TXT_FILE;
		String sortedAllFilePath = TEST_FILES_PATH + "all_sorted.txt";
		String unsortedAllString = convertFileToString(allFilePath);
		String sortedString = sortApplication.sortAll(unsortedAllString);
		String sortedAllString = convertFileToString(sortedAllFilePath);
		assertEquals(sortedAllString, sortedString);
	}

	@Test
	public void testSortAllInputSingleNewLine() throws IOException {
		String sortedString = sortApplication.sortAll(NEW_LINE);
		assertEquals(NEW_LINE, sortedString);
	}

	@Test
	public void testSortAllTreatFirstWordAsNumber() throws IOException {
		String allFilePath = TEST_FILES_PATH + ALL_TXT_FILE;
		String sortedAllFilePath = TEST_FILES_PATH +
				"all_sorted_treat_first_word_as_number.txt";
		String unsortedAllString = convertFileToString(allFilePath);
		String sortedString = sortApplication.sortAll(
				"-n" + NEW_LINE + unsortedAllString);
		String sortedAllString = convertFileToString(sortedAllFilePath);
		assertEquals(sortedAllString, sortedString);
	}

	@Test
	public void testSortAllMediumLengthSentences() throws IOException {
		String allFilePath = TEST_FILES_PATH + "medium_length_sentences.txt";
		String sortedAllFilePath = TEST_FILES_PATH +
				"medium_length_sentences_sorted.txt";
		String unsortedAllString = convertFileToString(allFilePath);
		String sortedString = sortApplication.sortAll(
				"-n" + NEW_LINE + unsortedAllString);
		String sortedAllString = convertFileToString(sortedAllFilePath);
		assertEquals(sortedAllString, sortedString);
	}

	@Test
	public void testSortAllEmptyString() {
		String sortedString = sortApplication.sortAll("");
		assertEquals("", sortedString);
	}

	@Test
	public void testRunUsingFile() throws AbstractApplicationException, IOException {
		String allFilePath = TEST_FILES_PATH + ALL_TXT_FILE;
		String sortedAllFilePath = TEST_FILES_PATH + "all_sorted.txt";
		String[] args = {allFilePath};
		inputStream = null;
		outputStream = new ByteArrayOutputStream();
		sortApplication.run(args, inputStream, outputStream);
		String sortedAllString = convertFileToString(sortedAllFilePath);
		assertEquals(sortedAllString, outputStream.toString());
	}

	@Test
	public void testRunTreatFirstWordAsNumber()
			throws AbstractApplicationException, IOException {
		String allFilePath = TEST_FILES_PATH + ALL_TXT_FILE;
		String sortedAllFilePath = TEST_FILES_PATH +
				"all_sorted_treat_first_word_as_number.txt";
		String[] args = {"-n", allFilePath};
		inputStream = null;
		outputStream = new ByteArrayOutputStream();
		sortApplication.run(args, inputStream, outputStream);
		String sortedAllString = convertFileToString(sortedAllFilePath);
		assertEquals(sortedAllString, outputStream.toString());
	}

	@Test
	public void testRunUsingStdInput() throws AbstractApplicationException, IOException {
		String allFilePath = TEST_FILES_PATH + ALL_TXT_FILE;
		String sortedAllFilePath = TEST_FILES_PATH + "all_sorted.txt";
		String allTestString = convertFileToString(allFilePath);
		String[] args = {};
		inputStream = new ByteArrayInputStream(allTestString.getBytes());
		outputStream = new ByteArrayOutputStream();
		sortApplication.run(args, inputStream, outputStream);
		String sortedAllString = convertFileToString(sortedAllFilePath);
		assertEquals(sortedAllString, outputStream.toString());
	}

	@Test
	public void testRunUsingEmptyStdInput() throws AbstractApplicationException, IOException {
		String[] args = {};
		inputStream = new ByteArrayInputStream("".getBytes());
		outputStream = new ByteArrayOutputStream();
		sortApplication.run(args, inputStream, outputStream);
		assertEquals("", outputStream.toString());
	}

	@Test
	public void testRunUsingStdInputTreatFirstWordAsNumber()
			throws AbstractApplicationException, IOException {
		String allFilePath = TEST_FILES_PATH + ALL_TXT_FILE;
		String sortedAllFilePath = TEST_FILES_PATH +
				"all_sorted_treat_first_word_as_number.txt";
		String allTestString = "-n" + NEW_LINE + convertFileToString(allFilePath);
		String[] args = {};
		inputStream = new ByteArrayInputStream(allTestString.getBytes());
		outputStream = new ByteArrayOutputStream();
		sortApplication.run(args, inputStream, outputStream);
		String sortedAllString = convertFileToString(sortedAllFilePath);
		assertEquals(sortedAllString, outputStream.toString());
	}

	@Test
	public void testRunUsingStdInputTreatFirstWordAsNumberNoInput()
			throws AbstractApplicationException {
		String allTestString = "-n" + NEW_LINE;
		String[] args = {};
		inputStream = new ByteArrayInputStream(allTestString.getBytes());
		outputStream = new ByteArrayOutputStream();
		sortApplication.run(args, inputStream, outputStream);
		assertEquals("", outputStream.toString());
	}

	@Test
	public void testRunThrowWhenStdOutputNull() throws AbstractApplicationException {
		String allFilePath = TEST_FILES_PATH + ALL_TXT_FILE;
		String[] args = {allFilePath};
		thrown.expect(SortException.class);
		thrown.expectMessage("No output stream provided");
		inputStream = null;
		outputStream = null;
		sortApplication.run(args, inputStream, outputStream);
	}

	@Test
	public void testRunThrowWhenArgsAndStdInputNull() throws AbstractApplicationException {
		String[] args = null;
		thrown.expect(SortException.class);
		thrown.expectMessage("No input provided");
		inputStream = null;
		outputStream = new ByteArrayOutputStream();
		sortApplication.run(args, inputStream, outputStream);
	}

	@Test
	public void testRunThrowWhenEmptyArgsAndStdInputNull() throws AbstractApplicationException {
		String[] args = {};
		thrown.expect(SortException.class);
		thrown.expectMessage("No input provided");
		inputStream = null;
		outputStream = new ByteArrayOutputStream();
		sortApplication.run(args, inputStream, outputStream);
	}

	@Test
	public void testRunThrowWhenFirstArgumentNull() throws AbstractApplicationException {
		String[] args = {null};
		thrown.expect(SortException.class);
		thrown.expectMessage("First argument provided is null");
		inputStream = null;
		outputStream = new ByteArrayOutputStream();
		sortApplication.run(args, inputStream, outputStream);
	}

	@Test
	public void testRunThrowWhenNecessarySecondArgumentNotGiven()
			throws AbstractApplicationException {
		String[] args = {"-n"};
		thrown.expect(SortException.class);
		thrown.expectMessage("No file path provided");
		inputStream = null;
		outputStream = new ByteArrayOutputStream();
		sortApplication.run(args, inputStream, outputStream);
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
