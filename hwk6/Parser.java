import java.util.Scanner;
import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * A class to parse the voting
 * @author Zach Eddy
 */
class Parser
{
	File file;

	/**
	 * Constructor that takes in the location of the file to parse
	 * @param  fileName path
	 */
	public Parser(String fileName)
	{
		this.file = new File(fileName);
	}

	/**
	 * A method that parses a .tsv file into a dataset
	 * @return data set with the voting data
	 */
	public DataSet parse()
	{
		try
		{
			Scanner scanner = new Scanner(this.file);
			DataSet dataSet = new DataSet();	

			//scan through the text file until EOF
			while(scanner.hasNextLine())
			{
				String voter = scanner.nextLine();				
				String[] voterInfo = voter.split("\\s+");
				//find the individual voting issues, then turn them into an array of doubles instead of Strings
				double[] voterHistory = getVoteData(voterInfo[2].split("(?!^)")); 
				//get the corresponding label
				int label = getLabel(voterInfo[1]);
				dataSet.addTrainingMember(new DataPoint(label, voterHistory));
			}			
			return dataSet;
		}
		catch(FileNotFoundException e)
		{
			//inform the user that the file wasn't found, then exit
			System.out.println("File \"" + this.file.getName() + "\" not found"); //+file name later
			System.exit(-1);
		}
		return null;
	}
	/**
	 * This takes a String[] of voting data and turns it into a Double[] for the purposes of the neural network and nearest neighbor classifiers
	 * @param  votes represented as a String[]
	 * @return votes represented as a Double[]
	 */
	public double[] getVoteData(String[] votes)
	{
		double[] voteData = new double[votes.length];
		//go through the history and see how a representative voted.
		for(int i = 0; i < voteData.length; i++)
		{
			String vote = votes[i];
			if(vote.equals("-")) //nay
			{
				voteData[i] = -1;
			}
			else if(vote.equals("+")) //yay
			{
				voteData[i] = 1;
			}
			else //undecided
			{
				voteData[i] = 0;
			}
		}
		return voteData;
	}

	/**
	 * This gets the label of a representative 
	 * @param  rep representative's political party
	 * @return the representative's political party defined numerically
	 */
	public int getLabel(String rep)
	{
		//1 for republican, 0 for democrat
		if(rep.equals("R"))
		{
			return 1;
		}
		return 0;
	}
}