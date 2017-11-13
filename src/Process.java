public class Process {
	
	public int arrival, timeQuant, PID, TAT, end, wait, start, timer;
	public int burst;
	public boolean INIT;
	
	// constructor to initialize the values for the processes
	Process(int arrivalTime, int cpuBurst, int timeQuantum, int processId, int startTime, boolean init) {
		arrival = arrivalTime;
		burst = cpuBurst;
		timeQuant = timeQuantum;
        PID = processId;
        start = startTime;
        INIT = init;
	}
	
	// Checks if burst is greater than time quant
	public int execute() {
		if (burst > timeQuant) {
            burst = burst - timeQuant;
            return 0;
        }
		return burst;
	}
	
	//sets the init value to false after a process accesses the cpu for the first time
	public void setStartTime(int timer) {
		start = timer;
        INIT = false;
	}
	
	
	public void setEndTime(int timer) {
		end = timer;
	}
	
	public int getArrivalTime() {
		return arrival;
	}
	

	public int getStartTime() {
		return start;
	}
	
	
	public int getEndTime() {
		return end;
	}
	
	public int getWaitTime() {
		wait = start - arrival;
        return wait;
	}
	
	public int getTurnAroundTime() {
		TAT = end - arrival;
        return TAT;
	}
	
	public boolean getInit() {
		return INIT;
	}

}
