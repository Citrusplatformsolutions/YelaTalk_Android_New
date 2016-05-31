package com.kainat.app.android.util;

import android.text.TextPaint;
import android.text.style.URLSpan;

public class NonUnderlinedSpan extends URLSpan {
	public NonUnderlinedSpan(String url) {
		super(url);
	}

	public void updateDrawState(TextPaint drawState) {
		super.updateDrawState(drawState);
		drawState.setUnderlineText(false);
	}
}