package com.craftysoft.flutterbyandmarguerite;

import java.util.HashMap;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class SoundPoolManager extends SoundPool{
	
	private HashMap<Integer, Integer> _soundIdMap = new HashMap<Integer, Integer>();
	
	public SoundPoolManager()
	{
		super(1, AudioManager.STREAM_MUSIC, 0);		
	}
	
	public void playSound(int resourceId)
	{
		try
		{
			play(_soundIdMap.get(resourceId), 0.8f, 0.8f, 1, 0, 1.0f);	
		}
		catch(Exception ex)
		{
			//do nothing
		}
	}
	  
	public void stopSound(int resourceId)
	{
		try
		{
			stop(_soundIdMap.get(resourceId));
		}
		catch(Exception ex)
		{
			//do nothing
		}
	}
	  
	public void unloadSound(int resourceId)
	{
		unload(_soundIdMap.get(resourceId));  
		_soundIdMap.remove(resourceId);  
	}
	  
	public void loadSound(int resourceId, Context context)
	{
		int soundId = this.load(context, resourceId, 1); 
		_soundIdMap.put(resourceId, soundId);
	}
	
	public void delete()
	{
		release();
		_soundIdMap.clear();
		_soundIdMap = null;
	}
}