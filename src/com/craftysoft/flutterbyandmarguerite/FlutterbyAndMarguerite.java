package com.craftysoft.flutterbyandmarguerite;

import java.io.InputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.WindowManager;
import android.widget.Toast;

import com.craftysoft.flutterbyandmarguerite.Haptic.BlueFlower;
import com.craftysoft.flutterbyandmarguerite.Haptic.Boing;
import com.craftysoft.flutterbyandmarguerite.Haptic.Butterfly;
import com.craftysoft.flutterbyandmarguerite.Haptic.ButterflyScared;
import com.craftysoft.flutterbyandmarguerite.Haptic.Cat;
import com.craftysoft.flutterbyandmarguerite.Haptic.Dog;
import com.craftysoft.flutterbyandmarguerite.Haptic.FlutterWings;
import com.craftysoft.flutterbyandmarguerite.Haptic.FlutterbySlurp;
import com.craftysoft.flutterbyandmarguerite.Haptic.Grass;
import com.craftysoft.flutterbyandmarguerite.Haptic.Hammer;
import com.craftysoft.flutterbyandmarguerite.Haptic.Hugs;
import com.craftysoft.flutterbyandmarguerite.Haptic.MarLaugh;
import com.craftysoft.flutterbyandmarguerite.Haptic.MarSlurp;
import com.craftysoft.flutterbyandmarguerite.Haptic.RedFlower;
import com.craftysoft.flutterbyandmarguerite.Haptic.Saw;
import com.craftysoft.flutterbyandmarguerite.Haptic.Soup;
import com.craftysoft.flutterbyandmarguerite.Page.BasePage;
import com.craftysoft.flutterbyandmarguerite.Page.Index;
import com.craftysoft.flutterbyandmarguerite.pro.R;
import com.immersion.uhl.Device;
import com.immersion.uhl.IVTBuffer;
import com.immersion.uhl.Launcher;

public class FlutterbyAndMarguerite extends Activity implements OptionsListener {
	
	//haptic stuff
	private Launcher mLauncher;
	private Device dev;
	
	private BroadcastReceiver mScreenReceiver = null;
	private IntentFilter filter = null;
	
	public static int _defaultBackgroundMusicId = R.raw.playfuljack;
	private static int _currentBackgroundMusicId = -1;
	
	private int _width, _height= 0;
	
	private boolean _hasMicrophone = true;
	
	public final static boolean AUTO_SCROLL_TEXT = true;//turn on or off auto scrollign for narration text
	
	private Index _indexPage = null;
	
	private static boolean _bgMusicIsPaused = false;
		
	// contains the page layout data for the page curler
	public static ArrayList<BasePage> PAGES = null;

	public static final String FLUTTERBY = "Flutterby";
	public static final String MARGUERITE = "Marguerite";

	public static final int TOTALPAGECOUNT = 24;
	
	private Bitmap bmpSounds = null;
	private String _titlePageSoundEffectLayer = "ptitlepagebutton.png";
	private String _flutterbySoundEffectColorCode = "ff000098";

	// activity result code returned from the page index activity
	private final int PAGE_INDEX_CODE = 34;

	private final String PREFERENCE_FILE = "craftysoft.flutterbyPreferences";
	private final String BOOKMARK_PREF = "bookmarkPref";
	private final String FLUTTERBY_ALIAS_PREF = "flutterbyAliasPref";
	private final String MARGUERITE_ALIAS_PREF = "margueriteAliasPref";
	private final String FLUTTERBY_GENDER_F_PREF = "flutterbyGenderFPref";
	private final String MARGUERITE_GENDER_F_PREF = "margueriteGenderFPref";
	private final String FLUTTERBY_ALIAS_ENABLED_PREF = "flutterbyAliasEnabledPref";
	private final String MARGUERITE_ALIAS_ENABLED_PREF = "margueriteAliasEnabledPref";

	// aliases and gender for Flutterby and Marg are saved in the preference
	// object
	public static String _flutterbyAlias = "";
	public static String _margueriteAlias = "";
	public static boolean _flutterbyFemale = false;
	public static boolean _margueriteFemale = false;
	public static boolean _flutterbyAliasEnabled = false;
	public static boolean _margueriteAliasEnabled = false;

	private SharedPreferences _flutterbyPrefs = null;

	public static OptionsListener optionsListener = null;

	public static boolean _textIsLarge = false;// font size can either be
												// default or large
	public static final float _defaultFontAdjustPercentage = .3f;
	public static float _defaultFontSize = 17f;
	public static float _densityScale;

	public static boolean _playAudioNarration = true;
	public static boolean _playBackgroundAudio = true;

	public static boolean _displayTextNarration = true;

	// handles and top level sound effects for the main activity
	private MasterSoundEffects _applicationSoundEffects = null;

	// handles the background music
	public static MediaPlayer _mediaPlayer = null;

	private Vibrator _vibe = null;
	
	public static String _selectedNarrationProfile = "";
	
	public ArrayList<SoundEffect> _soundEffectsLayerList = null;
	
	public static void setNarrationProfile(String profileName)
	{
		_selectedNarrationProfile = profileName;		
	}
	
	
	/*
	 * Flutterby and Marguerite alias logic
	 */
	public static String replaceCharacterDetails(String text)
	{
		return replaceMargueriteDetail(replaceFlutterbyDetail(text));
	}

	//all Flutterby references are surrounded with <f></f>
	private static String replaceFlutterbyDetail(String text)
	{
		String newText = text.replaceAll("<f>Flutterby</f>", get_flutterbyAlias());	
	
		return replaceProNouns(newText, is_flutterbyFemale(), "f", FlutterbyAndMarguerite._flutterbyAliasEnabled);
	}
	
	//all Marguerite references are surrounded with <m></m>
	private static String replaceMargueriteDetail(String text)
	{
		String newText = text.replaceAll("<m>Marguerite</m>", get_margueriteAlias());
		
		return replaceProNouns(newText, is_margueriteFemale(), "m", FlutterbyAndMarguerite._margueriteAliasEnabled);
	}

	private static String replaceProNouns(String text, boolean genderFemale, String ch, boolean aliasEnabled)
	{
		if((genderFemale) || (!aliasEnabled))
		{
			return text.replaceAll("<" + ch + ">her</" + ch + ">", "her").replaceAll("<" + ch + ">Her</" + ch + ">", "Her").
							replaceAll("<" + ch + ">she</" + ch + ">", "she").replaceAll("<" + ch + ">She</" + ch + ">", "She").
							replaceAll("<" + ch + "_>her</" + ch + "_>", "her").replaceAll("<" + ch + "_>Her</" + ch + "_>", "Her");	
		}
		else
		{
			return text.replaceAll("<" + ch + ">her</" + ch + ">", "him").replaceAll("<" + ch + ">Her</" + ch + ">", "Him").
							replaceAll("<" + ch + ">she</" + ch + ">", "he").replaceAll("<" + ch + ">She</" + ch + ">", "He").
							replaceAll("<" + ch + "_>her</" + ch + "_>", "his").replaceAll("<" + ch + "_>Her</" + ch + "_>", "His");	
		}
	}
	
	private static String get_flutterbyAlias() {
		String aliasName = FlutterbyAndMarguerite.FLUTTERBY;
		
		if(FlutterbyAndMarguerite._flutterbyAliasEnabled)//only replace text if the alias is turned on
		{
			if(!FlutterbyAndMarguerite._flutterbyAlias.equals(""))
				aliasName = FlutterbyAndMarguerite._flutterbyAlias;		
		}
		return aliasName;
	}

	private static String get_margueriteAlias() {
		String aliasName = FlutterbyAndMarguerite.MARGUERITE;
		
		if(FlutterbyAndMarguerite._margueriteAliasEnabled)//only replace text if the alias is turned on
		{
			if(!FlutterbyAndMarguerite._margueriteAlias.equals(""))
				aliasName = FlutterbyAndMarguerite._margueriteAlias;		
		}
		return aliasName;
	}

	private static boolean is_flutterbyFemale() {
		return FlutterbyAndMarguerite._flutterbyFemale;
	}

	private static boolean is_margueriteFemale() {
		return FlutterbyAndMarguerite._margueriteFemale;
	}

//	public void stopVibrate() {
//		try {
//		if (_vibe != null)
//			_vibe.cancel();
//		} catch (Exception ex) {
//			// do nothing
//		}
//	}
	
	public void stopVibrate() 
	{
		//first try and stop the builtin haptic vibes
		try 
		{
			if(mLauncher != null)
				mLauncher.stop();
		} 
		catch (Exception ex) 
		{
			// do nothing
		}
		
		//now try and stop the custom haptic effects
		try
		{
			if(dev != null)
				dev.stopAllPlayingEffects();
		}
		catch(Exception ex)
		{
			//do nothing
		}
		
		//try to stop the native vibration
		try 
		{
			if (_vibe != null)
				_vibe.cancel();
		} 
		catch (Exception ex) 
		{	
			// do nothing
		}
	}
	
	public void vibratePhone(SoundEffect se) 
	{		
		if(se.get_vibratePattern() != null)
		{
			vibratePhonePattern(se.get_vibratePattern());
		}
		else if(se.get_vibrateCustom() != null)
		{
			vibratePhoneCustom(se.get_vibrateCustom());
		}

		else
		{
			vibratePhoneId(se.get_vibrateId());
		}
	}
	
	private void vibratePhoneCustom(byte[] ivt)
	{
		try
		{
			IVTBuffer ivtBuffer = new IVTBuffer(ivt);

			dev.stopAllPlayingEffects();
			dev.playIVTEffect(ivtBuffer, 0);
		}
		catch(Exception e)
		{
			
		}
	}
	
	public void vibratePhoneId(int vibrateId)
	{
		try 
		{		 
			mLauncher.play(vibrateId);		 
		} 
		catch (Exception e) 
		{		 
			//Log.e("CraftySoft", "Failed to play built-in effect, index " + vibratePattern  + ": "+e); 
		}
	}
	
	private void vibratePhonePattern(long[] vibratePattern) {
		try {
			if (_vibe == null)
				_vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

			_vibe.vibrate(vibratePattern, -1);
		} catch (Exception ex) {
			// do nothing
		}
	}

//	public void vibratePhoneLength(long vibrateLength) {
//		try {
//			if (_vibe == null)
//				_vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//
//			_vibe.vibrate(vibrateLength);
//		} catch (Exception ex) {
//			// do nothing
//		}
//	}

	private boolean isXLargeScreen() {
		return ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
			
		String brandName = Build.BRAND;
		
		if(brandName.equals(this.getResources().getString(R.string.nookdevice)))
			setContentView(R.layout.indexnook);
		else
			setContentView(R.layout.index);
		
		//initialize haptic stuff
		try 
		{	 
			mLauncher = new Launcher(this);
		} 
		catch (Exception e) 
		{
		}
		
		try 
		{
			dev = Device.newDevice(this);	
		} 
		catch (Exception e) 
		{
		}
								
		//do we have a microphone on this device?
		_hasMicrophone  = getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE);
		
		_indexPage = (Index)findViewById(R.id.RootMain);

		// override the transition animation for this activity
		// FlutterbyAndMarguerite.this.overridePendingTransition(R.anim.curler_fade_in,
		// R.anim.curler_fade_out);

		// adjust default screen font size for XLarge screens
		if (isXLargeScreen())
			_defaultFontSize = 34f;

		_densityScale = getResources().getDisplayMetrics().density;

		// listener for the options menu activity to be able to call back to methods here
		optionsListener = (OptionsListener) this;

		// get singleton mastersoundeffects
		_applicationSoundEffects = MasterSoundEffects.getInstance(this);

		// get preferences file or create if not already created
		_flutterbyPrefs = getSharedPreferences(PREFERENCE_FILE,
				Context.MODE_PRIVATE);

		// DEBUG CALL ONLY...
		// resetPrefs();

		// register screen on/off receiver
//		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
//		filter.addAction(Intent.ACTION_SCREEN_OFF);
//		BroadcastReceiver mScreenReceiver = new ScreenReceiver();
		filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		mScreenReceiver = new ScreenReceiver();
//		registerReceiver(mScreenReceiver, filter);

		//initialize sound effect layer
		initSoundEffectLayer();

		//save width and height for sound effect layer creation
		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay(); 
		_width = display.getWidth(); 
		_height = display.getHeight();

		//initialize title page sound effect layer
		initializeTitlePageSoundEffectLayer();

		// initialize content pages to be used in the story
		PAGES = initPages();
	}
		
	private void initializeTitlePageSoundEffectLayer()
	{
		AssetManager assetManager = getAssets();

		InputStream imageIn = null;

		try 
		{    		
			imageIn = assetManager.open(_titlePageSoundEffectLayer, AssetManager.ACCESS_BUFFER);    
			
			if(bmpSounds != null)
				bmpSounds.recycle();//make sure to recycle anything that already may be in there
			
			bmpSounds = BitmapFactory.decodeStream(imageIn);			
			bmpSounds = Bitmap.createScaledBitmap(bmpSounds, _width, _height, true);
		} 
		catch (Exception e) 
		{
			//do nothing
		}
	}
	
	private void initSoundEffectLayer()
	{
		if(_soundEffectsLayerList == null)
			_soundEffectsLayerList = new ArrayList<SoundEffect>();

		_soundEffectsLayerList.add(new SoundEffect(R.raw.butterfly, "ff000001", Butterfly.ivt));
		
		_soundEffectsLayerList.add(new SoundEffect(R.raw.redflower, "ff000002", RedFlower.ivt));
		_soundEffectsLayerList.add(new SoundEffect(R.raw.blueflower, "ff000003", BlueFlower.ivt));
//		_soundEffectsLayerList.add(new SoundEffect(R.raw.redflower, "ff000002", Launcher.TEXTURE7));
//		_soundEffectsLayerList.add(new SoundEffect(R.raw.blueflower, "ff000003", Launcher.TEXTURE7));
		
		_soundEffectsLayerList.add(new SoundEffect(R.raw.flutterwings, "ff000004", FlutterWings.ivt));				
		_soundEffectsLayerList.add(new SoundEffect(R.raw.margueritelaugh, "ff000005", MarLaugh.ivt));
		_soundEffectsLayerList.add(new SoundEffect(R.raw.flutterbyslurp, "ff000006", FlutterbySlurp.ivt));	
		_soundEffectsLayerList.add(new SoundEffect(R.raw.margueriteslurp, "ff000007", MarSlurp.ivt));
		_soundEffectsLayerList.add(new SoundEffect(R.raw.wasp, "ff000008", new long[]{300,500,100,500,100,500,100,300,300,600,100,600,100}));		
		_soundEffectsLayerList.add(new SoundEffect(R.raw.margueritetough, "ff000009",Launcher.LONG_BUZZ_100));		
		_soundEffectsLayerList.add(new SoundEffect(R.raw.flutterbyscared, "ff000010", Launcher.LONG_TRANSITION_RAMP_DOWN_33));
		_soundEffectsLayerList.add(new SoundEffect(R.raw.hugs, "ff000011", Hugs.ivt));		
		
		_soundEffectsLayerList.add(new SoundEffect(R.raw.greenflower, "ff000012", RedFlower.ivt));		
//		_soundEffectsLayerList.add(new SoundEffect(R.raw.greenflower, "ff000012", Launcher.TEXTURE7));				
		
		_soundEffectsLayerList.add(new SoundEffect(R.raw.water, "ff000013", Launcher.TRANSITION_BOUNCE_66));		
		_soundEffectsLayerList.add(new SoundEffect(R.raw.seeds, "ff000014", Launcher.TRANSITION_BUMP_100));		
		_soundEffectsLayerList.add(new SoundEffect(R.raw.saw, "ff000015", Saw.ivt));
		_soundEffectsLayerList.add(new SoundEffect(R.raw.hammer, "ff000016", Hammer.ivt));		
		_soundEffectsLayerList.add(new SoundEffect(R.raw.cashregister, "ff000017", Launcher.TRANSITION_BUMP_66));
		_soundEffectsLayerList.add(new SoundEffect(R.raw.boing, "ff000018", Boing.ivt));
		_soundEffectsLayerList.add(new SoundEffect(R.raw.giggle, "ff000019", MarLaugh.ivt));
		_soundEffectsLayerList.add(new SoundEffect(R.raw.soup, "ff000020", Soup.ivt));
		_soundEffectsLayerList.add(new SoundEffect(R.raw.grass, "ff000021", Grass.ivt));
		_soundEffectsLayerList.add(new SoundEffect(R.raw.butterflyscared, "ff000022", ButterflyScared.ivt));
		_soundEffectsLayerList.add(new SoundEffect(R.raw.throat, "ff000023", Launcher.WEAPON7));		
		_soundEffectsLayerList.add(new SoundEffect(R.raw.cat, "ff000024", Cat.ivt));
		_soundEffectsLayerList.add(new SoundEffect(R.raw.dog, "ff000025", Dog.ivt));
	}
	
	
//	private void initSoundEffectLayer()
//	{
//		if(_soundEffectsLayerList == null)
//			_soundEffectsLayerList = new ArrayList<SoundEffect>();
//		
//		_soundEffectsLayerList.add(new SoundEffect(R.raw.butterfly, "ff000001", new long[]{100,200,100,200,100,200,100,200,100,200,100,200,100,200,100,200,100,200,100,200,100,200,100,200,100,200,100,200,100,200}));
//		_soundEffectsLayerList.add(new SoundEffect(R.raw.redflower, "ff000002"));
//		_soundEffectsLayerList.add(new SoundEffect(R.raw.blueflower, "ff000003"));
//		_soundEffectsLayerList.add(new SoundEffect(R.raw.flutterwings, "ff000004", new long[]{100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100}));		
//		_soundEffectsLayerList.add(new SoundEffect(R.raw.margueritelaugh, "ff000005"));
//		_soundEffectsLayerList.add(new SoundEffect(R.raw.flutterbyslurp, "ff000006"));
//		_soundEffectsLayerList.add(new SoundEffect(R.raw.margueriteslurp, "ff000007"));
//		_soundEffectsLayerList.add(new SoundEffect(R.raw.wasp, "ff000008", new long[]{300,500,100,500,100,500,100,300,300,600,100,600,100}));
//		_soundEffectsLayerList.add(new SoundEffect(R.raw.margueritetough, "ff000009",new long[]{0,1500}));
//		_soundEffectsLayerList.add(new SoundEffect(R.raw.flutterbyscared, "ff000010"));
//		_soundEffectsLayerList.add(new SoundEffect(R.raw.flutterbyscared, "ff000010", Launcher.LONG_TRANSITION_RAMP_DOWN_33));
//		_soundEffectsLayerList.add(new SoundEffect(R.raw.hugs, "ff000011"));
//		_soundEffectsLayerList.add(new SoundEffect(R.raw.greenflower, "ff000012"));
//		_soundEffectsLayerList.add(new SoundEffect(R.raw.water, "ff000013"));		
//		_soundEffectsLayerList.add(new SoundEffect(R.raw.seeds, "ff000014"));
//		_soundEffectsLayerList.add(new SoundEffect(R.raw.saw, "ff000015", new long[]{100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100}));
//		_soundEffectsLayerList.add(new SoundEffect(R.raw.hammer, "ff000016", new long[]{100,200,200,200,200,200,200,200,200,200,200,200,200,200}));
//		_soundEffectsLayerList.add(new SoundEffect(R.raw.cashregister, "ff000017"));
//		_soundEffectsLayerList.add(new SoundEffect(R.raw.boing, "ff000018"));
//		_soundEffectsLayerList.add(new SoundEffect(R.raw.giggle, "ff000019"));
//		_soundEffectsLayerList.add(new SoundEffect(R.raw.soup, "ff000020"));
//		_soundEffectsLayerList.add(new SoundEffect(R.raw.grass, "ff000021"));
//		_soundEffectsLayerList.add(new SoundEffect(R.raw.butterflyscared, "ff000022"));
//		_soundEffectsLayerList.add(new SoundEffect(R.raw.throat, "ff000023"));		
//		_soundEffectsLayerList.add(new SoundEffect(R.raw.cat, "ff000024"));
//		_soundEffectsLayerList.add(new SoundEffect(R.raw.dog, "ff000025"));
//	}

	// private BasePage initPages(int i, BasePage backPage)
	// {
	// BasePage page = new BasePage(this, i);
	//		
	// page.setBackPage(backPage);
	//
	// PAGES.add(page);
	//		
	// if(i == 3)
	// page.setNextPage(new BasePage[]{ initPages(4, page), initPages(16, page)
	// });
	// // else if(i == 17)
	// // page.setNextPage(new BasePage[]{ initPages(18, page), initPages(20,
	// page) });
	// // // else if(i == 19)
	// // // page.setNextPage(new BasePage[]{ initPages(6, page) });
	// else if(i == 8)
	// page.setNextPage(new BasePage[]{ initPages(9, page)});//, initPages(11,
	// page), initPages(14, page) });
	// else if(i <= TOTALPAGECOUNT)
	// {
	//			
	// //if((i != 4) && (i != 16) && (i != 18) && (i != 20) && (i != 9) && (i !=
	// 11) && (i != 14))
	// page.setNextPage(new BasePage[]{ initPages(++i, page) });
	// }
	//		
	// return page;
	// }
	
	public static void unPauseBackgroundMusic()
	{
		_forcePause = false;

		if(!_bgMusicIsPaused)
			return;
		
		_bgMusicIsPaused = false;
				
		try{
			if(_playBackgroundAudio)
			{
				if(_mediaPlayer != null)
					_mediaPlayer.start();
			}
		}
		catch(Exception ex)
		{
			//do nothing
		}		
	}
	
	private static boolean _forcePause = true;
	public static void pauseBackgroundMusic()
	{
		_forcePause = true;
		
		if(!_bgMusicIsPaused)
		{
			if(_mediaPlayer != null)
			{
				try {
					
					new Handler().postDelayed(new Runnable(){
						 
                        @Override
                        public void run() 
                        {
                        	//create a delay so we dont always pause the background music when switching between activities
                        	//because that creates a slight pause in the music, if we hide the application by pressing the home key etc.. then
                        	//the music will be paused after a second since the _forcePause flag will not be flipped
                        	if(_forcePause)
                        	{                        		
                        		if(_mediaPlayer != null)
                        		{
                        			_mediaPlayer.pause();
                            		_bgMusicIsPaused = true;
                        		}
                        	}
        		        }
					}, 1000);
				} catch (Exception e) {
					// do nothing
				}
			}
		}
	}
	
	public static void startBackgroundMusic(Context context, int id)
	{		
		_forcePause = false;
		
		try {
			if(id != _currentBackgroundMusicId)
			{
				_currentBackgroundMusicId = id;

				if(id == 0)//default
					id = _defaultBackgroundMusicId;
					
				endBackgroundMusic();//make sure we stop anything that is playing
				
				// start the background music
				_mediaPlayer = MediaPlayer.create(context, id);
				_mediaPlayer.setLooping(true);
				_mediaPlayer.setVolume(0.19f, 0.19f);// TODO: VOLUME, WHAT WORKS?
				
				_mediaPlayer.setOnPreparedListener(new OnPreparedListener(){

					@Override
					public void onPrepared(MediaPlayer mp) {
						if(_playBackgroundAudio)//only start if background music is turned on
						{
							if(_mediaPlayer != null)
								_mediaPlayer.start();
						}
					}});
			}
		} catch (Exception e) {
			//do nothing
		}
	}
	
	public static void endBackgroundMusic()
	{
		if(_mediaPlayer != null)
		{
			_mediaPlayer.stop();
			_mediaPlayer.release();
			_mediaPlayer = null;
		}
	}

	// initallize all the pages of the story
	private ArrayList<BasePage> initPages() {
		ArrayList<BasePage> bp = new ArrayList<BasePage>();

		for (int i = 0; i < TOTALPAGECOUNT; i++) {
			switch (i) {
			case 3:
				if (isXLargeScreen())
					bp.add(new BasePage(this, i + 1, R.string.optionyes,
						R.drawable.flower2large));
				else
					bp.add(new BasePage(this, i + 1, R.string.optionyes,
						R.drawable.flower2));
				break;
			case 8:
				if (isXLargeScreen())
					bp.add(new BasePage(this, i + 1, R.string.optionbush,
						R.drawable.bushlarge));
				else
					bp.add(new BasePage(this, i + 1, R.string.optionbush,
						R.drawable.bush));
				break;
			case 10:
				if (isXLargeScreen())
					bp.add(new BasePage(this, i + 1, R.string.optionflutterbys,
						R.drawable.butterflylarge));
				else
					bp.add(new BasePage(this, i + 1, R.string.optionflutterbys,
						R.drawable.butterfly));
				break;
			case 13:
				if (isXLargeScreen())
					bp.add(new BasePage(this, i + 1, R.string.optionmarguerites,
							R.drawable.leaflarge));
				else
					bp.add(new BasePage(this, i + 1, R.string.optionmarguerites,
							R.drawable.leaf));
				break;
			case 15:
				if (isXLargeScreen())
					bp.add(new BasePage(this, i + 1, R.string.optionno,
							R.drawable.beelarge));
				else
					bp.add(new BasePage(this, i + 1, R.string.optionno,
						R.drawable.bee));
				break;
			case 17:
				if (isXLargeScreen())
					bp.add(new BasePage(this, i + 1, R.string.optionjoin,
						R.drawable.butterflylarge));
				else
					bp.add(new BasePage(this, i + 1, R.string.optionjoin,
						R.drawable.butterfly));
				break;
			case 19:
				if (isXLargeScreen())
					bp.add(new BasePage(this, i + 1, R.string.optionleave,
						R.drawable.flowerlarge));
				else
					bp.add(new BasePage(this, i + 1, R.string.optionleave,
						R.drawable.flower));
				break;
			default:
				bp.add(new BasePage(this, i + 1));
			}

		}

		// set special case data for end page
		BasePage endpage = (BasePage) bp.get(TOTALPAGECOUNT - 1);
		endpage.setHasNarrationText(false);
		endpage.setHasNarrationAudio(false);
		
		//set sound effects layers
		bp.get(0).addToSoundEffectsLayerMap(_soundEffectsLayerList.get(0), _soundEffectsLayerList.get(1), _soundEffectsLayerList.get(2));
		bp.get(1).addToSoundEffectsLayerMap(_soundEffectsLayerList.get(1), _soundEffectsLayerList.get(2), _soundEffectsLayerList.get(3));
		bp.get(2).addToSoundEffectsLayerMap(_soundEffectsLayerList.get(1), _soundEffectsLayerList.get(2), _soundEffectsLayerList.get(0), _soundEffectsLayerList.get(3));
		bp.get(3).addToSoundEffectsLayerMap(_soundEffectsLayerList.get(1), _soundEffectsLayerList.get(2), _soundEffectsLayerList.get(4), _soundEffectsLayerList.get(9));
		bp.get(4).addToSoundEffectsLayerMap(_soundEffectsLayerList.get(1), _soundEffectsLayerList.get(2), _soundEffectsLayerList.get(5), _soundEffectsLayerList.get(6));
		bp.get(5).addToSoundEffectsLayerMap(_soundEffectsLayerList.get(7));
		bp.get(6).addToSoundEffectsLayerMap(_soundEffectsLayerList.get(8), _soundEffectsLayerList.get(9), _soundEffectsLayerList.get(1), _soundEffectsLayerList.get(2));
		bp.get(7).addToSoundEffectsLayerMap(_soundEffectsLayerList.get(10), _soundEffectsLayerList.get(2));
		bp.get(8).addToSoundEffectsLayerMap(_soundEffectsLayerList.get(3), _soundEffectsLayerList.get(0), _soundEffectsLayerList.get(11), _soundEffectsLayerList.get(12), _soundEffectsLayerList.get(13));
		bp.get(9).addToSoundEffectsLayerMap(_soundEffectsLayerList.get(0), _soundEffectsLayerList.get(3));
		bp.get(10).addToSoundEffectsLayerMap(_soundEffectsLayerList.get(14), _soundEffectsLayerList.get(15), _soundEffectsLayerList.get(20));
		bp.get(11).addToSoundEffectsLayerMap(_soundEffectsLayerList.get(16), _soundEffectsLayerList.get(20), _soundEffectsLayerList.get(18));		
		bp.get(12).addToSoundEffectsLayerMap(_soundEffectsLayerList.get(0), _soundEffectsLayerList.get(3), _soundEffectsLayerList.get(20));
		bp.get(13).addToSoundEffectsLayerMap(_soundEffectsLayerList.get(19), _soundEffectsLayerList.get(20));
		bp.get(14).addToSoundEffectsLayerMap(_soundEffectsLayerList.get(23), _soundEffectsLayerList.get(24));		
		bp.get(15).addToSoundEffectsLayerMap(_soundEffectsLayerList.get(21), _soundEffectsLayerList.get(9), _soundEffectsLayerList.get(1), _soundEffectsLayerList.get(2), _soundEffectsLayerList.get(0));				
		bp.get(16).addToSoundEffectsLayerMap(_soundEffectsLayerList.get(1), _soundEffectsLayerList.get(2), _soundEffectsLayerList.get(17), _soundEffectsLayerList.get(9));
		bp.get(17).addToSoundEffectsLayerMap(_soundEffectsLayerList.get(1),_soundEffectsLayerList.get(2), _soundEffectsLayerList.get(21), _soundEffectsLayerList.get(0));
		bp.get(18).addToSoundEffectsLayerMap(_soundEffectsLayerList.get(1),_soundEffectsLayerList.get(17), _soundEffectsLayerList.get(18));
		bp.get(19).addToSoundEffectsLayerMap(_soundEffectsLayerList.get(1),_soundEffectsLayerList.get(2),_soundEffectsLayerList.get(22), _soundEffectsLayerList.get(9));
		bp.get(20).addToSoundEffectsLayerMap(_soundEffectsLayerList.get(0),_soundEffectsLayerList.get(1),_soundEffectsLayerList.get(2), _soundEffectsLayerList.get(7));
		bp.get(21).addToSoundEffectsLayerMap(_soundEffectsLayerList.get(1),_soundEffectsLayerList.get(2), _soundEffectsLayerList.get(7));
		bp.get(22).addToSoundEffectsLayerMap(_soundEffectsLayerList.get(1),_soundEffectsLayerList.get(2), _soundEffectsLayerList.get(3));
		
		//set special pages case background music
		for(int i = 5; i < 7; i++)//set background music for page 6 and 7
		{
			BasePage page = (BasePage)bp.get(i);
			page.set_backgroundMusicId(R.raw.scaryloop);
		}
		
		for(int i = 20; i < 22; i++)//set background music for page 21 and 22
		{
			BasePage page = (BasePage)bp.get(i);
			page.set_backgroundMusicId(R.raw.scaryloop);
		}

//haptic		
		//set special page case vibrate on page load
//		bp.get(5).set_pageLoadVibratePattern(new long[]{0,600,100,200,100,200,100,200,100});//set page 6's vibrate pattern
//		bp.get(20).set_pageLoadVibratePattern(new long[]{0,600,100,200,100,200,100,200,100});//set page 21's vibrate pattern
//		bp.get(21).set_pageLoadVibratePattern(new long[]{0,600,100,200,100,200,100,200,100});//set page 22's vibrate pattern
		bp.get(5).set_pageLoadVibratePattern(Launcher.WEAPON8);//set page 6's vibrate pattern
		bp.get(20).set_pageLoadVibratePattern(Launcher.WEAPON8);//set page 21's vibrate pattern
		bp.get(21).set_pageLoadVibratePattern(Launcher.WEAPON8);//set page 22's vibrate pattern


		
		
		//set special case karaoke highlite colors
//		bp.get(3).set_NarrationHighliteColor(R.color.roseKarokeTextColor);
//		bp.get(4).set_NarrationHighliteColor(R.color.roseKarokeTextColor);
		//bp.get(15).set_NarrationHighliteColor(R.color.redKarokeTextColor);
		//bp.get(16).set_NarrationHighliteColor(R.color.redKarokeTextColor);
//		bp.get(17).set_NarrationHighliteColor(R.color.redKarokeTextColor);
//		bp.get(18).set_NarrationHighliteColor(R.color.redKarokeTextColor);
		
			
		// now set all the back pages
		for (int i = 1; i < TOTALPAGECOUNT; i++) {
			BasePage page = (BasePage) bp.get(i);

			switch (i) {
			case 8:
			case 10:
			case 13:
				page.setBackPage((BasePage) bp.get(7));
				break;
			case 15:
			case 3:
				page.setBackPage((BasePage) bp.get(2));
				break;
			case 17:
			case 19:
				page.setBackPage((BasePage) bp.get(16));
				break;
			default:
				page.setBackPage((BasePage) bp.get(i - 1));
			}
		}

		// now set all the forward pages
		for (int i = 0; i < TOTALPAGECOUNT - 1; i++) {
			BasePage page = (BasePage) bp.get(i);

			switch (i) {
			case 2:
				page.setNextPage(new BasePage[] { (BasePage) bp.get(3),
						(BasePage) bp.get(15) });
				break;
			case 7:
				page.setNextPage(new BasePage[] { (BasePage) bp.get(8),
						(BasePage) bp.get(10), (BasePage) bp.get(13) });
				break;
			case 16:
				page.setNextPage(new BasePage[] { (BasePage) bp.get(17),
						(BasePage) bp.get(19) });
				break;
			case 18:
				page.setNextPage(new BasePage[] { (BasePage) bp.get(5) });
				break;
			case 9:
			case 12:
			case 14:
				page.setNextPage(new BasePage[] { (BasePage) bp.get(23) });
				break;
			default:
				page.setNextPage(new BasePage[] { (BasePage) bp.get(i + 1) });
			}
		}

		return bp;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
	
		if(_indexPage != null)
		{
			_indexPage._blockBackgroundChange = false;
		}
		
		showOptionsDialog(true);
		return true;
	}
	
	
//	@Override 
//	public void onStop()
//	{
//		//close the haptic device!!
//		if(dev != null)
//		{
//			try {
//				dev.close();
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		
//		super.onStop();
//	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		_currentBackgroundMusicId = -1;//hack, sometime the variable isnt cleaned up by the time we start a new instance of app
		
		_applicationSoundEffects.delete();
		_applicationSoundEffects = null;

		if (_mediaPlayer != null) {
			_mediaPlayer.stop();
			_mediaPlayer.release();
			_mediaPlayer = null;
			_bgMusicIsPaused = false;
		}
		
		System.gc();
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		
		if(_indexPage != null)
		{
			if(!_indexPage._blockBackgroundChange)
				_indexPage.makeBackgroundPlain();
		}
		
		//unregister screen on/off reciever
		unregisterReceiver(mScreenReceiver);
		
		pauseBackgroundMusic();
	}

	@Override
	public void onResume() {
		super.onResume();

		// override the transition animation for this activity
		FlutterbyAndMarguerite.this.overridePendingTransition(
				R.anim.curler_fade_in, R.anim.curler_fade_out);
		
		if(_indexPage != null)
		{
			if(!_indexPage._blockBackgroundChange)
			{
				_indexPage.makeBackgroundNormal();
				_indexPage._blockBackgroundChange = true;
			}
		}

		//register screen on/off reciever
		registerReceiver(mScreenReceiver, filter);
		
		if(_bgMusicIsPaused)
			unPauseBackgroundMusic();
		else 
			startBackgroundMusic(this, 0);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// Handle the back button
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			FlutterbyAndMarguerite.optionsListener.playRandomButtonSoundEffect();


			// Ask the user if they want to quit
			AlertDialog.Builder adb = new AlertDialog.Builder(this);
			
			if(isXLargeScreen())
				adb.setIcon(R.drawable.butterflyplainlarge);
			else
				adb.setIcon(R.drawable.butterflyplain);
					
			adb.setTitle("Quit Flutterby?")
					// .setMessage("Are you sure you want to close the book?")
					.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									FlutterbyAndMarguerite.this.finish();
								}
							}).setNegativeButton("Cancel", null).show();

			return true;
		}
		// else if(keyCode == KeyEvent.KEYCODE_MENU)
		// {
		// return super.onKeyDown(keyCode, event);

		// }
		else {
			return super.onKeyDown(keyCode, event);
		}
	}

	private void getPreferences() {
		if (_flutterbyPrefs != null) {
			// get characted alias prefs
			_flutterbyAlias = _flutterbyPrefs.getString(FLUTTERBY_ALIAS_PREF,
					"");
			_margueriteAlias = _flutterbyPrefs.getString(MARGUERITE_ALIAS_PREF,
					"");
			_flutterbyFemale = _flutterbyPrefs.getBoolean(
					FLUTTERBY_GENDER_F_PREF, true);
			_margueriteFemale = _flutterbyPrefs.getBoolean(
					MARGUERITE_GENDER_F_PREF, true);
			_flutterbyAliasEnabled = _flutterbyPrefs.getBoolean(
					FLUTTERBY_ALIAS_ENABLED_PREF, false);
			_margueriteAliasEnabled = _flutterbyPrefs.getBoolean(
					MARGUERITE_ALIAS_ENABLED_PREF, false);
		}
	}

	public final void showOptionsDialog(boolean fromHome) {
		Intent intent = new Intent(this, OptionMenu.class);// .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));

		// pass option states
		intent.putExtra("com.craftysoft.flutterbyandmarguerite.textNar",
				_displayTextNarration);
		intent.putExtra("com.craftysoft.flutterbyandmarguerite.audioNar",
				_playAudioNarration);
		intent.putExtra("com.craftysoft.flutterbyandmarguerite.backAudio",
				_playBackgroundAudio);
		intent.putExtra("com.craftysoft.flutterbyandmarguerite.textLarge",
				_textIsLarge);
		
		intent.putExtra("com.craftysoft.flutterbyandmarguerite.hasMic",
				_hasMicrophone);

		// pass character alias information in case the character alias menu is
		// accessed
		intent.putExtra("com.craftysoft.flutterbyandmarguerite.flutterbyAlias",
				_flutterbyAlias);
		intent.putExtra(
				"com.craftysoft.flutterbyandmarguerite.margueriteAlias",
				_margueriteAlias);
		intent.putExtra(
				"com.craftysoft.flutterbyandmarguerite.flutterbyFemale",
				_flutterbyFemale);
		intent.putExtra(
				"com.craftysoft.flutterbyandmarguerite.margueriteFemale",
				_margueriteFemale);
		intent.putExtra(
				"com.craftysoft.flutterbyandmarguerite.flutterbyAliasEnabled",
				_flutterbyAliasEnabled);
		intent.putExtra(
				"com.craftysoft.flutterbyandmarguerite.margueriteAliasEnabled",
				_margueriteAliasEnabled);

		intent.putExtra("com.craftysoft.flutterbyandmarguerite.fromHome",
				fromHome);

		startActivity(intent);
	}

	// USED FOR DEBUGGING PURPOSES ONLY TO RESET TEST PREFERENCES
	private final void resetPrefs() {
		if (_flutterbyPrefs != null) {
			Editor prefsEditor = _flutterbyPrefs.edit();
			prefsEditor.clear();
			prefsEditor.commit();
		}
	}

	// should only ever be called from basepages within the page curler
	public final void setBookmark(int pageNumber) {
		// set bookmark
		if (_flutterbyPrefs != null) {
			Editor prefsEditor = _flutterbyPrefs.edit();
			prefsEditor.putInt(BOOKMARK_PREF, pageNumber);
			prefsEditor.commit();

			Toast.makeText(this, "Bookmark Saved", Toast.LENGTH_LONG).show();
		} else
			Toast.makeText(this, "Bookmark Not Saved", Toast.LENGTH_LONG)
					.show();
	}

	// return the page number of the page that is bookmarked
	private int getBookmark() {
		int pageNumber = -1;
		if (_flutterbyPrefs != null) {
			pageNumber = _flutterbyPrefs.getInt(BOOKMARK_PREF, -1);
		}

		return pageNumber;
	}

	public void readBook(int pageIndex) {
		Intent intent = new Intent(this, CurlActivity.class);
		intent.putExtra("com.craftysoft.flutterbyandmarguerite.pageIndex",
				pageIndex);

		startActivity(intent);
	}

	public void playRandomButtonSoundEffect() {
		
		//add click vibe
		vibratePhoneId(Launcher.STRONG_CLICK_66);//yes this is lazy should make it own call!!
		
		_applicationSoundEffects.playRandomButtonSoundEffect();
	}
	
	public void playQuickChime() {
		_applicationSoundEffects.playQuickChimeSound();
	}
	
	public void playSound(int x, int y) {
		
		int color = 0;
		if (bmpSounds != null)
			color = bmpSounds.getPixel(x, y);

		if(color != 0)
		{
			String colorCode = Integer.toHexString(color);
		
			if(colorCode.equals(_flutterbySoundEffectColorCode))
				_applicationSoundEffects.playTitleSound();
		}
	}

	public void playRandomNavigationSoundEffect() {
		_applicationSoundEffects.playRandomNavigationSoundEffect();
	}

	// sets all the necessary listeners, should be called from onCreate
	private void setListeners() {
		// startButton.setOnClickListener(this.startRecordingButtonListener);
		// stopButton.setOnClickListener(this.stopRecordingButtonListener);
		// deleteButton.setOnClickListener(this.deleteRecordingButtonListener);
	}

	@Override
	public void toggleAudioNarration() {
		_playAudioNarration = !_playAudioNarration;
	}

	@Override
	public void toggleBackgroundSound() {
		_playBackgroundAudio = !_playBackgroundAudio;

		try {
			if (_playBackgroundAudio) {
				if (_mediaPlayer != null)
					_mediaPlayer.start();
			} else {
				if (_mediaPlayer != null) {
					if (_mediaPlayer.isPlaying())
						_mediaPlayer.pause();
				}
			}
		} catch (Exception ex) {
			// do nothing
		}
	}

	@Override
	public void toggleTextNarration() {
		_displayTextNarration = !_displayTextNarration;
	}

	public void toggleTextSize() {
		_textIsLarge = !_textIsLarge;
	}

	@Override
	public void goToBookmarkPage() {
		int pageNumber = getBookmark();

		if (pageNumber == -1)
			Toast.makeText(this, "No Bookmark Found", Toast.LENGTH_SHORT)
					.show();
		else
			readBook(pageNumber);
	}

	@Override
	public void turnBackgroundAudioOff() {
		if (_playBackgroundAudio) {
			if (_mediaPlayer != null) {
				try {
					if (_mediaPlayer.isPlaying())
						_mediaPlayer.pause();
				} catch (Exception ex) {
					// do nothing
				}
			}
		}
	}

	@Override
	public void turnBackgroundAudioOn() {
		if (_playBackgroundAudio) {
			if (_mediaPlayer != null) {
				try {
					_mediaPlayer.start();
				} catch (Exception ex) {
					// do nothing
				}
			}
		}
	}

	// opens page index activity
	@Override
	public void goPageIndex() {
		this.startActivityForResult(new Intent(this, PageIndex.class)
				.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP), PAGE_INDEX_CODE);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case PAGE_INDEX_CODE:// the page index activity has returned
			if (resultCode == RESULT_OK) {
				if (data != null) {
					readBook(data.getIntExtra("pageIndex", 1));
				}
			}
			break;
		}
	}

	@Override
	public void saveCharacterAliasInfo(String fAlias, boolean fGirl,
			boolean fEnabled, String mAlias, boolean mGirl, boolean mEnabled) {
		if (_flutterbyPrefs != null) {
			_flutterbyAlias = fAlias;
			_margueriteAlias = mAlias;
			_flutterbyFemale = fGirl;
			_margueriteFemale = mGirl;
			_flutterbyAliasEnabled = fEnabled;
			_margueriteAliasEnabled = mEnabled;

			Editor prefsEditor = _flutterbyPrefs.edit();
			prefsEditor.putString(FLUTTERBY_ALIAS_PREF, _flutterbyAlias);
			prefsEditor.putString(MARGUERITE_ALIAS_PREF, _margueriteAlias);
			prefsEditor.putBoolean(FLUTTERBY_GENDER_F_PREF, _flutterbyFemale);
			prefsEditor.putBoolean(MARGUERITE_GENDER_F_PREF, _margueriteFemale);
			prefsEditor.putBoolean(FLUTTERBY_ALIAS_ENABLED_PREF,
					_flutterbyAliasEnabled);
			prefsEditor.putBoolean(MARGUERITE_ALIAS_ENABLED_PREF,
					_margueriteAliasEnabled);
			prefsEditor.commit();
		}
	}
}