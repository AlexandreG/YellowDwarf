package fr.zzi.yellowdwarf.view;

import java.util.LinkedList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

import fr.zzi.yellowdwarf.controller.AchvHandler;
import fr.zzi.yellowdwarf.controller.UIRenderer;
import fr.zzi.yellowdwarf.controller.WorldRenderer;
import fr.zzi.yellowdwarf.controller.utils.Assets;
import fr.zzi.yellowdwarf.controller.utils.Score;

public class Notif {
	public static final String UNLOCKED_JUMPER_KEY = "player_unlocked";

	public static final float LOGO_H = AchvHandler.MEDAL_H * 0.7f;
	public static final float LOGO_W = AchvHandler.MEDAL_W * 0.7f;
	public static final float LOGO_P_RIGHT = LOGO_W * 0.9f;

	public static final float LABEL_W = UIRenderer.SW * 0.4f;

	// the time the notif needs for its movement
	public static final float NOTIF_IN_TIME = 0.5f;
	// the time the notif is shown
	public static final float NOTIF_WAIT_TIME = 2;
	// the time to get outside
	public static final float NOTIF_OUT_TIME = 0.5f;

	public static final float NOTIF_H = LOGO_H * 2.0f;
	public static final float NOTIF_W = UIRenderer.SW * 0.7f;
	public static final float NOTIF_P = LOGO_W * 0.3f;
	public static final float NOTIF_ALPHA = 1.0f;

	private WorldRenderer wr;

	// next notifs to show
	private LinkedList<Actor> waitingList;
	// current notif
	private Actor currentActor;

	private Skin skin = Assets.getInstance().getMenuSkin();
	private NinePatchDrawable background;

	private TextureRegion achievTexture;
	private TextureRegion jumperTexture;

	public Notif(WorldRenderer renderer) {
		waitingList = new LinkedList<Actor>();
		currentActor = null;
		wr = renderer;

		background = new NinePatchDrawable(new NinePatch(Assets.getInstance()
				.getUiRegion("panel"), 15, 15, 15, 15));

		achievTexture = Assets.getInstance().getUiRegion("achievement");
		achievTexture.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);

		jumperTexture = Assets.getInstance().getUiRegion("jumper");
		jumperTexture.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
	}

	public void draw(SpriteBatch b) {
		b.begin();
		if (currentActor != null) {
			currentActor.draw(b, NOTIF_ALPHA);

		}
		b.end();
	}

	public void update(float delta) {
		if (currentActor != null) {
			currentActor.act(delta);
		} else {
			if (waitingList.size() >= 1) {
				currentActor = waitingList.getFirst();
				waitingList.removeFirst();

				currentActor.addAction(getNotifActions());
			}
		}
	}

	/**
	 * Return the come in/wait/get out sequence action of notifs
	 */
	private SequenceAction getNotifActions() {
		// come in
		return Actions.sequence(Actions.moveTo(currentActor.getX(),
				currentActor.getY() - currentActor.getHeight() * 2,
				NOTIF_IN_TIME),
		// wait
				Actions.delay(NOTIF_WAIT_TIME),
				// get out
				Actions.moveTo(currentActor.getX(), currentActor.getY(),
						NOTIF_OUT_TIME),
				// delete
				Actions.run(new Runnable() {
					public void run() {
						currentActor = null;
					}
				}));
	}

	/**
	 * Add an achievement in notifs list
	 * @param achvId
	 */
	public void addAchvEvent(int achvId) {
		Actor a = generateAchvNotif(achvId);
		waitingList.add(a);
	}

	/**
	 * Generate the content of the event div
	 */
	public Actor generateAchvNotif(int achvId) {
		Table result = new Table();
		result.setBackground(background);

		Image logo = new Image(achievTexture);
		Label label = new Label(wr.getScore().getAchvHandler()
				.getAchvTitle(achvId), skin, "defaultTextMedium");

		result.add(logo).width(LOGO_W).height(LOGO_H).padRight(LOGO_P_RIGHT);
		result.add(label);

		result.setWidth(getNotifWidth());
		result.setHeight(getNotifHeight());
		result.setX(UIRenderer.SW / 2 - result.getWidth() / 2);
		result.setY(UIRenderer.SH);
		return result;
	}

	/**
	 * Add a notif of the type player unlocked in the list
	 */
	public void addNewJumperEvent() {
		Actor a = generateNewJumperNotif();
		waitingList.add(a);
	}

	/**
	 * Generate the content of the event div
	 */
	public Actor generateNewJumperNotif() {
		Table result = new Table();
		result.setBackground(background);

		Image logo = new Image(jumperTexture);
		Label label = new Label(Assets.getInstance().getValue(
				UNLOCKED_JUMPER_KEY), skin, "defaultTextMedium");

		result.add(logo).width(LOGO_W).height(LOGO_H).padRight(LOGO_P_RIGHT);
		result.add(label);

		result.setWidth(getNotifWidth());
		result.setHeight(getNotifHeight());
		result.setX(UIRenderer.SW / 2 - result.getWidth() / 2);
		result.setY(UIRenderer.SH);
		return result;
	}
    //TODO AGH

	/**
	 * Calculate the width of the notif according to the screen size
	 */
	private float getNotifWidth(){
//		//the size of the pause button :
//		float k = wr.getScore().getScoreBounds().height * 2;
//
//		//the padding of the pause button :
//		float i = Score.MARGIN_TOP_RUNNING - k * 0.25f;
//
//		return UIRenderer.SW - 2*k - 4*i;
        return 0.9f*UIRenderer.SW;
	}

	/**
	 * Calculate the height of the notif according to the screen size
	 */
	private float getNotifHeight(){
//		return wr.getScore().getScoreBounds().height;
		return  0.15f*UIRenderer.SW;
	}
}
