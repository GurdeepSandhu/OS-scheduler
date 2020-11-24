
package cpuScheduler;
 
public class RoundRobbin2 {
 
    //ReadyQueue object 
    private double currentTime; 
 

     private double qTime; 
     private  Process process = new Process(); 
     
     
     
     
    public RoundRobbin2(Process process, double qTime, double currentTime) {
        this.process = process; 
        this.qTime = qTime;
        this.currentTime = currentTime;
    }
     
     
  /**
   *  Determines if a process has finished if the process 
   *  by seeing if the runtime is larger than the Burst time. 
   *   
   *   
   * @return True if process has finished
   * @return false if process has not finished 
   */
 public boolean executeRoundRobbin() {
    	process.addRT(qTime);
    	
  
    	currentTime += qTime;
    	
    	if(process.getRuntime() >= process.getBurst()) {
    		double a = process.getRuntime();
    		double b = process.getBurst();
    		

    		
    		double time = a - b; 
    		//Removes the extra time away from the overall time
    		currentTime -= time; 
    		 process.setFinished(true);
    	        // Setting runtime for process
    	        process.setRuntime(process.getBurst());
    	        // Setting waiting time for the process
    	        process.setWaitTime(process.getWaitTime() + currentTime - process.getBurst() - process.getArrival());
    	        //setting the turnaroundtime for process
    	        process.setTaT(process.getBurst() + process.getWaitTime() );
    	        
    		
    		return true; 
	     

    				
    	} else if( process.getRuntime() < process.getBurst()) {
    		process.setWaitTime(process.getWaitTime() + qTime);
    		return false;
    	}
    	return false; 
    	
    }
 

       // Returns the updated system time 
    public double getSystemTime () {
        return currentTime; 
    }

     // Returns the updated process
    public Process getProcess () {
        return process; 
    }
}