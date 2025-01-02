package fr.zzi.yellowdwarf.controller;

public interface GlobalCallback {
	   public void showAdds();
	   public void hideAdds();
	   public void showToast(String textToShow);
	   
	   public void initSwarm();
	   public void showLeaderboards();
	   public void showPublishScoresDialog(YesNoDialogCallback callback);
	   public void submitScore(int score);
	   public void submitScoreNextResume(int score);

	   public boolean isNetworkConnected();
		
	   public interface YesNoDialogCallback{
		   public void yesClicked();
		   public void noClicked();
	   }

}
