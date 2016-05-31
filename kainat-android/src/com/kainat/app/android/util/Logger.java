package com.kainat.app.android.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;

import android.os.Environment;
import android.util.Log;

import com.kainat.app.android.util.format.SettingData;

/**
 * This class used as logger for debugging purpose where needed.
 */
public final class Logger {

	private static SimpleDateFormat dateFormat = new SimpleDateFormat("[ MMM dd, yyyy HH:mm:ss ]");

	public static final boolean CONVERSATION_LOG = false;
	public static final boolean CHEAT = false;
	public static final boolean SEQ = false;
	public static final boolean ENABLE = false;
	public static final boolean LOGS_ENABLE_USERID = false;
	public static final boolean ENABLE2 = false;
	public static final boolean MULTICHANEL = false;
	public static boolean NET_ERROR = false;
	public static boolean MESSAGEVOICE=true;
	public static boolean NEW_PROFILE=true;
	/**
	 * This method will log message in debug mode
	 * 
	 * @param tag
	 *            Tag to LOG.
	 * @param message
	 *            Message to LOG.
	 */
	public static final void debug(String tag, String message) {
		if (ENABLE) 
		{
			Log.d(tag, message);
			saveToFile(message, null);							 
		}
	}
	public static final void debugOP(String tag, byte[] message) {
//		if (ENABLE) 
		{
//			Log.d(tag, message);
			saveToFile(tag,message, null);
		}
	}

	private static void saveToFile(String data, Throwable th) {
		File file = new File("/sdcard/rtlog.log");
//		System.out.println(data);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
			writer.append(dateFormat.format(System.currentTimeMillis()));
			writer.append(" " + data);
			writer.newLine();
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private static void saveToFile2(String data, Throwable th) {
//		File file = new File("/sdcard/rtlogConversation.log");
//		System.out.println("savefile : "+data);
		String path = Environment.getExternalStorageDirectory().getAbsolutePath();
		File file = new File(path+"/rocketalk/"+SettingData.sSelf.getUserName()+"_con_new.txt");
		
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
			writer.append(dateFormat.format(System.currentTimeMillis()));
			writer.append(" " + data+" \n ");
			writer.newLine();
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private static void saveToFile(String fname, byte []data, Throwable th) {
		File file = new File("/sdcard/"+fname);
		
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			file.createNewFile() ;
			FileOutputStream o = new FileOutputStream(file);
			o.write(data);
			o.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method will log message in info mode
	 * 
	 * @param tag
	 *            Tag to LOG.
	 * @param message
	 *            Message to LOG.
	 */
	public static final void info(String tag, String message) {
		if (ENABLE) {
			Log.i(tag, message);
			saveToFile(message, null);
		}
	}

	/**
	 * This method will log message in warning mode
	 * 
	 * @param tag
	 *            Tag to log.
	 * @param message
	 *            Message to LOG.
	 */
	public static final void warning(String tag, String message) {
		if (ENABLE) {
			Log.w(tag, message);
			saveToFile(message, null);
		}
	}

	/**
	 * This method will log message in error mode
	 * 
	 * @param tag
	 *            Tag to log.
	 * @param message
	 *            Message to LOG.
	 * @param th
	 *            Throwable value to track the stack
	 */
	public static final void error(String tag, String message, Throwable th) {
		if (ENABLE) {
			Log.e(tag, message, th);
			saveToFile(message, th);
			if (null != th) {
				th.printStackTrace();
			}
		}
	}

	/**
	 * @param tag
	 * @param string
	 */
	public static void verbose(String tag, String message) {
		if (ENABLE) {
			Log.v(tag, message);
			saveToFile(message, null);
		}
	}
	public static void conversationLog(String tag, String message) {
		if (CONVERSATION_LOG) 
		{
//			Log.v(tag, message);
			saveToFile2(tag+"::"+message, null);
		}
	}
}