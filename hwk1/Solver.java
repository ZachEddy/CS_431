import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.HashSet;

/**
 * This class solves a sliding puzzle when given a 2D array of tile positions
 * @author Zach Eddy
 */
class Solver
{
	/**
	 * @param the 2D array of integers that contains the positions of each tile on the puzzle board
	 * @return the sequence of movements represented as a string to complete the puzzle
	 */
	public static String solvePuzzle(int[][] b)
	{	
		PriorityQueue<State> openList = new PriorityQueue<State>(); //priority queue to easy get the best state
		HashSet<State> openListChecker = new HashSet<State>(); //a list to easily see if a state is contained within the open list (priority queue is linear time)
		HashSet<State> closedList = new HashSet<State>(); //closed list to keep track of explored nodes
		HashMap<State, State> path = new HashMap<State, State>(); //the path from the starting state to the goal state
		
		// if(!isSolvable(b)) //if the state is not solvable, don't bother trying to solve it
		// {
		// 	return null;
		// }

		//convert to array of bytes (saves space)
		byte[][] board = new byte[b.length][b[0].length]; 
		for(int i = 0; i < board.length; i++)
		{
			for(int j = 0; j < board[0].length; j++)
			{
				board[i][j] = (byte) b[i][j];
			}
		}

		State start = new State(board);
		start.gap(); //find the location of the '.' (zero) within the board
		if(!isSolvable(start)) //if the state is not solvable, don't bother trying to solve it
		{
			return null;
		}

		openList.add(start);
		int counter = 0;
		while(!openList.isEmpty()) //keep going while there are still nodes to be explored
		{	
			State active = openList.poll();
			
			if(active.h_distance == 0) //goal nodes has been found when h(x) is zero
			{
				String finish = active.direction;
				State next = path.get(active);	
				while(next.direction != null) //the starting state doesn't have a direction specified (it's null)
				{	
					//compute the sequences of movements from start to finish
					finish = next.direction + finish;
					next = path.get(next);
				}
				return finish;
			}

			
			List<State> neighbors = active.getNeighbors();
			for(State neighbor : neighbors) //explore all the neighbors
			{	
				if(closedList.contains(neighbor) || neighbor == null) continue; //don't bother if it has already been explored
				// int totalDistance = active.g_distance + 1;
				if(!openListChecker.contains(neighbor) || (active.g_distance + 1) < neighbor.g_distance)
				{
					//if the node hasn't been explored, add it to the open list and connect it to the path
					path.put(neighbor, active);
					openList.add(neighbor);
					openListChecker.add(neighbor);
				}
			}
			closedList.add(active);
		}
		return null;
	}

	/**
	 * @param puzzle board
	 * @return whether the board is solvable or not
	 */
	private static boolean isSolvable(State state)
	{
		byte[][] board = state.board;
		int height = board.length;
		int width = board[0].length;
		int[] numList = new int[height * width];
		int counter = 0;
		
		//determine whether a board is solvable or not by counting how many tiles exist before a given tile are of lower value
		for(byte i = 0; i < state.height; i++)
		{
			for(byte j = 0; j < state.width; j++)
			{	
				int check = board[i][j];
				if(check == 0)
				{
					check = height * width;
				}
				numList[(width * i) + j] = check;

				for(int k = 0; k < numList.length; k++) //see how many tiles before current tile are smaller
				{
					if(numList[k] > check) counter++;  //if a given tile is greater than the current tile, increment counter
				}
			}
		}
		return (counter + state.gapRow + state.gapColumn + height + width) % 2 == 0; //return the parity
	}
}
