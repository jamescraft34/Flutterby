package com.craftysoft.flutterbyandmarguerite;

import com.craftysoft.flutterbyandmarguerite.Page.BasePage;

public interface PageTurnListener {
	public void PageTurned(int pageIndex);
	public void displayUIControls(boolean showControls);
	public int getBitmapColorCode(int x, int y);
	public void playSoundEffect(int colorCode);
	public void displayNarrationOptions(BasePage page);
	public void popupBitmapColorCodeLastPage();
}
