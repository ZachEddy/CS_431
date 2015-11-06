import java.util.ArrayList;
/**
 * A tuple to hold all the parsed data points and the size of each feature (how many choices)
 * @author Zach Eddy
 */
class DataSet
{
	ArrayList<int[]> data;
	int[] featureSizes;

	/**
	 * Constructor
	 * @param list that contains datapoints. The first index is the outcome, and the remaining indices are its features.
	 * @param featureSizes how many choices a given feature has
	 */
	public DataSet(ArrayList<int[]> data, int[] featureSizes)
	{
		this.data = data;
		this.featureSizes = featureSizes;
	}
}