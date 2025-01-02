package fr.zzi.yellowdwarf.controller.utils;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;

import fr.zzi.yellowdwarf.controller.AchvHandler;
import fr.zzi.yellowdwarf.controller.WorldRenderer;
import fr.zzi.yellowdwarf.model.Planet;
import fr.zzi.yellowdwarf.model.jumper.BasicJumper;
import fr.zzi.yellowdwarf.model.jumper.JumperEffects;
import fr.zzi.yellowdwarf.model.jumper.BasicJumper.State;

/**
 *
 * Deal the score and notify AchvHandler
 *
 */
public class Score {
	public static final boolean DEBUG = WorldRenderer.DEBUG;

	public static final String PREF_KEY_HIGHSCORE = "highscore";
	public static final String PREF_KEY_TOTAL_PLANET = "total_planet";

	public static final int MARGIN_TOP_RUNNING = WorldRenderer.SW / 10;

	private WorldRenderer wr;
	private AchvHandler ah;
	private BitmapFont font;

	private int bestScore; // best of player score, ever
	private int score;

	private int highestPlanet; // the position in list of the heighest planed
								// landed on

	private int totalVisitedPlanets;
	private int previousPlanet;
	private int countSamePlanetJump;
	private long currentStateTime; // the moment we got the current state

	public Score(WorldRenderer renderer) {
		wr = renderer;
		ah = new AchvHandler(wr);
		font = Assets.getInstance().getManager()
				.get("fonts/score.fnt", BitmapFont.class);

		bestScore = loadHighScore();
		totalVisitedPlanets = PrefManager.getInt(PREF_KEY_TOTAL_PLANET);
	}

	public void reset(float playerY) {
		score = 0;
		highestPlanet = BasicJumper.STARTING_PLANET;
		countSamePlanetJump = 0;
		currentStateTime = System.currentTimeMillis();
	}

	/**
	 * The player just collide with the planet p
	 */
	public void landedOnPlanet(Planet p) {
		currentStateTime = System.currentTimeMillis();

		//Disable help
		if(highestPlanet>JumperEffects.SHOW_ARROW_PLANET_NB+BasicJumper.STARTING_PLANET && highestPlanet < JumperEffects.SHOW_ARROW_PLANET_NB+3+BasicJumper.STARTING_PLANET){
			wr.getJumper().getEffects().disableHelp();
		}

		// Achievements checks
		// ACH 1 : reach planet
		if (ah.getDoneList()[0] == false) {
			if (p.getId() - BasicJumper.STARTING_PLANET >= ah.ACHV_1_NB) {
				ah.achvCompleted(1);
			}
		}
		// ACH 2 : reach planet
		if (ah.getDoneList()[1] == false) {
			if (p.getId() - BasicJumper.STARTING_PLANET >= ah.ACHV_2_NB) {
				ah.achvCompleted(2);
			}
		}
		// ACH 3 : reach planet
		if (ah.getDoneList()[2] == false) {
			if (p.getId() - BasicJumper.STARTING_PLANET >= ah.ACHV_3_NB) {
				ah.achvCompleted(3);
			}
		}

		// ACH 4 : skip a planet
		if (ah.getDoneList()[3] == false) {
			if (p.getId() > highestPlanet
					&& p.getId() - highestPlanet > ah.ACHV_4_NB) {
				ah.achvCompleted(4);
			}
		}

		// ACH 5 : reach planet -
		if (ah.getDoneList()[4] == false) {
			if (p.getId() - BasicJumper.STARTING_PLANET <= ah.ACHV_5_NB) {
				ah.achvCompleted(5);
			}
		}
		// ACH 6 : reach planet -
		if (ah.getDoneList()[5] == false) {
			if (p.getId() - BasicJumper.STARTING_PLANET <= ah.ACHV_6_NB) {
				ah.achvCompleted(6);
			}
		}

		// ACH 7/8/9 : jump same planet
		if (ah.getDoneList()[8] == false) {
			if (previousPlanet == p.getId()) {
				++countSamePlanetJump;
				if(ah.getDoneList()[6] == false){
					if (countSamePlanetJump == ah.ACHV_7_NB) {
						ah.achvCompleted(7);
					}
				}
				if(ah.getDoneList()[7] == false){
					if (countSamePlanetJump == ah.ACHV_8_NB) {
						ah.achvCompleted(8);
					}
				}
				if(ah.getDoneList()[8] == false){
					if (countSamePlanetJump == ah.ACHV_9_NB) {
						ah.achvCompleted(9);
					}
				}
			} else {
				countSamePlanetJump = 0;
			}
		}
		previousPlanet = p.getId();

		// update score
		if (p.getId() > highestPlanet) {
			totalVisitedPlanets += p.getId() - highestPlanet;
			score += p.getId() - highestPlanet;
			highestPlanet = p.getId();
		}

		// ACH 16/17/18
		if (ah.getDoneList()[15] == false) {
			if (totalVisitedPlanets >= ah.ACHV_16_NB) {
				ah.achvCompleted(16);
				saveTotalVisitedPlanet();
			}
		}
		if (ah.getDoneList()[16] == false) {
			if (totalVisitedPlanets >= ah.ACHV_17_NB) {
				ah.achvCompleted(17);
				saveTotalVisitedPlanet();
			}
		}
		if (ah.getDoneList()[17] == false) {
			if (totalVisitedPlanets >= ah.ACHV_18_NB) {
				ah.achvCompleted(18);
				saveTotalVisitedPlanet();
			}
		}
	}

	/**
	 * The player collide a wall and start sliding
	 */
	public void startSlidingWall() {
		currentStateTime = System.currentTimeMillis();
		countSamePlanetJump = 0;
		previousPlanet = -1;
	}

	/**
	 * The player left the previous planet
	 */
	public void leavingPlanet() {
		currentStateTime = System.currentTimeMillis();
	}

	/**
	 * The player jump away from the wall
	 */
	public void leavingWall() {
		currentStateTime = System.currentTimeMillis();
		countSamePlanetJump = 0;
	}

	public void unpause() {
		currentStateTime = System.currentTimeMillis();
	}

	/**
	 * Update the count of achievement
	 */
	public void update(BasicJumper player) {
		// ACH 10/11/12 : slide on wall
		if (ah.getDoneList()[9] == false) {
			if (player.getState() == State.Sliping
					&& System.currentTimeMillis() - currentStateTime > ah.ACHV_10_NB * 1000) {
				ah.achvCompleted(10);
			}
		}
		if (ah.getDoneList()[10] == false) {
			if (player.getState() == State.Sliping
					&& System.currentTimeMillis() - currentStateTime > ah.ACHV_11_NB * 1000) {
				ah.achvCompleted(11);
			}
		}
		if (ah.getDoneList()[11] == false) {
			if (player.getState() == State.Sliping
					&& System.currentTimeMillis() - currentStateTime > ah.ACHV_12_NB * 1000) {
				ah.achvCompleted(12);
			}
		}

		// ACH 13/14/15
		if (ah.getDoneList()[12] == false) {
			if (player.getState() == State.Jumping
					&& System.currentTimeMillis() - currentStateTime > ah.ACHV_13_NB * 1000) {
				ah.achvCompleted(13);
			}
		}
		if (ah.getDoneList()[13] == false) {
			if (player.getState() == State.Jumping
					&& System.currentTimeMillis() - currentStateTime > ah.ACHV_14_NB * 1000) {
				ah.achvCompleted(14);
			}
		}
		if (ah.getDoneList()[14] == false) {
			if (player.getState() == State.Jumping
					&& System.currentTimeMillis() - currentStateTime > ah.ACHV_15_NB * 1000) {
				ah.achvCompleted(15);
			}
		}
	}

	/**
	 * Draw the score in the game state Running
	 *
	 * @param batch
	 * @param centerX
	 *            of screen = pos of the cam
	 * @param centerY
	 *            of screen = pos of the cam
	 */
	public void drawCurrent(SpriteBatch batch, float centerX, float centerY) {
		// font.draw(batch, "" + score, centerX, centerY + WorldRenderer.SH / 2
		// - MARGIN_TOP_RUNNING);
//		scoreBounds = font.getBounds(Integer.toString(score));
//		font.drawWrapped(batch, Integer.toString(score), MARGIN_TOP_RUNNING
//				+ scoreBounds.width / 4, centerY + WorldRenderer.SH / 2
//				- MARGIN_TOP_RUNNING, scoreBounds.width,
//				BitmapFont.HAlignment.CENTER);

        font.draw(batch,  Integer.toString(score), MARGIN_TOP_RUNNING, centerY + WorldRenderer.SH / 2
            - MARGIN_TOP_RUNNING);
	}

	public int getScore() {
		return score;
	}

	public int getBestScore() {
		return bestScore;
	}

	public int getHighestPlanet() {
		return highestPlanet;
	}

	public void setHighestPlanet(int highestPlanet) {
		this.highestPlanet = highestPlanet;
	}

	private int loadHighScore() {
		return PrefManager.getInt(PREF_KEY_HIGHSCORE);
	}

	public void saveHighScore() {
		if (score > bestScore) {
			bestScore = score;
			PrefManager.saveInt(PREF_KEY_HIGHSCORE, bestScore);
		}
	}

	public void saveTotalVisitedPlanet() {
		PrefManager.saveInt(PREF_KEY_TOTAL_PLANET, totalVisitedPlanets);
	}

	public AchvHandler getAchvHandler() {
		return ah;
	}

}
