package cpuScheduler;


import java.util.PriorityQueue;


/**
 * This class is responsible for distributing the processes into the job queue and 
 * observes the behaviour of a process. This implementation of the MLFQ uses
 * process aging and prempts a process  when it has used up its time slice. 
 * 
 * My overall goal for this simulation is to behave as close or exactly as a 
 * MLFQ in a system would act like. 
 */


public class MLFQ {
	
	// ReadyQueues for MLFQ
	 ReadyQueue queue1 = new ReadyQueue(); // Lowest Priority 
	 ReadyQueue queue2 = new ReadyQueue();// Middle priority  RR
	 ReadyQueue queue3 = new ReadyQueue(); // Highest priority RR
	
	// Job queue for CSV input which is sorted on the basis of arrival time 
	PriorityQueue<Process>jobQueue = new PriorityQueue<Process>(Process.OrderByArrival);

	// Current time in the system.
	double currentTime = 0;
	
	// Time Slice for the first queue in MLFQ 
	private double quantumTimeQ1 = 0; 
	
	// Global burst time in the system 
	private double globalBurst; 
	
	// Overall wait time 
	private double overallWait; 
	
	
	public  MLFQ () {
		
	}
	

	/**
	 * Starts the MLFQ scheduler and the simulation 
	 * @param queue
	 */
	
	public void runScheduler(PriorityQueue<Process> queue) {
		jobQueue = queue; 
		int size = jobQueue.size();
		assign2Queue();
		System.out.println("Queue size 1 (Lowest Priority): " + queue1.getSize());
		System.out.println("Queue size 2 (Middle Priority): " + queue2.getSize());
		System.out.println("Queue size 3 (Highest Priority): " + queue3.getSize());

		System.out.println(" ");
		System.out.println("future processes: " + jobQueue.size());
		System.out.println(" ");
		
		executeRR2();
		
		System.out.println("Current time in system " + currentTime);
		System.out.println("");
		System.out.println("Average Wait Time: " + overallWait / size );

	}
	
	
	
	/**
	 * Assigns processes from the job queue to the ready queues. 
	 * Jobs are assigned to each queue through the priority they are
	 * Jobs which have an arrival time higher than the current time will not be added
	 * 
	 */
	public void assign2Queue() {
		int loopTime = jobQueue.size();
		for(int i = 0; i < loopTime; i++) {
			if(!jobQueue.peek().hasFinished() && !jobQueue.peek().ifInQueue() && jobQueue.peek().getArrival() <= currentTime )
			{
				if(jobQueue.peek().getPriority() == 1) 
				{
					jobQueue.peek().inQueue(true);
					queue1.addProcess(jobQueue.poll());

				}
				else if (jobQueue.peek().getPriority() == 2) {
					jobQueue.peek().inQueue(true);
					queue2.addProcess(jobQueue.poll());
				}
				else {
					jobQueue.peek().inQueue(true);
					queue3.addProcess(jobQueue.poll());

				}
			}
		}
	
	}
	


	/**
	 *  Updates the queues to see if a process is ready to be deployed
	 *  by checking if the process has finished and if the process has an arrival time which is less than the current Time
	 *  This queue is specifically used 
	 */
	public void processArrived() {
		
		Process temp = new Process();
		
		for(int i = 0; i < jobQueue.size(); i++) {
			if(!jobQueue.peek().hasFinished() && !jobQueue.peek().ifInQueue()  && jobQueue.peek().getArrival() <= currentTime) {
				
				if(jobQueue.peek().getPriority() == 3) {
					temp = jobQueue.poll();
					queue3.addProcess(temp);	
					
				} else if(jobQueue.peek().getPriority() == 2) {
					temp = jobQueue.poll();
					queue2.addProcess(temp);
					
				} else if (jobQueue.peek().getPriority() == 1) {
					temp = jobQueue.poll();
					queue1.addProcess(temp);
				}
			}
			
			
		}
		
	}
	
	

	
	/**
	 * This function simulates the highest priority for the queue.
	 * This class is tasked with dealing with short burst CPU processes.
	 * @return True if queue 3 is empty
	 * @return False if there are still processes which need to be executed
	 */
		
	public boolean processQ3() {
		
		
		if(queue3.getSize() == 0) {
			return true; 
		}
		
		while(queue3.getSize() != 0) {
		
			// Time Quantum for process
		double qTime = quantumTimeQ1;
		
		
		System.out.println("//////////////////////////");
		System.out.println("Queue Time 3 " + qTime );
		System.out.println("///////////////////////// ");	
		System.out.println(" ");
		
		//Applying round robin algorithm to process
		RoundRobbin2 RR = new RoundRobbin2(queue3.getProcess(), qTime ,currentTime); 
		
		//Temp process to retrieve updated process from RoundRobin class
		Process temp = new Process ();
	
		
		boolean finished = RR.executeRoundRobbin();
		
		
			if( finished == true) {
					
				temp = RR.getProcess();
				 
				currentTime = RR.getSystemTime();
				
				printProcess(temp);
				
				queue3.deleteProcess();
				
				overallWait += temp.getWaitTime();
					
			} else if(finished == false) {
			
				queue3.deleteProcess();
				
				//Old aging here
		
				 // Fetching updated process from the RR queue
				 temp = RR.getProcess();
				 //Updating the current time
				 currentTime = RR.getSystemTime(); 
				 // Setting priroty 
				 temp.setPriority(2);
				 // Adding context switch
				 temp.addContextSwitch();
				 
				 // Prempts process to the next queue
				 assignQueue(temp);
				 
				 System.out.println("////////////////////////////////////////////////////////////////");
				 System.out.println(" ");
				 System.out.println("Process ID: " + temp.getID() + " has prempted into Queue 2");
				 System.out.println("Process remaining time: " + (temp.getBurst() - qTime) + "has prempted into Queue 2");

				 System.out.println(" ");
				 System.out.println("////////////////////////////////////////////////////////////////");	
			}	
			// Checks to see if there is a new process ready to arrive in one of the queues
			 processArrived();
			
		}
					
			return false; 
	}


	
	/**
	 * This function simulates the Middle priority for the queue.
	 * This class is tasked with dealing with short burst CPU processes.
	 * @return True if queue 2 is empty
	 * @return False if there are still processes which need to be executed
	 */
	
	
	
	public boolean processQ2() {	
		
		if(queue2.getSize() == 0)
		{
			return true; 
		}
		
		
		while(queue2.getSize() != 0) {
			
		
			// Leaves queue if there is a process in 3rd queuue (Higher priority)
			if(queue3.getSize() != 0) {
				return false; 
			}
			
		
		
		// Time Quantum for process
		double qTime = queue2.getTotalBurst();
		qTime /= queue2.getSize();
		qTime = Math.round(qTime*100)/100;
		
		System.out.println("///////////////////////// ");
		System.out.println("Queue Time 2 " + qTime );
		System.out.println("///////////////////////// ");
		System.out.println(" ");
		
		//Applying round robin algorithm to process in queue 
		RoundRobbin2 RR = new RoundRobbin2(queue2.getProcess(), qTime,currentTime); 
		System.out.println("Quantum Age Time " + queue2.getProcess().getQuantumAge());
		
		
		// Changes priority of the queue 
		boolean hasAged =  queue2.aging(qTime);

		//Temp process used to premept into the next queue
		Process temp = new Process ();

		
		
		boolean finished = RR.executeRoundRobbin();
	
			if( finished == true) {		
				double oldCT = currentTime; 
				currentTime = RR.getSystemTime();
				
				//Checks to see if a process is set to arrive in the higher queue in between the current process's CPU runtime.
				//If true then it halts the curerent process's runtime and switches over to the process in the higher queue. 
				// Process remains in the same queue. 
				
				if(queue3.getSize() != 0) {
				if(currentTime >= queue3.getProcess().getArrival() ) {
					double a = currentTime - queue3.getProcess().getArrival();
					currentTime -= a; 
					double runTime = queue2.getProcess().getBurst() - a; 
					queue1.getProcess().setRuntime(runTime);
					queue1.getProcess().setWaitTime(queue2.getProcess().getWaitTime() - oldCT + runTime);
	    	     
					System.out.println("Process " + queue2.getProcess().getID() + " Has been paused as new process has entered queue 3a");
					System.out.println("Time in System " + currentTime);
					
				}
				
			}
		
				// If not then the process has completed and will have its details printed out. 
				else {	
					temp = RR.getProcess();
					 
					currentTime = RR.getSystemTime();
					
					printProcess(temp);
					
					
					queue2.deleteProcess();
					overallWait += temp.getWaitTime();
					

				}
				
			} else if(finished == false) {
				
				//Checks to see if a process is set to arrive in the higher queue in between the current process's CPU runtime.
				//If true then it halts the curerent process's runtime and switches over to the process in the higher queue. 
				// Process remains in the same queue. 
				
				
				 double oldCT = currentTime; 
				 currentTime = RR.getSystemTime(); 
				 

				 
				if(queue3.getSize() != 0) {
					if(currentTime >= queue3.getProcess().getArrival() ) {
						double a = currentTime - queue3.getProcess().getArrival();
						currentTime -= a; 
						double runTime = queue2.getProcess().getBurst() - a; 
						queue1.getProcess().setRuntime(runTime);
						queue1.getProcess().setWaitTime(queue2.getProcess().getWaitTime() - oldCT + runTime);
		    	     
						System.out.println("Process " + queue2.getProcess().getID() + " Has been paused as new process has entered queue 3");
						System.out.println("Time in System " + currentTime);
						
				 	}
		 		 	
		  	   	 }
				queue2.deleteProcess();	 
				 temp = RR.getProcess();	 // Fetching updated process from the RR queue		
				 
				 if(hasAged != true) // If the process has not aged it will preempt 
				 { 
				 temp.setPriority(1);
				 }		 
				 temp.addContextSwitch();
				 //Checking to see if any processes have aged 
				 queue2.aging(qTime);
				 queue3.aging(qTime);
				 assignQueue(temp);
				 
				 
				 	System.out.println("////////////////////////////////////////////////////////////////");
				 	System.out.println(" ");
				 	System.out.println("Process ID: " + temp.getID() + " has prempted into Queue 1");
				 	System.out.println("Process Remaining time: " + (temp.getBurst() - qTime) + "has prempted into Queue 1");

				    System.out.println(" ");
					System.out.println("////////////////////////////////////////////////////////////////");

			}
			// Checks to see if there is a new process ready to arrive in one of the queues 
			processArrived();
			
		}		
		return false;	
	}
	
	
	
	/**
	 * This function simulates the Middle priority for the queue.
	 * This class is tasked with dealing with short burst CPU processes.
	 * @return True if queue 2 is empty
	 * @return False if there are still processes which need to be executed
	 */
	
	public boolean processQ1() {
		
		
		if(queue1.getSize() == 0) {
		return true; 
		}
		
		while(queue1.getSize() != 0) {
			
			
			// Breaks out of 
			if(queue3.getSize() != 0) {
				return false;
			}
		if(queue2.getSize() != 0) {
			return false;
		}
		
		
			
		double qTime = queue1.getTotalBurst();
		qTime /= queue1.getSize();

		qTime = Math.round(qTime*100)/100;
		
		queue2.aging(qTime);
		System.out.println("//////////////////////////");
		System.out.println("Queue Time 1 " + qTime );
		System.out.println("///////////////////////// ");

		//Applying round robin algorithm to processes in queue 
		RoundRobbin2 RR = new RoundRobbin2(queue1.getProcess(), qTime, currentTime); 
		
		
		Process temp = new Process(); 
		
			if(RR.executeRoundRobbin() == true) {
				double oldCT = currentTime;
				currentTime = RR.getSystemTime();
				// Updating to see if there are new processes available to enter the queues above 
				processArrived();
				 
				

				//Checks to see if a process is set to arrive in the higher queue in between the current process's CPU runtime.
				//If true then it halts the curerent process's runtime and switches over to the process in the higher queue. 
				// Process remains in the same queue. 
				
				if(queue2.getSize() != 0) {
				if(currentTime >= queue2.getProcess().getArrival() ) {
					double a = currentTime - queue2.getProcess().getArrival();
					currentTime -= a; 
					double runTime = queue1.getProcess().getBurst() - a; 
					queue1.getProcess().setRuntime(runTime);
					queue1.getProcess().setWaitTime(queue1.getProcess().getWaitTime() - oldCT + runTime);
	    	     
					System.out.println("Process " + queue1.getProcess().getID() + " Has been paused as new process has entered queue 2");
					System.out.println("Time in System " + currentTime);
					
				}
				
			}

				//Checks to see if a process is set to arrive in the higher queue in between the current process's CPU runtime.
				//If true then it halts the curerent process's runtime and switches over to the process in the higher queue. 
				// Process remains in the same queue. 
			else if(queue3.getSize() != 0) {
				if(currentTime >= queue3.getProcess().getArrival() ) {
					double a = currentTime - queue3.getProcess().getArrival();
					currentTime -= a; 
					double runTime = queue1.getProcess().getBurst() - a; 
					queue1.getProcess().setRuntime(runTime);
					queue1.getProcess().setWaitTime(queue1.getProcess().getWaitTime() - oldCT + runTime);
	    	     
					System.out.println("Process " + queue1.getProcess().getID() + " Has been paused as new process has entered queue 3");
					System.out.println("Time in System " + currentTime);
					System.out.println(" ");
					
				}
			}
				else {	
					
					
					temp = RR.getProcess();
					 
					currentTime = RR.getSystemTime();
					
					printProcess(temp);
					
					overallWait += temp.getWaitTime();

					queue1.deleteProcess();
					
					 
			}
				
			} else if(RR.executeRoundRobbin() == false) {
				
				System.out.println("Processb 1 runtime" + queue1.getProcess().getWaitTime());
				

				//Checks to see if a process is set to arrive in the higher queue in between the current process's CPU runtime.
				//If true then it halts the curerent process's runtime and switches over to the process in the higher queue. 
				// Process remains in the same queue. 
				
				if(queue2.getSize() != 0) {
					if(currentTime >= queue2.getProcess().getArrival() ) {
						double a = currentTime - queue2.getProcess().getArrival();
						currentTime -= a; 
						double runTime = queue1.getProcess().getBurst() - a; 
						queue1.getProcess().setRuntime(runTime);
						queue1.getProcess().setWaitTime(queue1.getProcess().getWaitTime() - currentTime + runTime);
		    	     
						System.out.println("Process " + queue1.getProcess().getID() + " Has been paused as new process has entered queue 2");
						System.out.println("Time in System " + currentTime);
						System.out.println(" ");
						
					}
					
				}

				//Checks to see if a process is set to arrive in the higher queue in between the current process's CPU runtime.
				//If true then it halts the curerent process's runtime and switches over to the process in the higher queue. 
				// Process remains in the same queue. 
				else if(queue3.getSize() != 0) {
					if(currentTime >= queue3.getProcess().getArrival() ) {
						double a = currentTime - queue3.getProcess().getArrival();
						currentTime -= a; 
						double runTime = queue1.getProcess().getBurst() - a; 
						queue1.getProcess().setRuntime(runTime);
						queue1.getProcess().setWaitTime(queue1.getProcess().getWaitTime() - currentTime + runTime);
		    	     
						System.out.println("Process " + queue1.getProcess().getID() + " Has been paused as new process has entered queue 3");
						System.out.println("Time in System " + currentTime);
						System.out.println(" ");
						
					}
				}
					else {	
				temp = RR.getProcess();
				assignQueue(temp);
				System.out.println("////////////////////////////////////////////////////////////////");
				 System.out.println("Process ID: " + queue1.getProcess().getID() + "has not completed");
				 System.out.println("currentTime" + currentTime);
				 queue1.deleteProcess();
				 
				 if(queue1.getSize() != 0) {
				 queue1.aging(qTime);
				 }
				
					System.out.println("////////////////////////////////////////////////////////////////");
					}
				
				
				 
			}
			// Checks to see if there is a new process ready to arrive in one of the queues 
			processArrived();
			
		}	
	 	
		return false; 
	}
	
	

	
/**
 * Assigns processes in each queue a quantum age that is a timer for when a process
 * requires aging to occur
 * 
 */

public void setQuantumAge() {

	
	double ageTime1  = globalBurst  /10; 
	ageTime1 *= 1.4; 
	queue1.setQuantumAge(ageTime1);
	
	
	double ageTime2 = globalBurst  /10;
	ageTime2 *= 1.4; 
	queue2.setQuantumAge(ageTime2);

	
	double ageTime3  = globalBurst  /10; 
	ageTime3 *= 1.4; 
	queue3.setQuantumAge(ageTime3);
}



	// Process queue 1, simulates the lowest priority queue 
	// Uses round robin scheduling 

	
	
	
	/**
	 * Calculates the total burst of each queue 
	 * @return the total burst time in system
	 */
	public double  calculateGlobalBurst() {
	 double totalBurst = queue1.getTotalBurst() + queue2.getTotalBurst() + queue3.getTotalBurst();
	 return totalBurst; 
	}
	
	
	/**
	 * Sends processes to the queue after premption or after being aged, 
	 * @param process
	 */
	public void assignQueue(Process process) {
		 
		 if(process.getPriority() == 1) {
			 process.addContextSwitch();
			 queue1.addProcess(process);
			 
		 } else if ( process.getPriority() == 2) {
			 process.addContextSwitch();
			 queue2.addProcess(process);
				 
		  }else if (process.getPriority() == 3) {
			  	 process.addContextSwitch();
				 queue3.addProcess(process);
			 }
	}
	

	
	
	/**
	 * Nested while loop which simulates the MLFQ hierachy structure.
	 * Breaks out of queue once processes have been emptied out of
	 * all queues
	 */
	
	
	public void executeRR2() {
		
		globalBurst = calculateGlobalBurst();
		
		
		
		double rqTotalBurst = 0; 
		double count = 0; 
		for(Process process : jobQueue) {
			if(process.getPriority() == 3 ) {
				rqTotalBurst += process.getBurst();
				System.out.println(process.getBurst());
				count++; 
			}
		}
		
		quantumTimeQ1 = (rqTotalBurst + queue3.getTotalBurst()) / (queue3.getSize() + count); 
	

		while(true) {
			
			
			// While loop that represents highest priority queue
			while (true ) {
	
				
				
				boolean hasFinished = processQ3();
					if(hasFinished == true) {
						break; 
					}
					
					if(hasFinished == false) {
						break; 
					}
				
			}
			
			// Middle queue
			while (true) {
				setQuantumAge();
				
				boolean hasFinished = processQ2();
				if(hasFinished == true) {
					break; 
				}
				
				if(hasFinished == false) {
					break; 
				}
		
				
			}
			// lowest queue 
			while (true ) {
				
				boolean hasFinished = processQ1();
				if(hasFinished == true) {
					break; 
				}

				if(hasFinished == false) {
					break; 
				}
				
			}	
			if(jobQueue.size() == 0 && queue1.getSize() == 0 && queue2.getSize() == 0 && queue3.getSize() == 0) {
			break;
			
			// For specific conditions where the current time is less than the arrival time of the next process coming into the system 
		} 		
	}			
}

	public void processBlock() {
	 if(jobQueue.size() != 0) {
			if(currentTime < jobQueue.peek().getArrival()) {
			double time = jobQueue.peek().getArrival() - currentTime; 
			currentTime = currentTime + time; 
			processArrived();
			System.out.println("");
			}
		}
	}
		
		
	
	public void printProcess(Process process) {
		double TaT = currentTime - process.getArrival(); 
		
		System.out.println("////////////////////////////////////////////////////////////////");
		
		System.out.println("Process has finished ");
		
		
		System.out.println("Process ID  " + process.getID());
		System.out.println("Process Arrival " +  process.getArrival());
		System.out.println("Process Burst " + process.getBurst());
		System.out.println("Process Priority " + process.getPriority());
		System.out.println("Turn around time " + TaT);
		System.out.println("Process runTime " + process.getRuntime());
		System.out.println("Process waitTime " + process.getWaitTime());
		System.out.println("Process Finish Time " + currentTime);
		System.out.println("Process Context Switches " + process.getContextSwitches());
		System.out.println("JobQueue size " + jobQueue.size());
		
		System.out.println(" ");
	}
		
}
	
	

	

		






	


	
	
	
	
	
	
	
	
	
	
	
	
	


	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	


	


//Need to inplement SJF and also introduce input for the program
//Another problem is that the program may need to be changed depending on feedback obtained. 






