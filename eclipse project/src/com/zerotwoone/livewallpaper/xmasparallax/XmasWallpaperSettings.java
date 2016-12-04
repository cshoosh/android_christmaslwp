package com.zerotwoone.livewallpaper.xmasparallax;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Preference change listener.
 */
public class XmasWallpaperSettings extends PreferenceActivity{


	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		addPreferencesFromResource(R.xml.wallpaper_settings);
	}


}
