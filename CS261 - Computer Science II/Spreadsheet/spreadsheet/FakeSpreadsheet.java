package spreadsheet;
/**
 * This class is to be used as an example only -- it will not be
 * used as part of your implementation.  It supports the Sheetable
 * interface in a trivial manner, but will let us build and run a
 * sample application to demonstrate the GUI.
 *
 * @author Brad Richards
 */
public class FakeSpreadsheet implements Sheetable {
	/**
	 * getValue is called by the GUI when it needs to know a cell's
	 * value so it can be displayed.  This simple implementation
	 * "invents" a value based on the row and column numbers.
	 *
	 * @param row  Zero-based row index for cell to be inspected
	 * @param col  Zero-based column index for cell to be inspected
	 * @return  A "fake" value computed from the row and col values
	 */
	public double getValue(int row, int col) {
		return row*100 + col;
	}

	/**
	 * getFormula is called by the GUI to get the cell's formula so
	 * it can be displayed or edited.  This "fake" spreadsheet
	 * implementation doesn't store any formulas, so we invent a bogus
	 * string here to pass back to the GUI.
	 *
	 * @param row  Zero-based row index for cell to be inspected
	 * @param col  Zero-based column index for cell to be inspected
	 * @return  A string containing the specified row and column
	 */
	public String getFormula(int row, int col) {
		return "formula for "+row+","+col;
	}

	/**
	 * setFormula is called when the GUI has a new expression string
	 * to be stored in a cell.  Normally, we'd create an Expression
	 * instance from the string and store that somewhere, but our
	 * "fake" spreadsheet doesn't store any formulas.  Instead, we'll
	 * just print a message indicating that we've been passed a
	 * formula.
	 *
	 * @param formula  A parenthesized infix expression string
	 * @param row  Zero-based row index for cell to be modified
	 * @param col  Zero-based column index for cell to be modified
	 */
	public void setFormula(String formula, int row, int col) {
		System.out.println("GUI asked us to set "+row+","+col
				+" formula to "+formula);
	}

	/**
	 * This method must return true if the cell at row, col contains
	 * no formula and therefore no value.  Our "fake" implementation
	 * always returns false so the GUI displays our cell values.
	 *
	 * @param row  Zero-based row index for cell to be inspected
	 * @param col  Zero-based column index for cell to be inspected
	 * @return <code>true</code> if cell does not contain a formula
	 */
	public boolean isEmpty(int row, int col) {
		return false;
	}
}
