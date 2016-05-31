package com.kainat.app.android.util;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

public class NonUnderlinedClickableSpan extends ClickableSpan {
	public void updateDrawState(TextPaint ds) {
		ds.setColor(ds.linkColor);
		ds.setUnderlineText(false); // set to false to remove underline
	}

	public void onClick(View widget) {
	}
}