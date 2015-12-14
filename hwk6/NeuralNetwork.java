import java.util.Random;
import java.util.ArrayList;

class NeuralNetwork
{
	DataSet data;
	int hiddenCount;
	int featureCount;
	
	double[][] inputWeights;
	double[][] outputWeights;
	
	public static final double LEARNING_RATE = 1.0;
	public static final int DEFAULT_HIDDEN_COUNT = 4;


	public NeuralNetwork(DataSet data)
	{
		this(DEFAULT_HIDDEN_COUNT, data);
	}

	public NeuralNetwork(int hiddenCount, DataSet data)
	{
		this.data = data;
		this.featureCount = data.set.get(0).features.length;
		this.hiddenCount = hiddenCount;
		this.inputWeights = initializeWeights(hiddenCount, featureCount);
		this.outputWeights = initializeWeights(hiddenCount, 1);
		train();
	}

	public void train()
	{
		for(DataPoint member : data.set)
		{
			backPropigate(member);
		}
		feedForward(data.set.get(0));
	}

	public double[][] initializeWeights(int rowCount, int columnCount)
	{
		double[][] weightMatrix = new double[rowCount][columnCount];
		Random randomGen = new Random();
		for(int i = 0; i < rowCount; i++)
		{
			for(int j = 0; j < columnCount; j++)
			{
				weightMatrix[i][j] = ((randomGen.nextDouble() * 2) - 1);
			}
		}
		return weightMatrix;
	}

	public void backPropigate(DataPoint member)
	{
		double[] input = member.features;

		//calculate the values for the hidden neurons
		double[] hiddenValues = Matrix.multiply(inputWeights, input);
		hiddenValues = sigmoidLayer(hiddenValues);

		//calculate the value for the output neuron
		double[] outputValues = Matrix.multiply(hiddenValues, outputWeights);
		outputValues = sigmoidLayer(outputValues);

		//calculate "blame" factor for output neuron
		double outputBlame = outputBlame(member.label, outputValues[0]);

		//hidden neurons
		for(int i = 0; i < hiddenCount; i++)
		{
			double delta = hiddenBlame(hiddenValues[i], outputWeights[i][0], outputBlame);
			for(int j = 0; j < featureCount; j++)
			{
				inputWeights[i][j] += (delta * input[j]) * LEARNING_RATE;
			}
		}
		//output neuron
		for(int i = 0; i < hiddenValues.length; i++)
		{
			outputWeights[i][0] += (outputBlame * hiddenValues[i]) * LEARNING_RATE;
		}
	}

	public double feedForward(DataPoint member)
	{
		double[] input = member.features;
		double[] hidden = Matrix.multiply(inputWeights, input);
		hidden = sigmoidLayer(hidden);

		double[] output = Matrix.multiply(hidden, outputWeights);
		output = sigmoidLayer(output);

		return output[0];
	}

	public double outputBlame(double expected, double actual)
	{
		return actual * (1 - actual) * (expected - actual);
	}

	public double hiddenBlame(double hiddenValue, double weight, double outputBlame)
	{
		return hiddenValue * (1 - hiddenValue) * weight * outputBlame;
	}

	public double[] sigmoidLayer(double[] layer)
	{
		double[] sigmoidLayer = new double[layer.length];
		for(int i = 0; i < sigmoidLayer.length; i++)
		{
			sigmoidLayer[i] = sigmoid(layer[i]);
		}
		return sigmoidLayer;
	}

	public double sigmoid(double value)
	{
		double e = 2.71828;
		double divisor = 1 + Math.pow(e, (value * -1));
		return 1 / divisor;
	}

	public static void main(String[] args)
	{
		Parser parser = new Parser(args[0]);
		DataSet data = parser.parse();
		NeuralNetwork n = new NeuralNetwork(data);
		oneOutCrossValidation(data);
	}

	public int classify(DataPoint member)
	{
		double result = feedForward(member);
		if(result > 0.5)
		{
			return 1;
		}
		return 0;
	}

	public static void oneOutCrossValidation(DataSet data)
	{
		double correct = 0;
		double correctNearestNeighbor = 0;
		double correctNeuralNetwork = 0;

		ArrayList<DataPoint> set = data.set;
		for(DataPoint member : set)
		{
			ArrayList<DataPoint> oneOut = new ArrayList<DataPoint>();
			oneOut.addAll(set);
			oneOut.remove(member);
			
			DataSet testSet = new DataSet(oneOut);
			NeuralNetwork neuralNetwork = new NeuralNetwork(testSet);
			NearestNeighbor nearestNeighbor = new NearestNeighbor(testSet);
			
			if(neuralNetwork.classify(member) == member.label)
			{
				correctNeuralNetwork++;
			}
			if(nearestNeighbor.classify(member) == member.label)
			{
				correctNearestNeighbor++;
			}
		}
		System.out.println("~~~~~~~~~~~~~~ Results from leave-one-out cross-validation ~~~~~~~~~~~~~~");
		System.out.print("\t- Nearest neighbor: ");
		System.out.println(roundedPercentage(correctNearestNeighbor / data.set.size()) + "% accurate.");
		System.out.print("\t- Neural network: ");
		System.out.println(roundedPercentage(correctNeuralNetwork / data.set.size()) + "% accurate.");
	}

	private static double roundedPercentage(double toRound)
	{
		toRound *= 100;
		return Math.round(toRound * 100.0) / 100.0;
	}
}