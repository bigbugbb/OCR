package com.bigbug.ocr.models;

import java.util.HashMap;

import android.content.Context;

public class ModelManager {
	
	private static HashMap<Class<?>, Model> sModels = new HashMap<Class<?>, Model>();
	
	public static Model getModel(Context context, Class<?> cls) {
		Model model = sModels.get(cls);
		
		if (model != null) {
			return model;
		}
		
		try {
			model = (Model) cls.newInstance();
			model.setContext(context);
			sModels.put(cls, model);
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return model;
	}
}
