// abstract class Classifer
// {
// 	public int classify(){}

// 	public Classifer(DataSet set)
// 	{

// 	}
// 	public Classifer newObject(DataSet set)
// 	{
// 		return new Classifer(set);
// 	}
// }
// 
import java.util.ArrayList;
class Classifier
{
	DataSet data;

	public int classify(DataPoint member)
	{
		return 0;
	}

	public Classifier(DataSet data)
	{

	}
	
	public void oneOutCrossValidation()
	{
		double correct = 0;
		for(DataPoint member : data.set)
		{
			ArrayList<DataPoint> set = new ArrayList<DataPoint>();
			set.addAll(data.set);
			set.remove(member);

			DataSet test = new DataSet(set);
			Classifier classifier = new Classifier(test);
			// neuralNetwork.train();

			int position = classifier.classify(member);
			if(position == member.label)
			{
				correct++;
			}
		}
		System.out.println((correct / data.set.size()) * 100);
	}
	// public Classifier newInstance(DataSet set);
}