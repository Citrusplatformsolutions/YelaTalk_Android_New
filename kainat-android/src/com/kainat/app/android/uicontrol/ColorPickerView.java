package com.kainat.app.android.uicontrol;


import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.view.MotionEvent;
import android.view.View;

import com.kainat.app.android.OnColorChangedListener;
import com.kainat.app.android.util.Constants;
import com.kainat.app.android.util.Utilities;

public class ColorPickerView extends View {
    private Paint mPaint;
    private Paint mCenterPaint;
   private int mCurrentColor, mDefaultColor;
    private final int[] mColors;
    private OnColorChangedListener mListener;
    int CENTER_X = 200;
    int CENTER_Y = 300;
     int CENTER_RADIUS = 90;
     int BUTTON_HEIGHT;
     int fontsize=15;
     Activity mActivity;
    ColorPickerView(Context c, OnColorChangedListener l, int color, int defaultColor, Activity activity) {
        super(c);
        mActivity = activity;
        
        if(Utilities.getScreenType(activity)==Constants.SCREENLAYOUT_SIZE_X_LARGE)
		{
        	   CENTER_X = 400;
        	    CENTER_Y =500;
        	   CENTER_RADIUS = 150;
        	   fontsize=40;
        	   BUTTON_HEIGHT = CENTER_Y - 80;
		}
		else if(Utilities.getScreenType(activity)==Constants.SCREENLAYOUT_SIZE_LARGE)
		{
			    CENTER_X = 200;
			   CENTER_Y = 280;
			    CENTER_RADIUS = 90;
			    fontsize=20;
			    BUTTON_HEIGHT = CENTER_Y - 50;
		}else if(Utilities.getScreenType(activity)==Constants.SCREENLAYOUT_SIZE_MEDIUM)
		{
			    CENTER_X = 200;
			    CENTER_Y = 300;
			    CENTER_RADIUS = 90;
			    fontsize=15;
			    BUTTON_HEIGHT = CENTER_Y - 50;
		}
		else if(Utilities.getScreenType(activity)==Constants.SCREENLAYOUT_SIZE_SMALL)  
		{
			  CENTER_X = 100;
			   CENTER_Y = 200;
			     CENTER_RADIUS = 50;
			     fontsize=15;
			     BUTTON_HEIGHT = CENTER_Y - 50;
		}
		else
		{
		 CENTER_X = 200;
		 CENTER_Y = 300;
		 CENTER_RADIUS = 90;
		 fontsize=15;
		 BUTTON_HEIGHT = CENTER_Y - 50;
		}
        mListener = l;
 mDefaultColor = defaultColor;
 
  mDefaultColor=-14536113 ;
        mColors = new int[] {
            0xFFFF0000, 0xFFFF00FF, 0xFF0000FF, 0xFF00FFFF, 0xFF00FF00,
            0xFFFFFF00, 0xFFFF0000
        };
        Shader s = new SweepGradient(0, 0, mColors, null);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setShader(s);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(32);

        mCenterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCenterPaint.setColor(color);
        mCenterPaint.setStrokeWidth(5);
    }

    private boolean mTrackingCenter;
    private boolean mHighlightCenter;

    protected void onDraw(Canvas canvas) {
        float r = CENTER_X - mPaint.getStrokeWidth()*0.5f;

        canvas.translate(CENTER_X, CENTER_X);

        canvas.drawOval(new RectF(-r, -r, r, r), mPaint);
        Paint   paint = new Paint(Paint.ANTI_ALIAS_FLAG);
       
        paint.setColor(Color.RED);
        canvas.drawRect(0, BUTTON_HEIGHT, CENTER_X, CENTER_Y, paint );
        paint.setColor(Color.BLACK);
        paint.setTextSize(fontsize);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Cancel", CENTER_X-CENTER_X/2+paint.getTextSize(), CENTER_Y-20,
        		paint);
        paint.setColor(Color.GREEN);
        canvas.drawRect(-CENTER_X, BUTTON_HEIGHT, -CENTER_X+CENTER_X,  CENTER_Y, paint );
        paint.setTextSize(fontsize);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(Color.BLACK);
               canvas.drawText("Set Colour", -CENTER_X+CENTER_X/2-paint.getTextSize(),
            		   CENTER_Y-20, paint);
        canvas.drawCircle(0, 0, CENTER_RADIUS, mCenterPaint);
        

        if (mTrackingCenter) {
            int c = mCenterPaint.getColor();
            mCenterPaint.setStyle(Paint.Style.STROKE);

            if (mHighlightCenter) {
                mCenterPaint.setAlpha(0xFF);
            } else {
                mCenterPaint.setAlpha(0x80);
            }
            canvas.drawCircle(0, 0,
                              CENTER_RADIUS + mCenterPaint.getStrokeWidth(),
                              mCenterPaint);
                mCenterPaint.setColor(mDefaultColor);
            mCenterPaint.setStyle(Paint.Style.FILL);
            mCenterPaint.setColor(c);
           

  
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(CENTER_X*2, CENTER_Y*2);
    }

  

    private int floatToByte(float x) {
        int n = java.lang.Math.round(x);
        return n;
    }
    private int pinToByte(int n) {
        if (n < 0) {
            n = 0;
        } else if (n > 255) {
            n = 255;
        }
        return n;
    }

    private int ave(int s, int d, float p) {
        return s + java.lang.Math.round(p * (d - s));
    }

    private int interpColor(int colors[], float unit) {
        if (unit <= 0) {
            return colors[0];
        }
        if (unit >= 1) {
            return colors[colors.length - 1];
        }

        float p = unit * (colors.length - 1);
        int i = (int)p;
        p -= i;

        // now p is just the fractional part [0...1) and i is the index
        int c0 = colors[i];
        int c1 = colors[i+1];
        int a = ave(Color.alpha(c0), Color.alpha(c1), p);
        int r = ave(Color.red(c0), Color.red(c1), p);
        int g = ave(Color.green(c0), Color.green(c1), p);
        int b = ave(Color.blue(c0), Color.blue(c1), p);

        return Color.argb(a, r, g, b);
    }

    private int rotateColor(int color, float rad) {
        float deg = rad * 180 / 3.1415927f;
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);

        ColorMatrix cm = new ColorMatrix();
        ColorMatrix tmp = new ColorMatrix();

        cm.setRGB2YUV();
        tmp.setRotate(0, deg);
        cm.postConcat(tmp);
        tmp.setYUV2RGB();
        cm.postConcat(tmp);

        final float[] a = cm.getArray();

        int ir = floatToByte(a[0] * r +  a[1] * g +  a[2] * b);
        int ig = floatToByte(a[5] * r +  a[6] * g +  a[7] * b);
        int ib = floatToByte(a[10] * r + a[11] * g + a[12] * b);

        return Color.argb(Color.alpha(color), pinToByte(ir),
                          pinToByte(ig), pinToByte(ib));
    }

    private static final float PI = 3.1415926f;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX() - CENTER_X;
        float y = event.getY() - CENTER_Y;
        boolean inCenter = java.lang.Math.sqrt(x*x + y*y) <= CENTER_RADIUS;
        
        System.out.println("Postion====="+x + " y===="+y);
        if (x > -CENTER_X+10 && x < 0 && y > CENTER_Y-140 && y < CENTER_Y){
          //  mListener.colorChanged("", mCurrentColor);
          
                	 mListener.colorChanged("", mCenterPaint.getColor());
              
     
            return true;
        }
        // If the touch event is located in the right button, notify the
        // listener with the default color
        if (x > 0 && x < CENTER_X-10 &&y > CENTER_Y-140 && y < CENTER_Y){
           // mListener.colorChanged(null, mDefaultColor);
          
                	 mListener.colorChanged(null, mDefaultColor);
                	    return true;
            }
        
       
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTrackingCenter = inCenter;
                if (inCenter) {
                    mHighlightCenter = true;
                    invalidate();
                    break;
                }
            case MotionEvent.ACTION_MOVE:
                if (mTrackingCenter) {
                    if (mHighlightCenter != inCenter) {
                        mHighlightCenter = inCenter;
                        invalidate();
                    }
                } else {
                    float angle = (float)java.lang.Math.atan2(y, x);
                    // need to turn angle [-PI ... PI] into unit [0....1]
                    float unit = angle/(2*PI);
                    if (unit < 0) {
                        unit += 1;
                    }
                    mCenterPaint.setColor(interpColor(mColors, unit));
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mTrackingCenter) {
                    if (inCenter) {
                        mListener.colorChanged(null, mCenterPaint.getColor());
                    }
                    mTrackingCenter = false;    // so we draw w/o halo
                    invalidate();
                }
                break;
        }

        return true;
    }
}