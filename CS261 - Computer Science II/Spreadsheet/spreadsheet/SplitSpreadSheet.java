

package spreadsheet;

import java.awt.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.swing.SwingUtilities;

import arithmeticExpression.Expression;
import arithmeticExpression.InfixToPostfixParens.SyntaxErrorException;

/**
 * The following spreadsheet is called SplitSpreadSheet because it splits the efforts of updating values between setFormula
 * and getValue. Because uses of this spreadsheet typically involving getting all values after each action with the sheet,
 * this easily updates all values.
 * @author DAVID S SMITH, ERIC ZEITZ, CHRISTIAN WIEMER
 * @version 12/7/2016
 */

public class SplitSpreadSheet implements Sheetable {

	private HashMap<Point, Expression> pointFormula = new HashMap<Point, Expression>(); // This map takes in ordered pairs to produce expressions stored in them (for spreadsheet)
	private HashMap<String, Double> variableValue = new HashMap<String,Double>(); // The map for variables that points to their value
	private HashMap<String, Point> cellPoint = new HashMap<String,Point>(); // This map just saves us a messy conversion
	private final int CHARACTERCONVERSION = 65; //used for conversion of numerical representation of column to string representation

	/**
	 * Verifies if the cell is empty
	 *
	 * @param row the row value as a number (A1's column value would be 0)
	 * @param col the column value as a number (A1's column value would be 0)
	 * @return A boolean value stating whether the cell is empty (true) or filled (false)
	 */
	public boolean isEmpty(int row, int col) {
		Expression cellExpression = pointFormula.get(new Point(row,col));
		if(cellExpression == null){
			return true;
		}
		return false;
	}

	/**
	 * Obtains the value from this cell, evaluating down to update all values it depends upon. If you set a formula using
	 * setFormula(), you should use getValue() on the slots dependent on that slot to update all values correctly.
	 *
	 * The system Brad provided properly does this, hence we consider this a valid solution.
	 *
	 * @param row the row value as a number (A1's column value would be 0)
	 * @param col the column value as a number (A1's column value would be 0)
	 * @return A double representing the value evaluated from the cell
	 */
	public double getValue(int row, int col) {
		if(isEmpty(row, col)){
			return 0.0;
		}
		Expression cellExpression = pointFormula.get(new Point(row,col));

		//View the set of variables in the expression at the specified cell
		Set<String> varsSet = new HashSet<String>();
		varsSet.addAll(cellExpression.getVariables());
		//For each variable in this set, find the get the corresponding value, recursively
		for(String variable : varsSet){
			Point subPoint = cellPoint.get(variable);
			getValue(subPoint.x, subPoint.y);


		}

		//If we pass the for loop, it means we've hit a variable that points to a cell with no variables in its expression
		//In that case, we evaluate the cell to verify its value

		//Alternatively, it means we've verified all values in the set are updated
		Double evaluation = cellExpression.evaluate(variableValue);
		//Then we find the variable that represents the cell (technically we had this in calls above this case, but we
		//can't reach it directly, so we just convert it off of the row and column values
		String cellVar = (char)(col + CHARACTERCONVERSION) + "" + (row + 1);
		//Using the value and a string variable to map to it, we update our map entry to allow the method calls
		//above this one to properly evaluate themselves (Because their variables will point to the correct values now)
		variableValue.put(cellVar, evaluation);
		//Now we return the value we've gotten from all of this.

		//In the simplest case (a number) this is just that number, evaluated off of the expression
		//In an operation on two numbers, it is just the result evaluated off of the expression
		//In a case with variable(s), it is the result evaluated off of the operation on the variable(s), when each variable's
		//cell has been updated all the way down to a simpler case

		return evaluation;

	}

	/**
	 * Returns the formula contained in the cell in String form
	 *
	 * @param row the row value as a number (A1's column value would be 0)
	 * @param col the column value as a number (A1's column value would be 0)
	 * @return A string representation of the formula in the cell
	 */
	public String getFormula(int row, int col) {
		if(isEmpty(row, col)){
			return "";
		}
		Expression cellExpression = pointFormula.get(new Point(row,col));
		return cellExpression.toString();
	}

	/**
	 * Sets the cell to contain a formula. To verify all cells dependent on this cell are updated,
	 * you'll need to call getValue() on them. Will print "Invalid Input" and not update a cell if
	 * entered formula is invalid. This includes illegal arguments, (nonsensical strings) references to
	 * empty cells, and cycles (cells that loop back to themselves, updating continuously.
	 *
	 * @param formula A string representing the formula to input (must use spaces between operands and operators)
	 * @param row the row value as a number (A1's column value would be 0)
	 * @param col the column value as a number (A1's column value would be 0)
	 */
	public void setFormula(String formula, int row, int col) {

		//Create the new formula, and find it's value. Update the maps at the formula's cell with the formula and value
		Point cellToSet = new Point(row,col);
		String cellVar = (char)(col + CHARACTERCONVERSION) + "" + (row + 1);
		Expression newFormula = null;

		try {
			newFormula = new Expression(formula);
		} catch (SyntaxErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		//Add expression to cell based upon (row, col)
		pointFormula.put(cellToSet, newFormula);
		//Find it's value
		Double newValue;
		try{
			newValue = newFormula.evaluate(variableValue);
		}
		//If evaluate fails (most likely an input error on the user), undo any map updates and end prematurely
		catch(Exception e){
			System.err.println("Invalid input");
			pointFormula.remove(cellToSet);
			return;
		}
		//Put the cell value pair into the variable map
		variableValue.put(cellVar, newValue);
		//Update this other map to be used in getValue()
		cellPoint.put(cellVar, cellToSet);
	}

	/**
	 * Tests a variety of cases in a spreadsheet, adding formulas and getting them to verify they've updated properly when altered.
	 * Also tests several errors, including syntax errors, referencing empty cells, and cycling.
	 * @throws SyntaxErrorException
	 */
	public static void test() throws SyntaxErrorException{


	        Sheetable data = new SplitSpreadSheet();

	        Double correctVal, gottenVal;


	        data.setFormula("5", 0, 0); //A1 = 5
	        assert data.getFormula(0, 0).equals("5"): "Test 1 failed! Returned " + data.getFormula(0, 0);
	        correctVal = 5.0;
	        gottenVal = data.getValue(0,0);
	        if (!gottenVal.equals(correctVal)){
	            System.out.println("Test 1 failed! Returned " + data.getValue(0, 0));
	        }


	        data.setFormula("A1", 1, 1); //B2 = A1
	        assert data.getFormula(1, 1).equals("A1"): "Test 2 failed! Returned " + data.getFormula(1, 1);
	        correctVal = 5.0;
	        gottenVal = data.getValue(1,1);

	        if (!gottenVal.equals(correctVal)){
	        	System.out.println("Test 2 failed! Returned " + data.getValue(1, 1));
	        }


	        data.setFormula("B2", 2, 2); //C3 = B2
	        assert data.getFormula(2, 2).equals("B2"): "Test 3 failed! Returned " + data.getFormula(2, 2);
	        correctVal = 5.0;
	        gottenVal = data.getValue(2,2);
	        if (!gottenVal.equals(correctVal)){
	            System.out.println("Test 3 failed! Returned " + data.getValue(2, 2));
	        }

	        data.setFormula("6", 1, 2); //C2 = 6
	        assert data.getFormula(1, 2).equals("6"): "Test 4 failed! Returned " + data.getFormula(1, 2);
	        correctVal = 6.0;
	        gottenVal = data.getValue(1,2);
	        if (!gottenVal.equals(correctVal)){
	        	System.out.println("Test 4 failed! Returned " + data.getValue(1, 2));
	        }


	        data.setFormula("10", 3, 3); // D4 = 10
	        assert data.getFormula(3, 3).equals("10"): "Test 5 failed! Returned " + data.getFormula(3, 3);

	        data.setFormula("A1 + B2", 3, 3); // D4 = A1 + B2
	        correctVal = 10.0;
	        gottenVal = data.getValue(3, 3);

	        if (!gottenVal.equals(correctVal)){
	        	System.out.println("Test 5 failed! Returned " + data.getValue(3, 3));
	        }


	        data.setFormula("7", 4, 4); //E5 = 7
	        assert data.getFormula(4, 4).equals("7"): "Test 6 failed! Returned " + data.getFormula(4, 4);
	        correctVal = 7.0;
	        gottenVal = data.getValue(4,4);

	        if (!gottenVal.equals(correctVal)){
	            System.out.println("Test 6 failed! Returned " + data.getValue(4, 4));
	        }


	        data.setFormula("6", 5, 5); //F6 = 6
	        assert data.getFormula(5, 5).equals("6"): "Test 7 failed! Returned " + data.getFormula(5, 5);
	        correctVal = 6.0;
	        gottenVal = data.getValue(5,5);

	        if (!gottenVal.equals(correctVal)){
	        	System.out.println("Test 7 failed! Returned " + data.getValue(5, 5));
	        }


	        data.setFormula("E5 + F6", 6, 6); // G7 = E5 + F6
	        correctVal = 13.0;
	        assert data.getFormula(6, 6).equals("E5 + F6"): "Test 8 failed! Returned " + data.getFormula(6, 6);
	        gottenVal = data.getValue(6,6);

	        if (!gottenVal.equals(correctVal)){
	        	System.out.println("Test 8 failed! Returned " + data.getValue(6, 6));
	        }


	        data.setFormula("G7 + D4 + 6", 7, 7); // H8 = G7 + D4 + 6
	        correctVal = 29.0;
	        assert data.getFormula(7, 7).equals("G7 + D4 + 6"): "Test 9 failed! Returned " + data.getFormula(7, 7);
	        gottenVal = data.getValue(7,7);

	        if (!gottenVal.equals(correctVal)){
	        	 System.out.println("Test 9 failed! Returned " + data.getValue(7, 7));
	        }

	        data.setFormula("H8", 0, 1); // B1 = H8
	        assert data.getFormula(0, 1).equals("H8"): "Test 10 failed! Returned " + data.getFormula(0, 1);
	        correctVal = 29.0;
	        gottenVal = data.getValue(0,1);

	        if (!gottenVal.equals(correctVal)){
	        	System.out.println("Test 10 failed! Returned " + data.getValue(0, 1));
	        }


	        data.setFormula("B1", 0, 7); // H1 = B1
	        assert data.getFormula(0, 7).equals("B1"): "Test 11 failed! Returned " + data.getFormula(0, 7);
	        correctVal = 29.0;
	        gottenVal = data.getValue(0,7);

	        if (!gottenVal.equals(correctVal)){
	        	System.out.println("Test 11 failed! Returned " + data.getValue(0, 7));
	        }



	        //Cell M with nothing in it

	        assert data.isEmpty(0, 4) : "Failure: 0,4 not empty";
	        data.setFormula("E1", 9, 9); //This will not add anything to 9,9, because there is nothing in E1
	        assert data.isEmpty(9, 9) : "Failure: 9,9 not empty after referencing empty cell";

	        //Cell N with wrong variable

	        assert data.isEmpty(0, 4) : "Failure: 0,4 not empty";
	        data.setFormula("nonsense", 0, 4); //This contains a variable not referring to a cell, and thus 0,4 will be empty
	        assert data.isEmpty(0, 4) : "Failure: 0,4 not empty after referencing nonexistent variable";

	        assert data.isEmpty(0, 4) : "Failure: 0,4 not empty";
	        data.setFormula("E1", 0, 4); //This references itself, and should through an error and not update
	        assert data.isEmpty(0, 4) : "Failure: 0,4 not empty after cycle was created";

	        data.setFormula("1", 0, 0);
	        correctVal = 21.0;
	        gottenVal = data.getValue(0,7);
	        assert (correctVal.equals(gottenVal)) : "Failure, H1 not updated when A1 updated";




	    }
	}
