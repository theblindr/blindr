package com.lesgens.blindr.controllers;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesController {
	private static final String PREF_NAME = "blindr_user_prefs";

	public static final String INTERESTED_IN = "USER_PREF_INTERESTED_IN";
	public static final String LAST_CONNECTION = "USER_PREF_LAST_CONNECTION";

	public static void setPreference(final Context context, final String preference, final String value){
		SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
		editor.putString(preference, value);
		editor.commit();
	}
	
	public static String getInterestedIn(final Context context){
		SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE); 
		String restoredText = prefs.getString(INTERESTED_IN, "");
		return restoredText;
	}
	
	public static String getLastConnection(final Context context){
		SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE); 
		String restoredText = prefs.getString(LAST_CONNECTION, "");
		return restoredText;
	}

}