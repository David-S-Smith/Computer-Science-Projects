package spreadsheet;
/**
 * The Sheetable interface contains methods for interacting with the
 * data stored in a spreadsheet.  Any object supporting the Sheetable
 * interface can be displayed (and edited) via the SpreadsheetGUI class.
 * 
 * @author Brad Richards
 */

public interface Sheetable {
	/**
	 * This method must return true if the cell at row, col contains
	 * no formula and therefore no value.
	 * 
	 * @param row  Zero-based row index for cell to be inspected
	 * @param col  Zero-based column index for cell to be inspected
	 * @return <code>true</code> if cell does not contain a formula
	 */
	boolean isEmpty(int row, int col);

	/**
	 * getValue must return the current value of the specified cell.
	 * It will never be called on empty cells (those for which isEmpty
	 * returns true).
	 * 
	 * @param row  Zero-based row index for cell to be inspected
	 * @param col  Zero-based column index for cell to be inspected
	 * @return The value of the formula in the specified cell
	 */
	double getValue(int row, int col);

	/**
	 * getFormula must return the formula in the specified cell.
	 * It will never be called on empty cells (those for which isEmpty
	 * returns true).
	 * 
	 * @param row  Zero-based row index for cell to be inspected
	 * @param col  Zero-based column index for cell to be inspected
	 * @return A string containing the cell's formula
	 */
	String getFormula(int row, int col);

	/**
	 * setFormula provides an expression string to be used as the
	 * contents of the specified cell.  There is no guarantee that it
	 * is a syntactically-correct infix expression.  If it's not, this
	 * method should print an error message and leave the cell unchanged.
	 * 
	 * @param formula  A parenthesized infix expression string
	 * @param row  Zero-based row index for cell to be modified
	 * @param col  Zero-based column index for cell to be modified
	 */
	void setFormula(String formula, int row, int col);
}