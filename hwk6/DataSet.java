import java.util.ArrayList;
class DataSet
{	
	ArrayList<DataPoint> set;
	public DataSet()
	{
		this.set = new ArrayList<DataPoint>();
		// this.trainingSet = new ArrayList<DataPoint>();
	}

	public DataSet(ArrayList<DataPoint> set)
	{
		this.set = set;
	}


	// public void addTestMember(DataPoint member)
	// {
	// 	this.testSet.add(member);
	// }

	public void addTrainingMember(DataPoint member)
	{
		this.set.add(member);
	}

	// public String toString()
	// {
	// 	String toReturn = "";
	// 	for(int i = 0; i < set.size(); i++)
	// 	{
	// 		toReturn += (i+1) + "," + set.get(i).toString() + "\n";
	// 	}
	// 	return toReturn;
	// }
}