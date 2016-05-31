package com.kainat.app.android.uicontrol;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.widget.TextView;

public class Ticker extends TextView {
	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public Ticker(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public Ticker(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * @param context
	 */
	public Ticker(Context context) {
		super(context);
	}

	public void setSelected(boolean selected) {
		super.setSelected(true);
	}

	public void setFocusable(boolean focusable) {
		super.setFocusable(true);
	}

	public void setFocusableInTouchMode(boolean focusableInTouchMode) {
		super.setFocusableInTouchMode(true);
	}

	public void setEllipsize(TruncateAt where) {
		super.setEllipsize(TruncateAt.MARQUEE);
	}

	public void setMarqueeRepeatLimit(int marqueeLimit) {
		super.setMarqueeRepeatLimit(-1);
	}

	public void setMaxLines(int maxlines) {
		super.setMaxLines(1);
	}

	public boolean isFocused() {
		return true;
	}

	public void setText(CharSequence text, BufferType type) {
		StringBuilder builder = new StringBuilder();
		int len = text.length();
		if (len < 100) {
			for (int i = 80; i > len; i -= 3) {
				builder.append("   ");
			}
		}
		super.setText(text + builder.toString(), type);
	}

	public boolean isClickable() {
		return true;
	}

	public void setTextColor(ColorStateList colors) {
		super.setTextColor(Color.BLACK);
	}
}
