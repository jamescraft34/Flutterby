package com.craftysoft.flutterbyandmarguerite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.craftysoft.flutterbyandmarguerite.pro.R;
import com.immersion.uhl.Launcher;

public class OptionMenu extends Activity {
	
	private final int CHARACTER_ALIAS_CODE = 35;
	
	private final int MAX_NARRATION_PROFILES = 3;
	
	private Button _closeButton = null;
	private Button _narrationTextButton = null;
	private Button _textSizeButton = null;
	private Button _characterAliasButton = null;
	private Button _indexPageButton = null;
	private Button _narrationAudioButton = null;
	private Button _backgroundSoundButton = null;
	private Button _narrationRecordButton = null;
	private Button _aboutButton = null;

	private boolean _isNarrationTextDisplayed;
	private boolean _isNarrationTextLarge;
	private boolean _isNarrationAudioOn;
	private boolean _isbackgroundMusicOn;
	private boolean _hasMicrophone;
	
	private View _rootLayoutView = null;
	
	//character alias info, will be saved in the intent, save in case the user clicks on character alias
	private String _flutterbyAliasName = "";
	private String _margueriteAliasName = "";
	private boolean _flutterbyFemale = true;
	private boolean _margueriteFemale = true;
	private boolean _flutterbyAliasEnabled = true;
	private boolean _margueriteAliasEnabled = true;	
	
	private boolean _fromHome = false;
	
	private boolean isXLargeScreen()
	{
		return ((getResources().getConfiguration().screenLayout 
 				& Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE);
	}
	
	private boolean isLargeScreen()
	{
		return ((getResources().getConfiguration().screenLayout 
 				& Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// Handle the back button
		if (keyCode == KeyEvent.KEYCODE_BACK)
			FlutterbyAndMarguerite.optionsListener.playRandomButtonSoundEffect();

		return super.onKeyDown(keyCode, event);
	}

	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
        setContentView(R.layout.optionmenu);

        initializeUI();

        //save character alias info in case we need it later
    	_flutterbyAliasName = getIntent().getStringExtra("com.craftysoft.flutterbyandmarguerite.flutterbyAlias");
    	_margueriteAliasName = getIntent().getStringExtra("com.craftysoft.flutterbyandmarguerite.margueriteAlias");
    	_flutterbyFemale = getIntent().getBooleanExtra("com.craftysoft.flutterbyandmarguerite.flutterbyFemale", true);
    	_margueriteFemale = getIntent().getBooleanExtra("com.craftysoft.flutterbyandmarguerite.margueriteFemale", true); 
    	_flutterbyAliasEnabled = getIntent().getBooleanExtra("com.craftysoft.flutterbyandmarguerite.flutterbyAliasEnabled", false); 
    	_margueriteAliasEnabled = getIntent().getBooleanExtra("com.craftysoft.flutterbyandmarguerite.margueriteAliasEnabled", false);	
    	
    	_fromHome = getIntent().getBooleanExtra("com.craftysoft.flutterbyandmarguerite.fromHome", false);
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		showUIControls(false);	
		
		FlutterbyAndMarguerite.pauseBackgroundMusic();
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		showUIControls(true);	

		
		FlutterbyAndMarguerite.unPauseBackgroundMusic();
	}
	
	
	private void initializeUI()
	{
        _closeButton = (Button)findViewById(R.id.ButtonCloseOMMain);
        _closeButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				//FlutterbyAndMarguerite.optionsListener.playRandomMasterSoundEffect();
				
				//haptic
				FlutterbyAndMarguerite.optionsListener.vibratePhoneId(Launcher.STRONG_CLICK_66);

				
				OptionMenu.this.finish();
			}	
        });
                
        //SETUP BUTTONS        
        _narrationTextButton = (Button)findViewById(R.id.ButtonNarrationTextOnOff);
        _narrationTextButton.setOnClickListener(narrationTextButtonListener);

    	_isNarrationTextDisplayed = getIntent().getExtras().getBoolean("com.craftysoft.flutterbyandmarguerite.textNar");
        //use default background resource unless...
        if(!_isNarrationTextDisplayed)
        {
        	if(isXLargeScreen())
        		_narrationTextButton.setBackgroundResource(R.drawable.textofflarge);
        	else
        		_narrationTextButton.setBackgroundResource(R.drawable.textoff);
        
        	TextView tv = (TextView)findViewById(R.id.TextViewNarrationTextOnOff);
        	tv.setText(R.string.narrationTextOff);
        }
        
        _textSizeButton = (Button)findViewById(R.id.ButtonTextZoom);
        _textSizeButton.setOnClickListener(textSizeButtonListener);
 
       	_isNarrationTextLarge = getIntent().getExtras().getBoolean("com.craftysoft.flutterbyandmarguerite.textLarge"); 
        //use default background resource unless...
        if(_isNarrationTextLarge)
        {
        	if(isXLargeScreen())
        		_textSizeButton.setBackgroundResource(R.drawable.zoomoutlarge);
        	else
            	_textSizeButton.setBackgroundResource(R.drawable.zoomout);
        }

        _characterAliasButton = (Button)findViewById(R.id.ButtonAlias);
        _characterAliasButton.setOnClickListener(characterAliasButtonListener);
        
        _indexPageButton = (Button)findViewById(R.id.ButtonHome);
        _indexPageButton.setOnClickListener(indexPageButtonListener);
        
        _narrationAudioButton = (Button)findViewById(R.id.ButtonNarrationAudioOnOff);
        _narrationAudioButton.setOnClickListener(narrationAudioButtonListener);
        
    	_isNarrationAudioOn = getIntent().getExtras().getBoolean("com.craftysoft.flutterbyandmarguerite.audioNar");
        //use default background resource unless...
        if(!_isNarrationAudioOn)
        {
        	if(isXLargeScreen())
        		_narrationAudioButton.setBackgroundResource(R.drawable.speechofflarge);
        	else
        		_narrationAudioButton.setBackgroundResource(R.drawable.speechoff);
        	
        	TextView tv = (TextView)findViewById(R.id.TextViewNarrationAudioOnOff);
        	tv.setText(R.string.narrationAudioOff);
        }

        _backgroundSoundButton = (Button)findViewById(R.id.ButtonMusic);
        _backgroundSoundButton.setOnClickListener(backgroundSoundButtonListener);

    	_isbackgroundMusicOn = getIntent().getExtras().getBoolean("com.craftysoft.flutterbyandmarguerite.backAudio");
        //use default background resource unless...
        if(!_isbackgroundMusicOn)
        {
        	if(isXLargeScreen())
            	_backgroundSoundButton.setBackgroundResource(R.drawable.soundofflarge);
        	else
        		_backgroundSoundButton.setBackgroundResource(R.drawable.soundoff);

        	TextView tv = (TextView)findViewById(R.id.TextViewBackgroundAudioOnOff);
        	tv.setText(R.string.backgroundAudioOff);
        }

        _narrationRecordButton = (Button)findViewById(R.id.ButtonNarrationRecord);
        _narrationRecordButton.setOnClickListener(narrationRecordButtonListener);

    	_hasMicrophone = getIntent().getExtras().getBoolean("com.craftysoft.flutterbyandmarguerite.hasMic");
    	if(!_hasMicrophone)
    	{
        	if(isXLargeScreen())    		
        		_narrationRecordButton.setBackgroundResource(R.drawable.microphoneofflarge);
        	else
        		_narrationRecordButton.setBackgroundResource(R.drawable.microphoneoff);
        	
        	TextView tv = (TextView)findViewById(R.id.TextViewNarrationDisabled);
        	tv.setVisibility(View.VISIBLE);
    	}

        _aboutButton = (Button)findViewById(R.id.ButtonAbout);
        _aboutButton.setOnClickListener(aboutButtonListener);
	}
	
	//show/hide all controls in layout so they do not show through when other menus
	//overlap
	private final void showUIControls(boolean showControls)
	{
		_rootLayoutView = findViewById(R.id.root);
		if(showControls)
			_rootLayoutView.setVisibility(View.VISIBLE);
		else
			_rootLayoutView.setVisibility(View.INVISIBLE);
	}
	
	
	//***************Listeners**********************

	private OnClickListener narrationTextButtonListener = new OnClickListener()
	{
		@Override
		public void onClick(View arg0) 
		{
			if(_isNarrationTextDisplayed)
			{
	        	if(isXLargeScreen())
	        		_narrationTextButton.setBackgroundResource(R.drawable.textofflarge);
	        	else
	        		_narrationTextButton.setBackgroundResource(R.drawable.textoff);
	        	
	        	TextView tv = (TextView)findViewById(R.id.TextViewNarrationTextOnOff);
	        	tv.setText(R.string.narrationTextOff);

	        	_isNarrationTextDisplayed = false;
			}
	        else
	        {
	        	if(isXLargeScreen())
	        		_narrationTextButton.setBackgroundResource(R.drawable.textlarge);
	        	else
	        		_narrationTextButton.setBackgroundResource(R.drawable.text);
	        	
	        	TextView tv = (TextView)findViewById(R.id.TextViewNarrationTextOnOff);
	        	tv.setText(R.string.narrationTextOn);

	        	_isNarrationTextDisplayed = true;
	        }
			
			FlutterbyAndMarguerite.optionsListener.playRandomButtonSoundEffect();
			FlutterbyAndMarguerite.optionsListener.toggleTextNarration();
		}	
	};
	
	private OnClickListener textSizeButtonListener = new OnClickListener()
	{
		@Override
		public void onClick(View arg0) 
		{
			if(_isNarrationTextLarge)
			{
	        	if(isXLargeScreen())
	        		_textSizeButton.setBackgroundResource(R.drawable.zoominlarge);
	        	else
					_textSizeButton.setBackgroundResource(R.drawable.zoomin);
				_isNarrationTextLarge = false;
			}
			else
			{
	        	if(isXLargeScreen())
	        		_textSizeButton.setBackgroundResource(R.drawable.zoomoutlarge);
	        	else
					_textSizeButton.setBackgroundResource(R.drawable.zoomout);
				_isNarrationTextLarge = true;
			}
			
			FlutterbyAndMarguerite.optionsListener.playRandomButtonSoundEffect();
			FlutterbyAndMarguerite.optionsListener.toggleTextSize();
		}	
	};
	
	private void editNarrationProfile(String profileName)
	{
		Intent intent = new Intent();
		intent.setClass(OptionMenu.this, AudioNarrationSetup.class);
		intent.putExtra("com.craftysoft.flutterbyandmarguerite.flutterbyprofile", profileName);
		OptionMenu.this.startActivity(intent);
		
	}
	
	private OnClickListener characterAliasButtonListener = new OnClickListener()
	{
		@Override
		public void onClick(View arg0) 
		{
			Intent intent = new Intent();
			intent.setClass(OptionMenu.this, CharacterAlias.class);
			
			intent.putExtra("com.craftysoft.flutterbyandmarguerite.flutterbyAlias", _flutterbyAliasName); 
			intent.putExtra("com.craftysoft.flutterbyandmarguerite.margueriteAlias", _margueriteAliasName); 
			intent.putExtra("com.craftysoft.flutterbyandmarguerite.flutterbyFemale", _flutterbyFemale); 
			intent.putExtra("com.craftysoft.flutterbyandmarguerite.margueriteFemale", _margueriteFemale); 
			intent.putExtra("com.craftysoft.flutterbyandmarguerite.flutterbyAliasEnabled", _flutterbyAliasEnabled); 
			intent.putExtra("com.craftysoft.flutterbyandmarguerite.margueriteAliasEnabled", _margueriteAliasEnabled); 
			
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

			FlutterbyAndMarguerite.optionsListener.playRandomButtonSoundEffect();
			OptionMenu.this.startActivityForResult(intent, CHARACTER_ALIAS_CODE);
		}	
	};
	
	@Override 
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch(requestCode)
		{
			case CHARACTER_ALIAS_CODE:
				if(resultCode == RESULT_OK)
				{
					if(data != null)
					{
						_flutterbyAliasEnabled = data.getBooleanExtra("com.craftysoft.flutterbyandmarguerite.flutterbyAliasEnabled", false);
						_margueriteAliasEnabled = data.getBooleanExtra("com.craftysoft.flutterbyandmarguerite.margueriteAliasEnabled", false);
				        _flutterbyFemale = data.getBooleanExtra("com.craftysoft.flutterbyandmarguerite.flutterbyFemale", true);
				        _margueriteFemale = data.getBooleanExtra("com.craftysoft.flutterbyandmarguerite.margueriteFemale", true);
				    	_flutterbyAliasName = data.getStringExtra("com.craftysoft.flutterbyandmarguerite.flutterbyAlias");				    	
				    	_margueriteAliasName = data.getStringExtra("com.craftysoft.flutterbyandmarguerite.margueriteAlias");

						//make the flutterby call here instead of from character alias, use the return variables
						FlutterbyAndMarguerite.optionsListener.saveCharacterAliasInfo(_flutterbyAliasName, _flutterbyFemale,_flutterbyAliasEnabled, 
																						_margueriteAliasName, _margueriteFemale, _margueriteAliasEnabled);
					}
				}
				break;
		}
	}

	
	private OnClickListener indexPageButtonListener = new OnClickListener()
	{
		@Override
		public void onClick(View arg0) 
		{
			FlutterbyAndMarguerite.optionsListener.playRandomNavigationSoundEffect();
			
			FlutterbyAndMarguerite.optionsListener.stopVibrate();//just in case we are vibrating
			
			//haptic
			FlutterbyAndMarguerite.optionsListener.vibratePhoneId(Launcher.STRONG_CLICK_66);


			if(!_fromHome)
			{
				FlutterbyAndMarguerite.startBackgroundMusic(OptionMenu.this, 0);
				
				Intent intent = new Intent(OptionMenu.this, FlutterbyAndMarguerite.class).addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);//.FLAG_ACTIVITY_CLEAR_TOP);
	
				OptionMenu.this.startActivity(intent);
			}
				
			OptionMenu.this.finish();			
		}	
	};
	
	private OnClickListener narrationAudioButtonListener = new OnClickListener()
	{
		@Override
		public void onClick(View arg0) 
		{
			if(_isNarrationAudioOn)
			{
	        	if(isXLargeScreen())
	        		_narrationAudioButton.setBackgroundResource(R.drawable.speechofflarge);
	        	else
	        		_narrationAudioButton.setBackgroundResource(R.drawable.speechoff);
	        	
	        	TextView tv = (TextView)findViewById(R.id.TextViewNarrationAudioOnOff);
	        	tv.setText(R.string.narrationAudioOff);

				_isNarrationAudioOn = false;
			}
			else
			{
	        	if(isXLargeScreen())
	        		_narrationAudioButton.setBackgroundResource(R.drawable.speechlarge);
	        	else
	        		_narrationAudioButton.setBackgroundResource(R.drawable.speech);
	        	
	        	TextView tv = (TextView)findViewById(R.id.TextViewNarrationAudioOnOff);
	        	tv.setText(R.string.narrationAudioOn);

				_isNarrationAudioOn = true;
			}

			FlutterbyAndMarguerite.optionsListener.playRandomButtonSoundEffect();
			FlutterbyAndMarguerite.optionsListener.toggleAudioNarration();
		}	
	};
	
	private OnClickListener backgroundSoundButtonListener = new OnClickListener()
	{
		@Override
		public void onClick(View arg0) 
		{
			if(_isbackgroundMusicOn)
			{
				if(isXLargeScreen())
					_backgroundSoundButton.setBackgroundResource(R.drawable.soundofflarge);
				else
					_backgroundSoundButton.setBackgroundResource(R.drawable.soundoff);
	        
				TextView tv = (TextView)findViewById(R.id.TextViewBackgroundAudioOnOff);
	        	tv.setText(R.string.backgroundAudioOff);

				_isbackgroundMusicOn = false;
			}
			else
			{
				if(isXLargeScreen())
					_backgroundSoundButton.setBackgroundResource(R.drawable.soundlarge);
				else
					_backgroundSoundButton.setBackgroundResource(R.drawable.sound);
				
				TextView tv = (TextView)findViewById(R.id.TextViewBackgroundAudioOnOff);
	        	tv.setText(R.string.backgroundAudioOn);
	        	
				_isbackgroundMusicOn = true;
			}
			
			FlutterbyAndMarguerite.optionsListener.playRandomButtonSoundEffect();
			FlutterbyAndMarguerite.optionsListener.toggleBackgroundSound();
		}	
	};	

	private OnClickListener narrationRecordButtonListener = new OnClickListener()
	{
		@Override
		public void onClick(View arg0) 
		{
	    	if(!_hasMicrophone)
	    		Toast.makeText(OptionMenu.this, R.string.nomic, Toast.LENGTH_SHORT).show();
	    	else
	    	{
	    		FlutterbyAndMarguerite.optionsListener.playRandomButtonSoundEffect();
	    		displayProfiles();
	    	}
		}	
	};
	
	private void displayProfiles()
	{
		try
		{
     	//if(AudioNarrationManager.isSdCardAvailable())
    	//{		
    		displayProfilesDialog();
    	//}
    	//else
    		//Toast.makeText(getApplicationContext(), R.string.sdCardNotAvailable, Toast.LENGTH_LONG).show();
		}
		catch(AudioManagerException ex)
		{
    		Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
		}
	}
	
	
	
	private AlertDialog ad = null;
	private int profileCount = 0;
	private ArrayList<String> items = null;
	
	private void displayProfilesDialog() throws AudioManagerException
	{
		try 
		{
			items = new ArrayList<String>();
			items.add(getResources().getString(R.string.createNarration));//always add the create item...			
			
			String[] profiles = null;
			
			profiles = AudioNarrationManager.getNarrationProfiles(getApplicationContext());
	
			profileCount = profiles.length;
			
			if(profiles != null)
				Collections.addAll(items, profiles);
						
			AlertDialog.Builder builder = new AlertDialog.Builder(OptionMenu.this);
			builder.setTitle("Select narration profile");
			
			ArrayAdapter<String> aa = new ArrayAdapter<String>(OptionMenu.this, R.layout.narrationrow, android.R.id.text1, items);
			aa.sort(new Comparator<String>() {
				public int compare(String object1, String object2) {
					return object1.compareTo(object2);
				};
			});
			
			aa.add(getResources().getString(R.string.defaultNarration));//always add the default item last...

			
			builder.setAdapter(aa, null);

//			builder.setAdapter(aa, new DialogInterface.OnClickListener()
//			{
//				@Override
//				public void onClick(DialogInterface dialog, int item) 
//				{   
//					handleNarrationDialogSelection(items.get(item), profileCount);
//				}
//			});

			builder.setNegativeButton("Cancel", null);
			//builder.show();
			ad = builder.create();

			LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
			View customTitle = inflater.inflate(R.layout.customnarrationprofiletitle, null);

			ad.setCustomTitle(customTitle);
			
			ad.getListView().setOnItemClickListener(new OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) 
				{	
					TextView tv = (TextView)arg1;
					String itemText = tv.getText().toString();

					if(handleNarrationDialogSelection(itemText, profileCount))
						ad.dismiss();					
				}});
			
			ad.getListView().setOnItemLongClickListener(new OnItemLongClickListener(){

				@Override
				public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) 
				{
					TextView tv = (TextView)arg1;
					final String itemText = tv.getText().toString();
					
					if( (!(itemText.equals(getResources().getString(R.string.createNarration)))) 
							&& (!(itemText.equals(getResources().getString(R.string.defaultNarration)))))
					{
						 new AlertDialog.Builder(OptionMenu.this)
			            	.setMessage("Are you sure you want to delete the profile '" + itemText + "'") 
			            	.setPositiveButton("Ok", new DialogInterface.OnClickListener() { 
				                @Override 
				                public void onClick(DialogInterface dialog, int which) { 
				        			AudioNarrationManager.deleteNarrationProfile(OptionMenu.this, itemText);

				        			//set to default flutterby profile
				        			FlutterbyAndMarguerite.setNarrationProfile("");

				        			ad.dismiss();
				        			displayProfiles();
				                } 
			            	}) 
			            	.setNegativeButton("Cancel", null) 
			            	.show(); 
					}

					 return true;
				}});
			
			ad.show();
			
		} 
		catch(AudioManagerException e) 
		{
    		throw e;
		}
	}
		
	private OnClickListener aboutButtonListener = new OnClickListener()
	{
		@Override
		public void onClick(View arg0) 
		{
			FlutterbyAndMarguerite.optionsListener.playRandomButtonSoundEffect();
			OptionMenu.this.startActivity(new Intent(OptionMenu.this, About.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
		}	
	};
	
	private boolean handleNarrationDialogSelection(final String item, int profileCount)
	{
		if(item.equals(getResources().getString(R.string.defaultNarration)))//default profile selected
		{
			FlutterbyAndMarguerite.setNarrationProfile("");
			
			Toast.makeText(OptionMenu.this, "Default narration profile 'Flutterby' selected.", Toast.LENGTH_SHORT).show();
			
			return true;
		}
		else if(item.equals(getResources().getString(R.string.createNarration)))//create new profile selected
		{
			if(profileCount >= MAX_NARRATION_PROFILES)
			{
				Toast.makeText(OptionMenu.this, getResources().getString(R.string.narrationProfileLimitError), Toast.LENGTH_SHORT).show();  
				return false;
			}
			else
			{
				LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
				View layout = inflater.inflate(R.layout.createprofiledialog, null);
	
				final EditText text = (EditText) layout.findViewById(R.id.EditTextCreateProfile);
				
				AlertDialog.Builder createDialog = new AlertDialog.Builder(this);
				createDialog.setTitle("Enter profile name");
				
				if(isXLargeScreen())
					createDialog.setIcon(R.drawable.butterflyplainlarge);
				else
					createDialog.setIcon(R.drawable.butterflyplain);
				
				createDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						try 
						{
							AudioNarrationManager.createNarrationProfile(OptionMenu.this, text.getText().toString());

							FlutterbyAndMarguerite.setNarrationProfile(text.getText().toString());

							//go straight to edit profile...
		        			editNarrationProfile(text.getText().toString());
						} 
						catch (AudioManagerException e) 
						{
							Toast.makeText(OptionMenu.this, e.getMessage(), Toast.LENGTH_LONG).show();
							displayProfiles();
						}
					}});
				
				createDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) 
					{
						displayProfiles();
					}
				});
				createDialog.setView(layout);
				final AlertDialog alertDialog = createDialog.show();
				
				alertDialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(false);
				
				text.addTextChangedListener(new TextWatcher(){
					@Override
					public void afterTextChanged(Editable arg0) {
						if(arg0.length() > 0)
							alertDialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(true);
						else
							alertDialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(false);
					}
	
					@Override
					public void beforeTextChanged(CharSequence s, int start,
							int count, int after) {
						// TODO Auto-generated method stub		
					}
	
					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {
						// TODO Auto-generated method stub
					}});
			}
			return true;
		}
		else//existing profile was selected...
		{
			 new AlertDialog.Builder(this)
         	.setMessage("Choose action for profile '" + item + "'") 
         	.setPositiveButton("Select", new DialogInterface.OnClickListener() { 
	                @Override 
	                public void onClick(DialogInterface dialog, int which) { 
	        			if(ad != null)
	        			{
	        				if(ad.isShowing())
	        					ad.dismiss();	        				
	        			}
	        			
        				FlutterbyAndMarguerite.setNarrationProfile(item);
        				
        				Toast.makeText(OptionMenu.this, "Narration profile '" + item + "' selected.", Toast.LENGTH_SHORT).show();
	                } 
         	}) 
         	.setNeutralButton("Edit", new DialogInterface.OnClickListener() { 
                @Override 
                public void onClick(DialogInterface dialog, int which) { 
        			if(ad != null)
        			{
        				if(ad.isShowing())
        					ad.dismiss();
        			}	        			
        			
        			editNarrationProfile(item);
                } 
         	}) 
         	.setNegativeButton("Cancel", null) 
         	.show(); 

			 return false;
		}
	}
}
