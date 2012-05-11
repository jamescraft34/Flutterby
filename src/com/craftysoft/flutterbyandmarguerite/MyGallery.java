package com.craftysoft.flutterbyandmarguerite;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Gallery;

public class MyGallery extends Gallery {
	public boolean _blocking = false;

	public MyGallery(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MyGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public MyGallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub	
	}
	
	@Override    
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		switch (keyCode) 
		{        
			case KeyEvent.KEYCODE_DPAD_LEFT:             
			case KeyEvent.KEYCODE_DPAD_RIGHT:
			case KeyEvent.KEYCODE_DPAD_CENTER:
				return false;
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if(_blocking)
			return false;
		else
			return super.onTouchEvent(event);
	}

//	@Override
//	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
//	{
//		if(_blocking)
//			return true;
//		else
//			return super.onFling(e1, e2, velocityX, velocityY);
//	}


	

}
