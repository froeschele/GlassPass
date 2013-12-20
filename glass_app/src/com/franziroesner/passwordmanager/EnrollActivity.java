package com.franziroesner.passwordmanager;

import com.google.android.glass.media.Sounds;

import android.content.Context;
import android.media.AudioManager;
import android.widget.TextView;

/** Activity that enrolls passwords from QR codes; launched in response to "OK Glass, enroll password". */
public class EnrollActivity extends QrCodeActivity {
	
	public static String TAG = "EnrollActivity";
	
	public void qrCodeFound(String text) {	
		stopCamera();
		
		AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		
		try {
			String site = text.split(":glasspasswordmanager:")[0];
			String password = text.split(":glasspasswordmanager:")[1];
			mPasswordStore.setPasswordForSite(site, password);
				
			audio.playSoundEffect(Sounds.SUCCESS);
			
			TextView textView = (TextView) findViewById(R.id.textView);
			textView.setText("Enrolled!\n\nSite: " + site + "\n" + "Password: " + password);
			
		} catch(Exception e) {
			audio.playSoundEffect(Sounds.ERROR);
			
			TextView textView = (TextView) findViewById(R.id.textView);
			textView.setText("Error enrolling password (check your enrollment QR code).");
		}
	}
}