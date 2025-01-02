package fr.zzi.yellowdwarf.view.scene2d;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * 
 * An ImageButton with a background and a logo.
 * This allows to have 9patch backgrounds and regular logo.
 *
 */
public class MultipleImageButton extends ImageButton {

	private TextureRegion type;
	private float h;
	private float w;

	public MultipleImageButton(Skin skin, String styleName, TextureRegion type) {
		super(skin, styleName);
		this.type = type;
		h = type.getRegionHeight();
		w = type.getRegionWidth();
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		//draw background
		super.draw(batch, parentAlpha);

		//draw logo
		if (super.isPressed()) {
			batch.draw(
					type,
					super.getX() + super.getWidth() / 2 - w / 2,
					super.getY() + super.getHeight() / 2 - h / 2
							+ super.getStyle().pressedOffsetY, w, h);

		} else {
			batch.draw(type, super.getX() + super.getWidth() / 2 - w / 2,
					super.getY() + super.getHeight() / 2 - h / 2, w, h);
		}

	}

	public void resizeLogo(float height, float width) {
		this.h = height;
		this.w = width;
	}

	public void setLogoHeight(float height) {
		float ratio = w / h;
		this.h = height;
		this.w = height * ratio;
	}

	public void setLogoWidth(float width) {
		float ratio = w / h;
		this.w = width;
		this.h = width / ratio;
	}

}
