package com.bigbug.ocr.services;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.bigbug.ocr.Globals;
import com.bigbug.ocr.models.Model;
import com.bigbug.ocr.models.ModelManager;


public class LoadModelService extends WakefulIntentService {
	
	public LoadModelService() {
		super("LoadModelService");
	}

	@Override
	protected void doWakefulWork(Intent intent) {
		Context context = getApplicationContext();		
		Model model = ModelManager.getModel(context, Globals.CURRENT_MODEL);
		
		if (model != null) {
			model.load();		
		}
	}	
}
