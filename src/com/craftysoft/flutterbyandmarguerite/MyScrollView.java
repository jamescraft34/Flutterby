package com.craftysoft.flutterbyandmarguerite;

import android.content.Context; 
import android.util.AttributeSet; 
import android.widget.ScrollView;  

public class MyScrollView extends ScrollView 
{      
	private MyScrollViewListener _myscrollViewListener = null;      
	
	public MyScrollView(Context context) 
	{         
		super(context);     
	}      
	
	public MyScrollView(Context context, AttributeSet attrs, int defStyle) 
	{         
		super(context, attrs, defStyle);     
	}      
	
	public MyScrollView(Context context, AttributeSet attrs) 
	{         
		super(context, attrs);     
	}      
	
	public void setScrollViewListener(MyScrollViewListener myscrollViewListener) 
	{         
		this._myscrollViewListener = myscrollViewListener;     
	}      
	
	@Override     
	protected void onScrollChanged(int x, int y, int oldx, int oldy) 
	{         
		super.onScrollChanged(x, y, oldx, oldy);         
		
		if(_myscrollViewListener != null) 
		{             
			_myscrollViewListener.ScrollToBottom(this.getHeight(), this.getScrollY());         
		}     
	}  
} 