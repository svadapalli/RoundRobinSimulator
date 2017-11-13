import java.util.*;
import java.io.*;
import java.text.DecimalFormat;
public class rrsimulator {
	
/* Global variables to hold arrival times, amount of CPU time process needs to complete and
	time quantum */
	public static int[] arrival = new int[22];
	public static int[] burst = new int[22];
	public static int TimeQuantum = -1;
	
	public static void main(String[] args) {
		
		//Take user input for the Time Quantum
		while(TimeQuantum < 0) {
			Scanner scanner = new Scanner(System.in);
			System.out.println("Please enter the Time Quantum: ");
			TimeQuantum = scanner.nextInt();
			scanner.close();
		}
	
		//store over head values
		int[] overHead = {0,5,10,15,20,25};
		
		//Reading data from the file
		try {
			readFile();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		// Run the RR simulator for each over head value
		for (int i = 0; i < 6; i++) {
			simulator(overHead[i]);
		}
		
		return;
}
	
//reading file for arrival time and burst time
	public static void readFile() throws FileNotFoundException {
		Scanner s = new Scanner(new File("input.txt"));
		int i =0;
		while(s.hasNextLine()) {

		    String line[] = s.nextLine().split("\\s");
		    double bur = (Double.parseDouble(line[1])*1000);
		    int burs = (int)bur;
		    int arr = (Integer.parseInt(line[0])*1000);
		    arrival[i] = arr;
		    burst[i] = burs;
		    i++;

		}

		s.close();
	}
	
//Process scheduler method
	public static int scheduler(int timer, Deque<Process>Ready, Deque<Process>Initial) {
		int n = Initial.size();
		
		//If Initial Q is not empty, move the first process to ready Q.
		if (!Initial.isEmpty()) {
			
			Process next = Initial.getFirst();
			int x = next.getArrivalTime();
			
			//Check the arrival time before moving to ready Q
			if (x <= timer) {
				Ready.addLast(next);
				Initial.removeFirst();
			}
			
			//Check to see if process is successfully removed from Initial Q
			if (n > Initial.size()) {
				return 1;
			}
		}
		return 0;
	}
	
// Method to calculate and return the Average length of the queue
	public static double AvgQLength(ArrayList<Integer> QLength) {
		double average;
		int total = 0;
		int n = QLength.size();
		
		for (int i : QLength) {
			total = total + i;
		}
		
		average = (double)total/(double)n;
		return average;
	}
	
// Method to calculate the max length of the Queue
	public static int MaxQLength(ArrayList<Integer> QLength) {
		
		int max = 0;
		for (int i : QLength) {
			if (i > max) {
				max = i;
			}
		}
		return max;
	}
	
// Method to calculate Average Process wait time	
	public static double AvgProcWaitTime(Deque<Process> Complete) {
		int totalWT = 0;
		double avg;
		int n = Complete.size();
		
		Iterator<Process> iterator = Complete.iterator();
        while (iterator.hasNext()) {
            totalWT = totalWT + (iterator.next().getWaitTime());
        }
        avg = (double)totalWT/n;
        return avg;
	}
	
// Method to calculate Average Turn Around time for all processes
	public static double AverageTAT(Deque<Process> Complete) {
		int totalTAT = 0;
		double avg;
		int n = Complete.size();
		
		Iterator<Process> iterator = Complete.iterator();
        while (iterator.hasNext()) {
            totalTAT = totalTAT + (iterator.next().getTurnAroundTime());
        }
        avg = (double)totalTAT/n;
        return avg;
	}
	
//The simulation kernel	
	public static void simulator(int oh) {
		int Processid, ET, var1, var2;
		int timer = 0;
		int Overhead = oh;
		
		/* Initialize 3 double ended queues to hold initial, ready and complete processes
			and an Array List to keep track of queue lengths at each point */
		Deque<Process> Initial = new LinkedList<Process>();
		Deque<Process> Ready = new LinkedList<Process>();
		Deque<Process> Complete = new LinkedList<Process>();
		ArrayList<Integer> QLength = new ArrayList<>();
		
		// Clear all queues before initialization
		Ready.clear();;
		QLength.clear();
		Complete.clear();
		
		// Initialize each process with all values and add to the initial Q
		for (int i = 0; i < 22; i++) {
			Processid = i;
        	Process proc = new Process(arrival[i], burst[i], TimeQuantum, Processid, 0, true);
        	Initial.add(proc);

		}
		
		// Run the simulator until there are processes remaining to be executed
		while (true) {
			//check if there are any processes in the ready Q
			var1 = scheduler(timer, Ready, Initial);
			
			// If not, increment timer
			if (var1 == 0 && Ready.size() == 0) {
				timer++;
			}
			
			// If ready Q has processes
			if (!Ready.isEmpty()) {
				// save the Q length
				QLength.add(Ready.size());
				
				// check if the process is accessing the CPU for the first time, set start time
				if ((Ready.getFirst().getStartTime() == 0) && (Ready.getFirst().getInit()== true)) {
					Ready.getFirst().setStartTime(timer);
				}
				
				//Execute the process
				var2 = Ready.getFirst().execute();
				
				//If burst time is greater than time quantum
				if (var2 == 0) {
					int n = Ready.size();
					timer = timer + TimeQuantum + Overhead;
					
					// check for new process, if waiting to enter the ready Q
					scheduler(timer, Ready, Initial);
					
					// Add overhead to the timer only if there are more than 1 processes in the ready Q
					if (n == 1 && Ready.size() > 1) {
						timer = timer + Overhead;
					}
					
					//Move the executed process to the back of ready Q
					Ready.addLast(Ready.getFirst());
					Ready.pop();
				}
				
				//ELse if burst time is less than time Quantum
				else {
					timer = timer + var2 + Overhead;
					ET = timer - Overhead;
					Ready.getFirst().setEndTime(ET);
					Complete.addLast(Ready.getFirst());
					Ready.pop();
					
					//If all processes finished execution
					if (Ready.isEmpty() && Initial.isEmpty()) {
						timer = timer - Overhead;
						break;
					}
				}
			}
		}
	    
		// Printing output for various performance measures to the console
		System.out.println("\n");
		System.out.println("Round Robin scheduling simulation output for Time Quantum = " +TimeQuantum+ " and Overhead = " +Overhead );
		System.out.println("--------------------------------------------------------------------------------");
	    
		DecimalFormat two = new DecimalFormat("#0.00");
		System.out.println("Total length of the simulation: " +two.format((double)timer/1000)+ " secs");
		double ATT = AverageTAT(Complete)/1000;
	    System.out.println("Average turnaround time: " +two.format(ATT)+ " secs");
	    double APWT = AvgProcWaitTime(Complete)/1000;
	    System.out.println("Average process wait time: " +two.format(APWT)+ " secs");
	    System.out.println("Average queue length: " +two.format(AvgQLength(QLength)));
	    System.out.println("Maximum queue length: " +MaxQLength(QLength));

	}

	
}
