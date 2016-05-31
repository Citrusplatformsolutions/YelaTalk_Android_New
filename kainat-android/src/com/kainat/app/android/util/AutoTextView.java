package com.kainat.app.android.util;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.MultiAutoCompleteTextView;

import com.kainat.app.android.util.DrawableClickListener.DrawablePosition;

public class AutoTextView extends MultiAutoCompleteTextView {
	private Drawable dRight;
	  private Rect rBounds;
	  private DrawableClickListener clickListener;
	public AutoTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

	}

	public AutoTextView(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	public AutoTextView(Context context) {
		super(context);

	}
	@Override
	  public void setCompoundDrawables(Drawable left, Drawable top,
	      Drawable right, Drawable bottom)
	  {
		if(Logger.ENABLE)Logger.debug("-------------------", "=---------------------setCompoundDrawables");
	    if(right !=null)
	    {
	    	if(Logger.ENABLE)Logger.debug("-------------------", "=------right !=null-----------setCompoundDrawables");
	      dRight = right;
	    }
	    super.setCompoundDrawables(left, top, right, bottom);
	  }
	@Override
	  public boolean onTouchEvent(MotionEvent event)
	  {
		if(Logger.ENABLE)Logger.debug("-------------------", "=----------1-----------onTouchEvent");
	    if(event.getAction() == MotionEvent.ACTION_UP && dRight!=null)
	    {
	    	if(Logger.ENABLE)Logger.debug("-------------------", "=------2---------------onTouchEvent");
	      rBounds = dRight.getBounds();
	      final int x = (int)event.getX();
	      final int y = (int)event.getY();
	      //System.out.println("x:/y: "+x+"/"+y);
	      //System.out.println("bounds: "+bounds.left+"/"+bounds.right+"/"+bounds.top+"/"+bounds.bottom);
	      //check to make sure the touch event was within the bounds of the drawable
	      if(x>=(this.getRight()-rBounds.width())-50 && x<=(this.getRight()-this.getPaddingRight())+50
	          && y>=this.getPaddingTop()-50 && y<=(this.getHeight()-this.getPaddingBottom())+50)
	      {
	        //System.out.println("touch");
	        /*this.setText("");*/
	    	  if( this.clickListener != null)
	    	  {
	    		  clickListener.onClick(DrawablePosition.RIGHT);
	    	  }
	        event.setAction(MotionEvent.ACTION_CANCEL);//use this to prevent the keyboard from coming up
	      }
	    }
	    return super.onTouchEvent(event);
	  }

	public void setDrawableClickListener(DrawableClickListener listener) {
	    this.clickListener = listener;
	}
	
}