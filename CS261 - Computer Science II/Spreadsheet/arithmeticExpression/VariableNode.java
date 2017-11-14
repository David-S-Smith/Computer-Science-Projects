package arithmeticExpression;

import java.util.Map;

/**
 * The VariableNode represents leaf nodes containing variable names.
 * We don't need references to left or right subtrees since this is 
 * a leaf, just the name of the variable.
 *
 * @author Brad Richards
 */
class VariableNode implements Evaluable {
   private String varName;  // Our name
   
   /**
    * The constructor takes the variable's name as a string.
    *
    * @param varName  The variable's name.
    */
   public VariableNode(String varName) {
      this.varName = varName;
   }
   
   /**
    * If the Map is null or doesn't contain a value for the variable
    * name stored in this node, throw an exception.  Otherwise, return
    * the variable's value from the Map.
    *
    * @param env  A Map from variable names to their values
    * @return  This variable's value, if it exists in the map
    * @throws IllegalArgumentException if variable's not in the map
    */
   public double evaluate(Map<String, Double> env) {
      if (env == null || !env.containsKey(varName))
         throw new IllegalArgumentException(varName);
      else
         return env.get(varName);
   }
   
   /**
    * We need to produce a string containing its <i>name</i>, not its <i>value</i>.
    *
    * @return A string containing the variable's name.
    */ 
   public String toString() {
      return varName; 
   }
}