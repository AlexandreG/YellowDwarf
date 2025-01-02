package fr.zzi.yellowdwarf.android;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import fr.zzi.yellowdwarf.YellowDwarfGame;
import fr.zzi.yellowdwarf.controller.utils.Assets;

public class AndroidLauncher extends AndroidApplication {

	// keep the score in case we are not logged yet
	private boolean submitScoreNextResume;
	private int scoreToSubmit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		GlobalApp.getInstance().setMainActivity(this);

		submitScoreNextResume = false;
		scoreToSubmit = 0;

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new YellowDwarfGame(new AndroidCallback()), config);
	}
	
	public void submitScoreNextResume(int score) {
		scoreToSubmit = score;
		submitScoreNextResume = true;
	}
}
