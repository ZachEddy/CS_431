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

    private byte side;
    private byte opponentSide; 
    private int level;
    /** 
     * Constructor for the computer player.
     * @param level the number of plies to look ahead
     * @param side -1 or 1, depending on which player this is
     */
    public ComputerConnectFourPlayer(int level, byte side)
    {
        this.side = side;
        this.opponentSide = (byte)(side * -1);
        this.level = level;
        System.out.println(side);
    }

    /** 
     * This computer is stupid. It always plays as far to the left as possible.
     * @param rack the current rack
     * @return the column to play
     */
    public int getNextPlay(byte[][] rack)
    {
        // Node node = new Node(rack, this.side, true);
        // System.out.println(side + "START" + opponentSide);
        // System.out.println(node.evaluation());


        // byte[][] test = new byte[7][6];

        // byte[] one = new byte[]{0,0,0,-1,-1,-1,-1};
        // byte[] two = new byte[]{0,0,0,0,0,0,0};
        // byte[] three = new byte[]{0,0,0,0,0,0,0};
        // byte[] four = new byte[]{0,0,0,0,0,0,0};
        // byte[] five = new byte[]{0,0,0,0,0,0,0};
        // byte[] six = new byte[]{0,0,0,0,0,0,0};
        // byte[] seven = new byte[]{1,1,1,1,0,0,0};

        // test[0] = one;
        // test[1] = two;
        // test[2] = three;
        // test[3] = four;
        // test[4] = five;
        // test[5] = six;
        // test[6] = seven;

        // System.out.println(node.evaluation(test, false));

        // int currentBest = 0;
        // int currentMove = 1;
        return miniMaxNew(rack, level);
    }

    
    public int miniMaxNew(byte[][] rack, int level)
    {
        HashMap<byte[][], Integer> childrenMoves = getMoves(rack, this.side);
        ArrayList<int[]> moves = new ArrayList<int[]>();


        for(byte[][] move : childrenMoves.keySet())
        {
            System.out.println(evaluation(move));

            int height = rack[0].length;
            int width = rack.length;

            for(int column = 0; column < width; column++)
            {
                for(int row = 0; row < height; row++)
                {
                    // System.out.print(move[column][row]);
                }
                // System.out.println();
            }


            int evaluation = miniMaxNew(move, level - 1, false);
            int moveVal = childrenMoves.get(move);
            moves.add(new int[]{evaluation, moveVal});
        }
        int[] currentBest = moves.get(0);
        for(int[] move : moves)
        {
            if(move[0] >= currentBest[0])
                currentBest = move;



            System.out.println(move[0] + " eval and move "+ move[1]);
        }
        return currentBest[1];
    }

    public int miniMaxNew(byte[][] rack, int level, boolean max)
    {
        int evaluation = evaluation(rack);
        // if(!max)
            // evaluation = evaluation(rack, (byte)(this.side * -1));


        if(level == 0 || evaluation == Integer.MAX_VALUE || evaluation == Integer.MIN_VALUE)
                return evaluation;
        
        if(max)
        {
            HashMap<byte[][], Integer> childrenMoves = getMoves(rack, this.side);
            int toReturn = Integer.MIN_VALUE;
            for(byte[][] child : childrenMoves.keySet())
            {
                int value = miniMaxNew(child, level - 1, false);
                toReturn = Math.max(toReturn, value);
            }
            return toReturn;
        }
        else
        {
            HashMap<byte[][], Integer> childrenMoves = getMoves(rack, (byte)(this.side * -1));
            int toReturn = Integer.MAX_VALUE;
            for(byte[][] child : childrenMoves.keySet())
            {
                int value = miniMaxNew(child, level - 1, true);
                toReturn = Math.min(toReturn, value);
            }
            return toReturn;
        }
    }

    public HashMap<byte[][], Integer> getMoves(byte[][] rack, byte s)
    {
        int height = rack[0].length;
        int width = rack.length;
        HashMap<byte[][], Integer> moves = new HashMap<byte[][], Integer>();

        for(int column = 0; column < width; column++)
        {  
            for(int row = (height-1); row >= 0; row--)
            {
                if(rack[column][row] == 0)
                {
                    byte[][] newMove = copyRack(rack);
                    newMove[column][row] = s;
                    moves.put(newMove, column);
                    break;
                }
            }
        }
        return moves;
    }

    private byte[][] copyRack(byte[][] rack)
    {
        int height = rack[0].length;
        int width = rack.length;

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

    public ArrayList<int[]> diagonalConnections(byte[][] rack)
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
    //START AGAIN

    public int evaluation(byte[][] rack)
    {
        int totalScore = 0;
        ArrayList<int[]> connections = getConnections(rack);
        for(int[] connection : connections)
        {
            int score = evaluateConnection(connection);
            if(score == FOUR_TOKEN || score == FOUR_TOKEN_MIN)
            {
                return score;
            }
            totalScore += score;
        }
        return totalScore;
    }

    private int evaluateConnection(int[] connection)
    {
        int playerCount = 0;
        int opponentCount = 0;
        for(int i = 0; i < connection.length; i++)
        {
            if(connection[i] == side)
            {
                playerCount++;
            }
            if(connection[i] == opponentSide)
            {
                opponentCount++;
            }
        }
        if(playerCount == 0 && opponentCount == 0)
            return 0;
        if(playerCount > 0 && opponentCount > 0)
            return 0;
        if(playerCount > 0)
        {
            return connectionScorePlayer(playerCount);
        }
        return connectionScoreOpponent(opponentCount);
    }

    private int connectionScore(int tokenCount)
    {
        if(tokenCount == 0)
            return 0;
        if(tokenCount == 1)
            return ONE_TOKEN;
        if(tokenCount == 2)
            return TWO_TOKEN;
        if(tokenCount == 3)
            return THREE_TOKEN;
        System.out.println("YEAH!" + side);
        return FOUR_TOKEN;
    }

    private int connectionScorePlayer(int tokenCount)
    {
        return connectionScore(tokenCount);
    }

    private int connectionScoreOpponent(int tokenCount)
    {
        int score = connectionScore(tokenCount);

        if(score == FOUR_TOKEN)
        {
            System.out.println("SDFJKLSDFJKLDFSJKLSDFJKLDFS");
            return FOUR_TOKEN_MIN;
        }
        return score * -1;
    }

    public ArrayList<int[]> getConnections(byte[][] rack)
    {
        ArrayList<int[]> connections = new ArrayList<int[]>();
        connections.addAll(diagonalConnections(rack));
        connections.addAll(horizontalConnections(rack));
        connections.addAll(verticalConnections(rack));
        return connections;
    }

    private ArrayList<int[]>  verticalConnections(byte[][] rack)
    {
        int height = rack[0].length;
        int width = rack.length;
        ArrayList<int[]> connections = new ArrayList<int[]>();

        for(int column = 0; column < width; column++)
        {
            int counter = 0;
            while((counter + 4) <= height)
            {
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

    private ArrayList<int[]>  horizontalConnections(byte[][] rack)
    {
        int height = rack[0].length;
        int width = rack.length;
        ArrayList<int[]> connections = new ArrayList<int[]>();

        for(int row = 0; row < height; row++)
        {
            int counter = 0;
            while((counter + 4) <= width)
            {
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
}
