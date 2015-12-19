import java.util.ArrayList;

/**
 * A class to represent all of the voting data
 * @author Zach Eddy
 */
class DataSet
{	
	ArrayList<DataPoint> set;
	/**
	 * Constructor to make an empty dataset
	 * @return [description]
	 */
	public DataSet()
	{
		this.set = new ArrayList<DataPoint>();
	}

	/**
	 * Constructor to make a new dataset with a pre-defined set of datapoints
	 * @param  set of datapoints
	 */
	public DataSet(ArrayList<DataPoint> set)
	{
		this.set = set;
	}

	/**
	 * Add a specific member to the dataset
	 * @param member a new data member
	 */
	public void addTrainingMember(DataPoint member)
	{
		this.set.add(member);
	}
}