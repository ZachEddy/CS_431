import java.util.ArrayList;
import java.util.HashMap;

/**
 * An implementaton of k-nearest neighbor
 * @author Zach Eddy
 */
class NearestNeighbor
{
	DataSet data;
	int k;

	/**
	 * Default constructor for the classifier. It chooses k to be 6
	 * @param  data dataset used in classification
	 */
	public NearestNeighbor(DataSet data)
	{
		this(data, 6);
		this.k = k;
	}

	/**
	 * Constructor for the classifier
	 * @param  data dataset used in classification
	 * @param  k number of neighbor to check during calculation
	 */
	public NearestNeighbor(DataSet data, int k)
	{
		//set instance variables
		this.data = data;		
		this.k = k;
	}

	/**
	 * A method that finds the closest neighbors to the input datum
	 * @param  datum the input data
	 * @return the list of neighbors determined by k
	 */
	public DataPoint[] neighbors(DataPoint datum)
	{
		//make a copy of the data so the algorithm doesn't actually remove member of the data set
		ArrayList<DataPoint> dataCopy = new ArrayList<DataPoint>();
		dataCopy.addAll(data.set);
		
		DataPoint[] neighborList = new DataPoint[k];	
		//go through 'k' number of times to find the closest neighbors
		for(int i = 0; i < k; i++)
		{
			//assume the closest is at index 0 (just so it isn't null)
			DataPoint closest = dataCopy.get(0);
			for(DataPoint neighbor : dataCopy)
			{
				//if a given neighbor is closer than the current closest, make the neighbor the current closest
				if(distance(neighbor, datum) < distance(closest, datum))
				{
					closest = neighbor;
				}
			}
			//add the closest neighbor to the list of closest neighbors
			//be sure to remove it, otherwise it'll be the only neighbor added for k iterations
			neighborList[i] = closest;
			dataCopy.remove(closest);
		}
		return neighborList;
	}

	/**
	 * A method to classify a given input datum
	 * @param  datum to classify
	 * @return label value (1 for republican, 0 for democrat) determined from the output neuron
	 */
	public int classify(DataPoint datum)
	{
		DataPoint[] nearest = neighbors(datum);
		double label = 0;
		//go through all the neighbors and get their labels
		for(int i = 0; i < nearest.length; i++)
		{
			//if the neighbor and the datum input have the same features, then 
			//just return the label of the datum
			if(distance(datum, nearest[i]) == 0)
			{
				return nearest[i].label;
			}
			//otherwise just add the label to the current label total
			label += nearest[i].label;
		}
		//find the average label for all the neighbors. If it's greater than 0.5, then you know
		//its k neighbors are mostly republican. If not, the datum has mostly democrat neighbors.
		//NOTE: I used a similarity function to weight the votes according to how close they 
		//were to the datum, but that surprisingly lowered the overall accuracy so I removed it.
		label = label / k;
		if(label > 0.5)
		{
			return 1;
		}
		return 0;
	}

	/**
	 * This method determines the distance bewteen two data points
	 * @param  first datapoint
	 * @param  second datapoint
	 * @return the corresponding distance between them
	 */
	public double distance(DataPoint first, DataPoint second)
	{
		double totalDistance = 0;
		for(int i = 0; i < first.features.length; i++)
		{
			double distance = first.features[i] - second.features[i];
			totalDistance += (distance * distance);
		}
		return Math.sqrt(totalDistance);
	}
}