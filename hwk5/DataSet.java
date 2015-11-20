import java.util.ArrayList;

class DataSet
{
	ArrayList<int[]> data;
	ArrayList<Integer> labels;
	public DataSet(ArrayList<Integer> labels, ArrayList<int[]> data)
	{
		this.labels = labels;
		this.data = data;
	}
}