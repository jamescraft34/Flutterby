package com.craftysoft.flutterbyandmarguerite;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.craftysoft.flutterbyandmarguerite.pro.R;
import com.immersion.uhl.Launcher;

public class About extends Activity {

	private Button _closeButton = null;
    private TextView _link = null;
    private TextView _blogLink = null;
    private Button _email = null;
    private TextView _linkJam = null;
    
    private String[] _emailList = {"craftySoft@gmail.com"};
    private String[] _emailListJam = {"jampro@earthlink.net"};

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
		String brandName = Build.BRAND;
		
		if(brandName.equals(this.getResources().getString(R.string.nookdevice)))
			setContentView(R.layout.aboutnook);
		else
			setContentView(R.layout.about);

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

		
	private void initializeUI()
	{
        _closeButton = (Button)findViewById(R.id.ButtonClose);
        _closeButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
			
				//FlutterbyAndMarguerite.optionsListener.playRandomMasterSoundEffect();
				
				//haptic
				FlutterbyAndMarguerite.optionsListener.vibratePhoneId(Launcher.STRONG_CLICK_66);

				About.this.finish();
			}	
        });
        
        
        _blogLink = (TextView)findViewById(R.id.TextViewBlogLink);
        _blogLink.setOnClickListener( new OnClickListener()
        {
        	@Override
        	public void onClick(View v)
        	{
				Uri uri = Uri.parse("http://www.craftysoft.me/flutterby.html" );
        		startActivity( new Intent( Intent.ACTION_VIEW, uri ) );
        	}
        });
        
        _link = (TextView)findViewById(R.id.TextViewLink);
        _link.setOnClickListener( new OnClickListener()
        {
        	@Override
        	public void onClick(View v)
        	{
				Uri uri = Uri.parse("http://www.craftysoft.me" );
        		startActivity( new Intent( Intent.ACTION_VIEW, uri ) );
        	}
        });
        
        _linkJam = (TextView)findViewById(R.id.TextViewLinkJam);
        _linkJam.setOnClickListener( new OnClickListener()
        {
        	@Override
        	public void onClick(View v)
        	{
        		callEmailIntentJam();
        	}
        });
        
        _email = (Button)findViewById(R.id.ButtonEmailLink);
        _email.setOnClickListener( new OnClickListener()
        {
        	@Override
        	public void onClick(View v)
        	{
        		callEmailIntent();
        	}
        });
	}

	private void callEmailIntent()
	{
		try{
			Intent i = new Intent(Intent.ACTION_SEND); 
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
			i.setType("plain/text");
			i.putExtra(Intent.EXTRA_EMAIL, _emailList);
			i.putExtra(Intent.EXTRA_SUBJECT, "Re: Flutterby");		
			startActivity(Intent.createChooser(i, "Email Developer"));		
		}
		catch(Exception ex)
		{
			Toast.makeText(this, "Email client could not be launched on device.", Toast.LENGTH_SHORT).show();
		}
	}
	
	private void callEmailIntentJam()
	{
		try{
			Intent i = new Intent(Intent.ACTION_SEND); 
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
			i.setType("plain/text");
			i.putExtra(Intent.EXTRA_EMAIL, _emailListJam);
			i.putExtra(Intent.EXTRA_SUBJECT, "Re: Flutterby Audio");		
			startActivity(Intent.createChooser(i, "Email Jam Productions"));		
		}
		catch(Exception ex)
		{
			Toast.makeText(this, "Email client could not be launched on device.", Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// Handle the back button
		if (keyCode == KeyEvent.KEYCODE_BACK)
			FlutterbyAndMarguerite.optionsListener.playRandomButtonSoundEffect();

		return super.onKeyDown(keyCode, event);
	}


}
