package fr.zzi.yellowdwarf.controller;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import fr.zzi.yellowdwarf.controller.utils.Assets;
import fr.zzi.yellowdwarf.controller.utils.PrefManager;

/**
 *
 * Handle the achievement content in the menu
 *
 */
public class AchvHandler {
	public static String PREF_SAVE_KEY = "achvDoneList";
	public static String I18N_TITLE = "achv_title_";
	public static String I18N_DESC = "achv_desc_";

	// Events vars : needed to fill the achievement description
	// max planet
	public int ACHV_1_NB = 10;
	public int ACHV_2_NB = 30;
	public int ACHV_3_NB = 50;
	// skip 1 planet
	public int ACHV_4_NB = 1;
	// reach planet -
	public int ACHV_5_NB = -1;
	public int ACHV_6_NB = -2;
	// jump on the same planet
	public int ACHV_7_NB = 2;
	public int ACHV_8_NB = 3;
	public int ACHV_9_NB = 4;
	// slide on wall
	public int ACHV_10_NB = 2;
	public int ACHV_11_NB = 3;
	public int ACHV_12_NB = 4;
	// fly without collision
	public int ACHV_13_NB = 2;
	public int ACHV_14_NB = 3;
	public int ACHV_15_NB = 4;
	// total planet
	public int ACHV_16_NB = 100;
	public int ACHV_17_NB = 1000;
	public int ACHV_18_NB = 5000;

	// Layout vars
	public static float CONTENT_W = UIRenderer.PANEL_W * 0.75f;
	public static float CONTENT_PAD_BOT = CONTENT_W * 0.07f;
	public static float CONTENT_PAD_TOP = CONTENT_W * 0.07f;
	public static float CONTENT_H = UIRenderer.PANEL_H - CONTENT_PAD_BOT
			- CONTENT_PAD_TOP;
	public static float MEDAL_H = UIRenderer.CONTENT_BUTT_W * 0.5f;
	public static float MEDAL_W = MEDAL_H;
	public static float MEDAL_PAD_L = 0;
	public static float MEDAL_PAD_R = MEDAL_H * 0.3f;
	public static float MEDAL_TOTAL_W = MEDAL_PAD_L + MEDAL_PAD_R + MEDAL_W;

	public static float SEPARATOR_PAD = CONTENT_H * 0.03f;

	public static final int TOTAL_NB = 18; // default nb

	private WorldRenderer wr;

	// Layout assets
	private TextureRegion separator;
	private TextureRegion goldMedal;
	private TextureRegion locked;

	private Table content; // div of achievements in the menu
	private Skin menuSkin = Assets.getInstance().getMenuSkin();

	private boolean[] doneList;
	private int progress;

	public AchvHandler(WorldRenderer renderer) {
		wr = renderer;

		separator = Assets.getInstance().getUiRegion("separator");
		separator.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);

		goldMedal = Assets.getInstance().getUiRegion("medalGold");
		goldMedal.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);

		locked = Assets.getInstance().getUiRegion("medalGoldLocked");
		locked.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);

		loadAchv();
		generateAchievementContent();
	}

	/**
	 * Load achievements from shared pref to class variables
	 */
	private void loadAchv() {
		String doneListString = PrefManager.getString(PREF_SAVE_KEY);
		if (doneListString.isEmpty()) {
			System.out.println("Unable to load achivements");
			doneListString = initAchvConfig();
		}

		doneList = new boolean[doneListString.length()];

		for (int i = 0; i < doneListString.length(); ++i) {
			if ((doneListString.charAt(i) + "").equals("0")) {
				doneList[i] = false;
			} else {
				doneList[i] = true;
			}
		}

		progress = calculateProgress();
	}

	/**
	 * Init the shared prefs with a basic configuration (everything locked)
	 *
	 * @return the new save
	 */
	public static String initAchvConfig() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < TOTAL_NB; ++i) {
			sb.append("0");
		}
		String init = sb.toString();
		PrefManager.saveString(PREF_SAVE_KEY, init);
		System.out.println("New achv config generated : " + init);
		return init;
	}

	public void generateAchievementContent() {
		content = new Table();

		for (int i = 0; i < TOTAL_NB; ++i) {

			content.add(generateAchievementCell(i));
			content.row();

			// add separator
			if (i != TOTAL_NB) {
				content.add(new Image(separator)).width(CONTENT_W)
						.padTop(SEPARATOR_PAD).padBottom(SEPARATOR_PAD);
				content.row();
			}
		}

		content.padTop(CONTENT_PAD_TOP).padBottom(CONTENT_PAD_BOT);
	}

	/**
	 * Generate the cell of an achievement
	 *
	 * @param achId
	 *            the number of the achievement between 0 and totalNb-1
	 * @return
	 */
	public Actor generateAchievementCell(int achId) {
		Table cellResult = new Table();

		// Left image
		Image leftColumnImg;
		if (doneList[achId] == false) {
			// locked img
			leftColumnImg = new Image(locked);
		} else {
			// medal img
			leftColumnImg = new Image(goldMedal);
		}

		// Right column
		Table rightColumn = new Table();

		Label title = new Label(getAchvTitle(achId + 1), menuSkin,
				"defaultTextMedium");
		Label desc = new Label(getAchvDesc(achId + 1), menuSkin,
				"defaultTextSmall");

		rightColumn.add(title).width(CONTENT_W - MEDAL_TOTAL_W);
		rightColumn.row();

		rightColumn.add(desc).width(CONTENT_W - MEDAL_TOTAL_W);

		// result
		cellResult.add(leftColumnImg).height(MEDAL_H).width(MEDAL_W)
				.padLeft(MEDAL_PAD_L).padRight(MEDAL_PAD_R);
		cellResult.add(rightColumn);

		return cellResult;
	}

	/**
	 * Get the title of an achievement
	 *
	 * @param id
	 *            of the achievement between 1 and totalNb
	 */
	public String getAchvTitle(int id) {
		return Assets.getInstance().getValue(I18N_TITLE + Integer.toString(id));
	}

	/**
	 * Get the description of an achievement
	 *
	 * @param id
	 *            of the achievement between 1 and totalNb
	 */
	private String getAchvDesc(int id) {
		if (id == 1) {
			return Assets.getInstance().getValueFormated(
					I18N_DESC + Integer.toString(id), ACHV_1_NB);
		} else if (id == 2) {
			return Assets.getInstance().getValueFormated(
					I18N_DESC + Integer.toString(id), ACHV_2_NB);
		} else if (id == 3) {
			return Assets.getInstance().getValueFormated(
					I18N_DESC + Integer.toString(id), ACHV_3_NB);
		} else if (id == 5) {
			return Assets.getInstance().getValueFormated(
					I18N_DESC + Integer.toString(id), ACHV_5_NB);
		} else if (id == 6) {
			return Assets.getInstance().getValueFormated(
					I18N_DESC + Integer.toString(id), ACHV_6_NB);
		} else if (id == 7) {
			return Assets.getInstance().getValueFormated(
					I18N_DESC + Integer.toString(id), ACHV_7_NB);
		} else if (id == 8) {
			return Assets.getInstance().getValueFormated(
					I18N_DESC + Integer.toString(id), ACHV_8_NB);
		} else if (id == 9) {
			return Assets.getInstance().getValueFormated(
					I18N_DESC + Integer.toString(id), ACHV_9_NB);
		} else if (id == 10) {
			return Assets.getInstance().getValueFormated(
					I18N_DESC + Integer.toString(id), ACHV_10_NB);
		} else if (id == 11) {
			return Assets.getInstance().getValueFormated(
					I18N_DESC + Integer.toString(id), ACHV_11_NB);
		} else if (id == 12) {
			return Assets.getInstance().getValueFormated(
					I18N_DESC + Integer.toString(id), ACHV_12_NB);
		} else if (id == 13) {
			return Assets.getInstance().getValueFormated(
					I18N_DESC + Integer.toString(id), ACHV_13_NB);
		} else if (id == 14) {
			return Assets.getInstance().getValueFormated(
					I18N_DESC + Integer.toString(id), ACHV_14_NB);
		} else if (id == 15) {
			return Assets.getInstance().getValueFormated(
					I18N_DESC + Integer.toString(id), ACHV_15_NB);
		} else if (id == 16) {
			return Assets.getInstance().getValueFormated(
					I18N_DESC + Integer.toString(id), ACHV_16_NB);
		} else if (id == 17) {
			return Assets.getInstance().getValueFormated(
					I18N_DESC + Integer.toString(id), ACHV_17_NB);
		} else if (id == 18) {
			return Assets.getInstance().getValueFormated(
					I18N_DESC + Integer.toString(id), ACHV_18_NB);
		} else {
			return Assets.getInstance().getValue(
					I18N_DESC + Integer.toString(id));
		}

	}

	/**
	 * Notify that the achievement id has been completed
	 *
	 * @param id of the achievement (between 1 and totalNb)
	 */
	public void achvCompleted(int id) {
		System.out.println("New achv : n" + Integer.toString(id));
		doneList[id - 1] = true;
		progress++;
		saveAchv();
		unlockAchv(id - 1); // refresh the content menu

		// add event notif
		wr.getUIRenderer().getEventHandler().addAchvEvent(id);

		// check if jumper unlocked
		wr.getUIRenderer().getJumperHandler().checkIfUnlockedJumper(progress);
	}

	/**
	 * Save in the shared prefs the unlocked achivements The save is a String
	 * looking like "1100010110" The char pos N represents a boolean if the N
	 * achv is completed
	 */
	public void saveAchv() {
		// Generate the save
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < doneList.length; ++i) {
			if (doneList[i] == false) {
				sb.append("0");
			} else {
				sb.append("1");
			}
		}
		// save in shared pref
		PrefManager.saveString(PREF_SAVE_KEY, sb.toString());
	}

	/**
	 * Reload the content of an unlocked achievement
	 *
	 * @param id
	 *            of the cell to reload between 0 and totalNb-1
	 */
	public void unlockAchv(int id) {
		int cellId = 2 * id; // offset because of the separators
		Cell cellToUpdate = content.getCells().get(cellId);
		cellToUpdate.setActor(generateAchievementCell(id));
	}

	public Actor getContent() {
		ScrollPane result = new ScrollPane(content);
		// result.debugAll();
		return result;
	}

	public boolean[] getDoneList() {
		return doneList;
	}

	/**
	 * Return the number of completed achievements
	 */
	private int calculateProgress() {
		int progress = 0;
		for (int i = 0; i < doneList.length; ++i) {
			if (doneList[i] == true) {
				++progress;
			}
		}
		return progress;
	}

	public int getProgress() {
		return progress;
	}
}
