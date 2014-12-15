package svm;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;

import weka.classifiers.Evaluation;
import weka.core.Instances;
import wlsvm.WLSVM;
import config.ConfigReader;

public class SVMClassifierTester {
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
                 new FileInputStream(ConfigReader.SVM_TRAINED_MODEL_PATH));
		 WLSVM svm = (WLSVM) ois.readObject();		
		 ois.close();
		 long start = System.currentTimeMillis();
		 Evaluation eval = new Evaluation(data);
		 eval.evaluateModel(svm, testdata);
		 System.out.println((System.currentTimeMillis()-start)/1000);
		 System.out.println(eval.toSummaryString("\nResults\n======\n", false));

		 
//		 br = new BufferedReader(new FileReader(SvmConstant.svmRealFile));
//		 Instances realdata = new Instances(br);
//		 br.close();
//		 realdata.setClassIndex(realdata.numAttributes() - 1);
//		 for (int i = 0; i < realdata.numInstances(); i++) {
//			double clsLabel = svm.classifyInstance(realdata.instance(i));
//			System.out.println(realdata.classAttribute().value((int) clsLabel));
//		 }
	}
}
