package fr.zzi.yellowdwarf.controller;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.graphics.g2d.Animation;

import fr.zzi.yellowdwarf.controller.WorldRenderer.GameState;
import fr.zzi.yellowdwarf.controller.utils.Assets;
import fr.zzi.yellowdwarf.controller.utils.Audios;
import fr.zzi.yellowdwarf.controller.utils.PrefManager;
import fr.zzi.yellowdwarf.controller.utils.Score;
import fr.zzi.yellowdwarf.view.Notif;
import fr.zzi.yellowdwarf.view.scene2d.MultipleImageButton;
import fr.zzi.yellowdwarf.view.scene2d.OnOffMultipleImageButton;
import fr.zzi.yellowdwarf.view.scene2d.SimpleNinePatchDrawable;
import fr.zzi.yellowdwarf.view.scene2d.SimpleTexture;

/**
 *
 * Render the game interface
 *
 */
public class UIRenderer {
	public static final boolean DEBUG = WorldRenderer.DEBUG;
	public static int SH = Gdx.graphics.getHeight();
	public static int SW = Gdx.graphics.getWidth();

	public static float READY_W = 2 * SW / 3;
	public static float READY_M_TOP = SH / 3.2f;
	public static float TAP_W = SW / 7;
	public static float TAP_H = TAP_W;

	public static float PANEL_M_TOP = SH * 0.6f;
	public static float PANEL_W = SW * 0.8f;
	public static float PANEL_H = SH * 0.3f;

	public static float TABLE_P_TOP = SH * 0.2f;
	public static float SCORE_P_BOT = SH * 0.03f;

	public static float MENU_BUTT_INTERSPACE = PANEL_H / 40;
	public static float MENU_BUTT_W = PANEL_H / 4 - MENU_BUTT_INTERSPACE * 5
			/ 4;
	public static float MENU_BUTT_W_LOGO = MENU_BUTT_W * 0.4f;
	public static float MENU_BUTT_INTER_PAD = 0;

	public static float CONTENT_BUTT_W = SW * 0.137f;

	public static float OPTION_BUTT_P_BOT = PANEL_H * 0.15f;
	public static float OPTION_P_BOT = PANEL_H * 0.1f;
	public static float OPTION_CONTENT_W = PANEL_W * 0.6f;

	private WorldRenderer wr;
	private SpriteBatch batch;
	private OrthographicCamera uiCamera;
	private Notif eventHandler;
	private JumperHandler jumperHandler;

	// global assets
	private Skin menuSkin = Assets.getInstance().getMenuSkin();
	private TextureRegion homeTexture = Assets.getInstance()
			.getUiRegion("home");
	private TextureRegion playerTexture = Assets.getInstance().getUiRegion(
			"jumper");
	private TextureRegion achievTexture = Assets.getInstance().getUiRegion(
			"achievement");
	private TextureRegion optionTexture = Assets.getInstance().getUiRegion(
			"option");
	private TextureRegion playTexture = Assets.getInstance()
			.getUiRegion("play");
	private TextureRegion leaderboardTexture = Assets.getInstance()
			.getUiRegion("leaderboard");
	private TextureRegion backTexture = Assets.getInstance().getUiRegion(
			"return");
	private TextureRegion soundOn = Assets.getInstance()
			.getUiRegion("sound_on");
	private TextureRegion soundOff = Assets.getInstance().getUiRegion(
			"sound_off");
	private TextureRegion musicOn = Assets.getInstance()
			.getUiRegion("music_on");
	private TextureRegion musicOff = Assets.getInstance().getUiRegion(
			"music_off");

	// tab buttons
	private MultipleImageButton homeButton = new MultipleImageButton(menuSkin,
			"menuButton", homeTexture);
	private MultipleImageButton playerButton = new MultipleImageButton(
			menuSkin, "menuButton", playerTexture);
	private MultipleImageButton achievButton = new MultipleImageButton(
			menuSkin, "menuButton", achievTexture);
	private MultipleImageButton optionButton = new MultipleImageButton(
			menuSkin, "menuButton", optionTexture);

	private SimpleNinePatchDrawable panel;
	private SimpleTexture gameTitle;
	private SimpleTexture getReady;
	private SimpleTexture paused;
	private SimpleTexture gameOver;

	private Animation tap;
	private float stateTime;

	private boolean showGameOverTitle;

	private Stage menuStage;
	private Table menuTable;
	private Stage pauseStage;

	// Menu contents
	private Stack contents = new Stack();
	private Actor homeContent;
	private Actor pauseContent;
	private Actor gameOverContent;
	private Actor playerContent;
	private Actor achievContent;
	private Actor optionContent;

	private Label scoreText = new Label("Score : 10", menuSkin,
			"scoreGameOverStyle");
	private Label bestScoreText = new Label("Best : 12", menuSkin,
			"scoreGameOverStyle");
	private Label bestScoreLabelPause;


	private OnOffMultipleImageButton music;
	private OnOffMultipleImageButton effects;
	private CheckBox useLeaderboard;

	public UIRenderer(WorldRenderer worldRenderer, SpriteBatch b) {
		this.batch = b;
		this.wr = worldRenderer;
		eventHandler = new Notif(wr);
		jumperHandler = new JumperHandler(wr);

		uiCamera = new OrthographicCamera();
		uiCamera.setToOrtho(false, WorldRenderer.SW, WorldRenderer.SH);
		uiCamera.update();

		playTexture.getTexture().setFilter(TextureFilter.Linear,
				TextureFilter.Linear);
		leaderboardTexture.getTexture().setFilter(TextureFilter.Linear,
				TextureFilter.Linear);
		homeTexture.getTexture().setFilter(TextureFilter.Linear,
				TextureFilter.Linear);
		playerTexture.getTexture().setFilter(TextureFilter.Linear,
				TextureFilter.Linear);
		achievTexture.getTexture().setFilter(TextureFilter.Linear,
				TextureFilter.Linear);
		optionTexture.getTexture().setFilter(TextureFilter.Linear,
				TextureFilter.Linear);
		backTexture.getTexture().setFilter(TextureFilter.Linear,
				TextureFilter.Linear);
		soundOn.getTexture().setFilter(TextureFilter.Linear,
				TextureFilter.Linear);
		soundOff.getTexture().setFilter(TextureFilter.Linear,
				TextureFilter.Linear);
		musicOn.getTexture().setFilter(TextureFilter.Linear,
				TextureFilter.Linear);
		musicOff.getTexture().setFilter(TextureFilter.Linear,
				TextureFilter.Linear);

		stateTime = 0;
		TextureRegion tap1 = Assets.getInstance().getUiRegion("tap1");
		TextureRegion tap2 = Assets.getInstance().getUiRegion("tap2");
		tap1.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		tap2.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		tap = new Animation(0.6f, new TextureRegion(tap1), new TextureRegion(
				tap2));
		tap.setPlayMode(PlayMode.LOOP);

		initMainTable();
		initPauseTable();

		gameTitle = new SimpleTexture(Assets.getInstance().getUiRegion(
				"gametitle"), SW / 2, SH - READY_M_TOP, READY_W);
		gameOver = new SimpleTexture(Assets.getInstance().getUiRegion(
				"gameover"), SW / 2, SH - READY_M_TOP, READY_W);
		paused = new SimpleTexture(Assets.getInstance().getUiRegion("paused"),
				SW / 2, SH - READY_M_TOP, READY_W);
		getReady = new SimpleTexture(Assets.getInstance().getUiRegion("ready"),
				SW / 2, SH - READY_M_TOP, READY_W);

		panel = new SimpleNinePatchDrawable(Assets.getInstance().getUiRegion(
				"panel"), SW / 2, SH - PANEL_M_TOP, PANEL_W, PANEL_H);

		showGameOverTitle = false;
	}

	/**
	 * Initialise the pause button in the game
	 */
	private void initPauseTable() {
		Table pauseTable = new Table();
		ImageButton pause = new ImageButton(Assets.getInstance().getMenuSkin(),
				"pauseButton");
		pauseStage = new Stage();

		pause.addListener(new ClickListener() {

			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				wr.pause();
				bestScoreLabelPause.setText("Best : "+ wr.getScore().getBestScore());
				Audios.getInstance().playClick();
				return super.touchDown(event, x, y, pointer, button);
			}
		});
		// transparency
		// pause.setColor(1f, 1f, 1f, 0.5f);

		// pause.setHeight(wr.getScore().getScoreBounds().height*2);

		if (DEBUG)
			pauseTable.setDebug(true);

        //TODO AGH
//		float h = wr.getScore().getScoreBounds().height * 2;
//		pauseTable.top().right();
//		pauseTable.add(pause).height(h).width(h)
//				.padTop(Score.MARGIN_TOP_RUNNING - h * 0.25f)
//				.padRight(Score.MARGIN_TOP_RUNNING / 2);

		pauseTable.setFillParent(true);
		pauseStage.addActor(pauseTable);

	}

	/**
	 * Initialize the home menu and its contents
	 */
	private void initMainTable() {
		menuStage = new Stage();
		menuTable = new Table();

		menuTable.setDebug(DEBUG);
		menuTable.setFillParent(true);
		menuTable.padTop(TABLE_P_TOP);

		// tab buttons
		menuTable.add(generateTabMenu()).width(MENU_BUTT_W).height(PANEL_H);

		// the contents
		menuTable.add(generateMenuContents()).width(PANEL_W - MENU_BUTT_W)
				.height(PANEL_H);

		// Listen to changes in the tab button checked states
		// Set visibility of the tab content to match the checked state
		ChangeListener tab_listener = new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Audios.getInstance().playClick();

				jumperHandler.resetCheatClick();
				homeContent.setVisible(homeButton.isChecked()
						&& wr.getState() == GameState.Home);
				pauseContent.setVisible(homeButton.isChecked()
						&& wr.getState() == GameState.Pause);
				gameOverContent.setVisible(homeButton.isChecked()
						&& wr.getState() == GameState.GameOver);

				playerContent.setVisible(playerButton.isChecked());
				achievContent.setVisible(achievButton.isChecked());
				optionContent.setVisible(optionButton.isChecked());
			}
		};
		homeButton.addListener(tab_listener);
		playerButton.addListener(tab_listener);
		achievButton.addListener(tab_listener);
		optionButton.addListener(tab_listener);

		menuStage.addActor(menuTable);
	}

	/**
	 * Generate and fill the contents of the menu
	 */
	private Actor generateMenuContents() {
		// Create the tab content. Just using images here for simplicity.
		contents = new Stack();
		homeContent = generateHomeContent();
		pauseContent = generatePauseContent();
		gameOverContent = generateGameOverContent();

		playerContent = jumperHandler.generateJumperContent();
		achievContent = wr.getAchvHandler().getContent();
		optionContent = generateOptionContent();

		// at launch : show home menu
		homeContent.setVisible(true);
		pauseContent.setVisible(false);
		gameOverContent.setVisible(false);

		playerContent.setVisible(false);
		achievContent.setVisible(false);
		optionContent.setVisible(false);

		contents.addActor(homeContent);
		contents.addActor(pauseContent);
		contents.addActor(gameOverContent);
		contents.addActor(playerContent);
		contents.addActor(achievContent);
		contents.addActor(optionContent);
		return contents;
	}

	/**
	 * Generate the tab buttons of the menu
	 */
	private Actor generateTabMenu() {
		Table menu = new Table();
		ButtonGroup tabs = new ButtonGroup();

		homeButton.setLogoWidth(MENU_BUTT_W_LOGO);
		playerButton.setLogoWidth(MENU_BUTT_W_LOGO);
		achievButton.setLogoWidth(MENU_BUTT_W_LOGO);
		optionButton.setLogoWidth(MENU_BUTT_W_LOGO);

		menu.add(homeButton).width(MENU_BUTT_W).height(MENU_BUTT_W)
				.padBottom(MENU_BUTT_INTERSPACE).padTop(MENU_BUTT_INTERSPACE);
		menu.row();

		menu.add(playerButton).width(MENU_BUTT_W).height(MENU_BUTT_W)
				.padBottom(MENU_BUTT_INTERSPACE);
		menu.row();

		menu.add(achievButton).width(MENU_BUTT_W).height(MENU_BUTT_W)
				.padBottom(MENU_BUTT_INTERSPACE);
		menu.row();

		menu.add(optionButton).width(MENU_BUTT_W).height(MENU_BUTT_W)
				.padBottom(MENU_BUTT_INTERSPACE);

		menu.padLeft(2 * MENU_BUTT_INTERSPACE);

		// Let only one tab button be checked at a time
		tabs.setMinCheckCount(1);
		tabs.setMaxCheckCount(1);
		tabs.add(homeButton);
		tabs.add(playerButton);
		tabs.add(achievButton);
		tabs.add(optionButton);

		return menu;
	}

	private Actor generateHomeContent() {
		Table homeContent = new Table();

		Label bestScoreHomeContent = new Label("Best : "
				+ wr.getScore().getBestScore(), menuSkin, "scoreGameOverStyle");
		homeContent.add(bestScoreHomeContent).padBottom(SCORE_P_BOT).colspan(2);
		homeContent.row();

//		homeContent.add(getNewLeaderboardButton()).width(CONTENT_BUTT_W * 0.8f)
//		.height(CONTENT_BUTT_W * 0.8f).padRight(MENU_BUTT_INTERSPACE*2);
		homeContent.add(getNewMainStartButton()).width(CONTENT_BUTT_W * 1.7f)
				.height(CONTENT_BUTT_W * 0.8f);

		return homeContent;
	}

	private MultipleImageButton getNewLeaderboardButton(){
		//leaderboard button
		MultipleImageButton homeLeaderboard = new MultipleImageButton(menuSkin,
				"greyButton", leaderboardTexture);
		homeLeaderboard.setLogoHeight(MENU_BUTT_W_LOGO*2);

		homeLeaderboard.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Audios.getInstance().playClick();
				wr.getGlobalCallback().showLeaderboards();
			}
		});
		return homeLeaderboard;
	}

	private MultipleImageButton getNewMainStartButton(){
		//play button
		MultipleImageButton homePlay = new MultipleImageButton(menuSkin,
				"yellowButton", playTexture);
		homePlay.setLogoHeight(MENU_BUTT_W_LOGO);

		homePlay.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Audios.getInstance().playClick();
				wr.start();
			}
		});
		return homePlay;
	}

	private MultipleImageButton getNewMainRestartButton(){
		//play button
		MultipleImageButton homePlay = new MultipleImageButton(menuSkin,
				"yellowButton", playTexture);
		homePlay.setLogoHeight(MENU_BUTT_W_LOGO);

		homePlay.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Audios.getInstance().playClick();
				wr.restart();
			}
		});
		return homePlay;
	}

	private Actor generateGameOverContent() {
		Table gameOverTable = new Table();

		gameOverTable.setDebug(DEBUG);

		gameOverTable.add(scoreText).colspan(2);
		gameOverTable.row();
		gameOverTable.add(bestScoreText).padBottom(SCORE_P_BOT).colspan(2);
		gameOverTable.row();

//		gameOverTable.add(getNewLeaderboardButton()).width(CONTENT_BUTT_W * 0.8f)
//		.height(CONTENT_BUTT_W * 0.8f).padRight(MENU_BUTT_INTERSPACE*2);
		gameOverTable.add(getNewMainRestartButton()).width(CONTENT_BUTT_W * 1.7f)
				.height(CONTENT_BUTT_W * 0.8f);

		return gameOverTable;
	}

	private Actor generatePauseContent() {
		Table pauseContent = new Table();

		// Resume button
		ImageTextButton resumeButton = new ImageTextButton(Assets.getInstance()
				.getValue("resume"), menuSkin, "yellowTextButton");
		resumeButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Audios.getInstance().playClick();
				wr.unpause();
			}
		});

		// Reset game button
		MultipleImageButton resetGameButton = new MultipleImageButton(menuSkin,
				"greyButton", backTexture);

		resetGameButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Audios.getInstance().playClick();
				wr.restart();
			}
		});
		resetGameButton.setLogoHeight(MENU_BUTT_W_LOGO);

		// Best score label
		bestScoreLabelPause = new Label("Best : "
				+ wr.getScore().getBestScore(), menuSkin, "scoreGameOverStyle");


		// Fill pauseContent
		pauseContent.add(bestScoreLabelPause).padBottom(SCORE_P_BOT)
				.colspan(3);
		pauseContent.row();

		pauseContent.add(resetGameButton).width(CONTENT_BUTT_W * 0.8f)
				.height(CONTENT_BUTT_W * 0.8f).padRight(SCORE_P_BOT * 0.5f);
//		pauseContent.add(getNewLeaderboardButton()).width(CONTENT_BUTT_W * 0.8f)
//		.height(CONTENT_BUTT_W * 0.8f).padRight(SCORE_P_BOT * 0.5f);
		pauseContent.add(resumeButton).width(CONTENT_BUTT_W * 1.7f)
				.height(CONTENT_BUTT_W * 0.8f);

		return pauseContent;
	}

	private Table generateOptionContent() {
		Table optionContent = new Table();
		optionContent.bottom().padBottom(OPTION_P_BOT);
		optionContent.setDebug(DEBUG);

		music = new OnOffMultipleImageButton(
				Assets.getInstance().getMenuSkin(), "audioButton", musicOn,
				musicOff);
		music.setDisabled(!Audios.getInstance().isMusicOn());
		music.setLogoHeight(MENU_BUTT_W_LOGO * 3);
		music.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Audios.getInstance().switchMusicState();
				Audios.getInstance().playClick();
				music.setDisabled(!music.isDisabled());
			}
		});

		effects = new OnOffMultipleImageButton(Assets.getInstance()
				.getMenuSkin(), "audioButton", soundOn, soundOff);
		effects.setDisabled(!Audios.getInstance().isEffectsOn());
		effects.setLogoHeight(MENU_BUTT_W_LOGO * 3);
		effects.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Audios.getInstance().switchEffectsState();
				Audios.getInstance().playClick();
				effects.setDisabled(!effects.isDisabled());
			}
		});

//		useLeaderboard = new CheckBox(Assets.getInstance().getValue("option_leaderboard"), menuSkin, "checkboxStyle");
//		useLeaderboard.getCells().get(0).padRight(OPTION_P_BOT*0.5f);
//		useLeaderboard.setChecked(wr.isLeaderboardUsed());
//		useLeaderboard.addListener(new ClickListener() {
//			@Override
//			public void clicked(InputEvent event, float x, float y) {
//				super.clicked(event, x, y);
//				wr.setUseLeaderboard(useLeaderboard.isChecked());
//				if(useLeaderboard.isChecked()){
//					wr.submitBestScoreDirect();
//				}
//				PrefManager.saveBoolean(WorldRenderer.USE_LEADERBOARD_KEY, useLeaderboard.isChecked());
//			}
//		});


		Label author = new Label("ALEXANDRE GHOZZI - 2015", menuSkin,
				"defaultTextSmall");
//		author.setAlignment(Align.center);
        //TODO AGH

		optionContent.add(useLeaderboard).colspan(2).padBottom(OPTION_BUTT_P_BOT*0.9f);
		optionContent.row();

		optionContent.add(music).width(MENU_BUTT_W).height(MENU_BUTT_W);
		optionContent.add(effects).width(MENU_BUTT_W).height(MENU_BUTT_W);
		optionContent.row();

		optionContent.add(author).width(OPTION_CONTENT_W)
				.padTop(OPTION_BUTT_P_BOT).colspan(2);

		return optionContent;
	}

	public void updateCheckbox(boolean val){
		useLeaderboard.setChecked(val);
	}

	/**
	 * Get the label of the music button
	 */
	private String getMusicText() {
		return Assets.getInstance().getValue("music") + " : ";
	}

	/**
	 * Get the label of the effects button
	 */
	private String getEffectsText() {
		return Assets.getInstance().getValue("effects") + " : ";
	}

	/**
	 * Set visible the Actor of the Game Over content and hide others
	 */
	public void showGameOverContent() {
		homeContent.setVisible(false);
		pauseContent.setVisible(false);
		gameOverContent.setVisible(true);

		playerContent.setVisible(false);
		achievContent.setVisible(false);
		optionContent.setVisible(false);
	}

	/**
	 * Set visible the Actor of the Pause content and hide others
	 */
	public void showPauseContent() {
		homeContent.setVisible(false);
		pauseContent.setVisible(true);
		gameOverContent.setVisible(false);

		playerContent.setVisible(false);
		achievContent.setVisible(false);
		optionContent.setVisible(false);
	}

	/**
	 * Set visible the Actor of the Home content and hide others
	 */
	public void showHomeContent() {
		homeContent.setVisible(false);
		pauseContent.setVisible(false);
		gameOverContent.setVisible(true);

		playerContent.setVisible(false);
		achievContent.setVisible(false);
		optionContent.setVisible(false);
	}

	/**
	 * Update and draw the ui
	 *
	 * @param delta
	 * @param state
	 */
	public void render(float delta, GameState state) {
		updateUI(delta);
		drawUI(state);
	}

	private void updateUI(float delta) {
		stateTime += delta;

		menuStage.act();
		pauseStage.act();
		eventHandler.update(delta);
		// move for animations ?
	}

	/**
	 * Update the content of the players
	 */
	public void updateContents() {
		jumperHandler.reloadCells();

		// achievements are updated each event
	}

	private void drawUI(GameState state) {

		batch.setProjectionMatrix(uiCamera.combined);

		if (state == GameState.Home) {
			batch.begin();
			gameTitle.draw(batch);
			panel.draw(batch);
			batch.end();

			// draw stage outside of begin/end !
			menuStage.draw();
		}

		if (state == GameState.Start) {
			batch.begin();
			getReady.draw(batch);
//			batch.draw(tap.getKeyFrame(stateTime, true), SW / 2 - TAP_W / 2, SH
//					/ 2 - TAP_H / 2, TAP_W, TAP_H);
            //TODO AGH

			batch.end();
		}

		if (state == GameState.Running) {
			pauseStage.draw();
		}

		if (state == GameState.Pause) {
			batch.begin();
			paused.draw(batch);
			panel.draw(batch);
			batch.end();

			menuStage.draw();
		}

		if (state == GameState.GameOver) {
			batch.begin();
			gameOver.draw(batch);
			panel.draw(batch);
			batch.end();

			menuStage.draw();
		}

		if (showGameOverTitle && state != GameState.Pause) {
			batch.begin();
			gameOver.draw(batch);
			batch.end();
		}

		// Draw event notif
		eventHandler.draw(batch);
	}

	public void setUpLabels(int score, int best) {
		if (score == best) {
			// new best
			scoreText.setText("Score : " + score);
			bestScoreText.setText("NEW Best : " + best);
		} else {
			scoreText.setText("Score : " + score);
			bestScoreText.setText("Best : " + best);
		}
	}

	public void showGameOverTitle() {
		showGameOverTitle = true;
	}

	public void hideGameOverTitle() {
		showGameOverTitle = false;
	}

	public void resize(int w, int h) {
	}

	public void dispose() {
		menuStage.dispose();
		pauseStage.dispose();
	}

	public InputProcessor getMenuInputProcessor() {
		return menuStage;
	}

	public InputProcessor getPauseInputProcessor() {
		return pauseStage;
	}

	public Notif getEventHandler() {
		return eventHandler;
	}

	public JumperHandler getJumperHandler() {
		return jumperHandler;
	}

	public float getPauseButtonHeight() {
		return ((Table) pauseStage.getActors().get(0)).getCells().get(0)
				.getActorHeight();
	}
}
