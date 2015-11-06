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
			this.feature = -50;
			this.decision = -2;
			this.outcome = -3;
		}
}