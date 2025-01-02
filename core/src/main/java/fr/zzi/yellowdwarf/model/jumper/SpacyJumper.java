package fr.zzi.yellowdwarf.model.jumper;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;

import fr.zzi.yellowdwarf.controller.WorldRenderer;
import fr.zzi.yellowdwarf.controller.utils.Assets;
import fr.zzi.yellowdwarf.controller.utils.Audios;
import fr.zzi.yellowdwarf.model.BlackHole;
import fr.zzi.yellowdwarf.model.PlanetHandler;
import fr.zzi.yellowdwarf.model.Walls;
import fr.zzi.yellowdwarf.model.jumper.BasicJumper.State;

public class SpacyJumper extends BasicJumper {

	private Animation frames;
	private float stateTime;

	public SpacyJumper(WorldRenderer wordlrenderer, State state) {
		super(wordlrenderer, state);

		init();
	}

	public SpacyJumper(BasicJumper bj, WorldRenderer wordlrenderer, State state) {
		super(bj, wordlrenderer, state);

		init();
	}

	/**
	 * Init this instance
	 */
	private void init(){
		stateTime = 0;

		TextureRegion frame1 = Assets.getInstance().getGameRegion("jumper3a");
		TextureRegion frame2 = Assets.getInstance().getGameRegion("jumper3b");
		TextureRegion frame3 = Assets.getInstance().getGameRegion("jumper3c");
		TextureRegion frame4 = Assets.getInstance().getGameRegion("jumper3d");

		frame1.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		frame2.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		frame3.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		frame4.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);

		frames = new Animation(0.15f, new TextureRegion(frame1), new TextureRegion(frame2), new TextureRegion(frame3), new TextureRegion(frame4));
		frames.setPlayMode(PlayMode.LOOP);

		super.getEffects().setTailColor(new float[] { 159f/255f, 0f, 0f });

		super.texture = Assets.getInstance().getGameRegion("jumper3");
		super.texture.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);

	}

	@Override
	public void update(float delta, PlanetHandler pHandler, Walls walls,
			BlackHole bh) {
		super.update(delta, pHandler, walls, bh);

		stateTime += delta;
	}

	@Override
	public void draw(SpriteBatch batch) {
		super.drawFrame(batch, (TextureRegion) frames.getKeyFrame(stateTime));
	}
}
