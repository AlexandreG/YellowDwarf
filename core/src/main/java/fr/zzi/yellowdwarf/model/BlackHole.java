package fr.zzi.yellowdwarf.model;

import java.util.LinkedList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import fr.zzi.yellowdwarf.controller.WorldRenderer;
import fr.zzi.yellowdwarf.controller.WorldRenderer.GameState;
import fr.zzi.yellowdwarf.controller.utils.Assets;
import fr.zzi.yellowdwarf.controller.utils.Audios;
import fr.zzi.yellowdwarf.model.jumper.BasicJumper;

/**
 * 
 * The main enemy of the game
 *
 */
public class BlackHole {
	public static final boolean DEBUG = WorldRenderer.DEBUG;

	public static final float INITIAL_Y = -100;
	
	public static final float THETA_SPEED = 3;
	
	public static final float INITIAL_SPEED_FACTOR = WorldRenderer.SW * 0.20f;
	public static final float LINEAR_SPEED_FACTOR = WorldRenderer.SW*1.2f;
	public static final float ATTRACTING_SPEED_FACTOR = 5 * WorldRenderer.SW / 2;

	public static final float PLANET_ATTRACT_SPEED = WorldRenderer.SW * 1f;
	public static final float PLANET_ATTRACT_MAX_SPEED = WorldRenderer.SW * 0.57f;
	public static final float PLANET_ATTRACT_SPEED_CONSTANT = 30000;
	public static final float PLANET_RADIUS_SPEED = WorldRenderer.SW * 2f;
	public static final float PLANET_RADIUS_MAX_SPEED = WorldRenderer.SW * 3f;
	public static final float PLANET_RADIUS_MIN = PlanetHandler.MIN_RADIUS / 2;

	public static final float JUMPER_ATTRACT_SPEED = WorldRenderer.SW * 1.07f;

	public static final float DISAPPEARING_Y = WorldRenderer.SW / 16;
	public static final float DISAPPEARING_R = DISAPPEARING_Y;
	
	//the distance where the black hole is silent
	public static final float SILENT_DISTANCE = WorldRenderer.SH*2;

	private TextureRegion texture;

	private float radius;
	private float theta; // turning on it self
	private float thetaSpeed;
	private Vector2 pos;
	private Vector2 speed;
	private int eatenPlanets;	//number of planets eaten
	private float attractingY;	// distance for attraction

	public BlackHole() {
		texture = Assets.getInstance().getGameRegion("blackhole");

		pos = new Vector2();
		speed = new Vector2();
		theta = 0;
		thetaSpeed = THETA_SPEED;
		
		Audios.getInstance().playBlackHole();
	}

	public void reset() {
		radius = WorldRenderer.SW / 6;
		pos.x = WorldRenderer.SW / 2;
		pos.y = INITIAL_Y;
		speed.x = 0;
		speed.y = INITIAL_SPEED_FACTOR;
		eatenPlanets = 0;
		attractingY = WorldRenderer.SW * 2;
	}
	
	public void moveBack(){
		pos.y = INITIAL_Y;
	}

	public void draw(SpriteBatch batch) {
		// the black hole
		batch.draw(texture, pos.x - radius, pos.y - radius, radius, radius,
				radius * 2, radius * 2, 1f, 1f, theta, true);
	}

	/**
	 * Move the blackhole 
	 */
	public void update(float delta, LinkedList<Planet> p, BasicJumper player) {
		theta += thetaSpeed;
		pos.mulAdd(speed, delta);
	}

	/**
	 * Cutscene mode : turn only
	 */
	public void updateCutscene(float delta, LinkedList<Planet> p, BasicJumper player) {
		theta += thetaSpeed;
	}
	
	/**
	 * Update the volume of the blackhole sound with the cam farness
	 * @param camD the distance to the game camera
	 */
	public void updateBlackholeVolume(float camD, GameState state){
		if(camD > SILENT_DISTANCE || state == GameState.Home|| state == GameState.Pause){
			Audios.getInstance().setBlackholeVolume(0.f);
		}else{
			Audios.getInstance().setBlackholeVolume((1-camD/SILENT_DISTANCE)*0.7f);
		}
	}

	public float getX() {
		return pos.x;
	}

	public float getY() {
		return pos.y;
	}
	
	public void setY(float y) {
		pos.y = y;
	}

	public float getRadius() {
		return radius;
	}

	/**
	 * The blackhole has swallowed a planet
	 */
	public void planetEaten() {
		++eatenPlanets;

		// speed increase delayed
		if (eatenPlanets > 6) {
			// each 5 planets, small speed boost
			if (eatenPlanets % 5 == 0) {
				speed.y += LINEAR_SPEED_FACTOR / (eatenPlanets - 5);
				attractingY += ATTRACTING_SPEED_FACTOR / (eatenPlanets - 5);
			}
		}
	}

	public float getAttractingY() {
		return attractingY;
	}

	public void setAttractingY(float attractingY) {
		this.attractingY = attractingY;
	}

}
