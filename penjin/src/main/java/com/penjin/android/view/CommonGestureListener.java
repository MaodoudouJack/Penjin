package com.penjin.android.view;

import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class CommonGestureListener  extends SimpleOnGestureListener implements OnTouchListener {

	/** 左右滑动的最短距离 */    
    private int distance = 100;    
    /** 左右滑动的最大速度 */    
    private int velocity = 200;    
	
	private GestureDetector gestureDetector;
	
	
	public void left(){
	}
	
	public void right(){
	}
	
	
	public CommonGestureListener(Context context) {
		super();
		this.gestureDetector=new GestureDetector(context, this);
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// 向左滑    
        if (e1.getX() - e2.getX() > distance    
                && Math.abs(velocityX) > velocity) {    
            left();    
        }    
        // 向右滑    
        if (e2.getX() - e1.getX() > distance    
                && Math.abs(velocityX) > velocity) {    
            right();    
        }    
		return false;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		this.gestureDetector.onTouchEvent(event);//调用本手势监听器来处理MotionEvent
		return false;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public int getVelocity() {
		return velocity;
	}

	public void setVelocity(int velocity) {
		this.velocity = velocity;
	}

	public GestureDetector getGestureDetector() {
		return gestureDetector;
	}

	public void setGestureDetector(GestureDetector gestureDetector) {
		this.gestureDetector = gestureDetector;
	}
	
	public void fireTouchEvent(MotionEvent event)
	{
		this.gestureDetector.onTouchEvent(event);
	}
	
}
