package com.craftysoft.flutterbyandmarguerite;

import java.io.File;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder.OnInfoListener;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

import com.craftysoft.flutterbyandmarguerite.pro.R;
import com.immersion.uhl.Launcher;

public class AudioNarrationSetup extends Activity {

	private Button _closeButton = null;
	
	private ImageButton _recordButton = null;
	private ImageButton _playButton = null;
	private TextView _textviewPrompter = null;
	private TextView _textViewPlay = null;
	private TextView _textViewRecord = null;
	
	//private MyScrollView _scrollView = null;
	
	private int _currentPage = 1;
	
	private MyGallery _gallery = null;
	
	private String _profileName = null;
	
	private boolean _prompterShowing = false;
	
	private boolean _isRecording = false;
	private boolean _isPlaying = false;
	//private boolean _displayDialog = true;
	
	private boolean isXLargeScreen() {
		return ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE);
	}

	
	private OnClickListener recordButtonListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			
//			FlutterbyAndMarguerite.optionsListener.playQuickChime();
			
			if(_isRecording)
			{				
				setRecordingStopped();
			}
			else
			{
				if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) 
				{		    								
					_gallery._blocking = true;
					
					//turn off background music if playing
					if(FlutterbyAndMarguerite._playBackgroundAudio)
						FlutterbyAndMarguerite.optionsListener.turnBackgroundAudioOff();

					if(isXLargeScreen())
						_playButton.setBackgroundResource(R.drawable.playdisabledlarge);
					else
						_playButton.setBackgroundResource(R.drawable.playdisabled);
					
					_playButton.setEnabled(false);

					//record button shows prompter
					showPrompter();

					if(isXLargeScreen())
						_recordButton.setBackgroundResource(R.drawable.stoplarge);
					else
						_recordButton.setBackgroundResource(R.drawable.stop);
					
					_textViewRecord.setText("Stop");
					
					_isRecording = true;				
							
					try
					{
						//first delete file if exists
						AudioNarrationManager.deleteNarrationAudioFile(AudioNarrationSetup.this, _profileName, _currentPage);
						
						String narrationFile = AudioNarrationManager.getNarrationFilePath(AudioNarrationSetup.this, _profileName, _currentPage);
		
						AudioNarrationManager.startRecording(narrationFile);
		
						AudioNarrationManager.recorder.setOnInfoListener(new OnInfoListener(){
							@Override
							public void onInfo(MediaRecorder arg0, int arg1, int arg2) {
								if(arg1 == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED)
									setRecordingStopped();		
							}
						});
					}
					catch(Exception ex)
					{
						Toast.makeText(AudioNarrationSetup.this, "An error occured while trying to record.", Toast.LENGTH_SHORT).show();
						setRecordingStopped();
					}
				}
				else
				{
					//media not available to record?
					Toast.makeText(AudioNarrationSetup.this, "Unable to record to the device at this time, external media unavailable.", Toast.LENGTH_SHORT).show();
				}
			}
		}};
		
		private void setRecordingStopped()
		{
			_gallery._blocking = false;

			//turn on background music if playing
			if(FlutterbyAndMarguerite._playBackgroundAudio)
				FlutterbyAndMarguerite.optionsListener.turnBackgroundAudioOn();

			if(isXLargeScreen())
				_recordButton.setBackgroundResource(R.drawable.recordlarge);
			else
				_recordButton.setBackgroundResource(R.drawable.record);
			
			_isRecording = false;
			
			AudioNarrationManager.stopRecording();
			
			initalizePlayButton();
			
			hidePrompter();
			
			_textViewRecord.setText("Record");
		}
		
		private void setPlaybackStopped()
		{
			_gallery._blocking = false;

			if(isXLargeScreen())
				_playButton.setBackgroundResource(R.drawable.playlarge);
			else
				_playButton.setBackgroundResource(R.drawable.play);
			
			_isPlaying = false;
			
			//enable recording bc narration text is already showing now
			if(isXLargeScreen())
				_recordButton.setBackgroundResource(R.drawable.recordlarge);
			else
				_recordButton.setBackgroundResource(R.drawable.record);
			
			_recordButton.setEnabled(true);
			_isRecording = false;
			
			//stop narration playback
			AudioNarrationManager.stopPlaybackRecording();
			
			hidePrompter();
			
			_textViewPlay.setText("Play");
		}

	private OnClickListener playButtonListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			
			//FlutterbyAndMarguerite.optionsListener.playQuickChime();
			
			if(_isPlaying)//we want to stop playback
			{
				setPlaybackStopped();				
			}
			else///start playback
			{
				if((Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) || (Environment.MEDIA_MOUNTED_READ_ONLY.equals(Environment.getExternalStorageState()))) 
				{
					showPrompter();
					
					_gallery._blocking = true;
	
					if(isXLargeScreen())
					{
						_playButton.setBackgroundResource(R.drawable.stoplarge);
						_recordButton.setBackgroundResource(R.drawable.recorddisabledlarge);
					}
					else
					{
						_playButton.setBackgroundResource(R.drawable.stop);
						_recordButton.setBackgroundResource(R.drawable.recorddisabled);
					}
					
					_textViewPlay.setText("Stop");
					
					_isPlaying = true;				
					
					_recordButton.setEnabled(false);
					_isRecording = false;
					
					//show narration text if already showing
				
					try{
						//play narration
						String narrationFile = AudioNarrationManager.getNarrationFilePath(AudioNarrationSetup.this, _profileName, _currentPage);
		
						AudioNarrationManager.playRecording(narrationFile);
						AudioNarrationManager.mPlayer.setOnCompletionListener(new OnCompletionListener(){
		
							@Override
							public void onCompletion(MediaPlayer arg0) {
								setPlaybackStopped();
							}});
					}
					catch(Exception ex)
					{
						Toast.makeText(AudioNarrationSetup.this, "An error occured while trying play narration.", Toast.LENGTH_SHORT).show();
						
						setPlaybackStopped();
					}
				}
				else
				{
					//media not available to play?
					Toast.makeText(AudioNarrationSetup.this, "Playback disabled at this time, external media unavailable.", Toast.LENGTH_SHORT).show();
				}
			}
		}};
		
		private void initalizePlayButton()
		{
			File narrationFile = AudioNarrationManager.getNarrationAudioFile(AudioNarrationSetup.this, _profileName, _currentPage);
    		
    		if(narrationFile == null)
    		{
    			if(isXLargeScreen())
    				_playButton.setBackgroundResource(R.drawable.playdisabledlarge);
    			else
    				_playButton.setBackgroundResource(R.drawable.playdisabled);
    			
    			_playButton.setEnabled(false);
    		}
    		else
    		{
    			if(isXLargeScreen())
    				_playButton.setBackgroundResource(R.drawable.playlarge);
    			else
    				_playButton.setBackgroundResource(R.drawable.play);
    			
    			_playButton.setEnabled(true);
    		}
		}
		
		private void showPrompter()
		{
			if(_currentPage < FlutterbyAndMarguerite.TOTALPAGECOUNT)
			{
				_prompterShowing = true;
				
	//			if(_scrollView != null)
	//			{
	//				_scrollView.setVisibility(View.VISIBLE);
	//			}
				
				if(_textviewPrompter != null)
				{
					_textviewPrompter.setVisibility(View.VISIBLE);
					_textviewPrompter.setMaxWidth(_gallery.getWidth());
					_textviewPrompter.setWidth(_gallery.getWidth());
					_textviewPrompter.setMaxHeight((int)(_gallery.getHeight()*.60));//set to 50% of gallery so we can still touch it
					_textviewPrompter.scrollTo(0, 0);
					_textviewPrompter.setText(FlutterbyAndMarguerite.replaceCharacterDetails(FlutterbyAndMarguerite.PAGES.get(_currentPage-1).getPageTextNarration()));
				}
			}
		}
		
		private void hidePrompter()
		{
			if(_prompterShowing)
			{
				_prompterShowing = false;
				
//				if(_scrollView != null)
//				{
//					_scrollView.setVisibility(View.INVISIBLE);
//				}
	
				if(_textviewPrompter != null)
				{
					_textviewPrompter.setText("");
	
					_textviewPrompter.setVisibility(View.INVISIBLE);
				}
			}
		}
		

		private boolean handleTouch()
		{
			if((_isPlaying) || (_isRecording))
				return true;
			
			//disable all buttons
//			_recordButton.setEnabled(false);
			_playButton.setEnabled(false);
			
			if(isXLargeScreen())
			{
//				_recordButton.setBackgroundResource(R.drawable.recorddisabledlarge);
				_recordButton.setBackgroundResource(R.drawable.recordlarge);
				_playButton.setBackgroundResource(R.drawable.playdisabledlarge);
			}
			else
			{
//				_recordButton.setBackgroundResource(R.drawable.recorddisabled);
				_recordButton.setBackgroundResource(R.drawable.record);
				_playButton.setBackgroundResource(R.drawable.playdisabled);
			}

			//hide the narration text...
			hidePrompter();
			
			return false;

		}

	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audionarrationsetupmain);
        
		_profileName = getIntent().getStringExtra("com.craftysoft.flutterbyandmarguerite.flutterbyprofile");
        
        _gallery = (MyGallery) findViewById(R.id.gallery);    
        
        _gallery.setAdapter(new ImageAdapter(this));    
        _gallery.setOnItemClickListener(new OnItemClickListener() 
        {        
        	public void onItemClick(AdapterView<?> parent, View v, int position, long id) 
        	{                		
        		_isPlaying = false;
        		_isRecording = false;
//        		
    			_currentPage = position + 1;//adjust for array index position
//
//				_recordButton.setEnabled(true);
//				
//				if(isXLargeScreen())				
//					_recordButton.setBackgroundResource(R.drawable.recordlarge);
//				else
//					_recordButton.setBackgroundResource(R.drawable.record);
//				
//				showPrompter();
//    			
				initalizePlayButton();
        	}    
        });
      	_gallery.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {

				return handleTouch();
				
			}});
        _gallery.setCallbackDuringFling(false);
        _gallery.setOnItemSelectedListener(new OnItemSelectedListener() 
        {        
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {

    			_currentPage = arg2 + 1;

    			initalizePlayButton();
    		}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}    
        });
        
        initializeUI();
	}
	
	
	@Override
	public void onStart()
	{
		super.onStart();
	
//		if(_displayDialog)
//		{
//			_displayDialog = false;
			
//			NarrationProfileDialog nd = new NarrationProfileDialog(this);
//			nd.show();
//		}		
	}

	public void onResume()
	{
		super.onResume();

		FlutterbyAndMarguerite.unPauseBackgroundMusic();
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		
		//stop recording or playback
		if(_isRecording)
			setRecordingStopped();

		if(_isPlaying)//we want to stop playback
			setPlaybackStopped();				
		
		FlutterbyAndMarguerite.pauseBackgroundMusic();		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// Handle the back button
		if (keyCode == KeyEvent.KEYCODE_BACK)
			FlutterbyAndMarguerite.optionsListener.playRandomButtonSoundEffect();

		return super.onKeyDown(keyCode, event);
	}

	
	@Override
	public void onDestroy()
	{
		super.onDestroy();

		_gallery = null;
		
		System.gc();
	}
	
	private void initializeUI()
	{
		_textViewPlay = (TextView)findViewById(R.id.textviewPlay);
		_textViewRecord = (TextView)findViewById(R.id.textviewRecord);
		
        _textviewPrompter = (TextView)findViewById(R.id.textViewPrompter);
        _textviewPrompter.setMovementMethod(ScrollingMovementMethod.getInstance());
//        _textviewPrompter.setOnTouchListener(new OnTouchListener(){
//
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				boolean ret = handleTouch();
//				
//				initalizePlayButton();
//				return ret;
//			}});
        
       // _scrollView = (MyScrollView)findViewById(R.id.textViewPrompterScroll);			 	 
		
        _recordButton = (ImageButton)findViewById(R.id.imageButtonRecord);
        _recordButton.setOnClickListener(recordButtonListener);
        
        _playButton = (ImageButton)findViewById(R.id.imageButtonPlay);
        _playButton.setOnClickListener(playButtonListener);
                
        _closeButton = (Button)findViewById(R.id.ButtonCloseAnMain);
        _closeButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				
				//FlutterbyAndMarguerite.optionsListener.playRandomMasterSoundEffect();
				
				//haptic
				FlutterbyAndMarguerite.optionsListener.vibratePhoneId(Launcher.STRONG_CLICK_66);

				//stop playing or and recording
				setRecordingStopped();
				setPlaybackStopped();
				
				AudioNarrationSetup.this.finish();
			}	
        });
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog)
	{
		super.onPrepareDialog(id, dialog);
		
	}
	
	//will be called for each initial dialog creation
	@Override
	protected Dialog onCreateDialog(int id)
	{
//		currAlertDialog = null;
//		
//		currGameClockTime = tvGameClock.getText().toString();
//		
//		switch(id)
//		{
//			case SHOT:
//				currAlertDialog = createShotDialog();
//			break;
//			case GROUNDBALL: 
//				currAlertDialog = createGroundballDialog();
//			break;
//			case TURNOVER:
//				currAlertDialog = createTurnoverDialog();
//			break;
//			case FACEOFF:
//				currAlertDialog = createFaceoffDialog();
//			break;
//			case PENALTY:
//				currAlertDialog = createPenaltyDialog();
//			break;
//			case STATSUMMARY:
//				currAlertDialog = createStatSummaryDialog();
//			break;
//		}
//		
		return null;	
	}

	
	public class ImageAdapter extends BaseAdapter 
	{    
		int mGalleryItemBackground;    
		private Context mContext;    

		public ImageAdapter(Context c) 
		{        
			mContext = c;        
			TypedArray a = obtainStyledAttributes(R.styleable.thumbnailPageGallery);        
			
			mGalleryItemBackground = a.getResourceId(R.styleable.thumbnailPageGallery_android_galleryItemBackground, 0);        
			a.recycle();    
		}    
		
		public int getCount() 
		{        
			return FlutterbyAndMarguerite.TOTALPAGECOUNT;    
		}    

		private boolean isXLargeScreen()
		{
			return ((getResources().getConfiguration().screenLayout 
	 				& Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE);
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override		
		public View getView(int position, View convertView, ViewGroup parent) 
		{        
			ImageView i = new ImageView(mContext);      
			
			if(isXLargeScreen())
				i.setImageResource(mContext.getResources().getIdentifier("p" + ++position, "drawable", mContext.getPackageName()));        
			else
				i.setImageResource(mContext.getResources().getIdentifier("p" + ++position + "thumb", "drawable", mContext.getPackageName()));


			//			i.setLayoutParams(new Gallery.LayoutParams(150, 100));        
			i.setScaleType(ImageView.ScaleType.FIT_XY);        
			i.setBackgroundResource(mGalleryItemBackground);        
			return i;    	
		}		
	}
}