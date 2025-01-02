package fr.zzi.yellowdwarf.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import fr.zzi.yellowdwarf.controller.GlobalCallback.YesNoDialogCallback;
import fr.zzi.yellowdwarf.controller.utils.Assets;
import fr.zzi.yellowdwarf.controller.utils.Audios;
import fr.zzi.yellowdwarf.controller.utils.PrefManager;
import fr.zzi.yellowdwarf.controller.utils.Score;
import fr.zzi.yellowdwarf.model.Background;
import fr.zzi.yellowdwarf.model.BlackHole;
import fr.zzi.yellowdwarf.model.PlanetHandler;
import fr.zzi.yellowdwarf.model.Walls;
import fr.zzi.yellowdwarf.model.boost.VerticalBoostHandler;
import fr.zzi.yellowdwarf.model.jumper.BasicJumper;
import fr.zzi.yellowdwarf.model.jumper.BasicJumper.State;
import fr.zzi.yellowdwarf.model.jumper.RainbowJumper;
import fr.zzi.yellowdwarf.model.jumper.RocketJumper;
import fr.zzi.yellowdwarf.model.jumper.SpacyJumper;

/**
 *
 * The heart of the game
 *
 */
public class WorldRenderer implements InputProcessor {
	public static final boolean DEBUG = false;
	public static int SH = Gdx.graphics.getHeight();
	public static int SW = Gdx.graphics.getWidth();


	public static int MAX_FPS = 200;
	public static String MEMORY_INITIATED_KEY = "memory_initiated";
	public static String LEADBOARD_INIT_KEY = "leadboard_init_dialog_shown";
	public static String USE_LEADERBOARD_KEY = "use_leaderboard";
	public static String SUBMIT_NEXT_TIME_KEY = "submit_next_time";

	//time needed to forget that the user pressed back
	public static long TIME_FOR_RELEASE_BACK_EXIT = 2000;


	// the duration of the black hole zoom
	// public static long CUTSCENE_BLACKHOLE_POSE = 2000;

	// public static final float CAM_MIN_MOVE = 0.01f;
	// public static final float CAM_MAX_SPEED = Jumper.MAX_SPEED * .005f;
	public static final float CAM_SPEED = WorldRenderer.SW * 0.5f;
	public static final float CAM_RETREAT = SH / 10;

	private GlobalCallback callback;
	private SpriteBatch batch;
	private OrthographicCamera camera;
	private float camDelta;

	// Model
	private PlanetHandler pHandler;
	private VerticalBoostHandler boostHandler;
	private BasicJumper player;
	private BlackHole blackHole;
	private Walls walls;
	private Background background;

	private UIRenderer uiRenderer;
	private InputMultiplexer inputMultiplexer = new InputMultiplexer();
	private long lastBackPressed;
	private long timeToSleep;	//time to sleep for the thread if too much fps

	private GameState gameState;
	private Score score;

	private boolean shownLeaderboardAlert;
	private boolean useLeaderboard;
	private boolean submitNextTime;

	private long cutsceneStart; // time of begining of the cutscene
	private boolean cutsceneShown;
	private long now;

	public enum GameState {
		Home, // home screen
		Cutscene, // show the bh and come back to the player
		Start, // get ready
		Pause, // game paused
		Running, // game being played
		GameOver // game over
	}

	/**
	 * Init the renderer
	 */
	public void init(GlobalCallback gcb) {
		Audios.getInstance().initInstance();
		checkFirstLaunch();

		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, SW, SH);
		camera.update();
		camDelta = 0;

		pHandler = new PlanetHandler(this);
		boostHandler = new VerticalBoostHandler(this);
		blackHole = new BlackHole();

		walls = new Walls();
		background = new Background();

		score = new Score(this);
		gameState = GameState.Start;
		uiRenderer = new UIRenderer(this, batch);
		callback = gcb;
		timeToSleep = 0;

		// don't change order :
		inputMultiplexer.addProcessor(uiRenderer.getPauseInputProcessor());
		inputMultiplexer.addProcessor(this);
		inputMultiplexer.addProcessor(uiRenderer.getMenuInputProcessor());
		Gdx.input.setInputProcessor(inputMultiplexer);
        Gdx.input.setCatchKey(Input.Keys.BACK, true);
		gameState = GameState.Home;
		cutsceneShown = false;
		lastBackPressed = System.currentTimeMillis() - TIME_FOR_RELEASE_BACK_EXIT;

		shownLeaderboardAlert = PrefManager.getBoolean(LEADBOARD_INIT_KEY);
		submitNextTime = PrefManager.getBoolean(SUBMIT_NEXT_TIME_KEY);

		// play music
		Audios.getInstance().playGlobalMusic();

		player = getNewJumper();
		resetWorld();
	}

	/**
	 * At fist launch of the game, init memory
	 */
	private void checkFirstLaunch() {
		if (PrefManager.getBoolean(MEMORY_INITIATED_KEY) == false) {
			System.out.println("Init memory");
			// Init player content
			JumperHandler.initPlayerConfig();

			// Init achievements
			AchvHandler.initAchvConfig();

			// Init sound config
			Audios.getInstance().initVolumes();

			// Init leaderboard config
			useLeaderboard = true;
			PrefManager.saveBoolean(USE_LEADERBOARD_KEY, true);

			PrefManager.saveBoolean(MEMORY_INITIATED_KEY, true);

		}else{
			useLeaderboard = PrefManager.getBoolean(USE_LEADERBOARD_KEY);
		}
	}

	/**
	 * Unlock all players in the game
	 */
	public void unlockAllJumpers(){
		uiRenderer.getJumperHandler().unlockAllJumpers();

		Gdx.app.exit();
	}

	/**
	 * Return the proper selecter perso
	 */
	private BasicJumper getNewJumper(){
		switch (uiRenderer.getJumperHandler().getSelectedJumperId()) {
		case 1:
			//yellow dwarf
			return new BasicJumper(this, State.Jumping);

		case 2:
			//Rocket
			return new RocketJumper(this, State.Jumping);

		case 3:
			//Spacy bird
			return new SpacyJumper(this, State.Jumping);

		case 4:
			//Rainbow star
			return new RainbowJumper(this, State.Jumping);

		default:
			return new BasicJumper(this, State.Jumping);
		}
	}

	/**
	 * Return the proper selecter perso
	 * @return
	 */
	private BasicJumper getNewJumper(BasicJumper bj){
		switch (uiRenderer.getJumperHandler().getSelectedJumperId()) {
		case 1:
			//yellow dwarf
			return new BasicJumper(bj, this, State.Jumping);

		case 2:
			//Rocket
			return new RocketJumper(bj, this, State.Jumping);

		case 3:
			//Spacy bird
			return new SpacyJumper(bj, this, State.Jumping);

		case 4:
			//Rainbow star
			return new RainbowJumper(bj, this, State.Jumping);

		default:
			return new BasicJumper(bj, this, State.Jumping);
		}
	}

	/**
	 * Copy the current player and copy its parameters to the selected one
	 */
	public void updateJumper(){
		player = getNewJumper(player);
	}

	/**
	 * Reset the world
	 */
	private void resetWorld() {
		boostHandler.reset();
		pHandler.reset();
		player.reset(pHandler.get(BasicJumper.STARTING_PLANET));
		blackHole.reset();

		walls.reset();
		background.reset();

		score.reset(player.getY());
		camera.position.y = player.getY() + CAM_RETREAT;
		camDelta = 0;
	}

	/**
	 * Update and draw the world
	 *
	 * @param delta
	 */
	public void render(float delta) {

		updateWorld(delta);
		drawWorld();
		updateGameState();

		// draw ui in last !
		uiRenderer.render(delta, gameState);

		if (Gdx.graphics.getFramesPerSecond() < 40
				&& Gdx.graphics.getFramesPerSecond() != 0) {
			if (DEBUG)
				System.out.println(Gdx.graphics.getFramesPerSecond());
		}

		limitFps();
	}

	private void limitFps(){
		//limit the number of fps
		timeToSleep = (long)(1000/MAX_FPS-Gdx.graphics.getDeltaTime());
		try {
			if(timeToSleep >0)
				Thread.sleep(timeToSleep);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Update the state of the game
	 */
	private void updateGameState() {
		// update achievements based on time
		if (gameState == GameState.Running) {
			score.update(player);
		}

		// game over full
		if (player.getState() == State.Exploded
				&& gameState != GameState.GameOver) {
			// begin of game over
			uiRenderer.hideGameOverTitle();
			uiRenderer.showGameOverContent();
			uiRenderer.setUpLabels(score.getScore(), score.getBestScore());
			gameState = GameState.GameOver;

			if(shownLeaderboardAlert == false){
				//at first game over we offer to submit the score
				shownLeaderboardAlert = true;
				PrefManager.saveBoolean(LEADBOARD_INIT_KEY, true);

				//show dialog
				callback.showPublishScoresDialog(new YesNoDialogCallback() {

					@Override
					public void yesClicked() {
						useLeaderboard = true;
						PrefManager.saveBoolean(USE_LEADERBOARD_KEY, useLeaderboard);
						callback.showLeaderboards();
						callback.submitScoreNextResume(score.getScore());
						uiRenderer.updateCheckbox(useLeaderboard);
					}

					@Override
					public void noClicked() {
						useLeaderboard = false;
						PrefManager.saveBoolean(USE_LEADERBOARD_KEY, useLeaderboard);
						uiRenderer.updateCheckbox(useLeaderboard);
						//nothing to publish
					}
				});
			}else{
				submitScore();
			}

			uiRenderer.updateContents();
		}
	}

	public void submitScore(){
		//use the user option
		if(useLeaderboard == true){
			//if new record or need to submit
			if(score.getScore() >= score.getBestScore() || submitNextTime){
				//and there is network
				if(callback.isNetworkConnected()){
					//submit
					callback.submitScore(score.getBestScore());

					if(submitNextTime == true){
						submitNextTime = false;
						PrefManager.saveBoolean(SUBMIT_NEXT_TIME_KEY, submitNextTime);
					}

				}else{
					submitNextTime =true;
					PrefManager.saveBoolean(SUBMIT_NEXT_TIME_KEY, submitNextTime);
				}
			}
		}
	}

	public void submitBestScoreDirect(){
		callback.submitScore(score.getBestScore());
	}

	/**
	 * Start the game from Home state to Start or show cutscene 1 time
	 *
	 * @return true if the state has been changed
	 */
	public boolean start() {
		if (gameState == GameState.Home || gameState == GameState.Cutscene) {
			if (cutsceneShown == false) {
				// start cutscene

				gameState = GameState.Cutscene;
				blackHole.setY(-200.f);
				cutsceneStart = System.currentTimeMillis();
				camera.position.y = blackHole.getY() + CAM_RETREAT;
			} else {
				gameState = GameState.Start;
				// resetWorld();
			}
			return true;
		}
		return false;
	}

	/**
	 * Restart game from game over
	 */
	public void restart() {
		if (gameState == GameState.GameOver || gameState == GameState.Pause) {
			gameState = GameState.Start;
			resetWorld();
		}
	}

	/**
	 * Pause game and show menu
	 */
	public void pause() {
		if (gameState == GameState.Running) {
			if (player.getState() == State.Dead
					|| player.getState() == State.Exploded) {
				// if killed directly go to gameover
				uiRenderer.hideGameOverTitle();
				uiRenderer.showGameOverContent();
				uiRenderer.setUpLabels(score.getScore(), score.getBestScore());
				gameState = GameState.GameOver;
			} else {
				gameState = GameState.Pause;
				uiRenderer.showPauseContent();
			}
		}
		uiRenderer.updateContents();
	}

	/**
	 * Resume game and exit pause
	 */
	public void unpause() {
		if (gameState == GameState.Pause) {
			gameState = GameState.Running;
			score.unpause();
		}
	}

	/**
	 * Begin the gameover animation but let shown player exploding
	 */
	public void beginGameOver() {
		uiRenderer.showGameOverTitle();
	}

	private void updateWorld(float delta) {
		// always update blackhole volume
		blackHole.updateBlackholeVolume(
				camera.position.y > blackHole.getY() ? camera.position.y
						- blackHole.getY() : blackHole.getY()
						- camera.position.y, gameState);

		if (gameState == GameState.Home) {
			updateCamera(delta);
			pHandler.updateTurnOnly(delta, player);
			boostHandler.updateBoosts(delta);
			// player.updateEffect(delta);
		}
		if (gameState == GameState.Cutscene) {
			updateCamera(delta);
			pHandler.updateCutscene(delta, player, blackHole);
			boostHandler.updateBoosts(delta);
			blackHole.update(delta, pHandler.getPlanets(), player);
			background.update(camDelta, player.getState(), gameState);
			// player.updateEffect(delta);
		}
		if (gameState == GameState.Start) {
			updateCamera(delta);
			pHandler.updateTurnOnly(delta, player);
			boostHandler.updateBoosts(delta);
			player.getEffects().updateTailEffect(delta);
		}
		if (gameState == GameState.Pause) {
			updateCamera(delta);
		}
		if (gameState == GameState.Running) {
			updateCamera(delta);
			pHandler.update(delta, player, blackHole);
			boostHandler.updateBoosts(delta);
			player.update(delta, pHandler, walls, blackHole);
			blackHole.update(delta, pHandler.getPlanets(), player);
			background.update(camDelta, player.getState(), gameState);
		}
		if (gameState == GameState.GameOver) {
			pHandler.update(delta, player, blackHole);
			boostHandler.updateBoosts(delta);
			blackHole.update(delta, pHandler.getPlanets(), player);
			player.update(delta, pHandler, walls, blackHole);
		}

	}

	private void updateCamera(float delta) {
		if (gameState != GameState.Cutscene) {
			// defalut cam behaviour
			if (player.getState() == State.Turning) {
				// following the player' planet
				camDelta = (player.getCurrentPlanet().getY() + CAM_RETREAT - camera.position.y) / 10;
			} else {
				// following the player
				camDelta = (player.getY() + CAM_RETREAT - camera.position.y) / 10;
			}
		} else {
			// move cam for cutscene
			now = System.currentTimeMillis();
			// if close to player, end cutscene
			if (camera.position.y > player.getCurrentPlanet().getY()) {
				cutsceneShown = true;
				blackHole.moveBack();
				start();
				return;
			}
			// move to player
			camDelta = delta * CAM_SPEED;
		}
		camera.position.y += camDelta;
		camera.update();
	}

	private void drawWorld() {
		batch.setProjectionMatrix(camera.combined);
		batch.begin();

		// batch.disableBlending();
		background.draw(batch, camera.position.y);
		// batch.enableBlending();

		boostHandler.draw(batch, player);
		blackHole.draw(batch);
		pHandler.draw(batch, player); // pHandler.getPlanets().indexOf(player.getCurrentPlanet())
		walls.draw(batch, camera);

		if (gameState == GameState.Running) {
			player.draw(batch);
			score.drawCurrent(batch, camera.position.x, camera.position.y);
		}

		if (gameState == GameState.Pause) {
			player.draw(batch);
			score.drawCurrent(batch, camera.position.x, camera.position.y);
		}
		if (gameState == GameState.GameOver) {
			player.draw(batch); // for the particles
		}

		batch.end();
	}

	public void resize(int w, int h) {
		uiRenderer.resize(w, h);
	}

	public void dispose() {
		uiRenderer.dispose();
		batch.dispose();
	}

	@Override
	public boolean keyDown(int keycode) {
        if(keycode == Keys.ESCAPE || keycode == Keys.BACK){
        	//On back pressed
        	if(gameState == GameState.Running){
        		//Pause
        		pause();
        	}else if(gameState == GameState.Cutscene || gameState == GameState.Start){
        		//start
        		touchDown(0, 0, 0, 0);
        	}else if(gameState == GameState.GameOver || gameState == GameState.Home || gameState == GameState.Pause){
        		if(System.currentTimeMillis() - TIME_FOR_RELEASE_BACK_EXIT > lastBackPressed){
        			if(keycode == Keys.ESCAPE){
            			Gdx.app.exit();
        			}else{
        				//show toast
        				callback.showToast(Assets.getInstance().getValue("press_again"));
        				lastBackPressed = System.currentTimeMillis();
        			}
        		}else{
        			//exit game
        			Gdx.app.exit();
        		}
        	}
        }else if(keycode == Keys.SPACE || keycode == Keys.ENTER){
        	if(gameState == GameState.GameOver || gameState == GameState.Pause){
        		//launch game
        		restart();
        	}else if(gameState == GameState.Home){
        		//launch game
        		start();
        	}else {
        		//simulate click in the midle of the screen
        		touchDown(SW/2, SH/2, 0, 0);
        	}

        }
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (gameState == GameState.Cutscene) {
			gameState = GameState.Start;
			return true;
		}
		if (gameState == GameState.Start) {
			gameState = GameState.Running;
			return true;
		}
		if (gameState == GameState.Running) {
			player.onTouchEvent();
			return true;
		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

	public Score getScore() {
		return score;
	}

	public GameState getState() {
		return gameState;
	}

	public AchvHandler getAchvHandler() {
		return getScore().getAchvHandler();
	}

	public UIRenderer getUIRenderer() {
		return uiRenderer;
	}

	public BasicJumper getJumper() {
		return player;
	}

	public VerticalBoostHandler getVerticalBoostHandler(){
		return boostHandler;
	}

	public GlobalCallback getGlobalCallback(){
		return callback;
	}

	public boolean isLeaderboardUsed(){
		return useLeaderboard;
	}

	public void setUseLeaderboard(boolean val){
		useLeaderboard = val;
	}
}
