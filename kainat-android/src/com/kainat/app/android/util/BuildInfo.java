package com.kainat.app.android.util;

import java.util.Date;

import android.content.Context;
import android.os.Build;
import android.webkit.WebView;

public class BuildInfo {
	public String FELICA;
	public String BOARD;
	public String BRAND;
	public String CPU_ABI;
	public String DEVICE;
	public String DISPLAY;
	public String FINGERPRINT;
	public String HOST;
	public String ID;
	public String MANUFACTURER;
	public String MODEL;
	public String PRODUCT;
	public String TAGS;
	public String TIME;
	public String TYPE;
	public String USER;
	public String VERSION_CODENAME;
	public String VERSION_INCREMENTAL;
	public String VERSION_RELEASE;
	public String VERSION_SDK;
	public String VERSION_SDK_INT;
	public String USER_AGENT;
//	public String SENSORS;

	private static BuildInfo instance;

	private BuildInfo() {
	}

	public static BuildInfo getInstance() {
		return instance ;
	}
	public static BuildInfo getInstance(Context context) {
		if (instance == null) {
			instance = new BuildInfo();
			instance.BOARD = Build.BOARD;
			instance.BRAND = Build.BRAND;
			instance.CPU_ABI = Build.CPU_ABI;
			instance.DEVICE = Build.DEVICE;
			instance.DISPLAY = Build.DISPLAY;
			instance.FINGERPRINT = Build.FINGERPRINT;
			instance.HOST = Build.HOST;
			instance.ID = Build.ID;
			instance.MANUFACTURER = Build.MANUFACTURER;
			instance.MODEL = Build.MODEL;
			instance.PRODUCT = Build.PRODUCT;
			instance.TAGS = Build.TAGS;
			instance.TIME = new Date(Build.TIME).toGMTString();
			instance.TYPE = Build.TYPE;
			instance.USER = Build.USER;
			instance.VERSION_CODENAME = Build.VERSION.CODENAME;
			instance.VERSION_INCREMENTAL = Build.VERSION.INCREMENTAL;
			instance.VERSION_RELEASE = Build.VERSION.RELEASE;
			instance.VERSION_SDK = Build.VERSION.SDK;
			instance.VERSION_SDK_INT = Integer.toString(Build.VERSION.SDK_INT);
			instance.USER_AGENT = new WebView(context.getApplicationContext()).getSettings().getUserAgentString();

//			SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
//			List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);
//			StringBuffer stb = new StringBuffer();
//			for (Sensor each : sensorList) {
//				if (stb.length() > 0) {
//					stb.append(',');
//				}
//				stb.append(each.getName());
//			}
//			instance.SENSORS = stb.toString();
		}
		return instance;
	}

	public String toString() {
		StringBuffer stb = new StringBuffer();
		if(FELICA!=null)
		stb.append("FELICA##" + FELICA).append("::");
		if(BOARD!=null)
		stb.append("BOARD##" + BOARD).append("::");
		if(BRAND!=null)
		stb.append("BRAND##" + BRAND).append("::");
		if(CPU_ABI!=null)
		stb.append("CPU_ABI##" + CPU_ABI).append("::");
		if(DEVICE!=null)
		stb.append("DEVICE##" + DEVICE).append("::");
		if(DISPLAY!=null)
		stb.append("DISPLAY##" + DISPLAY).append("::");
		if(FINGERPRINT!=null)
		stb.append("FINGERPRINT##" + FINGERPRINT).append("::");
		if(HOST!=null)
		stb.append("HOST##" + HOST).append("::");
		if(ID!=null)
		stb.append("ID##" + ID).append("::");
		if(MANUFACTURER!=null)
		stb.append("MANUFACTURER##" + MANUFACTURER).append("::");
		if(MODEL!=null)
		stb.append("MODEL##" + MODEL).append("::");
		if(PRODUCT!=null)
		stb.append("PRODUCT##" + PRODUCT).append("::");
		if(TAGS!=null)
		stb.append("TAGS##" + TAGS).append("::");
		if(TIME!=null)
		stb.append("TIME##" + TIME).append("::");
		if(TYPE!=null)
		stb.append("TYPE##" + TYPE).append("::");
		if(USER!=null)
		stb.append("USER##" + USER).append("::");
		if(VERSION_CODENAME!=null)
		stb.append("VERSION.CODENAME##" + VERSION_CODENAME).append("::");
		if(VERSION_INCREMENTAL!=null)
		stb.append("VERSION.INCREMENTAL##" + VERSION_INCREMENTAL).append("::");
		if(VERSION_RELEASE!=null)
		stb.append("VERSION.RELEASE##" + VERSION_RELEASE).append("::");
		if(VERSION_SDK!=null)
		stb.append("VERSION.SDK##" + VERSION_SDK).append("::");
		if(VERSION_SDK_INT!=null)
		stb.append("VERSION.SDK_INT##" + VERSION_SDK_INT).append("::");
		if(USER_AGENT!=null)
		stb.append("USER_AGENT##" + USER_AGENT);
//		stb.append("SENSORS##" + SENSORS);

		return stb.toString();
	}
}
