package fr.zzi.yellowdwarf.view;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import fr.zzi.yellowdwarf.controller.GlobalCallback;
import fr.zzi.yellowdwarf.controller.UIRenderer;
import fr.zzi.yellowdwarf.controller.WorldRenderer;
import fr.zzi.yellowdwarf.controller.utils.Assets;
import fr.zzi.yellowdwarf.controller.utils.PrefManager;

public class SplashScreen implements Screen {
	// public static float LOGO_W = WorldRenderer.SW*0.7f;
	public static float LOGO_W = UIRenderer.READY_W;

	public static float LOGO_P_BOT = LOGO_W * 0.4f;

	public static float PROGRESS_BAR_W = Gdx.graphics.getWidth()*0.33f;
	public static float PROGRESS_BAR_H = PROGRESS_BAR_W*0.01f;

	private Stage stage;
	private ProgressBar p;
	private GlobalCallback callback;

	public SplashScreen(GlobalCallback gcb) {
		callback = gcb;
	}

	@Override
	public void render(float delta) {
//		Gdx.gl.glClearColor(0, 60f / 255f, 167f / 255f, 1);
		Gdx.gl.glClearColor(62f / 255f, 67f / 255f, 65f / 255f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(delta);
		p.act(delta);

		if (Assets.getInstance().update()) {
			// if all files are loaded : launch game
			((Game) Gdx.app.getApplicationListener())
					.setScreen(new GameScreen(callback));
		} else {
			// update progressBar
			p.setValue(Assets.getInstance().getManager().getProgress());
		}

		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
		stage = new Stage();
		Table table = new Table();

		Image logo = new Image(new Texture(
				Gdx.files.internal("splashScreen.png")));
		float ratio = logo.getWidth() / logo.getHeight();
		float h = LOGO_W / ratio * 1.15f;

		Skin splashScreenSkin = new Skin(
				Gdx.files.internal("skins/progressBar.json"), new TextureAtlas(
						Gdx.files.internal("skins/progressBar.pack")));
		p = new ProgressBar(0.0f, 1.0f, 0.05f, false, splashScreenSkin,
				"splashScreenBar");
		p.setAnimateDuration(0.03f);
		p.setHeight(PROGRESS_BAR_H);

		table.add(logo).width(LOGO_W).height(h).padBottom(LOGO_P_BOT)
				.padTop(UIRenderer.READY_M_TOP - h / 2);
        		table.row();

		table.add(p).width(PROGRESS_BAR_W).height(PROGRESS_BAR_H);
		table.setFillParent(true);
		table.top();

		stage.addActor(table);

		// Assets.manager.clear();
		// not necessary, only when splash called more then once
		Assets.getInstance().initInstance();
		Assets.getInstance().queueLoading();

		if(PrefManager.getBoolean(WorldRenderer.USE_LEADERBOARD_KEY))
			callback.initSwarm();
	}

	@Override
	public void hide() {
		dispose();
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		stage.dispose();
	}
}
