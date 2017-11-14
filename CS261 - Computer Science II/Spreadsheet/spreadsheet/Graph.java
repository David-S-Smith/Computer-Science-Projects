package spreadsheet;

import java.util.List;
import java.util.LinkedList;
import java.util.HashMap;


/**
 * An adjacency list implementation of a graph, where the lists are associated with node
 * names in a HashMap.  In our simplified implementation, node names are Strings, and we
 * only care about connectivity, not edge weights.  Our edges are directional though --
 * adding an edge from X to Y means we can get from X to Y, but not from Y to X.
 */
public class Graph
{
    HashMap<String, List<String>> connections = new HashMap<String, List<String>>();
    
    /**
     * Adds information to the graph about a new connection between nodes.
     * 
     * @param from  Starting node
     * @param to  The node that's connected to from
     */
    public void addEdge(String from, String to) {
        // Look in map for nodes reachable from the starting node
        List<String> endpoints = connections.get(from);
        
        // If it's null then create a new list and make "to" the first node
        // in the list.  Otherwise just add "to" to the list that's already there.
        if (endpoints == null) {
            endpoints = new LinkedList<String>();
            endpoints.add(to);
            connections.put(from, endpoints);
        }
        else {
            endpoints.add(to);
        }
    }
    
    
    /**
     * See if there's an edge between a pair of nodes.
     * 
     * @param from  Start node
     * @param to  The destination node
     * @return  Returns true if there's an edge from "from" to "to"
     */
    public boolean adjacent(String from, String to) {
        List<String> endpoints = connections.get(from);
        return (endpoints != null) && endpoints.contains(to);
    }
    
    
    /**
     * Return a list of all nodes adjacent to "from".
     * 
     * @param from  The node whose neighbors we want
     * @return  List of all adjacent nodes
     */
    public List<String> getNeighbors(String from) {
        return connections.get(from);
    }
    
    
    /**
     * Just turn the table into a string and return that.
     */
    public String toString() {
        return connections.toString();
    }
}