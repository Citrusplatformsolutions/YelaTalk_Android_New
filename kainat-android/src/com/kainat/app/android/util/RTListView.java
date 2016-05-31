package com.kainat.app.android.util;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

public class RTListView extends ListView {
	private Drawable dRight;
	private Rect rBounds;
	private DrawableClickListener clickListener;
	private boolean stopListInvalidate ;
	public boolean isStopListInvalidate() {
		return stopListInvalidate;
	}

	public void setStopListInvalidate(boolean stopListInvalidate) {
		this.stopListInvalidate = stopListInvalidate;
	}

	public RTListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

//	@Override
//	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		// TODO Auto-generated method stub
//		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//	}
	public RTListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RTListView(Context context) {
		super(context);
	}
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
//		System.out.println("------RTListView------onTouchEvent--------------");
		
		if(stopListInvalidate)return true;
		else
		return super.onTouchEvent(ev);
	}	
	
	
}