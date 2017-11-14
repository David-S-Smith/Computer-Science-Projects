package arithmeticExpression;

import java.util.Map;

/**
 * Node classes implementing this interface will be used as the
 * building blocks of our expression trees.  
 * 
 * @author Brad Richards
 */

public interface Evaluable {

	/**
	 * Evaluate the expression rooted at this node and return its 
	 * value as a double.  The method takes a Map containing variable 
	 * names and their values so we can properly evaluate expressions 
	 * involving variables.
	 * 
	 *  @param env a Map from variable names to values
	 *  @return the value of the expression as a double
	 */
	public abstract double evaluate(Map<String, Double> env);

	/**
	 * Returns a String containing a fully parenthesized representation
	 * of the expression rooted at this node.  This expression should 
	 * have spaces separating the operators, operands, and parentheses.
	 * 
	 *  @return a String containing a fully parenthesized representation
	 *   of the expression rooted at this node
	 */
	public abstract String toString();

}