package fr.zzi.yellowdwarf.model.jumper;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

import fr.zzi.yellowdwarf.controller.WorldRenderer;
import fr.zzi.yellowdwarf.controller.utils.Assets;
import fr.zzi.yellowdwarf.controller.utils.Audios;
import fr.zzi.yellowdwarf.model.BlackHole;
import fr.zzi.yellowdwarf.model.Planet;
import fr.zzi.yellowdwarf.model.PlanetHandler;
import fr.zzi.yellowdwarf.model.Walls;
import fr.zzi.yellowdwarf.model.jumper.BasicJumper.State;

public class RainbowJumper extends BasicJumper {
	public static final float COLOR_ALPHA = 1.0f;// 0=transparent

	public static final float COLOR_SPEED = 0.005f;

	public static final float MIN_COLOR = 150f / 255f;// 0=black
	public static final float NEXT_COLOR = 200f/255f;// 0=black
	public static final float MAX_COLOR = 255f / 255f;

	private float colorR; // tint the texture
	private float colorG; // tint the texture
	private float colorB; // tint the texture

	private ColorState state;
	
	private boolean doubleJumpDone;	//true if we have already 2jump

	private enum ColorState {
		RedUp, GreenUp, BlueUp, RedDown, GreenDown, BlueDown
	}

	public RainbowJumper(WorldRenderer wordlrenderer, State state) {
		super(wordlrenderer, state);
		
		init();
	}
	
	public RainbowJumper(BasicJumper bj, WorldRenderer wordlrenderer, State state) {
		super(bj, wordlrenderer, state);
		
		init();
	}
	
	/**
	 * Init this instance
	 */
	private void init(){
		colorR = (MAX_COLOR - MIN_COLOR)/2;
		colorG = MIN_COLOR;
		colorB = MIN_COLOR;
		this.state = ColorState.RedUp;
		super.maxSpeed = BasicJumper.MAX_SPEED*1.00f;
		doubleJumpDone = false;
		
		super.texture = Assets.getInstance().getGameRegion("jumper4");
		super.texture.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
	}

	@Override
	public void draw(SpriteBatch batch) {
		batch.setColor(colorR, colorG, colorB, COLOR_ALPHA);
		super.draw(batch);
		batch.setColor(Color.WHITE); // default color
	}

	@Override
	public void update(float delta, PlanetHandler pHandler, Walls walls,
			BlackHole bh) {
		super.update(delta, pHandler, walls, bh);

		//RED
		if (this.state == ColorState.RedUp) {
			colorR += COLOR_SPEED;
			colorG -= COLOR_SPEED;
			colorB -= COLOR_SPEED;
			
			if (colorR > MAX_COLOR) {
				state = ColorState.RedDown;
			}
		}
		if (this.state == ColorState.RedDown) {
			colorR -= COLOR_SPEED;
			if (colorR < NEXT_COLOR) {
				state = ColorState.GreenUp;
			}
		}
		
		//GREEN
		if (this.state == ColorState.GreenUp) {
			colorR -= COLOR_SPEED;
			colorG += COLOR_SPEED;
			colorB -= COLOR_SPEED;
			if (colorG > MAX_COLOR) {
				state = ColorState.GreenDown;
			}
		}
		if (this.state == ColorState.GreenDown) {
			colorG -= COLOR_SPEED;
			if (colorG < NEXT_COLOR) {
				state = ColorState.BlueUp;
			}
		}
		
		//BLUE
		if (this.state == ColorState.BlueUp) {
			colorR -= COLOR_SPEED;
			colorG -= COLOR_SPEED;
			colorB += COLOR_SPEED;
			if (colorB > MAX_COLOR) {
				state = ColorState.BlueDown;
			}
		}
		if (this.state == ColorState.BlueDown) {
			colorB -= COLOR_SPEED;
			if (colorB < NEXT_COLOR) {
				state = ColorState.RedUp;
			}
		}

		//Cap color
		if (colorR < MIN_COLOR)
			colorR = MIN_COLOR;
		if (colorG < MIN_COLOR)
			colorG = MIN_COLOR;
		if (colorB < MIN_COLOR)
			colorB = MIN_COLOR;

		super.getEffects().setTailColor(new float[] { colorR, colorG, colorB });
	}
	
	@Override
	public void onTouchEvent() {
		if(super.state == State.Jumping){
			if(!doubleJumpDone){
				//disabled to simplify leaderboards
				// jumpAgain();
			}
		}
		super.onTouchEvent();
	}
	
	private void jumpAgain(){
		Audios.getInstance().playJump();
		if (this.getSpeed().x < 0) {
			this.getSpeed().y = maxSpeed * 0.7f;

		} else {
			this.getSpeed().y = maxSpeed * 0.7f;
		}
		doubleJumpDone = true;
	}
	
	@Override
	public void landOnPlanet(Planet p) {
		doubleJumpDone = false;
		super.landOnPlanet(p);
	}
	
	@Override
	public void landOnWall() {
		doubleJumpDone = false;
		super.landOnWall();
	}
}
