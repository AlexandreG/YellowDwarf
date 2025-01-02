package fr.zzi.yellowdwarf.view.scene2d;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

/**
 * 
 * A simple nine patch drawable who can draw itself
 *
 */
public class SimpleNinePatchDrawable {

	private NinePatchDrawable drawable;
	private float x;
	private float y;

	private float heigh;
	private float width;

	public SimpleNinePatchDrawable(TextureRegion txtr, float x, float y,
			float newWidth, float newHeight) {
		// I assume that top, left, right and bottom are all 15 for your 9patch
		NinePatch patch = new NinePatch(txtr, 15, 15, 15, 15);
		drawable = new NinePatchDrawable(patch);

		this.x = x;
		this.y = y;
		this.width = newWidth;
		this.heigh = newHeight;
	}

	public void draw(SpriteBatch b) {
		drawable.draw(b, x - width / 2, y - heigh / 2, width, heigh);
	}

}
