package fr.zzi.yellowdwarf.model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import fr.zzi.yellowdwarf.controller.WorldRenderer;
import fr.zzi.yellowdwarf.model.jumper.BasicJumper;

/**
 * 
 * A single planet
 *
 */
public class Planet {
	public static final boolean DEBUG = WorldRenderer.DEBUG;
	public static final float SHADOW_RATIO = 1.6f;

	private TextureRegion texture;
	private Color color; // tint the texture
	private int id; // the position among all the planets
	private Vector2 pos;
	private Vector2 speed;
	private float thetaSpeed; // the speed rotation
	private float r; // the radius
	private float shadowR; // the radius of the shadow
	private float theta; // the rotation
	private boolean clockwise; // turning clockwise or not
	private boolean isAspirated;

	public Planet(TextureRegion texture, Color color, int id, Vector2 position,
			float thetaSpeed, float r, boolean clockwise) {
		this.texture = texture;
		this.color = color;
		this.id = id;
		this.pos = position;
		this.speed = new Vector2(0, 0);
		this.thetaSpeed = thetaSpeed;
		this.r = r;
		this.shadowR = r * SHADOW_RATIO;
		this.theta = 0;
		this.clockwise = clockwise;
		this.isAspirated = false;
	}

	public void draw(SpriteBatch batch, TextureRegion shadow) {
		batch.setColor(color);
		batch.draw(texture, pos.x - r, pos.y - r, r, r, r * 2, r * 2, 1f, 1f,
				theta, clockwise);
		batch.setColor(Color.WHITE); // default color

		// draw the shadow
		batch.draw(shadow, pos.x - shadowR, pos.y - shadowR, shadowR, shadowR,
				shadowR * 2, shadowR * 2, 1f, 1f, 90, true);
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
				+ player.getWidth() / 2 - PlanetHandler.COLLIDE_JUMPER_MARGIN / 2)
				* (r + player.getWidth() / 2 - PlanetHandler.COLLIDE_JUMPER_MARGIN / 2)) {
			// if we are not jumping off this p
			if (!(this.equals(player.getCurrentPlanet()))
					|| (this.equals(player.getCurrentPlanet()) && (System
							.currentTimeMillis() - player.getLastPlanetJump() > BasicJumper.MIN_JUMP_TIME))) {

				player.landOnPlanet(this);

				return true;
			}
		}
		return false;
	}

	public void move(float delta, BlackHole bh) {
		if (clockwise) {
			theta += thetaSpeed;
		} else {
			theta -= thetaSpeed;
		}
		if (isAspirated) {
			float angleRad = MathUtils.atan2(bh.getY() - getY(), bh.getX()
					- getX());
			float distSquare = (getX() - bh.getX()) * (getX() - bh.getX())
					+ (getY() - bh.getY()) * (getY() - bh.getY());
			speed.x = BlackHole.PLANET_ATTRACT_SPEED * MathUtils.cos(angleRad)
					/ distSquare * BlackHole.PLANET_ATTRACT_SPEED_CONSTANT;
			speed.y = BlackHole.PLANET_ATTRACT_SPEED * MathUtils.sin(angleRad)
					/ distSquare * BlackHole.PLANET_ATTRACT_SPEED_CONSTANT;
			if (speed.y < -BlackHole.PLANET_ATTRACT_MAX_SPEED) {
				speed.y = -BlackHole.PLANET_ATTRACT_MAX_SPEED;
				// we don't really to do the other limit speed test
			}

			pos.mulAdd(speed, delta);
			if (r <= BlackHole.PLANET_RADIUS_MIN) {
				r = BlackHole.PLANET_RADIUS_MIN;
				shadowR = r * SHADOW_RATIO;
			} else {
				float thetaSpeed = BlackHole.PLANET_RADIUS_SPEED
						/ (delta * distSquare);
				if (thetaSpeed > BlackHole.PLANET_RADIUS_MAX_SPEED) {
					// thetaSpeed = BlackHole.PLANET_RADIUS_MAX_SPEED;
				}
				r -= thetaSpeed;
				shadowR = r * SHADOW_RATIO;
			}
		}
	}

	public void moveTurnOnly(float delta) {
		if (clockwise) {
			theta += thetaSpeed;
		} else {
			theta -= thetaSpeed;
		}
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

	public Vector2 getSpeed() {
		return speed;
	}

	public void setSpeed(Vector2 speed) {
		this.speed = speed;
	}

	public float getThetaSpeed() {
		return thetaSpeed;
	}

	public void setThetaSpeed(float thetaSpeed) {
		this.thetaSpeed = thetaSpeed;
	}

	public float getR() {
		return r;
	}

	public void setR(float r) {
		this.r = r;
	}

	public float getTheta() {
		return theta;
	}

	public void setTheta(float theta) {
		this.theta = theta;
	}

	public boolean isClockwise() {
		return clockwise;
	}

	public void setClockwise(boolean clockwise) {
		this.clockwise = clockwise;
	}

	public boolean isAspirated() {
		return isAspirated;
	}

	public void setAspirated(boolean isAspirated) {
		this.isAspirated = isAspirated;
	}

	public int getId() {
		return id;
	}

}
