package ann;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;
import config.ConfigReader;


public class OCR {
	
	private KohonenNetwork mNetwork;
	public List<SampleData> mTrainingSamples = new ArrayList<SampleData>();
	
	private int ROW_SIZE = 16;
	private int COL_SIZE = 8;
	private int EXTRA_SIZE = 24;
	private int CHAR_COUNT = 26;	
	
	static public void run() {
		//Get the jvm heap size.
        long heapSize = Runtime.getRuntime().maxMemory();
         
        //Print the jvm heap size.
        System.out.println("Heap Size = " + heapSize);
        
        // read config
        ConfigReader.read();
        
        // training the neural network
		OCR ocr = new OCR();			
		ocr.load(ocr.mTrainingSamples, ConfigReader.ANN_TRAINING_DATA_PATH);
		ocr.train();
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
			System.out.println("training data are loaded");
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
	
	public void train() {
		try {
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
					
			long start = System.currentTimeMillis();
			mNetwork.learn();
			long stop = System.currentTimeMillis();
			mNetwork.duration = (stop - start) / 1000; // in seconds
			System.out.println("duration = " + mNetwork.duration);
			
			// It seems that the serialized object is not working, however we can use
			// the serializable feature to save the weights easily.
			// Serializable network object
			try {					 
				FileOutputStream fout = new FileOutputStream(ConfigReader.ANN_TRAINED_MODEL_PATH);
				ObjectOutputStream oos = new ObjectOutputStream(fout);   
				oos.writeObject(mNetwork);
				oos.close();
				System.out.println("Done");	 
			} catch(Exception ex){
			    ex.printStackTrace();
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}	
}