package com.bigbug.ocr.models;

import android.content.Context;
import android.graphics.Bitmap;

public abstract class Model {
//	
//	public static int NAIVE_MODEL  = 0;
//	public static int VECTOR_MODEL = 1;
//	
	protected Context mContext;
	protected ModelEventListener mListener;
	
	public Model() {}
	
	public boolean isLoaded() {
		return false;
	}
	
	public void setContext(Context context) {
		mContext  = context;
	}
	
	public void setModelEventListener(ModelEventListener listener) {
		mListener = listener;
	}
	
	public interface ModelEventListener {
		void onModelLoaded(boolean success);
		void onCharRecognized(char c);
	}
	
	public abstract boolean load();
	public abstract char recognize(Bitmap input);
}
