package com.craftysoft.flutterbyandmarguerite;

import java.io.File;
import java.io.InputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.craftysoft.flutterbyandmarguerite.Page.BasePage;
import com.craftysoft.flutterbyandmarguerite.pro.R;
import com.immersion.uhl.Launcher;

public class CurlActivity extends Activity implements NarrationMediaPlayerListener {

	// handles the narration audio
	private NarrationMediaPlayer _narrationMedia = null;
	
	public static NarrationMediaPlayerListener narrationMediaPlayerListener = null;
	
	private boolean _controlsAreShowing = false;

	private TableLayout _navigationTableLayout = null;
	private boolean _storyOptionPathsDisplayed = false;

	private boolean _autoRedirect = false;// used to redirect from the end page,
											// set to false if we get to end
											// page and navigate away manually
	private boolean _manualRedirect = false;

	private int _width, _height;

	private CurlView mCurlView;

	private boolean _soundEffectLayerInitialized = false;
	private Bitmap bmpSounds = null;

	private FrameLayout _rootLayout = null;
	private Button _settingsButton = null;
	private Button _bookmarkButton = null;
	private TextView _narrationTextView = null;
	private MyScrollView _narrationScrollView = null;
	private RelativeLayout _narrationView = null;// holds the narration text
													// views, use this to
													// control narration
													// visibility with one
													// visibility call
	private int _pageIndex = 1;

	private long _narrationDelay = 1700;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		narrationMediaPlayerListener = (NarrationMediaPlayerListener)this;

		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay();
		_width = display.getWidth();
		_height = display.getHeight();

		_narrationDelay = 1700;// set delay that will work with the fade
								// animations

		// override the transition animation for this activity
		CurlActivity.this.overridePendingTransition(R.anim.curler_fade_in,
				R.anim.curler_fade_out);

		_rootLayout = (FrameLayout) findViewById(R.id.FrameRoot);

		// COMMENTED OUT FOR BUG FIX
		// _narrationView =
		// (RelativeLayout)findViewById(R.id.RelativeLayoutTextNarration);
		// _narrationTextView = (TextView)findViewById(R.id.TextViewNarriation);

		// setFontSize(_narrationTextView);

		// _narrationScrollView =
		// (MyScrollView)findViewById(R.id.ScrollViewText);
		// _narrationScrollView.setScrollViewListener(myScrollViewListener);

		_settingsButton = (Button) findViewById(R.id.ButtonInfo);
		_settingsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				if (!mCurlView.isbBlockingTouchInput()) {
					FlutterbyAndMarguerite.optionsListener
							.playRandomButtonSoundEffect();
					FlutterbyAndMarguerite.optionsListener
							.showOptionsDialog(false);
				}
			}
		});

		_bookmarkButton = (Button) findViewById(R.id.ButtonBookmark);
		_bookmarkButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				if (!mCurlView.isbBlockingTouchInput()) {
					// make sure they want to bookmark
					AlertDialog.Builder adb = new AlertDialog.Builder(
							CurlActivity.this);
					
					//haptic
					FlutterbyAndMarguerite.optionsListener.vibratePhoneId(Launcher.STRONG_CLICK_66);

					if (isXLargeScreen())
						adb.setIcon(R.drawable.butterflyplainlarge);
					else
						adb.setIcon(R.drawable.butterflyplain);

					adb.setTitle("Bookmark this page?").setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {

									FlutterbyAndMarguerite.optionsListener
											.stopVibrate();// stop vibrate if
															// page is still
															// vibrating

									FlutterbyAndMarguerite
											.startBackgroundMusic(
													CurlActivity.this, 0);

									FlutterbyAndMarguerite.optionsListener
											.playRandomNavigationSoundEffect();
									FlutterbyAndMarguerite.optionsListener
											.setBookmark(mCurlView
													.getCurrentIndex());

									Intent intent = new Intent(
											CurlActivity.this,
											FlutterbyAndMarguerite.class)
											.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);// .FLAG_ACTIVITY_CLEAR_TOP);
									CurlActivity.this.startActivity(intent);

									CurlActivity.this.finish();
								}
							}).setNegativeButton("Cancel", null).show();
				}
			}
		});

		_pageIndex = getIntent().getIntExtra(
				"com.craftysoft.flutterbyandmarguerite.pageIndex", 1);

		FlutterbyAndMarguerite.startBackgroundMusic(CurlActivity.this,
				FlutterbyAndMarguerite.PAGES.get(_pageIndex - 1)
						.get_backgroundMusicId());

		// if (getLastNonConfigurationInstance() != null) {
		// index = (Integer) getLastNonConfigurationInstance();
		// }
		mCurlView = (CurlView) findViewById(R.id.curl);

		mCurlView.setBitmapProvider(new BitmapProvider());
		mCurlView.setSizeChangedObserver(new SizeChangedObserver());
		mCurlView.setCurrentIndex(_pageIndex);
		mCurlView.setBackgroundColor(0xFF202830);

		// get singlton NarrationMediaPlayer for playing the audio narration
		_narrationMedia = NarrationMediaPlayer.getInstance(this);

		// craftyhack - set a listener for signaling when a page turns
		mCurlView.setPageTurnListener(pageturnListener);

		// This is something somewhat experimental. Before uncommenting next
		// line, please see method comments in CurlView.
		// mCurlView.setEnableTouchPressure(true);
	}

	private boolean isXLargeScreen() {
		return ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE);
	}

	@Override
	public void onPause() {
		super.onPause();
		mCurlView.onPause();

		FlutterbyAndMarguerite.pauseBackgroundMusic();

		_autoRedirect = false;// we are manually doing something so cancel the
								// last page's autoredirect just in case
		_manualRedirect = true;// set manual redirect in case we are on the last
								// page since now we are interacting with the
								// page

		_narrationDelay = 500;

		if (_narrationMedia != null)
			_narrationMedia.set_narrationComplete(false);

		showUIControls(false);

		_narrationMedia.setPlaySoundEffects(true);// always enable sound effects
													// to play in case user
													// turns off narration audio
	}

	@Override
	public void onResume() {
		super.onResume();
		mCurlView.onResume();

		showUIControls(true);

		// load sound effects
		if (!_soundEffectLayerInitialized) {
			initializeSoundEffectLayer();
			_soundEffectLayerInitialized = true;
		}

		FlutterbyAndMarguerite.unPauseBackgroundMusic();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// Handle the back button
		if ((keyCode == KeyEvent.KEYCODE_BACK)
				|| (keyCode == KeyEvent.KEYCODE_MENU)) {
			// HACK: make sure we arent flipping the page curler so we cant get
			// out of sync while animating
			if (mCurlView != null) {
				if (mCurlView.isbBlockingTouchInput())
					return true;
			}

			//if (keyCode == KeyEvent.KEYCODE_BACK)
				FlutterbyAndMarguerite.optionsListener
						.playRandomNavigationSoundEffect();

			return super.onKeyDown(keyCode, event);
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
			
		FlutterbyAndMarguerite.optionsListener.showOptionsDialog(true);
		return true;
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		return mCurlView.getCurrentIndex();
	}

	/*
	 * private MyScrollViewListener myScrollViewListener = new
	 * MyScrollViewListener() {
	 * 
	 * @Override public void ScrollToBottom(int scrollViewHeight, int scrollY) {
	 * int textViewBottom = _narrationTextView.getBottom();
	 * 
	 * boolean atBottom = (textViewBottom - (scrollViewHeight + scrollY)) == 0;
	 * 
	 * // if(atBottom) // { // if((_currentPage.getPageForwardCount() > 1)) //
	 * turnPage(_currentPage.getForwardPages(), false); // } // else // { //
	 * //TODO: maybe add this later if looks better... // //hide navigation //
	 * //Page.this.hideNavigation(); // } } };
	 */
	public PageTurnListener pageturnListener = new PageTurnListener() {
		@Override
		public void PageTurned(int pageIndex) {

			if (pageIndex != _pageIndex)// page was turned
			{
				//COMMENTED OUT HAPTIC (MAY NEED FURTHER TESTING)
				//FlutterbyAndMarguerite.optionsListener.stopVibrate();

				_pageIndex = pageIndex;// set new page number

				// since the page was just turned
				// check if we are on the last page and redirect us to the start
				// after a few seconds
				if (_pageIndex == FlutterbyAndMarguerite.TOTALPAGECOUNT) {
					_autoRedirect = true;
					_manualRedirect = false;

					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {
							// check if we still want to autoredirect
							if (_autoRedirect) {
								FlutterbyAndMarguerite.optionsListener
										.playRandomNavigationSoundEffect();

								Intent intent = new Intent(CurlActivity.this,
										FlutterbyAndMarguerite.class)
										.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);// .FLAG_ACTIVITY_CLEAR_TOP);
								CurlActivity.this.startActivity(intent);

								CurlActivity.this.finish();
							}
						}
					}, 5000);// redirect after 5 seconds
				} else {
					_autoRedirect = false;// just in case we turn from the last
											// page we want to cancel the
											// autoredirect
				}

				initializeSoundEffectLayer();// we are at a new page so load new
												// sounds

				// check is page has new background audio to play for us
				FlutterbyAndMarguerite.startBackgroundMusic(CurlActivity.this,
						FlutterbyAndMarguerite.PAGES.get(_pageIndex - 1)
								.get_backgroundMusicId());

//				// vibrate page on page turn if page contains a vibrate pattern
//				if (FlutterbyAndMarguerite.PAGES.get(_pageIndex - 1)
//						.get_pageLoadVibratePattern() != null)
//					FlutterbyAndMarguerite.optionsListener
//							.vibratePhonePattern(FlutterbyAndMarguerite.PAGES
//									.get(_pageIndex - 1)
//									.get_pageLoadVibratePattern());
				// vibrate page on page turn if page contains a vibrate pattern
				if (FlutterbyAndMarguerite.PAGES.get(_pageIndex - 1).get_pageLoadVibratePattern() != -1)
					FlutterbyAndMarguerite.optionsListener
							.vibratePhoneId(FlutterbyAndMarguerite.PAGES
									.get(_pageIndex - 1)
									.get_pageLoadVibratePattern());

				_storyOptionPathsDisplayed = false;

				// page turned so reset the flag that prevent narration from
				// replaying
				if (_narrationMedia != null)
					_narrationMedia.set_narrationComplete(false);
			} else {
				// since page wasn't turned make sure to cancel the auto
				// redirect and instead turn on the manual redirect
				if (_autoRedirect == true)// check if we are in auto redirect
											// mode
				{
					_autoRedirect = false;
					_manualRedirect = true;
				}
			}

			_narrationDelay = 500;// reset narration delay bc now we are inside
									// the page curl view

			showUIControls(true);

		}

		@Override
		public void playSoundEffect(int colorCode) {
			playEffect(colorCode);
		}

		@Override
		public void popupBitmapColorCodeLastPage() {
			// special case method that pops up a special alert dialog based on
			// the colorcode that we clicked on
			// JUST SPECIFIC TO THE LAST PAGE FOR NOW
			if (_manualRedirect) {
				// Ask the user if they want to quit
				AlertDialog.Builder adb = new AlertDialog.Builder(
						CurlActivity.this);

				if (isXLargeScreen())
					adb.setIcon(R.drawable.butterflyplainlarge);
				else
					adb.setIcon(R.drawable.butterflyplain);

				adb.setTitle("Start from Beginning?").setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								FlutterbyAndMarguerite.optionsListener
										.playRandomNavigationSoundEffect();

								Intent intent = new Intent(CurlActivity.this,
										FlutterbyAndMarguerite.class)
										.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);// .FLAG_ACTIVITY_CLEAR_TOP);
								CurlActivity.this.startActivity(intent);

								CurlActivity.this.finish();
							}
						}).setNegativeButton("Cancel", null).show();
			}
		}

		@Override
		public void displayUIControls(boolean showControls) {
			showUIControls(showControls);
		}

		@Override
		public int getBitmapColorCode(int x, int y) {
			return getBitmapColor(x, y);
		}

		@Override
		public void displayNarrationOptions(BasePage page) {
			createNavigationButtons(page);
			resizeTextViews(_rootLayout);
		}
	};

	// recursive function to resize all child textviews in view
	private void resizeTextViews(ViewGroup viewGroup) {
		int count = viewGroup.getChildCount();

		for (int i = 0; i < count; i++) {
			View childView = viewGroup.getChildAt(i);

			if (childView instanceof ViewGroup)
				resizeTextViews((ViewGroup) childView);
			else if ((childView instanceof TextView) && !(childView instanceof Button))
				setFontSize((TextView) childView);
//			else if ((childView instanceof TextView) && (childView.isShown())
//					&& !(childView instanceof Button))
//				setFontSize((TextView) childView);
		}
	}

	private void handleNavButtonClick(final BasePage currentPage,
			final BasePage forwardPage) {
		showUIControls(false);
		
		//haptic
		FlutterbyAndMarguerite.optionsListener.vibratePhoneId(Launcher.STRONG_CLICK_66);	

		FlutterbyAndMarguerite.optionsListener
				.playRandomNavigationSoundEffect();

		forwardPage.setBackPage(currentPage);
		mCurlView.performAutoCurl(forwardPage.getPageNumber());
	}

	// creates the optional navigation buttons
	private void createNavigationButtons(final BasePage page) {
		_settingsButton.setVisibility(View.VISIBLE);
		_bookmarkButton.setVisibility(View.VISIBLE);

		FrameLayout rootView = (FrameLayout) findViewById(R.id.FrameRoot);

		if (_navigationTableLayout == null) {
			LayoutInflater.from(this).inflate(R.layout.narrationoptions,
					rootView, true);
			_navigationTableLayout = (TableLayout) rootView
					.findViewById(R.id.TableLayoutNavigation);
		}

		if (_navigationTableLayout != null) {
			_storyOptionPathsDisplayed = true;

			_navigationTableLayout.setVisibility(View.VISIBLE);

			ImageButton buttonLeft = (ImageButton) findViewById(R.id.ButtonOptionLeft);
			ImageButton buttonCenter = (ImageButton) findViewById(R.id.ButtonOptionCenter);
			ImageButton buttonRight = (ImageButton) findViewById(R.id.ButtonOptionRight);
			TextView textviewLeft = (TextView) findViewById(R.id.TextViewLeftDescription);
			TextView textviewCenter = (TextView) findViewById(R.id.TextViewCenterDescription);
			TextView textviewRight = (TextView) findViewById(R.id.TextViewRightDescription);

			ScaleAnimation sa = new ScaleAnimation(0.5f, 1f, 0.5f, 1f,
					Animation.RELATIVE_TO_SELF, 0.5f,
					Animation.RELATIVE_TO_SELF, 0.5f);
			sa.setDuration(1000);

			buttonLeft.setVisibility(View.INVISIBLE);
			buttonCenter.setVisibility(View.INVISIBLE);
			buttonRight.setVisibility(View.INVISIBLE);
			textviewLeft.setVisibility(View.INVISIBLE);
			textviewCenter.setVisibility(View.INVISIBLE);
			textviewRight.setVisibility(View.INVISIBLE);

			if (page.getNextPageCount() == 2) {

				buttonLeft.setImageDrawable(this.getResources().getDrawable(
						page.getNextPage()[0].get_navigationIconId()));
				buttonLeft.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						handleNavButtonClick(page, page.getNextPage()[0]);
					}
				});
				textviewLeft.setText(FlutterbyAndMarguerite
						.replaceCharacterDetails(this.getResources().getString(
								page.getNextPage()[0].get_navigationTextId())));
				buttonLeft.startAnimation(sa);

				buttonLeft.setVisibility(View.VISIBLE);
				buttonRight.setVisibility(View.VISIBLE);
				textviewLeft.setVisibility(View.VISIBLE);
				textviewRight.setVisibility(View.VISIBLE);

				buttonRight.setImageDrawable(this.getResources().getDrawable(
						page.getNextPage()[1].get_navigationIconId()));
				buttonRight.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						handleNavButtonClick(page, page.getNextPage()[1]);
					}
				});
				textviewRight.setText(FlutterbyAndMarguerite
						.replaceCharacterDetails(this.getResources().getString(
								page.getNextPage()[1].get_navigationTextId())));

				// AnimationSet as = new AnimationSet(true);
				// as.setFillEnabled(true);
				// as.setInterpolator(new BounceInterpolator());
				//				
				// TranslateAnimation ta = new
				// TranslateAnimation(Animation.ABSOLUTE, 0, Animation.ABSOLUTE,
				// 15,
				// Animation.ABSOLUTE, 0, Animation.ABSOLUTE, 15);
				// ta.setDuration(2000);
				// as.addAnimation(ta);
				//				
				// TranslateAnimation ta2 = new
				// TranslateAnimation(Animation.ABSOLUTE, 15,
				// Animation.ABSOLUTE, 0,
				// Animation.ABSOLUTE, 15, Animation.ABSOLUTE, 0);
				//
				// ta2.setDuration(2000);
				// ta2.setStartOffset(2000);
				//				
				// as.addAnimation(ta2);

				buttonRight.startAnimation(sa);// AnimationUtils.loadAnimation(this,
												// R.anim.shake));//sa);
			} else// must be 3 the MAX for this app
			{
				buttonLeft.setVisibility(View.VISIBLE);
				buttonCenter.setVisibility(View.VISIBLE);
				buttonRight.setVisibility(View.VISIBLE);
				textviewLeft.setVisibility(View.VISIBLE);
				textviewCenter.setVisibility(View.VISIBLE);
				textviewRight.setVisibility(View.VISIBLE);

				buttonLeft.setImageDrawable(this.getResources().getDrawable(
						page.getNextPage()[0].get_navigationIconId()));
				buttonLeft.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						handleNavButtonClick(page, page.getNextPage()[0]);
					}
				});
				textviewLeft.setText(FlutterbyAndMarguerite
						.replaceCharacterDetails(this.getResources().getString(
								page.getNextPage()[0].get_navigationTextId())));
				buttonLeft.startAnimation(sa);

				buttonCenter.setImageDrawable(this.getResources().getDrawable(
						page.getNextPage()[1].get_navigationIconId()));
				buttonCenter.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						handleNavButtonClick(page, page.getNextPage()[1]);
					}
				});
				textviewCenter.setText(FlutterbyAndMarguerite
						.replaceCharacterDetails(this.getResources().getString(
								page.getNextPage()[1].get_navigationTextId())));
				buttonCenter.startAnimation(sa);

				buttonRight.setImageDrawable(this.getResources().getDrawable(
						page.getNextPage()[2].get_navigationIconId()));
				buttonRight.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						handleNavButtonClick(page, page.getNextPage()[2]);
					}
				});
				textviewRight.setText(FlutterbyAndMarguerite
						.replaceCharacterDetails(this.getResources().getString(
								page.getNextPage()[2].get_navigationTextId())));
				buttonRight.startAnimation(sa);
			}
		}
	}

	private void hideNarrationOptions() {
		if ((_storyOptionPathsDisplayed) && (_navigationTableLayout != null)) {
			_navigationTableLayout.setVisibility(View.GONE);// .INVISIBLE);
		}

		_navigationTableLayout = null;
	}

	private void hideNarrationBox() {
		if (_narrationView != null) {
			
			_narrationTextView.setText("");
			
			_narrationView.setVisibility(View.GONE);// INVISIBLE);
		}

		_narrationView = null;
	}

	private boolean displayNarrationBox() {
		// add for textview display bug
		FrameLayout rootView = (FrameLayout) findViewById(R.id.FrameRoot);

		if (_narrationView == null) {
			LayoutInflater.from(this).inflate(R.layout.narriationbox, rootView,
					true);
			_narrationView = (RelativeLayout) findViewById(R.id.RelativeLayoutTextNarration);
			_narrationTextView = (TextView) findViewById(R.id.TextViewNarriation);
			_narrationScrollView = (MyScrollView) findViewById(R.id.ScrollViewText);
		}
		// end of bug fix

		if (_narrationView != null) {
			if (FlutterbyAndMarguerite._displayTextNarration) {
				_narrationView.setVisibility(View.VISIBLE);

				_narrationScrollView.scrollTo(0, 0);// make sure we reset the
													// scroll to the top
				return true;
			} else {
				hideNarrationBox();
				return false;
			}
		}
		return false;
	}

	private void setNarrationBoxText(BasePage page) {
		if (page.hasNarrationText())
			_narrationTextView.setText(FlutterbyAndMarguerite
					.replaceCharacterDetails(page.getPageTextNarration()));
		else
			hideNarrationBox();
	}

	private int getBitmapColor(int x, int y) {
		if (bmpSounds != null)
			return bmpSounds.getPixel(x, y);
		else
			return 0;
	}

	private SoundPoolManager _soundPoolMgr = null;

	private void playEffect(int colorCode) {
		if (_narrationMedia.canPlaySoundEffects()) {
			// try and get the associated sound effect if available
			SoundEffect se = FlutterbyAndMarguerite.PAGES.get(_pageIndex - 1)
					.getSoundEffect(Integer.toHexString(colorCode));

			if (se != null) {
				FlutterbyAndMarguerite.optionsListener.stopVibrate();// just in case

				_soundPoolMgr.playSound(se.get_soundEffectId());

//				long[] vibratePattern = se.get_vibratePattern();
//
//				if (vibratePattern != null)
//					FlutterbyAndMarguerite.optionsListener
//							.vibratePhonePattern(vibratePattern);
				
				FlutterbyAndMarguerite.optionsListener.vibratePhone(se);
			}
		}
	}

	private void loadSoundEffects(BasePage page) {
		if (_soundPoolMgr == null)
			_soundPoolMgr = new SoundPoolManager();
		else {
			_soundPoolMgr.delete();
			_soundPoolMgr = null;
			_soundPoolMgr = new SoundPoolManager();
		}

		for (SoundEffect se : page.getSoundEffects())
			_soundPoolMgr.loadSound(se.get_soundEffectId(), this);
	}

	private void initializeSoundEffectLayer() {
		AssetManager assetManager = getAssets();

		InputStream imageIn = null;

		try {
			imageIn = assetManager.open(FlutterbyAndMarguerite.PAGES.get(
					_pageIndex - 1).getSoundEffectImageName(),
					AssetManager.ACCESS_BUFFER);

			if (bmpSounds != null)
				bmpSounds.recycle();// make sure to recycle anything that
									// already may be in there

			bmpSounds = BitmapFactory.decodeStream(imageIn);
			bmpSounds = Bitmap.createScaledBitmap(bmpSounds, _width, _height,
					true);

			loadSoundEffects(FlutterbyAndMarguerite.PAGES.get(_pageIndex - 1));
		} catch (Exception e) {
			// do nothing
		}
	}

	private void playNarration() {
		// play the narration audio
		if (FlutterbyAndMarguerite._playAudioNarration) 
		{
			// dont replay narration if its already played
			if (!_narrationMedia.is_narrationComplete()) 
			{
				// get narration profile in use
				String narrationProfile = FlutterbyAndMarguerite._selectedNarrationProfile;

				if (narrationProfile.equals("")) {
					_narrationMedia.playNarrationAudio(
							FlutterbyAndMarguerite.PAGES.get(_pageIndex - 1)
									.getPageAudioNarration(), _narrationDelay);
				} else {
					if ((Environment.MEDIA_MOUNTED.equals(Environment
							.getExternalStorageState()))
							|| (Environment.MEDIA_MOUNTED_READ_ONLY
									.equals(Environment
											.getExternalStorageState()))) {
						File narrationFile = AudioNarrationManager
								.getNarrationAudioFile(this, narrationProfile,
										FlutterbyAndMarguerite.PAGES.get(
												_pageIndex - 1).getPageNumber());
						if (narrationFile != null) {
							String fileName = narrationFile.getPath();// + "/" +
																		// narrationFile.getName();
							_narrationMedia.playNarrationAudio(fileName,
									_narrationDelay);
						} else
							_narrationMedia.playNarrationAudio(
									FlutterbyAndMarguerite.PAGES.get(
											_pageIndex - 1)
											.getPageAudioNarration(),
									_narrationDelay);
					} else
						_narrationMedia.playNarrationAudio(
								FlutterbyAndMarguerite.PAGES
										.get(_pageIndex - 1)
										.getPageAudioNarration(),
								_narrationDelay);
				}
			}
		}
	}
	
	// show/hide controls in layout so they do not show through when other menus
	// overlap, add more control ass needed
	public final void showUIControls(boolean showControls) {
		if ((showControls)) {
						
			if (!_controlsAreShowing)// dont show controls if they are already
										// showing
			{
				_controlsAreShowing = true;

				_settingsButton.setVisibility(View.VISIBLE);

				if (_pageIndex < FlutterbyAndMarguerite.TOTALPAGECOUNT)
					_bookmarkButton.setVisibility(View.VISIBLE);

				if ((_storyOptionPathsDisplayed)
						&& (_navigationTableLayout != null))
					createNavigationButtons(FlutterbyAndMarguerite.PAGES
							.get(_pageIndex - 1));// _navigationTableLayout.setVisibility(View.VISIBLE);
				else {
					if (displayNarrationBox())
						setNarrationBoxText(FlutterbyAndMarguerite.PAGES
								.get(_pageIndex - 1));// set the narration text

					if (FlutterbyAndMarguerite.PAGES.get(_pageIndex - 1)
							.hasNarrationAudio())
						playNarration();
				}

				resizeTextViews(_rootLayout);
			}
		} else {
			_controlsAreShowing = false;

			_settingsButton.setVisibility(View.INVISIBLE);
			_bookmarkButton.setVisibility(View.INVISIBLE);

			hideNarrationOptions();
			
			//cancel karaoke
			if(narrationTimer != null)
			{
				textIndex = 1;//reset the span

				narrationTimer.cancel();
				narrationTimer = null;
			}


			if (FlutterbyAndMarguerite.PAGES.get(_pageIndex - 1)
					.hasNarrationText()) {
				hideNarrationBox();

				// stop playing narration if playing
				if (FlutterbyAndMarguerite._playAudioNarration)
					_narrationMedia.stopPlayingNarrationAudio();

				// stop playing any sound effects that may be playing
				if (_soundPoolMgr != null)
					_soundPoolMgr.autoPause();

				// stop any vibrate that might be happening
				FlutterbyAndMarguerite.optionsListener.stopVibrate();
			}
		}
	}

	// convert pixels to density pixels
	private float convertPxToDp(float px) {
		return (px * FlutterbyAndMarguerite._densityScale) + 0.5f;
	}

	// convert density pixels to pixels
	private float convertDpToPx(float dp) {
		return (float) ((dp - 0.5f) / FlutterbyAndMarguerite._densityScale);
	}

	// if not large size then use default as defined in layout xml style
	// definition
	private void setFontSize(TextView tv) {
		// always set the default first
		tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,
				FlutterbyAndMarguerite._defaultFontSize);

		if (FlutterbyAndMarguerite._textIsLarge) {
			float textSizeInPixel = convertDpToPx(tv.getTextSize());
			tv
					.setTextSize(textSizeInPixel
							+ (textSizeInPixel * FlutterbyAndMarguerite._defaultFontAdjustPercentage));
		}
	}

	// /*
	// * Flutterby and Marguerite alias logic
	// */
	// private String replaceCharacterDetails(String text)
	// {
	// return replaceMargueriteDetail(replaceFlutterbyDetail(text));
	// }
	//
	// //all Flutterby references are surrounded with <f></f>
	// private String replaceFlutterbyDetail(String text)
	// {
	// String newText = text.replaceAll("<f>Flutterby</f>",
	// get_flutterbyAlias());
	//	
	// return replaceProNouns(newText, is_flutterbyFemale(), "f",
	// FlutterbyAndMarguerite._flutterbyAliasEnabled);
	// }
	//	
	// //all Marguerite references are surrounded with <m></m>
	// private String replaceMargueriteDetail(String text)
	// {
	// String newText = text.replaceAll("<m>Marguerite</m>",
	// get_margueriteAlias());
	//		
	// return replaceProNouns(newText, is_margueriteFemale(), "m",
	// FlutterbyAndMarguerite._margueriteAliasEnabled);
	// }
	//
	// private String replaceProNouns(String text, boolean genderFemale, String
	// ch, boolean aliasEnabled)
	// {
	// if((genderFemale) || (!aliasEnabled))
	// {
	// return text.replaceAll("<" + ch + ">her</" + ch + ">",
	// "her").replaceAll("<" + ch + ">Her</" + ch + ">", "Her").
	// replaceAll("<" + ch + ">she</" + ch + ">", "she").replaceAll("<" + ch +
	// ">She</" + ch + ">", "She").
	// replaceAll("<" + ch + "_>her</" + ch + "_>", "her").replaceAll("<" + ch +
	// "_>Her</" + ch + "_>", "Her");
	// }
	// else
	// {
	// return text.replaceAll("<" + ch + ">her</" + ch + ">",
	// "him").replaceAll("<" + ch + ">Her</" + ch + ">", "Him").
	// replaceAll("<" + ch + ">she</" + ch + ">", "he").replaceAll("<" + ch +
	// ">She</" + ch + ">", "He").
	// replaceAll("<" + ch + "_>her</" + ch + "_>", "his").replaceAll("<" + ch +
	// "_>Her</" + ch + "_>", "His");
	// }
	// }
	//	
	// private String get_flutterbyAlias() {
	// String aliasName = FlutterbyAndMarguerite.FLUTTERBY;
	//		
	// if(FlutterbyAndMarguerite._flutterbyAliasEnabled)//only replace text if
	// the alias is turned on
	// {
	// if(!FlutterbyAndMarguerite._flutterbyAlias.equals(""))
	// aliasName = FlutterbyAndMarguerite._flutterbyAlias;
	// }
	// return aliasName;
	// }
	//
	// private String get_margueriteAlias() {
	// String aliasName = FlutterbyAndMarguerite.MARGUERITE;
	//		
	// if(FlutterbyAndMarguerite._margueriteAliasEnabled)//only replace text if
	// the alias is turned on
	// {
	// if(!FlutterbyAndMarguerite._margueriteAlias.equals(""))
	// aliasName = FlutterbyAndMarguerite._margueriteAlias;
	// }
	// return aliasName;
	// }
	//
	// private boolean is_flutterbyFemale() {
	// return FlutterbyAndMarguerite._flutterbyFemale;
	// }
	//
	// private boolean is_margueriteFemale() {
	// return FlutterbyAndMarguerite._margueriteFemale;
	// }

	/**
	 * Bitmap provider.
	 */
	public class BitmapProvider implements CurlView.BitmapProvider {

		@Override
		public Bitmap getBitmap(int width, int height, int index,
				boolean leftFlag) {
			Bitmap b = Bitmap.createBitmap(width, height,
					Bitmap.Config.ARGB_8888);
			b.eraseColor(0xFFFFFFFF);
			Canvas c = new Canvas(b);

			Drawable d = null;// mBitmapIds[index]);

			if (leftFlag)
				d = FlutterbyAndMarguerite.PAGES.get(index - 1)
						.getPageDisplayLeftDrawable();
			else
				d = FlutterbyAndMarguerite.PAGES.get(index - 1)
						.getPageDisplayRightDrawable();

			int margin = 0;
			int border = 0;
			Rect r = new Rect(margin, margin, width - margin, height - margin);

			int imageWidth = r.width() - (border * 2);
			int imageHeight = imageWidth * d.getIntrinsicHeight()
					/ d.getIntrinsicWidth();

			if (imageHeight > r.height() - (border * 2)) {
				imageHeight = r.height() - (border * 2);
				imageWidth = imageHeight * d.getIntrinsicWidth()
						/ d.getIntrinsicHeight();
			}

			// CRAFTYHACK
			// r.left += ((r.width() - imageWidth) / 2) - border;
			// r.right = r.left + imageWidth + border + border;
			// r.top += ((r.height() - imageHeight) / 2) - border;
			// r.bottom = r.top + imageHeight + border + border;

			Paint p = new Paint();
			p.setColor(0xFFC0C0C0);

			c.drawRect(r, p);
			r.left += border;
			r.right -= border;
			r.top += border;
			r.bottom -= border;

			d.setBounds(r);
			d.draw(c);
			return b;
		}

		@Override
		public int getBitmapCount() {
			return FlutterbyAndMarguerite.PAGES.size();
		}
	}

	/**
	 * CurlView size changed observer.
	 */
	private class SizeChangedObserver implements CurlView.SizeChangedObserver {
		@Override
		public void onSizeChanged(int w, int h) {
			if (w > h) {
				mCurlView.setViewMode(CurlView.SHOW_TWO_PAGES);
				// mCurlView.setMargins(.1f, .05f, .1f, .05f);
				mCurlView.setMargins(0f, 0f, 0f, 0f);
			} else {
				mCurlView.setViewMode(CurlView.SHOW_ONE_PAGE);
				// mCurlView.setMargins(.1f, .1f, .1f, .1f);
				mCurlView.setMargins(0f, 0f, 0f, 0f);
			}
		}
	}
	
	public class NarrationTimer extends CountDownTimer {

		Runnable _runnable = null;
		long _duration = 0;
		long _delay = 0;
		
		public NarrationTimer(long duration, long delay, Runnable runnable) {
			super(duration, delay);
		
			_runnable = runnable;
			_duration = duration;
			_delay = delay;
		}

		@Override
		public void onFinish() 
		{
		}
		
		@Override
		public void onTick(long millisUntilFinished) {
			Handler handler = new Handler();
			handler.post(_runnable);		
		}

	}

	@Override
	public void narrationIsPrepared() {
		if(FlutterbyAndMarguerite.AUTO_SCROLL_TEXT)
		{
			//start the  text highlighting is we meet all conditions		
			if ((FlutterbyAndMarguerite._displayTextNarration) && (FlutterbyAndMarguerite._playAudioNarration) 
					&& (!FlutterbyAndMarguerite._flutterbyAliasEnabled) && (!FlutterbyAndMarguerite._margueriteAliasEnabled)
					&& (FlutterbyAndMarguerite._selectedNarrationProfile.equals("")))
			{
				int narrationAudioDuration = _narrationMedia.getDuration();//get audio file length
				final int textLength =  _narrationTextView.getText().length();//get narration text length
				
				long delayMillis = narrationAudioDuration / textLength;//what delay per character should we use?
				
				narrationAudioDuration = narrationAudioDuration + 1000;//round up just in case so we always highlight the whole narration in case the timer stumbles...			
				
				textIndex = 1;
				textScrollIndex = 0;
				
				Runnable runnable = new Runnable(){
	
					@Override
					public void run() {
						try {
							
							final String text = _narrationTextView.getText().toString();								
							
							if((text.charAt(textIndex) == ' ') || ((textIndex + 1)== textLength)) 
							{
							
								
								
								/*
								if((textIndex + 1) != textLength)
								{
									new Thread(new Runnable(){

										@Override
										public void run() {
											// TODO Auto-generated method stub
											String word = text.substring((textIndex + 1), text.indexOf(" ", textIndex + 1));
											if(word.equals("rattles"))
												_soundPoolMgr.playSound(R.raw.margueritetough);

										}
										
									}).run();
								}
								*/
								
								
								
								
								Spannable span = Spannable.Factory.getInstance().newSpannable(text);
								
								int index = textIndex;
								if((textIndex + 1)== textLength)
									index = textIndex + 1;
								
								int colorId = FlutterbyAndMarguerite.PAGES.get(_pageIndex - 1).get_NarrationHighliteColor();
								
								span.setSpan(new ForegroundColorSpan(CurlActivity.this.getResources().getColor(colorId)), 0, index, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

//								if((_pageIndex == 4) || (_pageIndex == 5))
//									span.setSpan(new ForegroundColorSpan(CurlActivity.this.getResources().getColor(R.color.roseKarokeTextColor)), 0, index, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//								else if((_pageIndex == 9) || (_pageIndex == 10) || (_pageIndex == 16) || (_pageIndex == 17) || (_pageIndex == 20)  || (_pageIndex == 21)  || (_pageIndex == 22)  || (_pageIndex == 23))
//									span.setSpan(new ForegroundColorSpan(CurlActivity.this.getResources().getColor(R.color.yellowKarokeTextColor)), 0, index, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//								else
//									span.setSpan(new ForegroundColorSpan(CurlActivity.this.getResources().getColor(R.color.redKarokeTextColor)), 0, index, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
									
								_narrationTextView.setText(span);							
								
								int lineHeight = _narrationTextView.getLineHeight();//get height for our scrollby sentinal
								int line = _narrationTextView.getLayout().getLineForOffset(textIndex); 	
	
								if(textScrollIndex != line)
								{
									_narrationScrollView.scrollBy(0, lineHeight);//auto scroll
									textScrollIndex = line;	
								}							
							}
							
							textIndex++;
							
						} catch (Exception e) {
							//do nothing
						}
						
					}};				
					
					narrationTimer = new NarrationTimer(narrationAudioDuration, delayMillis, runnable);
					narrationTimer.start();
			}
		}
		
	}
	static NarrationTimer narrationTimer = null;
	static int textIndex = 1;
	static int textScrollIndex = 0;

}