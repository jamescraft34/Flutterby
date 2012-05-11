package com.craftysoft.flutterbyandmarguerite;

//Interface so we can create a listener for the ScrollView
public interface MyScrollViewListener 
{
	//Determines if the scrollview has been scrolled to the bottom
	public void ScrollToBottom(int scrollViewHeight, int scrollY);
}
