package fr.zzi.yellowdwarf.model;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import fr.zzi.yellowdwarf.controller.WorldRenderer;
import fr.zzi.yellowdwarf.controller.utils.Assets;
import fr.zzi.yellowdwarf.model.jumper.BasicJumper;

/**
 *
 * Walls of the game.
 *
 */
public class Walls {
	public static final boolean DEBUG = WorldRenderer.DEBUG;
	public static final float WALL_WIDTH = WorldRenderer.SW / 30f;
	public static final float WALL_COLLIDE_MARGIN = WorldRenderer.SW / 25f;

	private TextureRegion wallLeft;
	private TextureRegion wallRight; // TextureRegion because needs to be fliped

	private float width; // width of the wall

	public Walls() {
		wallLeft = Assets.getInstance().getGameRegion("wall");
		wallRight = new TextureRegion(wallLeft);
		wallRight.flip(true, false);

		width = WALL_WIDTH;

		wallLeft.getTexture().setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		wallRight.getTexture().setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
	}

	public void reset() {
	}

	/**
	 * Test if there is collision and apply speed modifs
	 *
	 * @param player to check the collision with
	 * @return true if collision, false else
	 */
	public boolean collide(BasicJumper player) {
		// left wall collision
		if (player.getX() < player.getWidth() / 2 + width - WALL_COLLIDE_MARGIN) {
			if (player.getSpeed().x < 0) {
				player.landOnWall();
				return true;
			}
		}
		// right wall collision
		if (player.getX() > WorldRenderer.SW - player.getWidth() / 2 - width
				+ WALL_COLLIDE_MARGIN) {
			if (player.getSpeed().x > 0) {
				player.landOnWall();
				return true;
			}
		}
		return false;
	}

	/**
	 * Draw the walls
	 *
	 * @param batch to use
	 * @param cam
	 *            : needed for the coordinates
	 */
	public void draw(SpriteBatch batch, OrthographicCamera cam) {
		// repeat-Y
		// left wall
		batch.draw(wallLeft, 0, cam.position.y - WorldRenderer.SH / 2, width,
				WorldRenderer.SH);
		// right wall
		batch.draw(wallRight, WorldRenderer.SW - width, cam.position.y
				- WorldRenderer.SH / 2, width, WorldRenderer.SH);
	}

	public float getW() {
		return width;
	}

}
