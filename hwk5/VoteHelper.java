import java.util.Scanner;
import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;

class VoteHelper implements KMeansHelper
{
	File file;

	public VoteHelper(String filename)
	{
		this.file = new File(filename);
	}

	public DataSet parseData()
	{
		try
		{
			ArrayList<int[]> voteList = new ArrayList<int[]>();
			ArrayList<Integer> labels = new ArrayList<Integer>();
			Scanner scanner = new Scanner(this.file);
			int counter = 0;
			while(scanner.hasNextLine())
			{
				String voter = scanner.nextLine();
				String[] voterInfo = voter.split("\\s+");
				int[] voterHistory = getVoteData(voterInfo[2].split("(?!^)")); 
				int label = getLabel(voterInfo[1]);

				voteList.add(voterHistory);
				labels.add(label);
				counter++;
			}
			voteList.trimToSize();
			labels.trimToSize();
			return new DataSet(labels, voteList);
		}
		catch(FileNotFoundException e)
		{
			System.out.println("File \"" + this.file.getName() + "\" not found"); //+file name later
			System.exit(-1);
		}
		return null;
	}

	public ArrayList<int[]> initialCentroids(ArrayList<int[]> data)
	{
		int[] centroidOne = null;
		int[] centroidTwo = null;
		int positionOne = -1;
		int positionTwo = -1;
		int currentDistance = -1;
		for(int i = 0; i < data.size(); i++)
		{
			int[] voterOne = data.get(i);
			for(int j = i + 1; j < data.size(); j++)
			{
				int[] voterTwo = data.get(j);
				int relativeDistance = distanceBetween(voterOne, voterTwo);
				
				if(relativeDistance > currentDistance)
				{
					centroidOne = voterOne;
					centroidTwo = voterTwo;
					
					positionOne = i;
					positionTwo = j;

					currentDistance = relativeDistance;
				}
			}
		}
		ArrayList<int[]> centroids = new ArrayList<int[]>();
		System.out.println("Rep-" + (positionOne + 1) + " and Rep-" + (positionTwo + 1) + "  chosen for initial centroids.");
		centroids.add(centroidOne);
		centroids.add(centroidTwo);
		return centroids;
	}

	public int getLabel(String rep)
	{
		if(rep.equals("R"))
		{
			return 1;
		}
		return 0;
	}
	
	public int[] getVoteData(String[] votes)
	{
		int[] voteData = new int[votes.length];
		for(int i = 0; i < voteData.length; i++)
		{
			String vote = votes[i];
			if(vote.equals("-"))
			{
				voteData[i] = -1;
			}
			else if(vote.equals("+"))
			{
				voteData[i] = 1;
			}
			else
			{
				voteData[i] = 0;
			}
		}
		return voteData;
	}
	
	public int distanceBetween(int[] first, int[] second)
	{
		int relativeDistance = 0;
		for(int i = 0; i < first.length; i++)
		{
			relativeDistance += squareDistanceBetween(first[i], second[i]);
		}
		return relativeDistance;
	}
	
	private int squareDistanceBetween(int first, int second)
	{
		int distance = first - second;
		return distance * distance;
	}

	public void printResults(HashMap<int[], ArrayList<int[]>> clusters, DataSet dataSet)
	{
		ArrayList<Integer> labels = dataSet.labels;
		ArrayList<int[]> data = dataSet.data;

		int groupCount = 1;
		System.out.println("Final groups:");
		for(int[] centroid : clusters.keySet())
		{
			double democratCount = 0;
			double republicanCount = 0;
			double groupSize = clusters.get(centroid).size();
			for(int[] member : clusters.get(centroid))
			{
				int label = labels.get(data.indexOf(member));
				if(label == 1)
				{
					democratCount++;
				}
				else
				{
					republicanCount++;
				}
			}
			System.out.print("Group " + groupCount + " (" + groupSize + ") reps:  ");
			System.out.print(roundedPercentage(democratCount / groupSize) + "% Dem, ");
			System.out.print(roundedPercentage(republicanCount / groupSize) + "% Rep");
			System.out.println();
			groupCount++;
		}
	}

	private double roundedPercentage(double toRound)
	{
		toRound *= 100;
		return Math.round(toRound * 100.0) / 100.0;
	}
}