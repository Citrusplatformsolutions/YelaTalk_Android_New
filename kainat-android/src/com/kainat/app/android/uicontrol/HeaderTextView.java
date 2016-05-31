package com.kainat.app.android.uicontrol;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.Display;
import android.view.WindowManager;
import android.widget.TextView;

public class HeaderTextView extends TextView {
	int width = 0 ;
	public HeaderTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		width = display.getHeight() ;
	}

	public HeaderTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		width = display.getHeight() ;
	}

	public HeaderTextView(Context context) {
		super(context);
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		width = display.getHeight() ;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		TextPaint textPaint = getPaint();
		textPaint.drawableState = getDrawableState();
		textPaint.setColor(Color.BLACK);
		String textString = (String) getText();
		
		canvas.drawText(textString + "", width / 2
				- getTextSize() / 2, 10, textPaint);
		
		// textString = "Hello";
//		for (int i = 0; i < textString.length(); i++) {
//			canvas.drawText(textString.charAt(i) + "", width / 2
//					- getTextSize() / 2, (i + 1) * getTextSize(), textPaint);
//		}
		getLayout().draw(canvas);
	}
}