package spreadsheet;
import javax.swing.SwingUtilities;

import arithmeticExpression.InfixToPostfixParens.SyntaxErrorException;

/**
 * The Demo class implements a runnable demo program illustrating 
 * the use of the SpreadsheetGUI class and the Sheetable interface.
 * It creates a FakeSpreadsheet instance, and passes it to an
 * instance of the SpreadsheetGUI class.  The program ends when the
 * user closes the GUI window.
 * 
 * @author Brad Richards
 */
public class Demo {
	public static final int ROWS = 12;
	public static final int COLS = 10;

	/**
	 * The main method creates an instance of our FakeSpreadsheet implementation,
	 * glues it together with a SpreadsheetGUI object, and keeps things alive 
	 * until the spreadsheet window is closed.
	 */
	public static void main(String[] args) {

		// The FakeSpreadsheet class implements the Sheetable interface,
		// but doesn't do anything interesting.  You'll want to replace it
		// with your own class, and make theData be an instance of YOUR
		// class here.
		
		//Sheetable theData = new SimpleSpreadsheet();
		Sheetable theData = new SplitSpreadSheet();
		//Sheetable theData = new GraphSpreadsheet2();
		
		SpreadsheetGUI theGUI = new SpreadsheetGUI(theData, ROWS, COLS);

		// We're supposed to ensure that the GUI is drawn by the event dispatch
		// thread.  We'll use invokeAndWait to ensure it happens that way.
		try {
			SwingUtilities.invokeAndWait(theGUI);
		} catch (Exception e1) {
			System.err.println("Yikes!  Something went wrong when displaying the spreadsheet:");
			e1.printStackTrace();
		}
		try {
			SplitSpreadSheet.test();
		} catch (SyntaxErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}