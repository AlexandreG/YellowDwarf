package fr.zzi.yellowdwarf.controller.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

/**
 * 
 * Load and save in the sharedpref
 *
 */
public class PrefManager {
	private static Preferences prefs = Gdx.app.getPreferences("MyPrefs");

	public static int getInt(String key) {
		return prefs.getInteger(key);
	}

	public static void saveInt(String key, int value) {
		prefs.putInteger(key, value);
		prefs.flush();
	}

	public static String getString(String key) {
		return prefs.getString(key);
	}

	public static void saveString(String key, String value) {
		prefs.putString(key, value);
		prefs.flush();
	}

	public static boolean getBoolean(String key) {
		return prefs.getBoolean(key);
	}

	public static void saveBoolean(String key, boolean value) {
		prefs.putBoolean(key, value);
		prefs.flush();
	}
}
