package ann;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import au.com.bytecode.opencsv.CSVReader;
import config.ConfigReader;

public class ANNClassifierTester extends JFrame implements Runnable {
	
	private KohonenNetwork mNetwork;
	private List<SampleData> mTestSamples = new ArrayList<SampleData>();
	private List<SampleData> mTrainingSamples = new ArrayList<SampleData>();
	
	private int ROW_SIZE = 16;
	private int COL_SIZE = 8;
	private int EXTRA_SIZE = 24;
	private int CHAR_COUNT = 26;
	
	Thread mTestThread;
	
	ANNClassifierTester() {
		getContentPane().setLayout(null);
		
		ConfigReader.read();

		mTestThread = new Thread(this);
		mTestThread.start();	     
	}

	public static void main(String args[]) {
	     new ANNClassifierTester().show();
	}

	@Override
	public void run() {
		// load all data
		load(mTrainingSamples, ConfigReader.ANN_TRAINING_DATA_PATH);
		load(mTestSamples, ConfigReader.ANN_TEST_DATA_PATH);
		System.out.println("test data are loaded");
		
		// create training set for character mapping
		int inputNeuron  = ROW_SIZE * COL_SIZE + EXTRA_SIZE;
		int outputNeuron = mTrainingSamples.size();

		TrainingSet set = new TrainingSet(inputNeuron, /*outputNeuron*/CHAR_COUNT);
		set.setTrainingSetCount(mTrainingSamples.size());

		for (int i = 0; i < mTrainingSamples.size(); ++i) {
			int k = 0;
			SampleData s = (SampleData) mTrainingSamples.get(i);
			for (int y = 0; y < s.getHeight(); ++y) {
				for (int x = 0; x < s.getWidth(); ++x) {
					set.setInput(i, k++, s.getData(x, y) ? .5 : -.5);
				}
			}
			// add the extra features
			if (ConfigReader.USE_MORE_FEATURES) {
				for (int n = 0; n < EXTRA_SIZE; ++n) {
					set.setInput(i, k++, s.getExtraData(n));
				}
			}
		}						

		mNetwork = new KohonenNetwork(inputNeuron, outputNeuron);
		mNetwork.setTrainingSet(set);
		
		double[][] outputWeights = recover();
		mNetwork.outputWeights = outputWeights;
		char map[] = mapNeurons();
		
		PrintWriter writer = null;
		try {
			writer = new PrintWriter("./neural_network_test_result.txt", "UTF-8");			
			writer.println("Neural Network Results");
			// test the neural network
			int correct = 0, total = mTestSamples.size();
			for (int i = 0; i < mTestSamples.size(); ++i) {
				SampleData s = mTestSamples.get(i);
				char recognized = recognize(s, map);
				String result = "#" + i + " " + s.letter + " " + recognized;
				writer.println(result);
				System.out.println(result);

				if (recognized == s.letter) {
					++correct;
				}
			}
			String lastLine = "right: " + correct + " total: " + total + " " + correct / (double) total;
			writer.println(lastLine);
			System.out.println(lastLine);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (writer != null) {
				writer.close();
			}
		}	
	}
	
	public void load(List<SampleData> samples, String filePath) {
		samples.clear();

		String[] row = null;
		CSVReader csvReader = null;
		try {
			csvReader = new CSVReader(new FileReader(filePath), '\t');	
			while ((row = csvReader.readNext()) != null) {				
				char c = row[row.length - 1].charAt(0);
				int k = 0;
				SampleData s = new SampleData(c, ROW_SIZE, COL_SIZE, 
						ConfigReader.USE_MORE_FEATURES ? EXTRA_SIZE : 0);
				for (int i = 0; i < ROW_SIZE; ++i) {
					for (int j = 0; j < COL_SIZE; ++j) {
						s.setData(i, j, row[k++].charAt(0) == '1');
					}
				}
				// add the extra features
				if (ConfigReader.USE_MORE_FEATURES) {
					for (int i = 0; i < EXTRA_SIZE; ++i) {
						if (i < ROW_SIZE) {
							s.setExtraData(i, (double) Integer.parseInt(row[k++]) / COL_SIZE - 0.5);
						} else {
							s.setExtraData(i, (double) Integer.parseInt(row[k++]) / ROW_SIZE - 0.5);
						}
					}
				}
				samples.add(s);
			}					
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (csvReader != null) {
					csvReader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}		
	}
	
	char recognize(SampleData s, char[] map) {
		double input[] = new double[ROW_SIZE * COL_SIZE + EXTRA_SIZE];
	    int k = 0;	    
	    for (int y = 0; y < s.getHeight(); ++y) {
	    	for (int x = 0; x < s.getWidth(); ++x) {
	    		input[k++] = s.getData(x, y) ? .5 : -.5;
	    	}
	    }
	    // add the extra features
		if (ConfigReader.USE_MORE_FEATURES) {
			for (int i = 0; i < EXTRA_SIZE; ++i) {
				input[k++] = s.getExtraData(i);
			}
		}

	    double normfac[] = new double[1];
	    double synth[] = new double[1];

	    int best = mNetwork.winner(input, normfac, synth);    
	    return map[best];	    
	}

	/**
	 * Used to map neurons to actual letters.
	 *
	 * @return The current mapping between neurons and letters as an array.
	 */
	char[] mapNeurons() {
		char map[] = new char[mTrainingSamples.size()];
		double normfac[] = new double[1];
		double synth[] = new double[1];

		for (int i = 0; i < map.length; ++i) {
			map[i] = '?';
		}
		for (int i = 0; i < mTrainingSamples.size(); ++i) {
			double input[] = new double[ROW_SIZE * COL_SIZE + EXTRA_SIZE];
			int k = 0;
			SampleData s = (SampleData) mTrainingSamples.get(i);
			for (int y = 0; y < s.getHeight(); ++y) {
				for (int x = 0; x < s.getWidth(); ++x) {
					input[k++] = s.getData(x, y) ? .5 : -.5;
				}
			}
			if (ConfigReader.USE_MORE_FEATURES) {
				for (int n = 0; n < EXTRA_SIZE; ++n) {
					input[k++] = s.getExtraData(n);
				}
			}

			int best = mNetwork.winner(input, normfac, synth) ;
			map[best] = s.getLetter();
		}
		return map;
	}
	
	double[][] recover() {
		// Deserializable network object	
		KohonenNetwork network = null;
		try {
			InputStream file = new FileInputStream(ConfigReader.ANN_TRAINED_MODEL_PATH);
			InputStream buffer = new BufferedInputStream(file);
			ObjectInput input = new ObjectInputStream (buffer);		
			// deserialize the List
			network = (KohonenNetwork) input.readObject();		    	
		} catch(ClassNotFoundException e){
			e.printStackTrace();
		} catch(IOException e){
			e.printStackTrace();
		}
		
		return network.outputWeights;
	}	
}

