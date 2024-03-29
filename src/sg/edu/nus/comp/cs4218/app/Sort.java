package sg.edu.nus.comp.cs4218.app;

import sg.edu.nus.comp.cs4218.Application;

public interface Sort extends Application { // NOPMD - Provided interface

	/**
	 * Returns a sorted string containing only simple letters
	 * @param toSort String containing command and arguments
	 */
	public String sortStringsSimple(String toSort);

	/**
	 * Returns a sorted string containing only capital letters
	 * @param toSort String containing command and arguments
	 */
	public String sortStringsCapital(String toSort);

	/**
	 * Returns a sorted string containing only numbers
	 * @param toSort String containing command and arguments
	 */
	public String sortNumbers(String toSort);

	/**
	 * Returns a sorted string containing only special characters
	 * @param toSort String containing command and arguments
	 */
	public String sortSpecialChars(String toSort);

	/**
	 * Returns a sorted string containing simple and capital letters
	 * @param toSort String containing command and arguments
	 */
	public String sortSimpleCapital(String toSort);

	/**
	 * Returns a sorted string containing simple letters and numbers
	 * @param toSort String containing command and arguments
	 */
	public String sortSimpleNumbers(String toSort);

	/**
	 * Returns a sorted string containing simple letters and special characters
	 * @param toSort String containing command and arguments
	 */
	public String sortSimpleSpecialChars(String toSort);

	/**
	 * Returns a sorted string containing capital letters and numbers
	 * @param toSort String containing command and arguments
	 */
	public String sortCapitalNumbers(String toSort);

	/**
	 * Returns a sorted string containing capital letters and special character
	 * @param toSort String containing command and arguments
	 */
	public String sortCapitalSpecialChars(String toSort);

	/**
	 * Returns a sorted string containing numbers and special characters
	 * @param toSort String containing command and arguments
	 */
	public String sortNumbersSpecialChars(String toSort);

	/**
	 * Returns a sorted string containing simple and capital letters and numbers
	 * @param toSort String containing command and arguments
	 */
	public String sortSimpleCapitalNumber(String toSort);

	/**
	 * Returns a sorted string containing simple and capital letters and special
	 * characters
	 * @param toSort String containing command and arguments
	 */
	public String sortSimpleCapitalSpecialChars(String toSort);

	/**
	 * Returns a sorted string containing simple letters, numbers and special
	 * characters
	 * @param toSort String containing command and arguments
	 */
	public String sortSimpleNumbersSpecialChars(String toSort);

	/**
	 * Returns a sorted string containing capital letters, numbers and special
	 * characters
	 * @param toSort String containing command and arguments
	 */
	public String sortCapitalNumbersSpecialChars(String toSort);

	/**
	 * Returns a sorted string containing simple and capital letters, numbers
	 * and special characters
	 * @param toSort String containing command and arguments
	 */
	public String sortAll(String toSort);
}
