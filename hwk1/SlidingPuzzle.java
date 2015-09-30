import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * This is a class representing a common sliding puzzle, as for
 * example the fifteen puzzle. It consists of a number of sliding
 * tiles in a rectangle, with one tile missing. Thus, tiles are free
 * to slide horizontally or vertically.
 *
 * Students are expected to implement the Solver class, which holds
 * the solvePuzzle() method. This will allow the puzzle to be
 * solved. Animation is taken care of by this class. Thus, after you
 * write the proper AI, it should look cool!
 *
 * @author      Adam A. Smith
 * @version     1.0
 */

class SlidingPuzzle {
    private static final int TILE_SIZE = 100, HALF_TILE = TILE_SIZE/2;
    private static final Color TILE_COLOR = new Color(255, 192, 128), NUMBER_COLOR = new Color(128, 64, 0), BACKGROUND_COLOR = Color.BLACK;
    private static final int FRAMES_PER_MOVE = 25;

    private int[][] tiles;
    private int gapX = -1, gapY = -1;
    private BufferedImage[] tilePics;

    /** 
     * Execution entry point. Command-line argument is the name of the puzzle to
     * solve.
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
	// check for command-line args
	if (args.length != 1) {
	    System.err.println("Usage: java SlidingPuzzle <puzzle-file>");
	    System.exit(1);
	}

	// check for the Solver class
	if (!doesClassExist("Solver")) {	    
	    System.err.println("Could not find a legit Solver class.");
	    System.exit(1);
	}
	

	// load the puzzle & display it
	SlidingPuzzle puzzle = new SlidingPuzzle(args[0]);
	String solution = Solver.solvePuzzle(puzzle.makeCopyOfTiles());

	if (solution == null) System.out.println("Puzzle cannot be solved.");
	else System.out.println("Puzzle can be solved in " +solution.length()+ " moves: " +solution);
	if (doesClassExist("GraphicsWindow")) {	    
	    puzzle.animatePuzzle(solution);
	}
	else {
	    puzzle.print();
	}
    }

    /** 
     * Opens a file and reads it into a new SlidingPuzzle object.
     * @param filename name of the file to read
     */
    public SlidingPuzzle(String filename) {
	try {
	    // initialize data structures for reading file
	    Scanner scanner = new Scanner(new File(filename));
	    ArrayList<int[]> list = new ArrayList<int[]>();
	    int lineSize = -1;

	    // read each line, get #s
	    while (scanner.hasNextLine()) {

		// read a line, split it up
		String line = scanner.nextLine();
		String[] tokens = line.split(" +");

		// ensure that size of each line is constant
		if (lineSize == -1) lineSize = tokens.length;
		else if (tokens.length != lineSize) {
		    System.err.println("Error: Badly formed file!");
		    System.exit(1);
		}

		// convert to int[], throw into array-list
		int[] intArray = new int[tokens.length];
		for (int i=0; i<tokens.length; i++) {
		    if (tokens[i].equals(".")) intArray[i] = 0;
		    else intArray[i] = Integer.parseInt(tokens[i]);
		}
		list.add(intArray);
	    }

	    // all read in--close Scanner & convert to int[][]
	    scanner.close();
	    tiles = list.toArray(new int[list.size()][]);
	}
	catch (FileNotFoundException e) {
	    System.err.println("Error: Could not find file \"" +filename+ "\".");
	    System.exit(1);
	}
    }

    // what's the puzzle's width?
    private int getWidth() {
	return tiles[0].length;
    }

    // what's the puzzle's height?
    private int getHeight() {
	return tiles.length;
    }

    // how many tiles are there?
    private int getNumTiles() {
	return tiles.length * tiles[0].length - 1;
    }

    // print out the puzzle to the terminal
    private void print() {
	for (int i=0; i<tiles.length; i++) {
	    for (int j=0; j<tiles[i].length; j++) {
		if (j != 0) System.out.print("\t");
		if (tiles[i][j] == 0) System.out.print(".");
		else System.out.print(tiles[i][j]);
	    }
	    System.out.println();
	}
    }

    // copy the tile-position array, for protection
    private int[][] makeCopyOfTiles() {
	int[][] copy = new int[tiles.length][tiles[0].length];
	for (int i=0; i<copy.length; i++) {
	    for (int j=0; j<copy[i].length; j++) copy[i][j] = tiles[i][j];
	}
	return copy;
    }

    private BufferedImage[] makeTilePics(int numTiles) {
	BufferedImage[] images = new BufferedImage[numTiles+1];
	for (int i=1; i<=numTiles; i++) images[i] = makeTilePic(i, TILE_SIZE);
	return images;
    }

    // draw a String centered at a particular location
    private void drawCenteredString(Graphics2D pen, String text, Color color, Font font, int x, int y) {
	// complicated, annoying work to get the String's bounds
	java.awt.font.GlyphVector gv = font.layoutGlyphVector(pen.getFontRenderContext(), text.toCharArray(), 0, text.length(), Font.LAYOUT_LEFT_TO_RIGHT);
	Shape outline = gv.getOutline();
	java.awt.geom.Rectangle2D bounds = outline.getBounds2D();
	int xOffset = (int)(-bounds.getMinX() - bounds.getWidth()/2 + 0.5);
	int yOffset = (int)(bounds.getHeight()/2 + 0.5);

	// okay--draw the String
	pen.setFont(font);
	pen.setColor(color);
	pen.translate(x+xOffset, y+yOffset);
	pen.fill(outline);
    }

    // generates a pic of one tile, to be moved around
    private BufferedImage makeTilePic(int number, int tileSize) {
	BufferedImage image = new BufferedImage(tileSize, tileSize, BufferedImage.TYPE_INT_ARGB);
	Graphics2D pen = image.createGraphics();

	// smooth numbers
	pen.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        pen.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        pen.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        pen.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

	pen.setColor(TILE_COLOR);
	pen.fillRect(1, 1, tileSize-2, tileSize-2);

	Font font = new Font("Arial", Font.BOLD, (int)(tileSize*0.7));

	drawCenteredString(pen, Integer.toString(number), NUMBER_COLOR, font, tileSize/2, tileSize/2);

	return image;
    }

    // actually perform the animation
    private void animatePuzzle(String solution) {
	GraphicsWindow window = new GraphicsWindow("Sliding Puzzle", makeTilePic(15, 32), getWidth()*TILE_SIZE, getHeight()*TILE_SIZE, 400, 100, true);

	// initialization
	window.paintBackground(BACKGROUND_COLOR);
	drawTiles(window.getPen());
	window.flip();
	window.copyBack();

	// if solution is null, print IMPOSSIBLE & leave it at that
	if (solution == null) {
	    Font font = new Font("Arial", Font.BOLD, 48);
	    drawCenteredString(window.getPen(), "IMPOSSIBLE", Color.WHITE, font, window.getWidth()/2, window.getHeight()/2);
	    window.flip();
	}

	// otherwise, execute the animation
	else {
	    for (int i=0; i<solution.length(); i++) {
		char dir = solution.charAt(i);
		for (int j=0; j<FRAMES_PER_MOVE; j++) {
		    GraphicsWindow.sleep(10);
		    int distance = (int)(((j+1)*TILE_SIZE)/((double)FRAMES_PER_MOVE));
		    animateTile(window.getPen(), dir, distance);
		    window.flip();
		}
		doMove(dir);
		window.copyBack();
	    }
	}
    }

    // stop execution for some # of milliseconds
    private static void sleep(int millis) {
	try {
	    Thread.sleep(millis);
	} catch (Exception e) {
	}
    }

    // where's the gap square?
    private void findGap() {
	for (int i=0; i<tiles.length; i++) {
	    for (int j=0; j<tiles[0].length; j++) 
	    {
		if (tiles[i][j] == 0) {
		    gapX = j;
		    gapY = i;
		    return;
		}
	    }
	}

	throw new IllegalStateException("Couldn't find gap!?");
    }

    // draw the tiles
    private void drawTiles(Graphics2D pen) {
	// generate tile images, if they're not there
	if (tilePics == null) tilePics = makeTilePics(getNumTiles());

	for (int y=0; y<tiles.length; y++) {
	    for (int x=0; x<tiles[y].length; x++) {
		if (tiles[y][x] != 0) pen.drawImage(tilePics[tiles[y][x]], null, x*TILE_SIZE, y*TILE_SIZE);
	    }
	}
    }

    // actually execute the move
    private void doMove(char dir) {
	if (gapX == -1) findGap();
	if (dir == 'U') {
	    tiles[gapY][gapX] = tiles[gapY+1][gapX];
	    gapY++;
	}
	else if (dir == 'D') {
	    tiles[gapY][gapX] = tiles[gapY-1][gapX];
	    gapY--;
	}
	else if (dir == 'L') {
	    tiles[gapY][gapX] = tiles[gapY][gapX+1];
	    gapX++;
	}
	else if (dir == 'R') {
	    tiles[gapY][gapX] = tiles[gapY][gapX-1];
	    gapX--;
	}
	else throw new IllegalStateException("Bad direction \"" +dir+ "\"!");
    }

    // move a tile by a small amount
    private void animateTile(Graphics2D pen, char dir, int distance) {
	if (gapX == -1) findGap();
	BufferedImage pic;
	pen.setColor(Color.BLACK);
	if (dir == 'U') {
	    pen.fillRect(gapX*TILE_SIZE, gapY*TILE_SIZE, TILE_SIZE, 2*TILE_SIZE);
	    pic = tilePics[tiles[gapY+1][gapX]];
	    pen.drawImage(pic, null, gapX*TILE_SIZE, (gapY+1)*TILE_SIZE - distance);
	}
	else if (dir == 'D') {
	    pen.fillRect(gapX*TILE_SIZE, (gapY-1)*TILE_SIZE, TILE_SIZE, 2*TILE_SIZE);
	    pic = tilePics[tiles[gapY-1][gapX]];
	    pen.drawImage(pic, null, gapX*TILE_SIZE, (gapY-1)*TILE_SIZE + distance);
	}
	else if (dir == 'L') {
	    pen.fillRect(gapX*TILE_SIZE, gapY*TILE_SIZE, 2*TILE_SIZE, TILE_SIZE);
	    pic = tilePics[tiles[gapY][gapX+1]];
	    pen.drawImage(pic, null, (gapX+1)*TILE_SIZE - distance, gapY*TILE_SIZE);
	}
	else if (dir == 'R') {
	    pen.fillRect((gapX-1)*TILE_SIZE, gapY*TILE_SIZE, 2*TILE_SIZE, TILE_SIZE);
	    pic = tilePics[tiles[gapY][gapX-1]];
	    pen.drawImage(pic, null, (gapX-1)*TILE_SIZE + distance, gapY*TILE_SIZE);
	}
	else throw new IllegalStateException("Bad direction \"" +dir+ "\"!");
    }

    // figure out if a class exists
    private static boolean doesClassExist(String className) {
	try {
	    Class.forName(className);
	    return true;
	}
	catch (ClassNotFoundException e) {
	    return false;
	}

    }
}