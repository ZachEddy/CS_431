public class HumanConnectFourPlayer implements ConnectFourPlayer {
    private byte side; // -1 or 1, depending on which side this is
    private ConnectFour game; // pointer back to the game, to control graphics
    
    /**
     * Constructor for a human player.
     * @param side -1 or 1, depending on which player this is
     * @param game a pointer back to the ConnectFour object
     */
    public HumanConnectFourPlayer(byte side, ConnectFour game) {
	this.side = side;
	this.game = game;
    }

    /**
     * Given the current rack, which column should be played? Note that this
     * version uses the game's mouse handler to allow the human player to decide
     * on a column.
     * @param rack The current game board. It is column-major, with column 0
     * being the left column and row 0 being the top row. 0 indicates an empty
     * space, and the two colors are -1 and 1.
     * @return The column to drop a token into.
     */
    public int getNextPlay(byte[][] rack) {
	MouseHandler mouseHandler = game.getMouseHandler();
	int columnWidth = game.getColumnWidth();
	mouseHandler.clearAll();

	// display the hovering token 1st thing
	int x = mouseHandler.getPositionX();
	if (x == -1) game.hoverToken(-1, side);
	else {
	    int hoveringColumn = x/columnWidth;
	    if (isColumnPlayable(hoveringColumn, rack)) game.hoverToken(hoveringColumn, side);
	    else game.hoverToken(-1, side);
	}
	
	// loop around forever, until the player does something
	while (true) {
	    mouseHandler.waitForMouse();

	    // check to make sure the user didn't close the window
	    if (mouseHandler.isShutDown()) return -1;

	    // if there was a click, grab it as the chosen play
	    if (mouseHandler.isClick()) {
		x = mouseHandler.getClickX();
		mouseHandler.clearClick();

		// only return it if it's valid
		int choice = x/columnWidth;
		if (isColumnPlayable(choice, rack)) {
		    game.hoverToken(-1, side);
		    return choice;
		}
	    }

	    // otherwise, just check the position, to update the hover
	    else {
		x = mouseHandler.getPositionX();

		if (x == -1) game.hoverToken(-1, side);
		else {
		    int hoveringColumn = x/columnWidth;
		    if (isColumnPlayable(hoveringColumn, rack)) game.hoverToken(hoveringColumn, side);
		    else game.hoverToken(-1, side);
		}
	    }
	}
    }

    // can this column be played?
    private static boolean isColumnPlayable(int column, byte[][] rack) {
	return (rack[column][0] == 0);
    }
}