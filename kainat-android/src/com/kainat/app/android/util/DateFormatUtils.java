package com.kainat.app.android.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.content.Context;

public class DateFormatUtils {
	private DateFormatUtils() {
	}

	public static String convertDate(String date, String currentFormat, String newFormat,Context context) throws ParseException {
		
		
		Locale myLocale = new Locale(KeyValue.getString(context, KeyValue.LANGUAGE));
		
		SimpleDateFormat formatter = new SimpleDateFormat(currentFormat,myLocale);
		Date d = formatter.parse(date);
//		df = DateFormat.getDateInstance(style, Locale.FRANCE);
		SimpleDateFormat newFormatter = new SimpleDateFormat(newFormat,myLocale);
		return newFormatter.format(d);
	}
public static String convertDate(String date, String currentFormat, String newFormat) throws ParseException {
		
		
		Locale myLocale = new Locale("en");
		
		SimpleDateFormat formatter = new SimpleDateFormat(currentFormat,myLocale);
		Date d = formatter.parse(date);
//		df = DateFormat.getDateInstance(style, Locale.FRANCE);
		SimpleDateFormat newFormatter = new SimpleDateFormat(newFormat,myLocale);
		return newFormatter.format(d);
	}

	public static String convertGMTDateToCurrentGMTDate(String date, String currentFormat, String newFormat,Context context) throws ParseException {
		
		Locale myLocale = new Locale(KeyValue.getString(context, KeyValue.LANGUAGE));
		
		SimpleDateFormat formatter = new SimpleDateFormat(currentFormat,myLocale);
		formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
		Date d = formatter.parse(date);
		SimpleDateFormat newFormatter = new SimpleDateFormat(newFormat,myLocale);
		newFormatter.setTimeZone(TimeZone.getDefault());
		return newFormatter.format(d);
	}
	public static String convertGMTDateToCurrentGMTDate(String date, String currentFormat, String newFormat) throws ParseException {
		
		Locale myLocale = new Locale("en");
		
		SimpleDateFormat formatter = new SimpleDateFormat(currentFormat,myLocale);
		formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
		Date d = formatter.parse(date);
		SimpleDateFormat newFormatter = new SimpleDateFormat(newFormat,myLocale);
		newFormatter.setTimeZone(TimeZone.getDefault());
		return newFormatter.format(d);
	}
}
