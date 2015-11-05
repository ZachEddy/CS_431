import java.util.ArrayList;

class DataSet
{
	ArrayList<int[]> testSet;
	ArrayList<int[]> tuneSet;
	int[] featureSizes;
	public DataSet(ArrayList<int[]> testSet, ArrayList<int[]> tuneSet, int[] featureSizes)
	{
		this.testSet = testSet;
		this.tuneSet = tuneSet;
		this.featureSizes = featureSizes;
	}
}