package com.bigbug.ocr.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.bigbug.ocr.Globals;
import com.bigbug.ocr.R;
import com.bigbug.ocr.models.Model;
import com.bigbug.ocr.models.ModelManager;

public class HandwriteActivity extends Activity implements Model.ModelEventListener {
	
	private TextView mTextView;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handwrite);  
        
        mTextView = (TextView) findViewById(R.id.view_result);
        
        Model model = ModelManager.getModel(getApplicationContext(), Globals.CURRENT_MODEL);
        model.setModelEventListener(this);
	}

	@Override
	public void onModelLoaded(boolean success) {
		Globals.generateNotification(getApplicationContext(), "Model for OCR has been loaded :-)");		
	}

	@Override
	public void onCharRecognized(char c) {
		mTextView.setText(mTextView.getText() + " " + c);
	}
}

