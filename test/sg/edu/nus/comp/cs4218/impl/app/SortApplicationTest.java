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
// 2. The sorting methods only process strings that only contain the types of characters
//    that are contained in the return String. For example, sortStringSimple, which 
//    returns a string containing only simple letters, only takes in strings that contain
//    only simple letters.
// 3. When -n is specified, the first word of a line is treated as a number even if
//    only the first part of the word is a number (e.g. 34abc will be recognized as 34
//    but abc34 or a34bc will not be recognized as a number). -n has to be specified
//    by being the first line in the input string.
public class SortApplicationTest {

	private InputStream inputStream;
	private ByteArrayOutputStream outputStream;
	private SortApplication sortApplication;
	private final String testFilesPath = "test_inputs/sort/";
	private final String newLine = System.getProperty("line.separator");

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
		String numbersFilePath = testFilesPath + "numbers.txt";
		String sortedNumbersFilePath = testFilesPath + "numbers_sorted.txt";
		String unsortedNumbersString = convertFileToString(numbersFilePath);
		String sortedString = sortApplication.sortNumbers(unsortedNumbersString);
		String sortedNumbersString = convertFileToString(sortedNumbersFilePath);
		assertEquals(sortedNumbersString, sortedString);
	}

	@Test
	public void testSortNumbersTreatFirstWordAsNumber() throws IOException {
		String numbersFilePath = testFilesPath + "simple_numbers.txt";
		String sortedNumbersFilePath = testFilesPath + 
				"numbers_sorted_treating_first_word_as_number.txt";
		String unsortedNumbersString = convertFileToString(numbersFilePath);
		String sortedString = sortApplication.sortNumbers(
				"-n" + newLine + unsortedNumbersString);
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
		String specialFilePath = testFilesPath + "special.txt";
		String sortedSpecialsFilePath = testFilesPath + "special_sorted.txt";
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
		String simpleCapitalFilePath = testFilesPath + "simple_capital.txt";
		String sortedSimpleCapitalFilePath = testFilesPath + "simple_capital_sorted.txt";
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
		String simpleNumbersFilePath = testFilesPath + "simple_numbers.txt";
		String sortedSimpleNumbersFilePath = testFilesPath +
				"simple_numbers_sorted.txt";
		String unsortedSimpleNumbersString = convertFileToString(simpleNumbersFilePath);
		String sortedString = sortApplication.sortSimpleNumbers(unsortedSimpleNumbersString);
		String sortedSimpleNumbersString = convertFileToString(sortedSimpleNumbersFilePath);
		assertEquals(sortedSimpleNumbersString, sortedString);
	}

	@Test
	public void testSortSimpleNumbersTreatFirstWordAsNumber() throws IOException {
		String simpleNumbersFilePath = testFilesPath + "simple_numbers.txt";
		String sortedSimpleNumbersFilePath = testFilesPath + 
				"simple_numbers_sorted_treating_first_word_as_number.txt";
		String unsortedSimpleNumbersString = convertFileToString(simpleNumbersFilePath);
		String sortedString = sortApplication.sortSimpleNumbers(
				"-n" + newLine + unsortedSimpleNumbersString);
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
		String simpleSpecialFilePath = testFilesPath + "simple_special.txt";
		String sortedSimpleSpecialFilePath = testFilesPath + "simple_special.txt";
		String unsortedSimpleSpecialString = convertFileToString(simpleSpecialFilePath);
		String sortedString = sortApplication.sortSimpleNumbers(unsortedSimpleSpecialString);
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
		String capitalNumbersFilePath = testFilesPath + "capital_numbers.txt";
		String sortedCapitalNumbersFilePath = testFilesPath +
				"capital_numbers_sorted.txt";
		String unsortedCapitalNumbersString = convertFileToString(capitalNumbersFilePath);
		String sortedString = sortApplication.sortCapitalNumbers(unsortedCapitalNumbersString);
		String sortedCapitalNumbersString = convertFileToString(sortedCapitalNumbersFilePath);
		assertEquals(sortedCapitalNumbersString, sortedString);
	}

	@Test
	public void testSortCapitalNumbersTreatFirstWordAsNumber() throws IOException {
		String capitalNumbersFilePath = testFilesPath + "capital_numbers.txt";
		String sortedCapitalNumbersFilePath = testFilesPath + 
				"capital_numbers_sorted_treating_first_word_as_number.txt";
		String unsortedCapitalNumbersString = convertFileToString(capitalNumbersFilePath);
		String sortedString = sortApplication.sortCapitalNumbers(
				"-n" + newLine + unsortedCapitalNumbersString);
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
		String capitalSpecialFilePath = testFilesPath + "capital_special.txt";
		String sortedCapitalSpecialFilePath = testFilesPath +
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
		String numbersSpecialFilePath = testFilesPath + "numbers_special.txt";
		String sortedNumbersSpecialFilePath = testFilesPath +
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
		String numbersSpecialFilePath = testFilesPath + "numbers_special.txt";
		String sortedNumbersSpecialFilePath = testFilesPath + 
				"numbers_special_sorted_treating_first_word_as_number.txt";
		String unsortedNumbersSpecialString = convertFileToString(numbersSpecialFilePath);
		String sortedString = sortApplication.sortNumbersSpecialChars(
				"-n" + newLine + unsortedNumbersSpecialString);
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
		String simpleCapitalNumberFilePath = testFilesPath + "simple_capital_number.txt";
		String sortedSimpleCapitalNumberFilePath = testFilesPath +
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
		String simpleCapitalNumberFilePath = testFilesPath + "simple_capital_number.txt";
		String sortedSimpleCapitalNumberFilePath = testFilesPath + 
				"simple_capital_number_sorted_treating_first_word_as_number.txt";
		String unsortedSimpleCapitalNumberString =
				convertFileToString(simpleCapitalNumberFilePath);
		String sortedString = sortApplication.sortNumbersSpecialChars(
				"-n" + newLine + unsortedSimpleCapitalNumberString);
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
		String simpleCapitalSpecialFilePath = testFilesPath + "simple_capital_special.txt";
		String sortedSimpleCapitalSpecialFilePath = testFilesPath +
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
		String simpleNumbersSpecialFilePath = testFilesPath + "simple_numbers_special.txt";
		String sortedSimpleNumbersSpecialFilePath = testFilesPath +
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
		String simpleNumbersSpecialFilePath = testFilesPath + "simple_numbers_special.txt";
		String sortedSimpleNumbersSpecialFilePath = testFilesPath +
				"simple_numbers_special_treat_first_word_as_number_sorted.txt";
		String unsortedSimpleNumbersSpecialString = convertFileToString(
				simpleNumbersSpecialFilePath);
		String sortedString = sortApplication.sortSimpleNumbersSpecialChars(
				"-n" + newLine + unsortedSimpleNumbersSpecialString);
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
		String capitalNumbersSpecialFilePath = testFilesPath + "capital_numbers_special.txt";
		String sortedCapitalNumbersSpecialFilePath = testFilesPath +
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
		String capitalNumbersSpecialFilePath = testFilesPath + "capital_numbers_special.txt";
		String sortedCapitalNumbersSpecialFilePath = testFilesPath +
				"capital_numbers_special_treat_first_word_as_number_sorted.txt";
		String unsortedCapitalNumbersSpecialString = convertFileToString(
				capitalNumbersSpecialFilePath);
		String sortedString = sortApplication.sortCapitalNumbersSpecialChars(
				"-n" + newLine + unsortedCapitalNumbersSpecialString);
		String sortedCapitalNumbersSpecialString = convertFileToString(
				sortedCapitalNumbersSpecialFilePath);
		assertEquals(sortedCapitalNumbersSpecialString, sortedString);
	}

	@Test
	public void testSortCapitalNumbersSpecialCharsFile() {
		String sortedString = sortApplication.sortCapitalNumbersSpecialChars("");
		assertEquals("", sortedString);
	}

	@Test
	public void testSortAll() throws IOException {
		String allFilePath = testFilesPath + "all.txt";
		String sortedAllFilePath = testFilesPath + "all_sorted.txt";
		String unsortedAllString = convertFileToString(allFilePath);
		String sortedString = sortApplication.sortAll(unsortedAllString);
		String sortedAllString = convertFileToString(sortedAllFilePath);
		assertEquals(sortedAllString, sortedString);
	}

	@Test
	public void testSortAllTreatFirstWordAsNumber() throws IOException {
		String allFilePath = testFilesPath + "all.txt";
		String sortedAllFilePath = testFilesPath +
				"all_treat_first_word_as_number_sorted.txt";
		String unsortedAllString = convertFileToString(allFilePath);
		String sortedString = sortApplication.sortAll(
				"-n" + newLine + unsortedAllString);
		String sortedAllString = convertFileToString(sortedAllFilePath);
		assertEquals(sortedAllString, sortedString);
	}

	@Test
	public void testSortAllEmptyString() {
		String sortedString = sortApplication.sortAll("");
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
