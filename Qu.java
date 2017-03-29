import java.util.*;


public class Qu<T> {
	public PriorityQueue<Process> Queue;
	public int front;
	int Flag; // 0 being expired and 1 being active
	 public Qu()
	    {
		 Queue = new PriorityQueue<Process>(1,new ProcessComparator());
		 Flag = 0;
	    }
	 public void add(Process P){
		 Queue.add(P);
	 }
	 public Process pop(){
				return Queue.poll();

	 }
	 public void print(){
		 int size = Queue.size();
		 PriorityQueue<Process> temp = Queue;
		 for (int i = 0; i< size; i++){
		 temp.remove().print();
		 }
	 }
}
