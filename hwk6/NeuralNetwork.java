import java.util.Random;
import java.util.ArrayList;
import java.lang.NumberFormatException;

/**
 * A simple implementation of a neural network for the voting data.
 * It has one hidden layer that leads to a single output neuron.
 * @author Zach Eddy
 */
class NeuralNetwork
{
	DataSet data;
	int hiddenCount;
	int featureCount;
	
	//matrix for both layers
	double[][] inputWeights;
	double[][] outputWeights;
	
	public static final double LEARNING_RATE = 1.0;
	public static final int DEFAULT_HIDDEN_COUNT = 4;

	/**
	 * Default constructor for the neural network
	 * @param  data for training
	 */
	public NeuralNetwork(DataSet data)
	{
		this(data, DEFAULT_HIDDEN_COUNT);
	}

	/**
	 * Constructor for the neural network
	 * @param  hiddenCount number of hidden neurons in the hidden layer
	 * @param  data for training
	 */
	public NeuralNetwork(DataSet data, int hiddenCount)
	{
		//set instance variables
		this.data = data;
		this.featureCount = data.set.get(0).features.length;
		this.hiddenCount = hiddenCount;
		this.inputWeights = initializeWeights(hiddenCount, featureCount);
		this.outputWeights = initializeWeights(hiddenCount, 1);
		train();
	}

	/**
	 * A method to train the neural network with back propigation
	 */
	public void train()
	{
		//train by feeding forward every datum in the dataset, then back propigating
		for(DataPoint member : data.set)
		{
			backPropigate(member);
		}
	}

	/**
	 * A method that initializes the weight matrices to doubles between -1,1
	 * @param  rowCount row dimension of the matrix
	 * @param  columnCount column dimension of the matrix
	 * @return weight matrix
	 */
	public double[][] initializeWeights(int rowCount, int columnCount)
	{
		double[][] weightMatrix = new double[rowCount][columnCount];
		Random randomGen = new Random();
		//go through row by column
		for(int i = 0; i < rowCount; i++)
		{
			for(int j = 0; j < columnCount; j++)
			{
				//generate a new random value between -1,1
				weightMatrix[i][j] = ((randomGen.nextDouble() * 2) - 1);
			}
		}
		return weightMatrix;
	}

	/**
	 * Back propigation to train the neural network with one datum at a time
	 * @param member input to use for training
	 */
	public void backPropigate(DataPoint datum)
	{
		double[] input = datum.features;

		//calculate the values for each hidden neuron in the hidden layer by
		//multiplying the inputs (a vector) by the matrix representing
		//weights that connect a given input to each hidden neuron.
		double[] hiddenValues = Matrix.multiply(inputWeights, input);
		//bound values betwween 0,1 to prevent a runaway sum
		hiddenValues = sigmoidLayer(hiddenValues);

		//calculate the value for the output neuron
		//this step represents the connection between the hidden layer
		//and the output layer (fully connected)
		double[] outputValues = Matrix.multiply(hiddenValues, outputWeights);
		outputValues = sigmoidLayer(outputValues);

		//calculate the "blame" factor for output neurons
		//in order to establish how much a weight needs to be adjusted
		//to improve future classifications
		double outputBlame = outputBlame(datum.label, outputValues[0]);

		//hidden neurons
		for(int i = 0; i < hiddenCount; i++)
		{
			//calculate exactly how much a weight needs to be adjusted for the layer 
			//that spans between the input layer and the hidden layer 
			double delta = hiddenBlame(hiddenValues[i], outputWeights[i][0], outputBlame);
			for(int j = 0; j < featureCount; j++)
			{
				inputWeights[i][j] += (delta * input[j]) * LEARNING_RATE;
			}
		}
		//output neurons
		for(int i = 0; i < hiddenValues.length; i++)
		{
			//do the same process for the output weights and the hidden layer
			outputWeights[i][0] += (outputBlame * hiddenValues[i]) * LEARNING_RATE;
		}
	}

	/**
	 * This method feeds an input through the neural network to determine an output value
	 * @param  datum input into the neural network
	 * @return the neural network's output
	 */
	public double feedForward(DataPoint datum)
	{
		//calculate the values for each hidden neuron
		double[] input = datum.features;
		double[] hidden = Matrix.multiply(inputWeights, input);
		hidden = sigmoidLayer(hidden);

		//calculate the output neuron
		double[] output = Matrix.multiply(hidden, outputWeights);
		output = sigmoidLayer(output);
		//return the one and only output neuron
		return output[0];
	}

	/**
	 * Calculate the “blame factor”, δ, for the output node of the neural network
	 * @param  expected value, which is the label of the input data
	 * @param  actual value found by the neural network after feeding forward
	 * @return blame factor, δ
	 */
	public double outputBlame(double expected, double actual)
	{
		return actual * (1 - actual) * (expected - actual);
	}

	/** 
	 * Calculate the blame for a hidden node of the neural network
	 * @param  hiddenValue found during the feed-forward process
	 * @param  weight that leads out of the neuron
	 * @param  outputBlame blame from the output node
	 * @return blame derived for a hidden neuron
	 */
	public double hiddenBlame(double hiddenValue, double weight, double outputBlame)
	{
		return hiddenValue * (1 - hiddenValue) * weight * outputBlame;
	}

	/**
	 * This is the sigmoid function that bounds asymptotically positively increasing x values 
	 * to 1, and negatively increasing x values to -1. It does this for an entire layer
	 * @param  layer that will have all its neurons' values inputted into the sigmoid function
	 * @return layer after it has been through the sigmoid function
	 */
	public double[] sigmoidLayer(double[] layer)
	{
		//do the sigmoid function for every neuron in a layer
		double[] sigmoidLayer = new double[layer.length];
		for(int i = 0; i < sigmoidLayer.length; i++)
		{
			sigmoidLayer[i] = sigmoid(layer[i]);
		}
		return sigmoidLayer;
	}

	/**
	 * The sigmoid function
	 * @param  value input into the function
	 * @return output of the sigmoid function
	 */
	public double sigmoid(double value)
	{
		double e = 2.71828;
		double divisor = 1 + Math.pow(e, (value * -1));
		return 1 / divisor;
	}


	/**
	 * This method classifies someone as a democrat or republican based on their voting history
	 * @param  datum input
	 * @return label value (1 for republican, 0 for democrat) determined from the output neuron
	 */
	public int classify(DataPoint datum)
	{
		double result = feedForward(datum);
		if(result > 0.5)
		{
			return 1;
		}
		return 0;
	}

	/**
	 * Main method for the entire program
	 * @param args from the command line
	 */
	public static void main(String[] args)
	{
		if(args.length == 1 || args.length == 3)
		{
			Parser parser = new Parser(args[0]);
			DataSet data = parser.parse();
			if(args.length == 1)
			{
				oneOutCrossValidation(data, 4, 6);
			}
			else
			{
				//make sure the user enters integer values for arguments 2 and 3
				try
				{
					int hiddenCount = Integer.parseInt(args[1]);
					int k = Integer.parseInt(args[2]);
					oneOutCrossValidation(data, hiddenCount, k);
				}
				catch(NumberFormatException e)
				{
					System.out.println("~ Arguments 2 and 3 must both be integer values.");
				}
    		}
		}
		else
		{
			System.out.println("~~~~~~~~~~~~~~ This program accepts two input choices ~~~~~~~~~~~~~~");
			System.out.println("\t~ A \".tsv\" file with two consecutive integer values:");
			System.out.println("\t\t1) An integer to represent the number of neurons in the neural network's hidden layer.");
			System.out.println("\t\t2) An integer to represent \"k\" in k-nearest neighbor.");
			System.out.println("\t~ A \".tsv\" file alone, in which case the size of the hidden layer defaults to six, and \"k\" to four.");
		}
	}

	/**
	 * This method determines the accuracy of the neural net and nearest neighbor classifiers
	 * using n-fold cross-validation, where n is 1.
	 * @param data dataset to do the accuracy testing
	 */
	public static void oneOutCrossValidation(DataSet data, int hiddenCount, int k)
	{
		//the total correct for each classifier
		double correctNearestNeighbor = 0;
		double correctNeuralNetwork = 0;
		ArrayList<DataPoint> set = data.set;

		//both arguments must be greater than 0
		if(hiddenCount < 1 || k < 1)
		{
			System.out.println("~ You cannot integers numbers less than 1.");
			// System.out.println("~ The number of hidden neurons cannot be less than 1.");
			System.exit(-1);
		}
		//make sure k doesn't exceed the size of the data set
		if(k > data.set.size()-1)
		{
			System.out.println("~ \"k\" cannot exceed " + (data.set.size()-1) + " given this dataset.");
			System.exit(-1);
		}
		//go through each member in the data
		for(DataPoint member : set)
		{
			//don't actually modify the data set itself. Just make a copy instead
			ArrayList<DataPoint> oneOut = new ArrayList<DataPoint>();
			oneOut.addAll(set);
			oneOut.remove(member);
			
			//make a new data set with a single element taken out
			DataSet testSet = new DataSet(oneOut);
			NeuralNetwork neuralNetwork = new NeuralNetwork(testSet, hiddenCount);
			NearestNeighbor nearestNeighbor = new NearestNeighbor(testSet, k);
			//if the classified labels are the same, then the counter that tracks correctness should increment
			if(neuralNetwork.classify(member) == member.label)
			{
				correctNeuralNetwork++;
			}
			//ditto, but for nearest neighbor
			if(nearestNeighbor.classify(member) == member.label)
			{
				correctNearestNeighbor++;
			}
		}
		//print out results
		System.out.println("~~~~~~~~~~~~~~ Results from leave-one-out cross-validation ~~~~~~~~~~~~~~");
		System.out.print("\t- Neural network: ");
		System.out.println(roundedPercentage(correctNeuralNetwork / data.set.size()) + "% accurate using " + hiddenCount + " hidden neuron(s).");
		System.out.print("\t- Nearest neighbor: ");
		System.out.println(roundedPercentage(correctNearestNeighbor / data.set.size()) + "% accurate with \"k\" as " + k + ".");
	}

	/**
	 * A method to take a rounded percentage based on a decimal value between 0 and 1
	 * @param  toRound decimal input
	 * @return percentage determined by the decimal input
	 */
	private static double roundedPercentage(double toRound)
	{
		toRound *= 100;
		return Math.round(toRound * 100.0) / 100.0;
	}
}