package com.kainat.app.android.util;

import android.text.Spannable;
import android.text.style.URLSpan;

public class StringUtils {
	public static String toString(Object object) {
		return object == null ? "" : object.toString();
	}

	public static void removeUnderlines(Spannable text) {
		URLSpan[] spans = text.getSpans(0, text.length(), URLSpan.class);

		for (URLSpan span : spans) {
			int start = text.getSpanStart(span);
			int end = text.getSpanEnd(span);
			text.removeSpan(span);
			span = new NonUnderlinedSpan(span.getURL());
			text.setSpan(span, start, end, 0);
		}
	}

	public static String partialFillingString(String originalString, String fillingString, boolean secondHalf) {
		if (originalString == null || originalString.equals("") || fillingString == null || fillingString.equals("")) {
			return originalString;
		}
		int totalLen = originalString.length();
		int noOfCharToFill = totalLen / 2;
		if (totalLen % 2 == 0) {
			noOfCharToFill++;
		}
		StringBuffer result = new StringBuffer();
		if (secondHalf) {
			result.append(originalString.substring(0, noOfCharToFill));
		}
		int len = totalLen - noOfCharToFill;
		for (int i = 0; i < len; i++) {
			result.append(fillingString);
		}
		if (!secondHalf) {
			result.append(originalString.substring(len));
		}
		return result.toString();
	}

	public static String partialFillingString(String originalString, String fillingString) {
		if (originalString == null || originalString.equals("") || fillingString == null || fillingString.equals("")) {
			return originalString;
		}
		int totalLen = originalString.length();
		int noOfStarToFill = totalLen > 5 ? 5 : totalLen;
		int index = originalString.indexOf("@");
		if (index != -1) {
			noOfStarToFill = index;
		}

		StringBuffer result = new StringBuffer();
		for (int i = 0; i < noOfStarToFill; i++) {
			result.append(fillingString);
		}
		if (totalLen > 5) {
			result.append(originalString.substring(noOfStarToFill));
		}
		return result.toString();
	}
}