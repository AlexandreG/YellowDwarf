package fr.zzi.yellowdwarf.android;

import android.app.Application;
import android.content.Context;

public class GlobalApp extends Application {

	private static GlobalApp instance;
	private Context context;
	private AndroidLauncher mainActivity;

	@Override
	public void onCreate() {
		super.onCreate();

		// initialize the singleton
		instance = this;
		context = getApplicationContext();
	}

	public static GlobalApp getInstance() {
		return instance;
	}

	/**
	 * @return the application context
	 */
	public Context getContext() {
		return context;
	}

	public AndroidLauncher getMainActivity() {
		return mainActivity;
	}

	public void setMainActivity(AndroidLauncher mainActivity) {
		this.mainActivity = mainActivity;
	}

}
