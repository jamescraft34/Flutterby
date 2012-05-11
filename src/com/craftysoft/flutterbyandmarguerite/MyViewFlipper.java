package com.craftysoft.flutterbyandmarguerite;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.GestureDetector.OnGestureListener;
import android.widget.ViewFlipper;

public class MyViewFlipper extends ViewFlipper implements OnGestureListener {

	private GestureDetector _gestureDetectorListener;

	public MyViewFlipper(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MyViewFlipper(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
        _gestureDetectorListener = new GestureDetector(this);
	}
	
    @Override  
    public boolean onTouchEvent(MotionEvent me)
    { 	
    	this._gestureDetectorListener.onTouchEvent(me);  
    	return super.onTouchEvent(me);  
    }

	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {

			this.showNext();

			return false;
	}

	@Override
	public void onLongPress(MotionEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

}
