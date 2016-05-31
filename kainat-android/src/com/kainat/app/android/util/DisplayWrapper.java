package com.kainat.app.android.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class DisplayWrapper {
	private DisplayMetrics mMetrics;
	private boolean mIsPortrait;

	private DisplayWrapper(Context context) {
		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();

		mMetrics = new DisplayMetrics();
		mIsPortrait = display.getOrientation() == 0;

		display.getMetrics(mMetrics);
	}

	public static DisplayWrapper newInstance(Context context) {
		DisplayWrapper instance = new DisplayWrapper(context);
		return instance;
	}

	public float getDensity() {
		return mMetrics.density;
	}

	public float getDensityDpi() {
		return mMetrics.densityDpi;
	}

	public float getScaledDensity() {
		return mMetrics.scaledDensity;
	}

	public float getXDpi() {
		return mIsPortrait ? mMetrics.xdpi : mMetrics.ydpi;
	}

	public float getYDpi() {
		return mIsPortrait ? mMetrics.ydpi : mMetrics.xdpi;
	}

	public int getWidthPixels() {
		return mIsPortrait ? mMetrics.widthPixels : mMetrics.heightPixels;
	}

	public int getHeightPixels() {
		return mIsPortrait ? mMetrics.heightPixels : mMetrics.widthPixels;
	}
}