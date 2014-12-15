package naivebayes;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;

import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayesUpdateable;
import weka.core.Instances;
import config.ConfigReader;

public class NBClassifierTester {
	public static void main(String[] args) throws Exception {
		ConfigReader.read();
		BufferedReader br = new BufferedReader(new FileReader(ConfigReader.TEST_DATA_PATH));
		Instances testdata = new Instances(br);
		br.close();
		testdata.setClassIndex(testdata.numAttributes() - 1);
		br = new BufferedReader(new FileReader(ConfigReader.TRAINING_DATA_PATH));
		Instances data = new Instances(br);
		data.setClassIndex(data.numAttributes() - 1);
		br.close();
		ObjectInputStream ois = new ObjectInputStream(
                 new FileInputStream(ConfigReader.NB_TRAINED_MODEL_PATH));
		NaiveBayesUpdateable nb = (NaiveBayesUpdateable) ois.readObject();
		ois.close();
		Evaluation eval = new Evaluation(data);
		eval.evaluateModel(nb, testdata);
		System.out.println(eval.toSummaryString("\nResults\n======\n", false));
	}
}
