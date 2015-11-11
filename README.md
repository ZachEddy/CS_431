# CS_431 - Artificial Intelligence
Fall 2015, Professor Adam Smith.

### Course description from syllabus
Artificial Intelligence. The term conjures images of science fiction stories; of distopian
worlds with evil robot overlords, or beneficent android companions who will help us as
society evolves.

However state-of-the-art AI is a little less dramatic. Modern AI is more concerned with
pattern recognition and data mining. Computers such as Deep Blue and Watson have
made headlines by competing against humans in games. However other tasks are also
considered to be AI, such as recommending a movie or book that you’ll like, predicting
whether or not you’re a terrorist from your cell phone records, and data-mining your
social network accounts to determine your insurance premiums.

We will be using the Java programming language (with which most of you are already
familiar). You may if you wish use a different language such as C++ or Python. There
will also be some work in Prolog, which is a programming language made to represent
logic problems.

### Assignment 1 - Slide Puzzle Solver

Information about slide puzzles can be found here: https://en.wikipedia.org/wiki/Sliding_puzzle

Using A\*, create a program that can find the optimal solution to a slide puzzle. A\*, an informed search algorithm, relies 
on domain knowledge to avoid frivolously exploring bad solutions (i.e. halting searches that don't lead towards the goal).
In this case, domain knowledge is exploited by calculating how far away each tile is from its goal position. Paths that result
in sliding tiles closer to the goal state will be explored first.

The GUI interface is provided by Adam.

### Assignment 2 - Connect Four Bot

How to play Connect Four: https://en.wikipedia.org/wiki/Connect_Four

The objective is straightforward - use the MiniMax algorithm to create a bot that can make moves intelligently. The AI, 'Max', recursively looks ahead by N moves in order to make a decision that results in the best outcome for itself. It also assumes that the opponent, 'Min', will maximize its own gain (thereby minimizing the gain of Max). The AI uses a metric to quantifiably  determine how good or bad a move is, and the move that yields the highest value will be chosen.


### Assignment 3 - Wumpus World

The Wumpus World game: https://en.wikipedia.org/wiki/Hunt_the_Wumpus

The idea behind this assignment is to create a knowledge base for Wumpus World. As an agent explores this 


