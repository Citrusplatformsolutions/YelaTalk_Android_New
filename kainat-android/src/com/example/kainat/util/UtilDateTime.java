package com.example.kainat.util;

import android.content.Context;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import com.kainat.app.android.R;
import com.kainat.app.android.util.DateFormatUtils;

/**
 * Created by MANOJ SINGH on 5/14/2016.
 */
public class UtilDateTime {

	// Constants in seconds
	private static final long SECOND = 1;
	private static final long MINUTE = 60;
	private static final long HOUR = 3600;
	private static final long DAY = 86400;
	private static final long MONTH = 2592000;

	/**
	 * @param strDateTime
	 *            in "yyyy-MM-dd'T'HH:mm:ss.SSSZ" format eg
	 *            2001-07-04T12:08:56.235-0700
	 * @return Time difference between now and datetime passed as param. eq. if
	 *         passed date time is 2001-07-04T10:08:56.235-0700 and current is
	 *         2001-07-04T12:08:56.235-0700, it will return 2h
	 */
	public static synchronized String getTimePassed(String strDateTime)
			throws ParseException {
		try {
		/*	SimpleDateFormat dateFormatGmt = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");*/
			SimpleDateFormat dateFormatGmt = new SimpleDateFormat(
					"MMM dd, yyyy hh:mm:ss a");
			
			dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
			Date gmtDate = dateFormatGmt.parse(strDateTime);
			return getTimePassed(gmtDate);
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * @param gmtDate
	 *            Date object
	 * @return Time difference between now and datetime passed as param. eq. if
	 *         passed date time is 2001-07-04T10:08:56.235-0700 and current is
	 *         2001-07-04T12:08:56.235-0700, it will return 2h
	 * @throws ParseException
	 */
	public static synchronized String getTimePassed(Date gmtDate)
			throws ParseException {
		long diffInMillis = new Date().getTime() - gmtDate.getTime();
		long diffInSeconds = diffInMillis / 1000;

		String strDiff = "unknown";
		if (diffInSeconds < 5 * SECOND)
			strDiff = "Just Now";
		else if (diffInSeconds < 60 * SECOND)
			strDiff = diffInSeconds + " sec ago";
		else if (diffInSeconds < 120 * SECOND)
			strDiff = " a min ago";
		else if (diffInSeconds < HOUR)
			strDiff = diffInSeconds / MINUTE + " min ago";
		else if (diffInSeconds < 2 * HOUR)
			strDiff = " an hour ago";
		else if (diffInSeconds < DAY)
			strDiff = diffInSeconds / HOUR + " hrs ago";
		else if (diffInSeconds < 2 * DAY)
			strDiff = " yesterday";
		else if (diffInSeconds <= MONTH)
			strDiff = diffInSeconds / DAY + " days ago";
		else {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(gmtDate);
			Calendar calendarCurrent = Calendar.getInstance();
			calendarCurrent.setTime(new Date());
			if (calendarCurrent.get(Calendar.YEAR) == calendar
					.get(Calendar.YEAR)) {
				SimpleDateFormat dateFormatWithoutYear = new SimpleDateFormat(
						"d MMM");
				strDiff = dateFormatWithoutYear.format(calendar.getTime());
			} else {
				SimpleDateFormat dateFormatWithoutYear = new SimpleDateFormat(
						"d MMM yyyy");
				strDiff = dateFormatWithoutYear.format(calendar.getTime());
			}
		}
		return strDiff;
	}

	public static synchronized String compareDate(String date, Context context) {
		date = date.trim();
		date = date.replaceAll("\n", " ");
		try {
			StringBuilder time = new StringBuilder();
			SimpleDateFormat sdf = null;
			if (date.indexOf('/') != -1) {
				// "dd/MM/yyyy HH:mm:ss"
				sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			} else {
				date = DateFormatUtils.convertGMTDateToCurrentGMTDate(date,
						"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss");
				sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			}
			java.util.Date past = sdf.parse(date);
			java.util.Date now = new java.util.Date();
			String s = sdf.format(now);
			now = sdf.parse(s);

			long agosecond = Math.abs(TimeUnit.MILLISECONDS.toSeconds(now
					.getTime() - past.getTime()));

			if (agosecond < 110)
				agosecond = 0;

			// System.out.println("-------compareDate-----now-----"+now
			// .getTime());
			// System.out.println("-------compareDate----server-----"+past
			// .getTime());

			int seconds = (int) (agosecond % 60);
			int minutes = (int) ((agosecond / 60) % 60);
			int hours = (int) ((agosecond / 3600) % 24);

			long agodays = ((agosecond / 86400));
			long month = ((agodays / 30));

			if (month > 0) {
				time.append(month);
				if (month == 1)
					time.append(" " + context.getString(R.string.month_ago));// month
				// ago
				else
					time.append(" " + context.getString(R.string.month_ago));// months
				// ago

				if (month > 12)
					return (new Date()).toLocaleString();// oGMTString();///Sep
				// 14, 2013 12:33:19
				// AM

				return time.toString();
			} else if (agodays > 0) {
				time.append(agodays);
				if (agodays == 1)
					time.append(" " + context.getString(R.string.days_ago));// day
				// ago
				else
					time.append(" " + context.getString(R.string.days_ago));// days
				// ago
				return time.toString();
			} else if (hours > 0) {
				if (hours == 1) {
					time.append(hours);
					time.append("h");
				} else {

					time.append(hours);
					time.append("h");
				}
				if (minutes > 0) {
					// time.append(" and ");
					time.append(" ");
					time.append(minutes);

					if (minutes == 1)
						time.append(context.getString(R.string.m_ago));// m ago
					else
						time.append(context.getString(R.string.m_ago));// m ago
				} else {
					time.append(" " + context.getString(R.string.ago));// ago
				}
				return time.toString();
			} else if (minutes > 0) {

				time.append(minutes);
				if (minutes == 1)
					time.append("m");
				else
					time.append("m");
				if (seconds > 0) {
					// time.append(" and ");
					time.append(" ");
					time.append(seconds);
					if (seconds == 1)
						time.append(context.getString(R.string.s_ago));
					else
						time.append(context.getString(R.string.s_ago));
				} else {
					time.append(" " + context.getString(R.string.ago));
				}
				return time.toString();
			} else {
				if (agosecond < 60) {
					time.append(context.getString(R.string.a_moment_ago));
				} else {
					time.append(agosecond);
					if (agosecond == 1)
						time.append(context.getString(R.string.s_ago));
					else
						time.append(context.getString(R.string.s_ago));
				}
				return time.toString();
			}
		} catch (Exception ex) {
			if (date != null)
				return date;
			ex.printStackTrace();

		}
		return date;
	}
	
	
	public static String convertToDateStringWithsdf(String unformateddate,
			SimpleDateFormat sdf) {
		// TODO Auto-generated method stub
		Date date = null;
		String dateStr = unformateddate;
		try {

			date = sdf.parse(unformateddate);
			dateStr = new SimpleDateFormat("dd MMM yyyy").format(date);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return dateStr;
	}

	public static boolean containsDigit(String s) {
		boolean containsDigit = false;

		if (s != null && !s.isEmpty()) {
			for (char c : s.toCharArray()) {
				if (containsDigit = Character.isDigit(c)) {
					break;
				}
			}
		}

		return containsDigit;
	}

	public static Date String2Date(String datestr) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = sdf.parse(datestr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return date;
	}

	public static String setTimeAfter(String dateCreatedString) {

		Calendar now = Calendar.getInstance();
		Calendar day = Calendar.getInstance();
		try {
			day.setTime(new SimpleDateFormat("yyyy-MM-dd")
					.parse(dateCreatedString));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		String endDateFormated = "";

		Date nowDate = now.getTime();
		Date lastDate = day.getTime();

		endDateFormated = getDateDiffString(nowDate, lastDate);
		Log.e("endDateFormated", "nowDate :" + nowDate + "lastDate :"
				+ lastDate + "after:" + endDateFormated);
		return endDateFormated;

	}

	public static String getDateDiffString(Date lastDate, Date dateTwo) {
		long timeOne = lastDate.getTime();
		long timeTwo = dateTwo.getTime();
		long oneDay = 1000 * 60 * 60 * 24;
		long delta = (timeTwo - timeOne) / oneDay;

		/*
		 * long year = delta / 365; long rest = delta % 365; long month = rest /
		 * 30; rest = rest % 30; long weeks = rest / 7; long days = rest % 7;
		 */
		if (delta > 0) {
			if (delta <= 29) {
				return delta + " days left";
			} else if (delta >= 30 && delta <= 59) {
				return "1 Month left";
			} else if (delta >= 60 && delta <= 89) {
				return "2 Month left";
			} /*
			 * else if (delta >= 90 && delta <= 119) { return "3 Month left"; }
			 */else {
				String monthFormat = (String) android.text.format.DateFormat
						.format("MMM", dateTwo);
				String yearFormat = (String) android.text.format.DateFormat
						.format("yyyyy", dateTwo);
				String dayFormat = (String) android.text.format.DateFormat
						.format("dd", dateTwo); // 20
				String time = null;

				return dayFormat + "-" + monthFormat + "-" + yearFormat;
			}
		} else {
			delta *= -1;
			if (delta == 0) {
				return "Today";
			} else if (delta == 1) {
				return "Yesterday";
			} else {
				String monthFormat = (String) android.text.format.DateFormat
						.format("MMM", dateTwo);
				String yearFormat = (String) android.text.format.DateFormat
						.format("yyyyy", dateTwo);
				String dayFormat = (String) android.text.format.DateFormat
						.format("dd", dateTwo); // 20
				String time = null;

				return dayFormat + "-" + monthFormat + "-" + yearFormat;
			}
		}

	}

}
