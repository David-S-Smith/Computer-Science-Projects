import java.util.PriorityQueue;

/**
	A Teller and their line, used for processing customers in the bank sim.
*/

public class Teller {

	private int idleTime, waitTime, numProcessed, size, processTime;
	private PriorityQueue<Customer> line;
	private boolean idleStatus, isEmpty;
	private Customer current;

	/**
		Creates a Teller with no customers, that has not been idle and has not processed any customers thusfar.
	*/
	public Teller(){
		idleTime = 0;
		waitTime = 0;
		numProcessed = 0;
		size = 0;
		idleStatus = true;
		isEmpty = true;
		line = new PriorityQueue<Customer>();
		current = null;
		processTime = Integer.MAX_VALUE; //Teller is not processing a Customer, and therefore will not be done sooner than anyone.
	}

	public void add(Customer c){
		line.add(c);
	}

	public boolean isIdle(){
		return idleStatus;
	}

	public boolean isEmpty(){
		return isEmpty;
	}

	public int getNumProcessed(){
		return numProcessed;
	}

	public int getWaitTime(){
		return waitTime;
	}

	public int getIdleTime(){
		return idleTime;
	}

	public int size(){
		return size;
	}
	public int getProcessTime(){
		return processTime;
	}

	public void check(int time){

		if(idleStatus){
			if(line.size() == 0){
				isEmpty = true;
				idleTime++;
				return;
			}
			else{
				current = line.poll();
				waitTime += time - current.getArrival();
				processTime = time + current.getTransactionTime();
				idleStatus = false;
				return;
			}
		}
		else{
			if(time == processTime){
				System.out.println("Teller has processed customer at " + time);
				idleStatus = true;
				numProcessed++;
				processTime = Integer.MAX_VALUE;
				return;
			}
			else{
				return;
			}
		}
	}

	public String toString(){
		return line.toString();
	}


}
