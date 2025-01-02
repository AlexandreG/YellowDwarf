package fr.zzi.yellowdwarf.controller.utils;

import java.util.Locale;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.I18NBundleLoader;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.I18NBundle;

/**
 *
 * Handle the assets
 *
 */
public class Assets {

	private static Assets INSTANCE = new Assets();

	private AssetManager manager;
	private Skin menuSkin;

	public void initInstance() {
		manager = new AssetManager();
		menuSkin = new Skin(Gdx.files.internal("skins/menuSkin.json"));
	}

	/**
	 * Set filters and scale according to the screen width
	 */
	public void initFonts() {
		// Resize fonts
		menuSkin.getFont("ostrichBlack38").getRegion().getTexture()
				.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		menuSkin.getFont("ostrichBlack34B").getRegion().getTexture()
				.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		menuSkin.getFont("ostrichBlack48B").getRegion().getTexture()
		.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		manager.get("fonts/score.fnt", BitmapFont.class).getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		float scale = 0.000514f * Gdx.graphics.getWidth() - 0.33f;
		if (scale > 0) {
			menuSkin.getFont("ostrichBlack38").getData().scale(scale);
			menuSkin.getFont("ostrichBlack34B").getData().scale(scale);
			menuSkin.getFont("ostrichBlack48B").getData().scale(scale);
			Assets.getInstance().getManager()
					.get("fonts/score.fnt", BitmapFont.class)
                   .getData().scale(scale);
		}
	}

	public static Assets getInstance() {
		return INSTANCE;
	}

	// In here we'll put everything that needs to be loaded in this format:
	// manager.load("file location in assets", fileType.class);
	//
	// libGDX AssetManager currently supports: Pixmap, Texture, BitmapFont,
	// TextureAtlas, TiledAtlas, TiledMapRenderer, Music and Sound.
	public void queueLoading() {
		//Font
		manager.load("fonts/score.fnt", BitmapFont.class);

		//before planets.pack
		manager.load("ui.pack", TextureAtlas.class);
		manager.load("game.pack", TextureAtlas.class);

		//audio
		manager.load("audio/click.ogg", Sound.class);
		manager.load("audio/jump.wav", Sound.class);
		manager.load("audio/explosion.ogg", Sound.class);
		manager.load("audio/blackhole.ogg", Music.class);
		manager.load("audio/music.ogg", Music.class);

		// Localisation
		if (Locale.getDefault().getLanguage()
				.equals(Locale.FRENCH.toString())) {
			// French
			manager.load("i18n/MyBundle", I18NBundle.class,
					new I18NBundleLoader.I18NBundleParameter(Locale.FRENCH));
		} else {
			// English as default
			manager.load("i18n/MyBundle", I18NBundle.class,
					new I18NBundleLoader.I18NBundleParameter(Locale.ENGLISH));
		}
	}

	public AssetManager getManager() {
		return manager;
	}

	/**
	 * Return the texture region of the game.pack
	 */
	public TextureRegion getGameRegion(String name){
		return getGameAtlas().findRegion(name);
	}


	/**
	 * Return the texture region of the ui.pack
	 */
	public TextureRegion getUiRegion(String name){
		return getUiAtlas().findRegion(name);
	}

	/**
	 * Return the ui.pack atlas from the manager
	 */
	private TextureAtlas getUiAtlas(){
		return Assets.getInstance().getManager()
		.get("ui.pack", TextureAtlas.class);
	}

	/**
	 * Return the game.pack atlas from the manager
	 */
	private TextureAtlas getGameAtlas(){
		return Assets.getInstance().getManager()
		.get("game.pack", TextureAtlas.class);
	}

	/**
	 * Fetch the value associated to the key from the language properties file
	 */
	public String getValue(String key) {
		return manager.get("i18n/MyBundle", I18NBundle.class).get(key);
	}

	/**
	 * Fetch the value associated to the key from the language properties file
	 * and fill the string with the given arg
	 */
	public String getValueFormated(String key, Object arg1) {
		return manager.get("i18n/MyBundle", I18NBundle.class).format(key, arg1);
	}

	public Skin getMenuSkin() {
		if (menuSkin == null) {
			menuSkin = new Skin(Gdx.files.internal("skins/menuSkin.json"));
			return menuSkin;

		} else {
			return menuSkin;
		}
	}

	// This function gets called every render() and the AssetManager pauses the
	// loading each frame
	// so we can still run menus and loading screens smoothly
	public boolean update() {
		// System.out.println(manager.getProgress());
		return manager.update();
	}

	/**
	 * Dispose all the res
	 */
	public void dispose() {
		manager.dispose();
		menuSkin.dispose();
	}
}
