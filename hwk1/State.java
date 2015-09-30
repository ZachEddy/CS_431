import java.lang.Math;
import java.util.Arrays;
import java.util.List;


/**
 * This class holds the information about a single state of a slide puzzle board
 * @author Zach Eddy
 */
class State implements Comparable<State>
{
	int g_distance; //g[x]
	int h_distance; //h[x]
	int f_distance; //f[x]
	byte[][] board;
	byte width;
	byte height;
	byte gapRow;
	byte gapColumn;
	int hashCode;

	String direction;

	/**
	 * @param the 2D array of integers that contains the positions of each tile on the puzzle board
	 */
	public State(byte[][] board)
	{	
		this.width = (byte) board[0].length;
		this.height = (byte) board.length;
		this.board = board;
		this.g_distance = 0;
		this.h_distance = heuristicDistance(board);
		this.f_distance = this.g_distance + this.h_distance;  
	}

	/**
	 * @param an existing state
	 * @param the direction the previous state took to arrive at this new state
	 */
	public State(State state, String direction)
	{
		this.width = state.width;
		this.height = state.height;
		this.board = copyArray(state.board); //don't mess with the old board (Java is pass by reference)
		this.gapRow = state.gapRow;
		this.gapColumn = state.gapColumn;
		this.direction = direction;
		modifyBoard();
		this.g_distance = state.g_distance + 1;
		this.h_distance = heuristicDistance(this.board);
		this.f_distance = this.g_distance + this.h_distance;
	}

	public void modifyBoard()
	{
		if(direction.equals("D")) //slide down
		{
			byte number = this.board[gapRow-1][gapColumn]; //find number above gap
			this.board[gapRow][gapColumn] = number; //swap nums
			this.board[gapRow-1][gapColumn] = 0; //swap nums
			this.gapRow = (byte) (this.gapRow - 1); //change position of gap
			return; //other if statements are unnecessary
		}
		if(direction.equals("U")) //slide up
		{
			byte number = this.board[gapRow+1][gapColumn];
			this.board[gapRow][gapColumn] = number; 
			this.board[gapRow+1][gapColumn] = 0;
			this.gapRow = (byte) (this.gapRow + 1);
			return;
		}
		if(direction.equals("R")) //slide right
		{
			byte number = this.board[gapRow][gapColumn-1];
			this.board[gapRow][gapColumn] = number; 
			this.board[gapRow][gapColumn-1] = 0;
			this.gapColumn = (byte) (this.gapColumn - 1);
			return;
		}
		if(direction.equals("L")) //slide left
		{
			byte number = this.board[gapRow][gapColumn + 1];
			this.board[gapRow][gapColumn] = number; 
			this.board[gapRow][gapColumn+1] = 0;
			this.gapColumn = (byte) (this.gapColumn + 1);
			return;
		}
	}

	/**
	 * Find the gap of a puzzle board (where the blank is)
	 */
	public void gap()
	{
		//iterate through the whole board
		for(byte i = 0; i < this.height; i++)
		{
			for(byte j = 0; j < this.width; j++)
			{
				if(this.board[i][j] != 0)
					continue;
				this.gapRow = i;
				this.gapColumn = j;
				break; //break when gap is found
			}
		}
	}

	/**
	 * @param array to be copied
	 * @return new array
	 */
	public byte[][] copyArray(byte[][] array)
	{
		byte[][] copy = new byte[array.length][array[0].length];
		for(int i = 0; i < this.height; i++)
		{
			for(int j = 0; j < this.width; j++)
			{
				copy[i][j] = array[i][j]; //iterate through whole array and place value of original array into same position of new array
			}
		}
		return copy;
	}

	/**
	 * @param puzzle board
	 * @return total distance the puzzle pieces need to slide (assuming they can slide through other tiles)
	 */
	public int heuristicDistance(byte[][] board)
	{
		int distance = 0;
		for(int i = 0; i < this.height; i++)
		{
			for(int j = 0; j < this.width; j++)
			{
				int number = (board[i][j] - 1); //the following operations require zero indexing
				if(number < 0) continue;

				//determine where a number should be (requires zero indexing) in terms of rows and columns
				int colPlacement = number / width;
				int rowPlacement = number - (colPlacement * width);
				distance += Math.abs(colPlacement - i) + Math.abs(rowPlacement - j); //calculate the manhatten distance
			}
		}
		return distance;
	}

	/**
	 * @return the list of neighbors
	 */
	public List<State> getNeighbors()
	{
		//basically an array (any given position has a max of 4 neighbors), but has the for-each loop functionality. Kinda handy.
		List<State> neighbors = Arrays.asList(new State[4]); 

		//the following if statements are all for bounds checking. A neighbor cannot exist outside the board.
		if(gapRow > 0) //slide down
		{	
			State neighbor = new State(this, "D");
			neighbors.set(0, neighbor);
		}
		if(gapRow < height - 1) //slide up
		{
			State neighbor = new State(this, "U");
			neighbors.set(1, neighbor);
		}
		if(gapColumn > 0) //slide right
		{
			State neighbor = new State(this, "R");
			neighbors.set(2, neighbor);
		}
		if(gapColumn < width - 1) //slide left
		{			
			State neighbor = new State(this, "L");
			neighbors.set(3, neighbor);
		}
		return neighbors; //return the neighbors *SOME* of them may be null -- that explains the null check in A*
	}

	/**
	 * @return the hashcode of the puzzle
	 */
	public int hashCode()
	{

		//I store the hashcode as an instance var (slightly faster). An uninitialized int is 0, which would mean this function hasn't been called yet.
		if(this.hashCode != 0) //if it's something other than 0, 
			return this.hashCode;

		hashCode = Arrays.deepHashCode(this.board);
		if(hashCode == 0) //if indeed the hash code is 0 (unlikely), just set it to -1 so the program doesn't break
			hashCode = -1;
		return hashCode;
	}

	public boolean equals(Object obj)
	{
		boolean equal = false; 
		if(obj instanceof State) //if the object isn't a State, then obviously they won't be equal
		{
			State s = (State) obj;

			//compare all the numbers on the puzzle to make sure they're the same
			for(int i = 0; i < this.height; i++)
			{
				for(int j = 0; j < this.width; j++)
				{
					if(s.board[i][j] != this.board[i][j])
						return false;
				}
			}
			equal = s.g_distance == this.g_distance; //if they're the same, compare the g_distance (distance travelled so far)
		}
		return equal;
	}

	/**
	 * @param another State
	 * @return the difference between the f_distances of two given states
	 */
	public int compareTo(State other)
	{
		return this.f_distance - other.f_distance;
	}
}