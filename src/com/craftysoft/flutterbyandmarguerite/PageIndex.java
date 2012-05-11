package com.craftysoft.flutterbyandmarguerite;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;

import com.craftysoft.flutterbyandmarguerite.pro.R;
import com.immersion.uhl.Launcher;

public class PageIndex extends Activity {
	
	private Button _closeButton = null;
	private Gallery _gallery = null;
	
	private boolean isXLargeScreen()
	{
		return ((getResources().getConfiguration().screenLayout 
 				& Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// Handle the back button
		if (keyCode == KeyEvent.KEYCODE_BACK)
			FlutterbyAndMarguerite.optionsListener.playRandomButtonSoundEffect();

		return super.onKeyDown(keyCode, event);
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pageindex);
     
        
        _gallery = (Gallery) findViewById(R.id.gallery);    
        _gallery.setAdapter(new ImageAdapter(this));    
        _gallery.setOnItemClickListener(new OnItemClickListener() 
        {        
        	@Override
        	public void onItemClick(AdapterView<?> parent, View v, int position, long id) 
        	{         
				FlutterbyAndMarguerite.optionsListener.playRandomButtonSoundEffect();
				
        		Intent intent = new Intent();
        		intent.putExtra("pageIndex", ++position);//FlutterbyAndMarguerite.pageLayoutIds[position]);
        		PageIndex.this.setResult(RESULT_OK, intent);
        		PageIndex.this.finish();
        	}    
        });
        
        initializeUI();
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
        _closeButton = (Button)findViewById(R.id.ButtonCloseMain);
        _closeButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				
				//FlutterbyAndMarguerite.optionsListener.playRandomMasterSoundEffect();
				//haptic
				FlutterbyAndMarguerite.optionsListener.vibratePhoneId(Launcher.STRONG_CLICK_66);

				PageIndex.this.setResult(RESULT_CANCELED, null);
				PageIndex.this.finish();
			}	
        });
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
		
		@Override
		public int getCount() 
		{        
			return 23;
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

			//			i.setLayoutParams(new Gallery.LayoutParams(100, 100));        
			i.setScaleType(ImageView.ScaleType.FIT_XY);        
			i.setBackgroundResource(mGalleryItemBackground);        
			
			return i;    
		}
	}
}