package com.craftysoft.flutterbyandmarguerite;

import android.app.Activity;
import android.content.Intent;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Bundle;
import android.os.Handler;

import com.craftysoft.flutterbyandmarguerite.pro.R;

public class SplashScreen extends Activity {        
	 
	        private final int SPLASH_DISPLAY_LENGTH = 2800;
	        private SoundPoolManager _soundPoolMgr = new SoundPoolManager();
	        
	        
	        @Override
	        public void onCreate(Bundle savedInstanceState) 
	        {
	                super.onCreate(savedInstanceState);
	 
	                setContentView(R.layout.splashscreen);
	                
	                _soundPoolMgr.loadSound(R.raw.typer, this);
	                _soundPoolMgr.setOnLoadCompleteListener(new OnLoadCompleteListener(){
						@Override
						public void onLoadComplete(SoundPool soundPool,
								int sampleId, int status) {
								if(_soundPoolMgr != null)
									_soundPoolMgr.playSound(R.raw.typer);							
						}});
	 
	                /* New Handler to start the Main Activity 	 
	                 * and close this Splash-Screen after some seconds.*/	 
	                new Handler().postDelayed(new Runnable(){
	 
	                        @Override
	                        public void run() 
	                        {
	                                Intent mainIntent = new Intent(SplashScreen.this, FlutterbyAndMarguerite.class);
	 
	                                SplashScreen.this.startActivity(mainIntent);
	 
	                                _soundPoolMgr.delete();
	                                _soundPoolMgr = null;
	                                
	                                SplashScreen.this.finish();
	                                
	                                //TODO:FIND A WAY TP DETERMINE API LEVEL AND NOT MAKE THIS CALL FOR LOW API VERSIONS
	                                /* Apply our splash exit (fade out) and main
	                                entry (fade in) animation transitions. */
	                         //    SplashScreen.this.overridePendingTransition(R.anim.mainfadein, R.anim.splashfadeout);
	                        }
	                }, SPLASH_DISPLAY_LENGTH);
	        }
	
	//protected boolean _active = true;
	//protected int _splashTime = 3000;//time splash is displayed

/*	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splashscreen);
		
		Thread splashThread = new Thread()
		{
			@Override
			public void run()
			{
				try
				{
					int waited = 0;
					while(_active && (waited < _splashTime))
					{
						sleep(100);
					
						if(_active)
						{
							waited += 100;
						}
					}
				}
				catch(InterruptedException e)
				{
					//do nothing
				}
				finally
				{
					finish();
					startActivity(new Intent(Splash.this, FlutterbyAndMarguerite.class));
					stop();
				}
			}
		};
		
		splashThread.start();
	}*/
}
