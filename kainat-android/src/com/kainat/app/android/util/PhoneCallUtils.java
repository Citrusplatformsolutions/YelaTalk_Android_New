package com.kainat.app.android.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class PhoneCallUtils {
	private PhoneCallUtils() {
	}

	public static void call(Context context, String phoneNumber) {
		try {
			Intent callIntent = new Intent(Intent.ACTION_CALL);
			callIntent.setData(Uri.parse(phoneNumber));
			context.startActivity(callIntent);
		} catch (ActivityNotFoundException activityException) {
			activityException.printStackTrace();
		}
	}
}
