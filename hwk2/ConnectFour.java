/**
 * This object is the base Connect Four game. It sets two players against one
 * another, who may be either human or AI. To make a new player type, you must
 * implement the included ConnectFourPlayer interface.
 *
 * DO NOT ALTER THIS FILE. Your AI will be tested with this class, as it stands.
 *
 * @author      Adam A. Smith
 * @version     1.0
 */

import java.util.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;

public class ConnectFour {
    private static final int DEFAULT_WIDTH = 7, DEFAULT_HEIGHT = 6, DEFAULT_SPACE_SIZE = 100;
    private static final double TOKEN_RATIO = 0.9;
    private static final Color RACK_COLOR = new Color(255, 255, 201), BACKGROUND_COLOR = Color.BLACK;
    private static final Color TOKEN1_COLOR = new Color(204, 51, 0), TOKEN2_COLOR = new Color(0, 102, 255);
    //private static final Color TOKEN1_COLOR = new Color(0, 204, 51), TOKEN2_COLOR = new Color(128, 0, 128);
    private static final Stroke THICK_STROKE = new BasicStroke(2), THICKER_STROKE = new BasicStroke(4);

    private static final int HORIZONTAL=1, VERTICAL=2, ASCENDING=4, DESCENDING=8;

    private static final byte SIDE1 = 1, SIDE2 = -1;

    private static final double DROP_RATE = 0.01;

    private final int SPACE_SIZE;
    private byte[][] rack;
    private GraphicsWindow window;
    private MouseHandler mouseHandler;
    private BufferedImage rackSpace;
    private BufferedImage[] tokens;
    private int hoveringToken = -1;

    public static void main(String[] args) {
	ConnectFour c4 = new ConnectFour(DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_SPACE_SIZE);
	int computerLevel = 1;
	if (args.length >= 1) computerLevel = parseInt(args[0]);
	
	c4.playGame(0, computerLevel);
	c4.endGame();
    }

    // constructor
    public ConnectFour(int width, int height, int spaceSize) {
	// graphics & mouse setup
	window = new GraphicsWindow("Connect Four", makeIcon(32), width*spaceSize, (height+1)*spaceSize, 400, 100, true);
	window.paintBackground(BACKGROUND_COLOR);
	mouseHandler = new MouseHandler(window);
	window.addMouseListener(mouseHandler);
	window.addMouseMotionListener(mouseHandler);

	// design the need graphics
	SPACE_SIZE = spaceSize;
	makeGraphics();

	// prep the rack, draw it & display
	rack = new byte[width][height];
	drawFullRack();
	window.flip();
    }

    /** 
     * Starts up a game.
     * @param player1Type 0 for human, postive # for computer. The number designates the number of moves it looks ahead.
     * @param player2Type 0 for human, postive # for computer. The number designates the number of moves it looks ahead.
     */
    public void playGame(int player1Type, int player2Type) {
	// init the players
	ConnectFourPlayer player1, player2, winner = null;
	if (player1Type == 0) player1 = new HumanConnectFourPlayer(SIDE1, this);
	else player1 = new ComputerConnectFourPlayer(player1Type, SIDE1);
	if (player2Type == 0) player2 = new HumanConnectFourPlayer(SIDE2, this);
	else player2 = new ComputerConnectFourPlayer(player2Type, SIDE2);

	// player 1 goes 1st
	ConnectFourPlayer activePlayer = player1;
	byte activeSide = SIDE1;
	int chosenColumn = -1;

	// play the game!
	int numTurns = rack.length*rack[0].length;
	for (int i=0; i<numTurns; i++) {

	    // go!
	    chosenColumn = activePlayer.getNextPlay(copyRack());
	    if (mouseHandler.isShutDown()) return; // end program if window closed
	    if (chosenColumn < 0 || chosenColumn > rack.length) throw new IllegalStateException("Player chose invalid column " +chosenColumn+"!");
	    if (!isColumnPlayable(chosenColumn)) throw new IllegalStateException("Column " +chosenColumn+ " is already full!");
	    dropToken(chosenColumn, activeSide);

	    // test for winning
	    if (justWon(chosenColumn)) {
		winner = activePlayer;
		break;
	    }

	    // switch active player
	    if (activePlayer == player1) activePlayer = player2;
	    else activePlayer = player1;
	    activeSide = (byte)(-activeSide);
	}

	// game's over--draw the appropriate message across the top of the screen
	window.copyBack();
	if (winner == null) {
	    drawMessage("Tie Game", RACK_COLOR);
	}
	else {
	    Color color;
	    if (winner == player1) color = TOKEN1_COLOR;
	    else color = TOKEN2_COLOR;
	    drawMessage("WINNER", color);
	    highlightRow(chosenColumn);
	}

	// finally, display all & wait for the user to close out
	window.flip();
	mouseHandler.clearClick();
	mouseHandler.waitForClick();
    }

    // makes a copy of the rack, to prevent players from accessing it directly
    private byte[][] copyRack() {
	byte[][] copy = new byte[rack.length][rack[0].length];
	for (int i=0; i<rack.length; i++) System.arraycopy(rack[i], 0, copy[i], 0, rack[i].length);
	return copy;
    }

    // tears down the window when we're done
    public void endGame() {
	window.destroy();
    }

    // can this column be played?
    private boolean isColumnPlayable(int column) {
	return (rack[column][0] == 0);
    }

    // just returns true if there's a 4-in-a-row
    public boolean justWon(int columnPlayed) {
	return findWinningAlignment(columnPlayed) > 0;
    }

    // returns the bitwise-or of all alignments of a winning 4-in-a-row
    public int findWinningAlignment(int columnPlayed) {
	int rowPlayed = 0;
	int result = 0;
	while (rack[columnPlayed][rowPlayed] == 0) rowPlayed++;

	byte side = rack[columnPlayed][rowPlayed];

	// horizontal
	int count = 1;
	for (int c=columnPlayed-1; c>=0 && rack[c][rowPlayed]==side; c--) count++;
	for (int c=columnPlayed+1; c<rack.length && rack[c][rowPlayed]==side; c++) count++;
	if (count >= 4) result |= HORIZONTAL;

	// vertical
	count = 1;
	for (int r=rowPlayed+1; r<rack[0].length && rack[columnPlayed][r]==side; r++) count++;
	if (count >= 4) result |= VERTICAL;

	// ascending
	count = 1;
	for (int c=columnPlayed-1,r=rowPlayed+1; c>=0 && r<rack[0].length && rack[c][r]==side; c--,r++) count++;
	for (int c=columnPlayed+1,r=rowPlayed-1; c<rack.length && r>=0 && rack[c][r]==side; c++,r--) count++;
	if (count >= 4) result |= ASCENDING;

	// descending
	count = 1;
	for (int c=columnPlayed-1,r=rowPlayed-1; c>=0 && r>=0 && rack[c][r]==side; c--,r--) count++;
	for (int c=columnPlayed+1,r=rowPlayed+1; c<rack.length && r<rack[0].length && rack[c][r]==side; c++,r++) count++;
	if (count >= 4) result |= DESCENDING;

	return result; // 0 if nothing
    }

    // place a token
    private int dropToken(int column, byte side) {
	if (!isColumnPlayable(column)) return -1;

	// determine how far it drops down
	int row = 0;
	for (row = 0; row < 5; row++) {
	    if (rack[column][row+1] != 0) break;
	}

	rack[column][row] = side;

	// animate it!
	animateDrop(column, row, side);

	return row;
    }

    // returns the mouse handler (for the human player)
    public MouseHandler getMouseHandler() {
	return mouseHandler;
    }

    // returns the size of each column (for the human player)
    public int getColumnWidth() {
	return SPACE_SIZE;
    }

    ////////////////////////////////////////////////////////////////////////////
    // GRAPHICAL STUFF
    ////////////////////////////////////////////////////////////////////////////

    // hovers a piece above the rack, as a possible play (-1 to erase)
    public void hoverToken(int column, byte side) {
	// return if nothing's changed
	if (column == hoveringToken) return;

	window.copyBack();

	// erase the old one if necessary
	if (column != hoveringToken && hoveringToken != -1) {
	    eraseArea(hoveringToken, -1.0, -1.0);
	}

	hoveringToken = column;

	// and draw the new one
	if (hoveringToken != -1) {
	    drawToken(side, column, -1.0);
	}
	window.flip();
    }

    // draw one of the pieces dropping down
    private void animateDrop(int column, int row, byte side) {
	double delta = 0.0;

	double y = -1.0;
	while (y < row) {
	    window.copyBack();

	    // do the math
	    double oldY = y;
	    y += delta;
	    if (y > row) y = row;
	    delta += DROP_RATE;

	    // do the erasing & drawing
	    eraseArea(column, oldY, y);
	    drawToken(side, column, y);
	    drawRackSection(column, oldY, y);

	    // animate
	    window.flip();
	    window.sleep(10);
	}
    }

    // draw one of the holes in the rack
    private void drawRackSection(int column, double lowRow, double highRow) {
	// calculate start & end
	int start = (int)Math.floor(lowRow);
	if (start < 0) start = 0;
	int end = (int)Math.ceil(highRow);
	if (end >= rack[0].length) end = rack[0].length-1;

	// actual drawing
	Graphics2D pen = window.getPen();
	for (int i=start; i<=end; i++) pen.drawImage(rackSpace, null, column*SPACE_SIZE, (i+1)*SPACE_SIZE);
	pen.dispose();
    }

    // erase an area from the screen (so it can be redrawn)
    private void eraseArea(int column, double lowRow, double highRow) {
	// calculate start & end
	int start = (int)Math.floor(lowRow);
	if (start < -1) start = -1;
	int end = (int)Math.ceil(highRow);
	if (end >= rack[0].length) end = rack[0].length-1;

	// actual drawing
	Graphics2D pen = window.getPen();
	pen.setColor(BACKGROUND_COLOR);
	pen.fillRect(column*SPACE_SIZE, (start+1)*SPACE_SIZE, SPACE_SIZE, (end-start+1)*SPACE_SIZE);
	pen.dispose();
    }    

    // draw the rack (board)
    private void drawFullRack() {
	for (int i=0; i<rack.length; i++) drawRackSection(i, 0, rack[0].length-1);
    }

    // draw a token
    private void drawToken(byte side, int column, double row) {
	BufferedImage image;
	if (side == -1) image = tokens[1];
	else if (side == 1) image = tokens[0];
	else throw new IllegalArgumentException("'side' should be -1 or 1, not " +side);

	// actual drawing
	Graphics2D pen = window.getPen();
	pen.drawImage(image, null, column*SPACE_SIZE, (int)((row+1)*SPACE_SIZE));
	pen.dispose();
    }

    // draw a background-colored rectangle over a location
    private void eraseToken(int column, double row) {
	Graphics2D pen = window.getPen();
	pen.setColor(new Color((float)Math.random(), (float)Math.random(), (float)Math.random()));//BACKGROUND_COLOR);
	pen.fillRect(column*SPACE_SIZE, (int)((row+1)*SPACE_SIZE), SPACE_SIZE, SPACE_SIZE);
	pen.dispose();
    }

    // make all the graphics needed for the game itself
    private void makeGraphics() {
	rackSpace = makeRackSpace();
	tokens = new BufferedImage[2];
	tokens[0] = makeTokenPic(TOKEN1_COLOR);
	tokens[1] = makeTokenPic(TOKEN2_COLOR);	
    }

    // make one of the squares inside the game rack (board)
    private BufferedImage makeRackSpace() {

	// calc values
	int holeSize = (int)(SPACE_SIZE*0.8);
	int margin = (SPACE_SIZE-holeSize)/2;

	// make the images & interfaces
	BufferedImage image = new BufferedImage(SPACE_SIZE, SPACE_SIZE, BufferedImage.TYPE_INT_ARGB);
	Graphics2D pen = image.createGraphics();

	// make the round shape, in the mask
	pen.setComposite(AlphaComposite.Src);
	pen.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	pen.fillOval(margin, margin, holeSize, holeSize);

	// draw a rectangle, with the hole excluded
	pen.setComposite(AlphaComposite.SrcOut);
	pen.setColor(RACK_COLOR);
	pen.fillRect(0, 0, SPACE_SIZE, SPACE_SIZE);
	pen.dispose();

	return image;
    }

    // make the image of one of the tokens
    private BufferedImage makeTokenPic(Color color) {
	BufferedImage image = new BufferedImage(SPACE_SIZE, SPACE_SIZE, BufferedImage.TYPE_INT_ARGB);
	Graphics2D pen = image.createGraphics();
	pen.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

	// some math
	int tokenSize = (int)(TOKEN_RATIO * SPACE_SIZE);
	int margin = (SPACE_SIZE - tokenSize)/2;

	// draw the token itself
	pen.setColor(color);
	pen.fillOval(margin, margin, tokenSize, tokenSize);

	// draw some darker hollow circles
	Color darkColor = new Color((int)(color.getRed()*0.75), (int)(color.getGreen()*0.75), (int)(color.getBlue()*0.75));
	pen.setColor(darkColor);
	pen.setStroke(THICK_STROKE);
	pen.drawOval(margin, margin, tokenSize, tokenSize);
	pen.drawOval((int)(0.3*SPACE_SIZE), (int)(0.3*SPACE_SIZE), (int)(0.4*SPACE_SIZE), (int)(0.4*SPACE_SIZE));

	pen.dispose();
	return image;
    }

    // makes the icon for this program (just a "4" inside of a circle
    private static BufferedImage makeIcon(int size) {
	BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
	Graphics2D pen = image.createGraphics();

	// smooth image
	pen.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        pen.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        pen.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        pen.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

	pen.setColor(TOKEN2_COLOR);//new Color(51,51,51));
	pen.fillOval(0,0,size,size);

	Font font = new Font("Arial", Font.ITALIC|Font.BOLD, (int)(size*0.9));

	// complicated, annoying work to get the String's bounds
	String text = "4";
	java.awt.font.GlyphVector gv = font.layoutGlyphVector(pen.getFontRenderContext(), text.toCharArray(), 0, text.length(), Font.LAYOUT_LEFT_TO_RIGHT);
	Shape outline = gv.getOutline();
	java.awt.geom.Rectangle2D bounds = outline.getBounds2D();
	int xOffset = (int)(-bounds.getMinX() - bounds.getWidth()/2 + 0.5);
	int yOffset = (int)(bounds.getHeight()/2 + 0.5);

	// okay--draw the String ("4")
	pen.setFont(font);
	pen.setColor(TOKEN1_COLOR);
	pen.translate(size/2+xOffset-2, size/2+yOffset);
	pen.fill(outline);

	pen.dispose();
	return image;
    }

    // highlight a winning 4-in-a-row (given the last play made, to start the search)
    private void highlightRow(int column) {
	int alignment = findWinningAlignment(column);
	int row = 0;
	while (rack[column][row] == 0) row++;

	// make the color for highlighting
	Color color;
	byte side = rack[column][row];
	if (side == SIDE1) color = TOKEN1_COLOR;
	else color = TOKEN2_COLOR;
	color = new Color((color.getRed()+255)/2, (color.getGreen()+255)/2, (color.getBlue()+255)/2);

	int tokenSize = (int)(SPACE_SIZE * TOKEN_RATIO);

	// prep the pen
	Graphics2D pen = window.getPen();
	pen.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	pen.setColor(color);
	pen.setStroke(THICKER_STROKE);

	// draw 1st circle
	drawHighlightCircle(pen, column, row, tokenSize);

	// horizontal
	if ((alignment & HORIZONTAL) != 0) {
	    for (int c=column-1; c>=0 && rack[c][row]==side; c--) drawHighlightCircle(pen, c, row, tokenSize);
	    for (int c=column+1; c<rack.length && rack[c][row]==side; c++) drawHighlightCircle(pen, c, row, tokenSize);
	}

	// vertical
	if ((alignment & VERTICAL) != 0) {
	    for (int r=row+1; r<rack[0].length && rack[column][r]==side; r++) drawHighlightCircle(pen, column, r, tokenSize);
	}

	// ascending
	if ((alignment & ASCENDING) != 0) {
	    for (int c=column-1,r=row+1; c>=0 && r<rack[0].length && rack[c][r]==side; c--,r++) drawHighlightCircle(pen, c, r, tokenSize);
	    for (int c=column+1,r=row-1; c<rack.length && r>=0 && rack[c][r]==side; c++,r--) drawHighlightCircle(pen, c, r, tokenSize);
	}

	// descending
	if ((alignment & DESCENDING) != 0) {
	    for (int c=column-1,r=row-1; c>=0 && r>=0 && rack[c][r]==side; c--,r--) drawHighlightCircle(pen, c, r, tokenSize);
	    for (int c=column+1,r=row+1; c<rack.length && r<rack[0].length && rack[c][r]==side; c++,r++) drawHighlightCircle(pen, c, r, tokenSize);
	}

	pen.dispose();
    }

    // draw one of the circles of the highlighting
    private void drawHighlightCircle(Graphics2D pen, int column, int row, int size) {
	int margin = (SPACE_SIZE-size)/2;
	pen.drawOval(column*SPACE_SIZE+margin, (row+1)*SPACE_SIZE+margin, size, size);
    }

    // draws a message along the top of the screen
    private void drawMessage(String message, Color color) {
	Graphics2D pen = window.getPen();

	// smooth image
	pen.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        //pen.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        pen.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        pen.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

	Font font = new Font("Arial", Font.BOLD, (int)(SPACE_SIZE*0.8));

	// complicated, annoying work to get the String's bounds
	java.awt.font.GlyphVector gv = font.layoutGlyphVector(pen.getFontRenderContext(), message.toCharArray(), 0, message.length(), Font.LAYOUT_LEFT_TO_RIGHT);
	Shape outline = gv.getOutline();
	java.awt.geom.Rectangle2D bounds = outline.getBounds2D();
	int xOffset = (int)(-bounds.getMinX() - bounds.getWidth()/2 + 0.5);
	int yOffset = (int)(bounds.getHeight()/2 + 0.5);

	// okay--draw the message
	pen.setFont(font);
	pen.setColor(color);
	pen.translate(window.getWidth()/2+xOffset, SPACE_SIZE/2+yOffset);
	pen.fill(outline);

	pen.dispose();
    }

    // parse an int
    private static int parseInt(String input) {
	try {
	    return Integer.parseInt(input);
	}
	catch (NumberFormatException e) {
	    System.err.println("Warning: \"" +input+ "\" is not a valid number.");
	    return 1;
	}
    }
}

// tiny interface that must be implemented by all AIs
interface ConnectFourPlayer {
    int getNextPlay(byte[][] rack);
}