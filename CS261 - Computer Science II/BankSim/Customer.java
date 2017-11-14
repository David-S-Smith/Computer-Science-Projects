/**
	A customer object to be used in the bank simulation. Contains information on
	customer arrival time, transaction time, and whether or not they are using the
	driveThru if one is available.

	Contains a variety of "getter" and "setter" methods to assist in use.

	@author David S Smith, Max Haggard, and Miles Cameron
	@version 10/27/2016
*/

public class Customer implements Comparable<Customer>{

	private int arrival, transactionTime;
	private boolean driveThru;
	private String driveStatus;

	public Customer(int arrivalTime, int transactionTime, boolean driveThru){

		arrival = arrivalTime;
		this.transactionTime = transactionTime;
		this.driveThru = !driveThru;
		//Brad's code inputs this value to be a query as to if the customer was coming into the bank or not. We found it
		//more intuitive to treat it as though it was a query as to whether they were at the drive thru or not.
		driveStatus = "";

	}

	public int getArrival(){
		return arrival;
	}

	public void setTransactionTime(int newTime){
		transactionTime = newTime;
	}

	public int getTransactionTime(){
		return transactionTime;
	}

	public boolean getDriveStatus(){
		return driveThru;
	}

	public void setDriveStatus(boolean driveThru){
		this.driveThru = driveThru;
	}

	public String toString(){
		if(driveThru){
		driveStatus = "the drivethru line";
		}
		else{
		driveStatus = "the door";
		}
		return "Customer Arrived at " + driveStatus + " at step " + arrival + ", with transaction taking " + transactionTime + " steps.";
	}

	/**
		Compares two customers. Prioritizes whoever is at the drivethru, or simply the earlier arrival time.
	*/
	public int compareTo(Customer other){
		if(this.driveThru == true && other.driveThru == false){
			return -1;
		}
		else if(this.driveThru == false && other.driveThru == true){
			return 1;
		}
		else{
			return this.arrival - other.arrival;
		}
	}

}
