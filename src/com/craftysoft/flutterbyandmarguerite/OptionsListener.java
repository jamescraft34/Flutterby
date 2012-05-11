package com.craftysoft.flutterbyandmarguerite;


public interface OptionsListener {
	
		//toggle the text font size
        void toggleTextSize();
        
        //toggle the background sound on/off
        void toggleBackgroundSound();
        
    	//void vibratePhonePattern(long[] vibratePattern);
    	void vibratePhone(SoundEffect se);
    	void vibratePhoneId(int vibrateId);
    	//void vibratePhoneLength(long vibratePattern);
    	void stopVibrate();
        
        //toggle the audio narration on (visible) and off (invisible)
        void toggleAudioNarration();
        
        //toggle the text narration
        void toggleTextNarration();
                        
        void showOptionsDialog(boolean fromHome);
        
        void playRandomButtonSoundEffect();
        void playRandomNavigationSoundEffect();
        void playQuickChime();
        void playSound(int x, int y);
        
        void goToBookmarkPage();
        void setBookmark(int pageNumber);
        
        void turnBackgroundAudioOn();
        void turnBackgroundAudioOff();
        
        void readBook(int pageIndex);
        
        void saveCharacterAliasInfo(String fAlias, boolean fGirl, boolean fEnabled, 
        							String mAlias, boolean mGirl, boolean mEnabled);
          
		void goPageIndex();
}