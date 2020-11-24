package cpuScheduler;

import java.util.Comparator;

public class Process {

	
	private int ID;
	private double at; // Arrival Time
	private double bt; // Burst Time
	private int priority; // Priority 
	private double wt; // Wait Time
	private double RT;  // Remaining time 
	private double TaT; // Turn Around Time 
	private boolean isFinished; // If process has finished
	private int contextSwitches; // How many context switches process has had 
	private boolean inQueue; // If the process has entered the queue
	private double quantumAge; // Timer for when a process should have its priority increased 
	
	
	public Process (int ID, double a, double b, int priority) {
		this.at  = a; 
		this.bt = b;
		this.ID = ID; 
		RT = 0; 
		this.priority = priority; 
		isFinished = false; 
		inQueue = false; 
	}

	
	// Simple setter getter methods
	
	public Process() {
		
	}

	
	public void setQuantumAge(double QA) {
		this.quantumAge = QA;
	}
	
	public double getQuantumAge() {
		return quantumAge; 
		
	}
	
	public void changeQuantumAge(double time ) {
		this.quantumAge = this.quantumAge - time; 
	}
	

	public int getID() {
		return ID; 
	}
	
	public double getRuntime() {
		return RT;
	}
	
	public void setRuntime(double RT) {
		this.RT = RT; 
	}
	
	public double getBurst() {
		return bt;
	}
	
	public void setFinished(boolean isFinished) {
	
		this.isFinished = isFinished; 	
	}
	
	public double getArrival() {
		return at; 

	}
	
	public int getPriority() {
		return priority; 
	}
	
	public void setPriority (int Priority) {
		this.priority = Priority; 
	}
	
	public void setBurst(double newPT) {
		this.bt = newPT; 
	}
	
	public void addRT(double time) {
		this.RT += time;  
	}
	

	public void addContextSwitch() {
		this.contextSwitches += 1; 
	}
	
	public int getContextSwitches() {
		return contextSwitches; 
	}
	
	
	public boolean hasFinished() {
		return isFinished;
	}
	
	public void setWaitTime(double wt) {
		this.wt = wt; 
	}
	
	public double getWaitTime () {
		return wt; 
	}
	
	public void setTaT(double TaT) {
		this.TaT = TaT; 
	}
	
	public double getTaT() {
		return TaT; 
	}
	
	public void inQueue(boolean inQueue) {
		this.inQueue = inQueue; 
	}
	public boolean ifInQueue() {
		
		return inQueue;
	}

	
	//Orders each process by the basis of Arrival time 
	
	static final Comparator<Process> OrderByArrival = new Comparator<Process>() {
		public int compare(Process a, Process b) 
		{
			if(a.at < b.at ) return -1;
			if (a.at > b.at) return 1; 
			return 0; 
		}
	};
	
	//Orders each process by the basis of priority  
	
	static final Comparator<Process> OrderByPriority = new Comparator<Process>() {
		public int compare(Process a, Process b) 
		{
			
			if(a.priority < b.priority) return -1;
			if (a.priority > b.priority) return 1; 
			
			double c = OrderByArrival.compare(a, b);
			if (c > 0) return 1; 
			if (c < 0) return -1; 
			return 0; 
		}
	};
	
	
	//Orders each process by the basis of the burst time 
	
	static final Comparator<Process> OrderByBurstTime = new Comparator<Process>() {
		public int compare(Process a, Process b) 
		{
			
			if(a.bt < b.bt) return -1;
			if (a.bt > b.bt) return 1; 
			
			double c = OrderByArrival.compare(a, b);
			if (c > 0) return 1; 
			if (c < 0) return -1; 
			return 0; 
		}
	};
	
}






