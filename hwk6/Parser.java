import java.util.Scanner;
import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;

class Parser
{
	File file;
	public Parser(String fileName)
	{
		this.file = new File(fileName);
	}

	public DataSet parse()
	{
		try
		{
			Scanner scanner = new Scanner(this.file);
			DataSet dataSet = new DataSet();	
			int counter = 0;
			while(scanner.hasNextLine())
			{
				String voter = scanner.nextLine();				
				String[] voterInfo = voter.split("\\s+");
				double[] voterHistory = getVoteData(voterInfo[2].split("(?!^)")); 
				int label = getLabel(voterInfo[1]);

				dataSet.addTrainingMember(new DataPoint(label, voterHistory));
				counter++;
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