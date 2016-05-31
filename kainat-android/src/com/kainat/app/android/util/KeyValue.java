package com.kainat.app.android.util;

import android.content.Context;
import android.content.SharedPreferences;

public class KeyValue {
	private static final String KeyValue = "KeyValue";
	public static final String CONTACT = "CONTACT";
	public static final String LANGUAGE = "LAN";
	public static final String LANGUAGE_CHANGE = "LANGUAGE_CHANGE";
	public static final String CREATE_COMMUNITY = "CREATE_COMMUNITY";
	public static final String VERIFIED = "VERIFIED";

	public static final String INTEREST = "INTEREST";
	
	
	

	public static void setInt(Context context, String key,int value) {
		SharedPreferences.Editor editor = context.getSharedPreferences(KeyValue, Context.MODE_PRIVATE).edit();
		editor.putInt(key, value);
		editor.commit();		
	}
	public static int getInt(Context context, String key) {
		SharedPreferences pref = context.getSharedPreferences(KeyValue,
				Context.MODE_PRIVATE);
		return pref.getInt(key, 0);		
	}
	public static void setLong(Context context, String key,long value) {
		SharedPreferences.Editor editor = context.getSharedPreferences(KeyValue, Context.MODE_PRIVATE).edit();
		editor.putLong(key, value);
		editor.commit();		
	}
	public static long getLong(Context context, String key) {
		SharedPreferences pref = context.getSharedPreferences(KeyValue,
				Context.MODE_PRIVATE);
		return pref.getLong(key, 0);		
	}
	public static void setString(Context context, String key,String value) {
		SharedPreferences.Editor editor = context.getSharedPreferences(KeyValue, Context.MODE_PRIVATE).edit();
		editor.putString(key, value);
		editor.commit();		
	}
	public static String getString(Context context, String key) {
		try{
		SharedPreferences pref = context.getSharedPreferences(KeyValue,
				Context.MODE_PRIVATE);
		return pref.getString(key,null);
		}catch (Exception e) {
			return null;
		}
	}
	
	public static void setBoolean(Context context, String key,boolean value) {
		SharedPreferences.Editor editor = context.getSharedPreferences(KeyValue, Context.MODE_PRIVATE).edit();
		editor.putBoolean(key, value);
		editor.commit();		
	}
	public static boolean getBoolean(Context context, String key) {
		try{
		SharedPreferences pref = context.getSharedPreferences(KeyValue,
				Context.MODE_PRIVATE);
		boolean res =  pref.getBoolean(key,false);
//		if(key!=null )//&& key.equalsIgnoreCase(SOUND))
//		{
//			if(res)
//				res = false;
//			else
//				res = true ;
//		}
		return res ;
		}catch (Exception e) {
			return false;
		}
	}
}
