package ann;

import javax.swing.JFrame;

public class ANNClassifierBuilder extends JFrame implements Runnable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Entry  mEntry;
	Thread mTrainThread;
	
	ANNClassifierBuilder() {
		getContentPane().setLayout(null);
		mEntry = new Entry();
		getContentPane().add(mEntry);

		mTrainThread = new Thread(this);
		mTrainThread.start();	     
	}

	public static void main(String args[]) {
	     new ANNClassifierBuilder().show();
	}

	@Override
	public void run() {
		OCR.run();
	}
}
