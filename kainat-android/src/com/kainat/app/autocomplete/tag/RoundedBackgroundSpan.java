package com.kainat.app.autocomplete.tag;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.style.ReplacementSpan;

public class RoundedBackgroundSpan extends ReplacementSpan
{

	private final int mPadding = 4;
	private final int roundness = 5;
	private int mBackgroundColor;
	private int mTextColor;

	public RoundedBackgroundSpan(int backgroundColor, int textColor) {
		super();
		mBackgroundColor = backgroundColor;
		mTextColor = textColor;
	}

	@Override
	public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
		return (int) (mPadding + paint.measureText(text.subSequence(start, end).toString()) + mPadding + 5);
		//    	 return Math.round(measureText(paint, text, start, end));
	}

	@Override
	public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint)
	{
		int height = getTextHeight(text.toString(), paint);
		
		 /*float width = paint.measureText(text.subSequence(start, end).toString());
         RectF rect = new RectF(x, top+mPadding, x + width + 2*mPadding, bottom - mPadding);
         paint.setColor(mBackgroundColor);
         canvas.drawRoundRect(rect, mPadding, mPadding, paint);
         paint.setColor(mTextColor);
         canvas.drawText(text, start, end, x+mPadding, y + mPadding, paint);
         paint.setTextAlign(Paint.Align.CENTER);
*/
		// For Android kitkat and lollipop
		float width = paint.measureText(text.subSequence(start, end).toString());
		/*paint.setColor(Color.rgb(205, 136, 120));
		paint.setStrokeWidth(2);
		canvas.drawRoundRect(new RectF(x - 1, top - 1, (x + width +(2*mPadding) + 1), (y+(height/2)) + 1), roundness, roundness, paint);*/
        RectF rect = new RectF(x + 1, top, x + width +(2*mPadding) , y+(height/2));
        paint.setColor(mBackgroundColor);
        canvas.drawRoundRect(rect, roundness, roundness, paint);
        
        paint.setColor(mTextColor);
        canvas.drawText(text, start, end, x, y + mPadding , paint);
        paint.setTextAlign(Paint.Align.CENTER);
		
	}
	public int getTextHeight(String text, Paint paint) {
		Rect bounds = new Rect();
		paint.getTextBounds(text, 0, text.length(), bounds);
		int height = bounds.bottom + bounds.height();
		return height;
	}
}
