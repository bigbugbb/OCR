package com.bigbug.ocr.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bigbug.ocr.services.LoadModelService;
import com.bigbug.ocr.services.WakefulIntentService;


public class OcrBroadcastReceiver extends BroadcastReceiver {
	public static final String TAG = "OcrBroadcastReceiver";

	// TODO I'm concerned the string below will cause problems with
	// multiple apps using the library. Does this need to be project specific?
	public static final String ACTION_START_OCR_SERVICE = ".broadcastreceivers.OcrBroadcastReceiver.START_SERVICE";	

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		
		if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
			WakefulIntentService.sendWakefulWork(context, LoadModelService.class, "Message");
		} else if (action.equals(ACTION_START_OCR_SERVICE)) {
			WakefulIntentService.sendWakefulWork(context, LoadModelService.class, "Message");
		}
	}
}
