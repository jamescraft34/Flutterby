package com.craftysoft.flutterbyandmarguerite;

import com.immersion.uhl.Launcher;

public class SoundEffect {

	private long[] _vibratePattern = null;//the vibrate pattern to play when the sound effect is played
	private int _vibrateId = Launcher.BOUNCE_66;//DEFAULT SOUND (MAY CHANGE??)
	private byte[] _vibrateCustom = null;
	
	public byte[] get_vibrateCustom() {
		return _vibrateCustom;
	}

	public void set_vibrateCustom(byte[] vibrateCustom) {
		_vibrateCustom = vibrateCustom;
	}

	private int _soundEffectId;
	private String _soundEffectColorCode;
	
//	private long[] _defaultVibration = new long[]{0,50};

	public SoundEffect(int soundEffectId, String soundEffectColorCode) {
		_soundEffectId = soundEffectId;
		_soundEffectColorCode = soundEffectColorCode;

		//_vibratePattern = _defaultVibration;//for sound effects that dont have vibration add just a touch of vibration for effect.
	}
	
	public SoundEffect(int soundEffectId, String soundEffectColorCode, long[] vibratePattern) {
		_vibratePattern = vibratePattern;
		_soundEffectId = soundEffectId;
		_soundEffectColorCode = soundEffectColorCode;
	}
	
	public SoundEffect(int soundEffectId, String soundEffectColorCode, byte[] vibrateCustom) {
		_vibrateCustom = vibrateCustom;
		_soundEffectId = soundEffectId;
		_soundEffectColorCode = soundEffectColorCode;
	}

	public SoundEffect(int soundEffectId, String soundEffectColorCode, int vibrateId) {
		_vibrateId = vibrateId;
		_soundEffectId = soundEffectId;
		_soundEffectColorCode = soundEffectColorCode;
	}
	
	public int get_vibrateId() {
		return _vibrateId;
	}
	public void set_vibrateId(int vibrateId) {
		_vibrateId = vibrateId;
	}
			
	public long[] get_vibratePattern() {
		return _vibratePattern;
	}
	public void set_vibratePattern(long[] vibratePattern) {
		_vibratePattern = vibratePattern;
	}
	
	public int get_soundEffectId() {
		return _soundEffectId;
	}
	public void set_soundEffectId(int soundEffectId) {
		_soundEffectId = soundEffectId;
	}

	public String get_soundEffectColorCode() {
		return _soundEffectColorCode;
	}

	public void set_soundEffectColorCode(String soundEffectColorCode) {
		_soundEffectColorCode = soundEffectColorCode;
	}
}