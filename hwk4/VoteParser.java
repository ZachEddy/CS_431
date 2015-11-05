import java.util.Scanner;
import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;

class VoteParser implements DataParser
{
	private File file;
	private int tuneValue;

	public VoteParser(String fileName)
	{
		this(fileName, 4);
	}

	public VoteParser(String fileName, int tuningValue)
	{
		this.file = new File(fileName);
		this.tuneValue = tuningValue;
	}
	
	public int[] getVoterData(String voter)
	{
		String[] voterInfo = voter.split("\\s+");
		String[] voterHistory = voterInfo[2].split("(?!^)");
		int[] voterData = new int[voterHistory.length + 1];
		for(int i = 0; i < voterHistory.length; i++)
		{
			if(voterHistory[i].equals("-")) //yes
			{
				voterData[i+1] = 0;
			}
			else if(voterHistory[i].equals("+")) //no
			{
				voterData[i+1] = 1;
			}
			else //undecided
			{
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
		return voterData;
	}

	public DataSet parseData()
	{	
		try
		{
			Scanner scanner = new Scanner(this.file);
			ArrayList<int[]> testSet = new ArrayList<int[]>();
			ArrayList<int[]> tuneSet = new ArrayList<int[]>();
			int lineNumber = 0;
			while(scanner.hasNextLine())
			{
				// String voter = scanner.nextLine();


				// String[] voterInfo = voter.split("\\s+");
				// String[] voterHistory = voterInfo[2].split("(?!^)");
				// int[] voterData = new int[voterHistory.length + 1];
				
				// for(int i = 0; i < voterHistory.length; i++)
				// {
				// 	if(voterHistory[i].equals("-")) //yes
				// 	{
				// 		voterData[i+1] = 0;
				// 	}
				// 	else if(voterHistory[i].equals("+")) //no
				// 	{
				// 		voterData[i+1] = 1;
				// 	}
				// 	else //undecided
				// 	{
				// 		voterData[i+1] = 2;	
				// 	}
				// }
				// if(voterInfo[1].equals("D"))
				// {
				// 	voterData[0] = 0;
				// }
				// else
				// {
				// 	voterData[0] = 1;
				// }
				

				int[] voterData = getVoterData(scanner.nextLine());
				if(lineNumber % 4 == 0)
				{
					tuneSet.add(voterData);
				}
				else
				{
					testSet.add(voterData);
				}
				lineNumber++;
			}
			int[] featureSizes = new int[testSet.get(0).length];
			for(int i = 0; i < featureSizes.length; i++)
			{
				if(i == 0)
				{
					featureSizes[i] = 2;
				}
				else
				{
					featureSizes[i] = 3;
				}
			}
			return new DataSet(testSet, tuneSet, featureSizes);
		}
		catch(FileNotFoundException e)
		{
			System.out.println("File not found"); //+file name later
		}
		return null;
	}

	public void printDecisionTree(DecisionTree tree)
	{
		printDecisionTree(tree.root, 1);
	}

	public void printDecisionTree(DecisionNode node, int spacing)
	{
		if(node.terminal)
		{
			String voter = "";
			if(node.outcome == 0)
			{
				voter = "D";
			}
			else if(node.outcome == 1)
			{
				voter = "R";
			}
			else
			{
				System.out.println("Error");
				System.exit(-1);
			}
			System.out.println(voter);
		}
		else
		{
			System.out.println("Issue: " + node.feature);
			for(DecisionNode n : node.children)
			{
				for(int i = 0; i < spacing; i++)
				{
					System.out.print("    ");
				}
				String decision = "";
				if(n.decision == 0)
				{
					decision = "-";
				}
				else if(n.decision == 1)
				{
					decision = "+";
				}
				else
				{
					decision = ".";
				}
				System.out.print(decision + " ");
				printDecisionTree(n, spacing + 1);
			}
		}
	}
	

	// public void printDecisionTree(DecisionNode node, int spacing)
	// {
	// 	// if(node.terminal == true)
	// 	// {
	// 	// 	System.out.println(node.decision);
	// 	// 	return;
	// 	// }
		
	// 	for(int i = 0; i < spacing; i++)
	// 		System.out.print("    ");
	// 	System.out.println("Issue " + node.category);
	// 	if(!node.children.isEmpty())
	// 	{
	// 		for(DecisionNode n : node.children)
	// 		{
	// 			System.out.print(n.decision);
	// 			printDecisionTree(n, spacing + 1);
	// 		}
	// 	}
	// }
}