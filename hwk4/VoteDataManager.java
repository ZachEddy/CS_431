import java.util.Scanner;
import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
/**
 * A class made to parse and print the voting data
 * @author Zach Eddy
 */
class VoteDataManager
{
	private File file;
	private int tuneValue;
	private String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	/**
	 * Constructor
	 * @param  fileName name of file containing data 
	 */
	public VoteDataManager(String fileName)
	{
		this.file = new File(fileName);
	}
	/**
	 * a method that parses a single line of text and puts the outcome and feature choices into an array
	 * index 0 will always hold the outcome, and every value thereafter holds a feature choice.
	 * @param  voter string that holds the political party and vote history
	 * @return voter's political party and vote history in an array
	 */
	public int[] getVoterData(String voter)
	{
		//split the line by whitespace, then split the voting history into individual votes
		String[] voterInfo = voter.split("\\s+");
		String[] voterHistory = voterInfo[2].split("(?!^)");
		int[] voterData = new int[voterHistory.length + 1];
		for(int i = 0; i < voterHistory.length; i++)
		{
			if(voterHistory[i].equals("-")) //voted nay
			{
				voterData[i+1] = 0;
			}
			else if(voterHistory[i].equals("+")) //voted yay
			{
				voterData[i+1] = 1;
			}
			else //undecided
			{
				voterData[i+1] = 2;	
			}
		}
		//what political party does the voter identify with?
		if(voterInfo[1].equals("D"))
		{
			voterData[0] = 0;
		}
		else
		{
			voterData[0] = 1;
		}
		//return the array that contains all the info about the voter
		return voterData;
	}
	/**
	 * A method that parses the text file and adds each voter's information to a list
	 * @return [description]
	 */
	public DataSet parseData()
	{
		//make sure the file exists
		try
		{
			Scanner scanner = new Scanner(this.file);
			ArrayList<int[]> set = new ArrayList<int[]>();
			//add each line to the set of voter information until EOF
			while(scanner.hasNextLine())
			{
				int[] voterData = getVoterData(scanner.nextLine());
				set.add(voterData);
			}
			//define the features of the voter
			int[] featureSizes = new int[set.get(0).length];
			featureSizes[0] = 2;
			for(int i = 1; i < featureSizes.length; i++)
			{
				featureSizes[i] = 3;
			}
			return new DataSet(set, featureSizes);
		}
		catch(FileNotFoundException e)
		{
			System.out.println("File not found"); //+file name later
		}
		return null;
	}
	/**
	 * A helper function that initiates the recursive print method
	 * @param tree to print
	 */
	public void printDecisionTree(DecisionTree tree)
	{
		printDecisionTree(tree.root, 1);
	}
	/**
	 * A recursive function that prints a decision tree
	 * @param node to print
	 * @param spacing how many spaces to print before an issue
	 */
	private void printDecisionTree(DecisionNode node, int spacing)
	{
		//if the node is a terminal, print the outcome.
		//if the outcome is anything but the democrat/republican binary, print an error message
		if(node.terminal)
		{
			//determine whether they're democrat or republican based on the outcome
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
				System.out.println("Error during printing");
				System.exit(-1);
			}
			System.out.println(voter);
		}
		else
		{
			//if the node is a decision, print the issue and its subgroups
			System.out.println("Issue " + alphabet.charAt(node.feature - 1) + ":");
			for(DecisionNode child : node.children)
			{
				//go out a certain number of spaces
				for(int i = 0; i < spacing; i++)
				{
					System.out.print("   ");
				}
				String decision = "";
				//check the node's decision and match it to a "-+."
				if(child.decision == 0)
				{
					decision = "-";
				}
				else if(child.decision == 1)
				{
					decision = "+";
				}
				else
				{
					decision = ".";
				}
				System.out.print(decision + " ");
				printDecisionTree(child, spacing + 1);
			}
		}
	}
}