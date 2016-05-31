package com.kainat.app.android;



import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;

import com.example.kainat.util.AppUtil;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.kainat.app.android.YelatalkApplication.TrackerName;
import com.kainat.app.android.bean.User;
import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.engine.Auth;
import com.kainat.app.android.util.ClientProperty;
import com.kainat.app.android.util.Constants;
import com.kainat.app.android.util.ImageDownloader;
import com.kainat.app.android.util.KeyValue;
import com.kainat.app.android.util.MyBase64;
import com.kainat.app.android.util.RequestType;
import com.kainat.app.android.util.ResponseError;
import com.kainat.app.android.util.ResponseObject;
import com.kainat.app.android.util.ThumbnailImage;
import com.kainat.app.android.util.ThumbnailReponseHandler;
import com.kainat.app.android.util.Urls;
import com.kainat.app.android.util.Utilities;
import com.kainat.app.android.util.format.SettingData;

public class SplashActivity extends UIActivityManager {
	public static final String EXTRA_MESSAGE = "message";
	public static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
//	private static final String CRITTERCISM_APP_KEY = "54d3268951de5e9f042ed67a";
	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	Handler handler = new Handler();
	GoogleCloudMessaging gcm;
	static final String TAG = SplashActivity.class.getSimpleName();
	AtomicInteger msgId = new AtomicInteger();
	Context context;

	String regid;
	
	public static String getCurrentTimezoneOffset() {

		TimeZone tz = TimeZone.getDefault();  
		Calendar cal = GregorianCalendar.getInstance(tz);
		int offsetInMillis = tz.getOffset(cal.getTimeInMillis());
		String offset = String.format("%02d:%02d", Math.abs(offsetInMillis / 3600000), Math.abs((offsetInMillis / 60000) % 60));
		offset = (offsetInMillis >= 0 ? "+" : "-") + offset;
		return offset;
	}
	
	public JSONObject writeJSON() {
		  JSONObject object = new JSONObject();
		  try {
		    object.put("username", SettingData.sSelf.getUserName());
		    object.put("phonemodel", Utilities.sPhoneModel);
		    object.put("androidOS", Utilities.sOSVersion);
		    object.put("screensize", Utilities.getScreenResolution(context));
		    object.put("density", Utilities.getDensity(context));
		    object.put("countrycode", Utilities.getCountryCode(context));
		    object.put("country", SettingData.sSelf.getCountry());
		  } catch (JSONException e) {
		    e.printStackTrace();
		  }
         return object;
		} 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		context = this;
		UIActivityManager.TabValue = 2;
		UIActivityManager.mCurrentSelectedTab = UIActivityManager.TAB_COMMUNITY;
//		//Initialize crittercism 
//		Crittercism.initialize(getApplicationContext(), CRITTERCISM_APP_KEY);
//		if(SettingData.sSelf.getFirstName() != null)
//			Crittercism.setUsername(SettingData.sSelf.getFirstName());
//		//Set other parameters in JSON
//		Crittercism.setMetadata(writeJSON());
//		System.out.println(getCurrentTimezoneOffset());
//		TimeZone time = TimeZone.getDefault();
//		String gmt1=TimeZone.getTimeZone(time.getID()).getDisplayName(false,TimeZone.SHORT);


		if (checkPlayServices()) 
		{
			gcm = GoogleCloudMessaging.getInstance(this);
			regid = getRegistrationId(context);
			Utilities.sRegId = regid;
			Log.i(TAG, "onCreate :: getRegistrationId saved ===> " + regid);
			if (regid.isEmpty()) 
			{
				registerInBackgroundLocal();
			}
		} 
		else 
		{
			Log.i(TAG, "onCreate :: No valid Google Play Services APK found.");
		}
		String lan = KeyValue.getString(SplashActivity.this, KeyValue.LANGUAGE);
		if(lan!=null)
			setLocale(lan);
		if(!KeyValue.getBoolean(this, KeyValue.VERIFIED))
			SettingData.sSelf.setRemember(false);
		/*else
		{
			SettingData.sSelf.setRemember(true);
		}*/
		
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				if (SettingData.sSelf.isRemember()) 
				{
					if(isInternetOn())
						new AutoLogin().execute("");
					else
					{
						//Entering without network, so set values from settings
//						BusinessProxy.sSelf.initializeDatabase();
						UIActivityManager.launchedWithNoNetwork = true;
						getValuesFromSharedPrefAndSet();
						/*UIActivityManager.TabValue = 2;
						UIActivityManager.mCurrentSelectedTab = UIActivityManager.TAB_COMMUNITY;*/
						Intent intent = new Intent(SplashActivity.this, KainatHomeActivity.class);
						startActivity(intent);
						finish();
					}
				}
				else
				{
				finish();
				gotoLanguageSelectionPage();
				UIActivityManager.mCurrentSelectedTab = UIActivityManager.TAB_COMMUNITY;
				}
			}
		}, 2000);
		
		
		/*if (SettingData.sSelf.isRemember()) 
		{
			if(isInternetOn())
				new AutoLogin().execute("");
			else
			{
				//Entering without network, so set values from settings
//				BusinessProxy.sSelf.initializeDatabase();
				UIActivityManager.launchedWithNoNetwork = true;
				getValuesFromSharedPrefAndSet();
				UIActivityManager.TabValue = 2;
				UIActivityManager.mCurrentSelectedTab = UIActivityManager.TAB_COMMUNITY;
				Intent intent = new Intent(SplashActivity.this, KainatHomeActivity.class);
				startActivity(intent);
				finish();
			}
		}
		else
		{
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					finish();
					gotoLanguageSelectionPage();
					UIActivityManager.mCurrentSelectedTab = UIActivityManager.TAB_COMMUNITY;
				}
			}, 2000);
		}*/
		
		Tracker t = ((YelatalkApplication) getApplication()).getTracker(TrackerName.APP_TRACKER);
		        t.setScreenName("Splash Screen");
		        t.set("&uid",""+BusinessProxy.sSelf.getUserId());
		        t.send(new HitBuilders.AppViewBuilder().build());

	}
		
	@Override
	protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        GoogleAnalytics.getInstance(SplashActivity.this).reportActivityStart(this);
    }
    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        GoogleAnalytics.getInstance(SplashActivity.this).reportActivityStop(this);
    }

	
	private void getValuesFromSharedPrefAndSet(){
		if(SettingData.sSelf.getUserID() != 0)
			BusinessProxy.sSelf.setUserId(SettingData.sSelf.getUserID());
		if(SettingData.sSelf.getClientID() != 0)
			BusinessProxy.sSelf.setClientId(SettingData.sSelf.getClientID());
	}

	private void gotoLanguageSelectionPage()
	{
		Intent intent = new Intent(SplashActivity.this, LanguageActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out); 
//		if(gcm != null)
//			try {
//				gcm.unregister();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
	}
	protected void onResume() 
	{
		super.onResume();
		//Remove any notification, if app is launched by tapping app-icon
		removeNotification();
		String lan = KeyValue.getString(SplashActivity.this, KeyValue.LANGUAGE);
		if(lan!=null)
			setLocale(lan);
	}
	ImageDownloader imageManager = new ImageDownloader();

	private class AutoLogin extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			showAnimitionFullScreen("", "", true);
		}

		@Override
		protected String doInBackground(String... params) {
			ImageView imgAvatar = new ImageView(SplashActivity.this);
			ThumbnailReponseHandler handler = new ThumbnailReponseHandler() {
				@Override
				public void onThumbnailResponse(ThumbnailImage value,
						byte[] data) {					
				}
			};				
			imageManager.download(SettingData.sSelf.getUserName(),imgAvatar, handler,ImageDownloader.TYPE_THUMB_BUDDY);
            sendLoginRequest();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			 runOnUiThread(new Runnable() {
			        @Override
			        public void run() {
			        	dismissAnimationDialog();
			        }
			    });
		}
	}

	public void sendLoginRequest() {
		try {
			Hashtable<String, String> headerParam = new Hashtable<String, String>();
			headerParam.put("RT-Params", ClientProperty.RT_PARAMS);
			Hashtable<String, String> postParam = new Hashtable<String, String>();
			{
				User.getInstance().userName  = SettingData.sSelf.getUserName() ;
				User.getInstance().password  = SettingData.sSelf.getPassword();
				User.getInstance().email  = SettingData.sSelf.getEmail();
				Log.i(TAG, "sendLoginRequest :: ############## PUSH  client_param ###################" +  ClientProperty.CLIENT_PARAMS + "::requesttype##" + RequestType.LOGIN_FB_AUTO);
				postParam.put("mode", User.getInstance().LOGIN_ROCKETALK_SHORT_NAME);// mode=fb
				// rt
				if(SettingData.sSelf.getAppkey() != null)
					postParam.put("appkey", SettingData.sSelf.getAppkey());

				if (SettingData.sSelf.getPartneruserkey() != null)
					postParam.put("partneruserkey", SettingData.sSelf.getPartneruserkey());// fb

				if (User.getInstance().userName != null && !checkEmail(User.getInstance().userName) && !checkPhone(User.getInstance().userName)) 
				{
					postParam.put("username", User.getInstance().userName);
					if (User.getInstance().userName != null && User.getInstance().userName.indexOf("~") != -1) {

						String un[] = User.getInstance().userName.split("~");
						postParam.put("fname", un[0]);
						postParam.put("lname", un[1]);
						postParam.put("username", un[0] + " " + un[1]);
					}
				}
				if (User.getInstance().userName != null && checkEmail(User.getInstance().userName) && !checkPhone(User.getInstance().userName))
					postParam.put("emailid", User.getInstance().userName);
				if (User.getInstance().userName != null && !checkEmail(User.getInstance().userName) && checkPhone(User.getInstance().userName))
					postParam.put("phone", User.getInstance().userName);
				if (User.getInstance().password != null)
					postParam.put("password", User.getInstance().password);
				if (User.getInstance().multipleAccountOnRT && (User.getInstance().userName != null && User.getInstance().userName.indexOf("~") != -1)) 
				{
					String un[] = User.getInstance().userName.split("~");
					postParam.put("fname", un[0]);
					postParam.put("lname", un[1]);
					postParam.put("username", un[0] + " " + un[1]);
				}
				String urlStr = "http://" + Urls.TEJAS_HOST + "/tejas/feeds/api/user/login";
				DefaultHttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(urlStr);
				MultipartEntity mpEntity = new MultipartEntity();
				for (Iterator<String> iterator = postParam.keySet().iterator(); iterator.hasNext();) 
				{
					String key = iterator.next();
					String value = postParam.get(key);
					mpEntity.addPart(key, new StringBody(value));
				}
				httppost.setHeader("RT-Params", ClientProperty.RT_PARAMS);				
				httppost.setHeader("client_param", ClientProperty.CLIENT_PARAMS + "::requesttype##" + RequestType.LOGIN_FB_AUTO);
				httppost.setEntity(mpEntity);
				HttpResponse response;
				response = httpclient.execute(httppost);
				HttpEntity r_entity = response.getEntity();
				String responseString = EntityUtils.toString(r_entity);
				handleLoginResponse(responseString);
				Log.d("", responseString);
			}
		} catch (final Exception e) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					AppUtil.showTost(SplashActivity.this, getString(R.string.check_net_connection)) ;
				}
			});
			finish() ;
			e.printStackTrace();
		}
	}

	private void handleLoginResponse(final String response) 
	{
//		System.out.println(response);
		if (response != null) {
			try {
				final JSONObject jsonObject = new JSONObject(response);
				if (jsonObject.has("status") && jsonObject.getString("status").equalsIgnoreCase(ResponseError.STATUS_ERROR)) 
				{
					if (User.getInstance().LOGIN_VIA != null && User.getInstance().LOGIN_VIA.equalsIgnoreCase(User
							.getInstance().LOGIN_FACEBOOK)
							&& jsonObject != null && jsonObject.getString("code") != null && !jsonObject.getString("code").equalsIgnoreCase(
									ResponseError.AUTHENTICATION_FAILED)) 
					{
						Auth.getInstance().mFacebook = null;
					}
					finish();
					gotoLanguageSelectionPage();
					if (jsonObject.has("message")) 
					{
						//Show Error toast
						runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								try {
									showToast(jsonObject.getString("message"));
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						});
					}
					return;

				} else {
					if (jsonObject.has("mediaCategoryList")) {
						JSONArray jSONArray = jsonObject
								.getJSONArray("mediaCategoryList");
						BusinessProxy.sSelf.mediaCategories = new String[jSONArray
						                                                 .length()];
						BusinessProxy.sSelf.mediaCategoryIDs = new String[jSONArray
						                                                  .length()];

						for (int i = 0; i < jSONArray.length(); i++) {
							JSONObject nameObjectw = jSONArray.getJSONObject(i);

							if (nameObjectw.has("categoryId")) {
								BusinessProxy.sSelf.mediaCategoryIDs[i] = nameObjectw
										.getString("categoryId");
							}
							if (nameObjectw.has("categoryName")) {
								BusinessProxy.sSelf.mediaCategories[i] = nameObjectw
										.getString("categoryName");
							}
						}
						Constants.values = BusinessProxy.sSelf.mediaCategories;
						Constants.valuesId = BusinessProxy.sSelf.mediaCategoryIDs;
					}

//					if (jsonObject.has("advertisementParam")) {
//						BusinessProxy.sSelf.advertisementData.put("userParam",
//								jsonObject.getString("advertisementParam"));
//					}
//
//					if (jsonObject.has("animationURL"))
//						BusinessProxy.sSelf.singleAnimationURL = jsonObject
//						.getString("animationURL");
//
//					if (jsonObject.has("animationListURL"))
//						BusinessProxy.sSelf.animationListURL = jsonObject
//						.getString("animationListURL");

					if (jsonObject.has("clientId"))
						BusinessProxy.sSelf.setClientId(Integer
								.parseInt(jsonObject.getString("clientId")));
//					if (Logger.LOGS_ENABLE_USERID)
//						System.out.println("CURRENT USER ID IS - "
//								+ BusinessProxy.sSelf.getUserId());

					if (jsonObject.has("userId"))
						BusinessProxy.sSelf.setUserId(Integer
								.parseInt(jsonObject.getString("userId")));
//					if (Logger.LOGS_ENABLE_USERID)
//						System.out.println("SETTING USER ID TO - "
//								+ BusinessProxy.sSelf.getUserId());

					if (jsonObject.has("lastTransactionId"))
						BusinessProxy.sSelf.setTransactionId(Integer
								.parseInt(jsonObject
										.getString("lastTransactionId")));

//					if (jsonObject.has("lastTransactionId"))
//						BusinessProxy.sSelf.setTransactionId(Integer
//								.parseInt(jsonObject
//										.getString("lastTransactionId")));

					if (jsonObject.has("type"))
						SettingData.sSelf.setType(jsonObject.getString("type"));

					SettingData.sSelf.setType(User.getInstance().LOGIN_VIA);

					if (jsonObject.has("firstName"))
						SettingData.sSelf.setFirstName(jsonObject
								.getString("firstName"));

					if (jsonObject.has("lastName"))
						SettingData.sSelf.setLastName(jsonObject
								.getString("lastName"));

//					if (jsonObject.has("lastName"))
//						SettingData.sSelf.setLastName(jsonObject
//								.getString("lastName"));

					try {
						if (jsonObject.has("isEmailVerified"))
							SettingData.sSelf.setEmailVerified(Boolean
									.parseBoolean(jsonObject
											.getString("isEmailVerified")));

					} catch (Exception e) {
						// TODO: handle exception
					}

					if (jsonObject.has("ageGroup"))
						SettingData.sSelf.setAgeGroup(jsonObject
								.getString("ageGroup"));

					if (jsonObject.has("dob"))
						SettingData.sSelf.setBirthDate(jsonObject
								.getString("dob"));

					if (jsonObject.has("country"))
						SettingData.sSelf.setCountry(jsonObject
								.getString("country"));

					if (jsonObject.has("state"))
						SettingData.sSelf.setState(jsonObject
								.getString("state"));

					if (jsonObject.has("emailId"))
						SettingData.sSelf.setEmail(jsonObject
								.getString("emailId"));

					if (jsonObject.has("mobileNumber")) {
						SettingData.sSelf.setMobile(jsonObject
								.getString("mobileNumber"));
						SettingData.sSelf.setNumber(jsonObject
								.getString("mobileNumber"));
					}

					if (jsonObject.has("mobileModel"))
						SettingData.sSelf.setMobileModel(jsonObject
								.getString("mobileModel"));

					if (jsonObject.has("userName"))
						SettingData.sSelf.setUserName(jsonObject
								.getString("userName"));

					if (jsonObject.has("gender")){
						if(jsonObject
								.getString("gender").equalsIgnoreCase("M"))
							SettingData.sSelf.setGender(1);
					}



					if (jsonObject.has("password"))
						SettingData.sSelf.setPassword(jsonObject
								.getString("password"));

					if (jsonObject.has("buddyTimeStamp"))
						BusinessProxy.sSelf.mBuddyTimeStamp = jsonObject
						.getString("buddyTimeStamp");
					else
						BusinessProxy.sSelf.mBuddyTimeStamp = "1000-00-00 00:00:00.0";

					BusinessProxy.sSelf.mBuddyTimeStamp = "0000-00-00 00:00:00.0";
					BusinessProxy.sSelf.mGroupTimeStamp = "0000-00-00 00:00:00.0";
					BusinessProxy.sSelf.mInboxTimeStamp = "0000-00-00 00:00:00.0";

//					if (jsonObject.has("upgradeType")) {
//						if (aResponse == null)
//							aResponse = new ResponseObject();
//						aResponse.setUpgradeType(Integer.parseInt(jsonObject
//								.getString("upgradeType")));
//					}
//					if (jsonObject.has("upgradeRTML")) {
//						if (aResponse == null)
//							aResponse = new ResponseObject();
//						String rtml = jsonObject.getString("upgradeRTML");
//						rtml = rtml.replace("\u003c", "<");
//						rtml = rtml.replace("\u003d", ">");
//						aResponse.setUpgradeURL(jsonObject
//								.getString("upgradeRTML"));
//					}
					if (jsonObject.has("isMobileVerificationNeeded")) {
						if (aResponse == null)
							aResponse = new ResponseObject();
						try {
							aResponse.setMobileVerificationNeeded(Boolean
									.parseBoolean(jsonObject.getString(
											"isMobileVerificationNeeded")
											.trim()));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					try {

//						if (Auth.getInstance().mFacebook != null
//								&& Utilities.getInt(SplashActivity.this,
//										Constants.FIRST_LOGIN) == 0) {
//							String Description = "";
//							BusinessProxy.sSelf.loginWithFb = true;
//						} else if (Auth.getInstance().mFacebook != null) {
//							BusinessProxy.sSelf.loginWithFb = true;
//						}
						
						//Now we have got the full response here, so close the loading animation and move to next page.
						handleLoginResponseNew(aResponse);
						dismissAnimationDialog();
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				finish();
//				Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
//				startActivity(intent);
				return ;
			}
		}
		if (!SettingData.sSelf.isEmailVerified()
				&& BusinessProxy.sSelf.isRegistered()) {
			getIntent().putExtra("password", SettingData.sSelf.getPassword());
			return;
		}

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				onPostExecute(response);
			}
		});

	}

	ResponseObject aResponse;

	@Override
	public void notificationFromTransport(ResponseObject response) {
		// TODO Auto-generated method stub

	}

	protected void onPostExecute(String response) {
		if(!isRunningInForeground())
		{
			System.exit(0);
		}

		if (aResponse == null || aResponse.getUpgradeType() != 1)
			if(!KeyValue.getBoolean(this, "INCOMP"))
			{
//				pushNewScreen(KainatHomeActivity.class, false);
				finish();
				Intent intent = new Intent(SplashActivity.this, KainatHomeActivity.class);
				startActivity(intent);
			}
			else
			{
				finish();
				Intent intent = new Intent(this,KainatCreateProfileActivity.class);
				intent.putExtra("oldUser", true);
				intent.putExtra("REG_FLOW", true);
				startActivity(intent);
			}
		if (aResponse == null || aResponse.getUpgradeType() <= 0) {
			BusinessProxy.sSelf.setNetActive(true);
			BusinessProxy.sSelf.startRefresh();
			// pushNewScreen(KainatHomeActivity.class, false);
		} 
	}

	public void setLocale(String lang) {
		Locale myLocale = new Locale(lang);
		Resources res = getResources();
		DisplayMetrics dm = res.getDisplayMetrics();
		Configuration conf = res.getConfiguration();
		conf.locale = myLocale;
		res.updateConfiguration(conf, dm);
		PackageInfo packageInfo=null;
		try {
			packageInfo = getPackageManager().getPackageInfo(getPackageName(), 
					PackageManager.GET_SIGNATURES);
			for (Signature signature : packageInfo.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				String key = new String(MyBase64.encode(md.digest()));
				Log.i(TAG, "setLocale :: Sigunature key : "+key);
			} 
		}
		catch (NameNotFoundException e1) {
			e1.printStackTrace();
		}
		catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}

	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Log.i(TAG, "checkPlayServices :: This device is not supported.");
				finish();
			}
			return false;
		}
		return true;
	}

	private void registerInBackgroundLocal() 
	{
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(context);
					}
					if(Constants.isBuildLive)
						regid = gcm.register(Constants.GOOGLE_PROJECT_ID_PROD);
					else
						regid = gcm.register(Constants.GOOGLE_PROJECT_ID_DEV);
					msg = "Device registered, registration ID=" + regid;
					Log.i(TAG, "registerInBackgroundLocal :: regid----> "+regid);

					// You should send the registration ID to your server over HTTP, so it
					// can use GCM/HTTP or CCS to send messages to your app.
					sendRegistrationIdToBackend();

					// For this demo: we don't need to send it because the device will send
					// upstream messages to a server that echo back the message using the
					// 'from' address in the message.

				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
					// If there is an error, don't just keep trying to register.
					// Require the user to click a button again, or perform
					// exponential back-off.
				}
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				// Persist the regID - no need to register again.
				storeRegistrationId(context, regid);
			}
		}.execute(null, null, null);
	}
	/**
	 * @return Application's {@code SharedPreferences}.
	 */
	private SharedPreferences getGcmPreferences(Context context) {
		// This sample app persists the registration ID in shared preferences, but
		// how you store the regID in your app is up to you.
		return getSharedPreferences(SplashActivity.class.getSimpleName(),
				Context.MODE_PRIVATE);
	}
	/**
	 * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP or CCS to send
	 * messages to your app. Not needed for this demo since the device sends upstream messages
	 * to a server that echoes back the message using the 'from' address in the message.
	 */
	private void sendRegistrationIdToBackend() {
		// Your implementation here.
	}

	private String getRegistrationId(Context context) {
		final SharedPreferences prefs = getGcmPreferences(context);
		String registrationId = prefs.getString(PROPERTY_REG_ID, "");
		if (registrationId.isEmpty()) {
			Log.i(TAG, "getRegistrationId :: Registration not found.");
			return "";
		}
		// Check if app was updated; if so, it must clear the registration ID
		// since the existing regID is not guaranteed to work with the new
		// app version.
		int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
			Log.i(TAG, "getRegistrationId :: App version changed.");
			return "";
		}
		return registrationId;
	}
	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}
	private void storeRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = getGcmPreferences(context);
		int appVersion = getAppVersion(context);
		Log.i(TAG, "storeRegistrationId :: Saving regId ==> "+regId+", on app version " + appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PROPERTY_REG_ID, regId);
		editor.putInt(PROPERTY_APP_VERSION, appVersion);
		editor.commit();
		Utilities.sRegId = regId;//BusinessProxy.sSelf.getPushRegId(DBEngine.PUSH_TABLE);
	}
//----------------------------------------------------------------
	//Validate your push key
	public static void testPushMessage(String regID)
	{
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
		String apiKey = "AIzaSyCXNHLqn4WyqZVbpxMnQrFzKzAyIfOnzEM";
		if(Constants.isBuildLive)
			apiKey = "AIzaSyCfJJmamTU4CGdkCesL5l3z6IhR9m4XLrA";
        try{
        // 1. URL
        URL url = new URL("https://android.googleapis.com/gcm/send");
        // 2. Open connection
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        // 3. Specify POST method
        conn.setRequestMethod("POST");
        // 4. Set the headers
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "key="+apiKey);
        conn.setDoOutput(true);
            // 5. Add JSON data into POST request body
            //`5.1 Use Jackson object mapper to convert Contnet object into JSON
//            ObjectMapper mapper = new ObjectMapper();
            // 5.2 Get connection output stream
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            String message = "Testing Push Message 😋";
            byte[] data = message.getBytes("utf-8");
            String encodedMessage = new String(data, "utf-8");
            String jsonMessage = "{ \"data\": {\"title\": \"My Push\",\"message\": \"" + encodedMessage + "\"},\"registration_ids\": " +
 "[\"APA91bHsnWBM65gE0dxMWjGlxtwMbtm-Hxi-ekznvldPvrj3i8gB0hMzvnNoo8i-EJ5wdu0tuhLoyvNzcAfOa1-JIDUbqAvhGSrai30Z3_9E2jd46OaME-nbgiQcJ0yIfecJ92xT5VDb\"]}";
            //APA91bH0EPv2bqfQxrnvdv6SPfFqPtQATZBlhq_hieYkksUCI6s6kqFRZ5P1b65fd31KmzREUqbSC2kQrwTu9WqlzemG7m6qO_ADm5-SmJFpg5fh8GXty7wTU-Q2h5KvyjumjX4hCyQAsOXT8l0VHhb_-DlC5L1OILbFa4qUj5dpupkoBQ86Sco
            // 5.3 Copy Content "JSON" into
//            mapper.writeValue(wr, content);
//            // 5.4 Send the request
//            wr.flush();
//            // 5.5 close
//            wr.close();
            wr.write(jsonMessage.getBytes());
            wr.flush();
            wr.close();
            // 6. Get the response
            int responseCode = conn.getResponseCode();
            Log.i(TAG, "post :: \nSending 'POST' request to URL : " + url);
            Log.i(TAG, "post :: \nData Sent : " + jsonMessage);
            Log.i(TAG, "post :: Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            // 7. Print result
//            System.out.println(response.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
          }
		}
	  }).start();
	}
	public boolean RetriveRemember(){
		SharedPreferences pref = this.getSharedPreferences("SettingData", Context.MODE_PRIVATE);
		//SharedPreferences.Editor editor = this.getSharedPreferences("SettingData", Context.MODE_PRIVATE).edit();
		boolean mIsRemember = pref.getBoolean("isremember", false);
		
		return mIsRemember;
	}
}
