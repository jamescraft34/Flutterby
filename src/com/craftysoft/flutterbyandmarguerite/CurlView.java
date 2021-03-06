package com.craftysoft.flutterbyandmarguerite;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.RectF;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.craftysoft.flutterbyandmarguerite.Page.BasePage;
import com.immersion.uhl.Launcher;

/**
 * OpenGL ES View.
 * 
 * @author harism
 */
public class CurlView extends GLSurfaceView implements View.OnTouchListener,
		CurlRenderer.Observer {

	
	private boolean _startPageForward = true;
	private int mOldIndex = 1;
	
	
	//craftyhack
	private int _backPageNumber;
	private  PageTurnListener _pageTurnListener = null;
	private int _soundEffectColorClickedDown;
	private boolean _blockingForSoundEffect = false;//this allows us to mimic the single tap effect bc it wasn't possible to capture the singletapup listener
		
	// Shows one page at the center of view.
	public static final int SHOW_ONE_PAGE = 1;
	// Shows two pages side by side.
	public static final int SHOW_TWO_PAGES = 2;
	// One page is the default.
	private int mViewMode = SHOW_TWO_PAGES;//SHOW_ONE_PAGE;

	private boolean mRenderLeftPage = true;
	private boolean mAllowLastPageCurl = true;

	// Page meshes. Left and right meshes are 'static' while curl is used to
	// show page flipping.
	private CurlMesh mPageCurl;
	private CurlMesh mPageLeft;
	private CurlMesh mPageRight;

	// Curl state. We are flipping none, left or right page.
	private static final int CURL_NONE = 0;
	private static final int CURL_LEFT = 1;
	private static final int CURL_RIGHT = 2;
	private int mCurlState = CURL_NONE;

	// Current page index. This is always showed on right page.
	private int mCurrentIndex = 1;//0;craftyhack

	// Bitmap size. These are updated from renderer once it's initialized.
	public int mPageBitmapWidth = -1;
	public int mPageBitmapHeight = -1;

	// Start position for dragging.
	private PointF mDragStartPos = new PointF();
	private PointerPosition mPointerPos = new PointerPosition();
	private PointF mCurlPos = new PointF();
	private PointF mCurlDir = new PointF();

	private boolean mAnimate = false;
	private PointF mAnimationSource = new PointF();
	private PointF mAnimationTarget = new PointF();
	private long mAnimationStartTime;
	private long mAnimationDurationTime = 300;
	private int mAnimationTargetEvent;
	
	// Constants for mAnimationTargetEvent.
	private static final int SET_CURL_TO_LEFT = 1;
	private static final int SET_CURL_TO_RIGHT = 2;

	private CurlRenderer mRenderer;
	private BitmapProvider mBitmapProvider;
	private SizeChangedObserver mSizeChangedObserver;

	private boolean mEnableTouchPressure = false;

	/**
	 * Default constructor.
	 */
	public CurlView(Context ctx) {
		super(ctx);
		init(ctx);
	}

	/**
	 * Default constructor.
	 */
	public CurlView(Context ctx, AttributeSet attrs) {
		super(ctx, attrs);
		init(ctx);		
	}

	/**
	 * Default constructor.
	 */
	public CurlView(Context ctx, AttributeSet attrs, int defStyle) {
		this(ctx, attrs);
	}
	
	/**
	 * Set current page index.
	 */
	public int getCurrentIndex() {
		return mCurrentIndex;
	}
	
	//special case just to set the actual variable, different from other setCurrentIndex call
	public void setCurrentIndexOnly(int index)
	{
		mCurrentIndex = index;
	}

	@Override
	public void onDrawFrame() {
		
		// We are not animating.
		if (mAnimate == false) {
			return;
		}

		long currentTime = System.currentTimeMillis();
		// If animation is done.
		if (currentTime >= mAnimationStartTime + mAnimationDurationTime) {
			if (mAnimationTargetEvent == SET_CURL_TO_RIGHT) {				
				// Switch curled page to right.
				CurlMesh right = mPageCurl;
				CurlMesh curl = mPageRight;
				right.setRect(mRenderer.getPageRect(CurlRenderer.PAGE_RIGHT));
				right.setFlipTexture(false);
				
				// If we were curling left page update current index.
				if (mCurlState == CURL_LEFT) {
					mCurrentIndex = _backPageNumber;//mCurrentIndex--;//craftyhack
					right.swapFrontToBack = true;
				}
				
				right.reset();
				mRenderer.removeCurlMesh(curl);
				mPageCurl = curl;
				mPageRight = right;
//				// If we were curling left page update current index.//craftyhack, copied above in if statement
//				if (mCurlState == CURL_LEFT) {
//					mCurrentIndex--;
//				}
				
				if(_startPageForward)
				{
					//if we got here then the user attempted to turn the page but didnt finish
					//when the user taps on the right side of the page to move forward a rightcurl is recorded
					//if they finish the curl a leftcurl is what is finished with
					//since we are in this clause we are finishing with a right curl meaning the use went back and never finished the curl
					mCurrentIndex = mOldIndex;//set the index back
				}

				
			} 
			else if (mAnimationTargetEvent == SET_CURL_TO_LEFT) {
				
				// Switch curled page to left.
				CurlMesh left = mPageCurl;
				CurlMesh curl = mPageLeft;
				left.setRect(mRenderer.getPageRect(CurlRenderer.PAGE_LEFT));
				left.setFlipTexture(true);//craftyhack
				
				// If we were curling right page update current index.
				if (mCurlState == CURL_RIGHT) {//craftyhack
					mCurrentIndex++;
					left.swapFrontToBack = true;
				}
				//left.swapFrontToBack = true;
				
				left.reset();
				mRenderer.removeCurlMesh(curl);
				if (!mRenderLeftPage) {
					mRenderer.removeCurlMesh(left);
				}
				mPageCurl = curl;
				mPageLeft = left;
//				// If we were curling right page update current index.//craftyhack copied above in if statement
//				if (mCurlState == CURL_RIGHT) {
//					mCurrentIndex++;
//				}
			}
			mCurlState = CURL_NONE;

			mAnimate = false;
			requestRender();
			
			
			//HAPTIC
			//stop all vibes that are running while page turns
			//FlutterbyAndMarguerite.optionsListener.stopVibrate();
			
			
			
			//create this runnable bc onDrawFrame is called within the renderer thread and this allows use to call out to the UI thread
			mRenderer.handler.post(new Runnable() 
			{   
				public void run()    
				{       
					//craftyhack
					//page has finished turning so now grab the text and audio
					if(_pageTurnListener != null)
						_pageTurnListener.PageTurned(mCurrentIndex);
				}
			});
			

		} else {
			mPointerPos.mPos.set(mAnimationSource);
			float t = (float) Math
					.sqrt((double) (currentTime - mAnimationStartTime)
							/ mAnimationDurationTime);
			mPointerPos.mPos.x += (mAnimationTarget.x - mAnimationSource.x) * t;
			mPointerPos.mPos.y += (mAnimationTarget.y - mAnimationSource.y) * t;
			updateCurlPos(mPointerPos);
		}
	}
		
	

	@Override
	public void onPageSizeChanged(int width, int height) {
		mPageBitmapWidth = width;
		mPageBitmapHeight = height;
		updateBitmaps();
		requestRender();
	}

	@Override
	public void onSizeChanged(int w, int h, int ow, int oh) {
		super.onSizeChanged(w, h, ow, oh);
		requestRender();
		if (mSizeChangedObserver != null) {
			mSizeChangedObserver.onSizeChanged(w, h);
		}
	}

	@Override
	public void onSurfaceCreated() {
		// In case surface is recreated, let page meshes drop allocated texture
		// ids and ask for new ones. There's no need to set textures here as
		// onPageSizeChanged should be called later on.
		mPageLeft.resetTexture();
		mPageRight.resetTexture();
		mPageCurl.resetTexture();
	}
	
	public boolean isbBlockingTouchInput()
	{
		return mAnimate;
	}
	
	//craftyhack
	//determines if the down touch on the screen should trigger an attempt to turn the page
	private boolean determinePageCanTurn(float x)
	{
		//we will use a % of the margin to allow for touch to trigger page turn
		float touchMarginPercent = .15f;
		float touchMargin = getWidth() * touchMarginPercent;
		
		//check if x is within 15% from either side
		if((x <= touchMargin) && (FlutterbyAndMarguerite.PAGES.get(mCurrentIndex-1).getBackPage() != null))
			return true;
		else if((x >= (getWidth() - touchMargin)) && (FlutterbyAndMarguerite.PAGES.get(mCurrentIndex-1).getNextPageCount() != 0))
			return true;
		else
			return false;
	}


	@Override
	public boolean onTouch(View view, MotionEvent me) {
		
		// No dragging during animation at the moment.
		// TODO: Stop animation on touch event and retur to drag mode.
		if (mAnimate || mBitmapProvider == null) {
			return false;
		}

		// We need page rects quite extensively so get them for later use.
		RectF rightRect = mRenderer.getPageRect(CurlRenderer.PAGE_RIGHT);
		RectF leftRect = mRenderer.getPageRect(CurlRenderer.PAGE_LEFT);

		// Store pointer position.
		mPointerPos.mPos.set(me.getX(), me.getY());		
		mRenderer.translate(mPointerPos.mPos);
		if (mEnableTouchPressure) {
			mPointerPos.mPressure = me.getPressure();
		} else {
			mPointerPos.mPressure = 0f;
		}

		switch (me.getAction()) {
		case MotionEvent.ACTION_DOWN: {
			
			//craftyhack	
			//check if down was within the allowed area to trigger a page turn
			if(!determinePageCanTurn(me.getX()))
			{
				//get bitmap sound effects color that was clicked
				_soundEffectColorClickedDown = _pageTurnListener.getBitmapColorCode((int)me.getX(), (int)me.getY());
				
				_blockingForSoundEffect = true;
				return true;
			}
			else
			{
				_blockingForSoundEffect = false;
			}
			
			
			//hide all views while animating page flip
			_pageTurnListener.displayUIControls(false);
			
			
			//we should have stopped any vibrations after the call to displayUIControls, now play a quick button vibe bc we are touching the screen
			//to turn the page
			//haptic
			FlutterbyAndMarguerite.optionsListener.vibratePhoneId(Launcher.STRONG_CLICK_66);

			

			// Once we receive pointer down event its position is mapped to
			// right or left edge of page and that'll be the position from where
			// user is holding the paper to make curl happen.
			mDragStartPos.set(mPointerPos.mPos);

			// First we make sure it's not over or below page. Pages are
			// supposed to be same height so it really doesn't matter do we use
			// left or right one.
			if (mDragStartPos.y > rightRect.top) {
				mDragStartPos.y = rightRect.top;
			} else if (mDragStartPos.y < rightRect.bottom) {
				mDragStartPos.y = rightRect.bottom;
			}

			// Then we have to make decisions for the user whether curl is going
			// to happen from left or right, and on which page.
			if (mViewMode == SHOW_TWO_PAGES) {
				// If we have an open book and pointer is on the left from right
				// page we'll mark drag position to left edge of left page.
				// Additionally checking mCurrentIndex is higher than zero tells
				// us there is a visible page at all.
				if (mDragStartPos.x < rightRect.left && mCurrentIndex > 1){//0) {craftyhack
					mDragStartPos.x = leftRect.left;
					
					_startPageForward = false;//craftyhack
					
					startCurl(CURL_LEFT);
				}
				// Otherwise check pointer is on right page's side.
				else if (mDragStartPos.x >= rightRect.left
						&& mCurrentIndex < mBitmapProvider.getBitmapCount()) {
					
					
					
					mDragStartPos.x = rightRect.right;
					if (!mAllowLastPageCurl
							&& mCurrentIndex >= mBitmapProvider
									.getBitmapCount() - 1) {
						return false;
					}
					
					//craftyhack
					//check if there are multiple page forward decisions
					if(FlutterbyAndMarguerite.PAGES.get(mCurrentIndex-1).getNextPageCount() > 1)
					{
						_pageTurnListener.displayNarrationOptions(FlutterbyAndMarguerite.PAGES.get(mCurrentIndex-1));
						
						return false;
					}
					else
					{						
						mOldIndex = mCurrentIndex;//craftyhack
						
						BasePage curPage = FlutterbyAndMarguerite.PAGES.get(mCurrentIndex-1);
						mCurrentIndex = curPage.getNextPage()[0].getPageNumber() - 1;
						
						if(mCurrentIndex == (FlutterbyAndMarguerite.TOTALPAGECOUNT - 1))//we are going to the last page so record where we came from!
							FlutterbyAndMarguerite.PAGES.get(mCurrentIndex).setBackPage(curPage);
						else if((mCurrentIndex + 1) == 6)//special case page 6 can have two different pages feeding it (to make generic we should really do this for all pages)
							FlutterbyAndMarguerite.PAGES.get(mCurrentIndex).setBackPage(curPage);//we will just do this specific line of code for now (to tired to test for all cases)
						
						_startPageForward = true;
						
						startCurl(CURL_RIGHT);
					}
				}
			} else if (mViewMode == SHOW_ONE_PAGE) {
				float halfX = (rightRect.right + rightRect.left) / 2;
				if (mDragStartPos.x < halfX && mCurrentIndex > 0) {
					mDragStartPos.x = rightRect.left;
					startCurl(CURL_LEFT);
				} else if (mDragStartPos.x >= halfX
						&& mCurrentIndex < mBitmapProvider.getBitmapCount()) {
					mDragStartPos.x = rightRect.right;
					if (!mAllowLastPageCurl
							&& mCurrentIndex >= mBitmapProvider
									.getBitmapCount() - 1) {
						return false;
					}
					startCurl(CURL_RIGHT);
				}
			}
			// If we have are in curl state, let this case clause flow through
			// to next one. We have pointer position and drag position defined
			// and this will create first render request given these points.
			if (mCurlState == CURL_NONE) {
				return false;
			}
		}
		case MotionEvent.ACTION_MOVE: {
			
			if(_blockingForSoundEffect)
				return true;
			
			updateCurlPos(mPointerPos);
			break;
		}
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP: {
			
			if(_blockingForSoundEffect)
			{
				try
				{
					//get bitmap color and compare with what we got on ACTION_DOWN
					if(_soundEffectColorClickedDown == _pageTurnListener.getBitmapColorCode((int)me.getX(), (int)me.getY()))
					{
						String colorCode = Integer.toHexString(_soundEffectColorClickedDown);
						//special case for last page!!!
						if(colorCode.equals("ff000099"))
							_pageTurnListener.popupBitmapColorCodeLastPage();
						else
							_pageTurnListener.playSoundEffect(_soundEffectColorClickedDown);
					}
				}
				catch(Exception ex)
				{
					//do nothing, just dont play the sound
				}
				
				_blockingForSoundEffect = false;
				return true;
			}
			
			
			if (mCurlState == CURL_LEFT || mCurlState == CURL_RIGHT) {
				
				
				//feel the page turn at the end of its transition
				FlutterbyAndMarguerite.optionsListener.vibratePhoneId(Launcher.SHORT_TRANSITION_RAMP_UP_33);
				//FlutterbyAndMarguerite.optionsListener.vibratePhoneId(Launcher.TEXTURE7);//infinite vibe make sure to stop it later
				
				
				// Animation source is the point from where animation starts.
				// Also it's handled in a way we actually simulate touch events
				// meaning the output is exactly the same as if user drags the
				// page to other side. While not producing the best looking
				// result (which is easier done by altering curl position and/or
				// direction directly), this is done in a hope it made code a
				// bit more readable and easier to maintain.
				mAnimationSource.set(mPointerPos.mPos);
				mAnimationStartTime = System.currentTimeMillis();

				// Given the explanation, here we decide whether to simulate
				// drag to left or right end.
				if ((mViewMode == SHOW_ONE_PAGE && mPointerPos.mPos.x > (rightRect.left + rightRect.right) / 2)
						|| mViewMode == SHOW_TWO_PAGES
						&& mPointerPos.mPos.x > rightRect.left) {
					// On right side target is always right page's right border.
					mAnimationTarget.set(mDragStartPos);
					mAnimationTarget.x = mRenderer
							.getPageRect(CurlRenderer.PAGE_RIGHT).right;
					mAnimationTargetEvent = SET_CURL_TO_RIGHT;
				} else {
					// On left side target depends on visible pages.
					mAnimationTarget.set(mDragStartPos);
					if (mCurlState == CURL_RIGHT || mViewMode == SHOW_TWO_PAGES) {
						mAnimationTarget.x = leftRect.left;
					} else {
						mAnimationTarget.x = rightRect.left;
					}
					mAnimationTargetEvent = SET_CURL_TO_LEFT;
				}
				mAnimate = true;
				requestRender();
			}
			break;
			}
		}
		
		return true;
	}
		
	private void startAutoCurl(int x, int y)
	{
		// We need page rects quite extensively so get them for later use.
		RectF rightRect = mRenderer.getPageRect(CurlRenderer.PAGE_RIGHT);

		// Store pointer position.
		mPointerPos.mPos.set(x, y);		
		mRenderer.translate(mPointerPos.mPos);
		mPointerPos.mPressure = 0f;
		
			mDragStartPos.set(mPointerPos.mPos);

			// First we make sure it's not over or below page. Pages are
			// supposed to be same height so it really doesn't matter do we use
			// left or right one.
			if (mDragStartPos.y > rightRect.top) {
				mDragStartPos.y = rightRect.top;
			} else if (mDragStartPos.y < rightRect.bottom) {
				mDragStartPos.y = rightRect.bottom;
			}

			if (mDragStartPos.x >= rightRect.left
						&& mCurrentIndex < mBitmapProvider.getBitmapCount()) 
			{
					mDragStartPos.x = rightRect.right;
					startCurl(CURL_RIGHT);
			}
	}
	
	private void doAutoCurl(int x, int y)
	{
		// Store pointer position.
		mPointerPos.mPos.set(x, y);		
		mRenderer.translate(mPointerPos.mPos);
		mPointerPos.mPressure = 0f;
		
		updateCurlPos(mPointerPos);
	}
	
	private void finishAutoCurl(int x, int y)
	{		
		// We need page rects quite extensively so get them for later use.
		RectF rightRect = mRenderer.getPageRect(CurlRenderer.PAGE_RIGHT);
		RectF leftRect = mRenderer.getPageRect(CurlRenderer.PAGE_LEFT);

		// Store pointer position.
		mPointerPos.mPos.set(x, y);		
		mRenderer.translate(mPointerPos.mPos);
		mPointerPos.mPressure = 0f;
		
		if (mCurlState == CURL_LEFT || mCurlState == CURL_RIGHT) {
			// Animation source is the point from where animation starts.
			// Also it's handled in a way we actually simulate touch events
			// meaning the output is exactly the same as if user drags the
			// page to other side. While not producing the best looking
			// result (which is easier done by altering curl position and/or
			// direction directly), this is done in a hope it made code a
			// bit more readable and easier to maintain.
			mAnimationSource.set(mPointerPos.mPos);
			mAnimationStartTime = System.currentTimeMillis();

			// Given the explanation, here we decide whether to simulate
			// drag to left or right end.
			if ((mViewMode == SHOW_ONE_PAGE && mPointerPos.mPos.x > (rightRect.left + rightRect.right) / 2)
					|| mViewMode == SHOW_TWO_PAGES
					&& mPointerPos.mPos.x > rightRect.left) {
				// On right side target is always right page's right border.
				mAnimationTarget.set(mDragStartPos);
				mAnimationTarget.x = mRenderer
						.getPageRect(CurlRenderer.PAGE_RIGHT).right;
				mAnimationTargetEvent = SET_CURL_TO_RIGHT;
			} else {
				// On left side target depends on visible pages.
				mAnimationTarget.set(mDragStartPos);
				if (mCurlState == CURL_RIGHT || mViewMode == SHOW_TWO_PAGES) {
					mAnimationTarget.x = leftRect.left;
				} else {
					mAnimationTarget.x = rightRect.left;
				}
				mAnimationTargetEvent = SET_CURL_TO_LEFT;
			}
			mAnimate = true;
			requestRender();
		}
	}
	
	public void performAutoCurl(int goToPage)
	{
		mAnimate = false;
		mCurrentIndex = goToPage - 1;
		performAutoCurl(this.getWidth(), this.getHeight());
	}
	
	private void performAutoCurl(int screenWidth, int screenHeight)
	{
		//start infinite vobe for page turn, make sure to turn off when page turn done
//		FlutterbyAndMarguerite.optionsListener.vibratePhoneId(Launcher.TEXTURE7);

		
		startAutoCurl(screenWidth, screenHeight);
				
		int threshold = screenWidth / 2;
		int i = 0;
		for(; i <= threshold; i++)
		{
			int y = screenHeight - i;
			if(y < 0)//prevent negative values bc that will mess up the curl
				y = 0;
			
			doAutoCurl(screenWidth - i, y);
		}
		
		finishAutoCurl(screenWidth - i, screenHeight - i);
		
		FlutterbyAndMarguerite.optionsListener.vibratePhoneId(Launcher.SHORT_TRANSITION_RAMP_UP_33);
		
		//stop the infinite vibe
		//FlutterbyAndMarguerite.optionsListener.stopVibrate();
	}
	
	/**
	 * Allow the last page to curl.
	 */
	public void setAllowLastPageCurl(boolean allowLastPageCurl) {
		mAllowLastPageCurl = allowLastPageCurl;
	}

	/**
	 * Sets background color - or OpenGL clear color to be more precise. Color
	 * is a 32bit value consisting of 0xAARRGGBB and is extracted using
	 * android.graphics.Color eventually.
	 */
	@Override
	public void setBackgroundColor(int color) {
		mRenderer.setBackgroundColor(color);
		requestRender();
	}

	/**
	 * Update/set bitmap provider.
	 */
	public void setBitmapProvider(BitmapProvider bitmapProvider) {
		mBitmapProvider = bitmapProvider;
		mCurrentIndex = 0;
		updateBitmaps();
		requestRender();
	}

	/**
	 * Set page index.
	 */
	public void setCurrentIndex(int index) {
		if (mBitmapProvider == null || index <= 0) {
			mCurrentIndex = 1;//0;craftyhack
		} else {
			mCurrentIndex = index;//Math.min(index,			//craftyhack
					//mBitmapProvider.getBitmapCount() - 1);
		}
		
		mAnimate = true;
		
		updateBitmaps();
		requestRender();
		
		//craftyhack
		if(_pageTurnListener != null)
		{
			_pageTurnListener.PageTurned(mCurrentIndex);
		}
	}

	/**
	 * If set to true, touch event pressure information is used to adjust curl
	 * radius. The more you press, the flatter the curl becomes. This is
	 * somewhat experimental and results may vary significantly between devices.
	 * On emulator pressure information seems to be flat 1.0f which is maximum
	 * value and therefore not very much of use.
	 */
	public void setEnableTouchPressure(boolean enableTouchPressure) {
		mEnableTouchPressure = enableTouchPressure;
	}

	/**
	 * Set margins (or padding). Note: margins are proportional. Meaning a value
	 * of .1f will produce a 10% margin.
	 */
	public void setMargins(float left, float top, float right, float bottom) {
		mRenderer.setMargins(left, top, right, bottom);
	}

	/**
	 * Setter for whether left side page is rendered. This is useful mostly for
	 * situations where right (main) page is aligned to left side of screen and
	 * left page is not visible anyway.
	 */
	public void setRenderLeftPage(boolean renderLeftPage) {
		mRenderLeftPage = renderLeftPage;
	}

	/**
	 * Sets SizeChangedObserver for this View. Call back method is called from
	 * this View's onSizeChanged method.
	 */
	public void setSizeChangedObserver(SizeChangedObserver observer) {
		mSizeChangedObserver = observer;
	}

	/**
	 * Sets view mode. Value can be either SHOW_ONE_PAGE or SHOW_TWO_PAGES. In
	 * former case right page is made size of display, and in latter case two
	 * pages are laid on visible area.
	 */
	public void setViewMode(int viewMode) {
		switch (viewMode) {
		case SHOW_ONE_PAGE:
			mViewMode = viewMode;
			mRenderer.setViewMode(CurlRenderer.SHOW_ONE_PAGE);
			break;
		case SHOW_TWO_PAGES:
			mViewMode = viewMode;
			mRenderer.setViewMode(CurlRenderer.SHOW_TWO_PAGES);
			break;
		}
	}
	
	final Handler myHandler = new Handler(){
	};

	/**
	 * Initialize method.
	 */
	private void init(Context ctx) {
		mRenderer = new CurlRenderer(this);
		
		//craftyhack
		mRenderer.handler = myHandler;
				
		setRenderer(mRenderer);
		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		setOnTouchListener(this);

		// Even though left and right pages are static we have to allocate room
		// for curl on them too as we are switching meshes. Another way would be
		// to swap texture ids only.
		mPageLeft = new CurlMesh(10);
		mPageRight = new CurlMesh(10);
		mPageCurl = new CurlMesh(10);
		mPageLeft.setFlipTexture(true);
		mPageRight.setFlipTexture(false);
	}

	/**
	 * Sets mPageCurl curl position.
	 */
	private void setCurlPos(PointF curlPos, PointF curlDir, double radius) {

		// First reposition curl so that page doesn't 'rip off' from book.
		if (mCurlState == CURL_RIGHT
				|| (mCurlState == CURL_LEFT && mViewMode == SHOW_ONE_PAGE)) {
			RectF pageRect = mRenderer.getPageRect(CurlRenderer.PAGE_RIGHT);
			if (curlPos.x >= pageRect.right) {
				mPageCurl.reset();
				requestRender();
				return;
			}
			if (curlPos.x < pageRect.left) {
				curlPos.x = pageRect.left;
			}
			if (curlDir.y != 0) {
				float diffX = curlPos.x - pageRect.left;
				float leftY = curlPos.y + (diffX * curlDir.x / curlDir.y);
				if (curlDir.y < 0 && leftY < pageRect.top) {
					curlDir.x = curlPos.y - pageRect.top;
					curlDir.y = pageRect.left - curlPos.x;
				} else if (curlDir.y > 0 && leftY > pageRect.bottom) {
					curlDir.x = pageRect.bottom - curlPos.y;
					curlDir.y = curlPos.x - pageRect.left;
				}
			}
		} else if (mCurlState == CURL_LEFT) {
			RectF pageRect = mRenderer.getPageRect(CurlRenderer.PAGE_LEFT);
			if (curlPos.x <= pageRect.left) {
				mPageCurl.reset();
				requestRender();
				return;
			}
			if (curlPos.x > pageRect.right) {
				curlPos.x = pageRect.right;
			}
			if (curlDir.y != 0) {
				float diffX = curlPos.x - pageRect.right;
				float rightY = curlPos.y + (diffX * curlDir.x / curlDir.y);
				if (curlDir.y < 0 && rightY < pageRect.top) {
					curlDir.x = pageRect.top - curlPos.y;
					curlDir.y = curlPos.x - pageRect.right;
				} else if (curlDir.y > 0 && rightY > pageRect.bottom) {
					curlDir.x = curlPos.y - pageRect.bottom;
					curlDir.y = pageRect.right - curlPos.x;
				}
			}
		}

		// Finally normalize direction vector and do rendering.
		double dist = Math.sqrt(curlDir.x * curlDir.x + curlDir.y * curlDir.y);
		if (dist != 0) {
			curlDir.x /= dist;
			curlDir.y /= dist;
			mPageCurl.curl(curlPos, curlDir, radius);
		} else {
			mPageCurl.reset();
		}

		requestRender();
	}

	/**
	 * Switches meshes and loads new bitmaps if available.
	 */
	private void startCurl(int page) {
		switch (page) {

		// Once right side page is curled, first right page is assigned into
		// curled page. And if there are more bitmaps available new bitmap is
		// loaded into right side mesh.
		case CURL_RIGHT: {
			// Remove meshes from renderer.
			mRenderer.removeCurlMesh(mPageLeft);
			mRenderer.removeCurlMesh(mPageRight);
			mRenderer.removeCurlMesh(mPageCurl);

			// We are curling right page.
			CurlMesh curl = mPageRight;
			mPageRight = mPageCurl;
			mPageCurl = curl;
						
			
			// If there is something to show on left page, simply add it to
			// renderer.
			if (mCurrentIndex > 0) {
				mPageLeft
						.setRect(mRenderer.getPageRect(CurlRenderer.PAGE_LEFT));
				mPageLeft.reset();
				if (mRenderLeftPage) {
					mRenderer.addCurlMesh(mPageLeft);
				}
			}

			// If there is new/next available, set it to right page.
			if (mCurrentIndex < mBitmapProvider.getBitmapCount()){// - 1) {craftyhack
//				Bitmap bitmap = mBitmapProvider.getBitmap(mPageBitmapWidth,
//						mPageBitmapHeight, mCurrentIndex + 1);
//				Bitmap bitmap = mBitmapProvider.getBitmap(mPageBitmapWidth,
//						mPageBitmapHeight, (((mCurrentIndex + 1) * 2) - 1));//craftyhack
				Bitmap bitmap = mBitmapProvider.getBitmap(mPageBitmapWidth,
						mPageBitmapHeight, mCurrentIndex + 1, false);//craftyhack
				
				mPageRight.setBitmap(bitmap);

				
				//craftyhack
//				Bitmap bitmapback = mBitmapProvider.getBitmap(mPageBitmapWidth,
//						mPageBitmapHeight, (((mCurrentIndex + 1) * 2) - 2));
				Bitmap bitmapback = mBitmapProvider.getBitmap(mPageBitmapWidth,
						mPageBitmapHeight, mCurrentIndex + 1, true);
				
				mPageCurl.setBacksideBitmap(bitmapback);//craftyhack
				
				
				mPageRight.setRect(mRenderer
						.getPageRect(CurlRenderer.PAGE_RIGHT));
				mPageRight.setFlipTexture(false);
				mPageRight.reset();
				mRenderer.addCurlMesh(mPageRight);
			}
			
			// Add curled page to renderer.
			mPageCurl.setRect(mRenderer.getPageRect(CurlRenderer.PAGE_RIGHT));
			mPageCurl.setFlipTexture(false);
			mPageCurl.reset();
			mRenderer.addCurlMesh(mPageCurl);

			mCurlState = CURL_RIGHT;
			break;
		}

			// On left side curl, left page is assigned to curled page. And if
			// there are more bitmaps available before currentIndex, new bitmap
			// is loaded into left page.
		case CURL_LEFT: {
			// Remove meshes from renderer.
			mRenderer.removeCurlMesh(mPageLeft);
			mRenderer.removeCurlMesh(mPageRight);
			mRenderer.removeCurlMesh(mPageCurl);

			// We are curling left page.
			CurlMesh curl = mPageLeft;
			mPageLeft = mPageCurl;
			mPageCurl = curl;

			// If there is new/previous bitmap available load it to left page.
			if (mCurrentIndex > 1) {
				//Bitmap bitmap = mBitmapProvider.getBitmap(mPageBitmapWidth,
				//		mPageBitmapHeight, mCurrentIndex - 2);
				
				//grab the backpage page number from the current page, do this bc its dynamic for some pages
				_backPageNumber = FlutterbyAndMarguerite.PAGES.get(mCurrentIndex-1).getBackPage().getPageNumber();

				
//				Bitmap bitmap = mBitmapProvider.getBitmap(mPageBitmapWidth,
//						mPageBitmapHeight, (((mCurrentIndex - 1) * 2) - 2));//craftyhack
//				Bitmap bitmap = mBitmapProvider.getBitmap(mPageBitmapWidth,
//						mPageBitmapHeight, mCurrentIndex - 1, true);//craftyhack
				Bitmap bitmap = mBitmapProvider.getBitmap(mPageBitmapWidth,
						mPageBitmapHeight, _backPageNumber, true);//craftyhack
				
				
				//craftyhack
//				Bitmap bitmapback = mBitmapProvider.getBitmap(mPageBitmapWidth,
//						mPageBitmapHeight, (((mCurrentIndex - 1) * 2) - 1));
//				Bitmap bitmapback = mBitmapProvider.getBitmap(mPageBitmapWidth,
//						mPageBitmapHeight, mCurrentIndex - 1, false);
				Bitmap bitmapback = mBitmapProvider.getBitmap(mPageBitmapWidth,
						mPageBitmapHeight, _backPageNumber, false);

				mPageCurl.setBacksideBitmap(bitmapback);//craftyhack
				
				
				mPageLeft.setBitmap(bitmap);
				mPageLeft
						.setRect(mRenderer.getPageRect(CurlRenderer.PAGE_LEFT));
				mPageLeft.setFlipTexture(true);
				mPageLeft.reset();
				if (mRenderLeftPage) {
					mRenderer.addCurlMesh(mPageLeft);
				}
			}

			// If there is something to show on right page add it to renderer.
			if (mCurrentIndex <= mBitmapProvider.getBitmapCount()) {//craftyhack added "="
				mPageRight.setRect(mRenderer
						.getPageRect(CurlRenderer.PAGE_RIGHT));
				mPageRight.reset();
				mRenderer.addCurlMesh(mPageRight);
			}

			// How dragging previous page happens depends on view mode.
			if (mViewMode == SHOW_ONE_PAGE) {
				mPageCurl.setRect(mRenderer
						.getPageRect(CurlRenderer.PAGE_RIGHT));
				mPageCurl.setFlipTexture(false);
			} else {
				mPageCurl
						.setRect(mRenderer.getPageRect(CurlRenderer.PAGE_LEFT));
				mPageCurl.setFlipTexture(true);
			}
			mPageCurl.reset();
			mRenderer.addCurlMesh(mPageCurl);

			mCurlState = CURL_LEFT;
			break;
		}

		}
	}

	/**
	 * Updates bitmaps for left and right meshes.
	 */
	private void updateBitmaps() {
		if (mBitmapProvider == null || mPageBitmapWidth <= 0
				|| mPageBitmapHeight <= 0) {
			return;
		}

		// Remove meshes from renderer.
		mRenderer.removeCurlMesh(mPageLeft);
		mRenderer.removeCurlMesh(mPageRight);
		mRenderer.removeCurlMesh(mPageCurl);

//		int leftIdx = ((mCurrentIndex * 2) - 2);//craftyhack adjust for the index to page difference
//		int rightIdx = ((mCurrentIndex * 2) - 1);//craftyhack
		
		int leftIdx = mCurrentIndex;// - 1;
		int rightIdx = mCurrentIndex;
		
		int curlIdx = -1;
		if (mCurlState == CURL_LEFT) {
			curlIdx = leftIdx;
			leftIdx--;
		} else if (mCurlState == CURL_RIGHT) {
			curlIdx = rightIdx;
			rightIdx++;
		}

		if (rightIdx >= 0 && rightIdx <= mBitmapProvider.getBitmapCount()) {//craftyhack added "="
			Bitmap bitmap = mBitmapProvider.getBitmap(mPageBitmapWidth,
					mPageBitmapHeight, rightIdx, false);
			mPageRight.setBitmap(bitmap);
			mPageRight.setRect(mRenderer.getPageRect(CurlRenderer.PAGE_RIGHT));
			mPageRight.reset();
			mRenderer.addCurlMesh(mPageRight);
		}
		if (leftIdx >= 0 && leftIdx <= mBitmapProvider.getBitmapCount()) {//craftyhack added "="
			Bitmap bitmap = mBitmapProvider.getBitmap(mPageBitmapWidth,
					mPageBitmapHeight, leftIdx, true );
			mPageLeft.setBitmap(bitmap);
			mPageLeft.setRect(mRenderer.getPageRect(CurlRenderer.PAGE_LEFT));
			mPageLeft.reset();
			if (mRenderLeftPage) {
				mRenderer.addCurlMesh(mPageLeft);
			}
		}
		if (curlIdx >= 0 && curlIdx <= mBitmapProvider.getBitmapCount()) {//craftyhack added "="
			Bitmap bitmap = mBitmapProvider.getBitmap(mPageBitmapWidth,
					mPageBitmapHeight, curlIdx, false);
			mPageCurl.setBitmap(bitmap);
			if (mCurlState == CURL_RIGHT
					|| (mCurlState == CURL_LEFT && mViewMode == SHOW_TWO_PAGES)) {
				mPageCurl.setRect(mRenderer
						.getPageRect(CurlRenderer.PAGE_RIGHT));
			} else {
				mPageCurl
						.setRect(mRenderer.getPageRect(CurlRenderer.PAGE_LEFT));
			}
			mPageCurl.reset();
			mRenderer.addCurlMesh(mPageCurl);
		}
	}

	/**
	 * Updates curl position.
	 */
	private void updateCurlPos(PointerPosition pointerPos) {

		// Default curl radius.
		double radius = mRenderer.getPageRect(CURL_RIGHT).width() / 3;
		// TODO: This is not an optimal solution. Based on feedback received so
		// far; pressure is not very accurate, it may be better not to map
		// coefficient to range [0f, 1f] but something like [.2f, 1f] instead.
		// Leaving it as is until get my hands on a real device. On emulator
		// this doesn't work anyway.
		radius *= Math.max(1f - pointerPos.mPressure, 0f);
		// NOTE: Here we set pointerPos to mCurlPos. It might be a bit confusing
		// later to see e.g "mCurlPos.x - mDragStartPos.x" used. But it's
		// actually pointerPos we are doing calculations against. Why? Simply to
		// optimize code a bit with the cost of making it unreadable. Otherwise
		// we had to this in both of the next if-else branches.
		mCurlPos.set(pointerPos.mPos);

		// If curl happens on right page, or on left page on two page mode,
		// we'll calculate curl position from pointerPos.
		if (mCurlState == CURL_RIGHT
				|| (mCurlState == CURL_LEFT && mViewMode == SHOW_TWO_PAGES)) {

			mCurlDir.x = mCurlPos.x - mDragStartPos.x;
			mCurlDir.y = mCurlPos.y - mDragStartPos.y;
			float dist = (float) Math.sqrt(mCurlDir.x * mCurlDir.x + mCurlDir.y
					* mCurlDir.y);

			// Adjust curl radius so that if page is dragged far enough on
			// opposite side, radius gets closer to zero.
			float pageWidth = mRenderer.getPageRect(CurlRenderer.PAGE_RIGHT)
					.width();
			double curlLen = radius * Math.PI;
			if (dist > (pageWidth * 2) - curlLen) {
				curlLen = Math.max((pageWidth * 2) - dist, 0f);
				radius = curlLen / Math.PI;
			}

			// Actual curl position calculation.
			if (dist >= curlLen) {
				double translate = (dist - curlLen) / 2;
				mCurlPos.x -= mCurlDir.x * translate / dist;
				mCurlPos.y -= mCurlDir.y * translate / dist;
			} else {
				double angle = Math.PI * Math.sqrt(dist / curlLen);
				double translate = radius * Math.sin(angle);
				mCurlPos.x += mCurlDir.x * translate / dist;
				mCurlPos.y += mCurlDir.y * translate / dist;
			}

			setCurlPos(mCurlPos, mCurlDir, radius);
		}
		// Otherwise we'll let curl follow pointer position.
		else if (mCurlState == CURL_LEFT) {

			// Adjust radius regarding how close to page edge we are.
			float pageLeftX = mRenderer.getPageRect(CurlRenderer.PAGE_RIGHT).left;
			radius = Math.max(Math.min(mCurlPos.x - pageLeftX, radius), 0f);

			float pageRightX = mRenderer.getPageRect(CurlRenderer.PAGE_RIGHT).right;
			mCurlPos.x -= Math.min(pageRightX - mCurlPos.x, radius);
			mCurlDir.x = mCurlPos.x + mDragStartPos.x;
			mCurlDir.y = mCurlPos.y - mDragStartPos.y;

			setCurlPos(mCurlPos, mCurlDir, radius);
		}
	}

	/**
	 * Provider for feeding 'book' with bitmaps which are used for rendering
	 * pages.
	 */
	public interface BitmapProvider {

		/**
		 * Called once new bitmap is needed. Width and height are in pixels
		 * telling the size it will be drawn on screen and following them
		 * ensures that aspect ratio remains. But it's possible to return bitmap
		 * of any size though.<br/>
		 * <br/>
		 * Index is a number between 0 and getBitmapCount() - 1.
		 */
		public Bitmap getBitmap(int width, int height, int index, boolean leftPage);

		/**
		 * Return number of pages/bitmaps available.
		 */
		public int getBitmapCount();
	}

	/**
	 * Observer interface for handling CurlView size changes.
	 */
	public interface SizeChangedObserver {

		/**
		 * Called once CurlView size changes.
		 */
		public void onSizeChanged(int width, int height);
	}

	/**
	 * Simple holder for pointer position.
	 */
	private class PointerPosition {
		PointF mPos = new PointF();
		float mPressure;
	}


	//craftyhack
	public void setPageTurnListener(PageTurnListener pageTurnListener)
	{
		_pageTurnListener = pageTurnListener;
	}
}
