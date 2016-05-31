package com.kainat.app.android;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.PopupWindow;

public class PopupWindows {
	protected Context mContext;
	protected PopupWindow mWindow;
	protected View mRootView;
	protected Drawable mBackground = null;
	protected WindowManager mWindowManager;
	public boolean isArrow = true ;
	
	public PopupWindows(Context context) {
		mContext	= context;
		mWindow 	= new PopupWindow(context);

		mWindow.setTouchInterceptor(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					mWindow.dismiss();
					
					return false;
				}
				
				return false;
			}
		});

		mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
	}
	
	protected void onDismiss() {		
	}
	
	protected void onShow() {		
	}

	protected void preShow(boolean outsidescreen) {
		if (mRootView == null) 
			throw new IllegalStateException("setContentView was not called with a view to display.");
	
		onShow();

		if (mBackground == null) 
			mWindow.setBackgroundDrawable(new BitmapDrawable());
		else 
			mWindow.setBackgroundDrawable(mBackground);
if(this.isArrow)
		mWindow.setWidth(WindowManager.LayoutParams.FILL_PARENT);
else
	mWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
		mWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
		mWindow.setTouchable(true);
		mWindow.setFocusable(false);//window.setFocusable(false);
		mWindow.setOutsideTouchable(outsidescreen);

		mWindow.setContentView(mRootView);
	}

	public void setBackgroundDrawable(Drawable background) {
		mBackground = background;
	}

	public void setContentView(View root) {
		mRootView = root;
		
		mWindow.setContentView(root);
	}
	public void setContentView(int layoutResID) {
		LayoutInflater inflator = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		setContentView(inflator.inflate(layoutResID, null));
	}
	public void setOnDismissListener(PopupWindow.OnDismissListener listener) {
		mWindow.setOnDismissListener(listener);  
	}
	public boolean isShowing() {
		return mWindow.isShowing();
	}
	public void dismiss() {
		mWindow.dismiss();
	}
	

}