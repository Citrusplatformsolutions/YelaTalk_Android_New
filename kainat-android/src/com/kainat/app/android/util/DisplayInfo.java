package com.kainat.app.android.util;

import android.content.Context;

public class DisplayInfo {
	public String DENSITY_DPI;
	public String DENSITY;
	public String SCALED_DENSITY;
	public String XDPI;
	public String YDPI;
	public String WIDTH_PIXELS;
	public String HEIGHT_PIXELS;

	private static DisplayInfo instance;

	private DisplayInfo() {
	}

	public static DisplayInfo getInstance(Context context) {
		if (instance == null) {
			DisplayWrapper displayWrapper = DisplayWrapper.newInstance(context.getApplicationContext());

			instance = new DisplayInfo();
			instance.DENSITY = Float.toString(displayWrapper.getDensity());
			instance.DENSITY_DPI = Float.toString(displayWrapper.getDensityDpi());
			instance.SCALED_DENSITY = Float.toString(displayWrapper.getScaledDensity());
			instance.XDPI = Float.toString(displayWrapper.getXDpi());
			instance.YDPI = Float.toString(displayWrapper.getYDpi());
			instance.WIDTH_PIXELS = Integer.toString(displayWrapper.getWidthPixels());
			instance.HEIGHT_PIXELS = Integer.toString(displayWrapper.getHeightPixels());
		}
		return instance;
	}

	public String toString() {
		StringBuffer stb = new StringBuffer();
		stb.append("DENSITY_DPI##" + DENSITY_DPI).append("::");
		stb.append("DENSITY##" + DENSITY).append("::");
		stb.append("SCALED_DENSITY##" + SCALED_DENSITY).append("::");
		stb.append("XDPI##" + XDPI).append("::");
		stb.append("YDPI##" + YDPI).append("::");
		stb.append("WIDTH_PIXELS##" + WIDTH_PIXELS).append("::");
		stb.append("HEIGHT_PIXELS##" + HEIGHT_PIXELS);

		return stb.toString();
	}
}
