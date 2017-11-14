import java.lang.Exception;

/**
The Red-Black Tree is a Binary Search Tree of key-value-pairs that self-balances as pairs are added or removed.
@author David S Smith
@version 11/10/2017
 */
class RedBlackTree<K extends Comparable<K>,V>{

    private int size;
    private Node<K,V> root;
    private enum Colour {BLACK, RED}; //datatype representing the colour state of a node.

    /**
    Creates an empty Red Black Tree, containing no elements.
     */
    public RedBlackTree(){
        size = 0;
        root = null;
    }

    /**
      Places a key value pair in the Red Black Tree. If the key is already present, updates the value.
      @param key A key used to look up a value
      @param value A value associated with a key
    */
    public void put(K key, V value){
        //if there are no present nodes
        if(size == 0){
            root = new Node<K,V>(key, value); //make a black root node
        }
        else{
            //otherwise, start recursive search/alterations
            root = findAndAdd(root, key, value);
        }
        //make sure the root is black
        root.colour = Colour.BLACK;
        size = root.subTreeSize;
        return;
    }

    /*
      Used to fix up issues with the Red Black Tree, as we return from a series of recursive calls that may have broken these rules.
    */
    private Node<K,V> applyRules(Node<K,V> n){
      if(n == null) return n;
      //if there are any branches to this node
      if((n.left != null) || (n.right != null)){
          //begin checks
          //if the left child is null and the right child is not
          if((n.left == null) && (n.right != null)){
            n = rotateLeft(n);
          }

          //if the left side is not red but the right side is
          if(!isRed(n.left) && isRed(n.right)){
              n = rotateLeft(n);
          }
          //if a red node has a red child
          if(isRed(n.left) && isRed(n.left.left)){
              n = rotateRight(n);
          }
          //if both children are red
          if(isRed(n.left) && isRed(n.right)){
              n = colourFlip(n);
          }
      }
      return n;
    }

    /*
      Adds or updates the node, fixing the tree on the way up
    */
    private Node<K,V> findAndAdd(Node<K,V> n, K key, V val){

        if(n == null){
            size++;
            return new Node<K,V>(key, val);
        }

        //going down, BST search to alter value
        int cmp = key.compareTo(n.key);
        if(cmp < 0){//searchleft
            n.left = findAndAdd(n.left, key, val);
        }
        else if(cmp > 0) {//searchright
            n.right = findAndAdd(n.right, key, val);
        }
        else{//equal case, alter value
            n.value = val;
        }

        //going up, apply rules to maintain RB tree
        n = applyRules(n);

        recalcSize(n);

        return n;
    }
    /**
      Removes a key-value-pair from the tree if it exists
      @param key The key of the pair to be deleted
    */
    public void delete(K key){
        if(isEmpty()){
          return;
        }
        if(!contains(key)){
            return;
        }
        root.colour = Colour.RED;
        root = findAndRemove(root, key);
        size--;
        if(root != null) root.colour = Colour.BLACK;
        return;
    }
    /*
      Removes the node, rebalancing the tree on the way up
    */
    private Node<K,V> findAndRemove(Node<K,V> n, K desired){
        int cmp = desired.compareTo(n.key);

        if(cmp < 0){ //left
            //if the left child and right child are both black
            if(!isRed(n.left) && !isRed(n.right)){
                n = colourFlip(n);
            }
            n.left = findAndRemove(n.left,desired);

        }
        else if(cmp > 0){ //right
            //if the left child and right child are both black
            if(!isRed(n.left) && !isRed(n.right)){
                n = colourFlip(n);
                n.right = findAndRemove(n.right,desired);
            }
            else if(isRed(n.left) && !isRed(n.right)){
                n = rotateRight(n);
                n.right = colourFlip(n.right);
                n.right.right = findAndRemove(n.right.right,desired);
            }
        }
        else{ //equal
          //no children, just remove this node
          if((n.left == null) && (n.right == null)){
            n = null;
          }
          //one left child, promote node
          else if(n.right == null){
            n = n.left;
          }
          //two children, swap with successor and return tree with node removed post swap
          else{
            K succKey = findSuccessor(n.key);
            Node<K,V> succ = binSearch(n.right, succKey);
            V succVal = succ.value;
            Colour succColour = succ.colour;
            n.key = succKey;
            n.value = succVal;
            // n.colour = succColour;
            n.right = findAndRemove(n.right, succKey);
            n = n;
          }
        }

        //fix-ups
        n = applyRules(n);
        recalcSize(n);

        return n;
    }

/**
  Returns the value associated with a key, or null if the key is not present in the tree.
  @param key The key who's value we want to find.
  @return The value associated with the key passed in.
*/
    public V get(K key){
        Node<K,V> query = binSearch(root, key);
        if(query == null){ //key was not found
            return null;
        }
        else{ //key was found
            return query.value;
        }
    }

  /*
    Finds the size of the node given via it's left and right sides. Not recursive (left and right sides must have their sizes up to date).
  */

    private void recalcSize(Node<K,V> n){
        if(n == null) return;
        int l = 0;
        int r = 0;
        if(n.left != null){
            l = n.left.subTreeSize;
        }
        if(n.right != null){
            r = n.right.subTreeSize;
        }
        n.subTreeSize = l + r + 1;
    }

  /**
      Returns whether a key is present in the data structure
      @param key The key we want to verify the presence of.
      @return True if the key is present, false if it isn't.
  */
    public boolean contains(K key){
        Node<K,V> query = binSearch(root, key);
        if(query == null){ //key was not found
            return false;
        }
        else{ //key was found
            return true;
        }
    }

    /**
      Returns whether the tree is empty
      @return True if the tree is empty, false if it isn't.
    */
    public boolean isEmpty(){
        if(size==0){
            return true;
        }
        return false;
    }

    /**
      Returns the size of the tree.
      @return The number of nodes in the tree.
    */
    public int size(){
        return size;
    }

    /**
      Returns the first ranked key
      @return The key that comes before all other keys if ordered.
    */
    public K findMin(){ // get smallest key
        if(size<=1){
            return root.key;
        }
        else{
            return crawlLeft(root).key;
        }
    }

    /**
      Returns the last key
      @return The key that comes after all other keys if ordered.
    */

    public K findMax(){ // get largest key
        if(size<=1){
            return root.key;
        }
        else{
            return crawlRight(root).key;
        }
    }

    /**
      Returns the predecessor of a given key.
      @param key The key we want the predecessor of.
      @return The key that comes just before the key passed in.
    */

    public K findPredecessor(K key){
        if(findMin().equals(key)) return key;
        //rightmost child of key's left child
        Node<K,V> query = predecessorSearch(root, key, root);
        if(query==null){ //if original key doesn't exist, we can't find it's successor
          return null;
        }
        else{
          return query.key;
        }
    }

    /*
      Returns the predecessor
    */
    private Node<K,V> predecessorSearch(Node<K,V> n, K queried, Node<K,V> before){
      if(n == null) return null;//queried key dne
      int cmp = queried.compareTo(n.key);
      if(cmp < 0){ //left
        return predecessorSearch(n.left, queried, before);
      }
      else if(cmp > 0){ //right
        return predecessorSearch(n.right, queried, n);
      }
      else{ //equals
        if((n.left == null) && (n.right == null)) return before;
        else return crawlRight(n.left);
      }
    }

    /**
      Returns the succ of a given key.
      @param key The key we want the successor of.
      @return The key that comes just after the key passed in.
    */
    public K findSuccessor(K key){
        if(findMax().equals(key)) return key;
        //leftmost child of key's right child
        Node<K,V> query = successorSearch(root,key,root);
        if(query==null){ //if original key doesn't exist, we can't find it's successor
            return null;
        }
        else{
          return query.key;
        }
    }

    /*
      Returns the successor
    */
    private Node<K,V> successorSearch(Node<K,V> n, K queried, Node<K,V> before){
      if(n == null) return null;//queried key dne
      int cmp = queried.compareTo(n.key);
      if(cmp < 0){ //left
        return successorSearch(n.left, queried, n);
      }
      else if(cmp > 0){ //right
        return successorSearch(n.right, queried, before);
      }
      else{ //equals
        if((n.left == null) && (n.right == null)) return before;
        else return crawlLeft(n.right);
      }
    }

    /**
      Removes the key that is first in the order of all keys.
    */
    public void deleteMin(){
        if(size<=1){
            root = null;
            size = 0;
        }
        else{
            K k = findMin();
            delete(k);
        }
        return;
    }

    /**
      Removes the key that is last in the order of all keys.
    */

    public void deleteMax(){
        if(size<=1){
            root = null;
            size = 0;
        }
        else{
            K k = findMax();
            delete(k);
        }
        return;
    }


    /**
      Removes all keys from the tree.
    */
    public void clear(){
        size = 0;
        root = null;
    }

    /*
      Returns the node with the desired key, or null if it doesn't exist in the tree.
     */
    private Node<K,V> binSearch(Node<K,V> n, K desired){
        if(n == null) return null;
        int cmp = desired.compareTo(n.key);
        if(cmp < 0){
            return binSearch(n.left, desired);
        }
        else if(cmp > 0) {
            return binSearch(n.right, desired);
        }
        else{
            return n;
        }


    }

    /*
    Finds the leftmost Node
     */

    private Node<K,V> crawlLeft(Node<K,V> n){
        if(n==null) return null;
        if(n.left == null){
            return n;
        }
        else{
            return crawlLeft(n.left);
        }
    }

    /*
    Finds the rightmost Node
     */
    private Node<K,V> crawlRight(Node<K,V> n){
        if(n==null) return null;
        if(n.right == null){
            return n;
        }
        else{
            return crawlRight(n.right);
        }
    }

    /*
      Returns true if the node is red, false if is black or null
    */
    private boolean isRed(Node<K,V> n){
        if(n == null) return false;
        else if(n.colour == Colour.RED) return true;
        else return false;
    }

    /*
      The node passed adopts the colour of it's children and it's children adopt it's previous colour (only works if both children are the same colour)
    */
    private Node<K,V> colourFlip(Node<K,V> parent){
        Node<K,V> l = parent.left;
        Node<K,V> r = parent.right;
        //checks are already done
        if((l == null) || (r == null)){
            System.err.println("Error: Attempted colourFlip on a node with less than two children.");
            System.exit(1);
        }
        if(l.colour != r.colour){
            System.err.println("Error: Cannot perform colourFlip on dissimilar children, must be same colour.");
            System.exit(1);
        }

        Colour temp = parent.colour;
        parent.colour = l.colour;

        l.colour = temp;
        r.colour = temp;

        parent.left = l;
        parent.right = r;
        return parent;
    }

    /*
      Rotates the tree right about the parent
    */
    private Node<K,V> rotateRight(Node<K,V> parent){
        //split node
        Colour cHead = parent.colour;
        Colour cLeft = parent.left.colour;

        Node<K,V> leftSplit = parent.getLeft();
        Node<K,V> rightSplit = parent;
        rightSplit.left = null;

        Node<K,V> leftSplitSwing = null;
        if(leftSplit.right != null){
            leftSplitSwing = leftSplit.getRight();
        }
        leftSplit.right = null;

        Node<K,V> newParent;
        newParent = leftSplit;
        newParent.colour = cHead;

        newParent.right = rightSplit;
        newParent.right.colour = cLeft;

        newParent.right.left = leftSplitSwing;

        if(newParent.left != null)recalcSize(newParent.left);
        recalcSize(newParent.right);
        recalcSize(newParent);

        return newParent;
    }

    /*
      Rotates the tree left about the parent
    */
    private Node<K,V> rotateLeft(Node<K,V> parent){
        //split node
        Colour cHead = parent.colour;
        Colour cRight = parent.right.colour;

        Node<K,V> rightSplit = parent.getRight();
        Node<K,V> leftSplit = parent;
        leftSplit.right = null;

        Node<K,V> rightSplitSwing = null;
        if(rightSplit.left != null){
            rightSplitSwing = rightSplit.getLeft();
        }
        rightSplit.left = null;

        Node<K,V> newParent;
        newParent = rightSplit;
        newParent.colour = cHead;

        newParent.left = leftSplit;
        newParent.left.colour = cRight;

        newParent.left.right = rightSplitSwing;

        recalcSize(newParent.left);
        if(newParent.right != null)recalcSize(newParent.right);
        recalcSize(newParent);

        return newParent;
    }

    /*
      Stores the key value pair, the colour of the node, the node's children, and the subtree of the node (it's left and right size plus itself)
    */
    private class Node<K,V>{

        public Colour colour;
        public Node<K,V> left, right;
        public K key;
        public V value;
        public int subTreeSize;

        /*
          Makes a new red node with no children, based on the key and value passed.
        */
        public Node(K key, V value){
            colour = Colour.RED;
            this.key = key;
            this.value = value;
            left = null;
            right = null;
            subTreeSize = 1;
        }

        /*
          Returns a node of the left subtree of this node.
        */
        public Node<K,V> getLeft(){
            return left;
        }

        /*
          Returns a node of the right subtree of this node.
        */
        public Node<K,V> getRight(){
            return right;
        }

        /*
          Returns a string representation of the node. Includes information on the key and value, but not the children, colour, or subtreesize.
        */
        public String toString(){
            String str = "(" + key + "," + value + ")";
            return str;
        }
    }

}
