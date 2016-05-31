package com.kainat.app.android;


import io.fabric.sdk.android.Fabric;
import java.io.IOException;
import java.util.HashMap;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.database.ContentObserver;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.kainat.app.android.YelatalkApplication.TrackerName;
import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.engine.MediaEngine;
import com.kainat.app.android.util.ClientProperty;
import com.kainat.app.android.util.RTMediaPlayer;
import com.kainat.app.android.util.Utilities;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class YelatalkApplication extends Application implements SensorEventListener  {
	public static ClientProperty clientProperty;
	public static double lat,lon;
	public static boolean proximityChanged;
	public static AudioManager am ;
	public static Context applicationcContext ;
	Sensor sensor;
	TelephonyManager telephonyManager,telephonCareeryManager;
	PhoneStateListener listener,listenerData ,careerInformationListeber,careerNetworkChangeListeber;
	public static SensorManager	 sensorManager;
	private Activity mCurrentActivity = null;
	
	private static final String PROPERTY_ID = "UA-66920320-1";
    public static int GENERAL_TRACKER = 0;
    public enum TrackerName {
    	        APP_TRACKER, GLOBAL_TRACKER, ECOMMERCE_TRACKER,
    	    }
    public HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();
    public YelatalkApplication() {
        super();
    }
    public synchronized Tracker getTracker(TrackerName appTracker) {
        if (!mTrackers.containsKey(appTracker)) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker t = (appTracker == TrackerName.APP_TRACKER) ? analytics.newTracker(PROPERTY_ID) : (appTracker == TrackerName.GLOBAL_TRACKER) 
            		? analytics.newTracker(R.xml.global_tracker) : analytics.newTracker(R.xml.ecommerce_tracker);
            mTrackers.put(appTracker, t);
        }
        return (Tracker) mTrackers.get(appTracker);
    }
    	   


	@Override
	public void onCreate() {
		super.onCreate();
		Fabric.with(this, new Crashlytics());

		//		Intent intent = new Intent(this, SplashActivity.class);
		//		startActivity(intent);

		applicationcContext = getApplicationContext();
		
		// UNIVERSAL IMAGE LOADER SETUP
				DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
						.cacheOnDisc(true).cacheInMemory(true)
						.imageScaleType(ImageScaleType.EXACTLY)
						.displayer(new FadeInBitmapDisplayer(300)).build();

				ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
						getApplicationContext())
						.defaultDisplayImageOptions(defaultOptions)
						.memoryCache(new WeakMemoryCache())
						.discCacheSize(100 * 1024 * 1024).build();

				ImageLoader.getInstance().init(config);
				
				// END - UNIVERSAL IMAGE LOADER SETUP
				
		if (null == clientProperty) {
			try {
				clientProperty = new ClientProperty(this.getBaseContext());
			} catch (IOException e) {
			}
		}
		am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		MediaEngine.initMediaEngineInstance(this.getBaseContext());
		BusinessProxy.initialise(this.getBaseContext());
		Utilities.loadValues(this.getBaseContext());
		try {
			clientProperty = new ClientProperty(this.getBaseContext());
		} catch (IOException e) {
		}
//		LocationManager mlocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
//		LocationListener mlocListener = new MyLocationListener();
		ConnectivityManager conMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		//mobile
		try
		{
			final State mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
		}
		catch(Exception ex)
		{
			//ex.printStackTrace();
		}

		//wifi
		final State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();

		try{
//			mlocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
//			sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
//			sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
//			List<Sensor> sen = sensorManager.getSensorList(Sensor.TYPE_ALL);
//			sensorManager.registerListener(this, sensor, sensorManager.SENSOR_DELAY_NORMAL);

			telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

			// Create a new PhoneStateListener
			listener = new PhoneStateListener() {
				@Override
				public void onCallStateChanged(int state, String incomingNumber) {
					//			        String stateString = "N/A";
					switch (state) {
					case TelephonyManager.CALL_STATE_IDLE:
						BusinessProxy.sSelf.isCalling=false;
						RTMediaPlayer.callEnd();
						//			          stateString = "Idle";
						//			          System.out.println("---stateString----"+stateString);
						//			          System.out.println("---mode----"+RocketalkApplication.am.getMode());
						//			          System.out.println("---is speaker on----"+RocketalkApplication.am.isSpeakerphoneOn());

						break;

					case TelephonyManager.CALL_STATE_OFFHOOK:
						//			        	RocketalkApplication.am.setSpeakerphoneOn(false);
						//			        	RTMediaPlayer.reset();
						//			        	RTMediaPlayer.setStreemSystem();
						//			          stateString = "Off Hook";
						BusinessProxy.sSelf.isCalling=true;
						RTMediaPlayer.setCallStreemSystem();
						//			          System.out.println("---stateString----"+stateString);
						//			          System.out.println("---mode----"+RocketalkApplication.am.getMode());
						//			          System.out.println("---is speaker on----"+RocketalkApplication.am.isSpeakerphoneOn());

						break;
					case TelephonyManager.CALL_STATE_RINGING:
						RTMediaPlayer.pause();
						RTMediaPlayer.setCallStreemSystem();
						RTMediaPlayer.setCallStreemSystem();
						BusinessProxy.sSelf.isCalling=true;
						//			          stateString = "Ringing";
						//			          System.out.println("---stateString----"+stateString);
						//			          System.out.println("---mode----"+RocketalkApplication.am.getMode());
						//			          System.out.println("---is speaker on----"+RocketalkApplication.am.isSpeakerphoneOn());
						break;

					}
				}
			};

			listenerData = new PhoneStateListener() {
				@Override
				public void onCallStateChanged(int state, String incomingNumber) {
					String stateString = "N/A";
					switch (state) {
					case TelephonyManager.DATA_DISCONNECTED:				        	
						stateString = "DATA_DISCONNECTED";
						//     System.out.println("---stateString----"+stateString);
						//   System.out.println("---mode----"+RocketalkApplication.am.getMode());
						//   System.out.println("---is speaker on----"+RocketalkApplication.am.isSpeakerphoneOn());				          
						break;

					case TelephonyManager.DATA_CONNECTED:
						RTMediaPlayer.pause();
						RTMediaPlayer.setCallStreemSystem();
						RTMediaPlayer.setCallStreemSystem();
						stateString = "DATA_CONNECTED";
						//  System.out.println("---stateString----"+stateString);
						//  System.out.println("---mode----"+RocketalkApplication.am.getMode());
						//  System.out.println("---is speaker on----"+RocketalkApplication.am.isSpeakerphoneOn());
						break;

					}
				}
			};
			// Register the listener wit the telephony manager
			telephonyManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
			//			    telephonyManager.listen(listenerData, PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
			// NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

			telephonCareeryManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			careerInformationListeber =new PhoneStateListener(){
				@Override
				public void onDataConnectionStateChanged(int state) {
					super.onDataConnectionStateChanged(state);

					if(Connectivity.isConnectedFast(getApplicationContext())){

						ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
						NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
						NetworkInfo mMobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);


						if (mWifi != null && mWifi.isConnected()){

							//if wifi connected
							BusinessProxy.sSelf.MaxSizeData=10;
							//			 			    	com.kainat.app.android.util.Constants.EXTRA_VIDEO_QUALITY = 1 ;//Currently value 0 means low quality, suitable for MMS messages, and value 1 means high quality
							//			 			    	 if(Logger.CHEAT)
							//					 				Toast.makeText(getApplicationContext(), "wifi!", Toast.LENGTH_SHORT).show() ;
						}


						if (mMobile != null && mMobile.isConnected()) {
							//if internet connected
							BusinessProxy.sSelf.MaxSizeData=10;
							//			 			    	com.kainat.app.android.util.Constants.EXTRA_VIDEO_QUALITY = 1;//Currently value 0 means low quality, suitable for MMS messages, and value 1 means high quality
							//			 			    	 if(Logger.CHEAT)
							//					 				Toast.makeText(getApplicationContext(), "3g is connected!", Toast.LENGTH_SHORT).show() ;
						}




					}else{
						ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
						NetworkInfo mMobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

						if (mMobile != null && mMobile.isConnected()) {
							BusinessProxy.sSelf.MaxSizeData=2;
							//			 			    	com.kainat.app.android.util.Constants.EXTRA_VIDEO_QUALITY = 0 ;//Currently value 0 means low quality, suitable for MMS messages, and value 1 means high quality
							//			 			    	 if(Logger.CHEAT)
							//					 				Toast.makeText(getApplicationContext(), "gprs!", Toast.LENGTH_SHORT).show() ; //mobile
						}else
							if (mMobile != null && !mMobile.isConnected()) {
								BusinessProxy.sSelf.MaxSizeData=2;
								//			 			    	com.kainat.app.android.util.Constants.EXTRA_VIDEO_QUALITY = 0 ;//Currently value 0 means low quality, suitable for MMS messages, and value 1 means high quality
								//			 			    	if(Logger.CHEAT)
								//					 				Toast.makeText(getApplicationContext(), "gprs! is not connected", Toast.LENGTH_SHORT).show() ; //mobile
							}

					}
				}
			};
			telephonCareeryManager.listen(careerInformationListeber, PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);



			/*    if(Connectivity.isConnectedFast(getApplicationContext())){

							Toast.makeText(getApplicationContext(), "wifi!", Toast.LENGTH_SHORT).show() ;

			    }else{

							Toast.makeText(getApplicationContext(), "gprs!", Toast.LENGTH_SHORT).show() ;

			    }*/

		}catch (Exception e) {
			// TODO: handle exception
		}

		getContentResolver().registerContentObserver(ContactsContract.Data.CONTENT_URI, true, new ContentObserver(null){
			@Override
			public void onChange(boolean selfChange) {
				super.onChange(selfChange);
				UIActivityManager.ContactSelfChange = true;
			}

		});
	}

	public Activity getCurrentActivity(){
		return mCurrentActivity;
	}
	public void setCurrentActivity(Activity mCurrentActivity){
		this.mCurrentActivity = mCurrentActivity;
	}

	private class MyPhoneStateListener extends PhoneStateListener {

		public void onSignalStrengthsChanged(SignalStrength signalStrength) {
			super.onSignalStrengthsChanged(signalStrength);

			boolean gsm = signalStrength.isGsm();

			/// ... some logic here... 
		}
	}
	public static void killApplication() {

		Utilities.saveChatData();

		android.os.Process.killProcess(android.os.Process.myPid());
		System.runFinalizersOnExit(true);
	}

	public class MyLocationListener implements LocationListener
	{
		@Override
		public void onLocationChanged(Location loc)		
		{

			lat = loc.getLatitude();

			lon = loc.getLongitude();
			try {
				String Text = "My current location is: " +

			"Latitud = " + loc.getLatitude() +

			"Longitud = " + loc.getLongitude();
			} catch (Exception e) {
				String Text = "My current location is: " +
						"Latitud = " + loc.getLatitude() +
						"Longitud = " + loc.getLongitude();

				e.printStackTrace();
			}
		}

		@Override
		public void onProviderDisabled(String provider)

		{
		}

		@Override
		public void onProviderEnabled(String provider)

		{

			//			Toast.makeText(getApplicationContext(),
			//
			//			"Gps Enabled",
			//
			//			Toast.LENGTH_SHORT).show();

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras)

		{

		}

	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		//		System.out.println("-----------------------------accuracy-----"+accuracy);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {

		float azimuth_angle = event.values[0];
		float pitch_angle = event.values[1];
		float roll_angle = event.values[2];
		//		   System.out.println("-----------------------------azimuth_angle-----"+azimuth_angle);
		//		   System.out.println("-----------------------------pitch_angle-----"+pitch_angle);
		//		   System.out.println("-----------------------------azimuth_angle-----"+roll_angle);

		if(event.sensor.getType()==Sensor.TYPE_PROXIMITY && azimuth_angle < 1){
			if(proximityChanged)
				proximityChanged = false;
			else
				proximityChanged = true ;
		}else
			proximityChanged = true ;
	}





}