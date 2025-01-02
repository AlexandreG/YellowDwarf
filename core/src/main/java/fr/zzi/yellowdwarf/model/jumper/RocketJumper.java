package fr.zzi.yellowdwarf.model.jumper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import fr.zzi.yellowdwarf.controller.WorldRenderer;
import fr.zzi.yellowdwarf.controller.utils.Assets;
import fr.zzi.yellowdwarf.model.BlackHole;
import fr.zzi.yellowdwarf.model.PlanetHandler;
import fr.zzi.yellowdwarf.model.Walls;

public class RocketJumper extends BasicJumper {

	public RocketJumper(WorldRenderer wordlrenderer, State state) {
		super(wordlrenderer, state);

		init();
	}

	public RocketJumper(BasicJumper bj, WorldRenderer wordlrenderer, State state) {
		super(bj, wordlrenderer, state);
		
		init();
	}
	
	/**
	 * Init this instance
	 */
	private void init(){
		super.texture = Assets.getInstance().getGameRegion("jumper2");
		texture.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		super.planetProximity *= 0.7f;

		// load flame effects
		ParticleEffect tailEffect = new ParticleEffect();
		tailEffect.load(Gdx.files.internal("effects/flame.p"),
				Gdx.files.internal("effects"));
		super.getEffects().setTailEffect(tailEffect);
	}

	@Override
	public void update(float delta, PlanetHandler pHandler, Walls walls,
			BlackHole bh) {
		if(super.speed.y <0){
			super.getEffects().stopTail();
		}
		super.update(delta, pHandler, walls, bh);
	}

}
