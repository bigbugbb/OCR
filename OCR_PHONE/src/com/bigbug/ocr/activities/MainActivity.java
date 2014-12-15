package com.bigbug.ocr.activities;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.bigbug.ocr.R;

public class MainActivity extends TabActivity {
	// TabSpec Names
	private static final String PHOTO_SCAN_SPEC = "Photo extract";
	private static final String HAND_WRITE_SPEC = "Hand writing";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        TabHost tabHost = getTabHost();
        
        // Photo Tab
        TabSpec photoSpec = tabHost.newTabSpec(HAND_WRITE_SPEC);
        // Tab Icon
        photoSpec.setIndicator(HAND_WRITE_SPEC, null);
        Intent inboxIntent = new Intent(this, HandwriteActivity.class);
        // Tab Content
        photoSpec.setContent(inboxIntent);
        
        // Location Tab
        TabSpec locationSpec = tabHost.newTabSpec(PHOTO_SCAN_SPEC);
        locationSpec.setIndicator(PHOTO_SCAN_SPEC, null);
        Intent outboxIntent = new Intent(this, PhotoscanActivity.class);
        locationSpec.setContent(outboxIntent);
      
        // Adding all TabSpec to TabHost
        tabHost.addTab(photoSpec);
        tabHost.addTab(locationSpec);
        
    }
}
