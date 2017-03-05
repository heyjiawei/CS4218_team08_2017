package sg.edu.nus.comp.cs4218.impl.app;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;

// Assumptions made in tests:
// 1. 'Simple' letters means lowercase letters
// 2. The sorting methods only process files that only contain the types of characters
//    that are contained in the return String. For example, sortStringSimple, which 
//    returns a string containing only simple letters, only takes in files that contain
//    only simple letters.
// 3. When -n is specified, the first word of a line is treated as a number even if
//    only the first part of the word is a number (e.g. 34abc will be recognized as 34
//    but abc34 or a34bc will not be recognized as a number)
public class SortApplicationTest {

	private InputStream inputStream;
	private ByteArrayOutputStream outputStream;
	private SortApplication sortApplication;
	private final String testFilesPath = "test_inputs/sort/";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		sortApplication = new SortApplication();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSortStringsSimple() throws IOException {
		String simpleStringsFilePath = testFilesPath + "simple.txt";
		String sortedSimpleStringsFilePath = testFilesPath + "simple_sorted.txt";
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
		String capitalStringsFilePath = testFilesPath + "capital.txt";
		String sortedCapitalStringsFilePath = testFilesPath + "capital_sorted.txt";
		String sortedString = sortApplication.sortStringsCapital(capitalStringsFilePath);
		String sortedSimpleString = convertFileToString(sortedCapitalStringsFilePath);
		assertEquals(sortedSimpleString, sortedString);
	}

	@Test
	public void testSortStringsCapitalEmptyFile() {
		String emptyFilePath = testFilesPath + "empty.txt";
		String sortedString = sortApplication.sortStringsCapital(emptyFilePath);
		assertEquals("", sortedString);
	}

	@Test
	public void testSortNumbers() throws IOException {
		String numbersFilePath = testFilesPath + "numbers.txt";
		String sortedNumbersFilePath = testFilesPath + "numbers_sorted.txt";
		String sortedString = sortApplication.sortNumbers(numbersFilePath);
		String sortedNumbersString = convertFileToString(sortedNumbersFilePath);
		assertEquals(sortedNumbersString, sortedString);
	}

	@Test
	public void testSortNumbersTreatFirstWordAsNumber() throws IOException {
		String numbersFilePath = testFilesPath + "simple_numbers.txt";
		String sortedNumbersFilePath = testFilesPath + 
				"numbers_sorted_treating_first_word_as_number.txt";
		String sortedString = sortApplication.sortNumbers("-n " + numbersFilePath);
		String sortedNumbersString = convertFileToString(sortedNumbersFilePath);
		assertEquals(sortedNumbersString, sortedString);
	}

	@Test
	public void testSortNumbersEmptyFile() {
		String emptyFilePath = testFilesPath + "empty.txt";
		String sortedString = sortApplication.sortNumbers(emptyFilePath);
		assertEquals("", sortedString);
	}

	@Test
	public void testSortSpecialChars() throws IOException {
		String specialFilePath = testFilesPath + "special.txt";
		String sortedSpecialsFilePath = testFilesPath + "special_sorted.txt";
		String sortedString = sortApplication.sortSpecialChars(specialFilePath);
		String sortedSpecialString = convertFileToString(sortedSpecialsFilePath);
		assertEquals(sortedSpecialString, sortedString);
	}

	@Test
	public void testSortSpecialCharsEmptyFile() {
		String emptyFilePath = testFilesPath + "empty.txt";
		String sortedString = sortApplication.sortSpecialChars(emptyFilePath);
		assertEquals("", sortedString);
	}

	@Test
	public void testSortSimpleCapital() throws IOException {
		String simpleCapitalFilePath = testFilesPath + "simple_capital.txt";
		String sortedSimpleCapitalFilePath = testFilesPath + "simple_capital_sorted.txt";
		String sortedString = sortApplication.sortSimpleCapital(simpleCapitalFilePath);
		String sortedSimpleCapitalString = convertFileToString(sortedSimpleCapitalFilePath);
		assertEquals(sortedSimpleCapitalString, sortedString);
	}

	@Test
	public void testSortSimpleCapitalEmptyFile() {
		String emptyFilePath = testFilesPath + "empty.txt";
		String sortedString = sortApplication.sortSimpleCapital(emptyFilePath);
		assertEquals("", sortedString);
	}

	@Test
	public void testSortSimpleNumbers() throws IOException {
		String simpleNumbersFilePath = testFilesPath + "simple_numbers.txt";
		String sortedSimpleNumbersFilePath = testFilesPath +
				"simple_numbers_sorted.txt";
		String sortedString = sortApplication.sortSimpleNumbers(simpleNumbersFilePath);
		String sortedSimpleNumbersString = convertFileToString(sortedSimpleNumbersFilePath);
		assertEquals(sortedSimpleNumbersString, sortedString);
	}

	@Test
	public void testSortSimpleNumbersTreatFirstWordAsNumber() throws IOException {
		String simpleNumbersFilePath = testFilesPath + "simple_numbers.txt";
		String sortedSimpleNumbersFilePath = testFilesPath + 
				"simple_numbers_sorted_treating_first_word_as_number.txt";
		String sortedString = sortApplication.sortSimpleNumbers("-n " +
				simpleNumbersFilePath);
		String sortedSimpleNumbersString = convertFileToString(sortedSimpleNumbersFilePath);
		assertEquals(sortedSimpleNumbersString, sortedString);
	}

	@Test
	public void testSortSimpleNumbersEmptyFile() {
		String emptyFilePath = testFilesPath + "empty.txt";
		String sortedString = sortApplication.sortSimpleNumbers(emptyFilePath);
		assertEquals("", sortedString);
	}

	@Test
	public void testSortSimpleSpecialChars() throws IOException {
		String simpleSpecialFilePath = testFilesPath + "simple_special.txt";
		String sortedSimpleSpecialFilePath = testFilesPath + "simple_special.txt";
		String sortedString = sortApplication.sortSimpleNumbers(simpleSpecialFilePath);
		String sortedSimpleSpecialString = convertFileToString(sortedSimpleSpecialFilePath);
		assertEquals(sortedSimpleSpecialString, sortedString);
	}

	@Test
	public void testSortSimpleSpecialCharsEmptyFile() {
		String emptyFilePath = testFilesPath + "empty.txt";
		String sortedString = sortApplication.sortSimpleSpecialChars(emptyFilePath);
		assertEquals("", sortedString);
	}

	@Test
	public void testSortCapitalNumbers() throws IOException {
		String capitalNumbersFilePath = testFilesPath + "capital_numbers.txt";
		String sortedCapitalNumbersFilePath = testFilesPath +
				"capital_numbers_sorted.txt";
		String sortedString = sortApplication.sortCapitalNumbers(capitalNumbersFilePath);
		String sortedCapitalNumbersString = convertFileToString(sortedCapitalNumbersFilePath);
		assertEquals(sortedCapitalNumbersString, sortedString);
	}

	@Test
	public void testSortCapitalNumbersTreatFirstWordAsNumber() throws IOException {
		String capitalNumbersFilePath = testFilesPath + "capital_numbers.txt";
		String sortedCapitalNumbersFilePath = testFilesPath + 
				"capital_numbers_sorted_treating_first_word_as_number.txt";
		String sortedString = sortApplication.sortCapitalNumbers("-n " +
				capitalNumbersFilePath);
		String sortedCapitalNumbersString = convertFileToString(sortedCapitalNumbersFilePath);
		assertEquals(sortedCapitalNumbersString, sortedString);
	}

	@Test
	public void testSortCapitalNumbersEmptyFile() {
		String emptyFilePath = testFilesPath + "empty.txt";
		String sortedString = sortApplication.sortCapitalNumbers(emptyFilePath);
		assertEquals("", sortedString);
	}

	@Test
	public void testSortCapitalSpecialChars() throws IOException {
		String capitalSpecialFilePath = testFilesPath + "capital_special.txt";
		String sortedCapitalSpecialFilePath = testFilesPath +
				"capital_special_sorted.txt";
		String sortedString = sortApplication.sortCapitalSpecialChars(capitalSpecialFilePath);
		String sortedCapitalSpecialString = convertFileToString(sortedCapitalSpecialFilePath);
		assertEquals(sortedCapitalSpecialString, sortedString);
	}

	@Test
	public void testSortCapitalSpecialCharsEmptyFile() {
		String emptyFilePath = testFilesPath + "empty.txt";
		String sortedString = sortApplication.sortCapitalSpecialChars(emptyFilePath);
		assertEquals("", sortedString);
	}

	@Test
	public void testSortNumbersSpecialChars() throws IOException {
		String numbersSpecialFilePath = testFilesPath + "numbers_special.txt";
		String sortedNumbersSpecialFilePath = testFilesPath +
				"numbers_special_sorted.txt";
		String sortedString = sortApplication.sortNumbersSpecialChars(numbersSpecialFilePath);
		String sortedNumbersSpecialString = convertFileToString(sortedNumbersSpecialFilePath);
		assertEquals(sortedNumbersSpecialString, sortedString);
	}

	@Test
	public void testSortNumbersSpecialCharsTreatFirstWordAsNumber() throws IOException {
		String numbersSpecialFilePath = testFilesPath + "numbers_special.txt";
		String sortedNumbersSpecialFilePath = testFilesPath + 
				"numbers_special_sorted_treating_first_word_as_number.txt";
		String sortedString = sortApplication.sortNumbersSpecialChars("-n " +
				numbersSpecialFilePath);
		String sortedNumbersSpecialString = convertFileToString(sortedNumbersSpecialFilePath);
		assertEquals(sortedNumbersSpecialString, sortedString);
	}

	@Test
	public void testSortNumbersSpecialCharsEmptyFile() {
		String emptyFilePath = testFilesPath + "empty.txt";
		String sortedString = sortApplication.sortNumbersSpecialChars(emptyFilePath);
		assertEquals("", sortedString);
	}

	@Test
	public void testSortSimpleCapitalNumber() throws IOException{
		String simpleCapitalNumberFilePath = testFilesPath + "simple_capital_number.txt";
		String sortedSimpleCapitalNumberFilePath = testFilesPath +
				"simple_capital_number_sorted.txt";
		String sortedString = sortApplication.sortSimpleCapitalNumber(
				simpleCapitalNumberFilePath);
		String sortedSimpleCapitalNumberString = convertFileToString(
				sortedSimpleCapitalNumberFilePath);
		assertEquals(sortedSimpleCapitalNumberString, sortedString);
	}

	@Test
	public void testSortSimpleCapitalNumberTreatFirstWordAsNumber() throws IOException {
		String simpleCapitalNumberFilePath = testFilesPath + "simple_capital_number.txt";
		String sortedSimpleCapitalNumberFilePath = testFilesPath + 
				"simple_capital_number_sorted_treating_first_word_as_number.txt";
		String sortedString = sortApplication.sortNumbersSpecialChars(
				"-n " + simpleCapitalNumberFilePath);
		String sortedSimpleCapitalNumberString = convertFileToString(
				sortedSimpleCapitalNumberFilePath);
		assertEquals(sortedSimpleCapitalNumberString, sortedString);
	}

	@Test
	public void testSortSimpleCapitalNumberEmptyFile() {
		String emptyFilePath = testFilesPath + "empty.txt";
		String sortedString = sortApplication.sortSimpleCapitalNumber(emptyFilePath);
		assertEquals("", sortedString);
	}

	@Test
	public void testSortSimpleCapitalSpecialChars() throws IOException {
		String simpleCapitalSpecialFilePath = testFilesPath + "simple_capital_special.txt";
		String sortedSimpleCapitalSpecialFilePath = testFilesPath +
				"simple_capital_special_sorted.txt";
		String sortedString = sortApplication.sortSimpleCapitalSpecialChars(
				simpleCapitalSpecialFilePath);
		String sortedSimpleCapitalSpecialString = convertFileToString(
				sortedSimpleCapitalSpecialFilePath);
		assertEquals(sortedSimpleCapitalSpecialString, sortedString);
	}

	@Test
	public void testSortSimpleCapitalSpecialCharsEmptyFile() {
		String emptyFilePath = testFilesPath + "empty.txt";
		String sortedString = sortApplication.sortSimpleCapitalSpecialChars(emptyFilePath);
		assertEquals("", sortedString);
	}

	@Test
	public void testSortSimpleNumbersSpecialChars() throws IOException {
		String simpleNumbersSpecialFilePath = testFilesPath + "simple_numbers_special.txt";
		String sortedSimpleNumbersSpecialFilePath = testFilesPath +
				"simple_numbers_special_sorted.txt";
		String sortedString = sortApplication.sortSimpleNumbersSpecialChars(
				simpleNumbersSpecialFilePath);
		String sortedSimpleNumbersSpecialString = convertFileToString(
				sortedSimpleNumbersSpecialFilePath);
		assertEquals(sortedSimpleNumbersSpecialString, sortedString);
	}

	@Test
	public void testSortSimpleNumbersSpecialTreatFirstWordAsNumber() throws IOException {
		String simpleNumbersSpecialFilePath = testFilesPath + "simple_numbers_special.txt";
		String sortedSimpleNumbersSpecialFilePath = testFilesPath +
				"simple_numbers_special_treat_first_word_as_number_sorted.txt";
		String sortedString = sortApplication.sortSimpleNumbersSpecialChars(
				"-n " + simpleNumbersSpecialFilePath);
		String sortedSimpleNumbersSpecialString = convertFileToString(
				sortedSimpleNumbersSpecialFilePath);
		assertEquals(sortedSimpleNumbersSpecialString, sortedString);
	}

	@Test
	public void testSortSimpleNumbersSpecialCharsEmptyFile() {
		String emptyFilePath = testFilesPath + "empty.txt";
		String sortedString = sortApplication.sortSimpleNumbersSpecialChars(emptyFilePath);
		assertEquals("", sortedString);
	}

	@Test
	public void testSortCapitalNumbersSpecialChars() throws IOException {
		String capitalNumbersSpecialFilePath = testFilesPath + "capital_numbers_special.txt";
		String sortedCapitalNumbersSpecialFilePath = testFilesPath +
				"capital_numbers_special_sorted.txt";
		String sortedString = sortApplication.sortCapitalNumbersSpecialChars(
				capitalNumbersSpecialFilePath);
		String sortedCapitalNumbersSpecialString = convertFileToString(
				sortedCapitalNumbersSpecialFilePath);
		assertEquals(sortedCapitalNumbersSpecialString, sortedString);
	}

	@Test
	public void testSortCapitalNumbersSpecialCharsTreatFirstWordAsNumber()
			throws IOException {
		String capitalNumbersSpecialFilePath = testFilesPath + "capital_numbers_special.txt";
		String sortedCapitalNumbersSpecialFilePath = testFilesPath +
				"capital_numbers_special_treat_first_word_as_number_sorted.txt";
		String sortedString = sortApplication.sortCapitalNumbersSpecialChars(
				"-n " + capitalNumbersSpecialFilePath);
		String sortedCapitalNumbersSpecialString = convertFileToString(
				sortedCapitalNumbersSpecialFilePath);
		assertEquals(sortedCapitalNumbersSpecialString, sortedString);
	}

	@Test
	public void testSortCapitalNumbersSpecialCharsFile() {
		String emptyFilePath = testFilesPath + "empty.txt";
		String sortedString = sortApplication.sortCapitalNumbersSpecialChars(emptyFilePath);
		assertEquals("", sortedString);
	}

	@Test
	public void testSortAll() throws IOException {
		String allFilePath = testFilesPath + "all.txt";
		String sortedAllFilePath = testFilesPath + "all_sorted.txt";
		String sortedString = sortApplication.sortAll(allFilePath);
		String sortedAllString = convertFileToString(sortedAllFilePath);
		assertEquals(sortedAllString, sortedString);
	}

	@Test
	public void testSortAllTreatFirstWordAsNumber() throws IOException {
		String allFilePath = testFilesPath + "all.txt";
		String sortedAllFilePath = testFilesPath +
				"all_treat_first_word_as_number_sorted.txt";
		String sortedString = sortApplication.sortAll("-n " + allFilePath);
		String sortedAllString = convertFileToString(sortedAllFilePath);
		assertEquals(sortedAllString, sortedString);
	}

	@Test
	public void testSortAllEmptyFile() {
		String emptyFilePath = testFilesPath + "empty.txt";
		String sortedString = sortApplication.sortAll(emptyFilePath);
		assertEquals("", sortedString);
	}

	@Test
	public void testRun() throws AbstractApplicationException, IOException {
		String allFilePath = testFilesPath + "all.txt";
		String sortedAllFilePath = testFilesPath + "all_sorted.txt";
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
		String allFilePath = testFilesPath + "all.txt";
		String sortedAllFilePath = testFilesPath +
				"all_treat_first_word_as_number_sorted.txt";
		String[] args = {"-n", allFilePath};
		inputStream = null;
		outputStream = new ByteArrayOutputStream();
		sortApplication.run(args, inputStream, outputStream);
		String sortedAllString = convertFileToString(sortedAllFilePath);
		assertEquals(sortedAllString, outputStream.toString());
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
