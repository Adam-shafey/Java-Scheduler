import java.util.Comparator;

public class ProcessComparator implements Comparator<Process>
{
	@Override
	public int compare(Process o1, Process o2) {
		if(o1.getpriority() == o2.getpriority()) return 0;
		else if (o1.getpriority() > o2.getpriority()) return 1;
		else return -1;
	}
}