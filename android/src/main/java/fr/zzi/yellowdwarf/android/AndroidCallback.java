package fr.zzi.yellowdwarf.android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import fr.zzi.yellowdwarf.controller.GlobalCallback;
import fr.zzi.yellowdwarf.controller.utils.Assets;

public class AndroidCallback implements GlobalCallback {
	public static final int LEADERBOARD_ID = 18785;

	@Override
	public void showAdds() {
		// no ads
	}

	@Override
	public void hideAdds() {
		// no ads
	}

	@Override
	public void showToast(final String textToShow) {
		Handler handler = new Handler(Looper.getMainLooper());
		handler.post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(GlobalApp.getInstance().getContext(),
						textToShow, Toast.LENGTH_SHORT).show();
			}
		});

	}

	@Override
	public void showLeaderboards() {
//		if (!Swarm.isInitialized()) {
//			Swarm.init(GlobalApp.getInstance().getMainActivity(),
//					AndroidLauncher.SWARM_APP_ID, AndroidLauncher.SWARM_APP_KEY);
//		}
//		SwarmLeaderboard.showLeaderboard(LEADERBOARD_ID);
	}

    @Override
    public void showPublishScoresDialog(YesNoDialogCallback callback) {

    }

    @Override
	public void submitScore(int score) {
//		if (!Swarm.isInitialized()) {
//			System.out.println("init swarm");
//			Swarm.init(GlobalApp.getInstance().getMainActivity(),
//					AndroidLauncher.SWARM_APP_ID, AndroidLauncher.SWARM_APP_KEY);
//		}
//		SwarmLeaderboard.submitScore(LEADERBOARD_ID, score);
	}

//	@Override
//	/**
//	 * Offer the user to use leaderboards or not
//	 * return 1 if true, 0 if not, -1 if dialog not shown
//	 */
//	public void showPublishScoresDialog(final YesNoDialogCallback cb) {
//		Handler handler = new Handler(Looper.getMainLooper());
//		handler.post(new Runnable() {
//			@Override
//			public void run() {
//				DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						switch (which) {
//						case DialogInterface.BUTTON_POSITIVE:
//							cb.yesClicked();
//							break;
//
//						case DialogInterface.BUTTON_NEGATIVE:
//							cb.noClicked();
//							break;
//						}
//					}
//				};
//
//				AlertDialog.Builder builder = new AlertDialog.Builder(GlobalApp
//						.getInstance().getMainActivity().getContext());
//				builder.setMessage(
//						Assets.getInstance().getValue("use_leaderboard_dialog"))
//						.setPositiveButton(
//								Assets.getInstance().getValue("yes"),
//								dialogClickListener)
//						.setNegativeButton(Assets.getInstance().getValue("no"),
//								dialogClickListener).show();
//			}
//		});
//
//	}

	@Override
	public void submitScoreNextResume(int score) {
		GlobalApp.getInstance().getMainActivity().submitScoreNextResume(score);
	}

	@Override
	public void initSwarm() {
//		if (Swarm.isEnabled()) {
//			Swarm.init(GlobalApp.getInstance().getMainActivity(),
//					AndroidLauncher.SWARM_APP_ID, AndroidLauncher.SWARM_APP_KEY);
//		}
	}

	@Override
	public boolean isNetworkConnected() {
//		ConnectivityManager cm = (ConnectivityManager) GlobalApp.getInstance()
//				.getMainActivity()
//				.getSystemService(Context.CONNECTIVITY_SERVICE);
//		NetworkInfo netInfo = cm.getActiveNetworkInfo();
//		return netInfo != null && netInfo.isConnectedOrConnecting();
        return false;
	}

}
