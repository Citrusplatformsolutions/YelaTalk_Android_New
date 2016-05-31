package com.kainat.app.android.uicontrol;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class RTScrollView extends ScrollView{
	private static int MAJOR_MOVE = 60;
    private GestureDetector gd;
    public boolean stopScroll = false ;
    public RTFling rTFling ;
    public RTScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		 System.out.println("--1--constructor---RTScrollView--------");
		 
	}
    public void setRrTFling(RTFling rTFling){
    	this.rTFling = rTFling ;
    }
	public RTScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		 System.out.println("---2--constructor---RTScrollView--------");
		 gd = new GestureDetector(context,
                 new GestureDetector.OnGestureListener() {

                         @Override
                         public boolean onSingleTapUp(MotionEvent e) {
                                 return false;
                         }

                         @Override
                         public void onShowPress(MotionEvent e) {
                                                 }

                         @Override
                         public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                                         float distanceY) {
                                 return false;
                         }

                         @Override
                         public void onLongPress(MotionEvent e) {                        
                         }

                         @Override
                         public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                                         float velocityY) {
                               System.out.println("-----rtscrollview----onFling----------");
                        	 int dx = (int) (e2.getX() - e1.getX());
                                 if (Math.abs(dx) > MAJOR_MOVE
                                                 && Math.abs(velocityX) > Math.abs(velocityY)) {
                                         if (velocityX < 0) {
                                                 
                                         }
                                 }
                                 return false;
                         }

                         @Override
                         public boolean onDown(MotionEvent e) {
                                 return false;
                         }
                 });

	}

	
	public RTScrollView(Context context) {
		super(context);

		 System.out.println("---3--constructor---RTScrollView--------");
		 	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		System.out.println("-----onTouchEvent---RTScrollView-------here-"+stopScroll);
//		if(stopScroll)
//			return false;//super.onTouchEvent(ev);
//		else
//			if(stopScroll)
//			{
//				rTFling.onFling(ev, ev, 0,0) ;
//			}
			stopScroll = false ;
			return super.onTouchEvent(ev);
		
	}
}
