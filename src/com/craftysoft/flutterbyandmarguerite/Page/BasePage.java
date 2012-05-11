package com.craftysoft.flutterbyandmarguerite.Page;

import java.util.Collection;
import java.util.HashMap;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.craftysoft.flutterbyandmarguerite.SoundEffect;

public class BasePage {
	
	private int _pageNumber;//page number

	private int _navigationTextId = 0;//text used for buttons that navigate to this page	
	private int _navigationIconId = 0;//icon used for buttons that navigate to this page
	
	private int _narrationHighliteColor = com.craftysoft.flutterbyandmarguerite.pro.R.color.defaultKarokeTextColor;//id of color resource to use as text highliter
	
	private boolean _hasNarrationText = true;//set to false for pages that will not have narration, for example the end page
	private boolean _hasNarrationAudio = true;//set to false for pages that will not have audio narration
	
//	private long[] _pageLoadVibratePattern = null;
	private int _pageLoadVibratePattern = -1;
	
	private HashMap<String, SoundEffect> _soundEffectsLayerMap = new HashMap<String, SoundEffect>();
	
	private Context _context;
	
	private int _backgroundMusicId = 0;//0 is default
	
	private String _packageName = "";
	
	private BasePage[] _nextPages = null;
	private BasePage _backPage = null;
	
	public BasePage(Context context, int pageNumber)
	{
		_context = context;
		_pageNumber = pageNumber;
		
		_packageName = context.getPackageName();
	}
	
	public BasePage(Context context, int pageNumber, int navigationTextId, int navigationIconId)
	{
		_context = context;
		_pageNumber = pageNumber;
		_navigationTextId = navigationTextId;
		_navigationIconId = navigationIconId;
		
		_packageName = context.getPackageName();
	}
	
	public int getPageNumber()
	{
		return _pageNumber;
	}
	
	public boolean hasNarrationText()
	{
		return _hasNarrationText;
	}
	
	public boolean hasNarrationAudio()
	{
		return _hasNarrationAudio;
	}
	
	public void setHasNarrationText(boolean hasText)
	{
		_hasNarrationText = hasText;
	}
	
	public void setHasNarrationAudio(boolean hasAudio)
	{
		_hasNarrationAudio = hasAudio;
	}


	public String getSoundEffectImageName()
	{
		return "p" + _pageNumber + "button.png";
	}
	
	public int getPageDisplayRight()
	{
		return _context.getResources().getIdentifier("p" + _pageNumber + "r", "drawable", _packageName);
	}
	
	public int getPageDisplayLeft()
	{
		return _context.getResources().getIdentifier("p" + _pageNumber + "l", "drawable", _packageName);
	}
	
	public Drawable getPageDisplayRightDrawable()
	{
		return _context.getResources().getDrawable(_context.getResources().getIdentifier("p" + _pageNumber + "r", "drawable", _packageName));
	}
	
	public Drawable getPageDisplayLeftDrawable()
	{
		return _context.getResources().getDrawable(_context.getResources().getIdentifier("p" + _pageNumber + "l", "drawable", _packageName));
	}
	
	public int getPageAudioNarration()
	{
		return _context.getResources().getIdentifier("n" + _pageNumber, "raw", _packageName);
	}
	
	private int getPageTextNarrationId()
	{
		return _context.getResources().getIdentifier("p" + _pageNumber, "string", _packageName);
	}
	
	public String getPageTextNarration()
	{
		return _context.getResources().getText(getPageTextNarrationId()).toString();
	}
	
	//get back page
	public BasePage getBackPage()
	{
		return _backPage;
	}
	
	public void setBackPage(BasePage page)
	{
		_backPage = page;
	}
	
	public BasePage[] getNextPage()
	{
		return _nextPages;
	}
	
	public int getNextPageCount()
	{
		if(_nextPages == null)
			return 0;
		else
			return _nextPages.length;
	}
	
	public void setNextPage(BasePage[] pages)
	{
		_nextPages = pages;
	}

	public int get_navigationTextId() {
		return _navigationTextId;
	}

	public int get_navigationIconId() {
		return _navigationIconId;
	}

	public int get_backgroundMusicId() {
		return _backgroundMusicId;
	}

	public void set_backgroundMusicId(int backgroundMusicId) {
		_backgroundMusicId = backgroundMusicId;
	}

//	public long[] get_pageLoadVibratePattern() {
//		return _pageLoadVibratePattern;
//	}
//
//	public void set_pageLoadVibratePattern(long[] pageLoadVibratePattern) {
//		_pageLoadVibratePattern = pageLoadVibratePattern;
//	}
	public int get_pageLoadVibratePattern() {
		return _pageLoadVibratePattern;
	}

	public void set_pageLoadVibratePattern(int pageLoadVibratePattern) {
		_pageLoadVibratePattern = pageLoadVibratePattern;
	}

	
	
	
	
	public void addToSoundEffectsLayerMap(SoundEffect... soundEffect) {
		for(SoundEffect se : soundEffect)
			_soundEffectsLayerMap.put(se.get_soundEffectColorCode(), se);
	}

	public Collection<SoundEffect> getSoundEffects() {
		return _soundEffectsLayerMap.values();
	}
	
	public SoundEffect getSoundEffect(String colorCode) {
		return _soundEffectsLayerMap.get(colorCode);
	}
	
	public int get_NarrationHighliteColor()
	{
		return _narrationHighliteColor;
	}
	
	public void set_NarrationHighliteColor(int id)
	{
		_narrationHighliteColor = id;
	}

}
