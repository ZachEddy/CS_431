import java.util.ArrayList;

interface KMeansHelper
{
	public int distanceBetween(int[] first, int[] second);
	public DataSet parseData();
	public ArrayList<int[]> initialCentroids(ArrayList<int[]> data);
}