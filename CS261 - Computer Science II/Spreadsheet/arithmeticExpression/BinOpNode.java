package arithmeticExpression;

import java.util.Map;

/**
 * The BinOpNode represents internal (operator) nodes in our
 * expression trees.  We need references to the left and right
 * subtrees, and the operator itself.
 * 
 * @author Brad Richards
 */

public class BinOpNode implements Evaluable {
   private String op;      // The operator
   private Evaluable left;   // The expression for its left operand
   private Evaluable right;  // The expression for its right operand
   
   /**
    * Constructor takes an operator and the two trees to be used
    * as subtrees and builds a new tree out of the pieces. 
    * 
    * @param op  A string containing the operator
    * @param left  The left subtree
    * @param right The right subtree
    */
   public BinOpNode(String op, Evaluable left, Evaluable right) {
      this.op = op;
      this.left = left;
      this.right = right;
   }
   
   /**
    * The evaluate method finds the value of the left and right 
    * subtrees, then applies the appropriate operator.  The method
    * takes a Map containing variable names and values so we can 
    * evaluate trees containing variables.
    * 
    * @param env  The "environment": a map from var names to values.
    * @return  The value of the tree rooted at this node, as a double.
    */
   public double evaluate(Map<String, Double> env) {
      double x = left.evaluate(env);
      double y = right.evaluate(env);
      switch (op.charAt(0)) {
         case '+':  return x + y;
         case '-':  return x - y;
         case '*':  return x * y;
         case '/':  return x / y;
         default:   return Double.NaN;  // Bad operator!
      }
   }
   
   /**
    * Do an in-order traversal:  Produce the text of the left subtree,
    * the add the text of the operator, followed by the text of the
    * right subtree.  We always add parens so the precedence information
    * in the tree isn't lost, though we could be a bit more clever about
    * them if we wanted to...
    *
    * @return A fully parenthesized representation of the expression 
    *         rooted at this node.
    */
   public String toString() {
      return "( "+left+" "+op+" "+right+" )";
   }
}