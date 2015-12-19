/**
 * A class to hold a single datapoint within a dataset
 * @author Zach Eddy
 */
class DataPoint
{
	int label;
	double[] features;

	/**
	 * Constructor for a data point
	 * @param  label for the singular datum
	 * @param  features that correspond to the label
	 */
	public DataPoint(int label, double[] features)
	{
		this.label = label;
		this.features = features;	
	}
}