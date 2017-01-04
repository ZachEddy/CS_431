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

Implemented A\*, a search algorithm, to quickly solve a [sliding puzzle](https://en.wikipedia.org/wiki/Sliding_puzzle).

The GUI interface is provided by Adam.

### Assignment 2 - Connect Four Bot

Made a MiniMax bot to play [Connect Four](https://en.wikipedia.org/wiki/Connect_Four) against itself or a human. At the highest difficulty, it usually ties itself and always beats me.

The GUI interface is provided by Adam.

### Assignment 3 - Wumpus World

Built an agent in Prolog that explores the [Wumpus World](https://cis.temple.edu/~ingargio/cis587/readings/wumpus.shtml) environment.

### Assignment 4 - Decision Trees

Made a decision tree that classifies someone's political party based on their voting history for ten issues. It makes informed classifications after learning with ~500 examples. An example holds the voting history for a single person and their associated political party.

## Assignment 5 - K-Means

Used K-Means to cluster Democrats and Republicans into two seperate groups based on how they voted for ten issues. The clustering process used ~500 examples (same data from the decision tree assignment).

## Assignment 6 - Independent choice (Neural Network and k-Nearest Neighbor)

Built a simple neural network to determine political party (same data from the decision tree assignment). This was more straightforward than I originally thought, so I also implemented k-Nearest Neighbor for the same task.
