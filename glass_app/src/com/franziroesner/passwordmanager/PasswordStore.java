package com.franziroesner.passwordmanager;

import android.content.SharedPreferences;

/** A wrapper for SharedPreferences, to set and retrieve site/password pairs. */
public class PasswordStore {
	
	public static String TAG = "PasswordStore";
	
	private SharedPreferences mPrefs;
	
	public PasswordStore(SharedPreferences prefs) {		
		mPrefs = prefs;
	}
	
	public String lookupPasswordForSite(String site) {
		return mPrefs.getString(site, null);
	}
	
	public void setPasswordForSite(String site, String password) {
		SharedPreferences.Editor editor = mPrefs.edit();
		editor.putString(site, password);
		editor.commit();
	}
}
