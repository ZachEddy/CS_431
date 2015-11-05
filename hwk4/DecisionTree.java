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
		// this.parser = parser;
		this.testSet = dataSet.testSet;
		this.tuneSet = dataSet.tuneSet;
		this.featureSizes = dataSet.featureSizes;
		root = constructTree(testSet);
		
		

		
		// System.out.println(treeAccuracy(root.get(0)));

		tuneTree(root);
		// double correct = 0;
		// for(int[] group : tuneSet) 
		// {
			
		// 	if(group[0] == findOutcome(group, root))
		// 		correct++;
			


		// 	// System.out.println(group[0] == findOutcome(group));
		// }
		// System.out.println((correct/tuneSet.size()) * 100);

		// System.out.println(findOutcome(tuneSet.get(1)));
	}

	public DecisionNode constructTree(ArrayList<int[]> set)
	{
		// boolean[] visited = new boolean[categorySizes.length]; //defaults to false
		System.out.println(set == null);
		ArrayList<Integer> visited = new ArrayList<Integer>();
		for(int i = 1; i < featureSizes.length; i++)
			visited.add(i);
		return constructTree(set, visited);
	}


	// public DecisionNode constructTree(ArrayList<int[]> set, ArrayList<Integer> visited)
	// {
	// 	// System.out.println(visited);
	// 	DecisionNode node = new DecisionNode(set, true);
	// 	if(isPure(set))
	// 	{
	// 		node.decision = set.get(0)[0];
	// 		node.terminal = true;
	// 		return node;

	// 	}
	// 	double[] informationGain = calcInformationGain(set, visited);
	// 	int maxIndex = visited.get(0);
	// 	for(Integer i : visited)
	// 	{
	// 		if(informationGain[i] > informationGain[maxIndex])
	// 			maxIndex = i;
	// 	}
	// 	if(informationGain[maxIndex] == 0)
	// 	{
	// 		node.terminal = true;
	// 		return node;
	// 	}
	// 	ArrayList<ArrayList<int[]>> subgroups = getSubgroups(set, maxIndex);
	// 	ArrayList<Integer> newVisited = new ArrayList<Integer>();
	// 	newVisited.addAll(visited);
	// 	newVisited.remove(newVisited.indexOf(maxIndex));
	// 	int counter = 0;
	// 	for(ArrayList<int[]> group : subgroups)
	// 	{
	// 		if(group.size() == 0)
	// 		{
	// 			node.terminal = true;
	// 			return node;
	// 		}
	// 		DecisionNode childNode = constructTree(group, newVisited);
	// 		childNode.decision = counter;
	// 		childNode.terminal = false;
	// 		childNode.category = maxIndex;
	// 		node.children.add(childNode);
	// 		counter++;
	// 	}
	// 	return node;




	// 	// DecisionNode node = new DecisionNode(set, false);
	// 	// double[] informationGain = calcInformationGain(set, visited);
	// 	// int maxIndex = visited.get(0);
	// 	// for(Integer i : visited)
	// 	// {
	// 	// 	if(informationGain[i] > informationGain[maxIndex])
	// 	// 		maxIndex = i;
	// 	// }
	// 	// node.category = maxIndex;
	// 	// if(informationGain[maxIndex] == 0)
	// 	// {
	// 	// 	// System.out.println("Stopped because informaiton gain is zero");
	// 	// 	return node;
	// 	// }
	// 	// ArrayList<ArrayList<int[]>> subgroups = getSubgroups(set, maxIndex);
	// 	// ArrayList<Integer> newVisited = new ArrayList<Integer>();
	// 	// newVisited.addAll(visited);
	// 	// newVisited.remove(newVisited.indexOf(maxIndex));
		
	// 	// int counter = 0;
	// 	// for(ArrayList<int[]> group : subgroups)
	// 	// {
	// 	// 	// node.decision = counter;
			
	// 	// 	if(group.size() == 0)
	// 	// 	{
	// 	// 		// DecisionNode childNode = new DecisionNode(group, true);
	// 	// 		// childNode.decision = counter;
	// 	// 		// node.children.add(childNode);
	// 	// 		return node;
	// 	// 	}
	// 	// 	DecisionNode childNode = constructTree(group, newVisited);
	// 	// 	childNode.decision = counter;

	// 	// 	node.children.add(childNode);

	// 	// 	counter++;
	// 	// }
	// 	// return node;
	// }




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
		for(Integer i : visited)
		{
			if(informationGain[i] > informationGain[maxIndex])
			{
				maxIndex = i;
			}
		}
		if(informationGain[maxIndex] <= 0)
		{
			DecisionNode terminal = new DecisionNode(set, true);
			terminal.outcome = findMajorityFeature(set);
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
				decision.outcome = findMajorityFeature(set);
			}
			parent.children.add(decision);
			counter++;
		}
		return parent;


		// if(isPure(set))
		// {
		// 	DecisionNode node = new DecisionNode(set, true);
		// 	node.terminal = true;
		// 	node.decision = set.get(0)[0];
		// 	return node;
		// }
		// DecisionNode node = new DecisionNode(set, false);
		// double[] informationGain = calcInformationGain(set, visited);
		// int maxIndex = visited.get(0);
		// for(Integer i : visited)
		// {
		// 	if(informationGain[i] > informationGain[maxIndex])
		// 	{
		// 		maxIndex = i;
		// 	}
		// }
		// node.category = maxIndex;
		// if(informationGain[maxIndex] <= 0)
		// {
		// 	node.terminal = true;
		// 	return node;
		// }
		// ArrayList<ArrayList<int[]>> subgroups = getSubgroups(set, maxIndex);
		// ArrayList<Integer> newVisited = new ArrayList<Integer>();
		// newVisited.addAll(visited);
		// newVisited.remove(newVisited.indexOf(maxIndex));
		// int counter = 0;
		// for(ArrayList<int[]> group : subgroups)
		// {
		// 	if(group.size() == 0)
		// 	{

		// 		//find parent majority
		// 		node.decision = counter;
		// 		node.terminal = true;
		// 		return node;
		// 	}
		// 	DecisionNode childNode = constructTree(group, newVisited);
		// 	childNode.decision = counter;
		// 	node.terminal = false;
		// 	node.children.add(childNode);
		// 	counter++;
		// }
		// return node;
	}
		// for(int i = 0; i < informationGain.length; i++)
		// {
		// 	if(!visited.contains(i) && informationGain[i] == 0)
		// 	{



		// 		// System.out.println("hello world!");
		// 		// return null;
		// 	}



		// }
		// int category = maxGainIndex(informationGain);
		// ArrayList<Integer> v = new ArrayList<Integer>();
		// v.addAll(visited);
		// // visited.add(category);

		// ArrayList<ArrayList<int[]>> subgroups = getSubgroups(set, category);
		// node.decision = category;
			
		// for(ArrayList<int[]> group : subgroups)
		// {
		// 	if(group.size() == 0)
		// 	{
		// 		// for()


		// 		// node.decision = 
		// 		continue;
		// 	}
			

		// 	System.out.println("Category: " + category);
		// 	System.out.println("Size: " + group.size());
		// 	node.children.add(constructTree(group, visited));
			
		// }
		// System.out.println();
		// return node;
	// }

	public int findMajorityFeature(ArrayList<int[]> set)
	{
		int[] features = new int[featureSizes[0]];
		// System.out.println(categorySizes[0] + "!!!!");
		for(int[] member : set)
		{
			features[member[0]]++;
		}
		int highest = 0;
		


		// System.out.println(Arrays.toString(features));
		for(int i = 0; i < features.length; i++)
		{
			if(features[highest] > features[i])
				highest = i;
		}
		// System.out.println(highest + ":):)");

		return highest;
	}



	public int maxGainIndex(double[] informationGain)
	{
		int currentMax = 1;
		for(int i = 1; i < informationGain.length; i++)
		{
			if(informationGain[i] > informationGain[currentMax])
			{
				currentMax = i;
			}
		}
		return currentMax;
	}

	public double[] calcInformationGain(ArrayList<int[]> set, ArrayList<Integer> visited)
	{
		double[] informationGain = new double[featureSizes.length];
		informationGain[0] = Double.MIN_VALUE;
		//go through all features, find its information gain
		// visited[1] = true;
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
				double entropy = calcEntropy(group);
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
				return false;
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

	/**
	 * TODO -- change this to a HashMap of array lists to ints. This will allow you control which decision corresponds to a certain subgroup
	 */
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

	public DecisionNode tuneTree(DecisionNode root, boolean whatever)
	{
		return null;
	}

	public DecisionNode tuneTree(DecisionNode node)
	{
		if(node.terminal)
		{
			return node;
		}
		else
		{
			for(DecisionNode child : node.children)
			{

				child.


				return tuneTree(child);

			}
		}
		return null;
	}

	public double treeAccuracy(DecisionNode node)
	{
		double correct = 0;
		for(int[] group : tuneSet) 
		{
			if(group[0] == findOutcome(group, node))
				correct++;
			
			// System.out.println(group[0] == findOutcome(group));
		}
		return correct / tuneSet.size();
	}

	public static void main(String[] args)
	{
		VoteParser parser = new VoteParser("voting-data.tsv");
		DecisionTree tree = new DecisionTree(parser);
		parser.printDecisionTree(tree);
	}
}