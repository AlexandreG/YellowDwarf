package fr.zzi.yellowdwarf.controller.utils;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;

/**
 * 
 * Handle the music and effects
 * 
 */
public class Audios {
	private static final String MUSIC_KEY = "music_on";
	private static final String EFFECTS_KEY = "effects_on";

	private static Audios INSTANCE = new Audios();

	private boolean musicOn;
	private boolean effectsOn;

	private Sound click;
	private Sound jump;
	private Sound explode;
	
	private Music blackHole;
	private Music globalMusic;

	/**
	 * Init some vars of the instance
	 */
	public void initInstance() {
		musicOn = PrefManager.getBoolean(MUSIC_KEY);
		effectsOn = PrefManager.getBoolean(EFFECTS_KEY);

		click = Assets.getInstance().getManager()
				.get("audio/click.ogg", Sound.class);
		jump = Assets.getInstance().getManager()
				.get("audio/jump.wav", Sound.class);
		explode = Assets.getInstance().getManager()
				.get("audio/explosion.ogg", Sound.class);
		
		blackHole = Assets.getInstance().getManager()
				.get("audio/blackhole.ogg", Music.class);
		globalMusic = Assets.getInstance().getManager()
				.get("audio/music.ogg", Music.class);
	}

	public static Audios getInstance() {
		return INSTANCE;
	}

	public void playBlackHole(){
		if(!blackHole.isPlaying() && effectsOn){
			blackHole.setVolume(0.f);
			blackHole.play();
			blackHole.setLooping(true);
		}
	}
	
	public void pauseBlackHole(){
		blackHole.pause();
	}
	
	public void setBlackholeVolume(float vol){
		blackHole.setVolume(vol*1.2f);
	}
	
	public void playGlobalMusic(){
		if(!globalMusic.isPlaying() &&musicOn){
			globalMusic.setVolume(0.7f);
			globalMusic.play();
			globalMusic.setLooping(true);
		}
	}
	
	public void pauseGlobalMusic(){
		globalMusic.pause();
	}
	
	public void playClick() {
		if (effectsOn)
			click.play(0.7f);
	}
	
	public void playExplode() {
		if (effectsOn)
			explode.play(1.0f);
	}
	
	public void playJump() {
		if (effectsOn){
//			int r = MathUtils.random(1, 3);
//			if(r == 1)
//				jump1.play();
//			if(r == 2)
//				jump2.play();
//			if(r == 3)
//				jump3.play();
		    // stops the sound instance immediately
			long id = jump.play(1.f);
			jump.setPitch(id, MathUtils.random(1.5f)+0.5f);
			
		}
	}

	/**
	 * Switch on or off the sounds effects
	 */
	public void switchEffectsState() {
		effectsOn = !effectsOn;
		
		//switch Music
		if(effectsOn){
			playBlackHole();
		}else{
			pauseBlackHole();
		}
		
		PrefManager.saveBoolean(EFFECTS_KEY, effectsOn);
	}

	/**
	 * Switch on or off the music
	 */
	public void switchMusicState() {
		musicOn = !musicOn;

		//switch Music
		if(musicOn){
			playGlobalMusic();
		}else{
			pauseGlobalMusic();
		}
		
		PrefManager.saveBoolean(MUSIC_KEY, musicOn);
	}

	/**
	 * Init the memory with default config
	 */
	public void initVolumes() {
		effectsOn = true;
		musicOn = true;
		PrefManager.saveBoolean(EFFECTS_KEY, effectsOn);
		PrefManager.saveBoolean(MUSIC_KEY, musicOn);
	}
	
	public boolean isMusicOn() {
		return musicOn;
	}

	public boolean isEffectsOn() {
		return effectsOn;
	}

	/**
	 * Release audios files
	 */
	public void dispose() {
		click.dispose();
		jump.dispose();
		explode.dispose();
		blackHole.dispose();
		globalMusic.dispose();
	}
}
