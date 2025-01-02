package fr.zzi.yellowdwarf.controller;

import java.util.LinkedList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import fr.zzi.yellowdwarf.controller.utils.Assets;
import fr.zzi.yellowdwarf.controller.utils.Audios;
import fr.zzi.yellowdwarf.controller.utils.PrefManager;

/**
 * 
 * Handle the jumper content in the menu
 * 
 */
public class JumperHandler {
	public static String PREF_SAVE_KEY = "JumperList";
	public static String I18N_TITLE = "player_title_";
	public static String I18N_DESC = "player_desc_";

	public static float CHECK_BOX_W = AchvHandler.MEDAL_TOTAL_W;

	public static String LOCKED_TITLE_TEXT = "???";
	public static String LOCKED_DESC_KEY = "player_locked_desc";

	public static final int TOTAL_NB = 4;

	private WorldRenderer wr;
	private Skin menuSkin = Assets.getInstance().getMenuSkin();

	private TextureRegion separator;
	private TextureRegion locked;
	private TextureRegion jumper = Assets.getInstance().getGameRegion("jumper");
	private TextureRegion jumper2 = Assets.getInstance().getGameRegion("jumper2");
	private TextureRegion jumper3 = Assets.getInstance().getGameRegion("jumper3");
	private TextureRegion jumper4 = Assets.getInstance().getGameRegion("jumper4");

	private Table content; // div of players in the menu

	private boolean[] jumperList;// true if unlocked
	private int selectedJumper; // selected jumper, 1 to totalNb

	private LinkedList<CheckBox> boxes;
	
	private int cheatClick;

	public JumperHandler(WorldRenderer renderer) {
		wr = renderer;
		cheatClick = 0;

		separator = Assets.getInstance().getUiRegion("separator");
		separator.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);

		locked = Assets.getInstance().getUiRegion("locked");
		locked.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);

		boxes = new LinkedList<CheckBox>();

		loadPlayerConfig();
	}

	/**
	 * Generate the content of the jumpers in the menu
	 */
	public Actor generateJumperContent() {
		content = new Table();

		for (int i = 0; i < TOTAL_NB; ++i) {

			if (jumperList[i] == true) {
				content.add(generateJumperCell(i));
			} else {
				content.add(generateLockedCell(i));
			}
			content.row();

			// add separator
			if (i != TOTAL_NB) {
				content.add(new Image(separator)).width(AchvHandler.CONTENT_W)
						.padTop(AchvHandler.SEPARATOR_PAD)
						.padBottom(AchvHandler.SEPARATOR_PAD);
				content.row();
			}
		}

		setupButtonGroup();

		content.padTop(AchvHandler.CONTENT_PAD_TOP).padBottom(
				AchvHandler.CONTENT_PAD_BOT);

		ScrollPane result = new ScrollPane(content);
		// result.debugAll();
		return result;
	}

	/**
	 * Generate the cell of a player
	 * 
	 * @param id
	 *            the number of the player between 0 and totalNb-1
	 * @return
	 */
	public Actor generateJumperCell(final int id) {
		Table cellResult = new Table();

		// Left image
		Image leftColumnImg = null;
		if (id == 0) {
			leftColumnImg = new Image(jumper);
		} else if (id == 1) {
			leftColumnImg = new Image(jumper2);
		} else if (id == 2) {
			leftColumnImg = new Image(jumper3);
		} else if (id == 3) {
			leftColumnImg = new Image(jumper4);
		}

		// Right column
		Table rightColumn = new Table();

		Label title = new Label(getJumperTitle(id + 1), menuSkin,
				"defaultTextMedium");
		Label desc = new Label(getJumperDesc(id + 1), menuSkin,
				"defaultTextSmall");

		CheckBox checkBox = new CheckBox("", menuSkin, "radioButtonStyle");

		if (id == selectedJumper - 1) {
			checkBox.setChecked(true);
		} else {
			checkBox.setChecked(false);
		}

		rightColumn.add(title)
				.width(AchvHandler.CONTENT_W - AchvHandler.MEDAL_TOTAL_W
						- CHECK_BOX_W);
		rightColumn.row();

		rightColumn.add(desc)
				.width(AchvHandler.CONTENT_W - AchvHandler.MEDAL_TOTAL_W
						- CHECK_BOX_W);

		// result
		cellResult.add(leftColumnImg).height(AchvHandler.MEDAL_H)
				.width(AchvHandler.MEDAL_W).padLeft(AchvHandler.MEDAL_PAD_L)
				.padRight(AchvHandler.MEDAL_PAD_R);
		cellResult.add(rightColumn);

		if (jumperList[1] == true) {
			// we add the checkbox only if there are several character
			cellResult.add(checkBox);
			boxes.add(checkBox);
		}

		addCellListener(cellResult, id);
		return cellResult;
	}

	public Actor generateLockedCell(int id) {
		Table cellResult = new Table();

		// Left image
		Image leftColumnImg = new Image(locked);

		// Right column
		Table rightColumn = new Table();

		Label title = new Label(LOCKED_TITLE_TEXT, menuSkin,
				"defaultTextMedium");
		Label desc = new Label(getLockedDesc(getRemainingAchievement(id + 1)),
				menuSkin, "defaultTextSmall");

		rightColumn.add(title)
				.width(AchvHandler.CONTENT_W - AchvHandler.MEDAL_TOTAL_W
						- CHECK_BOX_W);
		rightColumn.row();

		rightColumn.add(desc)
				.width(AchvHandler.CONTENT_W - AchvHandler.MEDAL_TOTAL_W
						- CHECK_BOX_W);

		// result
		cellResult.add(leftColumnImg).height(AchvHandler.MEDAL_H)
				.width(AchvHandler.MEDAL_W).padLeft(AchvHandler.MEDAL_PAD_L)
				.padRight(AchvHandler.MEDAL_PAD_R);
		cellResult.add(rightColumn);

		return cellResult;
	}

	/**
	 * Add a clicklistener to the given cell, on click cellClicked()
	 * 
	 * @param cell
	 *            to listen
	 * @param id
	 *            of the cell
	 */
	private void addCellListener(Actor cell, final int id) {

		cell.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Audios.getInstance().playClick();
				super.clicked(event, x, y);
				cellClicked(id);
				
				if(jumperList[1] == false){
					++cheatClick;
					if(cheatClick == 50){
						wr.unlockAllJumpers();
					}
				}
			}
		});
	}

	public void resetCheatClick(){
		cheatClick = 0;
	}
	
	/**
	 * A cell has been clicked, updata UI and Game
	 * 
	 * @param id
	 *            of the clicked cell
	 */
	private void cellClicked(int id) {
		for (int i = 0; i < boxes.size(); ++i) {
			if (i == id) {
				boxes.get(id).setChecked(true);
			} else {
				boxes.get(id).setChecked(false);
			}
		}
		selectedJumper = id + 1;
		saveConfig();
		
		wr.updateJumper();
	}

	/**
	 * Init button group (= radio button effect)
	 */
	private void setupButtonGroup() {
		ButtonGroup group = new ButtonGroup();
		group.setMinCheckCount(1);
		group.setMaxCheckCount(1);

		for (CheckBox cb : boxes) {
			group.add(cb);
		}
	}

	/**
	 * Get the title of a jumper
	 * 
	 * @param id
	 *            of the jumper between 1 and totalNb
	 */
	public String getJumperTitle(int id) {
		return Assets.getInstance().getValue(I18N_TITLE + Integer.toString(id));
	}

	/**
	 * Get the description of a jumper
	 * 
	 * @param id
	 *            of the jumper between 1 and totalNb
	 */
	private String getJumperDesc(int id) {
		return Assets.getInstance().getValue(I18N_DESC + Integer.toString(id));
	}

	/**
	 * Get the description of a locked jumper
	 * 
	 * @param achvLeft
	 * @return
	 */
	private String getLockedDesc(int achvLeft) {
		return Assets.getInstance().getValueFormated(LOCKED_DESC_KEY, achvLeft);
	}

	/**
	 * Return the remaining achievements to unlock the given jumper
	 * 
	 * @param jumperId
	 *            (between 1 and totalNb)
	 * @return number of achievements
	 */
	private int getRemainingAchievement(int jumperId) {
		int needed = 0;
		// we remove 1 because first character is free
		needed = (jumperId - 1) * AchvHandler.TOTAL_NB / (TOTAL_NB - 1);
		return needed - wr.getScore().getAchvHandler().getProgress();
	}

	/**
	 * Load the shared pref into class variables
	 */
	private void loadPlayerConfig() {
		String unlockedJumperString = PrefManager.getString(PREF_SAVE_KEY);
		if (unlockedJumperString.isEmpty()) {
			System.out.println("Unable to load unlocked players");
			unlockedJumperString = initPlayerConfig();
		}

		jumperList = new boolean[unlockedJumperString.length()];

		for (int i = 0; i < unlockedJumperString.length(); ++i) {
			if ((unlockedJumperString.charAt(i) + "").equals("0")) {
				// 0 = locked
				jumperList[i] = false;
			} else if ((unlockedJumperString.charAt(i) + "").equals("2")) {
				// 2 = selected
				jumperList[i] = true;
				selectedJumper = i + 1;
			} else {
				// 1 = unlocked
				jumperList[i] = true;
			}
		}
	}

	/**
	 * Init the shared prefs with a basic configuration (everything locked)
	 * 
	 * @return the new save
	 */
	public static String initPlayerConfig() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < TOTAL_NB; ++i) {
			if (i == 0) {
				// first player unlocked
				sb.append("2");
			} else {
				sb.append("0");
			}
		}
		String init = sb.toString();
		PrefManager.saveString(PREF_SAVE_KEY, init);
		System.out.println("New player config generated : " + init);
		return init;
	}
	
	/**
	 * Init the shared prefs with a basic configuration (everything locked)
	 * 
	 * @return the new save
	 */
	public static String unlockAllJumpers() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < TOTAL_NB; ++i) {
			if (i == 0) {
				// first player unlocked
				sb.append("2");
			} else {
				sb.append("1");
			}
		}
		String init = sb.toString();
		PrefManager.saveString(PREF_SAVE_KEY, init);
		System.out.println("All jumpers unlocked : " + init);
		return init;
	}

	/**
	 * Reload the content of an unlocked jumper
	 * 
	 * @param id
	 *            of the cell to reload between 0 and totalNb-1
	 */
	public void reloadContentCell(int id) {
		int cellId = 2 * id; // offset because of the separators
		Cell cellToUpdate = content.getCells().get(cellId);
		if (jumperList[id] == false) {
			cellToUpdate.setActor(generateLockedCell(id));
		} else {
			cellToUpdate.setActor(generateJumperCell(id));
		}
		setupButtonGroup();
	}

	/**
	 * Reload the content of all cells
	 * 
	 */
	public void reloadCells() {
		boxes.clear();
		for (int i = 0; i < TOTAL_NB; ++i) {
			reloadContentCell(i);
		}
	}

	/**
	 * @param progress
	 *            : total of achievements done
	 */
	public void checkIfUnlockedJumper(int progress) {
		int nextLocked = getNextJumperToUnlock();
		if (nextLocked == -1) {
			return;
		}

		int needed = nextLocked * AchvHandler.TOTAL_NB / (TOTAL_NB-1);

		System.out.println("needed : "+ needed +" nextLocked : "+nextLocked);
		
		// new player unlocked
		if (needed <= progress) {
			jumperList[nextLocked] = true;
			
//			if (nextLocked == 2) {
//				// reload cell 1 for the check box
//				reloadContentCell(0);
//			}
//			reloadContentCell(nextLocked - 1);
			saveConfig();

			// launch event
			wr.getUIRenderer().getEventHandler().addNewJumperEvent();
		}
	}

	/**
	 * Return the id of the next player to unlock
	 * 
	 * @return the id (between 0 and totalNb-1), -1 if all are unlocked
	 */
	private int getNextJumperToUnlock() {
		for (int i = 0; i < jumperList.length; ++i) {
			if (jumperList[i] == false) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Return the texture of the given jumper
	 * 
	 * @param id
	 *            of the jumper (1 to totalNb)
	 * @return
	 */
	private TextureRegion getJumperTexture(int id) {
		if (id == 1) {
			return jumper;
		} else if (id == 2) {
			return jumper2;
		} else if (id == 3) {
			return jumper3;
		} else if (id == 4) {
			return jumper4;
		}
		return jumper;
	}

	/**
	 * Save in the shared prefs the unlocked jumpers and the selected one The
	 * save is a String looking like "120" The char pos N represents a boolean
	 * if the N achv is completed 0 = locked, 1 = unlocled, 2 = selected
	 */
	public void saveConfig() {
		// Generate the save
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < jumperList.length; ++i) {
			if (i == selectedJumper - 1) {
				sb.append("2");
			} else {
				if (jumperList[i] == false) {
					sb.append("0");
				} else {
					sb.append("1");
				}
			}
		}
		// save in shared pref
		PrefManager.saveString(PREF_SAVE_KEY, sb.toString());
	}
	
	/**
	 * Return the id of the current jumper between 1 and totalNb
	 * @return
	 */
	public int getSelectedJumperId(){
		return selectedJumper;
	}
}
