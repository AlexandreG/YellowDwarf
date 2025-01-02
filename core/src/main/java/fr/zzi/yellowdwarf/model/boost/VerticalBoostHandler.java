package fr.zzi.yellowdwarf.model.boost;

import java.util.LinkedList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.math.MathUtils;

import fr.zzi.yellowdwarf.controller.WorldRenderer;
import fr.zzi.yellowdwarf.controller.utils.Assets;
import fr.zzi.yellowdwarf.model.jumper.BasicJumper;

public class VerticalBoostHandler {
	public static final int NB_BOOST_PER_PLANET = 13;

	//the init value of the arithmetic sequence of planet to elapse
	public static final float NB_PLANET_TO_ELAPSE_AT_FIRST = 1.5f;
	//the reason of the arithmetic sequence of planet to elapse
	public static final float NB_PLANET_TO_ELAPSE_EACH_TIME_MORE = 0.5f;

	public static final float INITIAL_BOOST_SPEED = 1.9f*BasicJumper.MAX_SPEED;
	public static final float BOOST_SPEED_MAGIC_RATIO = 0.93f;


	private WorldRenderer wr;

	private LinkedList<VerticalBoost> boostList;

	// all boosts have the same frames and speed, lets keep it here
	private Animation frames;
	private float stateTime;

	public VerticalBoostHandler(WorldRenderer renderer) {
		wr = renderer;

		boostList = new LinkedList<VerticalBoost>();

		TextureRegion frame1 = Assets.getInstance().getGameRegion("boost1");
		TextureRegion frame2 = Assets.getInstance().getGameRegion("boost2");

		frame1.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		frame2.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);

		frames = new Animation(0.6f, new TextureRegion(frame1),
				new TextureRegion(frame2));
		frames.setPlayMode(PlayMode.LOOP);
	}

	/**
	 * Empty the list of boost
	 */
	public void reset() {
		boostList.clear();
	}

	/**
	 * Draw the boost
	 *
	 * @param batch
	 * @param player
	 */
	public void draw(SpriteBatch batch, BasicJumper player) {
		// boosts
		for (VerticalBoost b : boostList) {
			// if rather close to the camera
			if ((b.getY() < player.getY() + 2 * WorldRenderer.SH)
					&& (b.getY() > player.getY() - 2 * WorldRenderer.SH)) {
				b.draw(batch, (TextureRegion) frames.getKeyFrame(stateTime));
			}
		}
	}

	/**
	 * Update the vertical boosts
	 *
	 * @param delta
	 */
	public void updateBoosts(float delta) {
		// update boosts
		stateTime += delta;
	}

	/**
	 * Add a vertical boost at the given coordinates
	 * @param x
	 * @param y
	 */
	public void addBoost(float x, float y) {
		boostList.add(new VerticalBoost(x, y));
	}

	/**
	 * Test if a boost collide with the player then return true
	 * @param bj to test
	 * @return -1 if no collision, the id of the boost else(between 1 and size)
	 */
	public int collideWithPlayer(BasicJumper bj){
		for(int i=0 ; i<boostList.size() ; ++i){
			if(boostList.get(i).collide(bj)){
				return i+1;
			}
		}
		return -1;

		//boolean version
//		for (VerticalBoost boost : boostList) {
//			if(boost.collide(bj)){
//				return true;
//			}
//		}
//		return false;
	}

	/**
	 * @return the number of empty planet after the boost
	 */
	public float getNbPlanetToElapse(){
		//arithmetic sequence
		return boostList.size()*NB_PLANET_TO_ELAPSE_EACH_TIME_MORE+NB_PLANET_TO_ELAPSE_AT_FIRST;
	}

	/**
	 * @return the number of empty planet after the given boost
	 * @param boostId the id of the boost between 1 and boostList.size()
	 */
	public float getNbPlanetToElapse(int boostId){
		//arithmetic sequence
		return boostId*NB_PLANET_TO_ELAPSE_EACH_TIME_MORE+NB_PLANET_TO_ELAPSE_AT_FIRST;
	}

	/**
	 * Return the speed to apply on the jumper
	 * @param boostId the id of the boost between 1 and boostList.size()
	 */
	public float getVerticalBoostSpeed(int boostId){
		//make it proportionnal to the elapsed planets
		return INITIAL_BOOST_SPEED*(float)getNbPlanetToElapse(boostId)/((float)NB_PLANET_TO_ELAPSE_AT_FIRST+1)*(float)Math.pow(BOOST_SPEED_MAGIC_RATIO, boostId);
	}
}
