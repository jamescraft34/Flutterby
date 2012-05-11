package com.craftysoft.flutterbyandmarguerite;

import java.io.IOException;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;

public class NarrationMediaPlayer {

	private MediaPlayer _mediaPlayer = null;
	private Context _context = null;
	
	private boolean _soundEffectsPlayable = true;
	
	private boolean _narrationComplete = false;
	
	private static NarrationMediaPlayer _narrationMediaPlayer = null;
	
	private NarrationMediaPlayer(Context context)
	{
		if(_context == null)
			_context = context;

	}
	
	public boolean canPlaySoundEffects()
	{
		return _soundEffectsPlayable;
	}
	
	public void setPlaySoundEffects(boolean canPlay)
	{
		_soundEffectsPlayable = canPlay;
	}
		
	public static NarrationMediaPlayer getInstance(Context context)
	{
		if(_narrationMediaPlayer == null)
			_narrationMediaPlayer = new NarrationMediaPlayer(context);
		
		return _narrationMediaPlayer;
	}
	
	private void playNarration(final long playDelay)
	{		
		//comment this out when wanting to play sound effects during narration
//		_soundEffectsPlayable = false;
		
		_duration = _mediaPlayer.getDuration();//hack

		
		
		_mediaPlayer.setVolume(1.0f, 1.0f);
		_mediaPlayer.setOnPreparedListener(new OnPreparedListener(){

		@Override
		public void onPrepared(MediaPlayer arg0) {
					
					//give the page a second to display before playing narration audio
					new Handler().postDelayed(new Runnable() 
					{           
						public void run() 
						{   
							if(_mediaPlayer != null)
							{
								try
								{
									//notify that the narration is about to play
									//so we can attempt to karaoke
									CurlActivity.narrationMediaPlayerListener.narrationIsPrepared();
									
									_narrationComplete = false;
									_mediaPlayer.start();
								}
								catch(Exception ex)
								{
									//do nothing
								}
							}
						}      						
					}, playDelay);  
				}
			});
			
			_mediaPlayer.setOnCompletionListener(new OnCompletionListener(){

				@Override
				public void onCompletion(MediaPlayer arg0) {
					_soundEffectsPlayable = true;	
					_narrationComplete = true;
				}
			});			
	}

	private int _duration = 0;
	public void playNarrationAudio(String fileName, final long playDelay)
	{				
		try 
		{
			_mediaPlayer = new MediaPlayer();	
			_mediaPlayer.setDataSource(fileName);			
			_mediaPlayer.prepare();
			
			playNarration(playDelay);
		} 
		catch (IllegalArgumentException e) {
			_mediaPlayer = null;
		} 
		catch (IllegalStateException e) {
			_mediaPlayer = null;
		} 
		catch (IOException e) {
			_mediaPlayer = null;
		}
	}
	
	public void playNarrationAudio(int audioNarrationId, final long playDelay)
	{				
		try {
			_mediaPlayer = MediaPlayer.create(_context, audioNarrationId);

			playNarration(playDelay);
		} 
		catch (Exception e) 
		{
			_mediaPlayer = null;
		}
	}
	
	public int getDuration()
	{
		return _duration;
	}
	
	
	public void stopPlayingNarrationAudio()
	{		
		if(_mediaPlayer != null)
		{
			try
			{
				_mediaPlayer.stop();
				_mediaPlayer.release();
				_mediaPlayer = null;
			}
			catch(Exception ex)
			{
				//do nothing
			}
		}
		
		_soundEffectsPlayable = true;	
		
//		if(_soundPoolMgr != null)
//		{
//			_soundPoolMgr.autoPause();
//		}
	}

	public boolean is_narrationComplete() {
		return _narrationComplete;
	}

	public void set_narrationComplete(boolean narrationComplete) {
		_narrationComplete = narrationComplete;
	}	
}
