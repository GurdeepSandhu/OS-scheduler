package cpuScheduler;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Scanner;

public class Main {

	 
	
	
	public static void main(String args[]) throws FileNotFoundException {
		
		System.out.println("Insert file path");
		//Using scanner to retrieve file input 
		Scanner scanner = new Scanner(System.in);
		String getFile = scanner.nextLine();
		
		//Usin file reader to retrieve the file 
		FileReader reader = new FileReader(getFile);
		ArrayList<Process >list = new ArrayList<Process>();
		PriorityQueue<Process>jobQueue = new PriorityQueue<Process>(Process.OrderByArrival);

		list = reader.readFile();

		
		//Transfering objects from the Array list to the PriorityQueue 
		for(int i = 0; i < list.size(); i++) {
			if(list.size() == 0) {
				break;
			}
			Process temp = new Process();
			
			temp = list.get(i);
			
			jobQueue.add(temp);
		}
		MLFQ scheduler = new MLFQ();
		scheduler.runScheduler(jobQueue);
	}
	
}
