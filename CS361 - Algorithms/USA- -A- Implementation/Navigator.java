import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import java.util.ArrayList;
import java.util.AbstractMap.SimpleEntry;
/**
The Navigator parses the US-capitals.geo file for all 50 state capitals, then can be queried for connections between various capitals by land.

@author David S Smith
@version 12/7/2017
*/

class Navigator{
    public static void main(String[] args){

        Graph graph = new Graph();
        Scanner parser = null;
        try{
            parser = new Scanner(new File("US-capitals.geo"));
        }
        catch(FileNotFoundException f){
            System.err.println("Unable to find US-capitals.geo - Please make sure the file is accessible to the project");
            System.exit(1);
        }
        parser.useDelimiter("\\n|\\t|\\r");

        //read through until hitting a blank line, then start next looop
        graphPopulateVertices: while(parser.hasNext()){
            String name = parser.next();
            if(name.length() == 0) break graphPopulateVertices;

            double latitude = parser.nextDouble();
            double longitude = parser.nextDouble();
            graph.addCity(name, latitude, longitude);

        }

        graphPopulateEdges: while(parser.hasNext()){
            String first = parser.next();
            String second = parser.next();
            double distance = parser.nextDouble();
            graph.addEdge(first, second, distance);
        }

        parser.close();

        boolean inputReading = true;

        Scanner scan = new Scanner(System.in);

System.out.println("Please enter a pair of cities separated by a hyphen");
        userInput: while(inputReading){
            String input = scan.nextLine();

            if(input.length() == 0){
                inputReading = false;
                break userInput;
            }
            Scanner query = new Scanner(input);
            query.useDelimiter("\\n|-");
            String first = "";
            String second = "";
            try{
              first = query.next();
              second = query.next();
            }catch(NoSuchElementException e){
              System.out.println("Please enter two valid cities");
              continue userInput;
            }

            //test before finding path
            if((first.length() == 0) || (second.length() == 0)){
                System.out.println("Please enter two valid cities");
                continue userInput;
            }
            if(!graph.contains(first)){
                System.out.println("'"+ first + "' is not a valid city.");
                continue userInput;
            }
            if(!graph.contains(second)){
                System.out.println("'"+ second + "' is not a valid city.");
                continue userInput;
            }

            SimpleEntry<ArrayList<String>,Double> response = graph.findPath(first, second);

            if(response == null){
                System.out.println("Please enter two valid cities");
                continue userInput;
            }

            ArrayList<String> path = response.getKey();
            double distance = response.getValue();

            if(distance < 0){
                System.out.println("Sorry, there's no path by land from '" + first + "' to '" + second + "'.");
                continue userInput;
            }
            else{
                //route string construction
                String route = path.get(0);
                for(int i = 1; i < path.size(); i++){
                    route += " - " + path.get(i);
                }

                System.out.println("Path found: " + route + " (" + distance + "km)");
                continue userInput;
            }

        }
        System.out.println("Goodbye!");
        scan.close();
        System.exit(0);
    }

}
