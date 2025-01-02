package fr.zzi.yellowdwarf.model;

import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import fr.zzi.yellowdwarf.controller.WorldRenderer;
import fr.zzi.yellowdwarf.controller.utils.Assets;
import fr.zzi.yellowdwarf.model.boost.VerticalBoostHandler;
import fr.zzi.yellowdwarf.model.jumper.BasicJumper;
import fr.zzi.yellowdwarf.model.jumper.BasicJumper.State;

/**
 * Handle the planets of the game and the boosts
 *
 */
public class PlanetHandler {
	public static final boolean DEBUG = WorldRenderer.DEBUG;
	// the margin with the screen border
	public static final float WINDOWS_MARGIN = WorldRenderer.SW / 10;
	public static final float MIN_RADIUS = WorldRenderer.SW * 0.06f;
	public static final float MAX_RADIUS = WorldRenderer.SW * 0.23f;
	// planet id of the end of the progressive radius
	public static final float END_PROGRESSIVE_MODE = 10;
	// the gap between the max and min when generating in progrssive mode
	public static final float PROGRESSIVE_RADIUS_GAP = (MAX_RADIUS - MIN_RADIUS) / 2;

	// min rotation speed
	public static final float MIN_SPEED = 1.5f;
	// max rotation speed
	public static final float MAX_SPEED = 9;
	// the minimal gap between the max and the min
	public static final float PROGRESSIVE_MIN_SPEED = (MAX_SPEED - MIN_SPEED) / 4;

	// Color to tint the texture
	public static final int MIN_COLOR = 150;// 0=black
	public static final int MAX_COLOR = 255;
	public static final float COLOR_ALPHA = 1.0f;// 0=transparent

	// vertical distance between 2 planets
	public static final float VERTICAL_GAP = WorldRenderer.SW / 1.9f;
	// a variable to ajust the collision sensibility
	public static final float COLLIDE_JUMPER_MARGIN = WorldRenderer.SW / 33f;

	//total nb of planets
	public static final int TOTAL_NB = 18;

	private WorldRenderer wr;

	private LinkedList<Planet> planetList; // list of the planet
	private LinkedList<Planet> planetToRemove; // planets destroyed

	private TextureRegion planetShadow; // shadow of the planets
	private ArrayList<TextureRegion> textureList; // list of the textures

	public PlanetHandler(WorldRenderer worldrenderer) {
		wr = worldrenderer;
		planetList = new LinkedList<Planet>();
		planetToRemove = new LinkedList<Planet>();

		textureList = new ArrayList<TextureRegion>(TOTAL_NB);

		planetShadow = Assets.getInstance().getGameRegion("planetShadow");
		planetShadow.getTexture().setFilter(TextureFilter.Linear,
				TextureFilter.Linear);

		loadTextures();
	}

	/**
	 * Load the atlas into the list
	 */
	private void loadTextures() {
		TextureRegion tr = null;
		for (int i = 1; i <= TOTAL_NB; ++i) {
			tr = Assets.getInstance().getGameRegion(Integer.toString(i));
			tr.getTexture().setFilter(TextureFilter.Linear,
					TextureFilter.Linear);
			textureList.add(tr);
		}
	}

	public void reset() {
		planetList.clear();
		planetToRemove.clear();
		addNewPlanets(100, 100);
	}

	/**
	 * Draw all the planets and boosts
	 *
	 * @param batch
	 *            to use
	 * @param player
	 *            : usefull to draw only close planets (clipping)
	 */
	public void draw(SpriteBatch batch, BasicJumper player) {
		for (Planet p : planetList) {
			// if rather close to the camera
			if ((p.getY() < player.getY() + 2 * WorldRenderer.SH)
					&& (p.getY() > player.getY() - 2 * WorldRenderer.SH)) {
				p.draw(batch, planetShadow);
			}
		}
	}

	/**
	 * Test if player collide with a planet
	 *
	 * @param player
	 *            to test
	 * @return true if collision, false else
	 */
	public boolean collideWithPlayer(BasicJumper player) {
		boolean collide = false;
		for (Planet p : planetList) {
			// if rather close
			if ((p.getY() < player.getY() + 2 * WorldRenderer.SH)
					&& (p.getY() > player.getY() - 2 * WorldRenderer.SH)) {
				collide = p.collide(player);
				if (collide) {
					wr.getScore().landedOnPlanet(p);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Update the planets
	 *
	 * @param delta
	 * @param player
	 *            : move only close planets
	 * @param bh
	 */
	public void update(float delta, BasicJumper player, BlackHole bh) {
		for (Planet p : planetList) {

			// move if rather close to the player or aspired
			if (p.isAspirated()
					|| ((p.getY() < player.getY() + 2 * WorldRenderer.SH) && (p
							.getY() > player.getY() - 2 * WorldRenderer.SH))) {

				p.move(delta, bh);
			}

			// if close to the bh, set aspired
			if (p.getY() < bh.getY() + bh.getAttractingY()) {
				p.setAspirated(true);
			}

			// planet swallowed by the blackhole
			if (p.isAspirated()
					&& p.getY() < bh.getY() + BlackHole.DISAPPEARING_Y) {
				planetToRemove.add(p);
				if (player.getCurrentPlanet() == p
						&& player.getState() == State.Turning) {
					player.ejectFromPlanet();
				}
			}
		}

		// remove the planets to remove
		for (Planet p : planetToRemove) {
			planetList.remove(p);
			bh.planetEaten();
		}
		planetToRemove.clear();

		// repopulate planets if necessary
		if (planetList.size() >= 1) {
			if (planetList.getLast().getY() - player.getY() < 2 * WorldRenderer.SH) {
				System.out.println("Repopulate planets");
				addNewPlanets(20, planetList.getLast().getY() + VERTICAL_GAP);
			}
		}
	}

	/**
	 * Update only the rotation of the planets
	 *
	 * @param delta
	 * @param player
	 */
	public void updateTurnOnly(float delta, BasicJumper player) {
		for (Planet p : planetList) {
			// move if rather close to the player or aspired
			if (p.isAspirated()
					|| ((p.getY() < player.getY() + 2 * WorldRenderer.SH) && (p
							.getY() > player.getY() - 2 * WorldRenderer.SH))) {

				p.moveTurnOnly(delta);
			}
		}

		// repopulate planets if necessary
		if (planetList.size() >= 1) {
			if (planetList.getLast().getY() - player.getY() < 2 * WorldRenderer.SH) {
				System.out.println("Repopulate planets");
				addNewPlanets(20, planetList.getLast().getY() + VERTICAL_GAP);
			}
		}
	}

	/**
	 * Update planets in cutscene mode : turn all, first planet eaten
	 *
	 * @param delta
	 * @param player
	 */
	public void updateCutscene(float delta, BasicJumper player, BlackHole bh) {
		for (Planet p : planetList) {
			// move if rather close to the player or aspired
			if (p.isAspirated()
					|| ((p.getY() < player.getY() + 2 * WorldRenderer.SH) && (p
							.getY() > player.getY() - 2 * WorldRenderer.SH))) {

				if (p.getId() == 0) {
					p.move(delta, bh);
				} else {
					p.moveTurnOnly(delta);
				}
			}

			// Eat only first one
			if (p.getId() == 0) {
				// if close to the bh, set aspired
				if (p.getY() < bh.getY() + bh.getAttractingY()) {
					p.setAspirated(true);
				}

				// planet swallowed by the blackhole
				if (p.isAspirated()
						&& p.getY() < bh.getY() + BlackHole.DISAPPEARING_Y) {
					planetToRemove.add(p);
					if (player.getCurrentPlanet() == p
							&& player.getState() == State.Turning) {
						player.ejectFromPlanet();
					}
				}
			}
		}
		// remove the planets to remove
		for (Planet p : planetToRemove) {
			planetList.remove(p);
			bh.planetEaten();
		}
		planetToRemove.clear();
	}

	/**
	 * Generate new planets in the map
	 *
	 * @param nb
	 *            : the number of new planets to add
	 * @param firstY
	 *            : the y offset for the first new planet
	 */
	public void addNewPlanets(int nb, float firstY) {
		float r = 0;
		float thetaSpeed;
		float xPos;
		int currentId;
		int textureId = 0;
		int previousTextureId = -1;
		Color color;
		float rColor, gColor, bColor = 0;
		int lastPlanetPos = 0;
		if (planetList.size() != 0) {
			lastPlanetPos = planetList.getLast().getId();
		}

		//a gap to add because of vertical boosts
		float boostGap = 0;

		for (int i = 0; i < nb; i++) {
			if (planetList.size() == 0) {
				currentId = -1 - BasicJumper.STARTING_PLANET;
			} else {
				currentId = planetList.getLast().getId()
						- BasicJumper.STARTING_PLANET;
			}

			++currentId;
			r = getRandomRadius(currentId);
			thetaSpeed = getRandomSpeed(currentId);
			xPos = getRandomX(r);

			//boost time ?
			if(currentId != 0 && currentId%VerticalBoostHandler.NB_BOOST_PER_PLANET == 0){
				wr.getVerticalBoostHandler().addBoost(WorldRenderer.SW/2, firstY + VERTICAL_GAP * i+boostGap);
				boostGap = boostGap + VERTICAL_GAP*wr.getVerticalBoostHandler().getNbPlanetToElapse();
			}


			//first planet after the boost is centred
			if(currentId != 0 && currentId%VerticalBoostHandler.NB_BOOST_PER_PLANET == 0){
				xPos = WorldRenderer.SW/2;
				thetaSpeed = (MAX_SPEED-MIN_SPEED)*0.5f;
				r = (MAX_RADIUS-MIN_RADIUS)*0.8f;
			}


			rColor = (MathUtils.random(MAX_COLOR - MIN_COLOR) + MIN_COLOR) / 255.f;
			gColor = (MathUtils.random(MAX_COLOR - MIN_COLOR) + MIN_COLOR) / 255.f;
			bColor = (MathUtils.random(MAX_COLOR - MIN_COLOR) + MIN_COLOR) / 255.f;
			color = new Color(rColor, gColor, bColor, COLOR_ALPHA);

			textureId = MathUtils.random(textureList.size() - 1);
			if (textureId == previousTextureId) {
				// reduce the proba that 2 planets in a row have the same
				// texture
				textureId = MathUtils.random(textureList.size() - 1);
			}
			previousTextureId = textureId;

			planetList.add(new Planet(textureList.get(textureId), color,
					lastPlanetPos + i, new Vector2(xPos, firstY
							+ VERTICAL_GAP * i+boostGap), thetaSpeed, r,
							MathUtils.randomBoolean()));
		}
	}

	/**
	 * Get a random x position of a planet
	 *
	 * @param r
	 *            : the radius of the planet
	 */
	private float getRandomX(float r) {
		// if(id%2 == 0){
		// return r + WINDOWS_MARGIN;
		// }else{
		// return r + WINDOWS_MARGIN+WorldRenderer.SW - 2 * r - 2 *
		// WINDOWS_MARGIN;
		// }
		return MathUtils.random(WorldRenderer.SW - 2 * r - 2 * WINDOWS_MARGIN)
				+ r + WINDOWS_MARGIN;
	}

	/**
	 * Generate a random speed according to the planet position : regular speed
	 * at the begining
	 *
	 * @param relativePos
	 *            the pos of the planet relative to the player beginning
	 *            (begining planet = planet number 0)
	 * @return the random speed
	 */
	private float getRandomSpeed(int relativePos) {
		float d = PROGRESSIVE_MIN_SPEED + relativePos / END_PROGRESSIVE_MODE;
		if (relativePos < 0) {
			// random amplitude
			return MathUtils.random(MAX_SPEED - MIN_SPEED) + MIN_SPEED;
		} else if (relativePos == 0) {
			return (MAX_SPEED - MIN_SPEED) / 2;
		} else if (relativePos > END_PROGRESSIVE_MODE) {
			// random amplitude
			return MathUtils.random(MAX_SPEED - MIN_SPEED) + MIN_SPEED;
		} else {
			// amplitude centered on the average, getting more and more random
			return MathUtils.random(d) + (MAX_SPEED - MIN_SPEED) / 2 - d / 2;
		}

	}

	/**
	 * Generate a random radius according to the planet position : big radius at
	 * the begining
	 *
	 * @param relativePos
	 *            the pos of the planet relative to the player beginning
	 *            (begining planet = planet number 0)
	 * @return the random radius
	 */
	private float getRandomRadius(int relativePos) {
		if (relativePos < 0) {
			// small random
			return MIN_RADIUS;
		} else if (relativePos == 0) {
			return (MAX_RADIUS - MIN_RADIUS) * 0.7f;
		} else if (relativePos > END_PROGRESSIVE_MODE) {
			return MathUtils.random(MAX_RADIUS-MIN_RADIUS) + MIN_RADIUS;
		} else {
			// we have an area of probabiblity that we move from the MAX_RADIUS
			// to the MIN_RADIUS
			return MAX_RADIUS - MathUtils.random(PROGRESSIVE_RADIUS_GAP)
					- relativePos / END_PROGRESSIVE_MODE
					* (MAX_RADIUS - MIN_RADIUS - PROGRESSIVE_RADIUS_GAP);
			// return MathUtils.random(PROGRESSIVE_RADIUS_GAP) + MIN_RADIUS
			// + (END_PROGRESSIVE_MODE - relativePos)
			// / END_PROGRESSIVE_MODE;
		}
	}

	/**
	 * Return the planet indexed n
	 */
	public Planet get(int n) {
		return planetList.get(n);
	}

	/**
	 * Return the list of planets
	 */
	public LinkedList<Planet> getPlanets() {
		return planetList;
	}
}
