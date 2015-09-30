class ComputerConnectFourPlayer implements ConnectFourPlayer {
    /** 
     * Constructor for the computer player.
     * @param level the number of plies to look ahead
     * @param side -1 or 1, depending on which player this is
     */
    public ComputerConnectFourPlayer(int level, byte side) {

    }

    /** 
     * This computer is stupid. It always plays as far to the left as possible.
     * @param rack the current rack
     * @return the column to play
     */
    public int getNextPlay(byte[][] rack) {
	for (int i=0; i<rack.length; i++) {
	    if (rack[i][0] == 0) return i;
	}
	return -1; // shouldn't happen
    }
}