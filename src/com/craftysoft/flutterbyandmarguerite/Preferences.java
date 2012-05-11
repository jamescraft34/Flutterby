package com.craftysoft.flutterbyandmarguerite;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.craftysoft.flutterbyandmarguerite.pro.R;

public class Preferences extends PreferenceActivity{

	 @Override
	 public void onCreate(Bundle savedInstanceState) 
	 {
	        super.onCreate(savedInstanceState);
	        addPreferencesFromResource(R.xml.preferences); 
	 }
}
