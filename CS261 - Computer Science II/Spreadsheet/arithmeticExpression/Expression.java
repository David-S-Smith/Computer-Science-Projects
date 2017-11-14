package arithmeticExpression;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;

import arithmeticExpression.InfixToPostfixParens.SyntaxErrorException;

/**
 * The Expression class represents and evaluates arithmetic expressions
 * constructed from the four binary operators (+,-,*,/), floating-point
 * numeric values, and variables (names must be sequences of letters).
 * The constructor builds an expression tree representing the infix
 * expression string passed as argument, and the tree can later be 
 * evaluated or displayed via recursive traversals.  Input strings must
 * separate all tokens, including parens, with one or more spaces.
 *
 * @author Brad Richards
 */

public class Expression {
   // The converter's static -- only need one across all instances
   private static InfixToPostfixParens converter = new InfixToPostfixParens();
   // Also need a link to our expression tree and the set of variables
   private Evaluable root;
   private Set<String> variables = new HashSet<String>();
   // Scanner is a field to avoid resource leaks, but could be local otherwise
   private Scanner scan;
   
   /**
    * The constructor takes an infix expression as a String, builds and
    * stores a tree to represent it, and collects
    * all variable names found during this process in a Set.  Input expressions
    * must separate all values, operators, variables, and parentheses with
    * spaces.  (E.g. "1 * ( 2 + 3 )" is legal, but removal of any of the
    * spaces would result in a syntax error exception being thrown.)
    *
    *  @param infix a String describing the desired expression, in 
    *      infix notation
    */
   public Expression(String infix) throws SyntaxErrorException {
      // Use the book's code to turn the infix expression to postfix,
      // then create a Scanner to read from the postfix string.
      String postfix = converter.convert(infix);
      scan = new Scanner(postfix);
      Stack<Evaluable> nums = new Stack<Evaluable>();
      
      // Work through the "tokens" from the postfix expression, building
      // a tree.  If we find a number or a variable, push it onto the
      // stack.  When we come across an operator, pop the top two 
      // expressions, build a new binary operator node with the expressions
      // as subtrees, and push the new tree back onto the stack.
      while (scan.hasNext()) {
         // First, see if it's a number
         if (scan.hasNextDouble())
            nums.push(new ValueNode(scan.nextDouble()));
         else {
            String item = scan.next().trim();
            // Otherwise it's text of some sort.  See if it's
            // a variable name or an operator.
            if (Character.isLetter(item.charAt(0))) {
               nums.push(new VariableNode(item));
               variables.add(item);
            }
            else {
               if (nums.isEmpty())
                  throw new SyntaxErrorException("Too few operands in input expression");
               Evaluable right = nums.pop();
               if (nums.isEmpty())
                  throw new SyntaxErrorException("Too few operands in input expression");
               Evaluable left = nums.pop();
               nums.push(new BinOpNode(item, left, right));
            }
         }
      }
      root = nums.pop();
      if (!nums.isEmpty())
         throw new SyntaxErrorException("Too many operands in input expression");
   }
   
   /**
    * Evaluate the expression and return its value as a double.  The
    * method takes a Map containing variable names and their values
    * so we can properly evaluate expressions involving variables.  All
    * we need to do is ask the root node in the tree to evaluate itself.
    * 
    *  @param env  A map from variable names to values
    *  @return  The value of the expression as a double
    */
   public double evaluate(Map<String, Double> env) {
      return root.evaluate(env);
   }
   
   /**
    * Returns a Set containing the names of all variables found in 
    * this expression.
    * 
    *  @return the Set of variable names in the expression
    */
   public Set<String> getVariables() {
      return variables;
   }
   
   /**
    * Returns a String containing a fully-parenthesized representation
    * of the expression.  This expression should have spaces separating
    * the operators, operands, and parentheses so that the resulting
    * string could be used as input to the Expression constructor.  All
    * we need to do is ask the root node to print itself.
    * 
    *  @return a String containing a fully-parenthesized representation
    *   of the expression
    */
   public String toString() {
      return root.toString();
   }
   
   /**
    * The is a helper method for the test code below.  We shouldn't directly
    * compare floating-point values, but we can look at their difference.
    */
   private static boolean same(double d1, double d2) {
      return Math.abs(d1-d2) < .0000001;
   }
   
   public static void test() throws SyntaxErrorException {
      // Build an environment for use in tests below
      Hashtable<String,Double> env = new Hashtable<String,Double>();
      env.put("foo", 2.0);
      
      // Test a simple value
      Expression e = new Expression("5");
      assert same(e.evaluate(null), 5.0) : "value eval failed";
      assert e.toString().equals("5.0") : "value toString failed";
      assert e.getVariables().size() == 0 : "expr contained vars when it shouldn't";
      
      // Test a simple variable expression
      e = new Expression("foo");
      assert same(e.evaluate(env), 2.0) : "variable eval failed";
      assert e.toString().equals("foo") : "variable toString failed";
      assert e.getVariables().contains("foo") : "var not contained in getVars";
      assert e.getVariables().size() == 1 : "too many vars in set";
      
      // Test simple expressions involving each of the four operators
      e = new Expression("1 + 2");
      assert same(e.evaluate(null), 3.0) : "addition eval failed";
      assert e.toString().equals("( 1.0 + 2.0 )") : "addition toString failed";
      assert e.getVariables().size() == 0 : "unexpected vars in set";
      
      e = new Expression("1 - 2");
      assert same(e.evaluate(null), -1.0) : "subtraction eval failed";
      assert e.toString().equals("( 1.0 - 2.0 )") : "subtraction toString failed";
      
      e = new Expression("1 * 2");
      assert same(e.evaluate(null), 2.0) : "multiplication eval failed";
      assert e.toString().equals("( 1.0 * 2.0 )") : "multiplication toString failed";
      
      e = new Expression("1 / 2");
      assert same(e.evaluate(null), .5) : "division eval failed";
      assert e.toString().equals("( 1.0 / 2.0 )") : "division toString failed";
      
      // Make sure precedence works (should ideally do more of these)
      e = new Expression("( 3 + 2 ) * 5");
      assert same(e.evaluate(null), 25.0) : "parens test failed";
      assert e.toString().equals("( ( 3.0 + 2.0 ) * 5.0 )") : "parens toString failed";      
      
      // Test a large, complex expression involving parentheses, vars
      e = new Expression("foo + 1.5 * ( foo + 2.5 ) - 3.0 / 4.0");
      assert same(e.evaluate(env), 8.0) : "complex eval failed";
      assert e.toString().equals("( ( foo + ( 1.5 * ( foo + 2.5 ) ) ) - ( 3.0 / 4.0 ) )"):
         "complex toString failed";
      assert e.getVariables().contains("foo") : "var missing from getVars";
      assert e.getVariables().size() == 1 : "too many vars in set";
      
      // 
      // Now test things that should throw exceptions.  If the exception's not
      // thrown, we'll hit an assertion that's guaranteed to fail.  If the WRONG 
      // exception's thrown, we'll detect it via a second catch block.
      //
      
      // Evaluate an expression containing vars, but with a null environment
      try {
         e.evaluate(null);
         assert false : "Didn't throw exception on null!";
      }
      catch (IllegalArgumentException ex) {}                
      catch (Exception ex) {
          System.out.println("Threw wrong exception on null: "+ex);
      }
      
      // Evaluate an expression containing var, where var's missing from env
      try {
         new Expression("bar").evaluate(env);
         assert false : "Didn't throw exception on missing var!";
      }
      catch (IllegalArgumentException ex) {}          
      catch (Exception ex) {
          System.out.println("Threw wrong exception on missing var: "+ex);
      }
      
      // Evalute an expression containing a syntax error
      try {
         e = new Expression("( 1 + 2");
         assert false : "Didn't throw exception on syntax error!";
      }
      catch (SyntaxErrorException ex) {}        
      catch (Exception ex) {
          System.out.println("Threw wrong exception on syntax error: "+ex);
      }
      
      // Evalute an expression with too many operands
      try {
         e = new Expression("1 + 2 3");
         assert false : "Didn't throw exception on syntax error!";
      }
      catch (SyntaxErrorException ex) {}        
      catch (Exception ex) {
          System.out.println("Threw wrong exception on syntax error: "+ex);
      }
      
      // Evalute an expression with too few operands
      try {
         e = new Expression("1 +");
         assert false : "Didn't throw exception on syntax error!";
      }
      catch (SyntaxErrorException ex) {}        
      catch (Exception ex) {
          System.out.println("Threw wrong exception on syntax error: "+ex);
      }
   }
}