import java.util.ArrayList;
import java.util.Arrays;

class DecisionTree
{
	ArrayList<int[]> tuneSet;
	ArrayList<int[]> testSet;
	int[] featureSizes;
	DecisionNode root;
	// Parser parser;
	
	public DecisionTree(DataParser parser)
	{
		DataSet dataSet = parser.parseData();
		this.testSet = dataSet.testSet;
		this.tuneSet = dataSet.tuneSet;
		this.featureSizes = dataSet.featureSizes;
		root = constructTree(testSet);
	}

	public DecisionNode constructTree(ArrayList<int[]> set)
	{
		System.out.println(set == null);
		ArrayList<Integer> visited = new ArrayList<Integer>();
		for(int i = 1; i < featureSizes.length; i++)
		{
			visited.add(i);
		}
		return constructTree(set, visited);
	}

	public DecisionNode constructTree(ArrayList<int[]> set, ArrayList<Integer> visited)
	{
		if(set.size() == 0)
		{
			DecisionNode terminal = new DecisionNode(set, true);
			return terminal;
		}
		if(isPure(set))
		{
			DecisionNode terminal = new DecisionNode(set, true);
			terminal.outcome = set.get(0)[0];
			return terminal;
		}
		double[] informationGain = calcInformationGain(set, visited);
		int maxIndex = visited.get(0);
		for(Integer gainIndex : visited)
		{
			if(informationGain[gainIndex] > informationGain[maxIndex])
			{
				maxIndex = gainIndex;
			}
		}
		if(informationGain[maxIndex] <= 0)
		{
			DecisionNode terminal = new DecisionNode(set, true);
			terminal.outcome = findMajorityOutcome(set);
			return terminal;
		}
		ArrayList<ArrayList<int[]>> subgroups = getSubgroups(set, maxIndex);
		ArrayList<Integer> newVisited = new ArrayList<Integer>();
		newVisited.addAll(visited);
		newVisited.remove(newVisited.indexOf(maxIndex));

		DecisionNode parent = new DecisionNode(set, false);
		int counter = 0;
		for(ArrayList<int[]> group : subgroups)
		{
			parent.feature = maxIndex;
			DecisionNode decision = constructTree(group, newVisited);
			decision.decision = counter;
			if(group.size() == 0)
			{
				decision.outcome = findMajorityOutcome(set);
			}
			parent.outcome = findMajorityOutcome(set);
			parent.children.add(decision);
			counter++;
		}
		return parent;
	}

	public int findMajorityOutcome(ArrayList<int[]> set)
	{
		int[] features = new int[featureSizes[0]];
		for(int[] member : set)
		{
			features[member[0]]++;
		}
		int highest = 0;
		for(int i = 0; i < features.length; i++)
		{
			if(features[highest] < features[i])
				highest = i;
		}
		return highest;
	}

	// public int maxGainIndex(double[] informationGain)
	// {
	// 	int currentMax = 1;
	// 	for(int i = 1; i < informationGain.length; i++)
	// 	{
	// 		if(informationGain[i] > informationGain[currentMax])
	// 		{
	// 			currentMax = i;
	// 		}
	// 	}
	// 	return currentMax;
	// }

	public double[] calcInformationGain(ArrayList<int[]> set, ArrayList<Integer> visited)
	{
		double[] informationGain = new double[featureSizes.length];
		//go through all features, find its information gain
		for(int i = 1; i < informationGain.length; i++)
		{
			if(!visited.contains(i))
			{
				continue;
			}
			double gain = calcEntropy(set);
			ArrayList<ArrayList<int[]>> subgroups = getSubgroups(set, i);
			//go through all the subgroups of the feature and calculate its entropy
			for(ArrayList<int[]> group : subgroups)
			{
				if(group.size() == 0) //don't calucate entropy of group that doesn't have any members.
				{
					 continue;
				}
				// double entropy = calcEntropy(group);
				gain -= ((double) group.size() / (double) set.size()) * calcEntropy(group);
			}
			informationGain[i] = gain;
		}
		return informationGain;
	}

	public boolean isPure(ArrayList<int[]> set)
	{
		int decision = set.get(0)[0];
		for(int[] member : set)
		{
			if(member[0] != decision)
			{
				return false;
			}
		}
		return true;
	}

	public double calcEntropy(ArrayList<int[]> set)
	{
		double entropy = 0;
		int length = set.size();
		int[] occurence = new int[featureSizes[0]];
		//find how many times each choice appears for a given feature for probability purposes
		for(int i = 0; i < length; i++)
		{
			int[] member = set.get(i);
			occurence[member[0]]++;
		}
		//go through all the possible occurences. Find its probability and use it in the entropy function
		for(int i = 0; i < occurence.length; i++)
		{
			double probability = (double) occurence[i] / (double) length;
			if(probability == 0)
				continue;
			entropy -= probability * log2(probability);			
		}
		return entropy;
	}

	public ArrayList<ArrayList<int[]>> getSubgroups(ArrayList<int[]> set, int feature)
	{
		int featureSize = featureSizes[feature];
		ArrayList<ArrayList<int[]>> subgroups = new ArrayList<ArrayList<int[]>>();
		//go through all the possible choices for a given feature and create a fresh array list
		for(int i = 0; i < featureSize; i++)
		{
			subgroups.add(new ArrayList<int[]>());
		}
		//add a particular datum to an array list depending on what it exhibits for a given feature
		for(int[] member : set)
		{
			subgroups.get(member[feature]).add(member);
		}
		return subgroups;
	}

	public double log2(double x)
	{
		return Math.log(x)/Math.log(2.0d);
	}

	public int findOutcome(int[] datum, DecisionNode node)
	{
		DecisionNode visiting = node;
		while(!visiting.terminal)
		{
			visiting = visiting.children.get(datum[visiting.feature]);
		}
		return visiting.outcome;
	}

	public void tuneTree(DecisionNode node)
	{
		for(DecisionNode child : node.children)
		{
			if(child.terminal)
				continue;
			tuneTree(child);
		}
		double treeAccuracyBefore = treeAccuracy(this.root);
		node.terminal = true;
		double treeAccuracyAfter = treeAccuracy(this.root);
		if(treeAccuracyBefore > treeAccuracyAfter)
		{
			node.terminal = false;
		}
	}
	public double treeAccuracy(DecisionNode node)
	{
		double correct = 0;
		for(int[] group : tuneSet) 
		{
			if(group[0] == findOutcome(group, node))
				correct++;
		}
		return correct / tuneSet.size();
	}

	public static void main(String[] args)
	{
		VoteParser parser = new VoteParser("voting-data.tsv");
		DecisionTree tree = new DecisionTree(parser);
		System.out.println("BEFORE " + tree.treeAccuracy(tree.root));
		parser.printDecisionTree(tree);
		tree.tuneTree(tree.root);
		
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();

		System.out.println("AFTER " + tree.treeAccuracy(tree.root));
		parser.printDecisionTree(tree);

	}
}