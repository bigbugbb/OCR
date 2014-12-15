package config;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class ConfigReader {
	public static String TRAINING_DATA_PATH;
	public static String TEST_DATA_PATH;
	public static String ANN_TRAINING_DATA_PATH;
	public static String ANN_TEST_DATA_PATH;
	public static String ANN_TRAINED_MODEL_PATH;
	public static String NB_TRAINED_MODEL_PATH;
	public static String SVM_TRAINED_MODEL_PATH;
	public static boolean USE_MORE_FEATURES;
	
	public static void read() {
		BufferedReader br = null;
		HashMap<String, String> configs = new HashMap<String, String>();
		 
		try {
			String line;
			br = new BufferedReader(new FileReader("config.txt"));
			
			while ((line = br.readLine()) != null) {
				if (line.trim().equals("")) {
					continue;
				}
				if (line.trim().startsWith("//") || line.trim().startsWith("#")) {
					continue;
				}
				String[] sep = line.split("=");
				if (sep.length < 2) {
					continue;
				}
				
				String type  = sep[0].trim();
				String value = sep[1].trim();		
				configs.put(type, value);				
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}	
		
		if (configs.get("USE_MORE_FEATURES").toLowerCase().equals("true")) {
			TRAINING_DATA_PATH = configs.get("TRAINING_DATA_MORE_FEATURES_PATH");
			TEST_DATA_PATH = configs.get("TEST_DATA_MORE_FEATURES_PATH");
			ANN_TRAINING_DATA_PATH = configs.get("ANN_TRAINING_DATA_MORE_FEATURES_PATH");
			ANN_TEST_DATA_PATH = configs.get("ANN_TEST_DATA_MORE_FEATURES_PATH");
			USE_MORE_FEATURES = true;
		} else {
			TRAINING_DATA_PATH = configs.get("TRAINING_DATA_PATH");
			TEST_DATA_PATH = configs.get("TEST_DATA_PATH");
			ANN_TRAINING_DATA_PATH = configs.get("ANN_TRAINING_DATA_PATH");
			ANN_TEST_DATA_PATH = configs.get("ANN_TEST_DATA_PATH");
			USE_MORE_FEATURES = false;
		}		
		ANN_TRAINED_MODEL_PATH = configs.get("ANN_TRAINED_MODEL_PATH");
		NB_TRAINED_MODEL_PATH = configs.get("NB_TRAINED_MODEL_PATH");
		SVM_TRAINED_MODEL_PATH = configs.get("SVM_TRAINED_MODEL_PATH");
	}
}
