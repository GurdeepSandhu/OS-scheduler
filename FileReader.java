package cpuScheduler;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class FileReader {
	
	ArrayList<Process> queue = new ArrayList<Process>();
	private String fileName = "";
	public FileReader(String fileName) {
		this.fileName = fileName;

	}
	

	public ArrayList<Process> readFile() throws FileNotFoundException {
		//Reading the file
		Scanner scanner = new Scanner(new File(fileName));
		//Getting the next line
		
		// Checks to see if there is another line within the file it is reading 
		while(scanner.hasNextLine() ) {
		String nextLine = scanner.nextLine();
		// Splitting the values to an array
		String[] dataValue = nextLine.split(",");
		
		// Assigning each value to a string and then parsing it as doubles / ints
		String ID = dataValue[0];
		String AT = dataValue[1];
		String BT = dataValue[2];
		String Priority = dataValue[3];
		
		
		int ID1 = Integer.parseInt(ID); 
		double AT1 = Double.parseDouble(AT); 
		double BT1 = Double.parseDouble(BT); 
		int Priority1 = Integer.parseInt(Priority); 

		
		// Creating a new object using parameters 
		Process process = new Process(ID1, AT1, BT1, Priority1);
		queue.add(process);
		
		}
		scanner.close();

		
		return queue; 
		
	}


}