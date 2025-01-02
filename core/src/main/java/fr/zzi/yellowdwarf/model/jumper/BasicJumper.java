package fr.zzi.yellowdwarf.model.jumper;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import fr.zzi.yellowdwarf.controller.WorldRenderer;
import fr.zzi.yellowdwarf.controller.utils.Assets;
import fr.zzi.yellowdwarf.controller.utils.Audios;
import fr.zzi.yellowdwarf.model.BlackHole;
import fr.zzi.yellowdwarf.model.Planet;
import fr.zzi.yellowdwarf.model.PlanetHandler;
import fr.zzi.yellowdwarf.model.Walls;
import fr.zzi.yellowdwarf.model.boost.VerticalBoostHandler;

/**
 *
 * The main character of the game
 *
 */
public class BasicJumper {
	public static final boolean DEBUG = WorldRenderer.DEBUG;
	public static final int STARTING_PLANET = 4;
	public static final float JUMPER_SIZE = WorldRenderer.SW / 7f;

	// a variable to adjust the draw of the jumper around the planet
	protected float planetProximity;
	public static final float WALL_PROXIMITY = JUMPER_SIZE / 5;

	public static final float JUMP_SPEED = WorldRenderer.SW * 1.66f;
	public static final float MAX_SPEED = JUMP_SPEED;
	public static final float GRAVITY = -WorldRenderer.SW / 24;
	public static final float FRICTION_X = 0.985f;

	public static final float MIN_JUMP_TIME = 300f;

	public static final float MIN_SIZE = JUMPER_SIZE / 16;
	public static final float WIDTH_DECREASE_SPEED = WorldRenderer.SW * 0.00006f;

	public static final long TIME_ATTRACTED_TO_KILLED = 1000;

	public static final float TIME_BEFORE_GAME_OVER_SCREEN = 1300f;

	/**
	 * State of the jumper Turning : turning on a planet Jumping : in the air
	 * Sliping : sliping on a wall Attracted : attracted by the blackhole Dead :
	 * killed by the blackhole Exploded : killed and exploded by the blackhole
	 */
	public enum State {
		Turning, Jumping, Sliping, Attracted, Dead, Exploded
	}

	private WorldRenderer wr;

	protected State state; // state of the jumper
	protected Vector2 pos;
	protected Vector2 speed;
	protected float maxSpeed;
	private float width;
	private float height;
	private Planet currentPlanet;
	private long lastPlanetJump; // moment of last jump
	private float theta;// the theta on the planet

	private long timeOfDeath;

	protected TextureRegion texture;
	private JumperEffects je;

	public BasicJumper(WorldRenderer wordlrenderer, State state) {
		super();
		wr = wordlrenderer;
		this.state = state;
		speed = new Vector2(0, 0);
		maxSpeed = BasicJumper.JUMP_SPEED;
		pos = new Vector2(0, 0);
		texture = new TextureRegion(Assets.getInstance()
				.getGameRegion("jumper"));
		texture.getTexture().setFilter(TextureFilter.Linear,
				TextureFilter.Linear);
		je = new JumperEffects(this);
		planetProximity = JUMPER_SIZE / 3;
	}

	/**
	 * Copy constructor
	 */
	public BasicJumper(BasicJumper bj, WorldRenderer wordlrenderer, State state) {
		super();
		wr = wordlrenderer;
		this.state = bj.state;
		maxSpeed = BasicJumper.JUMP_SPEED;
		speed = new Vector2(bj.speed);
		pos = new Vector2(bj.pos);
		theta = bj.theta;
		currentPlanet = bj.currentPlanet;
		lastPlanetJump = bj.lastPlanetJump;
		timeOfDeath = bj.timeOfDeath;
		texture = new TextureRegion(Assets.getInstance()
				.getGameRegion("jumper"));
		texture.getTexture().setFilter(TextureFilter.Linear,
				TextureFilter.Linear);
		je = new JumperEffects(this);
		planetProximity = JUMPER_SIZE / 3;

		width = JUMPER_SIZE;
		height = JUMPER_SIZE;
	}

	public void reset(Planet startingPlanet) {
		width = JUMPER_SIZE;
		height = JUMPER_SIZE;
		theta = 0;
		state = State.Turning;
		speed.x = 0;
		speed.y = 0;
		pos.x = startingPlanet.getX();
		pos.y = startingPlanet.getY();
		currentPlanet = startingPlanet;
		lastPlanetJump = System.currentTimeMillis();
		timeOfDeath = 0;
	}

	/**
	 * Move the jumper
	 *
	 * @param delta
	 * @param bh
	 */
	public void move(float delta, BlackHole bh) {
		if (state == State.Jumping) {
			speed.y += BasicJumper.GRAVITY;
			speed.x *= FRICTION_X;

			if (speed.y < -MAX_SPEED)
				speed.y = -1 * MAX_SPEED;

			pos.mulAdd(speed, delta);

			// turn the star according to its speed
			theta = MathUtils.atan2(speed.y, speed.x)
					* MathUtils.radiansToDegrees;

		} else if (state == State.Turning) {
			// turning
			if (currentPlanet.isClockwise()) {
				setTheta(theta + currentPlanet.getThetaSpeed());
			} else {
				setTheta(theta - currentPlanet.getThetaSpeed());
			}

			if (currentPlanet.isAspirated() == true) {
				pos.x = currentPlanet.getX();
				pos.y = currentPlanet.getY();
			}
		} else if (state == State.Sliping) {
			// Only gravity
			speed.x = 0;
			speed.y += BasicJumper.GRAVITY;

			if (speed.y < -MAX_SPEED)
				speed.y = -1 * MAX_SPEED;

			pos.mulAdd(speed, delta);

		} else if (state == State.Attracted) {

			float angleRad = MathUtils.atan2(bh.getY() - pos.y, bh.getX()
					- pos.x);
			speed.x = BlackHole.JUMPER_ATTRACT_SPEED * MathUtils.cos(angleRad);
			speed.y = BlackHole.JUMPER_ATTRACT_SPEED * MathUtils.sin(angleRad);

			// move toward the black hole
			pos.mulAdd(speed, delta);
			// decrease size
			if (height < MIN_SIZE) {
				height = MIN_SIZE;
				width = MIN_SIZE;
			} else {
				height -= WIDTH_DECREASE_SPEED / delta;
				width -= WIDTH_DECREASE_SPEED / delta;
			}

			// turn the star according to its speed
			theta = MathUtils.atan2(speed.y, speed.x)
					* MathUtils.radiansToDegrees;
		} else if (state == State.Dead) {
			// don't move
		} else if (state == State.Exploded) {
			// don't move
		}
	}

	/**
	 * Deal a click or a finger touch
	 */
	public void onTouchEvent() {
		if (state == State.Jumping) {
			// nothing to do usually
		} else if (state == State.Turning) {
			jumpFromPlanet();

		} else if (state == State.Sliping) {
			jumpFromWall();
		} else if (state == State.Attracted) {
			// Nothing
		} else if (state == State.Dead) {
			// Nothing
		}

	}

	/**
	 * Jump away of the current planet
	 */
	private void jumpFromPlanet() {
		Audios.getInstance().playJump();

		this.getPos().y = this.getCurrentPlanet().getPos().y
				+ (this.getCurrentPlanet().getR()/*-Jumper.PLANET_PROXIMITY/2*/)
				* MathUtils.sinDeg(theta);
		this.getPos().x = this.getCurrentPlanet().getPos().x
				+ (this.getCurrentPlanet().getR()/*-Jumper.PLANET_PROXIMITY/2*/)
				* MathUtils.cosDeg(theta);

		this.getSpeed().y = maxSpeed * MathUtils.sinDeg(theta);
		this.getSpeed().x = maxSpeed * MathUtils.cosDeg(theta);
		this.setState(State.Jumping);
		this.setLastPlanetJump(System.currentTimeMillis());
		je.startTail();
		wr.getScore().leavingPlanet();
	}

	/**
	 * Jump away of the current wall
	 */
	private void jumpFromWall() {
		Audios.getInstance().playJump();

		if (this.getPos().x < WorldRenderer.SW / 2) {
			// left wall
			// if (DEBUG)
			// System.out.println("Jump from left wall");
			this.getSpeed().x = maxSpeed * 0.7f;
			this.getSpeed().y = maxSpeed * 0.7f;

		} else {
			// right wall
			// if (DEBUG)
			// System.out.println("Jump from right wall");
			this.getSpeed().x = -maxSpeed * 0.7f;
			this.getSpeed().y = maxSpeed * 0.7f;
		}
		this.setState(State.Jumping);
		je.startTail();
		wr.getScore().leavingWall();
	}

	/**
	 * Draw the jumper and its effects
	 *
	 * @param batch
	 *            to use
	 */
	public void draw(SpriteBatch batch) {

		switch (state) {
		case Jumping:

			// batch.draw(texture, pos.x - width / 2, pos.y - height / 2, 0, 0,
			// width, height, 1f, 1f, 0);
			batch.draw(texture, pos.x - width / 2, pos.y - height / 2,
					width / 2, height / 2, width, height, 1f, 1f, theta);
			break;

		case Turning:

			// Draw 90� turned
			batch.draw(texture, currentPlanet.getX() + currentPlanet.getR()
					- planetProximity, currentPlanet.getY() - height / 2,
					-currentPlanet.getR() + planetProximity, height / 2, width,
					height, 1f, 1f, theta);

			// Draw normally
			// batch.draw(texture, currentPlanet.getX() - width / 2,
			// currentPlanet.getY()+ currentPlanet.getR()- PLANET_PROXIMITY,
			// width / 2, -currentPlanet.getR()+ PLANET_PROXIMITY,
			// width, height, 1f, 1f, theta-90);

			// batch.draw(jumper,
			// player.getCurrentPlanet().getX() - player.getWidth()
			// / 2 + player.getCurrentPlanet().getR(),
			// player.getCurrentPlanet().getY() - player.getHeight()
			// / 2,
			// player.getWidth() / 2- player.getCurrentPlanet().getR(),
			// player.getHeight() / 2
			// ,
			// player.getWidth(), player.getHeight(), 1f, 1f,
			// player.getTheta());

			break;

		case Sliping:
			if (pos.x < WorldRenderer.SW / 2) {
				batch.draw(texture, Walls.WALL_WIDTH + WALL_PROXIMITY - width
						/ 2, pos.y - height / 2, width / 2, height / 2, width,
						height, 1f, 1f, theta);
			} else {
				batch.draw(texture, WorldRenderer.SW - Walls.WALL_WIDTH
						- WALL_PROXIMITY - width / 2, pos.y - height / 2,
						width / 2, height / 2, width, height, 1f, 1f, theta);
			}
			// batch.draw(texture, pos.x - width / 2, pos.y - height / 2, 0, 0,
			// width, height, 1f, 1f, 0);
			break;
		case Attracted:
			batch.draw(texture, pos.x - width / 2, pos.y - height / 2, 0, 0,
					width, height, 1f, 1f, 0);
			break;
		case Dead:
			// nothing
			break;

		case Exploded:
			// nothing
			break;

		default:
			break;
		}
		// in all case draw the particules
		je.draw(batch);
	}

	/**
	 * Draw the jumper and its effects with the given texture
	 *
	 * @param batch
	 *            to use
	 * @param tr
	 *            texture to draw
	 */
	public void drawFrame(SpriteBatch batch, TextureRegion tr) {

		switch (state) {
		case Jumping:

			// batch.draw(texture, pos.x - width / 2, pos.y - height / 2, 0, 0,
			// width, height, 1f, 1f, 0);
			batch.draw(tr, pos.x - width / 2, pos.y - height / 2, width / 2,
					height / 2, width, height, 1f, 1f, theta);
			break;

		case Turning:
			if (!currentPlanet.isClockwise()) {
				// Draw normally
				batch.draw(tr, currentPlanet.getX() - width / 2,
						currentPlanet.getY() + currentPlanet.getR()
								- planetProximity, width / 2,
						-currentPlanet.getR() + planetProximity, width, height,
						1f, 1f, theta - 90);
			} else {
				// draw in the other way
				tr.flip(true, false);
				batch.draw(tr, currentPlanet.getX() - width / 2,
						currentPlanet.getY() + currentPlanet.getR()
								- planetProximity, width / 2,
						-currentPlanet.getR() + planetProximity, width, height,
						1f, 1f, theta - 90);
				tr.flip(true, false);
			}

			break;

		case Sliping:
			if (pos.x < WorldRenderer.SW / 2) {
				batch.draw(tr, Walls.WALL_WIDTH + WALL_PROXIMITY - width / 2,
						pos.y - height / 2, width / 2, height / 2, width,
						height, 1f, 1f, theta);
			} else {
				batch.draw(tr, WorldRenderer.SW - Walls.WALL_WIDTH
						- WALL_PROXIMITY - width / 2, pos.y - height / 2,
						width / 2, height / 2, width, height, 1f, 1f, theta);
			}
			// batch.draw(texture, pos.x - width / 2, pos.y - height / 2, 0, 0,
			// width, height, 1f, 1f, 0);
			break;
		case Attracted:
			batch.draw(texture, pos.x - width / 2, pos.y - height / 2, 0, 0,
					width, height, 1f, 1f, 0);
			break;
		case Dead:
			// nothing
			break;

		case Exploded:
			// nothing
			break;

		default:
			break;
		}
		// in all case draw the particules
		je.draw(batch);
	}

	public void update(float delta, PlanetHandler pHandler, Walls walls,
			BlackHole bh) {

		if (delta == 0)
			return;

		je.update(delta);

		boolean collide = false;

		// global collision : black hole attracting collision
		if (state != State.Dead && state != State.Attracted
				&& state != State.Exploded) {
			collideBlackHoleAttraction(bh);
		}

		if (state == State.Jumping) {
			// bottom collision
			if (pos.y <= height / 2) {
				if (DEBUG)
					System.out.println("Bottom collision");
				if (speed.y < 0) {
					speed.y = 0;
					pos.y = height / 2;
					collide = true;
				}
			}

			// wheel collision
			collide = pHandler.collideWithPlayer(this);

			// wall collision
			if (walls.collide(this)) {
				collide = true;
				wr.getScore().startSlidingWall();
				// collide = collide || walls.collide(this);
			}

			// boost collision
			int boostCollision = wr.getVerticalBoostHandler().collideWithPlayer(this);
			if (boostCollision>0) {
				speed.y = wr.getVerticalBoostHandler().getVerticalBoostSpeed(boostCollision);
				speed.x = 0;
			}

			if (!collide) {
				move(delta, bh);
			}

		} else if (state == State.Turning) {
			// turning on a planet
			move(delta, bh);
		} else if (state == State.Sliping) {
			// bottom collision
			if (pos.y <= height / 2) {
				if (DEBUG)
					System.out.println("Bottom collision");
				if (speed.y < 0) {
					speed.y = 0;
					pos.y = height / 2;
					collide = true;
				}
			}
			// sliping on a wall
			move(delta, bh);
		} else if (state == State.Attracted) {
			// methode 1 : got killed because too close
			// point to point distance collision
			if (((pos.x - bh.getX()) * (pos.x - bh.getX()) + (pos.y - bh.getY())
					* (pos.y - bh.getY())) < (BlackHole.DISAPPEARING_R)
					* (BlackHole.DISAPPEARING_R)) {
				gotKilled();
			} else if ((bh.getY() - bh.getRadius() > pos.y)
					&& ((bh.getX() - pos.x) * (bh.getX() - pos.x)
							+ (bh.getY() - pos.y) * (bh.getY() - pos.y) > bh
							.getRadius() * bh.getRadius())) {
				gotKilled();
			} else {
				move(delta, bh);
			}

			// //methode 2 : got killed because time
			// if (System.currentTimeMillis() - TIME_ATTRACTED_TO_KILLED >
			// gotAttractedTime) {
			// gotKilled();
			// } else {
			// move(delta, bh);
			// }

		} else if (state == State.Dead) {
			if (System.currentTimeMillis() - timeOfDeath > TIME_BEFORE_GAME_OVER_SCREEN) {
				je.stopExplode();
				state = State.Exploded;

			}
		} else if (state == State.Exploded) {
			// nothing to update
		}
	}

	/**
	 * The player have been swallod by the blackhole
	 */
	private void gotKilled() {
		Audios.getInstance().playExplode();

		state = State.Dead;
		wr.getScore().saveHighScore();
		wr.getScore().saveTotalVisitedPlanet();

		wr.beginGameOver();
		timeOfDeath = System.currentTimeMillis();
		je.jumperKilled();
	}

	/**
	 * Test if player get attracted by the blackhole
	 *
	 * @param bh
	 */
	private boolean collideBlackHoleAttraction(BlackHole bh) {

		// if rather close
		if (pos.y - bh.getY() < bh.getAttractingY()) {
			// medium and safe collision
			if (pos.y - height / 2 - bh.getY() < 0) {
				gotAttracted(bh);
				return true;
			}
			// point to point distance collision
			if (((pos.x - bh.getX()) * (pos.x - bh.getX()) + (pos.y - bh.getY())
					* (pos.y - bh.getY())) < (bh.getRadius() + width)
					* (bh.getRadius() + width)) {
				gotAttracted(bh);
				return true;
			}
		}
		return false;
	}

	/**
	 * The player got attracted by the blackhole bh
	 *
	 * @param bh
	 */
	private void gotAttracted(BlackHole bh) {
		state = State.Attracted;
		float angleRad = MathUtils.atan2(bh.getY() - pos.y, bh.getX() - pos.x);
		speed.x = BlackHole.PLANET_ATTRACT_SPEED * MathUtils.cos(angleRad);
		speed.y = BlackHole.PLANET_ATTRACT_SPEED * MathUtils.sin(angleRad);
	}

	/**
	 * Force player to leave a planet (planet swallowed)
	 */
	public void ejectFromPlanet() {
		System.out.println("Ejected of planet");
		this.getPos().y = this.getCurrentPlanet().getPos().y
				+ (this.getCurrentPlanet().getR()/*-Jumper.PLANET_PROXIMITY/2*/)
				* MathUtils.sinDeg(theta);
		this.getPos().x = this.getCurrentPlanet().getPos().x
				+ (this.getCurrentPlanet().getR()/*-Jumper.PLANET_PROXIMITY/2*/)
				* MathUtils.cosDeg(theta);

		// this.getSpeed().y = Jumper.JUMP_SPEED * MathUtils.sinDeg(theta);
		// this.getSpeed().x = Jumper.JUMP_SPEED * MathUtils.cosDeg(theta);
		this.setState(State.Jumping);
		this.setLastPlanetJump(System.currentTimeMillis());
	}

	/**
	 * The player just collide with the planet p
	 *
	 * @param p
	 */
	public void landOnPlanet(Planet p) {
		// Find theta
		float thetaDeg = MathUtils.radiansToDegrees
				* MathUtils.atan2((pos.y - p.getY()), (pos.x - p.getX()));
		// adapt theta
		if (thetaDeg < 0) {
			// put in positive
			thetaDeg = thetaDeg + 360;
		}

		theta = thetaDeg;
		currentPlanet = p;
		state = State.Turning;

		speed.x = 0;
		speed.y = 0;

		// stop effect
		je.stopTail();
	}

	/**
	 * Start sliping on a wall after collision
	 */
	public void landOnWall() {
		// if (DEBUG)
		// System.out.println("Left wall collision");
		this.getSpeed().x = 0;
		this.getSpeed().y = 0;
		// if (speed.y < 0) {
		// this.getSpeed().y = 0;
		// } //removed : wall feels like oil

		this.setState(State.Sliping);
	}

	/**
	 * Player collide a boost and get a speed increase
	 */
	public void boostVertically() {
		speed.y *= 1.2;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public Vector2 getPos() {
		return pos;
	}

	public void setPos(Vector2 pos) {
		this.pos = pos;
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

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public Planet getCurrentPlanet() {
		return currentPlanet;
	}

	public void setCurrentPlanet(Planet currentPlanet) {
		this.currentPlanet = currentPlanet;
	}

	public float getTheta() {
		return theta;
	}

	public void setTheta(float theta) {
		this.theta = theta;
	}

	public long getLastPlanetJump() {
		return lastPlanetJump;
	}

	public void setLastPlanetJump(long lastPlanetJump) {
		this.lastPlanetJump = lastPlanetJump;
	}

	public void setTexture(TextureRegion txt) {
		this.texture = txt;
	}

	public JumperEffects getEffects() {
		return je;
	}

	public float getPlanetProximity() {
		return planetProximity;
	}
}
