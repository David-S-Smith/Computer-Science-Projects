import java.util.ArrayList;

/**
  The HashingTable is a symbol table that allows the user to input key-value-pairs, and retrieve values using the key reducing access time compared to normal searching.
  @author David S Smith
  @version 10/20/2017
*/

class HashingTable<K,V>{

    private final double MAX_LOAD = .75;
    private final int REMNEG = 0x7fffffff; //for taking off the negative sign on a number for hashing
    private final int[] primes = {11, 19, 41, 79, 163, 317, 641, 1279, 2557, 5119, 10243, 20479, 40961, 81919, 163841, 327673}; //a list of primes
    private int primeIndex, primeSize, totalItems;
    private ArrayList<ArrayList<Node<K,V>>> hashtab;


/**
  Creates an empty Hash Table.
*/
    public HashingTable(){
      primeIndex = 0;
      primeSize = primes[primeIndex];
      totalItems = 0;
      hashtab = new ArrayList<ArrayList<Node<K,V>>>(primeSize);

      //populate table
      for(int i = 0; i < primeSize; i++){
        hashtab.add(i, new ArrayList<Node<K,V>>());
      }
    }

/**
  Adds a key-value-pair to the table. In the event that the key is already present, overwrites its value with the entered value
  @param key A key to be hashed to match with the value
  @param value A value to be associated with the key
*/
    public void put(K key, V value){
      //get hashcode and create a node of the key-value-pair
      int modCode = (key.hashCode() & REMNEG) % primeSize;
      Node<K,V> node = new Node<K,V>(key, value);

      ArrayList<Node<K,V>> innerList = hashtab.get(modCode);
      int index = linSearch(innerList, key); //this will either be the index of the node with the same key or -1, in which case no node has this key already

      //add to end if it's not in the innerList already
      if(index == -1){
        innerList.add(node);
        //increment accordingly
        totalItems++;
        if(overloaded()){
          rehash();
        }
      }
      //replace the existing node with the new value if it is
      else{
        innerList.set(index, node);
      }
      return;
    }

/**
  Performs a linear search on the given ArrayList, returning the index of the key if present
  @param list An ArrayList of Nodes
  @param findK The key we're searching for in the list of nodes.
  @return The index in the ArrayList that contains a node with a key matching the argument key. Returns -1 if not found.
*/
    private int linSearch(ArrayList<Node<K,V>> list, K findK){

      for(int i = 0; i < list.size(); i++){
        Node<K,V> n = list.get(i);
        K k = n.getKey();
        if(k.equals(findK)){
          return i;
        }
      }
      return -1;
    }

/**
  Retrieves the value associated with a key.
  @param key The key whose hash code will be used as a reference to the value
  @return The value associated with the key, or null if no such value is found.
*/
    public V get(K key){
      int modCode = (key.hashCode() & REMNEG) % primeSize;
      ArrayList<Node<K,V>> innerList = hashtab.get(modCode);

      int index = linSearch(innerList, key);
      if(index == -1){
        return null;
      }

      //if the key-value-pair is actually in this place
      Node<K,V> n = innerList.get(index);
      if(n.getKey().equals(key)){
        return n.getVal();
        }
      else{
        return null;
      }
    }

/**
  Returns true or false based off the presence of the key in the table.
  @param key The key whose presence is in question.
  @return True for if the key is present in the table, false if it is not.
*/
    public boolean contains(K key){
      int modCode = (key.hashCode() & REMNEG) % primeSize;
      ArrayList<Node<K,V>> innerList = hashtab.get(modCode);

      int index = linSearch(innerList, key);
      if(index == -1){
        return false;
      }
      //if the key-value-pair is actually in this place
      Node<K,V> n = innerList.get(index);
      if(n.getKey().equals(key)){
        return true;
        }
      else{
        return false;
      }
    }

/**
  Removes a key-value-pair from the table.
  @param key The key associated with the key-value-pair
*/
    public void delete(K key){
      int modCode = (key.hashCode() & REMNEG) % primeSize;
      ArrayList<Node<K,V>> innerList = hashtab.get(modCode);

      int index = linSearch(innerList, key);
      if(index == -1){
        return;
      }
      //if the key-value-pair is actually in this place
      Node<K,V> n = innerList.get(index);
      if(n.getKey().equals(key)){
        innerList.remove(index);
        totalItems--;
        return;
        }
      else{
        return;
      }
    }

/**
  Returns the number of key-value-pairs in the table.
  @return An integer representing the number of key-value-pairs
*/
    public int size(){
      return totalItems;
    }

/**
  Checks to see if the table is overloaded based on a load factor requirement that it not exceed .75 load.
  @return True for if the table is overloaded, false otherwise.
*/
    private boolean overloaded(){

      double load = ((double) totalItems)/primeSize;

      if(load > MAX_LOAD){
        return true;
      }
      else{
        return false;
      }
    }

/**
  Rehashes the dataset into a larger hash table. Caps out at hashable 327673 indices, each of which chains out indefinitely.
*/
    private void rehash(){
      //if nextsize doesn't exist, return early
      if(primeIndex+1 >= primes.length){
        return;
      }

      //get next size we'll use
      primeIndex++;

      //make a temporary place to store the old map
      ArrayList<ArrayList<Node<K,V>>> temp = hashtab;

      //use the pointer of the old map to point to a new bigger map
      hashtab = new ArrayList<ArrayList<Node<K,V>>>();
      //update what size the big list uses
      primeSize = primes[primeIndex];
      for(int i = 0; i < primeSize; i++){
        hashtab.add(i, new ArrayList<Node<K,V>>());
      }
      //totalItems goes down to zero because every put will increment it anyway
      totalItems = 0;

      //now we rehash things into the new bigger list
      //for each list in the hashtab
      for(ArrayList<Node<K,V>> list : temp){
        //and for each node in the list
        for(Node<K,V> n : list){
          //put that node into the new hashtab
          put(n.getKey(), n.getVal());
        }
      }
      return;
    }

/**
  Storage for key-value-pairs
*/
    private class Node<K,V>{
        private K key;
        private V val;
        public Node(K key, V value){
            this.key = key;
            this.val = value;
        }
/**
  Returns key stored in node.
  @return The node's key.
*/
        public K getKey(){
            return key;
        }
/**
  Returns value stored in node
  @return The node's value.
*/
        public V getVal(){
            return val;
        }

/**
  Returns a String representation of the node
  @return The key and value (in that order) as a sString
*/
        public String toString(){
          return "[" + key + "," + val + "]";
        }
    }
}
