
public class Process extends Thread{
	String PID;
	int priority;
	int ptime;
	int application_time; //this should be removed and rplaced by time from the application
	long waiting_time;
	long time_at_pause;
	long time_at_resume;
	int arrival_time;
	int execution_time;
	int bonus;
	int numberofExecutions = 0;
	public volatile boolean running = true; //this stuff is for thread run and pause 
	public volatile boolean paused = false;
    public final Object pauseLock = new Object();


	
	public Process(String pid, int atim, int exectime, int p){
		PID = pid;
		priority = p;
		arrival_time = atim;
		execution_time = exectime;
		SetPtime();
	}
	public void print(){
	    System.out.println("time: " + arrival_time + " " + PID + ", Arrived");
	}
 	public void updatePriority(){
		if (numberofExecutions % 2 == 0 && numberofExecutions != 0){
			bonus = (int) Math.floor(10*waiting_time/(application_time-arrival_time));
			priority=Math.max(100, Math.min(priority-bonus+5,139));
		    System.out.println("time: " + Scheduler.time + " " + PID + ", Updated priority");

		}
	}
	public void SetPtime(){
		if(priority < 100){
		ptime = (140-priority)*20; }
		else if (priority >= 100){
		ptime = (140-priority)*5; }
	}
	
	public int getpriority(){
		return priority;
	}
	public boolean isEqual(Process other){
		return this.getpriority() == other.getpriority();
	}
	
    @Override
	public void run() {
		 while (running){
	            synchronized (pauseLock) {
	                if (!running) { // may have changed while waiting to
	                                // synchronize on pauseLock
	                    break;
	                }
	                if (paused) {
	                    try {
	                        pauseLock.wait(); // will cause this Thread to block until 
	                                          // another thread calls pauseLock.notifyAll()
	                                          // Note that calling wait() will 
	                                          // relinquish the synchronized lock that this 
	                                          // thread holds on pauseLock so another thread
	                                          // can acquire the lock to call notifyAll()
	                                          // (link with explanation below this code)
	                    } catch (InterruptedException ex) {
	                        break;
	                    }
	                    if (!running) { // running might have changed since we paused
	                        break;
	                    }
	                }
	            }
	            System.out.println("time: " + Scheduler.time + " " + PID + ", Started, granted  " +  ptime );
			    try {
					Thread.sleep(execution_time);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			    Stop();
			    }
	    }
			   
	public void Stop() {
        running = false;
        // you might also want to do this:
        //interrupt();
	    System.out.println("time: " + Scheduler.time + " " + PID + ", Terminated");
    }
	
	public void pause() {
        // you may want to throw an IllegalStateException if !running
        paused = true;
		numberofExecutions++;
		time_at_pause = Scheduler.time;
		updatePriority();
		SetPtime();
	    System.out.println("time: " + Scheduler.time + " " + PID + ", Paused");

		
    }

    public void Resume() {
        synchronized (pauseLock) {
    		time_at_resume = Scheduler.time;
    		waiting_time += time_at_resume-time_at_pause;
            paused = false;
            pauseLock.notifyAll(); // Unblocks thread
            System.out.println("time: " + Scheduler.time + " " + PID + ", Resumed, granted  " +  ptime );

        }
    }
}
