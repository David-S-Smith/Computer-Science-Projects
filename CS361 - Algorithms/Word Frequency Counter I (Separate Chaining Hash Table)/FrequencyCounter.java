import java.util.regex.Pattern;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
  The FrequencyCounter demonstrates the use of a HashingTable to parse a text file and track each unique words' frequency.
  The user can then poll the FrequencyCounter for how often a word appears, or remove a word from the dataset.
  @author David S Smith
  @version 10/20/2017
*/

class FrequencyCounter{

  public static void main(String[] args){
    try{
      if(args.length <= 0){
          throw new IllegalArgumentException("No filename, please enter a filename to parse");
      }
      try{
          File file = new File(args[0]);
          Scanner scanFile = new Scanner(file);
          //set delimiters
          scanFile.useDelimiter("'*" + "[^a-zA-Z0-9_']+" + "'*"); //skip nonwords, and apostrophes on the end/beginning of words

          HashingTable<String,Integer> words = new HashingTable<String,Integer>();

          //read through file, putting things in hashtab
          parsing: while(scanFile.hasNext()){

            //to do this, get it out of the hashtab, increment its value, and put it back in
            String key = scanFile.next();
            key = key.toLowerCase();

            //check for empty string
            if(key.length() == 0) continue parsing;

            int val = 0;
            //if the word is already in the map
            if(words.contains(key)){
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

            //user input is enter, exit program
            if(input.length() == 0){
              unended = true;
              System.out.println("Goodbye.");
              scanInput.close();
              break user;
            }

            Integer freq = words.get(input);

            //if user input begins with a minus, remove element
            if(input.charAt(0) == '-'){
              //no occurrences
              String deleteThis = input.substring(1,input.length());
              freq = words.get(deleteThis);
              if(freq == null){
                System.out.println(deleteThis + " does not appear in text.");
              }
              else{
                System.out.println(freq + " instances of the word " + deleteThis + " removed.");
                words.delete(deleteThis);
              }
            }

            //input is to be retrieved, not removed
            //no occurrences
            else if(freq == null){
              System.out.println(input + " does not appear in text.");
            }
            //finally, if we want to retrieve based upon a word, and the word exists in our table, print out it's frequency
            else{
              System.out.println("'" + input + "' appears " + freq + " times.");
            }

          }

          return;
      }
      catch(FileNotFoundException fnferror){
        //user entered a filename that doesn't exist
          System.err.println("Caught FileNotFoundException: " + fnferror);
          return;
      }
    }
    catch(IllegalArgumentException iaerror){
      //user did not enter a filename at all
      System.err.println("Caught IllegalArgumentException: " + iaerror);
      return;
    }

  }
}
