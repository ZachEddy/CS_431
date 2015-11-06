import java.util.ArrayList;
/**
 * A decision node object used for each level in the decision tree
 * @author Zach Eddy
 */
class DecisionNode
{
		ArrayList<DecisionNode> children;
		ArrayList<int[]> set;
		boolean terminal;
		int feature;
		int outcome;
		int decision;
		
		/**
		 * Constructor
		 * @param  set of data the node contains
		 * @param  terminal whether or not the node is a leaf
		 */
		public DecisionNode(ArrayList<int[]> set, boolean terminal)
		{
			children = new ArrayList<DecisionNode>();
			this.terminal = terminal;
			this.set = set;
			//these should all get set during the decision tree's construction
			//if -1 ever appears in the printed tree, then something went wrong.
			this.feature = -1;
			this.decision = -1;
			this.outcome = -1;
		}
}