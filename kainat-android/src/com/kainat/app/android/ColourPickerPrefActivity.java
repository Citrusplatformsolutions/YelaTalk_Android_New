package com.kainat.app.android;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class ColourPickerPrefActivity extends PreferenceActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.drawable.colour_preference);
	}
}
