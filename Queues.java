package cpuScheduler;



public interface  Queues {


	
	
	public  void addProcess(Process process);
	

	public  void deleteProcess();
	
	public  double getSize();
	
	public  boolean isEmpty();
	
	public  Process getProcess();
	
	public double getTotalBurst();
	
	public void clearQueue();
}
