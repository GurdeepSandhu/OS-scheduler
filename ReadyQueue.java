package cpuScheduler;

import java.util.PriorityQueue;

public class ReadyQueue implements Queues {
		
	/**
	 * Custom data structure which is based off a priority queue.  
	 * Uses mostly the same functions but has added functionality 
	 */
	
	PriorityQueue<Process> queue = new PriorityQueue<Process> (Process.OrderByArrival);

	public ReadyQueue() {
	
	}
		
	
	/**
	 * Simple methods of adding, returning, checking the size. checking if the queue is empty
	 * returning the process and clearing the queue. 
	 */
	public void addProcess(Process process ) {
	
		queue.add(process);
	}
	
	public void deleteProcess() {
		if (getSize() > 0) {
			queue.poll();
		}
	}
	

	public boolean isEmpty() {
		if(getSize() == 0) {
			return true; 
		}
		else {
			return false; 
		}
	}

	public Process getProcess() {
		return queue.peek(); 
	
	}
	
	
	// Returns the total burst for queue
	public double getTotalBurst() {
		double totalBurst = 0;
        for (Process process : queue) {
            totalBurst += process.getBurst();
        }
        return totalBurst; 
	}

	
	public void clearQueue() {
		queue.clear();
		
	}
	
	public double getSize() {
		return queue.size(); 
	}
	
	
	/**
	 * This function deducts the time away from the assigned Quantum Age 
	 * timer
	 * @param TS : The Time that has passed during execution of processes 
	 */
	public boolean aging(double ts) {
		
		for(Process process: queue) {
			// Making it so only processes which have been preempted can 
			// be aged 
			if(process.getContextSwitches() >= 1) {
			if ( process.getQuantumAge() <= ts) {
				
				if(process.getPriority() != 3) {
					process.setPriority(process.getPriority() +1);
					System.out.println("////////////////////////////////////////");
					System.out.println("Process has been aged " + process.getID());
					System.out.println("////////////////////////////////////////");
					System.out.println(" ");
					return true; 
			}
				
			} else if (process.getQuantumAge() > ts) {
				double newQA = process.getQuantumAge() - ts; 
				process.setQuantumAge(newQA);
				return false; 
			}
		}
	}
		return false; 
}
	
	
	
	
	// Assign the timer to each process of how much time it requires before
	// Being having its priority increased
	// @param time : The time assigned for the quantum age. 
	public void setQuantumAge(double time) {
		for(Process process: queue) {
			process.setQuantumAge(time);
			
		}
	}



	

}
