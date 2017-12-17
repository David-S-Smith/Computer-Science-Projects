import java.util.HashMap;
import java.util.ArrayList;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import java.util.PriorityQueue;
import java.util.Map;


/**
  A specialized graph designed for use in the Navigator project. Uses A* to find paths between two cities.

  @author David S Smith
  @version 12/7/2017
*/
class Graph {

    private HashMap<String, City> map;
    private int numCities;
    private int numEdges;

    public Graph(){
        map = new HashMap<String, City>();
        numEdges = 0;
        numCities = 0;
    }

    /**
      Returns the number of cities present in the graph.
      @return The number of cities present in the graph.
    */
    public int countCities(){
        return numCities;
      }

    /**
      Returns the number of edges present in the graph.
      @return The number of edges present in the graph.
    */
    public int countEdges(){
        return numEdges;
      }

    /**
      States whether a city is present in the graph.
      @param name The name of the city queried.
      @return True if the city is present, false otherwise.
    */
    public boolean contains(String name){
        return map.containsKey(name);
    }

    /**
    Finds a path from two cities, if one exists, and returns the path and the total distance of the path.
    @param first The name of the first city, the starting point.
    @param goal The name of the second city, the destination point.

    @return A SimpleEntry pair containing an ArrayList with the path's cities, from start to finish,
    and a double of the distance traveled (in that order). Returns null if one or more of the cities are invalid,
    and a pair CONTAINING null in the first term and -1 in the second if no path exists

     */


    public SimpleEntry<ArrayList<String>,Double> findPath(String first, String goal){
      City start = map.get(first);
      City finish = map.get(goal);

      if((start == null) || (finish == null)) return null;

      //directly adjacent case
      if(areAdjacent(first,goal)){
        ArrayList<String> path = new ArrayList<String>();
        path.add(first);
        path.add(goal);
        double d = start.adjacencies.get(goal);
        return new SimpleEntry<ArrayList<String>,Double>(path,d);
      }

      HashMap<String,String> closedList = new HashMap<String, String>();
      HashMap<String,Double> distanceTo = new HashMap<String, Double>(); //previous best distance to this city
      PriorityQueue<City> openList = new PriorityQueue<City>();

      City current = start;
      //add start onto nodes to view
      openList.add(current);
      closedList.put(first,null);
      current.reachedFrom = null;
      distanceTo.put(first, 0.0);

      while(openList.size() != 0){
        //remove best option
        current = openList.poll();

        if(distanceTo.containsKey(current.name)){
          if(distanceTo.get(current.name) < current.distanceToReachThis){
            continue;
          }
        }

        //we've found the end, assemble list and total-distance, return
        if(current.equals(finish)){
          //assemble path and total
          closedList.put(current.name, current.reachedFrom);
          ArrayList<String> path = new ArrayList<String>();
          String addition = current.name;

          //go backwards through the hashmap of pairs
          while(!(addition.equals(first))) {
            path.add(addition);
            addition = closedList.get(addition);
          }
          path.add(first);
          Collections.reverse(path);
          return new SimpleEntry<ArrayList<String>,Double>(path, current.distanceToReachThis);
        }
        //otherwise, pull the adjacencies from this city
        for(Map.Entry<String,Double> e : current.adjacencies.entrySet()){
          //pull out relevant information on city
          String name = e.getKey();
          double distanceToCity = e.getValue();
          City possibility = (City)map.get(name).clone();

          //if this city is not in the closed list
          if(closedList.get(name) == null){

            //adjust score and add to PriorityQueue
            possibility.distanceToReachThis = current.distanceToReachThis + distanceToCity;
            possibility.score = possibility.distanceToReachThis + possibility.crowFliesDistance(finish);

            if(distanceTo.containsKey(possibility.name)){
                if(distanceTo.get(possibility.name) < possibility.distanceToReachThis){
                continue;
              }
            }

            openList.add(possibility);
            distanceTo.put(possibility.name, possibility.distanceToReachThis);
            possibility.reachedFrom = current.name;
          }

        }
        //after we've done all we want with this city, add it to the closed list and update previous
          closedList.put(current.name, current.reachedFrom);

      }
      //path not found
      return new SimpleEntry<ArrayList<String>,Double>(null,-1.0);
    }


/**
  Connects two cities by an edge of a given distance. Can also overwrite an existing connection (DOES NOT ALLOW PARALLEL EDGES)
  @param name1 The first city in the connection
  @param name2 The second city in the connection
  @param distance The length of the edge connecting the two cities. In this context, the distance is interpretted as Kilometers
*/
    public void addEdge(String name1, String name2, double distance){
        City c1 = map.get(name1);
        City c2 = map.get(name2);

        c1.addEdge(name2, distance);
        if(c2.addEdge(name1, distance)) numEdges++; //increment number of edges if we actually added a new edge
    }

    /**
    Creates a city and adds it to the graph.
    @param name The city's name.
    @param latitude The city's latitude in degrees
    @param longitude The cities longitude in degrees
    */

    public void addCity(String name, double latitude, double longitude){
        City c = new City(name, latitude, longitude);
        map.put(name, c);
        numCities++;
        return;
    }

    /**
      Removes an edge in the graph. Does nothing if the edge does not exist.
      @param name1 The first city in the connection
      @param name2 The second city in the connection
    */
    public void deleteEdge(String name1, String name2){
        City c1 = map.get(name1);
        City c2 = map.get(name2);

        c1.remEdge(name2);
        if(c2.remEdge(name1)) numEdges--;
        return;
    }
/**
  Removes a city from the graph.
  @param name The city's name.
*/
    public void deleteCity(String name){
        if(map.remove(name) != null) numCities--;
        return;
    }

/**
  Returns whether two cities are directly adjacent
  @param name1 The first city in the connection
  @param name2 The second city in the connection
  @return True if the cities are adjacent, false otherwise
*/
    public boolean areAdjacent(String name1, String name2){
        City c1 = map.get(name1);
        return c1.adjacencies.containsKey(name2);
    };

    /**
      Returns all cities adjacent to a given city.
      @param name The name of the city
      @return An ArrayList of names of cities adjacent to the given city.
    */

    public ArrayList<String> getAdjacencyList(String name){
        City c = map.get(name);
        ArrayList<String> list = (ArrayList<String>)c.adjacencies.keySet();
        return list; //TODO: might not be enough?
    }   // list of adj

    /**
      A City contained in the graph. Contains its latitude and longitude in radians, as well as the cities name and the
      cities adjacent to it in the form of a hashmap pairing adjacent cities with doubles representing the distance between
      this city and the edge-partner.
    */

    private class City implements Comparable<City>, Cloneable{
        public String name;
        public double lat, lng;
        private double actualLat, actualLng;
        public HashMap<String, Double> adjacencies;

        public double score;
        public double distanceToReachThis;
        public String reachedFrom;

        //Creates a city with longitude and latituge. Coordinates are converted to radians from degrees.
        public City(String name, double lat, double lng){
            this.name = name;
            this.actualLat = lat;
            this.actualLng = lng;
            reachedFrom = "";

            this.lat = lat * Math.PI/180;
            this.lng = lng * Math.PI/180;
            score = 0.0;
            distanceToReachThis = 0.0;
            adjacencies = new HashMap<String,Double>();
        }
        //returns true if an edge was added, false if it was alterd.
        public boolean addEdge(String other, double distance){

          boolean val = true;
            if(adjacencies.containsKey(other)) val = false;
                adjacencies.put(other, distance);
            return val;
        }

        //returns true if the edge was removed, false if there was no edge to remove between the two cities.
        public boolean remEdge(String other){
          boolean val = true;
            if(adjacencies.containsKey(other)) val = false;
                adjacencies.remove(other);
            return val;
        }

        //returns true if the city is adjacent to the given city
        public boolean adjacent(String otherName){
            if(adjacencies.containsKey(otherName)) return true;
            else return false;
        }

        //calculates the "crow flies distance" between thic city and another, in Kilometers
        private final double EARTH_RADIUS = 6371;
        public double crowFliesDistance(City other){
            return Math.acos((Math.sin(this.lat) * Math.sin(other.lat)) + (Math.cos(this.lat) * Math.cos(other.lat) * Math.cos(this.lng - other.lng))) * EARTH_RADIUS;
        }

        //sets the score, to be used in comparisons of cities via the compareTo method (such as in a PriorityQueue)
        public void setScore(double score){
            this.score = score;
        }

        //cities are considered equal if they share a name, regardless of other values
        public boolean equals(City other){
            return this.name.equals(other.name);
        }

        //compares cities by their scores. Notably, cities being equal via compareTo means something VERY DIFFERENT from being equal via equals
        @Override
        public int compareTo(City other){
            return (int)(this.score - other.score); //larger scores are considered lower priority than smaller scores
        }

        //copies over a cities name, Coordinates and predecessor
        @Override
        protected City clone(){

          City copy = new City(this.name, actualLat, actualLng);
          copy.score = 0;
          copy.distanceToReachThis = 0;
          copy.reachedFrom = this.reachedFrom;

          @SuppressWarnings("unchecked")
          HashMap<String,Double> list = (HashMap<String,Double>) this.adjacencies.clone();
          copy.adjacencies = list;

          return copy;
        }

        //The string representing a city is just it's name
        @Override
        public String toString(){
          return name;
        }
    }
}
