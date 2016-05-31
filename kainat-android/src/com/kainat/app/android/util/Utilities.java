package com.kainat.app.android.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.FileObserver;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.DatePicker;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.kainat.app.android.C2DMBroadcastReceiver;
import com.kainat.app.android.ConversationsActivity;
import com.kainat.app.android.KainatHomeActivity;
import com.kainat.app.android.ProfileViewActivity;
import com.kainat.app.android.R;
import com.kainat.app.android.UIActivityManager;
import com.kainat.app.android.bean.DiscoveryUrl;
import com.kainat.app.android.bean.LandingPageBean;
import com.kainat.app.android.bean.LeftMenu;
import com.kainat.app.android.bean.Message;
import com.kainat.app.android.bean.ShiftNewMessage;
import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.engine.ComposeService;
import com.kainat.app.android.engine.DBEngine;
import com.kainat.app.android.engine.RefreshService;
import com.kainat.app.android.helper.FeedTask;
import com.kainat.app.android.helper.LandingPageContentProvider;
import com.kainat.app.android.helper.LandingPageTable;
import com.kainat.app.android.helper.MessageConversationTable;
import com.kainat.app.android.helper.MessageTable;
import com.kainat.app.android.helper.SettingTable;
import com.kainat.app.android.helper.db.media.MediaContentProvider;
import com.kainat.app.android.util.format.SettingData;

public final class Utilities {

	private static final String TAG = Utilities.class.getSimpleName();
	public static String sMacAddress;
	public static String sPhoneNumber;
	private static String sIMEINumber;
	public static String sPhoneModel;// = "iPod touch";
	public static String sPhoneLanguage = "1";
	public static String sOSVersion;// = "iPod touch";
	public static String sRegId = null;
	public static String chatpath = "/rockeTalk/datastorech/";
	public static String draftPostPath = "/rockeTalk/.draftpostdata/";
	public static String sendPostPath = "/rockeTalk/.sendpostdata/";
	public static String homeTab = "homeTab";
//	String pattern = "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}\\@[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}(\\.[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25})+"; //Old Email pattern
	public static final Pattern EMAIL_PATTERN = Pattern.compile("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
	public static final Pattern MOBILE_NUMBER_PATTERN = Pattern.compile("^\\d{1,5}[-]\\d{6,12}$");
	public static final Pattern USERNAME_PATTERN = Pattern.compile("^[A-Za-z][A-Za-z0-9_]{0,29}$");
	public static final Pattern NAME_PATTERN = Pattern.compile("^\\s*([A-Za-z]{3,})(\\s*([A-Za-z ]+?))?\\s*$");

	public static void loadValues(Context context) {
		// System.out.println("total row=="
		// + BusinessProxy.sSelf.getRecordCount(DBEngine.IMEI_TABLE));
		if (BusinessProxy.sSelf.getPushImeiId(DBEngine.IMEI_TABLE) != null) {

			sIMEINumber = BusinessProxy.sSelf
					.getPushImeiId(DBEngine.IMEI_TABLE);
		} else {

			sIMEINumber = getPhoneIMEINumber(context);

			// sIMEINumber= getBluetoothId();
			sIMEINumber = getWifiMacId(context);
			sIMEINumber = generateUniqueCode();
			ContentValues conValues = new ContentValues();
			conValues.put("imei", sIMEINumber);
			List<ContentValues> values = new ArrayList<ContentValues>();
			values.add(conValues);

			BusinessProxy.sSelf
					.insertValuesInTable(DBEngine.IMEI_TABLE, values);
			// System.out.println("total row=="+BusinessProxy.sSelf.getRecordCount(DBEngine.IMEI_TABLE));
		}
		getPhoneNumber(context);
		getWifiMacAddress(context);
		getPhoneModel(context);
	}

	public static final int bytesToInt(byte[] b, int offset, int byteLen) {
		int x, y = 0;
		int mask = (byteLen - 1) * 8;
		for (int i = offset; i < offset + byteLen; i++) {
			x = signedToUnsigned((int) b[i]);
			y |= ((x & 0x000000ff) << mask);
			mask -= 8;
		}
		return y;
	}

	private static final int signedToUnsigned(int a) {
		if (a < 0)
			return a + 256;
		return a;
	}

	public static final int getColor(int r, int g, int b) {
		return ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
	}

	// public static String convertTime(String aTimeToConvert) {
	// StringBuffer convertedTime = new StringBuffer();
	// try {
	// Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
	// long currGMT = cal.getTime().getTime();
	//
	// int temp = Integer.parseInt(aTimeToConvert.substring(0, 4));
	// cal.set(Calendar.YEAR, temp);
	// temp = Integer.parseInt(aTimeToConvert.substring(5, 7));
	// cal.set(Calendar.MONTH, temp - 1);
	// temp = Integer.parseInt(aTimeToConvert.substring(8, 10));
	// cal.set(Calendar.DAY_OF_MONTH, temp);
	// temp = Integer.parseInt(aTimeToConvert.substring(11, 13));
	// cal.set(Calendar.HOUR_OF_DAY, temp);
	// temp = Integer.parseInt(aTimeToConvert.substring(14, 16));
	// cal.set(Calendar.MINUTE, temp);
	// temp = Integer.parseInt(aTimeToConvert.substring(17, 19));
	// cal.set(Calendar.SECOND, temp);
	// long recGMT = cal.getTime().getTime();
	// long offset = currGMT - recGMT;
	// cal = Calendar.getInstance();
	// long currLocal = System.currentTimeMillis() - offset;
	// cal.setTime(new Date(currLocal));
	// if (cal.get(Calendar.DAY_OF_MONTH) < 10)
	// convertedTime.append('0');
	// convertedTime.append(cal.get(Calendar.DAY_OF_MONTH));
	// convertedTime.append('/');
	// if ((cal.get(Calendar.MONTH) + 1) < 10)
	// convertedTime.append('0');
	// convertedTime.append(cal.get(Calendar.MONTH) + 1);
	// convertedTime.append('/');
	// convertedTime.append(cal.get(Calendar.YEAR));
	// convertedTime.append('\n');
	// if (cal.get(Calendar.HOUR) >= 0 && cal.get(Calendar.HOUR) < 10)
	// convertedTime.append('0');
	// if (cal.get(Calendar.AM_PM) == 1) {
	// convertedTime.append(12 + cal.get(Calendar.HOUR));
	// } else
	// convertedTime.append(cal.get(Calendar.HOUR));
	// convertedTime.append(':');
	// if (cal.get(Calendar.MINUTE) >= 0 && cal.get(Calendar.MINUTE) < 10)
	// convertedTime.append('0');
	// convertedTime.append(cal.get(Calendar.MINUTE));
	// convertedTime.append(':');
	// if (cal.get(Calendar.SECOND) < 10)
	// convertedTime.append('0');
	// convertedTime.append(cal.get(Calendar.SECOND));
	// if (cal.get(Calendar.AM_PM) == 1)
	// convertedTime.append("PM");
	// else
	// convertedTime.append("AM");
	// } catch (Exception ex) {
	// convertedTime.delete(0, convertedTime.length());
	// convertedTime.append(aTimeToConvert);
	// }
	// return convertedTime.toString();
	// }

	public static String convertTime(String aTimeToConvert) {
		SimpleDateFormat serverFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		serverFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		Date serverDate = null;
		try {
			serverDate = serverFormat.parse(aTimeToConvert);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		SimpleDateFormat localFormat = new SimpleDateFormat(
				"dd/MM/yyyy HH:mm:ss");
		localFormat.setTimeZone(TimeZone.getDefault());
		if (serverDate != null) {
			return localFormat.format(serverDate);
		}
		return aTimeToConvert;
	}

	private static String getWifiMacAddress(Context context) {
		if (null != sMacAddress)
			return sMacAddress;
		try {
			WifiManager wifiManager = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			if (null != wifiManager) {
				WifiInfo info = wifiManager.getConnectionInfo();
				if (null != info) {
					sMacAddress = info.getMacAddress();
					if (null != sMacAddress) {
						sMacAddress = sMacAddress.toUpperCase();
					} else {
						sMacAddress = "";
					}
				}
			}
		} catch (Exception _ex) {
			if (Logger.ENABLE)
				Logger.error(TAG, "getWifiMacAddress--" + _ex.getMessage(), _ex);
		}
		return sMacAddress;
	}

	public static boolean isNetworkAvailable(Context context) {
		try {
			ConnectivityManager cm = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netinfo = cm.getActiveNetworkInfo();
			if (null != netinfo) {
				State state = netinfo.getState();
				if (NetworkInfo.State.CONNECTED == state
						|| NetworkInfo.State.CONNECTING == state
						|| NetworkInfo.State.SUSPENDED == state) {
					return true;
				}
			}
		} catch (Exception _ex) {
			if (Logger.ENABLE)
				Logger.error(TAG, "--isNetworkAvailable--" + _ex.getMessage(),
						_ex);
		}
		return false;
	}

	/**
	 * This method gets the phone number.
	 * 
	 * @param context
	 *            Context to access the {@link TelephonyManager} manager.
	 * @return Phone number if found other wise "".
	 */
	private static String getPhoneNumber(Context context) {
		if (null != sPhoneNumber)
			return sPhoneNumber;
		try {
			TelephonyManager telphonyManager = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			return sPhoneNumber = telphonyManager.getLine1Number();
		} catch (Exception _ex) {
			if (Logger.ENABLE)
				Logger.error(TAG, "--getPhoneNumber--" + _ex.getMessage(), _ex);
		}
		return null;
	}

	/**
	 * This method gets the phone IMEI number.
	 * 
	 * @param context
	 *            Context to access the {@link TelephonyManager} manager.
	 * @return Phone IMEI number if found other wise "".
	 */
	public static String getPhoneIMEINumber(Context context) {
		if (null != sIMEINumber)
			return sIMEINumber;
		try {
			TelephonyManager telphonyManager = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			sIMEINumber = telphonyManager.getDeviceId();
			// System.out.println("-------------sIMEINumber---------"+sIMEINumber);
			// if(sIMEINumber == null || sIMEINumber.equalsIgnoreCase("null")){
			// return sIMEINumber = telphonyManager.getDeviceId();
			// }
		} catch (Exception _ex) {
			if (Logger.ENABLE)
				Logger.error(TAG, "--getPhoneIMEINumber--" + _ex.getMessage(),
						_ex);
		}
		return sIMEINumber;
	}

	public static String getPhoneIMEINumberMethod(Context context) {
		if (null != sIMEINumber)
			return sIMEINumber;
		try {
			TelephonyManager telphonyManager = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			sIMEINumber = telphonyManager.getDeviceId();
			// System.out.println("-------------sIMEINumber---------"+sIMEINumber);
			// if(sIMEINumber == null || sIMEINumber.equalsIgnoreCase("null")){
			// return sIMEINumber = telphonyManager.getDeviceId();
			// }

			if (sIMEINumber == null) {
				sIMEINumber = "123456789";
			}
		} catch (Exception _ex) {
			if (Logger.ENABLE)
				Logger.error(TAG, "--getPhoneIMEINumber--" + _ex.getMessage(),
						_ex);
		}
		return sIMEINumber;
	}

	// private static String getBluetoothId(){
	// if (null != sIMEINumber)
	// return sIMEINumber;
	// BluetoothAdapter btAdapt= null;
	// String address=null;
	// btAdapt = BluetoothAdapter.getDefaultAdapter();
	// if(btAdapt!=null){
	// address= btAdapt.getAddress();
	// }else{
	// address=null;
	// }
	// return address;
	// }

	private static String getWifiMacId(Context context) {
		if (null != sIMEINumber)
			return sIMEINumber;
		String address = null;
		WifiManager wimanager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		if (wimanager != null)
			address = wimanager.getConnectionInfo().getMacAddress();
		else
			address = null;

		return address;
	}

	private static String generateUniqueCode() {
		if (null != sIMEINumber)
			return sIMEINumber;

		String code = "1234567890";

		try {
			code = "" + Math.abs((new Random().nextLong()));

			return code;
		} catch (NumberFormatException nEx) {
			return code;
		}
	}

	public static String getPhoneIMEINumber() {
		// if (sRegId != null)
		// return sIMEINumber + ";" + sRegId;//+";"+token ;
		return sIMEINumber;// +";"+token ;
	}

	public static String getPhoneIMEINumberWithPushReg() {
		// System.out.println("sRegId ::::::::::::::::::::::::::: "+sRegId);
		// System.out.println("sIMEINumber ::::::::::::::::::::::::::: "+sIMEINumber);
		if (sRegId != null)
			return sIMEINumber + ";" + sRegId;// +";"+token ;
		return sIMEINumber;// +";"+token ;
	}

	public static String getGoogleToken() {
		// System.out.println("sRegId ::::::::::::::::::::::::::: "+sRegId);
		// System.out.println("sIMEINumber ::::::::::::::::::::::::::: "+sIMEINumber);
//		if (sRegId != null)
			return sRegId;// +";"+token ;
//		return sIMEINumber;// +";"+token ;
	}

	private static String getPhoneModel(Context context) {
		if (null != sPhoneModel)
			return sPhoneModel;
		sPhoneModel = Build.MODEL;
		return sPhoneModel;// +";"+registration+";"+token ;
	}

	public static byte[] loadResToByteArray(int resId, Context ctx) {
		byte[] s = null;
		try {
			InputStream is = ctx.getResources().openRawResource(resId);
			s = new byte[is.available()];
			is.read(s);
			is.close();
		} catch (Exception e) {
		}
		return s;
	}

	public static InputStream LoadInputStreamFromWebOperations(String url) {
		try {
			InputStream is = (InputStream) new URL(url).getContent();

			return is;
		} catch (Exception e) {
			e.printStackTrace();
			// System.out.println("Exc=" + e);
			return null;
		}
	}

	public static byte[] inputStreamToByteArray(InputStream inputStream) {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		int nRead;
		byte[] data = new byte[100];

		try {
			while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
				buffer.write(data, 0, nRead);
			}
		} catch (IOException e) {

			e.printStackTrace();
		}

		try {
			buffer.flush();
		} catch (IOException e) {

			e.printStackTrace();
		}

		return buffer.toByteArray();
	}

	public static String replaceAll(String target, String from, String to) {

		int start = target.indexOf(from);
		if (start == -1)
			return target;
		int lf = from.length();
		char[] targetChars = target.toCharArray();
		StringBuffer buffer = new StringBuffer();
		int copyFrom = 0;
		while (start != -1) {
			buffer.append(targetChars, copyFrom, start - copyFrom);
			buffer.append(to);
			copyFrom = start + lf;
			start = target.indexOf(from, copyFrom);
		}
		buffer.append(targetChars, copyFrom, targetChars.length - copyFrom);
		return buffer.toString();
	}

	public static void closeSoftKeyBoard(View view, Activity context) {
		try {
			InputMethodManager imm = (InputMethodManager) context
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private static final int MAX_DATA_CHUNK = 1024 * 100;

	public static String saveDataIntoFile(byte iRequestData[], String fileExten) {
		String absPath = "";

		int iTotalBytesDone = 0;
		try {
			int dataLength = iRequestData.length;
			File file = new File(UIActivityManager.cacheDir, "/"
					+ System.currentTimeMillis() + "_rtin" + fileExten);
			absPath = file.getAbsolutePath();
			if (!file.exists()) {
				file.createNewFile();
				if (file.canWrite()) {
					FileOutputStream lOutputStream = new FileOutputStream(file);
					while (dataLength > 0) {
						if (dataLength >= MAX_DATA_CHUNK) {
							if (null != lOutputStream) {
								lOutputStream.write(iRequestData,
										iTotalBytesDone, MAX_DATA_CHUNK);
							}
							iTotalBytesDone += MAX_DATA_CHUNK;
							dataLength -= MAX_DATA_CHUNK;
						} else {
							if (null != lOutputStream) {
								lOutputStream.write(iRequestData,
										iTotalBytesDone, dataLength);
							}
							iTotalBytesDone += dataLength;
							dataLength = 0;
						}
					}
					lOutputStream.flush();
					lOutputStream.close();
					// out.close();
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
		return absPath;
	}

	public static String getCurrentdate() {
		Calendar cal = Calendar.getInstance();
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		return sdf.format(d);
		// return convertlongToDate(cal.getTime().getTime());
	}

	public static String convertlongToDate(long timestamp) {
		StringBuilder time = new StringBuilder();
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timestamp);
		if (calendar.get(Calendar.DAY_OF_MONTH) < 10)
			time.append('0');
		time.append(calendar.get(Calendar.DAY_OF_MONTH));
		time.append('/');
		if ((calendar.get(Calendar.MONTH) + 1) < 10)
			time.append('0');
		time.append(calendar.get(Calendar.MONTH) + 1);
		time.append('/');
		time.append(calendar.get(Calendar.YEAR));
		time.append('\n');
		if (calendar.get(Calendar.HOUR) >= 0
				&& calendar.get(Calendar.HOUR) < 10)
			time.append('0');
		if (calendar.get(Calendar.AM_PM) == 1) {
			time.append(12 + calendar.get(Calendar.HOUR));
		} else
			time.append(calendar.get(Calendar.HOUR));
		time.append(':');
		if (calendar.get(Calendar.MINUTE) < 10)
			time.append('0');
		time.append(calendar.get(Calendar.MINUTE));
		time.append(':');
		if (calendar.get(Calendar.SECOND) < 10)
			time.append('0');
		time.append(calendar.get(Calendar.SECOND));
		if (calendar.get(Calendar.AM_PM) == 1)
			time.append("PM");
		else
			time.append("AM");

		return time.toString();
	}

	public static long getDateStringToMilisec(String date) {// 13/05/2011
															// 9:41:14PM

		try {
			date = date.trim();
			date = date.replaceAll("\n", " ");
			StringBuilder time = new StringBuilder();
			SimpleDateFormat sdf = null;

			if (date.indexOf("-") != -1) {
				date = DateFormatUtils.convertGMTDateToCurrentGMTDate(date,
						"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss");
				sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			} else {
				sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			}

			// sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			// sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			java.util.Date past = sdf.parse(date);
			java.util.Date now = new java.util.Date();
			// System.out.println(past.toString()+"-----------"+now.toString());
			String s = sdf.format(now);
			now = sdf.parse(s);
			// long agosecond =
			// Math.abs(TimeUnit.MILLISECONDS.toSeconds(now.getTime() -
			// past.getTime()));
			return past.getTime();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return System.currentTimeMillis();
		}
		// return System.currentTimeMillis() ;
	}
	
	public static long gettimemillis(String datetime)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long convertedtime = TimeZone.getDefault().getRawOffset() + 18000000l;
		if(datetime.indexOf('/') != -1)
		{
			sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");//24/01/2015 00:38:52//2015-01-30T01:54:42-05:00
			convertedtime = 0;
		}
		Date date = null;
		//2015-01-06T17:45:15-05:00, 2015-01-23 18:37:30
		if(datetime == null)
			return convertedtime;
		if(datetime.length() > 19)
		{
		if(datetime.lastIndexOf('-') != -1)
			datetime = datetime.substring(0, datetime.lastIndexOf('-'));
		else if(datetime.lastIndexOf('+') != -1)
			datetime = datetime.substring(0, datetime.lastIndexOf('+'));
		if(datetime.indexOf('T') != -1)
			datetime = datetime.replace('T', ' ');
		}
		
		try {
			date = sdf.parse(datetime);
			System.out.println(date.getTime());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(date != null)
			convertedtime += date.getTime();
		return convertedtime;
	}

	public static long dateToLong(String date) {// 13/05/2011 9:41:14PM

		try {
			// System.out.println("------------date : " + date);
			date = date.trim();
			date = date.replaceAll("\n", " ");
			StringBuilder time = new StringBuilder();
			SimpleDateFormat sdf = null;

			if (date.indexOf("-") != -1) {
				date = DateFormatUtils.convertGMTDateToCurrentGMTDate(date,
						"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss");
				sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			} else {
				sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			}

			java.util.Date past = sdf.parse(date);
			return past.getTime();
		} catch (Exception e) {
			return System.currentTimeMillis();
		}
	}

	public static String compareDate(String date) {// 13/05/2011 9:41:14PM

		try {
			// System.out.println("------------date : " + date);//2015-01-30T01:54:42-05:00
			date = date.trim();
			date = date.replaceAll("\n", " ");
			StringBuilder time = new StringBuilder();
			SimpleDateFormat sdf = null;

			if (date.indexOf("-") != -1) {
				date = DateFormatUtils.convertGMTDateToCurrentGMTDate(date,
						"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss");
				sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			} else {
				sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			}

			// sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			// sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			java.util.Date past = sdf.parse(date);
			java.util.Date now = new java.util.Date();
			// System.out.println(past.toString()+"-----------"+now.toString());
			String s = sdf.format(now);
			now = sdf.parse(s);
			long agosecond = Math.abs(TimeUnit.MILLISECONDS.toSeconds(now
					.getTime() - past.getTime()));

			int seconds = (int) (agosecond % 60);
			int minutes = (int) ((agosecond / 60) % 60);
			int hours = (int) ((agosecond / 3600) % 24);

			long agodays = ((agosecond / 86400));
			// System.out.println("agodays===="+agodays);
			long month = ((agodays / 30));
			if (month > 0) {
				time.append(month);
				if (month == 1)
					time.append(" month ago");
				else
					time.append(" months ago");
				return time.toString();
			} else if (agodays > 0) {
				time.append(agodays);
				if (agodays == 1)
					time.append(" day ago");
				else
					time.append(" days ago");
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
						time.append("m ago");
					else
						time.append("m ago");
				} else {
					time.append(" ago");
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
						time.append("s ago");
					else
						time.append("s ago");
				} else {
					time.append(" ago");
				}
				return time.toString();
			} else {
				time.append(agosecond);
				if (agosecond == 1)
					time.append("s ago");
				else
					time.append("s ago");
				return time.toString();
			}

		} catch (Exception ex) {
			if (date != null)
				return date;
			ex.printStackTrace();
			return "NA";
		}

	}

	public static long CompareTime(long value) {

		Long xx = (System.currentTimeMillis() - value) / 1000;
		/*
		 * java.util.Date past = new Date(value);//sdf.parse(date);
		 * java.util.Date now = new java.util.Date(); //
		 * System.out.println(past.toString()+"-----------"+now.toString()); //
		 * String s = sdf.format(now); // now = sdf.parse(s); long agosecond =
		 * Math.abs(TimeUnit.MILLISECONDS.toSeconds(now .getTime() -
		 * past.getTime()));
		 * 
		 * int seconds = (int) (agosecond % 60);
		 */
		// System.out.println("xx===="+xx);
		return xx;
	}

	public static String compareDate(long date,Context context) {// 13/05/2011 9:41:14PM

		try {
//			date = date + (1000*60*60*5)
//					DateFormatUtils.
//			 System.out.println("------------date : " + date);
			// date = date.trim();
			// date = date.replaceAll("\n", " ");
			StringBuilder time = new StringBuilder();
			// SimpleDateFormat sdf = null;
			//
			// if (date.indexOf("-") != -1) {
			// date = DateFormatUtils.convertGMTDateToCurrentGMTDate(date,
			// "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss");
			// sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			// } else {
			// sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			// }

			// sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			// sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			java.util.Date past = new Date(date);// sdf.parse(date);
			java.util.Date now = new java.util.Date();
			// System.out.println(past.toString()+"-----------"+now.toString());
			// String s = sdf.format(now);
			// now = sdf.parse(s);
			long agosecond = Math.abs(TimeUnit.MILLISECONDS.toSeconds(now
					.getTime() - past.getTime()));
			
			if(agosecond < 0)
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
			
//			if (month > 0) {
//				time.append(month);
//				if (month == 1)
//					time.append(" month ago");
//				else
//					time.append(" months ago");
//				return time.toString();
//			} else if (agodays > 0) {
//				time.append(agodays);
//				if (agodays == 1)
//					time.append(" day ago");
//				else
//					time.append(" days ago");
//				return time.toString();
//			}
			if (month > 0) {
				time.append(month);
				if (month == 1)
					time.append(" "+context.getString(R.string.month_ago));//month ago
				else
					time.append(" "+context.getString(R.string.month_ago));//months ago

				if (month > 12)
					return (new Date()).toLocaleString();// oGMTString();///Sep
															// 14, 2013 12:33:19
															// AM

				return time.toString();
			} else if (agodays > 0) {
				time.append(agodays);
				if (agodays == 1)
					time.append(" "+context.getString(R.string.days_ago));//day ago
				else
					time.append(" "+context.getString(R.string.days_ago));//days ago
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
						time.append(context.getString(R.string.m_ago));//m ago
					else
						time.append(context.getString(R.string.m_ago));//m ago
				} else {
					time.append(" "+context.getString(R.string.ago));// ago
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
					time.append(" "+context.getString(R.string.ago));
				}
				return time.toString();
			} else {
				if(agosecond < 60)
				{
					time.append(context.getString(R.string.a_moment_ago));
				}
				else
				{
				time.append(agosecond);
				if (agosecond == 1)
				time.append(context.getString(R.string.s_ago));
				else
					time.append(context.getString(R.string.s_ago));
				}
				return time.toString();
			}

		} catch (Exception ex) {
			// if (date != null)

			ex.printStackTrace();
			return "" + date;
		}

	}

	public static boolean canWeWriteInSDCard() {
		boolean mExternalStorageAvailable = false;
		boolean mExternalStorageWriteable = false;
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// We can read and write the media
			mExternalStorageAvailable = mExternalStorageWriteable = true;

		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			// We can only read the media
			mExternalStorageAvailable = true;
			mExternalStorageWriteable = false;
		} else {
			// Something else is wrong. It may be one of many other states, but
			// all we need
			// to know is we can neither read nor write
			mExternalStorageAvailable = mExternalStorageWriteable = false;
		}
		if (mExternalStorageAvailable && mExternalStorageWriteable) {
			if (Logger.ENABLE)
				System.out
						.println("***********************SD CARD AVAILABLE**************************");
			return true;
		} else {
			if (Logger.ENABLE)
				System.out
						.println("***********************SD CARD NOT AVAILABLE**************************");
			return false;
		}
	}

	public static boolean needToSave() {
		try {
			if (!canWeWriteInSDCard())
				return false;
			String[] chatList = Utilities.getChatList();
			if (chatList == null || chatList.length == 0)
				return true;
			/*
			 * int t = 0 ; for (int i = 0; i < chatList.length; i++) { String
			 * currentChatter =
			 * HexStringConverter.getHexStringConverterInstance(
			 * ).hexToString(chatList
			 * [i].substring(0,chatList[i].indexOf('.')));/
			 * /(SettingData.sSelf.getUserName() + "_" + key) ;;//key;
			 * 
			 * if(!currentChatter.startsWith(SettingData.sSelf.getUserName()))
			 * continue; t++ ; }
			 */
			int t = 0;
			boolean willSaved = false;
			Set chatData = BusinessProxy.sSelf.chatData.keySet();// .get(mOtherName);
			Iterator<String> iterator = chatData.iterator();
			while (iterator.hasNext()) {
				String key = iterator.next();
				for (int i = 0; i < chatList.length; i++) {
					String currentChatter = HexStringConverter
							.getHexStringConverterInstance().hexToString(
									chatList[i].substring(0,
											chatList[i].indexOf('.')));// (SettingData.sSelf.getUserName()
																		// + "_"
																		// +
																		// key)
																		// ;;//key;

					if (!currentChatter.startsWith(SettingData.sSelf
							.getUserName()))
						continue;

					if (currentChatter.indexOf(key) != -1)
						t++;
				}
				if (t != chatList.length)
					willSaved = true;
			}
			if (Logger.ENABLE)
				System.out
						.println("eill save------------------------------------"
								+ willSaved);
			return willSaved;
			// if(t==BusinessProxy.sSelf.chatData.size())
			// return false;
			// return true ;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
	}

	public static String[] getChatList() {

		// Vector<String> chatter = new Vector<String>();
		if (canWeWriteInSDCard()) {
			try {

				String path = Environment.getExternalStorageDirectory()
						.getAbsolutePath() + chatpath;// "/RockeTalk/Chat/";
				if (Logger.ENABLE)
					Logger.debug(TAG,
							"--------------------------------------getChatList-----------"
									+ path);
				boolean exists = (new File(path)).exists();
				if (exists) {
					File file = new File(path);//
					if (file.isDirectory()) {
						return file.list();
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return null;
	}

	public static String[] getFileList(String path) {

		// Vector<String> chatter = new Vector<String>();
		if (canWeWriteInSDCard()) {
			try {

				// path = Environment.getExternalStorageDirectory()
				// .getAbsolutePath() + chatpath;// "/RockeTalk/Chat/";
				if (Logger.ENABLE)
					Logger.debug(TAG,
							"--------------------------------------getChatList-----------"
									+ path);
				boolean exists = (new File(path)).exists();
				if (exists) {
					File file = new File(path);//
					if (file.isDirectory()) {
						return file.list();
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return null;
	}

	public static String[] getAllfile(String path) {

		// Vector<String> chatter = new Vector<String>();
		if (canWeWriteInSDCard()) {
			try {

				// String path =
				// Environment.getExternalStorageDirectory().getAbsolutePath() +
				// "/RockeTalk/Chat/";
				if (Logger.ENABLE)
					Logger.debug(TAG,
							"--------------------------------------getChatList-----------"
									+ path);
				boolean exists = (new File(path)).exists();
				if (exists) {
					File file = new File(path);//
					if (file.isDirectory()) {
						return file.list();
					}

				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return null;
	}

	public static boolean moveFileToSdCard(String sourceFilepath) {

		FileInputStream fis = null;
		FileOutputStream fos = null;
		FileChannel in = null;
		FileChannel out = null;

		try {
			// File root = Environment.getExternalStorageDirectory();
			// File file = new
			// File(root.getAbsolutePath()+"/DCIM/Camera/img.jpg");

			String path = Environment.getExternalStorageDirectory()
					.getAbsolutePath()
					+ "/"
					+ System.currentTimeMillis()
					+ "_RT.jpg";// "/RockeTalk/Chat/";
			File backupFile = new File(path);
			backupFile.createNewFile();
			File sourceFile = new File(sourceFilepath);
			fis = new FileInputStream(sourceFile);
			fos = new FileOutputStream(backupFile);
			byte fData[] = new byte[fis.available()];
			fis.read(fData);
			fos.write(fData);
			// in = fis.getChannel();
			// out = fos.getChannel();
			//
			// long size = in.size();
			// in.transferTo(0, size, out);
			return true;
		} catch (Throwable e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (Throwable ignore) {
			}

			try {
				if (fos != null)
					fos.close();
			} catch (Throwable ignore) {
			}

			try {
				if (in != null && in.isOpen())
					in.close();
			} catch (Throwable ignore) {
			}

			try {
				if (out != null && out.isOpen())
					out.close();
			} catch (Throwable ignore) {

			}
		}

	}

	public static InputStream getFileInputStream(String path) {
		try {
			if (Logger.ENABLE)
				System.out
						.println("----------------getFileInputStream----------------->"
								+ path);
			InputStream finput = new FileInputStream(path);
			return finput;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	/*
	 * public static int getFileInputStream(String path) { int size = 0 ; try {
	 * InputStream finput = new FileInputStream(path); size= finput.available()
	 * ; finput.close(); } catch (FileNotFoundException e) {
	 * e.printStackTrace(); return null ; } return size ; }
	 */
	public static String[] getShort(String fname[]) {
		if (fname != null)
			for (int x = 0; x < fname.length; x++) {
				for (int y = 0; y < fname.length - 1; y++) {
					if (Long.parseLong(fname[y]) > Long.parseLong(fname[y + 1])) {
						String temp = fname[y + 1];
						fname[y + 1] = fname[y];
						fname[y] = temp;

					}
				}
			}
		return fname;
	}

	public static List<Chat> getChatData(String fname) {

		List<Chat> mCurrentUserChatData = new ArrayList<Chat>();
		if (!canWeWriteInSDCard())
			return mCurrentUserChatData;

		Chat chat = null;
		try {

			String path = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + chatpath;// "/RockeTalk/Chat/";
			byte[] s = null;
			try {
				boolean exists = (new File(path)).exists();
				if (!exists) {
					new File(path).mkdirs();
				}

				String fl[] = null;
				;
				exists = (new File(path
						+ HexStringConverter.getHexStringConverterInstance()
								.stringToHex(fname))).exists();
				if (exists) {
					File file = new File(path
							+ HexStringConverter
									.getHexStringConverterInstance()
									.stringToHex(fname));//
					if (file.isDirectory()) {
						fl = file.list();
					}
				}
				fl = getShort(fl);
				for (int i = 0; i < fl.length; i++) {
					FileInputStream finput = new FileInputStream(path
							+ HexStringConverter
									.getHexStringConverterInstance()
									.stringToHex(fname) + "/" + fl[i]);
					s = new byte[finput.available()];
					finput.read(s);
					finput.close();
					chat = (Chat) deserializeObject(encryptedoRDecrypt(s, false));
					// System.out.println("chat username : "+chat.userName);
					// System.out.println("fname : "+fname);
					String chaterName = fname.substring(fname.indexOf("_") + 1,
							fname.length());
					String loginusername = fname.substring(0,
							fname.indexOf("_"));
					// System.out.println("chaterName : "+chaterName);
					// System.out.println("loginusername : "+loginusername);
					// System.out.println("SettingData.sSelf.getUserName() : "+SettingData.sSelf.getUserName());
					if (chat != null
							&& chat.userName != null
							&& chat.userName.equals(chaterName) == false
							&& (chat.userName.equals(SettingData.sSelf
									.getUserName()) == false && loginusername
									.equals(chat.userName) == false))
						continue;
					if (chat != null)
						mCurrentUserChatData.add(chat);
					finput.close();
				}

			} catch (IOException ioe) {
				if (mCurrentUserChatData == null
						|| mCurrentUserChatData.size() <= 0)
					mCurrentUserChatData = null;
				ioe.printStackTrace();
			}
		} catch (Exception e) {
			Log.e("serializeObject", "error", e);
			return null;
		}
		return mCurrentUserChatData;
	}

	private static final String TAG_DECLINE = "decline";
	private static final String TAG_COMMONFRIEND = "commonfriends";
	private static final String TAG_MEDIAPOST = "mediaposts";
	private static final String TAG_IGNORE = "ignore";
	private static final String TAG_REPORT = "report";
	private static final String TAG_COMMONCOMMUNITIES = "commoncommunities";
	private static final String TAG_ACCEPT = "accept";
	private static final String TAG_LOCTION = "location";
	private static final String TAG_BIRTHDAY = "birthday";

	// ArrayList<HashMap<String, String>> contactList = new
	// ArrayList<HashMap<String, String>>();
	public static HashMap<String, String> mapData = new HashMap<String, String>();

	public static void jsonParserEngine(String parseString) {

		try {

			JSONObject myjson = new JSONObject(parseString);
			// HashMap<String, String> mapData=new HashMap<String, String>();
			// JSONArray nameArray = myjson.names();
			// JSONArray valArray = myjson.toJSONArray(nameArray);

			// for(int i=0;i<valArray.length();i++)
			// {

			// String p = nameArray.getString(i) + "," + valArray.getString(i);
			String decline = myjson.getString(TAG_DECLINE);
			String commonfriend = myjson.getString(TAG_COMMONFRIEND);
			String mediapost = myjson.getString(TAG_MEDIAPOST);
			String ignore = myjson.getString(TAG_IGNORE);
			String report = myjson.getString(TAG_REPORT);
			String communities = myjson.getString(TAG_COMMONCOMMUNITIES);
			String accept = myjson.getString(TAG_ACCEPT);
			String loctaion = myjson.getString(TAG_LOCTION);
			String birthday = myjson.getString(TAG_BIRTHDAY);

			mapData.put(TAG_DECLINE, decline);
			mapData.put(TAG_COMMONFRIEND, commonfriend);
			mapData.put(TAG_MEDIAPOST, mediapost);
			mapData.put(TAG_IGNORE, ignore);
			mapData.put(TAG_REPORT, report);
			mapData.put(TAG_COMMONCOMMUNITIES, communities);
			mapData.put(TAG_ACCEPT, accept);
			mapData.put(TAG_LOCTION, loctaion);
			mapData.put(TAG_BIRTHDAY, birthday);
			// contactList.add(mapData);
			// }

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static String decline() {
		return mapData.get(TAG_DECLINE);
	}

	public static String commonFriend() {
		return mapData.get(TAG_COMMONFRIEND);
	}

	public static String mediaPost() {
		return mapData.get(TAG_MEDIAPOST);
	}

	public static String ignore() {
		return mapData.get(TAG_IGNORE);

	}

	public static String report() {
		return mapData.get(TAG_REPORT);
	}

	public static String communities() {
		return mapData.get(TAG_COMMONCOMMUNITIES);
	}

	public static String accept() {
		return mapData.get(TAG_ACCEPT);
	}

	public static String location() {
		return mapData.get(TAG_LOCTION);
	}

	public static String birthday() {
		return mapData.get(TAG_BIRTHDAY);
	}

	public static List<Chat> getChatDatax(String fname) {

		List<Chat> mCurrentUserChatData = new ArrayList<Chat>();
		if (!canWeWriteInSDCard())
			return mCurrentUserChatData;

		Chat chat = null;
		try {

			String path = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + chatpath;// "/RockeTalk/Chat/";
			byte[] s = null;
			try {
				boolean exists = (new File(path)).exists();
				if (!exists) {
					new File(path).mkdirs();
				}

				FileInputStream finput = new FileInputStream(path
						+ HexStringConverter.getHexStringConverterInstance()
								.stringToHex(fname) + ".rt");
				s = new byte[finput.available()];
				finput.read(s);
				finput.close();
				mCurrentUserChatData = (List) deserializeObject(encryptedoRDecrypt(
						s, false));
				// mCurrentUserChatData.add(chat);
				finput.close();

			} catch (IOException ioe) {
				mCurrentUserChatData = null;
				ioe.printStackTrace();
			}
		} catch (Exception e) {
			Log.e("serializeObject", "error", e);
			return null;
		}
		return mCurrentUserChatData;
	}

	public static Object deserializeObject(byte[] b) {
		try {
			ObjectInputStream in = new ObjectInputStream(
					new ByteArrayInputStream(b));
			Object object = in.readObject();
			in.close();

			return object;
		} catch (ClassNotFoundException cnfe) {
			Log.e("deserializeObject", "class not found error", cnfe);

			return null;
		} catch (IOException ioe) {
			Log.e("deserializeObject", "io error", ioe);
			ioe.printStackTrace();
			return null;
		}
	}

	public static boolean deleteChatData(String name) {
		try {
			if (!canWeWriteInSDCard())
				return false;
			if (Logger.ENABLE)
				System.out.println("Delete chat data for " + name);
			// String path =
			// Environment.getExternalStorageDirectory().getAbsolutePath() +
			// chatpath;//"/RockeTalk/Chat/";
			// String fname = "chat.txt";
			// String n =
			// HexStringConverter.getHexStringConverterInstance().stringToHex(SettingData.sSelf.getUserName()
			// + "_" + name);

			String path = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + chatpath;// "/RockeTalk/Chat/";
			String fname = "chat.txt";

			String folderPath = path
					+ HexStringConverter.getHexStringConverterInstance()
							.stringToHex(
									SettingData.sSelf.getUserName() + "_"
											+ name);

			String fl[] = null;
			;
			// exists = (new
			// File(path+HexStringConverter.getHexStringConverterInstance().stringToHex(fname))).exists();
			// if (exists)
			{

				File file = new File(folderPath);//
				if (file.isDirectory()) {
					fl = file.list();
				}
			}
			if (Logger.ENABLE)
				System.out.println("Delete chat data path " + folderPath);
			for (int i = 0; i < fl.length; i++) {
				File f = new File(folderPath + "/" + fl[i]);//
				f.delete();
				if (Logger.ENABLE)
					System.out.println("Delete chat data file path "
							+ folderPath + "/" + fl[i]);
			}

			File f = new File(folderPath);
			f.delete();
		} catch (Exception e) {
			return false;
		}
		return true;

	}

	// chater name is otherside user name nagendra kumar<nagendra1020>
	public static boolean saveChatData(Chat chat, String chaterName) {
		if (Logger.ENABLE)
			Logger.debug(TAG, "--saveChatData--chaterName:" + chaterName);
		if (!canWeWriteInSDCard())
			return false;
		if (SettingData.sSelf.getUserName() == null
				|| SettingData.sSelf.getUserName().length() <= 0)
			return false;
		try {

			String path = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + chatpath;// "/RockeTalk/Chat/";
			String fname = "chat.txt";

			String folderPath = path
					+ HexStringConverter.getHexStringConverterInstance()
							.stringToHex(
									SettingData.sSelf.getUserName() + "_"
											+ chaterName);
			fname = folderPath + "/" + getDateStringToMilisec(chat.timestamp);// +".rt";
			if (Logger.ENABLE)
				Logger.debug(TAG, "----folder:" + folderPath);
			if (Logger.ENABLE)
				Logger.debug(TAG, "----fname:" + fname);
			byte[] chatBytes = serializeObject(chat);
			chatBytes = encryptedoRDecrypt(chatBytes, true);

			try {
				// Make sure the path exists
				boolean exists = (new File(folderPath)).exists();
				if (!exists) {
					new File(folderPath).mkdirs();
				}

				// Open output stream
				FileOutputStream fOut = new FileOutputStream(fname);

				fOut.write(chatBytes);

				// Close output stream
				fOut.flush();
				fOut.close();

			} catch (IOException ioe) {
				ioe.printStackTrace();
				// ioe.printStackTrace();
			}

		} catch (Exception e) {
			// TODO: handle exception
		}

		return true;
	}

	public static boolean saveChatData() {

		if (1 == 1)
			return false;
		if (!canWeWriteInSDCard())
			return false;
		if (SettingData.sSelf.getUserName() == null
				|| SettingData.sSelf.getUserName().length() <= 0)
			return false;
		// BusinessProxy.sSelf.chatData
		// public Map<String, List<Chat>> chatData = new HashMap<String,
		// List<Chat>>();
		try {

			String path = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + chatpath;// "/RockeTalk/Chat/";
			String fname = "chat.txt";

			Set chatData = BusinessProxy.sSelf.chatData.keySet();// .get(mOtherName);
			Iterator<String> iterator = chatData.iterator();
			while (iterator.hasNext()) {
				String key = iterator.next();

				String n = HexStringConverter.getHexStringConverterInstance()
						.stringToHex(
								SettingData.sSelf.getUserName() + "_" + key);
				;// key;
					// if (n.indexOf('<') != -1 && n.indexOf('>') != -1)
					// n = n.substring(n.indexOf('<') + 1, n.lastIndexOf('>'));

				fname = n + ".rt";
				List<Chat> mCurrentUserChatData = BusinessProxy.sSelf.chatData
						.get(key);
				// for (int i = 0; i < mCurrentUserChatData.size(); i++)
				{
					// Chat chat = mCurrentUserChatData.get(i);
					byte[] chatBytes = serializeObject(mCurrentUserChatData);
					chatBytes = encryptedoRDecrypt(chatBytes, true);
					try {
						// Make sure the path exists
						boolean exists = (new File(path)).exists();
						if (!exists) {
							new File(path).mkdirs();
						}

						// Open output stream
						FileOutputStream fOut = new FileOutputStream(path
								+ fname);

						fOut.write(chatBytes);

						// Close output stream
						fOut.flush();
						fOut.close();

					} catch (IOException ioe) {
						ioe.printStackTrace();
						// ioe.printStackTrace();
					}
					// Bla deserializedBla = (Bla)
					// SerializerClass.deserializeObject(blaBytes);
				}
			}
		} catch (Exception e) {
			Log.e("serializeObject", "error", e);
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static byte[] serializeObject(Object o) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		try {
			ObjectOutput out = new ObjectOutputStream(bos);
			out.writeObject(o);
			out.close();

			byte[] buf = bos.toByteArray();
			bos.close();
			return buf;
		} catch (IOException ioe) {
			Log.e("serializeObject", "error", ioe);
			ioe.printStackTrace();
			return null;
		}
	}

	public void animation() {

	}

	public static void startAnimition(Context context, View view, int animition) {
		try {
			Animation animation = AnimationUtils.loadAnimation(context,
					animition);
			// animation.setAnimationListener(listener)
			view.startAnimation(animation);
		} catch (Exception e) {
			// TODO: handle exception
		}
		//
	}

	public static float getDip(int dip, Context context) {
		Resources r = context.getResources();
		float pix = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip,
				r.getDisplayMetrics());
		return pix;
	}

	public static float convertDpToPixel(float dp, Context context) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float px = dp * (metrics.densityDpi / 160f);
		return px;
	}

	/**
	 * This method converts device specific pixels to device independent pixels.
	 * 
	 * @param px
	 *            A value in px (pixels) unit. Which we need to convert into db
	 * @param context
	 *            Context to get resources and device specific display metrics
	 * @return A float value to represent db equivalent to px value
	 */
	public static float convertPixelsToDp(float px, Context context) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float dp = px / (metrics.densityDpi / 160f);
		return dp;

	}

	public static byte[] encryptedoRDecrypt(byte data[], boolean isEncrypted) {
		// if(1==2){
		// return data ;
		// }
		byte[] keyStart = (BusinessProxy.sSelf.getUserId() + "NAGENROCKETALKDRA")
				.getBytes();
		try {
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
			sr.setSeed(keyStart);
			kgen.init(128, sr); // 192 and 256 bits may not be available
			SecretKey skey = kgen.generateKey();
			byte[] key = skey.getEncoded();

			if (isEncrypted)
				return encrypt(key, data);
			if (!isEncrypted)
				return decrypt(key, data);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "".getBytes();
	}

	private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
		if (Logger.ENABLE)
			Logger.debug(TAG, "-------encrypt------");
		// byte[] keyStart =
		// (BusinessProxy.sSelf.getUserId()+"NAGENROCKETALKDRA").getBytes();
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
		byte[] encrypted = cipher.doFinal(clear);
		return encrypted;
	}

	private static byte[] decrypt(byte[] raw, byte[] encrypted)
			throws Exception {
		if (Logger.ENABLE)
			Logger.debug(TAG, "-------decrypt------");
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, skeySpec);
		byte[] decrypted = cipher.doFinal(encrypted);
		return decrypted;
	}

	public static String[][] imageIDs2 = {
//		{ ":01", "" + R.drawable.emo_1 },
//			{ ":02", "" + R.drawable.emo_2, },
//			{ ":03", "" + R.drawable.emo_3, },
//			{ ":04", "" + R.drawable.emo_4, },
//			{ ":05", "" + R.drawable.emo_5, },
//			{ ":06", "" + R.drawable.emo_6, },
//			{ ":07", "" + R.drawable.emo_7, },
//			{ ":08", "" + R.drawable.emo_8, },
//			{ ":09", "" + R.drawable.emo_9, },
//			{ ":010", "" + R.drawable.emo_10, },
//			{ ":011", "" + R.drawable.emo_11, },
//			{ ":012", "" + R.drawable.emo_12, },
//			{ ":013", "" + R.drawable.emo_13, },
//			{ ":014", "" + R.drawable.emo_14, }

	};
	public static String[][] imageIDs3 = { 
//		{ ":rt01", "" + R.drawable.emo1 },
//			{ ":rt02", "" + R.drawable.emo2, },
//			{ ":rt03", "" + R.drawable.emo3, },
//			{ ":rt04", "" + R.drawable.emo4, },
//			{ ":rt05", "" + R.drawable.emo5, },
//			{ ":rt06", "" + R.drawable.emo6, },
//			{ ":rt07", "" + R.drawable.emo7, },
//			{ ":rt08", "" + R.drawable.emo8, },
//			{ ":rt09", "" + R.drawable.emo9, },
//			{ ":rt010", "" + R.drawable.emo10, },
//			{ ":rt011", "" + R.drawable.emo11, },
//			{ ":rt012", "" + R.drawable.emo12, },
//			{ ":rt013", "" + R.drawable.emo13, },
//			{ ":rt014", "" + R.drawable.emo14, }

	};

	public static String[][] imageIDs1 = {};

	public static final HashMap<String, Integer> mEmoticons = new HashMap<String, Integer>();
	static {}

	public static String getDateString(String date) {// 13/05/2011 9:41:14PM

		try {

			SimpleDateFormat sdf = null;
			sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				// 06-14 11:58:52.970: INFO/System.out(1185): input
				// data:2012-06-14 04:54:12

				if (date.indexOf("-") != -1) {
					date = DateFormatUtils.convertGMTDateToCurrentGMTDate(date,
							"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss");
					sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				} else {
					sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				}

				Date past = sdf.parse(date);

				// System.out.println(DateFormat.getDateTimeInstance(
				// DateFormat.MEDIUM, DateFormat.SHORT).format(past));

				return DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
						DateFormat.SHORT).format(past);
				// Calendar cal= Calendar.getInstance();
				// cal.setTime(past);
				// return cal.get(field);
			} catch (ParseException e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
		return date;
	}

	public static void trimCache(Context context) {
		try {
			File dir = context.getCacheDir();
			if (dir != null && dir.isDirectory()) {
				deleteDir(dir);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public static void deleteDraftPostData(Context context) {
		try {
			String path = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + draftPostPath;
			File dir = new File(path);
			if (dir != null && dir.isDirectory()) {
				deleteDir(dir);
			}
		} catch (Exception e) {
		}
	}
	public static void deleteTemp(Context context) {
		try {
			if((System.currentTimeMillis()-Utilities.getLong(context, Constants.DELETE_TEMP))>1000*20){
				
			}else
				return ;
			
			Utilities.setLong(context, Constants.DELETE_TEMP,System.currentTimeMillis()) ;
			
			String path = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + Constants.imageTempPath;
			File dir = new File(path);
			if (dir != null && dir.isDirectory()) {
				
				deleteDir(dir);
			}
		} catch (Exception e) {
		}
	}
	public static void deletePostTemp(Context context) {
		try {
			
			String path = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + Constants.contentPost;
			File dir = new File(path);
			if (dir != null && dir.isDirectory()) {
				
				deleteDir(dir);
			}
		} catch (Exception e) {
		}
	}

	public static void deleteData(Context context) {
		try {
			File dir = new File(
					"/data/data/com.kainat.app.android/files/");
			if (dir != null && dir.isDirectory()) {
				deleteDir(dir);
			}
		} catch (Exception e) {
		}
	}

	public static boolean deleteDir(File dir) {
		if (dir != null && dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		if (dir.getAbsolutePath().indexOf(".th") != -1)
			return false;
		// System.out.println("-----------delete file---"+dir.getAbsolutePath());
		return dir.delete();
	}

	public static byte[] readBytes(InputStream inputStream) throws IOException {
		// this dynamically extends to take the bytes you read
		ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

		// this is storage overwritten on each iteration with bytes
		int bufferSize = 1024;
		byte[] buffer = new byte[bufferSize];

		// we need to know how may bytes were read to write them to the
		// byteBuffer
		int len = 0;
		// System.out.println("-----byteBuffer is availabel : "+inputStream.available());
		if(inputStream != null)
		while ((len = inputStream.read(buffer)) != -1) {
			byteBuffer.write(buffer, 0, len);
			// System.out.println("-----byteBuffer size : "+byteBuffer.size());
		}
		// inputStream.close() ;
		// and then we can return your byte array.
		return byteBuffer.toByteArray();
	}

	public static byte[] readBytes(InputStream inputStream, int till)
			throws IOException {
		// this dynamically extends to take the bytes you read
		ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

		// this is storage overwritten on each iteration with bytes
		int bufferSize = till;
		byte[] buffer = new byte[bufferSize];

		// we need to know how may bytes were read to write them to the
		// byteBuffer
		int len = 0;
		while ((len = inputStream.read(buffer)) != -1) {
			byteBuffer.write(buffer, 0, len);
			break;
		}
		inputStream.close();
		// and then we can return your byte array.
		return byteBuffer.toByteArray();
	}

	public static String getPicPath() {
		// File file = new File(Environment.getExternalStorageDirectory(),
		// getRandomNumber() + ".jpg");

		File path = new File(Environment.getExternalStorageDirectory()
				+ "/DCIM");
		if (path.exists()) {
			File test1 = new File(path, "Camera/");
			if (test1.exists()) {
				path = test1;
			} else {
				File test2 = new File(path, "100ANDRO/");
				if (test2.exists()) {
					path = test2;
				} else {
					File test3 = new File(path, "100MEDIA/");
					if (!test3.exists()) {
						test3.mkdirs();
					}
					path = test3;
				}
			}
		} else {
			path = new File(path, "Camera/");
			path.mkdirs();
		}
		// System.out.println("------camera path----------"+path.getAbsolutePath());
		return path.getAbsolutePath();
	}

	public static boolean renameFile(String oriFile, String destFile) {
		File sdcard = Environment.getExternalStorageDirectory();
		File from = new File(oriFile);
		File to = new File(destFile);

		return from.renameTo(to);
	}

	public static void obs(FileObserver fo, String path) {
		// System.out.println("-----FileObserver "+ path);
		fo = new FileObserver(path.toString()) {

			@Override
			public void onEvent(int event, String path) {
				// System.out.println("-----event "+ event);
				/*
				 * Log.d("operator", "out side if" + Phototaken +
				 * externalStorageState .equals(Environment.MEDIA_MOUNTED)); if
				 * (Phototaken == 0 && event == 8){ String st = timeStamp();
				 * Log.d("operator", "in event " + Phototaken);
				 * Log.d("operator", "lat: " + MainService.lat + " " + "lng: " +
				 * MainService.lng + " " + "location: " + MainService.addre +
				 * " " + "time: " + st); ptd.insert(st,
				 * String.valueOf(MainService.lat),
				 * String.valueOf(MainService.lng), MainService.addre); }
				 */

			}
		};
		fo.startWatching();
	}

	public static String getVideoLastVideoFile(Activity ctx) {
		final String[] columns = { MediaStore.Video.Media.DATA,
				MediaStore.Video.Media._ID, MediaStore.Video.Media.DATE_TAKEN };
		final String orderBy = MediaStore.Video.Media._ID;
		Cursor imagecursor = ctx.managedQuery(
				MediaStore.Video.Media.EXTERNAL_CONTENT_URI, columns, null,
				null, orderBy);
		int count = imagecursor.getCount();
		// System.out.println("-------------count------------"+count);
		long cTime = System.currentTimeMillis();
		long DATE_TAKEN = 0;
		String s = "";
		for (int i = 0; i < count; i++) {
			imagecursor.moveToPosition(i);
			// long dataColumnIndex =
			// imagecursor.getLong(MediaStore.Images.Media.DATE_TAKEN);
			// long dataColumnIndex =
			// imagecursor.getLong(imagecursor.getColumnIndex(MediaStore.Video.Media.DATE_TAKEN));
			// if(dataColumnIndex > DATE_TAKEN)
			// DATE_TAKEN = dataColumnIndex ;
			// if((cTime - dataColumnIndex)>5000)
			{
				// System.out.println("--------dataColumnIndex-----------"+dataColumnIndex);
				int ind = imagecursor
						.getColumnIndex(MediaStore.Video.Media.DATA);
				s = imagecursor.getString(ind);
				// System.out.println("---------------path-----------"+s);
			}
		}
		return s;
	}

	public static Hashtable<String, String> getVideoPath(Activity ctx) {
		final String[] columns = { MediaStore.Video.Media.DATA,
				MediaStore.Video.Media._ID, MediaStore.Video.Media.DATE_TAKEN };
		final String orderBy = MediaStore.Video.Media._ID;
		Cursor imagecursor = ctx.managedQuery(
				MediaStore.Video.Media.EXTERNAL_CONTENT_URI, columns, null,
				null, orderBy);
		int count = imagecursor.getCount();
		int image_column_index = imagecursor
				.getColumnIndex(MediaStore.Images.Media._ID);
		Hashtable<String, String> sr = new Hashtable<String, String>();
		for (int i = 0; i < count; i++) {
			imagecursor.moveToPosition(i);
			int id = imagecursor.getInt(image_column_index);
			int dataColumnIndex = imagecursor
					.getColumnIndex(MediaStore.Images.Media.DATA);
			sr.put(id + "", imagecursor.getString(dataColumnIndex));
		}
		return sr;
	}

	public static Hashtable<String, String> getImagePath(Activity ctx) {
		final String[] columns = { MediaStore.Video.Media.DATA,
				MediaStore.Video.Media._ID, MediaStore.Video.Media.DATE_TAKEN };
		final String orderBy = MediaStore.Video.Media._ID;
		Cursor imagecursor = ctx.managedQuery(
				MediaStore.Video.Media.EXTERNAL_CONTENT_URI, columns, null,
				null, orderBy);
		int count = imagecursor.getCount();
		int image_column_index = imagecursor
				.getColumnIndex(MediaStore.Images.Media._ID);
		Hashtable<String, String> sr = new Hashtable<String, String>();
		for (int i = 0; i < count; i++) {
			imagecursor.moveToPosition(i);
			int id = imagecursor.getInt(image_column_index);
			int dataColumnIndex = imagecursor
					.getColumnIndex(MediaStore.Images.Media.DATA);
			sr.put(id + "", imagecursor.getString(dataColumnIndex));
		}
		return sr;
	}

	public static MediaPost getDraftPostData(String fname, Context context) {
		// String pathd = String.format("%s%s%s.ser", context.getCacheDir(),
		// File.separator,
		// "1");
		try {

			String path = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + draftPostPath;
			boolean exists = (new File(path)).exists();
			if (!exists) {
				new File(path).mkdirs();
			}
			String folderPath = path + fname;

			FileInputStream finput = new FileInputStream(folderPath);
			byte[] s = new byte[finput.available()];
			finput.read(s);
			finput.close();
			MediaPost mediaPost = (MediaPost) deserializeObject(s);

			try {
				new File(Environment.getExternalStorageDirectory()
						.getAbsolutePath()
						+ draftPostPath
						+ mediaPost.id
						+ ".pst").delete();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return mediaPost;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void draftPostData(MediaPost mediaPost, Context context) {
		// String pathd = String.format("%s%s%s.ser", context.getCacheDir(),
		// File.separator,
		// "1");
		try {

			String path = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + draftPostPath;
			boolean exists = (new File(path)).exists();
			if (!exists) {
				new File(path).mkdirs();
			}
			String fname = mediaPost.id + ".pst";

			String folderPath = path + fname;

			fname = folderPath;
			if (Logger.ENABLE)
				Logger.debug(TAG, "----folder:" + folderPath);
			if (Logger.ENABLE)
				Logger.debug(TAG, "----fname:" + fname);
			byte[] objectByte = serializeObject(mediaPost);
			FileOutputStream fout = new FileOutputStream(folderPath);
			fout.write(objectByte);
			fout.flush();
			fout.close();
			objectByte = null;

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void donePosting(MediaPost mediaPost, Context context) {
		try {

			try {
				new File(Environment.getExternalStorageDirectory()
						.getAbsolutePath()
						+ draftPostPath
						+ mediaPost.id
						+ ".pst").delete();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (!Logger.CHEAT)
				return;
			String path = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + sendPostPath;
			boolean exists = (new File(path)).exists();
			if (!exists) {
				new File(path).mkdirs();
			}
			String fname = mediaPost.id + ".pst";

			String folderPath = path + fname;

			fname = folderPath;
			if (Logger.ENABLE)
				Logger.debug(TAG, "----folder:" + folderPath);
			if (Logger.ENABLE)
				Logger.debug(TAG, "----fname:" + fname);
			byte[] objectByte = serializeObject(mediaPost);
			FileOutputStream fout = new FileOutputStream(folderPath);
			fout.write(objectByte);
			fout.flush();
			fout.close();
			objectByte = null;

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String[] initPostDraftList() {
		try {
			if (Logger.ENABLE)
				Logger.debug(TAG, "#initOpenchatList#");

			String path = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + draftPostPath;
			String s[] = Utilities.getFileList(path);

			String id = HexStringConverter.getHexStringConverterInstance()
					.stringToHex(
							SettingData.sSelf.getUserName()
									+ BusinessProxy.sSelf.getUserId() * 1847);
			Vector<String> v = new Vector<String>();
			for (int i = 0; i < s.length; i++) {
				if (s[i].startsWith(id))
					v.add(s[i]);
			}
			s = new String[v.size()];
			for (int i = 0; i < v.size(); i++) {
				s[i] = v.get(i);
			}
//			System.out.println("---------post draft size : " + s.length);
			return s;
		} catch (Exception e) {
			if (Logger.ENABLE)
				Logger.debug(TAG, "Total Email available " + e.getMessage());
		}
		return null;
	}

	public static String replace(String _text, String _searchStr,
			String _replacementStr) {
		StringBuffer sb = new StringBuffer();
		int searchStringPos = _text.indexOf(_searchStr);
		int startPos = 0;
		int searchStringLength = _searchStr.length();
		while (searchStringPos != -1) {
			sb.append(_text.substring(startPos, searchStringPos)).append(
					_replacementStr);
			startPos = searchStringPos + searchStringLength;
			searchStringPos = _text.indexOf(_searchStr, startPos);
		}
		sb.append(_text.substring(startPos, _text.length()));
		return sb.toString();
	}

	public static void setRateTime(Context mContext) {
		SharedPreferences prefs = mContext.getSharedPreferences("setting", 0);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putLong("ratetime", System.currentTimeMillis());
		editor.commit();
	}

	public static long getRateTime(Context mContext) {
		SharedPreferences prefs = mContext.getSharedPreferences("setting", 0);
		SharedPreferences.Editor editor = prefs.edit();
		return prefs.getLong("ratetime", 0);
	}

	public static void setLoginCount(Context mContext) {
		SharedPreferences prefs = mContext.getSharedPreferences("setting", 0);
		SharedPreferences.Editor editor = prefs.edit();
		int loginCount = prefs.getInt("loginCount", 0);
		editor.putInt("loginCount", ++loginCount);
		editor.commit();
	}

	public static int getLoginCount(Context mContext) {
		SharedPreferences prefs = mContext.getSharedPreferences("setting", 0);
		SharedPreferences.Editor editor = prefs.edit();
		return prefs.getInt("loginCount", 0);
	}

	public static void setSafeLogout(Context mContext) {
		SharedPreferences prefs = mContext.getSharedPreferences("setting", 0);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean("safelogin", false);
		editor.putBoolean("safelogout", true);
		editor.commit();
	}

	public static boolean isSafeLogout(Context mContext) {
		SharedPreferences prefs = mContext.getSharedPreferences("setting", 0);
		SharedPreferences.Editor editor = prefs.edit();
		return prefs.getBoolean("safelogout", false);
	}

	public static void setSafeLogin(Context mContext) {
		SharedPreferences prefs = mContext.getSharedPreferences("setting", 0);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean("safelogin", true);
		editor.putBoolean("safelogout", false);
		editor.commit();
	}

	public static boolean isSafeLoin(Context mContext) {
		SharedPreferences prefs = mContext.getSharedPreferences("setting", 0);
		SharedPreferences.Editor editor = prefs.edit();
		return prefs.getBoolean("safelogin", false);
	}

	public static String getConterType(byte[] data) {
		// System.out.println("-------------data.length----" + data.length);
		byte type[] = new byte[100];
		String contentType = null;
		System.arraycopy(data, 0, type, 0, 100);
		String cT = new String(type);
		cT = cT.toLowerCase();
		if (cT.indexOf("png") != -1)
			contentType = "image/png";
		else if (cT.indexOf("3gp") != -1)
			contentType = "video/3gp";
		else if (cT.indexOf("mp4") != -1)
			contentType = "video/mp4";
		else if (cT.indexOf("JFIF") != -1)
			contentType = "image/jpeg ";
		// System.out.println("-------------content type----" + contentType);
		return null;
	}

	public static String converMiliSecond(long timeMillis) {
		long time = timeMillis / 1000;
		String seconds = Integer.toString((int) (time % 60));
		String minutes = Integer.toString((int) ((time % 3600) / 60));
		String hours = Integer.toString((int) (time / 3600));
		// System.out.println("time====="+time);
		// System.out.println("seconds====="+seconds);
		// System.out.println("minutes====="+minutes);
		// System.out.println("hours====="+hours);

		for (int i = 0; i < 2; i++) {
			if (seconds.length() < 2) {
				seconds = "0" + seconds;
			}
			if (minutes.length() < 2) {
				minutes = "0" + minutes;
			}
			if (hours.length() < 2) {
				hours = "0" + hours;
			}
		}
		return minutes + ":" + seconds;
	}

	public static String converMiliSecondForAudioStatus(long timeMillis) {
		long time = timeMillis / 1000;//88000
		String seconds = (Integer.toString(60 - (int) (time % 60)));
		String minutes = Integer.toString((int) ((time % 3600) / 60));
		String hours = Integer.toString((int) (time / 3600));
		// System.out.println("time====="+time);
		// System.out.println("seconds====="+seconds);
		// System.out.println("minutes====="+minutes);
		// System.out.println("hours====="+hours);

		for (int i = 0; i < 2; i++) {
			if (seconds.length() < 2) {
				seconds = "" + seconds;
			}
			if (minutes.length() < 2) {
				minutes = "0" + minutes;
			}
			if (hours.length() < 2) {
				hours = "0" + hours;
			}
		}
		return seconds;
	}

	public static byte[] getFileData(String filePath) {
		try {
			System.out.println("getFileData:filePath : "+filePath);
			FileInputStream fin = new FileInputStream(filePath);
			byte[] data = Utilities.readBytes(fin);// new byte[fin.available()];
			fin.read(data, 0, data.length);
			return data;
		} catch (Exception e) {
			e.fillInStackTrace();
		}
		return null;
	}

	public static String[] split(StringBuffer sb, String splitter) {
		String[] strs = new String[sb.length()];
		int splitterLength = splitter.length();
		int initialIndex = 0;
		int indexOfSplitter = indexOf(sb, splitter, initialIndex);
		int count = 0;
		if (-1 == indexOfSplitter)
			return new String[] { sb.toString() };
		while (-1 != indexOfSplitter) {
			char[] chars = new char[indexOfSplitter - initialIndex];
			sb.getChars(initialIndex, indexOfSplitter, chars, 0);
			initialIndex = indexOfSplitter + splitterLength;
			indexOfSplitter = indexOf(sb, splitter, indexOfSplitter + 1);
			strs[count] = new String(chars);
			count++;
		}
		// get the remaining chars.
		if (initialIndex + splitterLength <= sb.length()) {
			char[] chars = new char[sb.length() - initialIndex];
			sb.getChars(initialIndex, sb.length(), chars, 0);
			strs[count] = new String(chars);
			count++;
		}
		String[] result = new String[count];
		for (int i = 0; i < count; i++) {
			result[i] = strs[i];
		}
		return result;
	}

	public static int indexOf(StringBuffer sb, String str, int start) {
		int index = -1;
		if ((start >= sb.length() || start < -1) || str.length() <= 0)
			return index;
		char[] tofind = str.toCharArray();
		outer: for (; start < sb.length(); start++) {
			char c = sb.charAt(start);
			if (c == tofind[0]) {
				if (1 == tofind.length)
					return start;
				inner: for (int i = 1; i < tofind.length; i++) { // start on the
																	// 2nd
																	// character
					char find = tofind[i];
					int currentSourceIndex = start + i;
					if (currentSourceIndex < sb.length()) {
						char source = sb.charAt(start + i);
						if (find == source) {
							if (i == tofind.length - 1) {
								return start;
							}
							continue inner;
						} else {
							start++;
							continue outer;
						}
					} else {
						return -1;
					}

				}
			}
		}
		return index;
	}

	public static void setInt(Context mContext, String key, int value) {
		SharedPreferences prefs = mContext.getSharedPreferences("setting", 0);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public static int getInt(Context mContext, String key) {
		SharedPreferences prefs = mContext.getSharedPreferences("setting", 0);
		SharedPreferences.Editor editor = prefs.edit();
		return prefs.getInt(key, 0);
	}

	public static void setBoolean(Context mContext, String key, boolean value) {
		SharedPreferences prefs = mContext.getSharedPreferences("setting", 0);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	public static boolean getBoolean(Context mContext, String key) {
		SharedPreferences prefs = mContext.getSharedPreferences("setting", 0);
		SharedPreferences.Editor editor = prefs.edit();
		return prefs.getBoolean(key, false);
	}

	public static void setString(Context mContext, String key, String value) {
		// System.out.println("----------------- key : " + key);
		// System.out.println("----------------- value : " + value);
		SharedPreferences prefs = mContext.getSharedPreferences("setting", 0);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static String getString(Context mContext, String key) {
		SharedPreferences prefs = mContext.getSharedPreferences("setting", 0);
		SharedPreferences.Editor editor = prefs.edit();
		return prefs.getString(key, null);
	}

	public static void setLong(Context mContext, String key, long value) {
		SharedPreferences prefs = mContext.getSharedPreferences("setting", 0);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putLong(key, value);
		editor.commit();
	}

	public static long getLong(Context mContext, String key) {
		SharedPreferences prefs = mContext.getSharedPreferences("setting", 0);
		SharedPreferences.Editor editor = prefs.edit();
		return prefs.getLong(key, 0);
	}

	public static boolean isSupportThisVideo(String path) {
		String extension = MimeTypeMap.getFileExtensionFromUrl(path);
		if (extension == null || extension.trim().length() <= 0) {
			try {
				extension = path.substring(path.lastIndexOf(".") + 1,
						path.length());
			} catch (Exception e) {
				extension = "3gp";
			}
		}
		String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
				extension);
		if (mimeType == null)
			return false;
		else
			return true;

	}

	public static ArrayList<DiscoveryBasicInfo> getDiscoveryBasicInfo(
			Context context) {
		ArrayList<DiscoveryBasicInfo> discoveryBasicInfos = new ArrayList<DiscoveryBasicInfo>();
		StringBuffer sb = new StringBuffer(getString(context,
				Constants.DISCOVERY_MENU_URL_ITEM));
		String arr[] = split(sb, Constants.ROW_SEPRETOR);
		for (int i = 0; i < arr.length; i++) {
			String arrT[] = split(new StringBuffer(arr[i]),
					Constants.COL_SEPRETOR);
			DiscoveryBasicInfo basicInfo = new DiscoveryBasicInfo();
			basicInfo.node = arrT[0];
			basicInfo.subtitle = arrT[1];
			basicInfo.url = arrT[2];
			discoveryBasicInfos.add(basicInfo);
		}
		return discoveryBasicInfos;
	}

	public static Vector<LeftMenu> getLeftMenu(Context context) {
		return BusinessProxy.sSelf.gridMenu;
	}

	public static Vector<LeftMenu> getLeftMenu2(Context context) {
		return BusinessProxy.sSelf.gridMenu2;
	}

	public static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			// pre-condition
			return;
		}

		int totalHeight = 0;
		int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(),
				MeasureSpec.AT_MOST);
		int count = listAdapter.getCount() ;
		if(count>4)
			count = count-2 ;
		for (int i = 0; i < count; i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1))+50;
		listView.setLayoutParams(params);
		// listView.requestLayout();
	}

	public static Hashtable<String, DiscoveryUrl> getDiscoveryUrl(
			Context context) {
		Hashtable<String, DiscoveryUrl> discovery = new Hashtable<String, DiscoveryUrl>();
		String s = Utilities.getString(context,
				Constants.DISCOVERY_MENU_URL_ITEM);
		String disUrl[] = Utilities.split(new StringBuffer(s),
				Constants.ROW_SEPRETOR);//

		for (int i = 0; i < disUrl.length; i++) {
			String ss[] = Utilities.split(new StringBuffer(disUrl[i]),
					Constants.COL_SEPRETOR);//
			DiscoveryUrl discoveryUrl = new DiscoveryUrl();
			discoveryUrl.node = ss[0];
			discoveryUrl.name = ss[1];
			discoveryUrl.url = ss[2];
			discovery.put(discoveryUrl.node, discoveryUrl);
		}
		return discovery;
	}

	public static void resetLandingPageSetting(Context context) {
		Utilities.setInt(context, Utilities.homeTab, R.landingpage.activity);
	}

	public static void cleanDB(Context context) {
		try {

			// System.out.println("------------------------cleanDB-------------------------START");
			Utilities.setString(context, "lastMessageId", "0");// ,
																// lastMessageId);
			Utilities.setLong(context, "lastMessageIdTime", 0);// ,
																// lastMessageIdTime);
			Utilities.setString(context,
					Constants.LAST_MFUID_FOR_CONVERSATION_LIST, "0");// ,
																		// FeedTask.insMoreTime)
																		// ;

			// context.getContentResolver().delete(
			// MediaContentProvider.CONTENT_URI_MEDIA, null, null);
			// context.getContentResolver().delete(
			// MediaContentProvider.CONTENT_URI_FEATUREDUSER, null, null);
			// context.getContentResolver().delete(
			// MediaContentProvider.CONTENT_URI_GROUPEVENT, null, null);
			// context.getContentResolver().delete(
			// MediaContentProvider.CONTENT_URI_COMMUNITY, null, null);
			context.getContentResolver().delete(
					MediaContentProvider.CONTENT_URI_MEDIA_COMMENT, null, null);
			context.getContentResolver().delete(
					LandingPageContentProvider.CONTENT_URI, null, null);

			Utilities.setString(context, "lastMessageId", "0");// ,
																// lastMessageId);
			Utilities.setLong(context, "lastMessageIdTime", 0);// ,
																// lastMessageIdTime);

			context.getContentResolver().delete(
					MediaContentProvider.CONTENT_URI_SETTING, null, null);
			context.getContentResolver().delete(
					MediaContentProvider.CONTENT_URI_INBOX, null, null);
			context.getContentResolver().delete(
					MediaContentProvider.CONTENT_URI_BOOKMARK, null, null);
			context.getContentResolver().delete(
					MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST,
					null, null);
			context.getContentResolver().delete(
					MediaContentProvider.CONTENT_URI_INBOX_COMPOSE, null, null);

			File sdDir = android.os.Environment.getExternalStorageDirectory();
			File cacheDir = new File(sdDir, Constants.imagePath);

			// if (cacheDir.exists()) {
			// if (cacheDir.isDirectory()) {
			// //if(dirSize(cacheDir) > Constants.MAX_IMAGE_DATA_STORE_SIZE)
			// {
			// String[] children = cacheDir.list();
			// for (int i = 0; i < children.length; i++) {
			// new File(cacheDir, children[i]).delete();
			// }
			// }
			// }
			// cacheDir.delete();
			// }
			// System.out.println("------------------------cleanDB-------------------------END");
		} catch (Exception e) {
			// System.out.println("------------------------cleanDB-------------------------ERROR :"+e.getMessage());
			e.printStackTrace();
		}
	}

	public static void cleanTemp() {
		File sdDir = android.os.Environment.getExternalStorageDirectory();
		File cacheDir = new File(sdDir, Constants.imagePath);

		if (cacheDir.exists()) {
			if (cacheDir.isDirectory()) {
				// if(dirSize(cacheDir) > Constants.MAX_IMAGE_DATA_STORE_SIZE)
				{
					String[] children = cacheDir.list();
					for (int i = 0; i < children.length; i++) {
						// new File(cacheDir, children[i]).delete();
					}
				}
			}
			// cacheDir.delete();
		}
	}

	/**
	 * Return the size of a directory in bytes
	 */
	private static long dirSize(File dir) {

		if (dir.exists()) {
			long result = 0;
			File[] fileList = dir.listFiles();
			for (int i = 0; i < fileList.length; i++) {
				// Recursive call if it's a directory
				if (fileList[i].isDirectory()) {
					result += dirSize(fileList[i]);
				} else {
					// Sum the file size in bytes
					result += fileList[i].length();
				}
			}
			return result; // return the file size
		}
		return 0;
	}

	// for underline of any string
	/*
	 * public static SpannableString makeLink(String tempString){
	 * 
	 * SpannableString spanString = new SpannableString(tempString);
	 * spanString.setSpan(new UnderlineSpan(), 0, spanString.length(), 0);
	 * 
	 * spanString.setSpan(new StyleSpan(Typeface.NORMAL), 0,
	 * spanString.length(), 0); //spanString.setSpan(new
	 * StyleSpan(Typeface.ITALIC), 0, spanString.length(), 0); return
	 * spanString; }
	 */
	public static String makeLink(String tempString) {

		return tempString;
	}

	public static String[] getActivityCount(Context context) {
		String s[] = new String[2];
		try {
			s = split(
					new StringBuffer(getString(context,
							Constants.NOTIFICATIO_COUNT)),
					Constants.COL_SEPRETOR);
			// System.out.println("-------get activity count : "+s);

		} catch (Exception e) {
			// TODO: handle exception
		}
		return s;
	}

	public static void initLastMsgId(Context context) {
		// FeedRequester.lastMessageId = "0" ;
		long sortId = 0;
		String[] columns = new String[] { MessageTable.SORT_TIME };
		Cursor cursor = context.getContentResolver().query(
				MediaContentProvider.CONTENT_URI_INBOX, columns, null, null,
				MessageTable.SORT_TIME + " ASC");// LandingPageTable.STORY_ID +
													// " ASC" DESC
		if (cursor.getCount() > 0) {
			cursor.moveToLast();
			int dataColumnIndex = cursor.getColumnIndex(MessageTable.SORT_TIME);
			sortId = cursor.getLong(dataColumnIndex);
		}
		cursor.close();

		FeedTask.LAST_MFUID_FOR_CONVERSATION_LIST = Utilities.getString(
				context, Constants.LAST_MFUID_FOR_CONVERSATION_LIST);// ,
																		// FeedTask.insMoreTime)
																		// ;

		cursor = context.getContentResolver().query(
				MediaContentProvider.CONTENT_URI_SETTING, null, null, null,
				null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			FeedTask.insMoreTime = cursor.getLong(cursor
					.getColumnIndex(SettingTable.INSMORE_TIME));
		}
		cursor.close();
		if (FeedTask.LAST_MFUID_FOR_CONVERSATION_LIST == null)
			FeedTask.LAST_MFUID_FOR_CONVERSATION_LIST = "0";

		// System.out.println("--------FeedTask.LAST_MFUID_FOR_CONVERSATION_LIST:"+FeedTask.LAST_MFUID_FOR_CONVERSATION_LIST);

		// System.out.println("--------insMoreTime-init 2 :"+FeedTask.insMoreTime);
		// System.out.println("--------insMoreTime-init  1 : "+sortId);
		if (sortId > FeedTask.insMoreTime)
			FeedTask.insMoreTime = sortId;

		FeedRequester.lastMessageId = Utilities.getString(context,
				"lastMessageId");// , lastMessageId);
		FeedRequester.lastMessageIdTime = Utilities.getLong(context,
				"lastMessageIdTime");// , lastMessageIdTime);
		// System.out.println("Feed Synch Discovery user---swaping top message id--------33-----"+FeedRequester.lastMessageIdTime);
		if (FeedRequester.lastMessageId == null) {
			FeedRequester.lastMessageId = "0";
			FeedRequester.lastMessageIdTime = 0;
		}

		Logger.conversationLog(
				"utitlity------------initLastMsgId lastMessageId :",
				FeedRequester.lastMessageId);
		Logger.conversationLog(
				"utitlity------------initLastMsgId insMoreTime :", ""
						+ FeedTask.insMoreTime);
		Logger.conversationLog(
				"utitlity------------initLastMsgId FeedTask.LAST_MFUID_FOR_CONVERSATION_LIST :",
				FeedTask.LAST_MFUID_FOR_CONVERSATION_LIST);

		// System.out.println("------------lastMessageId  init:"+FeedRequester.lastMessageId);
		// System.out.println("------------lastMessageIdTime  init:"+FeedRequester.lastMessageIdTime);
	}

	public static void cleanOnLogin(Context context) {
		RefreshService.PARSE_REFRESH_CONVERSIOTION_LIST = false;
		RefreshService.CONVERSIOTION_LIST_OLD = false;
		RefreshService.PARSE_REFRESH_REFRESH_FIRST = false;
		FeedTask.ShiftNewMessageVec = new Vector<ShiftNewMessage>();
		// /clean inbox thread message
		// context.getContentResolver().delete(
		// MediaContentProvider.CONTENT_URI_INBOX, null, null);
		// context.getContentResolver().delete(ni
		// MediaContentProvider.CONTENT_URI_BOOKMARK, null, null);
		// context.getContentResolver().delete(
		// MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST, null,
		// null);
		// context.getContentResolver().delete(
		// MediaContentProvider.CONTENT_URI_INBOX_COMPOSE, null, null);
		// context.getContentResolver().delete(
		// MediaContentProvider.CONTENT_URI_COMMUNITY, null, null);

		// shoul only clean at user change
		// Utilities.setLong(context, "insMoreTime", 0) ;
		// Utilities.setString(context, "lastMessageId","0");//, lastMessageId);
		// Utilities.setLong(context, "lastMessageIdTime",0);//,
		// lastMessageIdTime);
		// //////
		cleanToBeDeleted(context);
		ConversationsActivity.cidRefresh.clear();
		Utilities.setInt(context, "listpos", 0);
		Utilities.setInt(context, "top", 0);

		RefreshService.idealTime = 0;
		Utilities.setBoolean(context, Constants.REFRESH_CONVERSIOTION_LIST,
				false);
		ComposeService.isSendingMultiple = false;
		ComposeService.isSendingText = false;
		if (BusinessProxy.sSelf != null) {
//			BusinessProxy.sSelf.gridMenu = new Vector<LeftMenu>();
			BusinessProxy.sSelf.gridMenu2 = new Vector<LeftMenu>();
			if (BusinessProxy.sSelf.listBuddy != null
					&& BusinessProxy.sSelf.listBuddy.size() >= 0)
				BusinessProxy.sSelf.listBuddy.clear();
		}
		setLong(context, Constants.MENU_RFRESH_TIME,
				System.currentTimeMillis() - 1000 * 60 * 100);
		Utilities.setBoolean(context, Constants.CLICK_ON_ACTIVITY_NOTIFICATION,
				false);
		setString(context, Constants.NOTIFICATIO_COUNT, "0");
		Utilities.setBoolean(context, Constants.CLICK_ON_ACTIVITY_NOTIFICATION,
				false);
	}

	public static String readStream(InputStream in) {
		BufferedReader reader = null;
		String res = "";
		try {
			reader = new BufferedReader(new InputStreamReader(in));
			String line = "";
			while ((line = reader.readLine()) != null) {
				// System.out.println("line====="+line);
				res += line;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return res;
	}

	public static boolean isComposeServiceRunning(Context activity) {
		ActivityManager manager = (ActivityManager) activity
				.getSystemService(activity.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if ("com.kainat.app.android.engine.ComposeService"
					.equals(service.service.getClassName())) {
				// System.out.println("Rocketalk---------searvice already runnini!!!!!!!!!!!!!!!!!");
				return true;
			}
		}
		return false;
	}

	public static void startComposeService(Context activity) {
		// System.out.println("Rocketalk---------startComposeService");
		if (!isComposeServiceRunning(activity)) {
			activity.startService(new Intent(activity, ComposeService.class));
		}

	}

	public static void checkAndStartRefresh(Context activity) {
		long diff = ((System.currentTimeMillis() - RefreshService.idealTime) / 1000);

		// System.out.println("-----------idel--1------"+Utilities.isRefreshServiceRunning(activity));
		// System.out.println("-----------idel--1------"+diff);

		if (Utilities.forgroundApp(activity))
			if (/* (Utilities.isRefreshServiceRunning(activity)) && */diff > 15) {
				Intent service = new Intent(activity, RefreshService.class);
				activity.stopService(service);
				// System.out.println("-----------idel--inside if------"+diff);
				service = new Intent(activity, RefreshService.class);
				activity.startService(service);
			}
		// System.out.println("-----------idel--------"+Utilities.isRefreshServiceRunning(activity));
		// System.out.println("-----------idel--------"+diff);

	}

	public static Hashtable<String, String> parseError(String res) {
		Hashtable<String, String> parse = new Hashtable<String, String>();
		// System.out.println("res======"+res);
		try {
			// <response><status>failed</status><errorCode>ERR9999</errorCode><message>System
			// Error.Please try Laternull</message></response>

			// res="<response><status>failed</status><errorCode>RT_ERR_10005</errorCode><message>RT_ERR_10005</message></response>"
			// ;
			ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(
					res.getBytes("UTF-8"));
			Document doc = XMLUtils.parseInputSourceToXML(new InputSource(
					arrayInputStream));

			NodeList nodeGeneric = doc.getElementsByTagName("response");
			Element e = (Element) nodeGeneric.item(0);
			// NodeList nodeGeneric = doc.getElementsByTagName("status");
			// Element e = (Element) nodeGeneric.item(0);
			String code = XMLUtils.getValue(e, "status");
			// String code = e.getAttribute("status") ;
			parse.put("status", code);

			// NodeList errorCode = doc.getElementsByTagName("errorCode");
			// e = (Element) errorCode.item(0);
			// String errorCodeStr = e.getAttribute("errorCode") ;
			String errorCodeStr = XMLUtils.getValue(e, "errorCode");
			parse.put("errorCode", errorCodeStr);

			// NodeList message = doc.getElementsByTagName("message");
			// e= (Element) message.item(0);
			// String messageStr = e.getAttribute("message") ;
			String messageStr = XMLUtils.getValue(e, "message");
			parse.put("message", messageStr);
			// System.out.println("----------parse : "+parse.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return parse;
	}

	public static Hashtable<String, String> parseXMLRespons(String res) {
		Hashtable<String, String> parse = new Hashtable<String, String>();
		// System.out.println("res======"+res);
		try {
			// <response><status>failed</status><errorCode>ERR9999</errorCode><message>System
			// Error.Please try Laternull</message></response>

			// res="<response><status>failed</status><errorCode>RT_ERR_10005</errorCode><message>RT_ERR_10005</message></response>"
			// ;
			ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(
					res.getBytes("UTF-8"));
			Document doc = XMLUtils.parseInputSourceToXML(new InputSource(
					arrayInputStream));

			NodeList nodeGeneric = doc.getElementsByTagName("response");
			Element e = (Element) nodeGeneric.item(0);
			// NodeList nodeGeneric = doc.getElementsByTagName("status");
			// Element e = (Element) nodeGeneric.item(0);
			String code = XMLUtils.getValue(e, "status");
			// String code = e.getAttribute("status") ;
			parse.put("status", code);

			// NodeList errorCode = doc.getElementsByTagName("errorCode");
			// e = (Element) errorCode.item(0);
			// String errorCodeStr = e.getAttribute("errorCode") ;
			String errorCodeStr = XMLUtils.getValue(e, "errorCode");
			parse.put("errorCode", errorCodeStr);

			// NodeList message = doc.getElementsByTagName("message");
			// e= (Element) message.item(0);
			// String messageStr = e.getAttribute("message") ;
			String messageStr = XMLUtils.getValue(e, "desc");
			parse.put("desc", messageStr);
			// System.out.println("----------parse : "+parse.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return parse;
	}

	public static void manageDB(Context context) {
		if (1 == 1)
			return;
		// int limit = 50 ;
		Hashtable<String, String> cid = new Hashtable<String, String>();
		Cursor cursor = context.getContentResolver().query(
				MediaContentProvider.CONTENT_URI_INBOX, null, null, null,
				MessageTable.INSERT_TIME + " ASC");
		for (int i = 0; i < cursor.getCount(); i++) {
			cursor.moveToPosition(i);
			cid.put(cursor.getString(cursor
					.getColumnIndex(MessageTable.CONVERSATION_ID)), cursor
					.getString(cursor
							.getColumnIndex(MessageTable.CONVERSATION_ID)));
		}
		// System.out.println("----manageDB--CIDS-- : "+cid.toString());
		cursor.close();
		Enumeration<String> keys = cid.keys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			cursor = context.getContentResolver().query(
					MediaContentProvider.CONTENT_URI_INBOX, null,
					MessageTable.CONVERSATION_ID + " =?", new String[] { key },
					MessageTable.INSERT_TIME + " ASC");
			// System.out.println("----manageDB--row count--"+cursor.getCount()+" of cid "+key);
			if (cursor.getCount() > Constants.PAGE_DISPLAY_LIMITE) {
				cursor.moveToPosition(cursor.getCount()
						- Constants.PAGE_DISPLAY_LIMITE);
				long l = cursor.getLong(cursor
						.getColumnIndex(MessageTable.INSERT_TIME));
				int deletedRows = context.getContentResolver().delete(
						MediaContentProvider.CONTENT_URI_INBOX,
						MessageTable.CONVERSATION_ID + " = ? and "
								+ MessageTable.INSERT_TIME + " < " + l,
						new String[] { key });
				// System.out.println("----manageDB--deletedRows--"+deletedRows
				// +" of cid "+key);
			}
			cursor.close();
		}
	}

	public static void updateSendingToRetry(Context context) {
		try {
			ContentValues values = new ContentValues();
			values.put(MessageConversationTable.SENDING_STATE,
					Constants.MESSAGE_STATUS_UNABLE_TO_SEND);// int
			int updatedRow = context.getContentResolver().update(
					MediaContentProvider.CONTENT_URI_INBOX,
					values,
					MessageConversationTable.SENDING_STATE + "= "
							+ Constants.MESSAGE_STATUS_SENDING, null);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public static void manageDB(Context context, String cid) {
		// if(1==1)return;

		Cursor cursor = context.getContentResolver().query(
				MediaContentProvider.CONTENT_URI_INBOX, null,
				MessageTable.CONVERSATION_ID + " =?", new String[] { cid },
				MessageTable.INSERT_TIME + " ASC");
		// System.out.println("----manageDB--row count--"+cursor.getCount()+" of cid "+cid);
		if (cursor.getCount() > Constants.PAGE_DISPLAY_LIMITE) {
			try {
				cursor.moveToPosition(cursor.getCount()
						- Constants.PAGE_DISPLAY_LIMITE);
				// int i = cursor.getCount()-Constants.PAGE_DISPLAY_LIMITE ;
				// boolean delete = false;
				// for (; i > 0; i--) {
				// cursor.moveToPosition(i) ;
				// String l =
				// cursor.getString(cursor.getColumnIndex(MessageTable.MFU_ID));
				// if(l.equalsIgnoreCase("-1"))
				// continue;
				// else{
				// delete=true;
				// break;
				// }
				// }
				// cursor.moveToPosition(i) ;
				long l = cursor.getLong(cursor
						.getColumnIndex(MessageTable.INSERT_TIME));
				cursor.close();
				// if(delete)
				{
					int deletedRows = context.getContentResolver().delete(
							MediaContentProvider.CONTENT_URI_INBOX,
							MessageTable.CONVERSATION_ID + " = ? and "
									+ MessageTable.INSERT_TIME + " < " + l,
							new String[] { cid });
					// System.out.println("----manageDB--row deletedRows--"+deletedRows);
					if (deletedRows > 0) {
						ContentValues values = new ContentValues();
						values.put(MessageConversationTable.IS_NEXT, "1");// int
						int updatedRow = context.getContentResolver().update(
								MediaContentProvider.CONTENT_URI_INBOX,
								values,
								MessageConversationTable.CONVERSATION_ID
										+ "= ? and "
										+ MessageConversationTable.USER_ID
										+ " =? ",
								new String[] { cid,
										BusinessProxy.sSelf.getUserId() + "" });

						context.getContentResolver().delete(
								MediaContentProvider.CONTENT_URI_INBOX,
								MessageTable.MESSAGE_ID + " = '-999' ", null);
						Message message = new Message();
						message.messageId = "-999";
						message.user_id = "" + BusinessProxy.sSelf.getUserId();
						message.conversationId = cid;
						message.mfuId = "-999";
						message.inserTime = 0;// Long.MAX_VALUE ;
						FeedTask f = new FeedTask(context);
						f.more = true;
						f.saveMessage(message);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (!cursor.isClosed())
			cursor.close();
	}

	public static void manageDBXX(Context context, String cid) {
		// if(1==1)return;

		Cursor cursor = context.getContentResolver().query(
				MediaContentProvider.CONTENT_URI_INBOX, null,
				MessageTable.CONVERSATION_ID + " =?", new String[] { cid },
				MessageTable.INSERT_TIME + " ASC");
		// System.out.println("----manageDB--row count--"+cursor.getCount()+" of cid "+cid);
		if (cursor.getCount() > Constants.PAGE_DISPLAY_LIMITE) {
			try {
				// cursor.moveToPosition(cursor.getCount()-Constants.PAGE_DISPLAY_LIMITE)
				// ;
				int i = cursor.getCount() - Constants.PAGE_DISPLAY_LIMITE;
				boolean delete = false;
				for (; i > 0; i--) {
					cursor.moveToPosition(i);
					String l = cursor.getString(cursor
							.getColumnIndex(MessageTable.MFU_ID));
					if (l.equalsIgnoreCase("-1"))
						continue;
					else {
						delete = true;
						break;
					}
				}
				// cursor.moveToPosition(i) ;
				long l = cursor.getLong(cursor
						.getColumnIndex(MessageTable.INSERT_TIME));
				cursor.close();
				if (delete) {
					int deletedRows = context.getContentResolver().delete(
							MediaContentProvider.CONTENT_URI_INBOX,
							MessageTable.CONVERSATION_ID + " = ? and "
									+ MessageTable.INSERT_TIME + " < " + l,
							new String[] { cid });
					// System.out.println("----manageDB--row deletedRows--"+deletedRows);
					if (deletedRows > 0) {
						context.getContentResolver().delete(
								MediaContentProvider.CONTENT_URI_INBOX,
								MessageTable.MESSAGE_ID + " = '-999' ", null);
						Message message = new Message();
						message.messageId = "-999";
						message.user_id = "" + BusinessProxy.sSelf.getUserId();
						message.conversationId = cid;
						message.mfuId = "-999";
						message.inserTime = 0;// Long.MAX_VALUE ;
						FeedTask f = new FeedTask(context);
						f.more = true;
						f.saveMessage(message);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			// System.out.println("----manageDB--deletedRows--"+deletedRows
			// +" of cid "+key);
		}

	}

	public static void removeMoreFromInbox(Context context, String cid) {
		// if(1==1)return;
		int deletedRows = context.getContentResolver().delete(
				MediaContentProvider.CONTENT_URI_INBOX,
				MessageTable.CONVERSATION_ID + " = ? and " + MessageTable.MORE
						+ " = 'true' ", new String[] { cid });
		// System.out.println("----------------remover more message from inbox : "+deletedRows);
		if (deletedRows > 0) {
			ContentValues value = new ContentValues();
			value.put(MessageTable.IS_NEXT, "1");
			int updatedRow = context.getContentResolver().update(
					MediaContentProvider.CONTENT_URI_INBOX,
					value,
					MessageConversationTable.CONVERSATION_ID + "= ? and "
							+ MessageConversationTable.USER_ID + " =?",
					new String[] { cid, "" + BusinessProxy.sSelf.getUserId() });

			context.getContentResolver().delete(
					MediaContentProvider.CONTENT_URI_INBOX,
					MessageTable.MESSAGE_ID + " = '-999' ", null);
			Message message = new Message();
			message.messageId = "-999";
			message.user_id = "" + BusinessProxy.sSelf.getUserId();
			message.conversationId = cid;
			message.mfuId = "-999";
			message.inserTime = 0;// Long.MAX_VALUE ;
			FeedTask f = new FeedTask(context);
			f.more = true;
			f.saveMessage(message);
		}
	}

	public static void cleanBookmark(Context context) {
		// if(1==1)return;
		int deletedRows = context.getContentResolver().delete(
				MediaContentProvider.CONTENT_URI_BOOKMARK, null, null);
		// System.out.println("----------------remover more message from bookamrk : "+deletedRows);

	}

	public static void cleanToBeDeleted(Context context) {
		// if(1==1)return;
		try {
			int deletedRows = context.getContentResolver().delete(
					MediaContentProvider.CONTENT_URI_INBOX,
					MessageTable.MESSAGE_ATTRIBUTE + " = 'TOBEDELETD' ", null);
			// System.out.println("----------------remover more message from cleanToBeDeleted : "+deletedRows);

		} catch (Exception e) {
			// TODO: handle exception
		}
		updateSendingToRetry(context);
	}

	public static void checkAndShift(Context context) {

		try {
			Hashtable<String, String> cid = new Hashtable<String, String>();
			Cursor cursor = context.getContentResolver().query(
					MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST,
					null, null, null, MessageTable.INSERT_TIME + " ASC");
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				cid.put(cursor.getString(cursor
						.getColumnIndex(MessageTable.CONVERSATION_ID)), cursor
						.getString(cursor
								.getColumnIndex(MessageTable.CONVERSATION_ID)));
			}
			// System.out.println("----manageDB--CIDS-- : "+cid.toString());
			cursor.close();
			Enumeration<String> keys = cid.keys();
			if(BusinessProxy.sSelf.sqlDB == null)
				BusinessProxy.sSelf.initializeDatabase();
			while (keys.hasMoreElements()) {
				String key = keys.nextElement();
				cursor = BusinessProxy.sSelf.sqlDB.query(MessageTable.TABLE,
						null, MessageTable.CONVERSATION_ID + " = ? and "
								+ MessageTable.USER_ID + " = ? and "
								+ MessageTable.MESSAGE_TYPE + " !=?",
						new String[] { key,
								BusinessProxy.sSelf.getUserId() + "",
								"" + Message.MESSAGE_TYPE_SYSTEM_MESSAGE },
						null, null, MessageTable.INSERT_TIME + " ASC");
				// if(cursor.getCount()>0){
				// Message message2 = new Message();
				// cursor.moveToLast();
				// message2 = CursorToObject.conversationObject(cursor) ;
				// }

				Message message2 = new Message();
				{
					if (cursor.getCount() <= 0)
						continue;
					try {
						cursor.moveToPosition(cursor.getCount() - 1);
						message2 = null;
						message2 = CursorToObject.conversationObject(cursor,context);
					} catch (Exception e) {
						e.printStackTrace();
					}
					ContentValues values2 = new ContentValues();
					if (message2 != null) {
						values2.put(MessageTable.MESSAGE_ID, message2.messageId);
						values2.put(MessageTable.MSG_TXT, message2.msgTxt);
						values2.put(MessageTable.VOICE_CONTENT_URLS,
								message2.voice_content_urls);
						values2.put(MessageTable.AUDIO_ID, "");
						values2.put(MessageTable.MESSAGE_TYPE,
								message2.messageType);
						values2.put(MessageTable.IMAGE_CONTENT_THUMB_URLS,
								message2.image_content_thumb_urls);
						values2.put(MessageTable.IMAGE_CONTENT_URLS,
								message2.image_content_urls);

						values2.put(MessageTable.VIDEO_CONTENT_THUMB_URLS,
								message2.video_content_thumb_urls);
						values2.put(MessageTable.VIDEO_CONTENT_URLS,
								message2.video_content_urls);

						values2.put(MessageTable.ANI_TYPE, message2.aniType);
						values2.put(MessageTable.ANI_VALUE, message2.aniValue);
					}

					int updatedRow = context
							.getContentResolver()
							.update(MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST,
									values2,
									MessageConversationTable.CONVERSATION_ID
											+ "= ? and "
											+ MessageConversationTable.USER_ID
											+ " =?",
									new String[] {
											key,
											""
													+ BusinessProxy.sSelf
															.getUserId() });

					// System.out.println("---------checkAndShift : "+updatedRow+" : vid : "+key);

				}
			}
			if (!cursor.isClosed())
				cursor.close();
		} catch (Exception ee) {
			// System.out.println("---------checkAndShift : "+ee.getMessage());
			ee.printStackTrace();
		}
	}

	public static boolean isRefreshServiceRunning(Context context) {
		ActivityManager manager = (ActivityManager) context
				.getApplicationContext().getSystemService(
						context.getApplicationContext().ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if ("com.kainat.app.android.engine.RefreshService"
					.equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	public static String humanReadableByteCount(long bytes, boolean si) {
		int unit = si ? 1000 : 1024;
		if (bytes < unit)
			return bytes + " B";
		int exp = (int) (Math.log(bytes) / Math.log(unit));
		String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1)
				+ (si ? "" : "i");
		return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}

	public static int getScreenType(Activity context) {
		// Determine density
		DisplayMetrics metrics = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		switch (metrics.densityDpi) 
		{
		case DisplayMetrics.DENSITY_LOW:// 120
			return Constants.SCREENLAYOUT_SIZE_SMALL;
		case DisplayMetrics.DENSITY_HIGH:// 240
			return Constants.SCREENLAYOUT_SIZE_LARGE;
		case DisplayMetrics.DENSITY_DEFAULT:// DisplayMetrics.DENSITY_MEDIUM
											// this is 160
			return Constants.SCREENLAYOUT_SIZE_MEDIUM;
		case 320://320
			return Constants.SCREENLAYOUT_SIZE_LARGE;
			
		case 480://Samsung S4, HTC One
			return Constants.SCREENLAYOUT_SIZE_X_LARGE;
			
		default:
			return Constants.SCREENLAYOUT_SIZE_LARGE;
		}
	}

	public static String capitalizeString(String string) {
		char[] chars = string.toLowerCase().toCharArray();
		boolean found = false;
		for (int i = 0; i < chars.length; i++) {
			if (!found && Character.isLetter(chars[i])) {
				chars[i] = Character.toUpperCase(chars[i]);
				found = true;
			} else if (Character.isWhitespace(chars[i]) || chars[i] == '.'
					|| chars[i] == '\'') { // You can add other chars here
				found = false;
			}
		}
		return String.valueOf(chars);
	}

	public static void vibrate(Context c) {
		Vibrator v = (Vibrator) c.getSystemService(Context.VIBRATOR_SERVICE);
		// Vibrate for 500 milliseconds
		v.vibrate(500);
	}

	public static boolean forgroundApp(Context context) {
		// ActivityManager activityManager = (ActivityManager)
		// newContext.getSystemService( Context.ACTIVITY_SERVICE );
		// List<RunningAppProcessInfo> appProcesses =
		// activityManager.getRunningAppProcesses();
		// for(RunningAppProcessInfo appProcess : appProcesses){
		// if(appProcess.importance ==
		// RunningAppProcessInfo.IMPORTANCE_BACKGROUND){
		// System.out.println("----Foreground App: "+
		// appProcess.processName+" : "+appProcess.importance);
		// if("com.kainat.app.android".equalsIgnoreCase(appProcess.processName))
		// return true;
		// }
		// }
		// return false;

		ActivityManager am = (ActivityManager) context
				.getSystemService(Activity.ACTIVITY_SERVICE);
		String packageName = am.getRunningTasks(1).get(0).topActivity
				.getPackageName();
		String className = am.getRunningTasks(1).get(0).topActivity
				.getClassName();
		// System.out.println("----Foreground App: "+
		// packageName+" : "+className);
		final String packageNameMy = context.getPackageName();
		if (packageNameMy.equalsIgnoreCase(packageName))
			return true;
		else
			return false;

		// ActivityManager activityManager = (ActivityManager)
		// context.getSystemService(Context.ACTIVITY_SERVICE);
		// List<RunningAppProcessInfo> appProcesses =
		// activityManager.getRunningAppProcesses();
		// if (appProcesses == null) {
		// return false;
		// }
		// final String packageName = context.getPackageName();
		// // System.out.println("----Foreground App all : "+
		// appProcess.processName+" : "+appProcess.importance);
		//
		// for (RunningAppProcessInfo appProcess : appProcesses) {
		// System.out.println("----Foreground App: "+
		// appProcess.processName+" : "+appProcess.importance);
		// if (appProcess.importance ==
		// RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
		// appProcess.processName.equals(packageName)) {
		// return true;
		// }
		// }
		// return false;
	}

	/*
	 * These constants aren't yet available in my API level (7), but I need to
	 * handle these cases if they come up, on newer versions
	 */
	public static final int NETWORK_TYPE_EHRPD = 14; // Level 11
	public static final int NETWORK_TYPE_EVDO_B = 12; // Level 9
	public static final int NETWORK_TYPE_HSPAP = 15; // Level 13
	public static final int NETWORK_TYPE_IDEN = 11; // Level 8
	public static final int NETWORK_TYPE_LTE = 13; // Level 11

	/**
	 * Check if the connection is fast
	 * 
	 * @param type
	 * @param subType
	 * @return
	 */

	public static String isConnectedFast(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();

		if ((info != null && info.isConnected())) {
			return isConnectionFast(info.getType(), info.getSubtype());
		} else
			return "No NetWork Access";

	}

	public static String isConnectionFast(int type, int subType) {
		if (type == ConnectivityManager.TYPE_WIFI) {
			System.out.println("CONNECTED VIA WIFI");
			return "CONNECTED VIA WIFI";
		} else if (type == ConnectivityManager.TYPE_MOBILE) {
			switch (subType) {
			case TelephonyManager.NETWORK_TYPE_1xRTT:
				return "NETWORK TYPE 1xRTT"; // ~ 50-100 kbps
			case TelephonyManager.NETWORK_TYPE_CDMA:
				return "NETWORK TYPE CDMA (3G) Speed: 2 Mbps"; // ~ 14-64 kbps
			case TelephonyManager.NETWORK_TYPE_EDGE:

				return "NETWORK TYPE EDGE (2.75G) Speed: 100-120 Kbps"; // ~
																		// 50-100
																		// kbps
			case TelephonyManager.NETWORK_TYPE_EVDO_0:
				return "NETWORK TYPE EVDO_0"; // ~ 400-1000 kbps
			case TelephonyManager.NETWORK_TYPE_EVDO_A:
				return "NETWORK TYPE EVDO_A"; // ~ 600-1400 kbps
			case TelephonyManager.NETWORK_TYPE_GPRS:
				return "NETWORK TYPE GPRS (2.5G) Speed: 40-50 Kbps"; // ~ 100
																		// kbps
			case TelephonyManager.NETWORK_TYPE_HSDPA:
				return "NETWORK TYPE HSDPA (4G) Speed: 2-14 Mbps"; // ~ 2-14
																	// Mbps
			case TelephonyManager.NETWORK_TYPE_HSPA:
				return "NETWORK TYPE HSPA (4G) Speed: 0.7-1.7 Mbps"; // ~
																		// 700-1700
																		// kbps
			case TelephonyManager.NETWORK_TYPE_HSUPA:
				return "NETWORK TYPE HSUPA (3G) Speed: 1-23 Mbps"; // ~ 1-23
																	// Mbps
			case TelephonyManager.NETWORK_TYPE_UMTS:
				return "NETWORK TYPE UMTS (3G) Speed: 0.4-7 Mbps"; // ~ 400-7000
																	// kbps
				// NOT AVAILABLE YET IN API LEVEL 7
			case NETWORK_TYPE_EHRPD:
				return "NETWORK TYPE EHRPD"; // ~ 1-2 Mbps
			case NETWORK_TYPE_EVDO_B:
				return "NETWORK_TYPE_EVDO_B"; // ~ 5 Mbps
			case NETWORK_TYPE_HSPAP:
				return "NETWORK TYPE HSPA+ (4G) Speed: 10-20 Mbps"; // ~ 10-20
																	// Mbps
			case NETWORK_TYPE_IDEN:
				return "NETWORK TYPE IDEN"; // ~25 kbps
			case NETWORK_TYPE_LTE:
				return "NETWORK TYPE LTE (4G) Speed: 10+ Mbps"; // ~ 10+ Mbps
				// Unknown
			case TelephonyManager.NETWORK_TYPE_UNKNOWN:
				return "NETWORK TYPE UNKNOWN";
			default:
				return "";
			}
		} else {
			return "";
		}
	}

	static Matrix matrix = new Matrix();
	private static int containerWidth;
	private static int containerHeight;
	private static Bitmap imgBitmap = null;
	public static final int DEFAULT_SCALE_FIT_INSIDE = 0;
	public static final int DEFAULT_SCALE_ORIGINAL = 1;
	//

	private static int defaultScale;

	public static boolean reduceImageAndShift(Uri uri) {
		float newWidth = 0;
		float newHeight = 0;
//		System.out.println("----" + uri.toString());
		InputStream in = null;
		try {

			/*
			 * if(imgBitmap != null) { int imgHeight = imgBitmap.getHeight();
			 * int imgWidth = imgBitmap.getWidth();
			 * 
			 * System.out.println("imgWidth : "+imgWidth);
			 * System.out.println("imgHeight : "+imgHeight);
			 * 
			 * float scale; int initX = 0; int initY = 0;
			 * 
			 * if(defaultScale == DEFAULT_SCALE_FIT_INSIDE) {
			 * if(SettingData.sSelf.isIm())
			 * System.out.println("----DEFAULT_SCALE_FIT_INSIDE"); if(imgWidth >
			 * containerWidth) { if(SettingData.sSelf.isIm())
			 * System.out.println("----DEFAULT_SCALE_FIT_INSIDE if"); scale =
			 * (float)containerWidth / imgWidth; float newHeight = imgHeight *
			 * scale; initY = (containerHeight - (int)newHeight)/2;
			 * 
			 * matrix.setScale(scale, scale); matrix.postTranslate(0, initY); }
			 * else { if(SettingData.sSelf.isIm())
			 * System.out.println("----DEFAULT_SCALE_FIT_INSIDE else"); scale =
			 * (float)containerHeight / imgHeight; //
			 * if((imgHeight/containerWidth) scale =containerWidth/ imgHeight
			 * ;//1 float newWidth = imgWidth * scale; float newHeight =
			 * imgHeight * scale; initX = (containerWidth - (int)newWidth)/2;
			 * initY = (containerHeight - (int)newHeight)/2;
			 * matrix.setScale(scale, scale); matrix.postTranslate(initX,
			 * initY); } // // curX = initX; // curY = initY; // // currentScale
			 * = scale; // minScale = scale; } else {
			 * if(SettingData.sSelf.isIm())
			 * System.out.println("----DEFAULT_SCALE_FIT_INSIDE else");
			 * if(imgWidth > containerWidth) { if(SettingData.sSelf.isIm())
			 * System.out.println("----DEFAULT_SCALE_FIT_INSIDE else  1"); initY
			 * = (containerHeight - (int)imgHeight)/2; matrix.postTranslate(0,
			 * initY); } else { if(SettingData.sSelf.isIm())
			 * System.out.println("----DEFAULT_SCALE_FIT_INSIDE else  2"); initX
			 * = (containerWidth - (int)imgWidth)/2; matrix.postTranslate(initX,
			 * 0); }
			 * 
			 * // curX = initX; // curY = initY; // // currentScale = 1.0f; //
			 * minScale = 1.0f; Paint paint = new Paint() ; Canvas canvas = new
			 * Canvas(imgBitmap); canvas.drawBitmap(imgBitmap, matrix, paint);
			 * // paint.get } }
			 */
			// //////////////////////////////////////////////////////////
			final int IMAGE_MAX_SIZE = BusinessProxy.sSelf.displayheight
					* BusinessProxy.sSelf.displayheight;// 200000; // 0.2MP
			containerWidth = 50;// BusinessProxy.sSelf.displayheight;
			containerHeight = 50;// BusinessProxy.sSelf.displayheight;
			// File f = new File(uri.toString());

			File file = new File(uri.toString());
			if (file.exists()) {
				// Toast.makeText(getActivity(), "File exists in /mnt",
				// Toast.LENGTH_SHORT);
			}

			in = new FileInputStream(file);// new
											// inopenFileInput(uri.toString());//getContentResolver().openInputStream(uri);

			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			// BitmapFactory.Options bfo = new BitmapFactory.Options();
			// bfo.inDither=false; //Disable Dithering mode
			// bfo.inPurgeable=true;
			o.inDither = false; // Disable Dithering mode
			o.inPurgeable = true;
			o.inJustDecodeBounds = true; // request only the dimesion
			BitmapFactory.decodeStream(in, null, o);
			in.close();

			int scale = 1;
			while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) > IMAGE_MAX_SIZE) {
				scale++;
			}

			Bitmap b = null;
			// in = getContentResolver().openInputStream(uri);
			// in = openFileInput(uri.toString());
			file = new File(uri.toString());
			in = new FileInputStream(file);
			if (scale > 1) {
				scale--;
				// scale to max possible inSampleSize that still yields an image
				// larger than target
				o = new BitmapFactory.Options();
				o.inSampleSize = scale;
				b = BitmapFactory.decodeStream(in, null, o);
				// resize to desired dimensions
				int height = b.getHeight();
				int width = b.getWidth();

				double y = Math.sqrt(IMAGE_MAX_SIZE
						/ (((double) width) / height));
				double x = (y / height) * width;
				// ///////////&&&&&&&&&&&&&&&&&&&&&&
				int imgHeight = imgBitmap.getHeight();
				int imgWidth = imgBitmap.getWidth();

//				System.out.println("imgWidth : " + imgWidth);
//				System.out.println("imgHeight : " + imgHeight);

				float scale1;
				int initX = 0;
				int initY = 0;

				if (defaultScale == DEFAULT_SCALE_FIT_INSIDE) {
					if (SettingData.sSelf.isIm())
						System.out.println("----DEFAULT_SCALE_FIT_INSIDE");
					if (imgWidth > containerWidth) {
						if (SettingData.sSelf.isIm())
							System.out
									.println("----DEFAULT_SCALE_FIT_INSIDE if");
						scale1 = (float) containerWidth / imgWidth;
						newHeight = imgHeight * scale;
						newWidth = imgWidth * scale;
						// initY = (containerHeight - (int)newHeight)/2;
						//
						// matrix.setScale(scale, scale);
						// matrix.postTranslate(0, initY);
					} else {
						if (SettingData.sSelf.isIm())
							System.out
									.println("----DEFAULT_SCALE_FIT_INSIDE else");
						scale1 = (float) containerHeight / imgHeight;
						// if((imgHeight/containerWidth)
						scale = containerWidth / imgHeight;// 1
						newWidth = imgWidth * scale;
						newHeight = imgHeight * scale;
						// initX = (containerWidth - (int)newWidth)/2;
						// initY = (containerHeight - (int)newHeight)/2;
						// matrix.setScale(scale, scale);
						// matrix.postTranslate(initX, initY);
					}
				}
				// ///////////&&&&&&&&&&&&&&&&&&&&&&
				// Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, (int) x,
				// (int) y, true);
				Bitmap scaledBitmap = Bitmap.createScaledBitmap(b,
						(int) newWidth, (int) newHeight, true);
				b.recycle();
				b = scaledBitmap;
				System.gc();
			} else {
				b = BitmapFactory.decodeStream(in);
			}
			File sdDir = android.os.Environment.getExternalStorageDirectory();
			File cacheDir = new File(sdDir, Constants.imageTempPath);
			if (!cacheDir.exists())
				cacheDir.mkdirs();
			File f = new File(cacheDir, System.currentTimeMillis() + ".jpg");
			f.createNewFile();

			FileOutputStream out = new FileOutputStream(f);
			b.compress(Constants.COMPRESS_TYPE, Constants.COMPRESS, out);
			b.recycle();
			in.close();
			out.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			return false;
		}

	}

	public static String getDate(DatePicker datePicker) {
		Calendar dateOfKYC = new GregorianCalendar(datePicker.getYear(),
				datePicker.getMonth(), datePicker.getDayOfMonth());

		return dateOfKYC.get(Calendar.DAY_OF_MONTH) + "/"
				+ dateOfKYC.get(Calendar.MONTH) + "/"
				+ dateOfKYC.get(Calendar.YEAR);
	}

	public static String[] split(String str, String sep) {
		Vector v = new Vector();
		int offset = str.indexOf(sep);
		int sepLen = sep.length();
		while (offset != -1) {
			v.addElement(str.substring(0, offset).trim());
			if (str.length() > (offset + sepLen))
				str = str.substring(offset + sepLen);
			else {
				str = null;
				break;
			}
			offset = str.indexOf(sep);
		}
		if (str != null && str.length() > 0)
			v.addElement(str.trim());
		String[] array = new String[v.size()];
		v.copyInto(array);
		return array;
	}

	public static  String createMediaID(String filePath,String idFor) {
		try {

			String url = "http://" + Urls.TEJAS_HOST + "/tejas/feeds/api/media/create";
			String response = null;
			
//			boolean done = false;
//			int retry = 0;
//			while (!done && retry < 3) {
				try {
//					Thread.sleep(2000);
					response = uploadAudio(url,  filePath,idFor);

//					done = true;
				} catch (Exception e) {
//					retry++;
					e.printStackTrace();
				}
//			}

			final JSONObject jsonObject = new JSONObject(response);
			final String status = jsonObject.getString("status");
			if (status.equals("error")) {
				try {
					try {

					} catch (Exception e) {
						e.printStackTrace();
					}

					return response;
				} catch (Exception e) {
				}
			} else if (status.trim().equalsIgnoreCase("success")) {
				try {
					
					return jsonObject.getString("fileId");
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

		} catch (Exception e1) {
			e1.printStackTrace();

		}
		return null;
	}

	private static  String uploadAudio(String urlStr, String path,String idFor) {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(urlStr);

		File file = new File(path);
		MultipartEntity mpEntity = new MultipartEntity(HttpMultipartMode.STRICT);

		try {
			mpEntity.addPart("data", new FileBody(file,
					"application/octet-stream"));
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		httppost.setHeader("ID-FOR", idFor);
		httppost.setHeader("RT-DEV-KEY", "" + BusinessProxy.sSelf.getClientId());
		httppost.setHeader(
				"RT-APP-KEY",
				HttpHeaderUtils.createRTAppKeyHeader(
						BusinessProxy.sSelf.getUserId(),
						SettingData.sSelf.getPassword()));
		httppost.setEntity(mpEntity);

		HttpResponse response;
		try {
			response = httpclient.execute(httppost);
			HttpEntity resEntity = response.getEntity();

			HttpEntity r_entity = response.getEntity();
			String responseString = EntityUtils.toString(r_entity);
			if (resEntity != null) {
			}
			if (resEntity != null) {
				resEntity.consumeContent();
			}
			System.out.println("Utilities :: responseString : "+responseString);
			return responseString;// "sucess" ;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "fail";
	}
	
	public static void openProfile(Activity activity,String name){
		Intent i =new Intent(activity,ProfileViewActivity.class) ;
		i.putExtra("USERID", SettingData.sSelf.getUserName().toLowerCase()) ;
		i.putExtra("USERID",name) ;
		activity.startActivityForResult(i, 34234) ;
	}
	
	public static int calCulateAge(String date) {
		try{//birthday":"26/9/1997"
		String dobArr[] = date.split("/");
		
		int year = Integer.parseInt(dobArr[2]);
		int month = (Integer.parseInt(dobArr[1]) - 1);
		int datebirth = Integer.parseInt(dobArr[0]);
		Calendar dob = new GregorianCalendar(year, month, datebirth);
		Calendar dateOfBirth = new GregorianCalendar(year, month, datebirth);
		Calendar today = Calendar.getInstance();
		int age = today.get(Calendar.YEAR) - dateOfBirth.get(Calendar.YEAR);
		return age;
		}catch (Exception e) {
			System.out.println("-----date :"+date);
			return 16;
		}
	}
	
	public static String getDisplayName(String fn,String ln, String un){
		String d = "" ;
		if ((fn != null && fn
				.length() > 0)
				&& (ln != null && ln
						.length() > 0))
			d = fn+" "+ln;
		else if ((fn != null && fn
				.length() > 0)
				&& (ln == null || ln
						.length() <= 0))
			d = fn;
		else if ((ln != null && ln
				.length() > 0)
				&& (fn == null || fn
						.length() <= 0))
			d = ln;
		else
			d = un ;
		
		return d;
	}
	public static String getUNLNFNName(String fn,String ln, String un){
		String d = "" ;
		if ((fn != null && fn
				.length() > 0)
				&& (ln != null && ln
						.length() > 0))
			d = fn+" "+ln;
		else if ((fn != null && fn
				.length() > 0)
				&& (ln == null || ln
						.length() <= 0))
			d = d + fn;
		else if ((ln != null && ln
				.length() > 0)
				&& (fn == null || fn
						.length() <= 0))
			d = d + ln;
		
			d = d +"<"+un+">" ;
		
		return d;
	}
	
	public static void writeFile(byte[] data, File f, int id) {

		FileOutputStream out = null;
		if (f != null && !f.exists() && data != null) {
			try {

				f.createNewFile();
				out = new FileOutputStream(f);
				out.write(data);

			} catch (Exception e) {
			} finally {
				try {
					if (out != null)
						out.close();
				} catch (Exception ex) {
				}
			}
		}

	}
	
	public static int getComposeThumbWidth(Context context) {

		if (Utilities.getScreenType((Activity) context) == Constants.SCREENLAYOUT_SIZE_X_LARGE) {
			return 120;
		}  if (Utilities.getScreenType((Activity) context) == Constants.SCREENLAYOUT_SIZE_LARGE) {
			return 120;
		}  if (Utilities.getScreenType((Activity) context) == Constants.SCREENLAYOUT_SIZE_MEDIUM) {
			return 60;
		}  if (Utilities.getScreenType((Activity) context) == Constants.SCREENLAYOUT_SIZE_SMALL) {
			return 60;
		}
		return 60;
	}

	public static int getComposeThumbHeight(Context context) {
		if (Utilities.getScreenType((Activity) context) == Constants.SCREENLAYOUT_SIZE_X_LARGE) {
			return 120;
		}  if (Utilities.getScreenType((Activity) context) == Constants.SCREENLAYOUT_SIZE_LARGE) {
			return 120;
		}  if (Utilities.getScreenType((Activity) context) == Constants.SCREENLAYOUT_SIZE_MEDIUM) {
			return 60;
		}  if (Utilities.getScreenType((Activity) context) == Constants.SCREENLAYOUT_SIZE_SMALL) {
			return 60;
		}
		return 60;
	}
	
	public static int getMessageDetailThumbWidth(Context context) {

		if (Utilities.getScreenType((Activity) context) == Constants.SCREENLAYOUT_SIZE_X_LARGE) {
			return 120;
		}  if (Utilities.getScreenType((Activity) context) == Constants.SCREENLAYOUT_SIZE_LARGE) {
			return 120;
		}  if (Utilities.getScreenType((Activity) context) == Constants.SCREENLAYOUT_SIZE_MEDIUM) {
			return 60;
		}  if (Utilities.getScreenType((Activity) context) == Constants.SCREENLAYOUT_SIZE_SMALL) {
			return 60;
		}
		return 60;
	}

	public static int getMessageDetailThumbHeight(Context context) {
		if (Utilities.getScreenType((Activity) context) == Constants.SCREENLAYOUT_SIZE_X_LARGE) {
			return 120;
		}  if (Utilities.getScreenType((Activity) context) == Constants.SCREENLAYOUT_SIZE_LARGE) {
			return 120;
		}  if (Utilities.getScreenType((Activity) context) == Constants.SCREENLAYOUT_SIZE_MEDIUM) {
			return 60;
		}  if (Utilities.getScreenType((Activity) context) == Constants.SCREENLAYOUT_SIZE_SMALL) {
			return 60;
		}
		return 60;
	}

	public static boolean shouldCompressImage(long size){
		if((size/1024) >60)
			return true ;
		else
			return false;  	
	}
	public static boolean shouldCompressImage(long size, int mbkb){
		if((size/1024) >mbkb)
			return true ;
		else
			return false;  	
	}
	
	public static Bitmap getCompressImage(File f){
		Bitmap myBitmap = null ;
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		if((f.length()/1024)>1500)
			options.inSampleSize = 8;
		else if((f.length()/1024)>1200)
			options.inSampleSize = 6;
		else if((f.length()/1024)>600)
			options.inSampleSize = 4;
		else
			options.inSampleSize = 3;
//		System.out.println("--------------f.getPath():"+f.getPath());
//		System.out.println("--------------f.getPath():"+f.getPath());
//		System.out.println("--------------f.getAbsolutePath():"+f.getAbsolutePath());
		myBitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),options);
		return myBitmap ;
	}



	static Notification mNotification;
	public static NotificationManager mNotificationManager;
	static boolean toggle;
	static int HELLO_ID = 1;
	public static void showNotification(String aTickerText, String aContentText,Context context,boolean push,int screen ) {
		if (push && BusinessProxy.sSelf.mUIActivityManager != null && !BusinessProxy.sSelf.mUIActivityManager.isForeOrBack() )
		{
			try{
				System.out.println("push retrun");
				System.out.println("push BusinessProxy.sSelf.mUIActivityManager.isForeOrBack():"+BusinessProxy.sSelf.mUIActivityManager.isForeOrBack());
			}catch (Exception e) {
				e.printStackTrace();
			}
			return;
		}
		C2DMBroadcastReceiver.notification.add(aContentText);
//		if (DataModel.sSelf.getBoolean(DMKeys.FROM_REGISTRATION)) {
//			return;
//		}
		if (DataModel.sSelf.getBoolean(DMKeys.IS_VERIFIED)) {
			System.out.println("push DataModel.sSelf.getBoolean(DMKeys.IS_VERIFIED):"+DataModel.sSelf.getBoolean(DMKeys.IS_VERIFIED));
			return;
		}
		try {
			CharSequence contentTitle = "RockeTalk"; // expanded message title
			//        	    CharSequence tickerText = "You have new message in RockeTalk Inbox! Please click on the message to view!!";              // ticker-text
			//        	    CharSequence contentText = "You have new message in RockeTalk Inbox! Please click on the message to view!!";      // expanded message text
			int icon = R.drawable.logo; // icon from resources
			long when = System.currentTimeMillis(); // notification time - When to notify 
//			Context context = getApplicationContext(); // application Context
			if (mNotificationManager != null)
				mNotificationManager.cancel(HELLO_ID);
			if (toggle) {
				HELLO_ID = 2;
				toggle = false;
			} else {
				HELLO_ID = 1;
				toggle = true;
			}
			if (mNotification == null) {
				mNotification = new Notification(icon, aTickerText, when);
				mNotification.flags |= Notification.FLAG_AUTO_CANCEL;
				mNotification.defaults |= Notification.DEFAULT_LIGHTS;
			}
			mNotification.number = BusinessProxy.sSelf.newMessageCount;
			mNotification.tickerText = aTickerText;
			//Default
			Intent notificationIntent = new Intent(context, KainatHomeActivity.class);//(UIActivityManager.this, ComposeActivity.class);
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
			/*mNotification.setLatestEventInfo(context, contentTitle, aContentText, contentIntent);			
			mNotification.defaults |= Notification.DEFAULT_SOUND;
			mNotification.defaults |= Notification.DEFAULT_VIBRATE;
			mNotification.ledARGB = 0xff00ff00;
			mNotification.ledOnMS = 300;
			mNotification.ledOffMS = 1000;
			mNotification.flags |= Notification.FLAG_SHOW_LIGHTS;			
			mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			mNotificationManager.notify(HELLO_ID, mNotification);*/
			//Default
			/*Intent notificationIntent = new Intent(context, InboxActivity.class);//(UIActivityManager.this, ComposeActivity.class);
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
			mNotification.setLatestEventInfo(context, contentTitle, aContentText, contentIntent);
			mNotification.defaults |= Notification.DEFAULT_SOUND;
			mNotification.defaults |= Notification.DEFAULT_VIBRATE;
			mNotification.ledARGB = 0xff00ff00;
			mNotification.ledOnMS = 300;
			mNotification.ledOffMS = 1000;
			mNotification.flags |= Notification.FLAG_SHOW_LIGHTS;	
			mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			mNotificationManager.notify(HELLO_ID, mNotification);*/
			
			//Custom
			if(screen==-1)
				notificationIntent = new Intent(context, KainatHomeActivity.class);
			else if(screen==UIActivityManager.SCREEN_COMMUNITY_CHAT)
				notificationIntent = new Intent(context, KainatHomeActivity.class);
			else if(screen==UIActivityManager.SCREEN_COMMUNITY_CHAT)
				notificationIntent = new Intent(context, KainatHomeActivity.class);
			
//	        contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
	        contentIntent = PendingIntent.getActivity(
					context, 0, notificationIntent, 0);
	        
	        /*mNotification.contentIntent = contentIntent;
	        RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.noti_alert);
	    	contentView.setImageViewResource(R.id.image, R.drawable.pushicon);
	    	contentView.setTextViewText(R.id.text, aContentText);//"Hello, this is the custom expanded view. Here we can display the recieved text message. And if we cwant we can show any information regarding the notification or the application.");
	    	mNotification.contentView = contentView;
	    	*/
	        
//	        mNotification = new Notification(icon, aTickerText, when);
//	        mNotification.setLatestEventInfo(context, aTickerText, aContentText, contentIntent);
	        
	        mNotification = new Notification.Builder(context)
	         .setContentTitle(aTickerText)
	         .setContentText(aContentText)
	         .setSmallIcon(icon)
	         .build(); // available from API level 11 and onwards
	        
	        
	        mNotification.flags |= Notification.FLAG_AUTO_CANCEL;
	        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	        mNotificationManager.notify(HELLO_ID, mNotification);
	        ///
			
		} catch (Exception ex) {
			ex.printStackTrace();
			//System.out.println("Error - " + ex.toString());
		}

	}
	
	public static void removeMoreFromActivity(Context context){
		int mRowsUpdated = context.getContentResolver().delete(
				LandingPageContentProvider.CONTENT_URI,
				LandingPageTable.DIRECTION + "=" + 2, null);
		
		if (mRowsUpdated >  0) {
			if (isMore(context)) {
				LandingPageBean landingPageBean1 = new LandingPageBean();
				landingPageBean1.insertDate = 0;// Long.MAX_VALUE;//System.currentTimeMillis()
												// ;
				landingPageBean1.story_id = "0";
				landingPageBean1.direction = 3;
				landingPageBean1.story_type = "activity";
				landingPageBean1.desc = "loading...";
				FeedTask feedTask = new FeedTask(context);
				feedTask.saveStory(landingPageBean1);
			}
		}
	}
	public static  boolean isMore(Context context) {
		Cursor cursor = context.getContentResolver().query(
				LandingPageContentProvider.CONTENT_URI,
				null,
				LandingPageTable.TYPE + " = ? and "
						+ LandingPageTable.DIRECTION + " =?",
				new String[] { "activity", "3" }, null);// LandingPageTable.STORY_ID
														// + " ASC" DESC
		int c = cursor.getCount();
		cursor.close();
		if (c <= 0)
			return true;
		else
			return false;
	}

	public static boolean isSilentMode(Context context){
		AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);

		switch (am.getRingerMode()) {
		    case AudioManager.RINGER_MODE_SILENT:
		        return true ;
		        
		    case AudioManager.RINGER_MODE_VIBRATE:
		    	return true ;
		        
		    case AudioManager.RINGER_MODE_NORMAL:
		    	return false ;
		        
		}
		return false ;
	}
	public static void makeDim(Dialog lDialog){
		WindowManager.LayoutParams lp = lDialog.getWindow().getAttributes();
		lp.dimAmount = Constants.dimamount;
		lDialog.getWindow().setAttributes(lp);
		lDialog.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_DIM_BEHIND);
	}
	
	public static boolean checkEmail(String emailstring) {
		if (emailstring == null)
			return false;
		Matcher m = EMAIL_PATTERN.matcher(emailstring);
		return m.matches();
	}
	public static boolean checkName(String name) {
		if (name == null)
			return false;
		Matcher m = NAME_PATTERN.matcher(name);
		return m.matches();
	}
	public static boolean checkMobileNUmber(String number) {
		if (number == null)
			return false;
		Matcher m = MOBILE_NUMBER_PATTERN.matcher(number);
		return m.matches();
	}
	public static boolean canSendSMS (Context context)
	{
		PackageManager pm = context.getPackageManager();
		return (pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY) || pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY_CDMA));
	}
	public static boolean hasSIMSupport(Context context)
	{
		TelephonyManager telephonyManager1 = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        if(telephonyManager1.getPhoneType()==TelephonyManager.PHONE_TYPE_NONE)
		{
		//coming here if Tablet 
        	return false;
		}
		else{
		//coming here if phone
			return true;
		}
	}
	public static String getScreenResolution(Context context)
	{
	    WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
	    Display display = wm.getDefaultDisplay();
	    DisplayMetrics metrics = new DisplayMetrics();
	    display.getMetrics(metrics);
	    int width = metrics.widthPixels;
	    int height = metrics.heightPixels;

	    return "{" + width + "," + height + "}";
	}
	public static String getDensity(Context context)
	{
		int density= context.getResources().getDisplayMetrics().densityDpi;
		String densityValue = "MDPI";
	   switch(density)
	  {
		  case DisplayMetrics.DENSITY_LOW:
		     densityValue = "LDPI";
		      break;
		  case DisplayMetrics.DENSITY_MEDIUM:
		       densityValue = "MDPI";
		      break;
		  case DisplayMetrics.DENSITY_HIGH:
		      densityValue = "HDPI";
		      break;
		  case DisplayMetrics.DENSITY_XHIGH:
		       densityValue = "XHDPI";
		      break;
		  case DisplayMetrics.DENSITY_XXHIGH:
			  densityValue = "XXHDPI";
			  break;
	  }
	   return densityValue;
	}
	public static String getCountryCode(Context context)
	{
		String cc = "";
		cc = context.getResources().getConfiguration().locale.getCountry();
		if(cc == null || (cc != null && cc.trim().length() == 0))
		{
			TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
			cc = tm.getNetworkCountryIso();
		}
		return cc;
	}
	//---------------- Validation methods ---------------
		public static boolean validateEmail(String emailstring) {
			if (emailstring == null)
				return false;
			Matcher m = EMAIL_PATTERN.matcher(emailstring);
			return m.matches();
		}
		public static boolean validateName(String name) {
			if (name == null)
				return false;
			Matcher m = NAME_PATTERN.matcher(name);
			return m.matches();
		}
		public static boolean validateUserName(String name) {
			if (name == null)
				return false;
			Matcher m = USERNAME_PATTERN.matcher(name);
			return m.matches();
		}
		public static boolean validateMobileNumber(String number) {
			if (number == null)
				return false;
			Matcher m = MOBILE_NUMBER_PATTERN.matcher(number);
			return m.matches();
		}
}