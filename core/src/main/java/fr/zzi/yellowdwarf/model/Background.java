package fr.zzi.yellowdwarf.model;

import java.util.LinkedList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

import fr.zzi.yellowdwarf.controller.WorldRenderer;
import fr.zzi.yellowdwarf.controller.WorldRenderer.GameState;
import fr.zzi.yellowdwarf.controller.utils.Assets;
import fr.zzi.yellowdwarf.model.jumper.BasicJumper;
import fr.zzi.yellowdwarf.model.jumper.BasicJumper.State;

/**
 *
 * The background of the game
 *
 */
public class Background {
	public static final boolean DEBUG = WorldRenderer.DEBUG;

	public static final int STAR_NB = 30;

	public static final float STAR_POP_AREA = WorldRenderer.SH / 2;

	public static final float STAR_RAW_SPEED = 0.6f;// percent
	public static final float STAR_MIN_SPEED_RATIO = 0.3f;// percent
	public static final float STAR_MAX_SIZE = BasicJumper.JUMPER_SIZE * 0.2f;
	public static final float STAR_MIN_SIZE = STAR_MIN_SPEED_RATIO;

	private TextureRegion texture1;
	private TextureRegion texture2;
	private TextureRegion texture3;
	private TextureRegion texture4;
	private TextureRegion texture5;
	private int currentBackground;
	private TextureRegion starTxtr;

	private LinkedList<Star> stars;

	public Background() {
		texture1 = Assets.getInstance().getGameRegion("background1");
		texture1.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		texture2 = Assets.getInstance().getGameRegion("background2");
		texture2.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		texture3 = Assets.getInstance().getGameRegion("background3");
		texture3.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		texture4 = Assets.getInstance().getGameRegion("background4");
		texture4.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		texture5 = Assets.getInstance().getGameRegion("background5");
		texture5.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);

		starTxtr = Assets.getInstance().getGameRegion("backgroundStar");
		starTxtr.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);

		stars = new LinkedList<Star>();

		currentBackground = 0;

		initStars();
	}

	/**
	 * Fill the list with TOTAL_NB stars
	 */
	private void initStars() {
		Star s;
		float x, y, ratio, speedRatio;
		for (int i = 0; i < STAR_NB; ++i) {
			x = MathUtils.random(Walls.WALL_WIDTH + STAR_MAX_SIZE,
					WorldRenderer.SW - Walls.WALL_WIDTH - STAR_MAX_SIZE);
			y = MathUtils.random(-STAR_POP_AREA, WorldRenderer.SH
					+ STAR_POP_AREA);
			speedRatio = MathUtils.random(1 - STAR_MIN_SPEED_RATIO)
					+ STAR_MIN_SPEED_RATIO;
			// reverse increase
			// ratio = 1 - speedRatio+STAR_MIN_SIZE;

			s = new Star(x, y, speedRatio, speedRatio);
			stars.add(s);
		}
	}

	public void reset() {
		++currentBackground;
		if (currentBackground > 5) {
			currentBackground = 1;
		}
	}

	/**
	 * Draw the background on the given batch
	 *
	 * @param batch
	 *            to draw
	 * @param camY
	 *            : center the background on this value
	 */
	public void draw(SpriteBatch batch, float camY) {
		batch.draw(getCurrentTexture(), 0, camY - WorldRenderer.SH / 2,
				WorldRenderer.SW, WorldRenderer.SH);

		for (Star s : stars) {
			// if(s.ratio<(s.ratio+1)/2){
			batch.draw(starTxtr, s.x, s.y + camY - WorldRenderer.SH / 2,
					STAR_MAX_SIZE * s.ratio, STAR_MAX_SIZE * s.ratio);
			// }
		}
	}

	/**
	 * Move the background stars
	 */
	public void update(float delta, State state, GameState gameState) {
		for (Star s : stars) {

			if (s.y > WorldRenderer.SH + STAR_MAX_SIZE + STAR_POP_AREA) {
				// tp because too high
				s.y = MathUtils.random(STAR_POP_AREA) - STAR_POP_AREA
						- STAR_MAX_SIZE;
			} else if (s.y < -STAR_MAX_SIZE - STAR_POP_AREA) {
				// tp because too low
				s.y = MathUtils.random(STAR_POP_AREA) + WorldRenderer.SH
						+ STAR_MAX_SIZE;
			} else {
				// move
				if (gameState == GameState.Cutscene || state == State.Jumping || state == State.Sliping) {
					s.y -= s.speedRatio * delta * STAR_RAW_SPEED;
				}
			}
		}
	}

	private TextureRegion getCurrentTexture() {
		switch (currentBackground) {
		case 1:
			return texture1;
		case 2:
			return texture2;
		case 3:
			return texture3;
		case 4:
			return texture4;
		case 5:
			return texture5;

		default:
			return texture1;
		}
	}

	private class Star {
		public float x;
		public float y;
		// percent of the default size
		public float ratio;
		// the speed to move, proportional to the size for a parallax effect
		public float speedRatio;

		public Star(float x, float y, float ratio, float speed) {
			this.x = x;
			this.y = y;
			this.ratio = ratio;
			this.speedRatio = speed;
		}
	}
}
