package com.example.prora.demonoteandroid;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by prora on 3/10/2017.
 */

public class SettingsUtils {
	private static final String TAG = SettingsUtils.class.getSimpleName();
	private static SettingsUtils instances;
	private SharedPreferences sharedPreferences;
	private static final String SETTING_FILE_NAME = "demo_app";
	private Context context;

	private SettingsUtils() {
		context = MyApplication.getAppContext();
		getSharedPreferences();
	}

	public static SettingsUtils getInstances(){
		if(instances == null){
			instances = new SettingsUtils();
		}
		return instances;
	}

	private void getSharedPreferences(){
		if(sharedPreferences == null){
			sharedPreferences = context.getSharedPreferences(SETTING_FILE_NAME, Context.MODE_PRIVATE);
		}
	}

	public String getStringSharedPreferences(String key){
		return sharedPreferences.getString(key, "");
	}

	public void setStringSharedPreferences(String key, String value){
		sharedPreferences.edit().putString(key, value).commit();
	}
}
