package com.kainat.app.android;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Timer;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.util.ByteArrayBuffer;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.util.Log;

import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.helper.HttpConnectionHelper;
import com.kainat.app.android.util.CommunityFeed;
import com.kainat.app.android.util.CommunityFeedParser;
import com.kainat.app.android.util.KeyValue;
import com.kainat.app.android.util.Logger;
import com.kainat.app.android.util.OnSchedularListener;
import com.kainat.app.android.util.OtsSchedularTask;

public abstract class UICommunityManager extends UIActivityManager implements OnSchedularListener, Runnable {
	public String mPostURL = null;
	private boolean mIsRunning = true;
	public static byte[] mResponseData;
	public static byte[] mResponseDataSuggested;
	public static byte[] mResponseDataDISCOVER;
	protected CommunityFeed feed, oldFeed;
	static CommunityFeed feed_suggested,feed_discover;
	private Thread mThread;
	protected int iLoggedUserID;
	protected static final byte DATA_CALLBACK = 1;
	protected static final byte ERROR_CALLBACK = 2;
	protected static final byte HTTP_TIMEOUT = 3;
	private static final int CHUNK_LENGTH = 1024;
	public boolean requestednextPage = false;
	protected static boolean lodingCanceled = false;
	public boolean cancelRequest = false ;
	public static boolean timelineFollow = false;
	private static final String TAG = UICommunityManager.class.getSimpleName();
	
	
	/**
	 * Variable to store post data
	 */
	public byte[] postData;

	public Timer mCallBackTimer;

	private void notifyThread() {
		synchronized (this) {
			notify();
		}
	}

	protected void startThread() {
		if (mThread == null)
			mThread = new Thread(this);
		mIsRunning = true;
		if (mThread.getState() != Thread.State.WAITING)
			mThread.start();
		else
			notifyThread();
	}

	protected void cancelThread() {
		mIsRunning = false;
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
	}

	HttpConnectionHelper helper = null;
	int responseCode;

	protected void executeRequest(String aURL, String aMedia) {
		if (aURL == null || aURL.trim().length() <= 0)
			return;
		mResponseData = null;
		mPostURL = aURL;
		mIsRunning = true;
		mResponseData = null;

		notifyThread();
	}

	public void parseXMLData() {
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			XMLReader xr = sp.getXMLReader();
			CommunityFeedParser myXMLHandler = new CommunityFeedParser();
			xr.setContentHandler(myXMLHandler);
			ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(mResponseData);
			Log.w(TAG, "parseXMLData : "+new String(mResponseData));
//			String xml = new String(mResponseData);
//			System.out.println(xml);
//			mResponseData = null;
			xr.parse(new InputSource(arrayInputStream));
			if (feed != null) {
				for (int i = 0; i < myXMLHandler.feed.entry.size(); i++) {
					feed.entry.add(myXMLHandler.feed.entry.elementAt(i));
				}
//				feed.link = myXMLHandler.feed.link;
//				if(myXMLHandler.feed.nexturl != null && !myXMLHandler.feed.nexturl.equalsIgnoreCase("NA") && myXMLHandler.feed.nexturl.length() > 0)					
				feed.nexturl = myXMLHandler.feed.nexturl;
				feed.link = myXMLHandler.feed.link;
				feed.links = myXMLHandler.feed.links;
			} else
				feed = myXMLHandler.feed;
		} catch (Exception ex) {
			ex.printStackTrace();
			if (Logger.ENABLE) {
				Logger.error("UICommunityManager", "parseXMLData - " + ex.getMessage(), ex);
			}

		}
		UICommunityManager.mResponseData = null;
		requestednextPage = false;
	}
	
	///
	///
	public void parseXMLData_suggested() {
		try {
			feed_suggested = null;
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			XMLReader xr = sp.getXMLReader();
			CommunityFeedParser myXMLHandler = new CommunityFeedParser();
			xr.setContentHandler(myXMLHandler);
			ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(mResponseDataSuggested);
			Log.w("Response",new String(mResponseDataSuggested));
//			String xml = new String(mResponseData);
//			System.out.println(xml);
//			mResponseDataSuggested = null;
			xr.parse(new InputSource(arrayInputStream));
			if (feed_suggested != null) {
				for (int i = 0; i < myXMLHandler.feed.entry.size(); i++) {
					feed_suggested.entry.add(myXMLHandler.feed.entry.elementAt(i));
				}
//				feed.link = myXMLHandler.feed.link;
//				if(myXMLHandler.feed.nexturl != null && !myXMLHandler.feed.nexturl.equalsIgnoreCase("NA") && myXMLHandler.feed.nexturl.length() > 0)					
				feed_suggested.nexturl = myXMLHandler.feed.nexturl;
				feed_suggested.link = myXMLHandler.feed.link;
				feed_suggested.links = myXMLHandler.feed.links;
			} else
				feed_suggested = myXMLHandler.feed;
		} catch (Exception ex) {
			ex.printStackTrace();
			if (Logger.ENABLE) {
				Logger.error("UICommunityManager", "parseXMLData - " + ex.getMessage(), ex);
			}

		}
		mResponseDataSuggested = null;
		requestednextPage = false;
	}

	public void parseXMLData_discover() {
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			XMLReader xr = sp.getXMLReader();
			CommunityFeedParser myXMLHandler = new CommunityFeedParser();
			xr.setContentHandler(myXMLHandler);
			ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(mResponseDataDISCOVER);
			Log.w("Response",new String(mResponseDataDISCOVER));
//			String xml = new String(mResponseData);
//			System.out.println(xml);
//			mResponseDataSuggested = null;
			xr.parse(new InputSource(arrayInputStream));
			if (feed_discover != null) {
				for (int i = 0; i < myXMLHandler.feed.entry.size(); i++) {
					feed_discover.entry.add(myXMLHandler.feed.entry.elementAt(i));
				}
//				feed.link = myXMLHandler.feed.link;
//				if(myXMLHandler.feed.nexturl != null && !myXMLHandler.feed.nexturl.equalsIgnoreCase("NA") && myXMLHandler.feed.nexturl.length() > 0)					
				feed_discover.nexturl = myXMLHandler.feed.nexturl;
				feed_discover.link = myXMLHandler.feed.link;
				feed_discover.links = myXMLHandler.feed.links;
			} else
				feed_discover = myXMLHandler.feed;
		} catch (Exception ex) {
			ex.printStackTrace();
			if (Logger.ENABLE) {
				Logger.error("UICommunityManager", "parseXMLData - " + ex.getMessage(), ex);
			}

		}
		requestednextPage = false;
		mResponseDataDISCOVER = null;
	}
	public void setPostData(byte[] postData) {
		this.postData = postData;
	}

	public void run() {

		while (mIsRunning) {
			try {
				synchronized (this) {
					wait();
				}

				if (null != mPostURL && 0 < mPostURL.trim().length()) {
					helper = new HttpConnectionHelper(mPostURL);
					helper.setHeader("RT-APP-KEY", "" + BusinessProxy.sSelf.getUserId());
					if (postData != null)
						helper.setPostData(postData);
					mCallBackTimer = new Timer();
					responseCode = helper.getResponseCode();
					
					
					switch (responseCode) {
					case HttpURLConnection.HTTP_OK:
						String contentEncoding = helper.getHttpHeader("Content-Encoding");
						InputStream inputStream = null;
						if (null == contentEncoding) {
							inputStream = helper.getInputStream();
						} else if (contentEncoding.equals("gzip")) {
							inputStream = new GZIPInputStream(helper.getInputStream());
						}
						ByteArrayBuffer buffer = new ByteArrayBuffer(CHUNK_LENGTH);
						byte[] chunk = new byte[CHUNK_LENGTH];
						int len;
						while (!cancelRequest && -1 != (len = inputStream.read(chunk))) {
							buffer.append(chunk, 0, len);
						}
						this.mResponseData = buffer.toByteArray();
						break;
					default:
						if (mProgressDialog.isShowing()) {
							mProgressDialog.dismiss();
						}
						break;
					}
					if(cancelRequest)continue;
					mCallBackTimer.cancel();
					mCallBackTimer = new Timer();
					mCallBackTimer.schedule(new OtsSchedularTask(this, DATA_CALLBACK, (byte)0), 0);
				}
			} catch (Exception ex) {
				mCallBackTimer = new Timer();
				mCallBackTimer.schedule(new OtsSchedularTask(this, ERROR_CALLBACK, (byte)0), 0);
			}
		}
	}
	@Override
	public void onBackPressed() {
		finish();
	}
	
	public String getCommunityName(String s)
	{		
		String lan = KeyValue.getString(this, KeyValue.LANGUAGE);
		if(!lan.equalsIgnoreCase("en")){
		String ss[] = getResources().getStringArray(R.array.community_recomendad2);		
		for (int i = 0; i < ss.length; i++) {
			if(s.equalsIgnoreCase(ss[i])){
				String sss[] = getResources().getStringArray(R.array.community_recomendad_ar);
				return sss[i];
			}
		}	
		}
		return s ;
	}
}