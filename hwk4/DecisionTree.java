import java.util.ArrayList;
import java.util.Arrays;
/**
 * A decision tree made for CS 431
 * @author Zach Eddy
 */
class DecisionTree
{
	ArrayList<int[]> tuneSet;
	ArrayList<int[]> testSet;
	ArrayList<int[]> completeSet;
	int[] featureSizes;
	DecisionNode root;

	/**
	 * The tree expects 'n' number of features for 'm' possible of outcomes. Data must be complete.
	 * @param  dataSet object containing all the parsed data (in the specified format)
	 * @param  tuneInterval how often to add to your tuning set
	 */
	public DecisionTree(DataSet dataSet, int tuneInterval)
	{
		//initialize instance variables
		this.completeSet = dataSet.data;
		this.tuneSet = new ArrayList<int[]>();
		this.testSet = new ArrayList<int[]>();
		this.featureSizes = dataSet.featureSizes;
		populateSets(tuneInterval);
		build();
	}
	/**
	 * A method to build the tuning and test sets
	 * @param tuneInterval how frequently to add to the tuning set
	 */
	private void populateSets(int tuneInterval)
	{
		for(int i = 0; i < completeSet.size(); i++)
		{
			//check to see if data point needs to be added to the tuning set
			if(i % tuneInterval == 0)
			{
				this.tuneSet.add(completeSet.get(i));
			}
			else
			{
				this.testSet.add(completeSet.get(i));
			}
		}
	}
	/**
	 * A starter method to build the decision tree
	 * @return the root node of the tree
	 */
	private DecisionNode build()
	{
		//'remaining' keeps track of the features that haven't been visited yet.
		//i starts at 1 because the features in each data item start at index 1.
		ArrayList<Integer> remaining = new ArrayList<Integer>();
		for(int i = 1; i < featureSizes.length; i++)
		{
			remaining.add(i);
		}
		root = build(testSet, remaining);
		return root;
	}
	/**
	 * The method that actually builds a decision node
	 * @param set of data for a given node in the tree
	 * @param remaining features to possibly use for subgrouping
	 * @return the decision node a given set 
	 */
	public DecisionNode build(ArrayList<int[]> set, ArrayList<Integer> remaining)
	{
		//terminal nodes are leaf nodes, and they come into existence when the recursive process stops
		//check if there's nothing left in the set to provide information
		if(set.size() == 0)
		{
			DecisionNode terminal = new DecisionNode(set, true);
			return terminal;
		}
		//if everything in the set has the same outcome, it is considered 'pure' and recursing should stop
		if(isPure(set))
		{
			DecisionNode terminal = new DecisionNode(set, true);
			//everything has the same outcome, so just pick the outcome associated with the first data point
			terminal.outcome = set.get(0)[0];
			return terminal;
		}
		//the following for loop finds the highest information gain of the remaining features
		double[] informationGain = calcInformationGain(set, remaining);
		int maxIndex = remaining.get(0);
		for(Integer gainIndex : remaining)
		{
			if(informationGain[gainIndex] > informationGain[maxIndex])
			{
				maxIndex = gainIndex;
			}
		}
		//if the current set has no information gain, stop recursing further
		if(informationGain[maxIndex] <= 0)
		{
			DecisionNode terminal = new DecisionNode(set, true);
			terminal.outcome = findMajorityOutcome(set);
			return terminal;
		}
		//copy the remaining features for each node. Each recursive step needs its own list.
		//be sure to remove the index used to split the set into subgroups
		ArrayList<Integer> remainingCopy = new ArrayList<Integer>();
		remainingCopy.addAll(remaining);
		remainingCopy.remove(remainingCopy.indexOf(maxIndex));
		//build the parent node
		DecisionNode parent = new DecisionNode(set, false);
		parent.feature = maxIndex;
		parent.outcome = findMajorityOutcome(set);
		ArrayList<ArrayList<int[]>> subgroups = getSubgroups(set, maxIndex);
		int counter = 0;
		//go through each subgroup to recursively find the children nodes for the parent
		for(ArrayList<int[]> group : subgroups)
		{
			//build the children and define how to arrive there from the parent
			DecisionNode decision = build(group, remainingCopy);
			decision.decision = counter;
			if(group.size() == 0)
			{
				decision.outcome = findMajorityOutcome(set);
			}
			parent.children.add(decision);
			counter++;
		}
		return parent;
	}
	/**
	 * When a non-pure terminal is found, this method finds the majority outcome for the set
	 * @param set that holds some data
	 * @return which outcome makes up the majority for the set
	 */
	private int findMajorityOutcome(ArrayList<int[]> set)
	{
		//go through all the possible outcomes and find how many times each one appears in the set
		int[] features = new int[featureSizes[0]];
		for(int[] member : set)
		{
			features[member[0]]++;
		}
		int highest = 0;
		//a loop to find which outcome appears the most frequently
		for(int i = 0; i < features.length; i++)
		{
			if(features[highest] < features[i])
			{
				highest = i;
			}
		}
		return highest;
	}
	/**
	 * A method that finds the information gain for each remaining feature in a set
	 * @param set that holds some data
	 * @param remaining features to use in finding subgroups
	 * @return an array that holds the information gain for each remaining feature 
	 */
	private double[] calcInformationGain(ArrayList<int[]> set, ArrayList<Integer> remaining)
	{
		double[] informationGain = new double[featureSizes.length];
		//go through all features, find its information gain
		for(int i = 1; i < informationGain.length; i++)
		{
			if(!remaining.contains(i))
			{
				continue;
			}
			double gain = calcEntropy(set);
			ArrayList<ArrayList<int[]>> subgroups = getSubgroups(set, i);
			//go through all the subgroups of the feature and calculate its entropy
			for(ArrayList<int[]> group : subgroups)
			{
				//don't calucate entropy of group that doesn't have any members.
				if(group.size() == 0) 
				{
					 continue;
				}
				gain -= ((double) group.size() / (double) set.size()) * calcEntropy(group);
			}
			informationGain[i] = gain;
		}
		return informationGain;
	}
	/**
	 * A method to determine if a set has all the same outcome
	 * @param set that holds some data
	 * @return whether or not a set is pure
	 */
	private boolean isPure(ArrayList<int[]> set)
	{
		int outcome = set.get(0)[0];
		for(int[] member : set)
		{
			//the moment an outcome differs from the first outcome, you know the set isn't pure so return false
			if(member[0] != outcome)
			{
				return false;
			}
		}
		return true;
	}
	/**
	 * A method that calculates the entropy of a given set
	 * @param set of data
	 * @return set's entropy
	 */
	private double calcEntropy(ArrayList<int[]> set)
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
	/**
	 * A method that splits a set into subgroups based on a feature
	 * @return the list of subgroups
	 */
	private ArrayList<ArrayList<int[]>> getSubgroups(ArrayList<int[]> set, int feature)
	{
		int featureSize = featureSizes[feature];
		ArrayList<ArrayList<int[]>> subgroups = new ArrayList<ArrayList<int[]>>();
		//go through all the possible choices for a given feature and create a fresh array list
		for(int i = 0; i < featureSize; i++)
		{
			subgroups.add(new ArrayList<int[]>());
		}
		//add a particular data point to a subgroup depending on its decision for a given feature
		for(int[] member : set)
		{
			subgroups.get(member[feature]).add(member);
		}
		return subgroups;
	}
	/**
	 * This method finds the expected outcome of an inputted data point
	 * @param a single datapoint  
	 * @return expected output of the datapoint
	 */
	public int findOutcome(int[] datapoint)
	{
		DecisionNode visiting = root;
		//continue traversing the tree until a terminal node is found, then return the node's outcome
		while(!visiting.terminal)
		{
			visiting = visiting.children.get(datapoint[visiting.feature]);
		}
		return visiting.outcome;
	}
	/**
	 * Helper function to call the tune method from public
	 */
	public void tune()
	{
		tune(root);
	}
	/**
	 * Recursively tune the decision tree
	 * @param node to turn
	 */
	private void tune(DecisionNode node)
	{
		//recurse down to leaves (leafs?) and work up.
		for(DecisionNode child : node.children)
		{
			if(child.terminal)
			{
				continue;
			}
			tune(child);
		}
		//get the tree accuracy before and after making the current node a terminal
		double treeAccuracyBefore = treeAccuracy();
		node.terminal = true;
		double treeAccuracyAfter = treeAccuracy();
		//if setting the node as a terminal makes the tree perform worse, then restore it
		if(treeAccuracyBefore > treeAccuracyAfter)
		{
			node.terminal = false;
		}
	}
	/**
	 * A method that determines how well a tree performs based on a given set
	 * @return performance on given set
	 */
	public double treeAccuracy(ArrayList<int[]> set)
	{
		double correct = 0;
		for(int[] group : set) 
		{
			//if expected outcome matches actual outcome, then it's considered correct
			if(group[0] == findOutcome(group))
			{
				correct++;
			}
		}
		//ratio of data points it predicted correctly vs. total data points
		return correct / set.size();
	}
	/**
	 * Determine how well a tree performs with the tuning set
	 * @return performance with tuning set
	 */
	public double treeAccuracy()
	{
		return treeAccuracy(this.tuneSet);
	}
	/**
	 * Log in base two to calculate entropy
	 * @param input value to log
	 * @return resulting value
	 */
	private double log2(double x)
	{
		return Math.log(x)/Math.log(2.0d);
	}
	/**
	 * Main method to start the program
	 * @param args command line arguments
	 */
	public static void main(String[] args)
	{
		/*
		 * After parsing the data, do the following (for the sake of homework grading)
		 * 1) Print out tuned tree (with an interval of four)
		 * 2) Test how the tree performs by using leave-one-out cross validation
		 */
		VoteDataManager manager = new VoteDataManager("voting-data.tsv");
		DataSet data = manager.parseData();
		DecisionTree tree = new DecisionTree(data, 4);
		tree.tune();
		System.out.println("~~~~~~~~~~~~~~ Tree after tuning ~~~~~~~~~~~~~~");
		manager.printDecisionTree(tree);
		double correct = 0;
		//leave-one-out cross validation
		for(int i = 0; i < data.data.size(); i++)
		{
			//make an array list holds everything but the data member at i
			ArrayList<int[]> testData = new ArrayList<int[]>();
			testData.addAll(data.data);
			int[] testVoter = testData.remove(i);
			DecisionTree testTree = new DecisionTree(new DataSet(testData, data.featureSizes), 4);
			testTree.tune();
			//test to see if outcome matches expected result
			if(testTree.findOutcome(testVoter) == testVoter[0])
			{
				correct++;
			}
		}
		System.out.println("~~~~~~~~~~~~~~ Leave-one-out cross-validation ~~~~~~~~~~~~~~");
		System.out.println("Result: " + (correct / data.data.size()) * 100 + "% accuracy.");
	}
}