import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
/*

  Sportsball reads a text file to print out the order players will enter a game of sportsball, as well as inform the manager when
  no players are available to send out, and inform the manager how many players remain.

  @author   David S Smith
  @version  9/29/2017

*/


class Sportsball {

    public static void main(String[] args) throws IllegalArgumentException{
        if(args.length <= 0){
            throw new IllegalArgumentException();
        }

        File file = new File(args[0]);
        PrioQueue<String> q = new PrioQueue<String>();
        try{
            Scanner scan = new Scanner(file);

            loopDeeFile:
            do{
                //break file into lines
                String line = scan.nextLine();
                //look at the line
                if(line.equals("GO!")){ //line is go, grab a new player
                    if(q.getSize() == 0){ //no players in queue to grab
                        System.out.println("No one is ready!");
                        break loopDeeFile;
                    }
                    //if there are players in queue, take off the greatest valued one
                    String challengerApproaching = q.remove();
                    System.out.println(challengerApproaching + " enters the game.");
                }

                else{//break lines into name and score
                    String[] parts = line.split("/");
                    String name = parts[0];
                    int score = Integer.parseInt(parts[1]);
                    q.insert(name, score);
                }
            }while(scan.hasNext());
            System.out.println("At the end, there were " + q.getSize() + " players left.");
        }
        catch(FileNotFoundException fnferror){
            System.err.println("File not found: " + fnferror);
        }

    }
}
