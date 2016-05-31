package com.kainat.app.android.util;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

import com.kainat.app.android.util.DrawableClickListener.DrawablePosition;

public class InterestView extends TextView {
	private Drawable dRight;
	private Rect rBounds;
	private DrawableClickListener clickListener;

	public InterestView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public InterestView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public InterestView(Context context) {
		super(context);
	}

	@Override
	public void setCompoundDrawables(Drawable left, Drawable top,
			Drawable right, Drawable bottom) {
		if (right != null) {
			dRight = right;
		}
		super.setCompoundDrawables(left, top, right, bottom);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		System.out.println("---on touch");
		if (event.getAction() == MotionEvent.ACTION_DOWN && dRight != null) {
			rBounds = dRight.getBounds();
			 int x = (int) event.getX();
			final int y = (int) event.getY();
			final int leftx = (int) getLeft();
			System.out.println("-------x-----"+x);
			x = x+leftx ;
			
			System.out.println("-------left x-----"+leftx+" x+left"+(x));
			System.out.println("-------right x-----"+getRight());
//			final int lefty = (int) event.getY();
			if (x >= (this.getRight() - rBounds.width())
					&& x <= (this.getRight() - this.getPaddingRight())
					&& y >= this.getPaddingTop()
					&& y <= (this.getHeight() - this.getPaddingBottom())) {
				if (this.clickListener != null) {
					clickListener.onClick(DrawablePosition.RIGHT,this);
				}
				event.setAction(MotionEvent.ACTION_CANCEL);
			}
		}
		return super.onTouchEvent(event);
	}

	public void setDrawableClickListener(DrawableClickListener listener) {
		this.clickListener = listener;
	}
	
	@Override
	protected void onSelectionChanged(int selStart, int selEnd) {
	}
}