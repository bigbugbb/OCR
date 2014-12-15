package svm;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectOutputStream;

import weka.core.Instances;
import wlsvm.WLSVM;
import config.ConfigReader;

public class SVMClassifierBuilder {
	public static void main(String[] args) throws Exception {
		ConfigReader.read();
		BufferedReader br = new BufferedReader(new FileReader(ConfigReader.TRAINING_DATA_PATH));
		Instances data = new Instances(br);
		br.close();
		data.setClassIndex(data.numAttributes() - 1);
	    //initialize svm classifier
		WLSVM svm = new WLSVM();
		long start = System.currentTimeMillis();
        svm.buildClassifier(data);
		System.out.println((System.currentTimeMillis()-start));
        ObjectOutputStream oos = new ObjectOutputStream(
        		new FileOutputStream(ConfigReader.SVM_TRAINED_MODEL_PATH));
		oos.writeObject(svm);
		oos.flush();
		oos.close();

/*        Evaluation eTest = new Evaluation(data);
        eTest.crossValidateModel(svm, data, 10, new Random(1));
        String strSummary = eTest.toSummaryString();
        System.out.println(strSummary);*/
	}
}
