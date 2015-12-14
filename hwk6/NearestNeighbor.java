import java.util.ArrayList;
class NearestNeighbor
{
	DataSet data;
	public NearestNeighbor(DataSet data)
	{
		this.data = data;		
		DataPoint thingy = data.set.remove(1);
	}
	
	public int classify(DataPoint datum)
	{
		ArrayList<DataPoint> set = data.set;
		DataPoint nearest = set.get(0);
		double neartestSimilarity = similarity(datum, nearest);
		for(DataPoint member : set)
		{
			double similarity = similarity(datum, member);
			if(similarity == -1)
			{
				return datum.label;
			}
			if(similarity > neartestSimilarity)
			{
				neartestSimilarity = similarity;
				nearest = member;
			}
		}
		return nearest.label;
	}
	
	public double similarity(DataPoint first, DataPoint second)
	{	
		int relativeDistance = relativeDistance(first, second);
		if(relativeDistance == 0)
			return -1;
		return 1.0 / relativeDistance;
	}

	public int relativeDistance(DataPoint first, DataPoint second)
	{
		int relativeDistance = 0;
		for(int i = 0; i < first.features.length; i++)
		{
			relativeDistance += squareDistanceBetween((int)first.features[i], (int)second.features[i]);
		}
		return relativeDistance;
	}
	
	private int squareDistanceBetween(int first, int second)
	{
		int distance = first - second;
		return distance * distance;
	}
}