import java.util.Scanner;
import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;

class DecisionTree
{
	public DecisionTree(DataParser parser)
	{

	}

	public static void main(String[] args)
	{
		VoteParser p = new VoteParser("voting-data.tsv");
		new DecisionTree(p);
	}
}

class VoteParser implements DataParser
{
	private File file;
	private int tuneValue;
	private ArrayList<int[]> testSet;
	private ArrayList<int[]> dataSet;

	public VoteParser(String fileName)
	{
		this(fileName, 4);
	}

	public VoteParser(String fileName, int tuningValue)
	{
		this.file = new File(fileName);
		this.tuneValue = tuningValue;
		ParseData();
	}

	public void ParseData()
	{	
		try
		{
			int lineNumber = 0;
			Scanner scanner = new Scanner(this.file);
			ArrayList<int[]> testSet = new ArrayList<int[]>();
			ArrayList<int[]> tuningSet = new ArrayList<int[]>();

			while(scanner.hasNextLine())
			{
				String voter = scanner.nextLine();
				String[] voterInfo = voter.split("\\s+");
				String[] voterHistory = voterInfo[2].split("(?!^)");
				int[] voterData = new int[voterHistory.length + 1];
				
				for(int i = 0; i < voterHistory.length; i++)
				{
					if(voterHistory[i].equals("-")) //yes
					{
						System.out.print(voterHistory[i] + " , ");
						voterData[i+1] = 0;
					}
					else if(voterHistory[i].equals("+")) //no
					{
						System.out.print(voterHistory[i] + " , ");
						voterData[i+1] = 1;
					}
					else //undecided
					{
						System.out.print(voterHistory[i] + " , ");
						voterData[i+1] = 2;	
					}
				}
				if(voterInfo[1].equals("D"))
				{
					voterData[0] = 0;
				}
				else
				{
					voterData[0] = 1;
				}
				if(lineNumber % 4 == 0)
				{
					tuningSet.add(voterData);
				}
				else
				{
					testSet.add(voterData);
				}
				System.out.println();
				System.out.println(Arrays.toString(voterData));
				lineNumber++;
			}
		}
		catch(FileNotFoundException e)
		{
			System.out.println("File not found"); //+file name later
		}
	}

	public ArrayList<int[]> getTestSet()
	{
		return testSet;
	}

	public ArrayList<int[]> getTuningSet()
	{
		return dataSet;
	}
}


