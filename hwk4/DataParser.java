import java.util.ArrayList;
interface DataParser
{
	public ArrayList<int[]> getTestSet();
	public ArrayList<int[]> getTuningSet();
	public void ParseData();

}