package com.craftysoft.flutterbyandmarguerite;

import java.util.Random;

import android.content.Context;

import com.craftysoft.flutterbyandmarguerite.pro.R;

//singleton class
public class MasterSoundEffects {
	
	private static MasterSoundEffects _instance = null;
	private SoundPoolManager _soundPoolMgr = null;
		
	private int[] _buttonSoundEffectIds = {R.raw.chime1, R.raw.chime2, R.raw.chime3, R.raw.chime4};
	private int[] _navigationSoundEffectIds = {R.raw.harp2};
	
	private int _titlePageSoundEffectId = R.raw.chime2;//TODO: REPLACE WITH MARICA RECORDING
	
	private MasterSoundEffects(Context context)
	{	
		_soundPoolMgr = new SoundPoolManager();
		
//		_soundPoolMgr.loadSound(R.raw.pageflipping, context);//load page flip sound
        
        //load master button sound effects
        for(int id : _buttonSoundEffectIds)
        {
            _soundPoolMgr.loadSound(id, context);
        }
        
        //load title page sound effect
        _soundPoolMgr.loadSound(_titlePageSoundEffectId, context);
        
        //load navigation sound effects
        for(int id : _navigationSoundEffectIds)
        {
            _soundPoolMgr.loadSound(id, context);
        }
	}
	
	public void delete()
	{
		_soundPoolMgr.delete();
		_soundPoolMgr = null;
		
		_instance = null;
	}
		
	public static MasterSoundEffects getInstance(Context context) 
	{ 
		 if(_instance == null)
			 _instance = new MasterSoundEffects(context);
		 
		 return _instance;
	}
	
	public void playRandomButtonSoundEffect()
	{
		_soundPoolMgr.playSound(_buttonSoundEffectIds[getRandomEffectId(_buttonSoundEffectIds.length)]);
	}
	
	public void playRandomNavigationSoundEffect()
	{
		_soundPoolMgr.playSound(_navigationSoundEffectIds[getRandomEffectId(_navigationSoundEffectIds.length)]);
	}
	
	public void playPageFlippingSoundEffect()
	{
//		_soundPoolMgr.playSound(R.raw.pageflipping);
	}
	
	public void playQuickChimeSound()
	{
		_soundPoolMgr.playSound(R.raw.chime3);
	}
	
	public void playTitleSound()
	{
		_soundPoolMgr.playSound(_titlePageSoundEffectId);
	}

	private int getRandomEffectId(int effectsArrayLength)
	{
		Random generator = new Random();

		return generator.nextInt(effectsArrayLength);
	}	
}
