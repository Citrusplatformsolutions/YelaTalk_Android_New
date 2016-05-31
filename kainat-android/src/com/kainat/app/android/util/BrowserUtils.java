package com.kainat.app.android.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class BrowserUtils {
	private BrowserUtils() {
	}

	public static void openBrowser(Context context, String url) {
		try {
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(url));
			context.startActivity(i);
		} catch (ActivityNotFoundException activityException) {
			activityException.printStackTrace();
		}
	}
}
