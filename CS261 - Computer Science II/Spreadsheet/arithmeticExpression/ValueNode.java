package arithmeticExpression;

import java.util.Map;

/**
 * The BinOpNode represents leaf nodes containing numeric
 * literals (doubles).  We don't need references to left or right
 * subtrees since this is a leaf, just the double itself.
 *
 * @author Brad Richards
 */
public class ValueNode implements Evaluable {
   private double number;  // Our value
   
   /**
    * The constructor takes this literal node's value, as a double.
    *
    * @param val  The value of this numeric literal node.
    */
   public ValueNode(double val) {
      number = val;
   }
   
   /**
    * evaluate takes the Map as argument, even though we don't
    * use it, otherwise we wouldn't be implementing the abstract
    * method from Evaluable and the compiler would be cross.
    *
    * @param env  The current environment -- unused by ValueNodes.
    * @return  The value of this literal node.
    */
   public double evaluate(Map<String, Double> env) {
      return number;
   }
   
   /**
    * No subtrees, so we only need to return this node's value as
    * a string.
    *
    * @return A string containing this node's value.
    */
   public String toString() {
      return ""+number; 
   }
}