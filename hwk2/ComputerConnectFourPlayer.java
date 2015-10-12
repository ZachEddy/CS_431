import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Collections;

class ComputerConnectFourPlayer implements ConnectFourPlayer
{
    private final static int ONE_TOKEN = 1;
    private final static int TWO_TOKEN = 10;
    private final static int THREE_TOKEN = 100;
    private final static int FOUR_TOKEN = Integer.MAX_VALUE;
    private final static int FOUR_TOKEN_MIN = Integer.MIN_VALUE;
    private byte computerSide;
    private byte opponentSide; 
    private int level;


    /** 
     * Constructor for the computer player.
     * @param level the number of plies to look ahead
     * @param side -1 or 1, depending on which player this is
     */
    public ComputerConnectFourPlayer(int level, byte side)
    {
        this.computerSide = side;
        this.opponentSide = (byte)(side * -1);
        this.level = level;
    }

    /** 
     * This computer is stupid. It always plays as far to the left as possible.
     * @param rack the current rack
     * @return the column to play
     */
    public int getNextPlay(byte[][] rack)
    {
        return miniMax(rack, level);
    }

    /**
     * Starter method for the recursive MiniMax algorithm
     * @param  rack the Connect Four board
     * @param  level total number of recursive levels
     * @return the best move avaiable
     */
    private int miniMax(byte[][] rack, int level)
    {
        //get list of all moves
        HashMap<byte[][], Integer> moves = getMoves(rack, this.computerSide);
        int miniMaxBest = 0;
        byte[][] currentBest = null;
        //go through all moves and evaluate them with minimax; return the best move
        for(byte[][] move : moves.keySet())
        {   
            int currentMiniMax = miniMax(move, level - 1, false);
            //if a better move is found (or no move is selected), update the return value
            if(currentBest == null || currentMiniMax > miniMaxBest)
            {
                currentBest = move;
                miniMaxBest = currentMiniMax;
            }
        }
        //return the value that corresponds to the best move
        return moves.get(currentBest);
    }

    /**
     * An implementation of the MiniMax algorithm for connect four
     * @param  rack the Connect Four board
     * @param  level current number of recursive levels 
     * @param  max whether min or max is playing currently
     * @return optimatal move that corresponds to the player (min or max)
     */
    private int miniMax(byte[][] rack, int level, boolean max)
    {
        int evaluation = evaluation(rack);
        //if the terminal depth is reached, or the move is game-ending (no sense in continued exploration)
        if(level == 0 || evaluation == Integer.MAX_VALUE || evaluation == Integer.MIN_VALUE)
                return evaluation;
        
        //check which player should be "playing"
        if(max)
        {
            HashMap<byte[][], Integer> childrenMoves = getMoves(rack, this.computerSide);
            int toReturn = Integer.MIN_VALUE;
            //recursively explore all the moves, then return the best value
            for(byte[][] child : childrenMoves.keySet())
            {
                int value = miniMax(child, level - 1, false);
                toReturn = Math.max(toReturn, value);
            }
            return toReturn;
        }
        else
        {
            HashMap<byte[][], Integer> childrenMoves = getMoves(rack, this.opponentSide);
            int toReturn = Integer.MAX_VALUE;
            //recursively explore all the moves, then return the best value
            for(byte[][] child : childrenMoves.keySet())
            {
                int value = miniMax(child, level - 1, true);
                toReturn = Math.min(toReturn, value);
            }
            return toReturn;
        }
    }

    /**
     * A method that finds all the possible moves given a current state of Connect Four
     * @param  rack given Connect Four rack
     * @param  side which player is currently moving (1 or -1 are the two options)
     * @return  hash map of the board and the position that a token was placed in
     */
    private HashMap<byte[][], Integer> getMoves(byte[][] rack, byte side)
    {
        int height = rack[0].length;
        int width = rack.length;
        HashMap<byte[][], Integer> moves = new HashMap<byte[][], Integer>();
        for(int column = 0; column < width; column++)
        {  
            for(int row = (height-1); row >= 0; row--)
            {
                //if a token can be placed, create the board that matches
                if(rack[column][row] == 0)
                {
                    byte[][] newMove = copyRack(rack);
                    newMove[column][row] = side;
                    moves.put(newMove, column);
                    break;
                }
            }
        }
        return moves;
    }

    /**
     * This method copies a rack because Java is pass by reference, not pass by value
     * @param  rack given Connect Four rack
     * @return copy of the rack
     */
    private byte[][] copyRack(byte[][] rack)
    {
        int height = rack[0].length;
        int width = rack.length;
        
        //go through all rows and columns, add the values at the [row][column] to the copy rack
        byte[][] newRack = new byte[width][height];
        for(int column = 0; column < width; column++)
        {
            for(int row = 0; row < height; row++)
            {
                newRack[column][row] = rack[column][row];
            }
        }
        return newRack;
    }

    /**
     * This method is the evaluation function for a connect four rack
     * @param  rack given Connect Four rack
     * @return return the evalaluation of a rack
     */
    private int evaluation(byte[][] rack)
    {
        int totalScore = 0;
        //find all the connections in the rack (all possible four-in-a-row) of which there are 69 and sum their scores
        ArrayList<int[]> connections = getConnections(rack);
        for(int[] connection : connections)
        {
            int score = evaluateConnection(connection);
            //endgame move is treated like negative or positive infinity. If you find an endgame move, simply return +- infinity
            if(score == FOUR_TOKEN || score == FOUR_TOKEN_MIN)
                return score;
            //assuming move does not end game, add it to the total score
            totalScore += score;
        }
        return totalScore;
    }

    /**
     * This method evaluates a for-in-a-row connection
     * @param  connection values contained within the connection
     * @return the evaluation of the given conneciton
     */
    private int evaluateConnection(int[] connection)
    {
        int playerCount = 0;
        int opponentCount = 0;

        //sum up the number of tokens each player has in the connection
        for(int i = 0; i < connection.length; i++)
        {
            if(connection[i] == this.computerSide)
                playerCount++;
            if(connection[i] == this.opponentSide)
                opponentCount++;
        }
        //if nobody has tokens placed
        if(playerCount == 0 && opponentCount == 0) 
            return 0;
        //if both players have tokens placed
        if(playerCount > 0 && opponentCount > 0)
            return 0;
        //if it gets to this stage, only one of the two players has tokens placed
        if(playerCount > 0)
            return connectionScorePlayer(playerCount);
        return connectionScoreOpponent(opponentCount);
    }


    /**
     * This method determines the score of any player depending on the amount of tokens they have in a four-in-a-row
     * @param  tokenCount number of tokens they have in a connection
     * @return the score of the connection based on the number of tokens they have contained within it
     */
    private int connectionScore(int tokenCount)
    {
        //return the score that corresponds to a specific number of tokens
        if(tokenCount == 0)
            return 0;
        if(tokenCount == 1)
            return ONE_TOKEN;
        if(tokenCount == 2)
            return TWO_TOKEN;
        if(tokenCount == 3)
            return THREE_TOKEN;
        return FOUR_TOKEN;
    }

    /**
     * This method determines the score of the opposing player depending on the amount of tokens they have in a four-in-a-row
     * @param  tokenCount number of tokens they have in a connection
     * @return the score of the connection based on the number of tokens they have contained within it
     */
    private int connectionScoreOpponent(int tokenCount)
    {
        //just negate the score and return it
        //if it's positive infinity (in which case negating won't work due to how integers work), just return negative inifinity
        int score = connectionScore(tokenCount);
        if(score == FOUR_TOKEN)
            return FOUR_TOKEN_MIN;
        return score * -1;
    }

    /**
     * A wrapper method for the connectionScore method to make the code look nicer
     * @param  tokenCount number of tokens in a connection
     * @return the score of the connections based on the number of tokens
     */
    private int connectionScorePlayer(int tokenCount)
    {
        return connectionScore(tokenCount);
    }

    /**
     * This method finds the all of the four-in-a-row connections on a board
     * @param the Connect Four rack
     * @return the list of connections
     */
    private ArrayList<int[]> getConnections(byte[][] rack)
    {
        //return a list that contains all of the horizontal, vertical, and diagonal connections
        ArrayList<int[]> connections = new ArrayList<int[]>();
        connections.addAll(diagonalConnections(rack));
        connections.addAll(horizontalConnections(rack));
        connections.addAll(verticalConnections(rack));
        return connections;
    }

    /**
     * This method finds all of the vertical connections in a rack
     * @param rack Connect Four rack
     * @return the list of connections
     */
    private ArrayList<int[]>  verticalConnections(byte[][] rack)
    {
        int height = rack[0].length;
        int width = rack.length;
        ArrayList<int[]> connections = new ArrayList<int[]>();

        //iterate through all of the vertical connections in each column
        for(int column = 0; column < width; column++)
        {
            int counter = 0;
            while((counter + 4) <= height)
            {
                //ex: [0][0],[1][0],[2][0],[3][0] (remember that x and y are flipped)
                int[] connection = new int[4];
                connection[0] = rack[column][counter];
                connection[1] = rack[column][counter+1];
                connection[2] = rack[column][counter+2];
                connection[3] = rack[column][counter+3];
                connections.add(connection);
                counter++;
            }
        }
        return connections;
    }

    /**
     * This method finds all of the horizontal connections in a rack
     * @param rack Connect Four rack
     * @return the list of connections
     */
    private ArrayList<int[]>  horizontalConnections(byte[][] rack)
    {
        int height = rack[0].length;
        int width = rack.length;
        ArrayList<int[]> connections = new ArrayList<int[]>();
        //iterate through all of the horizontal connections in each row
        for(int row = 0; row < height; row++)
        {
            int counter = 0;
            while((counter + 4) <= width)
            {
                //ex: [0][0],[0][1],[0][2],[0][3] (remember that x and y are flipped)
                int[] connection = new int[4];
                connection[0] = rack[counter][row];
                connection[1] = rack[counter+1][row];
                connection[2] = rack[counter+2][row];
                connection[3] = rack[counter+3][row];
                connections.add(connection);
                counter++;
            }
        }
        return connections;
    }

    /**
     * This method finds all of the diagonal connections in a rack
     * @param rack Connect Four rack
     * @return the list of connections
     */
    private ArrayList<int[]> diagonalConnections(byte[][] rack)
    {
        int height = rack[0].length;
        int width = rack.length;
        ArrayList<int[]> connections = new ArrayList<int[]>();

        for(int row = 0; row < height; row++)
        {
            if((row + 4) > height)
                break;
            for(int column = 0; column < width; column++)
            {
                //find the diagonal connection, then its mirror image (to find both directions)
                //ex: ([0][0],[1][1],[2][2],[3][3]),([6][0],[5][1],[4][2],[3][3]) (remember that x and y are flipped)
                if((column + 4) > width)
                    break;
                int[] diagonal = new int[4];
                diagonal[0] = rack[column][row];
                diagonal[1] = rack[column+1][row + 1];
                diagonal[2] = rack[column+2][row + 2];
                diagonal[3] = rack[column+3][row + 3];
                connections.add(diagonal);

                int[] diagonalMirror = new int[4];
                int top = height - 1;
                diagonalMirror[0] = rack[column][top - row];
                diagonalMirror[1] = rack[column+1][top - (row+1)];
                diagonalMirror[2] = rack[column+2][top - (row+2)];
                diagonalMirror[3] = rack[column+3][top - (row+3)];
                connections.add(diagonalMirror);
            }
        }
        return connections;
    }
}