import java.util.ArrayList;

public class MainCode {
	static long timenow = System.currentTimeMillis();
	public static void main(String[] args) {
	ArrayList<Process> input = new ArrayList<Process>();
	input.add(new Process("P1", 1000, 2500, 90));	
	input.add(new Process("P2", 2000, 100, 120));	
	input.add(new Process("P3", 3200, 100, 120));
	Scheduler S = new Scheduler(input);
	S.start();
	}
}
