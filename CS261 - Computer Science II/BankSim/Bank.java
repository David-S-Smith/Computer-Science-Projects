import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

import javax.swing.JFileChooser;
/**
	Simulation of a bank with a series of lines operated by tellers.
	Customers are entered via a text file containing data on timestamp of arrival, duration of transaction,
	and whether they have arrived at the bank itself or are using the drivethru line, which has different
	rules than the other lines.

	@author Max Haggard, David S Smith, and Miles Cameron
	@version 10/27/2016
*/

public class Bank {
	private int numLines, totalCustomers, clock, nextArrival;
	private Queue<Customer> entries;
	private Teller[] tellers;


	public Bank(int numLines){
		nextArrival = 0;
		clock = 0;
		this.numLines = numLines;
		totalCustomers = 0;
		tellers = new Teller[numLines];
		for(int i = 0; i < numLines; i++){
			tellers[i] = new Teller();
		}
		generateCustomers();
	}

	/**
	 * fills the entries list with customers parsed from a selected file. also updates total customer count
	 */
	private void generateCustomers(){
		File theFile = selectFile();
		entries = new LinkedList<Customer>();

		try {
            Scanner data = new Scanner(theFile);   // Wrap a scanner around File
            while(data.hasNext()) {// Keep going while there's more
                int arrivalTime = data.nextInt();
                int transTime = data.nextInt();
                boolean driveThru = data.nextBoolean();
                Customer newCust = new Customer(arrivalTime, transTime, driveThru);
                entries.add(newCust);
            }

        totalCustomers = entries.size();

        }

        catch (FileNotFoundException e) {
            System.err.println("File does not exist: "+theFile);
        }

        catch (Exception e) {
            System.err.println("Something else went wrong...");
            e.printStackTrace(System.err);
        }
	}
        /**
         * This method brings up a dialog box asking the user to select a file.
         * If all goes well, we get a File variable set to the actual file the
         * user selected.
         */
        private static File selectFile() {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION)
                throw new Error("Input file not selected");
            File inFile = chooser.getSelectedFile();
            return inFile;
        }

	/**
		Simulates the bank running, based on a series of customer entries.
		Prints information based on the time
	*/
	public void runSim(){
		boolean tellersFinished = false;
		int timeEarliestDone = Integer.MAX_VALUE; //This is initialized at max value so it isn't selected until after Tellers start processing
		while(entries.isEmpty() || tellersFinished != true){
			clock = Math.min(timeEarliestDone, nextArrival);
			System.out.println(clock);
			addNow();
			for(Teller t : tellers){
				t.check(clock);
			}
			for(Teller t : tellers){
				if(t.isEmpty() && t.isIdle()){
					tellersFinished = true;
				}
				else{
					tellersFinished = false;
					break;
				}
			}
			for(Teller t : tellers){
				timeEarliestDone = Math.min(timeEarliestDone, t.getProcessTime());
				System.out.println(t);
			}
		clock++;
		}


		int totalWaitTime = 0;
		for(Teller t : tellers){
			totalWaitTime += t.getWaitTime();
		}
		double averageWaitTime = ((double)totalWaitTime)/clock;
		System.out.println("Bank finished processing " + totalCustomers + " customers at time " + clock + ".");
		System.out.println("Average wait time of a generic customer: " + averageWaitTime); //DECI FORMAT
		for(Teller t : tellers){
			int i = 1;
			int numProcessed = t.getNumProcessed();
			double percentIdle = ((double)t.getIdleTime())/clock;
			System.out.println("Teller " + i + " processed " + numProcessed + " customers and was idle " + percentIdle + "% of the time.");
		}

	}

	private void addNow(){
		Customer next = entries.peek();
		nextArrival = next.getArrival();

		if(clock == nextArrival){
			next = entries.poll();
			Teller chosen = null; //the teller the next customer will choose based upon whether they're in the drivethru or not

			if(next.getDriveStatus()){
				//CHECK THE TIMES EACH TELLER WILL BE DONE, PICK ONE WITH LOWEST TIME
				int earliest = Integer.MAX_VALUE;
				for(Teller t : tellers){
					int tellerTime = t.getProcessTime();
					earliest = Math.min(earliest, tellerTime);
					if(earliest == tellerTime){ //If the current teller has had earliest time to finish so far
						chosen = t; // the teller that will be chosen is this one (for now!)
					}
				}
			}
			else{
				//CHECK THE LENGTH OF EACH TELLER'S LINE, ADD TO SHORTEST
				int shortest = Integer.MAX_VALUE;
				for(Teller t : tellers){
					int tellerSize = t.size();
					shortest = Math.min(shortest, tellerSize);
					if(shortest == tellerSize){ //If the current teller has had the shortest lines so far
						chosen = t; //the teller that will be chosen is this one (for now!)
					}
				}
			}
			chosen.add(next);
		}
		else{
			return;
		}
	}
	/**
		Reduces time transactions take by a percentage

		@param percentIncrease The percent increase in efficiency (or, the percent decrease in transaction time)
	*/
	public void makeEfficient(double percentIncrease){
    	double multi = 1 - percentIncrease;
    	for(Customer c : entries){
    		c = entries.poll();
    		int newTime = (int)((c.getTransactionTime())*multi);
    		c.setTransactionTime(newTime);
    		entries.offer(c);
    	}
    }

/**
	Removes drive thru variable from consideration in data, forcing all clients to use the main tellers.
*/

	public void removeDriveThru(){
		for(Customer c : entries){
			c = entries.poll();
			c.setDriveStatus(false);
		}
	}
}
