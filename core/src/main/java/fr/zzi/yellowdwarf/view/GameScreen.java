package fr.zzi.yellowdwarf.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;

import fr.zzi.yellowdwarf.controller.GlobalCallback;
import fr.zzi.yellowdwarf.controller.WorldRenderer;
import fr.zzi.yellowdwarf.controller.utils.Assets;
import fr.zzi.yellowdwarf.controller.utils.Audios;

public class GameScreen implements Screen {

	private WorldRenderer renderer;
	private GlobalCallback callback;

	public GameScreen(GlobalCallback gcb) {
		Assets.getInstance().initFonts();
		renderer = new WorldRenderer();
		callback = gcb;
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0 / 255f, 60 / 255f, 167 / 255f, 1);
		// Gdx.gl.glClearColor(30/255f, 69/255f, 160/255f, 1);
		// Gdx.gl.glClearColor(59/255f, 78/255f, 156/255f, 1);
		// Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		renderer.render(delta);
	}

	@Override
	public void resize(int width, int height) {
		// use true here to center the camera
		// that's what you probably want in case of a UI

		renderer.resize(width, height);
	}

	@Override
	public void show() {
		renderer.init(callback);
		
		callback.showAdds();
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		renderer.dispose();
		
		//release audio
		Audios.getInstance().dispose();
		
		//release ressources
		Assets.getInstance().dispose();
		
//		org.lwjgl.openal.AL.destroy();
//		Gdx.app.exit();
	}
}
