import java.util.ArrayList;
class DecisionNode
{
		ArrayList<DecisionNode> children;
		ArrayList<int[]> set;
		boolean terminal;
		int feature;
		int outcome;
		int decision;
		public DecisionNode(ArrayList<int[]> set, boolean terminal)
		{
			children = new ArrayList<DecisionNode>();
			this.terminal = terminal;
			this.set = set;
			this.feature = -1;
			this.decision = -1;
			this.outcome = -1;
		}
}