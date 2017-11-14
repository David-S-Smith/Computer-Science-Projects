import java.util.regex.Pattern;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.TreeMap;
/**
  The FrequencyCounter demonstrates the use of a HashingTable to parse a text file and track each unique words' frequency.
  The user can then poll the FrequencyCounter for how often a word appears, or remove a word from the dataset.
  @author David S Smith
  @version 11/10/2017
*/

class WordFreqs2{

  public static void main(String[] args){
    try{
      if(args.length <= 0){
          System.err.println("No filename, please enter a filename to parse");
          System.exit(1);
      }
      try{
          File file = new File(args[0]);
          Scanner scanFile = new Scanner(file);
          //set delimiters
          scanFile.useDelimiter("'*" + "[^a-zA-Z0-9_']+" + "'*"); //skip nonwords, and apostrophes on the end/beginning of words


          RedBlackTree<String,Integer> words = new RedBlackTree<String,Integer>();

          //read through file, putting things in hashtab
          parsing: while(scanFile.hasNext()){

            //to do this, get it out of the hashtab, increment its value, and put it back in
            String key = scanFile.next();
            key = key.toLowerCase();

            //check for empty string
            if(key.length() == 0) continue parsing;

            int val = 0;
            //if the word is already in the map

            if(words.contains(key)){ //TODO: CHANGE BACK
            // if(words.containsKey(key)){
              val = words.get(key);
              //increment, we're adding a new words
              val++;
            }
            //otherwise, put the word in the map with 1 count on it's val
            else{
              val = 1;
            }
            //put new value into map
            words.put(key,val);
          }

          //output unique words
          scanFile.close();
          System.out.println("The text contains " + words.size() + " distinct words.");

          //start loop to read user inputs
          System.out.println("Please enter a word to get its frequency, or press enter to leave.");
          Scanner scanInput = new Scanner(System.in);

          //based off of user inputs do different things
          boolean unended = true;
          user: while(unended){
              String input = scanInput.nextLine();

              if(input.length() == 0){ //pressed enter, end program
                  unended = true;
                  System.out.println("Goodbye.");
                  scanInput.close();
                  break user;
                }


              else if(input.startsWith("-")){ //- remove
                  String word = input.substring(1);
                       if(!words.contains(word)){
                      System.out.println("'"+ word + "' does not appear in text.");
                  }
                  else{
                      int freq = words.get(word);
                      words.delete(word);
                      // words.remove(word);
                      System.out.println(freq + " instances of the word '" + word + "' removed.");
                  }
              }

              else if(input.startsWith(">")){ //> successor or last
                  if(input.length()==1){
                      String last = words.findMax();
                      System.out.println("The alphabetically-last word in the parsed text is '" + last + "'.");
                    }
                  else{
                    String word = input.substring(1);
                         if(!words.contains(word)){
                      System.out.println("'"+ word + "' does not appear in text");
                    }
                    else{
                      String succ = words.findSuccessor(word);
                      System.out.println("The next word after '" + word + "' is '" + succ + "'.");
                    }
                  }
              }

              else if(input.startsWith("<")){ //< predecessor or first
                  if(input.length()==1){
                      String first = words.findMin();
                      System.out.println("The alphabetically-first word in the parsed text is '" + first + "'.");
                    }
                  else{
                      String word = input.substring(1);
                             if(!words.contains(word)){
                          System.out.println("'"+ word + "' does not appear in text");
                        }
                        else{
                           String pred = words.findPredecessor(word);// TODO
                          System.out.println("The word just before '" + word + "' is '" + pred + "'.");
                        }
                  }
              }

              else{ //frequency
                  String word = input;
                         if(!words.contains(word)){
                      System.out.println("'"+ word + "' does not appear in text");
                  }
                  else{
                      int freq = words.get(word);
                      System.out.println("'" + word + "' appears " + freq + " times");
                  }
              }

            }

          System.exit(0);
      }
      catch(FileNotFoundException fnferror){
        //user entered a filename that doesn't exist
        System.err.println("Cannot open file: " + args[0]);
        System.exit(1);
      }
    }
    catch(IllegalArgumentException iaerror){
      //user did not enter a filename at all
      System.err.println("No filename, please enter a filename to parse");
      System.exit(1);
    }

  }
}
