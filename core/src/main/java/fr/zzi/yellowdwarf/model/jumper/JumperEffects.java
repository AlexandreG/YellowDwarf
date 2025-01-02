package fr.zzi.yellowdwarf.model.jumper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import fr.zzi.yellowdwarf.controller.WorldRenderer;
import fr.zzi.yellowdwarf.controller.utils.Assets;
import fr.zzi.yellowdwarf.model.Walls;
import fr.zzi.yellowdwarf.model.jumper.BasicJumper.State;

public class JumperEffects {
	public static final int SHOW_ARROW_PLANET_NB = 5;
	public static final float ARROW_W = BasicJumper.JUMPER_SIZE*0.3f;
	public static final float ARROW_H = ARROW_W * 44/28;
	public static final float ARROW_PADDING_PLANET = BasicJumper.JUMPER_SIZE;
	public static final float ARROW_PADDING_WALL = BasicJumper.JUMPER_SIZE*0.7f;
	public static final int ARROW_WALL_THETA = 50;

	public static final float EFFECT_TAIL_SCALE = 0.3f * WorldRenderer.SW / 480;
	public static final float EFFECT_EXPLODE_SCALE = EFFECT_TAIL_SCALE * 2;

	private BasicJumper j;

	private ParticleEffect tailEffect;
	private ParticleEffect explodeEffect;
	private TextureRegion arrow;
	private boolean showArrow;

	public JumperEffects(BasicJumper j) {
		this.j = j;

		// load the effects
		tailEffect = new ParticleEffect();
		tailEffect.load(Gdx.files.internal("effects/fallingStar.p"),
				Gdx.files.internal("effects"));
		tailEffect.setPosition(j.getX(), j.getY());
		tailEffect.scaleEffect(EFFECT_TAIL_SCALE);

		explodeEffect = new ParticleEffect();
		explodeEffect.load(Gdx.files.internal("effects/explodingStar.p"),
				Gdx.files.internal("effects"));
		explodeEffect.setPosition(j.getX(), j.getY());
		explodeEffect.scaleEffect(EFFECT_EXPLODE_SCALE);

		TextureRegion arrowTxt = Assets.getInstance().getGameRegion("arrow");
		arrowTxt.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		arrow = new TextureRegion(arrowTxt);
		showArrow = true;
	}

	public void startTail() {
		tailEffect.start();
	}

	public void stopExplode() {
		explodeEffect.setDuration(0); // Stop gueue
	}

	public void stopTail() {
		tailEffect.setDuration(0); // Stop gueue
	}

	public void jumperKilled() {
		stopTail();
		explodeEffect.setPosition(j.getX(), j.getY());
		explodeEffect.start();
	}

	public void draw(SpriteBatch b){
		tailEffect.draw(b);
		explodeEffect.draw(b);

		b.setColor(Color.WHITE); // default color

		if(showArrow){
			if(j.getState() == State.Turning){
				 b.draw(arrow, j.getCurrentPlanet().getX() - ARROW_W/ 2,
						 j.getCurrentPlanet().getY()+ j.getCurrentPlanet().getR()- j.getPlanetProximity()+ARROW_PADDING_PLANET,
						 ARROW_W / 2, -j.getCurrentPlanet().getR()+ j.getPlanetProximity()-ARROW_PADDING_PLANET,
						 ARROW_W, ARROW_H, 1f, 1f, j.getTheta()-90);
			}else if(j.getState() == State.Sliping){
				
				if (j.getPos().x < WorldRenderer.SW / 2) {
					//left wall
					b.draw(arrow, Walls.WALL_WIDTH + BasicJumper.WALL_PROXIMITY - ARROW_W
							/ 2+ARROW_PADDING_WALL, j.getY() +ARROW_PADDING_WALL*0.7f - ARROW_H/ 2, ARROW_W / 2, ARROW_H / 2, ARROW_W,
							ARROW_H, 1f, 1f, -ARROW_WALL_THETA);
					
				}else{
					//right wall

					b.draw(arrow, WorldRenderer.SW -Walls.WALL_WIDTH - BasicJumper.WALL_PROXIMITY - ARROW_W - ARROW_PADDING_WALL
							/ 2, j.getY() - ARROW_H/ 2+ARROW_PADDING_WALL*0.7f, ARROW_W / 2, ARROW_H / 2, ARROW_W,
							ARROW_H, 1f, 1f, ARROW_WALL_THETA);
					
//					b.draw(arrow, WorldRenderer.SW - Walls.WALL_WIDTH
//							- Jumper.WALL_PROXIMITY - width / 2, pos.y - height / 2,
//							width / 2, height / 2, width, height, 1f, 1f, theta);
					
				}
			}
		}
	}

	public void setTailEffect(ParticleEffect pe){
		pe.setPosition(j.getX(), j.getY());
		pe.scaleEffect(EFFECT_TAIL_SCALE*2);
		
		this.tailEffect.dispose();
		this.tailEffect = pe;
	}
	
	public void updateTailEffect(float delta) {
		tailEffect.update(delta);

		if (j.getState() == State.Jumping || j.getState() == State.Sliping
				|| j.getState() == State.Attracted) {
			tailEffect.setPosition(j.getX(), j.getY());
			if (tailEffect.isComplete()) {
				tailEffect.start();
			}
		}
	}

	public void updateExplodeEffect(float delta) {
		explodeEffect.update(delta);

		// if (state == State.Exploded) {
		// if(explodeEffect.isComplete()){
		// tailEffect.setDuration(0); //Stop gueue
		// }
		// }
	}

	public void update(float delta) {
		// update the effects
		updateTailEffect(delta);
		updateExplodeEffect(delta);
	}
	
	/**
	 * Color the tail of the jumper with the given colors
	 * @param colors
	 */
	public void setTailColor(float[] colors){
		if(tailEffect == null || tailEffect.getEmitters() == null || tailEffect.getEmitters().size == 0)
			return;
		for (ParticleEmitter pm : tailEffect.getEmitters()) {
			float[] t = pm.getTint().getColors();
			t[0] = colors[0];
			t[1] = colors[1];
			t[2] = colors[2];
			pm.getTint().setColors(t);
		}
	}
	
	public void disableHelp(){
		showArrow = false;
	}
}
