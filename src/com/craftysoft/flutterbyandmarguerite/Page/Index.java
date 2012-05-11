package com.craftysoft.flutterbyandmarguerite.Page;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.craftysoft.flutterbyandmarguerite.FlutterbyAndMarguerite;
import com.craftysoft.flutterbyandmarguerite.pro.R;

//serves as the start page of the application
//will contain menu buttons to drive application functionality
public class Index extends RelativeLayout implements View.OnTouchListener{
	private Button _buttonBookmark = null;
	private Button _buttonRead = null;
	private Button _buttonPageIndex = null;
	private Button _buttonHelp = null;
	
	public boolean _blockBackgroundChange = true;
		
	public Index(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		
		setOnTouchListener(this);
		
		//HACK: keep track of the backpage that page6 came from...
//		Page.backPageNav = null;			
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// Handle the back button
		if (keyCode == KeyEvent.KEYCODE_BACK)
			FlutterbyAndMarguerite.optionsListener.playRandomButtonSoundEffect();

		return super.onKeyDown(keyCode, event);
	}
				
	boolean _initialInflationPerformed = false;
	@Override
	public void onFinishInflate()
	{
		super.onFinishInflate();
		
		if(!_initialInflationPerformed)//check to make sure subsequent inflations, ie: textview, etc trigger these to be performed again
		{
			setBackgroundResource(com.craftysoft.flutterbyandmarguerite.pro.R.drawable.index);
			
			initButtons();
					
			_initialInflationPerformed = true;						
		}		
	}
	
	public void makeBackgroundNormal()
	{
		setBackgroundResource(com.craftysoft.flutterbyandmarguerite.pro.R.drawable.index);
		
		_buttonHelp.setVisibility(View.VISIBLE);
		_buttonBookmark.setVisibility(View.VISIBLE);
		_buttonPageIndex.setVisibility(View.VISIBLE);
		_buttonRead.setVisibility(View.VISIBLE);
	}
	
	public void makeBackgroundPlain()
	{
		setBackgroundResource(com.craftysoft.flutterbyandmarguerite.pro.R.drawable.indexplain);
		
		_buttonHelp.setVisibility(View.INVISIBLE);
		_buttonBookmark.setVisibility(View.INVISIBLE);
		_buttonPageIndex.setVisibility(View.INVISIBLE);
		_buttonRead.setVisibility(View.INVISIBLE);
	}
	
	public void initButtons() 
	{
		_buttonHelp = (Button)findViewById(R.id.ButtonHelp);
	    _buttonHelp.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					_blockBackgroundChange = false;
					FlutterbyAndMarguerite.optionsListener.playRandomButtonSoundEffect();
					FlutterbyAndMarguerite.optionsListener.showOptionsDialog(true);
				}
	        });

		_buttonBookmark = (Button)findViewById(R.id.ButtonGoBookmark);
		_buttonBookmark.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				FlutterbyAndMarguerite.optionsListener.playRandomButtonSoundEffect();
				FlutterbyAndMarguerite.optionsListener.goToBookmarkPage();
			}			
		});
		
		_buttonPageIndex = (Button)findViewById(R.id.ButtonIndex);
		_buttonPageIndex.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {		
				_blockBackgroundChange =  false;
				FlutterbyAndMarguerite.optionsListener.playRandomButtonSoundEffect();
				FlutterbyAndMarguerite.optionsListener.goPageIndex();
			}			
		});
		
		_buttonRead = (Button)findViewById(R.id.ButtonRead);
		_buttonRead.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				FlutterbyAndMarguerite.optionsListener.playRandomButtonSoundEffect();
				FlutterbyAndMarguerite.optionsListener.readBook(1);
			}			
		});	
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent me) {

		switch (me.getAction()) {
			case MotionEvent.ACTION_UP: {
				FlutterbyAndMarguerite.optionsListener.playSound((int)me.getX(), (int)me.getY());
			}
		}
		return true;
	}

}