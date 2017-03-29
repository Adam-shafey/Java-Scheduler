import java.util.*;

public class Scheduler extends Thread{
	public static long time = 0;
	public ArrayList<Process> DidNotArriveYet;
	public Qu<Process> A = new Qu<Process>(); // starts as expired and the switches every time
	public Qu<Process> B = new Qu<Process>();
	private volatile boolean running = true; //this stuff is for thread run and pause 
	private volatile boolean paused = false;
    private final Object pauseLock = new Object();
    Process RunningProc; // Reserved for the currently running process
	
	public Scheduler(ArrayList<Process> input){
		DidNotArriveYet = input;
		A.Flag = 1;
	}
	
	public void SwitchFlags(Qu A, Qu B){
		if (A.Flag==1){
			A.Flag=0;
			B.Flag=1;
		}
		else if (A.Flag==0){
			A.Flag=1;
			B.Flag=0;
		}
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
		            if (time == 0){
		            	IncTime(min_arrival_time());
		            	
		            }
		            
		          //Now all arrived processes have been loaded to expired queue (should make this a seperate method later)
	            	
	            	if (A.Flag == 1 && A.Queue.isEmpty()) SwitchFlags(A,B); // if active queue is empty switch flags
            		else if (B.Flag == 1 && B.Queue.isEmpty()) SwitchFlags(A,B);

            			
	            	//Now that Flags are switched
	            	
	            	if (A.Flag == 1){//SLEEPS UNTIL IT'S TIME TO PAUSE
						try { 
							if(A.Queue.size() != 0){
							RunningProc = A.pop();
							if (RunningProc.paused == false) RunningProc.start();
							else if (RunningProc.paused == true) RunningProc.Resume();
							if (RunningProc.ptime > RunningProc.execution_time){ Thread.sleep(RunningProc.execution_time); IncTime(RunningProc.execution_time);}
							else if (RunningProc.ptime <= RunningProc.execution_time) { Thread.sleep(RunningProc.ptime); IncTime(RunningProc.ptime);}
						}
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		 			}
					else if (B.Flag == 1){
						try {
						if(B.Queue.size() != 0){
							RunningProc = B.pop();
							if (RunningProc.paused == false) RunningProc.start();
							else if (RunningProc.paused == true) RunningProc.Resume();	
							if (RunningProc.ptime > RunningProc.execution_time){ Thread.sleep(RunningProc.execution_time); IncTime(RunningProc.execution_time);}
							else if (RunningProc.ptime <= RunningProc.execution_time) { Thread.sleep(RunningProc.ptime); IncTime(RunningProc.ptime);}
						}
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
	            	
	            	if(DidNotArriveYet.isEmpty() && A.Queue.isEmpty() && B.Queue.isEmpty()) Stop();
	            	
	            	//When Scheduler Wakes up
	            	if (RunningProc != null){
	            	RunningProc.pause();
	            	if (A.Flag == 0 && RunningProc.running == true) A.add(RunningProc);
        			else if (B.Flag == 0 && RunningProc.running == true) B.add(RunningProc);
	            	}
	            	
	            	if(DidNotArriveYet.isEmpty() && A.Queue.isEmpty() && B.Queue.isEmpty()) Stop();
		        }
		            	
		 }
	public void Stop() {
        running = false;
        // you might also want to do this:
        //interrupt();
    }
	
	long min_arrival_time(){//returns the earliest arrival time available
    	long min = 99999;
		for (int i = 0; i < DidNotArriveYet.size(); i++) {// load arrived processes into expired queue
			if (DidNotArriveYet.get(i).arrival_time < min) min = DidNotArriveYet.get(i).arrival_time;
		}
		return min;
	}
	
	public void checkforArrivals(){
		for (int i = 0; i < DidNotArriveYet.size(); i++) {// load arrived processes into expired queue
    		if(time >= DidNotArriveYet.get(i).arrival_time){
    			if (A.Flag == 0){ A.add(DidNotArriveYet.get(i)); DidNotArriveYet.get(i).print(); DidNotArriveYet.remove(i);} 
    			else if (B.Flag == 0){ B.add(DidNotArriveYet.get(i)); DidNotArriveYet.get(i).print(); DidNotArriveYet.remove(i);} 
	
    		}
		}
	}
	public void pause() {
        // you may want to throw an IllegalStateException if !running
        paused = true;
    }

    public void Resume() {
        synchronized (pauseLock) {
            paused = false;
            pauseLock.notifyAll(); // Unblocks thread
        }
    }
    public void IncTime(long incby){
    	for(int i = 1; i<=incby; i++){
    		time++;
    		if(time % 50 == 0) checkforArrivals();
    	}
    }
}
