package com.franziroesner.passwordmanager;

import com.google.android.glass.media.Sounds;

import android.content.Context;
import android.media.AudioManager;
import android.widget.TextView;

/** Activity that finds passwords based on site QR codes; launched in response to "OK Glass, find password". */
public class ScannerActivity extends QrCodeActivity {
	
	public static String TAG = "ScannerActivity";
	
	public void qrCodeFound(String text) {	
		stopCamera();
		
		String site = text;
        String password = mPasswordStore.lookupPasswordForSite(site);
		
		AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);		
		
		TextView textView = (TextView) findViewById(R.id.textView);
		if (password != null) {
			audio.playSoundEffect(Sounds.SUCCESS);
			textView.setText("Site: " + site + "\n" + "Password: " + password);
		} else {
			audio.playSoundEffect(Sounds.ERROR);
			textView.setText("No password found for site:\n" + site);
		}
		
	}
}
