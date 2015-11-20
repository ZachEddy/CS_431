import java.util.ArrayList;
import java.util.HashMap;
import java.util.Arrays;

class KMeans
{
	ArrayList<int[]> centroids;
	DataSet dataSet;
	KMeansHelper helper;

	public KMeans(KMeansHelper helper)
	{
		DataSet dataSet = helper.parseData();
		this.helper = helper;
		this.dataSet = dataSet;
		this.centroids = helper.initialCentroids(this.dataSet.data);
		expectationMaximization();
	}

	public HashMap<int[], ArrayList<int[]>> getClusters()
	{
		return clusters();
	}

	public HashMap<int[], ArrayList<int[]>> clusters()
	{
		HashMap<int[], ArrayList<int[]>> clusters = new HashMap<int[], ArrayList<int[]>>();
		for(int i = 0; i < this.centroids.size(); i++)
		{
			clusters.put(this.centroids.get(i), new ArrayList<int[]>());
		}
		for(int[] member : this.dataSet.data)
		{	
			int[] closestCentroid = this.centroids.get(0);
			for(int[] centroid : this.centroids)
			{
				if(helper.distanceBetween(member, centroid) == helper.distanceBetween(member, closestCentroid))
				{
					closestCentroid = breakTie(closestCentroid, centroid);
				}
				else if(helper.distanceBetween(member, centroid) < helper.distanceBetween(member, closestCentroid))
				{
					closestCentroid = centroid;
				}
			}
			clusters.get(closestCentroid).add(member);
		}
		return clusters;
	}

	public void expectationMaximization()
	{
		while(true)
		{
			HashMap<int[], ArrayList<int[]>> clusters = clusters();
			ArrayList<int[]> newCentroids = new ArrayList<int[]>();
			for(ArrayList<int[]> cluster : clusters.values())
			{
				newCentroids.add(adjustCentroid(cluster));
			}
			if(newCentroids.equals(this.centroids))
			{
				return;
			}
			else
			{
				this.centroids = newCentroids;
			}
		}
	}

	public int[] adjustCentroid(ArrayList<int[]> cluster)
	{
		int[] newCentroid = cluster.get(0);
		int currentDistance = Integer.MAX_VALUE;
		for(int[] member : cluster)
		{
			int relativeDistance = 0;
			for(int[] other : cluster)
			{
				relativeDistance += helper.distanceBetween(member, other);
			}
			if(relativeDistance < currentDistance)
			{
				newCentroid = member;
				currentDistance = relativeDistance;
			}
			else if(relativeDistance == currentDistance)
			{
				newCentroid = breakTie(member, newCentroid);
			}
		}
		return newCentroid;
	}

	public int[] breakTie(int[] first, int[] second)
	{
		if(this.dataSet.data.indexOf(first) > this.dataSet.data.indexOf(second))
		{
			return first;
		}
		return second;
	}

	public static void main(String[] args)
	{
		if(args.length != 1)
		{
			System.out.println("Program takes one \".tsv\" file argument.");
			System.exit(-1);
		}
		VoteHelper helper = new VoteHelper(args[0]);
		KMeans classifier = new KMeans(helper);
		helper.printResults(classifier.getClusters(), classifier.dataSet);
	}
}