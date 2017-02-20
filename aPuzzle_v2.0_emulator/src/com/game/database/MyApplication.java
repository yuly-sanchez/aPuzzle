package com.game.database;

import android.app.Application;
import android.content.Context;

/**
 * Classe ausiliaria per avere sempre a 
 * disposizione il contesto dell'applicazione
 */
public class MyApplication extends Application {
	private static Context context;
	
	public void onCreate() {
		super.onCreate();
		MyApplication.context = getApplicationContext();
	}

	public static Context getAppContext() {
		return MyApplication.context;
	}

}
