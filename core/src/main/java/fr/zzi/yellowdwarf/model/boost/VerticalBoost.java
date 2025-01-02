package fr.zzi.yellowdwarf.model.boost;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import fr.zzi.yellowdwarf.controller.WorldRenderer;
import fr.zzi.yellowdwarf.controller.utils.Assets;
import fr.zzi.yellowdwarf.model.PlanetHandler;
import fr.zzi.yellowdwarf.model.jumper.BasicJumper;

/**
 * 
 * A single vertical boost
 *
 */
public class VerticalBoost {
	public static final boolean DEBUG = WorldRenderer.DEBUG;
	
	public static final float RADIUS = WorldRenderer.SW * 0.23f;
	public static final float COLLIDE_JUMPER_MARGIN = RADIUS*1.2f;
	
	private Vector2 pos;
	private float r; // the radius

	public VerticalBoost(float x, float y) {
		pos = new Vector2(x, y);
		r = RADIUS;
	}

	/**
	 * Draw the given frame of the boost
	 * @param batch
	 * @param frame
	 */
	public void draw(SpriteBatch batch, TextureRegion frame) {
		batch.draw(frame, pos.x - r, pos.y - r, r, r, r * 2, r * 2, 1f, 1f,
				0, true);
	}

	/**
	 * Test if the player collide with the planet
	 * 
	 * @param player
	 * @return
	 */
	public boolean collide(BasicJumper player) {

		// if inside p
		if ((pos.y - player.getY()) * (pos.y - player.getY())
				+ (pos.x - player.getX()) * (pos.x - player.getX()) < (r
				+ player.getWidth() / 2 - COLLIDE_JUMPER_MARGIN / 2)
				* (r + player.getWidth() / 2 - COLLIDE_JUMPER_MARGIN / 2)) {
			// if we are not jumping off this p
			if (!(this.equals(player.getCurrentPlanet()))
					|| (this.equals(player.getCurrentPlanet()) && (System
							.currentTimeMillis() - player.getLastPlanetJump() > BasicJumper.MIN_JUMP_TIME))) {

				player.boostVertically();

				return true;
			}
		}
		return false;
	}

	public Vector2 getPos() {
		return pos;
	}

	public void setPos(Vector2 position) {
		this.pos = position;
	}

	public float getX() {
		return pos.x;
	}

	public float getY() {
		return pos.y;
	}

	public float getR() {
		return r;
	}

	public void setR(float r) {
		this.r = r;
	}


}
