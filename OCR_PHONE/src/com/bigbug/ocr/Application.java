package com.bigbug.ocr;

import android.content.ComponentCallbacks;
import android.content.Intent;
import android.content.res.Configuration;

import com.bigbug.ocr.broadcastreceivers.OcrBroadcastReceiver;

public class Application extends android.app.Application {

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();		
		sendBroadcast(new Intent(OcrBroadcastReceiver.ACTION_START_OCR_SERVICE));
	}

	@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub
		super.onLowMemory();
	}

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
	}

}
