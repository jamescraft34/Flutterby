package com.craftysoft.flutterbyandmarguerite;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.craftysoft.flutterbyandmarguerite.pro.R;
import com.immersion.uhl.Launcher;

public class CharacterAlias extends Activity {

	private Button _closeButton = null;
	private CheckBox _checkboxFlutterby = null;
	private CheckBox _checkboxMarguerite = null;
	private RadioGroup _radioGroupGenderFlutterby = null;
	private RadioGroup _radioGroupGenderMarguerite = null;
	private RadioButton _radioButtonGirlFlutterby = null;
	private RadioButton _radioButtonBoyFlutterby = null;
	private RadioButton _radioButtonGirlMarguerite = null;
	private RadioButton _radioButtonBoyMarguerite = null;	
	private EditText _editTextFlutterby = null;
	private EditText _editTextMarguerite = null;

	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
        setContentView(R.layout.characteralias);

        initializeUI();
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
		
		FlutterbyAndMarguerite.pauseBackgroundMusic();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// Handle the back button
		if (keyCode == KeyEvent.KEYCODE_BACK)
			FlutterbyAndMarguerite.optionsListener.playRandomButtonSoundEffect();

		return super.onKeyDown(keyCode, event);
	}
		
	private void initializeUI()
	{
        _closeButton = (Button)findViewById(R.id.ButtonClose);
        _closeButton.setOnClickListener(closeListener);   

        boolean checkboxFlutterby = getIntent().getBooleanExtra("com.craftysoft.flutterbyandmarguerite.flutterbyAliasEnabled", false);
        _checkboxFlutterby = (CheckBox)findViewById(R.id.CheckBoxFlutterby);
        _checkboxFlutterby.setChecked(checkboxFlutterby);
        _checkboxFlutterby.setOnCheckedChangeListener(flutterbyOnCheckedChangedListener);
                
        boolean checkboxMarguerite = getIntent().getBooleanExtra("com.craftysoft.flutterbyandmarguerite.margueriteAliasEnabled", false);
        _checkboxMarguerite = (CheckBox)findViewById(R.id.CheckBoxMarquerite);
        _checkboxMarguerite.setChecked(checkboxMarguerite);
        _checkboxMarguerite.setOnCheckedChangeListener(marqueriteOnCheckedChangedListener);
	
    	_radioGroupGenderFlutterby = (RadioGroup)findViewById(R.id.RadioGroupFlutterbyGender);    	
    	_radioGroupGenderMarguerite = (RadioGroup)findViewById(R.id.RadioGroupMargueriteGender);


    	_radioButtonGirlFlutterby = (RadioButton)findViewById(R.id.RadioButtonFlutterbyGirl);
    	_radioButtonGirlFlutterby.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				FlutterbyAndMarguerite.optionsListener.vibratePhoneId(Launcher.STRONG_CLICK_66);
			}});    	

    	_radioButtonBoyFlutterby = (RadioButton)findViewById(R.id.RadioButtonFlutterbyBoy);
    	_radioButtonBoyFlutterby.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				FlutterbyAndMarguerite.optionsListener.vibratePhoneId(Launcher.STRONG_CLICK_66);
			}});    	

    	_radioButtonGirlMarguerite = (RadioButton)findViewById(R.id.RadioButtonMargueriteGirl);
    	_radioButtonGirlMarguerite.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				FlutterbyAndMarguerite.optionsListener.vibratePhoneId(Launcher.STRONG_CLICK_66);
			}});    	

    	_radioButtonBoyMarguerite = (RadioButton)findViewById(R.id.RadioButtonMargueriteBoy);	
    	_radioButtonBoyMarguerite.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				FlutterbyAndMarguerite.optionsListener.vibratePhoneId(Launcher.STRONG_CLICK_66);
			}});    	

    	
    	boolean flutterbyGirl = getIntent().getBooleanExtra("com.craftysoft.flutterbyandmarguerite.flutterbyFemale", true);
    	if(flutterbyGirl)
    		_radioButtonGirlFlutterby.setChecked(true);
    	else
    		_radioButtonBoyFlutterby.setChecked(true);
    	
    	boolean margueriteGirl = getIntent().getBooleanExtra("com.craftysoft.flutterbyandmarguerite.margueriteFemale", true);
    	if(margueriteGirl)
    		_radioButtonGirlMarguerite.setChecked(true);
    	else
    		_radioButtonBoyMarguerite.setChecked(true);
    	
    	_editTextFlutterby = (EditText)findViewById(R.id.EditTextFlutterbyAlias);   
    	_editTextFlutterby.setText(getIntent().getStringExtra("com.craftysoft.flutterbyandmarguerite.flutterbyAlias"));
    	
    	_editTextMarguerite = (EditText)findViewById(R.id.EditTextMargueriteAlias);
    	_editTextMarguerite.setText(getIntent().getStringExtra("com.craftysoft.flutterbyandmarguerite.margueriteAlias"));
    		
    	setEnabledStates(_editTextFlutterby, _radioGroupGenderFlutterby, checkboxFlutterby);
    	setEnabledStates(_editTextMarguerite, _radioGroupGenderMarguerite, checkboxMarguerite);
	
	}
	
	//itterates over a radiogroup and enables or disables each item in the group
	private void changeRadioGroupState(RadioGroup radioGroup, boolean state)
	{
		for(int i = 0; i < radioGroup.getChildCount(); i++)
		{
			radioGroup.getChildAt(i).setEnabled(state);
			radioGroup.getChildAt(i).setFocusable(state);
		}
	}
	
	//sets the enabled states of the radio and editText boxes
	private void setEnabledStates(EditText editText, RadioGroup radioGroup, boolean state)
	{
		changeRadioGroupState(radioGroup, state);

		editText.setEnabled(state);
		editText.setFocusable(state);
		editText.setFocusableInTouchMode(state);//call this too bc of bug in api
	}
	
	private OnCheckedChangeListener flutterbyOnCheckedChangedListener = new OnCheckedChangeListener()
	{
		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
			//haptic
			FlutterbyAndMarguerite.optionsListener.vibratePhoneId(Launcher.STRONG_CLICK_66);

			setEnabledStates(_editTextFlutterby, _radioGroupGenderFlutterby, arg1);
		}
	};
	
	private OnCheckedChangeListener marqueriteOnCheckedChangedListener = new OnCheckedChangeListener()
	{
		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
			//haptic
			FlutterbyAndMarguerite.optionsListener.vibratePhoneId(Launcher.STRONG_CLICK_66);

			setEnabledStates(_editTextMarguerite, _radioGroupGenderMarguerite, arg1);
		}
	};
	
	private OnClickListener closeListener = new OnClickListener()
	{
		@Override
		public void onClick(View v) 
		{				
			//haptic
			FlutterbyAndMarguerite.optionsListener.vibratePhoneId(Launcher.STRONG_CLICK_66);

			//FlutterbyAndMarguerite.optionsListener.playRandomMasterSoundEffect();
			
    		Intent intent = new Intent();
			intent.putExtra("com.craftysoft.flutterbyandmarguerite.flutterbyAlias", _editTextFlutterby.getText().toString()); 
			intent.putExtra("com.craftysoft.flutterbyandmarguerite.margueriteAlias", _editTextMarguerite.getText().toString()); 
			intent.putExtra("com.craftysoft.flutterbyandmarguerite.flutterbyFemale", _radioButtonGirlFlutterby.isChecked()); 
			intent.putExtra("com.craftysoft.flutterbyandmarguerite.margueriteFemale", _radioButtonGirlMarguerite.isChecked()); 
			intent.putExtra("com.craftysoft.flutterbyandmarguerite.flutterbyAliasEnabled", _checkboxFlutterby.isChecked()); 
			intent.putExtra("com.craftysoft.flutterbyandmarguerite.margueriteAliasEnabled", _checkboxMarguerite.isChecked()); 

    		CharacterAlias.this.setResult(RESULT_OK, intent);
    		
			CharacterAlias.this.finish();
		}	
    };    
}