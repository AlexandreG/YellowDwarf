package fr.zzi.yellowdwarf.view.scene2d;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * A simple texture who can draw itself
 *
 */
public class SimpleTexture {

	private TextureRegion texture;
	private float x;
	private float y;

	private float heigh;
	private float width;
	private float ratio;	//ratio of the texture

	public SimpleTexture(TextureRegion txtr, float x, float y, float newWidth) {
		texture = txtr;
		texture.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);

		this.x = x;
		this.y = y;

		setWidth(newWidth);
	}

	public SimpleTexture(TextureRegion txtr, float x, float y, float newWidth,
			float newHeight) {
		texture = txtr;
		texture.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		this.x = x;
		this.y = y;
		this.width = newWidth;
		this.heigh = newHeight;
	}

	public void draw(SpriteBatch b) {
		b.draw(texture, x - width / 2, y - heigh / 2, width, heigh);
	}

	/**
	 * Set the width of the texture but keep the img ratio
	 * 
	 * @param newWidth
	 */
	public void setWidth(float newWidth) {
		ratio = texture.getRegionWidth() / texture.getRegionHeight();
		width = newWidth;
		heigh = newWidth / ratio;
	}
}
