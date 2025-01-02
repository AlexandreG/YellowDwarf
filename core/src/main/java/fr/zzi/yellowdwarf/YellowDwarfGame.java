package fr.zzi.yellowdwarf;

import com.badlogic.gdx.Game;

import fr.zzi.yellowdwarf.controller.GlobalCallback;
import fr.zzi.yellowdwarf.view.SplashScreen;

public class YellowDwarfGame extends Game {
	private final GlobalCallback callbacks;
	
	public YellowDwarfGame(GlobalCallback gcb) {
		callbacks = gcb;
	}

	@Override
	public void create() {
		//Launch splashScreen and load resources
		setScreen(new SplashScreen(callbacks));
	}
}